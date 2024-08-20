package com.bosswallet.app.ui.widget.holder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bosswallet.app.R;
import com.bosswallet.app.entity.walletconnect.WalletConnectSessionItem;
import com.bosswallet.app.ui.WalletConnectNotificationActivity;

import java.util.List;

public class WalletConnectSessionHolder extends BinderViewHolder<List<WalletConnectSessionItem>>
{
    public static final int VIEW_TYPE = 2024;
    private final View container;

    public WalletConnectSessionHolder(int resId, ViewGroup parent)
    {
        super(resId, parent);
        container = findViewById(R.id.layout_item_wallet_connect);
    }

    public void bind(@Nullable List<WalletConnectSessionItem> sessionItemList, @NonNull Bundle addition)
    {
        container.setOnClickListener(view -> onClick());
    }

    private void onClick()
    {
        getContext().startActivity(new Intent(getContext(), WalletConnectNotificationActivity.class));
    }
}
