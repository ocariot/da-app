package br.edu.uepb.nutes.ocariot.data.repository.local.pref;

import net.openid.appauth.AuthState;

import br.edu.uepb.nutes.ocariot.data.model.Child;
import br.edu.uepb.nutes.ocariot.data.model.UserAccess;

/**
 * Interface for Preferences Helper.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public interface PreferencesHelper {
    boolean addUserAccessOcariot(final UserAccess userAccess);

    boolean addAuthStateFiBIt(final AuthState authState);

    boolean addChildProfile(final Child user);

    boolean addString(String key, String value);

    boolean addBoolean(String key, boolean value);

    boolean addInt(String key, int value);

    boolean addLong(String key, long value);

    UserAccess getUserAccessOcariot();

    AuthState getAuthStateFitBit();

    Child getUserProfile();

    boolean removeUserAccessOcariot();

    boolean removeAuthStateFitBit();

    boolean removeItem(String key);

    String getString(String key);

    boolean getBoolean(String key);
}
