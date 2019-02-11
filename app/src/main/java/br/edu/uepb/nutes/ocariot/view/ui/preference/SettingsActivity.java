package br.edu.uepb.nutes.ocariot.view.ui.preference;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.view.ui.activity.DeleteDataActivity;

/**
 * SettingsActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class SettingsActivity extends BaseSettingsActivity implements
        SettingsFragment.OnClickSettingsListener {

    private long currentMills;
    private int countClicks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        replaceFragment(SettingsFragment.newInstance());

        currentMills = 0;
        countClicks = 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrefClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.key_fitibit))) {
            finish();
        } else if (preference.getKey().equals(getString(R.string.key_version))) {
            //get system current milliseconds
            long time = System.currentTimeMillis();

            // if it is the first time, or if it has been more than 3 seconds
            // since the first tap ( so it is like a new try), we reset everything
            if (currentMills == 0 || (time - currentMills > 3000)) {
                currentMills = time;
                countClicks = 1;
            }
            //it is not the first, and it has been  less than 3 seconds since the first
            else { //  time-startMillis< 3000
                countClicks++;
            }

            if (countClicks == 5) {
                startActivity(new Intent(this, DeleteDataActivity.class));
            }
        }
    }

    private void replaceFragment(PreferenceFragment fragment) {
        if (fragment != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .commit();
        }
    }
}
