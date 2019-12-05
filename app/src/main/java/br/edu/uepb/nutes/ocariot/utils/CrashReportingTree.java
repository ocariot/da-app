package br.edu.uepb.nutes.ocariot.utils;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.jetbrains.annotations.Nullable;

import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import retrofit2.HttpException;
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
    protected void log(int priority, String tag, String message, Throwable throwable) {
        Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority);
        Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag);
        Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message);
        Crashlytics.setUserIdentifier(AppPreferencesHelper.getInstance().getUserAccessOcariot().getUserId());

        if (throwable instanceof HttpException) {
            HttpException httpEx = ((HttpException) throwable);
            Crashlytics.setString("http_message", httpEx.getMessage());
        }

        if (throwable == null) {
            Crashlytics.logException(new Throwable(message));
            return;
        }
        Crashlytics.logException(throwable);
    }
}
