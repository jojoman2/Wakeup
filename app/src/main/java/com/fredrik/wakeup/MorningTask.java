package com.fredrik.wakeup;

public class MorningTask {

    private String name;
    private int secondsToDoIt;
    private boolean useSoundAlarm;

    public MorningTask(String name, int secondsToDoIt, boolean useSoundAlarm) {
        this.name = name;
        this.secondsToDoIt = secondsToDoIt;
        this.useSoundAlarm = useSoundAlarm;
    }

    public String getName() {
        return name;
    }

    public int getSecondsToDoIt() {
        return secondsToDoIt;
    }

    public boolean useSoundAlarm(){return useSoundAlarm;}
}
