package com.pait.smartpos.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.pait.smartpos.constant.Constant;

public class MySMSBroadcastReceiver  extends BroadcastReceiver {

    private OTPReceiveListener receiver;

    public void initOTPListener(OTPReceiveListener _receiver) {
        this.receiver = _receiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Constant.showLog("onReceive");
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    if (receiver != null) {
                        /*ArrayList<String> appCodes = new ArrayList<>();
                        AppSignatureHelper hash = new AppSignatureHelper(context);
                        appCodes= hash.getAppSignatures();
                        String yourhash = appCodes.get(0);
                        Constant.showLog(yourhash);*/
                        //message = "<#> Your OTP code is: 123456\nBmce+9aHdOoVtE7fS3B07tfj7Bc=";
                        message = message.replace("<#> Your OTP code is: ", "");
                        Constant.showLog(message);
                        message = message.split("\\\\n")[0];
                        Constant.showLog(message);
                        receiver.onOTPReceived(message);
                    }
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    Constant.showLog("onOTPTimeOut");
                    if (receiver != null)
                        receiver.onOTPTimeOut();
                    break;
            }
        } else {
            Constant.showLog("In onReceive else");
        }
    }

    public interface OTPReceiveListener {
        void onOTPReceived(String otp);
        void onOTPTimeOut();
    }
}

