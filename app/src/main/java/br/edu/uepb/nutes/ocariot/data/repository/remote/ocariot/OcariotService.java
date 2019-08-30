package br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot;

import java.util.List;

import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.ChildrenGroup;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Educator;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Environment;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Family;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.HealthProfessional;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.LogData;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.MultiStatusResult;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.PhysicalActivity;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Sleep;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Weight;
import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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
    // String BASE_URL_OCARIOT = "https://ocariot.nutes.uepb.edu.br"; // API GATEWAY
//    String BASE_URL_OCARIOT = "https://172.17.0.1"; // API GATEWAY
    String BASE_URL_OCARIOT = "https://192.168.0.105"; // API GATEWAY

    // Child
    @POST("/v1/auth")
    Single<UserAccess> authUser(@Body Child user);

    @GET("/v1/children/{child_id}")
    Single<Child> getChildById(@Path("child_id") String childId);

    @GET("/v1/families/{family_id}")
    Single<Family> getFamilyById(@Path("family_id") String familyId);

    @GET("/v1/families/{family_id}/children")
    Single<List<Child>> getFamilyChildrenById(@Path("family_id") String familyId);

    @GET("/v1/educators/{educator_id}")
    Single<Educator> getEducatorById(@Path("educator_id") String educatorId);

    @GET("/v1/educators/{educator_id}/children/groups")
    Single<List<ChildrenGroup>> getEducatorGroupsById(@Path("educator_id") String educatorId);

    @GET("/v1/healthprofessionals/{healthprofessional_id}")
    Single<HealthProfessional> getHealthProfessionalById(@Path("healthprofessional_id") String educatorId);

    @GET("/v1/healthprofessionals/{healthprofessional_id}/children/groups")
    Single<List<ChildrenGroup>> getHealthProfessionalGroupsById(@Path("healthprofessional_id") String educatorId);

    @PATCH("/v1/children/{child_id}")
    Single<Child> updateChild(@Path("child_id") String childId, @Body Child child);

    @FormUrlEncoded
    @PATCH("/v1/children/{child_id}/")
    Completable updateLastSync(
            @Path("child_id") String childId,
            @Field("last_sync") String dataTime
    );

    // Physical Activity
    @GET("/v1/children/{child_id}/physicalactivities")
    Single<List<PhysicalActivity>> listActivities(
            @Path("child_id") String childId,
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @POST("/v1/children/{child_id}/physicalactivities")
    Single<PhysicalActivity> publishPhysicalActivity(@Path("child_id") String childId,
                                                     @Body PhysicalActivity activity);

    @POST("/v1/children/{child_id}/physicalactivities")
    Single<MultiStatusResult<PhysicalActivity>> publishPhysicalActivities(@Path("child_id") String childId,
                                                                          @Body PhysicalActivity[] activity);

    @DELETE("/v1/children/{child_id}/physicalactivities/{activity_id}")
    Completable deleteActivity(@Path("child_id") String childId, @Path("activity_id") String activityId);

    // Sleep
    @GET("/v1/children/{child_id}/sleep")
    Single<List<Sleep>> listSleep(
            @Path("child_id") String childId,
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @POST("/v1/children/{child_id}/sleep")
    Single<Sleep> publishSleep(@Path("child_id") String childId, @Body Sleep sleep);

    @POST("/v1/children/{child_id}/sleep")
    Single<MultiStatusResult<Sleep>> publishSleep(@Path("child_id") String childId, @Body Sleep[] sleep);

    @DELETE("/v1/children/{child_id}/sleep/{sleep_id}")
    Completable deleteSleep(@Path("child_id") String childId, @Path("sleep_id") String sleepId);

    // Logs
    @POST("/v1/children/{child_id}/logs/{resource}")
    Single<MultiStatusResult<LogData>> publishLog(
            @Path("child_id") String childId,
            @Path("resource") String resource,
            @Body LogData[] logData
    );

    // Weight
    @POST("/v1/children/{child_id}/weights")
    Single<MultiStatusResult<Weight>> publishWeights(@Path("child_id") String childId, @Body Weight[] weights);

    @GET("/v1/children/{child_id}/weights")
    Single<List<Weight>> listhWeights(@Path("child_id") String childId);

    // Environments
    @GET("/v1/environments")
    Single<List<Environment>> listEnvironments(
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("institution_id") String institutionId,
            @Query("location.room") String room,
            @Query("timestamp") String startDate,
            @Query("timestamp") String endDate
    );

    Completable publishFitBitAuth(@Path("child_id") String childId, @Body UserAccess userAccess);
}
