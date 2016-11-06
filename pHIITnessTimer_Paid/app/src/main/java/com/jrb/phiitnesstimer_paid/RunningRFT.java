package com.jrb.phiitnesstimer_paid;

import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Toast;

public class RunningRFT extends FragmentActivity implements OnClickListener, DialogInterface.OnDismissListener  {
    FragmentManager fm = getSupportFragmentManager();
    protected Button btnStop;
    protected TextView lblRoundNum;
    protected TextView lblTimeDisplay;
    protected LinearLayout whole_layout;

    protected Boolean hasStarted;
    protected Boolean onRestCD;
    protected Boolean onStartingCD;
    protected SharedPreferences PHIIT_preferences;
    protected AudioManager audioManager;
    protected Vibrator v;
    protected ToneGenerator toneG;
    protected long[] timeSplits;
    protected int currentRound = 0;
    protected Handler timeHandler = new Handler();
    protected int restRound = 0;
    protected int timeWarning = 0;
    protected StartingCD startingDialog;
    protected RestCD restDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_rft);
        PHIIT_preferences = getSharedPreferences(getString(R.string.PHIIT_Prefs), 0);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Connect interface elements to properties
        btnStop = (Button) findViewById(R.id.btnStop);
        lblRoundNum = (TextView) findViewById(R.id.lblRoundNum);
        lblTimeDisplay = (TextView) findViewById(R.id.lblTimeDisplay);
        whole_layout = (LinearLayout) findViewById(R.id.whole_layout);

        // Setup ClickListeners
        btnStop.setOnClickListener(this);
        btnStop.bringToFront();
        whole_layout.setOnClickListener(this);

        //Stop Screen Timeout
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            hasStarted = savedInstanceState.getBoolean(getString(R.string.has_started));
            timeSplits = savedInstanceState.getLongArray(getString(R.string.time_splits));
            currentRound = savedInstanceState.getInt(getString(R.string.current_round));
            onRestCD = savedInstanceState.getBoolean(getString(R.string.on_rest));
            onStartingCD = savedInstanceState.getBoolean(getString(R.string.on_starting_cd));

            lblRoundNum.setText(String.valueOf(currentRound) + getString(R.string.text_of) +PHIIT_preferences.getInt(getString(R.string.ValRFTRounds), 1));

        } else {
            hasStarted = false;
            onRestCD = false;
            onStartingCD = false;
        }

        timeWarning += PHIIT_preferences.getInt(getString(R.string.ValTimeWarning), 5 * 60) * 1000;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // send the tone to the "alarm" stream (classic beeps go there)
        if(PHIIT_preferences.getBoolean(getString(R.string.CheckSoundVolume), true)) {
            toneG = new ToneGenerator(AudioManager.STREAM_DTMF, PHIIT_preferences.getInt(getString(R.string.SoundVolume), Constants.buzzer_volume));
            audioManager.setStreamVolume(AudioManager.STREAM_DTMF, audioManager.getStreamMaxVolume(audioManager.STREAM_DTMF), 0);
        } else
            toneG = new ToneGenerator(AudioManager.STREAM_DTMF, 0);

        // Starting Countdown display
        //If required and hasn't already started
        if(PHIIT_preferences.getBoolean(getString(R.string.CheckStartingCD), false) && !onStartingCD && !hasStarted)
            startingCountdownDialog();
        else if(!onRestCD && !onStartingCD) {
            if(!hasStarted) {
                v.vibrate(Constants.long_tone);
                toneG.startTone(ToneGenerator.TONE_DTMF_D, Constants.long_tone);
            }
            startTimer();
        }
    }

    private void startingCountdownDialog () {
        onStartingCD = true;
        // custom dialog
        startingDialog = StartingCD.newInstance();
        startingDialog.show(fm, getString(R.string.check_starting_CD));

    }

    // When it's dismissed, start the actual timer
    @Override
    public void onDismiss(final DialogInterface dialog) {
        onRestCD = false;
        onStartingCD = false;
        startTimer();
    }

    private void startTimer() {
        // Start Timer!
        if (!hasStarted) {
            if(PHIIT_preferences.getBoolean(getString(R.string.CheckRFTRounds), false))
                timeSplits = new long[PHIIT_preferences.getInt(getString(R.string.ValRFTRounds), 2) + 1];
            else
                timeSplits = new long[ 2]; // Only holds start and end times
            timeSplits[0] = SystemClock.uptimeMillis();
            currentRound = 1;
        }
        lblRoundNum.setText(String.valueOf(currentRound) + getString(R.string.text_of) +PHIIT_preferences.getInt(getString(R.string.ValRFTRounds), 1));
        hasStarted = true;
        timeHandler.postDelayed(updateTimerMethod, 0);
    }


    private Runnable updateTimerMethod = new Runnable() {
        public void run() {
            long timeInMillis = SystemClock.uptimeMillis() - timeSplits[0];
            lblTimeDisplay.setText(TimeFunctions.displayTime(timeInMillis));
            // If there's a time cap, check regularly
            if (PHIIT_preferences.getBoolean(getString(R.string.CheckTimeCap), false))
                if (timeInMillis >= PHIIT_preferences.getInt(getString(R.string.ValTimeCap), 20 * 60) * 1000) {
                    stopTimer();
                    return;
                }
            // If there's a time warning, check regularly
            if (PHIIT_preferences.getBoolean(getString(R.string.CheckTimeWarning), false)) {
                //two short beeps within one second of the time warning
                if (timeInMillis >= timeWarning && timeInMillis <= (timeWarning + 1000)) {
                    timeWarning += PHIIT_preferences.getInt(getString(R.string.ValTimeWarning), 5 * 60) * 1000;
                    toneG.startTone(ToneGenerator.TONE_DTMF_0, Constants.beep_tone);
                    v.vibrate(Constants.beep_tone);
                    android.os.SystemClock.sleep(Constants.short_tone);
                    v.vibrate(Constants.beep_tone);
                    toneG.startTone(ToneGenerator.TONE_DTMF_0, Constants.beep_tone);
                }
            }
            if(!onRestCD)
                timeHandler.postDelayed(this, 0);
        }

    };

    public void onClick(View v)  {
        // don't do anything if there's a dialog box displayed
        if(!onRestCD && hasStarted) {
            switch (v.getId()) {
                case R.id.btnStop:
                    stopTimer();
                    break;
                case R.id.whole_layout:
                    countRound();
                    break;
            }
        }
    }

    private void stopTimer() {
        v.vibrate(Constants.long_tone);
        toneG.startTone(ToneGenerator.TONE_DTMF_D, Constants.long_tone);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Record final time and stop clock
        timeSplits[currentRound] = SystemClock.uptimeMillis();
        timeHandler.removeCallbacks(updateTimerMethod);
        // Commit timeSplits to database
        saveTimeSplits();
        // Reset Volume
        android.os.SystemClock.sleep(Constants.long_tone);
        audioManager.setStreamVolume(AudioManager.STREAM_DTMF, AudioManager.ADJUST_SAME, 0);
        // Go to display screen
        Intent i = new Intent(this, DisplayTypeOne.class);
        RunningRFT.this.startActivity(i);
    }

    private void countRound(){
        // Exit when all rounds are counted
        if (currentRound +1 == timeSplits.length)
            stopTimer();
        else {
            v.vibrate(Constants.beep_tone);
            toneG.startTone(ToneGenerator.TONE_DTMF_0, Constants.beep_tone);
            timeSplits[currentRound] = SystemClock.uptimeMillis();
            currentRound += 1;
            lblRoundNum.setText(String.valueOf(currentRound) + getString(R.string.text_of) +PHIIT_preferences.getInt(getString(R.string.ValRFTRounds), 1));
            // Add rest dialog for those rounds
            if (PHIIT_preferences.getBoolean(getString(R.string.CheckRestRounds), false) && PHIIT_preferences.getInt(getString(R.string.ValRestRounds), 0) >0) {
                timeHandler.removeCallbacks(updateTimerMethod);
                restCountdownDialog();
            }
        }
    }

    private void restCountdownDialog (){
        onRestCD = true;
        restRound = PHIIT_preferences.getInt(getString(R.string.ValRestRounds), 10) * 1000;
        restDialog = RestCD.newInstance(restRound);
        restDialog.setCancelable(false);
        restDialog.show(fm, getString(R.string.check_rest_round));

    }

    private void saveTimeSplits() {
        PHIIT_DataSource dataSource = new PHIIT_DataSource(this);
        // set workout number as one higher than previous max workout number
        int workoutNumber = 1 + dataSource.LastEntry(DatabaseContract.ArchivedWorkouts.TABLE_NAME, DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NUMBER);
        Workout justCompleted = new Workout();
        justCompleted.setWorkoutNumber(workoutNumber);
        justCompleted.setWorkoutName(getString(R.string.text_workout_prefix) + workoutNumber);
        justCompleted.setWorkoutType(this.getClass().getSimpleName().replace(getString(R.string.text_running_prefix), "").replace(getString(R.string.text_repeats_suffix), ""));
//        justCompleted.setDateRan(); Set in DataSource;
        justCompleted.setRoundsSet(PHIIT_preferences.getInt(getString(R.string.ValRFTRounds), 0));
        justCompleted.setRoundsCompleted(currentRound);
        //justCompleted.setExercisesSet();
        //justCompleted.setExercisesCompleted();
        if(PHIIT_preferences.getBoolean(getString(R.string.CheckTimeCap), false))
            justCompleted.setTimeCap(PHIIT_preferences.getInt(getString(R.string.ValTimeCap), 0));
        if(PHIIT_preferences.getBoolean(getString(R.string.CheckRestRounds), false))
            justCompleted.setRestRound(PHIIT_preferences.getInt(getString(R.string.ValRestRounds), 0));
        //justCompleted.setBuyIn(false);
        //justCompleted.setCashOut(false);
        //justCompleted.setNotes(false);

        dataSource.open();
        dataSource.saveRound(workoutNumber, timeSplits, restRound);
        dataSource.saveWorkout(justCompleted);
        dataSource.close();
    }

    public void onDestroy() {
        if (!onRestCD && hasStarted)
            timeHandler.removeCallbacks(updateTimerMethod);
        super.onDestroy();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //Save variables and trace which part of the timer it's currently on
        savedInstanceState.putBoolean(getString(R.string.has_started), hasStarted);
        savedInstanceState.putBoolean(getString(R.string.on_rest), onRestCD);
        savedInstanceState.putBoolean(getString(R.string.on_starting_cd), onStartingCD);
        savedInstanceState.putInt(getString(R.string.current_round), currentRound);
        savedInstanceState.putLongArray(getString(R.string.time_splits), timeSplits);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        stopTimer();
    }

}
