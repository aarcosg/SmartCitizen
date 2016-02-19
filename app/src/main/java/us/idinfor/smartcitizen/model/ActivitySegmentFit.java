package us.idinfor.smartcitizen.model;


public class ActivitySegmentFit {

    Integer id;
    String name;
    Long startTime;
    Long endTime;

    public ActivitySegmentFit() {
    }

    public ActivitySegmentFit(Integer id, String name, Long startTime, Long endTime) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
