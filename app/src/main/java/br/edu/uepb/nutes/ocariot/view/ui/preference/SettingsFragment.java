package br.edu.uepb.nutes.ocariot.view.ui.preference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.tapadoo.alerter.Alerter;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.User;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot.OcariotNetRepository;
import br.edu.uepb.nutes.ocariot.utils.AlertMessage;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import br.edu.uepb.nutes.ocariot.utils.DialogLoading;
import br.edu.uepb.nutes.ocariot.utils.FirebaseLogEvent;
import br.edu.uepb.nutes.ocariot.view.ui.activity.ChildrenManagerActivity;
import br.edu.uepb.nutes.ocariot.view.ui.activity.LoginActivity;
import br.edu.uepb.nutes.ocariot.view.ui.activity.MainActivity;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.HttpException;
import timber.log.Timber;

/**
 * SettingsFragment implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private SwitchPreference switchPrefFitBit;
    private LoginFitBit loginFitBit;
    private OnClickSettingsListener mListener;
    private Context mContext;
    private CompositeDisposable mDisposable;
    private AppPreferencesHelper appPref;
    private DialogLoading mDialogSync;
    private AlertMessage mAlertMessage;
    private Child mChild;
    private UserAccess mUserAccess;

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
        mUserAccess = appPref.getUserAccessOcariot();

        if (mUserAccess.getSubjectType().equals(User.Type.FAMILY)) {
            addPreferencesFromResource(R.xml.preferences_family);
        } else {
            addPreferencesFromResource(R.xml.preferences);
        }

        initEvents();
        // Initialize configuration for return of Fitbit authentication and authorization.
        initResultAuthFitBit();
        Alerter.hide();
    }

    private void initEvents() {
        // FitBit
        switchPrefFitBit = (SwitchPreference) findPreference(getString(R.string.key_fitibit));
        if (switchPrefFitBit != null) {
            switchPrefFitBit.setOnPreferenceClickListener(this);
        }

        // Sign Out
        findPreference(getString(R.string.key_signout)).setOnPreferenceClickListener(this);

        // sync
        findPreference(getString(R.string.key_sync_data)).setOnPreferenceClickListener(this);

        // List of Children
        Preference listChildrenPrefFitBit = findPreference(getString(R.string.key_children));
        if (listChildrenPrefFitBit != null) {
            listChildrenPrefFitBit.setOnPreferenceClickListener(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mChild = appPref.getLastSelectedChild();
        populateView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnClickSettingsListener) {
            mListener = (OnClickSettingsListener) context;
        } else throw new ClassCastException();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mListener = (OnClickSettingsListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDisposable.dispose();
        Alerter.hide();
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
            fitBitSync();
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
                mContext.getResources().getString(R.string.synchronizing_data_fitbit, mChild.getUsername())
        );
        updateViewLastSync();
        populateSwitchFitbit();
    }

    /**
     * Updates key preference equal to "key_sync_data" with last sync date.
     */
    private void updateViewLastSync() {
        new Handler().post(() -> {
            Preference prefSync = findPreference(getString(R.string.key_sync_data));
            prefSync.setSummary(getResources().getString(R.string.synchronization_data)
                    .concat("\n\n")
                    .concat(getResources().getString(R.string.last_sync_date_time, mChild.getLastSync() != null ?
                            DateUtils.convertDateTimeUTCToLocale(mChild.getLastSync(),
                                    getResources().getString(R.string.date_time_abb5), null) : "--")
                    )
            );
        });
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
     * Checks if Fitbit access has been provided for mChild
     * and marks the Switch, otherwise uncheck.
     */
    private void populateSwitchFitbit() {
        if (mUserAccess.getSubjectType().equals(User.Type.FAMILY)) {
            return;
        }
        switchPrefFitBit.setChecked(false);
        if (mChild.isFitbitAccessValid()) {
            switchPrefFitBit.setChecked(true);
        }
    }

    /**
     * Open dialog confirm revoke FitBit.
     */
    private void openDialogRevokeFitBit() {
        getActivity().runOnUiThread(() -> {
            AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext);
            mDialog.setMessage(mContext.getResources()
                    .getString(R.string.dialog_confirm_revoke_fitbit, mChild.getUsername()))
                    .setPositiveButton(R.string.title_yes, (dialog, which) -> revokeFitBitAuth())
                    .setNegativeButton(R.string.title_no, null)
                    .create()
                    .show();
        });
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
                    .create()
                    .show();
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
                        .subscribe(userAccess -> {
                            switchPrefFitBit.setChecked(true);
                            publishFitBitAuth(userAccess);
                        }, err -> {
                            Timber.e(err);
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

    /**
     * Send Fitbit authentication data to server.
     */
    private void publishFitBitAuth(UserAccess userAccess) {
        mDisposable.add(
                OcariotNetRepository.getInstance()
                        .publishFitBitAuth(mChild.getId(), userAccess, null)
                        .doOnSubscribe(disposable -> mDialogSync.show(getFragmentManager()))
                        .subscribe(() -> {
                                    mChild.setFitBitAccess(userAccess);
                                    appPref.addLastSelectedChild(mChild);
                                    fitBitSync();
                                    FirebaseLogEvent.fitbitAuthGranted();
                                },
                                err -> {
                                    mDialogSync.close();
                                    switchPrefFitBit.setChecked(false);
                                    if (err instanceof HttpException) {
                                        HttpException httpEx = ((HttpException) err);
                                        if (httpEx.code() == 400) {
                                            mAlertMessage.show(R.string.title_error,
                                                    R.string.error_400_1,
                                                    R.color.colorDanger,
                                                    R.drawable.ic_sad_dark);
                                            return;
                                        }
                                    }
                                    mAlertMessage.handleError(err);
                                }
                        )
        );
    }

    /**
     * Force a new Fitbit sync.
     */
    private void fitBitSync() {
        if (!mChild.isFitbitAccessValid()) {
            mAlertMessage.show(mContext.getResources().getString(R.string.alert_title_no_token_fitbit),
                    mContext.getResources().getString(R.string.alert_no_token_fitbit, mChild.getUsername()),
                    R.color.colorDanger, R.drawable.ic_warning_dark, 15000, true,
                    new AlertMessage.AlertMessageListener() {
                        @Override
                        public void onHideListener() {
                            // not implemented!
                        }

                        @Override
                        public void onClickListener() {
                            if (mUserAccess.getSubjectType().equals(User.Type.FAMILY)) {
                                return;
                            }
                            openFitbitAuth();
                        }
                    });
            return;
        }

        mDisposable.add(
                OcariotNetRepository.getInstance()
                        .fitBitSync(mChild.getId())
                        .doOnSubscribe(disposable -> {
                            if (!mDialogSync.isVisible()) mDialogSync.show(getFragmentManager());
                        })
                        .doAfterTerminate(() -> mDialogSync.close())
                        .subscribe(
                                fitBitSync -> {
                                    mChild.setLastSync(DateUtils.getCurrentDatetimeUTC());
                                    appPref.addLastSelectedChild(mChild);
                                    updateViewLastSync();
                                    showAlertResultSync(true);
                                    FirebaseLogEvent.fitbitSync();
                                },
                                err -> {
                                    Timber.e(err);
                                    showAlertResultSync(false);
                                }
                        )
        );
    }

    /**
     * Revokes the Fitbit access token.
     */
    private void revokeFitBitAuth() {
        DialogLoading dialog = DialogLoading.newDialog(0,
                mContext.getResources().getString(R.string.title_processing),
                mContext.getResources().getString(R.string.dialog_revoke_fitbit_processing)
        );
        mDisposable.add(
                OcariotNetRepository.getInstance()
                        .revokeFitBitAuth(mChild.getId())
                        .doOnSubscribe(disposable -> dialog.show(getFragmentManager()))
                        .doOnTerminate(dialog::close)
                        .subscribe(this::revokeSuccess, err -> {
                            if (err instanceof HttpException) {
                                HttpException httpEx = ((HttpException) err);
                                if (httpEx.code() == 400) {
                                    revokeSuccess();
                                    return;
                                }
                            }
                            mAlertMessage.handleError(err);
                        })
        );
    }

    private void revokeSuccess() {
        FirebaseLogEvent.fitbitAuthRevoke();
        switchPrefFitBit.setChecked(false);
        mChild.setFitBitAccess(null);
        appPref.addLastSelectedChild(mChild);

        mAlertMessage.show(
                R.string.title_success,
                R.string.alert_revoke_fitbit_success,
                R.color.colorAccent,
                R.drawable.ic_action_check_dark);
    }

    /**
     * Display Fitbit data sync success or error message.
     *
     * @param isSuccess boolean
     */
    private void showAlertResultSync(boolean isSuccess) {
        Alerter alerter = Alerter.create(getActivity())
                .setDuration(30000)
                .enableSwipeToDismiss()
                .setTitle(isSuccess ? R.string.title_success : R.string.title_error)
                .setText(isSuccess ? mContext.getResources()
                        .getString(R.string.sync_data_fitbit_success, mChild.getUsername()) :
                        mContext.getResources().getString(R.string.sync_data_fitbit_error, mChild.getUsername()))
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

    /**
     * Open CustomTab for Fitbit authorization.
     */
    private void openFitbitAuth() {
        mListener.onPrefClick(getResources().getString(R.string.key_fitibit));
        loginFitBit.doAuthorizationCode();
    }

    public interface OnClickSettingsListener {
        void onPrefClick(String key);
    }
}
