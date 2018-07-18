package com.pait.smartpos.broadcastreceivers;

// Created by anup on 5/20/2017.

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class DateChangeBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.DATE_CHANGED")){
            //DateChangedBroadcastReceiverInterface _interface
                   // = (DateChangedBroadcastReceiverInterface) context;
            //_interface.dateChanged();
            Toast.makeText(context,"Date Changed Received1",Toast.LENGTH_LONG).show();
            Log.d("Log","Date Changed1");
        }
    }
}
