package com.pait.smartpos.adpaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pait.smartpos.R;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.model.DailyPettyExpClass;
import com.pait.smartpos.model.InwardMasterClass;

import java.util.List;

public class InwardSummeryReportAdapter extends BaseAdapter {
    List<InwardMasterClass> list;
    Context context;

    public InwardSummeryReportAdapter(List<InwardMasterClass> list, Context _context){
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
            view = inflater.inflate(R.layout.inward_summery_item,null);
            holder = new ViewHolder();
            holder.tv_inward_no = view.findViewById(R.id.tv_inward_no);
            holder.tv_inward_date = view.findViewById(R.id.tv_inward_date);
            holder.tv_inv_no = view.findViewById(R.id.tv_inv_no);
            holder.tv_inv_date = view.findViewById(R.id.tv_inv_date);
            holder.tv_supp = view.findViewById(R.id.tv_supp);
            holder.tv_qty = view.findViewById(R.id.tv_qty);
            holder.tv_amnt = view.findViewById(R.id.tv_amnt);
            holder.tv_status = view.findViewById(R.id.tv_status);
            holder.tv_cgst = view.findViewById(R.id.tv_cgst);
            holder.tv_sgst = view.findViewById(R.id.tv_sgst);
            holder.tv_igst = view.findViewById(R.id.tv_igst);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        InwardMasterClass mast = (InwardMasterClass) getItem(i);
        holder.tv_inward_no.setText(mast.getInwNo());
        holder.tv_inward_date.setText(mast.getInwardDate());
        holder.tv_inv_no.setText(mast.getInvNo());
        holder.tv_inv_date.setText(mast.getRefundDate());
        holder.tv_supp.setText(new DBHandler(context).getInwardSupplierName((mast.getSupplierID())));
        holder.tv_qty.setText(String.valueOf(mast.getTotalQty()));
        holder.tv_amnt.setText(String.valueOf(mast.getTotalAmt()));
        holder.tv_status.setText(String.valueOf(mast.getStatus()));
        holder.tv_cgst.setText(String.valueOf(mast.getCGSTAMT()));
        holder.tv_sgst.setText(String.valueOf(mast.getSGSTAMT()));
        holder.tv_igst.setText(String.valueOf(mast.getIGSTAMT()));
        return view;
    }

    private class ViewHolder{
        TextView tv_inward_no,tv_inward_date,tv_inv_date,tv_inv_no,tv_supp,tv_qty,
                tv_amnt,tv_status,tv_cgst,tv_sgst,tv_igst;
    }
}

