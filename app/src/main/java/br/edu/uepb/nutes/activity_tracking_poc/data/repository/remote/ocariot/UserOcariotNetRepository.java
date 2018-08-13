package br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.ocariot;

import android.content.Context;

import com.auth0.android.jwt.JWT;

import br.edu.uepb.nutes.activity_tracking_poc.data.model.User;
import br.edu.uepb.nutes.activity_tracking_poc.data.model.UserAccess;
import br.edu.uepb.nutes.activity_tracking_poc.data.model.UserAccessMode;
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

    public Single<UserAccess> auth(String email, String password) {
        return ocariotService.authUser(new User(email, password))
                .map(res -> {
                    if (res.getAccessToken() != null && !res.getAccessToken().isEmpty()) {
                        JWT jwt = new JWT(res.getAccessToken());
                        res.setId(jwt.getSubject());
                        res.setExpirationDate(jwt.getExpiresAt().getTime());
                        res.setExpirationDate(jwt.getExpiresAt().getTime());
                        res.setScopes(jwt.getClaim(UserAccess.ROLES_NAME).asList(String.class));
                        res.setMode(UserAccessMode.OCARIOT);
                    }
                    return res;
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
