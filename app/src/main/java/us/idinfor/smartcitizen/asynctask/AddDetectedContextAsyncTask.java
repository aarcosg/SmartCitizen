package us.idinfor.smartcitizen.asynctask;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.backend.contextApi.ContextApi;
import us.idinfor.smartcitizen.backend.contextApi.model.Context;
import us.idinfor.smartcitizen.backend.contextApi.model.Device;
import us.idinfor.smartcitizen.backend.contextApi.model.GeoPt;

public class AddDetectedContextAsyncTask extends AsyncTask<Void, Void, Context> {
    private static final String TAG = AddDetectedContextAsyncTask.class.getCanonicalName();
    private static ContextApi contextApi = null;
    private Long deviceId;
    private Integer detectedContext;
    private Location location;

    public AddDetectedContextAsyncTask(Long deviceId, Integer detectedContext, Location location) {
        this.deviceId = deviceId;
        this.detectedContext = detectedContext;
        this.location = location;
    }

    @Override
    protected Context doInBackground(Void... params) {
        Context newContext = null;
        if (contextApi == null) {
            ContextApi.Builder builder = new ContextApi.Builder(AndroidHttp.newCompatibleTransport(),new AndroidJsonFactory(), null)
                .setRootUrl(Constants.GOOGLE_APPENGINE_URL)
                .setApplicationName(Constants.GOOGLE_APPENGINE_APP_NAME);
            contextApi = builder.build();
        }

        try {
            Context context = new Context();
            if(location != null){
                GeoPt geoPoint = new GeoPt();
                geoPoint.setLatitude((float)location.getLatitude());
                geoPoint.setLongitude((float)location.getLongitude());
                context.setLocation(geoPoint);
            }
            Device device = new Device();
            device.setId(deviceId);
            context.setContext(detectedContext);
            context.setDevice(device);
            newContext = contextApi.insert(context).execute();
            Log.d(TAG, "New context added: " + detectedContext);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return newContext;
    }

}