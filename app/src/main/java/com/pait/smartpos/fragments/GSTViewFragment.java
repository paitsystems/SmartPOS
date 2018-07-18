package com.pait.smartpos.fragments;

//Created by lnb on 8/4/2017.

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pait.smartpos.AddGSTActivity;
import com.pait.smartpos.R;
import com.pait.smartpos.UpdateGSTDetailActivity;
import com.pait.smartpos.adpaters.GSTExpandableListAdapter;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.model.GSTDetailClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GSTViewFragment extends Fragment{

    private ExpandableListView listView;
    private LinearLayout addLayout;
    private DBHandlerR db;
    private Toast toast;
    private ArrayList<String> gstGroupList,gstGroupList1;
    private List<Integer> gstMastAutoList, gstDetAutoList;
    public static int isUpdated = 0;
    private int isListClickable = 0;
    /*
    {
        1-[
        [0,0,0,0,0,0,0,0]
        ],
        2-[
        [1,1000,5,2.5,2.5,50,50,0],
        [1001,2000,5,2.5,2.5,50,50,0],
        [2001,5000,12,6,6,50,50,0]
        ],
        3-[
        [1,1000,18,9,9,50,50,0]
        ]
    }*/

    private HashMap<Integer,List<List<GSTDetailClass>>> mastDetMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gst_view_fragment, container, false);

        init(view);

        setGSTList();

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view,
                                        int groupPosition, int childPosition, long l) {
                if(isListClickable==1) {
                    List<List<GSTDetailClass>> list = mastDetMap.get(gstMastAutoList.get(groupPosition));
                    List<GSTDetailClass> detList = list.get(childPosition);
                    GSTDetailClass det = detList.get(0);
                    String str = det.getMastAuto() + "-" + det.getAuto();
                    Constant.showLog(str);
                    showDia(1,str);
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isUpdated==1){
            setGSTList();
            isUpdated = 0;
        }
    }

    private void init(View view){
        addLayout = (LinearLayout) view.findViewById(R.id.add_layout);
        listView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        db = new DBHandlerR(getContext());
        gstGroupList = new ArrayList<>();
        gstGroupList1 = new ArrayList<>();
        toast = Toast.makeText(getContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        gstMastAutoList = new ArrayList<>();
        gstDetAutoList = new ArrayList<>();
        mastDetMap = new HashMap<>();
    }

    private void setGSTList(){
        gstGroupList1.clear();
        gstMastAutoList.clear();
        gstDetAutoList.clear();
        mastDetMap.clear();
        Cursor res = db.getGSTMastDet();
        if(res.moveToFirst()){
            List<List<GSTDetailClass>> detlistList = null;
            do{
                if(gstDetAutoList.isEmpty()){
                    gstDetAutoList.add(res.getInt(0));
                }else{
                    if(!gstDetAutoList.contains(res.getInt(0))){
                        gstDetAutoList.add(res.getInt(0));
                    }
                }
                if(gstMastAutoList.isEmpty()){
                    gstMastAutoList.add(res.getInt(1));
                    gstGroupList1.add(res.getString(10));
                    detlistList = new ArrayList<>();
                }else{
                    if(!gstMastAutoList.contains(res.getInt(1))){
                        gstMastAutoList.add(res.getInt(1));
                        gstGroupList1.add(res.getString(10));
                        detlistList = new ArrayList<>();
                    }
                }
                List<GSTDetailClass> detList = new ArrayList<>();
                GSTDetailClass detailClass = new GSTDetailClass();
                detailClass.setAuto(res.getInt(0));
                detailClass.setMastAuto(res.getInt(1));
                detailClass.setFromRange(res.getFloat(2));
                detailClass.setToRange(res.getFloat(3));
                detailClass.setGstPer(res.getFloat(4));
                detailClass.setCgstPer(res.getFloat(5));
                detailClass.setSgstPer(res.getFloat(6));
                detailClass.setCgstShare(res.getFloat(7));
                detailClass.setSgstShare(res.getFloat(8));
                detailClass.setCessPer(res.getFloat(9));
                detList.add(detailClass);
                detlistList.add(detList);
                mastDetMap.put(res.getInt(1),detlistList);
            }while (res.moveToNext());
        }
        res.close();
        listView.setAdapter(new GSTExpandableListAdapter(getContext(),mastDetMap,gstMastAutoList,gstDetAutoList,gstGroupList1));
    }

    public void addGST(){
        isListClickable = 0;
        Intent intent = new Intent(getContext(), AddGSTActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.enter,R.anim.exit);
    }

    public void updateGST(){
        isListClickable = 1;
        setGSTList();
        toast.setText("Please Select GST Detail To");
        toast.show();
    }

    public void viewGST(){
        isListClickable = 0;
        setGSTList();
    }

    private void showDia(int i, final String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        if(i==1) {
            builder.setMessage("Do You Want To Update This GST Group?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getContext(), UpdateGSTDetailActivity.class);
                    intent.putExtra("str", str);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }
        builder.create().show();
    }


}
