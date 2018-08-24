package br.edu.uepb.nutes.activity_tracking_poc.view.ui.preference;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.ResponseTypeValues;

import br.edu.uepb.nutes.activity_tracking_poc.data.repository.local.pref.AppPreferencesHelper;
import io.reactivex.Single;

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

    public static LoginFitBit instance;
    private static Context mContext;

    private AuthState mAuthState;
    private AppPreferencesHelper mPreferences;

    private LoginFitBit() {
    }

    /**
     * Recover single instance of class.
     *
     * @param context {@link Context}
     * @return LoginFitBit
     */
    public static synchronized LoginFitBit getInstance(Context context) {
        if (instance == null) instance = new LoginFitBit();

        LoginFitBit.mContext = context;
        return instance;
    }

    /**
     * Initialize settings to obtain authorization code.
     */
    public void start() {
        AuthorizationServiceConfiguration serviceConfig = new AuthorizationServiceConfiguration(
                AUTHORIZATION_ENDPOINT, // authorization endpoint
                TOKEN_ENDPOINT // token endpoint
        );

        AuthorizationRequest.Builder authRequestBuilder =
                new AuthorizationRequest.Builder(
                        serviceConfig, // the authorization service configuration
                        CLIENT_ID, // the client ID, typically pre-registered and static
                        ResponseTypeValues.CODE, // the response_type value: we want a code
                        REDIRECT_URI); // the redirect URI to which the auth response is sent

        mAuthState = new AuthState(serviceConfig);

        AuthorizationRequest authRequest = authRequestBuilder
                .setScopes("activity", "sleep")
                .build();

        doAuthorizationCode(authRequest);
    }

    /**
     * Initialize settings to get call answer.
     * A startActivityForResult call using an Intention returned
     * from the {@link AuthorizationService}.
     *
     * @param authRequest {@link AuthorizationRequest}
     * @param authRequest
     */
    private void doAuthorizationCode(AuthorizationRequest authRequest) {
        AuthorizationService authService = new AuthorizationService(mContext);

        authService.performAuthorizationRequest(
                authRequest,
                PendingIntent.getActivity(mContext, REQUEST_LOGIN_FITBIT_SUCCESS,
                        new Intent(mContext, SettingsActivity.class), 0),
                PendingIntent.getActivity(mContext, REQUEST_LOGIN_FITBIT_CANCELED,
                        new Intent(mContext, SettingsActivity.class), 0));
    }

    /**
     * Retrieve access token based on authorization code.
     *
     * @param resp {@link AuthorizationResponse}
     * @return Single<AuthState>
     */
    public Single<AuthState> doAuthorizationToken(AuthorizationResponse resp) {
        AuthorizationResponse response = resp;
        ClientAuthentication clientAuth = new ClientSecretBasic(CLIENT_SECRET);
        AuthorizationService service = new AuthorizationService(mContext);

        return Single.create(emitter -> {
            service.performTokenRequest(response.createTokenExchangeRequest(),
                    clientAuth, (tokenResponse, exception) -> {
                        mAuthState.update(tokenResponse, exception);

                        if (exception != null) {
                            emitter.onError(exception);
                            return;
                        }

                        // Save object AuthState in sharedPreferences
                        mPreferences = AppPreferencesHelper.getInstance(mContext);
                        mPreferences.addAuthStateFiBIt(mAuthState).subscribe(() -> {
                            Log.w(LOG_TAG, "addAuthStateFiBIt() success: ");
                            emitter.onSuccess(mAuthState);
                        }, error -> {
                            Log.w(LOG_TAG, "addAuthStateFiBIt() error: " + error.getMessage());
                            emitter.onError(error);
                        });
                    });
        });

    }
}