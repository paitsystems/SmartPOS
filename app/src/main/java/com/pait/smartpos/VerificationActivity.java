package com.pait.smartpos;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hoin.btsdk.BluetoothService;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.log.WriteLog;
import com.pait.smartpos.parse.ParseJSON;
import com.pait.smartpos.permission.GetPermission;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class VerificationActivity extends AppCompatActivity implements View.OnClickListener {

    public static SharedPreferences pref;
    public static String PREF_NAME = "SmartRestro";
    private EditText ed_clientID;
    private Button btn_verify;
    private GetPermission permission;
    private Constant constant;
    private Toast toast;
    private int renewOrValidate = -1;
    public static BluetoothService mService;
    private BluetoothDevice con_dev = null;
    public static String data = "";
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        permission = new GetPermission();
        constant = new Constant(VerificationActivity.this);
        context = getApplicationContext();
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        checkpermmission();
    }

    private void init() {
        ed_clientID = findViewById(R.id.ed_client_id);
        btn_verify = findViewById(R.id.btn_verify);
        btn_verify.setOnClickListener(this);
    }

    private void checkpermmission() {
        if (!permission.checkBluetoothPermission(getApplicationContext())) {
            permission.requestBluetoothPermission(getApplicationContext(), VerificationActivity.this);
        } else if (!permission.checkBluetoothAdminPermission(getApplicationContext())) {
            permission.requestBluetoothAdminPermission(getApplicationContext(), VerificationActivity.this);
        } else if (!permission.checkReadPhoneStatePermission(getApplicationContext())) {
            permission.requestReadPhoneStatePermission(getApplicationContext(), VerificationActivity.this);
        } else {
            doThis();
        }
    }

    private void doThis() {
        //new DBHandlerR(getApplicationContext()).deleteTable(DBHandlerR.BillMaster_Table);
        //new DBHandlerR(getApplicationContext()).deleteTable(DBHandlerR.BillDetail_Table);
        mService = new BluetoothService(getApplicationContext(), mHandler1);
        //TODO: Uncomment this
        //connectBT();
        init();
        if (pref.contains(getString(R.string.pref_isRegister))) {
            renewOrValidate = 1;
            if (checkExpiration()) {
                if (pref.contains(getString(R.string.pref_isTableSaved))) {
                    if (pref.contains(getString(R.string.pref_isCategorySaved))) {
                        if (pref.contains(getString(R.string.pref_logged))) {
                            if (pref.getBoolean(getString(R.string.pref_logged), false)) {
                                //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                startActivity(new Intent(getApplicationContext(), DrawerTestActivity.class));
                                doFinish();
                            } else {
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                doFinish();
                            }
                        } else {
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            doFinish();
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), AddProductMasterActivity.class);
                        intent.putExtra("from", "FromVerificationActivity");
                        startActivity(intent);
                        doFinish();
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), AddTableMasterActivity.class);
                    intent.putExtra("from", "FromVerificationActivity");
                    startActivity(intent);
                    doFinish();
                }
            } else {
                showDia(4);
            }
        } else {
            init();
            renewOrValidate = 0;
        }
    }

    private void connectBT() {
        Set<BluetoothDevice> set = mService.getPairedDev();
        if (!set.isEmpty()) {
            for (BluetoothDevice device : set) {
                con_dev = mService.getDevByMac(device.getAddress());
                Log.d("Log", "getDevByMac :- " + con_dev);
                mService.connect(con_dev);
            }
        }
    }

    private void showDia(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(VerificationActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Verification");
        if (i == 1) {
            builder.setMessage("Network Connectivity Required");
            builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        } else if (i == 2) {
            builder.setMessage("Something Went Wrong");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    verifyUser();
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        } else if (i == 3) {
            builder.setMessage("Invalid Client ID");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        } else if (i == 4) {
            builder.setTitle("APK Licence Expired");
            data = "APK_Licence_Expired";
            builder.setMessage("Please Contact Administrator");
            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("Renew", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    showDia(5);
                }
            });
        } else if (i == 5) {
            builder.setMessage("Please Enter New Client Id");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    btn_verify = (Button) findViewById(R.id.btn_verify);
                    String s = "Renew";
                    btn_verify.setText(s);
                    dialogInterface.dismiss();
                }
            });
        } else if (i == 6) {
            builder.setTitle("APK Renew");
            builder.setMessage("APK Renewed Successfully");
            builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    doThis();
                }
            });
        }
        builder.create().show();
    }

    private void verifyUser() {
        try {
            String deviceID = getDeviceID();
            final String IMEI = deviceID;
            String clientID = ed_clientID.getText().toString();
            data = deviceID + "_" + IMEI;
            writeLog(data);
            deviceID = URLEncoder.encode(deviceID, "UTF-8");
            clientID = URLEncoder.encode(clientID, "UTF-8");
            String url = Constant.ipaddress + "/ValidateClient?ClientID=" + clientID + "&IMEINo=" + deviceID;
            Constant.showLog("VerificationActivity :- URL :- " + url);
            constant.showPD();
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String result) {
                    result = result.replace("\\", "");
                    result = result.replace("''", "");
                    result = result.substring(1, result.length() - 1);
                    Constant.showLog("VerificationActivity :- Response :- " + result);
                    ArrayList<String> list = new ParseJSON(result, getApplicationContext()).parseValidation();
                    if (list.size() != 0) {
                        constant.showPD();
                        toast.setText("Successfully Validate");
                        toast.show();
                        doFinish();
                        data = "Verified_Successfully";
                        writeLog(data);
                        Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                        intent.putStringArrayListExtra("list", list);
                        intent.putExtra("imei", IMEI);
                        startActivity(intent);
                    } else {
                        showDia(3);
                        data = "Verification_Invalid_Client_ID";
                        writeLog(data);
                        constant.showPD();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showDia(2);
                    data = "Verification_" + error.getMessage();
                    writeLog(data);
                    constant.showPD();
                }
            });
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            showDia(2);
            data = "Verification_" + e.getMessage();
            writeLog(data);
            constant.showPD();
        }
    }

    private void renewUser() {
        try {
            String deviceID = getDeviceID();
            //final String IMEI = deviceID;
            String clientID = ed_clientID.getText().toString();
            deviceID = URLEncoder.encode(deviceID, "UTF-8");
            clientID = URLEncoder.encode(clientID, "UTF-8");
            String url = Constant.ipaddress + "/RenewClient?ClientID=" + clientID + "&IMEINo=" + deviceID;
            Constant.showLog("VerificationActivity :- URL :- " + url);
            constant.showPD();
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String result) {
                    result = result.replace("\\", "");
                    result = result.replace("''", "");
                    result = result.substring(1, result.length() - 1);
                    Constant.showLog("VerificationActivity :- Response :- " + result);
                    ArrayList<String> list = new ParseJSON(result, getApplicationContext()).parseRenew();
                    if (list.size() != 0) {
                        constant.showPD();
                        toast.setText("Renew Validate");
                        toast.show();
                        data = "Renew_Validate";
                        writeLog(data);
                        SharedPreferences.Editor editor = VerificationActivity.pref.edit();
                        editor.putString(getString(R.string.pref_expiryDate), list.get(6));
                        editor.apply();
                        showDia(6);
                    } else {
                        showDia(3);
                        data = "Renew_Invalid_Client_ID";
                        writeLog(data);
                        constant.showPD();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showDia(2);
                    data = "Renew_" + error.getMessage();
                    writeLog(data);
                    constant.showPD();
                }
            });
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            showDia(2);
            data = "Renew_" + e.getMessage();
            writeLog(data);
            constant.showPD();
        }
    }

    private void validation() {
        if (!permission.checkReadPhoneStatePermission(getApplicationContext())) {
            permission.requestReadPhoneStatePermission(getApplicationContext(), VerificationActivity.this);
        } else {
            String clientid = ed_clientID.getText().toString();
            if (!clientid.equals("") || clientid.length() != 0) {
                if (renewOrValidate == 0) {
                    verifyUser();
                } else if (renewOrValidate == 1) {
                    renewUser();
                }
            } else {
                toast.setText("Please Enter ClientID");
                toast.show();
            }
        }
    }

    private String getDeviceID() {
        TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            toast.setText("Please Allow All Permission");
            toast.show();
        }else {
            Constant.showLog("VerificationActivity :- DeviceID :- " + manager.getDeviceId());
        }
        if(manager!=null) {
            return manager.getDeviceId();
        }else {
            return "";
        }
    }

    private void doFinish(){
        finish();
        toast.cancel();
        overridePendingTransition(R.anim.enter,R.anim.exit);
        //Runtime.getRuntime().gc();
        //Runtime.getRuntime().freeMemory();
    }

    private boolean checkExpiration() {
        boolean stat=false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
            Date expDate = sdf.parse(pref.getString(getString(R.string.pref_expiryDate), "01/Jan/1900"));
            String cd = sdf.format(Calendar.getInstance().getTime());
            Date currentDate = sdf.parse(cd);
            if (currentDate.compareTo(expDate) <= 0) {
                stat = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stat;
    }

    public static File checkFolder(String foldername){
        File extFolder = new File(android.os.Environment.getExternalStorageDirectory() + File.separator + foldername);
        if (!extFolder.exists()) {
            if (extFolder.mkdir()) {
                Constant.showLog("Directory Created");
            }
        }
        return extFolder;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verify:
                if (ConnectivityTest.getNetStat(getApplicationContext())) {
                    validation();
                } else {
                    showDia(1);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        doFinish();
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            toast.setText("Bluetooth Printer Connected");
                            toast.show();
                            //btnClose.setEnabled(true);
                            //btnSend.setEnabled(true);
                            //qrCodeBtnSend.setEnabled(true);
                            //btnSendDraw.setEnabled(true);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            break;
                        case BluetoothService.STATE_LISTEN:
                            break;
                        case BluetoothService.STATE_NONE:
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:
                    //connectBT();
                    toast.setText("Device connection was lost");
                    toast.show();
                    //btnClose.setEnabled(false);
                    //btnSend.setEnabled(false);
                    //qrCodeBtnSend.setEnabled(false);
                    //btnSendDraw.setEnabled(false);
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:
                    //connectBT();
                    toast.setText("Unable to Connect Bluetooth Printer");
                    toast.show();
                    break;
            }
        }

    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                checkpermmission();
                break;
            case 2:
                checkpermmission();
                break;
            case 3:
                if (grantResults.length > 0&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doThis();
                }
                break;
        }
    }

    public static void writeLog(String _data){
        new WriteLog().writeLog(context,"VerificationActivity_"+_data);
        data = "";
    }
}
