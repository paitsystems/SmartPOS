package com.pait.smartpos.drive;

import com.pait.smartpos.DataBackupActivity;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.pait.smartpos.DriveBaseActivity;

import android.content.DialogInterface;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.pait.smartpos.R;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandlerR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class RetrieveContentsActivity extends DriveBaseActivity {

    private static final String TAG = "RetrieveContents";
    private TextView mFileContents;
    private String dbpath;
    private Constant constant;

    /*@Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);
        mFileContents = findViewById(R.id.fileContents);
        mFileContents.setText("");
    }*/

    @Override
    protected void onDriveClientReady() {
        constant = new Constant(RetrieveContentsActivity.this);
        constant.showPD();
        dbpath = "//data//" + "//data//" + getApplicationContext().getPackageName() + "//databases//" + DBHandlerR.Database_Name;

        Query query = new Query.Builder()
                .addFilter(Filters.and(Filters.eq(SearchableField.MIME_TYPE, "application/x-sqlite3"),
                        Filters.eq(SearchableField.STARRED, true)))
                .build();

        Task<MetadataBuffer> queryTask = getDriveResourceClient().query(query);

        queryTask.addOnSuccessListener(this,
                new OnSuccessListener<MetadataBuffer>() {
                    @Override
                    public void onSuccess(MetadataBuffer metadataBuffer) {
                        for (Metadata m : metadataBuffer) {
                            retrieveContents(m.getDriveId().asDriveFile());
                            Log.i(TAG, "Created file: " + DBHandlerR.Database_Name + "  DriveId:(" + m.getDriveId() + ")");
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "ERROR: File not found: " + DBHandlerR.Database_Name);
                        constant.showPD();
                    }
                });
    }

    private void retrieveContents(DriveFile file1) {
        Task<DriveContents> openFileTask = getDriveResourceClient().openFile(file1, DriveFile.MODE_READ_ONLY);
        openFileTask.continueWithTask(task -> {
            DriveContents driveContents = task.getResult();
                    /*try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(contents.getInputStream()))) {
                        StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line).append("\n");
                        }
                        showMessage(getString(R.string.content_loaded));
                        mFileContents.setText(builder.toString());
                    }*/

            InputStream inputStream = driveContents.getInputStream();

            String dbDir = getDatabasePath(DBHandlerR.Database_Name).getParent();
            String newFileName = "NewDB.db";

            Log.i(TAG, "dbDir = " + dbDir);

            // Deletion previous versions of new DB file from drive
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Constant.folder_name+ "/" + newFileName);
            if (file.exists()) {
                Log.i(TAG, "newDbName.db EXISTS");
                if (file.delete()) {
                    Log.i(TAG, "newDbName.db DELETING old file....");
                } else {
                    Log.i(TAG, "newDbName.db Something went wrong with deleting");
                    Toast.makeText(getApplicationContext(), "Import DB error", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            try {
                OutputStream output = new FileOutputStream(file);
                try {
                    try {
                        byte[] buffer = new byte[4 * 1024]; // or other buffer size
                        int read;

                        while ((read = inputStream.read(buffer)) != -1) {
                            output.write(buffer, 0, read);
                        }
                        output.flush();
                    } finally {
                        output.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Import DB error", Toast.LENGTH_LONG).show();
                    finish();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Import DB error", Toast.LENGTH_LONG).show();
                finish();
            } finally {
                try {
                    inputStream.close();
                    File sd = Environment.getExternalStorageDirectory();
                    File data = Environment.getDataDirectory();
                    FileChannel source=null;
                    FileChannel destination=null;
                    String currentDBPath = "/"+Constant.folder_name+"/"+newFileName;
                    String backupDBPath = "/data/"+ getPackageName() +"/databases/"+ DBHandlerR.Database_Name;
                    File currentDB = new File(sd, currentDBPath);
                    File backupDB = new File(data, backupDBPath);
                    try {
                        source = new FileInputStream(currentDB).getChannel();
                        destination = new FileOutputStream(backupDB).getChannel();
                        destination.transferFrom(source, 0, source.size());
                        source.close();
                        destination.close();
                        currentDB.delete();
                        Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Import DB error", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            showDia(1);
            constant.showPD();
            Task<Void> discardTask = getDriveResourceClient().discardContents(driveContents);
            return discardTask;
        })
                .addOnFailureListener(e -> {
                    showDia(2);
                    Log.e(TAG, "Unable to read contents", e);
                    showMessage(getString(R.string.read_failed));
                    constant.showPD();
                });
    }

    private void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RetrieveContentsActivity.this);
        builder.setCancelable(false);
        if (a == 1) {
            builder.setMessage("Backup Restore Successful");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DataBackupActivity.backupRestoreFlag = 2;
                    new Constant(RetrieveContentsActivity.this).doFinish();
                    dialog.dismiss();
                }
            });
        }else if (a == 2) {
            builder.setMessage("Error While Restoring Backup");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DataBackupActivity.backupRestoreFlag = 0;
                    dialog.dismiss();
                    new Constant(RetrieveContentsActivity.this).doFinish();
                }
            });
        }
        builder.create().show();
    }

}
