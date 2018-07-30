package com.pait.smartpos.drive;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.pait.smartpos.DataBackupActivity;
import com.pait.smartpos.constant.Constant;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.pait.smartpos.DriveBaseActivity;
import com.pait.smartpos.R;
import com.pait.smartpos.db.DBHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class CreateFileActivity extends DriveBaseActivity {

    private static final String TAG = "CreateFileActivity";
    private String dbpath;
    private Constant constant;

    @Override
    protected void onDriveClientReady() {

        constant = new Constant(CreateFileActivity.this);
        constant.showPD();
        dbpath = "//data//" + "//data//" + getApplicationContext().getPackageName() + "//databases//" + DBHandler.Database_Name;

        Query query = new Query.Builder()
                        .addFilter(Filters.and(Filters.eq(SearchableField.MIME_TYPE, "application/x-sqlite3"),
                                Filters.eq(SearchableField.STARRED, true)))
                        .build();

        Task<MetadataBuffer> queryTask = getDriveResourceClient().query(query);

        queryTask.addOnSuccessListener(this, new OnSuccessListener<MetadataBuffer>() {
                    @Override
                    public void onSuccess(MetadataBuffer metadataBuffer) {
                        for (Metadata m : metadataBuffer) {
                            DriveResource driveResource = m.getDriveId().asDriveResource();
                            Log.i(TAG, "Deleting file: " + DBHandler.Database_Name + "  DriveId:(" + m.getDriveId() + ")");
                            getDriveResourceClient().delete(driveResource);
                        }
                        createFile(DBHandler.Database_Name,
                                "application/x-sqlite3",
                                new File(dbpath)
                        );
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        constant.showPD();
                        Log.i(TAG, "ERROR: File not found: " + DBHandler.Database_Name);
                        createFile(DBHandler.Database_Name, "application/x-sqlite3", new File(dbpath)
                        );
                    }
                });
    }

    private void createFile(final String titl, final String mime, final File file) {
        // [START create_file]
        final Task<DriveFolder> rootFolderTask = getDriveResourceClient().getRootFolder();
        final Task<DriveContents> createContentsTask = getDriveResourceClient().createContents();
        Tasks.whenAll(rootFolderTask, createContentsTask)
                .continueWithTask(task -> {
                    DriveFolder parent = rootFolderTask.getResult();
                    DriveContents contents = createContentsTask.getResult();
                    OutputStream oos = contents.getOutputStream();
                    if (oos != null) try {
                        InputStream is = new FileInputStream(file);
                        byte[] buf = new byte[4096];
                        int c;
                        while ((c = is.read(buf, 0, buf.length)) > 0) {
                            oos.write(buf, 0, c);
                            oos.flush();
                        }
                    } finally {
                        oos.close();
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(titl)
                            .setMimeType(mime)
                            .setStarred(true)
                            .build();

                    return getDriveResourceClient().createFile(parent, changeSet, contents);
                })
                .addOnSuccessListener(this,
                        driveFile -> {
                            showMessage(getString(R.string.file_created, driveFile.getDriveId().encodeToString()));
                            constant.showPD();
                            showDia(1);
                        })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to create file", e);
                    showMessage(getString(R.string.file_create_error));
                    constant.showPD();
                    showDia(2);
                });
        // [END create_file]
    }

    private void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateFileActivity.this);
        builder.setCancelable(false);
        if (a == 1) {
            builder.setMessage("Data Backup Successful");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DataBackupActivity.backupRestoreFlag = 1;
                    new Constant(CreateFileActivity.this).doFinish();
                    dialog.dismiss();
                }
            });
        }else if (a == 2) {
            builder.setMessage("Error While Data Backup");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DataBackupActivity.backupRestoreFlag = 0;
                    dialog.dismiss();
                    new Constant(CreateFileActivity.this).doFinish();
                }
            });
        }
        builder.create().show();
    }

}
