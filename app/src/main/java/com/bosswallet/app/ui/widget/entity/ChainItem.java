package com.bosswallet.app.ui.widget.entity;

import com.bosswallet.app.entity.tokendata.TokenGroup;
import com.bosswallet.app.ui.widget.holder.ChainNameHeaderHolder;

/**
 * Created by JB on 10/01/2022.
 */
public class ChainItem extends SortedItem<Long>
{

    public static long CHAIN_ITEM_WEIGHT = 4;

    public ChainItem(Long networkId, TokenGroup group)
    {
        super(ChainNameHeaderHolder.VIEW_TYPE, networkId, new TokenPosition(group, networkId, CHAIN_ITEM_WEIGHT));
    }

    @Override
    public boolean areContentsTheSame(SortedItem other)
    {
        return true;
    }

    @Override
    public boolean areItemsTheSame(SortedItem other)
    {
        return other.viewType == viewType && other.value.equals(value) && other.weight.weighting == weight.weighting && other.weight.group == weight.group;
    }
}
