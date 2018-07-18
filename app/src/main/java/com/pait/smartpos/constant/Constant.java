package com.pait.smartpos.constant;

// Created by anup on 4/3/2017.

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.Gson;
import com.pait.smartpos.R;
import com.pait.smartpos.VerificationActivity;
import com.pait.smartpos.model.UserProfileClass;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Constant {

    //TODO: Check Ip Address,App Name, Database Version, Application ID
    public static String folder_name = "PAIT";
    public static String log_file_name = "RestoLog";
    public static String default_gst_name = "GST5";
    public static final String ipaddress = "http://172.30.1.209/PAITSP/service.svc";
    //public static final String ipaddress = "http://license.lnbinfotech.com/PAITSP/service.svc";
    //TODO: Check CustImage Url
    public static final String imgIpaddress = "http://license.lnbinfotech.com/PAIT/Aditya_Wawse.jpg";
    public static final String bgImgIPAddress = "https://api.androidhive.info/images/nav-menu-header-bg.jpg";

    public static String ftp_adress = "ftp.lnbinfotech.com";
    public static String ftp_username = "supportftp@lnbinfotech.com";
    public static String ftp_password = "support$456";
    public static String ftp_folder = "SmartPOS";
    public static String import_fileName = "SmartPOS.xls";

    public static String support_mail_id = "anup.p@paitsystems.com";

    private Activity activity;
    private Context context;
    private ProgressDialog pd;

    public static int liveTestFlag = 0;
    public static int TIMEOUT_CON = 10000;
    public static int TIMEOUT_SO = 70000;

    public static void showLog(String log) {
        Log.d("Log", ""+log);
    }

    public Constant(Activity activity) {
        this.activity = activity;
        pd = new ProgressDialog(activity);
        pd.setCancelable(false);
        pd.setMessage("Please Wait");
    }

    public Constant(Context context) {
        this.context = context;
    }

    public void showPD() {
        if (pd.isShowing()) {
            pd.dismiss();
        } else {
            pd.show();
        }
    }

    public void doFinish() {
        activity.finish();
        activity.overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    public String getDate() {
        String str = "";
        try {
            str = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public String getTime() {
        String str = "";
        try {
            str = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static File checkFolder(String foldername) {
        File extFolder = new File(android.os.Environment.getExternalStorageDirectory() + File.separator + foldername);
        if (!extFolder.exists()) {
            if (extFolder.mkdir()) {
                showLog("Directory Created");
            }
        }
        return extFolder;
    }

    public String getIMEINo1(){
        String imeino="";
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getFirstMethod = telephonyClass.getMethod("getDeviceId", parameter);
            Log.d("Log", getFirstMethod.toString());
            Object[] obParameter = new Object[1];
            obParameter[0] = 0;
            //TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            String first = (String) getFirstMethod.invoke(telephony, obParameter);
            Log.d("Log", "FIRST :" + first);
            obParameter[0] = 1;
            String second = (String) getFirstMethod.invoke(telephony, obParameter);
            Log.d("Log", "SECOND :" + second);
            imeino = first;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imeino;
    }

    public void saveToPref(UserProfileClass user){
        VerificationActivity.pref = context.getSharedPreferences(VerificationActivity.PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = VerificationActivity.pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("obj", json);
        editor.apply();
    }

    public UserProfileClass getPref(){
        VerificationActivity.pref = context.getSharedPreferences(VerificationActivity.PREF_NAME,Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = VerificationActivity.pref.getString("obj", null);
        return gson.fromJson(json, UserProfileClass.class);
    }
}
