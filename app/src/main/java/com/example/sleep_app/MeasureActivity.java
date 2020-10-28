package com.example.sleep_app;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

//import androidx.appcompat.app.AppCompatActivity;

public class MeasureActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    Sensor sensor;
    TextView textView;
    TextView alarm;
    Measurement measurement;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        Intent intent = getIntent();
        textView = findViewById(R.id.measureResults);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss-dd/MM/yy");
        LocalDateTime now = LocalDateTime.now();
        //Er moet nog gezorgd worden dat de ActivityID wordt meegegeven naar deze klasse zodat deze hier kan meegegeven worden masurement is toch autoincrement.
        measurement = new Measurement(-1, -1, dtf.format(now));

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
                measurement.Merge();

                MyDBHandler dbHandler = new MyDBHandler(MeasureActivity.this);
                dbHandler.addMeasurementHandler(measurement);

                //String message = "Measurement time: "+measurement.getTimestamp()+" Value: " +measurement.getValue();
                //Toast.makeText(MeasureActivity.this, message, Toast.LENGTH_SHORT).show();

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss-dd/MM/yy");
                LocalDateTime now = LocalDateTime.now();
                measurement = new Measurement(-1, -1, dtf.format(now));
            }
        };
        Timer timer = new Timer();
        long delay = 1*60*1000;
        long intervalPeriod = 1*60*1000;
        // schedules the task to be run in an interval
        timer.scheduleAtFixedRate(task, delay, intervalPeriod);
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
