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

public class SetupAMRAPrepeats extends Activity implements OnClickListener {
    protected CheckBox chkStartingCD;
    protected CheckBox chkTimeCapCycles;
    protected CheckBox chkNumberCycles;
    protected CheckBox chkRestCycles;
    protected CheckBox chkTimeWarning;
    protected CheckBox chkCountUD;
    protected CheckBox chkResetRounds;
    protected Spinner spinStartingCD;
    protected Spinner spinNumberCycles;
    protected Spinner spinCountUD;
    protected Spinner spinResetRounds;
    protected EditText txtTimeCapCyclesMins;
    protected EditText txtTimeCapCyclesSecs;
    protected EditText txtRestCyclesMins;
    protected EditText txtRestCyclesSecs;
    protected EditText txtTimeWarningMins;
    protected EditText txtTimeWarningSecs;
    protected Button btnStart;
    protected SharedPreferences PHIIT_preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_amraprepeats);
        PHIIT_preferences = getSharedPreferences(getString(R.string.PHIIT_Prefs), 0);

        // Connect interface elements to properties
        chkStartingCD = (CheckBox) findViewById(R.id.chkStartingCD);
        chkTimeCapCycles = (CheckBox) findViewById(R.id.chkTimeCapCycles);
        chkNumberCycles = (CheckBox) findViewById(R.id.chkNumberCycles);
        chkRestCycles = (CheckBox) findViewById(R.id.chkRestCycles);
        chkTimeWarning = (CheckBox) findViewById(R.id.chkTimeWarning);
        chkCountUD = (CheckBox) findViewById(R.id.chkCountUD);
        chkResetRounds = (CheckBox) findViewById(R.id.chkResetRounds);
        spinStartingCD = (Spinner) findViewById(R.id.spinStartingCD);
        txtTimeCapCyclesMins = (EditText) findViewById(R.id.txtTimeCapCyclesMins);
        txtTimeCapCyclesSecs = (EditText) findViewById(R.id.txtTimeCapCyclesSecs);
        spinNumberCycles = (Spinner) findViewById(R.id.spinNumberCycles);
        txtRestCyclesMins = (EditText) findViewById(R.id.txtRestCyclesMins);
        txtRestCyclesSecs = (EditText) findViewById(R.id.txtRestCyclesSecs);
        txtTimeWarningMins = (EditText) findViewById(R.id.txtTimeWarningMins);
        txtTimeWarningSecs = (EditText) findViewById(R.id.txtTimeWarningSecs);
        spinCountUD = (Spinner) findViewById(R.id.spinCountUD);
        spinResetRounds = (Spinner) findViewById(R.id.spinResetRounds);
        btnStart = (Button) findViewById(R.id.btnStart);

        //Populate Spinners
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.array_startingCD, R.layout.custom_spinner_style);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinStartingCD.setAdapter(adapter);
        adapter = ArrayAdapter.createFromResource(this, R.array.array_number_cycles, R.layout.custom_spinner_style);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinNumberCycles.setAdapter(adapter);
        adapter = ArrayAdapter.createFromResource(this, R.array.array_countUD, R.layout.custom_spinner_style);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCountUD.setAdapter(adapter);
        adapter = ArrayAdapter.createFromResource(this, R.array.array_reset_rounds, R.layout.custom_spinner_style);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinResetRounds.setAdapter(adapter);

        //Populate check boxes and time boxes from sharedPreferences
        chkStartingCD.setChecked(PHIIT_preferences.getBoolean(getString(R.string.CheckStartingCD), true));
        chkTimeCapCycles.setChecked(true);
        chkTimeCapCycles.setText(chkTimeCapCycles.getText() + getString(R.string.check_mandatory));
        chkNumberCycles.setChecked(true);
        chkNumberCycles.setText(chkNumberCycles.getText() + getString(R.string.check_mandatory));
        chkRestCycles.setChecked(PHIIT_preferences.getBoolean(getString(R.string.CheckRestCycles), false));
        chkTimeWarning.setChecked(PHIIT_preferences.getBoolean(getString(R.string.CheckTimeWarning), false));
        chkCountUD.setChecked(true);
        chkCountUD.setText(chkCountUD.getText() + getString(R.string.check_mandatory));
        chkResetRounds.setChecked(true);
        chkResetRounds.setText(chkResetRounds.getText() + getString(R.string.check_mandatory));
        spinStartingCD.setSelection(PHIIT_preferences.getInt(getString(R.string.DefStartingCD), 5));
        txtTimeCapCyclesMins.setText(TimeFunctions.getMins(PHIIT_preferences.getInt(getString(R.string.DefTimeCapCycles), 0)));
        txtTimeCapCyclesSecs.setText(TimeFunctions.getSecs(PHIIT_preferences.getInt(getString(R.string.DefTimeCapCycles), 0)));
        spinNumberCycles.setSelection(PHIIT_preferences.getInt(getString(R.string.DefNumberCycles), 2));
        txtRestCyclesMins.setText(TimeFunctions.getMins(PHIIT_preferences.getInt(getString(R.string.DefRestCycles), 0)));
        txtRestCyclesSecs.setText(TimeFunctions.getSecs(PHIIT_preferences.getInt(getString(R.string.DefRestCycles), 0)));
        txtTimeWarningMins.setText(TimeFunctions.getMins(PHIIT_preferences.getInt(getString(R.string.DefTimeWarning), 0)));
        txtTimeWarningSecs.setText(TimeFunctions.getSecs(PHIIT_preferences.getInt(getString(R.string.DefTimeWarning), 0)));
        spinCountUD.setSelection(PHIIT_preferences.getInt(getString(R.string.DefCountUD), 0));
        spinResetRounds.setSelection(PHIIT_preferences.getInt(getString(R.string.DefResetRounds), 0));

        // Setup ClickListeners
        chkTimeCapCycles.setOnClickListener(this);
        chkNumberCycles.setOnClickListener(this);
        chkCountUD.setOnClickListener(this);
        chkResetRounds.setOnClickListener(this);
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
                    i = new Intent(this, RunningAMRAPrepeats.class);
                    savePreferences();
                }
                break;
            case R.id.chkTimeCapCycles:
                chkTimeCapCycles.setChecked(true);
                Toast.makeText(getApplicationContext(), getString(R.string.toast_mandatory), Toast.LENGTH_SHORT).show();
                break;
            case R.id.chkNumberCycles:
                chkNumberCycles.setChecked(true);
                Toast.makeText(getApplicationContext(), getString(R.string.toast_mandatory), Toast.LENGTH_SHORT).show();
                break;
            case R.id.chkCountUD:
                chkCountUD.setChecked(true);
                Toast.makeText(getApplicationContext(), getString(R.string.toast_mandatory), Toast.LENGTH_SHORT).show();
                break;
            case R.id.chkResetRounds:
                chkResetRounds.setChecked(true);
                Toast.makeText(getApplicationContext(), getString(R.string.toast_mandatory), Toast.LENGTH_SHORT).show();
                break;
        }
        if (i != null)
            SetupAMRAPrepeats.this.startActivity(i);
    }

    private void savePreferences(){
        // Save shared preferences
        SharedPreferences.Editor editor = PHIIT_preferences.edit();
        editor.putBoolean(getString(R.string.CheckStartingCD), chkStartingCD.isChecked());
        editor.putBoolean(getString(R.string.CheckRestCycles), chkRestCycles.isChecked());
        editor.putBoolean(getString(R.string.CheckTimeWarning), chkTimeWarning.isChecked());
        editor.putInt(getString(R.string.ValStartingCD), Integer.valueOf(spinStartingCD.getSelectedItem().toString()));
        editor.putInt(getString(R.string.ValTimeCapCycles), Integer.valueOf(TimeFunctions.totalSecs(txtTimeCapCyclesSecs.getText().toString(), txtTimeCapCyclesMins.getText().toString())));
        editor.putInt(getString(R.string.ValNumberCycles), Integer.valueOf(spinNumberCycles.getSelectedItem().toString()));
        editor.putInt(getString(R.string.ValRestCycles), Integer.valueOf(TimeFunctions.totalSecs(txtRestCyclesSecs.getText().toString(), txtRestCyclesMins.getText().toString())));
        editor.putInt(getString(R.string.ValTimeWarning), Integer.valueOf(TimeFunctions.totalSecs(txtTimeWarningSecs.getText().toString(), txtTimeWarningMins.getText().toString())));
        editor.putInt(getString(R.string.ValCountUD), spinCountUD.getSelectedItemPosition());
        editor.putInt(getString(R.string.ValResetRounds), spinResetRounds.getSelectedItemPosition());
        editor.apply();

    }

    private Boolean validateText(){
        Boolean isValid = true;
        if(chkTimeCapCycles.isChecked())
            if(Integer.valueOf(TimeFunctions.totalSecs(txtTimeCapCyclesSecs.getText().toString(), txtTimeCapCyclesMins.getText().toString())) == 0){
                Toast.makeText(getApplicationContext(), getString(R.string.check_time_cap_cycle) + getString(R.string.toast_valid_zero), Toast.LENGTH_SHORT).show();
                isValid = false;
            }
        if(chkRestCycles.isChecked())
            if(Integer.valueOf(TimeFunctions.totalSecs(txtRestCyclesSecs.getText().toString(), txtRestCyclesMins.getText().toString())) == 0){
                Toast.makeText(getApplicationContext(), getString(R.string.check_rest_per_cycle) + getString(R.string.toast_valid_zero), Toast.LENGTH_SHORT).show();
                isValid = false;
            }
        if(chkTimeWarning.isChecked())
            if(Integer.valueOf(TimeFunctions.totalSecs(txtTimeWarningSecs.getText().toString(), txtTimeWarningMins.getText().toString())) == 0){
                Toast.makeText(getApplicationContext(), getString(R.string.check_time_warning) + getString(R.string.toast_valid_zero), Toast.LENGTH_SHORT).show();
                isValid = false;
            }

        return isValid;
    }
}
