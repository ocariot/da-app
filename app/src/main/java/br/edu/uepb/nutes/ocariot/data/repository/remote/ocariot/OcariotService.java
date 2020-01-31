package br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot;

import java.util.List;

import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.ChildrenGroup;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.FitBitAppData;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.FitBitSync;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.PhysicalActivity;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Sleep;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Weight;
import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.Body;
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
    // Child
    @POST("/v1/auth")
    Single<UserAccess> authUser(@Body Child user);

    @GET("/v1/children/{child_id}")
    Single<Child> getChildById(@Path("child_id") String childId);

    @GET("/v1/families/{family_id}/children")
    Single<List<Child>> getFamilyChildrenById(@Path("family_id") String familyId);

    @GET("/v1/educators/{educator_id}/children/groups")
    Single<List<ChildrenGroup>> getEducatorGroupsById(@Path("educator_id") String educatorId);

    @GET("/v1/healthprofessionals/{healthprofessional_id}/children/groups")
    Single<List<ChildrenGroup>> getHealthProfessionalGroupsById(@Path("healthprofessional_id") String educatorId);

    // Physical Activity
    @GET("/v1/children/{child_id}/physicalactivities")
    Single<List<PhysicalActivity>> listActivities(
            @Path("child_id") String childId,
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("limit") int limit
    );

    // Sleep
    @GET("/v1/children/{child_id}/sleep")
    Single<List<Sleep>> listSleep(
            @Path("child_id") String childId,
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("/v1/children/{child_id}/weights")
    Single<List<Weight>> listhWeights(
            @Path("child_id") String childId,
            @Query("timestamp") String startDate,
            @Query("timestamp") String endDate,
            @Query("sort") String sort
    );

    // Fitbit
    @GET("/v1/fitbit")
    Single<FitBitAppData> getFitBitAppData();

    @POST("/v1/users/{user_id}/fitbit/auth")
    Completable publishFitBitAuth(
            @Path("user_id") String childId,
            @Body UserAccess userAccess,
            @Query("last_sync") String lastSync
    );

    @POST("/v1/users/{user_id}/fitbit/auth/revoke")
    Completable revokeFitBitAuth(@Path("user_id") String childId);

    @POST("/v1/users/{user_id}/fitbit/sync")
    Single<FitBitSync> fitBitSync(@Path("user_id") String childId);
}
