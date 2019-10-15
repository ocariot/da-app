package br.edu.uepb.nutes.ocariot.data.repository.remote.fitbit;

import android.content.Context;
import android.util.Log;

import net.openid.appauth.AuthorizationService;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.edu.uepb.nutes.ocariot.BuildConfig;
import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.ActivityLevelFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.HeartRateZoneFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.LogDataFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.MinutesFairlyActiveListFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.MinutesVeryActiveListFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.PhysicalActivityFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.SleepFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.SleepLevelDataFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.UserFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.UserResultFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.WeightFitBit;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.ActivityLevel;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.HeartRateZone;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.HeartRateZoneItem;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.LogData;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.PhysicalActivity;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Sleep;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.SleepPattern;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.SleepPatternDataSet;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.SleepType;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Weight;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.BaseNetRepository;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import br.edu.uepb.nutes.ocariot.utils.MessageEvent;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Repository to consume the FitBit API.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class FitBitNetRepository extends BaseNetRepository {
    private static FitBitNetRepository mInstance;

    private FitBitService fitBitService;
    private AuthorizationService authService;

    private FitBitNetRepository(Context context) {
        super(context);

        super.addInterceptor(requestInterceptor());
        super.addInterceptor(responseInterceptor());
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.level(HttpLoggingInterceptor.Level.BODY);
            this.addInterceptor(logging);
        }

        fitBitService = super.provideRetrofit(FitBitService.BASE_URL_FITBIT)
                .create(FitBitService.class);
        authService = new AuthorizationService(context);
    }

    public static FitBitNetRepository getInstance(Context context) {
        if (mInstance == null) mInstance = new FitBitNetRepository(context);
        return mInstance;
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
    private Interceptor requestInterceptor() {
        return chain -> {
            Request original = chain.request();
            final Request.Builder requestBuilder = original.newBuilder()
                    .header("Accept", "application/json")
                    .header("Content-type", "application/json")
                    .method(original.method(), original.body());

            UserAccess userAccess = AppPreferencesHelper
                    .getInstance(mContext)
                    .getLastSelectedChild().getFitBitAccess();

            if (userAccess != null && userAccess.getAccessToken() != null) {
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
                        new MessageEvent(MessageEvent.EventType.FITBIT_ACCESS_TOKEN_EXPIRED)
                );
            }
            return response;
        };
    }

    public Single<List<PhysicalActivity>> listActivities(String beforeDate, String afterDate,
                                                         String sort, int offset, int limit) {
        return fitBitService.listActivities(beforeDate, afterDate, sort, offset, limit)
                .map(activitiesListFitBit -> {
                    List<PhysicalActivity> result = new ArrayList<>();
                    // convert to ocariot platform
                    for (PhysicalActivityFitBit activity : activitiesListFitBit.getActivities()) {
                        PhysicalActivity physicalActivity = new PhysicalActivity();
                        physicalActivity.setName(activity.getActivityName());
                        physicalActivity.setDuration(activity.getDuration());
                        physicalActivity.setStartTime(DateUtils.convertDateTimeToUTC(
                                activity.getStartTime()));
                        physicalActivity.setEndTime(DateUtils.convertDateTimeToUTC(
                                DateUtils.addMillisecondsToString(
                                        activity.getStartTime(),
                                        activity.getDuration()
                                ))
                        );
                        physicalActivity.setSteps(activity.getSteps());
                        physicalActivity.setCalories(activity.getCalories());
                        for (ActivityLevelFitBit level : activity.getActivityLevel()) {
                            // In the FitBit API the duration comes in minutes.
                            // The OCARIoT API waits in milliseconds.
                            // Converts the duration in minutes to milliseconds.
                            physicalActivity.addLevel(new ActivityLevel(
                                    level.getName(),
                                    level.getMinutes() * 60000
                            ));
                        }
                        // Heart Rate
                        if (activity.getHeartRateZones() != null) {
                            HeartRateZone heartRateZone = new HeartRateZone();
                            heartRateZone.setAverage(activity.getAverageHeartRate());
                            for (HeartRateZoneFitBit zone : activity.getHeartRateZones()) {
                                HeartRateZoneItem item = new HeartRateZoneItem(zone.getMin(),
                                        zone.getMax(), (zone.getMinutes() * 60000));
                                if (zone.getName().toLowerCase().contains("out")) {
                                    heartRateZone.setOutOfRangeZone(item);
                                } else if (zone.getName().toLowerCase().contains("fat")) {
                                    heartRateZone.setFatBurnZone(item);
                                } else if (zone.getName().toLowerCase().contains("cardio")) {
                                    heartRateZone.setCardioZone(item);
                                } else if (zone.getName().toLowerCase().contains("peak")) {
                                    heartRateZone.setPeakZone(item);
                                }
                            }
                            physicalActivity.setHeartRate(heartRateZone);
                        }
                        if (activity.getDistance() != null) {
                            physicalActivity.setDistance(activity.getDistance() * 1000);
                        }
                        result.add(physicalActivity);
                        Log.w("ACTIVITY_TEST", physicalActivity.toString());
                    }
                    Log.w("ACTIVITY_TEST RESULT", Arrays.toString(result.toArray()) + " SIZE " + result.size());
                    return result;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Sleep>> listSleep(String startDate, String endDate) {
        return fitBitService.listSleep(startDate, endDate)
                .map(sleepListFitBit -> {
                    List<Sleep> result = new ArrayList<>();

                    for (SleepFitBit sleepFitBit : sleepListFitBit.getSleepList()) {
                        Sleep sleep = new Sleep();
                        SleepPattern sleepPattern = new SleepPattern();

                        sleep.setType(sleepFitBit.getType().equalsIgnoreCase(SleepType.STAGES)
                                ? sleepFitBit.getType() : SleepType.CLASSIC);
                        sleep.setStartTime(DateUtils.convertDateTimeToUTC(sleepFitBit.getStartTime()));
                        sleep.setEndTime(DateUtils.convertDateTimeToUTC(sleepFitBit.getEndTime()));

                        sleep.setDuration((DateUtils.convertDateTime(sleep.getEndTime()).getTime() -
                                DateUtils.convertDateTime(sleep.getStartTime()).getTime()));
                        for (SleepLevelDataFitBit dataSet : sleepFitBit.getLevels().getData()) {
                            // In the FitBit API the duration comes in seconds.
                            // The OCARIoT API waits in milliseconds.
                            // Converts the duration in seconds to milliseconds.
                            sleepPattern.addItemDataSet(new SleepPatternDataSet(
                                    DateUtils.convertDateTimeToUTC(dataSet.getDateTime()),
                                    dataSet.getLevel(),
                                    dataSet.getSeconds() * 1000
                            ));
                        }
                        sleep.setPattern(sleepPattern);
                        result.add(sleep);
                    }
                    return result;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<UserFitBit> getProfile() {
        return fitBitService.getProfile()
                .map(UserResultFitBit::getUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<LogData>> getSteps(String startDate, String endDate) {
        return fitBitService.getSteps(startDate, endDate)
                .map(steps -> {
                    List<LogData> result = new ArrayList<>();
                    for (LogDataFitBit log : steps.getSteps()) {
                        result.add(new LogData(log.getDate(), Integer.valueOf(log.getValue())));
                    }
                    return result;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<LogData>> getCalories(String startDate, String endDate) {
        return fitBitService.getCalories(startDate, endDate)
                .map(calories -> {
                    List<LogData> result = new ArrayList<>();
                    for (LogDataFitBit log : calories.getCalories()) {
                        result.add(new LogData(log.getDate(), Integer.valueOf(log.getValue())));
                    }
                    return result;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<LogData>> getSedentaryMinutes(String startDate, String endDate) {
        return fitBitService.getSedentaryMinutes(startDate, endDate)
                .map(minutesSedentary -> {
                    List<LogData> result = new ArrayList<>();
                    for (LogDataFitBit log : minutesSedentary.getMinutesSedentary()) {
                        result.add(new LogData(log.getDate(), Integer.valueOf(log.getValue())));
                    }
                    return result;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<LogData>> getLightlyActiveMinutes(String date, String period) {
        return fitBitService.getLightlyActiveMinutes(date, period)
                .map(minutesLightlyActive -> {
                    List<LogData> result = new ArrayList<>();
                    for (LogDataFitBit log : minutesLightlyActive.getMinutesLightlyActive()) {
                        result.add(new LogData(log.getDate(), Integer.valueOf(log.getValue())));
                    }
                    return result;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<LogData>> getActiveMinutes(String date, String period) {
        return Single.zip(
                fitBitService.getFairlyActiveMinutes(date, period)
                        .map(MinutesFairlyActiveListFitBit::getMinutesFairlyActive)
                        .subscribeOn(Schedulers.io()),
                fitBitService.getVeryActiveMinutes(date, period)
                        .map(MinutesVeryActiveListFitBit::getMinutesVeryActive)
                        .subscribeOn(Schedulers.io()),
                (log1, log2) -> {
                    List<LogData> result = new ArrayList<>();
                    for (int i = 0; i < log1.size(); i++) {
                        result.add(new LogData(
                                        log1.get(i).getDate(),
                                        (Integer.valueOf(log1.get(i).getValue()) +
                                                Integer.valueOf(log2.get(i).getValue()))
                                )
                        );
                    }
                    return result;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Weight>> listWeights(String startDate, String endDate) {
        return fitBitService.listWeights(startDate, endDate)
                .map(weights -> {
                    List<Weight> result = new ArrayList<>();
                    for (WeightFitBit weight : weights.getWeights()) {
                        result.add(
                                new Weight(
                                        DateUtils.convertDateTimeToUTC(weight.getDate()
                                                .concat("T").concat(weight.getTime())),
                                        weight.getValue(),
                                        "kg",
                                        weight.getFat() == 0 ? null : weight.getFat()
                                )
                        );
                    }
                    return result;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
