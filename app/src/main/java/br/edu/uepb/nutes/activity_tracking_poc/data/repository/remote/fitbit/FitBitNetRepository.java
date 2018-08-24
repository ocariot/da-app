package br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.fitbit;

import android.content.Context;
import android.util.Log;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationService;

import br.edu.uepb.nutes.activity_tracking_poc.data.model.ActivityList;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.local.pref.AppPreferencesHelper;
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
    private static Context context;

    private AuthState authState;

    private FitBitNetRepository(Context context) {
        super(context, provideInterceptor(), FitBitService.BASE_URL_FITBIT);

        authService = new AuthorizationService(context);
        fitBitService = super.retrofit.create(FitBitService.class);
    }

    public static synchronized FitBitNetRepository getInstance(Context c) {
        if (instance == null) instance = new FitBitNetRepository(c);
        context = c;
        return instance;
    }

    public Observable<ActivityList> listActivities(String beforeDate, String afterDate,
                                                   String sort, int offset, int limit) {
        return fitBitService.listActivity(beforeDate, afterDate, sort, offset, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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

            AppPreferencesHelper.getInstance(context)
                    .getAuthStateFitBit().subscribe(authState -> {
                authState.performActionWithFreshTokens(authService, (accessToken, idToken, ex) -> {
                    if (accessToken != null) {
                        requestBuilder.header("Authorization",
                                authState.getLastTokenResponse().tokenType
                                        .concat(" ")
                                        .concat(accessToken));
                    } else if (ex != null) {
                        Log.w("TOKEN ERROR", ex.toJsonString());
                    }
                });
            }, error -> Log.w("Interceptor", error.getMessage()));

            Request request = requestBuilder.build();
            Log.w("Interceptor", request.toString());
            return chain.proceed(request);
        };
    }
}
