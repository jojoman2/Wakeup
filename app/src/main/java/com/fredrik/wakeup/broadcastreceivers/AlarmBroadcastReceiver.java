package com.fredrik.wakeup.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import com.fredrik.wakeup.activities.TaskTimer;


public class AlarmBroadcastReceiver extends BroadcastReceiver {

    public static final String INTENT_EXTRA_ALARM_TIME = "militime";

    private static final long WAKELOCK_TIMEOUT = 10000;
    private static final String WAKELOCK_TAG = "WakeAppLockToStartActivity";

    private static PowerManager.WakeLock wakeLock = null;

    @Override
    public void onReceive(Context context, Intent inIntent) {
        if(wakeLock == null || (!wakeLock.isHeld())) {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG);
            wakeLock.acquire(WAKELOCK_TIMEOUT);
        }
        long militime = inIntent.getExtras().getLong(INTENT_EXTRA_ALARM_TIME);

        Intent outIntent = new Intent(context,TaskTimer.class);
        outIntent.putExtra(INTENT_EXTRA_ALARM_TIME,militime);
        outIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(outIntent);
    }

    public static void releaseAnyWakelock(){
        if(wakeLock != null){
            if(wakeLock.isHeld()) {
                wakeLock.release();
            }
            wakeLock = null;
        }
    }
}
