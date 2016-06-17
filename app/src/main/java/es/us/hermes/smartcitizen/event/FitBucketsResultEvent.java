package es.us.hermes.smartcitizen.event;


import com.google.android.gms.fitness.data.Bucket;

import java.util.List;

public class FitBucketsResultEvent {

    private int queryType;
    private List<Bucket> buckets;

    public FitBucketsResultEvent(int queryType, List<Bucket> buckets) {
        this.queryType = queryType;
        this.buckets = buckets;
    }

    public int getQueryType() {
        return queryType;
    }

    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }

    public List<Bucket> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<Bucket> buckets) {
        this.buckets = buckets;
    }
}
