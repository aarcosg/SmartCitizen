package es.us.hermes.smartcitizen.interactor;

import com.google.android.gms.fitness.request.DataReadRequest;

import es.us.hermes.smartcitizen.data.api.google.fit.ActivityDetails;
import rx.Observable;

public interface FitnessInteractor extends Interactor {

    void initGoogleFitApi();
    void subscribeUserToGoogleFit();
    DataReadRequest.Builder buildFitDataReadRequest();
    Observable<ActivityDetails> getGoogleFitQueryResponse(int timeRange);

}
