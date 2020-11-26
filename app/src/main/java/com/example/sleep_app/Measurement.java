package com.example.sleep_app;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.concurrent.locks.ReentrantLock;

public class Measurement {
    private int MeasurementID;
    private int SleepID;
    List<Double>  ValueList = new ArrayList<Double>();
    String Timestamp = null;
    Double Value = 0.0;
    ReentrantLock lock = new ReentrantLock();

    public Measurement(int measurementID, int sleepID, String timestamp) {
        MeasurementID = measurementID;
        SleepID = sleepID;
        if(Timestamp == null) Timestamp = timestamp;
    }

    public void AddMeasurement(double value){
        this.ValueList.add(value);
    }

    public void ShowValueList(){
        Log.d("MERGE", "Merge is started");
        for( Double i : ValueList){
            Log.d("MERGE", "Value: " + i);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void Merge(){
        synchronized (ValueList) {
            try {
                //Value = ValueList.stream().mapToDouble(val -> val).average().getAsDouble();
                if (!ValueList.isEmpty()) {
                    for (Double i : ValueList) {
                        Value += i;
                    }
                    Value = Value / ValueList.size();
                } else {
                    Log.d("ERROR MERGE", "Merge: VALUELIST was empty");
                    Value = 0.0;
                }
            } catch (Exception e) {
                Log.d("ERROR MERGE", "Merge: ERROR in merge : " + e.toString());
            }
        }
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
