package br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot;

import java.util.List;

import br.edu.uepb.nutes.ocariot.data.model.Activity;
import br.edu.uepb.nutes.ocariot.data.model.User;
import br.edu.uepb.nutes.ocariot.data.model.UserAccess;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Interface for OCARIoT API.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public interface OcariotService {
//    String BASE_URL_OCARIOT = "http://200.129.82.7:8081/"; // API GATEWAY
    String BASE_URL_OCARIOT = "http://192.168.31.113:3000/api/v1/"; // ACCOUNT-SERVICE

    // User
    @POST("users/auth")
    Single<UserAccess> authUser(@Body User user);

    @GET("users/{user_id}")
    Single<User> getUserById(@Path("user_id") String user);

    // Activity
    @GET("users/{user_id}/activities?sort=-start_time")
    Observable<List<Activity>> getActivities(@Path("user_id") String userId);

    @POST("users/{user_id}/activities")
    Observable<Activity> publishActivity(@Path("user_id") String userId, @Body Activity activity);

    // Sleep
//    @GET("users/{user_id}/sleep?sort=-start_time")
//    Observable<List<Activity>> getSleep(@Path("user_id") String userId);
}
