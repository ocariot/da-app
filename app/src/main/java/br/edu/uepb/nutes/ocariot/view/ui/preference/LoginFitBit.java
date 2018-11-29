package br.edu.uepb.nutes.ocariot.view.ui.preference;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
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
import net.openid.appauth.TokenResponse;

import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

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

    private final Uri REDIRECT_URI = Uri.parse("fitbitauth://finished");

    private final String CLIENT_ID = "22CY5N";
    private final String CLIENT_SECRET = "fe3276dc3210391d3e234532278d8c33"; // TODO REMOVER!!!

    private Context mContext;

    private AuthState mAuthState;
    private AuthorizationService mAuthService;
    private AuthorizationRequest mAuthRequest;

    public LoginFitBit(Context context) {
        this.mContext = context;
        initConfig();
    }

    /**
     * Initialize settings to obtain authorization code.
     */
    public void initConfig() {
        AuthorizationServiceConfiguration serviceConfig = new AuthorizationServiceConfiguration(
                AUTHORIZATION_ENDPOINT, // authorization endpoint
                TOKEN_ENDPOINT, // token endpoint
                null
        );

        AuthorizationRequest.Builder authRequestBuilder =
                new AuthorizationRequest.Builder(
                        serviceConfig, // the authorization service configuration
                        CLIENT_ID, // the client ID, typically pre-registered and static
                        ResponseTypeValues.CODE, // the response_type value: we want a code
                        REDIRECT_URI); // the redirect URI to which the auth response is sent

        mAuthState = new AuthState(serviceConfig);

        mAuthRequest = authRequestBuilder
                .setScopes("activity", "sleep")
                .build();
    }

    /**
     * Initialize settings to get call answer.
     * A startActivityForResult call using an Intention returned
     * from the {@link AuthorizationService}.
     */
    public void doAuthorizationCode() {
        mAuthService = new AuthorizationService(mContext);

        mAuthService.performAuthorizationRequest(
                mAuthRequest,
                PendingIntent.getActivity(mContext, REQUEST_LOGIN_FITBIT_SUCCESS,
                        new Intent(mContext, SettingsActivity.class), 0),
                PendingIntent.getActivity(mContext, REQUEST_LOGIN_FITBIT_CANCELED,
                        new Intent(mContext, SettingsActivity.class), 0));
    }

    /**
     * Retrieve access token based on authorization code.
     *
     * @param authResp {@link AuthorizationResponse}
     * @return Single<AuthState>
     */
    public Single<AuthState> doAuthorizationToken(final AuthorizationResponse authResp) {
        final ClientAuthentication clientAuth = new ClientSecretBasic(CLIENT_SECRET);
        if (mAuthService == null) mAuthService = new AuthorizationService(mContext);

        return Single.create(new SingleOnSubscribe<AuthState>() {
            @Override
            public void subscribe(final SingleEmitter<AuthState> emitter) throws Exception {
                mAuthService.performTokenRequest(authResp.createTokenExchangeRequest(),
                        clientAuth, new AuthorizationService.TokenResponseCallback() {
                            @Override
                            public void onTokenRequestCompleted(
                                    @Nullable TokenResponse tokenResponse,
                                    @Nullable AuthorizationException e) {
                                mAuthState.update(tokenResponse, e);

                                if (e != null) {
                                    Log.w(LOG_TAG, "performTokenRequest() error: " + e.getMessage());
                                    emitter.onError(e);
                                    return;
                                }

                                if (AppPreferencesHelper.getInstance(mContext)
                                        .addAuthStateFiBIt(mAuthState))
                                    emitter.onSuccess(mAuthState);
                            }
                        });
            }
        });
    }
}