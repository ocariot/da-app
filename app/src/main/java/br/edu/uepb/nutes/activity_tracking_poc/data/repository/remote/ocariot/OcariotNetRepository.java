package br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.ocariot;

import android.content.Context;
import android.util.Log;

import com.auth0.android.jwt.JWT;

import java.util.List;

import br.edu.uepb.nutes.activity_tracking_poc.data.model.Activity;
import br.edu.uepb.nutes.activity_tracking_poc.data.model.User;
import br.edu.uepb.nutes.activity_tracking_poc.data.model.UserAccess;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.BaseNetRepository;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.Request;

public class OcariotNetRepository extends BaseNetRepository {
    private OcariotService ocariotService;
    private static OcariotNetRepository instance;

    private OcariotNetRepository(Context context) {
        super(context, provideInterceptor(), OcariotService.BASE_URL_OCARIOT);

        ocariotService = super.retrofit.create(OcariotService.class);
    }

    public static synchronized OcariotNetRepository getInstance(Context context) {
        if (instance == null) instance = new OcariotNetRepository(context);
        return instance;
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

            AppPreferencesHelper.getInstance(BaseNetRepository.mContext).getUserAccessOcariot()
                    .subscribe(userAccess -> {
                        requestBuilder.header(
                                "Authorization",
                                "Bearer ".concat(userAccess.getAccessToken())
                        );
                    }, error -> Log.w("Interceptor error", error.getMessage()));

            Request request = requestBuilder.build();
            return chain.proceed(request);
        };
    }

    public Single<User> signup(User user) {
        return ocariotService.signup(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<UserAccess> auth(String username, String password) {
        return ocariotService.authUser(new User(username, password))
                .map((UserAccess userAccess) -> {
                    if (userAccess.getAccessToken() != null && !userAccess.getAccessToken().isEmpty()) {
                        JWT jwt = new JWT(userAccess.getAccessToken());
                        userAccess.setSubject(jwt.getSubject());
                        userAccess.setExpirationDate(jwt.getExpiresAt().getTime());
                        userAccess.setExpirationDate(jwt.getExpiresAt().getTime());
                        userAccess.setScopes(jwt.getClaim(UserAccess.ROLES_NAME).asList(String.class));
                    }
                    return userAccess;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<User> getById(String userId) {
        return ocariotService.getUserById(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Activity>> listActivities(String userId) {
        Log.w("TESTANDO", userId);
        return ocariotService.getActivities(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
