package com.bosswallet.app.repository;

import com.bosswallet.app.entity.lifi.SwapProvider;

import java.util.List;

public interface SwapRepositoryType
{
    List<SwapProvider> getProviders();
}
