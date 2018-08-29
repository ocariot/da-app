package br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.preference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import br.edu.uepb.nutes.ocariot.uaal_poc.R;
import br.edu.uepb.nutes.ocariot.uaal_poc.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.activity.LoginActivity;
import io.reactivex.CompletableObserver;
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
//    private AuthorizationResponse authFitBitResponse;
//    private AuthorizationException authFitBitException;

    private LoginFitBit loginFitBit;
    private OnClickSettingsListener mListener;

    public SettingsFragment() {
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        loginFitBit = new LoginFitBit(getActivity().getApplicationContext());

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
//        authFitBitResponse = AuthorizationResponse.fromIntent(getActivity().getIntent());
//        authFitBitException = AuthorizationException.fromIntent(getActivity().getIntent());
//
//        if (authFitBitException != null) {
////            Log.w("SETTIGNS", "ex " + authFitBitException.toJsonString());
//            switchPrefFitBit.setChecked(false);
//
//            /**
//             * It only displays message if there is an internal error or with the fitbit server.
//             * For other types of cases, such as the user canceled the operation,
//             * no messages will be displayed.
//             */
////            if (authFitBitException.type != AuthorizationException.TYPE_GENERAL_ERROR)
////                Toast.makeText(getActivity(), R.string.error_oauth_fitbit, Toast.LENGTH_LONG).show();
//        } else if (authFitBitResponse != null) {
//            // Request access token in server FitBit OAuth.
////            getToken(authFitBitResponse);
//        }
    }

    /**
     * Open dialog confirm revoke FitBit.
     */
    private void openDialogRevokeFitBit() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder mDialog = new AlertDialog.Builder(getActivity());
                mDialog.setMessage(R.string.dialog_confirm_revoke_fitbit)
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AppPreferencesHelper.getInstance(getActivity())
                                                .removeAuthStateFitBit().subscribe
                                                (new CompletableObserver() {
                                                    @Override
                                                    public void onSubscribe(Disposable d) {

                                                    }

                                                    @Override
                                                    public void onComplete() {
                                                        switchPrefFitBit.setChecked(false);
                                                    }

                                                    @Override
                                                    public void onError(Throwable e) {

                                                    }
                                                });
                                    }
                                }
                        ).setNegativeButton(android.R.string.no, null)
                        .create().show();
            }
        });
    }

    /**
     * Show dialog confirm sign out in app.
     */
    private void openDialogSignOut() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder mDialog = new AlertDialog.Builder(getActivity());
                mDialog.setMessage(R.string.dialog_confirm_sign_out)
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AppPreferencesHelper.getInstance(getActivity())
                                                .removeUserAccessOcariot()
                                                .subscribe(new CompletableObserver() {
                                                    @Override
                                                    public void onSubscribe(Disposable d) {

                                                    }

                                                    @Override
                                                    public void onComplete() {
                                                        startActivity(new Intent(getActivity(), LoginActivity.class));
                                                        getActivity().finish();
                                                    }

                                                    @Override
                                                    public void onError(Throwable e) {

                                                    }
                                                });
                                    }
                                }
                        ).setNegativeButton(android.R.string.no, null)
                        .create().show();
            }
        });
    }

//    /**
//     * Run process to get fitbit access token.
//     *
//     * @param authFitBitResponse {@link AuthorizationResponse}
//     */
//    private void getToken(AuthorizationResponse authFitBitResponse) {
//        loginFitBit.doAuthorizationToken(authFitBitResponse)
//                .subscribe(new SingleObserver<AuthState>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(AuthState authState) {
//                        Log.w("SETTIGNS", "TOKEN BEST " + authState.jsonSerializeString());
//
//                        switchPrefFitBit.setChecked(true);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        switchPrefFitBit.setChecked(false);
//                        Log.w("SETTIGNS", "error " + e.getMessage());
//                    }
//                });
//    }

    public interface OnClickSettingsListener {
        void onPrefClick(Preference preference);
    }
}
