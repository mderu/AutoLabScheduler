package com.autolabucr;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by markd on 4/19/2016.
 */
public class LabComponent implements Serializable {

    public static ArrayList<LabComponent> allComponents = new ArrayList<LabComponent>();

    LockSchedule lockSchedule;
    String name;

    public LabComponent(String name) {
        allComponents.add(this);
        lockSchedule = new LockSchedule();
        this.name = name;
    }
}
