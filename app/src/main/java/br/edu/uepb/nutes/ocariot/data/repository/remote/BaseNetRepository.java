package br.edu.uepb.nutes.ocariot.data.repository.remote;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Base class to be inherited for repository implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public abstract class BaseNetRepository {
    protected Context mContext;
    private OkHttpClient.Builder mClient;

    public BaseNetRepository(Context mContext) {
        this.mContext = mContext;
    }

    private Cache provideHttpCache() {
        int cacheSize = 10 * 1024 * 1024;
        return new Cache(mContext.getCacheDir(), cacheSize);
    }

    private Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    private OkHttpClient provideOkHttpClient() {
        if (mClient == null) mClient = new OkHttpClient().newBuilder();
        mClient.followRedirects(false)
                .cache(provideHttpCache());

        return mClient.build();
    }

    protected void addInterceptor(Interceptor interceptor) {
        if (interceptor == null) return;
        if (mClient == null) mClient = new OkHttpClient().newBuilder();

        mClient.addInterceptor(interceptor);
    }

    protected Retrofit provideRetrofit(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(provideGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(provideOkHttpClient())
                .build();
    }
}
