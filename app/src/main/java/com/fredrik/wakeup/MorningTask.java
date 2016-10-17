package com.fredrik.wakeup;

public class MorningTask {

    private String name;
    private int secondsToDoIt;
    private boolean soundAlarm;

    public MorningTask(String name, int secondsToDoIt, boolean soundAlarm) {
        this.name = name;
        this.secondsToDoIt = secondsToDoIt;
        this.soundAlarm = soundAlarm;
    }

    public String getName() {
        return name;
    }

    public int getSecondsToDoIt() {
        return secondsToDoIt;
    }

    public boolean getSoundAlarm(){return soundAlarm;}
}
