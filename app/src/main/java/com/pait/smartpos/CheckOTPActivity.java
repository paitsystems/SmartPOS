package com.pait.smartpos;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.pait.smartpos.broadcastreceivers.MySMSBroadcastReceiver;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.interfaces.ServerCallback;
import com.pait.smartpos.interfaces.SmsListener;
import com.pait.smartpos.log.WriteLog;
import com.pait.smartpos.model.UserProfileClass;
import com.pait.smartpos.volleyrequests.VolleyRequests;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class CheckOTPActivity extends AppCompatActivity implements View.OnClickListener,
        MySMSBroadcastReceiver.OTPReceiveListener{

    private EditText ed1, ed2, ed3, ed4, ed5, ed6;
    private AppCompatButton btn_verifyotp, btn_resendotp;
    private Toast toast;
    private UserProfileClass user;
    private Constant constant;
    private final Timer timer = new Timer();
    private int time = 0, flag = -1;
    private TextView tv_timecount, tv_text1, tv_otp;
    private CountDownTimer countDown;
    private String mobNo, imeiNo;
    private String response_value;
    private MySMSBroadcastReceiver receiver;
    private SmsRetrieverClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Constant.liveTestFlag == 1) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        setContentView(R.layout.activity_check_otp);
        
        init();

        user = (UserProfileClass) getIntent().getSerializableExtra("user");
        response_value = user.getOTP();
        //tv_otp.setText(response_value);
        mobNo = user.getMobileNo();
        imeiNo = user.getImeiNo();
        flag = user.getRegisterValidFlag();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle(R.string.title_activity_login);
        }

        /*IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        receiver = new MySMSReceiver();

        receiver.bindListener(new SmsListener() {
            @Override
            public void onReceivedMessage(String message) {
                Constant.showLog("message:" + message);
                ed1.setText(message.substring(0, 1));
                Constant.showLog("message:" + message.substring(0, 1));
                ed2.setText(message.substring(1, 2));
                Constant.showLog("message:" + message.substring(1, 2));
                ed3.setText(message.substring(2, 3));
                Constant.showLog("message:" + message.substring(2, 3));
                ed4.setText(message.substring(3, 4));
                Constant.showLog("message:" + message.substring(3, 4));
                ed5.setText(message.substring(4, 5));
                Constant.showLog("message:" + message.substring(4, 5));
                ed6.setText(message.substring(5, 6));
                Constant.showLog("message:" + message.substring(5, 6));
                timer.cancel();
                countDown.cancel();
                tv_text1.setText("OTP get successfully");
                Constant.showLog("CheckOTPActivity_onReceivedMessage_Called");
            }
        });

        registerReceiver(receiver, filter);*/

        /*ArrayList<String> appCodes = new ArrayList<>();
        AppSignatureHelper hash = new AppSignatureHelper(getApplicationContext());
        appCodes= hash.getAppSignatures();
        String yourhash = appCodes.get(0);
        Constant.showLog("yourhash-"+yourhash);*/

        client = SmsRetriever.getClient(this);
        receiver = new MySMSBroadcastReceiver();
        receiver.initOTPListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        getApplicationContext().registerReceiver(receiver, filter);
        startSMSListener();

        requestOTP();

        if (countDown == null) {
            tv_text1.setText("Your OTP will get within 5 min..");
            int minutes = 5 * 60 * 1000;
            startTimerCount(minutes);
        }

        btn_verifyotp.setOnClickListener(this);
        btn_resendotp.setOnClickListener(this);

        ed1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (ed1.getText().toString().length() == 1) {
                    ed2.requestFocus();
                }
            }
        });

        ed2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (ed2.getText().toString().length() == 1) {
                    ed3.requestFocus();
                }
            }
        });

        ed3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (ed3.getText().toString().length() == 1) {
                    ed4.requestFocus();
                }
            }
        });

        ed4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (ed4.getText().toString().length() == 1) {
                    ed5.requestFocus();
                }
            }
        });

        ed5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (ed5.getText().toString().length() == 1) {
                    ed6.requestFocus();
                }
            }
        });

        ed6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void afterTextChanged(Editable editable) {
                if (ed6.getText().toString().length() == 1) {
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ed6.getWindowToken(), 0);
                    verifyOTP();
                    btn_resendotp.setSupportBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.lightgray));
                }
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verifyotp:
                verifyOTP();
                break;
            case R.id.btn_resendotp:
                Constant.showLog("resend btn click!!");
                btn_verifyotp.setEnabled(true);
                btn_resendotp.setEnabled(false);
                btn_resendotp.setSupportBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.lightgray));
                requestOTP();
                //autoOTP();
                tv_text1.setText("Your OTP will get within 5 min..");
                int minutes = 5 * 60 * 1000;
                startTimerCount(minutes);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        showDia(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showDia(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        try {
            if (receiver != null) {
                //receiver.bindListener(null);
                unregisterReceiver(receiver);
                receiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            writeLog("onDestroy_" + e.getMessage());
        }
        super.onDestroy();
    }

    private void startSMSListener() {
        Task<Void> task = client.startSmsRetriever();

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Constant.showLog("onSuccess");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Constant.showLog("onFailure");

            }
        });
    }

    private void verifyOTP() {
        if (ed1.getText().toString().length() == 1 && ed2.getText().toString().length() == 1 &&
                ed3.getText().toString().length() == 1 && ed4.getText().toString().length() == 1 &&
                ed5.getText().toString().length() == 1 && ed6.getText().toString().length() == 1) {
            String otp = ed1.getText().toString() + ed2.getText().toString() +
                    ed3.getText().toString() + ed4.getText().toString() +
                    ed5.getText().toString() + ed6.getText().toString();
            Constant.showLog(otp);
            Log.d("Log", "response_value:" + response_value);
            writeLog("response_value:" + response_value);
            if (otp.equals(response_value)) {
                Constant.showLog("response_value:" + response_value);
                writeLog("OTP_Matched");
                showDia(1);
            } else {
                writeLog("Invalid_OTP");
                toast.setText(R.string.invalid_otp);
                toast.show();
            }
        } else {
            writeLog("Enter_OTP");
            toast.setText(R.string.pleaseenterotp);
            toast.show();
        }
    }

    private void timerCount() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time++;
                        Constant.showLog("Time" + time);
                        if (time == 180) {
                            timer.cancel();
                            btn_resendotp.setEnabled(true);
                        }
                    }
                });
            }
        }, 0, 1000);

    }

    private void startTimerCount(int noOfMinutes) {
        countDown = new CountDownTimer(noOfMinutes, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                String ms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                tv_timecount.setText(ms);
                //Constant.showLog("count:" + ms);
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onFinish() {
                tv_text1.setText("Time's up!!");
                response_value = "0";
                ed1.setText(null);ed2.setText(null);ed3.setText(null);
                ed4.setText(null);ed5.setText(null);ed6.setText(null);
                ed1.requestFocus();
                btn_verifyotp.setEnabled(false);
                btn_resendotp.setEnabled(true);
                // btn_resendotp.setBackgroundColor(getResources().getColor(R.color.maroon));
                btn_resendotp.setSupportBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.maroon));
            }
        }.start();
    }

    private void requestOTP(){
        try {
            constant.showPD();
            String mobNo = user.getMobileNo();
            String emailId = user.getEmailId();
            String  imeiNo = new Constant(getApplicationContext()).getIMEINo1();
            String _mobNo = URLEncoder.encode(mobNo, "UTF-8");
            String _emailId = URLEncoder.encode(emailId, "UTF-8");
            String _imeiNo = URLEncoder.encode(imeiNo,"UTF-8");
            String  clientId= user.getUserid();
            if(clientId.equals("")){
                clientId = "NA";
            }
            clientId = URLEncoder.encode(clientId, "UTF-8");
            String url = Constant.ipaddress + "/GetOTPCode?mobileno="+_mobNo+"&emailId="+_emailId+"&IMEINo="+_imeiNo+"&clientId=" + clientId;
            Constant.showLog(url);
            writeLog("requestOTP_" + url);

            VolleyRequests requests = new VolleyRequests(CheckOTPActivity.this);
            requests.getOTPCode(url, new ServerCallback() {
                @Override
                public void onSuccess(String response) {
                    constant.showPD();
                    if (!response.equals("0") && !response.equals("-1") && !response.equals("-2")) {
                        //On Success
                        String arr[] = response.split("-");
                        response_value = arr[1];
                        writeLog("requestOTP_Success_" + response);
                    } else if (!response.equals("0") && response.equals("-1") && !response.equals("-2")) {
                        //Already Registered
                        showDia(2);
                        writeLog("requestOTP_Fail_" + response);
                    } else if (!response.equals("0") && !response.equals("-1") && response.equals("-2")) {
                        //Already Registered
                        showDia(4);
                        writeLog("requestOTP_Fail_" + response);
                    }
                }
                @Override
                public void onFailure(String result) {
                    constant.showPD();
                    showDia(4);
                    writeLog("requestOTP_VolleyError_"+result);
                }
            });
        }catch (Exception e){
            showDia(3);
            e.printStackTrace();
            writeLog("requestOTP_Exception_" + e.getMessage());
        }
    }

    private void doFinish() {
        if (countDown != null) {
            countDown.cancel();
        }
        finish();
        Intent intent = new Intent(getApplicationContext(), DrawerTestActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private void init() {
        ed1 = findViewById(R.id.ed1);
        ed2 = findViewById(R.id.ed2);
        ed3 = findViewById(R.id.ed3);
        ed4 = findViewById(R.id.ed4);
        ed5 = findViewById(R.id.ed5);
        ed6 = findViewById(R.id.ed6);

        tv_otp = findViewById(R.id.tv_otp);
        tv_text1 = findViewById(R.id.tv_text1);
        tv_timecount = findViewById(R.id.tv_timecount);
        VerificationActivity.pref = getSharedPreferences(VerificationActivity.PREF_NAME, MODE_PRIVATE);
        btn_verifyotp = findViewById(R.id.btn_verifyotp);
        btn_resendotp = findViewById(R.id.btn_resendotp);
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        constant = new Constant(CheckOTPActivity.this);
    }

    private void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckOTPActivity.this);
        builder.setCancelable(false);
        if (a == 0) {
            builder.setMessage("Do You Want Exist From App");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (countDown != null) {
                        countDown.cancel();
                    }
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else if (a == 1) {
            builder.setMessage("Registration Success");
            builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    setUserData();
                }
            });
        } else if (a == 2) {
            builder.setMessage("Expired...");
            builder.setPositiveButton("Contact Support", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    new Constant(CheckOTPActivity.this).doFinish();
                }
            });
        } else if (a == 3) {
            builder.setMessage("Please Try Again...");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    setUserData();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    new Constant(CheckOTPActivity.this).doFinish();
                }
            });
        } else if (a == 4) {
            builder.setMessage("Invalid UserId...");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        builder.create().show();
    }

    private void writeLog(String _data) {
        new WriteLog().writeLog(getApplicationContext(), "RegistrationActivity_" + _data);
    }

    @Override
    public void onOTPReceived(String message) {
        Constant.showLog("message:" + message);
        ed1.setText(message.substring(0, 1));
        //Constant.showLog("message:" + message.substring(0, 1));
        ed2.setText(message.substring(1, 2));
        //Constant.showLog("message:" + message.substring(1, 2));
        ed3.setText(message.substring(2, 3));
        //Constant.showLog("message:" + message.substring(2, 3));
        ed4.setText(message.substring(3, 4));
        //Constant.showLog("message:" + message.substring(3, 4));
        ed5.setText(message.substring(4, 5));
        //Constant.showLog("message:" + message.substring(4, 5));
        ed6.setText(message.substring(5, 6));
        //Constant.showLog("message:" + message.substring(5, 6));
        timer.cancel();
        countDown.cancel();
        tv_text1.setText("OTP get successfully");
        Constant.showLog("CheckOTPActivity_onReceivedMessage_Called");
    }

    @Override
    public void onOTPTimeOut() {
        btn_verifyotp.setEnabled(false);
        btn_resendotp.setEnabled(true);
    }

    public static class MySMSReceiver extends BroadcastReceiver {
        private boolean b;
        private String text;
        private SmsListener smsListener;

        @Override
        public void onReceive(Context context, Intent intent) {
            final Bundle bundle = intent.getExtras();
            if (bundle != null) {
                final Object[] pdusobj = (Object[]) bundle.get("pdus");
                assert pdusobj != null;
                for (int i = 0; i <= pdusobj.length - 1; i++) {
                    if (smsListener != null) {
                        SmsMessage current_msg = SmsMessage.createFromPdu((byte[]) pdusobj[i]);

                        String cmp_name = current_msg.getDisplayOriginatingAddress();
                        Constant.showLog("mob_no" + cmp_name);

                        String service_center = current_msg.getServiceCenterAddress();
                        Constant.showLog("service_cebter" + service_center);

                        //String sender_no = cmp_name;
                        // if(cmp_name.equals("MD-LNBTCH") && service_center.equals("+919868191090")) {

                        String message = current_msg.getDisplayMessageBody();
                        text = message.replaceAll("[^0-9]", "");
                        Constant.showLog("text:" + text.substring(0, 6));
                        Constant.showLog("text:" + text.substring(0, 1));
                        Constant.showLog("text:" + text.substring(1, 2));
                        if (!b) {
                            smsListener.onReceivedMessage(text);
                        }
                        Constant.showLog("ReadSMS_onReceive_Called");
                    } else {
                        Constant.showLog("NULL");
                    }
                    //writeLog("ReadSMS_onReceive_Called");
                }
            }
        }

        public void bindListener(SmsListener listener) {
            smsListener = listener;
            Constant.showLog("ReadSMS_bindListener_Called");
        }
    }

    private void setUserData() {
        String data = "";
        data = 1+"*"+user.getFirmName() + "^" + user.getUserName() + "^" + user.getCity() + "^" +
                user.getMobileNo() + "^" + user.getImeiNo()+ "^" + user.getEmailId()+ "^" + user.getImgName();
        Constant.showLog(data);
        new saveUserDetails().execute(data);
    }

    @SuppressLint("StaticFieldLeak")
    private class saveUserDetails extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(CheckOTPActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... url) {
            String value = "";
            HttpPost request = new HttpPost(Constant.ipaddress + "/UniversalPOST");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");
            try {
                JSONStringer vehicle = new JSONStringer().object().key("rData").object().key("details").value(url[0]).endObject().endObject();
                Constant.showLog(vehicle.toString());
                StringEntity entity = new StringEntity(vehicle.toString());
                request.setEntity(entity);
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams,Constant.TIMEOUT_CON);
                HttpConnectionParams.setSoTimeout(httpParams, Constant.TIMEOUT_SO);
                DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
                HttpResponse response = httpClient.execute(request);
                Constant.showLog("Saving : " + response.getStatusLine().getStatusCode());
                value = new BasicResponseHandler().handleResponse(response);
            } catch (Exception e) {
                e.printStackTrace();
                writeLog("saveUserDetails_result_" + e.getMessage());
            }
            return value;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Constant.showLog(result);
            pd.dismiss();
            try {
                //String str = new JSONObject(result).get("UniversalPOSTResult").toString();
                JSONArray jsonArray = new JSONArray(new JSONObject(result).get("UniversalPOSTResult").toString());
                String str = jsonArray.getJSONObject(0).getString("Column1");
                if(!str.equals("-1")) {
                    String value[] = str.split("\\^");
                    if (value.length > 1) {
                        user.setUserid(value[0]);
                        user.setExpiryDate(value[1]);
                        user.setStartDate(value[2]);
                        user.setUserType(value[3]);
                        new Constant(getApplicationContext()).saveToPref(user);
                        if(!user.getImgName().equals("NA")) {
                            new UploadImage(user.getImgName()).execute();
                        }else {
                            doFinish();
                        }
                    } else {
                        showDia(3);
                    }
                }else {
                    showDia(2);
                }
            } catch (Exception e) {
                e.printStackTrace();
                writeLog("saveUserDetails_" + e.getMessage());
                showDia(3);
            }
        }
    }

    private class UploadImage extends AsyncTask<Void,Void,String>{

        private ProgressDialog pd;
        private String imagePath;

        private UploadImage(String _imagePath){
            this.imagePath = _imagePath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(CheckOTPActivity.this);
            pd.setMessage("Uploading Image...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result;
            try {
                String ftpaddress = Constant.ftp_adress;
                if(!ftpaddress.equals("")) {
                    String ftpuser = Constant.ftp_username;
                    String ftppass = Constant.ftp_password;
                    String ftpfolder = Constant.ftp_folder;
                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + Constant.folder_name, imagePath);
                    FTPClient client = new FTPClient();
                    client.connect(ftpaddress, 21);
                    client.login(ftpuser, ftppass);
                    client.setFileType(FTP.BINARY_FILE_TYPE);
                    client.enterLocalPassiveMode();
                    FileInputStream ifile = new FileInputStream(f);
                    client.cwd(ftpfolder);
                    client.storeFile(imagePath, ifile);
                    client.disconnect();
                    result = "1";
                    writeLog("AddNewTicketActivity_UploadImage_Success");
                }else{
                    writeLog("AddNewTicketActivity_UploadImage_"+ftpaddress);
                    result = "2";
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = "0";
                writeLog("AddNewTicketActivity_UploadImage_"+e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            doFinish();
        }
    }


}
