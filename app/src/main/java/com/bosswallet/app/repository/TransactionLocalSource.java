package com.bosswallet.app.repository;

import com.bosswallet.app.entity.ActivityMeta;
import com.bosswallet.app.entity.Transaction;
import com.bosswallet.app.entity.Wallet;
import com.bosswallet.app.repository.entity.RealmAuxData;

import java.util.List;

import io.reactivex.Single;
import io.realm.Realm;

public interface TransactionLocalSource {
	Transaction fetchTransaction(Wallet wallet, String hash);
	Transaction putTransaction(Wallet wallet, Transaction tx);
	Realm getRealmInstance(Wallet wallet);

	Single<ActivityMeta[]> fetchActivityMetas(Wallet wallet, List<Long> networkFilters, long fetchTime, int fetchLimit);
	Single<ActivityMeta[]> fetchActivityMetas(Wallet wallet, long chainId, String tokenAddress, int historyCount);
	Single<ActivityMeta[]> fetchEventMetas(Wallet wallet, List<Long> networkFilters);

	void markTransactionBlock(String walletAddress, String hash, long blockValue);
	Transaction[] fetchPendingTransactions(String currentAddress);

	RealmAuxData fetchEvent(String walletAddress, String eventKey);

    long fetchTxCompletionTime(Wallet wallet, String hash);

    Single<Boolean> deleteAllForWallet(String currentAddress);

    Single<Boolean> deleteAllTickers();
}
