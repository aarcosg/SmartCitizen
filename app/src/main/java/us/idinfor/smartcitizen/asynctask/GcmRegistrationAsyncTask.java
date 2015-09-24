package us.idinfor.smartcitizen.asynctask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.Utils;
import us.idinfor.smartcitizen.backend.deviceApi.DeviceApi;
import us.idinfor.smartcitizen.backend.deviceApi.model.Device;

public class GcmRegistrationAsyncTask extends AsyncTask<String, Void, Device> {
    private static final String TAG = GcmRegistrationAsyncTask.class.getCanonicalName();
    private static DeviceApi deviceApi = null;
    private GoogleCloudMessaging mGCM;
    private Context mContext;


    public GcmRegistrationAsyncTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected Device doInBackground(String... params) {
        Device newDevice = null;
        if(deviceApi == null){
            DeviceApi.Builder builder = new DeviceApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(),null)
                    .setRootUrl(Constants.GOOGLE_APPENGINE_URL)
                    .setApplicationName(Constants.GOOGLE_APPENGINE_APP_NAME);
            deviceApi = builder.build();
        }
        String UDID = params[0];

        try {
            if (mGCM == null) {
                mGCM = GoogleCloudMessaging.getInstance(mContext);
            }
            SharedPreferences prefs = Utils.getSharedPreferences(mContext);
            Long deviceId = prefs.getLong(Constants.PROPERTY_DEVICE_ID, 0L);
            String gcmID = mGCM.register(Constants.GCM_SENDER_ID);
            Log.d(TAG, "Device registered, gcmID=" + gcmID);
            Device device = new Device();
            device.setDeviceId(UDID);
            device.setGcmId(gcmID);
            /*User user = new User();
            user.setRegId(UDID);
            device.setUser(user);*/
            if(deviceId > 0L){
                //Device already registered. Update GCM ID
                newDevice = deviceApi.update(deviceId,device).execute();
            }else{
                // New device. Insert GCM ID
                newDevice = deviceApi.insert(device).execute();
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return newDevice;
    }
}