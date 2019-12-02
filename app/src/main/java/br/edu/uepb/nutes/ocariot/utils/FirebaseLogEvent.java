package br.edu.uepb.nutes.ocariot.utils;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import br.edu.uepb.nutes.ocariot.OcariotApp;

public class FirebaseLogEvent {
    private static final String USER_ID_EVENT_KEY = "user_id";
    private static final String CHILD_ID_EVENT_KEY = "child_id";
    private static final String USER_TYPE_EVENT_KEY = "user_type";
    private static final String FITBIT_AUTH_GRANTED_EVENT_KEY = "fitbit_auth_granted";
    private static final String FITBIT_AUTH_REVOKE_EVENT_KEY = "fitbit_auth_revoke";
    private static final String FITBIT_SYNC_EVENT_KEY = "fitbit_sync";

    public static void login(String userId, String userType) {
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID_EVENT_KEY, userId);
        bundle.putString(USER_TYPE_EVENT_KEY, userType);
        FirebaseAnalytics firebaseAnalytics = OcariotApp.getFirebaseAnalytics();
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
    }

    public static void fitbitAuthGranted(String childId) {
        Bundle bundle = new Bundle();
        bundle.putString(CHILD_ID_EVENT_KEY, childId);
        FirebaseAnalytics firebaseAnalytics = OcariotApp.getFirebaseAnalytics();
        firebaseAnalytics.logEvent(FITBIT_AUTH_GRANTED_EVENT_KEY, bundle);
    }

    public static void fitbitAuthRevoke(String childId) {
        Bundle bundle = new Bundle();
        bundle.putString(CHILD_ID_EVENT_KEY, childId);
        FirebaseAnalytics firebaseAnalytics = OcariotApp.getFirebaseAnalytics();
        firebaseAnalytics.logEvent(FITBIT_AUTH_REVOKE_EVENT_KEY, bundle);
    }

    public static void fitbitSync(String childId) {
        Bundle bundle = new Bundle();
        bundle.putString(CHILD_ID_EVENT_KEY, childId);
        FirebaseAnalytics firebaseAnalytics = OcariotApp.getFirebaseAnalytics();
        firebaseAnalytics.logEvent(FITBIT_SYNC_EVENT_KEY, bundle);
    }
}
