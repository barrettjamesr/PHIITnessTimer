package com.jrb.phiitnesstimer_paid;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class NewTimer extends Fragment implements OnClickListener {
    protected Button btnRFT;
    protected Button btnAMRAP;
    protected Button btnAMRAPrepeats;
    protected Button btnTabata;
    protected Button btnFGB;
    protected Button btnEMOM;
    protected View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.activity_new_timer, container, false);

        // Connect interface elements to properties
        btnRFT = (Button) rootView.findViewById(R.id.imageButton_RFT);
        btnAMRAP = (Button) rootView.findViewById(R.id.imageButton_AMRAP);
        btnAMRAPrepeats = (Button) rootView.findViewById(R.id.imageButton_AMRAPrepeats);
        btnTabata = (Button) rootView.findViewById(R.id.imageButton_Tabata);
        btnFGB = (Button) rootView.findViewById(R.id.imageButton_FGB);
        btnEMOM = (Button) rootView.findViewById(R.id.imageButton_EMOM);

        // Setup ClickListeners
        btnRFT.setOnClickListener(this);
        btnAMRAP.setOnClickListener(this);
        btnAMRAPrepeats.setOnClickListener(this);
        btnTabata.setOnClickListener(this);
        btnFGB.setOnClickListener(this);
        btnEMOM.setOnClickListener(this);

        return rootView;
    }

    public void onClick(View v) {
        Intent i = null;
        switch(v.getId()){
            case R.id.imageButton_RFT:
                i = new Intent(this.getActivity(), SetupRFT.class);
                break;
            case R.id.imageButton_AMRAP:
                i = new Intent(this.getActivity(), SetupAMRAP.class);
                break;
            case R.id.imageButton_AMRAPrepeats:
                i = new Intent(this.getActivity(), SetupAMRAPrepeats.class);
                break;
            case R.id.imageButton_Tabata:
                i = new Intent(this.getActivity(), SetupTabata.class);
                break;
            case R.id.imageButton_FGB:
                i = new Intent(this.getActivity(), SetupFGB.class);
                break;
            case R.id.imageButton_EMOM:
                i = new Intent(this.getActivity(), SetupEMOM.class);
                break;
        }
        if (i != null)
            this.getActivity().startActivity(i);
    }

}

