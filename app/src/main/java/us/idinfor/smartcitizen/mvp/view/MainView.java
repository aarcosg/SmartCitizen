package us.idinfor.smartcitizen.mvp.view;

public interface MainView extends View {

    void navigateToLoginScreen();
    void bindDrawerLearned(boolean isDrawerLearned);
    void setupNavigationDrawer();
    void setupNavigationDrawerHeader();
    void openDrawerNotLearned();
    void selectDrawerItem();
    void setupBackgroundSyncService();
}