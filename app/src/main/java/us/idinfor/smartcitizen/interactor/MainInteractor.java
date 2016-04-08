package us.idinfor.smartcitizen.interactor;

import us.idinfor.smartcitizen.data.api.hermes.entity.User;
import us.idinfor.smartcitizen.mvp.model.UserNotFoundInPreferencesException;

public interface MainInteractor extends Interactor {

    User getUserInPreferences() throws UserNotFoundInPreferencesException;
    boolean isDrawerLearnedInPreferences();
    void setDrawerLearnedInPreferences(boolean learned);
}
