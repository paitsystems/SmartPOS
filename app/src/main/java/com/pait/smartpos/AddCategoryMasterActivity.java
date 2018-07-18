package com.pait.smartpos;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;

import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.fragments.CategoryFragment;
import com.pait.smartpos.log.WriteLog;
import com.pait.smartpos.model.CategoryClass;

public class AddCategoryMasterActivity extends AppCompatActivity implements View.OnClickListener {

    private Constant constant, constant1;
    private Toast toast;
    private EditText ed_cat2;
    private TextView tv_cat1;
    private Button btn_add;
    private Switch aSwitch;
    private String catName,catId,active;
    private DBHandlerR db;
    private TextView tv_InActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category_master);

        init();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                validation();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //showDia(0);
        new Constant(AddCategoryMasterActivity.this).doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //showDia(0);
                new Constant(AddCategoryMasterActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        constant = new Constant(AddCategoryMasterActivity.this);
        constant1 = new Constant(getApplicationContext());
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        aSwitch = (Switch) findViewById(R.id.switch1);
        tv_cat1 = (TextView) findViewById(R.id.tv_cat1);
        ed_cat2 = (EditText) findViewById(R.id.ed_cat2);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        db = new DBHandlerR(getApplicationContext());
        tv_InActive = (TextView) findViewById(R.id.tv_InActive);
        aSwitch.setOnClickListener(this);
    }

    private void validation() {
        String cat2 = ed_cat2.getText().toString();
        boolean check = true;
        View view = null;
        if (cat2.equals("") || cat2.length() == 0) {
            view = ed_cat2;
            toast.setText("Please Enter Category Name");
            check = false;
        }
        if(check){
            addProduct();
        }else{
            view.requestFocus();
            toast.show();
        }
    }

    private void addProduct(){
        String _catName = ed_cat2.getText().toString();
        String _active;
        if(aSwitch.isChecked()){
            _active = "Y";
        }else{
            _active = "N";
        }
        if(!db.isCategoryAlreadyPresent(_catName)){
            CategoryClass categoryClass = new CategoryClass();
            int max = db.getMax(DBHandlerR.Category_Table);
            categoryClass.setCategory_ID(max);
            categoryClass.setCategory(_catName);
            categoryClass.setIsActive(_active);
            db.addCategory(categoryClass);
            ed_cat2.setText(null);
            ed_cat2.requestFocus();
            aSwitch.setChecked(true);
            toast.setText("Category Added");
            toast.show();
        }else{
            toast.setText("Category Already Present");
            toast.show();
        }
    }

    private void showDia(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddCategoryMasterActivity.this);
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
            builder.setMessage("Category Updated Successfully");
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

    private void writeLog(String _data) {
        new WriteLog().writeLog(getApplicationContext(), "AddCategoryMasterActivity_" + _data);
    }
}
