package br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseNetRepository {
    protected Context mContext;
    protected Retrofit retrofit;
    protected OkHttpClient mOkHttpClient;

    public BaseNetRepository(Context mContext, OkHttpClient mOkHttpClient, String baseUrl) {
        this.mContext = mContext;
        this.mOkHttpClient = mOkHttpClient;
        this.retrofit = provideRetrofit(baseUrl);
    }

    private Cache provideHttpCache() {
        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(mContext.getCacheDir(), cacheSize);
        return cache;
    }

    private Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    protected OkHttpClient provideOkHttpClient() {
        if (mOkHttpClient == null)
            mOkHttpClient = new OkHttpClient();

        return mOkHttpClient.newBuilder()
                .followRedirects(false)
                .cache(provideHttpCache())
                .build();
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
