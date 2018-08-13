package br.edu.uepb.nutes.activity_tracking_poc.data.repository.local.pref;

import android.support.annotation.Nullable;

import br.edu.uepb.nutes.activity_tracking_poc.data.model.UserAccess;

public interface PreferencesHelper {
    boolean setUserAccess(UserAccess userAccess);

    boolean setToken(String token);

    boolean setString(String key, String value);

    boolean setBoolean(String key, boolean value);

    boolean setInt(String key, int value);

    boolean setLong(String key, long value);


    @Nullable
    String getToken(int mode);

    @Nullable
    UserAccess getUserAccessOcariot();

    @Nullable
    UserAccess getUserAccessFitBit();

    boolean removeUserAccess(int mode);

    boolean removeItem(String key);

}
