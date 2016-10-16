package com.fredrik.wakeup;

/**
 * Created by Fredrik on 17-Oct-16.
 */
public class DefaultTasks {

    public static MorningTask[] getDefaultTasks(){
        return new MorningTask[]{
            new MorningTask("Eat breakfest",6),
            new MorningTask("Shower",10),
            new MorningTask("Get dressed",6),
            new MorningTask("Pack stuff and leave apartement",5)
        };
    }
}
