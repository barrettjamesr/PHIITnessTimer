package com.jrb.phiitnesstimer_paid;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;


public class DisplayTypeOne extends Activity implements OnClickListener {
    protected Button btnSave;
    protected Button btnBackMain;
    protected ListView listRoundSplits;
    protected TextView lblRoundsCompleted;
    protected TextView lblWorkout;
    protected TextView lblWorkTime;
    protected TextView lblRestTime;
    protected int workoutNumber;
    protected int workoutRounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_type_one);
        PHIIT_DataSource dataSource = new PHIIT_DataSource(this);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnBackMain = (Button) findViewById(R.id.btnBackMain);
        listRoundSplits = (ListView) findViewById(R.id.listRoundSplits);
        lblRoundsCompleted = (TextView) findViewById(R.id.lblRoundsCompleted);
        lblWorkout = (TextView) findViewById(R.id.lblWorkout);
        lblWorkTime = (TextView) findViewById(R.id.lblWorkTime);
        lblRestTime = (TextView) findViewById(R.id.lblRestTime);


        // Check whether we've got a new or archived workout
        Intent i = getIntent();
        workoutNumber = i.getIntExtra(getString(R.string.bundle_workout_number)
                , dataSource.LastEntry(DatabaseContract.ArchivedWorkouts.TABLE_NAME, DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NUMBER));

        //Populate Views
        workoutRounds = dataSource.WorkoutNumRounds(workoutNumber);
        lblRoundsCompleted.setText(lblRoundsCompleted.getText() + String.valueOf(workoutRounds));
        lblWorkout.setText(dataSource.getField(workoutNumber, DatabaseContract.ArchivedWorkouts.TABLE_NAME, DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NAME));
        lblWorkTime.setText(lblWorkTime.getText() + TimeFunctions.displayTime(dataSource.WorkTime(workoutNumber)));
        lblRestTime.setText(lblRestTime.getText() + TimeFunctions.displayTime(dataSource.RestTime(workoutNumber)));

        ArrayList<Round> time_splits = dataSource.WorkoutRounds(workoutNumber);
        ArrayAdapter_Rounds arrayAdapter = new ArrayAdapter_Rounds(this, R.layout.list_rounds_layout, time_splits);
        listRoundSplits.setAdapter(arrayAdapter);

        // Setup ClickListeners
        btnSave.setOnClickListener(this);
        btnBackMain.setOnClickListener(this);

        btnSave.requestFocus();
        btnSave.setOnFocusChangeListener(new  View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    v.callOnClick();
            }
        });
        btnBackMain.setOnFocusChangeListener(new  View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    v.callOnClick();
            }
        });

    }

    public void onClick(View v)  {
        Intent i = null;
        switch(v.getId()){
            case R.id.btnSave:
                //i = new Intent(this, RunningRFT.class);
                Toast.makeText(getApplicationContext(), "SORRY \nNot ready yet!", Toast.LENGTH_LONG).show();
                break;
            case R.id.btnBackMain:
                i = new Intent(this, MainActivity.class);
                break;
        }
        if (i != null)
            DisplayTypeOne.this.startActivity(i);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(getString(R.string.bundle_goto_archive), true);
        DisplayTypeOne.this.startActivity(i);
    }

}
