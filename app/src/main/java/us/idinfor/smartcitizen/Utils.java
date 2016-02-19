package us.idinfor.smartcitizen;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import java.util.Calendar;

public class Utils {
    private static final String TAG = Utils.class.getCanonicalName();

    /**
     * @return Application's {@code SharedPreferences}.
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public static boolean isInternetAvailable(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static long getStartTimeRange(int range){
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        switch (range){
            case Constants.RANGE_DAY:
                break;
            case Constants.RANGE_WEEK:
                now.set(Calendar.DAY_OF_WEEK, now.getFirstDayOfWeek());
                break;
            case Constants.RANGE_MONTH:
                now.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case Constants.RANGE_YEAR:
                now.set(Calendar.DAY_OF_YEAR,1);
                break;
        }
        return now.getTimeInMillis();
    }

    public static Integer getIconResourceId(Context context, String activity){
        return context.getResources().getIdentifier("ic_activity_" + activity,"drawable",context.getPackageName());
    }

    public static Integer getIconColorId(Context context, String activity){
        Integer color = context.getResources().getIdentifier("activity_" + activity,"color",context.getPackageName());
        return color > 0 ? ContextCompat.getColor(context,color) : ContextCompat.getColor(context,R.color.accent);
    }
}
