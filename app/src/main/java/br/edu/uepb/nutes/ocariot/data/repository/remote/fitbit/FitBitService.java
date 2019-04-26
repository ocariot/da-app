package br.edu.uepb.nutes.ocariot.data.repository.remote.fitbit;

import br.edu.uepb.nutes.ocariot.data.model.ActivitiesList;
import br.edu.uepb.nutes.ocariot.data.model.Child;
import br.edu.uepb.nutes.ocariot.data.model.SleepList;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.CaloriesLogList;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.MinutesFairlyActiveLogList;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.MinutesVeryActiveLogList;
import br.edu.uepb.nutes.ocariot.data.model.fitbit.StepsLogList;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
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
     * @return Observable<ActivitiesList>
     */
    @GET("1/user/-/activities/list.json")
    Single<ActivitiesList> listActivities(
            @Query("beforeDate") String beforeDate,
            @Query("afterDate") String afterDate,
            @Query("sort") String sort,
            @Query("offset ") int offset,
            @Query("limit  ") int limit
    );

    /**
     * Retrieves a list of user sleep log entries before or after a certain day.
     *
     * @return Observable<SleepList>
     */
    @GET("1.2/user/-/sleep/list.json")
    Single<SleepList> listSleep(
            @Query("beforeDate") String beforeDate,
            @Query("afterDate") String afterDate,
            @Query("sort") String sort,
            @Query("offset ") int offset,
            @Query("limit  ") int limit
    );

    @GET("1/user/-/profile.json")
    Single<Child> getProfile();

    @GET("1/user/-/activities/steps/date/{date}/{period}.json")
    Single<StepsLogList> getStepsLog(@Query("date") String date, @Query("period") String period);

    @GET("1/user/-/activities/calories/date/{date}/{period}.json")
    Single<CaloriesLogList> getCaloriesLog(@Query("date") String date, @Query("period") String period);

    @GET("1/user/-/activities/minutesFairlyActive/date/{date}/{period}.json")
    Single<MinutesFairlyActiveLogList> getMinutesFairlyActiveLog(
            @Query("date") String date,
            @Query("period") String period
    );

    @GET("1/user/-/activities/minutesVeryActive/date/{date}/{period}.json")
    Single<MinutesVeryActiveLogList> getMinutesVeryActiveLog(
            @Query("date") String date,
            @Query("period") String period
    );
}
