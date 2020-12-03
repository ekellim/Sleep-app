package com.example.sleep_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.app.PendingIntent;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;

import static java.lang.Integer.parseInt;

public class SleepService extends Service implements SensorEventListener {
    public static final String TIMER = "com.example.myfirstapp.MESSAGE";
    public static final String ACTIVITY_ID = "com.example.myfirstapp.ACTIVITY_ID";
    public static final String CHANNEL_ID = "channelId1";
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    private static double ACTIVITY_THRESHOLD = 0.2;

    private static int READINGRATE = 20000; // time in us

    SensorManager sensorManager;
    Sensor sensor;
    Measurement measurement;
    int activityId;
    Handler handler;
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
        //sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensor, READINGRATE); //50Hz

        Intent notificationIntent = new Intent(this, MeasureActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Sleep app")
                .setContentText("Slaap wordt gemeten, alarm gaat af om " + timer[0] + ":" + timer[1] + ".")
                .setSmallIcon(R.drawable.set_alarm_icon)
                .setContentIntent(pendingIntent)
                .build();

        notificationIntent.putExtra(TIMER, timer);
        notificationIntent.putExtra(ACTIVITY_ID, activityId);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.sleepapp.UNREGISTER");
        registerReceiver(receiver, intentFilter);

        createTask();
        startForeground(1, notification);


        return START_REDELIVER_INTENT;
    }

    public void createTask(){
        //Om de 1 min wordt een gemiddelde van de metingen genomen.
        //Dan wordt deze waarde in de db opgeslagen en een nieuwe meting gestart.
        //Dit gebeurd met deze TimerTask
        handler = new Handler();
        long delay = 1*60*1000;
        //long delay = 10*1000; //for testing
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
        Log.d("TIMER", "current time = "+time+" vs set time "+timer[0]+":"+timer[1]+"-"+timer[2]);
        if(time.split("-")[1] == timer[2]) {
            if (parseInt(timesplit[0]) >= parseInt(timer[0])) {
                if (parseInt(timesplit[1]) >= parseInt(timer[1])) {
                    Log.d("TIMER EXPIRED", "Timer expired, service shutting down");
                    sensorManager.unregisterListener(this, sensor);
                    unregisterReceiver(receiver);
                    stopSelf();
                }
            }
        }
        measurement = new Measurement(-1, activityId, time);
        sensorManager.registerListener(this, sensor, READINGRATE); //50Hz
        //sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
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
           if (value >= ACTIVITY_THRESHOLD){
               this.measurement.IncrementActivityCounter();
           }
           intent1.putExtra("DATAPASSED", value);
           sendBroadcast(intent1);
       }
   }

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("Boadcast receiver", "Receiver has received! items wil be unregistred.");
            unregister();
            stopSelf();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void unregister(){
        sensorManager.unregisterListener(this, sensor);
        handler.removeCallbacksAndMessages(null);
        measurement.Merge();
        unregisterReceiver(receiver);
    }

    @Override
    public void onDestroy(){
        Log.d("OnDestroy", "service wordt afgesloten via destroy");
        /*
        try{
            sensorManager.unregisterListener(this, sensor);
        } catch (Exception e){
            Log.d("OnDestroy", "Error on destroy : " + e.toString());
        }
*/
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}
