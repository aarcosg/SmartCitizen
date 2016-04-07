package us.idinfor.smartcitizen.interactor;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import rx.Observable;
import us.idinfor.smartcitizen.data.api.hermes.entity.User;

public interface LoginInteractor extends Interactor {

    Observable<User> loginOrRegisterUserInHermes(GoogleSignInAccount account);
    void saveLoggedUserInPreferences(User user);
}
