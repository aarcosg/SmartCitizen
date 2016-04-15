package us.idinfor.smartcitizen.data.api.ztreamy;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;
import us.idinfor.smartcitizen.data.api.google.fit.entity.ActivitySegmentFit;
import us.idinfor.smartcitizen.data.api.google.fit.entity.LocationSampleFit;
import us.idinfor.smartcitizen.data.api.hermes.entity.ItemsList;

public interface ZtreamyApi {

    int RESPONSE_OK = 200;

    String APPLICATION_ID = "SmartCitizen";
    String SYNTAX = "application/json";
    String EVENT_TYPE_LOCATIONS = "User Locations";
    String EVENT_TYPE_ACTIVITIES = "User Activities";
    String SERVICE_ENDPOINT = "http://hermes1.gast.it.uc3m.es:9100/";

    @Headers({
            "Syntax: " + SYNTAX,
            "Application-Id: " + APPLICATION_ID,
            "Event-Type: " + EVENT_TYPE_LOCATIONS
    })
    @POST("collector/publish/")
    Observable<Response<Integer>> uploadLocations(
            @Header("Event-Id") String eventId,
            @Header("Source-Id") String sourceId,
            @Header("Timestamp") String timestamp,
            @Body ItemsList<LocationSampleFit> list);


    @Headers({
        "Syntax: " + SYNTAX,
        "Application-Id: " + APPLICATION_ID,
        "Event-Type: " + EVENT_TYPE_ACTIVITIES
    })
    @POST("collector/publish/")
    Observable<Response<Integer>> uploadActivities(
            @Header("Event-Id") String eventId,
            @Header("Source-Id") String sourceId,
            @Header("Timestamp") String timestamp,
            @Body ItemsList<ActivitySegmentFit> list);

}
