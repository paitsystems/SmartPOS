package com.pait.smartpos;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.interfaces.ServerCallback;
import com.pait.smartpos.log.WriteLog;
import com.pait.smartpos.model.UserProfileClass;
import com.pait.smartpos.parse.ParseJSON;
import com.pait.smartpos.volleyrequests.VolleyRequests;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Constant constant, constant1;
    private Toast toast;
    private Button btn_otp, btn_validate;
    private TextView tv_emailid,tv_mobileno,tv_firmname,tv_username, tv_city,
                    tv_isRegistered, tv_cancel, tv_expDate, tv_userId;
    private EditText ed_emailid,ed_mobileno,ed_firmname,ed_username, ed_city, ed_userId;
    private LinearLayout lay_view, lay_add, lay_valid, lay_pic;
    private ImageView img_pic;
    private CardView cardView, cardViewValid;
    private int flag = -1, REQUEST_IMAGE_TAKE = 1;
    private String from = null, imagePath = "NA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        init();

        try {
            from = getIntent().getExtras().getString("from");
            if(from!=null) {
                if (from.equals("first")) {
                    cardView.setVisibility(View.VISIBLE);
                    lay_add.setVisibility(View.VISIBLE);
                    lay_view.setVisibility(View.GONE);
                    cardViewValid.setVisibility(View.INVISIBLE);
                }else{
                    cardView.setVisibility(View.VISIBLE);
                    lay_add.setVisibility(View.GONE);
                    lay_view.setVisibility(View.VISIBLE);
                    cardViewValid.setVisibility(View.INVISIBLE);
                    setSavedData();
                }
            }else{
                toast.setText("Something Went Wrong...");
                toast.show();
            }
        }catch (Exception e){
            e.printStackTrace();
            writeLog(e.getMessage());
            toast.setText("Something Went Wrong...");
            toast.show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_otp:
                flag = 1;
                if(validation()) {
                    requestOTP();
                }
                break;
            case R.id.btn_valid:
                flag = 2;
                if(!TextUtils.isEmpty(ed_userId.getText().toString())){
                    validateClient();
                }
                break;
            case R.id.tv_isRegistered:
                cardView.animate()
                        .translationY(view.getHeight())
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                cardView.setVisibility(View.GONE);
                            }
                        });
                cardViewValid.animate()
                        .translationY(view.getHeight())
                        .alpha(1.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                cardViewValid.setVisibility(View.VISIBLE);
                            }
                        });
                break;
            case R.id.tv_cancel:
                cardView.animate()
                        .translationY(view.getHeight())
                        .alpha(1.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                cardView.setVisibility(View.VISIBLE);
                            }
                        });
                cardViewValid.animate()
                        .translationY(view.getHeight())
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                cardViewValid.setVisibility(View.GONE);
                            }
                        });
                break;
            case R.id.lay_pic:
                takeimage();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //showDia(0);
        new Constant(UserProfileActivity.this).doFinish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(from!=null && !from.equals("first"))
        getMenuInflater().inflate(R.menu.userprofile_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //showDia(0);
                new Constant(UserProfileActivity.this).doFinish();
                break;
            case R.id.refresh:
                if(ConnectivityTest.getNetStat(getApplicationContext())){
                    UserProfileClass user = new Constant(getApplicationContext()).getPref();
                    getUserData(user);
                }else {
                    toast.setText("You Are Offline");
                    toast.show();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_TAKE && resultCode==RESULT_OK){
            try {
                String _imagePath = getRealPathFromURI(Environment.getExternalStorageDirectory() + File.separator + Constant.folder_name + File.separator + "temp.jpg");
                img_pic.setImageBitmap(scaleBitmap(_imagePath));
                long datetime = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("dd_MMM_yyyy_HH_mm_ss", Locale.ENGLISH);
                Date resultdate = new Date(datetime);
                imagePath = "Cust_" + sdf.format(resultdate) + ".jpg";

                File f = new File(Environment.getExternalStorageDirectory() + File.separator + Constant.folder_name);
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }

                OutputStream outFile;
                Bitmap bitmap;
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + Constant.folder_name,imagePath);
                try {
                    outFile = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 15, outFile);
                    outFile.flush();
                    outFile.close();
                } catch (Exception e) {
                    writeLog("UserProfileActivity_onActivityResult_outFile_"+e.getMessage());
                    e.printStackTrace();
                }
            }catch (Exception e){
                writeLog("UserProfileActivity_onActivityResult_"+e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void init() {
        constant = new Constant(UserProfileActivity.this);
        constant1 = new Constant(getApplicationContext());
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);

        btn_otp = findViewById(R.id.btn_otp);
        btn_otp.setOnClickListener(this);
        btn_validate = findViewById(R.id.btn_valid);
        btn_validate.setOnClickListener(this);

        tv_emailid = findViewById(R.id.tv_emailid);
        tv_mobileno = findViewById(R.id.tv_mobileno);
        tv_firmname = findViewById(R.id.tv_firmname);
        tv_username = findViewById(R.id.tv_username);
        tv_city = findViewById(R.id.tv_city);
        tv_isRegistered = findViewById(R.id.tv_isRegistered);
        tv_isRegistered.setOnClickListener(this);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(this);
        tv_expDate = findViewById(R.id.tv_expDate);
        tv_userId = findViewById(R.id.tv_userid);

        ed_emailid =  findViewById(R.id.ed_emailid);
        ed_mobileno = findViewById(R.id.ed_mobileno);
        ed_firmname = findViewById(R.id.ed_firmname);
        ed_username = findViewById(R.id.ed_username);
        ed_city = findViewById(R.id.ed_city);
        ed_userId = findViewById(R.id.ed_userid);

        lay_add = findViewById(R.id.lay_add);
        lay_view = findViewById(R.id.lay_view);
        lay_valid = findViewById(R.id.lay_valid);
        lay_pic = findViewById(R.id.lay_pic);
        lay_pic.setOnClickListener(this);

        img_pic = findViewById(R.id.img_pic);

        cardView = findViewById(R.id.cardView);
        cardViewValid = findViewById(R.id.cardViewValid);
    }

    private void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setCancelable(false);
        if (a == 0) {
            builder.setMessage("Do You Want To Exit App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Constant(UserProfileActivity.this).doFinish();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
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
                    new Constant(UserProfileActivity.this).doFinish();
                }
            });
        } else if (a == 3) {
            builder.setMessage("Invalid UserId...");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else if (a == 4) {
            builder.setMessage("Something Went Wrong...");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(ConnectivityTest.getNetStat(getApplicationContext())) {
                        requestOTP();
                    }else{
                        toast.setText("You Are Offline");
                        toast.show();
                    }
                    dialog.dismiss();
                }
            });
        }else if (a == 5) {
            builder.setMessage("Something Went Wrong...");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else if (a == 6) {
            builder.setMessage("No Record Available");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        builder.create().show();
    }

    private boolean validation(){
        boolean status = true;
        if(TextUtils.isEmpty(ed_username.getText().toString())){
            status = false;
            toast.setText("Username Required");
        }else if(TextUtils.isEmpty(ed_emailid.getText().toString())){
            status = false;
            toast.setText("Email Id Required");
        }else if(!Patterns.EMAIL_ADDRESS.matcher(ed_emailid.getText().toString()).matches()){
            status = false;
            toast.setText("Valid Email id Required");
        }else if(TextUtils.isEmpty(ed_mobileno.getText().toString()) || ed_mobileno.getText().toString().length()<10){
            status = false;
            toast.setText("Mobile No Required");
        }else if(TextUtils.isEmpty(ed_firmname.getText().toString())){
            status = false;
            toast.setText("Firm Name Required");
        }else if(TextUtils.isEmpty(ed_city.getText().toString())){
            status = false;
            toast.setText("City Name Required");
        }
        if(!status){
            toast.show();
        }
        return  status;
    }

    private void requestOTP() {
        if (ConnectivityTest.getNetStat(getApplicationContext())) {
            try {
                constant.showPD();
                String mobNo = ed_mobileno.getText().toString();
                String emailId = ed_emailid.getText().toString();
                String imeiNo = new Constant(getApplicationContext()).getIMEINo1();
                String _mobNo = URLEncoder.encode(mobNo, "UTF-8");
                String _emailId = URLEncoder.encode(emailId, "UTF-8");
                String _imeiNo = URLEncoder.encode(imeiNo, "UTF-8");
                String  clientId =  "NA";
                clientId = URLEncoder.encode(clientId, "UTF-8");
                String url = Constant.ipaddress + "/GetOTPCode?mobileno=" + _mobNo + "&emailId=" + _emailId + "&IMEINo=" + _imeiNo+"&clientId=" + clientId;
                Constant.showLog(url);
                writeLog("requestOTP_" + url);

                VolleyRequests requests = new VolleyRequests(UserProfileActivity.this);
                requests.getOTPCode(url, new ServerCallback() {
                    @Override
                    public void onSuccess(String response) {
                        constant.showPD();
                        if (!response.equals("0") && !response.equals("-1") && !response.equals("-2")) {
                            //On Success
                            String arr[] = response.split("-");
                            doThis(arr[1]);
                            writeLog("requestOTP_Success_" + response);
                        } else if (!response.equals("0") && response.equals("-1") && !response.equals("-2")) {
                            //Already Registered
                            showDia(2);
                            writeLog("requestOTP_Fail_" + response);
                        } else if (!response.equals("0") && !response.equals("-1") && response.equals("-2")) {
                            //Already Registered
                            showDia(3);
                            writeLog("requestOTP_Fail_" + response);
                        }
                    }

                    @Override
                    public void onFailure(String result) {
                        constant.showPD();
                        showDia(4);
                        writeLog("requestOTP_VolleyError_" + result);
                    }
                });
            } catch (Exception e) {
                showDia(4);
                e.printStackTrace();
                writeLog("requestOTP_Exception_" + e.getMessage());
            }
        } else {
            toast.setText("You Are Offline");
            toast.show();
        }
    }

    private void doThis(String response) {
        writeLog("requestOTP_Success_" + response);
        UserProfileClass user = setData();
        user.setOTP(response);
        user.setImeiNo(new Constant(getApplicationContext()).getIMEINo1());
        user.setRegisterValidFlag(flag);
        user.setImgName(imagePath);
        String  clientId= ed_userId.getText().toString();
        if(clientId.equals("")){
            clientId = "NA";
        }
        user.setUserid(clientId);
        if(flag==1) {
            Intent intent = new Intent(getApplicationContext(), CheckOTPActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }else{
            getUserData(user);
        }
    }

    private void takeimage(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = Constant.checkFolder(Constant.folder_name);
        f = new File(f.getAbsolutePath(),"temp.jpg");
        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName()
                + ".provider", f);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent,REQUEST_IMAGE_TAKE);
    }

    private UserProfileClass setData(){
        UserProfileClass user = new UserProfileClass();
        user.setUserName(ed_username.getText().toString());
        user.setEmailId(ed_emailid.getText().toString());
        user.setMobileNo(ed_mobileno.getText().toString());
        user.setFirmName(ed_firmname.getText().toString());
        user.setCity(ed_city.getText().toString());
        return user;
    }

    private void setSavedData(){
        UserProfileClass user = new Constant(getApplicationContext()).getPref();
        tv_username.setText(user.getUserName());
        tv_emailid.setText(user.getEmailId());
        tv_mobileno.setText(user.getMobileNo());
        tv_firmname.setText(user.getFirmName());
        tv_city.setText(user.getCity());
        tv_expDate.setText(user.getExpiryDate());
        tv_userId.setText(user.getUserid());
    }

    private void validateClient(){
         doThis("NA");
    }

    private void getUserData(UserProfileClass user){
        String data = "";
        data = 5+"*"+user.getUserid() + "^" + user.getMobileNo() + "^" + user.getImeiNo();
        Constant.showLog(data);
        new getUserDetails().execute(data);
    }

    private class getUserDetails extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(UserProfileActivity.this);
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
                //JSONArray jsonArray = new JSONArray(new JSONObject(result).get("UniversalPOSTResult").toString());
                String str = new JSONObject(result).get("UniversalPOSTResult").toString();
                int a = new ParseJSON(str,getApplicationContext()).parseGetUserDetail();
                if(a!=0) {
                    if(from!=null && from.equals("first")) {
                        doFinish();
                    }else{
                       setSavedData();
                    }
                }else{
                    showDia(6);
                }
            } catch (Exception e) {
                e.printStackTrace();
                writeLog("saveUserDetails_" + e.getMessage());
                showDia(5);
            }
        }
    }

    private void doFinish() {
        finish();
        Intent intent = new Intent(getApplicationContext(), DrawerTestActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String s = cursor.getString(idx);
            cursor.close();
            return s;
        }
    }

    public Bitmap scaleBitmap(String imagePath) {
        Bitmap resizedBitmap = null;
        try {
            int inWidth, inHeight;
            InputStream in;
            in = new FileInputStream(imagePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            //in = null;
            inWidth = options.outWidth;
            inHeight = options.outHeight;
            in = new FileInputStream(imagePath);
            options = new BitmapFactory.Options();
            options.inSampleSize = Math.max(inWidth / 300, inHeight / 300);
            Bitmap roughBitmap = BitmapFactory.decodeStream(in, null, options);

            Matrix m = new Matrix();
            RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
            RectF outRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
            m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
            float[] values = new float[9];
            m.getValues(values);
            resizedBitmap = Bitmap.createScaledBitmap(roughBitmap, (int) (roughBitmap.getWidth() * values[0]), (int) (roughBitmap.getHeight() * values[4]), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resizedBitmap;
    }

    private void writeLog(String _data) {
        new WriteLog().writeLog(getApplicationContext(), "UserProfileActivity_" + _data);
    }
}
