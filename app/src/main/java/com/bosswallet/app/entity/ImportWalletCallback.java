package com.bosswallet.app.entity;
import com.bosswallet.app.entity.cryptokeys.KeyEncodingType;
import com.bosswallet.app.service.KeyService;

public interface ImportWalletCallback
{
    void walletValidated(String address, KeyEncodingType type, KeyService.AuthenticationLevel level);
}
