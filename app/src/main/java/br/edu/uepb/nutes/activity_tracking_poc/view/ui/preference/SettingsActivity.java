package br.edu.uepb.nutes.activity_tracking_poc.view.ui.preference;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.ClientSecretBasic;

import br.edu.uepb.nutes.activity_tracking_poc.R;
import br.edu.uepb.nutes.activity_tracking_poc.view.ui.LoginFitBit;
import io.reactivex.disposables.Disposable;

/**
 * SettingsActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class SettingsActivity extends BaseSettingsActivity {
    private static final int RESULT_OCARIOT_LOGOFF = 1;
    private static final int RESULT_FITBIT_LOGIN = 2;
    private static final int RESULT_FITBIT_REVOKE = 3;

    private SwitchPreference switchPreference;
    private AuthorizationResponse authFitBitResponse;
    private AuthorizationException authFitBitException;

    private LoginFitBit loginFitBit;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        loginFitBit = LoginFitBit.getInstance(this);
        checkAuthFitBit();
    }

    /**
     *
     */
    private void checkAuthFitBit() {
        authFitBitResponse = AuthorizationResponse.fromIntent(getIntent());
        authFitBitException = AuthorizationException.fromIntent(getIntent());

        if (authFitBitException != null) {
            Log.w("SETTIGNS", "ex " + authFitBitException.toJsonString());
            switchPreference.setChecked(false);

            /**
             * It only displays message if there is an internal error or with the fitbit server.
             * For other types of cases, such as the user canceled the operation,
             * no messages will be displayed.
             */
            if (authFitBitException.code != AuthorizationException.TYPE_GENERAL_ERROR)
                Toast.makeText(this, R.string.error_oauth_fitbit, Toast.LENGTH_LONG).show();
        }

        // Request access token in server FitBit OAuth.
        getToken(authFitBitResponse);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickItemPref(Preference preference, int key) {
        switch (key) {
            case R.string.key_fitibit: {
                switchPreference = (SwitchPreference) preference;
                if (!switchPreference.isChecked()) openDialogRevokeFitBit();
                else loginFitBit.start();
            }
            break;
            case R.string.key_signout:
                openDialogSignOut();
                break;
            default:
                break;
        }
    }

    /**
     * Open dialog confirm revoke FitBit.
     */
    private void openDialogRevokeFitBit() {
        runOnUiThread(() -> {
            AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
            mDialog.setMessage(R.string.dialog_confirm_revoke_fitbit)
                    .setPositiveButton(android.R.string.yes,
                            (dialogInterface, which) -> {
                                switchPreference.setChecked(false);
                            }
                    ).setNegativeButton(android.R.string.no, null)
                    .create().show();
        });
    }

    /**
     * Show dialog confirm sign out in app.
     */
    private void openDialogSignOut() {
        runOnUiThread(() -> {
            AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
            mDialog.setMessage(R.string.dialog_confirm_sign_out)
                    .setPositiveButton(android.R.string.yes,
                            (dialogInterface, which) -> {

                            }
                    ).setNegativeButton(android.R.string.no, null)
                    .create().show();
        });
    }

    private void getToken(AuthorizationResponse authFitBitResponse) {
        disposable = loginFitBit.doAuthorizationToken(authFitBitResponse)
                .subscribe(authState -> {
                    Log.w("SETTIGNS", "TOKEN BEST " + authState.jsonSerializeString());

                    switchPreference.setChecked(true);
                }, error -> {
                    switchPreference.setChecked(false);
                    Log.w("SETTIGNS", "error " + error.getMessage());
                });
    }

}