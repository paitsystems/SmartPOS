package com.pait.smartpos;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pait.smartpos.db.DBHandlerR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaxMasterActivity extends AppCompatActivity {

    ListView tax_listView;
    List<String> taxList;
    List<Integer> taxIdList;
    HashMap<Integer,String> taxIdInfoMap;
    DBHandlerR db;
    static int isUpdated = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tax_master);
        
        init();

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Tax Master");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTax();

        tax_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),Change_TaxActivity.class);
                String str = taxIdInfoMap.get(taxIdList.get(i));
                str = str +"^"+ taxIdList.get(i);
                intent.putExtra("str",str);
                startActivity(intent);
                overridePendingTransition(R.anim.enter,R.anim.exit);
                Log.d("Log",str);
            }
        });
    }
    
    void init(){
        tax_listView = (ListView) findViewById(R.id.tax_listView);
        db = new DBHandlerR(getApplicationContext());
        taxList = new ArrayList<>();
        taxIdList = new ArrayList<>();
        taxIdInfoMap = new HashMap<>();
    }

    void setTax(){
        taxIdList.clear();
        taxIdInfoMap.clear();
        taxList.clear();
        tax_listView.setAdapter(null);
        Cursor res = db.getTax();
        if(res.moveToFirst()){
            do{
                taxIdList.add(res.getInt(0));
                taxList.add(res.getString(1));
                String str = res.getString(1) + "^" + res.getString(2)+ "^" + res.getString(3);
                taxIdInfoMap.put(res.getInt(0),str);
            }while (res.moveToNext());
        }
        res.close();
        tax_listView.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.list_item_option_setttings,taxList));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isUpdated==1){
            setTax();
            isUpdated = 0;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tax_master_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                doFinish();
                break;
            case R.id.addTax:
                Intent intent = new Intent(getApplicationContext(),AddNewTaxActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter,R.anim.exit);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        doFinish();
    }

    void doFinish(){
        finish();
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }
}
