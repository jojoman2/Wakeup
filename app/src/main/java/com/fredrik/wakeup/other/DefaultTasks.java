package com.fredrik.wakeup.other;

/**
 * Created by Fredrik on 17-Oct-16.
 */
public class DefaultTasks {

    public static MorningTask[] getDefaultTasks(){
        return new MorningTask[]{
            new MorningTask("Turn of alarm",2*60),
            new MorningTask("Eat breakfest",13*60),
            new MorningTask("Shower",12*60),
            new MorningTask("Get dressed",8*60),
            new MorningTask("Pack stuff and leave apartement",5*60)
        };
    }
}
