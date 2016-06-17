package es.us.hermes.smartcitizen.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import es.us.hermes.smartcitizen.R;
import es.us.hermes.smartcitizen.di.HasComponent;
import es.us.hermes.smartcitizen.di.components.DaggerLocationDetailsComponent;
import es.us.hermes.smartcitizen.di.components.LocationDetailsComponent;
import es.us.hermes.smartcitizen.ui.fragment.LocationDetailsFragment;

public class LocationDetailsActivity extends BaseActivity implements HasComponent<LocationDetailsComponent>{

    private static final String TAG = LocationDetailsActivity.class.getCanonicalName();

    private LocationDetailsComponent mLocationDetailsComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);

        this.initializeInjector();
        if(savedInstanceState == null){
            addFragment(R.id.fragment_container, new LocationDetailsFragment());
        }
    }

    private void initializeInjector() {
        this.mLocationDetailsComponent = DaggerLocationDetailsComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();
    }

    @Override
    public LocationDetailsComponent getComponent() {
        return this.mLocationDetailsComponent;
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, LocationDetailsActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

}
