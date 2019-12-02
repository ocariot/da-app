package br.edu.uepb.nutes.ocariot.data.repository.remote;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import br.edu.uepb.nutes.ocariot.OcariotApp;
import br.edu.uepb.nutes.ocariot.exception.ConnectivityException;
import okhttp3.Interceptor;
import okhttp3.Response;

public class NetworkConnectionInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        if (!isConnected()) throw new ConnectivityException();
        return chain.proceed(chain.request().newBuilder().build());
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) OcariotApp.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }
}
