package es.us.hermes.smartcitizen.interactor;

import com.google.android.gms.fitness.request.DataReadRequest;

import java.util.Calendar;
import java.util.List;

import rx.Observable;
import es.us.hermes.smartcitizen.mvp.model.google.fit.ActivitySegmentFit;
import es.us.hermes.smartcitizen.mvp.model.google.fit.CaloriesExpendedFit;
import es.us.hermes.smartcitizen.mvp.model.google.fit.DistanceDeltaFit;
import es.us.hermes.smartcitizen.mvp.model.google.fit.HeartRateSampleFit;
import es.us.hermes.smartcitizen.mvp.model.google.fit.LocationSampleFit;
import es.us.hermes.smartcitizen.mvp.model.google.fit.StepCountDeltaFit;
import es.us.hermes.smartcitizen.mvp.model.hermes.User;

public interface SyncServiceInteractor extends Interactor {

    User getUserFromPreferences();

    Calendar getLastDayDataSentFromPreferences();

    void saveLastDayDataSentInPreferences();

    long getLastLocationTimeSentFromPreferences();

    long getLastActivityTimeSentFromPreferences();

    long getLastStepsTimeSentFromPreferences();

    long getLastDistanceTimeSentFromPreferences();

    long getLastCaloriesExpendedTimeSentFromPreferences();

    long getLastHeartRateTimeSentFromPreferences();

    long getLastSleepTimeSentFromPreferences();

    DataReadRequest.Builder buildGoogleFitLocationsRequest();

    DataReadRequest.Builder buildGoogleFitActivitiesRequest();

    DataReadRequest.Builder buildGoogleFitStepsRequest();

    DataReadRequest.Builder buildGoogleFitDistancesRequest();

    DataReadRequest.Builder buildGoogleFitCaloriesExpendedRequest();

    DataReadRequest.Builder buildGoogleFitHeartRateSamplesRequest();

    Observable<List<LocationSampleFit>> queryLocationsToGoogleFit(long startTime, long endTime);

    Observable<List<ActivitySegmentFit>> queryActivitiesToGoogleFit(long startTime, long endTime);

    Observable<List<StepCountDeltaFit>> queryStepsToGoogleFit(long startTime, long endTime);

    Observable<List<DistanceDeltaFit>> queryDistancesToGoogleFit(long startTime, long endTime);

    Observable<List<CaloriesExpendedFit>> queryCaloriesExpendedToGoogleFit(long startTime, long endTime);

    Observable<List<HeartRateSampleFit>> queryHeartRateSamplesToGoogleFit(long startTime, long endTime);

    Observable<List<ActivitySegmentFit>> querySleepActivityToGoogleFit(long startTime, long endTime);

    void uploadPeriodicLocations(List<LocationSampleFit> locations);

    void uploadPeriodicActivities(List<ActivitySegmentFit> activities);

    void uploadPeriodicSteps(List<StepCountDeltaFit> stepsList);

    void uploadPeriodicDistances(List<DistanceDeltaFit> distances);

    void uploadPeriodicCaloriesExpended(List<CaloriesExpendedFit> caloriesExpended);

    void uploadPeriodicHeartRateSample(List<HeartRateSampleFit> heartRates);

    void uploadPeriodicSleep(List<ActivitySegmentFit> activities);

    void uploadFullDayLocations(List<LocationSampleFit> locations);

    void uploadFullDayActivities(List<ActivitySegmentFit> activities);

    void uploadFullDaySteps(List<StepCountDeltaFit> steps);

    void uploadFullDayDistances(List<DistanceDeltaFit> distances);

    void uploadFullDayCaloriesExpended(List<CaloriesExpendedFit> caloriesExpended);

    void uploadFullDayHeartRates(List<HeartRateSampleFit> heartRates);
}
