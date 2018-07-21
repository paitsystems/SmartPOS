package com.pait.smartpos;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.fragments.CategoryFragment;
import com.pait.smartpos.model.MasterUpdationClass;
import com.pait.smartpos.model.ProductClass;

import java.util.ArrayList;
import java.util.List;

public class UpdateProductMasterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText ed_cat2, ed_rate;
    private TextView tv_cat1;
    private Button btn_add;
    private String prodText, rateText,prodId,catName;
    private Toast toast;
    private DBHandlerR db;
    private Spinner sp_gstGroup;
    private List<String> gstList;
    private RadioButton rdo_gstInclude, rdo_gstExclude;
    private TextView tv_InActive;
    private Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change__product);

        init();

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /*String prodChangeStr = getIntent().getExtras().getString("str");
        assert prodChangeStr != null;
        String[] prodArr = prodChangeStr.split("\\^");

        if(prodArr.length>1){
            prodText = prodArr[0];
            rateText = prodArr[1];
            prodId  = prodArr[2];
            catName = prodArr[3];

            tv_cat1.setText(catName);
            ed_cat2.setText(prodText);
            ed_rate.setText(rateText);

        }else{
            prodText = prodChangeStr;
        }*/

        MasterUpdationClass master = (MasterUpdationClass) getIntent().getExtras().getSerializable("obj");
        assert master != null;
        prodText = master.getMasterName();
        rateText = master.getMasterRate();
        prodId  = String.valueOf(master.getMasterAuto());
        catName = master.getMasterCat();
        /*if(master.getMasterTaxType().equals("I")){
            rdo_gstInclude.setChecked(true);
            rdo_gstExclude.setChecked(false);
        }else{
            rdo_gstInclude.setChecked(false);
            rdo_gstExclude.setChecked(true);
        }*/
        int i=-1;
        for(String s : gstList){
            i++;
            if(s.equals(master.getMasterGSTGroup())){
                sp_gstGroup.setSelection(i);
                break;
            }
        }
        if(master.getIsMasterActive().equals("Y") || master.getIsMasterActive().equals("A")){
            aSwitch.setChecked(true);
        }else{
            aSwitch.setChecked(false);
        }
        tv_cat1.setText(catName);
        ed_cat2.setText(prodText);
        ed_rate.setText(rateText);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add:
                validation();
                break;
            case R.id.rdo_gstInclude:
                rdo_gstInclude.setChecked(true);
                rdo_gstExclude.setChecked(false);
                break;
            case R.id.rdo_gstExclude:
                rdo_gstExclude.setChecked(true);
                rdo_gstInclude.setChecked(false);
                break;
            case R.id.switch1:
                if(aSwitch.isChecked()){
                    tv_InActive.setText("Product Active");
                }else {
                    tv_InActive.setText("Product InActive");
                }
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
        tv_cat1 = (TextView) findViewById(R.id.tv_cat1);
        ed_cat2 = (EditText) findViewById(R.id.ed_cat2);
        ed_rate = (EditText) findViewById(R.id.ed_rate);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        db = new DBHandlerR(getApplicationContext());
        sp_gstGroup = (Spinner) findViewById(R.id.sp_gstgroup);
        gstList = new ArrayList<>();
        gstList = db.getGSTGroup();
        sp_gstGroup.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.custom_spinner,gstList));
        rdo_gstInclude = (RadioButton) findViewById(R.id.rdo_gstInclude);
        rdo_gstExclude = (RadioButton) findViewById(R.id.rdo_gstExclude);
        tv_InActive = (TextView) findViewById(R.id.tv_InActive);
        rdo_gstInclude.setOnClickListener(this);
        rdo_gstExclude.setOnClickListener(this);
        aSwitch = (Switch) findViewById(R.id.switch1);
        aSwitch.setOnClickListener(this);

    }

    private void validation() {
        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ed_cat2.getWindowToken(),0);
        String cat2 = ed_cat2.getText().toString();
        String rate = ed_rate.getText().toString();
        int pos = sp_gstGroup.getSelectedItemPosition();

        boolean check = true;
        View view = null;
        if (rate.equals("") || rate.length() == 0) {
            toast.setText("Please Enter Rate");
            view = ed_rate;
            check = false;
        }
        if (cat2.equals("") || cat2.length() == 0) {
            view = ed_cat2;
            toast.setText("Please Enter Cat2");
            check = false;
        }
        if(pos==0){
            toast.setText("Please Select GST Group");
            check = false;
        }
        if(check){
            updateProduct();
        }else{
            if(view!=null) {
                view.requestFocus();
            }
            toast.show();
        }
    }

    private void updateProduct(){
        String gstGroup = gstList.get(sp_gstGroup.getSelectedItemPosition());
        String gstType = "I";
        if(rdo_gstExclude.isChecked()){
            gstType = "E";
        }
        String isAtive = "Y";
        if(!aSwitch.isChecked()) {
            isAtive = "N";
        }

        String _prodName = ed_cat2.getText().toString();
        String _prodRate = ed_rate.getText().toString();

        ProductClass productClass = new ProductClass();
        productClass.setProduct_Name(_prodName);
        productClass.setProduct_Rate(Double.parseDouble(_prodRate));
        productClass.setGstGroup(gstGroup);
        productClass.setIsActive(isAtive);
        productClass.setTaxType(gstType);
        db.updateProduct(prodId,_prodName,_prodRate, productClass);
        showDia(2);
    }

    private void showDia(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProductMasterActivity.this);
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
            builder.setMessage("ProductClass Updated Successfully");
            builder.setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    AddProductMasterActivity.isUpdated = 1;
                    CategoryFragment.isUpdated = 1;
                    DrawerTestActivity.isUpdated = 1;
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
