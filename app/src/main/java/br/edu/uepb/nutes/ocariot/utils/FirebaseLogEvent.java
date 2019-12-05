package br.edu.uepb.nutes.ocariot.utils;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import br.edu.uepb.nutes.ocariot.OcariotApp;

public final class FirebaseLogEvent {
    private static final String USER_TYPE_EVENT_KEY = "user_type";
    private static final String FITBIT_AUTH_GRANTED_EVENT_KEY = "fitbit_auth_granted";
    private static final String FITBIT_AUTH_REVOKE_EVENT_KEY = "fitbit_auth_revoke";
    private static final String FITBIT_SYNC_EVENT_KEY = "fitbit_sync";

    private FirebaseLogEvent() {
        throw new IllegalStateException("Utility class. Does not allow inheritance or instances to be created!");
    }

    public static void login(String userType) {
        Bundle bundle = new Bundle();
        bundle.putString(USER_TYPE_EVENT_KEY, userType);
        FirebaseAnalytics firebaseAnalytics = OcariotApp.getFirebaseAnalytics();
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
    }

    public static void fitbitAuthGranted() {
        FirebaseAnalytics firebaseAnalytics = OcariotApp.getFirebaseAnalytics();
        firebaseAnalytics.logEvent(FITBIT_AUTH_GRANTED_EVENT_KEY, null);
    }

    public static void fitbitAuthRevoke() {
        FirebaseAnalytics firebaseAnalytics = OcariotApp.getFirebaseAnalytics();
        firebaseAnalytics.logEvent(FITBIT_AUTH_REVOKE_EVENT_KEY, null);
    }

    public static void fitbitSync() {
        FirebaseAnalytics firebaseAnalytics = OcariotApp.getFirebaseAnalytics();
        firebaseAnalytics.logEvent(FITBIT_SYNC_EVENT_KEY, null);
    }
}
