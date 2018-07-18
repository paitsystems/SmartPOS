package com.pait.smartpos;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.fragments.TaxFragment;
import com.pait.smartpos.model.TaxClass;

public class AddNewTaxActivity extends AppCompatActivity implements View.OnClickListener {

    EditText ed_new_tax_name, ed_rate;
    TextView tv_perv_tax_name;
    Button btn_add;
    //String taxText, rateText,taxId,taxName;
    Toast toast;
    DBHandlerR db;
    Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_tax);

        init();

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    void init(){
        tv_perv_tax_name = (TextView) findViewById(R.id.tv_perv_tax_name);
        ed_new_tax_name = (EditText) findViewById(R.id.ed_new_tax_name);
        ed_rate = (EditText) findViewById(R.id.ed_rate);
        btn_add = (Button) findViewById(R.id.btn_add);
        aSwitch = (Switch) findViewById(R.id.switch1);
        btn_add.setOnClickListener(this);
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        db = new DBHandlerR(getApplicationContext());
    }

    void validation() {
        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ed_new_tax_name.getWindowToken(),0);
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
            addNewTax();
        }else{
            view.requestFocus();
            toast.show();
        }
    }

    void addNewTax(){
        String _taxName = ed_new_tax_name.getText().toString();
        String _taxRate = ed_rate.getText().toString();
        if(!db.isTaxAlreadyPresent(_taxName,_taxRate)) {
            int taxId = db.getMax(DBHandlerR.Tax_Table);
            String _active;
            if (aSwitch.isChecked()) {
                _active = "Y";
            } else {
                _active = "N";
            }
            TaxClass tax = new TaxClass();
            tax.setTax_ID(taxId);
            tax.setTaxName(_taxName);
            tax.setTaxRate(Float.parseFloat(_taxRate));
            tax.setIsActive(_active);
            db.addTax(tax);
            showDia(2);
        }else{
            toast.setText("This Tax Already Present");
            toast.show();
        }
    }

    void showDia(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewTaxActivity.this);
        builder.setCancelable(false);
        if(i==1) {
            builder.setMessage("Add New Tax");
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
            builder.setMessage("Tax Added Successfully");
            builder.setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    TaxMasterActivity.isUpdated = 1;
                    TaxFragment.isUpdated = 1;
                    DrawerTestActivity.isUpdated = 1;
                    finish();
                    overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("Add More", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    doClear();
                    dialogInterface.dismiss();
                }
            });

        }
        builder.create().show();
    }

    void doClear(){
        ed_new_tax_name.setText(null);
        ed_rate.setText(null);
        aSwitch.setChecked(true);
        ed_new_tax_name.requestFocus();
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
