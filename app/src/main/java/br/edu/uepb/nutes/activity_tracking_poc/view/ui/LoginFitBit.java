package br.edu.uepb.nutes.activity_tracking_poc.view.ui;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.ResponseTypeValues;

import java.util.Observable;

import javax.inject.Singleton;

import br.edu.uepb.nutes.activity_tracking_poc.view.ui.preference.SettingsActivity;
import io.reactivex.Single;

public class LoginFitBit {
    private final String LOG_TAG = "LoginFitBit";

    public static final int REQUEST_LOGIN_FITBIT = 1;
    public static final int REQUEST_LOGIN_FITBIT_SUCCESS = 2;
    public static final int REQUEST_LOGIN_FITBIT_CANCELED = 3;

    private final Uri AUTHORIZATION_ENDPOINT = Uri.parse("https://www.fitbit.com/oauth2/authorize");
    private final Uri TOKEN_ENDPOINT = Uri.parse("https://api.fitbit.com/oauth2/token");
    private final Uri REDIRECT_URI = Uri.parse("fitbitauth://finished");

    private final String CLIENT_ID = "22CY5N";
    private final String CLIENT_SECRET = "fe3276dc3210391d3e234532278d8c33"; // TODO REMOVER!!!

    public static LoginFitBit instance;
    private static Context mContext;

    private AuthState mAuthState;

    private LoginFitBit() {
    }

    public static synchronized LoginFitBit getInstance(Context context) {
        if (instance == null) instance = new LoginFitBit();

        LoginFitBit.mContext = context;
        return instance;
    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        initConfig();
//    }

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
                .setScopes("profile", "activity")
                .build();

        doAuthorizationCode(authRequest);
    }

    /**
     * Initialize settings to get call answer.
     * A startActivityForResult call using an Intention returned from the {@link AuthorizationService}.
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

    public Single<AuthState> doAuthorizationToken(AuthorizationResponse resp) {
        AuthorizationResponse response = resp;
        ClientAuthentication clientAuth = new ClientSecretBasic(CLIENT_SECRET);
        AuthorizationService service = new AuthorizationService(mContext);

        return Single.create(emitter -> {
            service.performTokenRequest(response.createTokenExchangeRequest(), clientAuth, (tokenResponse, exception) -> {
                if (exception != null) {
                    Log.w(LOG_TAG, "Token Exchange failed: " + exception.toJsonString());
                    emitter.onError(exception);
                    return;
                }

                mAuthState.update(tokenResponse, exception);
                emitter.onSuccess(mAuthState);
            });
        });

    }
}