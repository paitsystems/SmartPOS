package com.pait.smartpos.fragments;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.pait.smartpos.R;
import com.pait.smartpos.adpaters.UpdatedBillReportAdapter;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandlerR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UpdateBillReportFragment extends Fragment implements View.OnClickListener{

    private Constant constant;
    private DBHandlerR db;
    private Toast toast;
    private ExpandableListView expandableListView;
    private List<String> parentList;
    private List<List<String>> childList;
    private HashMap<String,List<List<String>>> childMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gst_view_fragment, container, false);
        init(view);
        setList();
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_otp:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                new Constant(getActivity()).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setList(){
        parentList.clear();
        childList.clear();
        childMap.clear();
        Cursor res = db.getUpdateBillData("","","A");
        if(res.moveToFirst()){
            do {
                List<String> list = new ArrayList<>();
                String billNo = res.getString(res.getColumnIndex(DBHandlerR.UB_BillNo));
                String oldQty = res.getString(res.getColumnIndex(DBHandlerR.UB_OldQty));
                String newQty = res.getString(res.getColumnIndex(DBHandlerR.UB_NewQty));
                String oldRate = res.getString(res.getColumnIndex(DBHandlerR.UB_OldRate));
                String newRate = res.getString(res.getColumnIndex(DBHandlerR.UB_NewRate));
                int prodId = res.getInt(res.getColumnIndex(DBHandlerR.UB_ProdId));
                String prodName = db.getProductNameFromId(prodId);
                list.add(oldQty);list.add(newQty);
                list.add(oldRate);list.add(newRate);
                list.add(prodName);

                if(!parentList.contains(billNo)) {
                    parentList.add(billNo);
                }
                if(childMap.containsKey(billNo)){
                    List<List<String>> childList = childMap.get(billNo);
                    childList.add(list);
                    childMap.put(billNo,childList);
                }else{
                    List<List<String>> childList = new ArrayList<>();
                    childList.add(list);
                    childMap.put(billNo,childList);
                }
            }while (res.moveToNext());
        }
        res.close();
        expandableListView.setAdapter(new UpdatedBillReportAdapter(getContext(),parentList,childMap));
    }

    private void init(View view){
        constant = new Constant(getActivity());
        db = new DBHandlerR(getContext());
        toast = Toast.makeText(getContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        parentList = new ArrayList<>();
        childMap = new HashMap<>();
        childList = new ArrayList<>();
        expandableListView = view.findViewById(R.id.expandableListView);
    }

    private void showDia(int a){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        if (a == 0) {
            builder.setMessage("Do You Want To Exit App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
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

}
