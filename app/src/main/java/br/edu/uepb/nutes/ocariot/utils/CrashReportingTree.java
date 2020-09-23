package br.edu.uepb.nutes.ocariot.utils;

import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import timber.log.Timber;

public class CrashReportingTree extends Timber.Tree {
    private static final String CRASHLYTICS_KEY_PRIORITY = "priority";
    private static final String CRASHLYTICS_KEY_TAG = "tag";
    private static final String CRASHLYTICS_KEY_MESSAGE = "message";

    @Override
    protected boolean isLoggable(@Nullable String tag, int priority) {
        return priority == Log.ERROR || priority == Log.WARN;
    }

    @Override
    protected void log(int priority, String tag, @NotNull String message, Throwable throwable) {
        FirebaseCrashlytics.getInstance().setCustomKey(CRASHLYTICS_KEY_PRIORITY, priority);
        FirebaseCrashlytics.getInstance().setCustomKey(CRASHLYTICS_KEY_TAG, tag);
        FirebaseCrashlytics.getInstance().setCustomKey(CRASHLYTICS_KEY_MESSAGE, message);

        if (AppPreferencesHelper.getInstance().getUserAccessOcariot() != null) {
            FirebaseCrashlytics.getInstance().setUserId(
                    AppPreferencesHelper.getInstance().getUserAccessOcariot().getUserId()
            );
        }

        if (throwable == null) {
            FirebaseCrashlytics.getInstance().log(message);
            return;
        }

        FirebaseCrashlytics.getInstance().recordException(throwable);
    }
}
