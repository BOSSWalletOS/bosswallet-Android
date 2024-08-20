package com.bosswallet.app.di;

import static com.bosswallet.app.service.KeystoreAccountService.KEYSTORE_FOLDER;

import android.content.Context;

import com.bosswallet.app.service.AnalyticsService;
import com.bosswallet.app.repository.CoinbasePayRepository;
import com.bosswallet.app.repository.CoinbasePayRepositoryType;
import com.bosswallet.app.repository.EthereumNetworkRepository;
import com.bosswallet.app.repository.EthereumNetworkRepositoryType;
import com.bosswallet.app.repository.OnRampRepository;
import com.bosswallet.app.repository.OnRampRepositoryType;
import com.bosswallet.app.repository.PreferenceRepositoryType;
import com.bosswallet.app.repository.SharedPreferenceRepository;
import com.bosswallet.app.repository.SwapRepository;
import com.bosswallet.app.repository.SwapRepositoryType;
import com.bosswallet.app.repository.TokenLocalSource;
import com.bosswallet.app.repository.TokenRepository;
import com.bosswallet.app.repository.TokenRepositoryType;
import com.bosswallet.app.repository.TokensMappingRepository;
import com.bosswallet.app.repository.TokensMappingRepositoryType;
import com.bosswallet.app.repository.TokensRealmSource;
import com.bosswallet.app.repository.TransactionLocalSource;
import com.bosswallet.app.repository.TransactionRepository;
import com.bosswallet.app.repository.TransactionRepositoryType;
import com.bosswallet.app.repository.TransactionsRealmCache;
import com.bosswallet.app.repository.WalletDataRealmSource;
import com.bosswallet.app.repository.WalletRepository;
import com.bosswallet.app.repository.WalletRepositoryType;
import com.bosswallet.app.service.AccountKeystoreService;
import com.bosswallet.app.service.AlphaWalletNotificationService;
import com.bosswallet.app.service.BossWalletService;
import com.bosswallet.app.service.AnalyticsServiceType;
import com.bosswallet.app.service.AssetDefinitionService;
import com.bosswallet.app.service.GasService;
import com.bosswallet.app.service.IPFSService;
import com.bosswallet.app.service.IPFSServiceType;
import com.bosswallet.app.service.KeyService;
import com.bosswallet.app.service.KeystoreAccountService;
import com.bosswallet.app.service.NotificationService;
import com.bosswallet.app.service.OpenSeaService;
import com.bosswallet.app.service.RealmManager;
import com.bosswallet.app.service.SwapService;
import com.bosswallet.app.service.TickerService;
import com.bosswallet.app.service.TokensService;
import com.bosswallet.app.service.TransactionNotificationService;
import com.bosswallet.app.service.TransactionsNetworkClient;
import com.bosswallet.app.service.TransactionsNetworkClientType;
import com.bosswallet.app.service.TransactionsService;
import com.google.gson.Gson;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;

@Module
@InstallIn(SingletonComponent.class)
public class RepositoriesModule
{
    @Singleton
    @Provides
    PreferenceRepositoryType providePreferenceRepository(@ApplicationContext Context context)
    {
        return new SharedPreferenceRepository(context);
    }

    @Singleton
    @Provides
    AccountKeystoreService provideAccountKeyStoreService(@ApplicationContext Context context, KeyService keyService)
    {
        File file = new File(context.getFilesDir(), KEYSTORE_FOLDER);
        return new KeystoreAccountService(file, context.getFilesDir(), keyService);
    }

    @Singleton
    @Provides
    TickerService provideTickerService(OkHttpClient httpClient, PreferenceRepositoryType sharedPrefs, TokenLocalSource localSource)
    {
        return new TickerService(httpClient, sharedPrefs, localSource);
    }

    @Singleton
    @Provides
    EthereumNetworkRepositoryType provideEthereumNetworkRepository(
        PreferenceRepositoryType preferenceRepository,
        @ApplicationContext Context context
    )
    {
        return new EthereumNetworkRepository(preferenceRepository, context);
    }

    @Singleton
    @Provides
    WalletRepositoryType provideWalletRepository(
        PreferenceRepositoryType preferenceRepositoryType,
        AccountKeystoreService accountKeystoreService,
        EthereumNetworkRepositoryType networkRepository,
        WalletDataRealmSource walletDataRealmSource,
        KeyService keyService)
    {
        return new WalletRepository(
            preferenceRepositoryType, accountKeystoreService, networkRepository, walletDataRealmSource, keyService);
    }

    @Singleton
    @Provides
    TransactionRepositoryType provideTransactionRepository(
        EthereumNetworkRepositoryType networkRepository,
        AccountKeystoreService accountKeystoreService,
        TransactionLocalSource inDiskCache,
        TransactionsService transactionsService)
    {
        return new TransactionRepository(
            networkRepository,
            accountKeystoreService,
            inDiskCache,
            transactionsService);
    }

    @Singleton
    @Provides
    OnRampRepositoryType provideOnRampRepository(@ApplicationContext Context context)
    {
        return new OnRampRepository(context);
    }

    @Singleton
    @Provides
    SwapRepositoryType provideSwapRepository(@ApplicationContext Context context)
    {
        return new SwapRepository(context);
    }

    @Singleton
    @Provides
    CoinbasePayRepositoryType provideCoinbasePayRepository()
    {
        return new CoinbasePayRepository();
    }

    @Singleton
    @Provides
    TransactionLocalSource provideTransactionInDiskCache(RealmManager realmManager)
    {
        return new TransactionsRealmCache(realmManager);
    }

    @Singleton
    @Provides
    TransactionsNetworkClientType provideBlockExplorerClient(
        OkHttpClient httpClient,
        Gson gson,
        RealmManager realmManager)
    {
        return new TransactionsNetworkClient(httpClient, gson, realmManager);
    }

    @Singleton
    @Provides
    TokenRepositoryType provideTokenRepository(
        EthereumNetworkRepositoryType ethereumNetworkRepository,
        TokenLocalSource tokenLocalSource,
        @ApplicationContext Context context,
        TickerService tickerService)
    {
        return new TokenRepository(
            ethereumNetworkRepository,
            tokenLocalSource,
            context,
            tickerService);
    }

    @Singleton
    @Provides
    TokenLocalSource provideRealmTokenSource(RealmManager realmManager, EthereumNetworkRepositoryType ethereumNetworkRepository, TokensMappingRepositoryType tokensMappingRepository)
    {
        return new TokensRealmSource(realmManager, ethereumNetworkRepository, tokensMappingRepository);
    }

    @Singleton
    @Provides
    WalletDataRealmSource provideRealmWalletDataSource(RealmManager realmManager)
    {
        return new WalletDataRealmSource(realmManager);
    }

    @Singleton
    @Provides
    TokensService provideTokensServices(EthereumNetworkRepositoryType ethereumNetworkRepository,
                                        TokenRepositoryType tokenRepository,
                                        TickerService tickerService,
                                        OpenSeaService openseaService,
                                        AnalyticsServiceType analyticsService)
    {
        return new TokensService(ethereumNetworkRepository, tokenRepository, tickerService, openseaService, analyticsService);
    }

    @Singleton
    @Provides
    IPFSServiceType provideIPFSService(OkHttpClient client)
    {
        return new IPFSService(client);
    }

    @Singleton
    @Provides
    TransactionsService provideTransactionsServices(TokensService tokensService,
                                                    EthereumNetworkRepositoryType ethereumNetworkRepositoryType,
                                                    TransactionsNetworkClientType transactionsNetworkClientType,
                                                    TransactionLocalSource transactionLocalSource,
                                                    TransactionNotificationService transactionNotificationService)
    {
        return new TransactionsService(tokensService, ethereumNetworkRepositoryType, transactionsNetworkClientType, transactionLocalSource, transactionNotificationService);
    }

    @Singleton
    @Provides
    GasService provideGasService(EthereumNetworkRepositoryType ethereumNetworkRepository, OkHttpClient client, RealmManager realmManager)
    {
        return new GasService(ethereumNetworkRepository, client, realmManager);
    }

    @Singleton
    @Provides
    OpenSeaService provideOpenseaService()
    {
        return new OpenSeaService();
    }

    @Singleton
    @Provides
    SwapService provideSwapService()
    {
        return new SwapService();
    }

    @Singleton
    @Provides
    BossWalletService provideFeemasterService(OkHttpClient okHttpClient, Gson gson)
    {
        return new BossWalletService(okHttpClient, gson);
    }

    @Singleton
    @Provides
    NotificationService provideNotificationService(@ApplicationContext Context ctx)
    {
        return new NotificationService(ctx);
    }

    @Singleton
    @Provides
    AssetDefinitionService providingAssetDefinitionServices(IPFSServiceType ipfsService, @ApplicationContext Context ctx, NotificationService notificationService, RealmManager realmManager,
                                                            TokensService tokensService, TokenLocalSource tls,
                                                            BossWalletService alphaService)
    {
        return new AssetDefinitionService(ipfsService, ctx, notificationService, realmManager, tokensService, tls, alphaService);
    }

    @Singleton
    @Provides
    KeyService provideKeyService(@ApplicationContext Context ctx, AnalyticsServiceType analyticsService)
    {
        return new KeyService(ctx, analyticsService);
    }

    @Singleton
    @Provides
    AnalyticsServiceType provideAnalyticsService(@ApplicationContext Context ctx, PreferenceRepositoryType preferenceRepository)
    {
        return new AnalyticsService(ctx, preferenceRepository);
    }

    @Singleton
    @Provides
    TokensMappingRepositoryType provideTokensMappingRepository(@ApplicationContext Context ctx)
    {
        return new TokensMappingRepository(ctx);
    }

    @Singleton
    @Provides
    TransactionNotificationService provideTransactionNotificationService(@ApplicationContext Context ctx,
                                                                         PreferenceRepositoryType preferenceRepositoryType)
    {
        return new TransactionNotificationService(ctx, preferenceRepositoryType);
    }

    @Singleton
    @Provides
    AlphaWalletNotificationService provideAlphaWalletNotificationService(WalletRepositoryType walletRepository)
    {
        return new AlphaWalletNotificationService(walletRepository);
    }
}
