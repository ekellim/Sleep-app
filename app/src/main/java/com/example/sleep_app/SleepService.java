package com.example.sleep_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.util.Measure;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.app.PendingIntent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static java.lang.Integer.parseInt;

public class SleepService extends Service implements SensorEventListener {
    public static final String TIMER = "com.example.myfirstapp.MESSAGE";
    public static final String ACTIVITY_ID = "com.example.myfirstapp.ACTIVITY_ID";
    public static final String CHANNEL_ID = "channelId1";
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;

    SensorManager sensorManager;
    Sensor sensor;
    Measurement measurement;
    int activityId;
    String timer[];
    Timer timerT;

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        String time = getCurrentTime();
        activityId = parseInt(intent.getExtras().getString(ACTIVITY_ID));
        timer = intent.getExtras().getStringArray(TIMER);

        measurement = new Measurement(-1, activityId, time);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        Intent notificationIntent = new Intent(this, MeasureActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Sleep app")
                .setContentText("Slaap wordt momenteel gemeten, alarm gaat af om " + timer[0] + ":" + timer[1] + ".")
                .setSmallIcon(R.drawable.set_alarm_icon)
                .setContentIntent(pendingIntent)
                .build();

        notificationIntent.putExtra(TIMER, timer);
        notificationIntent.putExtra(ACTIVITY_ID, activityId);

        createTask();

        startForeground(1, notification);
        Log.d("SleepService",  "Service started");
        //Log.d("SleepService",  "Timer task activated");

        return START_REDELIVER_INTENT;
    }

    public void createTask(){
        //Om de 5 min wordt een gemiddelde van de metingen genomen.
        //Dan wordt deze waarde in de db opgeslagen en een nieuwe meting gestart.
        //Dit gebeurd met deze TimerTask
        Handler handler = new Handler();
        long delay = 10*1000;

        Runnable runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                Log.d("SleepService",  "New measurement is going to start");
                startNewMeasurement();
                handler.postDelayed(this, delay);
            }
        };
        handler.postDelayed(runnable, delay);
    }

   public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID, "ForegroundNotification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }

   @RequiresApi(api = Build.VERSION_CODES.O)
   private String getCurrentTime(){
       DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss-dd/MM/yy");
       LocalDateTime now = LocalDateTime.now();
       return dtf.format(now);
   }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startNewMeasurement(){
        //measurement.ShowValueList();
        sensorManager.unregisterListener(this, sensor);
        measurement.Merge();

        MyDBHandler dbHandler = new MyDBHandler(this);
        dbHandler.addMeasurementHandler(measurement);
        //textView.setText("Updated db with next measurement: " + measurement.getTimestamp() +" and value : " + measurement.getValue());

        String time = getCurrentTime();
        String timesplit[] = time.split(":");

        if(parseInt(timesplit[0]) >= parseInt(timer[0])){
            if(parseInt(timesplit[1]) >= parseInt(timer[1])){
                sensorManager.unregisterListener(this, sensor);
                stopSelf();
            }
        }

        measurement = new Measurement(-1, activityId, time);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

   @Override
   public void onSensorChanged(SensorEvent event) {
       if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
           Intent intent1 = new Intent();
           intent1.setAction("com.example.myfirstapp.DATAPASSED");

           double value = (Math.abs(event.values[0])+Math.abs(event.values[1])+Math.abs(event.values[2]))/3;
           //double value = Math.max(Math.abs(event.values[0]), Math.max(Math.abs(event.values[1]), Math.abs(event.values[2])));
           Log.d("SENSOR Value", "onSensorChanged: value to db = " + value  );
           this.measurement.AddMeasurement(value);
           intent1.putExtra("DATAPASSED", value);
           sendBroadcast(intent1);
       }
   }

    @Override
    public void onDestroy(){
        sensorManager.unregisterListener(this, sensor);
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onStop(){
        sensorManager.unregisterListener(this, sensor);
    }



}
