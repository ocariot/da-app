package br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot;

import java.util.List;

import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Environment;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.LogData;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.PhysicalActivity;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Sleep;
import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface for OCARIoT API.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public interface OcariotService {
//     String BASE_URL_OCARIOT = "https://ocariot.nutes.uepb.edu.br"; // API GATEWAY
    String BASE_URL_OCARIOT = "https://172.17.0.1"; // API GATEWAY

    // Child
    @POST("/auth")
    Single<UserAccess> authUser(@Body Child user);

    @GET("/users/children/{child_id}")
    Single<Child> getChildById(@Path("child_id") String childId);

    @PATCH("/users/children/{child_id}")
    Single<Child> updateChild(@Path("child_id") String childId, @Body Child child);

    // Activity
    @GET("/users/children/{child_id}/physicalactivities")
    Single<List<PhysicalActivity>> listActivities(
            @Path("child_id") String childId,
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @POST("/users/children/{child_id}/physicalactivities")
    Single<PhysicalActivity> publishActivity(@Path("child_id") String childId,
                                             @Body PhysicalActivity activity);

    @POST("/users/children/{child_id}/physicalactivities/logs/{resource}")
    Single<List<Object>> publishActivityLog(
            @Path("child_id") String childId,
            @Path("resource") String resource,
            @Body List<LogData> logData
    );

    @DELETE("/users/children/{child_id}/physicalactivities/{activity_id}")
    Completable deleteActivity(@Path("child_id") String childId, @Path("activity_id") String activityId);

    // Sleep
    @GET("/users/children/{child_id}/sleep")
    Single<List<Sleep>> listSleep(
            @Path("child_id") String childId,
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @POST("/users/children/{child_id}/sleep")
    Single<Sleep> publishSleep(@Path("child_id") String childId, @Body Sleep sleep);

    @DELETE("/users/children/{child_id}/sleep/{sleep_id}")
    Completable deleteSleep(@Path("child_id") String childId, @Path("sleep_id") String sleepId);

    // Environments
    @GET("/environments")
    Single<List<Environment>> listEnvironments(
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("institution_id") String institutionId,
            @Query("location.room") String room,
            @Query("timestamp") String dateStart,
            @Query("timestamp") String dateEnd
    );
}
