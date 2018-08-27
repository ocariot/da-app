package br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.fitbit;

import android.content.Context;
import android.util.Log;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationService;

import br.edu.uepb.nutes.activity_tracking_poc.data.model.ActivityList;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.BaseNetRepository;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.Request;

public class FitBitNetRepository extends BaseNetRepository {
    private FitBitService fitBitService;
    private static FitBitNetRepository instance;
    private static AuthorizationService authService;

    private AuthState authState;

    private FitBitNetRepository(Context context) {
        super(context, provideInterceptor(), FitBitService.BASE_URL_FITBIT);

        fitBitService = super.retrofit.create(FitBitService.class);
    }

    public static synchronized FitBitNetRepository getInstance(Context context) {
        if (instance == null) instance = new FitBitNetRepository(context);
        if (authService == null) authService = new AuthorizationService(context);

        return instance;
    }

    public Observable<ActivityList> listActivities(String beforeDate, String afterDate,
                                                   String sort, int offset, int limit) {
        return fitBitService.listActivity(beforeDate, afterDate, sort, offset, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Prevents {@link AuthorizationService} memory leak
     */
    public void dispose() {
        if (authService != null) authService.dispose();
    }

    /**
     * Provide intercept with header according to fitbit.
     *
     * @return Interceptor
     */
    private static Interceptor provideInterceptor() {
        return chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Accept", "application/json")
                    .header("Content-type", "application/json")
                    .method(original.method(), original.body());

            AppPreferencesHelper.getInstance(BaseNetRepository.mContext).getAuthStateFitBit()
                    .subscribe(authState -> {
                        authState.performActionWithFreshTokens(authService, (accessToken, idToken, ex) -> {
                            if(accessToken == null) return;
                            requestBuilder.header(
                                    "Authorization",
                                    authState.getLastTokenResponse()
                                            .tokenType
                                            .concat(" ")
                                            .concat(accessToken)
                            );
                        });
                    }, error -> Log.w("Interceptor error", error.getMessage()));

            Request request = requestBuilder.build();
            return chain.proceed(request);
        };
    }
}
