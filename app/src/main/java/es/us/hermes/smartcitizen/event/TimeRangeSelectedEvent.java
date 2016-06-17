package es.us.hermes.smartcitizen.event;

public class TimeRangeSelectedEvent {

    private int timeRange;

    public TimeRangeSelectedEvent(int timeRange) {
        this.timeRange = timeRange;
    }

    public int getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(int timeRange) {
        this.timeRange = timeRange;
    }
}
