package com.pait.smartpos.adpaters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.pait.smartpos.R;
import com.pait.smartpos.model.ProductClass;

import java.util.List;

public class AllProductAdapterP1 extends BaseAdapter {

    private Context context;
    private List<ProductClass> prodList;

    public AllProductAdapterP1(Context _context, List<ProductClass> _prodList){
        this.context = _context;
        this.prodList = _prodList;
    }

    @Override
    public int getCount() {
        return prodList.size();
    }

    @Override
    public Object getItem(int i) {
        return prodList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(view==null) {
            view = inflater.inflate(R.layout.list_item_all_product, null);
            holder = new ViewHolder();
            holder.btn_allProd = view.findViewById(R.id.btn_allProd);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        ProductClass prod = prodList.get(i);
        holder.btn_allProd.setText(prod.getProduct_Name());
        if(prod.isSelected()){
            holder.btn_allProd.setTextColor(context.getResources().getColor(R.color.red));
        }else{
            holder.btn_allProd.setTextColor(context.getResources().getColor(R.color.black));
        }
        return view;
    }

    private class ViewHolder{
        Button btn_allProd;
    }
}