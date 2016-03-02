package us.idinfor.smartcitizen.model.fit;

import android.os.Parcel;
import android.os.Parcelable;

public class ActivitySegmentFit implements Parcelable, ISampleFit {

    String name;
    Long startTime;
    Long endTime;

    public ActivitySegmentFit() {
    }

    public ActivitySegmentFit( String name, Long startTime, Long endTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        dest.writeString(this.name);
        dest.writeValue(this.startTime);
        dest.writeValue(this.endTime);
    }

    protected ActivitySegmentFit(Parcel in) {
        this.name = in.readString();
        this.startTime = (Long) in.readValue(Long.class.getClassLoader());
        this.endTime = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Creator<ActivitySegmentFit> CREATOR = new Creator<ActivitySegmentFit>() {
        public ActivitySegmentFit createFromParcel(Parcel source) {
            return new ActivitySegmentFit(source);
        }

        public ActivitySegmentFit[] newArray(int size) {
            return new ActivitySegmentFit[size];
        }
    };
}