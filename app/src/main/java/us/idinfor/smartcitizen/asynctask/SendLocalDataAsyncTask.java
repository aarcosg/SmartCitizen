package us.idinfor.smartcitizen.asynctask;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import us.idinfor.smartcitizen.HermesCitizenApi;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.SmartCitizenApplication;

public class SendLocalDataAsyncTask  extends AsyncTask<Void,Integer,Boolean>{

    private ProgressDialog progressDialog;
    private Context context;

    public SendLocalDataAsyncTask(Context context){
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.sending_data));
        progressDialog.setMax(100);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressDialog.setProgress(values[0]);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        List<us.idinfor.smartcitizen.model.Context> contexts = ((SmartCitizenApplication)context.getApplicationContext())
                .getDaoSession().getContextDao().loadAll();
        return HermesCitizenApi.sendContexts(contexts);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        progressDialog.dismiss();
        Log.e("SendLocaldataasync",aBoolean.toString());
    }
}
