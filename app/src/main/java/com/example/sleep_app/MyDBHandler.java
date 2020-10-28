package com.example.sleep_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHandler extends SQLiteOpenHelper {
    //information about database
    private static final String TABLE1 = "Activities";   // Table Name
    private static final int DATABASE_Version = 1;    // Database Version
    private static final String ACTIVITY_ID="Activity_id";     // Column I (Primary Key)
    public static final String START = "Start";
    public static final String STOP = "Stop";
    private static final String TABLE2 = "Measurements";   // Table Name
    private static final String MEASUREMENT_ID="Measurement_id";     // Column I (Primary Key)
    //private static final String ACTIVITYID="Activity_id";
    public static final String TIMESTAMP = "Timestamp";
    public static final String VALUE = "Value";

    //initialize the database
    public MyDBHandler(Context context) {
        super(context, "sleepApp.db", null, DATABASE_Version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String CREATE_TABLE1 = "CREATE TABLE IF NOT EXISTS " + TABLE1 + " ( " + ACTIVITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + START + " VARCHAR(30), " + STOP + " VARCHAR(30));";
            db.execSQL(CREATE_TABLE1);
            String CREATE_TABLE2 = "CREATE TABLE IF NOT EXISTS " + TABLE2 + " ( " + MEASUREMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TIMESTAMP + " VARCHAR(30)," + VALUE + " REAL," + ACTIVITY_ID + " integer, FOREIGN KEY (" + ACTIVITY_ID + ") REFERENCES " + TABLE1 + "(" + ACTIVITY_ID + "));";
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

    public void addActivityHandler(Activity activity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(START, activity.getStart());
        values.put(STOP, activity.getStop());
        db.insert(TABLE1, null, values);
        db.close();
    }

    public void addMeasurementHandler(Measurement measurement) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TIMESTAMP, measurement.getTimestamp());
        values.put(VALUE, measurement.getValue());
        values.put(ACTIVITY_ID, measurement.getActivityID());
        db.insert(TABLE2, null, values);
        db.close();
    }

    public String loadActivityHandler() {
        String result = "";
        String qry = "SELECT * FROM "+TABLE1;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(qry, null);
        while (cursor.moveToNext()){
            int result_0 = cursor.getInt(0);
            String result_1 = cursor.getString(1);
            result += String.valueOf(result_0) + " " + result_1 + System.getProperty("line.separator");
        }
        cursor.close();
        db.close();
        return result;
    }

    public int GetLastActivityId(){
        SQLiteDatabase db = this.getReadableDatabase();
        int id = -1;
        String qry = "SELECT id FROM " + TABLE1 + " WHERE id = ( SELECT MAX(" + ACTIVITY_ID +") FROM " + TABLE1 + ");";
        Cursor cursor = db.rawQuery(qry, null);
        while (cursor.moveToNext()){
            id = cursor.getInt(0);
        }
        return id;
    }

    public MainActivity findHandler(String start) {return null;}
    public boolean deleteHandler(int ID){return false;}
    public boolean UpdateHandler(int ID, String name){return false;}
}
