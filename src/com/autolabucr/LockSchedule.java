package com.autolabucr;

import java.util.ArrayList;

/**
 * Created by markd on 4/12/2016.
 */

public class LockSchedule{
    private String lockName = "";
    private boolean locked = false;
    private Task currentTask = null;
    private ArrayList<Range> lockSchedule = new ArrayList<Range>();
    private ArrayList<Task> taskSchedule = new ArrayList<Task>();


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
     * Returns whether or not the {@code Lock} is available throughout the given {@code range}.
     * @param range The {@code range} to check if the {@code Lock} is completely free for.
     * @return Whether or not the lock was free.
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

    public boolean reserveLock(Range range, Task task) {
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
        return true;
    }
}
