package br.edu.uepb.nutes.activity_tracking_poc.data.repository.local.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.auth0.android.jwt.JWT;
import com.securepreferences.SecurePreferences;

import br.edu.uepb.nutes.activity_tracking_poc.data.model.UserAccess;
import br.edu.uepb.nutes.activity_tracking_poc.data.model.UserAccessMode;

public class AppPreferencesHelper implements PreferencesHelper {
    private final String PREF_KEY_ACCESS_OCARIOT_TOKEN = "pref_key_access_ocariot_token";
    private final String PREF_KEY_ACCESS_FITBIT_TOKEN = "pref_key_access_fitbit_token";
    private final String PREF_KEY_CURRENT_USER_ID = "pref_key_current_user_id";

    public static AppPreferencesHelper instance;
    private SharedPreferences mPrefs;

    private AppPreferencesHelper(Context context) {
        mPrefs = new SecurePreferences(context);
    }

    public static synchronized AppPreferencesHelper getInstance(Context context) {
        if (instance == null) instance = new AppPreferencesHelper(context);
        return instance;
    }

    @Override
    public boolean setUserAccess(@NonNull UserAccess userAccess) {
        return setToken(userAccess.getAccessToken());
    }

    @Override
    public boolean setToken(@NonNull String token) {
        if (token == null || token.isEmpty()) return false;

        JWT jwt = new JWT(token);
        if (jwt.getIssuer() == null ||
                jwt.getSubject() == null ||
                jwt.getExpiresAt() == null) return false;

        if (jwt.getIssuer().toLowerCase().contains(UserAccessMode.OCARIOT_NAME))
            return mPrefs.edit().putString(PREF_KEY_ACCESS_OCARIOT_TOKEN, token).commit();

        if (jwt.getIssuer().toLowerCase().contains(UserAccessMode.FITBIT_NAME))
            return mPrefs.edit().putString(PREF_KEY_ACCESS_FITBIT_TOKEN, token).commit();

        return false;
    }

    @Nullable
    @Override
    public String getToken(int mode) {
        if (mode == UserAccessMode.OCARIOT)
            return mPrefs.getString(PREF_KEY_ACCESS_OCARIOT_TOKEN, null);

        if (mode == UserAccessMode.FITBIT)
            return mPrefs.getString(PREF_KEY_ACCESS_FITBIT_TOKEN, null);

        return null;
    }

    @Nullable
    @Override
    public UserAccess getUserAccessOcariot() {
        String token = mPrefs.getString(PREF_KEY_ACCESS_OCARIOT_TOKEN, null);
        UserAccess userAccess = tokenToUserAccess(token);
        if (userAccess != null) userAccess.setMode(UserAccessMode.OCARIOT);

        return userAccess;
    }

    @Nullable
    @Override
    public UserAccess getUserAccessFitBit() {
        String token = mPrefs.getString(PREF_KEY_ACCESS_FITBIT_TOKEN, null);
        UserAccess userAccess = tokenToUserAccess(token);
        if (userAccess != null) userAccess.setMode(UserAccessMode.FITBIT);

        return userAccess;
    }

    @Override
    public boolean removeUserAccess(int mode) {
        if (mode == UserAccessMode.OCARIOT)
            return mPrefs.edit().remove(PREF_KEY_ACCESS_OCARIOT_TOKEN).commit();
        if (mode == UserAccessMode.FITBIT)
            return mPrefs.edit().remove(PREF_KEY_ACCESS_FITBIT_TOKEN).commit();
        return false;
    }

    @Override
    public boolean setString(String key, String value) {
        if (key == null || key.isEmpty() || value == null) return false;
        return mPrefs.edit().putString(key, value).commit();
    }

    @Override
    public boolean setBoolean(String key, boolean value) {
        if (key == null || key.isEmpty()) return false;
        return mPrefs.edit().putBoolean(key, value).commit();
    }

    @Override
    public boolean setInt(String key, int value) {
        if (key == null || key.isEmpty()) return false;
        return mPrefs.edit().putInt(key, value).commit();
    }

    @Override
    public boolean setLong(String key, long value) {
        if (key == null || key.isEmpty()) return false;
        return mPrefs.edit().putLong(key, value).commit();
    }

    @Override
    public boolean removeItem(String key) {
        if (key == null) return false;
        return mPrefs.edit().remove(key).commit();
    }

    private UserAccess tokenToUserAccess(String token) {
        if (token == null) return null;

        JWT jwt = new JWT(token);
        return new UserAccess(
                jwt.getSubject(),
                token,
                jwt.getExpiresAt().getTime(),
                jwt.getClaim(UserAccess.ROLES_NAME).asList(String.class)
        );
    }
}
