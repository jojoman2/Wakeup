package com.fredrik.wakeup;

import android.app.IntentService;
import android.content.Intent;


public class ScheduledAlarm extends IntentService {

    public ScheduledAlarm() {
        super("ScheduledAlarm");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Intent dialogIntent = new Intent(this, TaskTimer.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);
    }
}
