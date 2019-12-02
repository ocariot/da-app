package br.edu.uepb.nutes.ocariot.utils;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

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
    protected void log(int priority, String tag, String message, Throwable throwable) {
        Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority);
        Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag);
        Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message);
        Crashlytics.setUserIdentifier(AppPreferencesHelper.getInstance().getUserAccessOcariot().getUserId());
        Crashlytics.setString("user", AppPreferencesHelper.getInstance().getUserAccessOcariot().getUserId());
        Crashlytics.setString("child", AppPreferencesHelper.getInstance().getLastSelectedChild().getId());
        if (throwable == null) {
            Crashlytics.logException(new Throwable(message));
            return;
        }
        Crashlytics.logException(throwable);

        if (priority == Log.INFO) {
            Answers.getInstance().logCustom(new CustomEvent(message));
        }
    }
}
