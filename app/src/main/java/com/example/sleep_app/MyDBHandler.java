package com.example.sleep_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHandler extends SQLiteOpenHelper {
    //information about database
    private static final String TABLE1 = "Activities";   // Table Name
    private static final String TABLE_NAME2 = "Measurements";   // Table Name
    private static final int DATABASE_Version = 1;    // Database Version
    private static final String ACTIVITY_ID="Activity_id";     // Column I (Primary Key)
    private static final String Measurement_ID="Measurement_id";     // Column I (Primary Key)
    public static final String START = "Start";
    public static final String STOP = "Stop";

    //initialize the database
    public MyDBHandler(Context context) {
        super(context, "sleepApp.db", null, 1);
    }

    private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE1;


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE1 = "CREATE TABLE " + TABLE1 + " ( "+ ACTIVITY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " + START + " VARCHAR(30), " + STOP + " VARCHAR(30));";
        //String CREATE_TABLE2 = "CREATE TABLE "+TABLE_NAME2+" ("+UID2+" INTEGER PRIMARY KEY AUTOINCREMENT, "+UID1+" INTEGER FOREIGN KEY  Timestamp VARCHAR(30));";
        try{
            db.execSQL(CREATE_TABLE1);
            //db.execSQL(CREATE_TABLE2);
        } catch (Exception e){
            //Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public String loadHandler() {
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

    public void addHandler(Activity activity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(START, activity.getStart());
        values.put(STOP, activity.getStop());

        long insert = db.insert(TABLE1, null, values);
    }

    public MainActivity findHandler(String start) {return null;}
    public boolean deleteHandler(int ID){return false;}
    public boolean UpdateHandler(int ID, String name){return false;}
}
