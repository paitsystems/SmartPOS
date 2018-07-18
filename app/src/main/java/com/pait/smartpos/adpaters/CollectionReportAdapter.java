package com.pait.smartpos.adpaters;// Created by anup on 5/22/2017.

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pait.smartpos.model.CollectionClass;
import com.pait.smartpos.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CollectionReportAdapter extends BaseAdapter {

    private Context context;
    private List<CollectionClass> list;
    private SimpleDateFormat sdf,sdf1;

    public CollectionReportAdapter(Context _context, List<CollectionClass> _list){
        this.context = _context;
        this.list = _list;
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        sdf1 = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
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
        if(view==null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_repo_collection,null);
            holder.tv_billNo = (TextView) view.findViewById(R.id.tv_billNo);
            holder.tv_billDate = (TextView) view.findViewById(R.id.tv_billDate);
            holder.tv_billTime = (TextView) view.findViewById(R.id.tv_billTime);
            holder.tv_emp = (TextView) view.findViewById(R.id.tv_emp);
            holder.tv_totSale = (TextView) view.findViewById(R.id.tv_totSale);
            holder.tv_netAmnt = (TextView) view.findViewById(R.id.tv_netAmnt);
            holder.tv_cashAmnt = (TextView) view.findViewById(R.id.tv_cashAmnt);
            holder.tv_cardAmnt = (TextView) view.findViewById(R.id.tv_cardAmnt);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        CollectionClass collection = (CollectionClass) getItem(i);
        holder.tv_billNo.setText(collection.getBillNo());
        try {
            Date d = sdf.parse(collection.getBillDate());
            String dstr = sdf1.format(d);
            holder.tv_billDate.setText(dstr);
            holder.tv_billTime.setText(collection.getBilltime());
            holder.tv_emp.setText(collection.getGenBy());
            holder.tv_totSale.setText(collection.getTotSale());
            holder.tv_netAmnt.setText(collection.getNetAmnt());
            holder.tv_cashAmnt.setText(collection.getCashAmnt());
            holder.tv_cardAmnt.setText(collection.getCardAmnt());
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    private class ViewHolder{
        TextView tv_billNo, tv_billDate,tv_billTime,tv_emp,tv_totSale, tv_netAmnt,
                tv_cashAmnt, tv_cardAmnt;
    }
}
