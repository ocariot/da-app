package br.edu.uepb.nutes.activity_tracking_poc.data.repository.local.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.securepreferences.SecurePreferences;

import net.openid.appauth.AuthState;

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
            if (userAccess.getSubject() == null || userAccess.getSubject().isEmpty())
                throw new NullPointerException("attribute subject can not be null or empty!");

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
    public Single<String> getLoggedUserId() {
        return getUserAccessOcariot()
                .map(UserAccess::getSubject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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

//
//    @Override
//    public boolean setToken(@NonNull String token) {
//        if (token == null || token.isEmpty()) return false;
//
//        JWT jwt = new JWT(token);
//        if (jwt.getIssuer() == null ||
//                jwt.getSubject() == null ||
//                jwt.getExpiresAt() == null) return false;
//
//        if (jwt.getIssuer().toLowerCase().contains(UserAccessMode.OCARIOT_NAME))
//            return mPrefs.edit().putString(PREF_KEY_AUTH_STATE_OCARIOT, token).commit();
//
//        if (jwt.getIssuer().toLowerCase().contains(UserAccessMode.FITBIT_NAME))
//            return mPrefs.edit().putString(PREF_KEY_ACCESS_FITBIT_TOKEN, token).commit();
//
//        return false;
//    }

//    @Nullable
//    @Override
//    public String getToken(int mode) {
//        if (mode == UserAccessMode.OCARIOT)
//            return mPrefs.getString(PREF_KEY_AUTH_STATE_OCARIOT, null);
//
//        if (mode == UserAccessMode.FITBIT)
//            return mPrefs.getString(PREF_KEY_ACCESS_FITBIT_TOKEN, null);
//
//        return null;
//    }
//
//    @Override
//    public Single<Boolean> addAuthState(UserAccess userAccess) {
//        return Single.create((SingleOnSubscribe<Boolean>) emitter -> {
//            if (!UserAccessMode.isValid(userAccess.getMode()))
//                emitter.onError(new IllegalArgumentException("Access mode invalid!"));
//
//            if (userAccess.getMode() == UserAccessMode.OCARIOT_NAME)
//                mPrefs.edit().putString(PREF_KEY_AUTH_STATE_OCARIOT, userAccess).commit();
//
//            if (jwt.getIssuer().toLowerCase().contains(UserAccessMode.FITBIT_NAME))
//                return mPrefs.edit().putString(PREF_KEY_ACCESS_FITBIT_TOKEN, token).commit();
//
//        });
//    }


//    @Nullable
//    @Override
//    public UserAccess getUserAccessOcariot() {
//        String token = mPrefs.getString(PREF_KEY_AUTH_STATE_OCARIOT, null);
//        UserAccess userAccess = tokenToUserAccess(token);
//        if (userAccess != null) userAccess.setMode(UserAccessMode.OCARIOT);
//
//        return userAccess;
//    }
//
//    @Nullable
//    @Override
//    public UserAccess getAuthStateFitBit() {
//        String token = mPrefs.getString(PREF_KEY_ACCESS_FITBIT_TOKEN, null);
//        UserAccess userAccess = tokenToUserAccess(token);
//        if (userAccess != null) userAccess.setMode(UserAccessMode.FITBIT);
//
//        return userAccess;
//    }

//    @Override
//    public boolean removeUserAccess(int mode) {
//        if (mode == UserAccessMode.OCARIOT)
//            return mPrefs.edit().remove(PREF_KEY_AUTH_STATE_OCARIOT).commit();
//        if (mode == UserAccessMode.FITBIT)
//            return mPrefs.edit().remove(PREF_KEY_ACCESS_FITBIT_TOKEN).commit();
//        return false;
//    }
//
//    @Override
//    public boolean setString(String key, String value) {
//        if (key == null || key.isEmpty() || value == null) return false;
//        return mPrefs.edit().putString(key, value).commit();
//    }
//
//    @Override
//    public boolean setBoolean(String key, boolean value) {
//        if (key == null || key.isEmpty()) return false;
//        return mPrefs.edit().putBoolean(key, value).commit();
//    }
//
//    @Override
//    public boolean setInt(String key, int value) {
//        if (key == null || key.isEmpty()) return false;
//        return mPrefs.edit().putInt(key, value).commit();
//    }
//
//    @Override
//    public boolean setLong(String key, long value) {
//        if (key == null || key.isEmpty()) return false;
//        return mPrefs.edit().putLong(key, value).commit();
//    }
//
//    @Override
//    public boolean removeItem(String key) {
//        if (key == null) return false;
//        return mPrefs.edit().remove(key).commit();
//    }

//    private UserAccess tokenToUserAccess(String token) {
//        if (token == null) return null;
//
//        JWT jwt = new JWT(token);
//        return new UserAccess(
//                jwt.getSubject(),
//                token,
//                jwt.getExpiresAt().getTime(),
//                jwt.getClaim(UserAccess.ROLES_NAME).asList(String.class)
//        );
//    }
}
