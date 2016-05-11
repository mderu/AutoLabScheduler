package com.autolabucr.Protocol;

import com.autolabucr.Equipment.RobotArm;
import com.autolabucr.Range;
import com.autolabucr.Task;

import com.google.gson.*;

import java.util.ArrayList;

/**
 * Created by markd on 5/10/2016.
 */

/* <rant title="AutoProtocol was Built by Monkeys">
 *
 *                     <section title="Mix And Transfer">
 * So AutoProtocol is a real work of art. Both the groups "transfer" and "mix"
 * are actually JsonArrays instead of JsonObjects. Sounds pretty benign and okay
 * until you realize that they are there to hold an array of different transfer
 * and mix operations. Cool, you might be thinking, but it's actually nonsensical.
 *
 * You see, transfer and mix are both stored and accessed in an array to begin
 * with, which means they already have the capability of having an array of mix
 * and transfers.
 *
 * But Mark, maybe they did this so the user won't have to specify "transfer" and
 * "mix" each time for a large set of transfers and mixes? Well first off, the
 * actual end user will never be writing in Json, and AutoProtocol makes it clear
 * that there is a simple and useful Python tool that will help with the creation
 * of the Json format. Second, if this was the case then "distribute" and
 * "consolidate" should also have the same array format instead of only one Json
 * object right? Well they don't.
 *
 * The addition of an array here is arbitrary and it makes another hoop for
 * developers to jump through in order to get the four similar pipette ops to
 * work.
 *                                 </section>
 *
 *                   <section title="Aspirate And Dispense">
 * And this one also bugs me to no end. Take a look at the "to" and "from" syntax.
 * Makes sense. You have a well and a volume if there are multiple to's and
 * from's, and only the well name when it's a single to or from. But then you
 * see the "aspirate_speed" and "dispense_speed". When there is only one to/from,
 * "aspirate_speed"/"dispense_speed" is found within the transfer object. What if
 * we have multiple to's and from's? Well, AutoProtocol believes we should create
 * a completely different syntax for this. Drop the speeds into the to's and
 * from's so we can control the speed for each of those transfers.
 *
 * Makes absolute sense, until you realize: why didn't they just put them there in
 * the first place, and if you want that level of detail, why don't you specify
 * the other speed for each transfer as well?
 *
 * The to's and from's should (optionally) contain "aspirate_speed" and
 * "dispense_speed" respectively, no matter if it's a transfer, distribute, or
 * consolidate. Even mix can have the same structure and it would be fine.
 *                                 </section>
 *
 * TL;DR: Monkeys.
 *
 * </rant>
 */
public class ProtocolBuilder {

    /**
     * Takes in an AutoProtocol script and breaks it down into tasks.
     * @param jsonProtocol A string consisting of the AutoProtocol in JSON format.
     * @return An {@code ArrayList<Task>} of the tasks specified by the protocol.
     */
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

    /**
     * Handles the building of op "pipette", group "transfer"
     * @param jsonElement The jsonElement "transfer" found in the groups from a pipette op. <br>
     *                    Example JSON can be found at http://autoprotocol.org/specification/ <br>
     *                    Note: do not pass in the group.
     * @return An {@code ArrayList<Task>} of the tasks specified by the transfer JsonObject.
     */
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

    /**
     * Handles the building of op "pipette", group "distribute"
     * @param jsonElement The jsonElement "distribute" found in the groups from a pipette op. <br>
     *                    Example JSON can be found at http://autoprotocol.org/specification/ <br>
     *                    Note: do not pass in the group.
     * @return An {@code ArrayList<Task>} of the tasks specified by the distribute JsonObject.
     */
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

    /**
     * Handles the building of op "pipette", group "consolidate"
     * @param jsonElement The jsonElement "consolidate" found in the groups from a pipette op. <br>
     *                    Example JSON can be found at http://autoprotocol.org/specification/ <br>
     *                    Note: do not pass in the group.
     * @return An {@code ArrayList<Task>} of the tasks specified by the consolidate JsonObject.
     */
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

    /**
     * Handles the building of op "pipette", group "mix"
     * @param jsonElement The jsonElement "mix" found in the groups from a pipette op. <br>
     *                    Example JSON can be found at http://autoprotocol.org/specification/ <br>
     *                    Note: do not pass in the group.
     * @return An {@code ArrayList<Task>} of the tasks specified by the mix JsonObject.
     */
    public static ArrayList<Task> buildMix(JsonElement jsonElement){

        ArrayList<Task> tasks = new ArrayList<>();
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        for(int i = 0; i < jsonArray.size(); i++){
            tasks.addAll(pipetteMix(jsonArray.get(i).getAsJsonObject()));
        }
        return tasks;
    }

    /**
     * Helper function to handle the "mix_before" and "mix_after" options on the
     * "transfer", "dispense", and "consolidate" groups from pipette ops. <br>
     * Consequently, this also works for the array elements of the "mix" group from pipette ops. <br>
     * Equivalent to pipetteMix(mixInstructions, "").
     * @param mixInstructions The {@code JsonObject} "mix_before", "mix_after", or an element of "mix".
     * @return The {@code ArrayList<Task>} of all tasks specified by the mix operation.
     */
    private static ArrayList<Task> pipetteMix(JsonObject mixInstructions){
        return pipetteMix(mixInstructions, "");
    }

    /**
     * Helper function to handle the "mix_before" and "mix_after" options on the
     * "transfer", "dispense", and "consolidate" groups from pipette ops. <br>
     * Consequently, this also works for the array elements of the "mix" group from pipette ops.
     * @param mixInstructions The {@code JsonObject} "mix_before", "mix_after", or an element of "mix".
     * @param well The name of the well that is being mixed. Null string if you are calling an element from "mix".
     * @return The {@code ArrayList<Task>} of all tasks specified by the mix operation.
     */
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
