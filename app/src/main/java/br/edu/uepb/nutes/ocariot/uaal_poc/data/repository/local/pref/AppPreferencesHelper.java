package br.edu.uepb.nutes.ocariot.uaal_poc.data.repository.local.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.securepreferences.SecurePreferences;

import br.edu.uepb.nutes.ocariot.uaal_poc.data.model.UserAccess;
import br.edu.uepb.nutes.ocariot.uaal_poc.exception.LocalPreferenceException;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

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
    public Completable addUserAccessOcariot(final UserAccess userAccess) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (userAccess.getAccessToken() == null || userAccess.getAccessToken().isEmpty())
                    emitter.onError(new LocalPreferenceException("attribute accessToken can not be null or empty!"));

                mPrefs.edit().putString(PREF_KEY_AUTH_STATE_OCARIOT,
                        userAccess.toJsonString()).apply();

                emitter.onComplete();
            }
        });
    }

    @Override
    public Completable addAuthStateFiBIt(final String authState) {
//        return Completable.create(new CompletableOnSubscribe() {
//            @Override
//            public void subscribe(CompletableEmitter emitter) throws Exception {
//                mPrefs.edit().putString(PREF_KEY_AUTH_STATE_FITBIT,
//                        authState.jsonSerializeString()).apply();
//                emitter.onComplete();
//            }
//        });
        return null;
    }

    @Override
    public Completable addString(final String key, final String value) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (key == null || key.isEmpty()) {
                    emitter.onError(new NullPointerException("key can not be null or empty!"));
                    return;
                }
                mPrefs.edit().putString(key, value).apply();
                emitter.onComplete();
            }
        });
    }

    @Override
    public Completable addBoolean(final String key, final boolean value) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (key == null || key.isEmpty()) {
                    emitter.onError(new NullPointerException("key can not be null or empty!"));
                    return;
                }
                mPrefs.edit().putBoolean(key, value).apply();
                emitter.onComplete();

            }
        });
    }

    @Override
    public Completable addInt(final String key, final int value) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {

                if (key == null || key.isEmpty()) {
                    emitter.onError(new NullPointerException("key can not be null or empty!"));
                    return;
                }
                mPrefs.edit().putInt(key, value).apply();
                emitter.onComplete();
            }
        });
    }

    @Override
    public Completable addLong(final String key, final long value) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {

                if (key == null || key.isEmpty()) {
                    emitter.onError(new NullPointerException("key can not be null or empty!"));
                    return;
                }
                mPrefs.edit().putLong(key, value).apply();
                emitter.onComplete();
            }
        });
    }

    @Override
    public Single<UserAccess> getUserAccessOcariot() {
        return Single.create(new SingleOnSubscribe<UserAccess>() {
            @Override
            public void subscribe(SingleEmitter<UserAccess> emitter) throws Exception {
                String userAccess = mPrefs.getString(PREF_KEY_AUTH_STATE_OCARIOT, null);

                if (userAccess != null) {
                    emitter.onSuccess(UserAccess.jsonDeserialize(userAccess));
                    return;
                }
                emitter.onError(new LocalPreferenceException("Data does not exist!"));
            }
        });
    }

    @Override
    public Single<String> getAuthStateFitBit() {
//        return Single.create(new SingleOnSubscribe<AuthState>() {
//            @Override
//            public void subscribe(SingleEmitter<AuthState> emitter) throws Exception {
//                String authStateJson = mPrefs.getString(PREF_KEY_AUTH_STATE_FITBIT, null);
//                if (authStateJson != null) {
//                    emitter.onSuccess(AuthState.jsonDeserialize(authStateJson));
//                    return;
//                }
//                emitter.onError(new LocalPreferenceException("Data does not exist!"));
//            }
//        });
        return null;
    }

    @Override
    public Completable removeUserAccessOcariot() {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                mPrefs.edit().remove(PREF_KEY_AUTH_STATE_OCARIOT).apply();
                emitter.onComplete();

            }
        });
    }

    @Override
    public Completable removeAuthStateFitBit() {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                mPrefs.edit().remove(PREF_KEY_AUTH_STATE_FITBIT).apply();
                emitter.onComplete();
            }
        });
    }

    @Override
    public Completable removeItem(final String key) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (key == null || key.isEmpty()) {
                    emitter.onError(new NullPointerException("key can not be null or empty!"));
                    return;
                }
                mPrefs.edit().remove(key).apply();
                emitter.onComplete();
            }
        });
    }
}
