package com.example.sleep_app;
import android.os.Build;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class Measurement {
    private int MeasurementID;
    private int SleepID;
    List<Double>  ValueList = new ArrayList<Double>();
    String Timestamp = null;
    Double Value;

    public Measurement(int measurementID, int sleepID, String timestamp) {
        MeasurementID = measurementID;
        SleepID = sleepID;
        if(Timestamp == null) Timestamp = timestamp;
    }

    public void AddMeasurement(double value){
        this.ValueList.add(value);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void Merge(){
        Value = ValueList.stream().mapToDouble(val -> val).average().orElse(0.0);
    }

    public int getMeasurementID() {
        return MeasurementID;
    }

    public void setMeasurementID(int measurementID) {
        MeasurementID = measurementID;
    }

    public int getSleepID() {
        return SleepID;
    }

    public void setSleepID(int sleepID) {
        SleepID = sleepID;
    }

    public double getValue() {
        return Value;
    }

    public void setValue(double value) {
        Value = value;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }
}
