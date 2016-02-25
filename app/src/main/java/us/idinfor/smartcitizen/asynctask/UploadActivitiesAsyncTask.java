package us.idinfor.smartcitizen.asynctask;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import us.idinfor.smartcitizen.HermesCitizenApi;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.model.ActivitySegmentFit;

public class UploadActivitiesAsyncTask extends AsyncTask<Void,Integer,Integer>{

    private ProgressDialog progressDialog;
    private Context context;
    private List<ActivitySegmentFit> activities;
    private String username;

    public UploadActivitiesAsyncTask(Context context, String username, List<ActivitySegmentFit> activities){
        this.context = context;
        this.username = username;
        this.activities = activities;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.uploading_activities_hermes));
        progressDialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        return HermesCitizenApi.uploadActivities(username,activities);
    }

    @Override
    protected void onPostExecute(Integer res) {
        super.onPostExecute(res);
        progressDialog.dismiss();
    }
}
