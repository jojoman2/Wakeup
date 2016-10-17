package com.fredrik.wakeup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class SetupTime extends AppCompatActivity {

    private DatePicker datePicker;
    private TimePicker timePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        datePicker = (DatePicker)findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        Button scheduleButton = (Button) findViewById(R.id.scheduleButton);

        timePicker.setIs24HourView(true);
        //datePicker.getCalendarView().setFirstDayOfWeek(Calendar.MONDAY);

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();

                int hour;
                int minute;
                if (Build.VERSION.SDK_INT >= 23 ) {
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                }
                else {
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,day);
                calendar.set(Calendar.HOUR_OF_DAY,hour);
                calendar.set(Calendar.MINUTE,minute);
                calendar.set(Calendar.SECOND,0);
                long militime = calendar.getTimeInMillis();

                PendingIntent pi = PendingIntent.getService(SetupTime.this, 0,
                        new Intent(SetupTime.this, ScheduledAlarm.class),PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager)SetupTime.this.getSystemService(Context.ALARM_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,militime,pi);
                }
                else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP,militime,pi);
                }
                else{
                    alarmManager.set(AlarmManager.RTC_WAKEUP,militime,pi);
                }

                Toast.makeText(SetupTime.this,"Scheduled!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
