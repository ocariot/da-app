package br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot;

import android.content.Context;
import android.util.Log;

import com.auth0.android.jwt.JWT;

import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Environment;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.LogData;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.PhysicalActivity;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Sleep;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.BaseNetRepository;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

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

        super.addInterceptor(requestInterceptor());
        super.addInterceptor(responseInterceptor());
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

    /**
     * Provide intercept with to request response.
     *
     * @return Interceptor
     */
    private Interceptor responseInterceptor() {
        return chain -> {
            Response response = chain.proceed(chain.request());

            Log.w("RESPONSEBODY", response.code() + " | " + Objects.requireNonNull(response.body()).string());
            return chain.proceed(chain.request());
        };
    }

    public Single<UserAccess> auth(String username, String password) {
        return ocariotService.authUser(new Child(username, password))
                .map(userAccess -> {
                    if (userAccess != null && userAccess.getAccessToken() != null) {
                        JWT jwt = new JWT(userAccess.getAccessToken());
                        userAccess.setSubject(jwt.getSubject());
                        userAccess.setExpirationDate(Objects.requireNonNull(jwt.getExpiresAt()).getTime());
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

    public Single<Child> updateChild(Child child) {
        return ocariotService.updateChild(child.get_id(), child)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<PhysicalActivity>> listActivities(String childId, String sort, int page, int limit) {
        return ocariotService.listActivities(childId, sort, page, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<PhysicalActivity> publishActivity(PhysicalActivity activity) {
        return ocariotService.publishActivity(activity.getChildId(), activity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Object>> publishActivityStepsLog(String childId, List<LogData> logData) {
        return ocariotService.publishActivityLog(childId, "steps", logData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Object>> publishActivityCaloriesLog(String childId, List<LogData> logData) {
        return ocariotService.publishActivityLog(childId, "calories", logData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Object>> publishActivityActiveMinutesLog(String childId, List<LogData> logData) {
        return ocariotService.publishActivityLog(childId, "active_minutes", logData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Sleep>> listSleep(String childId, String sort, int page, int limit) {
        return ocariotService.listSleep(childId, sort, page, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteActivity(String childId, String activityId) {
        return ocariotService.deleteActivity(childId, activityId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Sleep> publishSleep(Sleep sleep) {
        return ocariotService.publishSleep(sleep.getChildId(), sleep)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteSleep(String childId, String sleepId) {
        return ocariotService.deleteSleep(childId, sleepId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Environment>> listEnvironments(String sort, int page, int limit,
                                                      String institutionId, String room,
                                                      String dateStart, String dateEnd) {
        return ocariotService.listEnvironments(sort, page, limit, institutionId, room, dateStart, dateEnd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
