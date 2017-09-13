package es.us.hermes.smartcitizen.data.api;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;
import es.us.hermes.smartcitizen.mvp.model.hermes.ItemsList;
import es.us.hermes.smartcitizen.mvp.model.hermes.User;
import es.us.hermes.smartcitizen.mvp.model.google.fit.ActivitySegmentFit;
import es.us.hermes.smartcitizen.mvp.model.google.fit.LocationSampleFit;

public interface HermesCitizenApi {

    //String SERVICE_ENDPOINT = "http://10.141.0.50:8080/HermesWeb/webresources/hermes.citizen.";
    int RESPONSE_ERROR_UNKNOWN = 0;
    int RESPONSE_OK = 1;
    int RESPONSE_ERROR_USER_NOT_FOUND = 2;
    int RESPONSE_ERROR_DATA_NOT_UPLOADED = 3;
    int RESPONSE_ERROR_USER_EXISTS = 5;
    int RESPONSE_ERROR_USER_NOT_REGISTERED = 6;
    String SERVICE_ENDPOINT = "https://www.hermescitizen.us.es/HermesWeb/webresources/";

    @GET("hermes.citizen.person/existsUser/{userEmail}")
    Observable<Response<Boolean>> existsUser(@Path("userEmail") String userEmail);

    @POST("hermes.citizen.person/registerUser")
    Observable<Response<Integer>> registerUser(@Body User user);

    @POST("hermes.citizen.context/createRange")
    Observable<Response<Integer>> uploadLocations(@Body ItemsList<LocationSampleFit> list);

    @POST("hermes.citizen.context/createRange")
    Observable<Response<Integer>> uploadActivities(@Body ItemsList<ActivitySegmentFit> list);

}
