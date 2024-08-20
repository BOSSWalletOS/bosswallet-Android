package com.bosswallet.app.repository;

import com.bosswallet.app.entity.OnRampContract;
import com.bosswallet.app.entity.tokens.Token;

public interface OnRampRepositoryType {
    String getUri(String address, Token token);

    OnRampContract getContract(Token token);
}
