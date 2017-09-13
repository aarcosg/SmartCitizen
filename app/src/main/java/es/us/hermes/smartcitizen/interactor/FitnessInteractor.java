package es.us.hermes.smartcitizen.interactor;

import com.google.android.gms.fitness.request.DataReadRequest;

import es.us.hermes.smartcitizen.mvp.model.ActivityDetails;
import rx.Observable;

public interface FitnessInteractor extends Interactor {

    void initGoogleFitApi();
    void subscribeUserToGoogleFit();
    DataReadRequest.Builder buildFitDataReadRequest();
    Observable<ActivityDetails> getGoogleFitQueryResponse(long statTime, long endTime);

}
