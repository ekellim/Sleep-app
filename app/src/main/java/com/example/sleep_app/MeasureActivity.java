package com.example.sleep_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.valueOf;

//import androidx.appcompat.app.AppCompatActivity;

public class MeasureActivity extends AppCompatActivity {

    public static final String TIMER = "com.example.myfirstapp.MESSAGE";
    public static final String ACTIVITY_ID = "com.example.myfirstapp.ACTIVITY_ID";
    TextView textView;
    TextView alarm;
    TextView measureText;
    Measurement measurement;
    int activityId;
    String values[];

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MeasureActivity.this);
        String deftheme = sharedPreferences.getString("mode", "light");
        if (deftheme.equals("night")){
            setTheme(R.style.NightTheme);
        }
        else if (deftheme.equals("light")){
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_running);
        Intent intent = getIntent();
        textView = findViewById(R.id.measureResults);

        String time = getCurrentTime();
        activityId = parseInt(intent.getStringExtra(MainActivity.ACTIVITY_ID));
        values = intent.getStringArrayExtra(MainActivity.TIMER);
        measurement = new Measurement(-1, activityId, time);

        //String clock = intent.getStringExtra(MainActivity.TIMER);
        alarm = findViewById(R.id.alarm);
        measureText = findViewById(R.id.measureResults);
        alarm.setText("Good night!");
        if (valueOf(values[0])>24){
            measureText.setText("No alarm set");
        }
        else{
            measureText.setText("Alarm: "+String.format("%02d", valueOf(values[0]))+"h"+String.format("%02d", valueOf(values[1])));
        }

        Intent intentService = new Intent(this, SleepService.class);
        intentService.putExtra(TIMER, values);
        intentService.putExtra(ACTIVITY_ID, Integer.toString(activityId));

        startService(intentService);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void stopMeasurement(View v) throws InterruptedException {
        Log.d("STOP_MEASUREMENT", "Measureactivity is stopping measurement via stopMeasurement.");
        Intent intentService = new Intent(this, SleepService.class);
        intentService.putExtra(Intent.EXTRA_TEXT, getCurrentTime());
        setResult(RESULT_OK, intentService);

        try {
            sendBroadcast();
            stopService(intentService);
        }
        catch (Exception e){
            Log.e("STOP MEASUREMENT", "stopMeasurement: service couldn't be stopped or has already stopped: " + e);
        }
        //Navigation.findNavController(v).navigate(R.id.action_nav_first_fragment_to_nav_second_fragment);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getCurrentTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss-dd/MM/yy");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public void sendBroadcast(){
        Intent intent = new Intent();
        intent.setAction("com.example.sleepapp.UNREGISTER");
        Log.d("sendBroadcast", "Broadcast will be send!");
        sendBroadcast(intent);
    }

}
