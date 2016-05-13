package com.autolabucr;

/**
 * Created by markd on 4/5/2016.
 * This is the main class for the scheduler, created as a singleton so it can be accessed globally.
 *
 */
public class Scheduler {
    private static Scheduler instance = new Scheduler();

    private long currentTime = 0;

    public static Scheduler getInstance() {
        return instance;
    }

    /**
     * Returns the current time of the scheduler.
     * @return the current time of the scheduler
     */
    public static long getCurrentTime() {
        return instance.currentTime;
    }

    //Here we need to pair a LabComponent with a Schedule

    private Scheduler() {
        currentTime = System.currentTimeMillis() / 1000L;
    }

    public static void scheduleTask(Task task) {

    }

    public static void storeExperiment(String Experiment, int jobId){

    }

    /*public static boolean scheduleExperiment(Experiment exp){

    }*/
}
