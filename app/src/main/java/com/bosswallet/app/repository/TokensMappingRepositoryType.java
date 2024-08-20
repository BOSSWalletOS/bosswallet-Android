package com.bosswallet.app.repository;

import com.bosswallet.token.entity.ContractAddress;
import com.bosswallet.app.entity.ContractType;
import com.bosswallet.app.entity.tokendata.TokenGroup;

public interface TokensMappingRepositoryType
{
    TokenGroup getTokenGroup(long chainId, String address, ContractType type);

    ContractAddress getBaseToken(long chainId, String address);
}
