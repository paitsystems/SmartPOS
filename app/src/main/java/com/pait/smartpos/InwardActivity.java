package com.pait.smartpos;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.pait.smartpos.adpaters.CheckableSpinnerAdapter;
import com.pait.smartpos.adpaters.CustomSpinnerAdapter;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.interfaces.checkBoxListener;
import com.pait.smartpos.model.CustomSpinnerClass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InwardActivity extends AppCompatActivity implements checkBoxListener{

    private AutoCompleteTextView auto_supplier;
    private Spinner spinner;
    private List<String> prodList;
    private List<String> suppList;
    private  ArrayList<CustomSpinnerClass> listVOs;
    private DBHandler db;
    private Button btn_save;
    private EditText ed_qty,ed_rate;
    private List<Integer> posLs;
    private CustomSpinnerAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inward);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setTitle();
        }
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSupplier();
        setSpinnerValue();
    }

    private void init(){
        auto_supplier = findViewById(R.id.auto_supplier);
        spinner = findViewById(R.id.sp_prod);
        prodList = new ArrayList<>();
        suppList = new ArrayList<>();
        listVOs = new ArrayList<>();
        db = new DBHandler(InwardActivity.this);
        btn_save = findViewById(R.id.btn_save);
        ed_qty = findViewById(R.id.ed_qty);
        ed_rate = findViewById(R.id.ed_rate);
        posLs = new ArrayList<>();


        auto_supplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auto_supplier.clearListSelection();
                auto_supplier.setThreshold(0);
                auto_supplier.showDropDown();
            }
        });
    }

    private void setSupplier(){
        /*Cursor res = db.getDistinctSupplier();
        if(res != null){
            if (res.moveToFirst()){
                do{
                    suppList.add(res.getString(res.getColumnIndex(DBHandler.SM_Name)));
                }while (res.moveToNext());
            }
        }
        res.close();*/   //todo uncomment for live data
        auto_supplier.setAdapter(null);
        suppList.add("GANESH");
        suppList.add("Digvijay");
        suppList.add("Prashant");
        suppList.add("Pooja");
        suppList.add("Rohini");
        suppList.add("Ankita");
        suppList.add("Aditya");
        suppList.add("Kedar");
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),R.layout.adapter_item,suppList);
        auto_supplier.setAdapter(adapter);
    }

    private void setSpinnerValue(){
       final List<CheckableSpinnerAdapter.SpinnerItem<CustomSpinnerClass>> spinner_items = new ArrayList<>();
       final Set<CustomSpinnerClass> selected_items = new HashSet<>();
       prodList.clear();
       listVOs.clear();

        //prodList.add("Select Product");
        Cursor res = db.getDistinctProduct();
        if(res != null){
            if (res.moveToFirst()){
                do{
                    prodList.add(res.getString(res.getColumnIndex(DBHandler.PM_Cat3)));
                }while (res.moveToNext());
            }
        }
        res.close();

        for (String product : prodList) {
            CustomSpinnerClass stateVO = new CustomSpinnerClass();
            stateVO.setTitle(product);
            stateVO.setSelected(false);
            listVOs.add(stateVO);
        }

        //List<CustomSpinnerClass> all_objects = getMyObjects();
        for(CustomSpinnerClass o : listVOs) {
            spinner_items.add(new CheckableSpinnerAdapter.SpinnerItem<CustomSpinnerClass>(o, o.getTitle()));
        }

        String headerText = "Select Product";
        CheckableSpinnerAdapter adapter = new CheckableSpinnerAdapter<CustomSpinnerClass>(this, headerText, spinner_items, selected_items);
        spinner.setAdapter(adapter);

    }

    private void setSpinnerValue1(){

        prodList.clear();
        listVOs.clear();

        prodList.add("Select Product");
        Cursor res = db.getDistinctProduct();
        if(res != null){
            if (res.moveToFirst()){
                do{
                    prodList.add(res.getString(res.getColumnIndex(DBHandler.PM_Cat3)));
                }while (res.moveToNext());
            }
        }
        res.close();

        for (String product : prodList) {
            CustomSpinnerClass stateVO = new CustomSpinnerClass();
            stateVO.setTitle(product);
            stateVO.setSelected(false);
            listVOs.add(stateVO);
        }

        myAdapter = new CustomSpinnerAdapter(InwardActivity.this, 0,listVOs,posLs);
        spinner.setAdapter(myAdapter);
    }

    @Override
    public List<Integer> setPosition(List<Integer> ls) {
        posLs = ls;
        //myAdapter.notifyDataSetChanged();
        Constant.showLog("ls.size"+posLs.size());
        return ls;
    }

    /*public List<Integer> setPosition( List<Integer> ls){
        Constant.showLog("ls.size"+ls.size());
      //posLs.clear();
        posLs = ls;
        return ls;
    }*/
}
