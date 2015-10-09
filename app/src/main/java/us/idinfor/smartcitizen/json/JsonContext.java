package us.idinfor.smartcitizen.json;


public class JsonContext {

    private Integer activity;
    private Double latitude;
    private Double longitude;
    private Long time;

    public JsonContext() {
    }

    public JsonContext(Integer activity, Double latitude, Double longitude, Long time) {
        this.activity = activity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public Integer getActivity() {
        return activity;
    }

    public void setActivity(Integer activity) {
        this.activity = activity;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
