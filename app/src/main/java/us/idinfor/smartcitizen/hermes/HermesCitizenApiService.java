package us.idinfor.smartcitizen.hermes;

import java.util.List;

import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;
import us.idinfor.smartcitizen.model.fit.ActivitySegmentFit;
import us.idinfor.smartcitizen.model.fit.LocationSampleFit;

public interface HermesCitizenApiService {

    //String SERVICE_ENDPOINT = "http://10.141.0.50:8080/HermesWeb/webresources/hermes.citizen.";
    String SERVICE_ENDPOINT = "https://www.hermescitizen.us.es/HermesWeb/webresources/hermes.citizen.";

    @GET("person/existsUser/{userEmail}")
    Observable<Response<Boolean>> existsUser(@Path("userEmail") String userEmail);

    @POST("person/registerUser")
    Observable<Response<Integer>> registerUser(@Field("email") String userEmail, @Field("password") String password);

    @POST("context/createRange")
    Observable<Response<Integer>> uploadLocations(@Field("user") String userEmail, List<LocationSampleFit> items);

    @POST("context/createRange")
    Observable<Response<Integer>> uploadActivities(@Field("user") String userEmail, List<ActivitySegmentFit> items);

}
