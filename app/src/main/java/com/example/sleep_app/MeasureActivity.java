package com.example.sleep_app;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Integer.parseInt;

//import androidx.appcompat.app.AppCompatActivity;

public class MeasureActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    Sensor sensor;
    TextView textView;
    TextView alarm;
    Measurement measurement;
    int activityId;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        Intent intent = getIntent();
        textView = findViewById(R.id.measureResults);

        String time = getCurrentTime();
        activityId = parseInt(intent.getStringExtra(MainActivity.ACTIVITY_ID));
        measurement = new Measurement(-1, activityId, time);

        String clock = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        alarm = findViewById(R.id.alarm);
        alarm.setText(clock);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        //Om de 5 min wordt een gemiddelde van de metingen genomen.
        //Dan wordt deze waarde in de db opgeslagen en een nieuwe meting gestart.
        //Dit gebeurd met deze TimerTask
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                MeasureActivity.this.runOnUiThread(new Runnable(){
                    public void run(){
                        startNewMeasurement();
                    }
                });
            }
        };
        Timer timer = new Timer();
        long delay = 1*60*1000;
        long intervalPeriod = 1*60*1000;
        // schedules the task to be run in an interval
        timer.scheduleAtFixedRate(task, delay, intervalPeriod);
    }
    public void stopMeasurement(View v){
        finish();
        //View_stats_fragment view_stats_fragment = new View_stats_fragment();
        //FragmentManager manager = getFragmentManager();
        //FragmentTransaction transaction = manager.beginTransaction();
        //transaction.replace(R.id.container,view_stats_fragment,view_stats_fragment.toString());
        //transaction.addToBackStack(null);
        //transaction.commit();
        //manager.beginTransaction().replace(R.id.container, view_stats_fragment).commit();
        //getFragmentManager().beginTransaction().replace(R.id.content, view_stats_fragment).addToBackStack(null).commit();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getCurrentTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss-dd/MM/yy");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startNewMeasurement(){
        measurement.Merge();

        MyDBHandler dbHandler = new MyDBHandler(MeasureActivity.this);
        dbHandler.addMeasurementHandler(measurement);
        textView.setText("Updated db with next measurement: " + measurement.getTimestamp() +" and value : " + measurement.getValue());

        String time = getCurrentTime();
        measurement = new Measurement(-1, activityId, time);
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener((SensorEventListener) this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener((SensorEventListener) this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            double value = (event.values[0]+event.values[1]+event.values[2])/3;
            this.measurement.AddMeasurement(value);

            textView.setText(
                "x:" + event.values[0]+"\n"+
                "y:" + event.values[1]+"\n"+
                "z:" + event.values[2]
            );
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
