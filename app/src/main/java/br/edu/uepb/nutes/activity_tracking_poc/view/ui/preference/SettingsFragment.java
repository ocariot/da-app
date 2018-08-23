package br.edu.uepb.nutes.activity_tracking_poc.view.ui.preference;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;

import br.edu.uepb.nutes.activity_tracking_poc.R;
import io.reactivex.disposables.Disposable;

/**
 * SettingsFragment implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    public final String LOG_TAG = "SettingsFragment";

    private SwitchPreference switchPrefFitBit;
    private AuthorizationResponse authFitBitResponse;
    private AuthorizationException authFitBitException;
    private ProgressDialog progressDialog;

    private LoginFitBit loginFitBit;
    private Disposable disposable;

    public SettingsFragment() {
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        loginFitBit = LoginFitBit.getInstance(getActivity());

        // FitBit
        Preference prefFitbit = findPreference(getString(R.string.key_fitibit));
        prefFitbit.setOnPreferenceClickListener(this);
        switchPrefFitBit = (SwitchPreference) prefFitbit;

        // Sign Out
        Preference prefSignout = findPreference(getString(R.string.key_signout));
        prefSignout.setOnPreferenceClickListener(this);

        checkAuthFitBit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null) disposable.dispose();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.key_fitibit))) {
            if (!switchPrefFitBit.isChecked()) openDialogRevokeFitBit();
            else {
                loginFitBit.start();
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
        authFitBitResponse = AuthorizationResponse.fromIntent(getActivity().getIntent());
        authFitBitException = AuthorizationException.fromIntent(getActivity().getIntent());

        if (authFitBitException != null) {
            Log.w("SETTIGNS", "ex " + authFitBitException.toJsonString());
            switchPrefFitBit.setChecked(false);

            /**
             * It only displays message if there is an internal error or with the fitbit server.
             * For other types of cases, such as the user canceled the operation,
             * no messages will be displayed.
             */
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
                            (dialogInterface, which) -> {
                                switchPrefFitBit.setChecked(false);
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
                    .setPositiveButton(android.R.string.yes,
                            (dialogInterface, which) -> {

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
        disposable = loginFitBit.doAuthorizationToken(authFitBitResponse)
                .subscribe(authState -> {
                    Log.w("SETTIGNS", "TOKEN BEST " + authState.jsonSerializeString());

                    switchPrefFitBit.setChecked(true);
                }, error -> {
                    switchPrefFitBit.setChecked(false);
                    Log.w("SETTIGNS", "error " + error.getMessage());
                });
    }
}
