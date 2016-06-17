package es.us.hermes.smartcitizen.mvp.presenter;

import es.us.hermes.smartcitizen.mvp.view.View;

public interface Presenter {

    void setView(View v);
    void onPause();

}