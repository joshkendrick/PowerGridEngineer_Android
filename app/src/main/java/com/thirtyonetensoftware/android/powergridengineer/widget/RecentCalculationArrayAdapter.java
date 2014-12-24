package com.thirtyonetensoftware.android.powergridengineer.widget;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.thirtyonetensoftware.android.powergridengineer.R;
import com.thirtyonetensoftware.android.powergridengineer.util.DijkstraAlgorithm;

/**
 * RecentCalculationArrayAdapter
 * <p/>
 * Power Grid Engineer
 * 31Ten Software
 * <p/>
 * Author: Josh Kendrick
 */
public class RecentCalculationArrayAdapter extends ArrayAdapter<DijkstraAlgorithm.Result> {

    public RecentCalculationArrayAdapter(Activity activity, int layout) {
        super(activity, layout);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CalculationHolder holder;

        if ( row == null ) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            row = inflater.inflate(R.layout.route_row, parent, false);

            holder = new CalculationHolder();
            holder.pathText = (TextView) row.findViewById(R.id.row_path_textview);
            holder.costText = (TextView) row.findViewById(R.id.row_cost_textview);

            row.setTag(holder);
        }
        else {
            holder = (CalculationHolder) row.getTag();
        }

        DijkstraAlgorithm.Result result = getItem(position);
        holder.pathText.setText(result.getPath());
        holder.costText.setText(String.valueOf(result.getCost() + result.getStepCost()));

        return row;
    }

    private static class CalculationHolder {
        TextView pathText;

        TextView costText;
    }
}
