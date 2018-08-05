package com.pait.smartpos;

import android.annotation.SuppressLint;
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
import android.view.MotionEvent;
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
import com.pait.smartpos.db.DBHandler;
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

    private AutoCompleteTextView auto_cat1, auto_cat2;
    private EditText ed_cat3, ed_rate, ed_ssp, ed_qty;
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
    private DBHandler db;
    private List<String> cat1List, cat2List;
    private int flag = -1;//0-ViewOnly, 1-Add,2-Update
    private String prodChangeStr = "", catChangeStr = "";
    private int flag1 = 0;
    public static int isUpdated = 0;
    private Spinner sp_gstGroup;
    private List<String> gstList;
    private RadioButton rdo_gstInclude, rdo_gstExclude;
    private TextView tv_InActive;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_master);

        init();

        /*String from = getIntent().getExtras().getString("from");
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
            //prepareExpandableList();
            if(getSupportActionBar()!=null){
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }*/

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

        auto_cat1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    auto_cat1.showDropDown();
                    auto_cat1.setThreshold(0);
                }
                return false;
            }
        });

        auto_cat2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    auto_cat2.showDropDown();
                    auto_cat2.setThreshold(0);
                }
                return false;
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

        setCategory();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add:
                validation();
                break;
            case R.id.btn_proceed:
                //proceed();
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
        aSwitch = findViewById(R.id.switch1);
        auto_cat1 = findViewById(R.id.auto_cat1);
        auto_cat2 = findViewById(R.id.auto_cat2);
        ed_cat3 = findViewById(R.id.ed_cat3);
        ed_rate = findViewById(R.id.ed_pprice);
        ed_ssp = findViewById(R.id.ed_ssp);
        ed_qty = findViewById(R.id.ed_qty);
        btn_change_cat1 = findViewById(R.id.btn_change_cat1);
        btn_add = findViewById(R.id.btn_add);
        btn_proceed = findViewById(R.id.btn_proceed);
        btn_change_cat1.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_proceed.setOnClickListener(this);
        listView = findViewById(R.id.expandableListView);
        permission = new GetPermission();
        constant = new Constant(AddProductMasterActivity.this);
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        db = new DBHandler(getApplicationContext());
        cat1List = new ArrayList<>();
        cat2List = new ArrayList<>();
        catNameList = new ArrayList<>();
        catIdList  = new ArrayList<>();
        prodIdList  = new ArrayList<>();
        catIdNameMap = new HashMap<>();
        catIDProdIdMap = new HashMap<>();
        catNameProdName = new HashMap<>();
        addLayout = findViewById(R.id.add_layout);

        sp_gstGroup = findViewById(R.id.sp_gstgroup);
        gstList = new ArrayList<>();
        gstList = db.getGSTGroup();
        sp_gstGroup.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.custom_spinner,gstList));
        rdo_gstInclude = findViewById(R.id.rdo_gstInclude);
        rdo_gstExclude = findViewById(R.id.rdo_gstExclude);
        tv_InActive = findViewById(R.id.tv_InActive);
        rdo_gstInclude.setOnClickListener(this);
        rdo_gstExclude.setOnClickListener(this);
        aSwitch.setOnClickListener(this);
    }

    private void setCategory(){
        cat1List.clear();
        cat2List.clear();
        auto_cat1.setAdapter(null);
        auto_cat2.setAdapter(null);
        Cursor res1 = db.getCat1();
        if(res1.moveToFirst()){
            do {
                cat1List.add(res1.getString(0));
            }while (res1.moveToNext());
        }else{
            cat1List.add("NA");
        }
        res1.close();
        auto_cat1.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.list_item_auto_category,cat1List));

        Cursor res = db.getCat2();
        if(res.moveToFirst()){
            do {
                cat2List.add(res.getString(0));
            }while (res.moveToNext());
        }else{
            cat1List.add("NA");
        }
        res.close();
        auto_cat2.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.list_item_auto_category,cat2List));
    }

    private void prepareExpandableList(){
        catIdList.clear();
        prodIdList.clear();
        catIdNameMap.clear();
        catNameList.clear();
        catNameProdName.clear();
        Cursor res = db.getCat1();
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
               Cursor res1 = db.getCat1();
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
        String cat2 = auto_cat2.getText().toString();
        String cat3 = ed_cat3.getText().toString();
        String rate = ed_rate.getText().toString();
        String ssp = ed_ssp.getText().toString();
        String qty = ed_qty.getText().toString();

        boolean check = true;
        View view = null;
        if (qty.equals("") || qty.length() == 0) {
            toast.setText("Please Enter Qty");
            view = ed_qty;
            check = false;
        }
        if (ssp.equals("") || ssp.length() == 0) {
            toast.setText("Please Enter SSP");
            view = ed_ssp;
            check = false;
        }
        if (rate.equals("") || rate.length() == 0) {
            toast.setText("Please Enter Rate");
            view = ed_rate;
            check = false;
        }
        if (cat3.equals("") || cat3.length() == 0) {
            view = ed_cat3;
            toast.setText("Please Enter Cat2");
            check = false;
        }
        if (cat2.equals("") || cat2.length() == 0) {
            view = auto_cat2;
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

    private void addProduct() {
        String cat1 = auto_cat1.getText().toString();
        String cat2 = auto_cat2.getText().toString();
        String cat3 = ed_cat3.getText().toString();
        float rate = Float.parseFloat(ed_rate.getText().toString());
        float ssp = Float.parseFloat(ed_ssp.getText().toString());
        int qty = Integer.parseInt(ed_qty.getText().toString());
        String gstType;
        if (rdo_gstInclude.isChecked()) {
            gstType = "I";
        } else {
            gstType = "E";
        }
        int prodMax = db.getMaxPMId();
        ProductClass productClass = new ProductClass();
        productClass.setProduct_ID(prodMax);
        productClass.setCat1(cat1);
        productClass.setCat2(cat2);
        productClass.setCat3(cat3);
        productClass.setFinalProduct(cat1 + "-" + cat2 + "-" + cat3);
        productClass.setPprice(rate);
        productClass.setProduct_Barcode(String.valueOf(prodMax));
        productClass.setIsActive("Y");
        productClass.setHsnCode(String.valueOf(prodMax));
        productClass.setSsp(ssp);
        productClass.setGstGroup(gstList.get(sp_gstGroup.getSelectedItemPosition()));
        productClass.setGstType(gstType);
        productClass.setStockQty(qty);
        db.addProduct(productClass);
        toast.setText("Added Successfully");
        toast.show();
        clearField();
    }

    private void clearField(){
        aSwitch.setChecked(true);
        auto_cat1.setText(null);
        auto_cat2.setText(null);
        ed_cat3.setText(null);
        ed_rate.setText(null);
        ed_ssp.setText(null);
        ed_qty.setText(null);
        auto_cat1.requestFocus();
        sp_gstGroup.setSelection(0);
    }

    private void doFinish(){
        toast.cancel();
        finish();
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
        int a = db.getExpMaxAuto();
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
        //db.addGSTDetail(gstD);
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
                    //prepareExpandableList();
                    dialogInterface.dismiss();
                }
            });
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    flag = 1;
                    addLayout.setVisibility(View.VISIBLE);
                    setCategory();
                    //prepareExpandableList();
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    flag = 2;
                    addLayout.setVisibility(View.GONE);
                    //prepareExpandableList();
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
