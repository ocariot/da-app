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

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.user_details)
    TextView mUserDetailsTextView;

    private FitBitNetRepository fitBitRepository;
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
                        fitBitRepository = FitBitNetRepository.getInstance(this, authState);
                        getActivities();
                        mUserDetailsTextView.setText(authState.jsonSerializeString());
                    }
                }, error -> {
                    Log.w(LOG_TAG, "ERROR - " + error.getMessage());
                    mUserDetailsTextView.setText(error.getMessage());
                });

        replaceFragment(PhysicalActivityListFragment.newInstance(this));

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void getActivities() {
        fitBitDisposable = fitBitRepository.listActivities("2018-08-23",
                null, "desc", 0, 10).subscribe(activities -> {
            Log.w(LOG_TAG, "ACTIVITIES - " + Arrays.toString(activities.getActivities().toArray()));
        }, error -> {
            Log.w(LOG_TAG, "ERROR ACTIVITY - " + error.getMessage());
        });
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
            transaction.replace(R.id.date_textview, fragment).commit();
//            transaction.replace(R.id.content, fragment).addToBackStack(null).commit();
        }
    }
}
