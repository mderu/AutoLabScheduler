package com.autolabucr;

import com.autolabucr.Equipment.LabComponent;

import java.util.ArrayList;

/**
 * Created by markd on 4/23/2016.
 */
public class Experiment {
    ArrayList< ArrayList<Task> > operationList = new ArrayList<>();
    ArrayList<TimingConstraint> timingList = new ArrayList<>();

    public Experiment (String JSON){
        ArrayList<Task> currentOperation = new ArrayList<>();
        currentOperation.add(new Task(Well.class, "ConsumeResource 5", Range.fromDuration(0, 15)));
        currentOperation.add(new Task(Well.class, "Pipette 5 Src Dst", Range.fromDuration(0, 15)));
        operationList.add(currentOperation);
        timingList.add(new TimingConstraint(TimingConstraint.Type.Eventually, 0));

        currentOperation.clear();
        //taskList.add(new Task());
    }

    public boolean schedule() {
        for(int i = 0; i < operationList.size(); i++) {
            ArrayList<Task> currentOperation = operationList.get(i);

            for(int j= 0; j < currentOperation.size(); i++) {
                Task currentTask = currentOperation.get(j);

                //Suppress warnings because we know it must return an ArrayList of LabComponents
                @SuppressWarnings("unchecked")
                ArrayList<LabComponent> components = LabComponent.getComponentsOfType(currentTask.getComponentType());
                boolean isScheduled = false;

                for(int k = 0; k < components.size(); k++){
                    LabComponent currentComponent = components.get(k);
                    if(currentComponent.getClass() == Well.class &&
                           !((Well)currentComponent).resource.name.equals(currentTask.getResourceUsed().name)){
                        continue;
                    }
                    switch (timingList.get(i).type){
                        case Overnight: {
                            //Overnight has a large amount of time that should be between experiments.
                            continue;
                        }
                        case Eventually: {
                            //For now, assume the time is simply the current task's time range.
                            currentComponent.lockSchedule.scheduleNextAvailability(currentTask.timeRange, currentTask);
                            isScheduled = true;
                            break;
                        }
                        case Immediately: {
                            isScheduled = currentComponent.lockSchedule.scheduleTask(currentTask.timeRange, currentTask);
                            //Bad stuff happens when scheduling immediates.
                            //If we cannot schedule this at any given time, then we have to remove the last
                            //task recursively until we no longer hit immediate tasks.
                        }
                    }
                    if(isScheduled){
                        break;
                    }
                }

                if(!isScheduled){
                    //Recursively remove tasks until we no longer have immediate tasks. Use the difference between
                    //The most recent attempt at scheduling the task with the next available time in the lockSchedule
                    //To help speed up the process of finding the proper time.
                }
            }
        }
        return false;
    }


}
