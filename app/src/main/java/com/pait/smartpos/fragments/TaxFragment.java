package com.pait.smartpos.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pait.smartpos.AddNewTaxActivity;
import com.pait.smartpos.Change_TaxActivity;
import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaxFragment extends Fragment {

    ListView tax_listView;
    List<String> taxList;
    List<Integer> taxIdList;
    HashMap<Integer,String> taxIdInfoMap;
    DBHandlerR db;
    public static int isUpdated = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_tax_master,null);
        init(view);

        setTax();

        tax_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(),Change_TaxActivity.class);
                String str = taxIdInfoMap.get(taxIdList.get(i));
                str = str +"^"+ taxIdList.get(i);
                intent.putExtra("str",str);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter,R.anim.exit);
                Log.d("Log",str);
            }
        });

        return view;
    }
    
    void init(View view){
        tax_listView = (ListView) view.findViewById(R.id.tax_listView);
        db = new DBHandlerR(getContext());
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
        tax_listView.setAdapter(new ArrayAdapter<>(getContext(),R.layout.list_item_option_setttings,taxList));
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        if(isUpdated==1){
            setTax();
            isUpdated = 0;
        }
    }*/

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tax_master_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                doFinish();
                break;
            case R.id.addTax:
                Intent intent = new Intent(getContext(),AddNewTaxActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter,R.anim.exit);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onBackPressed() {
        doFinish();
    }*/

    void doFinish(){
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }
}
