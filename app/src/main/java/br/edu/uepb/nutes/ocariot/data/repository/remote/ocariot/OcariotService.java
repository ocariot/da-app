package br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot;

import java.util.List;

import br.edu.uepb.nutes.ocariot.data.model.Activity;
import br.edu.uepb.nutes.ocariot.data.model.User;
import br.edu.uepb.nutes.ocariot.data.model.UserAccess;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OcariotService {
    String BASE_URL_OCARIOT = "http://200.129.82.7:8081/"; // API GATEWAY

    // User
    @POST("users/")
    Single<User> signup(@Body User user);

    @POST("users/auth")
    Single<UserAccess> authUser(@Body User user);

    @GET("users/{user_id}")
    Single<User> getUserById(@Path("user_id") String user);

    // Activity
    @GET("users/{user_id}/activities")
    Observable<List<Activity>> getActivities(@Path("user_id") String user);
}
