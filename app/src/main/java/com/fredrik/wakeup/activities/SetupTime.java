package com.fredrik.wakeup.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.*;
import com.fredrik.wakeup.R;
import com.fredrik.wakeup.broadcastreceivers.AlarmBroadcastReceiver;
import com.fredrik.wakeup.other.*;

import java.util.Calendar;

public class SetupTime extends AppCompatActivity {

    private static final String LAST_TASKS_JSON_PREF = "lastTasksJson";

    private DatePicker datePicker;
    private TimePicker timePicker;
    private EditText taskTextField;

    private SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_time);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        taskTextField = (EditText) findViewById(R.id.task_text_field);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        Button scheduleButton = (Button) findViewById(R.id.scheduleButton);

        taskTextField.setScroller(new Scroller(this));
        taskTextField.setVerticalFadingEdgeEnabled(true);
        taskTextField.setMovementMethod(new ScrollingMovementMethod());
        timePicker.setIs24HourView(true);

        String lastTasksJson = sharedPref.getString(LAST_TASKS_JSON_PREF, null);
        MorningTask[] currentTasks;
        if (lastTasksJson != null) {
            currentTasks = TaskFormatTransform.fromJson(lastTasksJson);
        }
        else {
            currentTasks = DefaultTasks.getDefaultTasks();
        }

        final String taskTextFieldInput = TaskFormatTransform.toTextFieldText(currentTasks);
        taskTextField.setText(taskTextFieldInput);


        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();

                int hour;
                int minute;
                if (Build.VERSION.SDK_INT >= 23) {
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                }
                else {
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                long militime = calendar.getTimeInMillis();

                Database database = new Database(SetupTime.this);

                String taskEditTextInput = taskTextField.getText().toString();
                MorningTask[] resultingTasks;
                try {
                    resultingTasks = TaskFormatTransform.fromTextfieldInput(taskEditTextInput);
                }
                catch (IllegalArgumentException e) {
                    displayMessage("Wrong format", e.getMessage());
                    return;
                }

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(LAST_TASKS_JSON_PREF, TaskFormatTransform.toJson(resultingTasks));
                editor.apply();

                database.addAlarmTimestamp(new MorningAlarm(militime, resultingTasks));
                database.close();

                Intent intent = new Intent(getApplicationContext(), AlarmBroadcastReceiver.class);
                intent.setData(Uri.parse("custom://" + militime));
                intent.putExtra(AlarmBroadcastReceiver.INTENT_EXTRA_ALARM_TIME, militime);
                PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                /*PendingIntent pi = PendingIntent.getService(SetupTime.this, 0,
                        new Intent(SetupTime.this, ScheduledAlarm.class),PendingIntent.FLAG_UPDATE_CURRENT);*/

                AlarmManager alarmManager = (AlarmManager) SetupTime.this.getSystemService(Context.ALARM_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, militime, pi);
                }
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, militime, pi);
                }
                else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, militime, pi);
                }

                Toast.makeText(SetupTime.this, "Scheduled!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayMessage(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        alertDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
