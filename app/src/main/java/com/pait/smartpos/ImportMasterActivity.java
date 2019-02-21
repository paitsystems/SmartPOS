package com.pait.smartpos;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.log.WriteLog;
import com.pait.smartpos.model.UserProfileClass;
import com.pait.smartpos.model.XlsxCon;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONStringer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

public class ImportMasterActivity extends AppCompatActivity implements View.OnClickListener {

    private Constant constant, constant1;
    private Toast toast;
    private Button btn_import;
    private String filepath = null, dwnlfilepath = null;
    private WebView webView;
    private TextView tv_dwnldPath, tv_importpath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_master);

        init();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        /*Button crashButton = new Button(this);
        crashButton.setText("Crash!");
        crashButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Crashlytics.getInstance().crash(); // Force a crash
            }
        });
        addContentView(crashButton,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));*/

        webView.loadUrl("file:///android_asset/import.html");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.equals("file:///%22hrupin://download/%22")) {
                    if(ConnectivityTest.getNetStat(getApplicationContext())){
                        new DownloadImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "ABC");
                    }else{
                        toast.setText("You Are Offline");
                        toast.show();
                    }
                    return true;
                }else if(url.equals("file:///%22hrupin://select_file/%22")){
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("gagt/sdf");
                    try {
                        startActivityForResult(intent, 1);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                    /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                            +  File.separator + Constant.folder_name + File.separator);
                    intent.setDataAndType(uri, "text/csv");
                    startActivityForResult(intent, 1);*/
                    return true;
                }else if(url.equals("file:///%22hrupin://email/%22")){
                    setUserData();
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_import:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("gagt/sdf");
                try {
                    startActivityForResult(intent, 1);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        new Constant(ImportMasterActivity.this).doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //showDia(0);
                new Constant(ImportMasterActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            toast.setText("No File Selected");
            return;
        }
        if (requestCode == 1) {
            //filepath = data.getData().getPath();
            Uri uri = data.getData();
            filepath = getPath(getApplicationContext(),uri);
            Constant.showLog(filepath);
            tv_importpath.setVisibility(View.VISIBLE);
            tv_importpath.setText(filepath);
            writeLog(filepath);
            new readFile().execute(filepath);
        }
    }

    public String getPath(Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else
            if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private void init() {
        constant = new Constant(ImportMasterActivity.this);
        constant1 = new Constant(getApplicationContext());
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        btn_import = (Button) findViewById(R.id.btn_import);
        btn_import.setOnClickListener(this);
        webView = (WebView) findViewById(R.id.webView);
        tv_dwnldPath = (TextView) findViewById(R.id.tv_dwnlpath);
        tv_importpath = (TextView) findViewById(R.id.tv_importpath);
        tv_dwnldPath.setVisibility(View.GONE);
        tv_importpath.setVisibility(View.GONE);
    }

    private void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ImportMasterActivity.this);
        builder.setCancelable(false);
        if (a == 0) {
            builder.setMessage("Do You Want To Exit App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Constant(ImportMasterActivity.this).doFinish();
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
            builder.setMessage("Error While Importing File");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
        } else if (a == 3) {
            builder.setMessage("Do You Want To Exit ?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        } else if (a == 4) {
            builder.setMessage("Data Imported Succesfully");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    showDia(5);
                }
            });
        } else if (a == 5) {
            builder.setMessage("App Reopen Required");
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

    private class DownloadImage extends AsyncTask<String, Integer, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ImportMasterActivity.this);
            pd.setCancelable(false);
            pd.setMax(1);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setMessage("Downloading File...");
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                FTPClient ftp = new FTPClient();
                ftp.connect(Constant.ftp_adress, 21);
                int reply = ftp.getReplyCode();
                if (FTPReply.isPositiveCompletion(reply)) {
                    ftp.setConnectTimeout(60000);
                    ftp.setSoTimeout(45000);
                    ftp.login(Constant.ftp_username, Constant.ftp_password);
                    Constant.showLog(ftp.getReplyString());
                    ftp.setFileType(FTP.BINARY_FILE_TYPE);
                    ftp.enterLocalPassiveMode();
                    //ftp.setDataTimeout(30000);
                    int count = 0;
                    OutputStream outstream;
                    Constant.checkFolder(Constant.folder_name);
                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + Constant.folder_name + "/" + Constant.import_fileName);
                    if(f.exists()){
                        if(f.delete()){
                            Constant.showLog("File Deleted");
                        }
                        if(f.createNewFile()){
                            Constant.showLog("File Created");
                        }
                    }
                    String fname = Environment.getExternalStorageDirectory() + File.separator + Constant.folder_name + "/" + Constant.import_fileName;
                    dwnlfilepath = fname;
                    outstream = new BufferedOutputStream(new FileOutputStream(fname));
                    if (ftp.retrieveFile(Constant.import_fileName, outstream)) {
                        outstream.close();
                        count++;
                        pd.setProgress(count);
                        result = "ok";
                    }
                    ftp.logout();
                    ftp.disconnect();
                    pd.dismiss();
                    return result;
                }
            } catch (Exception e) {
                e.printStackTrace();
                pd.dismiss();
                result = "";
                return result;
                // Toast.makeText(getApplicationContext(),"FTP Error", Toast.LENGTH_LONG).show();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            if (result.equals("ok")) {
                tv_dwnldPath.setVisibility(View.VISIBLE);
                tv_dwnldPath.setText(dwnlfilepath);
                toast.setText("File Downloaded Successfully");
                toast.show();
            } else {
                tv_dwnldPath.setVisibility(View.GONE);
                toast.setText("Error While Downloading File");
                toast.show();
            }
        }
    }

    private class readFile extends AsyncTask<String, Void, String> {
        ProgressDialog pd1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd1 = new ProgressDialog(ImportMasterActivity.this);
            pd1.setMessage("Importing Data Please Wait");
            pd1.setCancelable(false);
            pd1.setMax(0);
            pd1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd1.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String status = "";
            if (filepath != null) {
                Log.d("FilePath", filepath);
                try {
                    InputStream inputStream = new FileInputStream(filepath);
                    String f1[] = filepath.split("\\.");
                    if (f1.length == 2) {
                        if (f1[1].equals("xls")) {
                            Workbook wb = new HSSFWorkbook(inputStream);
                            inputStream.close();
                            int sheetsNo = wb.getNumberOfSheets();
                            writeLog("readFile_NumberOfSheets_"+sheetsNo);
                            Constant.showLog("getNumberOfSheets_"+sheetsNo);
                            if(sheetsNo>0) {
                                for(int a=0;a<sheetsNo;a++) {
                                    Sheet sheet = wb.getSheetAt(a);
                                    pd1.setMax(sheet.getLastRowNum());
                                    boolean sequence = false;
                                    int count = 0;
                                    if (sheet != null) {
                                        XlsxCon dbAdapter = new XlsxCon(ImportMasterActivity.this);
                                        dbAdapter.open();
                                        String sheetName = sheet.getSheetName();
                                        Constant.showLog(sheetName);

                                        for (Iterator<Row> rit = sheet.rowIterator(); rit.hasNext(); ) {
                                            count++;
                                            Constant.showLog("Row Count " + count);
                                            Row row = rit.next();
                                            ContentValues cv = new ContentValues();

                                            if (sheetName.equals(XlsxCon.Sheet_CPM)) {
                                                if (count == 1) {
                                                    String _auto = row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _id = row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _compName = row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _city = row.getCell(3, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _state = row.getCell(4, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _adress = row.getCell(5, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _phone = row.getCell(6, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _email = row.getCell(7, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _initital = row.getCell(8, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _printmsg = row.getCell(9, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _printname = row.getCell(10, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _panno = row.getCell(11, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _gstno = row.getCell(12, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _muname = row.getCell(13, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _mpwd = row.getCell(14, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _mfrom = row.getCell(15, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _smtp = row.getCell(16, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _gsttype = row.getCell(17, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

                                                    if (_auto.equals(XlsxCon.CPM_Auto)) {
                                                        if (_id.equals(XlsxCon.CPM_Id)) {
                                                            if (_compName.equals(XlsxCon.CPM_CompanyName)) {
                                                                if (_city.equals(XlsxCon.CPM_City)) {
                                                                    if (_state.equals(XlsxCon.CPM_State)) {
                                                                        if (_adress.equals(XlsxCon.CPM_Address)) {
                                                                            if (_phone.equals(XlsxCon.CPM_Phone)) {
                                                                                if (_email.equals(XlsxCon.CPM_Email)) {
                                                                                    if (_initital.equals(XlsxCon.CPM_Initials)) {
                                                                                        if (_printmsg.equals(XlsxCon.CPM_PrintMsg)) {
                                                                                            if (_printname.equals(XlsxCon.CPM_PrintName)) {
                                                                                                if (_panno.equals(XlsxCon.CPM_PANNo)) {
                                                                                                    if (_gstno.equals(XlsxCon.CPM_GSTNo)) {
                                                                                                        if (_muname.equals(XlsxCon.CPM_MailUname)) {
                                                                                                            if (_mpwd.equals(XlsxCon.CPM_MailPwd)) {
                                                                                                                if (_mfrom.equals(XlsxCon.CPM_Mailfrom)) {
                                                                                                                    if (_smtp.equals(XlsxCon.CPM_SMTPServer)) {
                                                                                                                        if (_gsttype.equals(XlsxCon.CPM_GSTType)) {
                                                                                                                            sequence = true;
                                                                                                                            dbAdapter.delete(DBHandler.Table_CompanyMaster);
                                                                                                                            Constant.showLog(DBHandler.Table_CompanyMaster + " Deleted");
                                                                                                                        } else {
                                                                                                                            status = null;
                                                                                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_GSTType);
                                                                                                                            writeLog("readFile_Column_Not_Matched_" + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_GSTType);
                                                                                                                            break;
                                                                                                                        }
                                                                                                                    } else {
                                                                                                                        status = null;
                                                                                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_SMTPServer);
                                                                                                                        writeLog("readFile_Column_Not_Matched_" + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_SMTPServer);
                                                                                                                        break;
                                                                                                                    }
                                                                                                                } else {
                                                                                                                    status = null;
                                                                                                                    showColMisMatchToast("Sheet " + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_Mailfrom);
                                                                                                                    writeLog("readFile_Column_Not_Matched_" + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_Mailfrom);
                                                                                                                    break;
                                                                                                                }
                                                                                                            } else {
                                                                                                                status = null;
                                                                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_MailPwd);
                                                                                                                writeLog("readFile_Column_Not_Matched_" + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_MailPwd);
                                                                                                                break;
                                                                                                            }
                                                                                                        } else {
                                                                                                            status = null;
                                                                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_MailUname);
                                                                                                            writeLog("readFile_Column_Not_Matched_" + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_MailUname);
                                                                                                            break;
                                                                                                        }

                                                                                                    } else {
                                                                                                        status = null;
                                                                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_GSTNo);
                                                                                                        writeLog("readFile_Column_Not_Matched_" + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_GSTNo);
                                                                                                        break;
                                                                                                    }
                                                                                                } else {
                                                                                                    status = null;
                                                                                                    showColMisMatchToast("Sheet " + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_PANNo);
                                                                                                    writeLog("readFile_Column_Not_Matched_" + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_PANNo);
                                                                                                    break;
                                                                                                }

                                                                                            } else {
                                                                                                status = null;
                                                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_PrintName);
                                                                                                writeLog("readFile_Column_Not_Matched_" + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_PrintName);
                                                                                                break;
                                                                                            }
                                                                                        } else {
                                                                                            status = null;
                                                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_PrintMsg);
                                                                                            writeLog("readFile_Column_Not_Matched_" + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_PrintMsg);
                                                                                            break;
                                                                                        }
                                                                                    } else {
                                                                                        status = null;
                                                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_Initials);
                                                                                        writeLog("readFile_Column_Not_Matched_" + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_Initials);
                                                                                        break;
                                                                                    }
                                                                                } else {
                                                                                    status = null;
                                                                                    showColMisMatchToast("Sheet " + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_Email);
                                                                                    writeLog("readFile_Column_Not_Matched_" + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_Email);
                                                                                    break;
                                                                                }
                                                                            } else {
                                                                                status = null;
                                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_Phone);
                                                                                writeLog("readFile_Column_Not_Matched_" + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_Phone);
                                                                                break;
                                                                            }
                                                                        } else {
                                                                            status = null;
                                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_Address);
                                                                            writeLog("readFile_Column_Not_Matched_" + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_Address);
                                                                            break;
                                                                        }
                                                                    } else {
                                                                        status = null;
                                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_State);
                                                                        writeLog("readFile_Column_Not_Matched_" + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_State);
                                                                        break;
                                                                    }

                                                                } else {
                                                                    status = null;
                                                                    showColMisMatchToast("Sheet " + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_CompanyName);
                                                                    writeLog("readFile_Column_Not_Matched_" + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_CompanyName);
                                                                    break;
                                                                }
                                                            } else {
                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_Id);
                                                                status = null;
                                                                writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_Id);
                                                                break;
                                                            }
                                                        } else {
                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_Auto);
                                                            status = null;
                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_CPM + " Column " + XlsxCon.CPM_Auto);
                                                            break;
                                                        }
                                                    }
                                                } else if (sequence) {
                                                    row.getCell(0, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(1, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(2, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(3, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(4, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(5, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(6, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(7, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(8, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(9, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(10, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(11, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(12, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(13, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(14, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(15, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(16, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(17, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

                                                    cv.put(DBHandler.CPM_Auto, row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CPM_Id, row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CPM_CompanyName, row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CPM_City, row.getCell(3, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CPM_State, row.getCell(4, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CPM_Address, row.getCell(5, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CPM_Phone, row.getCell(6, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CPM_Email, row.getCell(7, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CPM_Initials, row.getCell(8, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CPM_PrintMsg, row.getCell(9, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CPM_PrintName, row.getCell(10, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CPM_PANNo, row.getCell(11, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CPM_GSTNo, row.getCell(12, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CPM_MailUname, row.getCell(13, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CPM_MailPwd, row.getCell(14, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CPM_Mailfrom, row.getCell(15, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CPM_SMTPServer, row.getCell(16, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CPM_GSTType, row.getCell(17, Row.CREATE_NULL_AS_BLANK).getStringCellValue());

                                                    if (!row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue().equalsIgnoreCase("")) {
                                                        dbAdapter.insert(DBHandler.Table_CompanyMaster, cv);
                                                        Constant.showLog(DBHandler.Table_CompanyMaster + " Added");
                                                    }
                                                }
                                            } else if (sheetName.equals(XlsxCon.Sheet_EM)) {
                                                if (count == 1) {
                                                    String _auto = row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _id = row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _branch = row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _empname = row.getCell(3, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _address = row.getCell(4, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _dob = row.getCell(5, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _mobile = row.getCell(6, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _username = row.getCell(7, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _pass = row.getCell(8, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _desc1 = row.getCell(9, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

                                                    if (_auto.equals(XlsxCon.EM_Auto)) {
                                                        if (_id.equals(XlsxCon.EM_Id)) {
                                                            if (_branch.equals(XlsxCon.EM_Branchcode)) {
                                                                if (_empname.equals(XlsxCon.EM_Empname)) {
                                                                    if (_address.equals(XlsxCon.EM_Address)) {
                                                                        if (_dob.equals(XlsxCon.EM_Empdob)) {
                                                                            if (_mobile.equals(XlsxCon.EM_Mobile)) {
                                                                                if (_username.equals(XlsxCon.EM_Usernm)) {
                                                                                    if (_pass.equals(XlsxCon.EM_Pass)) {
                                                                                        if (_desc1.equals(XlsxCon.EM_Desc1)) {
                                                                                            sequence = true;
                                                                                            dbAdapter.delete(DBHandler.Table_EmployeeMaster);
                                                                                            Constant.showLog(DBHandler.Table_EmployeeMaster + " Deleted");
                                                                                        } else {
                                                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_EM + " Column " + XlsxCon.EM_Desc1);
                                                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_EM + " Column " + XlsxCon.EM_Desc1);
                                                                                            status = null;
                                                                                            break;
                                                                                        }
                                                                                    } else {
                                                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_EM + " Column " + XlsxCon.EM_Pass);
                                                                                        writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_EM + " Column " + XlsxCon.EM_Pass);
                                                                                        status = null;
                                                                                        break;
                                                                                    }
                                                                                } else {
                                                                                    showColMisMatchToast("Sheet " + XlsxCon.Sheet_EM + " Column " + XlsxCon.EM_Usernm);
                                                                                    writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_EM + " Column " + XlsxCon.EM_Usernm);
                                                                                    status = null;
                                                                                    break;
                                                                                }
                                                                            } else {
                                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_EM + " Column " + XlsxCon.EM_Mobile);
                                                                                writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_EM + " Column " + XlsxCon.EM_Mobile);
                                                                                status = null;
                                                                                break;
                                                                            }
                                                                        } else {
                                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_EM + " Column " + XlsxCon.EM_Empdob);
                                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_EM + " Column " + XlsxCon.EM_Empdob);
                                                                            status = null;
                                                                            break;
                                                                        }
                                                                    } else {
                                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_EM + " Column " + XlsxCon.EM_Address);
                                                                        writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_EM + " Column " + XlsxCon.EM_Address);
                                                                        status = null;
                                                                        break;
                                                                    }
                                                                } else {
                                                                    showColMisMatchToast("Sheet " + XlsxCon.Sheet_EM + " Column " + XlsxCon.EM_Empname);
                                                                    writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_EM + " Column " + XlsxCon.EM_Empname);
                                                                    status = null;
                                                                    break;
                                                                }
                                                            } else {
                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_EM + " Column " + XlsxCon.EM_Branchcode);
                                                                writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_EM + " Column " + XlsxCon.EM_Branchcode);
                                                                status = null;
                                                                break;
                                                            }
                                                        } else {
                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_EM + " Column " + XlsxCon.EM_Id);
                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_EM + " Column " + XlsxCon.EM_Id);
                                                            status = null;
                                                            break;
                                                        }
                                                    } else {
                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_EM + " Column " + XlsxCon.EM_Auto);
                                                        writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_EM + " Column " + XlsxCon.EM_Auto);
                                                        status = null;
                                                        break;
                                                    }
                                                } else if (sequence) {
                                                    row.getCell(0, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(1, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(2, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(3, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(4, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(5, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(6, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(7, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(8, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(9, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

                                                    cv.put(DBHandler.EM_Auto, row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.EM_Id, row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.EM_Branchcode, row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.EM_Empname, row.getCell(3, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.EM_Address, row.getCell(4, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.EM_Empdob, row.getCell(5, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.EM_Mobile, row.getCell(6, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.EM_Usernm, row.getCell(7, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.EM_Pass, row.getCell(8, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.EM_Desc1, row.getCell(9, Row.CREATE_NULL_AS_BLANK).getStringCellValue());

                                                    if (!row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue().equalsIgnoreCase("")) {
                                                        dbAdapter.insert(DBHandler.Table_EmployeeMaster, cv);
                                                        Constant.showLog(DBHandler.Table_EmployeeMaster + " Added");
                                                    }
                                                }
                                            } else if (sheetName.equals(XlsxCon.Sheet_PM)) {
                                                if (count == 1) {
                                                    String _id = row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _cat1 = row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _cat2 = row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _cat3 = row.getCell(3, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _finalProd = row.getCell(4, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _unit = row.getCell(5, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _pprice = row.getCell(6, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _mrp = row.getCell(7, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _wprice = row.getCell(8, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _active = row.getCell(9, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _barcode = row.getCell(10, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _ssp = row.getCell(11, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _gstgroup = row.getCell(12, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _hsncode = row.getCell(13, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _gsttype = row.getCell(14, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _stockqty = row.getCell(15, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

                                                    if (_id.equals(XlsxCon.PM_Id)) {
                                                        if (_cat1.equals(XlsxCon.PM_Cat1)) {
                                                            if (_cat2.equals(XlsxCon.PM_Cat2)) {
                                                                if (_cat3.equals(XlsxCon.PM_Cat3)) {
                                                                    if (_finalProd.equals(XlsxCon.PM_Finalproduct)) {
                                                                        if (_unit.equals(XlsxCon.PM_Unit)) {
                                                                            if (_pprice.equals(XlsxCon.PM_Pprice)) {
                                                                                if (_mrp.equals(XlsxCon.PM_Mrp)) {
                                                                                    if (_wprice.equals(XlsxCon.PM_Wprice)) {
                                                                                        if (_active.equals(XlsxCon.PM_Active)) {
                                                                                            if (_barcode.equals(XlsxCon.PM_Barcode)) {
                                                                                                if (_ssp.equals(XlsxCon.PM_Ssp)) {
                                                                                                    if (_gstgroup.equals(XlsxCon.PM_Gstgroup)) {
                                                                                                        if (_hsncode.equals(XlsxCon.PM_Hsncode)) {
                                                                                                            if (_gsttype.equals(XlsxCon.PM_GSTType)) {
                                                                                                                if (_stockqty.equals(XlsxCon.PM_StockQty)) {
                                                                                                                    sequence = true;
                                                                                                                    dbAdapter.delete(DBHandler.Table_ProductMaster);
                                                                                                                    Constant.showLog(DBHandler.Table_ProductMaster + " Deleted");
                                                                                                                } else {
                                                                                                                    showColMisMatchToast("Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_StockQty);
                                                                                                                    writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_StockQty);
                                                                                                                    status = null;
                                                                                                                    break;
                                                                                                                }
                                                                                                            } else {
                                                                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_GSTType);
                                                                                                                writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_GSTType);
                                                                                                                status = null;
                                                                                                                break;
                                                                                                            }
                                                                                                        } else {
                                                                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Hsncode);
                                                                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Hsncode);
                                                                                                            status = null;
                                                                                                            break;
                                                                                                        }
                                                                                                    } else {
                                                                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Gstgroup);
                                                                                                        writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Gstgroup);
                                                                                                        status = null;
                                                                                                        break;
                                                                                                    }
                                                                                                } else {
                                                                                                    showColMisMatchToast("Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Ssp);
                                                                                                    writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Ssp);
                                                                                                    status = null;
                                                                                                    break;
                                                                                                }
                                                                                            } else {
                                                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Barcode);
                                                                                                writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Barcode);
                                                                                                status = null;
                                                                                                break;
                                                                                            }

                                                                                        } else {
                                                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Active);
                                                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Active);
                                                                                            status = null;
                                                                                            break;
                                                                                        }
                                                                                    } else {
                                                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Wprice);
                                                                                        writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Wprice);
                                                                                        status = null;
                                                                                        break;
                                                                                    }
                                                                                } else {
                                                                                    showColMisMatchToast("Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Mrp);
                                                                                    writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Mrp);
                                                                                    status = null;
                                                                                    break;
                                                                                }
                                                                            } else {
                                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Pprice);
                                                                                writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Pprice);
                                                                                status = null;
                                                                                break;
                                                                            }
                                                                        } else {
                                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Unit);
                                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Unit);
                                                                            status = null;
                                                                            break;
                                                                        }
                                                                    } else {
                                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Finalproduct);
                                                                        writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Finalproduct);
                                                                        status = null;
                                                                        break;
                                                                    }
                                                                } else {
                                                                    showColMisMatchToast("Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Cat3);
                                                                    writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Cat3);
                                                                    status = null;
                                                                    break;
                                                                }
                                                            } else {
                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Cat2);
                                                                writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Cat2);
                                                                status = null;
                                                                break;
                                                            }
                                                        } else {
                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Cat1);
                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Cat1);
                                                            status = null;
                                                            break;
                                                        }
                                                    } else {
                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Id);
                                                        writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_PM + " Column " + XlsxCon.PM_Id);
                                                        status = null;
                                                        break;
                                                    }
                                                } else if (sequence) {
                                                    row.getCell(0, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(1, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(2, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(3, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(4, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(5, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(6, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(7, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(8, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(9, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(10, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(11, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(12, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(13, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(14, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(15, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

                                                    cv.put(DBHandler.PM_Id, row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.PM_Cat1, row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.PM_Cat2, row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.PM_Cat3, row.getCell(3, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.PM_Finalproduct, row.getCell(4, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.PM_Unit, row.getCell(5, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.PM_Pprice, row.getCell(6, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.PM_Mrp, row.getCell(7, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.PM_Wprice, row.getCell(8, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.PM_Active, row.getCell(9, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.PM_Barcode, row.getCell(10, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.PM_Ssp, row.getCell(11, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.PM_Gstgroup, row.getCell(12, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.PM_Hsncode, row.getCell(13, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.PM_GSTType, row.getCell(14, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.PM_StockQty, row.getCell(15, Row.CREATE_NULL_AS_BLANK).getStringCellValue());

                                                    if (!row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue().equalsIgnoreCase("")) {
                                                        dbAdapter.insert(DBHandler.Table_ProductMaster, cv);
                                                        Constant.showLog(DBHandler.Table_ProductMaster + " Added");
                                                    }
                                                }
                                            } else if (sheetName.equals(XlsxCon.Sheet_GM)) {
                                                if (count == 1) {
                                                    String _auto = row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _groupName = row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _active = row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    if (_auto.equals(XlsxCon.GM_Auto)) {
                                                        if (_groupName.equals(XlsxCon.GM_GroupName)) {
                                                            if (_active.equals(XlsxCon.GM_Status)) {
                                                                sequence = true;
                                                                dbAdapter.delete(DBHandler.GSTMaster_Table);
                                                            } else {
                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_GM + " Column " + XlsxCon.GM_Status);
                                                                writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_GM + " Column " + XlsxCon.GM_Status);
                                                                status = null;
                                                                break;
                                                            }
                                                        } else {
                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_GM + " Column " + XlsxCon.GM_GroupName);
                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_GM + " Column " + XlsxCon.GM_GroupName);
                                                            status = null;
                                                            break;
                                                        }
                                                    } else {
                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_GM + " Column " + XlsxCon.GM_Auto);
                                                        writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_GM + " Column " + XlsxCon.GM_Auto);
                                                        status = null;
                                                        break;
                                                    }
                                                } else if (sequence) {
                                                    row.getCell(0, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(1, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(2, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

                                                    cv.put(DBHandler.GSTMaster_Auto, row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.GSTMaster_GroupName, row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.GSTMaster_Status, row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.GSTMaster_CrDate, new Constant(getApplicationContext()).getDate());
                                                    cv.put(DBHandler.GSTMaster_CrTime, new Constant(getApplicationContext()).getTime());
                                                    Constant.showLog(row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    if (!row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue().equalsIgnoreCase("")) {
                                                        dbAdapter.insert(DBHandler.GSTMaster_Table, cv);
                                                    }
                                                }
                                            } else if (sheetName.equals(XlsxCon.Sheet_GD)) {
                                                if (count == 1) {
                                                    String _auto = row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _mastAuto = row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _fromRange = row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _toRange = row.getCell(3, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _GSTPer = row.getCell(4, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _CGSTPer = row.getCell(5, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _SGSTPer = row.getCell(6, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _CGSTShare = row.getCell(7, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _SGSTShare = row.getCell(8, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _CessPer = row.getCell(9, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

                                                    if (_auto.equals(XlsxCon.GD_Auto)) {
                                                        if (_mastAuto.equals(XlsxCon.GD_MastAuto)) {
                                                            if (_fromRange.equals(XlsxCon.GD_FromRange)) {
                                                                if (_toRange.equals(XlsxCon.GD_ToRange)) {
                                                                    if (_GSTPer.equals(XlsxCon.GD_GSTPer)) {
                                                                        if (_CGSTPer.equals(XlsxCon.GD_CGSTPer)) {
                                                                            if (_SGSTPer.equals(XlsxCon.GD_SGSTPer)) {
                                                                                if (_CGSTShare.equals(XlsxCon.GD_CGSTShare)) {
                                                                                    if (_SGSTShare.equals(XlsxCon.GD_SGSTShare)) {
                                                                                        if (_CessPer.equals(XlsxCon.GD_CESSPer)) {
                                                                                            sequence = true;
                                                                                            dbAdapter.delete(DBHandler.GSTDetail_Table);
                                                                                        } else {
                                                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_GD + " Column " + XlsxCon.GD_CESSPer);
                                                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_GD + " Column " + XlsxCon.GD_CESSPer);
                                                                                            status = null;
                                                                                            break;
                                                                                        }
                                                                                    } else {
                                                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_GD + " Column " + XlsxCon.GD_SGSTShare);
                                                                                        writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_GD + " Column " + XlsxCon.GD_SGSTShare);
                                                                                        status = null;
                                                                                        break;
                                                                                    }
                                                                                } else {
                                                                                    showColMisMatchToast("Sheet " + XlsxCon.Sheet_GD + " Column " + XlsxCon.GD_CGSTShare);
                                                                                    writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_GD + " Column " + XlsxCon.GD_CGSTShare);
                                                                                    status = null;
                                                                                    break;
                                                                                }
                                                                            } else {
                                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_GD + " Column " + XlsxCon.GD_SGSTPer);
                                                                                writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_GD + " Column " + XlsxCon.GD_SGSTPer);
                                                                                status = null;
                                                                                break;
                                                                            }
                                                                        } else {
                                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_GD + " Column " + XlsxCon.GD_CGSTPer);
                                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_GD + " Column " + XlsxCon.GD_CGSTPer);
                                                                            status = null;
                                                                            break;
                                                                        }
                                                                    } else {
                                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_GD + " Column " + XlsxCon.GD_GSTPer);
                                                                        writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_GD + " Column " + XlsxCon.GD_GSTPer);
                                                                        status = null;
                                                                        break;
                                                                    }
                                                                } else {
                                                                    showColMisMatchToast("Sheet " + XlsxCon.Sheet_GD + " Column " + XlsxCon.GD_ToRange);
                                                                    writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_GD + " Column " + XlsxCon.GD_ToRange);
                                                                    status = null;
                                                                    break;
                                                                }
                                                            } else {
                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_GD + " Column " + XlsxCon.GD_FromRange);
                                                                writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_GD + " Column " + XlsxCon.GD_FromRange);
                                                                status = null;
                                                                break;
                                                            }
                                                        } else {
                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_GD + " Column " + XlsxCon.GD_MastAuto);
                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_GD + " Column " + XlsxCon.GD_MastAuto);
                                                            status = null;
                                                            break;
                                                        }
                                                    } else {
                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_GD + " Column " + XlsxCon.GD_Auto);
                                                        writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_GD + " Column " + XlsxCon.GD_Auto);
                                                        status = null;
                                                        break;
                                                    }
                                                } else if (sequence) {
                                                    row.getCell(0, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(1, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(2, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(3, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(4, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(5, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(6, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(7, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(8, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(9, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

                                                    cv.put(DBHandler.GSTDetail_Auto, row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.GSTDetail_MastAuto, row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.GSTDetail_FromRange, row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.GSTDetail_ToRange, row.getCell(3, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.GSTDetail_GSTPer, row.getCell(4, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.GSTDetail_CGSTPer, row.getCell(5, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.GSTDetail_SGSTPer, row.getCell(6, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.GSTDetail_CGSTShare, row.getCell(7, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.GSTDetail_SGSTShare, row.getCell(8, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.GSTDetail_CessPer, row.getCell(9, Row.CREATE_NULL_AS_BLANK).getStringCellValue());

                                                    if (!row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue().equalsIgnoreCase("")) {
                                                        dbAdapter.insert(DBHandler.GSTDetail_Table, cv);
                                                    }
                                                }
                                            } else if (sheetName.equals(XlsxCon.Sheet_PY)) {
                                                if (count == 1) {
                                                    String _auto = row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _type = row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _status = row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    if (_auto.equals(XlsxCon.PY_Auto)) {
                                                        if (_type.equals(XlsxCon.PY_TYPE)) {
                                                            if (_status.equals(XlsxCon.PY_Status)) {
                                                                sequence = true;
                                                                dbAdapter.delete(DBHandler.Table_Payment);
                                                            } else {
                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_PY + " Column " + XlsxCon.PY_Status);
                                                                writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_PY + " Column " + XlsxCon.PY_Status);
                                                                status = null;
                                                                break;
                                                            }
                                                        } else {
                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_PY + " Column " + XlsxCon.PY_TYPE);
                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_PY + " Column " + XlsxCon.PY_TYPE);
                                                            status = null;
                                                            break;
                                                        }
                                                    } else {
                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_PY + " Column " + XlsxCon.PY_Auto);
                                                        writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_PY + " Column " + XlsxCon.PY_Auto);
                                                        status = null;
                                                        break;
                                                    }
                                                } else if (sequence) {
                                                    row.getCell(0, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(1, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(2, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

                                                    cv.put(DBHandler.PY_Auto, row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.PY_TYPE, row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.PY_Status, row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    dbAdapter.insert(DBHandler.Table_Payment, cv);
                                                }
                                            } else if (sheetName.equals(XlsxCon.Sheet_SM)) {
                                                if (count == 1) {
                                                    String _auto = row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _id = row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _name = row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _address = row.getCell(3, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _phone1 = row.getCell(4, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _phone2 = row.getCell(5, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _mobNo = row.getCell(6, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _email = row.getCell(7, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _cp = row.getCell(8, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _reamark = row.getCell(9, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _city = row.getCell(10, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _gstno = row.getCell(11, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

                                                    if (_auto.equals(XlsxCon.SM_Auto)) {
                                                        if (_id.equals(XlsxCon.SM_Id)) {
                                                            if (_name.equals(XlsxCon.SM_Name)) {
                                                                if (_address.equals(XlsxCon.SM_Address)) {
                                                                    if (_phone1.equals(XlsxCon.SM_Phone1)) {
                                                                        if (_phone2.equals(XlsxCon.SM_Phone2)) {
                                                                            if (_mobNo.equals(XlsxCon.SM_Mobile)) {
                                                                                if (_email.equals(XlsxCon.SM_Email)) {
                                                                                    if (_cp.equals(XlsxCon.SM_CP)) {
                                                                                        if (_reamark.equals(XlsxCon.SM_Remark)) {
                                                                                            if (_city.equals(XlsxCon.SM_City)) {
                                                                                                if (_gstno.equals(XlsxCon.SM_GSTNO)) {
                                                                                                    sequence = true;
                                                                                                    dbAdapter.delete(DBHandler.Table_SupplierMaster);
                                                                                                } else {
                                                                                                    showColMisMatchToast("Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_GSTNO);
                                                                                                    writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_GSTNO);
                                                                                                    status = null;
                                                                                                    break;
                                                                                                }
                                                                                            } else {
                                                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_City);
                                                                                                writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_City);
                                                                                                status = null;
                                                                                                break;
                                                                                            }

                                                                                        } else {
                                                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_Remark);
                                                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_Remark);
                                                                                            status = null;
                                                                                            break;
                                                                                        }
                                                                                    } else {
                                                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_CP);
                                                                                        writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_CP);
                                                                                        status = null;
                                                                                        break;
                                                                                    }
                                                                                } else {
                                                                                    showColMisMatchToast("Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_Email);
                                                                                    writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_Email);
                                                                                    status = null;
                                                                                    break;
                                                                                }
                                                                            } else {
                                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_Mobile);
                                                                                writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_Mobile);
                                                                                status = null;
                                                                                break;
                                                                            }
                                                                        } else {
                                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_Phone2);
                                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_Phone2);
                                                                            status = null;
                                                                            break;
                                                                        }
                                                                    } else {
                                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_Phone1);
                                                                        writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_Phone1);
                                                                        status = null;
                                                                        break;
                                                                    }
                                                                } else {
                                                                    showColMisMatchToast("Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_Address);
                                                                    writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_Address);
                                                                    status = null;
                                                                    break;
                                                                }
                                                            } else {
                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_Name);
                                                                writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_Name);
                                                                status = null;
                                                                break;
                                                            }
                                                        } else {
                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_Id);
                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_Id);
                                                            status = null;
                                                            break;
                                                        }
                                                    } else {
                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_Auto);
                                                        writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_SM + " Column " + XlsxCon.SM_Auto);
                                                        status = null;
                                                        break;
                                                    }
                                                } else if (sequence) {
                                                    row.getCell(0, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(1, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(2, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(3, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(4, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(5, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(6, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(7, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(8, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(9, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(10, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(11, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

                                                    cv.put(DBHandler.SM_Auto, row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.SM_Id, row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.SM_Name, row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.SM_Address, row.getCell(3, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.SM_Phone1, row.getCell(4, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.SM_Phone2, row.getCell(5, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.SM_Mobile, row.getCell(6, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.SM_Email, row.getCell(7, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.SM_Contactperson, row.getCell(8, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.SM_Remark, row.getCell(9, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.SM_City, row.getCell(10, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.SM_Gstno, row.getCell(11, Row.CREATE_NULL_AS_BLANK).getStringCellValue());

                                                    if (!row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue().equalsIgnoreCase("")) {
                                                        dbAdapter.insert(DBHandler.Table_SupplierMaster, cv);
                                                    }
                                                }
                                            } else if (sheetName.equals(XlsxCon.Sheet_CSM)) {
                                                if (count == 1) {
                                                    String _auto = row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _id = row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _name = row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _cityid = row.getCell(3, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _adress = row.getCell(4, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _area = row.getCell(5, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _phone = row.getCell(6, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _mobno = row.getCell(7, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _dob = row.getCell(8, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _adob = row.getCell(9, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _panno = row.getCell(10, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _gstno = row.getCell(11, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

                                                    if (_auto.equals(XlsxCon.CSM_Auto)) {
                                                        if (_id.equals(XlsxCon.CSM_Id)) {
                                                            if (_name.equals(XlsxCon.CSM_Name)) {
                                                                if (_cityid.equals(XlsxCon.CSM_Cityid)) {
                                                                    if (_adress.equals(XlsxCon.CSM_Address)) {
                                                                        if (_area.equals(XlsxCon.CSM_Area)) {
                                                                            if (_phone.equals(XlsxCon.CSM_Phone)) {
                                                                                if (_mobno.equals(XlsxCon.CSM_Mobno)) {
                                                                                    if (_dob.equals(XlsxCon.CSM_Dob)) {
                                                                                        if (_adob.equals(XlsxCon.CSM_Anniversarydate)) {
                                                                                            if (_panno.equals(XlsxCon.CSM_Panno)) {
                                                                                                if (_gstno.equals(XlsxCon.CSM_Gstno)) {
                                                                                                    sequence = true;
                                                                                                    dbAdapter.delete(DBHandler.Table_CustomerMaster);
                                                                                                } else {
                                                                                                    showColMisMatchToast("Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Gstno);
                                                                                                    writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Gstno);
                                                                                                    status = null;
                                                                                                    break;
                                                                                                }
                                                                                            } else {
                                                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Panno);
                                                                                                writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Panno);
                                                                                                status = null;
                                                                                                break;
                                                                                            }
                                                                                        } else {
                                                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Anniversarydate);
                                                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Anniversarydate);
                                                                                            status = null;
                                                                                            break;
                                                                                        }
                                                                                    } else {
                                                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Dob);
                                                                                        writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Dob);
                                                                                        status = null;
                                                                                        break;
                                                                                    }
                                                                                } else {
                                                                                    showColMisMatchToast("Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Mobno);
                                                                                    writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Mobno);
                                                                                    status = null;
                                                                                    break;
                                                                                }
                                                                            } else {
                                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Phone);
                                                                                writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Phone);
                                                                                status = null;
                                                                                break;
                                                                            }
                                                                        } else {
                                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Area);
                                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Area);
                                                                            status = null;
                                                                            break;
                                                                        }
                                                                    } else {
                                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Address);
                                                                        writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Address);
                                                                        status = null;
                                                                        break;
                                                                    }
                                                                } else {
                                                                    showColMisMatchToast("Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Cityid);
                                                                    writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Cityid);
                                                                    status = null;
                                                                    break;
                                                                }
                                                            } else {
                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Name);
                                                                writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Name);
                                                                status = null;
                                                                break;
                                                            }
                                                        } else {
                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Id);
                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Id);
                                                            status = null;
                                                            break;
                                                        }
                                                    } else {
                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Auto);
                                                        writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_CSM + " Column " + XlsxCon.CSM_Auto);
                                                        status = null;
                                                        break;
                                                    }
                                                } else if (sequence) {
                                                    row.getCell(0, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(1, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(2, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(3, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(4, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(5, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(6, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(7, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(8, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(9, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(10, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(11, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

                                                    cv.put(DBHandler.CSM_Auto, row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CSM_Id, row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CSM_Name, row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CSM_Cityid, row.getCell(3, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CSM_Address, row.getCell(4, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CSM_Area, row.getCell(5, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CSM_Phone, row.getCell(6, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CSM_Mobno, row.getCell(7, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CSM_Dob, row.getCell(8, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CSM_Anniversarydate, row.getCell(9, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CSM_Panno, row.getCell(10, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.CSM_Gstno, row.getCell(11, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    if (!row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue().equalsIgnoreCase("")) {
                                                        dbAdapter.insert(DBHandler.Table_CustomerMaster, cv);
                                                    }
                                                }
                                            } else if (sheetName.equals(XlsxCon.Sheet_EX)) {
                                                if (count == 1) {
                                                    String _auto = row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _id = row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _name = row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _expDesc = row.getCell(3, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _active = row.getCell(4, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _costcenter = row.getCell(5, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _remark = row.getCell(6, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

                                                    if (_auto.equals(XlsxCon.EX_Auto)) {
                                                        if (_id.equals(XlsxCon.EX_Id)) {
                                                            if (_name.equals(XlsxCon.EX_Name)) {
                                                                if (_expDesc.equals(XlsxCon.EX_Expdesc)) {
                                                                    if (_active.equals(XlsxCon.EX_Active)) {
                                                                        if (_costcenter.equals(XlsxCon.EX_Costcentre)) {
                                                                            if (_remark.equals(XlsxCon.EX_Remark)) {
                                                                                sequence = true;
                                                                                dbAdapter.delete(DBHandler.Table_ExpenseHead);
                                                                            } else {
                                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_EX + " Column " + XlsxCon.EX_Remark);
                                                                                writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_EX + " Column " + XlsxCon.EX_Remark);
                                                                                status = null;
                                                                                break;
                                                                            }
                                                                        } else {
                                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_EX + " Column " + XlsxCon.EX_Costcentre);
                                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_EX + " Column " + XlsxCon.EX_Costcentre);
                                                                            status = null;
                                                                            break;
                                                                        }
                                                                    } else {
                                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_EX + " Column " + XlsxCon.EX_Active);
                                                                        writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_EX + " Column " + XlsxCon.EX_Active);
                                                                        status = null;
                                                                        break;
                                                                    }
                                                                } else {
                                                                    showColMisMatchToast("Sheet " + XlsxCon.Sheet_EX + " Column " + XlsxCon.EX_Expdesc);
                                                                    writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_EX + " Column " + XlsxCon.EX_Expdesc);
                                                                    status = null;
                                                                    break;
                                                                }
                                                            } else {
                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_EX + " Column " + XlsxCon.EX_Name);
                                                                writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_EX + " Column " + XlsxCon.EX_Name);
                                                                status = null;
                                                                break;
                                                            }
                                                        } else {
                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_EX + " Column " + XlsxCon.EX_Id);
                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_EX + " Column " + XlsxCon.EX_Id);
                                                            status = null;
                                                            break;
                                                        }
                                                    } else {
                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_EX + " Column " + XlsxCon.EX_Auto);
                                                        writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_EX + " Column " + XlsxCon.EX_Auto);
                                                        status = null;
                                                        break;
                                                    }
                                                } else if (sequence) {
                                                    row.getCell(0, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(1, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(2, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(3, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(4, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(5, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(6, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

                                                    cv.put(DBHandler.EXM_Auto, row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.EXM_Id, row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.EXM_Name, row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.EXM_Expdesc, row.getCell(3, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.EXM_Active, row.getCell(4, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.EXM_Costcentre, row.getCell(5, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.EXM_Remark, row.getCell(6, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    if (!row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue().equalsIgnoreCase("")) {
                                                        dbAdapter.insert(DBHandler.Table_ExpenseHead, cv);
                                                    }
                                                }
                                            } else if (sheetName.equals(XlsxCon.Sheet_RM)) {
                                                if (count == 1) {
                                                    String _auto = row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _rate = row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    String _active = row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                                    if (_auto.equals(XlsxCon.RTM_Auto)) {
                                                        if (_rate.equals(XlsxCon.RTM_Rates)) {
                                                            if (_active.equals(XlsxCon.RTM_Active)) {
                                                                sequence = true;
                                                                dbAdapter.delete(DBHandler.Table_RateMaster);
                                                            } else {
                                                                showColMisMatchToast("Sheet " + XlsxCon.Sheet_RM + " Column " + XlsxCon.RTM_Active);
                                                                writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_RM + " Column " + XlsxCon.RTM_Active);
                                                                status = null;
                                                                break;
                                                            }
                                                        } else {
                                                            showColMisMatchToast("Sheet " + XlsxCon.Sheet_RM + " Column " + XlsxCon.RTM_Rates);
                                                            writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_RM + " Column " + XlsxCon.RTM_Rates);
                                                            status = null;
                                                            break;
                                                        }
                                                    } else {
                                                        showColMisMatchToast("Sheet " + XlsxCon.Sheet_RM + " Column " + XlsxCon.RTM_Auto);
                                                        writeLog("readFile_Column_Not_Matched_Sheet " + XlsxCon.Sheet_RM + " Column " + XlsxCon.RTM_Auto);
                                                        status = null;
                                                        break;
                                                    }
                                                } else if (sequence) {
                                                    row.getCell(0, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(1, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                                    row.getCell(2, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

                                                    cv.put(DBHandler.RTM_Auto, row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.RTM_Rates, row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    cv.put(DBHandler.RTM_Active, row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                                    if (!row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue().equalsIgnoreCase("")) {
                                                        dbAdapter.insert(DBHandler.Table_RateMaster, cv);
                                                    }
                                                }
                                            }
                                            pd1.setProgress(count);
                                        }
                                    } else {
                                        writeLog("readFile_SheetISBlank");
                                        toast.setText("Sheet Is Blank");
                                        toast.show();
                                        status = null;
                                    }
                                }
                            }else{
                                toast.setText("No Sheet Found");
                                toast.show();
                                writeLog("readFile_NumberOfSheet_0");
                                status = null;
                            }
                        }else{
                            writeLog("readFile_FileFormatChanged");
                            status = null;
                        }
                    }else{
                        status = null;
                        writeLog("readFile_f[]!=2");
                    }
                    pd1.dismiss();
                } catch (Exception e) {
                    writeLog("readFile_"+e.getMessage());
                    e.printStackTrace();
                    pd1.dismiss();
                    status = null;
                }
            } else {
                writeLog("readFile_FilePath_NULL");
                status = null;
                pd1.dismiss();
            }
            return status;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd1.dismiss();
            if (result != null) {
                Log.d("Log", filepath);
                showDia(4);
            } else {
                showDia(2);
            }
        }
    }

    private void showColMisMatchToast(String msg){
        Constant.showLog(msg);
        toast.setText("Excel Sheet Column Mismatch "+msg);
        toast.show();
    }

    private void setUserData() {
        String data = "";
        UserProfileClass user = new Constant(getApplicationContext()).getPref();
        data = 4+"*"+user.getMobileNo() + "^" + user.getEmailId();
        Constant.showLog(data);
        new saveUserDetails().execute(data);
    }

    private class saveUserDetails extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ImportMasterActivity.this);
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
            toast.setText("Please Check Your Mail");
            toast.show();
        }
    }

    private void writeLog(String _data) {
        new WriteLog().writeLog(getApplicationContext(), "ImportMasterActivity_" + _data);
    }
}
