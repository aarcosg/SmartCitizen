package us.idinfor.smartcitizen;


import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.lang.reflect.Type;
import java.util.List;

import us.idinfor.smartcitizen.model.Context;

public class HermesCitizenApi {

    private static final String TAG = HermesCitizenApi.class.getCanonicalName();
    private static final String HOST_URL = "";
    private static final String USER_URL = HOST_URL + "";
    private static final String CONTEXT_URL = HOST_URL + "";
    private static final OkHttpClient client = new OkHttpClient();

    public static Boolean existsUser(String username){
        String responseString = "";
        RequestBody formBody = new FormEncodingBuilder()
                .add("email",username)
                .build();
        Request request = new Request.Builder()
                .url(USER_URL)
                .post(formBody)
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

    public static Boolean sendContexts(List<Context> contexts){
        String responseString = "";
        Type type = new TypeToken<List<Context>>(){}.getType();
        String json = new Gson().toJson(contexts, type);

        RequestBody formBody = new FormEncodingBuilder()
                .add("contexts",json)
                .build();
        Request request = new Request.Builder()
                .url(CONTEXT_URL)
                .post(formBody)
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


}
