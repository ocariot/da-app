package br.edu.uepb.nutes.ocariot.data.repository.remote.fitbit;

import br.edu.uepb.nutes.ocariot.BuildConfig;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.ActivitiesListFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.CaloriesListFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.MinutesFairlyActiveListFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.MinutesLightlyActiveListFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.MinutesSedentaryListFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.MinutesVeryActiveListFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.SleepListFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.StepsListFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.UserResultFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.WeightListFitBit;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface for FitBit API.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public interface FitBitService {
    String FITBIT_BASE_URL = BuildConfig.FITBIT_BASE_URL;

    @GET("1.2/user/-/activities/list.json")
    Single<ActivitiesListFitBit> listActivities(
            @Query("beforeDate") String beforeDate,
            @Query("afterDate") String afterDate,
            @Query("sort") String sort,
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    @GET("1.2/user/-/sleep/date/{start-date}/{end-date}.json")
    Single<SleepListFitBit> listSleep(
            @Path("start-date") String startDate,
            @Path("end-date") String endDate
    );

    @GET("1/user/-/profile.json")
    Single<UserResultFitBit> getProfile();

    @GET("1.2/user/-/activities/tracker/steps/date/{start-date}/{end-date}.json")
    Single<StepsListFitBit> getSteps(
            @Path("start-date") String startDate,
            @Path("end-date") String endDate
    );

    @GET("1.2/user/-/activities/tracker/calories/date/{start-date}/{end-date}.json")
    Single<CaloriesListFitBit> getCalories(
            @Path("start-date") String startDate,
            @Path("end-date") String endDate
    );

    @GET("1.2/user/-/activities/tracker/minutesSedentary/date/{start-date}/{end-date}.json")
    Single<MinutesSedentaryListFitBit> getSedentaryMinutes(
            @Path("start-date") String startDate,
            @Path("end-date") String endDate
    );

    @GET("1.2/user/-/activities/tracker/minutesLightlyActive/date/{start-date}/{end-date}.json")
    Single<MinutesLightlyActiveListFitBit> getLightlyActiveMinutes(
            @Path("start-date") String startDate,
            @Path("end-date") String endDate
    );

    @GET("1.2/user/-/activities/tracker/minutesFairlyActive/date/{start-date}/{end-date}.json")
    Single<MinutesFairlyActiveListFitBit> getFairlyActiveMinutes(
            @Path("start-date") String startDate,
            @Path("end-date") String endDate
    );

    @GET("1.2/user/-/activities/tracker/minutesVeryActive/date/{start-date}/{end-date}.json")
    Single<MinutesVeryActiveListFitBit> getVeryActiveMinutes(
            @Path("start-date") String startDate,
            @Path("end-date") String endDate
    );

    @GET("1.2/user/-/body/log/weight/date/{start-date}/{end-date}.json")
    Single<WeightListFitBit> listWeights(
            @Path("start-date") String startDate,
            @Path("end-date") String endDate
    );
}
