package us.idinfor.smartcitizen.mvp.view;

import us.idinfor.smartcitizen.data.api.hermes.entity.User;

public interface MainView extends View {

    void navigateToLoginScreen();
    void finishActivity();
    void bindUser(User user);
    void bindDrawerLearned(boolean isDrawerLearned);
}