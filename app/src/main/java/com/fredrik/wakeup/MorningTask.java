package com.fredrik.wakeup;

public class MorningTask {

    private String name;
    private int secondsToDoIt;

    public MorningTask(String name, int secondsToDoIt) {
        this.name = name;
        this.secondsToDoIt = secondsToDoIt;
    }

    public String getName() {
        return name;
    }

    public int getSecondsToDoIt() {
        return secondsToDoIt;
    }
}
