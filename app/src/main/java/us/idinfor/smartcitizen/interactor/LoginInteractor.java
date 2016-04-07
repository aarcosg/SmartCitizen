package us.idinfor.smartcitizen.interactor;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface LoginInteractor extends Interactor {

    void loginOrRegisterUserInHermes(GoogleSignInAccount account);
}
