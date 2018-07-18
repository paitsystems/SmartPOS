package com.pait.smartpos;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.fragments.TableViewFragment;
import com.pait.smartpos.model.MasterUpdationClass;

public class UpdateTableMasterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText ed_tablename;
    private TextView tv_tablename, tv_tax_rate, tv_tax_amnt, tv_InActive;
    private Button btn_add;
    private Switch aSwitch;
    private String tableName,tableId,active, gstGroup;
    private Toast toast;
    private DBHandlerR db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_table_name);

        init();

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /*String catChangeStr = getIntent().getExtras().getString("str");
        assert catChangeStr != null;
        String[] catArr = catChangeStr.split("\\^");
        if(catArr.length>1){
            tableName = catArr[0];
            active = catArr[1];
            gstGroup = catArr[2];
            tableId  = catArr[3];
            tv_tablename.setText(tableName);
            if(active.equals("Y")){
                aSwitch.setChecked(true);
            }else{
                aSwitch.setChecked(false);
            }
        }else{
            tableName = catChangeStr;
            tv_tablename.setText(tableName);
        }*/

        MasterUpdationClass master = (MasterUpdationClass) getIntent().getExtras().getSerializable("obj");
        assert master != null;
        tableName = master.getMasterName();
        tableId  = String.valueOf(master.getMasterAuto());
        tv_tablename.setText(tableName);
        ed_tablename.setText(tableName);
        if(master.getIsMasterActive().equals("Y") || master.getIsMasterActive().equals("A")){
            aSwitch.setChecked(true);
        }else{
            aSwitch.setChecked(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add:
                validation();
                break;
            case R.id.switch1:
                if(aSwitch.isChecked()){
                    tv_InActive.setText("Table Active");
                }else {
                    tv_InActive.setText("Table InActive");
                }
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

    private void init(){
        aSwitch = (Switch) findViewById(R.id.switch1);
        tv_tablename = (TextView) findViewById(R.id.tv_tablename);
        ed_tablename = (EditText) findViewById(R.id.ed_tablename);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        tv_tax_rate = (TextView) findViewById(R.id.tv_tax_rate);
        tv_tax_amnt = (TextView) findViewById(R.id.tv_tax_amnt);
        db = new DBHandlerR(getApplicationContext());
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        tv_InActive = (TextView) findViewById(R.id.tv_InActive);
        aSwitch.setOnClickListener(this);
    }

    private void validation() {
        String tablename = ed_tablename.getText().toString();

        boolean check = true;
        View view = null;
        if (tablename.equals("") || tablename.length() == 0) {
            view = ed_tablename;
            toast.setText("Please Enter TableName");
            check = false;
        }
        if(check){
            updateTable();
        }else{
            view.requestFocus();
            toast.show();
        }
    }

    private void updateTable(){
        String _tableName = ed_tablename.getText().toString();
        String _active;
        if(aSwitch.isChecked()){
            _active = "Y";
        }else{
            _active = "N";
        }
        db.updateTableName(tableId,_tableName,_active);
        showDia(2);
    }

    private void showDia(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateTableMasterActivity.this);
        builder.setCancelable(false);
        if(i==1) {
            builder.setMessage("Update Changes You Have Made?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    validation();
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }else if(i==2){
            builder.setMessage("Table Updated Successfully");
            builder.setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    TableViewFragment.isUpdated = 1;
                    finish();
                    overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
                    dialogInterface.dismiss();
                }
            });
        }
        builder.create().show();
    }

    private void doFinish(){
        finish();
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }

}
