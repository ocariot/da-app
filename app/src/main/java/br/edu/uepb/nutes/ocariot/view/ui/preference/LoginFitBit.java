package br.edu.uepb.nutes.ocariot.view.ui.preference;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.auth0.android.jwt.JWT;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.ResponseTypeValues;

import java.util.Objects;

import br.edu.uepb.nutes.ocariot.BuildConfig;
import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import io.reactivex.Single;

/**
 * LoginFitBit implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class LoginFitBit {
    private final int REQUEST_LOGIN_FITBIT_SUCCESS = 2;
    private final int REQUEST_LOGIN_FITBIT_CANCELED = 3;

    private final Uri AUTHORIZATION_ENDPOINT = Uri.parse(BuildConfig.FITBIT_AUTH_URL);
    private final Uri TOKEN_ENDPOINT = Uri.parse(BuildConfig.FITBIT_TOKEN_URL);

    private final Uri REDIRECT_URI = Uri.parse("fitbitauth://finished");

    private Context mContext;

    private AuthState mAuthState;
    private AuthorizationService mAuthService;
    private AuthorizationRequest mAuthRequest;
    private AppPreferencesHelper appPref;
    private String CLIENT_SECRET;

    public LoginFitBit(Context context) {
        this.mContext = context;
        appPref = AppPreferencesHelper.getInstance();

        initConfig();
    }

    /**
     * Initialize settings to obtain authorization code.
     */
    private void initConfig() {
        String CLIENT_ID = appPref.getFitbitAppData().getClientId();
        CLIENT_SECRET = appPref.getFitbitAppData().getClientSecret();

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

        mAuthRequest = authRequestBuilder
                .setScopes("activity", "sleep", "heartrate", "weight")
                .setPrompt("login consent")
                .build();
    }

    /**
     * Initialize settings to get call answer.
     * A startActivityForResult call using an Intention returned
     * from the {@link AuthorizationService}.
     */
    public void doAuthorizationCode() {
        mAuthService = new AuthorizationService(mContext);

        Intent intent = new Intent(mContext, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mAuthService.performAuthorizationRequest(
                mAuthRequest,
                PendingIntent.getActivity(mContext.getApplicationContext(),
                        REQUEST_LOGIN_FITBIT_SUCCESS, intent, 0),
                PendingIntent.getActivity(mContext.getApplicationContext(),
                        REQUEST_LOGIN_FITBIT_CANCELED, intent, 0)
        );
    }

    /**
     * Retrieve access token based on authorization code.
     *
     * @param authResp {@link AuthorizationResponse}
     * @return Single<UserAccess>
     */
    Single<UserAccess> doAuthorizationToken(final AuthorizationResponse authResp) {
        final ClientAuthentication clientAuth = new ClientSecretBasic(CLIENT_SECRET);
        if (mAuthService == null) mAuthService = new AuthorizationService(mContext);

        return Single.create(emitter -> mAuthService.performTokenRequest(
                authResp.createTokenExchangeRequest(),
                clientAuth, (tokenResponse, e) -> {
                    mAuthState.update(tokenResponse, e);
                    if (e != null) {
                        emitter.onError(e);
                        return;
                    }

                    UserAccess userAccess = new UserAccess();
                    JWT jwt = new JWT(Objects.requireNonNull(mAuthState.getAccessToken()));

                    userAccess.setSubject(jwt.getSubject());
                    userAccess.setAccessToken(mAuthState.getAccessToken());
                    userAccess.setRefreshToken(mAuthState.getRefreshToken());
                    userAccess.setExpirationDate(Objects.requireNonNull(jwt.getExpiresAt()).getTime());
                    userAccess.setScope(jwt.getClaim(UserAccess.KEY_SCOPES).asString());
                    userAccess.setSubjectType(jwt.getClaim(UserAccess.KEY_SUB_TYPE).asString());
                    userAccess.setStatus(UserAccess.TokenStatus.VALID);

                    emitter.onSuccess(userAccess);
                }));
    }
}