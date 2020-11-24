package com.example.sleep_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.jjoe64.graphview.series.DataPoint;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class MyDBHandler extends SQLiteOpenHelper {
    //information about database
    private static final String TABLE1 = "Activities";   // Table Name
    private static final int DATABASE_Version = 1;    // Database Version
    private static final String SLEEP_ID="Sleep_id";     // Column I (Primary Key)
    public static final String START = "Start";
    public static final String STOP = "Stop";
    private static final String TABLE2 = "Measurements";   // Table Name
    private static final String MEASUREMENT_ID="Measurement_id";     // Column I (Primary Key)
    //private static final String SleepID="Sleep_id";
    public static final String TIMESTAMP = "Timestamp";
    public static final String VALUE = "Value";

    //initialize the database
    public MyDBHandler(Context context) {
        super(context, "sleepApp.db", null, DATABASE_Version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String CREATE_TABLE1 = "CREATE TABLE IF NOT EXISTS " + TABLE1 + " ( " + SLEEP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + START + " VARCHAR(30), " + STOP + " VARCHAR(30));";
            db.execSQL(CREATE_TABLE1);
            String CREATE_TABLE2 = "CREATE TABLE IF NOT EXISTS " + TABLE2 + " ( " + MEASUREMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TIMESTAMP + " VARCHAR(30)," + VALUE + " REAL," + SLEEP_ID + " integer, FOREIGN KEY (" + SLEEP_ID + ") REFERENCES " + TABLE1 + "(" + SLEEP_ID + "));";
            //String CREATE_TABLE2 = "CREATE TABLE IF NOT EXISTS " + TABLE2 + " ( " + MEASUREMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TIMESTAMP + " VARCHAR(30)," + VALUE + "REAL);";
            db.execSQL(CREATE_TABLE2);
        } catch (Exception e){
            Log.e("MyDBHandler", e.getMessage());
        }
        //Log.i("TABLE_2", "Is created");        //try{
        //} catch (Exception e){
        //    Log.e("MyDBHandler", e.getMessage());
        //}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //Class Activity vervangen door Sleep, android studio heeft reeds een klasse Activity, voorkomt ook verwarring
    public void addActivityHandler(Sleep sleep) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(START, sleep.getStart());
        values.put(STOP, sleep.getStop());
        db.insert(TABLE1, null, values);
        db.close();
    }

    public void addMeasurementHandler(Measurement measurement) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TIMESTAMP, measurement.getTimestamp());
        values.put(VALUE, measurement.getValue());
        values.put(SLEEP_ID, measurement.getSleepID());
        db.insert(TABLE2, null, values);
        db.close();
    }

    public String loadSleepHandler() {
        String result = "";
        String qry = "SELECT * FROM "+TABLE1;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(qry, null);
        result += String.valueOf(cursor.getCount()) + System.getProperty("line.separator");
        while (cursor.moveToNext()){
            int result_0 = cursor.getInt(0);
            String result_1 = cursor.getString(1);
            result += String.valueOf(result_0) + " " + result_1 + System.getProperty("line.separator");
        }
        cursor.close();
        db.close();
        return result;
    }

    public int GetLastSleepId(){
        SQLiteDatabase db = this.getReadableDatabase();
        int id = -1;
        String qry = "SELECT MAX(Sleep_id) FROM " + TABLE1;
        Cursor cursor = db.rawQuery(qry, null);
        while (cursor.moveToNext()){
            id = cursor.getInt(0);
        }
        return id;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DataPoint[] getSleepData(int sleep_id){
        SQLiteDatabase db = this.getReadableDatabase();
        String qry = "SELECT Value, Timestamp FROM " + TABLE2+ " WHERE Sleep_id is " + sleep_id;
        Cursor cursor = db.rawQuery(qry, null);
        DataPoint[] value_time_pairs = new DataPoint[cursor.getCount()] ;
        int i = 0;
        while (cursor.moveToNext()){
            String timestamp = cursor.getString(1);
            DateTimeFormatter f = DateTimeFormatter.ofPattern("HH:mm:ss-dd/MM/yy");
            LocalDateTime dateTime = LocalDateTime.from(f.parse(timestamp));
            double time_of_day;
            time_of_day = 0;
            time_of_day = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            value_time_pairs[i] = new DataPoint(time_of_day, cursor.getFloat(0));
            i++;
        }
        return value_time_pairs;
    }

    public void UpdateStopTime(int sleep_id, String stop_time){
        SQLiteDatabase db = this.getWritableDatabase();
        String qry = "UPDATE Activities SET Stop = '" + stop_time + "' WHERE Sleep_id is " + sleep_id;
        db.execSQL(qry);
        db.close();
    }

    public MainActivity findHandler(String start) {return null;}
    public boolean deleteHandler(int ID){return false;}
    public boolean UpdateHandler(int ID, String name){return false;}
}
