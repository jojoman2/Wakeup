package com.fredrik.wakeup;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class TaskTimer extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private CountDownTimer taskCountdown;
    private TextView taskTitle;
    private TextView countDownMarker;

    private MorningTask[] morningTasks;
    private int[] results;

    private int secondsLeft;

    private int currentTask = 0;
    private boolean alarmRinging;

    private boolean focusDuringOnPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_timer);


        morningTasks = DefaultTasks.getDefaultTasks();
        results = new int[morningTasks.length];

        Button bottomButton = (Button) findViewById(R.id.bottomButton);
        countDownMarker = (TextView)findViewById(R.id.countDownMarker);
        taskTitle = (TextView)findViewById(R.id.taskTitle);


        bottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonPressed();
            }
        });
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        currentTask = 0;
        startNewCountDown(currentTask);
    }

    private void startMediaPlayer(){
        AssetFileDescriptor afd = null;
        try {
            afd = this.getResources().openRawResourceFd(R.raw.lg_good_morning);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
        }
        catch (IOException e) {
            e.printStackTrace();
            try {
                mediaPlayer.setDataSource(Settings.System.DEFAULT_RINGTONE_URI.getPath());
                mediaPlayer.prepare();
            }
            catch (IOException ignored){}
        }
        finally {
            if (afd != null){
                try {
                    afd.close();
                }
                catch (IOException ignored) {}
            }
        }


        mediaPlayer.start();
        alarmRinging = true;
    }

    private void buttonPressed(){
        results[currentTask] = secondsLeft;
        if(currentTask+1 >= morningTasks.length){
            Intent intent = new Intent(getBaseContext(), Results.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(Results.RESULT_KEY, results);
            startActivity(intent);
        }
        else{
            currentTask++;
            startNewCountDown(currentTask);
        }
    }

    private static String numberToString(int number){
        String numberString = Integer.toString(number);
        if(numberString.length() == 1) {
            numberString = "0" + numberString;

        }
        return numberString;

    }

    private void startNewCountDown(int number){
        MorningTask thisMorningTask = morningTasks[number];

        final boolean soundAlarm = thisMorningTask.getSoundAlarm();
        if(soundAlarm && (!mediaPlayer.isPlaying())){
            startMediaPlayer();
        }
        else if((!soundAlarm) && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        final String taskTitleStr = thisMorningTask.getName();
        taskTitle.setText(taskTitleStr);

        final int secondsToDoIt = thisMorningTask.getSecondsToDoIt();


        if(taskCountdown != null){
            taskCountdown.cancel();
        }
        final long timerRunningTime = Long.MAX_VALUE;
        secondsLeft = secondsToDoIt;
        taskCountdown = new CountDownTimer(timerRunningTime,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long millisPassed = timerRunningTime-millisUntilFinished;
                secondsLeft = secondsToDoIt - Math.round(millisPassed/1000);
                boolean positive = secondsLeft >= 0;
                int secondsLeftAbs = Math.abs(secondsLeft);

                int minutesLeft = (int)Math.floor(secondsLeftAbs/60); //-1
                int additionalSecLeft = secondsLeftAbs-minutesLeft*60;

                String minutesLeftStr = numberToString(minutesLeft);
                String additionalSecLeftStr = numberToString(additionalSecLeft);

                String textToShow = minutesLeftStr + ":" +additionalSecLeftStr;
                if(positive){
                    countDownMarker.setTextColor(Color.BLACK);
                }
                else{
                    textToShow = "-" +textToShow;
                    countDownMarker.setTextColor(Color.RED);
                }

                countDownMarker.setText(textToShow);
            }

            @Override
            public void onFinish() { }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        focusDuringOnPause = hasWindowFocus();
    }

    @Override
    protected void onStop() {
        if(focusDuringOnPause) {
            if (AlarmBroadcastReceiver.wakeLock != null) {
                AlarmBroadcastReceiver.wakeLock.release();
                AlarmBroadcastReceiver.wakeLock = null;
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            if (taskCountdown != null) {
                taskCountdown.cancel();
            }
            finish();
        }
        super.onStop();
    }
}
