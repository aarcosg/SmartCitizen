package es.us.hermes.smartcitizen.mvp.model;


import java.util.List;

import es.us.hermes.smartcitizen.mvp.model.google.fit.ActivitySummaryFit;
import es.us.hermes.smartcitizen.mvp.model.google.fit.CaloriesExpendedFit;
import es.us.hermes.smartcitizen.mvp.model.google.fit.DistanceDeltaFit;
import es.us.hermes.smartcitizen.mvp.model.google.fit.HeartRateSummaryFit;
import es.us.hermes.smartcitizen.mvp.model.google.fit.LocationBoundingBoxFit;
import es.us.hermes.smartcitizen.mvp.model.google.fit.StepCountDeltaFit;

public class ActivityDetails {

    ActivitySummaryFit activitySummary;
    LocationBoundingBoxFit locationBoundingBox;
    StepCountDeltaFit stepCountDelta;
    DistanceDeltaFit distanceDelta;
    CaloriesExpendedFit caloriesExpended;
    HeartRateSummaryFit heartRateSummary;
    List<ActivitySummaryFit> activitiesSummary;

    public ActivityDetails() {
    }

    public ActivityDetails(ActivitySummaryFit activitySummary, LocationBoundingBoxFit locationBoundingBox, StepCountDeltaFit stepCountDelta, DistanceDeltaFit distanceDelta, CaloriesExpendedFit caloriesExpended, HeartRateSummaryFit heartRateSummary, List<ActivitySummaryFit> activitiesSummary) {
        this.activitySummary = activitySummary;
        this.locationBoundingBox = locationBoundingBox;
        this.stepCountDelta = stepCountDelta;
        this.distanceDelta = distanceDelta;
        this.caloriesExpended = caloriesExpended;
        this.heartRateSummary = heartRateSummary;
        this.activitiesSummary = activitiesSummary;
    }

    public ActivitySummaryFit getActivitySummary() {
        return activitySummary;
    }

    public void setActivitySummary(ActivitySummaryFit activitySummary) {
        this.activitySummary = activitySummary;
    }

    public LocationBoundingBoxFit getLocationBoundingBox() {
        return locationBoundingBox;
    }

    public void setLocationBoundingBox(LocationBoundingBoxFit locationBoundingBox) {
        this.locationBoundingBox = locationBoundingBox;
    }

    public StepCountDeltaFit getStepCountDelta() {
        return stepCountDelta;
    }

    public void setStepCountDelta(StepCountDeltaFit stepCountDelta) {
        this.stepCountDelta = stepCountDelta;
    }

    public DistanceDeltaFit getDistanceDelta() {
        return distanceDelta;
    }

    public void setDistanceDelta(DistanceDeltaFit distanceDelta) {
        this.distanceDelta = distanceDelta;
    }

    public CaloriesExpendedFit getCaloriesExpended() {
        return caloriesExpended;
    }

    public void setCaloriesExpended(CaloriesExpendedFit caloriesExpended) {
        this.caloriesExpended = caloriesExpended;
    }

    public HeartRateSummaryFit getHeartRateSummary() {
        return heartRateSummary;
    }

    public void setHeartRateSummary(HeartRateSummaryFit heartRateSummary) {
        this.heartRateSummary = heartRateSummary;
    }

    public List<ActivitySummaryFit> getActivitiesSummary() {
        return activitiesSummary;
    }

    public void setActivitiesSummary(List<ActivitySummaryFit> activitiesSummary) {
        this.activitiesSummary = activitiesSummary;
    }
}
