package com.pait.smartpos.drive;

import android.util.Log;

import com.google.android.gms.drive.DriveFile;
import com.pait.smartpos.DriveBaseActivity;
import com.pait.smartpos.R;

public class DeleteFileActivity extends DriveBaseActivity {
    private static final String TAG = "DeleteFileActivity";

    @Override
    protected void onDriveClientReady() {
        pickTextFile()
                .addOnSuccessListener(this,
                        driveId -> deleteFile(driveId.asDriveFile()))
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "No file selected", e);
                    showMessage(getString(R.string.file_not_selected));
                    finish();
                });
    }
    private void deleteFile(DriveFile file) {
        // [START delete_file]
        getDriveResourceClient()
                .delete(file)
                .addOnSuccessListener(this,
                        aVoid -> {
                            showMessage(getString(R.string.file_deleted));
                            finish();
                        })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to delete file", e);
                    showMessage(getString(R.string.delete_failed));
                    finish();
                });
        // [END delete_file]
    }
}
