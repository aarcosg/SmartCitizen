package us.idinfor.smartcitizen.asynctask;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;

import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.HermesCitizenApi;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.Utils;

public class UploadDataHermesCitizenAsyncTask<T> extends AsyncTask<Void,Integer,Integer>{

    private ProgressDialog progressDialog;
    private Context context;
    private List<T> items;
    private String username;

    public UploadDataHermesCitizenAsyncTask(Context context, String username, List<T> items){
        this.context = context;
        this.username = username;
        this.items = items;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.uploading_data_hermes));
        progressDialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        return HermesCitizenApi.uploadData(username,items);
    }

    @Override
    protected void onPostExecute(Integer res) {
        super.onPostExecute(res);
        progressDialog.dismiss();
        switch (res) {
            case HermesCitizenApi.RESPONSE_OK:
                Toast.makeText(context, "Data uploaded successfully", Toast.LENGTH_LONG).show();
                break;
            case HermesCitizenApi.RESPONSE_ERROR_USER_NOT_FOUND:
                Toast.makeText(context, "Error: User not found", Toast.LENGTH_LONG).show();
                Utils.getSharedPreferences(context).edit().remove(Constants.PROPERTY_USER_NAME).commit();
                break;
            case HermesCitizenApi.RESPONSE_ERROR_DATA_NOT_UPLOADED:
                Toast.makeText(context, "Error: Data not uploaded", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(context, "Unknown error", Toast.LENGTH_LONG).show();
        }
    }
}
