package us.idinfor.smartcitizen;


import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.List;

import us.idinfor.smartcitizen.json.JsonContext;
import us.idinfor.smartcitizen.json.JsonContextList;
import us.idinfor.smartcitizen.model.Context;

public class HermesCitizenApi {

    private static final String TAG = HermesCitizenApi.class.getCanonicalName();
    private static final String HOST_URL = "http://10.141.1.53:8080/HermesWeb/webresources/hermes.citizen.";
    private static final String USER_URL = HOST_URL + "person/existsUser/";
    private static final String CONTEXT_URL = HOST_URL + "context/create";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final int RESPONSE_ERROR_UNKNOWN = 0;
    public static final int RESPONSE_OK = 1;
    public static final int RESPONSE_ERROR_USER_NOT_FOUND = 2;
    public static final int RESPONSE_ERROR_DATA_NOT_UPLOADED = 3;

    private static final OkHttpClient client = new OkHttpClient();

    public static Boolean existsUser(String username){
        String responseString = "";
        Request request = new Request.Builder()
                .url(USER_URL + username)
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

    public static Integer sendContexts(List<Context> contexts){
        String responseString = "";
        JsonContextList jsonContextList = new JsonContextList();
        jsonContextList.setUser(contexts.get(0).getUser());
        jsonContextList.setDeviceId(contexts.get(0).getDeviceId());
        List<JsonContext> aux = new ArrayList<>(contexts.size());
        for(us.idinfor.smartcitizen.model.Context c : contexts){
            JsonContext jsonContext = new JsonContext(
                    c.getActivity(),
                    c.getLatitude(),
                    c.getLongitude(),
                    c.getTime().getTime());
            aux.add(jsonContext);
        }
        jsonContextList.setContexts(aux);

        String json = new Gson().toJson(jsonContextList, JsonContextList.class);
        Log.e(TAG,json);

        RequestBody formBody = RequestBody.create(JSON,json);
        Request request = new Request.Builder()
                .url(CONTEXT_URL)
                .post(formBody)
                .build();
        try{
            Response response = client.newCall(request).execute();
            Log.e(TAG,response.toString());
            responseString = response.body().string();
            response.body().close();

        }catch (Exception e){
            Log.e(TAG,"Exception: " + e);
        }
        Integer res = RESPONSE_ERROR_UNKNOWN;
        try{
            return Integer.valueOf(responseString);
        }catch (NumberFormatException e){
            return RESPONSE_ERROR_UNKNOWN;
        }
    }


}
