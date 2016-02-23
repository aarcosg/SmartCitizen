package us.idinfor.smartcitizen.model;


public class ActivitySummaryFit {

    String name;
    Integer duration;
    Integer segments;
    Long startTime;
    Long endTime;

    public ActivitySummaryFit() {
    }

    public ActivitySummaryFit( String name, Integer duration, Integer segments) {
        this.name = name;
        this.duration = duration;
        this.segments = segments;
    }

    public ActivitySummaryFit(String name, Integer duration, Integer segments, Long startTime, Long endTime) {
        this.name = name;
        this.duration = duration;
        this.segments = segments;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getSegments() {
        return segments;
    }

    public void setSegments(Integer segments) {
        this.segments = segments;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
}
