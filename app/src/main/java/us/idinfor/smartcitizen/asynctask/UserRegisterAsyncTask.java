package us.idinfor.smartcitizen.asynctask;


import android.os.AsyncTask;

import us.idinfor.smartcitizen.hermes.HermesCitizenApi;

public class UserRegisterAsyncTask extends AsyncTask<Void, Void, Integer> {
    private String email;
    private String password;

    public UserRegisterAsyncTask(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        return HermesCitizenApi.registerUser(email,password);
    }
}