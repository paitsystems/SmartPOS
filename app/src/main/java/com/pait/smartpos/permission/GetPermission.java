package com.pait.smartpos.permission;

// Created by anup on 4/28/2017.

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class GetPermission {

    public boolean checkBluetoothPermission(Context context){
        return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void requestBluetoothPermission(Context context, Activity activity){
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.BLUETOOTH)){
            Toast.makeText(context,"ACCESS BLUETOOTH PERMISSION REQUIRED", Toast.LENGTH_LONG).show();
        }else{
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.BLUETOOTH}, 9);
        }
    }

    public boolean checkBluetoothAdminPermission(Context context){
        return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN)== PackageManager.PERMISSION_GRANTED;
    }

    public void requestBluetoothAdminPermission(Context context, Activity activity){
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.BLUETOOTH_ADMIN)){
            Toast.makeText(context,"ACCESS BLUETOOTH PERMISSION REQUIRED", Toast.LENGTH_LONG).show();
        }else{
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.BLUETOOTH_ADMIN},10);
        }
    }

    public boolean checkReadPhoneStatePermission(Context context){
        return ContextCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestReadPhoneStatePermission(Context context, Activity activity){
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.READ_PHONE_STATE)){
            Toast.makeText(context,"ACCESS READ PHONE STATE PERMISSION REQUIRED", Toast.LENGTH_LONG).show();
        }else{
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_PHONE_STATE},11);
        }
    }

    public boolean checkCameraPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void requestCameraPermission(Context context, Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    public boolean checkReadExternalStoragePermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestReadExternalPermission(Context context, Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        }
    }

    public boolean checkWriteExternalStoragePermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestWriteExternalPermission(Context context, Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
        }
    }

    public boolean checkFineLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestFineLocationPermission(Context context, Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
        }
    }

    public boolean checkCoarseLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestCoarseLocationPermission(Context context, Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 6);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 6);
        }
    }

    public boolean checkRebootPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_BOOT_COMPLETED) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestRebootPermission(Context context, Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECEIVE_BOOT_COMPLETED)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, 7);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, 7);
        }
    }

    public boolean checkWakeUpPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestWakeUpPermission(Context context, Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WAKE_LOCK)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WAKE_LOCK}, 8);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WAKE_LOCK}, 8);
        }
    }

    public boolean checkSendSMSPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestSendSMSPermission(Context context, Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.SEND_SMS)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, 12);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, 12);
        }
    }
}
