package com.thirtyonetensoftware.android.powergridengineer.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.thirtyonetensoftware.android.powergridengineer.BuildConfig;
import com.thirtyonetensoftware.android.powergridengineer.R;
import com.thirtyonetensoftware.android.powergridengineer.fragment.MainFragment;
import io.fabric.sdk.android.Fabric;

/**
 * MainActivity
 * <p/>
 * Power Grid Engineer
 * 31Ten Software
 * <p/>
 * Author: Josh Kendrick
 */
public class MainActivity extends AppCompatActivity {

    private static final String MAIN_FRAGMENT_KEY = "main_fragment_key";

    private AlertDialog.Builder infoBuilder;

    private String infoMessage;

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

        String message = getString(R.string.info_message);
        infoMessage = String.format(message, BuildConfig.VERSION_NAME);

        if ( savedInstanceState == null ) {
            getFragmentManager().beginTransaction()
                                .replace(R.id.container, new MainFragment(), MAIN_FRAGMENT_KEY)
                                .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if ( id == R.id.action_settings ) {
            startActivity(new Intent(MainActivity.this, PreferencesActivity.class));
        }
        else if ( id == R.id.action_clear ) {
            MainFragment fragment = (MainFragment) getFragmentManager().findFragmentByTag(MAIN_FRAGMENT_KEY);
            fragment.clear();
        }
        else if ( id == R.id.action_info ) {
            showInfoPopup();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showInfoPopup() {
        if ( infoBuilder == null ) {
            infoBuilder = new AlertDialog.Builder(this);
            infoBuilder.setTitle(getString(R.string.info_title));
            infoBuilder.setMessage(infoMessage);
            infoBuilder.setCancelable(true);
            infoBuilder.setPositiveButton(getString(R.string.ok), null);
        }

        infoBuilder.show();
    }
}
