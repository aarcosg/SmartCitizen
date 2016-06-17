package es.us.hermes.smartcitizen.interactor;

import com.google.android.gms.fitness.request.DataReadRequest;

import rx.Observable;
import es.us.hermes.smartcitizen.data.api.google.fit.ActivityDetails;

public interface FitnessInteractor extends Interactor {

    void initGoogleFitApi();
    void subscribeUserToGoogleFit();
    DataReadRequest.Builder buildFitDataReadRequest();
    Observable<ActivityDetails> getGoogleFitQueryResponse(int timeRange);
    Observable<Boolean> requestMandatoryAppPermissions();
}
