package com.bosswallet.app.interact;

import android.text.TextUtils;

import com.bosswallet.app.entity.Wallet;
import com.bosswallet.app.repository.WalletItem;
import com.bosswallet.app.repository.WalletRepositoryType;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

public class FetchWalletsInteract {

    private final WalletRepositoryType accountRepository;

    public FetchWalletsInteract(WalletRepositoryType accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Single<Wallet[]> fetch() {
        return accountRepository
                .fetchWallets()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Single<Wallet> getWallet(String keyAddress)
    {
        return accountRepository.findWallet(keyAddress);
    }

    public Single<Wallet> storeWallet(Wallet wallet) {
        return accountRepository.storeWallet(wallet);
    }

    public void updateWalletData(Wallet wallet, Realm.Transaction.OnSuccess onSuccess) {
        accountRepository.updateWalletData(wallet, onSuccess);
    }

    public void updateWalletItem(Wallet wallet, WalletItem item, Realm.Transaction.OnSuccess onSuccess) {
        accountRepository.updateWalletItem(wallet, item, onSuccess);
    }

    /**
     * Called when wallet marked as backed up.
     * Update the wallet backup date
     *
     * @param walletAddr
     */
    public void updateBackupTime(String walletAddr)
    {
        accountRepository.updateBackupTime(walletAddr);
    }

    public Single<Wallet> updateENS(Wallet wallet)
    {
        if (TextUtils.isEmpty(wallet.ENSname)) return Single.fromCallable(() -> wallet);
        return storeWallet(wallet);
    }
}
