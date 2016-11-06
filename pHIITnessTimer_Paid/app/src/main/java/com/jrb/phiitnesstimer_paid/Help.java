package com.jrb.phiitnesstimer_paid;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class Help extends Fragment {
    protected View rootView;
    protected TextView txtVersion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.activity_help, container, false);
        txtVersion = (TextView) rootView.findViewById(R.id.lblVersion);

        txtVersion.setText(txtVersion.getText() + MainActivity.getVersion() );
        return rootView;
    }

}
