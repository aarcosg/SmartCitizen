package es.us.hermes.smartcitizen.event;


import com.google.android.gms.fitness.data.DataSet;

import java.util.List;

public class FitDataSetsResultEvent {

    private int queryType;
    private List<DataSet> dataSets;

    public FitDataSetsResultEvent(int queryType, List<DataSet> dataSets) {
        this.queryType = queryType;
        this.dataSets = dataSets;
    }

    public int getQueryType() {
        return queryType;
    }

    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }

    public List<DataSet> getDataSets() {
        return dataSets;
    }

    public void setDataSets(List<DataSet> dataSets) {
        this.dataSets = dataSets;
    }
}
