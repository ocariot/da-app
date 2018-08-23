package br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.fitbit;

import android.content.Context;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;

import java.io.IOException;

import br.edu.uepb.nutes.activity_tracking_poc.data.model.Activity;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.BaseNetRepository;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.ocariot.OcariotService;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UserFitBitNetRepository extends BaseNetRepository {
    private FitBitService fitBitService;
    private static UserFitBitNetRepository instance;
    private AuthState authState;
    private AuthorizationService authService;

    private UserFitBitNetRepository(Context context, AuthState authState) {
        super(context, provideInterceptor(authState), OcariotService.BASE_URL_OCARIOT);
        this.authState = authState;
        this.authService = new AuthorizationService(context);

        fitBitService = super.retrofit.create(FitBitService.class);
    }

    public static synchronized UserFitBitNetRepository getInstance(Context context, AuthState authState) {
        if (instance == null) instance = new UserFitBitNetRepository(context, authState);
        return instance;
    }

    public Single<String> performActionWithRefreshTokens() {
        return Single.create(emitter -> {
            authState.performActionWithFreshTokens(authService, (accessToken, idToken, ex) -> {
                if (ex != null) {
                    // negotiation for fresh tokens failed, check ex for more details
                    emitter.onError(ex);
                    return;
                }
                emitter.onSuccess(accessToken);
            });
        });
    }

    public Observable<Activity> listActivities() {

    }

    /**
     * Provide intercept with header according to fitbit.
     *
     * @param authState {@link AuthState}
     * @return Interceptor
     */
    private static Interceptor provideInterceptor(AuthState authState) {
        return chain -> {
            Request original = chain.request();

            Request.Builder requestBuilder = original.newBuilder()
                    .header("Accept", "application/json")
                    .header("Content-type", "application/json")
                    .header("Authorization",
                            authState.getLastTokenResponse().tokenType
                                    .concat(" ")
                                    .concat(authState.getAccessToken()))
                    .method(original.method(), original.body());

            Request request = requestBuilder.build();
            return chain.proceed(request);
        };
    }
}
