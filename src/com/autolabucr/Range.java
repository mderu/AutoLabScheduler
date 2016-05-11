package com.autolabucr;

/**
 * Created by markd on 4/12/2016.
 */
public class Range {
    public long start;
    public long duration;
    public long end;

    private Range(long start, long end, long duration) {
        this.start = start;
        this.end = end;
        this.duration = duration;
    }

    public static Range fromEndpoints(long start, long end) {
        return new Range(start, end, end-start);
    }

    public static Range fromDuration(long start, long duration){
        return new Range(start, start+duration, duration);
    }
    /**
     * Note: This function is not thread safe.
     * @param duration how long in milliseconds to make the range.
     * @return A {@code Range} starting from {@code Scheduler.getCurrentTime()} for {@code duration} milliseconds.
     */
    public static Range forMilliseconds(long duration){
        return new Range(Scheduler.getCurrentTime(), Scheduler.getCurrentTime()+duration, duration);
    }

    /**
     * Note: This function is not thread safe.
     * @param end When to end the range.
     * @return A {@code Range} from {@code Scheduler.getCurrentTime()} to {@code end}.
     */
    public static Range until(long end){
        return new Range(Scheduler.getCurrentTime(), end, end-Scheduler.getCurrentTime());
    }
}
