package com.example.sleep_app;

public class Activity {
    private int ActivityId;
    String start;
    String stop;

    //constructor
    public Activity(int id, String start, String stop){
        this.ActivityId = id;
        this.start = start;
        this.stop = stop;
    }

    public Activity(){

    }

    //getters and setters
    public int getActivityId() {
        return ActivityId;
    }

    public void setActivityId(int activityId) {
        ActivityId = activityId;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }
}
