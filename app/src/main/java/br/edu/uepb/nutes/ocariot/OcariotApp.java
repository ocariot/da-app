package br.edu.uepb.nutes.ocariot;

import android.app.Application;
import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class OcariotApp extends Application {
    private static OcariotApp instance;
    private static FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = (OcariotApp) getApplicationContext();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        Timber.plant(new Timber.DebugTree() {
            @Override
            protected String createStackElementTag(@NotNull StackTraceElement element) {
                return super.createStackElementTag(element) + ":" + element.getLineNumber();
            }
        });
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public static FirebaseAnalytics getFirebaseAnalytics() {
        return OcariotApp.mFirebaseAnalytics;
    }
}
