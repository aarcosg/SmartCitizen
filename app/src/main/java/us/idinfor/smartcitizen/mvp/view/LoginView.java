package us.idinfor.smartcitizen.mvp.view;

public interface LoginView extends View {

    void showProgressDialog();
    void hideProgressDialog();
    void showGoogleSignInErrorMessage();
    void showLoginSuccessMessage();
    void initGoogleFitApi();
    void navigateToMainScreen();
    void finishActivity();
    void showErrorMessage(Throwable throwable);
    void doGoogleSignOut();
    void doGoogleRevokeAccess();
}