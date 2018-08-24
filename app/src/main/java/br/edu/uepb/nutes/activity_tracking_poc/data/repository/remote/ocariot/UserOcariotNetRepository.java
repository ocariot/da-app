package br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.ocariot;

import android.content.Context;
import android.util.Log;

import com.auth0.android.jwt.JWT;

import br.edu.uepb.nutes.activity_tracking_poc.data.model.User;
import br.edu.uepb.nutes.activity_tracking_poc.data.model.UserAccess;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.BaseNetRepository;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UserOcariotNetRepository extends BaseNetRepository {
    private OcariotService ocariotService;
    private static UserOcariotNetRepository instance;

    private UserOcariotNetRepository(Context context) {
        super(context, null, OcariotService.BASE_URL_OCARIOT);

        ocariotService = super.retrofit.create(OcariotService.class);
    }

    public static synchronized UserOcariotNetRepository getInstance(Context context) {
        if (instance == null) instance = new UserOcariotNetRepository(context);
        return instance;
    }

    public Single<User> signup(User user) {
        return ocariotService.signup(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<UserAccess> auth(String username, String password) {
        return ocariotService.authUser(new User(username, password))
                .map((UserAccess userAccess) -> {
                    Log.w("TESTANDO", userAccess.toJsonString());
                    if (userAccess.getAccessToken() != null && !userAccess.getAccessToken().isEmpty()) {
                        JWT jwt = new JWT(userAccess.getAccessToken());
                        userAccess.setSubject(jwt.getSubject());
                        userAccess.setExpirationDate(jwt.getExpiresAt().getTime());
                        userAccess.setExpirationDate(jwt.getExpiresAt().getTime());
                        userAccess.setScopes(jwt.getClaim(UserAccess.ROLES_NAME).asList(String.class));
                    }
                    return userAccess;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<User> getById(String userId) {
        return ocariotService.getUserById(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
