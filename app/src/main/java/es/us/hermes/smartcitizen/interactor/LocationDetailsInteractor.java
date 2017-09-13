package es.us.hermes.smartcitizen.interactor;

import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import rx.Observable;

public interface LocationDetailsInteractor extends Interactor {

    DataReadRequest.Builder buildFitDataReadRequest();
    Observable<List<LatLng>> getGoogleFitQueryResponse(long statTime, long endTime);

}
