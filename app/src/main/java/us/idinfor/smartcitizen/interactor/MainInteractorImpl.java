package us.idinfor.smartcitizen.interactor;

import android.content.SharedPreferences;
import android.text.TextUtils;

import javax.inject.Inject;

import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.data.api.hermes.entity.User;
import us.idinfor.smartcitizen.mvp.model.UserNotFoundInPreferencesException;

public class MainInteractorImpl implements MainInteractor {

    private static final String TAG = MainInteractorImpl.class.getCanonicalName();

    private final SharedPreferences mPrefs;

    @Inject
    public MainInteractorImpl(SharedPreferences prefs){
        this.mPrefs = prefs;
    }

    @Override
    public User getUserInPreferences() throws UserNotFoundInPreferencesException {
        User user = new User(this.mPrefs.getString(Constants.PROPERTY_USER_NAME,""),null);
        if(TextUtils.isEmpty(user.getEmail())){
            throw new UserNotFoundInPreferencesException();
        }
        return user;
    }

    @Override
    public boolean isDrawerLearnedInPreferences() {
        return this.mPrefs.getBoolean(Constants.PROPERTY_DRAWER_LEARNED, false);
    }

    @Override
    public void setDrawerLearnedInPreferences(boolean learned) {
        this.mPrefs.edit().putBoolean(Constants.PROPERTY_DRAWER_LEARNED, learned).commit();
    }


}