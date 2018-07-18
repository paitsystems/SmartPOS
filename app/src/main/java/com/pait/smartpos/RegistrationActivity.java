package com.pait.smartpos;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.parse.ParseJSON;

import java.net.URLEncoder;
import java.util.ArrayList;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener{

    EditText ed_username, ed_password;
    Button btn_register;
    TextView tv_comp_name, tv_address, tv_contact_no, tv_expiry_date;
    Toast toast;
    String IMEINo, ClientID, username, password, isNew;
    Constant constant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        constant = new Constant(RegistrationActivity.this);
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);

        init();

        ArrayList list = getIntent().getExtras().getParcelableArrayList("list");
        ClientID = list.get(1).toString();
        IMEINo = getIntent().getExtras().getString("imei");
        tv_comp_name.setText(list.get(2).toString());
        tv_address.setText(list.get(4).toString());
        tv_contact_no.setText(list.get(5).toString());
        tv_expiry_date.setText(list.get(6).toString());
        username = list.get(7).toString();
        password = list.get(8).toString();
        String sd = list.get(9).toString();
        if(sd.equals("")||sd.length()==0){
            isNew = "Y";
        }else{
            isNew = "N";
        }
        if(username.length()!=0 || !username.equals("")){
            ed_username.setText(username);
        }else{
            ed_username.setText("");
        }
        if(password.length()!=0 || !password.equals("")){
            ed_password.setText(password);
        }else{
            ed_password.setText("");
        }
    }

    void init(){
        ed_username = (EditText) findViewById(R.id.ed_username);
        ed_password = (EditText) findViewById(R.id.ed_password);

        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);

        tv_comp_name = (TextView) findViewById(R.id.tv_comp_name);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_contact_no = (TextView) findViewById(R.id.tv_contact_no);
        tv_expiry_date = (TextView) findViewById(R.id.tv_expiry_date);
    }

    void showDia(int i){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Registration");
        if(i==1) {
            builder.setMessage("Network Connectivity Required");
            builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                    overridePendingTransition(R.anim.enter,R.anim.exit);
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }else if(i==2) {
            builder.setMessage("Something Went Wrong");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    registerUser();
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }
        builder.create().show();
    }

    void validation(){
        String uname = ed_username.getText().toString();
        String pass = ed_password.getText().toString();
        if(uname.length()!=0 || !uname.equals("")){
            if(pass.length()!=0 || !pass.equals("")){
                if (ConnectivityTest.getNetStat(getApplicationContext())) {
                    registerUser();
                } else {
                    showDia(1);
                }
            }else{
                toast.setText("Enter Password");
                toast.show();
            }
        }else{
            toast.setText("Enter Username");
            toast.show();
        }
    }

    void registerUser(){
        try {
            String deviceID = IMEINo;
            String clientID = ClientID;
            String uname = ed_username.getText().toString();
            String pass = ed_password.getText().toString();
            uname = URLEncoder.encode(uname, "UTF-8");
            pass = URLEncoder.encode(pass, "UTF-8");
            deviceID = URLEncoder.encode(deviceID, "UTF-8");
            clientID = URLEncoder.encode(clientID, "UTF-8");
            String url = Constant.ipaddress + "/RegisterClient?ClientID="+clientID+"&IMEINo="+deviceID+"&username="+uname+"&pwd="+pass+"&isNew="+isNew;
            Constant.showLog("RegistrationActivity :- URL :- "+url);
            constant.showPD();
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String result) {
                    result = result.replace("\\", "");
                    result = result.replace("''", "");
                    result = result.substring(1, result.length() - 1);
                    Constant.showLog("RegistrationActivity :- Response :- "+result);
                    int auto = new ParseJSON(result,getApplicationContext()).parseRegister();
                    if(auto!=0){
                        constant.showPD();
                        toast.setText("Successfully Register");
                        toast.show();
                        saveLocally();
                        Intent intent = new Intent(getApplicationContext(),AddTableMasterActivity.class);
                        intent.putExtra("from","FromVerificationActivity");
                        startActivity(intent);
                        doFinish();
                    }else{
                        showDia(2);
                        constant.showPD();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    constant.showPD();
                    showDia(2);
                }
            });
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }catch (Exception e){
            e.printStackTrace();
            constant.showPD();
            showDia(2);
        }
    }

    void saveLocally(){
        SharedPreferences.Editor  editor = VerificationActivity.pref.edit();
        editor.putBoolean(getString(R.string.pref_isRegister),true);
        editor.putString(getString(R.string.pref_compName),tv_comp_name.getText().toString());
        editor.putString(getString(R.string.pref_address),tv_address.getText().toString());
        editor.putString(getString(R.string.pref_contactNo),tv_contact_no.getText().toString());
        editor.putString(getString(R.string.pref_expiryDate),tv_expiry_date.getText().toString());
        editor.putString(getString(R.string.pref_username),ed_username.getText().toString());
        editor.putString(getString(R.string.pref_password),ed_password.getText().toString());
        editor.putString(getString(R.string.pref_clientID),ClientID);
        editor.putString(getString(R.string.pref_IMEI),IMEINo);
        editor.putBoolean(getString(R.string.pref_logged),false);
        editor.apply();
    }

    void doFinish(){
        finish();
        toast.cancel();
        Runtime.getRuntime().gc();
        Runtime.getRuntime().freeMemory();
        overridePendingTransition(R.anim.enter,R.anim.exit);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                validation();
            break;

        }
    }

    @Override
    public void onBackPressed() {
        doFinish();
    }
}
