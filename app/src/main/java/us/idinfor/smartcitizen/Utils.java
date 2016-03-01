package us.idinfor.smartcitizen;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import us.idinfor.smartcitizen.model.fit.ISampleFit;

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

    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
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
                //now.add(Calendar.WEEK_OF_YEAR,-1);
                break;
            case Constants.RANGE_MONTH:
                now.set(Calendar.DAY_OF_MONTH, 1);
                //now.add(Calendar.MONTH,-1);
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

    public static boolean sameDay(long startTime, long endTime){
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.setTimeInMillis(startTime);
        end.setTimeInMillis(endTime);
        return start.get(Calendar.DAY_OF_YEAR) == end.get(Calendar.DAY_OF_YEAR) &&
                start.get(Calendar.YEAR) == end.get(Calendar.YEAR);
    }

    public static Calendar getLastMinuteOfDay(long time){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.set(Calendar.HOUR,23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND,59);
        return cal;
    }

    public static Calendar getFirstMinuteOfDay(long time){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.set(Calendar.HOUR,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        return cal;
    }

    public static Calendar getFirstMinuteOfNextDay(long time){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.add(Calendar.DAY_OF_YEAR,1);
        cal.set(Calendar.HOUR,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        return cal;
    }

    public static List<Pair<Long,Long>> splitTimeRangeInDays(long startTime, long endTime){
        List<Pair<Long,Long>> ranges = new ArrayList<>();
        if(sameDay(startTime,endTime)){
            ranges.add(Pair.create(startTime,endTime));
        }else{
            ranges.add(Pair.create(startTime,getLastMinuteOfDay(startTime).getTimeInMillis()));
            Calendar end = Calendar.getInstance();
            end.setTimeInMillis(endTime);
            Calendar aux = Calendar.getInstance();
            aux.setTimeInMillis(startTime);
            aux.add(Calendar.DAY_OF_YEAR,1);
            while(aux.get(Calendar.DAY_OF_YEAR) != end.get(Calendar.DAY_OF_YEAR)){
                ranges.add(Pair.create(
                        getFirstMinuteOfDay(aux.getTimeInMillis()).getTimeInMillis(),
                        getLastMinuteOfDay(aux.getTimeInMillis()).getTimeInMillis()));
                aux.add(Calendar.DAY_OF_YEAR,1);
            }
            ranges.add(Pair.create(getFirstMinuteOfDay(endTime).getTimeInMillis(),endTime));
        }
        return ranges;
    }

    public static <T extends ISampleFit> List<List<T>> splitSamplesInDays(List<T> items){
        List<List<T>> splitList = new ArrayList<>();
        List<T> auxList = new ArrayList<>();
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        for(T item : items){
            start.setTimeInMillis(item.getStartTime());
            end.setTimeInMillis(item.getEndTime());
            if(sameDay(item.getStartTime(),item.getEndTime())){
                auxList.add(item);
            }else{
                T sample = item;
                sample.setEndTime(getLastMinuteOfDay(item.getStartTime()).getTimeInMillis());
                auxList.add(sample);
                start.add(Calendar.DAY_OF_YEAR,1);
                while(start.get(Calendar.DAY_OF_YEAR) != end.get(Calendar.DAY_OF_YEAR)){
                    sample.setStartTime(getFirstMinuteOfDay(start.getTimeInMillis()).getTimeInMillis());
                    sample.setEndTime(getLastMinuteOfDay(start.getTimeInMillis()).getTimeInMillis());
                    auxList.add(sample);
                    start.add(Calendar.DAY_OF_YEAR,1);
                }
                sample.setStartTime(getFirstMinuteOfDay(item.getEndTime()).getTimeInMillis());
                sample.setEndTime(item.getEndTime());
                auxList.add(sample);
            }
        }

        int splitPos = 0;
        for(int i = 1; i < auxList.size() - 1; i++){
            if(!sameDay(auxList.get(i).getStartTime(),auxList.get(i - 1).getStartTime())){
                splitList.add(auxList.subList(splitPos,i));
                splitPos = i;
            }
        }
        return splitList;
    }


    public static Drawable getFitnessProgresBarDrawable(Context context, Integer progress){
        Integer drawable = R.drawable.fitness_progress_bar_100;
        if(progress >= 0 && progress < 25){
            drawable = R.drawable.fitness_progress_bar_25;
        }else if(progress >= 25 && progress < 75){
            drawable = R.drawable.fitness_progress_bar_25_75;
        }else if(progress >=75 && progress < 100){
            drawable = R.drawable.fitness_progress_bar_75;
        }
        return ContextCompat.getDrawable(context,drawable);
    }

}
