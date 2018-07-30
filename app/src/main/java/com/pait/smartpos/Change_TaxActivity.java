package com.pait.smartpos;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pait.smartpos.db.DBHandlerR;

public class Change_TaxActivity extends AppCompatActivity implements View.OnClickListener{

    EditText ed_new_tax_name, ed_rate;
    TextView tv_perv_tax_name;
    Button btn_add;
    String taxText, rateText,taxId,taxName;
    Toast toast;
    DBHandlerR db;
    Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change__tax);

        init();

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String taxChangeStr = getIntent().getExtras().getString("str");
        assert taxChangeStr != null;
        String[] taxArr = taxChangeStr.split("\\^");

        if(taxArr.length>1){
            taxText = taxArr[0];
            rateText = taxArr[1];
            taxName = taxArr[0];
            taxId  = taxArr[3];
            if(taxArr[2].equals("Y")){
                aSwitch.setChecked(true);
            }else{
                aSwitch.setChecked(false);
            }
            tv_perv_tax_name.setText(taxName);
            ed_new_tax_name.setText(taxName);
            ed_rate.setText(rateText);
        }else{
            taxText = taxChangeStr;
        }
    }

    void init(){
        tv_perv_tax_name = (TextView) findViewById(R.id.tv_perv_tax_name);
        ed_new_tax_name = (EditText) findViewById(R.id.ed_new_tax_name);
        ed_rate = (EditText) findViewById(R.id.ed_ssp);
        btn_add = (Button) findViewById(R.id.btn_add);
        aSwitch = (Switch) findViewById(R.id.switch1);
        btn_add.setOnClickListener(this);
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        db = new DBHandlerR(getApplicationContext());
    }

    void validation() {
        String cat2 = ed_new_tax_name.getText().toString();
        String rate = ed_rate.getText().toString();

        boolean check = true;
        View view = null;
        if (rate.equals("") || rate.length() == 0) {
            toast.setText("Please Enter Tax Rate");
            view = ed_rate;
            check = false;
        }
        if (cat2.equals("") || cat2.length() == 0) {
            view = ed_new_tax_name;
            toast.setText("Please Enter Tax Name");
            check = false;
        }
        if(check){
            updateTax();
        }else{
            view.requestFocus();
            toast.show();
        }
    }

    void updateTax(){
        String _prodName = ed_new_tax_name.getText().toString();
        String _prodRate = ed_rate.getText().toString();
        String _active;
        if(aSwitch.isChecked()){
            _active = "Y";
        }else{
            _active = "N";
        }
        db.updateTax(taxId,_prodName,_prodRate,_active);
        showDia(2);
    }

    void showDia(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Change_TaxActivity.this);
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
            builder.setMessage("Tax Updated Successfully");
            builder.setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    TaxMasterActivity.isUpdated = 1;
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
