package com.pait.smartpos.log;

// Created by anup on 4/21/2017.

import android.content.Context;
import com.pait.smartpos.constant.Constant;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class WriteLog {

    public boolean writeLog(Context context,String data) {
        try {
            String todayDateTime = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss", Locale.ENGLISH).format(Calendar.getInstance().getTime());
            String str = todayDateTime+"_"+data;
            /*File sdFile = SplashActivity.checkFolder(Constant.folder_name);
            File writeFile = new File(sdFile, Constant.log_file_name);

            if(!writeFile.exists()){
                if(writeFile.createNewFile()){
                    Log.d("Log","Log File Created");
                }
            }*/
            FileOutputStream fOut = context.openFileOutput(Constant.log_file_name,Context.MODE_APPEND);
            fOut.write(str.getBytes());
            fOut.write(System.getProperty("line.separator").getBytes());
            fOut.close();
            return true;
        } catch (Exception e1) {
            e1.printStackTrace();
            return false;
        }
    }
}
