package com.bosswallet.app.entity;

import com.bosswallet.token.entity.TokenScriptResult;

import java.util.List;

public interface TSAttrCallback
{
    void showTSAttributes(List<TokenScriptResult.Attribute> attrs, boolean updateRequired);
}
