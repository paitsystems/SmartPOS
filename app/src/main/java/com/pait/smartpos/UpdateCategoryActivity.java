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
import com.pait.smartpos.fragments.CategoryFragment;
import com.pait.smartpos.model.MasterUpdationClass;

public class UpdateCategoryActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText ed_cat2;
    private TextView tv_cat1;
    private Button btn_add;
    private Switch aSwitch;
    private String catName,catId,active;
    private Toast toast;
    private DBHandlerR db;
    private TextView tv_InActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change__category);

        init();

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

       /* String catChangeStr = getIntent().getExtras().getString("str");
        assert catChangeStr != null;
        String[] catArr = catChangeStr.split("\\^");
        if(catArr.length>1){
            catName = catArr[0];
            active = catArr[1];
            catId  = catArr[2];
            tv_cat1.setText(catName);
            if(active.equals("Y")){
                aSwitch.setChecked(true);
            }else{
                aSwitch.setChecked(false);
            }
        }else{
            catName = catChangeStr;
        }*/

        MasterUpdationClass master = (MasterUpdationClass) getIntent().getExtras().getSerializable("obj");
        assert master != null;
        catName = master.getMasterName();
        catId  = String.valueOf(master.getMasterAuto());
        catName = master.getMasterName();
        if(master.getIsMasterActive().equals("Y") || master.getIsMasterActive().equals("A")){
            aSwitch.setChecked(true);
        }else{
            aSwitch.setChecked(false);
        }
        tv_cat1.setText(catName);
        ed_cat2.setText(catName);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add:
                validation();
                break;
            case R.id.switch1:
                if(aSwitch.isChecked()){
                    tv_InActive.setText("Category Active");
                }else {
                    tv_InActive.setText("Category InActive");
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
        aSwitch = (Switch) findViewById(R.id.switch1);
        tv_cat1 = (TextView) findViewById(R.id.tv_cat1);
        ed_cat2 = (EditText) findViewById(R.id.ed_cat2);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
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
            toast.setText("Please Enter Cat2");
            check = false;
        }
        if(check){
            updateProduct();
        }else{
            view.requestFocus();
            toast.show();
        }
    }

    private void updateProduct(){
        String _catName = ed_cat2.getText().toString();
        String _active;
        if(aSwitch.isChecked()){
            _active = "Y";
        }else{
            _active = "N";
        }
        db.updateCategory(catId,_catName,_active);
        showDia(2);
    }

    private void showDia(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateCategoryActivity.this);
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

}
