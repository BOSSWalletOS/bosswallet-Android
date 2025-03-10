package com.bosswallet.app.repository;

import android.util.Pair;

import com.bosswallet.token.entity.ContractAddress;
import com.bosswallet.app.entity.ContractLocator;
import com.bosswallet.app.entity.ContractType;
import com.bosswallet.app.entity.ImageEntry;
import com.bosswallet.app.entity.TransferFromEventResponse;
import com.bosswallet.app.entity.Wallet;
import com.bosswallet.app.entity.nftassets.NFTAsset;
import com.bosswallet.app.entity.tokendata.TokenGroup;
import com.bosswallet.app.entity.tokendata.TokenTicker;
import com.bosswallet.app.entity.tokens.Token;
import com.bosswallet.app.entity.tokens.TokenCardMeta;
import com.bosswallet.app.entity.tokens.TokenInfo;
import com.bosswallet.app.service.AssetDefinitionService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.realm.Realm;

public interface TokenRepositoryType
{
    Observable<Token> fetchActiveTokenBalance(String walletAddress, Token token);

    Single<BigDecimal> updateTokenBalance(String walletAddress, Token token);

    Single<ContractLocator> getTokenResponse(String address, long chainId, String method);

    Single<Token> checkInterface(Token tokens, Wallet wallet);

    void setEnable(Wallet wallet, ContractAddress cAddr, boolean isEnabled);

    void setVisibilityChanged(Wallet wallet, ContractAddress cAddr);

    Single<TokenInfo> update(String address, long chainId, ContractType type);

    Observable<TransferFromEventResponse> burnListenerObservable(String contractAddress);

    Single<TokenTicker> getEthTicker(long chainId);

    TokenTicker getTokenTicker(Token token);

    Single<BigInteger> fetchLatestBlockNumber(long chainId);

    Token fetchToken(long chainId, String walletAddress, String address);

    String getTokenImageUrl(long chainId, String address);

    Single<Token[]> storeTokens(Wallet wallet, Token[] tokens);

    Single<String> resolveENS(long chainId, String address);

    void updateAssets(String wallet, Token erc721Token, List<BigInteger> additions, List<BigInteger> removals);

    void storeAsset(String currentAddress, Token token, BigInteger tokenId, NFTAsset asset);

    Token initNFTAssets(Wallet wallet, Token token);

    Single<ContractType> determineCommonType(TokenInfo tokenInfo);

    Single<Boolean> fetchIsRedeemed(Token token, BigInteger tokenId);

    void addImageUrl(List<ImageEntry> entries);

    void updateLocalAddress(String walletAddress);

    void deleteRealmTokens(Wallet wallet, List<TokenCardMeta> tcmList);

    Single<TokenCardMeta[]> fetchTokenMetas(Wallet wallet, List<Long> networkFilters,
                                            AssetDefinitionService svs);

    Single<TokenCardMeta[]> fetchAllTokenMetas(Wallet wallet, List<Long> networkFilters,
                                               String searchTerm);

    Single<Token[]> fetchTokensThatMayNeedUpdating(String walletAddress, List<Long> networkFilters);

    Single<ContractAddress[]> fetchAllTokensWithBlankName(String walletAddress, List<Long> networkFilters);

    TokenCardMeta[] fetchTokenMetasForUpdate(Wallet wallet, List<Long> networkFilters);

    Realm getRealmInstance(Wallet wallet);

    Realm getTickerRealmInstance();

    Single<BigDecimal> fetchChainBalance(String walletAddress, long chainId);

    Single<Integer> fixFullNames(Wallet wallet, AssetDefinitionService svs);

    boolean isEnabled(Token newToken);

    Single<Pair<Double, Double>> getTotalValue(String currentAddress, List<Long> networkFilters);

    Single<List<String>> getTickerUpdateList(List<Long> networkFilter);

    TokenGroup getTokenGroup(long chainId, String address, ContractType type);

    Single<TokenInfo> storeTokenInfo(Wallet wallet, TokenInfo tInfo, ContractType type);

    Token fetchAttestation(long chainId, String currentAddress, String toLowerCase, String attnId);

    List<Token> fetchAttestations(long chainId, String walletAddress, String tokenAddress);
}
