package br.edu.uepb.nutes.activity_tracking_poc.data.repository.local.pref;

import net.openid.appauth.AuthState;

import br.edu.uepb.nutes.activity_tracking_poc.data.model.UserAccess;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.Nullable;

public interface PreferencesHelper {
    Completable addUserAccessOcariot(UserAccess userAccess);

    Completable addAuthStateFiBIt(AuthState authState);

    Completable addString(String key, String value);

    Completable addBoolean(String key, boolean value);

    Completable addInt(String key, int value);

    Completable addLong(String key, long value);

    Single<UserAccess> getUserAccessOcariot();

    Single<AuthState> getAuthStateFitBit();

    Completable removeUserAccessOcariot();

    Completable removeAuthStateFitBit();

    Completable removeItem(String key);
}
