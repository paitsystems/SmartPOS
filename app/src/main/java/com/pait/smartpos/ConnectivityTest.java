package com.pait.smartpos;

//Created by Android on 12/8/15.

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityTest {
    public static boolean getNetStat(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net_info = cm.getActiveNetworkInfo();
        if(net_info!=null){
            if(net_info.isConnected()){
                return true;
            }
        }
        return false;
    }

}
