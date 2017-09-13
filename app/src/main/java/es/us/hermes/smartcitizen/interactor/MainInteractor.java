package es.us.hermes.smartcitizen.interactor;

import es.us.hermes.smartcitizen.mvp.model.hermes.User;

public interface MainInteractor extends Interactor {

    User getUserFromPreferences();
    boolean isDrawerLearnedInPreferences();
    void setDrawerLearnedInPreferences(boolean learned);
}
