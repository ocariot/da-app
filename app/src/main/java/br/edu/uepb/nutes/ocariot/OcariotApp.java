package br.edu.uepb.nutes.ocariot;

import android.app.Application;
import android.content.Context;

public class OcariotApp extends Application {
    private static OcariotApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = (OcariotApp) getApplicationContext();
    }

    public static OcariotApp getAppContext() {
        return instance;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }
}
