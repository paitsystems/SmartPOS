package com.pait.smartpos;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.Gravity;

import com.pait.smartpos.adpaters.MasterUpdationRecyclerAdapter;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.interfaces.OnItemClickListenerCustom;
import com.pait.smartpos.log.WriteLog;
import com.pait.smartpos.model.MasterUpdationClass;

import java.util.ArrayList;
import java.util.List;

public class MasterUpdationActivity extends AppCompatActivity
        implements View.OnClickListener,RecyclerItemTouchHelper.RecyclerItemTouchHelperListener,OnItemClickListenerCustom {

    private Constant constant, constant1;
    private Toast toast;
    private List<MasterUpdationClass> list;
    private DBHandlerR db;
    private RecyclerView recyclerView;
    private MasterUpdationRecyclerAdapter adapter;
    private LinearLayout lay;
    private String masterType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_updation);

        init();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        masterType = getIntent().getExtras().getString("master");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case 0:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(masterType.equals("product")){
            setProductData();
        }else if(masterType.equals("table")){
            setTableData();
        }else if(masterType.equals("category")){
            setCategoryData();
        }

    }

    @Override
    public void onBackPressed() {
        new Constant(MasterUpdationActivity.this).doFinish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.masterupdate_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                new Constant(MasterUpdationActivity.this).doFinish();
                break;
            case R.id.add:
                Intent intent = null;
                if(masterType.equals("product")) {
                    intent = new Intent(getApplicationContext(),AddProductMasterActivity.class);
                    intent.putExtra("from","FromSettingsOptions");
                }else if(masterType.equals("table")) {
                    intent = new Intent(getApplicationContext(),AddTableMasterActivity.class);
                    intent.putExtra("from","FromSettingsOptions");
                }else if(masterType.equals("category")) {
                    intent = new Intent(getApplicationContext(),AddCategoryMasterActivity.class);
                }
                startActivity(intent);
                overridePendingTransition(R.anim.enter,R.anim.exit);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        constant = new Constant(MasterUpdationActivity.this);
        constant1 = new Constant(getApplicationContext());
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        db = new DBHandlerR(getApplicationContext());
        list = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        lay = (LinearLayout) findViewById(R.id.layout);
    }

    private void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MasterUpdationActivity.this);
        builder.setCancelable(false);
        if (a == 0) {
            builder.setMessage("Do You Want To Exit App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Constant(MasterUpdationActivity.this).doFinish();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        builder.create().show();
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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MasterUpdationRecyclerAdapter(getApplicationContext(),list,this);
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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MasterUpdationRecyclerAdapter(getApplicationContext(),list,this);
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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MasterUpdationRecyclerAdapter(getApplicationContext(),list,this);
        recyclerView.setAdapter(adapter);
    }

    private void writeLog(String _data) {
        new WriteLog().writeLog(getApplicationContext(), "MasterUpdationActivity_" + _data);
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
                intent = new Intent(getApplicationContext(),UpdateProductMasterActivity.class);
            }else if(masterType.equals("table")) {
                intent = new Intent(getApplicationContext(),UpdateTableMasterActivity.class);
            }else if(masterType.equals("category")) {
                intent = new Intent(getApplicationContext(),UpdateCategoryActivity.class);
            }
            intent.putExtra("obj",master);
            startActivity(intent);
            overridePendingTransition(R.anim.enter,R.anim.exit);
        }
    }

}
