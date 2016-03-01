package us.idinfor.smartcitizen.model.fit;


public class CaloriesExpendedFit {

    Float calories;
    Long startTime;
    Long endTime;

    public CaloriesExpendedFit() {
    }

    public CaloriesExpendedFit(Float calories, Long startTime, Long endTime) {
        this.calories = calories;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Float getCalories() {
        return calories;
    }

    public void setCalories(Float calories) {
        this.calories = calories;
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
