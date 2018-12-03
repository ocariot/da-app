package br.edu.uepb.nutes.ocariot.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.User;
import br.edu.uepb.nutes.ocariot.data.model.UserAccess;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot.OcariotNetRepository;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * LoginActivity implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
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

    @BindView(R.id.message_error)
    TextView mMessageError;

    private OcariotNetRepository userRepository;
    private AppPreferencesHelper appPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        userRepository = OcariotNetRepository.getInstance(this);
        appPref = AppPreferencesHelper.getInstance(this);

        mSignInButton.setOnClickListener(v -> login());

        mPasswordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) login();

            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        UserAccess userAccess = appPref.getUserAccessOcariot();
        if (userAccess != null) openMainActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void login() {
        if (!validateForm()) return;

        String username = String.valueOf(mUsernameEditText.getText());
        String password = String.valueOf(mPasswordEditText.getText());


        userRepository.auth(username, password)
                .doOnSubscribe(disposable -> showProgress(true))
                .subscribe(new SingleObserver<UserAccess>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(UserAccess userAccess) {
                        // save user logged
                        if (appPref.addUserAccessOcariot(userAccess))
                            openMainActivity();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException) {
                            HttpException httpEx = ((HttpException) e);
                            if (httpEx.code() == 401) {
                                showMessageInvalidAuth(getString(R.string.error_login_invalid));
                                showProgress(false);
                                return;
                            }
                        }
                        showMessageInvalidAuth(getString(R.string.error_500));
                        showProgress(false);
                    }
                });
    }

    /**
     * Open main activity.
     */
    private void openMainActivity() {
        userRepository.getById(appPref.getUserAccessOcariot().getSubject())
                .subscribe(new SingleObserver<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(User user) {
                        Log.w(LOG_TAG, user.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w(LOG_TAG, e.toString());
                    }
                });

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

        if (String.valueOf(mUsernameEditText.getText()).isEmpty()) {
            mUsernameEditText.setError(getString(R.string.required_username));
            valid = false;
        } else {
            mUsernameEditText.setError(null);
        }

        if (String.valueOf(mPasswordEditText.getText()).isEmpty()) {
            mPasswordEditText.setError(getString(R.string.required_password));
            valid = false;
        } else {
            mPasswordEditText.setError(null);
        }

        return valid;
    }

    /**
     * Shows/hide the progress bar.
     */
    private void showProgress(final boolean show) {
        runOnUiThread(() -> {
            if (show) showMessageInvalidAuth(null);
            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        });
    }

    /**
     * Displays or removes invalid login message:
     * -  messaga to display message;
     * - False to remove message.
     * Animation fadein and fadeout are applied.
     *
     * @param message Message to display.
     */
    private void showMessageInvalidAuth(String message) {
        Animation mAnimation;
        if (message != null) {
            mMessageError.setText(message);
            mAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            mBoxMessageError.startAnimation(mAnimation);
            mBoxMessageError.setVisibility(View.VISIBLE);
            return;
        }
        mAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        mBoxMessageError.startAnimation(mAnimation);
        mBoxMessageError.setVisibility(View.GONE);
    }
}

