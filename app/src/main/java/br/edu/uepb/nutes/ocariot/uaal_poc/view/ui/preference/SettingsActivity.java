package br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.preference;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import br.edu.uepb.nutes.ocariot.uaal_poc.R;

/**
 * SettingsActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class SettingsActivity extends BaseSettingsActivity implements SettingsFragment.OnClickSettingsListener {
    public static int REQUEST_CODE_UAAL_STATUS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        replaceFragment(SettingsFragment.newInstance());
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
        if (preference.getKey() == getString(R.string.key_fitibit)) {
            finish();
        } else if (preference.getKey().equals(getString(R.string.key_uaal))) {
            startActivityForResult(new Intent(this, UaalStatusActivity.class),
                    REQUEST_CODE_UAAL_STATUS);
        }
    }

    private void replaceFragment(SettingsFragment fragment) {
        if (fragment != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .commit();
        }
    }
}