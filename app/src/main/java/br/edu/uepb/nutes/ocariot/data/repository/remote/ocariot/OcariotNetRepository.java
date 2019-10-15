package br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot;

import android.content.Context;

import com.auth0.android.jwt.JWT;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.ocariot.BuildConfig;
import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.ChildrenGroup;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Environment;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.FitBitAppData;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.FitBitSync;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.LogData;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.MultiStatusResult;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.PhysicalActivity;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Sleep;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Weight;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.BaseNetRepository;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import br.edu.uepb.nutes.ocariot.utils.MessageEvent;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

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
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.level(HttpLoggingInterceptor.Level.BODY);
            this.addInterceptor(logging);
        }

        ocariotService = super.provideRetrofit(OcariotService.BASE_URL_OCARIOT)
                .create(OcariotService.class);
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
            // access token expired!
            if (response.code() == 401) {
                EventBus.getDefault().post(
                        new MessageEvent(MessageEvent.EventType.OCARIOT_ACCESS_TOKEN_EXPIRED)
                );
            }
            return response;
        };
    }

    public Single<UserAccess> auth(String username, String password) {
        return ocariotService.authUser(new Child(username, password))
                .map(userAccess -> {
                    if (userAccess != null && userAccess.getAccessToken() != null) {
                        JWT jwt = new JWT(userAccess.getAccessToken());
                        userAccess.setSubject(jwt.getSubject());
                        userAccess.setExpirationDate(Objects.requireNonNull(jwt.getExpiresAt()).getTime());
                        userAccess.setScope(jwt.getClaim(UserAccess.KEY_SCOPE).asString());
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

    public Single<List<Child>> getChildrenOfFamily(String familyId) {
        return ocariotService.getFamilyChildrenById(familyId)
                .flatMap(this::populateFitBitAuthOfChildren)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Child>> getChildrenOfEducator(String educatorId) {
        return ocariotService
                .getEducatorGroupsById(educatorId)
                .map(this::getUniqueChildrenFromGroups)
                .flatMap(this::populateFitBitAuthOfChildren)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Child>> getChildrenOfHealthProfessional(String healthprofessionalId) {
        return ocariotService
                .getHealthProfessionalGroupsById(healthprofessionalId)
                .map(this::getUniqueChildrenFromGroups)
                .flatMap(this::populateFitBitAuthOfChildren)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private List<Child> getUniqueChildrenFromGroups(List<ChildrenGroup> childrenGroups) {
        List<Child> children = new ArrayList<>();
        for (ChildrenGroup group : childrenGroups) {
            for (Child child : group.getChildren()) {
                if (!children.contains(child)) children.add(child);
            }
        }
        return children;
    }

    private Single<List<Child>> populateFitBitAuthOfChildren(List<Child> children) {
        return Single
                .zip(this.mountFitBitAuthChildrenRequest(children), objects -> objects)
                .map(objects -> {
                    List<Child> result = new ArrayList<>();
                    for (Object o : objects) result.add((Child) o);
                    return result;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private List<Single<Child>> mountFitBitAuthChildrenRequest(List<Child> children) {
        List<Single<Child>> requests = new ArrayList<>();
        for (Child child : children) {
            requests.add(this.getFitBitAuth(child.get_id())
                    .onErrorReturn(throwable -> new UserAccess())
                    .map(userAccess -> {
                        child.setFitBitAccess(userAccess);
                        return child;
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            );
        }
        return requests;
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

    public Single<List<Weight>> listWeights(String childId, String startDate, String endDate) {
        return ocariotService.listhWeights(childId, startDate, endDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<FitBitAppData> getFitBitAppData() {
        return ocariotService.getFitBitAppData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable publishFitBitAuth(String childId, UserAccess userAccess) {
        return ocariotService
                .publishFitBitAuth(childId, userAccess, false, DateUtils.getCurrentDatetimeUTC())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable revokeFitBitAuth(String childId) {
        return ocariotService.revokeFitBitAuth(childId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<UserAccess> getFitBitAuth(String childId) {
        return ocariotService.getFitBitAuth(childId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<FitBitSync> fitBitSync(String childId) {
        return ocariotService.fitBitSync(childId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
