package com.pait.smartpos.log;

// Created by anup on 4/21/2017.

import android.content.Context;
import com.pait.smartpos.VerificationActivity;
import com.pait.smartpos.constant.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class CopyLog {

    public boolean copyLog(Context context){
        try {
            FileChannel sourcechannel, destinationchannel;
            File source = new File(context.getFilesDir().getAbsoluteFile()+File.separator+Constant.log_file_name);
            if(source.exists()) {
                sourcechannel = new FileInputStream(source.getAbsoluteFile()).getChannel();
                File sdFile = VerificationActivity.checkFolder(Constant.folder_name);
                File writeFile = new File(sdFile, Constant.log_file_name);
                if (writeFile.exists()) {
                    writeFile.delete();
                }
                writeFile.createNewFile();
                destinationchannel = new FileOutputStream(writeFile).getChannel();
                if (sourcechannel != null) {
                    destinationchannel.transferFrom(sourcechannel, 0, sourcechannel.size());
                }
                if (sourcechannel != null) {
                    sourcechannel.close();
                }
                destinationchannel.close();
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
