package us.idinfor.smartcitizen.model;


public class LocationBoundingBoxFit {

    Float latitudeSW; //low latitude
    Float longitudeSW; //low longitude
    Float latitudeNE; // high latitude
    Float longitudeNE; // high longitude
    Long startTime;
    Long endTime;

    public LocationBoundingBoxFit() {
    }

    public LocationBoundingBoxFit(Float latitudeSW, Float longitudeSW, Float latitudeNE, Float longitudeNE, Long startTime, Long endTime) {
        this.latitudeSW = latitudeSW;
        this.longitudeSW = longitudeSW;
        this.latitudeNE = latitudeNE;
        this.longitudeNE = longitudeNE;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Float getLatitudeSW() {
        return latitudeSW;
    }

    public void setLatitudeSW(Float latitudeSW) {
        this.latitudeSW = latitudeSW;
    }

    public Float getLongitudeSW() {
        return longitudeSW;
    }

    public void setLongitudeSW(Float longitudeSW) {
        this.longitudeSW = longitudeSW;
    }

    public Float getLatitudeNE() {
        return latitudeNE;
    }

    public void setLatitudeNE(Float latitudeNE) {
        this.latitudeNE = latitudeNE;
    }

    public Float getLongitudeNE() {
        return longitudeNE;
    }

    public void setLongitudeNE(Float longitudeNE) {
        this.longitudeNE = longitudeNE;
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
