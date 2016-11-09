package com.fredrik.wakeup.other;

/**
 * Created by Fredrik on 02-Nov-16.
 */
public class MorningAlarm {

    private long timestamp;
    private MorningTask[] tasksToPerform;

    public MorningAlarm(long timestamp, MorningTask[] tasksToPerform) {
        this.timestamp = timestamp;
        this.tasksToPerform = tasksToPerform;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public MorningTask[] getTasksToPerform() {
        return tasksToPerform;
    }


}
