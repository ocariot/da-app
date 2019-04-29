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

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.fitbit.FitBitNetRepository;
import br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot.OcariotNetRepository;
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
    private OcariotNetRepository ocariotRepository;
    private FitBitNetRepository fitBitRepository;

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
        ocariotRepository = OcariotNetRepository.getInstance(mContext);
        fitBitRepository = FitBitNetRepository.getInstance(mContext);

        // FitBit
        switchPrefFitBit = (SwitchPreference) findPreference(getString(R.string.key_fitibit));
        switchPrefFitBit.setOnPreferenceClickListener(this);

        // Sign Out
        findPreference(getString(R.string.key_signout)).setOnPreferenceClickListener(this);

        // Version
        findPreference(getString(R.string.key_version)).setOnPreferenceClickListener(this);

        checkAuthFitBit();
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
        }

        if (preference.getKey().equals(getString(R.string.key_signout))) {
            openDialogSignOut();
            return true;
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
            Log.w(LOG_TAG, authFitBitException.toJsonString());
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
                                if (AppPreferencesHelper.getInstance(mContext)
                                        .removeUserAccessOcariot()) {
                                    startActivity(
                                            new Intent(getActivity(), LoginActivity.class)
                                    );
                                    getActivity().finish();
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
                            syncChildOcariotWithFitbit(AppPreferencesHelper
                                    .getInstance(mContext)
                                    .getUserAccessOcariot()
                                    .getSubject()
                            );
                        })
        );
    }

    public interface OnClickSettingsListener {
        void onPrefClick(Preference preference);
    }


    /**
     * TODO Temporary method. To remove!!! Well, you should not get fitbit profile data.
     *
     * @param userId
     */
    private void syncChildOcariotWithFitbit(String userId) {
        mDisposable.add(
                fitBitRepository
                        .getProfile()
                        .subscribe(userFitBit -> {
                            Log.w(LOG_TAG, "CHILD FITBIT " + userFitBit.toString());
                            Child child = new Child();
                            child.set_id(userId);
                            child.setAge(userFitBit.getAge());
                            child.setGender(userFitBit.getGender().toLowerCase());
                            Log.w(LOG_TAG, "CHILD UP " + child.toString());
                            this.updateChild(child);
                        }, err -> {
                            Log.w(LOG_TAG, "FITBIT ERROR PROFILE" + err.getMessage());
                            Toast.makeText(mContext, "Falha ao tentar recuperar perfil da criança no FitBit!",
                                    Toast.LENGTH_LONG).show();
                        })
        );
    }

    /**
     * TODO Temporary method. To remove!!! Well, you should not get fitbit profile data.
     *
     * @param child
     */
    private void updateChild(Child child) {
        mDisposable.add(
                ocariotRepository
                        .updateChild(child)
                        .subscribe(childRes -> {
                            AppPreferencesHelper.getInstance(mContext).addChildProfile(childRes);
                            Toast.makeText(mContext, "Perfil da criança atualizado!",
                                    Toast.LENGTH_LONG).show();
                        }, error -> {
                            Toast.makeText(mContext, "Falha ao tentar sincronizar perfil da criança no OCARIoT!",
                                    Toast.LENGTH_LONG).show();
                        })
        );
    }
}
