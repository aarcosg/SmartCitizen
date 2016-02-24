package us.idinfor.smartcitizen.model;


public class LocationSampleFit {

    Float latitude; // (degrees)
    Float longitude; // (degrees)
    Float accuracy; // (meters)
    Long startTime;
    Long endTime;

    public LocationSampleFit() {
    }

    public LocationSampleFit(Float latitude, Float longitude, Float accuracy, Long startTime, Long endTime) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Float accuracy) {
        this.accuracy = accuracy;
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
