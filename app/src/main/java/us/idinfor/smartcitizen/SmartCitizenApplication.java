package us.idinfor.smartcitizen;

import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDexApplication;

import us.idinfor.smartcitizen.model.DaoMaster;
import us.idinfor.smartcitizen.model.DaoSession;

public class SmartCitizenApplication extends MultiDexApplication {
    private static final String TAG = SmartCitizenApplication.class.getCanonicalName();
    public DaoSession daoSession;


    public SmartCitizenApplication(){
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //ActivityRecognitionService.actionStartActivityRecognition(this);
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, Constants.DATABASE_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
