package br.edu.uepb.nutes.ocariot;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.jetbrains.annotations.NotNull;

import br.edu.uepb.nutes.ocariot.utils.CrashReportingTree;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class OcariotApp extends Application {
    private static OcariotApp instance;
    private static FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = (OcariotApp) getApplicationContext();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        // Crashlytics
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());

        // Logs Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected String createStackElementTag(@NotNull StackTraceElement element) {
                    return super.createStackElementTag(element) + ":" + element.getLineNumber();
                }
            });
        }
        Fabric.with(this, new Crashlytics());
        Timber.plant(new CrashReportingTree());
    }

    public static OcariotApp getAppContext() {
        return instance;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public static FirebaseAnalytics getFirebaseAnalytics() {
        return OcariotApp.mFirebaseAnalytics;
    }
}
