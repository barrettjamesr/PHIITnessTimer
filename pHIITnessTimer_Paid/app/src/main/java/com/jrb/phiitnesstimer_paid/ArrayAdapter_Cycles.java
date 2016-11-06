package com.jrb.phiitnesstimer_paid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by James on 20/04/2015.
 */
public class ArrayAdapter_Cycles extends ArrayAdapter{

    private final Context context;
    private final ArrayList<Cycle> values;

    public ArrayAdapter_Cycles(Context context, int layoutResourceId,  ArrayList<Cycle> values) {
        super(context, layoutResourceId, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_rounds_layout, parent, false);
        TextView textViewLeft = (TextView) rowView.findViewById(R.id.LeftColumn);
        TextView textViewRight = (TextView) rowView.findViewById(R.id.RightColumn);

        textViewLeft.setText(values.get(position).getRoundName() );
        textViewRight.setText(String.valueOf(values.get(position).getRepCount()));

        return rowView;
    }
}
