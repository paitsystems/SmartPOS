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

import com.pait.smartpos.adpaters.CustomSpinnerAdapter;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.model.CustomSpinnerClass;

import java.util.ArrayList;
import java.util.List;

public class InwardActivity extends AppCompatActivity {

    private AutoCompleteTextView auto_supplier;
    private Spinner spinner;
    private List<String> prodList;
    private List<String> suppList;
    private  ArrayList<CustomSpinnerClass> listVOs;
    private DBHandler db;
    private Button btn_save;
    private EditText ed_qty,ed_rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inward);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setTitle();
        }

        init();
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
        CustomSpinnerAdapter myAdapter = new CustomSpinnerAdapter(InwardActivity.this, 0,listVOs);
        spinner.setAdapter(myAdapter);
    }
}
