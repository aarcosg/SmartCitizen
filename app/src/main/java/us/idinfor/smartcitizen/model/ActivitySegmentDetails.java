package us.idinfor.smartcitizen.model;


public class ActivitySegmentDetails {

    ActivitySummaryFit activitySummary;
    LocationBoundingBoxFit locationBoundingBox;
    StepCountDeltaFit stepCountDelta;
    DistanceDeltaFit distanceDelta;
    CaloriesExpendedFit caloriesExpended;

    public ActivitySegmentDetails() {
    }

    public ActivitySegmentDetails(ActivitySummaryFit activitySummary, LocationBoundingBoxFit locationBoundingBox, StepCountDeltaFit stepCountDelta, DistanceDeltaFit distanceDelta, CaloriesExpendedFit caloriesExpended) {
        this.activitySummary = activitySummary;
        this.locationBoundingBox = locationBoundingBox;
        this.stepCountDelta = stepCountDelta;
        this.distanceDelta = distanceDelta;
        this.caloriesExpended = caloriesExpended;
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
}
