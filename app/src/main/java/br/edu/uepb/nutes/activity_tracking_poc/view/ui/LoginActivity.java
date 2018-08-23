package br.edu.uepb.nutes.activity_tracking_poc.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import br.edu.uepb.nutes.activity_tracking_poc.R;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.ocariot.UserOcariotNetRepository;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.login_progress)
    ProgressBar mProgressBar;

    @BindView(R.id.email)
    EditText mEmailEditText;

    @BindView(R.id.password)
    EditText mPasswordEditText;

    @BindView(R.id.sign_in_button)
    Button mSignInButton;

    private UserOcariotNetRepository userRepository;
    private AppPreferencesHelper appPref;
    private Disposable userDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        userRepository = UserOcariotNetRepository.getInstance(this);
        appPref = AppPreferencesHelper.getInstance(this);

        mSignInButton.setOnClickListener(e -> login());
        mPasswordEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) login();

            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (appPref.getUserAccessOcariot() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userDisposable != null) userDisposable.dispose();
    }

    private void login() {
        if (!validateForm()) return;

        // TODO - REMOVER!!! apenas para teste
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();

        String username = String.valueOf(mEmailEditText.getText());
        String password = String.valueOf(mPasswordEditText.getText());

        userDisposable = userRepository.auth(username, password)
                .doOnSubscribe((d) -> showProgress(true))
                .subscribe(userAccess -> {
                    showProgress(false);
                    Log.d(TAG, "userAccess" + userAccess.toString());

                    appPref.addUserAccessOcariot(userAccess);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }, error -> {
                    Log.d(TAG, error.getMessage());
                    Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
                    showProgress(false);
                });
    }

    /**
     * Validade form
     *
     * @return boolean
     */
    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailEditText.getText().toString();

        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailEditText.setError(getString(R.string.error_invalid_email));
            valid = false;
        } else {
            mEmailEditText.setError(null);
        }

        return valid;
    }

    /**
     * Shows/hide the progress bar.
     */
    private void showProgress(final boolean show) {
        runOnUiThread(() -> mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE));
    }
}

