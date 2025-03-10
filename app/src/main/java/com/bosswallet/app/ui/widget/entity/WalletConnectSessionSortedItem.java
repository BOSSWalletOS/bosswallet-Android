package com.bosswallet.app.ui.widget.entity;

import com.bosswallet.app.entity.walletconnect.WalletConnectSessionItem;
import com.bosswallet.app.ui.widget.holder.WalletConnectSessionHolder;

import java.util.List;

public class WalletConnectSessionSortedItem extends SortedItem<List<WalletConnectSessionItem>>
{
    public WalletConnectSessionSortedItem(List<WalletConnectSessionItem> sessions, int weight)
    {
        super(WalletConnectSessionHolder.VIEW_TYPE, sessions, new TokenPosition(weight));
    }

    @Override
    public boolean areContentsTheSame(SortedItem newItem)
    {
        return false; // always override the existed one
    }

    @Override
    public boolean areItemsTheSame(SortedItem other)
    {
        return other.viewType == viewType;
    }
}
