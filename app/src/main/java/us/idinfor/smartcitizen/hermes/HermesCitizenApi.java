package us.idinfor.smartcitizen.hermes;

import java.util.List;

import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;
import us.idinfor.smartcitizen.model.entities.fit.ActivitySegmentFit;
import us.idinfor.smartcitizen.model.entities.fit.LocationSampleFit;

public interface HermesCitizenApi {

    //String SERVICE_ENDPOINT = "http://10.141.0.50:8080/HermesWeb/webresources/hermes.citizen.";
    String SERVICE_ENDPOINT = "https://www.hermescitizen.us.es/HermesWeb/webresources/";

    @GET("hermes.citizen.person/existsUser/{userEmail}")
    Observable<Response<Boolean>> existsUser(@Path("userEmail") String userEmail);

    @POST("hermes.citizen.person/registerUser")
    Observable<Response<Integer>> registerUser(@Field("email") String userEmail, @Field("password") String password);

    @POST("hermes.citizen.context/createRange")
    Observable<Response<Integer>> uploadLocations(@Field("user") String userEmail, @Field("items") List<LocationSampleFit> items);

    @POST("hermes.citizen.context/createRange")
    Observable<Response<Integer>> uploadActivities(@Field("user") String userEmail, @Field("items") List<ActivitySegmentFit> items);

}
