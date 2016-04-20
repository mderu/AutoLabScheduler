package com.autolabucr;

import java.util.ArrayList;

/**
 * Created by markd on 4/19/2016.
 */
public class Well extends LabComponent{

    public static ArrayList<Well> allWells= new ArrayList<Well>();

    Resource resource;

    public Well(String name, Resource resource) {
        super(name);
        allWells.add(this);
        this.resource = resource;
    }


}
