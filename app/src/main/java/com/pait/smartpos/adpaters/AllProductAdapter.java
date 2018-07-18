package com.pait.smartpos.adpaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pait.smartpos.R;
import com.pait.smartpos.model.ProductClass;

import java.util.List;

public class AllProductAdapter extends BaseAdapter {

    private Context context;
    private List<ProductClass> prodList;

    public AllProductAdapter(Context _context, List<ProductClass> _prodList){
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
            holder.tv_allPord = view.findViewById(R.id.tv_allPord);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        ProductClass prod = prodList.get(i);
        holder.tv_allPord.setText(prod.getProduct_Name());
        if(prod.isSelected()){
            holder.tv_allPord.setTextColor(context.getResources().getColor(R.color.red));
        }else{
            holder.tv_allPord.setTextColor(context.getResources().getColor(R.color.black));
        }
        return view;
    }

    private class ViewHolder{
        TextView tv_allPord;
    }
}
