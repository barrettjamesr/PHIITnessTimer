package com.jrb.phiitnesstimer_paid;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;


public class SavedTimers extends Fragment implements OnClickListener {
    protected Button btnStart;
    protected View rootView;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.activity_saved_timers, container, false);
        context = rootView.getContext();

        // Connect interface elements to properties
        //btnStart = (Button) rootView.findViewById(R.id.btnStart);

        // Setup ClickListeners
        //btnStart.setOnClickListener(this);

        return rootView;
    }

    public void onClick(View v){
        Intent i = null;
        switch(v.getId()){
            case R.id.btnStart:
                i = new Intent(getActivity(), SetupRFT.class);

        }
        if (i != null) // Add this
            SavedTimers.this.startActivity(i);
    }

}
