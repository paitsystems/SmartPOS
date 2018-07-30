package com.pait.smartpos;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.adpaters.CategoryExpandableListAdapter;
import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.model.CategoryClass;
import com.pait.smartpos.model.GSTDetailClass;
import com.pait.smartpos.model.GSTMasterClass;
import com.pait.smartpos.model.ProductClass;
import com.pait.smartpos.model.TaxClass;
import com.pait.smartpos.permission.GetPermission;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddProductMasterActivity extends AppCompatActivity implements View.OnClickListener{

    private AutoCompleteTextView auto_cat1;
    private EditText ed_cat2, ed_rate;
    private Button btn_add, btn_proceed, btn_change_cat1;
    private Switch aSwitch;
    private boolean changeCat1Clicked = false;
    private ExpandableListView listView;
    private LinearLayout addLayout;

    private List<Integer> catIdList, prodIdList;
    private List<String> catNameList;
    private HashMap<Integer,String> catIdNameMap;
    private HashMap<String,List<String>> catNameProdName;
    private HashMap<Integer,List<Integer>> catIDProdIdMap;

    private GetPermission permission;
    private Constant constant;
    private Toast toast;
    private DBHandlerR db;
    private List<String> catList;
    private int flag = -1;//0-ViewOnly, 1-Add,2-Update
    private String prodChangeStr = "", catChangeStr = "";
    private int flag1 = 0;
    public static int isUpdated = 0;
    private Spinner sp_gstGroup;
    private List<String> gstList;
    private RadioButton rdo_gstInclude, rdo_gstExclude;
    private TextView tv_InActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_master);

        init();

        String from = getIntent().getExtras().getString("from");
        assert from != null;
        if(from.equals("FromVerificationActivity")){
            flag1 = 1;
            addLayout.setVisibility(View.VISIBLE);
            btn_proceed.setVisibility(View.VISIBLE);
        }else if(from.equals("FromSettingsOptions")){
            //showDia(0,"");
            //addLayout.setVisibility(View.GONE);
            //btn_proceed.setVisibility(View.GONE);
            flag = 1;
            addLayout.setVisibility(View.VISIBLE);
            setCategory();
            prepareExpandableList();
            if(getSupportActionBar()!=null){
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        /*auto_cat1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showSoftInputFromInputMethod(auto_cat1.getWindowToken(),0);
                    auto_cat1.setThreshold(0);
                    auto_cat1.showDropDown();
                }
                return true;
            }
        });*/

        auto_cat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showSoftInputFromInputMethod(auto_cat1.getWindowToken(),0);
                auto_cat1.showDropDown();
            }
        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                      int groupPosition, int childPosition, long id) {
                if(flag==2) {
                    prodChangeStr ="";
                    String cat1 = catIdNameMap.get(catIdList.get(groupPosition));
                    String str = catNameProdName.get(catIdNameMap.get(catIdList.get(groupPosition))).get(childPosition);
                    int prodId = catIDProdIdMap.get(catIdList.get(groupPosition)).get(childPosition);
                    Log.d("Log","ProdID - "+prodId);
                    String[] prodArr = str.split("\\^");
                    String prodText;
                    if(prodArr.length>1){
                        prodText = prodArr[0];
                    }else{
                        prodText = str;
                    }
                    //ProdName^Rate^ProdID
                    prodChangeStr = str+"^"+prodId+"^"+cat1;
                    showDia(2, prodText);
                }
                return false;
            }
        });

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view,
                           int groupPosition, long id) {
                if(flag==2) {
                    catChangeStr = "";
                    int catID = catIdList.get(groupPosition);
                    String str = catIdNameMap.get(catIdList.get(groupPosition));
                    String[] catArr = str.split("\\^");
                    String catText;
                    if(catArr.length>1){
                        catText = catArr[0];
                    }else{
                        catText = str;
                    }
                    catChangeStr = str +"^"+ catID;
                    showDia(1, catText);
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isUpdated == 1){
            setCategory();
            prepareExpandableList();
            isUpdated = 0;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add:
                validation();
                break;
            case R.id.btn_proceed:
                proceed();
                break;
            case R.id.btn_change_cat1:
                changeCat1Clicked = true;
                clearField();
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
        aSwitch = (Switch) findViewById(R.id.switch1);
        auto_cat1 = (AutoCompleteTextView) findViewById(R.id.auto_cat1);
        ed_cat2 = (EditText) findViewById(R.id.ed_cat2);
        ed_rate = (EditText) findViewById(R.id.ed_ssp);
        btn_change_cat1 = (Button) findViewById(R.id.btn_change_cat1);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_proceed = (Button) findViewById(R.id.btn_proceed);
        btn_change_cat1.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_proceed.setOnClickListener(this);
        listView = (ExpandableListView) findViewById(R.id.expandableListView);
        permission = new GetPermission();
        constant = new Constant(AddProductMasterActivity.this);
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        db = new DBHandlerR(getApplicationContext());
        catList = new ArrayList<>();
        catNameList = new ArrayList<>();
        catIdList  = new ArrayList<>();
        prodIdList  = new ArrayList<>();
        catIdNameMap = new HashMap<>();
        catIDProdIdMap = new HashMap<>();
        catNameProdName = new HashMap<>();
        addLayout = (LinearLayout) findViewById(R.id.add_layout);

        sp_gstGroup = (Spinner) findViewById(R.id.sp_gstgroup);
        gstList = new ArrayList<>();
        gstList = db.getGSTGroup();
        sp_gstGroup.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.custom_spinner,gstList));
        rdo_gstInclude = (RadioButton) findViewById(R.id.rdo_gstInclude);
        rdo_gstExclude = (RadioButton) findViewById(R.id.rdo_gstExclude);
        tv_InActive = (TextView) findViewById(R.id.tv_InActive);
        rdo_gstInclude.setOnClickListener(this);
        rdo_gstExclude.setOnClickListener(this);
        aSwitch.setOnClickListener(this);
    }

    private void setCategory(){
        catList.clear();
        auto_cat1.setAdapter(null);
        Cursor res = db.getCatgory();
        if(res.moveToFirst()){
            do {
                catList.add(res.getString(1));
            }while (res.moveToNext());
        }else{
            catList.add("NA");
        }
        auto_cat1.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.list_item_auto_category,catList));
        res.close();
    }

    private void prepareExpandableList(){
        catIdList.clear();
        prodIdList.clear();
        catIdNameMap.clear();
        catNameList.clear();
        catNameProdName.clear();
        Cursor res = db.getAllCatgory();
        if(res.moveToFirst()){
            do{
                catIdList.add(res.getInt(0));
                String str = res.getString(1) + "^" + res.getString(2);
                catIdNameMap.put(res.getInt(0),str);
                catNameList.add(res.getString(1));
            }while (res.moveToNext());
        }else{
            catNameList.add("NA");
        }
        res.close();

        if(!catIdNameMap.isEmpty()){
            for(Integer i : catIdList){
               Cursor res1 = db.getProduct(i);
                List<String> prodlist = new ArrayList<>();
                List<Integer> prodId = new ArrayList<>();
                if(res1.moveToFirst()){
                    do {
                        //ProductName^Rate^Active
                        String str = res1.getString(1) + "^" + res1.getString(3);// + "^" + res1.getString(6);
                        prodlist.add(str);
                        prodId.add(res1.getInt(0));
                        prodIdList.add(res1.getInt(0));
                    }while (res1.moveToNext());
                }
                catIDProdIdMap.put(i,prodId);
                catNameProdName.put(catIdNameMap.get(i),prodlist);
                res1.close();
            }
        }
        listView.setAdapter(new CategoryExpandableListAdapter(getApplicationContext(),catIdList,catIdNameMap,catNameProdName));
    }

    private void validation() {
        String cat1 = auto_cat1.getText().toString();
        String cat2 = ed_cat2.getText().toString();
        String rate = ed_rate.getText().toString();

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
        if (cat1.equals("") || cat1.length() == 0) {
            view = auto_cat1;
            toast.setText("Please Select Cat1");
            check = false;
        }
        if(sp_gstGroup.getSelectedItemPosition()==0){
            check = false;
            toast.setText("Please Select GSTGroup");
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

    private void addProduct(){
        String catname = auto_cat1.getText().toString();
        String prodName = ed_cat2.getText().toString();
        double rate = Double.parseDouble(ed_rate.getText().toString());

        if(!db.isCategoryAlreadyPresent(catname)){
            CategoryClass categoryClass = new CategoryClass();
            int max = db.getMax(DBHandlerR.Category_Table);
            categoryClass.setCategory_ID(max);
            categoryClass.setCategory(catname);
            if(aSwitch.isChecked()) {
                categoryClass.setIsActive("Y");
            }else{
                categoryClass.setIsActive("N");
            }
            db.addCategory(categoryClass);
        }
        if(!db.isProductAlreadyPresent(prodName,String.valueOf(rate))) {
            int catid = db.getCategoryID(catname);
            int prodMax = db.getMax(DBHandlerR.Product_Table);
            ProductClass productClass = new ProductClass();
            productClass.setProduct_Barcode("1");
            productClass.setProduct_KArea("1");
            productClass.setProduct_Cat(catid);
            productClass.setProduct_ID(prodMax);
            productClass.setProduct_Name(prodName);
            productClass.setProduct_Rate(rate);
            productClass.setGstGroup(Constant.default_gst_name);
            productClass.setTaxType("I");
            db.addProduct(productClass);
            toast.setText("Added Successfully");
            toast.show();
            setCategory();
            prepareExpandableList();
            clearField();
        }else{
            toast.setText("This ProductClass Already Present");
            toast.show();
        }
    }

    private void clearField(){
        aSwitch.setChecked(true);
        ed_cat2.setText(null);
        ed_rate.setText(null);
        ed_cat2.requestFocus();
        if(changeCat1Clicked){
            auto_cat1.setText(null);
            auto_cat1.requestFocus();
            changeCat1Clicked = false;
        }
    }

    private void proceed(){
        int max = db.getMaxId(DBHandlerR.Category_Table);
        if(max!=0){
            saveLocally();
            flag1 = 2;
            doFinish();
        }else{
            toast.setText("Please Enter Atleast One CategoryClass");
            toast.show();
        }
    }

    private void doFinish(){
        toast.cancel();
        finish();
        if(flag1 == 2){
            addDefaultTax();
            //startActivity(new Intent(getApplicationContext(),MainActivity.class));
            startActivity(new Intent(getApplicationContext(),DrawerTestActivity.class));
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }else if(flag1 == 1){
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
        Runtime.getRuntime().gc();
        Runtime.getRuntime().freeMemory();
    }

    private void saveLocally(){
        SharedPreferences.Editor  editor = VerificationActivity.pref.edit();
        editor.putBoolean(getString(R.string.pref_isCategorySaved),true);
        editor.apply();
    }

    private void addDefaultTax(){
        TaxClass tax = new TaxClass();
        tax.setTax_ID(1);
        tax.setTaxName("Central GST @ 19%");
        tax.setIsActive("Y");
        tax.setTaxRate(19);
        //db.addTax(tax);

        addGSTMaster();
    }

    private void addGSTMaster(){
        GSTMasterClass gst = new GSTMasterClass();
        gst.setGroupName(Constant.default_gst_name);
        gst.setEff_date(new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime()));
        //TODO: set CrBy
        gst.setCrby(1);
        gst.setRemark("");
        gst.setStatus("Y");
        int a = db.addGSTMaster(gst);
        addGSTDetail(a);
    }

    private void addGSTDetail(int mastAuto) {
        GSTDetailClass gstD = new GSTDetailClass();
        gstD.setMastAuto(mastAuto);
        gstD.setFromRange(0f);
        gstD.setToRange(100000f);
        gstD.setGstPer(5f);
        gstD.setCgstPer(2.5f);
        gstD.setSgstPer(2.5f);
        gstD.setCgstShare(50f);
        gstD.setSgstShare(50f);
        gstD.setCessPer(0f);
        db.addGSTDetail(gstD);
        //db.setDefaultGSTToTable();
    }

    private void showDia(int i, String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddProductMasterActivity.this);
        builder.setCancelable(false);
        if(i==0) {
            builder.setMessage("What Do You Want To Do?");
            builder.setNeutralButton("View Only", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    flag = 0;
                    addLayout.setVisibility(View.GONE);
                    prepareExpandableList();
                    dialogInterface.dismiss();
                }
            });
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    flag = 1;
                    addLayout.setVisibility(View.VISIBLE);
                    setCategory();
                    prepareExpandableList();
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    flag = 2;
                    addLayout.setVisibility(View.GONE);
                    prepareExpandableList();
                    dialogInterface.dismiss();
                }
            });
        }
        else if(i==1) {
            builder.setMessage("Do You Want To Change CategoryClass " + str +"?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getApplicationContext(),UpdateCategoryActivity.class);
                    intent.putExtra("str",catChangeStr);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter,R.anim.exit);
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }else if(i==2) {
            builder.setMessage("Do You Want To Change ProductClass " + str+"?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getApplicationContext(),UpdateProductMasterActivity.class);
                    intent.putExtra("str",prodChangeStr);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter,R.anim.exit);
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }else if(i==3) {
            builder.setMessage("Do You Want Add Tax Or Use Existing(You Can Change Later)?");
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getApplicationContext(),Change_TaxActivity.class);
                    intent.putExtra("str","");
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter,R.anim.exit);
                    dialogInterface.dismiss();
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("Use Existing", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    dialogInterface.dismiss();
                }
            });
        }
        builder.create().show();
    }

}
