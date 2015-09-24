package us.idinfor.smartcitizen;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utils {
    private static final String TAG = Utils.class.getCanonicalName();

    /**
     * @return Application's {@code SharedPreferences}.
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }
}
