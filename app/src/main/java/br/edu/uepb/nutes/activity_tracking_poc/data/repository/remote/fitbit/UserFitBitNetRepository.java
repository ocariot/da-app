package br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.fitbit;

import android.content.Context;

import net.openid.appauth.AuthState;

import br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.BaseNetRepository;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.ocariot.OcariotService;
import io.reactivex.Single;

public class UserFitBitNetRepository extends BaseNetRepository {
    private FitBitService fitBitService;
    private static UserFitBitNetRepository instance;

    private UserFitBitNetRepository(Context context) {
        super(context, null, OcariotService.BASE_URL_OCARIOT);

        fitBitService = super.retrofit.create(FitBitService.class);
    }

    public static synchronized UserFitBitNetRepository getInstance(Context context) {
        if (instance == null) instance = new UserFitBitNetRepository(context);
        return instance;
    }

    public Single<AuthState> getAuthState() {
        return Single.create(emitter -> {

            emitter.onSuccess(null);
        });
    }

    public Single<AuthState> performActionWithFreshTokens() {
        return Single.create(emitter -> {


            emitter.onSuccess(null);
        });
    }
}
