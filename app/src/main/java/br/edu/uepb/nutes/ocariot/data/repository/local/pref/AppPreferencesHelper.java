package br.edu.uepb.nutes.ocariot.data.repository.local.pref;

import android.content.SharedPreferences;

import com.securepreferences.SecurePreferences;

import br.edu.uepb.nutes.ocariot.BuildConfig;
import br.edu.uepb.nutes.ocariot.OcariotApp;
import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.FitBitAppData;
import br.edu.uepb.nutes.ocariot.exception.LocalPreferenceException;
import timber.log.Timber;

/**
 * Class to perform operations on the device's shared preference.
 * The data is saved encrypted.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class AppPreferencesHelper implements PreferencesHelper {
    private static final String PREF_KEY_AUTH_OCARIOT = "pref_key_user_access_ocariot";
    private static final String PREF_KEY_LAST_SELECTED_CHILD = "pref_key_user_profile";
    private static final String PREF_KEY_FITBIT_DATA = "pref_key_fitbit_data";
    private static final String PREF_KEY_OCARIOT_API = "pref_key_ocariot_api";
    private static final String PREF_KEY_CHANGED_OCARIOT_API = "pref_key_changed_ocariot_api";

    private static AppPreferencesHelper instance;
    private SharedPreferences mPrefs;

    private AppPreferencesHelper() {
        mPrefs = new SecurePreferences(OcariotApp.getAppContext(), "",
                BuildConfig.PREFERENCES_FILENAME);
    }

    public static synchronized AppPreferencesHelper getInstance() {
        if (instance == null) instance = new AppPreferencesHelper();
        return instance;
    }

    @Override
    public boolean addOcariotURL(final String url) {
        return mPrefs.edit().putString(PREF_KEY_OCARIOT_API, url).commit();
    }

    @Override
    public boolean changedOcariotUrl(boolean value) {
        return mPrefs.edit().putBoolean(PREF_KEY_CHANGED_OCARIOT_API, value).commit();
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
    public String getOcariotURL() {
        try {
            return mPrefs.getString(PREF_KEY_OCARIOT_API, null);
        } catch (ClassCastException | NullPointerException ex) {
            Timber.e(ex);
            return null;
        }
    }

    @Override
    public boolean changedOcariotUrl() {
        try {
            return mPrefs.getBoolean(PREF_KEY_CHANGED_OCARIOT_API, false);
        } catch (ClassCastException | NullPointerException ex) {
            Timber.e(ex);
            return false;
        }
    }

    @Override
    public UserAccess getUserAccessOcariot() {
        try {
            String userAccess = mPrefs.getString(PREF_KEY_AUTH_OCARIOT, null);
            return UserAccess.jsonDeserialize(userAccess);
        } catch (ClassCastException | NullPointerException ex) {
            Timber.e(ex);
        }
        return null;
    }

    @Override
    public Child getLastSelectedChild() {
        try {
            String user = mPrefs.getString(PREF_KEY_LAST_SELECTED_CHILD, null);
            return Child.jsonDeserialize(user);
        } catch (ClassCastException | NullPointerException ex) {
            Timber.e(ex);
        }
        return null;
    }

    @Override
    public FitBitAppData getFitbitAppData() {
        try {
            String fitbitAppData = mPrefs.getString(PREF_KEY_FITBIT_DATA, null);
            if (fitbitAppData != null) return FitBitAppData.jsonDeserialize(fitbitAppData);
        } catch (ClassCastException | NullPointerException ex) {
            Timber.e(ex);
        }
        return null;
    }

    @Override
    public boolean removeSession() {
        return mPrefs.edit().clear().commit();
    }

    @Override
    public boolean getBoolean(String key) {
        checkKey(key);
        try {
            return mPrefs.getBoolean(key, false);
        } catch (ClassCastException | NullPointerException ex) {
            Timber.e(ex);
            return false;
        }
    }

    @Override
    public int getInt(String key) {
        checkKey(key);
        try {
            return mPrefs.getInt(key, -1);
        } catch (ClassCastException | NumberFormatException | NullPointerException ex) {
            Timber.e(ex);
            return -1;
        }
    }

    private void checkKey(String key) {
        if (key == null || key.isEmpty()) {
            throw new NullPointerException("key can not be null or empty!");
        }
    }
}
