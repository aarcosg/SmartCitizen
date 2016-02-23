package us.idinfor.smartcitizen.model;


public class ActivitySummary {

    Integer id;
    String name;
    Integer duration;
    Integer segments;

    public ActivitySummary() {
    }

    public ActivitySummary(Integer id, String name, Integer duration, Integer segments) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.segments = segments;
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
}
