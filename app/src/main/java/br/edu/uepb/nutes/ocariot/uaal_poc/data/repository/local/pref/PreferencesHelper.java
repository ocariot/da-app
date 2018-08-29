package br.edu.uepb.nutes.ocariot.uaal_poc.data.repository.local.pref;

import br.edu.uepb.nutes.ocariot.uaal_poc.data.model.UserAccess;
import io.reactivex.Completable;
import io.reactivex.Single;

public interface PreferencesHelper {
    Completable addUserAccessOcariot(final UserAccess userAccess);

    Completable addAuthStateFiBIt(final String authState);

    Completable addString(String key, String value);

    Completable addBoolean(String key, boolean value);

    Completable addInt(String key, int value);

    Completable addLong(String key, long value);

    Single<UserAccess> getUserAccessOcariot();

    Single<String> getAuthStateFitBit();

    Completable removeUserAccessOcariot();

    Completable removeAuthStateFitBit();

    Completable removeItem(String key);
}
