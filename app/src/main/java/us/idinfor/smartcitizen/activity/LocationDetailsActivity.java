package us.idinfor.smartcitizen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.OnItemSelected;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.event.TimeRangeSelectedEvent;

public class LocationDetailsActivity extends BaseActivity {

    private static final String TAG = LocationDetailsActivity.class.getCanonicalName();

    @Bind(R.id.toolbarSpinner)
    AppCompatSpinner mToolbarSpinner;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void injectActivityComponent() {
        getActivityComponent().inject(this);
    }

    @OnItemSelected(R.id.toolbarSpinner)
    public void timeRangeSelected(int position){
        EventBus.getDefault().post(new TimeRangeSelectedEvent(position));
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, LocationDetailsActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

}
