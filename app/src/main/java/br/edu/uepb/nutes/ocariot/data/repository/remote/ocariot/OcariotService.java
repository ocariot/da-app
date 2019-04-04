package br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot;

import java.util.List;

import br.edu.uepb.nutes.ocariot.data.model.Activity;
import br.edu.uepb.nutes.ocariot.data.model.Child;
import br.edu.uepb.nutes.ocariot.data.model.Environment;
import br.edu.uepb.nutes.ocariot.data.model.PhysicalActivity;
import br.edu.uepb.nutes.ocariot.data.model.Sleep;
import br.edu.uepb.nutes.ocariot.data.model.UserAccess;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface for OCARIoT API.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public interface OcariotService {
    String BASE_URL_OCARIOT = "https://ocariot.nutes.uepb.edu.br"; // API GATEWAY

    // Child
    @POST("/auth")
    Single<UserAccess> authUser(@Body Child user);

    @GET("/users/children/{child_id}")
    Single<Child> getChildById(@Path("child_id") String user);

    // Activity
    @GET("/users/children/{child_id}/physicalactivities")
    Observable<List<PhysicalActivity>> listActivities(
            @Path("child_id") String userId,
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @POST("/users/children/{child_id}/physicalactivities")
    Observable<PhysicalActivity> publishActivity(@Path("child_id") String userId, @Body Activity activity);

    @DELETE("/users/children/{child_id}/physicalactivities/{activity_id}")
    Observable<Void> deleteActivity(@Path("child_id") String userId, @Path("activity_id") String activityId);

    // Sleep
    @GET("/users/children/{child_id}/sleep")
    Observable<List<Sleep>> listSleep(
            @Path("child_id") String childId,
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @POST("/users/children/{child_id}/sleep")
    Observable<Sleep> publishSleep(@Path("child_id") String userId, @Body Sleep sleep);

    @DELETE("/users/children/{child_id}/sleep/{sleep_id}")
    Observable<Void> deleteSleep(@Path("child_id") String userId, @Path("sleep_id") String sleepId);

    // Environments
    @GET("/environments")
    Observable<List<Environment>> listEnvironments(
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("institution_id") String institutionId,
            @Query("location.room") String room,
            @Query("timestamp") String dateStart,
            @Query("timestamp") String dateEnd
    );
}
