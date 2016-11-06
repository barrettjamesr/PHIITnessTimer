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

public class RunningAMRAPrepeats extends FragmentActivity implements OnClickListener, DialogInterface.OnDismissListener  {
    FragmentManager fm = getSupportFragmentManager();
    protected Button btnStop;
    protected TextView lblRoundNum;
    protected TextView lblCycleNum;
    protected TextView lblTimeDisplay;
    protected LinearLayout whole_layout;

    protected Boolean hasStarted;
    protected Boolean onRestCD;
    protected Boolean onStartingCD;
    protected SharedPreferences PHIIT_preferences;
    protected AudioManager audioManager;
    protected Vibrator v;
    protected ToneGenerator toneG;
    protected long[][] timeSplits;
    protected long startCycle;
    protected int currentRound = 0;
    protected int currentCycle = 0;
    protected Handler timeHandler = new Handler();
    protected int timeWarning = 0;
    protected StartingCD startingDialog;
    protected RestCD restDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_amraprepeats);
        PHIIT_preferences = getSharedPreferences(getString(R.string.PHIIT_Prefs), 0);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Connect interface elements to properties
        btnStop = (Button) findViewById(R.id.btnStop);
        lblRoundNum = (TextView) findViewById(R.id.lblRoundNum);
        lblCycleNum = (TextView) findViewById(R.id.lblCycleNum);
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
            if(hasStarted) {
                long[] oneDimSplits = savedInstanceState.getLongArray(getString(R.string.time_splits));
                timeSplits = new long[PHIIT_preferences.getInt(getString(R.string.ValNumberCycles), 3) + 1][oneDimSplits.length / (PHIIT_preferences.getInt(getString(R.string.ValNumberCycles), 3)+1)];
                for (int i = 0; i < timeSplits.length; i++) {
                    System.arraycopy(oneDimSplits, i * timeSplits[0].length, timeSplits[i], 0, timeSplits[0].length);
                }
            }
            currentRound = savedInstanceState.getInt(getString(R.string.current_round));
            currentCycle = savedInstanceState.getInt(getString(R.string.current_cycle));
            startCycle = savedInstanceState.getLong(getString(R.string.current_start));
            onStartingCD = savedInstanceState.getBoolean(getString(R.string.on_starting_cd));
            onRestCD = savedInstanceState.getBoolean(getString(R.string.on_rest));
            lblRoundNum.setText(String.valueOf(currentRound));
            lblCycleNum.setText(String.valueOf(currentCycle));
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
        if(onStartingCD)
            onStartingCD = false;
        if(onRestCD) {
            //Reset if chosen
            startCycle = SystemClock.uptimeMillis();
            if (PHIIT_preferences.getInt(getString(R.string.ValResetRounds), 0) == 0){
                timeSplits[currentCycle][0] = SystemClock.uptimeMillis();
                currentRound = 1;
            } else {
                timeSplits[currentCycle][currentRound - 1] = timeSplits[currentCycle - 1][currentRound - 1];
            }
            onRestCD = false;
        }
        startTimer();
    }

    private void startTimer() {
        // Start Timer!
        timeWarning += PHIIT_preferences.getInt(getString(R.string.ValTimeWarning), 5 * 60) * 1000;
        if (!hasStarted) {
            // set initial length to 10, add more later if/when required, // one extra D1 for lining up of cycles
            timeSplits = new long[PHIIT_preferences.getInt(getString(R.string.ValNumberCycles), 3) +1][Constants.array_length];
            currentCycle = 1;
            startCycle = SystemClock.uptimeMillis();
            timeSplits[currentCycle][0] = SystemClock.uptimeMillis();
            currentRound = 1;
        }
        lblRoundNum.setText(String.valueOf(currentRound));
        lblCycleNum.setText(String.valueOf(currentCycle) + getString(R.string.text_of) +PHIIT_preferences.getInt(getString(R.string.ValNumberCycles), 1));
        hasStarted = true;
        timeHandler.postDelayed(updateTimerMethod, 0);
    }

    private Runnable updateTimerMethod = new Runnable() {
        public void run() {
            long timeInMillis = SystemClock.uptimeMillis() - startCycle;
            if(PHIIT_preferences.getInt(getString(R.string.ValCountUD), 0) == 1)
                lblTimeDisplay.setText(TimeFunctions.displayTime(timeInMillis));
            else
                lblTimeDisplay.setText(TimeFunctions.displayTime(PHIIT_preferences.getInt(getString(R.string.ValTimeCapCycles), 20 * 60) * 1000 - timeInMillis));
            // Check time cap regularly
            if (timeInMillis >= PHIIT_preferences.getInt(getString(R.string.ValTimeCapCycles), 20 * 60) * 1000) {
                if(currentCycle == PHIIT_preferences.getInt(getString(R.string.ValNumberCycles), 3)){
                    stopTimer();
                } else{
                    v.vibrate(Constants.long_tone);
                    toneG.startTone(ToneGenerator.TONE_DTMF_D, Constants.long_tone);
                    if (PHIIT_preferences.getInt(getString(R.string.ValResetRounds), 0) == 0)
                        timeSplits[currentCycle][currentRound] = SystemClock.uptimeMillis();
                    timeHandler.removeCallbacks(updateTimerMethod);
                    currentCycle += 1;
                    lblCycleNum.setText(String.valueOf(currentCycle) + getString(R.string.text_of) +PHIIT_preferences.getInt(getString(R.string.ValNumberCycles), 1));
                    if (PHIIT_preferences.getBoolean(getString(R.string.CheckRestCycles), false) && PHIIT_preferences.getInt(getString(R.string.ValRestCycles), 0) > 0) {
                        timeHandler.removeCallbacks(updateTimerMethod);
                        restCountdownDialog();
                    } else {
                        //Reset if chosen
                        startCycle = SystemClock.uptimeMillis();
                        if (PHIIT_preferences.getInt(getString(R.string.ValResetRounds), 0) == 0){
                            timeSplits[currentCycle][0] = SystemClock.uptimeMillis();
                            currentRound = 1;
                        } else {
                            timeSplits[currentCycle][currentRound - 1] = timeSplits[currentCycle - 1][currentRound - 1];
                        }
                        startTimer();
                    }
                }
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
        timeSplits[currentCycle][currentRound] = SystemClock.uptimeMillis();
        timeHandler.removeCallbacks(updateTimerMethod);
        // Commit timeSplits to database
        saveTimeSplits();
        // Reset Volume
        android.os.SystemClock.sleep(Constants.long_tone);
        audioManager.setStreamVolume(AudioManager.STREAM_DTMF, AudioManager.ADJUST_SAME, 0);
        // Go to display screen
        Intent i = new Intent(this, DisplayTypeOne.class);
        RunningAMRAPrepeats.this.startActivity(i);
    }

    private void countRound(){
        // increase array size if current size is full
        if (currentRound + 1 == timeSplits.length){
            long[][] tempSplits = new long[timeSplits.length][timeSplits[0].length + Constants.array_length];
            for (int i = 0; i < timeSplits.length; i++) {
                System.arraycopy(timeSplits[i], 0, tempSplits[i], 0, timeSplits[0].length);
            }
            timeSplits = tempSplits;
        }
        v.vibrate(Constants.beep_tone);
        toneG.startTone(ToneGenerator.TONE_DTMF_0, Constants.beep_tone);
        timeSplits[currentCycle][currentRound] = SystemClock.uptimeMillis();
        currentRound += 1;
        lblRoundNum.setText(String.valueOf(currentRound));
    }

    private void restCountdownDialog (){
        timeHandler.removeCallbacks(updateTimerMethod);
        onRestCD = true;
        restDialog = RestCD.newInstance(PHIIT_preferences.getInt(getString(R.string.ValRestCycles), 10) * 1000);
        restDialog.setCancelable(false);
        restDialog.show(fm, getString(R.string.check_rest_round));

    }

    private void saveTimeSplits() {
        PHIIT_DataSource dataSource = new PHIIT_DataSource(this);
        dataSource.open();
        // set workout number as one higher than previous max workout number
        int workoutNumber = 1 + dataSource.LastEntry(DatabaseContract.ArchivedWorkouts.TABLE_NAME, DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NUMBER);
        dataSource.saveRound(workoutNumber, timeSplits);
        Workout justCompleted = new Workout();
        justCompleted.setWorkoutNumber(workoutNumber);
        justCompleted.setWorkoutName(getString(R.string.text_workout_prefix) + workoutNumber);
        justCompleted.setWorkoutType(this.getClass().getSimpleName().replace(getString(R.string.text_running_prefix), "").replace(getString(R.string.text_repeats_suffix), getString(R.string.text_repeats_replace)));
//        justCompleted.setDateRan(); Set in DataSource;
        //justCompleted.setRoundsSet();
        justCompleted.setRoundsCompleted(dataSource.WorkoutNumRounds(workoutNumber));
        justCompleted.setExercisesSet(PHIIT_preferences.getInt(getString(R.string.ValNumberCycles), 0));
        justCompleted.setExercisesCompleted(currentCycle);
        justCompleted.setTimeCap(PHIIT_preferences.getInt(getString(R.string.ValTimeCapCycles), 0));
        if(PHIIT_preferences.getBoolean(getString(R.string.CheckRestCycles), false))
            justCompleted.setRestRound(PHIIT_preferences.getInt(getString(R.string.ValRestCycles), 0));
        //justCompleted.setBuyIn(false);
        //justCompleted.setCashOut(false);
        //justCompleted.setNotes(false);

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
        savedInstanceState.putBoolean(getString(R.string.on_rest), onRestCD);
        savedInstanceState.putBoolean(getString(R.string.on_starting_cd), onStartingCD);
        savedInstanceState.putInt(getString(R.string.current_round), currentRound);
        savedInstanceState.putInt(getString(R.string.current_cycle), currentCycle);
        savedInstanceState.putLong(getString(R.string.current_start), startCycle);
        savedInstanceState.putBoolean(getString(R.string.has_started), hasStarted);
        // make a one dimensional array
        if(hasStarted) {
            long[] oneDimSplits = new long[timeSplits.length * timeSplits[0].length];
            for (int i = 0; i < timeSplits.length; i++) {
                System.arraycopy(timeSplits[i], 0, oneDimSplits, i * timeSplits[0].length, timeSplits[0].length);
            }
            savedInstanceState.putLongArray(getString(R.string.time_splits), oneDimSplits);
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

}
