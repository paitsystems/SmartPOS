package com.pait.smartpos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.model.ProductClass;
import com.pait.smartpos.model.SupplierMasterClass;
import com.pait.smartpos.permission.GetPermission;

import java.util.ArrayList;
import java.util.HashMap;

public class AddSupplierMaster extends AppCompatActivity implements View.OnClickListener {

    private EditText ed_name, ed_address, ed_mobileNo, ed_emailid, ed_remark, ed_gstNo;
    private Button btn_add;
    private Toast toast;
    private DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_supplier_master);

        init();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                validation();
                break;
        }
    }

    private void init(){
        ed_name = findViewById(R.id.ed_name);
        ed_address = findViewById(R.id.ed_address);
        ed_mobileNo = findViewById(R.id.ed_mobileNo);
        ed_gstNo = findViewById(R.id.ed_gstno);
        ed_emailid = findViewById(R.id.ed_emailId);
        ed_remark = findViewById(R.id.ed_remark);
        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        db = new DBHandler(getApplicationContext());
    }

    private void validation() {
        String name = ed_name.getText().toString();

        boolean check = true;
        View view = null;
        if (name.equals("") || name.length() == 0) {
            view = ed_name;
            toast.setText("Please Enter Name");
            check = false;
        }
        if(check){
            addProduct();
        }else{
            if(view!=null) {
                view.requestFocus();
            }
            toast.show();
        }
    }

    private void addProduct() {
        String name = ed_name.getText().toString();
        String address = ed_address.getText().toString();
        String mobileNo = ed_mobileNo.getText().toString();
        String emailId = ed_emailid.getText().toString();
        String gstNo = ed_gstNo.getText().toString();
        String remark = ed_remark.getText().toString();

        int auto = db.getMaxSMAuto();
        SupplierMasterClass supp = new SupplierMasterClass();
        supp.setAuto(auto);
        supp.setId(auto);
        supp.setName(name);
        supp.setAddress(address);
        supp.setMobile(mobileNo);
        supp.setEmail(emailId);
        supp.setGstno(gstNo);
        supp.setRemark(remark);
        db.addSupplier(supp);
        toast.setText("Added Successfully");
        toast.show();
        clearField();
    }

    private void clearField(){
        ed_name.setText(null);
        ed_address.setText(null);
        ed_mobileNo.setText(null);
        ed_emailid.setText(null);
        ed_gstNo.setText(null);
        ed_remark.setText(null);
        ed_name.requestFocus();
    }
}

