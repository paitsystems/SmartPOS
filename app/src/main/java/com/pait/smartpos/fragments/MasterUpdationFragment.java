package com.pait.smartpos.fragments;

//Created by ANUP on 04-06-2018.

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pait.smartpos.R;
import com.pait.smartpos.RecyclerItemTouchHelper;
import com.pait.smartpos.UpdateCategoryActivity;
import com.pait.smartpos.UpdateProductMasterActivity;
import com.pait.smartpos.UpdateTableMasterActivity;
import com.pait.smartpos.adpaters.MasterUpdationRecyclerAdapter;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.interfaces.OnItemClickListener;
import com.pait.smartpos.log.WriteLog;
import com.pait.smartpos.model.MasterUpdationClass;

import java.util.ArrayList;
import java.util.List;

public class MasterUpdationFragment extends Fragment
        implements View.OnClickListener,RecyclerItemTouchHelper.RecyclerItemTouchHelperListener,OnItemClickListener {

    private Constant constant, constant1;
    private Toast toast;
    private List<MasterUpdationClass> list;
    private DBHandlerR db;
    private RecyclerView recyclerView;
    private MasterUpdationRecyclerAdapter adapter;
    private LinearLayout lay;
    private String masterType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_master_updation,null);

        init(view);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        
        Bundle bundle = getArguments();
        masterType = bundle.getString("master");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (masterType.equals("product")) {
            setProductData();
        } else if (masterType.equals("table")) {
            setTableData();
        } else if (masterType.equals("category")) {
            setCategoryData();
        }
    }

    @Override
    public void onItemClick(MasterUpdationClass master) {
        Constant.showLog("Item Clicked");
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof MasterUpdationRecyclerAdapter.MyViewHolder) {
            final int[] flag = {0};
            String name = list.get(viewHolder.getAdapterPosition()).getMasterName();
            final MasterUpdationClass master = list.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();
            adapter.removeItem(viewHolder.getAdapterPosition());
            Snackbar snackbar = Snackbar.make(lay, name + " removed from list!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    flag[0] = 1;
                    adapter.restoreItem(master, deletedIndex);
                }
            });
            snackbar.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {

                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    if(flag[0] !=1) {
                        if (masterType.equals("product")) {
                            db.inActivateItem(masterType, DBHandlerR.Product_Table, master.getMasterAuto(), master.getMasterName());
                        } else if (masterType.equals("table")) {
                            db.inActivateItem(masterType, DBHandlerR.Table_Table, master.getMasterAuto(), master.getMasterName());
                        } else if (masterType.equals("category")) {
                            db.inActivateItem(masterType, DBHandlerR.Category_Table, master.getMasterAuto(), master.getMasterName());
                        }
                        master.setIsMasterActive("N");
                        adapter.restoreItem(master, deletedIndex);
                        Constant.showLog("Item Deleted");
                    }
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public void startNewActivty(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        Constant.showLog("Update Activity");
        if (viewHolder instanceof MasterUpdationRecyclerAdapter.MyViewHolder) {
            Intent intent = null;
            MasterUpdationClass master = list.get(position);
            if(masterType.equals("product")) {
                String cat = db.getCatgoryName(master.getMasterCatId());
                master.setMasterCat(cat);
                intent = new Intent(getContext(),UpdateProductMasterActivity.class);
            }else if(masterType.equals("table")) {
                intent = new Intent(getContext(),UpdateTableMasterActivity.class);
            }else if(masterType.equals("category")) {
                intent = new Intent(getContext(),UpdateCategoryActivity.class);
            }
            intent.putExtra("obj",master);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.enter,R.anim.exit);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case 0:
                break;
        }
    }

    private void init(View view){
        db = new DBHandlerR(getContext());
        list = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view);
        lay = view.findViewById(R.id.layout);
    }

    private void setProductData(){
        list.clear();
        recyclerView.setAdapter(null);
        Cursor res = db.getAllProduct();
        if(res.moveToFirst()){
            do{
                MasterUpdationClass master = new MasterUpdationClass();
                master.setMasterAuto(res.getInt(res.getColumnIndex(DBHandlerR.Product_ID)));
                master.setMasterName(res.getString(res.getColumnIndex(DBHandlerR.Product_Name)));
                master.setIsMasterActive(res.getString(res.getColumnIndex(DBHandlerR.Product_Active)));
                master.setMasterRate(res.getString(res.getColumnIndex(DBHandlerR.Product_Rate)));
                master.setMasterCatId(res.getInt(res.getColumnIndex(DBHandlerR.Product_Cat)));
                master.setMasterGSTGroup(res.getString(res.getColumnIndex(DBHandlerR.Product_GSTGroup)));
                master.setMasterTaxType(res.getString(res.getColumnIndex(DBHandlerR.Product_TaxTyp)));
                master.setMasterType("P");
                list.add(master);
            }while (res.moveToNext());
        }
        res.close();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MasterUpdationRecyclerAdapter(getContext(),list,this);
        recyclerView.setAdapter(adapter);
    }

    private void setTableData(){
        list.clear();
        recyclerView.setAdapter(null);
        Cursor res = db.getAllTable();
        if(res.moveToFirst()){
            do{
                MasterUpdationClass master = new MasterUpdationClass();
                master.setMasterAuto(res.getInt(res.getColumnIndex(DBHandlerR.Table_ID)));
                master.setMasterName(res.getString(res.getColumnIndex(DBHandlerR.Table_Table)));
                master.setIsMasterActive(res.getString(res.getColumnIndex(DBHandlerR.Table_Active)));
                master.setMasterType("T");
                list.add(master);
            }while (res.moveToNext());
        }
        res.close();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MasterUpdationRecyclerAdapter(getContext(),list,this);
        recyclerView.setAdapter(adapter);
    }

    private void setCategoryData(){
        list.clear();
        recyclerView.setAdapter(null);
        Cursor res = db.getAllCatgory();
        if(res.moveToFirst()){
            do{
                MasterUpdationClass master = new MasterUpdationClass();
                master.setMasterAuto(res.getInt(res.getColumnIndex(DBHandlerR.Category_ID)));
                master.setMasterName(res.getString(res.getColumnIndex(DBHandlerR.Category_Cat)));
                master.setIsMasterActive(res.getString(res.getColumnIndex(DBHandlerR.Category_Active)));
                master.setMasterType("C");
                list.add(master);
            }while (res.moveToNext());
        }
        res.close();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MasterUpdationRecyclerAdapter(getContext(),list,this);
        recyclerView.setAdapter(adapter);
    }

    private void writeLog(String _data) {
        new WriteLog().writeLog(getContext(), "MasterUpdationActivity_" + _data);
    }
}
