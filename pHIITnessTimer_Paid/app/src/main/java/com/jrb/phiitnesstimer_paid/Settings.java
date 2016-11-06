package com.jrb.phiitnesstimer_paid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends Fragment implements OnClickListener {
    FragmentManager fm;
    protected View rootView;
    protected SharedPreferences PHIIT_preferences;
    protected FragmentActivity context;
    protected CheckBox chkSoundVolume;
    protected CheckBox chkStartingCD;
    protected CheckBox chkTimeWarning;
    protected CheckBox chkCountUD;
    protected Spinner spinStartingCD;
    protected Spinner spinCountUD;
    protected TextView txtVolume;
    protected EditText txtTimeWarningMins;
    protected EditText txtTimeWarningSecs;
    protected Button btnConfirm;
    protected Button btnUndo;
    Activity thisActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.activity_settings, container, false);
        thisActivity = (Activity) (rootView.getContext());
        PHIIT_preferences = thisActivity.getSharedPreferences(getString(R.string.PHIIT_Prefs), 0);
        fm  = context.getSupportFragmentManager();

        // Connect interface elements to properties
        chkSoundVolume = (CheckBox) rootView.findViewById(R.id.chkSoundVolume);
        chkStartingCD = (CheckBox) rootView.findViewById(R.id.chkStartingCD);
        chkTimeWarning = (CheckBox) rootView.findViewById(R.id.chkTimeWarning);
        chkCountUD = (CheckBox) rootView.findViewById(R.id.chkCountUD);
        txtVolume = (TextView) rootView.findViewById(R.id.lblVolume);
        spinStartingCD = (Spinner) rootView.findViewById(R.id.spinStartingCD);
        txtTimeWarningMins = (EditText) rootView.findViewById(R.id.txtTimeWarningMins);
        txtTimeWarningSecs = (EditText) rootView.findViewById(R.id.txtTimeWarningSecs);
        spinCountUD = (Spinner) rootView.findViewById(R.id.spinCountUD);
        btnConfirm = (Button) rootView.findViewById(R.id.btnConfirm);
        btnUndo = (Button) rootView.findViewById(R.id.btnUndo);

        //Populate Spinners
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.array_startingCD, R.layout.custom_spinner_style);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinStartingCD.setAdapter(adapter);
        adapter = ArrayAdapter.createFromResource(context, R.array.array_countUD, R.layout.custom_spinner_style);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCountUD.setAdapter(adapter);

        //Populate check boxes and time boxes from sharedPreferences
        loadDefaults();

        // Setup ClickListeners
        txtVolume.setOnClickListener(this);
        chkCountUD.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        btnUndo.setOnClickListener(this);
        btnConfirm.requestFocus();
        btnConfirm.setOnFocusChangeListener(new  View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    v.callOnClick();
            }
        });
        btnUndo.setOnFocusChangeListener(new  View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    v.callOnClick();
            }
        });

        return rootView;
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.btnConfirm:
                saveChanges();
                break;
            case R.id.btnUndo:
                loadDefaults();
                break;
            case R.id.lblVolume:
                VolumeSetting setVolume = VolumeSetting.newInstance();
                setVolume.setTargetFragment(this, 1);
                setVolume.show(fm, getString(R.string.check_sound_volume));
                break;
            case R.id.chkCountUD:
                chkCountUD.setChecked(true);
                Toast.makeText(this.getActivity().getApplicationContext(), getString(R.string.toast_mandatory), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        context=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==1)
            txtVolume.setText(data.getStringExtra(getString(R.string.bundle_volume)));
    }

    private void loadDefaults(){

        chkSoundVolume.setChecked(PHIIT_preferences.getBoolean(getString(R.string.CheckSoundVolume), true));
        chkStartingCD.setChecked(PHIIT_preferences.getBoolean(getString(R.string.CheckStartingCD), true));
        chkTimeWarning.setChecked(PHIIT_preferences.getBoolean(getString(R.string.CheckTimeWarning), true));
        chkCountUD.setChecked(true);    //Must be checked
        txtVolume.setText(String.valueOf (PHIIT_preferences.getInt(getString(R.string.SoundVolume), 50)));
        spinStartingCD.setSelection(PHIIT_preferences.getInt(getString(R.string.DefStartingCD), 5));
        txtTimeWarningMins.setText(TimeFunctions.getMins(PHIIT_preferences.getInt(getString(R.string.DefTimeWarning), 0)));
        txtTimeWarningSecs.setText(TimeFunctions.getSecs(PHIIT_preferences.getInt(getString(R.string.DefTimeWarning), 0)));
        spinCountUD.setSelection(PHIIT_preferences.getInt(getString(R.string.DefCountUD), 0));
    }

    private void saveChanges(){
        // Save shared preferences
        SharedPreferences.Editor editor = PHIIT_preferences.edit();

        editor.putBoolean(getString(R.string.CheckSoundVolume), chkSoundVolume.isChecked());
        editor.putBoolean(getString(R.string.CheckStartingCD), chkStartingCD.isChecked());
        editor.putBoolean(getString(R.string.CheckTimeWarning), chkTimeWarning.isChecked());
        editor.putInt(getString(R.string.SoundVolume), Integer.valueOf(String.valueOf(txtVolume.getText())));
        editor.putInt(getString(R.string.DefStartingCD), spinStartingCD.getSelectedItemPosition());
        editor.putInt(getString(R.string.DefTimeWarning), Integer.valueOf(TimeFunctions.totalSecs(txtTimeWarningSecs.getText().toString(), txtTimeWarningMins.getText().toString())));
        editor.putInt(getString(R.string.DefCountUD), spinCountUD.getSelectedItemPosition());
        editor.commit();
        Toast.makeText(this.getActivity().getApplicationContext(), R.string.label_defaults_saved, Toast.LENGTH_SHORT ).show();
    }
}
