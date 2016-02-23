package us.idinfor.smartcitizen.model;


public class ActivityDetails {

    ActivitySummaryFit activitySummary;
    LocationBoundingBoxFit locationBoundingBox;
    StepCountDeltaFit stepCountDelta;
    DistanceDeltaFit distanceDelta;
    CaloriesExpendedFit caloriesExpended;
    HeartRateSummary heartRateSummary;

    public ActivityDetails() {
    }

    public ActivityDetails(ActivitySummaryFit activitySummary, LocationBoundingBoxFit locationBoundingBox, StepCountDeltaFit stepCountDelta, DistanceDeltaFit distanceDelta, CaloriesExpendedFit caloriesExpended, HeartRateSummary heartRateSummary) {
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

    public HeartRateSummary getHeartRateSummary() {
        return heartRateSummary;
    }

    public void setHeartRateSummary(HeartRateSummary heartRateSummary) {
        this.heartRateSummary = heartRateSummary;
    }
}
