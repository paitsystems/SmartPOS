package com.pait.smartpos.adpaters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.pait.smartpos.R;

import com.pait.smartpos.parse.BillReprintCancelClass;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BillReprintCancelAdapter extends BaseAdapter{

    private Context context;
    private List<BillReprintCancelClass> list;

    public BillReprintCancelAdapter(Context _context, List<BillReprintCancelClass> _list){
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
        if(view==null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_bill_reprint_cancel,null);
            holder.tv_billNo = view.findViewById(R.id.tv_billNo);
            holder.tv_date = view.findViewById(R.id.tv_date);
            holder.tv_status = view.findViewById(R.id.tv_status);
            holder.tv_amnt = view.findViewById(R.id.tv_amnt);
            holder.tv_tableNo = view.findViewById(R.id.tv_tableNo);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        BillReprintCancelClass bill = (BillReprintCancelClass) getItem(i);
        holder.tv_billNo.setText(bill.getBillNo());
        holder.tv_tableNo.setText(String.valueOf(bill.getQty()));
        holder.tv_amnt.setText(bill.getNetAmt());
        if(bill.getStatus().equals("Y")|| bill.getStatus().equals("A")){
            holder.tv_status.setBackground(ContextCompat.getDrawable(context,R.drawable.active_draw));
        }else {
            holder.tv_status.setBackground(ContextCompat.getDrawable(context,R.drawable.inactive_draw));
        }
        try {
            String str = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).format(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(bill.getBillDate()));
            str = str +" "+ bill.getBillTime();
            holder.tv_date.setText(str);
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    private class ViewHolder{
        private TextView tv_billNo, tv_status, tv_tableNo, tv_amnt, tv_date;
    }
}
