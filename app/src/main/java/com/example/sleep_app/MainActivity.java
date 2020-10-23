package com.example.sleep_app;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    SensorManager sensorManager;
    Sensor sensor;
    TextView textView;
    String values;
    TimePicker timePicker;
    TimePicker.OnTimeChangedListener setTime;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView title = findViewById(R.id.title);
        title.setText("Set your alarm clock");
        timePicker = findViewById(R.id.timePicker);

        values = String.format("%d:%d",timePicker.getHour(),timePicker.getCurrentMinute());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void launchTestActivity(View view){
        Intent intent = new Intent(this, MeasureActivity.class);
        String message = values;
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

}