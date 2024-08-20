package com.bosswallet.app.ui.widget.entity;

import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;

import com.bosswallet.hardware.SignatureFromKey;
import com.bosswallet.token.entity.Signable;
import com.bosswallet.app.entity.SignAuthenticationCallback;
import com.bosswallet.app.entity.WalletType;
import com.bosswallet.app.entity.tokens.Token;
import com.bosswallet.app.service.GasService;
import com.bosswallet.app.web3.entity.Web3Transaction;

import java.math.BigInteger;

/**
 * Created by JB on 27/11/2020.
 */
public interface ActionSheetCallback
{
    void getAuthorisation(SignAuthenticationCallback callback);

    void sendTransaction(Web3Transaction tx);

    //For Hardware wallet
    void completeSendTransaction(Web3Transaction tx, SignatureFromKey signature); //return from hardware signing

    default void completeSignTransaction(Web3Transaction tx, SignatureFromKey signature) //return from hardware signing - sign tx only
    {

    }

    void dismissed(String txHash, long callbackId, boolean actionCompleted);

    void notifyConfirm(String mode);

    ActivityResultLauncher<Intent> gasSelectLauncher();

    default void signTransaction(Web3Transaction tx)
    {
    } // only WalletConnect uses this so far

    default void buttonClick(long callbackId, Token baseToken)
    {
    }

    default void notifyWalletConnectApproval(long chainId)
    {
    }

    default void denyWalletConnect()
    {
    }

    default void openChainSelection()
    {
    }

    default void signingComplete(SignatureFromKey signature, Signable message)
    {
    }

    default void signingFailed(Throwable error, Signable message)
    {
    }

    WalletType getWalletType();

    default BigInteger getTokenId()
    {
        return BigInteger.ZERO;
    }

    GasService getGasService();
}
