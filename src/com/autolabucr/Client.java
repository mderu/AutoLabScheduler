package com.autolabucr;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by HoRay on 5/13/2016.
 */
public class Client implements Runnable{

    private static int PORT = 2094;
    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;

    @Override
    public void run() {

        try {
            while (true) {

                try {
                    receiveMessage();
                } catch (Exception e) {

                }

            }
        }finally{
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void receiveMessage() throws Exception {
        String serverString;
        final Gson gson = new Gson();

        while (true) {
            serverString = in.readLine();
            JOptionPane.showMessageDialog(null, serverString);
            JsonObject command = gson.fromJson(serverString, JsonObject.class);

            if(command.get("Experiment").getAsString() != null){
                Scheduler.storeExperiment(command.get("Experiment").getAsString(), command.get("jobID").getAsInt());
            }
            else if(command.get("Request").getAsString() != null) {
                //sendSupply();
            }
            else{
                JsonObject report = new JsonObject();
                report.addProperty("command", "invalid");
            }

        }

    }

    public static void send(boolean Success, int startTime, int endTime, int jobID){
        JsonObject report = new JsonObject();

        report.addProperty("success", Success);
        report.addProperty("start", startTime);
        report.addProperty("end", endTime);
        report.addProperty("jobID", jobID);

        out.println(report);


    }
    public static void send(boolean Success, boolean lowResources, String errorMessage){
        JsonObject report = new JsonObject();

        report.addProperty("success", Success);
        report.addProperty("lowResources", lowResources);
        report.addProperty("errorMessage", errorMessage);

        out.println(report);
    }

    public static void send(int jobID, int startTime, int endTime){
        JsonObject report = new JsonObject();

        report.addProperty("jobID", jobID);
        report.addProperty("start", startTime);
        report.addProperty("end", endTime);

        out.println(report);
    }

    //This will be an array commented out for now because we will decide on the format of how the data will be sent
    public static void sendSupply(/*String substance, int amount, int units*/)
    {
        /*JsonObject report = new JsonObject();

        report.addProperty("substance", substance);
        report.addProperty("amount", amount);
        report.addProperty("nano", units);

        out.println(report);*/
    }

    public static void initialization() throws Exception {

        String serverAddress = "localhost";

        socket = new Socket(serverAddress, PORT);
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        Client clientThread = new Client();
        Thread t = new Thread(clientThread);
        t.start();


    }

}
