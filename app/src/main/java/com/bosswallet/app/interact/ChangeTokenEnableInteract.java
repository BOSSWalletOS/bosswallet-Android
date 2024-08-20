package com.bosswallet.app.interact;

import com.bosswallet.token.entity.ContractAddress;
import com.bosswallet.app.entity.Wallet;
import com.bosswallet.app.repository.TokenRepositoryType;

import io.reactivex.Completable;

public class ChangeTokenEnableInteract
{
    private final TokenRepositoryType tokenRepository;

    public ChangeTokenEnableInteract(TokenRepositoryType tokenRepository)
    {
        this.tokenRepository = tokenRepository;
    }

    public Completable setEnable(Wallet wallet, ContractAddress cAddr, boolean enabled)
    {
        tokenRepository.setEnable(wallet, cAddr, enabled);
        tokenRepository.setVisibilityChanged(wallet, cAddr);
        return Completable.fromAction(() -> {});
    }
}
