package br.edu.uepb.nutes.ocariot.data.repository.remote.fitbit;

import android.content.Context;
import android.util.Log;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.ocariot.data.model.fitbit.ActivityLevelFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.LogDataFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.MinutesFairlyActiveLogListFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.MinutesVeryActiveLogListFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.PhysicalActivityFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.SleepFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.SleepLevelDataFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.UserFitBit;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.ActivityLevel;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.LogData;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.PhysicalActivity;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Sleep;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.SleepPattern;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.SleepPatternDataSet;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.BaseNetRepository;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

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

        super.addInterceptor(requestInterceptor());
        super.addInterceptor(responseInterceptor());
        fitBitService = super.provideRetrofit(FitBitService.BASE_URL_FITBIT)
                .create(FitBitService.class);
        authService = new AuthorizationService(context);
    }

    public static FitBitNetRepository getInstance(Context context) {
        return new FitBitNetRepository(context);
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
            Log.w("InterceptorFitBit", "| REQUEST: " + requestBuilder.build().method() + " "
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
                        result.add(physicalActivity);
                    }
                    return result;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Sleep>> listSleep(String beforeDate, String afterDate,
                                         String sort, int offset, int limit) {
        return fitBitService.listSleep(beforeDate, afterDate, sort, offset, limit)
                .map(sleepListFitBit -> {
                    List<Sleep> result = new ArrayList<>();

                    for (SleepFitBit sleepFitBit : sleepListFitBit.getSleepList()) {
                        Sleep sleep = new Sleep();
                        SleepPattern sleepPattern = new SleepPattern();

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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<LogData>> getStepsLog(String date, String period) {
        return fitBitService.getStepsLog(date, period)
                .map(stepsLogList -> {
                    List<LogData> result = new ArrayList<>();
                    for (LogDataFitBit log : stepsLogList.getSteps()) {
                        result.add(new LogData(log.getDate(), Integer.valueOf(log.getValue())));
                    }
                    return result;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<LogData>> getCaloriesLog(String date, String period) {
        return fitBitService.getCaloriesLog(date, period)
                .map(caloriesLogList -> {
                    List<LogData> result = new ArrayList<>();
                    for (LogDataFitBit log : caloriesLogList.getCalories()) {
                        result.add(new LogData(log.getDate(), Integer.valueOf(log.getValue())));
                    }
                    return result;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<LogData>> getMinutesActive(String date, String period) {
        return Single.zip(
                fitBitService.getMinutesFairlyActiveLog(date, period)
                        .map(MinutesFairlyActiveLogListFitBit::getMinutesFairlyActive)
                        .subscribeOn(Schedulers.io()),
                fitBitService.getMinutesVeryActiveLog(date, period)
                        .map(MinutesVeryActiveLogListFitBit::getMinutesVeryActive)
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
}
