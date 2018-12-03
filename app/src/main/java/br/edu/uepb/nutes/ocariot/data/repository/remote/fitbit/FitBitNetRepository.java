package br.edu.uepb.nutes.ocariot.data.repository.remote.fitbit;

import android.content.Context;
import android.util.Log;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationService;

import br.edu.uepb.nutes.ocariot.data.model.ActivitiesList;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.BaseNetRepository;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * Repository to consume the FitBit API.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class FitBitNetRepository extends BaseNetRepository {
    private FitBitService fitBitService;
    private AuthorizationService authService;

    private FitBitNetRepository(Context context) {
        super(context);

        super.addInterceptor(provideInterceptor());
        fitBitService = super.provideRetrofit(FitBitService.BASE_URL_FITBIT)
                .create(FitBitService.class);
        authService = new AuthorizationService(context);
    }

    public static FitBitNetRepository getInstance(Context context) {
        return new FitBitNetRepository(context);
    }

    public Observable<ActivitiesList> listActivities(String beforeDate, String afterDate,
                                                     String sort, int offset, int limit) {
        return fitBitService.listActivity(beforeDate, afterDate, sort, offset, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Dispose AuthorizationService oAuth2 service.
     */
    public void dispose() {
        if (authService != null) authService.dispose();
    }

    /**
     * Provide intercept with header according to fitbit.
     *
     * @return Interceptor
     */
    private Interceptor provideInterceptor() {
        return chain -> {
            Request original = chain.request();
            final Request.Builder requestBuilder = original.newBuilder()
                    .header("Accept", "application/json")
                    .header("Content-type", "application/json")
                    .method(original.method(), original.body());

            final AuthState authState = AppPreferencesHelper.getInstance(this.mContext)
                    .getAuthStateFitBit();

            authState.performActionWithFreshTokens(authService, (accessToken, idToken, exception) -> {
                if (accessToken != null) {
                    requestBuilder.header(
                            "Authorization",
                            "Bearer ".concat(accessToken)
                    );
                }
            });
            Log.w("InterceptorFitBit", requestBuilder.build().headers().toString());
            return chain.proceed(requestBuilder.build());
        };
    }
}
