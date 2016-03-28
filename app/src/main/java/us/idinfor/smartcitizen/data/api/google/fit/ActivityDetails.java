package us.idinfor.smartcitizen.data.api.google.fit;


import us.idinfor.smartcitizen.data.api.google.fit.entity.ActivitySummaryFit;
import us.idinfor.smartcitizen.data.api.google.fit.entity.CaloriesExpendedFit;
import us.idinfor.smartcitizen.data.api.google.fit.entity.DistanceDeltaFit;
import us.idinfor.smartcitizen.data.api.google.fit.entity.HeartRateSummaryFit;
import us.idinfor.smartcitizen.data.api.google.fit.entity.LocationBoundingBoxFit;
import us.idinfor.smartcitizen.data.api.google.fit.entity.StepCountDeltaFit;

public class ActivityDetails {

    ActivitySummaryFit activitySummary;
    LocationBoundingBoxFit locationBoundingBox;
    StepCountDeltaFit stepCountDelta;
    DistanceDeltaFit distanceDelta;
    CaloriesExpendedFit caloriesExpended;
    HeartRateSummaryFit heartRateSummary;

    public ActivityDetails() {
    }

    public ActivityDetails(ActivitySummaryFit activitySummary, LocationBoundingBoxFit locationBoundingBox, StepCountDeltaFit stepCountDelta, DistanceDeltaFit distanceDelta, CaloriesExpendedFit caloriesExpended, HeartRateSummaryFit heartRateSummary) {
        this.activitySummary = activitySummary;
        this.locationBoundingBox = locationBoundingBox;
        this.stepCountDelta = stepCountDelta;
        this.distanceDelta = distanceDelta;
        this.caloriesExpended = caloriesExpended;
        this.heartRateSummary = heartRateSummary;
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
}
