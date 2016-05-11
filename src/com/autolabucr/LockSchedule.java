package com.autolabucr;

import com.autolabucr.Equipment.LabComponent;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by markd on 4/12/2016.
 */

public class LockSchedule implements Serializable{
    private String lockName = "";
    private int currentTaskIndex = 0;
    private LabComponent labComponent;
    private ArrayList<Range> lockSchedule = new ArrayList<>();
    private ArrayList<Task> taskSchedule = new ArrayList<>();


    public LockSchedule() {

    }

    /**
     * Gets the first available time slot in the lock's schedule.
     * @param duration the duration of how long the resource needs to be locked down.
     * @return the first available range from within the lock supporting the specified duration.
     */
    public Range getFirstAvailability(long duration) {
        for (int i = 0; i < lockSchedule.size(); i += 2) {
            if (lockSchedule.get(i + 1).start - lockSchedule.get(i).end >= duration) {
                return Range.fromDuration(lockSchedule.get(i).end, duration);
            }
        }
        return Range.fromDuration(lockSchedule.get(lockSchedule.size() - 1).end, duration);
    }

    /**
     * Gets the next available time slot in the lock's schedule.
     * @param start the beginning time to start looking for an available slot.
     * @param duration the duration of how long the resource needs to be locked down.
     * @return The first available range after {@code start} from within the lock supporting the specified duration.
     */
    public Range nextAvailability(long start, long duration){
        for (int i = 1; i < lockSchedule.size(); i++) {
            long startTime = Math.max(start, lockSchedule.get(i-1).end);
            if(lockSchedule.get(i-1).end <= start && startTime <= lockSchedule.get(i).start) {
                return Range.fromDuration(lockSchedule.get(i-1).end, duration);
            }
        }
        if(lockSchedule.isEmpty()) {
            return Range.fromDuration(Scheduler.getCurrentTime(), duration);
        }
        return Range.fromDuration(lockSchedule.get(lockSchedule.size() - 1).end, duration);
    }

    /**
     * Schedules the task for the next available time slot within the schedule. The task then has it's {@code timeRange}
     * updated to match the returned time.
     * @param range The desired earliest start and duration of the task.
     * @param task The task being scheduled.
     * @return The scheduled range.
     */
    public Range scheduleNextAvailability(Range range, Task task){
        //Checks for possible gaps in schedule
        for (int i = 1; i < lockSchedule.size(); i++) {
            long startTime = Math.max(range.start, lockSchedule.get(i-1).end);
            if(lockSchedule.get(i-1).end <= range.start && startTime <= lockSchedule.get(i).start) {
                Range availableRange = Range.fromDuration(lockSchedule.get(i-1).end, range.duration);
                lockSchedule.add(availableRange);
                taskSchedule.add(task);
                task.timeRange = availableRange;
                return availableRange;
            }
        }
        //Checks if the schedule is empty
        if(lockSchedule.isEmpty()) {
            Range availableRange = Range.fromDuration(Scheduler.getCurrentTime(), range.duration);
            lockSchedule.add(availableRange);
            taskSchedule.add(task);
            task.timeRange = availableRange;
            return availableRange;
        }
        //No opening defaults to the end of the schedule
        Range availableRange = Range.fromDuration(lockSchedule.get(lockSchedule.size() - 1).end, range.duration);
        lockSchedule.add(availableRange);
        taskSchedule.add(task);
        task.timeRange = availableRange;
        return availableRange;
    }

    /**
     * Returns whether or not the {@code LockSchedule} is available throughout the given {@code range}.
     * @param range The {@code range} to check if the {@code LockSchedule} is completely free for.
     * @return Whether or not the lock was free during the given range.
     */
    public boolean isAvailableFrom(Range range){
        for (int i = 1; i < lockSchedule.size(); i++) {
            if(lockSchedule.get(i-1).end <= range.start && range.end <= lockSchedule.get(i).start) {
                return true;
            }
            else if(lockSchedule.get(i-1).end > range.start){
                return false;
            }
        }
        return true;
    }

    /**
     * Immediately schedules a task iff the range given is free within the {@code LockSchedule}.
     * @param range The range in which the task should be scheduled.
     * @param task The task being scheduled.
     * @return Whether or not the task was reserved.
     */
    public boolean scheduleTask(Range range, Task task) {
        for (int i = 1; i < lockSchedule.size(); i++) {
            if(lockSchedule.get(i-1).end <= range.start && range.end <= lockSchedule.get(i).start) {
                lockSchedule.add(i, range);
                taskSchedule.add(i, task);
                return true;
            }
            else if(lockSchedule.get(i-1).end > range.start) {
                return false;
            }
        }
        lockSchedule.add(range);
        taskSchedule.add(task);
        //Add the labComponent being used to the task so the scheduler can keep track.
        task.setComponentUsed(labComponent);
        return true;
    }

    /**
     * Unschedule the given task, freeing up its time slot within the lock.
     * @param task the task being unscheduled.
     * @return Whether or not the task was scheduled to begin with.
     */
    public boolean unscheduleTask(Task task){
        for(int i = 0; i < taskSchedule.size(); i++){
            if(taskSchedule.get(i) == task){
                taskSchedule.remove(i);
                lockSchedule.remove(i);
                task.setComponentUsed(null);
                return true;
            }
        }
        return false;
    }
}
