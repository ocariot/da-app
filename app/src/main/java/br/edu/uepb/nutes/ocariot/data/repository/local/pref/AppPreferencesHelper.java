package br.edu.uepb.nutes.ocariot.data.repository.local.pref;

import android.content.SharedPreferences;
import android.widget.Toast;

import java.io.IOException;
import java.security.GeneralSecurityException;

import br.edu.uepb.nutes.ocariot.BuildConfig;
import br.edu.uepb.nutes.ocariot.OcariotApp;
import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.FitBitAppData;
import br.edu.uepb.nutes.ocariot.exception.LocalPreferenceException;
import in.co.ophio.secure.core.KeyStoreKeyGenerator;
import in.co.ophio.secure.core.ObscuredPreferencesBuilder;

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
    private final String PREF_KEY_OCARIOT_API = "pref_key_ocariot_api";

    private static AppPreferencesHelper instance;
    private SharedPreferences mPrefs;

    private AppPreferencesHelper() {
        mPrefs = new ObscuredPreferencesBuilder()
                .setApplication(OcariotApp.getAppContext())
                .obfuscateValue(true)
                .obfuscateKey(true)
                .setSharePrefFileName(BuildConfig.PREFERENCES_FILENAME)
                .setSecret(getKey())
                .createSharedPrefs();
    }

    public static synchronized AppPreferencesHelper getInstance() {
        if (instance == null) instance = new AppPreferencesHelper();
        return instance;
    }

    /**
     * use getKey to get an encrypted Key for obscuring preferences
     *
     * @return String
     */
    private String getKey() {
        String secretKey;
        KeyStoreKeyGenerator keyGenerator = KeyStoreKeyGenerator.get(OcariotApp.getAppContext(),
                BuildConfig.APPLICATION_ID);
        try {
            secretKey = keyGenerator.loadOrGenerateKeys();
        } catch (GeneralSecurityException e) {
            Toast.makeText(OcariotApp.getContext(), "can't create key", Toast.LENGTH_SHORT).show();
            throw new RuntimeException("can't create  key");
        } catch (IOException e) {
            Toast.makeText(OcariotApp.getContext(), "can't create key", Toast.LENGTH_SHORT).show();
            throw new RuntimeException("can't create key");
        }
        return secretKey;
    }

    @Override
    public boolean addOcariotURL(final String url) {
        return mPrefs.edit().putString(PREF_KEY_OCARIOT_API, url).commit();
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
        return mPrefs.getString(PREF_KEY_OCARIOT_API, null);
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
        return mPrefs.edit().clear().commit();
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
