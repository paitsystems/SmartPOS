package com.pait.smartpos;

import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.fragments.TableViewFragment;

import java.util.ArrayList;

public class UpdateTableNameGSTActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText ed_tablename;
    private TextView tv_tablename, tv_tax_rate, tv_tax_amnt;
    private Button btn_add;
    private Switch aSwitch;
    private String tableName,tableId,active, gstGroup;
    private Toast toast;
    private DBHandlerR db;
    private Spinner sp_gst;
    private ArrayList<String> gstNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_table_name_gst);

        init();

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String catChangeStr = getIntent().getExtras().getString("str");
        assert catChangeStr != null;
        String[] catArr = catChangeStr.split("\\^");
        if(catArr.length>1){
            tableName = catArr[0];
            active = catArr[1];
            gstGroup = catArr[2];
            tableId  = catArr[3];
            tv_tablename.setText(tableName);
            ed_tablename.setText(tableName);
            if(active.equals("Y")){
                aSwitch.setChecked(true);
            }else{
                aSwitch.setChecked(false);
            }
        }else{
            tableName = catChangeStr;
            tv_tablename.setText(tableName);
        }

        if(!gstNameList.isEmpty()) {
            for (int i = 0; i < gstNameList.size(); i++) {
                if (gstNameList.get(i).equals(gstGroup)) {
                    sp_gst.setSelection(i);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add:
                showDia(1);
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
        sp_gst = (Spinner) findViewById(R.id.sp_gst);
        gstNameList = new ArrayList<>();
        Cursor res = db.getActiveGSTGroup();
        if(res.moveToFirst()){
            do{
                gstNameList.add(res.getString(1));
            }while (res.moveToNext());
        }
        res.close();
        sp_gst.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.list_item_option_setttings,gstNameList));

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
            if(!db.isTableAlreadyPresent(tablename)) {
                updateTable();
            }
            updateTableGST();
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
        //showDia(2);
    }

    private void updateTableGST(){
        String gstgroup = sp_gst.getSelectedItem().toString();
        db.updateTableGST(tableId,tableName,gstgroup);
        showDia(2);
    }

    private void showDia(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateTableNameGSTActivity.this);
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
            builder.setMessage("Table Data Updated Successfully");
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
