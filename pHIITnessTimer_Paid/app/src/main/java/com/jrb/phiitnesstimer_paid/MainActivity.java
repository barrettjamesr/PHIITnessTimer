package com.jrb.phiitnesstimer_paid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity{
    SharedPreferences PHIIT_preferences;
    public static String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSavedPreferences();
        try {
            version = this.getApplicationContext().getPackageManager().getPackageInfo(this.getApplicationContext().getPackageName(), PackageManager.GET_META_DATA).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(version,
                    "Failed to load meta-data, NameNotFound: " + e.getMessage());
        }

        // Set up tabs
        FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("tabNew").setIndicator(getTabIndicator(mTabHost.getContext(), R.string.nav_new)),NewTimer.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tabSaved").setIndicator(getTabIndicator(mTabHost.getContext(), R.string.nav_saved)),SavedTimers.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tabArchive").setIndicator(getTabIndicator(mTabHost.getContext(), R.string.nav_archive)),Archive.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tabSettings").setIndicator(getTabIndicator(mTabHost.getContext(), R.string.nav_settings)),Settings.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tabHelp").setIndicator(getTabIndicator(mTabHost.getContext(), R.string.nav_help)),Help.class, null);

        mTabHost.bringToFront();

        // go to archive if called
        Intent i = getIntent();
        if(i.getBooleanExtra(getString(R.string.bundle_goto_archive), false)) {
            mTabHost.setCurrentTabByTag("tabArchive");
        }

    }

    //Just Text Labels
    private View getTabIndicator(Context context, int title) {
//        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
//        ImageView iv = (ImageView) view.findViewById(R.id.imageView);
//        iv.setImageResource(icon);
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.textView);
            tv.setBackgroundColor(getResources().getColor(R.color.transparent));
            tv.setPadding(0, 0, 0, 0);
            tv.setTextColor(getResources().getColor(R.color.tab_text));
//            tv.setTypeface(null, Typeface.BOLD);
            tv.setText(title);
        return view;
    }

    private void setSavedPreferences() {
        PHIIT_preferences = getSharedPreferences(getString(R.string.PHIIT_Prefs), 0);
        if (PHIIT_preferences.getInt(getString(R.string.PrefVersion), 1) < Constants.prefVersion) {
            SharedPreferences.Editor editor = PHIIT_preferences.edit();
            editor.putInt(getString(R.string.PrefVersion), Constants.prefVersion); // 10 secs
            editor.putBoolean(getString(R.string.CheckStartingCD), true);
            editor.putInt(getString(R.string.DefStartingCD), 5); // 10 secs
            editor.putBoolean(getString(R.string.CheckTimeCap), false);
            editor.putInt(getString(R.string.DefTimeCap), 20 * 60); //20 mins
            editor.putBoolean(getString(R.string.CheckRFTRounds), true);
            editor.putInt(getString(R.string.DefRFTRounds), 4);
            editor.putBoolean(getString(R.string.CheckRestRounds), false);
            editor.putInt(getString(R.string.DefRestRounds), 2 * 60); //2 mins
            editor.putBoolean(getString(R.string.CheckTimeWarning), false);
            editor.putInt(getString(R.string.DefTimeWarning), 5 * 60); //5 mins
            editor.putBoolean(getString(R.string.CheckCountUD), true);
            editor.putInt(getString(R.string.DefCountUD), 0);
            editor.putBoolean(getString(R.string.CheckTimeCapCycles), true);
            editor.putInt(getString(R.string.DefTimeCapCycles), 5 * 60); //5 mins
            editor.putBoolean(getString(R.string.CheckNumberCycles), true);
            editor.putInt(getString(R.string.DefNumberCycles), 3);
            editor.putBoolean(getString(R.string.CheckRestCycles), false);
            editor.putInt(getString(R.string.DefRestCycles), 2 * 60); //2 mins
            editor.putBoolean(getString(R.string.CheckRestRounds), true);
            editor.putInt(getString(R.string.DefResetRounds), 0);
            editor.putBoolean(getString(R.string.CheckTabataTimeCap), true);
            editor.putInt(getString(R.string.DefTabataTimeCap), 20); //20 secs
            editor.putBoolean(getString(R.string.CheckTabataRest), false);
            editor.putInt(getString(R.string.DefTabataRest), 10); //10 secs
            editor.putBoolean(getString(R.string.CheckTabataRounds), true);
            editor.putInt(getString(R.string.DefTabataRounds), 7);  //8 rounds
            editor.putBoolean(getString(R.string.CheckTabataExercises), true);
            editor.putInt(getString(R.string.DefTabataExercises), 3);  //4 exercises
            editor.putBoolean(getString(R.string.CheckFGBTimeCap), true);
            editor.putInt(getString(R.string.DefFGBTimeCap), 60); //1 min
            editor.putBoolean(getString(R.string.CheckFGBRest), false);
            editor.putInt(getString(R.string.DefFGBRest), 60); //1 min
            editor.putBoolean(getString(R.string.CheckFGBRounds), true);
            editor.putInt(getString(R.string.DefFGBRounds), 2); //3 rounds
            editor.putBoolean(getString(R.string.CheckFGBExercises), true);
            editor.putInt(getString(R.string.DefFGBExercises), 4); //5 exercises
            editor.putBoolean(getString(R.string.CheckEMOMTime), true);
            editor.putInt(getString(R.string.DefEMOMTime), 60); //1 min
            editor.putBoolean(getString(R.string.CheckEMOMCycles), true);
            editor.putInt(getString(R.string.DefEMOMCycles), 9);
            editor.putBoolean(getString(R.string.CheckSoundVolume), true);
            editor.putInt(getString(R.string.SoundVolume), Constants.buzzer_volume);
            editor.apply();
        }
    }

    public static String getVersion(){
        return version;

    }
}


