package us.idinfor.smartcitizen.asynctask;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import us.idinfor.smartcitizen.HermesCitizenApi;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.SmartCitizenApplication;
import us.idinfor.smartcitizen.model.ContextDao;

public class SendLocalDataAsyncTask  extends AsyncTask<Void,Integer,Integer>{

    private ProgressDialog progressDialog;
    private Context context;

    public SendLocalDataAsyncTask(Context context){
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.sending_data));
        progressDialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        List<us.idinfor.smartcitizen.model.Context> contexts = ((SmartCitizenApplication)context.getApplicationContext())
                .getDaoSession().getContextDao()
                .queryBuilder()
                .where(ContextDao.Properties.Sent.eq(0))
                .orderAsc(ContextDao.Properties.Time)
                .list();
        return HermesCitizenApi.sendContexts(contexts);
    }

    @Override
    protected void onPostExecute(Integer res) {
        super.onPostExecute(res);
        progressDialog.dismiss();
    }
}
