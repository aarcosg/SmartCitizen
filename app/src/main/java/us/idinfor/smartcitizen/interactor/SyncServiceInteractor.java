package us.idinfor.smartcitizen.interactor;

import com.google.android.gms.fitness.request.DataReadRequest;

import java.util.List;

import rx.Observable;
import us.idinfor.smartcitizen.data.api.google.fit.entity.ActivitySegmentFit;
import us.idinfor.smartcitizen.data.api.google.fit.entity.LocationSampleFit;
import us.idinfor.smartcitizen.data.api.hermes.entity.User;

public interface SyncServiceInteractor extends Interactor {

    User getUserFromPreferences();

    DataReadRequest.Builder buildGoogleFitLocationsRequest();
    DataReadRequest.Builder buildGoogleFitActivitiesRequest();
    Observable<List<LocationSampleFit>> queryLocationsToGoogleFit();
    Observable<List<ActivitySegmentFit>> queryActivitiesToGoogleFit();
    void uploadLocations(List<LocationSampleFit> locations);
    void uploadActivities(List<ActivitySegmentFit> activities);
}
