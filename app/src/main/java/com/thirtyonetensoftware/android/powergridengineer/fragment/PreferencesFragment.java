package com.thirtyonetensoftware.android.powergridengineer.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import com.thirtyonetensoftware.android.powergridengineer.R;
import com.thirtyonetensoftware.android.powergridengineer.database.DBHelper;
import com.thirtyonetensoftware.android.powergridengineer.model.Country;
import com.thirtyonetensoftware.android.powergridengineer.model.Region;

/**
 * SettingsFragment
 * <p/>
 * Power Grid Engineer
 * 31Ten Software
 * <p/>
 * Author: Josh Kendrick
 */
public class PreferencesFragment extends PreferenceFragment implements SharedPreferences
                                                                           .OnSharedPreferenceChangeListener {

    private DBHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(false);

        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener
            (this);

        // refresh the summaries for the step and country prefs
        ListPreference pref = (ListPreference) findPreference(getString(R.string.step_key));
        pref.setSummary(pref.getEntry());

        pref = (ListPreference) findPreference(getString(R.string.country_key));
        updateRegionCheckboxes(Country.valueOf(Integer.parseInt(pref.getValue())));
        pref.setSummary(pref.getEntry());
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener
            (this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        dbHelper = new DBHelper(activity);
        // The database will ship with all the United States cities active
        // preferences.xml will have US and all US regions as default: TEAL, PURPLE, RED, YELLOW,
        // BROWN, GREEN
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ( key.equals(getString(R.string.country_key)) ) {
            ListPreference countryPref = (ListPreference) findPreference(key);

            Country country = Country.valueOf(Integer.parseInt(countryPref.getValue()));
            updateRegionCheckboxes(country);

            try {
                dbHelper.openWritableDatabase();
                dbHelper.setCountryActive(country);
            }
            finally {
                dbHelper.close();
            }

            countryPref.setSummary(countryPref.getEntry());
        }
        else if ( key.equals(getString(R.string.step_key)) ) {
            ListPreference stepPref = (ListPreference) findPreference(key);
            stepPref.setSummary(stepPref.getEntry());
        }
        else {
            Region region = null;
            if ( key.equals(getString(R.string.region_blue_key)) ) {
                region = Region.BLUE;
            }
            else if ( key.equals(getString(R.string.region_brown_key)) ) {
                region = Region.BROWN;
            }
            else if ( key.equals(getString(R.string.region_green_key)) ) {
                region = Region.GREEN;
            }
            else if ( key.equals(getString(R.string.region_purple_key)) ) {
                region = Region.PURPLE;
            }
            else if ( key.equals(getString(R.string.region_red_key)) ) {
                region = Region.RED;
            }
            else if ( key.equals(getString(R.string.region_teal_key)) ) {
                region = Region.TEAL;
            }
            else if ( key.equals(getString(R.string.region_yellow_key)) ) {
                region = Region.YELLOW;
            }

            CheckBoxPreference regionPref = (CheckBoxPreference) findPreference(key);

            if ( region != null ) {
                try {
                    dbHelper.openWritableDatabase();
                    dbHelper.setRegionActive(region, regionPref.isChecked());
                }
                finally {
                    dbHelper.close();
                }
            }
        }
    }

    private void updateRegionCheckboxes(Country country) {
        CheckBoxPreference bluePref = (CheckBoxPreference) findPreference(getString(R.string
                                                                                        .region_blue_key));
        CheckBoxPreference greenPref = (CheckBoxPreference) findPreference(getString(R.string
                                                                                         .region_green_key));

        if ( country.equals(Country.UNITED_STATES) ) {
            bluePref.setEnabled(false);
            greenPref.setEnabled(true);
        }
        else if ( country.equals(Country.GERMANY) ) {
            bluePref.setEnabled(true);
            greenPref.setEnabled(false);
        }
    }
}
