package br.edu.uepb.nutes.activity_tracking_poc.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Arrays;

import br.edu.uepb.nutes.activity_tracking_poc.R;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.fitbit.FitBitNetRepository;
import br.edu.uepb.nutes.activity_tracking_poc.view.ui.preference.SettingsActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

/**
 * MainActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private Disposable fitBitDisposable, preferencesDisposable;
    private AppPreferencesHelper mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mPreferences = AppPreferencesHelper.getInstance(this);

        preferencesDisposable = mPreferences.getAuthStateFitBit()
                .subscribe(authState -> {
                    if (authState != null) {
                        Log.w(LOG_TAG, "USER ACCESS - " + authState.jsonSerializeString());
                    }
                }, error -> {
                    Log.w(LOG_TAG, "ERROR - " + error.getMessage());
                });

        replaceFragment(PhysicalActivityListFragment.newInstance());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fitBitDisposable != null) fitBitDisposable.dispose();
        if (preferencesDisposable != null) preferencesDisposable.dispose();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Replace fragment.
     *
     * @param fragment {@link Fragment}
     */
    private void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, fragment).commit();
//            transaction.replace(R.id.content, fragment).addToBackStack(null).commit();
        }
    }
}
