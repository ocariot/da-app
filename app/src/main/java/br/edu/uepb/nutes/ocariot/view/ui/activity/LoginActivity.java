package br.edu.uepb.nutes.ocariot.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;

import java.util.Objects;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.FitBitAppData;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.User;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot.OcariotNetRepository;
import br.edu.uepb.nutes.ocariot.utils.AlertMessage;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.HttpException;

import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

/**
 * LoginActivity implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.login_progress)
    ProgressBar mProgressBar;

    @BindView(R.id.username)
    AppCompatEditText mUsernameEditText;

    @BindView(R.id.password)
    AppCompatEditText mPasswordEditText;

    @BindView(R.id.sign_in_button)
    AppCompatButton mSignInButton;

    @BindView(R.id.image_back)
    KenBurnsView mImageBack;

    @BindView(R.id.logo)
    ImageView mImageLogo;

    private OcariotNetRepository ocariotRepository;
    private AppPreferencesHelper appPref;
    private CompositeDisposable mDisposable;
    private AlertMessage alertMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mDisposable = new CompositeDisposable();

        ocariotRepository = OcariotNetRepository.getInstance();
        appPref = AppPreferencesHelper.getInstance();
        alertMessage = new AlertMessage(this);

        mSignInButton.setOnClickListener(v -> login());
        mPasswordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) login();
            return false;
        });

        AccelerateDecelerateInterpolator ACCELERATE_DECELERATE = new AccelerateDecelerateInterpolator();
        RandomTransitionGenerator generator = new RandomTransitionGenerator(30000, ACCELERATE_DECELERATE);
        mImageBack.setTransitionGenerator(generator); // set new transition on kenburns view
    }

    @Override
    protected void onResume() {
        super.onResume();

        UserAccess userAccess = appPref.getUserAccessOcariot();
        if (userAccess != null) {
            openMainActivity();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }

    /**
     * Login in server.
     */
    private void login() {
        // close keyboard
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus())
                    .getWindowToken(), HIDE_NOT_ALWAYS);
        }

        if (!validateForm()) return;

        String username = String.valueOf(mUsernameEditText.getText());
        String password = String.valueOf(mPasswordEditText.getText());

        mDisposable.add(ocariotRepository
                .auth(username, password)
                .doOnSubscribe(disposable -> showProgress(true))
                .doAfterTerminate(() -> showProgress(false))
                .subscribe(userAccess -> {
                    if (userAccess.getSubjectType().equalsIgnoreCase(User.Type.ADMIN)) {
                        alertMessage.show(R.string.title_access_blocked,
                                R.string.alert_access_blocked,
                                R.color.colorWarning,
                                R.drawable.ic_sad_dark
                        );
                        return;
                    }
                    appPref.addUserAccessOcariot(userAccess); // save user logged
                    getResources(userAccess);
                }, error -> {
                    if (error instanceof HttpException) {
                        HttpException httpEx = ((HttpException) error);
                        if (httpEx.code() == 401) {
                            alertMessage.show(
                                    R.string.title_login_failed,
                                    R.string.error_login_invalid,
                                    R.color.colorWarning,
                                    R.drawable.ic_sad_dark
                            );
                            return;
                        }
                    }
                    alertMessage.handleError(error);
                }));
    }

    /**
     * Get user profile in server.
     *
     * @param userAccess {@link UserAccess}
     */
    private void getResources(UserAccess userAccess) {
        if (userAccess.getSubjectType().equalsIgnoreCase(User.Type.CHILD)) {
            mDisposable.add(ocariotRepository.getFitBitAppData()
                    .zipWith(ocariotRepository.getChildById(userAccess.getSubject()), ResponseData::new)
                    .doOnSubscribe(disposable -> showProgress(true))
                    .doAfterTerminate(() -> {
                        showProgress(false);
                        openMainActivity();
                    })
                    .subscribe(
                            responseData -> {
                                appPref.addFitbitAppData(responseData.fitBitAppData);
                                appPref.addLastSelectedChild(responseData.child);
                            }, err -> alertMessage.handleError(err)
                    )
            );
        } else {
            mDisposable.add(ocariotRepository
                    .getFitBitAppData()
                    .doOnSubscribe(disposable -> showProgress(true))
                    .doAfterTerminate(() -> {
                        showProgress(false);
                        openMainActivity();
                    })
                    .subscribe(
                            fitBitAppData -> appPref.addFitbitAppData(fitBitAppData),
                            err -> alertMessage.handleError(err)
                    )
            );
        }
    }

    /**
     * Open main activity.
     */
    private void openMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
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
        runOnUiThread(() -> mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE));
    }

    private class ResponseData {
        FitBitAppData fitBitAppData;
        Child child;

        ResponseData(FitBitAppData fitBitAppData, Child child) {
            this.fitBitAppData = fitBitAppData;
            this.child = child;
        }
    }
}
