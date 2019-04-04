package br.edu.uepb.nutes.ocariot.data.repository.local.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.securepreferences.SecurePreferences;

import net.openid.appauth.AuthState;

import org.json.JSONException;

import br.edu.uepb.nutes.ocariot.data.model.Child;
import br.edu.uepb.nutes.ocariot.data.model.UserAccess;
import br.edu.uepb.nutes.ocariot.exception.LocalPreferenceException;

/**
 * Class to perform operations on the device's shared preference.
 * The data is saved encrypted.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class AppPreferencesHelper implements PreferencesHelper {
    private final String PREF_KEY_AUTH_STATE_OCARIOT = "pref_key_user_access_ocariot";
    private final String PREF_KEY_AUTH_STATE_FITBIT = "pref_key_access_fitbit";
    private final String PREF_KEY_USER_PROFILE = "pref_key_user_profile";

    private static AppPreferencesHelper instance;
    private SharedPreferences mPrefs;

    private AppPreferencesHelper(Context context) {
        mPrefs = new SecurePreferences(context);
    }

    public static synchronized AppPreferencesHelper getInstance(Context context) {
        if (instance == null) instance = new AppPreferencesHelper(context);
        return instance;
    }

    @Override
    public boolean addUserAccessOcariot(final UserAccess userAccess) {
        if (userAccess.getAccessToken() == null || userAccess.getAccessToken().isEmpty())
            throw new LocalPreferenceException("attribute accessToken can not be null or empty!");

        return mPrefs.edit().putString(PREF_KEY_AUTH_STATE_OCARIOT,
                userAccess.toJsonString()).commit();
    }

    @Override
    public boolean addAuthStateFiBIt(final AuthState authState) {
        if (authState == null)
            throw new LocalPreferenceException("attribute authState can not be null or empty!");

        return mPrefs.edit().putString(PREF_KEY_AUTH_STATE_FITBIT,
                authState.jsonSerializeString()).commit();
    }

    @Override
    public boolean addChildProfile(Child user) {
        if (user == null) {
            throw new LocalPreferenceException("attribute user can not be null or empty!");
        }

        return mPrefs.edit().putString(PREF_KEY_USER_PROFILE,
                user.toJsonString()).commit();
    }

    @Override
    public boolean addString(final String key, final String value) {
        checkKey(key);
        return mPrefs.edit().putString(key, value).commit();
    }

    @Override
    public boolean addBoolean(final String key, final boolean value) {
        checkKey(key);
        return mPrefs.edit().putBoolean(key, value).commit();
    }

    @Override
    public boolean addInt(final String key, final int value) {
        checkKey(key);
        return mPrefs.edit().putInt(key, value).commit();
    }

    @Override
    public boolean addLong(final String key, final long value) {
        checkKey(key);
        return mPrefs.edit().putLong(key, value).commit();
    }

    @Override
    public UserAccess getUserAccessOcariot() {
        String userAccess = mPrefs.getString(PREF_KEY_AUTH_STATE_OCARIOT, null);
        return UserAccess.jsonDeserialize(userAccess);
    }

    @Override
    public AuthState getAuthStateFitBit() {
        String authStateJson = mPrefs.getString(PREF_KEY_AUTH_STATE_FITBIT, null);
        try {
            if (authStateJson != null)
                return AuthState.jsonDeserialize(authStateJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Child getUserProfile() {
        String user = mPrefs.getString(PREF_KEY_USER_PROFILE, null);
        return Child.jsonDeserialize(user);
    }

    @Override
    public boolean removeUserAccessOcariot() {
        return mPrefs.edit().remove(PREF_KEY_AUTH_STATE_OCARIOT).commit();
    }


    @Override
    public boolean removeAuthStateFitBit() {
        return mPrefs.edit().remove(PREF_KEY_AUTH_STATE_FITBIT).commit();
    }

    @Override
    public boolean removeItem(final String key) {
        checkKey(key);
        return mPrefs.edit().remove(key).commit();
    }

    @Override
    public String getString(String key) {
        checkKey(key);
        return mPrefs.getString(key, null);
    }


    @Override
    public boolean getBoolean(String key) {
        checkKey(key);
        return mPrefs.getBoolean(key, false);
    }

    private void checkKey(String key) {
        if (key == null || key.isEmpty())
            throw new NullPointerException("key can not be null or empty!");
    }
}
