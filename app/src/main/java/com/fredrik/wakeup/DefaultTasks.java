package com.fredrik.wakeup;

/**
 * Created by Fredrik on 17-Oct-16.
 */
public class DefaultTasks {

    public static MorningTask[] getDefaultTasks(){
        return new MorningTask[]{
            new MorningTask("Get out of bed",2*60,true),
            new MorningTask("Eat breakfest",10*60,false),
            new MorningTask("Shower",10*60,false),
            new MorningTask("Get dressed",6*60,false),
            new MorningTask("Pack stuff and leave apartement",5*60,false)
        };
    }
}
