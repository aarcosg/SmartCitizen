package us.idinfor.smartcitizen.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import us.idinfor.smartcitizen.R;

public class ActivityDetailsActivity extends BaseActivity {

    private static final String TAG = ActivityDetailsActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_details);
        buildActionBarToolbar(getString(R.string.title_activity_activity_details),true);
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, ActivityDetailsActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

}
