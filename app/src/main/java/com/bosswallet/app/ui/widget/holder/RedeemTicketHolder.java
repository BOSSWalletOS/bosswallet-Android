package com.bosswallet.app.ui.widget.holder;

/**
 * Created by James on 27/02/2018.
 */

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bosswallet.app.R;
import com.bosswallet.app.entity.tokens.Token;

/**
 * Created by James on 13/02/2018.
 */

public class RedeemTicketHolder extends BinderViewHolder<Token> {

    public static final int VIEW_TYPE = 1299;

    private final TextView title;

    public RedeemTicketHolder(int resId, ViewGroup parent) {
        super(resId, parent);
        title = findViewById(R.id.name);
    }

    @Override
    public void bind(@Nullable Token token, @NonNull Bundle addition)
    {
        title.setText(R.string.select_tickets_redeem);
    }
}
