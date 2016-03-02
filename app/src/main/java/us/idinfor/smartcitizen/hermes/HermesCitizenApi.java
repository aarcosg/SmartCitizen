package us.idinfor.smartcitizen.hermes;


import android.util.Log;

import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import us.idinfor.smartcitizen.json.JsonListHermes;
import us.idinfor.smartcitizen.model.fit.ActivitySegmentFit;
import us.idinfor.smartcitizen.model.fit.LocationSampleFit;

public class HermesCitizenApi {

    private static final String TAG = HermesCitizenApi.class.getCanonicalName();
    //private static final String HOST_URL = "http://10.141.0.50:8080/HermesWeb/webresources/hermes.citizen.";
    private static final String HOST_URL = "https://www.hermescitizen.us.es/HermesWeb/webresources/hermes.citizen.";
    private static final String USER_ENDPOINT = HOST_URL + "person/existsUser/";
    private static final String CONTEXT_RANGE_ENDPOINT = HOST_URL + "context/createRange";
    private static final String REGISTER_USER_ENDPOINT = HOST_URL + "person/registerUser";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final int RESPONSE_ERROR_UNKNOWN = 0;
    public static final int RESPONSE_OK = 1;
    public static final int RESPONSE_ERROR_USER_NOT_FOUND = 2;
    public static final int RESPONSE_ERROR_DATA_NOT_UPLOADED = 3;
    public static final int RESPONSE_ERROR_USER_EXISTS = 5;
    public static final int RESPONSE_ERROR_USER_NOT_REGISTERED = 6;

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();

    public static Boolean existsUser(String username){
        String responseString = "";
        Request request = new Request.Builder()
                .url(USER_ENDPOINT + username)
                .build();
        try{
            Response response = client.newCall(request).execute();
            responseString = response.body().string();
            response.body().close();

        }catch (Exception e){
            Log.e(TAG,"Exception: " + e);
        }
        return Boolean.valueOf(responseString);
    }


    public static Integer uploadLocations(String username, List<LocationSampleFit> locations) {
        Log.i(TAG,"@uploadLocations");
        String responseString = "";

        JsonListHermes<LocationSampleFit> jsonList = new JsonListHermes<LocationSampleFit>(username,locations);
        String json = new Gson().toJson(jsonList, JsonListHermes.class);

        RequestBody formBody = RequestBody.create(JSON,json);
        Request request = new Request.Builder()
                .url(CONTEXT_RANGE_ENDPOINT)
                .post(formBody)
                .build();
        try{
            Response response = client.newCall(request).execute();
            Log.i(TAG,response.toString());
            responseString = response.body().string();
            response.body().close();
        }catch (Exception e){
            Log.e(TAG,"Exception: " + e);
        }

        try{
            return Integer.valueOf(responseString);
        }catch (NumberFormatException e){
            return RESPONSE_ERROR_UNKNOWN;
        }

    }

    public static Integer uploadActivities(String username, List<ActivitySegmentFit> activities) {
        Log.i(TAG,"@uploadActivities");
        String responseString = "";

        JsonListHermes<ActivitySegmentFit> jsonList = new JsonListHermes<ActivitySegmentFit>(username,activities);
        String json = new Gson().toJson(jsonList, JsonListHermes.class);

        RequestBody formBody = RequestBody.create(JSON,json);
        Request request = new Request.Builder()
                .url(CONTEXT_RANGE_ENDPOINT)
                .post(formBody)
                .build();
        try{
            Response response = client.newCall(request).execute();
            Log.i(TAG,response.toString());
            responseString = response.body().string();
            response.body().close();
        }catch (Exception e){
            Log.e(TAG,"Exception: " + e);
        }

        try{
            return Integer.valueOf(responseString);
        }catch (NumberFormatException e){
            return RESPONSE_ERROR_UNKNOWN;
        }
    }

    public static Integer registerUser(String email, String password) {
        Log.i(TAG,"@registerUser");
        String responseString = "";

        String json = "{\"email\" : \"" + email +"\" , \"password\" : \"" + password + "\"}";

        RequestBody formBody = RequestBody.create(JSON,json);
        Request request = new Request.Builder()
                .url(REGISTER_USER_ENDPOINT)
                .post(formBody)
                .build();
        try{
            Response response = client.newCall(request).execute();
            Log.i(TAG,response.toString());
            responseString = response.body().string();
            response.body().close();
        }catch (Exception e){
            Log.e(TAG,"Exception: " + e);
        }

        try{
            return Integer.valueOf(responseString);
        }catch (NumberFormatException e){
            return RESPONSE_ERROR_UNKNOWN;
        }
    }
}
