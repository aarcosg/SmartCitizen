package us.idinfor.smartcitizen.asynctask;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.List;

import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.backend.contextApi.ContextApi;
import us.idinfor.smartcitizen.backend.contextApi.model.Activity;


public class LoadActivitiesAsyncTask extends AsyncTask<Void,Void,List<Activity>> {

    private static final String TAG = LoadActivitiesAsyncTask.class.getCanonicalName();
    private static ContextApi contextApi = null;
    private Long deviceId;

    public LoadActivitiesAsyncTask(Long deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    protected List<Activity> doInBackground(Void... params) {
        List<Activity> activities = null;
        if (contextApi == null) {
            ContextApi.Builder builder = new ContextApi.Builder(AndroidHttp.newCompatibleTransport(),new AndroidJsonFactory(), null)
                    .setRootUrl(Constants.GOOGLE_APPENGINE_URL)
                    .setApplicationName(Constants.GOOGLE_APPENGINE_APP_NAME);
            contextApi = builder.build();
        }

        try {
            activities = contextApi.activity(deviceId, "today").execute().getItems();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return activities;
    }
}
