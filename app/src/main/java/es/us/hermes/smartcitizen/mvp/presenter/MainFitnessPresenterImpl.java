package es.us.hermes.smartcitizen.mvp.presenter;

import javax.inject.Inject;

import es.us.hermes.smartcitizen.interactor.MainFitnessInteractor;
import es.us.hermes.smartcitizen.mvp.view.MainFitnessView;
import es.us.hermes.smartcitizen.mvp.view.View;

public class MainFitnessPresenterImpl implements MainFitnessPresenter{

    private static final String TAG = MainFitnessPresenterImpl.class.getCanonicalName();

    private MainFitnessView mMainFitnessView;
    private final MainFitnessInteractor mMainFitnessInteractor;

    @Inject
    public MainFitnessPresenterImpl(MainFitnessInteractor mainFitnessInteractor){
        this.mMainFitnessInteractor = mainFitnessInteractor;
    }

    @Override
    public void setView(View v) {
        mMainFitnessView = (MainFitnessView) v;
    }


    @Override
    public void onPause() {

    }
}