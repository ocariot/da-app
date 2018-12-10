package br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot;

import java.util.List;

import br.edu.uepb.nutes.ocariot.data.model.Activity;
import br.edu.uepb.nutes.ocariot.data.model.Environment;
import br.edu.uepb.nutes.ocariot.data.model.Sleep;
import br.edu.uepb.nutes.ocariot.data.model.SleepList;
import br.edu.uepb.nutes.ocariot.data.model.User;
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
    String BASE_URL_OCARIOT = "http://ocariot.nutes.uepb.edu.br:8080/"; // API GATEWAY
//    String BASE_URL_OCARIOT = "http://192.168.31.113:3000/api/v1/"; // API GATEWAY

    // User
    @POST("users/auth")
    Single<UserAccess> authUser(@Body User user);

    @GET("users/{user_id}")
    Single<User> getUserById(@Path("user_id") String user);

    // Activity
    @GET("users/{user_id}/activities")
    Observable<List<Activity>> listActivities(
            @Path("user_id") String userId,
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @POST("users/{user_id}/activities")
    Observable<Activity> publishActivity(@Path("user_id") String userId, @Body Activity activity);

    @DELETE("users/{user_id}/activities/{activity_id}")
    Observable<Void> deleteActivity(@Path("user_id") String userId, @Path("activity_id") String activityId);

    // Sleep
    @GET("users/{user_id}/sleep")
    Observable<List<Sleep>> listSleep(
            @Path("user_id") String userId,
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @POST("users/{user_id}/sleep")
    Observable<Sleep> publishSleep(@Path("user_id") String userId, @Body Sleep sleep);

    @DELETE("users/{user_id}/sleep/{sleep_id}")
    Observable<Void> deleteSleep(@Path("user_id") String userId, @Path("sleep_id") String sleepId);

    // Environments
    @GET("environments")
    Observable<List<Environment>> listEnvironments(
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("location.school") String school,
            @Query("location.room") String room,
            @Query("timestamp") String dateStart,
            @Query("timestamp") String dateEnd
    );

    @POST("environments")
    Observable<Environment> publishEnvironment(@Body Environment environment);
}
