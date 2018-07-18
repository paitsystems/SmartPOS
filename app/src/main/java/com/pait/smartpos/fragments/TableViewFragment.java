package com.pait.smartpos.fragments;

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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.pait.smartpos.R;
import com.pait.smartpos.AddTableMasterActivity;
import com.pait.smartpos.UpdateTableGSTActivity;
import com.pait.smartpos.UpdateTableNameGSTActivity;
import com.pait.smartpos.adpaters.TableNameListAdapter;
import com.pait.smartpos.db.DBHandlerR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TableViewFragment extends Fragment {

    private ListView listView;
    private DBHandlerR db;
    private List<String> tableList;
    private List<Integer> tableIDList;
    private HashMap<Integer,String> tableIDNameMap;
    public static int isUpdated = 0;
    private Toast toast;
    private String tableChangeStr = "";
    private int isListClickable = 0, isNameGSTupdate = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = new DBHandlerR(getContext());
        tableList = new ArrayList<>();
        tableIDList = new ArrayList<>();
        tableIDNameMap = new HashMap<>();
        toast = Toast.makeText(getContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        View view = inflater.inflate(R.layout.fragment_table_view, container, false);
        listView = (ListView) view.findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(isListClickable == 1){
                    tableChangeStr = tableIDNameMap.get(tableIDList.get(i))+"^"+tableIDList.get(i);
                    if(isNameGSTupdate == 1) {
                        showDia(1, tableList.get(i));
                    }else{
                        showDia(2, tableList.get(i));
                    }
                }
            }
        });
        setTableList();
        return view;
    }

    private void setTableList(){
        tableList.clear();
        tableIDList.clear();
        tableIDNameMap.clear();
        listView.setAdapter(null);
        Cursor res = db.getAllTable();
        if(res.moveToFirst()){
            do{
                tableList.add(res.getString(1));
                tableIDList.add(res.getInt(0));
                tableIDNameMap.put(res.getInt(0),res.getString(1)+"^"+res.getString(2)+"^"+res.getString(7));
            }while (res.moveToNext());
        }
        res.close();
        listView.setAdapter(new TableNameListAdapter(getContext(),tableList));
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isUpdated == 1){
            isUpdated = 0;
            setTableList();
        }
    }

    public void addTable(){
        Intent intent = new Intent(getContext(), AddTableMasterActivity.class);
        intent.putExtra("from","FromSettingsOptions");
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    public void updateTable(){
        isListClickable = 1;
        isNameGSTupdate = 1;
        setTableList();
        toast.setText("Please Select Table To Update Table Name");
        toast.show();
    }

    public void updateTableGST(){
        isListClickable = 1;
        isNameGSTupdate = 2;
        setTableList();
        toast.setText("Please Select Table To Update GST");
        toast.show();
    }

    public void viewTable(){
        isListClickable = 0;
        setTableList();
    }

    private void showDia(int i, final String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        if(i==1) {
            builder.setMessage("Do You Want To Update Name "+str);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getContext(),UpdateTableNameGSTActivity.class);
                    intent.putExtra("str",tableChangeStr);
                    intent.putExtra("name","name");
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.enter,R.anim.exit);
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
            builder.setMessage("Do You Want To Update GST Of "+str);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getContext(),UpdateTableGSTActivity.class);
                    intent.putExtra("str",tableChangeStr);
                    intent.putExtra("name","gst");
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.enter,R.anim.exit);
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
