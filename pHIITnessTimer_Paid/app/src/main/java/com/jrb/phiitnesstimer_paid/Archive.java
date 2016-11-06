package com.jrb.phiitnesstimer_paid;

import android.app.Fragment;
import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Archive extends Fragment implements OnClickListener {
    protected View rootView;

    protected Button btnDetails;
    protected ListView listArchivedWorkouts;
    protected ArrayAdapter_Workouts arrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.activity_archive, container, false);
        btnDetails = (Button) rootView.findViewById(R.id.btnDetails);
        listArchivedWorkouts = (ListView) rootView.findViewById(R.id.listArchivedWorkouts);

        //Populate Views
        PHIIT_DataSource dataSource = new PHIIT_DataSource(this.getActivity());

        // Initialise Database if no workouts
        if (dataSource.LastEntry(DatabaseContract.ArchivedWorkouts.TABLE_NAME, DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NUMBER) <1) {
            dataSource.open();
            int dummy_workout_number = 1;
            long[] dummy_new_round = new long[] {0, 123};
            int dummy_rest = 0;
            dataSource.saveRound (dummy_workout_number, dummy_new_round, dummy_rest);

            Workout dummy_workout = new Workout();
            dummy_workout.setWorkoutNumber(dummy_workout_number);
            dummy_workout.setWorkoutName("Sample ");
            dummy_workout.setWorkoutType("RFT");
            dataSource.saveWorkout (dummy_workout);
            dataSource.close();

        }

        ArrayList<Workout> workout_list = dataSource.WorkoutArchive();
        arrayAdapter = new ArrayAdapter_Workouts(this.getActivity(), R.layout.list_workouts_layout, workout_list);
        listArchivedWorkouts.setAdapter(arrayAdapter);
        listArchivedWorkouts.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listArchivedWorkouts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                arrayAdapter.onListItemClick(view, position);
            }
        });

        // Setup ClickListeners, for click, or get focus
        btnDetails.setOnClickListener(this);
        btnDetails.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    v.callOnClick();
            }
        });

        return rootView;
    }

    public void onClick(View v)  {
        Intent i = null;
        switch(v.getId()){
            case R.id.btnDetails:
                if(findString(getResources().getStringArray(R.array.array_display_one), arrayAdapter.getSelectedItem().getWorkoutType()) >= 0 )
                    i = new Intent(this.getActivity(), DisplayTypeOne.class);
                else
                    i = new Intent(this.getActivity(), DisplayTypeTwo.class);
                i.putExtra(getString(R.string.bundle_workout_number), arrayAdapter.getSelectedItem().getWorkoutNumber());
                break;
        }
        if (i != null)
            this.getActivity().startActivity(i);
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public int findString(String[] stringArray, String target){
        int match = -1;
        for (String s : stringArray) {
            int i = s.indexOf(target);
            if (i >= 0) {
                match = i;
            }
        }
        return match;
    }


}
