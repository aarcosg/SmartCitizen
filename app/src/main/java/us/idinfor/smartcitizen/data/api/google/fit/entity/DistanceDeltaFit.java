package us.idinfor.smartcitizen.data.api.google.fit.entity;


public class DistanceDeltaFit {

    Float distance;
    Long startTime;
    Long endTime;

    public DistanceDeltaFit() {
    }

    public DistanceDeltaFit(Float distance, Long startTime, Long endTime) {
        this.distance = distance;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
}
