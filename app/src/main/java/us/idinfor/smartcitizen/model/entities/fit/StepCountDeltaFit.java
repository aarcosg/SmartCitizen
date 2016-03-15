package us.idinfor.smartcitizen.model.entities.fit;


public class StepCountDeltaFit {

    Integer steps;
    Long startTime;
    Long endTime;

    public StepCountDeltaFit() {
    }

    public StepCountDeltaFit(Integer steps, Long startTime, Long endTime) {
        this.steps = steps;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
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
