package com.pait.smartpos.adpaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pait.smartpos.CashMemoActivityR;
import com.pait.smartpos.R;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.interfaces.UpdateValue;

import java.util.HashMap;
import java.util.List;

// Created by Android on 10/21/2016.

public class CMOrderItemListAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater inflater;
    private HashMap<Integer, List<Integer>> order_hash_map;
    private HashMap<Integer, List<String>> order_prod_hash_map;
    private Integer tableid;
    private List<Integer> list;
    private UpdateValue updateValue;

    public CMOrderItemListAdapter(Context _context, HashMap<Integer, List<Integer>> _order_hash_map, HashMap<Integer, List<String>> _order_prod_hash_map, Integer _tableid){
        context = _context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        order_hash_map = _order_hash_map;
        order_prod_hash_map = _order_prod_hash_map;
        tableid = _tableid;
        list = order_hash_map.get(tableid);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Integer getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void initInterface(UpdateValue _updateValue){
        this.updateValue = _updateValue;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = new ViewHolder();
        if(view == null){
            view = inflater.inflate(R.layout.list_item_cust_order,null);
            holder.tv_item = view.findViewById(R.id.tv_item);
            holder.tv_qty = view.findViewById(R.id.tv_qty);
            holder.tv_amt = view.findViewById(R.id.tv_amt);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        List<String> list1 = order_prod_hash_map.get(getItem(i));
        holder.tv_item.setText(list1.get(0));
        holder.tv_qty.setText(list1.get(1));
        holder.tv_amt.setText(list1.get(3));
        CashMemoActivityR.totalqtyFl = CashMemoActivityR.totalqtyFl + Float.parseFloat(list1.get(1));
        CashMemoActivityR.totalamtFl = CashMemoActivityR.totalamtFl + Float.parseFloat(list1.get(3));
        Constant.showLog(CashMemoActivityR.totalqtyFl+"-"+ CashMemoActivityR.totalamtFl);
        Constant.showLog("LOG");
        if(i==list.size()-1){
            updateValue.updateValue();
        }
        return view;
    }

    private class ViewHolder{
        TextView tv_item,tv_qty,tv_amt;
    }
}
