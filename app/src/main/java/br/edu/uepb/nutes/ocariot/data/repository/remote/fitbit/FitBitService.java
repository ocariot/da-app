package br.edu.uepb.nutes.ocariot.data.repository.remote.fitbit;

import br.edu.uepb.nutes.ocariot.data.model.ActivitiesList;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface for FitBit API.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public interface FitBitService {
    String BASE_URL_FITBIT = "https://api.fitbit.com/1/user/-/";

    /**
     * Retreives a list of user’s activity log entries before or after a given day.
     *
     * @return
     */
    @GET("activities/list.json")
    Observable<ActivitiesList> listActivity(
            @Query("beforeDate") String beforeDate,
            @Query("afterDate") String afterDate,
            @Query("sort") String sort,
            @Query("offset ") int offset,
            @Query("limit  ") int limit);
}
