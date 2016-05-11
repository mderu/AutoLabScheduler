package com.autolabucr.Protocol;

import com.autolabucr.Equipment.RobotArm;
import com.autolabucr.Range;
import com.autolabucr.Task;

import com.google.gson.*;

import java.util.ArrayList;

/**
 * Created by markd on 5/10/2016.
 */
public class ProtocolBuilder {

    public static ArrayList<Task> BuildProtocol(String jsonProtocol){
        ArrayList<Task> tasks = new ArrayList<>();
        Gson gson = new GsonBuilder().create();
        JsonParser parser = new JsonParser();
        JsonArray protocol = parser.parse(jsonProtocol).getAsJsonArray();

        for(int i = 0; i < protocol.size(); i++){
            System.out.println("op: " + protocol.get(i).getAsJsonObject().get("op").getAsString());
            if(protocol.get(i).getAsJsonObject().get("op").getAsString().equals("pipette")){

                JsonArray groups = protocol.get(i).getAsJsonObject().get("groups").getAsJsonArray();
                for(int j = 0; j < groups.size(); j++){
                    System.out.println("transfer: " + groups.get(j).getAsJsonObject().get("transfer"));
                    if(groups.get(j).getAsJsonObject().get("transfer") != null){
                        tasks.addAll(buildTransfer(groups.get(j).getAsJsonObject().get("transfer")));
                    }else if(groups.get(j).getAsJsonObject().get("distribute") != null){
                        tasks.addAll(buildDistribute(groups.get(j).getAsJsonObject().get("distribute")));
                    }else if(groups.get(j).getAsJsonObject().get("consolidate") != null) {
                        tasks.addAll(buildConsolidate(groups.get(j).getAsJsonObject().get("consolidate")));
                    }else if(groups.get(j).getAsJsonObject().get("mix") != null) {
                        tasks.addAll(buildMix(groups.get(j).getAsJsonObject().get("mix")));
                    }
                }
            }
        }
        for(int i = 0; i < tasks.size(); i++){
            System.out.println(tasks.get(i).getCommand());
        }

        return tasks;
    }

    private static ArrayList<Task> buildTransfer(JsonElement jsonElement){
        ArrayList<Task> tasks = new ArrayList<>();
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        for(int i = 0; i < jsonArray.size(); i++){
            String from = jsonArray.get(i).getAsJsonObject().get("from").getAsString();
            String to = jsonArray.get(i).getAsJsonObject().get("to").getAsString();
            String volume = jsonArray.get(i).getAsJsonObject().get("volume").getAsString();
            String aspirateSpeed = jsonArray.get(i).getAsJsonObject().get("aspirate_speed").getAsString();
            String dispenseSpeed = jsonArray.get(i).getAsJsonObject().get("dispense_speed").getAsString();

            Task task = new Task(RobotArm.class,
                    from +" " +
                        to + " " +
                        volume + " " +
                        aspirateSpeed + " " +
                        dispenseSpeed,
                    Range.fromDuration(0,5));
            tasks.add(task);
            tasks.addAll(0, pipetteMix(jsonArray.get(i).getAsJsonObject().get("mix_before").getAsJsonObject(), from));
            tasks.addAll(pipetteMix(jsonArray.get(i).getAsJsonObject().get("mix_after").getAsJsonObject(), to));
        }
        return tasks;
    }

    private static ArrayList<Task> buildDistribute(JsonElement jsonElement){
        ArrayList<Task> tasks = new ArrayList<>();
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String from = jsonObject.get("from").getAsString();
        String aspirateSpeed = jsonObject.get("aspirate_speed").getAsString();
        JsonArray to = jsonObject.get("to").getAsJsonArray();
        tasks.addAll(pipetteMix(jsonObject.get("mix_before").getAsJsonObject(), from));

        for(int i = 0; i < to.size(); i++){
            Task task = new Task(RobotArm.class,
                    from +" " +
                            to.get(i).getAsJsonObject().get("well").getAsString() + " " +
                            to.get(i).getAsJsonObject().get("volume").getAsString() + " " +
                            aspirateSpeed + " " +
                            to.get(i).getAsJsonObject().get("dispense_speed").getAsString(),
                    Range.fromDuration(0,5));
            tasks.add(task);
        }
        return tasks;
    }

    private static ArrayList<Task> buildConsolidate(JsonElement jsonElement){
        ArrayList<Task> tasks = new ArrayList<>();
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        JsonArray from = jsonObject.get("from").getAsJsonArray();
        String to = jsonObject.get("to").getAsString();
        String dispenseSpeed = jsonObject.get("dispense_speed").getAsString();
        for(int i = 0; i < from.size(); i++){
            Task task = new Task(RobotArm.class,
                    from.get(i).getAsJsonObject().get("well").getAsString() +" " +
                            to + " " +
                            from.get(i).getAsJsonObject().get("volume").getAsString() + " " +
                            from.get(i).getAsJsonObject().get("aspirate_speed").getAsString() + " " +
                            dispenseSpeed,
                    Range.fromDuration(0,5));
            tasks.add(task);
        }
        tasks.addAll(pipetteMix(jsonObject.get("mix_after").getAsJsonObject(), to));
        return tasks;
    }

    public static ArrayList<Task> buildMix(JsonElement jsonElement){
        ArrayList<Task> tasks = new ArrayList<>();
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        for(int i = 0; i < jsonArray.size(); i++){
            tasks.addAll(pipetteMix(jsonArray.get(i).getAsJsonObject()));
        }
        return tasks;
    }

    private static ArrayList<Task> pipetteMix(JsonObject mixInstructions){
        return pipetteMix(mixInstructions, "");
    }

    private static ArrayList<Task> pipetteMix(JsonObject mixInstructions, String well){
        ArrayList<Task> tasks = new ArrayList<>();
        assert mixInstructions != null;
        assert well != "" && mixInstructions.get("well").getAsString() != null;

        String volume = mixInstructions.get("volume").getAsString();
        well = well != "" ? well : mixInstructions.get("well").getAsString();
        String speed = well != "" ? well : mixInstructions.get("well").getAsString();
        int repetitions = 1;
        if(mixInstructions.get("repetitions") != null && mixInstructions.get("repetitions").getAsInt() > 0)
        {
            repetitions = mixInstructions.get("repetitions").getAsInt();
        }

        for(int i = 0; i < repetitions; i++){
            Task task = new Task(RobotArm.class,
                    well +" " +
                            well + " " +
                            volume + " " +
                            speed + " " +
                            speed,
                    Range.fromDuration(0,3));
            tasks.add(task);
        }

        return tasks;
    }

}
