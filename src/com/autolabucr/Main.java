package com.autolabucr;

import com.autolabucr.Equipment.LabComponent;
import com.autolabucr.Protocol.Protocol;
import com.autolabucr.Protocol.ProtocolBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {

    public static void writeEquipment(){
        try {
            FileOutputStream fileOut =  new FileOutputStream("Equipment.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            System.out.println(LabComponent.allComponents);
            out.writeObject(LabComponent.allComponents);
            out.close();
            fileOut.close();
            System.out.println("Serialized Equipment info stored in Equipment.ser");
        }
        catch(IOException i) {
            i.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
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
        new Well("WellA", new Resource("Kerosene", 100000));
        writeEquipment();
        loadEquipment();
        ResourceManager.beginResourceTracking();

        Protocol protocol = null;
        try{
            String jsonFile = new String(Files.readAllBytes(Paths.get("apJson.txt")), StandardCharsets.UTF_8);
            ProtocolBuilder.BuildProtocol(jsonFile);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            Client.initialization();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
