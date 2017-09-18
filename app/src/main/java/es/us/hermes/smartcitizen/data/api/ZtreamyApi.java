package es.us.hermes.smartcitizen.data.api;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;
import es.us.hermes.smartcitizen.mvp.model.ztreamy.Event;

public interface ZtreamyApi {

    String SERVICE_ENDPOINT = "http://hermes1.gast.it.uc3m.es:8080/";
    String APPLICATION_ID = "SmartCitizen";
    String SYNTAX = "application/json";

    String LIST_KEY_LOCATIONS = "userLocationsList";
    String LIST_KEY_ACTIVITIES = "userActivitiesList";
    String LIST_KEY_STEPS = "userStepsList";
    String LIST_KEY_DISTANCES = "userDistancesList";
    String LIST_KEY_CALORIES_EXPENDED = "userCaloriesExpendedList";
    String LIST_KEY_HEART_RATES = "userHeartRatesList";
    String LIST_KEY_SLEEP = "userSleepList";

    String EVENT_TYPE_PERIODIC_LOCATIONS = "User Locations";
    String EVENT_TYPE_PERIODIC_ACTIVITIES = "User Activities";
    String EVENT_TYPE_PERIODIC_STEPS = "User Steps";
    String EVENT_TYPE_PERIODIC_DISTANCES = "User Distances";
    String EVENT_TYPE_PERIODIC_CALORIES_EXPENDED = "User Calories Expended";
    String EVENT_TYPE_PERIODIC_HEART_RATES = "User Heart Rates";
    String EVENT_TYPE_PERIODIC_SLEEP = "User Sleep";

    String EVENT_TYPE_FULL_LOCATIONS = "Full User Locations";
    String EVENT_TYPE_FULL_ACTIVITIES = "Full User Activities";
    String EVENT_TYPE_FULL_STEPS = "Full User Steps";
    String EVENT_TYPE_FULL_DISTANCES = "Full User Distances";
    String EVENT_TYPE_FULL_CALORIES_EXPENDED = "Full User Calories Expended";
    String EVENT_TYPE_FULL_HEART_RATES = "Full User Heart Rates";

    @Headers("Content-Type: application/json")
    @POST("collector/publish")
    Observable<Response<ResponseBody>> uploadEvent(@Body Event event);

}
