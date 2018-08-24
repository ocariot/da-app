package br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.fitbit;

import android.content.Context;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationService;

import java.util.List;

import br.edu.uepb.nutes.activity_tracking_poc.data.model.Activities;
import br.edu.uepb.nutes.activity_tracking_poc.data.model.Activity;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.BaseNetRepository;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.Request;

public class FitBitNetRepository extends BaseNetRepository {
    private FitBitService fitBitService;
    private static FitBitNetRepository instance;
    private static AuthorizationService authService;

    private AuthState authState;

    private FitBitNetRepository(Context context, AuthState authState) {
        super(context, provideInterceptor(authState), FitBitService.BASE_URL_FITBIT);
        this.authState = authState;
        authService = new AuthorizationService(context);

        fitBitService = super.retrofit.create(FitBitService.class);
    }

    public static synchronized FitBitNetRepository getInstance(Context context, AuthState authState) {
        if (instance == null) instance = new FitBitNetRepository(context, authState);
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

    public Observable<Activities> listActivities(String beforeDate, String afterDate,
                                                 String sort, int offset, int limit) {
        return fitBitService.listActivity(beforeDate, afterDate, sort, offset, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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
                    .method(original.method(), original.body());

            authState.performActionWithFreshTokens(authService, (accessToken, idToken, ex) -> {
                if (accessToken != null) {
                    requestBuilder.header("Authorization",
                            authState.getLastTokenResponse().tokenType
                                    .concat(" ")
                                    .concat(authState.getAccessToken()));
                }
            });
            Request request = requestBuilder.build();
            return chain.proceed(request);
        };
    }
}
