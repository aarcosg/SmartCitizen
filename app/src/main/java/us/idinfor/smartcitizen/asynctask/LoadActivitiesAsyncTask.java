package us.idinfor.smartcitizen.asynctask;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.List;

import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.backend.contextApi.ContextApi;
import us.idinfor.smartcitizen.backend.contextApi.model.Context;


public class LoadActivitiesAsyncTask extends AsyncTask<Void,Void,List<Context>> {

    private static final String TAG = LoadActivitiesAsyncTask.class.getCanonicalName();
    private static ContextApi contextApi = null;
    private Long deviceId;

    public LoadActivitiesAsyncTask(Long deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    protected List<Context> doInBackground(Void... params) {
        List<Context> contexts = null;
        if (contextApi == null) {
            ContextApi.Builder builder = new ContextApi.Builder(AndroidHttp.newCompatibleTransport(),new AndroidJsonFactory(), null)
                    .setRootUrl(Constants.GOOGLE_APPENGINE_URL)
                    .setApplicationName(Constants.GOOGLE_APPENGINE_APP_NAME);
            contextApi = builder.build();
        }

        try {
            contexts = contextApi.time(deviceId,"today").execute().getItems();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contexts;
    }
}
