package com.bosswallet.app.service;

import com.bosswallet.app.entity.NetworkInfo;
import com.bosswallet.app.entity.Transaction;
import com.bosswallet.app.entity.TransactionMeta;
import com.bosswallet.app.entity.transactionAPI.TransferFetchType;
import com.bosswallet.app.entity.transactions.TransferEvent;

import java.util.List;
import java.util.Map;

import io.reactivex.Single;

public interface TransactionsNetworkClientType
{
    Single<Transaction[]> storeNewTransactions(TokensService svs, NetworkInfo networkInfo, String tokenAddress, long lastBlock);

    Single<TransactionMeta[]> fetchMoreTransactions(TokensService svs, NetworkInfo network, long lastTxTime);

    Single<Map<String, List<TransferEvent>>> readTransfers(String currentAddress, NetworkInfo networkByChain, TokensService tokensService, TransferFetchType tfType);

    void checkRequiresAuxReset(String walletAddr);
}
