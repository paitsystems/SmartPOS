package com.pait.smartpos;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.interfaces.ServerCallback;
import com.pait.smartpos.log.WriteLog;
import com.pait.smartpos.model.UserProfileClass;
import com.pait.smartpos.permission.GetPermission;
import com.pait.smartpos.volleyrequests.VolleyRequests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {

    private Constant constant;
    private Toast toast;
    private GetPermission permission;
    private String dbpath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        init();
        checkpermmission();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case 0:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        new Constant(FirstActivity.this).doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                new Constant(FirstActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 9:
                checkpermmission();
                break;
            case 10:
                checkpermmission();
                break;
            case 11:
                checkpermmission();
                break;
            case 12:
                checkpermmission();
                break;
            case 2:
                checkpermmission();
                break;
            case 1:
                checkpermmission();
                break;
            case 3:
                if (grantResults.length > 0&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doThis();
                }
                break;
        }
    }

    private void init() {
        constant = new Constant(FirstActivity.this);
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        permission = new GetPermission();
    }

    private void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
        builder.setCancelable(false);
        if (a == 0) {
            builder.setMessage("Do You Want To Exit App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Constant(FirstActivity.this).doFinish();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else if (a == 2) {
            builder.setTitle("License Expired");
            builder.setPositiveButton("Purchase", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                    Intent intent = new Intent(getApplicationContext(),ContactUsActivity.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Detail", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                    Intent intent = new Intent(getApplicationContext(),UserProfileActivity.class);
                    intent.putExtra("from","second");
                    startActivity(intent);
                }
            });
            builder.setNeutralButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    new Constant(FirstActivity.this).doFinish();
                }
            });
        } else if (a == 3) {
            builder.setTitle("Invalid UserId...");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else if (a == 4) {
            builder.setTitle("Something Went Wrong...");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(ConnectivityTest.getNetStat(getApplicationContext())) {
                        checkValidity();
                    }else{
                        toast.setText("You Are Offline");
                        toast.show();
                    }
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
                new Constant(FirstActivity.this).doFinish();
            });
        }
        builder.create().show();
    }

    private void checkpermmission(){
        if(!permission.checkBluetoothPermission(getApplicationContext())){
            permission.requestBluetoothPermission(getApplicationContext(),FirstActivity.this);//9
        }else if(!permission.checkBluetoothAdminPermission(getApplicationContext())){
            permission.requestBluetoothAdminPermission(getApplicationContext(),FirstActivity.this);//10
        }else if(!permission.checkReadPhoneStatePermission(getApplicationContext())){
            permission.requestReadPhoneStatePermission(getApplicationContext(),FirstActivity.this);//11
        }else if(!permission.checkReadExternalStoragePermission(getApplicationContext())){
            permission.requestReadExternalPermission(getApplicationContext(),FirstActivity.this);//2
        }else if(!permission.checkSendSMSPermission(getApplicationContext())){
            permission.requestSendSMSPermission(getApplicationContext(),FirstActivity.this);//12
        }else if(!permission.checkCameraPermission(getApplicationContext())){
            permission.requestCameraPermission(getApplicationContext(),FirstActivity.this);//1
        }else if(!permission.checkWriteExternalStoragePermission(getApplicationContext())){
            permission.requestWriteExternalPermission(getApplicationContext(),FirstActivity.this);//3
        }else {
            doThis();
        }
    }

    private void doThis(){
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            dbpath = pInfo.applicationInfo.dataDir+"/databases";
            Constant.showLog(dbpath);
            new DBHandler(getApplicationContext()).getReadableDatabase();
            //CopyDb();
        } catch (Exception e) {
            e.printStackTrace();
            writeLog("doThis_"+e.getMessage());
        }
        overridePendingTransition(R.anim.enter,R.anim.exit);
        doFinish();
    }

    private void CopyDb() throws IOException {
        if(!checkDB()){
            InputStream is = getApplicationContext().getAssets().open(DBHandlerR.Database_Name);
            File file = new File(dbpath);
            if(!file.exists()) {
                if (file.mkdir()) {
                    Constant.showLog("Database Created");
                    writeLog("CopyDb_Database_Created");
                }
            }
            OutputStream os = new FileOutputStream(dbpath+"/"+ DBHandlerR.Database_Name);
            byte[] buffer = new byte[2014];
            while (is.read(buffer)>0){
                os.write(buffer);
            }
            os.flush();
            os.close();
            is.close();
        }
    }

    private boolean checkDB(){
        File file = getApplicationContext().getDatabasePath(DBHandlerR.Database_Name);
        return file.exists();
    }

    private void doFinish(){
        VerificationActivity.pref = getSharedPreferences(VerificationActivity.PREF_NAME,MODE_PRIVATE);
        UserProfileClass user = new Constant(getApplicationContext()).getPref();
        if(user!=null){
            if(user.getUserType().equals("P")) {
                //TODO:check here expiry date
                if(checkExpiryDate(user.getExpiryDate())) {
                    startDrawer();
                }else{
                    showDia(2);
                }
            }else{
                if(ConnectivityTest.getNetStat(getApplicationContext())) {
                    checkValidity();
                }else {
                    toast.setText("You Are Offline");
                    toast.show();
                }
            }
        }else{
            finish();
            Intent intent = new Intent(getApplicationContext(),UserProfileActivity.class);
            intent.putExtra("from","first");
            startActivity(intent);
        }
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private void startDrawer(){
        finish();
        Intent intent = new Intent(getApplicationContext(), DrawerTestActivity.class);
        startActivity(intent);
    }

    private boolean checkExpiryDate(String expDate){
        boolean stat = false;
        try {
            Date _expDate = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).parse(expDate);
            Date _curDate = new Date();
            if(_expDate.compareTo(_curDate)>=0){
                stat = true;
            }
        }catch (Exception e){
            e.printStackTrace();
            stat = false;
        }
        return stat;
    }

    private void checkValidity() {
        if (ConnectivityTest.getNetStat(getApplicationContext())) {
            try {
                constant.showPD();
                UserProfileClass user = new Constant(getApplicationContext()).getPref();
                String mobNo = user.getMobileNo();
                String userId = user.getUserid();
                String imeiNo = new Constant(getApplicationContext()).getIMEINo1();
                String _mobNo = URLEncoder.encode(mobNo, "UTF-8");
                String _userId = URLEncoder.encode(userId, "UTF-8");
                String _imeiNo = URLEncoder.encode(imeiNo, "UTF-8");
                String  clientId = "NA";
                clientId = URLEncoder.encode(clientId, "UTF-8");
                String url = Constant.ipaddress + "/CheckValidity?mobileno=" + _mobNo + "&userId=" + _userId + "&IMEINo=" + _imeiNo+"&clientId=" + clientId;
                Constant.showLog(url);
                writeLog("checkValidity_" + url);

                VolleyRequests requests = new VolleyRequests(FirstActivity.this);
                requests.getOTPCode(url, new ServerCallback() {
                    @Override
                    public void onSuccess(String response) {
                        constant.showPD();
                        if (!response.equals("0") && !response.equals("-1") && !response.equals("-2")) {
                            //On Success
                            startDrawer();
                            writeLog("checkValidity_Success_" + response);
                        } else if (!response.equals("0") && response.equals("-1") && !response.equals("-2")) {
                            //Already Registered
                            showDia(2);
                            writeLog("checkValidity_Fail_" + response);
                        } else if (!response.equals("0") && !response.equals("-1") && response.equals("-2")) {
                            //Already Registered
                            showDia(3);
                            writeLog("checkValidity_Fail_" + response);
                        }
                    }

                    @Override
                    public void onFailure(String result) {
                        constant.showPD();
                        showDia(4);
                        writeLog("checkValidity_VolleyError_" + result);
                    }
                });
            } catch (Exception e) {
                showDia(4);
                e.printStackTrace();
                writeLog("checkValidity_Exception_" + e.getMessage());
            }
        } else {
            toast.setText("You Are Offline");
            toast.show();
        }
    }

    private void writeLog(String _data) {
        new WriteLog().writeLog(getApplicationContext(), "FirstActivity_" + _data);
    }
}
