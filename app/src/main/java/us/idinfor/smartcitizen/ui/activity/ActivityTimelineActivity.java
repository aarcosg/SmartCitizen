package us.idinfor.smartcitizen.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.di.HasComponent;
import us.idinfor.smartcitizen.di.components.ActivityTimelineComponent;
import us.idinfor.smartcitizen.di.components.DaggerActivityTimelineComponent;
import us.idinfor.smartcitizen.ui.fragment.ActivityTimelineFragment;

public class ActivityTimelineActivity extends BaseActivity implements HasComponent<ActivityTimelineComponent> {

    private static final String TAG = ActivityTimelineActivity.class.getCanonicalName();

    private ActivityTimelineComponent mActivityTimelineComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_timeline);
        buildActionBarToolbar(getString(R.string.title_activity_activity_timeline),true);

        this.initializeInjector();
        if(savedInstanceState == null){
            addFragment(R.id.fragment_container, new ActivityTimelineFragment());
        }
    }

    private void initializeInjector() {
        this.mActivityTimelineComponent = DaggerActivityTimelineComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();
    }

    @Override
    public ActivityTimelineComponent getComponent() {
        return mActivityTimelineComponent;
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, ActivityTimelineActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

}
