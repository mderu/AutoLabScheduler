package com.autolabucr;

/**
 * Created by markd on 4/19/2016.
 */

import java.io.Serializable;

public class Resource implements Serializable {
    String name = "";

    /**
     * In nanoliters
     */
    int amount = 0;

    public Resource(String name, int amount){
        this.name = name;
        this.amount = amount;
    }
}
