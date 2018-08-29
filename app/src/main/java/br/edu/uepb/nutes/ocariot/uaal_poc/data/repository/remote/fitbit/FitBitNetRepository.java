package br.edu.uepb.nutes.ocariot.uaal_poc.data.repository.remote.fitbit;

import android.content.Context;

import java.io.IOException;

import br.edu.uepb.nutes.ocariot.uaal_poc.data.model.ActivityList;
import br.edu.uepb.nutes.ocariot.uaal_poc.data.repository.remote.BaseNetRepository;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class FitBitNetRepository extends BaseNetRepository {
    private FitBitService fitBitService;
    private static FitBitNetRepository instance;
//    private static AuthorizationService authService;

//    private AuthState authState;

    private FitBitNetRepository(Context context) {
        super(context, provideInterceptor(), FitBitService.BASE_URL_FITBIT);

        fitBitService = super.retrofit.create(FitBitService.class);
    }

    public static synchronized FitBitNetRepository getInstance(Context context) {
        if (instance == null) instance = new FitBitNetRepository(context);
//        if (authService == null) authService = new AuthorizationService(context);

        return instance;
    }

    public Observable<ActivityList> listActivities(String beforeDate, String afterDate,
                                                   String sort, int offset, int limit) {
        return fitBitService.listActivity(beforeDate, afterDate, sort, offset, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     */
    public void dispose() {
//        if (authService != null) authService.dispose();
    }

    /**
     * Provide intercept with header according to fitbit.
     *
     * @return Interceptor
     */
    private static Interceptor provideInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                final Request.Builder requestBuilder = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Content-type", "application/json")
                        .method(original.method(), original.body());

//                AppPreferencesHelper.getInstance(BaseNetRepository.mContext).getAuthStateFitBit()
//                        .subscribe(new SingleObserver<AuthState>() {
//                            @Override
//                            public void onSubscribe(Disposable d) {
//
//                            }
//
//                            @Override
//                            public void onSuccess(final AuthState authState) {
//                                authState.performActionWithFreshTokens(authService, new AuthState.AuthStateAction() {
//                                    @Override
//                                    public void execute(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException ex) {
//                                        if (accessToken == null) return;
//                                        requestBuilder.header(
//                                                "Authorization",
//                                                authState.getLastTokenResponse()
//                                                        .tokenType
//                                                        .concat(" ")
//                                                        .concat(accessToken)
//                                        );
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                Log.w("Interceptor error", e.getMessage());
//                            }
//                        });
                return chain.proceed(requestBuilder.build());
            }
        };
    }
}
