package us.idinfor.smartcitizen;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.Calendar;

public class Utils {
    private static final String TAG = Utils.class.getCanonicalName();

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

    public static boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        Constants.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
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

    public static Drawable getFitnessProgressBarDrawable(Context context, Integer progress){
        Integer drawable = R.drawable.fitness_progress_bar_100;
        if(progress >= 0 && progress < 25){
            drawable = R.drawable.fitness_progress_bar_25;
        }else if(progress >= 25 && progress < 75){
            drawable = R.drawable.fitness_progress_bar_25_75;
        }else if(progress >= 75 && progress < 100){
            drawable = R.drawable.fitness_progress_bar_75;
        }
        return ContextCompat.getDrawable(context,drawable);
    }

}
