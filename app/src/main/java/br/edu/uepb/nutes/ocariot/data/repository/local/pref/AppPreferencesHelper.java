package br.edu.uepb.nutes.ocariot.data.repository.local.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.securepreferences.SecurePreferences;

import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.FitBitAppData;
import br.edu.uepb.nutes.ocariot.exception.LocalPreferenceException;

/**
 * Class to perform operations on the device's shared preference.
 * The data is saved encrypted.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class AppPreferencesHelper implements PreferencesHelper {
    private final String PREF_KEY_AUTH_OCARIOT = "pref_key_user_access_ocariot";
    private final String PREF_KEY_LAST_SELECTED_CHILD = "pref_key_user_profile";
    private final String PREF_KEY_FITBIT_DATA = "pref_key_fitbit_data";

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
        if (userAccess.getAccessToken() == null || userAccess.getAccessToken().isEmpty()) {
            throw new LocalPreferenceException("attribute accessToken can not be null or empty!");
        }
        return mPrefs.edit().putString(PREF_KEY_AUTH_OCARIOT, userAccess.toString()).commit();
    }

    @Override
    public boolean addLastSelectedChild(Child user) {
        if (user == null) {
            throw new LocalPreferenceException("attribute user can not be null or empty!");
        }
        return mPrefs.edit().putString(PREF_KEY_LAST_SELECTED_CHILD, user.toString()).commit();
    }

    @Override
    public boolean addFitbitAppData(FitBitAppData fitBitAppData) {
        if (fitBitAppData == null) {
            throw new LocalPreferenceException("attribute fitBitAppData can not be null or empty!");
        }
        return mPrefs.edit().putString(PREF_KEY_FITBIT_DATA, fitBitAppData.toString()).commit();
    }

    @Override
    public boolean addBoolean(final String key, final boolean value) {
        checkKey(key);
        return mPrefs.edit().putBoolean(key, value).commit();
    }

    @Override
    public boolean addInt(String key, int value) {
        checkKey(key);
        return mPrefs.edit().putInt(key, value).commit();
    }

    @Override
    public UserAccess getUserAccessOcariot() {
        String userAccess = mPrefs.getString(PREF_KEY_AUTH_OCARIOT, null);
        return UserAccess.jsonDeserialize(userAccess);
    }

    @Override
    public Child getLastSelectedChild() {
        String user = mPrefs.getString(PREF_KEY_LAST_SELECTED_CHILD, null);
        return Child.jsonDeserialize(user);
    }

    @Override
    public FitBitAppData getFitbitAppData() {
        String fitbitAppData = mPrefs.getString(PREF_KEY_FITBIT_DATA, null);
        if (fitbitAppData != null) return FitBitAppData.jsonDeserialize(fitbitAppData);
        return null;
    }

    @Override
    public boolean removeSession() {
        return mPrefs.edit().remove(PREF_KEY_AUTH_OCARIOT).commit() &&
                mPrefs.edit().remove(PREF_KEY_LAST_SELECTED_CHILD).commit();
    }

    @Override
    public boolean getBoolean(String key) {
        checkKey(key);
        return mPrefs.getBoolean(key, false);
    }

    @Override
    public int getInt(String key) {
        checkKey(key);
        return mPrefs.getInt(key, -1);
    }

    private void checkKey(String key) {
        if (key == null || key.isEmpty()) {
            throw new NullPointerException("key can not be null or empty!");
        }
    }
}
