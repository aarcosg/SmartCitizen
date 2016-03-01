package us.idinfor.smartcitizen.model.fit;


import android.os.Parcel;
import android.os.Parcelable;

public class LocationSampleFit implements Parcelable{

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.latitude);
        dest.writeValue(this.longitude);
        dest.writeValue(this.accuracy);
        dest.writeValue(this.startTime);
        dest.writeValue(this.endTime);
    }

    protected LocationSampleFit(Parcel in) {
        this.latitude = (Float) in.readValue(Float.class.getClassLoader());
        this.longitude = (Float) in.readValue(Float.class.getClassLoader());
        this.accuracy = (Float) in.readValue(Float.class.getClassLoader());
        this.startTime = (Long) in.readValue(Long.class.getClassLoader());
        this.endTime = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Creator<LocationSampleFit> CREATOR = new Creator<LocationSampleFit>() {
        public LocationSampleFit createFromParcel(Parcel source) {
            return new LocationSampleFit(source);
        }

        public LocationSampleFit[] newArray(int size) {
            return new LocationSampleFit[size];
        }
    };
}
