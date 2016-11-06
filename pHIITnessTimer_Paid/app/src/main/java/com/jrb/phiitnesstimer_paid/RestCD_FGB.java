package com.jrb.phiitnesstimer_paid;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.DialogFragment;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RestCD_FGB extends DialogFragment {
    protected TextView countDownDisplay;
    ToneGenerator toneG;
    SharedPreferences PHIIT_preferences;
    CountDownTimer restCountDownTimer;
//    EditText repsCompleted;
    ListView listRepEntry;
    int numExercises;
    String[] repsArray;
    private static final String restRound = "RestRound";
    Vibrator v;

    private int intRestRound;

    public static RestCD_FGB newInstance(int restTime) {
        RestCD_FGB fragment = new RestCD_FGB();
        Bundle args = new Bundle();
        args.putInt(restRound, restTime);
        fragment.setArguments(args);
        return fragment;
    }

    public RestCD_FGB() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            intRestRound = getArguments().getInt(restRound);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rest_cd_fgb, container, false);
        countDownDisplay = (TextView) rootView.findViewById(R.id.countdownDisplay);
        PHIIT_preferences =  rootView.getContext().getSharedPreferences(getString(R.string.PHIIT_Prefs), 0);
        toneG = new ToneGenerator(AudioManager.STREAM_DTMF, PHIIT_preferences.getInt(getString(R.string.SoundVolume), Constants.buzzer_volume));
        v = (Vibrator) rootView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        listRepEntry = (ListView) rootView.findViewById(R.id.listRepEntry);
        numExercises = PHIIT_preferences.getInt(getString(R.string.ValFGBExercises), 1);
        repsArray = new String[numExercises];

        ArrayList<String> integerArrayList = new ArrayList<>();
        for (int i = 1 ; i <= numExercises ; i++ ) {
            integerArrayList.add("");
        }
        ArrayAdapter_Reps arrayAdapter = new ArrayAdapter_Reps(getActivity(), R.layout.list_fgb_exercises, integerArrayList );
        listRepEntry.setAdapter(arrayAdapter);

        getDialog().setTitle(getResources().getString(R.string.title_rest_countdown));

        if(savedInstanceState!=null)
            intRestRound = savedInstanceState.getInt(getString(R.string.current_rest));

        restCountDownTimer = new CountDownTimer(intRestRound, 10) {
            boolean three = true;
            boolean two = true;
            boolean one = true;
            @Override
            public void onTick(long millisUntilFinished) {
                intRestRound = (int) millisUntilFinished;
                countDownDisplay.setText(String.format("%.1f", millisUntilFinished / 1000.0));
                //Play tone once in each second prior to start
                if (millisUntilFinished <= 3000 && three) {
                    toneG.startTone(ToneGenerator.TONE_DTMF_0, Constants.short_tone);
                    v.vibrate(Constants.short_tone);
                    three = false;
                }
                else if (millisUntilFinished <= 2000 && two) {
                    toneG.startTone(ToneGenerator.TONE_DTMF_0, Constants.short_tone);
                    v.vibrate(Constants.short_tone);
                    two = false;
                }
                else if (millisUntilFinished <= 1000 && one) {
                    toneG.startTone(ToneGenerator.TONE_DTMF_0, Constants.short_tone);
                    v.vibrate(Constants.short_tone);
                    one = false;
                }
            }

            @Override
            public void onFinish() {
                if(listRepEntry.getFocusedChild()!=null)
                    listRepEntry.getFocusedChild().clearFocus();
                toneG.startTone(ToneGenerator.TONE_DTMF_D,  Constants.long_tone);
                v.vibrate(Constants.long_tone);
                getDialog().dismiss();
            }
        };

        restCountDownTimer.start();
        getDialog().show();

        return rootView;
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);

        final int firstListItemPosition = 0;
        final int lastListItemPosition = firstListItemPosition + numExercises - 1;
        RunningFGB callingActivity = (RunningFGB) getActivity();

        for (int pos = firstListItemPosition ; pos <= lastListItemPosition ; pos++) {
            String reps = listRepEntry.getAdapter().getItem(pos).toString().trim() ;
            if (reps.equals("")) reps = "0";
            repsArray[pos- firstListItemPosition] = reps ;
        }
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            callingActivity.saveReps(repsArray);
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    @Override
    public void onDestroyView() {
        restCountDownTimer.cancel();
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(getString(R.string.current_rest), intRestRound);
        super.onSaveInstanceState(savedInstanceState);
    }

}
