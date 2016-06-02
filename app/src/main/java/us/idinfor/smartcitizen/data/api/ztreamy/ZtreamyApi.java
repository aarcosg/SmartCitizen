package us.idinfor.smartcitizen.data.api.ztreamy;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;
import us.idinfor.smartcitizen.data.api.ztreamy.entity.Event;

public interface ZtreamyApi {

    String APPLICATION_ID = "SmartCitizen";
    String SYNTAX = "application/json";
    String EVENT_TYPE_PERIODIC_LOCATIONS = "User Locations";
    String EVENT_TYPE_FULL_LOCATIONS = "Full User Locations";
    String EVENT_TYPE_PERIODIC_ACTIVITIES = "User Activities";
    String EVENT_TYPE_FULL_ACTIVITIES = "Full User Activities";
    String LOCATIONS_LIST_KEY = "userLocationsList";
    String ACTIVITIES_LIST_KEY = "userActivitiesList";
    String SERVICE_ENDPOINT = "http://hermes1.gast.it.uc3m.es:9100/";

    @Headers("Content-Type: application/json")
    @POST("collector/publish")
    Observable<Response<ResponseBody>> uploadLocations(@Body Event locationsEvent);

    @Headers("Content-Type: application/json")
    @POST("collector/publish")
    Observable<Response<ResponseBody>> uploadActivities(@Body Event activitiesEvent);

}
