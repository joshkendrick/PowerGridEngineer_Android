package com.thirtyonetensoftware.android.powergridengineer.activity;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.crashlytics.android.Crashlytics;
import com.thirtyonetensoftware.android.powergridengineer.BuildConfig;
import com.thirtyonetensoftware.android.powergridengineer.R;
import com.thirtyonetensoftware.android.powergridengineer.fragment.MainFragment;
import com.thirtyonetensoftware.android.powergridengineer.fragment.PreferencesFragment;
import io.fabric.sdk.android.Fabric;

/**
 * MainActivity
 * <p/>
 * Power Grid Engineer
 * 31Ten Software
 * <p/>
 * Author: Josh Kendrick
 */
public class MainActivity extends AppCompatActivity implements MainFragment.OnPreferencesSelectedListener {

    private static final String MAIN_FRAGMENT_KEY = "main_fragment_key";

    private static final String PREFERENCES_FRAGMENT_KEY = "preferences_fragment_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( BuildConfig.REPORT_CRASHES ) {
            Fabric.with(this, new Crashlytics());
        }
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        if ( savedInstanceState == null ) {
            getFragmentManager().beginTransaction()
                                .replace(R.id.container, new MainFragment(), MAIN_FRAGMENT_KEY)
                                .commit();
        }
    }

    @Override
    public void onPreferencesSelected() {
        getFragmentManager().beginTransaction()
                            .replace(R.id.container, new PreferencesFragment(), PREFERENCES_FRAGMENT_KEY)
                            .addToBackStack(null)
                            .commit();
    }
}
