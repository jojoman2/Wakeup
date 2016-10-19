package com.fredrik.wakeup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WakeupDatabase";
    private static final int VERSION = 1;

    private static final String ALARMS_TABLE_NAME = "alarms";
    private static final String COLUMN_ALARM_TIMESTAMP = "timestamp";

    private static final String TASK_TABLE_NAME = "tasks";
    private static final String COLUMN_TASK_NUMBER = "number";
    private static final String COLUMN_TASK_NAME = "name";
    private static final String COLUMN_TASK_TIME = "time";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_ALARM_TABLE_SQL="CREATE TABLE "+ ALARMS_TABLE_NAME +" ("+
                COLUMN_ALARM_TIMESTAMP +" INTEGER"+
        ")";

        final String CREATE_TASK_TABLE_SQL = "CREATE TABLE "+ TASK_TABLE_NAME + " ("+
                COLUMN_TASK_NUMBER +" INTEGER,"+
                COLUMN_TASK_NAME +" TEXT NOT NULL,"+
                COLUMN_TASK_TIME +"INTEGER"+
        ")";

        db.execSQL(CREATE_ALARM_TABLE_SQL);
        db.execSQL(CREATE_TASK_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public void addAlarmTimestamp(long timestamp){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ALARM_TIMESTAMP,timestamp);

        db.insert(ALARMS_TABLE_NAME,null,values);

        db.close();
    }

    public long[] getAlarmTimestamps(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.query(ALARMS_TABLE_NAME, new String[]{COLUMN_ALARM_TIMESTAMP}, null, null, null, null, COLUMN_ALARM_TIMESTAMP);
            int numberOfAlarms = cursor.getCount();

            long[] timestamps = new long[numberOfAlarms];
            for(int i = 0; i < numberOfAlarms; i++){
                timestamps[i] = cursor.getLong(i);
            }
            return timestamps;
        }
        finally {
            if(cursor != null){
                cursor.close();
            }
            db.close();
        }
    }

    public void removeAlarmsOlderThan(long timeStamp){
        SQLiteDatabase db = getWritableDatabase();

        String timeStampStr = Long.toString(timeStamp);
        db.delete(ALARMS_TABLE_NAME, COLUMN_ALARM_TIMESTAMP + "< ?",new String[]{timeStampStr});

        db.close();
    }
}
