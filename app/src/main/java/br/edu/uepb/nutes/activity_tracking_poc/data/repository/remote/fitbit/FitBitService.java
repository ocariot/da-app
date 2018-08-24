package br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.fitbit;

import java.util.List;

import br.edu.uepb.nutes.activity_tracking_poc.data.model.Activities;
import br.edu.uepb.nutes.activity_tracking_poc.data.model.Activity;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FitBitService {
    String BASE_URL_FITBIT = "https://api.fitbit.com/1/user/-/";

    /**
     * Retreives a list of user’s activity log entries before or after a given day.
     *
     * @return
     */
    @GET("activities/list.json")
    Observable<Activities> listActivity(
            @Query("beforeDate") String beforeDate,
            @Query("afterDate") String afterDate,
            @Query("sort") String sort,
            @Query("offset ") int offset,
            @Query("limit  ") int limit);
}
