package com.autolabucr;

import java.util.ArrayList;

/**
 * Created by markd on 4/12/2016.
 */

public class Task {
    private String name = "";

    private Range timeRange;

    ArrayList<String> componentTypesNeeded;
    //ArrayList<Component> componentsAssigned;

    ArrayList<LockSchedule> locksHeld;

    public Task(String json) {
        //TODO: Add JSON parser here.
    }

    public String toJSON() {
        //TODO: Reverse JSON parser here.
        return "";
    }

    public String toString() {
        return name;
    }

    //public ArrayList<ResourceID>
}
