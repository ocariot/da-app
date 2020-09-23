package br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot;

import com.auth0.android.jwt.JWT;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.ChildrenGroup;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.FitBitAppData;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.FitBitSync;
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
import timber.log.Timber;

/**
 * Repository to consume the OCARIoT API.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class OcariotNetRepository extends BaseNetRepository {
    private static OcariotNetRepository mInstance;

    private OcariotService ocariotService;
    private AppPreferencesHelper appPref;

    private OcariotNetRepository() {
        super();
        super.addInterceptor(requestInterceptor());
        super.addInterceptor(responseInterceptor());

        appPref = AppPreferencesHelper.getInstance();
        ocariotService = super.provideRetrofit(AppPreferencesHelper.getInstance().getOcariotURL())
                .create(OcariotService.class);
    }

    public static synchronized OcariotNetRepository getInstance() {
        if (mInstance == null || AppPreferencesHelper.getInstance().changedOcariotUrl()) {
            mInstance = new OcariotNetRepository();
            AppPreferencesHelper.getInstance().changedOcariotUrl(false);
        }
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

            UserAccess userAccess = appPref.getUserAccessOcariot();
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
                        userAccess.setUserId(jwt.getSubject());
                        userAccess.setExpirationDate(Objects.requireNonNull(jwt.getExpiresAt()).getTime());
                        userAccess.setScope(jwt.getClaim(UserAccess.KEY_SCOPE).asString());
                        userAccess.setSubjectType(jwt.getClaim(UserAccess.KEY_SUB_TYPE).asString());
                    }
                    return userAccess;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Child>> getChildrenOfFamily(String familyId) {
        return ocariotService.getFamilyChildrenById(familyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Child>> getChildrenOfEducator(String educatorId) {
        return ocariotService
                .getEducatorGroupsById(educatorId)
                .map(this::getUniqueChildrenFromGroups)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Child>> getChildrenOfHealthProfessional(String healthprofessionalId) {
        return ocariotService
                .getHealthProfessionalGroupsById(healthprofessionalId)
                .map(this::getUniqueChildrenFromGroups)
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

    public Single<List<PhysicalActivity>> listActivities(String childId, String sort, int page, int limit) {
        return ocariotService.listActivities(childId, sort, page, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Sleep>> listSleep(String childId, String sort, int page, int limit) {
        return ocariotService.listSleep(childId, sort, page, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Weight>> listWeights(String childId, String startDate, String endDate, String sort) {
        return ocariotService.listhWeights(childId, startDate, endDate, sort)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<FitBitAppData> getFitBitAppData() {
        return ocariotService.getFitBitAppData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable publishFitBitAuth(String childId, UserAccess userAccess, String lastSync) {
        return ocariotService
                .publishFitBitAuth(childId, userAccess, lastSync)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable revokeFitBitAuth(String childId) {
        return ocariotService.revokeFitBitAuth(childId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<FitBitSync> fitBitSync(String childId) {
        return ocariotService.fitBitSync(childId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
