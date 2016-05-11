package com.autolabucr;

import com.autolabucr.Equipment.LabComponent;

/**
 * Created by markd on 4/12/2016.
 */

public class Task {

    public Range timeRange;
    private Resource resourceUsed;
    //Exact components used are determined by the scheduler.
    private LabComponent componentUsed;
    private Class componentType;

    private String command = "";

    public Task(Class tClass, String command, Range estTime) {
        componentType = tClass;
        this.command = command;
        timeRange = estTime;
    }

    public String getCommand(){return command;}

    public LabComponent getComponentUsed(){
        return componentUsed;
    }

    public Resource getResourceUsed(){
        return resourceUsed;
    }

    public void setComponentUsed(LabComponent labComponent){
        componentUsed = labComponent;
    }

    public Class getComponentType(){
        return componentType;
    }

    //public ArrayList<ResourceID>
}
