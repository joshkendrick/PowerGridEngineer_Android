package com.thirtyonetensoftware.android.powergridengineer.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.thirtyonetensoftware.android.powergridengineer.R;
import com.thirtyonetensoftware.android.powergridengineer.database.DBHelper;
import com.thirtyonetensoftware.android.powergridengineer.model.City;
import com.thirtyonetensoftware.android.powergridengineer.model.Path;
import com.thirtyonetensoftware.android.powergridengineer.util.DijkstraAlgorithm;
import com.thirtyonetensoftware.android.powergridengineer.util.Graph;
import com.thirtyonetensoftware.android.powergridengineer.widget.PGESpinner;
import com.thirtyonetensoftware.android.powergridengineer.widget.RecentCalculationArrayAdapter;

/**
 * MainFragment
 * <p/>
 * Power Grid Engineer
 * 31Ten Software
 * <p/>
 * Author: Josh Kendrick
 */
public class MainFragment extends Fragment {

    private static final String ROUTES_LISTVIEW_KEY = "routes_listview_bundle_key";

    private PGESpinner fromSpinner;

    private PGESpinner toSpinner;

    private ArrayAdapter<City> spinnerAdapter;

    private TextView costTextView;

    private RecentCalculationArrayAdapter listViewAdapter;

    private ArrayList<DijkstraAlgorithm.Result> cachedResults;

    private AlertDialog.Builder infoBuilder;

    private OnPreferencesSelectedListener callback;

    private DBHelper dbHelper;

    private String stepValue;

    private String stepText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        costTextView = (TextView) rootView.findViewById(R.id.cost_textview);

        spinnerAdapter = new ArrayAdapter<>(getActivity(),
                                            android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromSpinner = (PGESpinner) rootView.findViewById(R.id.source_dropdown);
        fromSpinner.setAdapter(spinnerAdapter);
        toSpinner = (PGESpinner) rootView.findViewById(R.id.destination_dropdown);
        toSpinner.setAdapter(spinnerAdapter);
        toSpinner.setOnItemSelectedListener(new PGEOnItemSelectedListener());

        ListView listView = (ListView) rootView.findViewById(R.id.calculations_listview);
        listViewAdapter = new RecentCalculationArrayAdapter(getActivity(), R.layout.route_row);
        if ( savedInstanceState != null ) {
            cachedResults = savedInstanceState.getParcelableArrayList(ROUTES_LISTVIEW_KEY);
        }
        listView.setAdapter(listViewAdapter);

        dbHelper = new DBHelper(getActivity());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);

        if ( cachedResults != null ) {
            listViewAdapter.addAll(cachedResults);
        }

        spinnerAdapter.clear();
        try {
            dbHelper.openReadableDatabase();
            spinnerAdapter.addAll(dbHelper.getCities());
        }
        finally {
            dbHelper.close();
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        stepValue = sharedPref.getString(getString(R.string.step_key), getString(R.string.default_step_value));
        stepText = getStepText(Integer.valueOf(stepValue));

        Graph.buildGraph(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();

        // must be called in onStart; onResume is too late or something
        fromSpinner.setSelection(0, false);
        toSpinner.setSelection(0, false);
    }

    @Override
    public void onPause() {
        super.onPause();

        cachedResults = new ArrayList<>();
        for ( int i = 0; i < listViewAdapter.getCount(); i++ ) {
            cachedResults.add(listViewAdapter.getItem(i));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(ROUTES_LISTVIEW_KEY, cachedResults);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (OnPreferencesSelectedListener) activity;
        }
        catch ( ClassCastException e ) {
            throw new ClassCastException(activity.toString() + " must implement " +
                                             OnPreferencesSelectedListener.class.getSimpleName());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if ( id == R.id.action_settings ) {
            callback.onPreferencesSelected();
        }
        else if ( id == R.id.action_clear ) {
            fromSpinner.setSelection(0, false);
            toSpinner.setSelection(0, false);
            costTextView.setText("");
            listViewAdapter.clear();
        }
        else if ( id == R.id.action_info ) {
            showInfoPopup();
        }

        return super.onOptionsItemSelected(item);
    }

    private String getStepText(int stepValue) {
        switch ( stepValue ) {
            case 10:
                return "Step 1";
            case 15:
                return "Step 2";
            case 20:
                return "Step 3";
            default:
                return null;
        }
    }

    private void showInfoPopup() {
        if ( infoBuilder == null ) {
            infoBuilder = new AlertDialog.Builder(getActivity());
            infoBuilder.setTitle(getActivity().getString(R.string.info_title));
            infoBuilder.setMessage(getActivity().getString(R.string.info_message));
            infoBuilder.setCancelable(true);
            infoBuilder.setPositiveButton(getActivity().getString(R.string.ok), null);
        }

        infoBuilder.show();
    }

    public interface OnPreferencesSelectedListener {
        public void onPreferencesSelected();
    }

    private class PGEOnItemSelectedListener implements OnItemSelectedListener {

        private boolean firstSelection = true;

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if ( firstSelection ) {
                firstSelection = false;
                return;
            }

            City source = (City) fromSpinner.getSelectedItem();
            City destination = (City) toSpinner.getSelectedItem();

            if ( source == null || destination == null || source.equals(destination) ) {
                costTextView.setText("");
            }
            else {
                HashMap<City, Path> route = DijkstraAlgorithm.determineShortestRoute(source,
                                                                                     destination);

                DijkstraAlgorithm.Result result = DijkstraAlgorithm.processRoute(route, destination,
                                                                                 Integer.valueOf(stepValue));
                costTextView.setText(result.getCost() + " + " + stepText + " (" + stepValue + ") " +
                                         "= " + (result.getCost() + result.getStepCost()));

                listViewAdapter.insert(result, 0);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}
