package us.idinfor.smartcitizen.model.fit;


public class ActivitySegmentFit implements ISampleFit {

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
}
