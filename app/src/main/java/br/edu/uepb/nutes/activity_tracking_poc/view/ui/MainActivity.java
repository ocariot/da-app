package br.edu.uepb.nutes.activity_tracking_poc.view.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import br.edu.uepb.nutes.activity_tracking_poc.R;
import br.edu.uepb.nutes.activity_tracking_poc.data.model.UserAccessMode;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.ocariot.UserOcariotNetRepository;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();


    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.user_details)
    TextView mUserDetailsTextView;

    private UserOcariotNetRepository userRepository;
    private Disposable userDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        userRepository = UserOcariotNetRepository.getInstance(this);

        userDisposable = userRepository.getById("156")
                .subscribe(user -> {
                    mUserDetailsTextView.setText("Name: " + user.getName());
                }, error -> {
                    Log.d(TAG, error.getMessage());
                    Toast.makeText(this, "ERROR: " + error.getMessage(), Toast.LENGTH_LONG).show();
                });

        AppPreferencesHelper appPref = AppPreferencesHelper.getInstance(this);
        mUserDetailsTextView.setText(appPref.getUserAccessOcariot().toString());

        Log.d(TAG, "OCARIOT " + appPref.getUserAccessOcariot().toString());
        Log.d(TAG, "OCARIOT TOKEN" + appPref.getToken(UserAccessMode.OCARIOT).toString());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userDisposable != null) userDisposable.dispose();
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
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
