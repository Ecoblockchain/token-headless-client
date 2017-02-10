package com.bakkenbaeck.token.network;


import com.bakkenbaeck.token.network.interceptor.LoggingInterceptor;
import com.bakkenbaeck.token.network.interceptor.SigningInterceptor;
import com.bakkenbaeck.token.network.interceptor.UserAgentInterceptor;
import com.squareup.moshi.Moshi;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import rx.schedulers.Schedulers;

public class IdService {

    private static IdService instance;

    private final IdInterface idInterface;
    private final OkHttpClient.Builder client;

    public static IdInterface getApi() {
        return get().idInterface;
    }

    private static IdService get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized IdService getSync() {
        if (instance == null) {
            instance = new IdService();
        }
        return instance;
    }

    private IdService() {
        final RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
        this.client = new OkHttpClient.Builder();

        addUserAgentHeader();
        addSigningInterceptor();
        addLogging();

        final Moshi moshi = new Moshi.Builder().build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://token-id-service.herokuapp.com")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(rxAdapter)
                .client(client.build())
                .build();
        this.idInterface = retrofit.create(IdInterface.class);
    }

    private void addUserAgentHeader() {
        this.client.addInterceptor(new UserAgentInterceptor());
    }

    private void addSigningInterceptor() {
        //this.client.addInterceptor(new SigningInterceptor());
    }

    private void addLogging() {
        final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new LoggingInterceptor());
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        this.client.addInterceptor(interceptor);
    }
}
