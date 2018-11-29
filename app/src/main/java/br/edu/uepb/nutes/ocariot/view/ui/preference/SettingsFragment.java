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

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.view.ui.activity.LoginActivity;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

/**
 * SettingsFragment implementation.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    public final String LOG_TAG = "SettingsFragment";

    private SwitchPreference switchPrefFitBit;
    private LoginFitBit loginFitBit;
    private OnClickSettingsListener mListener;

    public SettingsFragment() {
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

        loginFitBit = new LoginFitBit(getActivity().getApplicationContext());

        // FitBit
        switchPrefFitBit = (SwitchPreference) findPreference(getString(R.string.key_fitibit));
        switchPrefFitBit.setOnPreferenceClickListener(this);

        // Sign Out
        findPreference(getString(R.string.key_signout)).setOnPreferenceClickListener(this);

        checkAuthFitBit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnClickSettingsListener)
            mListener = (OnClickSettingsListener) context;
        else throw new ClassCastException();
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

        if (authFitBitException != null) {
            Log.w(LOG_TAG, authFitBitException.toJsonString());
            switchPrefFitBit.setChecked(false);

            // It only displays message if there is an internal error or with the fitbit server.
            // For other types of cases, such as the user canceled the operation,
            // no messages will be displayed.
            if (authFitBitException.type != AuthorizationException.TYPE_GENERAL_ERROR)
                Toast.makeText(getActivity(), R.string.error_oauth_fitbit, Toast.LENGTH_LONG).show();
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
            AlertDialog.Builder mDialog = new AlertDialog.Builder(getActivity());
            mDialog.setMessage(R.string.dialog_confirm_revoke_fitbit)
                    .setPositiveButton(android.R.string.yes,
                            (dialog, which) -> {
                                if (AppPreferencesHelper.getInstance(getActivity())
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
            AlertDialog.Builder mDialog = new AlertDialog.Builder(getActivity());
            mDialog.setMessage(R.string.dialog_confirm_sign_out)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                if (AppPreferencesHelper.getInstance(getActivity())
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

        loginFitBit.doAuthorizationToken(authFitBitResponse)
                .subscribe(new SingleObserver<AuthState>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(AuthState authState) {
                        Log.w("SETTIGNS", "TOKEN BEST " + authState.jsonSerializeString());

                        switchPrefFitBit.setChecked(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        switchPrefFitBit.setChecked(false);
                        Log.w("SETTIGNS", "error " + e.getMessage());
                    }
                });
    }

    public interface OnClickSettingsListener {
        void onPrefClick(Preference preference);
    }
}
