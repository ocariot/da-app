package br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot;

import android.content.Context;
import android.util.Log;

import com.auth0.android.jwt.JWT;

import java.util.List;

import br.edu.uepb.nutes.ocariot.data.model.Child;
import br.edu.uepb.nutes.ocariot.data.model.Environment;
import br.edu.uepb.nutes.ocariot.data.model.PhysicalActivity;
import br.edu.uepb.nutes.ocariot.data.model.Sleep;
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

        super.addRequestInterceptor(requestInterceptor());
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
    private Interceptor requestInterceptor() {
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
        return ocariotService.authUser(new Child(username, password))
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

    public Single<Child> getChildById(String childId) {
        return ocariotService.getChildById(childId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<PhysicalActivity>> listActivities(String childId, String sort, int page, int limit) {
        return ocariotService.listActivities(childId, sort, page, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PhysicalActivity> publishActivity(String childId, PhysicalActivity activity) {
        return ocariotService.publishActivity(childId, activity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Sleep>> listSleep(String childId, String sort, int page, int limit) {
        return ocariotService.listSleep(childId, sort, page, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Void> deleteActivity(String childId, String activityId) {
        return ocariotService.deleteActivity(childId, activityId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Sleep> publishSleep(String childId, Sleep sleep) {
        return ocariotService.publishSleep(childId, sleep)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Void> deleteSleep(String childId, String sleepId) {
        return ocariotService.deleteSleep(childId, sleepId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Environment>> listEnvironments(String sort, int page, int limit,
                                                          String institutionId, String room,
                                                          String dateStart, String dateEnd) {
        return ocariotService.listEnvironments(sort, page, limit, institutionId, room, dateStart, dateEnd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
