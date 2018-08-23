package br.edu.uepb.nutes.activity_tracking_poc.view.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import br.edu.uepb.nutes.activity_tracking_poc.R;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.ocariot.UserOcariotNetRepository;
import br.edu.uepb.nutes.activity_tracking_poc.view.ui.preference.SettingsActivity;
import br.edu.uepb.nutes.activity_tracking_poc.view.ui.preference.SettingsFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();


    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.user_details)
    TextView mUserDetailsTextView;

    private UserOcariotNetRepository userRepository;
    private Disposable userDisposable, preferencesDisposable;
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
                    if (authState != null)
                        mUserDetailsTextView.setText(authState.jsonSerializeString());
                }, error -> {
                    Log.w(LOG_TAG, "ERROR - " + error.getMessage());
                    mUserDetailsTextView.setText(error.getMessage());
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userDisposable != null) userDisposable.dispose();
//        if (preferencesDisposable != null) preferencesDisposable.dispose();
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
}
