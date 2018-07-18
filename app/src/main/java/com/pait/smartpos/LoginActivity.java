package com.pait.smartpos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.model.CategoryClass;
import com.pait.smartpos.model.ProductClass;
import com.pait.smartpos.model.TableClass;

import org.json.JSONArray;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailView;
    private EditText mPasswordView;
    private CheckBox remember;
    private ProgressDialog pd;

    //Check Ip Address,App Name, Database Version, Application ID
    //public static String ipaddress = "http://172.30.1.233/LNBRESTO/service.svc";
    //public static String ipaddress = "http://win9.cloudapp.net/LNBRESTO/service.svc";
    //public static String macAdd = "";

    private InputMethodManager input;
    private DBHandlerR db;
    private Drawable drawable;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*if(getActionBar()!=null) {
            getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#990000")));
        }*/
        if(getSupportActionBar()!=null){
            getSupportActionBar().show();
        }
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        remember = (CheckBox) findViewById(R.id.remember);
        db = new DBHandlerR(LoginActivity.this);
        input = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        drawable = mEmailView.getBackground();

        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);

        mEmailView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showSoftKeyboard(mEmailView);
                mEmailView.setBackgroundDrawable(drawable);
                mEmailView.setError(null);
                return false;
            }
        });

        mPasswordView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showSoftKeyboard(mPasswordView);
                mPasswordView.setBackgroundDrawable(drawable);
                mPasswordView.setError(null);
                return false;
            }
        });

        mPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mPasswordView.setBackgroundDrawable(drawable);
                mPasswordView.setError(null);
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    input.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);
                    handled = true;
                    attemptLogin();
                }
                return handled;
            }
        });

        final Button mEmailSignInButton = (Button) findViewById(R.id.login);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                input.hideSoftInputFromWindow(mEmailSignInButton.getWindowToken(), 0);
                attemptLogin();
            }
        });

        /*if(pref.contains("logged")){
            if(pref.getBoolean("logged", false)){
                if (ConnectivityTest.getNetStat(LoginActivity.this)) {
                    getTableData();
                } else {
                    Toast.makeText(getApplicationContext(), "Could Not Connect To Internet", Toast.LENGTH_LONG).show();
                }
            }
        }*/

        /*if(VerificationActivity.pref.contains("logged")) {
            if (VerificationActivity.pref.getBoolean("logged", false)) {
                Intent intent = new Intent(getApplicationContext(), MainActivity1.class);
                Runtime.getRuntime().gc();
                Runtime.getRuntime().freeMemory();
                startActivity(intent);
                finish();
            }
        }*/
    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
        Runtime.getRuntime().gc();
        Runtime.getRuntime().freeMemory();
    }

    private void doThing(){
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        String uname = VerificationActivity.pref.getString("username","");
        String pass = VerificationActivity.pref.getString("password","");

        if (email.equals(uname) && password.equals(pass)) {
            SharedPreferences.Editor editor = VerificationActivity.pref.edit();
            if (remember.isChecked()) {
                editor.putBoolean("logged", true);
            } else {
                editor.putBoolean("logged", false);
            }
            editor.apply();
            //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            Intent intent = new Intent(getApplicationContext(), DrawerTestActivity.class);
            Runtime.getRuntime().gc();
            Runtime.getRuntime().freeMemory();
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.enter,R.anim.exit);
        }else{
            toast.setText("Invalid Username/Password...");
            toast.show();
        }
    }

    private void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void attemptLogin() {
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            mPasswordView.setBackgroundResource(R.drawable.apptheme_textfield_activated_holo_light);
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            mEmailView.setBackgroundResource(R.drawable.apptheme_textfield_activated_holo_light);
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            doThing();
            /*if(!VerificationActivity.pref.contains("logged")) {
                doThing();
            }else if(VerificationActivity.pref.contains("logged")){
                if(VerificationActivity.pref.getBoolean("logged", false)){
                    Intent intent = new Intent(getApplicationContext(), MainActivity1.class);
                    Runtime.getRuntime().gc();
                    Runtime.getRuntime().freeMemory();
                    startActivity(intent);
                    finish();
                }else {
                    doThing(email,password);
                }
            }*/
            /*if(!pref.contains("logged")){
                if (ConnectivityTest.getNetStat(LoginActivity.this)) {
                    String url = LoginActivity.ipaddress + "/getEmpValid?UserName=" + mEmailView.getText().toString() + "&Password=" + mPasswordView.getText().toString();
                    Log.d("Login URL", url);
                    new UserLoginTask().execute(url);
                } else {
                    Toast.makeText(getApplicationContext(), "Could Not Connect To Internet", Toast.LENGTH_LONG).show();
                }
            }else if(pref.contains("logged")){
                if(pref.getBoolean("logged", false)){
                    if (ConnectivityTest.getNetStat(LoginActivity.this)) {
                        getTableData();
                    } else {
                        Toast.makeText(getApplicationContext(), "Could Not Connect To Internet", Toast.LENGTH_LONG).show();
                    }
                }else{
                    if(pref.getString("username","").equals(mEmailView.getText().toString())&&pref.getString("password","").equals(mPasswordView.getText().toString())) {
                        if (ConnectivityTest.getNetStat(LoginActivity.this)) {
                            getTableData();
                        } else {
                            Toast.makeText(getApplicationContext(), "Could Not Connect To Internet", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        if (ConnectivityTest.getNetStat(LoginActivity.this)) {
                            String url = LoginActivity.ipaddress + "/getEmpValid?UserName=" + mEmailView.getText().toString() + "&Password=" + mPasswordView.getText().toString();
                            Log.d("Login URL", url);
                            new UserLoginTask().execute(url);
                        } else {
                            Toast.makeText(getApplicationContext(), "Could Not Connect To Internet", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }*/
        }
    }

    private void doThing(String email, String password){
        if (email.equals("admin") && password.equals("admin")) {
            SharedPreferences.Editor editor = VerificationActivity.pref.edit();
            editor.putString("username", email);
            editor.putString("password", password);
            if (remember.isChecked()) {
                editor.putBoolean("logged", true);
            } else {
                editor.putBoolean("logged", false);
            }
            editor.apply();
            //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            Intent intent = new Intent(getApplicationContext(), DrawerTestActivity.class);
            Runtime.getRuntime().gc();
            Runtime.getRuntime().freeMemory();
            startActivity(intent);
            finish();
        }else{
            toast.setText("Invalid Username/Password...");
            toast.show();
        }
    }

    private class UserLoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(LoginActivity.this);
            pd.setCancelable(false);
            pd.setMessage("Logging In Please Wait...");
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return Post.POST(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && !result.equals("")) {
                try {
                    result = result.replace("\\", "");
                    result = result.replace("''", "");
                    result = result.substring(1, result.length() - 1);
                    JSONArray jsonArray = new JSONArray(result);
                    int id = 0, branchid = 0;
                    String uname = null;
                    if (jsonArray.length() >= 1) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            id = jsonArray.getJSONObject(i).getInt("Id");
                            Log.d("id",""+id);
                            uname = jsonArray.getJSONObject(i).getString("EmpName");
                            Log.d("uname",uname);
                            branchid = jsonArray.getJSONObject(i).getInt("BranchID");
                            Log.d("BranchID",""+branchid);
                        }
                        SharedPreferences.Editor editor = VerificationActivity.pref.edit();
                        editor.putInt("id", id);
                        editor.putString("uname", uname);
                        editor.putInt("branchid", branchid);
                        editor.apply();
                        pd.dismiss();
                        getTableData();
                    }else {
                        Toast.makeText(getApplicationContext(), "Invalid Username/Password...", Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Something Go Wrong...", Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Check Network Connection...", Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
        }
    }

    private void getTableData(){
        String url = Constant.ipaddress + "/getTable?id=0";
        Log.d("getTable() URL", url);
        if (ConnectivityTest.getNetStat(LoginActivity.this)) {
            new LoadTable().execute(url);
        } else {
            Toast.makeText(getApplicationContext(), "Network Connection Error", Toast.LENGTH_LONG).show();
            pd.dismiss();
        }
    }

    private class LoadTable extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(LoginActivity.this);
            pd.setMessage("Getting TableClass Data");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return Post.POST(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null && result.length() != 0) {
                try {
                    db.deleteTable();
                    TableClass tableClass = new TableClass();
                    result = result.replace("\\", "");
                    result = result.replace("''", "");
                    result = result.substring(1, result.length() - 1);
                    JSONArray jsonArray = new JSONArray(result);
                    if (jsonArray.length() >= 1) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            tableClass.setTable_ID(jsonArray.getJSONObject(i).getInt("Id"));
                            tableClass.setTables(jsonArray.getJSONObject(i).getString("Name"));
                            db.addTable(tableClass);
                        }
                        String url = Constant.ipaddress + "/getCategory?id=0";
                        Log.d("getCategory() URL", url);
                        if (ConnectivityTest.getNetStat(LoginActivity.this)) {
                            new LoadCategory().execute(url);
                        } else {
                            Toast.makeText(getApplicationContext(), "Could Not Connect To Internet", Toast.LENGTH_LONG).show();
                            pd.dismiss();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    pd.dismiss();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Could Not Retrieve Data", Toast.LENGTH_LONG).show();
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Something Go Wrong...", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class LoadCategory extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Getting CategoryClass Data");
        }

        @Override
        protected String doInBackground(String... params) {
            return Post.POST(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null && result.length() != 0) {
                try {
                    db.deleteCategory();
                    CategoryClass cat = new CategoryClass();
                    result = result.replace("\\", "");
                    result = result.replace("''", "");
                    result = result.substring(1, result.length() - 1);
                    JSONArray jsonArray = new JSONArray(result);
                    if (jsonArray.length() >= 1) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            cat.setCategory_ID(jsonArray.getJSONObject(i).getInt("Id"));
                            cat.setCategory(jsonArray.getJSONObject(i).getString("CategoryClass"));
                            db.addCategory(cat);
                        }
                        String url = Constant.ipaddress + "/getProduct?id=0";
                        Log.d("getProduct() URL", url);
                        if (ConnectivityTest.getNetStat(LoginActivity.this)) {
                            new LoadProduct().execute(url);
                        } else {
                           Toast.makeText(getApplicationContext(), "Could Not Connect To Internet", Toast.LENGTH_LONG).show();
                           pd.dismiss();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Something Go Wrong...", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "No Record Available", Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
        }
    }

    private class LoadProduct extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Getting ProductClass Data");
        }

        @Override
        protected String doInBackground(String... params) {
            return Post.POST(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null && result.length() != 0) {
                try {
                    db.deleteProduct();
                    ProductClass prod = new ProductClass();
                    result = result.replace("\\", "");
                    result = result.replace("''", "");
                    result = result.substring(1, result.length() - 1);
                    JSONArray jsonArray = new JSONArray(result);
                    if (jsonArray.length() >= 1) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            prod.setProduct_ID(jsonArray.getJSONObject(i).getInt("Id"));
                            prod.setProduct_Name(jsonArray.getJSONObject(i).getString("ProductClass"));
                            prod.setProduct_Cat(jsonArray.getJSONObject(i).getInt("CategoryClass"));
                            prod.setProduct_Rate(jsonArray.getJSONObject(i).getDouble("Rate"));
                            prod.setProduct_Barcode(jsonArray.getJSONObject(i).getString("Barcode"));
                            prod.setProduct_KArea(jsonArray.getJSONObject(i).getString("KitchenArea"));
                            db.addProduct(prod);
                        }
                        SharedPreferences.Editor editor = VerificationActivity.pref.edit();
                        editor.putString("username", mEmailView.getText().toString());
                        editor.putString("password", mPasswordView.getText().toString());
                        if (remember.isChecked()) {
                            editor.putBoolean("logged", true);
                        } else {
                            editor.putBoolean("logged", false);
                        }
                        editor.apply();
                        //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        Intent intent = new Intent(getApplicationContext(), DrawerTestActivity.class);
                        Runtime.getRuntime().gc();
                        Runtime.getRuntime().freeMemory();
                        startActivity(intent);
                        finish();
                        pd.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Something Go Wrong...", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "No Record Available", Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
            pd.dismiss();
        }
    }

}

