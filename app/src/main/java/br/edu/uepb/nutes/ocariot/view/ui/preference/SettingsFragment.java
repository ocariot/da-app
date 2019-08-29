package br.edu.uepb.nutes.ocariot.view.ui.preference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.Toast;

import com.tapadoo.alerter.Alerter;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.User;
import br.edu.uepb.nutes.ocariot.data.repository.SyncDataRepository;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot.OcariotNetRepository;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import br.edu.uepb.nutes.ocariot.utils.DialogLoading;
import br.edu.uepb.nutes.ocariot.utils.MessageEvent;
import br.edu.uepb.nutes.ocariot.view.ui.activity.LoginActivity;
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
    private AppPreferencesHelper appPreferencesHelper;
    private DialogLoading mDialogSync;

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
        addPreferencesFromResource(R.xml.preferences);

        mContext = getActivity();
        mDisposable = new CompositeDisposable();
        loginFitBit = new LoginFitBit(mContext);
        appPreferencesHelper = AppPreferencesHelper.getInstance(mContext);
        mDialogSync = DialogLoading.newDialog(0, R.string.synchronizing,
                R.string.synchronizing_data_fitbit);

        // FitBit
        switchPrefFitBit = (SwitchPreference) findPreference(getString(R.string.key_fitibit));
        switchPrefFitBit.setOnPreferenceClickListener(this);

        // Sign Out
        findPreference(getString(R.string.key_signout)).setOnPreferenceClickListener(this);

        // Version
        findPreference(getString(R.string.key_version)).setOnPreferenceClickListener(this);

        // sync
        findPreference(getString(R.string.key_sync_data)).setOnPreferenceClickListener(this);

        checkAuthFitBit();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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
            if (!switchPrefFitBit.isChecked()) openDialogRevokeFitBit();
            else {
                mListener.onPrefClick(preference);
                loginFitBit.doAuthorizationCode();
            }

            // Activate the switch button only when you receive the action response.
            // Who will activate or deactivate will be the response of the action.
            switchPrefFitBit.setChecked(!switchPrefFitBit.isChecked());
            return true;
        } else if (preference.getKey().equals(getString(R.string.key_signout))) {
            openDialogSignOut();
            return true;
        } else if (preference.getKey().equals(getString(R.string.key_sync_data))) {
            syncData();
        }

        mListener.onPrefClick(preference);
        return false;
    }

    /**
     * Checks the result of the authorization.
     * <p>
     * If the AuthorizationResponse object  is different from null,
     * authorization has been granted.
     * If the AuthorizationException object is different from null,
     * it means that some authorization problem has occurred
     */
    private void checkAuthFitBit() {
        AuthorizationResponse authFitBitResponse = AuthorizationResponse.fromIntent(getActivity().getIntent());
        AuthorizationException authFitBitException = AuthorizationException.fromIntent(getActivity().getIntent());
        if (AppPreferencesHelper.getInstance(mContext).getAuthStateFitBit() == null) {
            switchPrefFitBit.setChecked(false);
        } else {
            switchPrefFitBit.setChecked(true);
        }

        if (authFitBitException != null) {
            switchPrefFitBit.setChecked(false);

            // It only displays message if there is an internal error or with the fitbit server.
            // For other types of cases, such as the user canceled the operation,
            // no messages will be displayed.
            if (authFitBitException.type != AuthorizationException.TYPE_GENERAL_ERROR)
                Toast.makeText(mContext, R.string.error_oauth_fitbit,
                        Toast.LENGTH_LONG).show();
        } else if (authFitBitResponse != null) {
            // Request access token in server FitBit OAuth.
            getToken(authFitBitResponse);
        }
    }

    /**
     * Open dialog confirm revoke FitBit.
     */
    private void openDialogRevokeFitBit() {
        getActivity().runOnUiThread(() -> {
            AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext);
            mDialog.setMessage(R.string.dialog_confirm_revoke_fitbit)
                    .setPositiveButton(android.R.string.yes,
                            (dialog, which) -> {
                                if (AppPreferencesHelper.getInstance(mContext)
                                        .removeAuthStateFitBit()) {
                                    switchPrefFitBit.setChecked(false);
                                }
                            }
                    ).setNegativeButton(android.R.string.no, null)
                    .create().show();
        });
    }

    /**
     * Show dialog confirm sign out in app.
     */
    private void openDialogSignOut() {
        getActivity().runOnUiThread(() -> {
            AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext);
            mDialog.setMessage(R.string.dialog_confirm_sign_out)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                if (AppPreferencesHelper.getInstance(mContext).removeSession()) {
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            }
                    ).setNegativeButton(android.R.string.no, null)
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
                        .subscribe(authState -> {
                            switchPrefFitBit.setChecked(true);
                            syncData();
                        })
        );
    }

    public interface OnClickSettingsListener {
        void onPrefClick(Preference preference);
    }

    private void syncData() {
        SyncDataRepository syncDataRepo = SyncDataRepository.getInstance(mContext);

        if (appPreferencesHelper.getAuthStateFitBit() == null) {
            Alerter.create(getActivity())
                    .setDuration(40000)
                    .enableVibration(true)
                    .enableSwipeToDismiss()
                    .setTitle(R.string.alert_title_no_token_fitbit)
                    .setText(R.string.alert_no_token_fitbit)
                    .setBackgroundColorRes(R.color.colorDanger)
                    .setIcon(R.drawable.ic_warning_dark)
                    .show();
            return;
        }

        mDisposable.add(
                syncDataRepo.syncAll(appPreferencesHelper.getChildProfile().get_id())
                        .doOnSubscribe(disposable -> mDialogSync.show(getFragmentManager()))
                        .doAfterTerminate(() -> mDialogSync.close())
                        .subscribe(
                                o -> {
                                    showAlertResultSync(true);
                                    updateLastSync();
                                },
                                err -> showAlertResultSync(false)
                        )
        );
    }

    /**
     * Update last sync.
     * Run when the logged in user is Educator / HP / Family, as child is not allowed.
     */
    private void updateLastSync() {
        if (appPreferencesHelper.getUserAccessOcariot().getSubjectType()
                .equalsIgnoreCase(User.Type.CHILD)) return;

        mDisposable.add(
                OcariotNetRepository.getInstance(mContext)
                        .updateLastSync(
                                appPreferencesHelper.getChildProfile().get_id(),
                                DateUtils.getCurrentDatetimeUTC()
                        )
                        .subscribe(() -> Log.w(LOG_TAG, "Update last sync success!"),
                                err -> Log.w(LOG_TAG, "Error update last sync: " + err.getMessage()))
        );
    }

    private void publishFitBitAuth() {
//        mDisposable.add(
//                OcariotNetRepository.getInstance(mContext)
//                        .publishFitBitAuth(
//                                appPreferencesHelper.getChildProfile().get_id(),
//                                appPreferencesHelper.getUserAccessOcariot().getAccessToken()
//                        )
//                        .subscribe(
//                                child -> Log.w(LOG_TAG, "Update last sync success!"),
//                                err -> Log.w(LOG_TAG, "Error update last sync: " + err.getMessage())
//                        )
//        );
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMessageEvent(MessageEvent event) {
        if (event.getName().equals(MessageEvent.EventType.FITBIT_ACCESS_TOKEN_EXPIRED)) {
            Alerter.create(getActivity())
                    .setDuration(40000)
                    .enableSwipeToDismiss()
                    .setTitle(R.string.alert_title_token_expired)
                    .setBackgroundColorRes(R.color.colorDanger)
                    .setIcon(R.drawable.ic_warning_dark)
                    .setText(R.string.alert_token_expired_fitbit)
                    .setOnClickListener((v) -> loginFitBit.doAuthorizationCode())
                    .show();
        }
    }

    private void showAlertResultSync(boolean isSuccess) {
        Alerter alerter = Alerter.create(getActivity())
                .setDuration(30000)
                .enableSwipeToDismiss()
                .setTitle(isSuccess ? R.string.title_success : R.string.title_error)
                .setText(isSuccess ? R.string.sync_data_fitbit_success : R.string.sync_data_fitbit_error)
                .setBackgroundColorRes(isSuccess ? R.color.colorAccent : R.color.colorDanger)
                .setIcon(isSuccess ? R.drawable.ic_happy_dark : R.drawable.ic_sad_dark);
        if (isSuccess) alerter.setOnClickListener(v -> getActivity().finish());
        alerter.show();
    }
}
