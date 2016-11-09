package com.fredrik.wakeup.other;

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
    private static final String COLUMN_TASK_JSON = "taskJson";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_ALARM_TABLE_SQL="CREATE TABLE IF NOT EXISTS "+ ALARMS_TABLE_NAME +" ("+
                COLUMN_ALARM_TIMESTAMP +" INTEGER PRIMARY KEY,"+
                COLUMN_TASK_JSON + " TEXT"+
        ")";

        db.execSQL(CREATE_ALARM_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public void addAlarmTimestamp(MorningAlarm alarm){


        long timestamp = alarm.getTimestamp();
        MorningTask[] tasks = alarm.getTasksToPerform();

        String tasksJson = TaskFormatTransform.toJson(tasks);

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ALARM_TIMESTAMP, timestamp);
            values.put(COLUMN_TASK_JSON,tasksJson);

            db.insert(ALARMS_TABLE_NAME, null, values);
        }
        finally {
            db.close();
        }
    }

    public MorningTask[] getTasksFromTimestamp(long timestamp){
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = null;
        try {
            cursor = db.query(ALARMS_TABLE_NAME, new String[]{COLUMN_TASK_JSON}, COLUMN_ALARM_TIMESTAMP + " = ?", new String[]{Long.toString(timestamp)}, null, null, null,"1");
                cursor.moveToNext();
                String taskJson = cursor.getString(0);

                return TaskFormatTransform.fromJson(taskJson);
        }
        finally {
            if(cursor != null){
                cursor.close();
            }
            db.close();
        }
    }


    public long[] getAlarmTimestamps(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.query(ALARMS_TABLE_NAME, new String[]{COLUMN_ALARM_TIMESTAMP}, null, null, null, null, COLUMN_ALARM_TIMESTAMP);
            int numberOfAlarms = cursor.getCount();

            long[] timestamps = new long[numberOfAlarms];
            for(int i = 0; i < numberOfAlarms; i++){
                cursor.moveToNext();
                timestamps[i] = cursor.getLong(0);
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

    public void removeSpecificAlarm(long timeStamp){
        removeAlarm(timeStamp,"=");
    }

    public void removeAlarmsOlderThan(long timeStamp){
        removeAlarm(timeStamp,"<");
    }

    private void removeAlarm(long timeStamp, String compactionSign){
        SQLiteDatabase db = getWritableDatabase();
        try {
            String timeStampStr = Long.toString(timeStamp);
            db.delete(ALARMS_TABLE_NAME, COLUMN_ALARM_TIMESTAMP + compactionSign+" ?", new String[]{timeStampStr});
        }
        finally {
            db.close();
        }
    }
}
