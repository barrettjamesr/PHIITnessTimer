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
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Toast;

public class RunningAMRAP extends FragmentActivity implements OnClickListener, DialogInterface.OnDismissListener  {
    FragmentManager fm = getSupportFragmentManager();
    protected Button btnStop;
    protected TextView lblRoundNum;
    protected TextView lblTimeDisplay;
    protected LinearLayout whole_layout;

    protected Boolean hasStarted;
    protected Boolean onStartingCD;
    protected SharedPreferences PHIIT_preferences;
    protected AudioManager audioManager;
    protected CountDownTimer startCountDownTimer;
    protected ToneGenerator toneG;
    protected Vibrator v;
    protected long[] timeSplits;
    protected int currentRound;
    protected Handler timeHandler = new Handler();
    protected int timeWarning = 0;
    protected StartingCD startingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_amrap);
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
            onStartingCD = savedInstanceState.getBoolean(getString(R.string.on_starting_cd));

            lblRoundNum.setText(String.valueOf(currentRound));
        } else {
            hasStarted = false;
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
        else if(!onStartingCD) {
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
        onStartingCD = false;
        startTimer();
    }

    private void startTimer() {
        // Start Timer!
        if (!hasStarted) {
            // set initial length to 10, add more later if/when required
            timeSplits = new long[Constants.array_length];
            timeSplits[0] = SystemClock.uptimeMillis();
            currentRound = 1;
        }
        lblRoundNum.setText(String.valueOf(currentRound));
        hasStarted = true;
        timeHandler.postDelayed(updateTimerMethod, 0);
    }

    private Runnable updateTimerMethod = new Runnable() {
        public void run() {
            long timeInMillis = SystemClock.uptimeMillis() - timeSplits[0];
            if(PHIIT_preferences.getInt(getString(R.string.ValCountUD), 0) == 1)
                lblTimeDisplay.setText(TimeFunctions.displayTime(timeInMillis));
            else
                lblTimeDisplay.setText(TimeFunctions.displayTime(PHIIT_preferences.getInt(getString(R.string.ValTimeCap), 20 * 60) * 1000 - timeInMillis));
            // Check time cap regularly
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
            timeHandler.postDelayed(this, 0);
        }

    };

    public void onClick(View v)  {
        // don't do anything if there's a dialog box displayed
        if(hasStarted) {
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
        RunningAMRAP.this.startActivity(i);
    }

    private void countRound(){
        // increase array size if rounds are completed
        if (currentRound +1 == timeSplits.length){
            long[] tempSplits = new long[timeSplits.length + Constants.array_length];
            System.arraycopy(timeSplits, 0, tempSplits, 0, timeSplits.length);
            timeSplits = tempSplits;
        }
        v.vibrate(Constants.beep_tone);
        toneG.startTone(ToneGenerator.TONE_DTMF_0, Constants.beep_tone);
        timeSplits[currentRound] = SystemClock.uptimeMillis();
        currentRound += 1;
        lblRoundNum.setText(String.valueOf(currentRound));
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
        //justCompleted.setRoundsSet();
        justCompleted.setRoundsCompleted(currentRound);
        //justCompleted.setExercisesSet();
        //justCompleted.setExercisesCompleted();
        justCompleted.setTimeCap(PHIIT_preferences.getInt(getString(R.string.ValTimeCap), 0));
        //justCompleted.setRestRound();
        //justCompleted.setBuyIn(false);
        //justCompleted.setCashOut(false);
        //justCompleted.setNotes(false);

        dataSource.open();
        dataSource.saveRound(workoutNumber, timeSplits, 0);
        dataSource.saveWorkout(justCompleted);
        dataSource.close();
    }

    public void onDestroy() {
        if (hasStarted)
            timeHandler.removeCallbacks(updateTimerMethod);
        super.onDestroy();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(getString(R.string.has_started), hasStarted);
        savedInstanceState.putInt(getString(R.string.current_round), currentRound);
        savedInstanceState.putBoolean(getString(R.string.on_starting_cd), onStartingCD);
        savedInstanceState.putLongArray(getString(R.string.time_splits), timeSplits);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

}
