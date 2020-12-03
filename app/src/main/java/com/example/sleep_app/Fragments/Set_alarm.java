package com.example.sleep_app.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sleep_app.MeasureActivity;
import com.example.sleep_app.MyDBHandler;
import com.example.sleep_app.R;
import com.example.sleep_app.Sleep;
import com.example.sleep_app.SleepService;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.Integer.parseInt;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Set_alarm extends Fragment {

    public static final String TIMER = "com.example.myfirstapp.MESSAGE";
    public static final String ACTIVITY_ID = "com.example.myfirstapp.ACTIVITY_ID";
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    public static final String CHANNEL_ID = "sleepAppServiceChannel";

    SensorManager sensorManager;
    Sensor sensor;
    TextView textView;
    String values[];
    TimePicker timePicker;
    TimePicker.OnTimeChangedListener setTime;
    MyDBHandler db;
    Button startButton;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            int flags = View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

            Activity activity = getActivity();
            if (activity != null
                    && activity.getWindow() != null) {
                activity.getWindow().getDecorView().setSystemUiVisibility(flags);
            }
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }

        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    private View mContentView;
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            //hide();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_set_alarm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVisible = true;

        final TextView title = view.findViewById(R.id.title);
        title.setText("Set your alarm clock");
        timePicker = view.findViewById(R.id.timePicker);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //db = new MyDBHandler();
        startButton = (Button) view.findViewById(R.id.start_button);
        startButton.setActivated(false);
        startButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(!startButton.isActivated()){
                    Snackbar.make(view, "You have to set an alarm first", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                } else {
                    startSleepMeasure(v);
                    //testService(v);
                }
            }
        });

        Button setAlarmButton = view.findViewById(R.id.setAlarm_button);
        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                setAlarm(v);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setAlarm(View view){
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);

        startButton.setActivated(true);

        intent.putExtra(AlarmClock.EXTRA_HOUR, timePicker.getHour());
        intent.putExtra(AlarmClock.EXTRA_MINUTES, timePicker.getMinute());
        startActivity(intent);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void testService(View v){
        Intent intent = new Intent(getActivity(), SleepService.class);
        Sleep activity;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss-dd/MM/yy");
        LocalDateTime now = LocalDateTime.now();
        try {
            activity = new Sleep(-1, dtf.format(now), "null");
        } catch (Exception e){
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
            activity = new Sleep(-1, "error", "error");
        }
        MyDBHandler dbHandler = new MyDBHandler(getActivity());
        dbHandler.addActivityHandler(activity);
        int activityId = dbHandler.GetLastSleepId();
        //values = String.format("%s:%s",timePicker.getHour(),timePicker.getMinute());
        values = new String[]{Integer.toString(timePicker.getHour()), Integer.toString(timePicker.getMinute())};

        intent.putExtra(TIMER, values);
        intent.putExtra(ACTIVITY_ID, Integer.toString(activityId));
        getActivity().startService(intent);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startSleepMeasure(View view){
        createNotification();
        Sleep activity;
        Intent intent = new Intent(getActivity(), MeasureActivity.class);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss-dd/MM/yy");
        LocalDateTime now = LocalDateTime.now();
        try {
            activity = new Sleep(-1, dtf.format(now), "null");
        } catch (Exception e){
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
            activity = new Sleep(-1, "error", "error");
        }
        MyDBHandler dbHandler = new MyDBHandler(getActivity());
        dbHandler.addActivityHandler(activity);
        int activityId = dbHandler.GetLastSleepId();
        values = new String[]{Integer.toString(timePicker.getHour()), Integer.toString(timePicker.getMinute()), getDay()};

        intent.putExtra(TIMER, values);
        intent.putExtra(ACTIVITY_ID, Integer.toString(activityId));
        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDay(){
        String day = "";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss-dd/MM/yy");
        LocalDateTime now = LocalDateTime.now();
        String dateTime = dtf.format(now);
        if(parseInt(dateTime.split(":")[0]) >= timePicker.getHour()) {
            if (parseInt(dateTime.split(":")[1]) >= timePicker.getMinute()) {
                int d = parseInt(dateTime.split("-")[1].split("/")[0])+1;
                int m = parseInt(dateTime.split("-")[1].split("/")[1]);
                int y = parseInt(dateTime.split("-")[1].split("/")[2]);
                day = d+"/"+m+"/"+y;
            }
        }
        else{
            day = dateTime.split("-")[1];
        }
        return day;
    }

    public void createNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,"Sleep app Service Channel",NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == -1) {
                Log.i("my_tag", "Ik kom hier");
                MyDBHandler dbHandler = new MyDBHandler(getActivity());
                int activityId = dbHandler.GetLastSleepId();
                dbHandler.UpdateStopTime(activityId, data.getStringExtra(Intent.EXTRA_TEXT));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
        //NavHostFragment.findNavController(this).navigate(R.id.action_nav_first_fragment_to_nav_second_fragment);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            // Clear the systemUiVisibility flag
            getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
        }
//        show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContentView = null;
        mControlsView = null;
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Nullable
    private ActionBar getSupportActionBar() {
        ActionBar actionBar = null;
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            actionBar = activity.getSupportActionBar();
        }
        return actionBar;
    }



}