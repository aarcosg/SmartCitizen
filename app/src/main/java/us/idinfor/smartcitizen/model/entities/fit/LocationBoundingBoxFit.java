package us.idinfor.smartcitizen.model.entities.fit;


import com.google.android.gms.maps.model.LatLng;

public class LocationBoundingBoxFit {

    Float latitudeSW; //low latitude
    Float longitudeSW; //low longitude
    Float latitudeNE; // high latitude
    Float longitudeNE; // high longitude
    Long startTime;
    Long endTime;

    LatLng locationSW;
    LatLng locationNE;

    public LocationBoundingBoxFit() {
    }

    public LocationBoundingBoxFit(Float latitudeSW, Float longitudeSW, Float latitudeNE, Float longitudeNE, Long startTime, Long endTime) {
        this.latitudeSW = latitudeSW;
        this.longitudeSW = longitudeSW;
        this.latitudeNE = latitudeNE;
        this.longitudeNE = longitudeNE;
        this.startTime = startTime;
        this.endTime = endTime;
        setLatLngPoints();
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

    public LatLng getLocationSW() {
        return locationSW;
    }

    public void setLocationSW(LatLng locationSW) {
        this.locationSW = locationSW;
    }

    public LatLng getLocationNE() {
        return locationNE;
    }

    public void setLocationNE(LatLng locationNE) {
        this.locationNE = locationNE;
    }

    private void setLatLngPoints(){
        locationSW = new LatLng(latitudeSW, longitudeSW);
        locationNE = new LatLng(latitudeNE, longitudeNE);
    }
}
