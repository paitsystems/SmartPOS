package com.pait.smartpos.adpaters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.pait.smartpos.R;
import com.pait.smartpos.model.DailyPettyExpClass;
import com.pait.smartpos.model.ProductClass;

import java.util.List;

public class ProductwiseStockAdapter extends BaseAdapter{
    List<ProductClass> list;
    Context context;


    public ProductwiseStockAdapter(List<ProductClass> list, Context _context){
        this.context = _context;
        this.list  = list;
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

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.productwise_stock_item,null);
            holder = new ViewHolder();
            holder.tv_qty = view.findViewById(R.id.tv_qty);
            holder.tv_cat3 = view.findViewById(R.id.tv_cat3);
            holder.tv_finalprod = view.findViewById(R.id.tv_finalprod);
            holder.tv_ssp = view.findViewById(R.id.tv_ssp);
            holder.tv_wprice = view.findViewById(R.id.tv_wprice);
            holder.tv_mrp = view.findViewById(R.id.tv_mrp);
            holder.tv_price = view.findViewById(R.id.tv_price);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        ProductClass detail = (ProductClass) getItem(i);
        holder.tv_qty.setText(String.valueOf(detail.getStockQty()));
        holder.tv_cat3.setText(detail.getCat3());
        holder.tv_finalprod.setText(detail.getFinalProduct());
        holder.tv_mrp.setText(String.valueOf(detail.getMrp()));
        holder.tv_price.setText(String.valueOf(detail.getPprice()));
        holder.tv_wprice.setText(String.valueOf(detail.getWprice()));
        holder.tv_ssp.setText(String.valueOf(detail.getSsp()));

        return view;
    }

    private class ViewHolder{
        TextView tv_qty,tv_cat3,tv_finalprod,tv_ssp,tv_wprice,tv_mrp,tv_price;
    }
}

