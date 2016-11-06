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


public class DisplayTypeTwo extends Activity implements OnClickListener {
    protected Button btnSave;
    protected Button btnBackMain;
    protected ListView listCycleReps;
    protected TextView lblCyclesCompleted;
    protected TextView lblWorkout;
    protected TextView lblTotalReps;
    protected int workoutNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_type_two);
        PHIIT_DataSource dataSource = new PHIIT_DataSource(this);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnBackMain = (Button) findViewById(R.id.btnBackMain);
        lblCyclesCompleted = (TextView) findViewById(R.id.lblTotalCyclesCompleted);
        lblWorkout = (TextView) findViewById(R.id.lblWorkout);
        lblTotalReps = (TextView) findViewById(R.id.lblTotalReps);
        listCycleReps = (ListView) findViewById(R.id.listRepCounts);

        // Check whether we've got a new or archived workout
        Intent i = getIntent();
        workoutNumber = i.getIntExtra(getString(R.string.bundle_workout_number)
                , dataSource.LastEntry(DatabaseContract.ArchivedWorkouts.TABLE_NAME, DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NUMBER));

        //Populate Views
        Workout activeWorkout = dataSource.WorkoutDetails(workoutNumber);
        String workoutType = activeWorkout.getWorkoutType();
        int roundsComp = activeWorkout.getRoundsCompleted();
        int roundsSet = activeWorkout.getRoundsSet();
        int exercisesComp = activeWorkout.getExercisesCompleted();
        int exercisesSet = activeWorkout.getExercisesSet();
        int cyclesComp;
        int cyclesSet = roundsSet * exercisesSet;

        // FGB & Tabata
        if (exercisesSet == exercisesComp && roundsSet == roundsComp)
            cyclesComp = roundsComp * exercisesComp;
        else {
            if (workoutType.equals(getResources().getStringArray(R.array.array_display_two)[0]))
                cyclesComp = roundsComp + roundsSet * exercisesComp;
            else if (workoutType.equals(getResources().getStringArray(R.array.array_display_two)[1]))
                cyclesComp = exercisesComp + exercisesSet * roundsComp;
            else
                cyclesComp = 0;
        }
        //EMOM
        if (workoutType.equals(getResources().getStringArray(R.array.array_display_two)[2])) {
            cyclesComp = roundsComp;
            cyclesSet = roundsSet;
        }

        lblCyclesCompleted.setText(String.valueOf(lblCyclesCompleted.getText()) + String.valueOf(cyclesComp) + getString(R.string.text_of) + String.valueOf(cyclesSet));
        lblWorkout.setText(dataSource.getField(workoutNumber, DatabaseContract.ArchivedWorkouts.TABLE_NAME, DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NAME));
        lblTotalReps.setText(String.valueOf(lblTotalReps.getText()) + dataSource.TotalReps(workoutNumber));

//        ArrayList<Cycle> repCounts = dataSource.RepCounts(workoutNumber);
        ArrayList<Cycle> repCounts = dataSource.SummaryRepCounts(workoutNumber);
        ArrayAdapter_Cycles arrayAdapter = new ArrayAdapter_Cycles(this, R.layout.list_rounds_layout, repCounts);
        listCycleReps.setAdapter(arrayAdapter);

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
            DisplayTypeTwo.this.startActivity(i);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(getString(R.string.bundle_goto_archive), true);
        DisplayTypeTwo.this.startActivity(i);
    }

}
