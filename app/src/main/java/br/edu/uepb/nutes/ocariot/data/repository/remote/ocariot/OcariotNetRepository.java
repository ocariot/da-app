package br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot;

import android.content.Context;
import android.util.Log;

import com.auth0.android.jwt.JWT;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.ChildrenGroup;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Educator;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Environment;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Family;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.HealthProfessional;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.LogData;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.MultiStatusResult;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.PhysicalActivity;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Sleep;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Weight;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.BaseNetRepository;
import br.edu.uepb.nutes.ocariot.utils.MessageEvent;
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
    private static OcariotNetRepository mInstance;

    private OcariotService ocariotService;

    private OcariotNetRepository(Context context) {
        super(context);
        this.mContext = context;

        super.addInterceptor(requestInterceptor());
        super.addInterceptor(responseInterceptor());
        ocariotService = super.provideRetrofit(OcariotService.BASE_URL_OCARIOT)
                .create(OcariotService.class);
        Log.w("OcariotNetRepo", "CONSTRUCT");
    }

    public static OcariotNetRepository getInstance(Context context) {
        if (mInstance == null) mInstance = new OcariotNetRepository(context);
        return mInstance;
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

//            Log.w("RESPONSEBODY", response.code() + " | " +
//                    Objects.requireNonNull(response.body()).string());

            // access token expired!
            if (response.code() == 401) {
                EventBus.getDefault().post(
                        new MessageEvent(MessageEvent.EventType.OCARIOT_ACCESS_TOKEN_EXPIRED)
                );
            }
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
                        userAccess.setSubjectType(jwt.getClaim(UserAccess.KEY_SUB_TYPE).asString());
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

    public Single<Family> getFamilyById(String familyId) {
        return ocariotService.getFamilyById(familyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Child>> getFamilyChildrenById(String familyId) {
        return ocariotService.getFamilyChildrenById(familyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Educator> getEducatorById(String educatorId) {
        return ocariotService.getEducatorById(educatorId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<ChildrenGroup>> getEducatorGroupsById(String educatorId) {
        return ocariotService.getEducatorGroupsById(educatorId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<HealthProfessional> getHealthProfessionalById(String healthprofessionalId) {
        return ocariotService.getHealthProfessionalById(healthprofessionalId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<ChildrenGroup>> getHealthProfessionalGroupsById(String healthprofessionalId) {
        return ocariotService.getHealthProfessionalGroupsById(healthprofessionalId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Child> updateChild(Child child) {
        return ocariotService.updateChild(child.get_id(), child)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable updateLastSync(String childId, String date) {
        return ocariotService.updateLastSync(childId, date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<PhysicalActivity>> listActivities(String childId, String sort, int page, int limit) {
        return ocariotService.listActivities(childId, sort, page, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<PhysicalActivity> publishPhysicalActivity(PhysicalActivity activity) {
        return ocariotService.publishPhysicalActivity(activity.getChildId(), activity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<MultiStatusResult<PhysicalActivity>> publishPhysicalActivities(String childId,
                                                                                 PhysicalActivity[] activities) {
        return ocariotService.publishPhysicalActivities(childId, activities)
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

    public Single<MultiStatusResult<Sleep>> publishSleep(String childId, Sleep[] sleep) {
        return ocariotService.publishSleep(childId, sleep)
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
                                                      String startDate, String endDate) {
        return ocariotService.listEnvironments(sort, page, limit, institutionId, room, startDate, endDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<MultiStatusResult<LogData>> publishSteps(String childId, LogData[] logData) {
        return ocariotService.publishLog(childId, "steps", logData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<MultiStatusResult<LogData>> publishCalories(String childId, LogData[] logData) {
        return ocariotService.publishLog(childId, "calories", logData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<MultiStatusResult<LogData>> publishActiveMinutes(String childId, LogData[] logData) {
        return ocariotService.publishLog(childId, "active_minutes", logData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<MultiStatusResult<LogData>> publishLightlyActiveMinutes(String childId, LogData[] logData) {
        return ocariotService.publishLog(childId, "lightly_active_minutes", logData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<MultiStatusResult<LogData>> publishSedentaryMinutes(String childId, LogData[] logData) {
        return ocariotService.publishLog(childId, "sedentary_minutes", logData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<MultiStatusResult<Weight>> publishWeights(String childId, Weight[] weights) {
        return ocariotService.publishWeights(childId, weights)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Weight>> listWeights(String childId) {
        return ocariotService.listhWeights(childId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable publishFitBitAuth(String childId, UserAccess userAccess) {
        return ocariotService.publishFitBitAuth(childId, userAccess)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
