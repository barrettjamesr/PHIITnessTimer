package com.jrb.phiitnesstimer_paid;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by James on 20/04/2015.
 */
public class ArrayAdapter_Workouts extends ArrayAdapter{

    private final Context context;
    private final ArrayList<Workout> values;
    private View selectedItem;
    private int selectedItemPosition;

    public ArrayAdapter_Workouts(Context context, int layoutResourceId,  ArrayList<Workout> values) {
        super(context, layoutResourceId, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Workout getItem(int position) {
        return values.get(position);
    }

    public void onListItemClick(View v, int position) {

        if (selectedItem != null && selectedItem != v) {
            unhighlightCurrentRow(selectedItem);
        }
        selectedItem = v;
        selectedItemPosition = position;
        highlightCurrentRow(selectedItem);

    }

    public Workout getSelectedItem(){
        return values.get(selectedItemPosition);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_workouts_layout, parent, false);
        TextView textViewHidden = (TextView) rowView.findViewById(R.id.HiddenColumn);
        TextView textViewLeft = (TextView) rowView.findViewById(R.id.LeftColumn);
        TextView textViewMiddle = (TextView) rowView.findViewById(R.id.MiddleColumn);
        TextView textViewRight = (TextView) rowView.findViewById(R.id.RightColumn);

        textViewHidden.setText(String.valueOf(values.get(position).getWorkoutNumber()));
        textViewLeft.setText(values.get(position).getWorkoutName());
        textViewMiddle.setText(values.get(position).getWorkoutType());
        textViewRight.setText(values.get(position).getDateRan().substring(0, Constants.date_length));

        return rowView;
    }

    private void unhighlightCurrentRow(View rowView) {
        rowView.setBackgroundColor(context.getResources().getColor(R.color.list_default_color));
        TextView textViewLeft = (TextView) rowView.findViewById(R.id.LeftColumn);
        TextView textViewMiddle = (TextView) rowView.findViewById(R.id.MiddleColumn);
        TextView textViewRight = (TextView) rowView.findViewById(R.id.RightColumn);

        textViewLeft.setTextColor(context.getResources().getColor(R.color.black));
        textViewMiddle.setTextColor(context.getResources().getColor(R.color.black));
        textViewRight.setTextColor(context.getResources().getColor(R.color.black));
    }

    private void highlightCurrentRow(View rowView) {
        rowView.setBackgroundColor(context.getResources().getColor(R.color.list_selected_color));

        TextView textViewLeft = (TextView) rowView.findViewById(R.id.LeftColumn);
        TextView textViewMiddle = (TextView) rowView.findViewById(R.id.MiddleColumn);
        TextView textViewRight = (TextView) rowView.findViewById(R.id.RightColumn);

        textViewLeft.setTextColor(context.getResources().getColor(R.color.white));
        textViewMiddle.setTextColor(context.getResources().getColor(R.color.white));
        textViewRight.setTextColor(context.getResources().getColor(R.color.white));

    }

}
