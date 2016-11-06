package com.jrb.phiitnesstimer_paid;

import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by James on 20/04/2015.
 */
public class ArrayAdapter_Reps extends ArrayAdapter{

    private final Context context;
    private ArrayList<String> values;


    public ArrayAdapter_Reps(Context context, int layoutResourceId, ArrayList<String> values) {
        super(context, layoutResourceId, values);
        this.context = context;
        this.values = values;
    }

    public void clear() {
        this.values.clear();
    }

    public void setData(ArrayList<String> data) {
        this.values = data;
    }

    public int getCount() {
        return values.size();
    }

    @Override
    public String getItem(int item) {
        return values.get(item);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_fgb_exercises, parent, false);
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.textCount = (TextView) rowView.findViewById(R.id.exerciseCount);
        viewHolder.textLabel = (TextView) rowView.findViewById(R.id.repsLabel);
        viewHolder.textEntry = (EditText) rowView.findViewById(R.id.repsCompleted);

        viewHolder.textCount.setText(String.valueOf(position));
        viewHolder.textLabel.setText(viewHolder.textLabel.getText() + String.valueOf(position+1) + context.getResources().getString(R.string.text_colon));
        viewHolder.textEntry.setText(values.get(position));

        viewHolder.textEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                values.set(position, s.toString());

            }

        });
        /*
        viewHolder.textEntry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText et = (EditText) v.findViewById(R.id.repsCompleted);
                    values.set(position, et.getText().toString().trim());
                }
            }
        });
*/
        return rowView;
    }

    static class ViewHolder
    {
        protected TextView textCount;
        protected TextView textLabel;
        protected EditText textEntry;
    }
}

