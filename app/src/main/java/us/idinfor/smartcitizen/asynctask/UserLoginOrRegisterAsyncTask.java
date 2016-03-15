package us.idinfor.smartcitizen.asynctask;


import android.os.AsyncTask;

import us.idinfor.smartcitizen.hermes.HermesCitizenApi_old;

public class UserLoginOrRegisterAsyncTask extends AsyncTask<Void, Void, Integer> {
    private String email;
    private String password;

    public UserLoginOrRegisterAsyncTask(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        Integer result;
        if(!HermesCitizenApi_old.existsUser(email)){
            result = HermesCitizenApi_old.registerUser(email,password);
        }else{
            result = HermesCitizenApi_old.RESPONSE_OK;
        }
        return result;
    }
}