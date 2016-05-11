package com.autolabucr;

/**
 * Created by markd on 4/23/2016.
 */
public class TimingConstraint {
    public enum Type {Eventually, Overnight, Immediately};
    Type type;
    int time;

    public TimingConstraint(Type type, int time){
        this.type = type;
        this.time = time;
    }
}
