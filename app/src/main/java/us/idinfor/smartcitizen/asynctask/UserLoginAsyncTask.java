package us.idinfor.smartcitizen.asynctask;


import android.os.AsyncTask;

import us.idinfor.smartcitizen.HermesCitizenApi;

public class UserLoginAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private String username;

    public UserLoginAsyncTask(String username) {
        this.username = username;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return HermesCitizenApi.existsUser(username);
    }
}