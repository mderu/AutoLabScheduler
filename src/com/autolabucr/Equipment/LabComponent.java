package com.autolabucr.Equipment;

import com.autolabucr.LockSchedule;
import com.autolabucr.Well;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by markd on 4/19/2016.
 */
public class LabComponent implements Serializable {

    public static ArrayList<LabComponent> allComponents = new ArrayList<LabComponent>();

    public LockSchedule lockSchedule;
    String name;

    public LabComponent(String name) {
        allComponents.add(this);
        lockSchedule = new LockSchedule();
        this.name = name;
    }


    public static <T> ArrayList<?> getComponentsOfType(Class<T> tClass){
        if(tClass == Well.class){
            return Well.allWells;
        }
        //TODO: Add in the other subclasses
        else{
            System.err.println("Class of type " + tClass.toString() + "is an unknown subclass of LabComponent.");
            return null;
        }
    }
}
