package com.pait.smartpos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.pait.smartpos.adpaters.BackupRestoreAdapter;
import com.pait.smartpos.model.BackupRestoreClass;
import com.pait.smartpos.model.UserProfileClass;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.gson.Gson;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.drive.CreateFileActivity;
import com.pait.smartpos.drive.RetrieveContentsActivity;
import com.pait.smartpos.log.WriteLog;
import com.pait.smartpos.model.Driver_utils;
import com.google.gson.JsonArray;

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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class DataBackupActivity extends AppCompatActivity implements View.OnClickListener {

    private Constant constant, constant1;
    private Toast toast;
    private Button btn_backup;
    private ListView mListViewSamples;
    public static int backupRestoreFlag = -1;
    private String[] titles;
    private List<BackupRestoreClass> list;

    private final Class[] sActivities = new Class[] {
            CreateFileActivity.class, RetrieveContentsActivity.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_data);

        init();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        titles = getResources().getStringArray(R.array.titles_array);

        /*mListViewSamples.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles));
        mListViewSamples.setOnItemClickListener((arg0, arg1, i, arg3) -> {
            if(i<sActivities.length) {
                Intent intent = new Intent(getBaseContext(), sActivities[i]);
                startActivity(intent);
            }else{
                toast.setText("Work In Progress");
                toast.show();
            }
        });*/

        setList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(backupRestoreFlag==1){
            setLastBackup();
        }else if(backupRestoreFlag==2) {
            setLastRestore();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_backup:
                Driver_utils.create_backup(DataBackupActivity.this);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //showDia(0);
        new Constant(DataBackupActivity.this).doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //showDia(0);
                new Constant(DataBackupActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        constant = new Constant(DataBackupActivity.this);
        constant1 = new Constant(getApplicationContext());
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        btn_backup = findViewById(R.id.btn_backup);
        btn_backup.setOnClickListener(this);
        mListViewSamples = findViewById(R.id.listViewSamples);
        list = new ArrayList<>();
    }

    private void setList(){
        list.clear();
        UserProfileClass user = new Constant(getApplicationContext()).getPref();
        BackupRestoreClass backup = new BackupRestoreClass();
        backup.setName(titles[0]);
        String lastBackup = "Not Backup Yet", lastRestore = "Not Restore Yet";
        backup.setName(titles[0]);
        if(user.getLastBackup()==null){
           backup.setLastDate(lastBackup);
        }else{
            backup.setLastDate(user.getLastBackup());
        }
        list.add(backup);

        backup = new BackupRestoreClass();
        backup.setName(titles[1]);
        if(user.getLastRestore()==null){
            backup.setLastDate(lastRestore);
        }else{
            backup.setLastDate(user.getLastRestore());
        }
        list.add(backup);

        mListViewSamples.setAdapter(new BackupRestoreAdapter(getApplicationContext(),list));
        mListViewSamples.setOnItemClickListener((arg0, arg1, i, arg3) -> {
            if(i<sActivities.length) {
                Intent intent = new Intent(getBaseContext(), sActivities[i]);
                startActivity(intent);
            }else{
                toast.setText("Work In Progress");
                toast.show();
            }
        });
    }

    private void setLastBackup() {
        UserProfileClass user = new Constant(getApplicationContext()).getPref();
        String data = "";
        data = 2+"*"+user.getMobileNo() + "^" + user.getImeiNo()+ "^" + user.getUserid();
        Constant.showLog(data);
        new saveUserDetails(2).execute(data);
    }

    private void setLastRestore() {
        UserProfileClass user = new Constant(getApplicationContext()).getPref();
        String data = "";
        data = 3+"*"+user.getMobileNo() + "^" + user.getImeiNo()+ "^" + user.getUserid();
        Constant.showLog(data);
        new saveUserDetails(3).execute(data);
    }

    private class saveUserDetails extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;
        private int type;

        public saveUserDetails(int _type){
            this.type = _type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(DataBackupActivity.this);
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
            backupRestoreFlag = 0;
            try {
                JSONArray jsonArray = new JSONArray(new JSONObject(result).get("UniversalPOSTResult").toString());
                String str = jsonArray.getJSONObject(0).getString("Success");
                if(!str.equals("-1") && !str.equals("")) {
                    String date = new Constant(getApplicationContext()).getDate();
                    String time = new Constant(getApplicationContext()).getTime();
                    UserProfileClass user = new Constant(getApplicationContext()).getPref();
                    if(type==2) {
                        user.setLastBackup(date + " " + time);
                    }else{
                        user.setLastRestore(date + " " + time);
                        showDia(5);
                    }
                    new Constant(getApplicationContext()).saveToPref(user);
                    setList();
                    toast.setText("Success");
                    toast.show();
                }else {
                    toast.setText("Error");
                    toast.show();
                }
            } catch (Exception e) {
                toast.setText("Error");
                toast.show();
                e.printStackTrace();
                writeLog("saveUserDetails_" + e.getMessage());
            }
        }
    }

    private void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DataBackupActivity.this);
        builder.setCancelable(false);
        if (a == 0) {
            builder.setMessage("Do You Want To Exit App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Constant(DataBackupActivity.this).doFinish();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else if (a == 1) {
            builder.setMessage("Data Backup Successful");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Constant(DataBackupActivity.this).doFinish();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else if (a == 5) {
            builder.setMessage("App ReOpen Required");
            builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finishAffinity();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                }
            });
        }
        builder.create().show();
    }

    private void writeLog(String _data) {
        new WriteLog().writeLog(getApplicationContext(), "DataBackupActivity_" + _data);
    }
}
