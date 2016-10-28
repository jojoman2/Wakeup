package com.fredrik.wakeup;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

public class TaskTimer extends AppCompatActivity {

    private static final String CURRENT_TASK_KEY = "currentTask";
    private static final String TIME_WHEN_TASK_SHOULD_BE_DONE_KEY = "timeDone";
    private static final String RESULTS_KEY = "results";

    private MediaPlayer mediaPlayer;
    private TextView taskTitle;
    private TextView countDownMarker;

    private MorningTask[] morningTasks;


    private int currentTask;
    private long timeWhenItShouldBeDone;
    private int[] results;

    private boolean focusDuringOnPause;
    private boolean textToSpeechOnline = false;

    private static final long timerRunningTime = Long.MAX_VALUE;
    private CountDownTimer taskCountdown = new CountDownTimer(timerRunningTime, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            final int secondsLeft = getTimeLeftInSec();
            handleTick(secondsLeft);
        }

        @Override
        public void onFinish() {
        }
    };
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_timer);

        morningTasks = DefaultTasks.getDefaultTasks();

        if(savedInstanceState == null){
            currentTask = 0;
            timeWhenItShouldBeDone = -1;
            results = new int[morningTasks.length];
        }
        else{
            currentTask = savedInstanceState.getInt(CURRENT_TASK_KEY);
            timeWhenItShouldBeDone = savedInstanceState.getLong(TIME_WHEN_TASK_SHOULD_BE_DONE_KEY);
            results = savedInstanceState.getIntArray(RESULTS_KEY);

        }

        Button bottomButton = (Button) findViewById(R.id.bottomButton);
        countDownMarker = (TextView)findViewById(R.id.countDownMarker);
        taskTitle = (TextView)findViewById(R.id.taskTitle);


        bottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonPressed();
            }
        });

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                textToSpeechOnline = status == TextToSpeech.SUCCESS;
            }
        });
        tts.setLanguage(Locale.US);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        setupCountdownForCurrentTask();
    }

    @Override
    protected void onStart() {
        super.onStart();

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
    }

    private int getTimeLeftInSec(){
        return Math.round((timeWhenItShouldBeDone - System.currentTimeMillis())/1000);
    }

    private void buttonPressed(){
        results[currentTask] = getTimeLeftInSec();
        timeWhenItShouldBeDone = -1;

        if(currentTask+1 >= morningTasks.length){
            Intent intent = new Intent(getBaseContext(), Results.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(Results.RESULT_KEY, results);
            startActivity(intent);
        }
        else{
            currentTask++;
            setupCountdownForCurrentTask();
        }

        if (AlarmBroadcastReceiver.wakeLock != null) {
            AlarmBroadcastReceiver.wakeLock.release();
            AlarmBroadcastReceiver.wakeLock = null;
        }
    }

    private static String numberToString(int number){
        String numberString = Integer.toString(number);
        if(numberString.length() == 1) {
            numberString = "0" + numberString;

        }
        return numberString;

    }

    private void handleTick(int secondsLeft){
        boolean positive = secondsLeft >= 0;
        int secondsLeftAbs = Math.abs(secondsLeft);

        int minutesLeft = (int) Math.floor(secondsLeftAbs / 60); //-1
        int additionalSecLeft = secondsLeftAbs - minutesLeft * 60;

        String minutesLeftStr = numberToString(minutesLeft);
        String additionalSecLeftStr = numberToString(additionalSecLeft);

        String textToShow = minutesLeftStr + ":" + additionalSecLeftStr;
        if (positive) {
            countDownMarker.setTextColor(Color.BLACK);
        }
        else {
            textToShow = "-" + textToShow;
            countDownMarker.setTextColor(Color.RED);
        }

        countDownMarker.setText(textToShow);

        if(secondsLeft % 60 == 0 && textToSpeechOnline && (!mediaPlayer.isPlaying())){
            String toSay;
            if(minutesLeft == 0){
                toSay = "Time is up";
            }
            else if(positive) {
                toSay = minutesLeftStr + " minutes remaining";
            }
            else{
                toSay = "Behind " + minutesLeftStr + " minutes";
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(toSay,TextToSpeech.QUEUE_FLUSH,null, UUID.randomUUID().toString());
            }
            else{
                tts.speak(toSay,TextToSpeech.QUEUE_FLUSH,null);
            }
        }
    }

    private void setupCountdownForCurrentTask(){
        final MorningTask thisMorningTask = morningTasks[currentTask];


        final boolean soundAlarm = thisMorningTask.useSoundAlarm();
        if(mediaPlayer != null) {
            if (soundAlarm && (!mediaPlayer.isPlaying())) {
                startMediaPlayer();
            }
            else if ((!soundAlarm) && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }
        final String taskTitleStr = thisMorningTask.getName();
        taskTitle.setText(taskTitleStr);

        if(timeWhenItShouldBeDone == -1) {
            timeWhenItShouldBeDone = System.currentTimeMillis() + thisMorningTask.getSecondsToDoIt() * 1000;
        }

        taskCountdown.start();

    }

    @Override
    protected void onPause() {
        super.onPause();

        focusDuringOnPause = hasWindowFocus();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_TASK_KEY,currentTask);
        outState.putIntArray(RESULTS_KEY,results);
        outState.putLong(TIME_WHEN_TASK_SHOULD_BE_DONE_KEY,timeWhenItShouldBeDone);
        super.onSaveInstanceState(outState);
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
            }
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (taskCountdown != null) {
            taskCountdown.cancel();
            taskCountdown = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
