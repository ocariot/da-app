package br.edu.uepb.nutes.ocariot.view.ui.preference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;

import com.tapadoo.alerter.Alerter;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.User;
import br.edu.uepb.nutes.ocariot.data.repository.SyncDataRepository;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot.OcariotNetRepository;
import br.edu.uepb.nutes.ocariot.utils.AlertMessage;
import br.edu.uepb.nutes.ocariot.utils.DialogLoading;
import br.edu.uepb.nutes.ocariot.utils.MessageEvent;
import br.edu.uepb.nutes.ocariot.view.ui.activity.ChildrenManagerActivity;
import br.edu.uepb.nutes.ocariot.view.ui.activity.LoginActivity;
import br.edu.uepb.nutes.ocariot.view.ui.activity.MainActivity;
import io.reactivex.disposables.CompositeDisposable;

/**
 * SettingsFragment implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    public final String LOG_TAG = "SettingsFragment";

    private SwitchPreference switchPrefFitBit;
    private LoginFitBit loginFitBit;
    private OnClickSettingsListener mListener;
    private Context mContext;
    private CompositeDisposable mDisposable;
    private AppPreferencesHelper appPref;
    private DialogLoading mDialogSync;
    private AlertMessage mAlertMessage;
    private Child child;

    public SettingsFragment() {
        // Empty constructor required!
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        appPref = AppPreferencesHelper.getInstance();
        loginFitBit = new LoginFitBit(mContext);
        mDisposable = new CompositeDisposable();
        mAlertMessage = new AlertMessage(getActivity());

        if (appPref.getUserAccessOcariot().getSubjectType().equals(User.Type.CHILD)) {
            addPreferencesFromResource(R.xml.preferences_child);
        } else {
            addPreferencesFromResource(R.xml.preferences);
        }

        // FitBit
        switchPrefFitBit = (SwitchPreference) findPreference(getString(R.string.key_fitibit));
        switchPrefFitBit.setOnPreferenceClickListener(this);

        // Sign Out
        findPreference(getString(R.string.key_signout)).setOnPreferenceClickListener(this);

        // sync
        findPreference(getString(R.string.key_sync_data)).setOnPreferenceClickListener(this);

        // List of Children
        if (!appPref.getUserAccessOcariot().getSubjectType().equalsIgnoreCase(User.Type.CHILD)) {
            findPreference(getString(R.string.key_children)).setOnPreferenceClickListener(this);
        }

        // Initialize configuration for return of Fitbit authentication and authorization.
        initResultAuthFitBit();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        child = appPref.getLastSelectedChild();
        populateView();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnClickSettingsListener)
            mListener = (OnClickSettingsListener) context;
        else throw new ClassCastException();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDisposable.dispose();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.key_fitibit))) {
            if (!fitbitValidConfigs()) {
                mAlertMessage.show(R.string.title_error, R.string.error_configs_fitbit,
                        R.color.colorDanger, R.drawable.ic_warning_dark);
                return true;
            }

            if (!switchPrefFitBit.isChecked()) openDialogRevokeFitBit();
            else openFitbitAuth();

            // Activate the switch button only when you receive the action response.
            // Who will activate or deactivate will be the response of the action.
            switchPrefFitBit.setChecked(!switchPrefFitBit.isChecked());
            return true;
        }

        if (preference.getKey().equals(getString(R.string.key_signout))) {
            openDialogSignOut();
            return true;
        }

        if (preference.getKey().equals(getString(R.string.key_sync_data))) {
            fitBitSyncForced();
            return true;
        }

        if (preference.getKey().equals(getString(R.string.key_children))) {
            startActivity(new Intent(getActivity(), ChildrenManagerActivity.class));
            return true;
        }
        return false;
    }

    private void populateView() {
        mDialogSync = DialogLoading.newDialog(0,
                mContext.getResources().getString(R.string.synchronizing),
                mContext.getResources().getString(R.string.synchronizing_data_fitbit, child.getUsername())
        );
        populateSwitchFitbit();
    }

    private boolean fitbitValidConfigs() {
        if (appPref.getFitbitAppData() == null) return false;
        return appPref.getFitbitAppData().getClientId() != null &&
                appPref.getFitbitAppData().getClientSecret() != null;
    }

    /**
     * Checks the result of the authorization.
     * <p>
     * If the AuthorizationResponse object  is different from null,
     * authorization has been granted.
     * If the AuthorizationException object is different from null,
     * it means that some authorization problem has occurred
     */
    private void initResultAuthFitBit() {
        AuthorizationResponse authFitBitResponse = AuthorizationResponse.fromIntent(getActivity().getIntent());
        AuthorizationException authFitBitException = AuthorizationException.fromIntent(getActivity().getIntent());

        if (authFitBitException != null) {
            switchPrefFitBit.setChecked(false);

            // It only displays message if there is an internal error or with the fitbit server.
            // For other types of cases, such as the user canceled the operation,
            // no messages will be displayed.
            if (authFitBitException.type != AuthorizationException.TYPE_GENERAL_ERROR) {
                mAlertMessage.show(
                        R.string.title_error,
                        R.string.error_oauth_fitbit,
                        R.color.colorDanger,
                        R.drawable.ic_warning_dark
                );
            }
        } else if (authFitBitResponse != null) {
            // Request access token in server FitBit OAuth.
            getToken(authFitBitResponse);
        }
    }

    /**
     * Checks if Fitbit access has been provided for child
     * and marks the Switch, otherwise uncheck.
     */
    private void populateSwitchFitbit() {
        if (child.getFitBitAccess() != null && child.getFitBitAccess().getStatus() != null &&
                (child.getFitBitAccess().getStatus().equals("valid_token") ||
                        child.getFitBitAccess().getStatus().equals("expired_token"))) {
            switchPrefFitBit.setChecked(true);
        } else {
            switchPrefFitBit.setChecked(false);
        }
    }

    /**
     * Open dialog confirm revoke FitBit.
     */
    private void openDialogRevokeFitBit() {
        getActivity().runOnUiThread(() -> {
            AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext);
            mDialog.setMessage(mContext.getResources()
                    .getString(R.string.dialog_confirm_revoke_fitbit, child.getUsername()))
                    .setPositiveButton(R.string.title_yes, (dialog, which) -> revokeFitBitAuth())
                    .setNegativeButton(R.string.title_no, null)
                    .create()
                    .show();
        });
    }

    private void revokeFitBitAuth() {
        DialogLoading dialog = DialogLoading.newDialog(0,
                mContext.getResources().getString(R.string.title_processing),
                mContext.getResources().getString(R.string.dialog_revoke_fitbit_processing)
        );
        mDisposable.add(
                OcariotNetRepository.getInstance()
                        .revokeFitBitAuth(child.get_id())
                        .doOnSubscribe(disposable -> dialog.show(getFragmentManager()))
                        .doOnTerminate(dialog::close)
                        .subscribe(() -> {
                            switchPrefFitBit.setChecked(false);
                            child.setFitBitAccess(null);
                            appPref.addLastSelectedChild(child);

                            mAlertMessage.show(
                                    R.string.title_success,
                                    R.string.alert_revoke_fitbit_success,
                                    R.color.colorAccent,
                                    R.drawable.ic_action_check_dark);
                        }, err -> mAlertMessage.handleError(err))
        );
    }

    /**
     * Show dialog confirm sign out in app.
     */
    private void openDialogSignOut() {
        getActivity().runOnUiThread(() -> {
            AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext);
            mDialog.setMessage(R.string.dialog_confirm_sign_out)
                    .setPositiveButton(R.string.title_yes, (dialog, which) -> {
                                if (appPref.removeSession()) {
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            }
                    ).setNegativeButton(R.string.title_no, null)
                    .create().show();
        });
    }

    /**
     * Run process to get fitbit access token.
     *
     * @param authFitBitResponse {@link AuthorizationResponse}
     */
    private void getToken(AuthorizationResponse authFitBitResponse) {
        mDisposable.add(
                loginFitBit
                        .doAuthorizationToken(authFitBitResponse)
                        .doOnError(throwable -> switchPrefFitBit.setChecked(false))
                        .subscribe(mAuthState -> {
                            switchPrefFitBit.setChecked(true);
                            UserAccess userAccess = new UserAccess();
                            userAccess.setAccessToken(mAuthState.getAccessToken());
                            userAccess.setRefreshToken(mAuthState.getRefreshToken());
                            userAccess.setScope(mAuthState.getScope());
                            userAccess.setStatus("valid_token");

                            child.setFitBitAccess(userAccess);
                            appPref.addLastSelectedChild(child);
                            fitBitInitSync();
                        }, err -> {
                            switchPrefFitBit.setChecked(false);
                            mAlertMessage.show(
                                    R.string.title_error,
                                    R.string.fitbit_get_access_token_error,
                                    R.color.colorDanger,
                                    R.drawable.ic_warning_dark
                            );
                        })
        );
    }

    private void fitBitInitSync() {
        SyncDataRepository syncDataRepo = SyncDataRepository.getInstance(mContext);

        if (child.getFitBitAccess() == null || child.getFitBitAccess().getAccessToken() == null) {
            mAlertMessage.show(mContext.getResources().getString(R.string.alert_title_no_token_fitbit),
                    mContext.getResources().getString(R.string.alert_no_token_fitbit, child.getUsername()),
                    R.color.colorDanger, R.drawable.ic_warning_dark, 15000, true, null);
            return;
        }

        mDisposable.add(
                syncDataRepo.syncAll(child.get_id())
                        .doOnSubscribe(disposable -> mDialogSync.show(getFragmentManager()))
                        .doAfterTerminate(() -> mDialogSync.close())
                        .subscribe(
                                objects -> {
                                    showAlertResultSync(true);
                                    publishFitBitAuth();
                                },
                                err -> showAlertResultSync(false)
                        )
        );
    }

    private void fitBitSyncForced() {
        if (child.getFitBitAccess() == null || child.getFitBitAccess().getStatus() == null ||
                !child.getFitBitAccess().getStatus().equals("valid_token") &&
                        !child.getFitBitAccess().getStatus().equals("expired_token")) {
            mAlertMessage.show(mContext.getResources().getString(R.string.alert_title_no_token_fitbit),
                    mContext.getResources().getString(R.string.alert_no_token_fitbit, child.getUsername()),
                    R.color.colorDanger, R.drawable.ic_warning_dark, 15000, true,
                    new AlertMessage.AlertMessageListener() {
                        @Override
                        public void onHideListener() {
                            // not implemented!
                        }

                        @Override
                        public void onClickListener() {
                            openFitbitAuth();
                        }
                    });
            return;
        }

        mDisposable.add(
                OcariotNetRepository.getInstance()
                        .fitBitSync(child.get_id())
                        .doOnSubscribe(disposable -> mDialogSync.show(getFragmentManager()))
                        .doAfterTerminate(() -> mDialogSync.close())
                        .subscribe(
                                fitBitSync -> showAlertResultSync(true),
                                err -> {
                                    Log.w(LOG_TAG, "ERROR SYNC: " + err.getMessage());
                                    showAlertResultSync(false);
                                }
                        )
        );
    }

    /**
     * Send Fitbit authentication data to server.
     */
    private void publishFitBitAuth() {
        UserAccess fitBitAuth = child.getFitBitAccess();

        if (fitBitAuth == null || fitBitAuth.getAccessToken() == null) return;

        UserAccess userAccess = new UserAccess();
        userAccess.setAccessToken(fitBitAuth.getAccessToken());
        userAccess.setRefreshToken(fitBitAuth.getRefreshToken());

        mDisposable.add(
                OcariotNetRepository.getInstance()
                        .publishFitBitAuth(child.get_id(), userAccess)
                        .subscribe(() -> Log.d(LOG_TAG, "Fitbit authentication data sent successfully!"),
                                err -> Log.e(LOG_TAG, "Error sending Fitbit authentication data: " + err.getMessage())
                        )
        );
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMessageEvent(MessageEvent event) {
        if (event.getName().equals(MessageEvent.EventType.FITBIT_ACCESS_TOKEN_EXPIRED)) {
            mAlertMessage.show(mContext.getResources().getString(R.string.alert_title_token_expired),
                    mContext.getResources().getString(R.string.alert_token_expired_fitbit, child.getUsername()),
                    R.color.colorDanger, R.drawable.ic_warning_dark, 20000,
                    true, new AlertMessage.AlertMessageListener() {
                        @Override
                        public void onHideListener() {
                            // not implemented!
                        }

                        @Override
                        public void onClickListener() {
                            openFitbitAuth();
                        }
                    });
        }
    }

    private void showAlertResultSync(boolean isSuccess) {
        Alerter alerter = Alerter.create(getActivity())
                .setDuration(30000)
                .enableSwipeToDismiss()
                .setTitle(isSuccess ? R.string.title_success : R.string.title_error)
                .setText(isSuccess ? mContext.getResources()
                        .getString(R.string.sync_data_fitbit_success, child.getUsername()) :
                        mContext.getResources().getString(R.string.sync_data_fitbit_error, child.getUsername()))
                .setBackgroundColorRes(isSuccess ? R.color.colorAccent : R.color.colorDanger)
                .setIcon(isSuccess ? R.drawable.ic_happy_dark : R.drawable.ic_sad_dark);
        if (isSuccess) {
            alerter.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }
        alerter.show();
    }

    private void openFitbitAuth() {
        mListener.onPrefClick(mContext.getResources().getString(R.string.key_fitibit));
        loginFitBit.doAuthorizationCode();
    }

    public interface OnClickSettingsListener {
        void onPrefClick(String key);
    }
}
