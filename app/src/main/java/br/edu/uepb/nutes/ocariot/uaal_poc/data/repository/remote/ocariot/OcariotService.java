package br.edu.uepb.nutes.ocariot.uaal_poc.data.repository.remote.ocariot;

import java.util.List;

import br.edu.uepb.nutes.ocariot.uaal_poc.data.model.Activity;
import br.edu.uepb.nutes.ocariot.uaal_poc.data.model.User;
import br.edu.uepb.nutes.ocariot.uaal_poc.data.model.UserAccess;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OcariotService {
    String BASE_URL_OCARIOT = "http://ec2-18-212-183-11.compute-1.amazonaws.com:8080/"; // API GATEWAY

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
