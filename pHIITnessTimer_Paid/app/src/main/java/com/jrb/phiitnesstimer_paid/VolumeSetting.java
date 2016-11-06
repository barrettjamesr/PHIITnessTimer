package com.jrb.phiitnesstimer_paid;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class VolumeSetting extends DialogFragment {
    SharedPreferences PHIIT_preferences;
    Activity thisActivity;
    SeekBar seekVolume;
    Button btnOkay;
    Button btnCancel;

    public static VolumeSetting newInstance() {
        VolumeSetting fragment = new VolumeSetting();
        return fragment;
    }

    public VolumeSetting() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_volume_setting, container, false);
        PHIIT_preferences = rootView.getContext().getSharedPreferences(getString(R.string.PHIIT_Prefs), 0);
        seekVolume = (SeekBar) rootView.findViewById(R.id.seekVolume);
        btnOkay = (Button) rootView.findViewById(R.id.btnOkay);
        btnCancel = (Button) rootView.findViewById(R.id.btnCancel);

        seekVolume.setProgress(PHIIT_preferences.getInt(getString(R.string.SoundVolume), Constants.buzzer_volume) );
        getDialog().setTitle(getString(R.string.title_change_volume) + seekVolume.getProgress());

        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(getString(R.string.bundle_volume), String.valueOf(seekVolume.getProgress()));
                getTargetFragment().onActivityResult(getTargetRequestCode(), 1, intent);
                getDialog().dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), 0, null);
                getDialog().dismiss();
            }
        });
        seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                getDialog().setTitle(getString(R.string.title_change_volume) + seekVolume.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
        return rootView;
    }

    public interface VolumeDialogListener {
        void onFinishDialog(String volumeAsText);
    }
}
