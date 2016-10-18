package com.fredrik.wakeup;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



//TODO
public class WakeupDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WakeupDatabase";
    private static final int VERSION = 1;


    public WakeupDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
