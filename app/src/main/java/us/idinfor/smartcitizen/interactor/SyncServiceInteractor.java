package us.idinfor.smartcitizen.interactor;

import com.google.android.gms.fitness.request.DataReadRequest;

import java.util.Calendar;
import java.util.List;

import rx.Observable;
import us.idinfor.smartcitizen.data.api.google.fit.entity.ActivitySegmentFit;
import us.idinfor.smartcitizen.data.api.google.fit.entity.LocationSampleFit;
import us.idinfor.smartcitizen.data.api.hermes.entity.User;

public interface SyncServiceInteractor extends Interactor {

    User getUserFromPreferences();
    Calendar getLastDayDataSentFromPreferences();
    void saveLastDayDataSentInPreferences();
    long getLastLocationTimeSentFromPreferences();
    long getLastActivityTimeSentFromPreferences();
    DataReadRequest.Builder buildGoogleFitLocationsRequest();
    DataReadRequest.Builder buildGoogleFitActivitiesRequest();
    Observable<List<LocationSampleFit>> queryLocationsToGoogleFit(long startTime, long endTime);
    Observable<List<ActivitySegmentFit>> queryActivitiesToGoogleFit(long startTime, long endTime);
    void uploadPeriodicLocations(List<LocationSampleFit> locations);
    void uploadPeriodicActivities(List<ActivitySegmentFit> activities);
    void uploadFullDayLocations(List<LocationSampleFit> locations);
    void uploadFullDayActivities(List<ActivitySegmentFit> activities);
}
