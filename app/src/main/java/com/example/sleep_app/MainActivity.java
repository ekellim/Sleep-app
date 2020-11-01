package com.example.sleep_app;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    SensorManager sensorManager;
    Sensor sensor;
    TextView textView;
    String values;
    TimePicker timePicker;
    TimePicker.OnTimeChangedListener setTime;
    MyDBHandler db;

    private AppBarConfiguration mAppBarConfiguration;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_first_fragment, R.id.nav_second_fragment, R.id.nav_third_fragment)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startSleepMeasure(View view){
        Sleep activity;
        Intent intent = new Intent(MainActivity.this, MeasureActivity.class);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss-dd/MM/yy");
        LocalDateTime now = LocalDateTime.now();
        try {
            activity = new Sleep(-1, dtf.format(now), "null");
        } catch (Exception e){
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            activity = new Sleep(-1, "error", "error");
        }
        MyDBHandler dbHandler = new MyDBHandler(MainActivity.this);
        dbHandler.addActivityHandler(activity);

        //Toast.makeText(MainActivity.this, "success: " + success, Toast.LENGTH_SHORT).show();

        values = String.format("%s:%s",timePicker.getHour(),timePicker.getMinute());
        String message = values;
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void launchTestActivity(View view) {
        Sleep activity;
        Intent intent = new Intent(MainActivity.this, MeasureActivity.class);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss-dd/MM/yy");
        LocalDateTime now = LocalDateTime.now();
        try {
            activity = new Sleep(-1, dtf.format(now), "null");
        } catch (Exception e){
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            activity = new Sleep(-1, "error", "error");
        }
        MyDBHandler dbHandler = new MyDBHandler(MainActivity.this);
        dbHandler.addActivityHandler(activity);

        values = String.format("%s:%s",timePicker.getHour(),timePicker.getMinute());
        String message = values;
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}