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
import android.widget.TextView;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Toast;

public class RunningEMOM extends FragmentActivity implements OnClickListener, DialogInterface.OnDismissListener  {
    FragmentManager fm = getSupportFragmentManager();
    protected Button btnStop;
    protected TextView lblRoundNum;
    protected TextView lblTimeDisplay;

    protected Boolean hasStarted;
    protected Boolean onStartingCD;
    protected SharedPreferences PHIIT_preferences;
    protected AudioManager audioManager;
    protected ToneGenerator toneG;
    protected Vibrator v;
    protected int currentRound = 0;
    protected long startTime;
    protected Handler timeHandler = new Handler();
    protected int timeWarning = 0;
    protected StartingCD startingDialog;
    protected Boolean completed = false;
    boolean three;
    boolean two;
    boolean one;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_emom);
        PHIIT_preferences = getSharedPreferences(getString(R.string.PHIIT_Prefs), 0);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Connect interface elements to properties
        btnStop = (Button) findViewById(R.id.btnStop);
        lblRoundNum = (TextView) findViewById(R.id.lblRoundNum);
        lblTimeDisplay = (TextView) findViewById(R.id.lblTimeDisplay);

        // Setup ClickListeners
        btnStop.setOnClickListener(this);
        btnStop.bringToFront();

        //Stop Screen Timeout
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            hasStarted = savedInstanceState.getBoolean(getString(R.string.has_started));
            currentRound = savedInstanceState.getInt(getString(R.string.current_round));
            startTime = savedInstanceState.getLong(getString(R.string.current_start));
            onStartingCD = savedInstanceState.getBoolean(getString(R.string.on_starting_cd));
            lblRoundNum.setText(String.valueOf(currentRound));
        } else {
            hasStarted = false;
            onStartingCD = false;
        }

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
        startTime = SystemClock.uptimeMillis();
        startTimer();
    }

    private void startTimer() {
        // Start Timer!
        timeWarning += PHIIT_preferences.getInt(getString(R.string.ValTimeWarning), 5 * 60) * 1000;
        if (!hasStarted) {
            currentRound = 1;
            startTime = SystemClock.uptimeMillis();
        }
        lblRoundNum.setText(String.valueOf(currentRound) + getString(R.string.text_of) + PHIIT_preferences.getInt(getString(R.string.ValEMOMCycles), 1));
        hasStarted = true;
        three = true;
        two = true;
        one = true;
        timeHandler.postDelayed(updateTimerMethod, 0);
    }

    private Runnable updateTimerMethod = new Runnable() {
        public void run() {
            long timeInMillis = SystemClock.uptimeMillis() - startTime;
            if(PHIIT_preferences.getInt(getString(R.string.ValCountUD), 0) == 1)
                lblTimeDisplay.setText(TimeFunctions.displayTime(timeInMillis));
            else
                lblTimeDisplay.setText(TimeFunctions.displayTime(PHIIT_preferences.getInt(getString(R.string.ValEMOMTime), 60) * 1000 - timeInMillis));
            // Check time cap regularly
            if (timeInMillis >= PHIIT_preferences.getInt(getString(R.string.ValEMOMTime), 60) * 1000) {
                timeHandler.removeCallbacks(updateTimerMethod);
                if(currentRound == PHIIT_preferences.getInt(getString(R.string.ValEMOMCycles), 3)){
                    // all rounds and exercises done
                    completed = true;
                    stopTimer();
                } else{
                    v.vibrate(Constants.long_tone);
                    toneG.startTone(ToneGenerator.TONE_DTMF_D, Constants.long_tone);
                    currentRound += 1;
                    startTime = SystemClock.uptimeMillis();
                    startTimer();
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
            //Countdown
            if (PHIIT_preferences.getInt(getString(R.string.ValEMOMTime), 60) * 1000 - timeInMillis <= 3000 && three) {
                v.vibrate(Constants.short_tone);
                toneG.startTone(ToneGenerator.TONE_DTMF_0, Constants.short_tone);
                three = false;
            }
            else if (PHIIT_preferences.getInt(getString(R.string.ValEMOMTime), 60) * 1000 - timeInMillis <= 2000 && two) {
                v.vibrate(Constants.short_tone);
                toneG.startTone(ToneGenerator.TONE_DTMF_0, Constants.short_tone);
                two = false;
            }
            else if (PHIIT_preferences.getInt(getString(R.string.ValEMOMTime), 60) * 1000 - timeInMillis <= 1000 && one) {
                v.vibrate(Constants.short_tone);
                toneG.startTone(ToneGenerator.TONE_DTMF_0, Constants.short_tone);
                one = false;
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
            }
        }
    }

    private void stopTimer() {
        v.vibrate(Constants.long_tone);
        toneG.startTone(ToneGenerator.TONE_DTMF_D, Constants.long_tone);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        timeHandler.removeCallbacks(updateTimerMethod);
        saveWorkout();
        // Reset Volume
        android.os.SystemClock.sleep(Constants.long_tone);
        audioManager.setStreamVolume(AudioManager.STREAM_DTMF, AudioManager.ADJUST_SAME, 0);
        // Go to display screen
        Intent i = new Intent(this, DisplayTypeTwo.class);
        RunningEMOM.this.startActivity(i);
    }

    private void saveWorkout() {
        PHIIT_DataSource dataSource = new PHIIT_DataSource(this);
        dataSource.open();
        // set workout number as one higher than previous max workout number
        int workoutNumber = 1 + dataSource.LastEntry(DatabaseContract.ArchivedWorkouts.TABLE_NAME, DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NUMBER);
        Workout justCompleted = new Workout();
        justCompleted.setWorkoutNumber(workoutNumber);
        justCompleted.setWorkoutName(getString(R.string.text_workout_prefix) + workoutNumber);
        justCompleted.setWorkoutType(this.getClass().getSimpleName().replace(getString(R.string.text_running_prefix), "").replace(getString(R.string.text_repeats_suffix), getString(R.string.text_repeats_replace)));
//        justCompleted.setDateRan(); Set in DataSource;
        justCompleted.setRoundsSet(PHIIT_preferences.getInt(getString(R.string.ValEMOMCycles), 0));
//        justCompleted.setExercisesSet();
        if (completed)
            justCompleted.setRoundsCompleted(currentRound);
        else
            justCompleted.setRoundsCompleted(currentRound -1);
//        justCompleted.setExercisesCompleted();
        justCompleted.setTimeCap(PHIIT_preferences.getInt(getString(R.string.ValEMOMTime), 0));
//        justCompleted.setRestRound();
        //justCompleted.setBuyIn(false);
        //justCompleted.setCashOut(false);
        //justCompleted.setNotes(false);

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
        savedInstanceState.putBoolean(getString(R.string.on_starting_cd), onStartingCD);
        savedInstanceState.putInt(getString(R.string.current_round), currentRound);
        savedInstanceState.putLong(getString(R.string.current_start), startTime);
        savedInstanceState.putBoolean(getString(R.string.has_started), hasStarted);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

}
