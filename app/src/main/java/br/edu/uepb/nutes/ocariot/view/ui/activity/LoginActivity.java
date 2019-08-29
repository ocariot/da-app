package br.edu.uepb.nutes.ocariot.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.User;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot.OcariotNetRepository;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
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
    AppCompatButton mSignInButton;

    @BindView(R.id.box_message_error)
    LinearLayout mBoxMessageError;

    @BindView(R.id.message_error)
    TextView mMessageError;

    private OcariotNetRepository ocariotRepository;
    private AppPreferencesHelper appPref;
    private CompositeDisposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mDisposable = new CompositeDisposable();
        ocariotRepository = OcariotNetRepository.getInstance(this);
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
        mDisposable.dispose();
    }

    private void login() {
        if (!validateForm()) return;

        String username = String.valueOf(mUsernameEditText.getText());
        String password = String.valueOf(mPasswordEditText.getText());

        mDisposable.add(
                ocariotRepository
                        .auth(username, password)
                        .doOnSubscribe(disposable -> showProgress(true))
                        .doAfterTerminate(() -> showProgress(false))
                        .subscribe(userAccess -> {
                            Log.w("AAA", "FEito login!");
                            // save user logged
                            if (appPref.addUserAccessOcariot(userAccess)) {
                                Log.w("AAA", "addUserAccessOcariot");
                                getUserProfile(userAccess);
                            }
                        }, error -> {
                            Log.w("ERROR-LOG", error.toString());
                            if (error instanceof HttpException) {
                                HttpException httpEx = ((HttpException) error);
                                if (httpEx.code() == 401) {
                                    showMessageInvalidAuth(getString(R.string.error_login_invalid));
                                    return;
                                }
                            }
                            showMessageInvalidAuth(getString(R.string.error_500));
                        })
        );
    }

    private void getUserProfile(UserAccess userAccess) {
        Log.w("AAA", "TYPE: " + userAccess.getSubjectType());
        if (userAccess.getSubjectType().equalsIgnoreCase(User.Type.CHILD)) {
            mDisposable.add(
                    ocariotRepository
                            .getChildById(userAccess.getSubject())
                            .doOnSubscribe(disposable -> showProgress(true))
                            .doAfterTerminate(() -> showProgress(false))
                            .subscribe(child -> {
                                appPref.addChildProfile(child);
                                Log.w("AAA", child.toJson());
                                openMainActivity();
                            }, error -> {
                                if (error instanceof HttpException) {
                                    HttpException httpEx = ((HttpException) error);
                                    if (httpEx.code() == 401) {
                                        showMessageInvalidAuth(getString(R.string.error_401));
                                        return;
                                    } else if (httpEx.code() == 403) {
                                        showMessageInvalidAuth(getString(R.string.error_403));
                                        return;
                                    }
                                    showMessageInvalidAuth(getString(R.string.error_500));
                                }
                            })
            );
        } else if (userAccess.getSubjectType().equalsIgnoreCase(User.Type.FAMILY)) {
            mDisposable.add(
                    ocariotRepository
                            .getFamilyById(userAccess.getSubject())
                            .doOnSubscribe(disposable -> showProgress(true))
                            .doAfterTerminate(() -> showProgress(false))
                            .subscribe(family -> {
                                Log.w("AAA", family.toJson());
                                Intent intent = new Intent(getApplicationContext(), ChildrenManagerActivity.class);
                                intent.putExtra(User.Type.FAMILY, family.toJson());
                                startActivity(intent);
                            }, error -> {
                                if (error instanceof HttpException) {
                                    HttpException httpEx = ((HttpException) error);
                                    if (httpEx.code() == 401) {
                                        showMessageInvalidAuth(getString(R.string.error_401));
                                        return;
                                    } else if (httpEx.code() == 403) {
                                        showMessageInvalidAuth(getString(R.string.error_403));
                                        return;
                                    }
                                    showMessageInvalidAuth(getString(R.string.error_500));
                                }
                            })
            );
        } else if (userAccess.getSubjectType().equalsIgnoreCase(User.Type.EDUCATOR)) {
            mDisposable.add(
                    ocariotRepository
                            .getEducatorById(userAccess.getSubject())
                            .doOnSubscribe(disposable -> showProgress(true))
                            .doAfterTerminate(() -> showProgress(false))
                            .subscribe(educator -> {
                                Log.w("AAA", educator.toJson());
                                Intent intent = new Intent(getApplicationContext(), ChildrenManagerActivity.class);
                                intent.putExtra(User.Type.EDUCATOR, educator.toJson());
                                startActivity(intent);
                            }, error -> {
                                if (error instanceof HttpException) {
                                    HttpException httpEx = ((HttpException) error);
                                    if (httpEx.code() == 401) {
                                        showMessageInvalidAuth(getString(R.string.error_401));
                                        return;
                                    } else if (httpEx.code() == 403) {
                                        showMessageInvalidAuth(getString(R.string.error_403));
                                        return;
                                    }
                                    showMessageInvalidAuth(getString(R.string.error_500));
                                }
                            })
            );
        } else if (userAccess.getSubjectType().equalsIgnoreCase(User.Type.HEALTH_PROFESSIONAL)) {
            mDisposable.add(
                    ocariotRepository
                            .getHealthProfessionalById(userAccess.getSubject())
                            .doOnSubscribe(disposable -> showProgress(true))
                            .doAfterTerminate(() -> showProgress(false))
                            .subscribe(healthProfessional -> {
                                Log.w("AAA", healthProfessional.toJson());
                                Intent intent = new Intent(getApplicationContext(), ChildrenManagerActivity.class);
                                intent.putExtra(User.Type.HEALTH_PROFESSIONAL, healthProfessional.toJson());
                                startActivity(intent);
                            }, error -> {
                                if (error instanceof HttpException) {
                                    HttpException httpEx = ((HttpException) error);
                                    if (httpEx.code() == 401) {
                                        showMessageInvalidAuth(getString(R.string.error_401));
                                        return;
                                    } else if (httpEx.code() == 403) {
                                        showMessageInvalidAuth(getString(R.string.error_403));
                                        return;
                                    }
                                    showMessageInvalidAuth(getString(R.string.error_500));
                                }
                            })
            );
        }
    }

    /**
     * Open main activity.
     */
    private void openMainActivity() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    /**
     * Open children manager activity.
     */
    private void openChildrenManager() {
        startActivity(new Intent(getApplicationContext(), ChildrenManagerActivity.class));
        finish();
    }

    /**
     * Validate form
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
     * - message to display message;
     * - False to remove message.
     * Animation fade in and fade out are applied.
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

