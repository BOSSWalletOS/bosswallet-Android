package com.bosswallet.app.entity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bosswallet.app.C;

public class HomeReceiver extends BroadcastReceiver
{
    private final HomeCommsInterface homeCommsInterface;
    private final LocalBroadcastManager broadcastManager;

    public HomeReceiver(Context context, HomeCommsInterface homeCommsInterface)
    {
        broadcastManager = LocalBroadcastManager.getInstance(context);
        this.homeCommsInterface = homeCommsInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle bundle = intent.getExtras();
        if (intent.getAction() != null)
        {
            switch (intent.getAction())
            {
                case C.REQUEST_NOTIFICATION_ACCESS:
                    homeCommsInterface.requestNotificationPermission();
                    break;
                case C.BACKUP_WALLET_SUCCESS:
                    String keyAddress = bundle != null ? bundle.getString("Key", "") : "";
                    homeCommsInterface.backupSuccess(keyAddress);
                    break;
                default:
                    break;
            }
        }
    }

    public void register()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(C.REQUEST_NOTIFICATION_ACCESS);
        filter.addAction(C.BACKUP_WALLET_SUCCESS);
        filter.addAction(C.WALLET_CONNECT_REQUEST);
        broadcastManager.registerReceiver(this, filter);
    }

    public void unregister()
    {
        broadcastManager.unregisterReceiver(this);
    }
}
