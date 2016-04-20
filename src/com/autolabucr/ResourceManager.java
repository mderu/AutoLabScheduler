package com.autolabucr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by markd on 4/19/2016.
 */
public class ResourceManager {
    private static Map<String, Integer> totalResources;
    private static Map<String, Integer> reservedResources;

    private ResourceManager() {
        totalResources = new HashMap<>();

        for(int i = 0; i < Well.allWells.size(); i++) {
            Well curWell = Well.allWells.get(i);
            if(!totalResources.containsKey(Well.allWells.get(i).resource.name)) {
                totalResources.put(curWell.resource.name, curWell.resource.amount);
            }
            else {
                totalResources.put(curWell.resource.name, totalResources.get(curWell.resource.name) + curWell.resource.amount);
            }
        }

        reservedResources = new HashMap<String, Integer>(totalResources);
    }

    public static boolean ReserveResources(Map<String, Integer> requiredResources) {
        
        ArrayList<String> keySet = new ArrayList<>();
        keySet.addAll(requiredResources.keySet());
        
        for(int i = 0; i < keySet.size(); i++) {
            if(!totalResources.containsKey(keySet.get(i))) {
                System.out.println("Error: lab does not contain resource " + keySet.get(i));
                return false;
            }
            else if (reservedResources.get(keySet.get(i)) + requiredResources.get(keySet.get(i)) > totalResources.get(keySet.get(i))){
                System.out.println("Error: not enough " + keySet.get(i) + ". Asking for " + 
                        requiredResources.get(keySet.get(i)) + 
                        "nl, contains " + 
                        (totalResources.get(keySet.get(i)) - reservedResources.get(keySet.get(i))) + "nl.");
                return false;
            }
        }


        for(int i = 0; i < keySet.size(); i++) {
            reservedResources.put(keySet.get(i), reservedResources.get(keySet.get(i)) + requiredResources.get(keySet.get(i)));
        }
        return true;
    }
}
