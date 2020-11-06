package com.example.sleep_app;

public class Sleep {
    private int SleepId;
    String start;
    String stop;

    //constructor
    public Sleep(int id, String start, String stop){
        this.SleepId = id;
        this.start = start;
        this.stop = stop;
    }

    public Sleep(){

    }

    //getters and setters
    public int getSleepId() {
        return SleepId;
    }

    public void setSleepId(int SleepId) {
        SleepId = SleepId;
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