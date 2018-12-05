package br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot;

import android.content.Context;
import android.util.Log;

import com.auth0.android.jwt.JWT;

import java.util.List;

import br.edu.uepb.nutes.ocariot.data.model.Activity;
import br.edu.uepb.nutes.ocariot.data.model.Environment;
import br.edu.uepb.nutes.ocariot.data.model.Sleep;
import br.edu.uepb.nutes.ocariot.data.model.SleepList;
import br.edu.uepb.nutes.ocariot.data.model.User;
import br.edu.uepb.nutes.ocariot.data.model.UserAccess;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.BaseNetRepository;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * Repository to consume the OCARIoT API.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class OcariotNetRepository extends BaseNetRepository {
    private OcariotService ocariotService;
    private final Context mContext;

    private OcariotNetRepository(Context context) {
        super(context);
        this.mContext = context;

        super.addInterceptor(provideInterceptor());
        ocariotService = super.provideRetrofit(OcariotService.BASE_URL_OCARIOT)
                .create(OcariotService.class);


    }

    public static OcariotNetRepository getInstance(Context context) {
        return new OcariotNetRepository(context);
    }

    /**
     * Provide intercept with header according to OCARioT API Service.
     *
     * @return Interceptor
     */
    private Interceptor provideInterceptor() {
        return chain -> {
            Request request = chain.request();
            final Request.Builder requestBuilder = request.newBuilder()
                    .header("Accept", "application/json")
                    .header("Content-type", "application/json")
                    .method(request.method(), request.body());

            UserAccess userAccess = AppPreferencesHelper
                    .getInstance(mContext)
                    .getUserAccessOcariot();

            if (userAccess != null) {
                requestBuilder.header(
                        "Authorization",
                        "Bearer ".concat(userAccess.getAccessToken())
                );
            }
            Log.w("InterceptorOcariot", requestBuilder.build().headers().toString());
            Log.w("InterceptorOcariot", "| REQUEST: " + requestBuilder.build().method() + " "
                    + requestBuilder.build().url().toString());
            return chain.proceed(requestBuilder.build());
        };
    }

    public Single<UserAccess> auth(String username, String password) {
        return ocariotService.authUser(new User(username, password))
                .map(userAccess -> {
                    if (userAccess != null && userAccess.getAccessToken() != null) {
                        JWT jwt = new JWT(userAccess.getAccessToken());
                        userAccess.setSubject(jwt.getSubject());
                        userAccess.setExpirationDate(jwt.getExpiresAt().getTime());
                        userAccess.setScopes(jwt.getClaim(UserAccess.KEY_SCOPES).asString());
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

    public Observable<List<Activity>> listActivities(String userId, String sort, int page, int limit) {
        return ocariotService.listActivities(userId, sort, page, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Activity> publishActivity(String userId, Activity activity) {
        return ocariotService.publishActivity(userId, activity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Sleep>> listSleep(String userId, String sort, int page, int limit) {
        return ocariotService.listSleep(userId, sort, page, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Sleep> publishSleep(String userId, Sleep sleep) {
        return ocariotService.publishSleep(userId, sleep)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Environment>> listEnvironments(String sort, int page, int limit,
                                                          String school, String room) {
        return ocariotService.listEnvironments(sort, page, limit, school, room)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
