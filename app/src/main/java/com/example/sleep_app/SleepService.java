package com.example.sleep_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.util.Measure;
import android.os.Build;
import android.os.IBinder;
import android.app.PendingIntent;
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
    String timer;


    @Override
    public void onCreate(){
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //createNotificationChannel();
        String time = getCurrentTime();
        activityId = parseInt(intent.getExtras().getString(ACTIVITY_ID));
        timer = intent.getExtras().getStringArray(TIMER)[0] + ":" +intent.getExtras().getStringArray(TIMER)[1];

        measurement = new Measurement(-1, activityId, time);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        Intent notificationIntent = new Intent(this, MeasureActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Sleep app")
                .setContentText("Slaap wordt momenteel gemeten, alarm gaat af om " + timer + ".")
                .setSmallIcon(R.drawable.set_alarm_icon)
                .setContentIntent(pendingIntent)
                .build();

        notificationIntent.putExtra(TIMER, timer);
        notificationIntent.putExtra(ACTIVITY_ID, activityId);

        //Om de 5 min wordt een gemiddelde van de metingen genomen.
        //Dan wordt deze waarde in de db opgeslagen en een nieuwe meting gestart.
        //Dit gebeurd met deze TimerTask
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Log.d("SleepService",  "New measurement is going to start");
                startNewMeasurement();
            }
        };
        Timer timer = new Timer();
        long delay = 1*60*1000;
        long intervalPeriod = 1*60*1000;
        // schedules the task to be run in an interval
        timer.scheduleAtFixedRate(task, delay, intervalPeriod);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        //startForeground(1, notification);
        Log.d("SleepService",  "Service started");
        //Log.d("SleepService",  "Timer task activated");

        return START_REDELIVER_INTENT;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.example.sleep_app";
        String channelName = "Sleep app";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.set_alarm_icon)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

   /* public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID, "ForegroundNotification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }*/

   @RequiresApi(api = Build.VERSION_CODES.O)
   private String getCurrentTime(){
       DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss-dd/MM/yy");
       LocalDateTime now = LocalDateTime.now();
       return dtf.format(now);
   }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startNewMeasurement(){
        measurement.Merge();

        MyDBHandler dbHandler = new MyDBHandler(this);
        dbHandler.addMeasurementHandler(measurement);
        //textView.setText("Updated db with next measurement: " + measurement.getTimestamp() +" and value : " + measurement.getValue());

        String time = getCurrentTime();
        measurement = new Measurement(-1, activityId, time);
    }

   @Override
   public void onSensorChanged(SensorEvent event) {
       if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
           double value = (event.values[0]+event.values[1]+event.values[2])/3;
           this.measurement.AddMeasurement(value);
       }
   }

    @Override
    public void onDestroy(){
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
}
