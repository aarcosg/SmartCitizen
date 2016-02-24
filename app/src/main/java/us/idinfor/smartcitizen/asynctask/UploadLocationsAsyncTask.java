package us.idinfor.smartcitizen.asynctask;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import us.idinfor.smartcitizen.HermesCitizenApi;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.model.LocationSampleFit;

public class UploadLocationsAsyncTask extends AsyncTask<Void,Integer,Integer>{

    private ProgressDialog progressDialog;
    private Context context;
    private List<LocationSampleFit> locations;
    private String username;

    public UploadLocationsAsyncTask(Context context, String username, List<LocationSampleFit> locations){
        this.context = context;
        this.username = username;
        this.locations = locations;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.uploading_locations_hermes));
        progressDialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        return HermesCitizenApi.uploadLocations(username,locations);
    }

    @Override
    protected void onPostExecute(Integer res) {
        super.onPostExecute(res);
        progressDialog.dismiss();
    }
}
