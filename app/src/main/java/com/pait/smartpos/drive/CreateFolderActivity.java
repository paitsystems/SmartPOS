package com.pait.smartpos.drive;

import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.MetadataChangeSet;
import com.pait.smartpos.DriveBaseActivity;
import com.pait.smartpos.R;
import com.pait.smartpos.db.DBHandlerR;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class CreateFolderActivity extends DriveBaseActivity {
    private static final String TAG = "CreateFolderActivity";

    @Override
    protected void onDriveClientReady() {
        //createFolder();
        String dbpath = "//data//" + getApplicationContext().getPackageName() + "//databases//"+ DBHandlerR.Database_Name;
        saveToDrive(
                Drive.DriveApi.getAppFolder(getDriveResourceClient().asGoogleApiClient()),
                DBHandlerR.Database_Name,
                "application/x-sqlite3",
                new File(dbpath)
        );
    }

    // [START create_folder]
    private void createFolder() {
        getDriveResourceClient().getAppFolder()
                .continueWithTask(task -> {
                    DriveFolder parentFolder = task.getResult();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("New folder")
                            .setMimeType(DriveFolder.MIME_TYPE)
                            .setStarred(true)
                            .build();
                    return getDriveResourceClient().createFolder(parentFolder, changeSet);
                })
                .addOnSuccessListener(this,
                        driveFolder -> {
                            showMessage(getString(R.string.file_created,
                                    driveFolder.getDriveId().encodeToString()));
                            finish();
                        })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to create file", e);
                    showMessage(getString(R.string.file_create_error));
                    finish();
                });
    }

    private void saveToDrive(final DriveFolder pFldr, final String titl,
                     final String mime, final File file){
        Drive.DriveApi.newDriveContents(getDriveResourceClient().asGoogleApiClient()).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(DriveApi.DriveContentsResult driveContentsResult) {
                DriveContents cont = driveContentsResult != null && driveContentsResult.getStatus().isSuccess() ?
                        driveContentsResult.getDriveContents() : null;

                // write file to content, chunk by chunk
                if (cont != null) try {
                    OutputStream oos = cont.getOutputStream();
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

                    // content's COOL, create metadata
                    MetadataChangeSet meta = new MetadataChangeSet.Builder().setTitle(titl).setMimeType(mime).build();

                    // now create file on GooDrive
                    pFldr.createFile(getDriveResourceClient().asGoogleApiClient(), meta, cont).setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                        @Override
                        public void onResult(DriveFolder.DriveFileResult driveFileResult) {
                            if (driveFileResult != null && driveFileResult.getStatus().isSuccess()) {
                                DriveFile dFil = driveFileResult != null && driveFileResult.getStatus().isSuccess() ?
                                        driveFileResult.getDriveFile() : null;
                                if (dFil != null) {
                                    // BINGO , file uploaded
                                    dFil.getMetadata(getDriveResourceClient().asGoogleApiClient()).setResultCallback(new ResultCallback<DriveResource.MetadataResult>() {
                                        @Override
                                        public void onResult(DriveResource.MetadataResult metadataResult) {
                                            if (metadataResult != null && metadataResult.getStatus().isSuccess()) {
                                                DriveId mDriveId = metadataResult.getMetadata().getDriveId();
                                            }
                                        }
                                    });
                                }
                            } else { /* report error */ }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
