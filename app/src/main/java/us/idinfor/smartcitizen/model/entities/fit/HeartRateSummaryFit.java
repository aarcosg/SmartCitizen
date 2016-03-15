package us.idinfor.smartcitizen.model.entities.fit;


public class HeartRateSummaryFit {

    Float average;
    Float min;
    Float max;
    Long startTime;
    Long endTime;

    public HeartRateSummaryFit() {
    }

    public HeartRateSummaryFit(Float average, Float min, Float max, Long startTime, Long endTime) {
        this.average = average;
        this.min = min;
        this.max = max;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Float getAverage() {
        return average;
    }

    public void setAverage(Float average) {
        this.average = average;
    }

    public Float getMin() {
        return min;
    }

    public void setMin(Float min) {
        this.min = min;
    }

    public Float getMax() {
        return max;
    }

    public void setMax(Float max) {
        this.max = max;
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
