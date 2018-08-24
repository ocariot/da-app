package br.edu.uepb.nutes.activity_tracking_poc.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import br.edu.uepb.nutes.activity_tracking_poc.R;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.ocariot.UserOcariotNetRepository;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

/**
 * LoginActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class LoginActivity extends AppCompatActivity {
    private final String LOG_TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.login_progress)
    ProgressBar mProgressBar;

    @BindView(R.id.username)
    EditText mUsernameEditText;

    @BindView(R.id.password)
    EditText mPasswordEditText;

    @BindView(R.id.sign_in_button)
    Button mSignInButton;

    @BindView(R.id.box_message_error)
    LinearLayout mBoxMessageError;

    private UserOcariotNetRepository userRepository;
    private AppPreferencesHelper appPref;
    private Disposable disposable;

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

        disposable = appPref.getUserAccessOcariot()
                .subscribe(userAccess -> {
                    if (userAccess != null) openMainActivity();
                }, error -> {});
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) disposable.dispose();
    }

    private void login() {
        if (!validateForm()) return;

        // TODO - REMOVER!!! apenas para teste
//        startActivity(new Intent(getApplicationContext(), MainActivity.class));
//        finish();

        String username = String.valueOf(mUsernameEditText.getText());
        String password = String.valueOf(mPasswordEditText.getText());

        disposable = userRepository.auth(username, password)
                .doOnSubscribe((d) -> showProgress(true))
                .subscribe(userAccess -> {
                    showProgress(false);

                    // save user logged
                    appPref.addUserAccessOcariot(userAccess).subscribe();

                    openMainActivity();
                }, error -> {
                    mBoxMessageError.setVisibility(View.VISIBLE);
                    showProgress(false);
                });
    }

    /**
     * Open main activity.
     */
    private void openMainActivity() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    /**
     * Validade form
     *
     * @return boolean
     */
    private boolean validateForm() {
        boolean valid = true;

        return valid;
    }

    /**
     * Shows/hide the progress bar.
     */
    private void showProgress(final boolean show) {
        runOnUiThread(() -> {
            if (show) mBoxMessageError.setVisibility(View.GONE);
            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        });
    }
}

