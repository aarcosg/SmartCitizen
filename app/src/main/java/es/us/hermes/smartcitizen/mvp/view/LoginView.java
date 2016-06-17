package es.us.hermes.smartcitizen.mvp.view;

public interface LoginView extends View {

    void showProgressDialog();
    void hideProgressDialog();
    void showGoogleSignInErrorMessage();
    void showLoginSuccessMessage();
    void navigateToMainScreen();
    void finishActivity();
    void showErrorMessage(Throwable throwable);
    void doGoogleSignOut();
    void doGoogleRevokeAccess();
}