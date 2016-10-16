package com.fredrik.wakeup;

import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;

public class TaskTimer extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private CountDownTimer taskCountdown;
    private Button bottomButton;
    private TextView countDownMarker;

    private MorningTask[] morningTasks;

    private int currentTask = 0;
    private boolean alarmRinging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        morningTasks = DefaultTasks.getDefaultTasks();

        bottomButton = (Button)findViewById(R.id.bottomButton);
        countDownMarker = (TextView)findViewById(R.id.countDownMarker);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        AssetFileDescriptor afd = null;
        try {
            afd = this.getResources().openRawResourceFd(R.raw.lg_good_morning);
            mediaPlayer.setDataSource(afd.getFileDescriptor());
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

        bottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonPressed();
            }
        });
    }

    private void buttonPressed(){
        if(alarmRinging){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            alarmRinging = false;

            startNewCountDown(0);
        }

        if(currentTask >= morningTasks.length){
            Toast.makeText(this,"Done",Toast.LENGTH_SHORT).show();
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

        final int secondsToDoIt = thisMorningTask.getSecondsToDoIt();

        final long timerRunningTime = Long.MAX_VALUE;
        taskCountdown = new CountDownTimer(timerRunningTime,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long millisPassed = timerRunningTime-millisUntilFinished;
                int secondsLeft = secondsToDoIt - Math.round(millisPassed/1000);
                boolean positive = secondsLeft >= 0;
                secondsLeft = Math.abs(secondsLeft);

                int minutesLeft = (int)Math.floor(secondsLeft/60); //-1
                int additionalSecLeft = secondsLeft-minutesLeft*60;

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
    protected void onStop() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onStop();
    }
}
