package br.edu.uepb.nutes.activity_tracking_poc.data.repository.local.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.securepreferences.SecurePreferences;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationRequest;

import br.edu.uepb.nutes.activity_tracking_poc.data.model.UserAccess;
import br.edu.uepb.nutes.activity_tracking_poc.exception.LocalPreferenceException;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AppPreferencesHelper implements PreferencesHelper {
    private final String PREF_KEY_AUTH_STATE_OCARIOT = "pref_key_user_access_ocariot";
    private final String PREF_KEY_AUTH_STATE_FITBIT = "pref_key_access_fitbit";

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
    public Completable addUserAccessOcariot(UserAccess userAccess) {
        return Completable.create(emitter -> {
            if (userAccess.getAccessToken() == null || userAccess.getAccessToken().isEmpty())
                emitter.onError(new LocalPreferenceException("attribute accessToken can not be null or empty!"));

            mPrefs.edit().putString(PREF_KEY_AUTH_STATE_OCARIOT,
                    userAccess.toJsonString()).apply();

            emitter.onComplete();
        });
    }

    @Override
    public Completable addAuthStateFiBIt(AuthState authState) {
        return Completable.create(emitter -> {
            mPrefs.edit().putString(PREF_KEY_AUTH_STATE_FITBIT,
                    authState.jsonSerializeString()).apply();
            emitter.onComplete();
        });
    }

    @Override
    public Completable addString(String key, String value) {
        return Completable.create(emitter -> {
            if (key == null || key.isEmpty()) {
                emitter.onError(new NullPointerException("key can not be null or empty!"));
                return;
            }
            mPrefs.edit().putString(key, value).apply();
            emitter.onComplete();
        });
    }

    @Override
    public Completable addBoolean(String key, boolean value) {
        return Completable.create(emitter -> {
            if (key == null || key.isEmpty()) {
                emitter.onError(new NullPointerException("key can not be null or empty!"));
                return;
            }
            mPrefs.edit().putBoolean(key, value).apply();
            emitter.onComplete();
        });
    }

    @Override
    public Completable addInt(String key, int value) {
        return Completable.create(emitter -> {
            if (key == null || key.isEmpty()) {
                emitter.onError(new NullPointerException("key can not be null or empty!"));
                return;
            }
            mPrefs.edit().putInt(key, value).apply();
            emitter.onComplete();
        });
    }

    @Override
    public Completable addLong(String key, long value) {
        return Completable.create(emitter -> {
            if (key == null || key.isEmpty()) {
                emitter.onError(new NullPointerException("key can not be null or empty!"));
                return;
            }
            mPrefs.edit().putLong(key, value).apply();
            emitter.onComplete();
        });
    }

    @Override
    public Single<UserAccess> getUserAccessOcariot() {
        return Single.create(emitter -> {
            String userAccess = mPrefs.getString(PREF_KEY_AUTH_STATE_OCARIOT, null);

            if (userAccess != null) {
                emitter.onSuccess(UserAccess.jsonDeserialize(userAccess));
                return;
            }
            emitter.onError(new LocalPreferenceException("Data does not exist!"));
        });
    }

    @Override
    public Single<AuthState> getAuthStateFitBit() {
        return Single.create(emitter -> {
            String authStateJson = mPrefs.getString(PREF_KEY_AUTH_STATE_FITBIT, null);
            if (authStateJson != null) {
                emitter.onSuccess(AuthState.jsonDeserialize(authStateJson));
                return;
            }
            emitter.onError(new LocalPreferenceException("Data does not exist!"));
        });
    }

    @Override
    public Completable removeUserAccessOcariot() {
        return Completable.create(emitter -> {
            mPrefs.edit().remove(PREF_KEY_AUTH_STATE_OCARIOT).apply();
            emitter.onComplete();
        });
    }

    @Override
    public Completable removeAuthStateFitBit() {
        return Completable.create(emitter -> {
            mPrefs.edit().remove(PREF_KEY_AUTH_STATE_FITBIT).apply();
            emitter.onComplete();
        });
    }

    @Override
    public Completable removeItem(String key) {
        return Completable.create(emitter -> {
            if (key == null || key.isEmpty()) {
                emitter.onError(new NullPointerException("key can not be null or empty!"));
                return;
            }
            mPrefs.edit().remove(key).apply();
            emitter.onComplete();
        });
    }
}
