package com.pait.smartpos.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.gson.Gson;
import com.pait.smartpos.VerificationActivity;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandlerR;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Driver_utils {

    public static DriveFile mfile;
    public static GoogleApiClient api;
    public static DriveId driveId;
    public static Context ctxs;
    public static SharedPreferences preferences_driverId;
    public static SharedPreferences.Editor editor;
    private static final String GOOGLE_DRIVE_FILE_NAME = "Databackup";

    public static void restoreDriveBackup(Context ctx, GoogleApiClient apis, String GOOGLE_DRIVE_FILE_NAME, SharedPreferences preferences_driverIds, DriveFile mfiles) {
        mfile = mfiles;
        api = apis;
        preferences_driverId = preferences_driverIds;
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, GOOGLE_DRIVE_FILE_NAME))
                .build();

        Drive.DriveApi.query(api, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(DriveApi.MetadataBufferResult metadataBufferResult) {
                Gson gson = new Gson();
                VerificationActivity.pref = ctx.getSharedPreferences(VerificationActivity.PREF_NAME, Context.MODE_PRIVATE);
                String json = preferences_driverId.getString("drive_id", "");
                DriveId driveId = gson.fromJson(json, DriveId.class);
                Log.e("driveId put", "" + driveId);
                Log.e("filesize in cloud ", +metadataBufferResult.getMetadataBuffer().get(0).getFileSize() + "");
                metadataBufferResult.getMetadataBuffer().release();
                /*mfile = Drive.DriveApi.getFile(api, driveId);
                mfile.open(api, DriveFile.MODE_READ_ONLY, new DriveFile.DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDown, long bytesExpected) {
                        Log.e("Downloading..", "" + bytesDown + "/" + bytesExpected);
                    }
                }).setResultCallback(restoreContentsCallback);*/
            }
        });
    }

    static final private ResultCallback<DriveApi.DriveContentsResult> restoreContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.e("Unable to open,try", "data");
                        return;
                    }
                    File sd = Environment.getExternalStorageDirectory();
                    String backupDBPath = "/Databackup.zip";
                    File imgFile = new File(sd, backupDBPath);
                    Log.e("FILE EXIST", imgFile.exists() + "");

                    if (!imgFile.exists())
                        try {
                            imgFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    imgFile = new File(imgFile.getAbsolutePath());
                    DriveContents contents = result.getDriveContents();
                    try {
                        FileOutputStream fos = new FileOutputStream(imgFile.getAbsolutePath());
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        BufferedInputStream in = new BufferedInputStream(contents.getInputStream());
                        byte[] buffer = new byte[1024];
                        int n, cnt = 0;
                        while ((n = in.read(buffer)) > 0) {
                            bos.write(buffer, 0, n);
                            cnt += n;
                            Log.e("buffer: ", buffer[0] + "");
                            Log.e("buffer: ", "" + buffer[1]);
                            Log.e("buffer: ", "" + buffer[2]);
                            Log.e("buffer: ", "" + buffer[3]);
                            bos.flush();
                        }

                        bos.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //Unzip when download from drive

                    try {
                        String dest_file_path = Environment.getExternalStorageDirectory()
                                .getAbsolutePath() + "/Databackup";
                        String src_location = Environment.getExternalStorageDirectory()
                                .getAbsolutePath() + "/Databackup.zip";
                        Decompress.unzip(new File(src_location), new File(dest_file_path));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

    public static void creatBackupDrive(Context ctx, GoogleApiClient apis) {
        ctxs = ctx;
        api = apis;
        Drive.DriveApi.newDriveContents(api).setResultCallback(contentsCallback);
    }

    final public static ResultCallback<DriveApi.DriveContentsResult> contentsCallback = new ResultCallback<DriveApi.DriveContentsResult>() {

        @Override
        public void onResult(DriveApi.DriveContentsResult result) {
            if (!result.getStatus().isSuccess()) {
                Log.e("Log", "Error while trying to create new file contents");
                return;
            }

            String mimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType("db");
            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                    .setTitle(GOOGLE_DRIVE_FILE_NAME) // Google Drive File name
                    .setMimeType("application/zip")
                    .setStarred(true).build();
            // create a file on root folder
            Drive.DriveApi.getRootFolder(api)
                    .createFile(api, changeSet, result.getDriveContents())
                    .setResultCallback(fileCallback);
        }

    };

    final public static ResultCallback<DriveFolder.DriveFileResult> fileCallback = new ResultCallback<DriveFolder.DriveFileResult>() {

        @Override
        public void onResult(DriveFolder.DriveFileResult result) {
            preferences_driverId = ctxs.getSharedPreferences("ID", Context.MODE_PRIVATE);
            editor = preferences_driverId.edit();
            if (!result.getStatus().isSuccess()) {
                Log.v("Log", "Error while trying to create the file");
                return;
            }
            driveId = result.getDriveFile().getDriveId();
            Log.e("Log", "Created a file with content: " + driveId);
            Gson gson = new Gson();
            String json = gson.toJson(driveId); // myObject - instance of MyObject
            editor.putString("drive_id", json).commit();
            Log.e("Log", "driveId " + driveId);
            mfile = result.getDriveFile();
            mfile.open(api, DriveFile.MODE_WRITE_ONLY, new DriveFile.DownloadProgressListener() {
                @Override
                public void onProgress(long bytesDownloaded, long bytesExpected) {
                    Log.e("Log", "Creating backup file" + bytesDownloaded + "/" + bytesExpected);
                }
            }).setResultCallback(contentsOpenedCallback);
        }
    };
    final public static ResultCallback<DriveApi.DriveContentsResult> contentsOpenedCallback = new ResultCallback<DriveApi.DriveContentsResult>() {

        @Override
        public void onResult(DriveApi.DriveContentsResult result) {
            if (!result.getStatus().isSuccess()) {
                Log.v("Log", "Error opening file");
                return;
            }
            String sd = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DiaryDatabackup.zip";
            Log.e("DB FILE NAME---", sd + "");
            DriveContents contents = result.getDriveContents();
            BufferedOutputStream out = new BufferedOutputStream(contents.getOutputStream());
            byte[] buffer = new byte[1024];
            int n;

            try {
                FileInputStream is = new FileInputStream(sd);
                BufferedInputStream in = new BufferedInputStream(is);

                while ((n = in.read(buffer)) > 0) {
                    out.write(buffer, 0, n);
                    Log.e("Backing up...", "Backup");
                }
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            contents.commit(api, null).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    Log.e("Backup completed!", "complete" + status);

                }
            });
        }
    };

    public static void trash(DriveId dId, GoogleApiClient apis) {
        api = apis;
        try {
            Log.e("Log", "Goes in trans");
            DriveFile sumFile = dId.asDriveFile();
            com.google.android.gms.common.api.Status deleteStatus =
                    sumFile.delete(api).await();
            if (!deleteStatus.isSuccess()) {
                Log.e("Log", "Unable to delete app data.");

            } else {
                // Remove stored DriveId.
                preferences_driverId.edit().remove("drive_id").apply();
            }
            Log.d("Log", "Past sums deleted.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void restore(Context ctx) {
        OutputStream myOutput;
        String dbpath = "//data//" + ctx.getPackageName() + "//databases//databaseName.db";
        String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Databackup";
        File directorys = new File(sdpath + "/backup_sd");
        if (directorys.exists()) {
            try {
                myOutput = new FileOutputStream(Environment.getDataDirectory()
                        + dbpath);
                // Set the folder on the SDcard
                File directory = new File(sdpath + "/backup_sd");
                // Set the input file stream up:
                InputStream myInputs = new FileInputStream(directory.getPath());
                // Transfer bytes from the input file to the output file
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInputs.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                // Close and clear the streams
                myOutput.flush();
                myOutput.close();
                myInputs.close();
                Toast.makeText(ctx, "Suceess", Toast.LENGTH_LONG)
                        .show();

            } catch (FileNotFoundException e) {
                Toast.makeText(ctx, "Error", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(ctx, "Error", Toast.LENGTH_LONG).show();

                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Log.e("NO DB YET ", "Created");
            Toast.makeText(ctx, "Save Some", Toast.LENGTH_LONG).show();

        }

    }

    public static void create_backup(Context ctx) {
        InputStream myInput;
        String dbpath = "//data//" + ctx.getPackageName() + "//databases//"+ DBHandlerR.Database_Name;
        String sdpath_createbackup = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+ Constant.folder_name;
        File file = new File(sdpath_createbackup);
        if (!file.exists())
            file.mkdirs();
        try {
            myInput = new FileInputStream(Environment.getDataDirectory() + dbpath);
            // Set the output folder on the Scard
            File directory = new File(file + "/backup_sd");
            // Create the folder if it doesn't exist:
            if (!directory.exists()) {
                directory.createNewFile();
            }
            // Set the output file stream up:
            OutputStream myOutput = new FileOutputStream(directory.getPath());
            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[100024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            // Close and clear the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
            Toast.makeText(ctx, "Backup", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(ctx, "Error", Toast.LENGTH_LONG).show();
            Log.e("error", e.getMessage());
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(ctx, "Error", Toast.LENGTH_LONG).show();
            Log.e("error 1", e.getMessage());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String src_file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Databackup";
        String destination_location = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Databackup.zip";
        Decompress.backupfolder(new File(src_file_path), new File(destination_location));
    }
}
