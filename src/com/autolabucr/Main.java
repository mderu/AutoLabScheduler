package com.autolabucr;

import java.io.*;
import java.util.ArrayList;

public class Main {

    public static void writeEquipment(){
        try {
            FileOutputStream fileOut =  new FileOutputStream("Equipment.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(LabComponent.allComponents);
            out.close();
            fileOut.close();
            System.out.println("Serialized Wells info stored in Wells.ser");
        }
        catch(IOException i) {
            i.printStackTrace();
        }
    }

    public static void loadEquipment(){
        try {
            FileInputStream fileIn = new FileInputStream("Equipment.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            LabComponent.allComponents = (ArrayList<LabComponent>) in.readObject();
            in.close();
            fileIn.close();
        }
        catch(IOException i) {
            i.printStackTrace();
            return;
        }
        catch(ClassNotFoundException c) {
            System.out.println("ArrayList<Well> not found");
            c.printStackTrace();
            return;
        }
    }

    public static void main(String[] args)
    {
        writeEquipment();
        loadEquipment();
        ResourceManager.beginResourceTracking();
    }
}
