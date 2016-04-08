package us.idinfor.smartcitizen.interactor;

import us.idinfor.smartcitizen.data.api.hermes.entity.User;

public interface MainInteractor extends Interactor {

    User getUserFromPreferences();
    boolean isDrawerLearnedInPreferences();
    void setDrawerLearnedInPreferences(boolean learned);
}
