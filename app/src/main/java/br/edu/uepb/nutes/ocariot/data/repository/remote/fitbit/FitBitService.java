package br.edu.uepb.nutes.ocariot.data.repository.remote.fitbit;

import br.edu.uepb.nutes.ocariot.data.model.fitbit.ActivitiesListFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.SleepListFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.CaloriesLogListFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.MinutesFairlyActiveLogListFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.MinutesVeryActiveLogListFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.StepsLogListFitBit;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.UserFitBit;
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
    String BASE_URL_FITBIT = "https://api.fitbit.com/";

    /**
     * Retrieves a list of user’s activity log entries before or after a given day.
     *
     * @return Observable<ActivitiesListFitBit>
     */
    @GET("1.2/user/-/activities/list.json")
    Single<ActivitiesListFitBit> listActivities(
            @Query("beforeDate") String beforeDate,
            @Query("afterDate") String afterDate,
            @Query("sort") String sort,
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    /**
     * Retrieves a list of user sleep log entries before or after a certain day.
     *
     * @return Observable<SleepListFitBit>
     */
    @GET("1.2/user/-/sleep/list.json")
    Single<SleepListFitBit> listSleep(
            @Query("beforeDate") String beforeDate,
            @Query("afterDate") String afterDate,
            @Query("sort") String sort,
            @Query("offset ") int offset,
            @Query("limit  ") int limit
    );

    @GET("1/user/-/profile.json")
    Single<UserFitBit> getProfile();

    @GET("1.2/user/-/activities/steps/date/{date}/{period}.json")
    Single<StepsLogListFitBit> getStepsLog(@Path("date") String date, @Path("period") String period);

    @GET("1.2/user/-/activities/calories/date/{date}/{period}.json")
    Single<CaloriesLogListFitBit> getCaloriesLog(@Path("date") String date, @Path("period") String period);

    @GET("1.2/user/-/activities/minutesFairlyActive/date/{date}/{period}.json")
    Single<MinutesFairlyActiveLogListFitBit> getMinutesFairlyActiveLog(
            @Path("date") String date,
            @Path("period") String period
    );

    @GET("1.2/user/-/activities/minutesVeryActive/date/{date}/{period}.json")
    Single<MinutesVeryActiveLogListFitBit> getMinutesVeryActiveLog(
            @Path("date") String date,
            @Path("period") String period
    );
}
