package com.bosswallet.app.router;

import android.content.Context;
import android.content.Intent;

import com.bosswallet.app.C;
import com.bosswallet.app.entity.Wallet;
import com.bosswallet.app.entity.tokens.Token;
import com.bosswallet.app.ui.SellDetailActivity;

/**
 * Created by James on 22/02/2018.
 */

public class SellDetailRouter {

    public void openMarketPlace(Context context, Token token, String ticketIDs, Wallet wallet) {
        Intent intent = new Intent(context, SellDetailActivity.class);
        intent.putExtra(C.Key.WALLET, wallet);
        intent.putExtra(C.EXTRA_CHAIN_ID, token.tokenInfo.chainId);
        intent.putExtra(C.EXTRA_ADDRESS, token.getAddress());
        intent.putExtra(C.EXTRA_TOKENID_LIST, ticketIDs);
        intent.putExtra(C.EXTRA_STATE, SellDetailActivity.SET_MARKET_SALE);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    public void openUniversalLink(Context context, Token token, String ticketIDs, Wallet wallet, int state, double price) {
        Intent intent = new Intent(context, SellDetailActivity.class);
        intent.putExtra(C.Key.WALLET, wallet);
        intent.putExtra(C.EXTRA_CHAIN_ID, token.tokenInfo.chainId);
        intent.putExtra(C.EXTRA_ADDRESS, token.getAddress());
        intent.putExtra(C.EXTRA_TOKENID_LIST, ticketIDs);
        intent.putExtra(C.EXTRA_STATE, state);
        intent.putExtra(C.EXTRA_PRICE, price);
        intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        context.startActivity(intent);
    }
}
