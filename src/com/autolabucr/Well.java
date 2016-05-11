package com.autolabucr;

import com.autolabucr.Equipment.LabComponent;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by markd on 4/19/2016.
 */
public class Well extends LabComponent implements Serializable{

    public static ArrayList<Well> allWells= new ArrayList<>();

    public Resource resource;

    public Well(String name, Resource resource) {
        super(name);
        allWells.add(this);
        this.resource = resource;
    }
}
