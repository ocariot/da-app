package br.edu.uepb.nutes.activity_tracking_poc.view.ui.preference;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

/**
 * SettingsActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class SettingsActivity extends BaseSettingsActivity {
    private static final int RESULT_OCARIOT_LOGOFF = 1;
    private static final int RESULT_FITBIT_LOGIN = 2;
    private static final int RESULT_FITBIT_REVOKE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
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
}