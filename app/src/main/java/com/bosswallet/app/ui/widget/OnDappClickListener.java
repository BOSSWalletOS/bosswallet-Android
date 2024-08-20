package com.bosswallet.app.ui.widget;

import com.bosswallet.app.entity.DApp;

import java.io.Serializable;

public interface OnDappClickListener extends Serializable {
    void onDappClick(DApp dapp);
}
