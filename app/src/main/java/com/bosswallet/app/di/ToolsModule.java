package com.bosswallet.app.di;

import android.content.Context;

import com.bosswallet.app.C;
import com.bosswallet.app.interact.WalletConnectInteract;
import com.bosswallet.app.repository.PreferenceRepositoryType;
import com.bosswallet.app.service.GasService;
import com.bosswallet.app.service.RealmManager;
import com.bosswallet.app.walletconnect.AWWalletConnectClient;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;

@Module
@InstallIn(SingletonComponent.class)
public class ToolsModule
{

    @Singleton
    @Provides
    Gson provideGson()
    {
        return new Gson();
    }

    @Singleton
    @Provides
    OkHttpClient okHttpClient()
    {
        return new OkHttpClient.Builder()
                //.addInterceptor(new LogInterceptor())
                .connectTimeout(C.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(C.READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(C.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build();
    }

    @Singleton
    @Provides
    RealmManager provideRealmManager()
    {
        return new RealmManager();
    }

    @Singleton
    @Provides
    AWWalletConnectClient provideAWWalletConnectClient(@ApplicationContext Context context, WalletConnectInteract walletConnectInteract, PreferenceRepositoryType preferenceRepositoryType, GasService gasService)
    {
        return new AWWalletConnectClient(context, walletConnectInteract, preferenceRepositoryType, gasService);
    }
}
