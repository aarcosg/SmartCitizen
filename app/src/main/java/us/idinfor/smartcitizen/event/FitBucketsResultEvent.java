package us.idinfor.smartcitizen.event;


import com.google.android.gms.fitness.data.Bucket;

import java.util.List;

public class FitBucketsResultEvent {

    private List<Bucket> buckets;

    public FitBucketsResultEvent(List<Bucket> buckets) {
        this.buckets = buckets;
    }

    public List<Bucket> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<Bucket> buckets) {
        this.buckets = buckets;
    }
}
