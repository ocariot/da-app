package br.edu.uepb.nutes.ocariot.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;

import java.util.Objects;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import br.edu.uepb.nutes.ocariot.BuildConfig;
import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.User;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot.OcariotNetRepository;
import br.edu.uepb.nutes.ocariot.utils.AlertMessage;
import br.edu.uepb.nutes.ocariot.utils.FirebaseLogEvent;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.HttpException;
import timber.log.Timber;

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
    CircularProgressButton mSignInButton;

    @BindView(R.id.image_back)
    KenBurnsView mImageBack;

    @BindView(R.id.logo)
    ImageView mImageLogo;

    @BindView(R.id.region_radioGroup)
    RadioGroup regionRadioGroup;

    @BindView(R.id.brazil_radioButton)
    RadioButton brazilRadioButton;

    @BindView(R.id.europe_radioButton)
    RadioButton europeRadioButton;

    @BindView(R.id.locale_hint_text)
    TextView localeText;

    private OcariotNetRepository ocariotRepository;
    private AppPreferencesHelper appPref;
    private CompositeDisposable mDisposable;
    private AlertMessage alertMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        appPref = AppPreferencesHelper.getInstance();
        mDisposable = new CompositeDisposable();
        alertMessage = new AlertMessage(this);

        // Must be called before booting Ocariot Repository!!!
        initComponents();
    }

    private void initComponents() {
        mSignInButton.setOnClickListener(v -> login());

        mPasswordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) login();
            return false;
        });

        AccelerateDecelerateInterpolator accelerateDecelerate = new AccelerateDecelerateInterpolator();
        RandomTransitionGenerator generator = new RandomTransitionGenerator(30000, accelerateDecelerate);
        mImageBack.setTransitionGenerator(generator); // set new transition on kenburns view

        regionRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            slideUp(localeText);
            appPref.changedOcariotUrl(true);
            if (checkedId == R.id.brazil_radioButton) {
                localeText.setText(R.string.brazilian_pilot);
                europeRadioButton.setScaleX(0.85f);
                europeRadioButton.setScaleY(0.85f);
                brazilRadioButton.setScaleX(1f);
                brazilRadioButton.setScaleY(1f);
                appPref.addOcariotURL(BuildConfig.OCARIOT_BR_BASE_URL);
            } else if (checkedId == R.id.europe_radioButton) {
                localeText.setText(R.string.european_pilot);
                brazilRadioButton.setScaleX(0.85f);
                brazilRadioButton.setScaleY(0.85f);
                europeRadioButton.setScaleX(1f);
                europeRadioButton.setScaleY(1f);
                appPref.addOcariotURL(BuildConfig.OCARIOT_EU_BASE_URL);
            }
            ocariotRepository = OcariotNetRepository.getInstance();
        });

        if (appPref.getOcariotURL() == null) appPref.addOcariotURL(BuildConfig.OCARIOT_BR_BASE_URL);
        if (appPref.getOcariotURL().equals(BuildConfig.OCARIOT_EU_BASE_URL)) {
            europeRadioButton.setChecked(true);
        } else {
            brazilRadioButton.setChecked(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        UserAccess userAccess = appPref.getUserAccessOcariot();
        if (userAccess != null) {
            openMainActivity();
        }
    }

    private void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                30,
                0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
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
                    if (userAccess.getSubjectType().equalsIgnoreCase(User.Type.CHILD) ||
                            userAccess.getSubjectType().equalsIgnoreCase(User.Type.ADMIN) ||
                            userAccess.getSubjectType().equalsIgnoreCase(User.Type.APPLICATION)) {
                        alertMessage.show(R.string.title_access_blocked,
                                R.string.alert_access_blocked,
                                R.color.colorWarning,
                                R.drawable.ic_sad_dark
                        );
                        return;
                    }
                    appPref.addUserAccessOcariot(userAccess); // save user logged
                    getFitBitAppData();
                    FirebaseLogEvent.login(userAccess.getSubjectType());
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
     * Get data in server.
     */
    private void getFitBitAppData() {
        mDisposable.add(ocariotRepository
                .getFitBitAppData()
                .doOnSubscribe(disposable -> showProgress(true))
                .doAfterTerminate(() -> showProgress(false))
                .doAfterTerminate(this::openMainActivity)
                .subscribe(
                        fitBitAppData -> appPref.addFitbitAppData(fitBitAppData),
                        Timber::e
                )
        );
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
        if (show) mSignInButton.startAnimation();
        else mSignInButton.revertAnimation();
    }
}
