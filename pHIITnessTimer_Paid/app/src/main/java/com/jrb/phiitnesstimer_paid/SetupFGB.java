package com.jrb.phiitnesstimer_paid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SetupFGB extends Activity implements OnClickListener {
    protected CheckBox chkStartingCD;
    protected CheckBox chkTimeCapRound;
    protected CheckBox chkRestRound;
    protected CheckBox chkNumberRounds;
    protected CheckBox chkNumberExercises;
    protected CheckBox chkCountUD;
    protected Spinner spinStartingCD;
    protected Spinner spinNumberRounds;
    protected Spinner spinNumberExercises;
    protected Spinner spinCountUD;
    protected EditText txtTimeCapRoundMins;
    protected EditText txtTimeCapRoundSecs;
    protected EditText txtRestRoundMins;
    protected EditText txtRestRoundSecs;
    protected Button btnStart;
    protected SharedPreferences PHIIT_preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_fgb);
        PHIIT_preferences = getSharedPreferences(getString(R.string.PHIIT_Prefs), 0);

        // Connect interface elements to properties
        chkStartingCD = (CheckBox) findViewById(R.id.chkStartingCD);
        chkTimeCapRound = (CheckBox) findViewById(R.id.chkTimeCapRound);
        chkRestRound = (CheckBox) findViewById(R.id.chkRestRound);
        chkNumberRounds = (CheckBox) findViewById(R.id.chkNumberRounds);
        chkNumberExercises = (CheckBox) findViewById(R.id.chkNumberExercises);
        chkCountUD = (CheckBox) findViewById(R.id.chkCountUD);
        spinStartingCD = (Spinner) findViewById(R.id.spinStartingCD);
        txtTimeCapRoundMins = (EditText) findViewById(R.id.txtTimeCapRoundMins);
        txtTimeCapRoundSecs = (EditText) findViewById(R.id.txtTimeCapRoundSecs);
        txtRestRoundMins = (EditText) findViewById(R.id.txtRestRoundMins);
        txtRestRoundSecs = (EditText) findViewById(R.id.txtRestRoundSecs);
        spinNumberRounds = (Spinner) findViewById(R.id.spinNumberRounds);
        spinNumberExercises = (Spinner) findViewById(R.id.spinNumberExercises);
        spinCountUD = (Spinner) findViewById(R.id.spinCountUD);
        btnStart = (Button) findViewById(R.id.btnStart);

        //Populate Spinners
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.array_startingCD, R.layout.custom_spinner_style);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinStartingCD.setAdapter(adapter);
        adapter = ArrayAdapter.createFromResource(this, R.array.array_number_cycles, R.layout.custom_spinner_style);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinNumberRounds.setAdapter(adapter);
        adapter = ArrayAdapter.createFromResource(this, R.array.array_number_cycles, R.layout.custom_spinner_style);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinNumberExercises.setAdapter(adapter);
        adapter = ArrayAdapter.createFromResource(this, R.array.array_countUD, R.layout.custom_spinner_style);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCountUD.setAdapter(adapter);

        //Populate check boxes and time boxes from sharedPreferences
        chkStartingCD.setChecked(PHIIT_preferences.getBoolean(getString(R.string.CheckStartingCD), true));
        chkTimeCapRound.setChecked(true);
        chkTimeCapRound.setText(chkTimeCapRound.getText() + getString(R.string.check_mandatory));
        chkRestRound.setChecked(PHIIT_preferences.getBoolean(getString(R.string.CheckRestRounds), false));
        chkNumberRounds.setChecked(true);
        chkNumberRounds.setText(chkNumberRounds.getText() + getString(R.string.check_mandatory));
        chkNumberExercises.setChecked(true);
        chkNumberExercises.setText(chkNumberExercises.getText() + getString(R.string.check_mandatory));
        chkCountUD.setChecked(true);
        chkCountUD.setText(chkCountUD.getText() + getString(R.string.check_mandatory));
        spinStartingCD.setSelection(PHIIT_preferences.getInt(getString(R.string.DefStartingCD), 5));
        txtTimeCapRoundMins.setText(TimeFunctions.getMins(PHIIT_preferences.getInt(getString(R.string.DefFGBTimeCap), 0)));
        txtTimeCapRoundSecs.setText(TimeFunctions.getSecs(PHIIT_preferences.getInt(getString(R.string.DefFGBTimeCap), 0)));
        txtRestRoundMins.setText(TimeFunctions.getMins(PHIIT_preferences.getInt(getString(R.string.DefFGBRest), 0)));
        txtRestRoundSecs.setText(TimeFunctions.getSecs(PHIIT_preferences.getInt(getString(R.string.DefFGBRest), 0)));
        spinNumberRounds.setSelection(PHIIT_preferences.getInt(getString(R.string.DefFGBRounds), 2));
        spinNumberExercises.setSelection(PHIIT_preferences.getInt(getString(R.string.DefFGBExercises), 2));
        spinCountUD.setSelection(PHIIT_preferences.getInt(getString(R.string.DefCountUD), 0));

        // Setup ClickListeners
        chkTimeCapRound.setOnClickListener(this);
        chkNumberRounds.setOnClickListener(this);
        chkNumberExercises.setOnClickListener(this);
        chkCountUD.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnStart.requestFocus();
        btnStart.setOnFocusChangeListener(new  View.OnFocusChangeListener() {
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
            case R.id.btnStart:
                if(validateText()) {
                    i = new Intent(this, RunningFGB.class);
                    savePreferences();
                }
                break;
            case R.id.chkTimeCapRound:
                chkTimeCapRound.setChecked(true);
                Toast.makeText(getApplicationContext(), getString(R.string.toast_mandatory), Toast.LENGTH_SHORT).show();
                break;
            case R.id.chkNumberRounds:
                chkNumberRounds.setChecked(true);
                Toast.makeText(getApplicationContext(), getString(R.string.toast_mandatory), Toast.LENGTH_SHORT).show();
                break;
            case R.id.chkNumberExercises:
                chkNumberExercises.setChecked(true);
                Toast.makeText(getApplicationContext(), getString(R.string.toast_mandatory), Toast.LENGTH_SHORT).show();
                break;
            case R.id.chkCountUD:
                chkCountUD.setChecked(true);
                Toast.makeText(getApplicationContext(), getString(R.string.toast_mandatory), Toast.LENGTH_SHORT).show();
                break;
        }
        if (i != null)
            SetupFGB.this.startActivity(i);
    }

    private void savePreferences(){
        // Save shared preferences
        SharedPreferences.Editor editor = PHIIT_preferences.edit();
        editor.putBoolean(getString(R.string.CheckStartingCD), chkStartingCD.isChecked());
        editor.putBoolean(getString(R.string.CheckFGBRest), chkRestRound.isChecked());
        editor.putInt(getString(R.string.ValStartingCD), Integer.valueOf(spinStartingCD.getSelectedItem().toString()));
        editor.putInt(getString(R.string.ValFGBTimeCap), Integer.valueOf(TimeFunctions.totalSecs(txtTimeCapRoundSecs.getText().toString(), txtTimeCapRoundMins.getText().toString())));
        editor.putInt(getString(R.string.ValFGBRest), Integer.valueOf(TimeFunctions.totalSecs(txtRestRoundSecs.getText().toString(), txtRestRoundMins.getText().toString())));
        editor.putInt(getString(R.string.ValFGBRounds), Integer.valueOf(spinNumberRounds.getSelectedItem().toString()));
        editor.putInt(getString(R.string.ValFGBExercises), Integer.valueOf(spinNumberExercises.getSelectedItem().toString()));
        editor.putInt(getString(R.string.ValCountUD), spinCountUD.getSelectedItemPosition());
        editor.apply();

    }

    private Boolean validateText(){
        Boolean isValid = true;
        if(chkTimeCapRound.isChecked())
            if(Integer.valueOf(TimeFunctions.totalSecs(txtTimeCapRoundSecs.getText().toString(), txtTimeCapRoundMins.getText().toString())) == 0){
                Toast.makeText(getApplicationContext(), getString(R.string.check_time_cap_cycle) + getString(R.string.toast_valid_zero), Toast.LENGTH_SHORT).show();
                isValid = false;
            }
        if(chkRestRound.isChecked())
            if(Integer.valueOf(TimeFunctions.totalSecs(txtRestRoundSecs.getText().toString(), txtRestRoundMins.getText().toString())) == 0){
                Toast.makeText(getApplicationContext(), getString(R.string.check_rest_per_cycle) + getString(R.string.toast_valid_zero), Toast.LENGTH_SHORT).show();
                isValid = false;
            }

        return isValid;
    }
}
