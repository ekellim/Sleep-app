package com.example.sleep_app;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public static final String ACTIVITY_ID = "com.example.myfirstapp.ACTIVITY_ID";

    SensorManager sensorManager;
    Sensor sensor;
    TextView textView;
    String values;
    TimePicker timePicker;
    TimePicker.OnTimeChangedListener setTime;
    MyDBHandler db;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView title = findViewById(R.id.title);
        title.setText("Set your alarm clock");
        timePicker = findViewById(R.id.timePicker);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //db = new MyDBHandler();
        Button button= (Button) findViewById(R.id.start_button);
        button.setEnabled(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setAlarm(View v){
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);

        Button button= (Button) findViewById(R.id.start_button);
        button.setEnabled(true);

        intent.putExtra(AlarmClock.EXTRA_HOUR, timePicker.getHour());
        intent.putExtra(AlarmClock.EXTRA_MINUTES, timePicker.getMinute());
        startActivity(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void launchActivity(View v) {
        Activity activity;
        Intent intent = new Intent(MainActivity.this, MeasureActivity.class);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss-dd/MM/yy");
        LocalDateTime now = LocalDateTime.now();
        try {
            activity = new Activity(-1, dtf.format(now), "null");
        } catch (Exception e){
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            activity = new Activity(-1, "error", "error");
        }
        MyDBHandler dbHandler = new MyDBHandler(MainActivity.this);
        dbHandler.addActivityHandler(activity);
        int activityId = dbHandler.GetLastActivityId();
        values = String.format("%s:%s",timePicker.getHour(),timePicker.getMinute());

        intent.putExtra(EXTRA_MESSAGE, values);
        intent.putExtra(ACTIVITY_ID, Integer.toString(activityId));
        startActivity(intent);
    }


}