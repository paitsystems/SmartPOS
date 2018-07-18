package com.pait.smartpos.adpaters;// Created by anup on 5/24/2017.

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pait.smartpos.model.ItemwiseSaleClass;
import com.pait.smartpos.R;

import java.util.List;

public class ItemwiseSaleAdapter extends BaseAdapter {

    Context context;
    List<ItemwiseSaleClass> list;

    public ItemwiseSaleAdapter(Context _context, List<ItemwiseSaleClass> _list){
        this.context = _context;
        this.list = _list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_repo_itemwise_sale, null);
            holder.tv_cat_name = (TextView) view.findViewById(R.id.tv_cat_name);
            holder.tv_prod_name = (TextView) view.findViewById(R.id.tv_prod_name);
            holder.tv_qty = (TextView) view.findViewById(R.id.tv_qty);
            holder.tv_total = (TextView) view.findViewById(R.id.tv_total);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        ItemwiseSaleClass sale = (ItemwiseSaleClass) getItem(i);
        holder.tv_cat_name.setText(sale.getCatName());
        holder.tv_prod_name.setText(sale.getProdName());
        holder.tv_qty.setText(sale.getQty());
        holder.tv_total.setText(sale.getTotal());
        return view;
    }

    private class ViewHolder{
        TextView tv_cat_name, tv_prod_name,tv_qty,tv_total;
    }
}
