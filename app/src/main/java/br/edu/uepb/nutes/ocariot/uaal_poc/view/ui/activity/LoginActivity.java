package br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import br.edu.uepb.nutes.ocariot.uaal_poc.R;
import br.edu.uepb.nutes.ocariot.uaal_poc.data.model.UserAccess;
import br.edu.uepb.nutes.ocariot.uaal_poc.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.uaal_poc.data.repository.remote.ocariot.OcariotNetRepository;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

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

    private OcariotNetRepository userRepository;
    private AppPreferencesHelper appPref;
    private Disposable disposable;
    private Animation mAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        userRepository = OcariotNetRepository.getInstance(this);
        appPref = AppPreferencesHelper.getInstance(this);

        mAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        mPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) login();

                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        appPref.getUserAccessOcariot()
                .subscribe(new SingleObserver<UserAccess>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(UserAccess userAccess) {
                        if (userAccess != null) openMainActivity();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) disposable.dispose();
    }

    private void login() {
        if (!validateForm()) return;

        String username = String.valueOf(mUsernameEditText.getText());
        String password = String.valueOf(mPasswordEditText.getText());

        userRepository.auth(username, password)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showProgress(true);
                    }
                })
                .subscribe(new SingleObserver<UserAccess>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(UserAccess userAccess) {
                        // save user logged
                        appPref.addUserAccessOcariot(userAccess).subscribe();
                        openMainActivity();
                    }

                    @Override
                    public void onError(Throwable e) {
                        showMessageErrorAuth(true);
                        showProgress(false);
                    }
                });

        showProgress(false);
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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show) showMessageErrorAuth(false);
                mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void showMessageErrorAuth(boolean isVisible) {
        if (isVisible) {
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

