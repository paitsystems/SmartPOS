package com.pait.smartpos;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.fragments.TableViewFragment;
import com.pait.smartpos.model.TableClass;
import com.pait.smartpos.permission.GetPermission;

public class AddTableMasterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText ed_table_size;
    private Button btn_save;
    private LinearLayout count_layout;
    private TextView tv_count;
    private GetPermission permission;
    private Constant constant;
    private Toast toast;
    private DBHandlerR db;
    private int flag = -1,flag1 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_table_master);

        init();

        String from = getIntent().getExtras().getString("from");
        assert from != null;
        if(from.equals("FromVerificationActivity")){
            count_layout.setVisibility(View.GONE);
            btn_save.setText("Save");
            flag1 = 1;
            flag = 0;
            //showDia(1);
        }else if(from.equals("FromSettingsOptions")){
            count_layout.setVisibility(View.VISIBLE);
            btn_save.setText("Add");
            flag = 1;
            flag1 = 1;
            int maxID = db.getTableMax(DBHandlerR.Table_Table);
            tv_count.setText(String.valueOf(maxID));
            if(getSupportActionBar()!=null){
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void init(){
        ed_table_size = (EditText) findViewById(R.id.ed_table_size);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
        permission = new GetPermission();
        constant = new Constant(AddTableMasterActivity.this);
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        db = new DBHandlerR(getApplicationContext());
        count_layout = (LinearLayout) findViewById(R.id.count_layout);
        tv_count = (TextView) findViewById(R.id.tv_count);
    }

    private void showDia(int i){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddTableMasterActivity.this);
        builder.setCancelable(false);
        if(i==1) {
            builder.setMessage("Do You Have Table Systems In Your Company?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    saveLocally();
                    doFinish();
                    dialogInterface.dismiss();
                }
            });
        }else if(i==2) {
            builder.setMessage("Tables Record Saved Successfully");
            builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    saveLocally();
                    flag1 = 0;
                    doFinish();
                    dialogInterface.dismiss();
                }
            });
        }else if(i==3) {
            builder.setMessage("Tables Added Successfully");
            builder.setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    flag1 = 1;
                    TableViewFragment.isUpdated = 1;
                    doFinish();
                    dialogInterface.dismiss();
                }
            });
        }
        builder.create().show();
    }

    private void doFinish(){
        if(flag1 ==0) {
            Intent intent = new Intent(getApplicationContext(), AddProductMasterActivity.class);
            intent.putExtra("from", "FromVerificationActivity");
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.enter,R.anim.exit);
        }else if(flag1 ==1){
            finish();
            overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
        }
        toast.cancel();
        Runtime.getRuntime().gc();
        Runtime.getRuntime().freeMemory();
    }

    private void saveLocally(){
        SharedPreferences.Editor  editor = VerificationActivity.pref.edit();
        editor.putBoolean(getString(R.string.pref_isTableSaved),true);
        editor.apply();
    }

    private void saveTableMaster() {
        int maxID = db.getMax(DBHandlerR.Table_Table);
        int size;
        if (!ed_table_size.getText().toString().equals("") || ed_table_size.getText().toString().length() != 0) {
            size = Integer.parseInt(ed_table_size.getText().toString());
            if (flag == 0) {
                for (int i = maxID; i <= size; i++) {
                    TableClass tableClass = new TableClass();
                    String tablename = "T-" + i;
                    tableClass.setTable_ID(i);
                    tableClass.setTables(tablename);
                    tableClass.setGstgroup(Constant.default_gst_name);
                    db.addTable(tableClass);
                }
                showDia(2);
            } else {
                for (int i = 0; i < size; i++) {
                    TableClass tableClass = new TableClass();
                    String tablename = "T-" + maxID;
                    tableClass.setTable_ID(maxID);
                    tableClass.setTables(tablename);
                    tableClass.setGstgroup(Constant.default_gst_name);
                    db.addTable(tableClass);
                    maxID++;
                }
                showDia(3);
            }
        }
        else{
            toast.setText("Please Enter Table Size");
            toast.show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                saveTableMaster();
                /*if(ed_table_size.getText().toString().equals("") || ed_table_size.getText().toString().length()!=0) {
                    saveTableMaster();
                }else{
                    toast.setText("Please Enter TableClass Size");
                    toast.show();
                }*/
                break;
        }
    }

    @Override
    public void onBackPressed() {
        doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
