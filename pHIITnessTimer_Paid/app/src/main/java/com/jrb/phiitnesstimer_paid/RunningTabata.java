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

public class RunningTabata extends FragmentActivity implements OnClickListener, DialogInterface.OnDismissListener  {
    FragmentManager fm = getSupportFragmentManager();
    protected Button btnStop;
    protected TextView lblRoundNum;
    protected TextView lblExerciseNum;
    protected TextView lblTimeDisplay;
    protected LinearLayout whole_layout;

    protected Boolean hasStarted;
    protected Boolean onRestCD_Tabata;
    protected Boolean onStartingCD;
    protected SharedPreferences PHIIT_preferences;
    protected AudioManager audioManager;
    protected Vibrator v;
    protected ToneGenerator toneG;
    protected int currentRound = 0;
    protected int currentExercise = 0;
    protected int[][] repCount;
    protected long startTime;
    protected Handler timeHandler = new Handler();
    protected StartingCD startingDialog;
    protected RestCD_Tabata restDialog;
    protected Boolean completed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_tabata);
        PHIIT_preferences = getSharedPreferences(getString(R.string.PHIIT_Prefs), 0);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Connect interface elements to properties
        btnStop = (Button) findViewById(R.id.btnStop);
        lblRoundNum = (TextView) findViewById(R.id.lblRoundNum);
        lblExerciseNum = (TextView) findViewById(R.id.lblExerciseNum);
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
            currentRound = savedInstanceState.getInt(getString(R.string.current_round));
            currentExercise = savedInstanceState.getInt(getString(R.string.current_exercise));
            startTime = savedInstanceState.getLong(getString(R.string.current_start));
            onStartingCD = savedInstanceState.getBoolean(getString(R.string.on_starting_cd));
            onRestCD_Tabata = savedInstanceState.getBoolean(getString(R.string.on_rest));
            lblRoundNum.setText(String.valueOf(currentRound));
            lblExerciseNum.setText(String.valueOf(currentExercise));
            if(hasStarted) {
                int[] oneDimSplits = savedInstanceState.getIntArray(getString(R.string.rep_count));
                repCount = new int[PHIIT_preferences.getInt(getString(R.string.ValTabataExercises), 3) + 1][oneDimSplits.length / (PHIIT_preferences.getInt(getString(R.string.ValTabataExercises), 3)+1)];
                for (int i = 0; i < repCount.length; i++) {
                    System.arraycopy(oneDimSplits, i * repCount[0].length, repCount[i], 0, repCount[0].length);
                }
            }
        } else {
            hasStarted = false;
            onRestCD_Tabata = false;
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
        else if(!onRestCD_Tabata && !onStartingCD) {
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
        if(onRestCD_Tabata) {
            onRestCD_Tabata = false;
        }

        startTime = SystemClock.uptimeMillis();
        startTimer();
    }

    public void saveReps(String reps){
        repCount[currentExercise][currentRound] = Integer.valueOf(reps);

    }

    private void startTimer() {
        // Start Timer!
        if(currentExercise == PHIIT_preferences.getInt(getString(R.string.ValTabataExercises), 3) && currentRound == PHIIT_preferences.getInt(getString(R.string.ValTabataRounds), 3)){
            // all rounds and exercises done
            completed = true;
            stopTimer();
        } else if (!hasStarted) {
            //initialise round & exercise count
            hasStarted = true;
            repCount = new int[PHIIT_preferences.getInt(getString(R.string.ValTabataExercises), 3) +1][PHIIT_preferences.getInt(getString(R.string.ValTabataRounds), 3) +1];
            currentRound = 1;
            currentExercise = 1;
            startTime = SystemClock.uptimeMillis();
        } else if(currentRound == PHIIT_preferences.getInt(getString(R.string.ValTabataRounds), 3)){
            currentExercise += 1;
            currentRound = 1;
        } else
            currentRound += 1;
        lblRoundNum.setText(String.valueOf(currentRound) + getString(R.string.text_of) + PHIIT_preferences.getInt(getString(R.string.ValTabataRounds), 1));
        lblExerciseNum.setText(String.valueOf(currentExercise) + getString(R.string.text_of) + PHIIT_preferences.getInt(getString(R.string.ValTabataExercises), 1));

        timeHandler.postDelayed(updateTimerMethod, 0);
    }

    private Runnable updateTimerMethod = new Runnable() {
        public void run() {
            long timeInMillis = SystemClock.uptimeMillis() - startTime;
            if(PHIIT_preferences.getInt(getString(R.string.ValCountUD), 0) == 1)
                lblTimeDisplay.setText(TimeFunctions.displayTime(timeInMillis));
            else
                lblTimeDisplay.setText(TimeFunctions.displayTime(PHIIT_preferences.getInt(getString(R.string.ValTabataTimeCap), 20) * 1000 - timeInMillis));
            // Check time cap regularly
            if (timeInMillis >= PHIIT_preferences.getInt(getString(R.string.ValTabataTimeCap), 20) * 1000) {
                v.vibrate(Constants.long_tone);
                toneG.startTone(ToneGenerator.TONE_DTMF_D, Constants.long_tone);
                if (PHIIT_preferences.getBoolean(getString(R.string.CheckTabataRest), false) && PHIIT_preferences.getInt(getString(R.string.ValTabataRest), 0) > 0) {
                    timeHandler.removeCallbacks(updateTimerMethod);
                    restCountdownDialog();
                } else {
                    startTime = SystemClock.uptimeMillis();
                    startTimer();
                }
                return;
            }
            if(!onRestCD_Tabata && !completed)
                timeHandler.postDelayed(this, 0);
        }
    };

    public void onClick(View v)  {
        // don't do anything if there's a dialog box displayed
        if(!onRestCD_Tabata && hasStarted) {
            switch (v.getId()) {
                case R.id.btnStop:
                    stopTimer();
                    break;
                case R.id.whole_layout:
                    break;
            }
        }
    }

    private void stopTimer() {
        toneG.startTone(ToneGenerator.TONE_DTMF_D, Constants.long_tone);
        v.vibrate(Constants.long_tone);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        timeHandler.removeCallbacks(updateTimerMethod);
        saveWorkout();
        // Reset Volume
        android.os.SystemClock.sleep(Constants.long_tone);
        audioManager.setStreamVolume(AudioManager.STREAM_DTMF, AudioManager.ADJUST_SAME, 0);
        // Go to display screen
        Intent i = new Intent(this, DisplayTypeTwo.class);
        RunningTabata.this.startActivity(i);
    }

    private void restCountdownDialog (){
        timeHandler.removeCallbacks(updateTimerMethod);
        onRestCD_Tabata = true;
        restDialog = RestCD_Tabata.newInstance(PHIIT_preferences.getInt(getString(R.string.ValTabataRest), 10) * 1000);
        restDialog.setCancelable(false);
        restDialog.show(fm, getString(R.string.check_rest_round));

    }

    private void saveWorkout() {
        PHIIT_DataSource dataSource = new PHIIT_DataSource(this);
        dataSource.open();
        // set workout number as one higher than previous max workout number
        int workoutNumber = 1 + dataSource.LastEntry(DatabaseContract.ArchivedWorkouts.TABLE_NAME, DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NUMBER);
        dataSource.saveReps(workoutNumber, repCount);

        Workout justCompleted = new Workout();
        justCompleted.setWorkoutNumber(workoutNumber);
        justCompleted.setWorkoutName(getString(R.string.text_workout_prefix) + workoutNumber);
        justCompleted.setWorkoutType(this.getClass().getSimpleName().replace(getString(R.string.text_running_prefix), "").replace(getString(R.string.text_repeats_suffix), getString(R.string.text_repeats_replace)));
//        justCompleted.setDateRan(); Set in DataSource;
        justCompleted.setRoundsSet(PHIIT_preferences.getInt(getString(R.string.ValTabataRounds), 0));
        justCompleted.setExercisesSet(PHIIT_preferences.getInt(getString(R.string.ValTabataExercises), 0));
        if (completed) {
            justCompleted.setRoundsCompleted(currentRound);
            justCompleted.setExercisesCompleted(currentExercise);
        } else{
            justCompleted.setRoundsCompleted(currentRound -1);
            justCompleted.setExercisesCompleted(currentExercise - 1);
        }
        justCompleted.setTimeCap(PHIIT_preferences.getInt(getString(R.string.ValTabataTimeCap), 0));
        if(PHIIT_preferences.getBoolean(getString(R.string.CheckTabataRest), false))
            justCompleted.setRestRound(PHIIT_preferences.getInt(getString(R.string.ValTabataRest), 0));
        //justCompleted.setBuyIn(false);
        //justCompleted.setCashOut(false);
        //justCompleted.setNotes(false);

        dataSource.saveWorkout(justCompleted);
        dataSource.close();
    }

    public void onDestroy() {
        if (!onRestCD_Tabata && hasStarted)
            timeHandler.removeCallbacks(updateTimerMethod);
        super.onDestroy();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(getString(R.string.on_rest), onRestCD_Tabata);
        savedInstanceState.putBoolean(getString(R.string.on_starting_cd), onStartingCD);
        savedInstanceState.putInt(getString(R.string.current_round), currentRound);
        savedInstanceState.putInt(getString(R.string.current_exercise), currentExercise);
        savedInstanceState.putLong(getString(R.string.current_start), startTime);
        savedInstanceState.putBoolean(getString(R.string.has_started), hasStarted);
        // make a one dimensional array
        if(hasStarted) {
            int[] oneDimSplits = new int[repCount.length * repCount[0].length];
            for (int i = 0; i < repCount.length; i++) {
                System.arraycopy(repCount[i], 0, oneDimSplits, i * repCount[0].length, repCount[0].length);
            }
            savedInstanceState.putIntArray(getString(R.string.rep_count), oneDimSplits);
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

}
