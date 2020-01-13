package br.edu.uepb.nutes.ocariot.view.ui.preference;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;

/**
 * SettingsActivity implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class SettingsActivity extends BaseSettingsActivity implements
        SettingsFragment.OnClickSettingsListener {
    private AppPreferencesHelper appPref;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appPref = AppPreferencesHelper.getInstance();
        initToolBar();

        if (appPref.getLastSelectedChild() == null) {
            finish();
            return;
        }
        replaceFragment(SettingsFragment.newInstance());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (actionBar != null) {
            actionBar.setSubtitle(appPref.getLastSelectedChild().getUsername());
        }
    }

    private void initToolBar() {
        actionBar = super.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrefClick(String key) {
        if (key.equals(getString(R.string.key_fitibit))) {
            finish();
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
