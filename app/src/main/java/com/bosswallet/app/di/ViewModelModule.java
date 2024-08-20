package com.bosswallet.app.di;

import com.bosswallet.app.interact.ChangeTokenEnableInteract;
import com.bosswallet.app.interact.CreateTransactionInteract;
import com.bosswallet.app.interact.DeleteWalletInteract;
import com.bosswallet.app.interact.ExportWalletInteract;
import com.bosswallet.app.interact.FetchTokensInteract;
import com.bosswallet.app.interact.FetchTransactionsInteract;
import com.bosswallet.app.interact.FetchWalletsInteract;
import com.bosswallet.app.interact.FindDefaultNetworkInteract;
import com.bosswallet.app.interact.GenericWalletInteract;
import com.bosswallet.app.interact.ImportWalletInteract;
import com.bosswallet.app.interact.MemPoolInteract;
import com.bosswallet.app.interact.SetDefaultWalletInteract;
import com.bosswallet.app.interact.SignatureGenerateInteract;
import com.bosswallet.app.repository.CurrencyRepository;
import com.bosswallet.app.repository.CurrencyRepositoryType;
import com.bosswallet.app.repository.EthereumNetworkRepositoryType;
import com.bosswallet.app.repository.LocaleRepository;
import com.bosswallet.app.repository.LocaleRepositoryType;
import com.bosswallet.app.repository.PreferenceRepositoryType;
import com.bosswallet.app.repository.TokenRepositoryType;
import com.bosswallet.app.repository.TransactionRepositoryType;
import com.bosswallet.app.repository.WalletRepositoryType;
import com.bosswallet.app.router.CoinbasePayRouter;
import com.bosswallet.app.router.ExternalBrowserRouter;
import com.bosswallet.app.router.HomeRouter;
import com.bosswallet.app.router.ImportTokenRouter;
import com.bosswallet.app.router.ImportWalletRouter;
import com.bosswallet.app.router.ManageWalletsRouter;
import com.bosswallet.app.router.MyAddressRouter;
import com.bosswallet.app.router.RedeemSignatureDisplayRouter;
import com.bosswallet.app.router.SellDetailRouter;
import com.bosswallet.app.router.TokenDetailRouter;
import com.bosswallet.app.router.TransferTicketDetailRouter;
import com.bosswallet.app.service.AnalyticsServiceType;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;

@Module
@InstallIn(ViewModelComponent.class)
/** Module for providing dependencies to viewModels.
 * All bindings of modules from BuildersModule is shifted here as they were injected in activity for ViewModelFactory but not needed in Hilt
 * */
public class ViewModelModule {

    @Provides
    FetchWalletsInteract provideFetchWalletInteract(WalletRepositoryType walletRepository) {
        return new FetchWalletsInteract(walletRepository);
    }

    @Provides
    SetDefaultWalletInteract provideSetDefaultAccountInteract(WalletRepositoryType accountRepository) {
        return new SetDefaultWalletInteract(accountRepository);
    }

    @Provides
    ImportWalletRouter provideImportAccountRouter() {
        return new ImportWalletRouter();
    }

    @Provides
    HomeRouter provideHomeRouter() {
        return new HomeRouter();
    }

    @Provides
    FindDefaultNetworkInteract provideFindDefaultNetworkInteract(
            EthereumNetworkRepositoryType networkRepository) {
        return new FindDefaultNetworkInteract(networkRepository);
    }

    @Provides
    ImportWalletInteract provideImportWalletInteract(
            WalletRepositoryType walletRepository) {
        return new ImportWalletInteract(walletRepository);
    }

    @Provides
    ExternalBrowserRouter externalBrowserRouter() {
        return new ExternalBrowserRouter();
    }

    @Provides
    FetchTransactionsInteract provideFetchTransactionsInteract(TransactionRepositoryType transactionRepository,
                                                               TokenRepositoryType tokenRepositoryType) {
        return new FetchTransactionsInteract(transactionRepository, tokenRepositoryType);
    }

    @Provides
    CreateTransactionInteract provideCreateTransactionInteract(TransactionRepositoryType transactionRepository,
                                                               AnalyticsServiceType analyticsService) {
        return new CreateTransactionInteract(transactionRepository, analyticsService);
    }

    @Provides
    MyAddressRouter provideMyAddressRouter() {
        return new MyAddressRouter();
    }

    @Provides
    CoinbasePayRouter provideCoinbasePayRouter() {
        return new CoinbasePayRouter();
    }

    @Provides
    FetchTokensInteract provideFetchTokensInteract(TokenRepositoryType tokenRepository) {
        return new FetchTokensInteract(tokenRepository);
    }

    @Provides
    SignatureGenerateInteract provideSignatureGenerateInteract(WalletRepositoryType walletRepository) {
        return new SignatureGenerateInteract(walletRepository);
    }

    @Provides
    MemPoolInteract provideMemPoolInteract(TokenRepositoryType tokenRepository) {
        return new MemPoolInteract(tokenRepository);
    }

    @Provides
    TransferTicketDetailRouter provideTransferTicketRouter() {
        return new TransferTicketDetailRouter();
    }

    @Provides
    LocaleRepositoryType provideLocaleRepository(PreferenceRepositoryType preferenceRepository) {
        return new LocaleRepository(preferenceRepository);
    }

    @Provides
    CurrencyRepositoryType provideCurrencyRepository(PreferenceRepositoryType preferenceRepository) {
        return new CurrencyRepository(preferenceRepository);
    }

    @Provides
    TokenDetailRouter provideErc20DetailRouterRouter() {
        return new TokenDetailRouter();
    }

    @Provides
    GenericWalletInteract provideGenericWalletInteract(WalletRepositoryType walletRepository) {
        return new GenericWalletInteract(walletRepository);
    }

    @Provides
    ChangeTokenEnableInteract provideChangeTokenEnableInteract(TokenRepositoryType tokenRepository) {
        return new ChangeTokenEnableInteract(tokenRepository);
    }

    @Provides
    ManageWalletsRouter provideManageWalletsRouter() {
        return new ManageWalletsRouter();
    }

    @Provides
    SellDetailRouter provideSellDetailRouter() {
        return new SellDetailRouter();
    }

    @Provides
    DeleteWalletInteract provideDeleteAccountInteract(
            WalletRepositoryType accountRepository) {
        return new DeleteWalletInteract(accountRepository);
    }

    @Provides
    ExportWalletInteract provideExportWalletInteract(
            WalletRepositoryType walletRepository) {
        return new ExportWalletInteract(walletRepository);
    }

    @Provides
    ImportTokenRouter provideImportTokenRouter() {
        return new ImportTokenRouter();
    }

    @Provides
    RedeemSignatureDisplayRouter provideRedeemSignatureDisplayRouter() {
        return new RedeemSignatureDisplayRouter();
    }
}
