package us.idinfor.smartcitizen.interactor;

import rx.Observable;
import us.idinfor.smartcitizen.data.api.google.fit.ActivityDetails;

public interface FitnessInteractor extends Interactor {

    void initGoogleFitApi();
    Observable<ActivityDetails> getGoogleFitQueryResponse(int timeRange);
}
