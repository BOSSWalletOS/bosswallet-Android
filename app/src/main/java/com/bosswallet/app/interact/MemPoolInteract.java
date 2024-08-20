package com.bosswallet.app.interact;

import com.bosswallet.app.entity.TransferFromEventResponse;
import com.bosswallet.app.repository.TokenRepositoryType;

import io.reactivex.Observable;

/**
 * Created by James on 1/02/2018.
 */

public class MemPoolInteract
{
    private final TokenRepositoryType tokenRepository;

    public MemPoolInteract(TokenRepositoryType tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    //create an observable
    public Observable<TransferFromEventResponse> burnListener(String contractAddress) {
        return tokenRepository.burnListenerObservable(contractAddress);
    }
}
