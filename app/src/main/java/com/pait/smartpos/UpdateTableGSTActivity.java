package com.pait.smartpos;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.fragments.TableViewFragment;

import java.util.ArrayList;

public class UpdateTableGSTActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_tablename, tv_tax_rate, tv_tax_amnt;
    Button btn_add;
    String tableName,tableId,active, gstGroup;
    Toast toast;
    DBHandlerR db;
    Spinner sp_gst;
    ArrayList<String> gstNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_table_gst);

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

        sp_gst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    void init(){
        tv_tablename = (TextView) findViewById(R.id.tv_tablename);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        tv_tax_rate = (TextView) findViewById(R.id.tv_tax_rate);
        tv_tax_amnt = (TextView) findViewById(R.id.tv_tax_amnt);
        sp_gst = (Spinner) findViewById(R.id.sp_gst);
        db = new DBHandlerR(getApplicationContext());
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
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

    void validation() {
        boolean check = true;
        View view = null;
        if(check){
            updateTableGST();
        }else{
            view.requestFocus();
            toast.show();
        }
    }

    void updateTableGST(){
        String gstgroup = sp_gst.getSelectedItem().toString();
        db.updateTableGST(tableId,tableName,gstgroup);
        showDia(2);
    }

    void showDia(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateTableGSTActivity.this);
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
            builder.setMessage("Table GST Updated Successfully");
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

    void doFinish(){
        finish();
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add:
                validation();
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
