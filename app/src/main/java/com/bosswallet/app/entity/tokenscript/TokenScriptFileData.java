package com.bosswallet.app.entity.tokenscript;

import com.bosswallet.token.entity.XMLDsigDescriptor;

public class TokenScriptFileData
{
    public String hash;
    public XMLDsigDescriptor sigDescriptor;

    public TokenScriptFileData()
    {
        hash = null;
        sigDescriptor = null;
    }
}
