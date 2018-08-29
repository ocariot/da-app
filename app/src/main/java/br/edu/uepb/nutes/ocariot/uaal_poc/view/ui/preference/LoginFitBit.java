package br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.preference;

import android.content.Context;
import android.net.Uri;

import  br.edu.uepb.nutes.ocariot.uaal_poc.data.repository.local.pref.AppPreferencesHelper;

/**
 * LoginFitBit implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class LoginFitBit {
    private final String LOG_TAG = "LoginFitBit";

    public static final int REQUEST_LOGIN_FITBIT_SUCCESS = 2;
    public static final int REQUEST_LOGIN_FITBIT_CANCELED = 3;

    private final Uri AUTHORIZATION_ENDPOINT = Uri.parse("https://www.fitbit.com/oauth2/authorize");
    private final Uri TOKEN_ENDPOINT = Uri.parse("https://api.fitbit.com/oauth2/token");
    private final Uri REVOKE_TOKEN_ENDPOINT = Uri.parse("https://api.fitbit.com/oauth2/revoke");

    private final Uri REDIRECT_URI = Uri.parse("fitbitauth://finished");

    private final String CLIENT_ID = "22CY5N";
    private final String CLIENT_SECRET = "fe3276dc3210391d3e234532278d8c33"; // TODO REMOVER!!!

    private Context mContext;

//    private AuthState mAuthState;
//    private AuthorizationService mAuthService;
//    private AuthorizationRequest mAuthRequest;
    private AppPreferencesHelper mPreferences;

    public LoginFitBit(Context context) {
        this.mContext = context;
        initConfig();
    }
    /**
     * Initialize settings to obtain authorization code.
     */
    public void initConfig() {
//        AuthorizationServiceConfiguration serviceConfig = new AuthorizationServiceConfiguration(
//                AUTHORIZATION_ENDPOINT, // authorization endpoint
//                TOKEN_ENDPOINT // token endpoint
//        );
//
//        AuthorizationRequest.Builder authRequestBuilder =
//                new AuthorizationRequest.Builder(
//                        serviceConfig, // the authorization service configuration
//                        CLIENT_ID, // the client ID, typically pre-registered and static
//                        ResponseTypeValues.CODE, // the response_type value: we want a code
//                        REDIRECT_URI); // the redirect URI to which the auth response is sent
//
//        mAuthState = new AuthState(serviceConfig);
//
//        mAuthRequest = authRequestBuilder
//                .setScopes("activity", "sleep")
//                .build();
    }

    /**
     * Initialize settings to get call answer.
     * A startActivityForResult call using an Intention returned
     *
     */
    public void doAuthorizationCode() {
//        mAuthService = new AuthorizationService(mContext);
//
//        mAuthService.performAuthorizationRequest(
//                mAuthRequest,
//                PendingIntent.getActivity(mContext, REQUEST_LOGIN_FITBIT_SUCCESS,
//                        new Intent(mContext, SettingsActivity.class), 0),
//                PendingIntent.getActivity(mContext, REQUEST_LOGIN_FITBIT_CANCELED,
//                        new Intent(mContext, SettingsActivity.class), 0));
    }
}