package com.fredrik.wakeup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;


public class AlarmBroadcastReceiver extends BroadcastReceiver {

    public static PowerManager.WakeLock wakeLock = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(wakeLock == null) {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    "MyWakelockTag");
            wakeLock.acquire();
        }

        Intent intent1 = new Intent(context,TaskTimer.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }
}
