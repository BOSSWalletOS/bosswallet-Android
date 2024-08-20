package com.bosswallet.app.entity.attestation;

import com.bosswallet.app.entity.tokens.TokenCardMeta;

public interface AttestationImportInterface
{
    void attestationImported(TokenCardMeta newToken);
    void importError(String error);
    void smartPassValidation(SmartPassReturn validation);
}
