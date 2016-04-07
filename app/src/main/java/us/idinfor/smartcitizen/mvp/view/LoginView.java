package us.idinfor.smartcitizen.mvp.view;

public interface LoginView extends View {

    void showProgressDialog();
    void hideProgressDialog();
    void showGoogleSignInError();
}