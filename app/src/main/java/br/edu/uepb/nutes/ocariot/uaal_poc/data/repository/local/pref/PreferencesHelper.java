package br.edu.uepb.nutes.ocariot.uaal_poc.data.repository.local.pref;

import net.openid.appauth.AuthState;

import br.edu.uepb.nutes.ocariot.uaal_poc.data.model.UserAccess;
import br.edu.uepb.nutes.ocariot.uaal_poc.exception.LocalPreferenceException;
import io.reactivex.Completable;
import io.reactivex.Single;

public interface PreferencesHelper {
    boolean addUserAccessOcariot(final UserAccess userAccess);

    boolean addAuthStateFiBIt(final AuthState authState);

    boolean addString(String key, String value);

    boolean addBoolean(String key, boolean value);

    boolean addInt(String key, int value);

    boolean addLong(String key, long value);

    UserAccess getUserAccessOcariot();

    AuthState getAuthStateFitBit();

    boolean removeUserAccessOcariot();

    boolean removeAuthStateFitBit();

    boolean removeItem(String key);
}
