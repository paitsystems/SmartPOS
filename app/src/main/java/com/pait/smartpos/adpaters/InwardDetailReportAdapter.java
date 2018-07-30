package com.pait.smartpos.adpaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.pait.smartpos.R;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.model.InwardDetailClass;

import java.util.List;

public class InwardDetailReportAdapter  extends BaseAdapter {
    List<InwardDetailClass> list;
    Context context;

    public InwardDetailReportAdapter(List<InwardDetailClass> list, Context _context){
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
            view = inflater.inflate(R.layout.inward_report_detail_item,null);
            holder = new ViewHolder();
            holder.tv_inward_no = view.findViewById(R.id.tv_inward_no);
            holder.tv_final_prod = view.findViewById(R.id.tv_final_prod);
            holder.tv_purchase_rate = view.findViewById(R.id.tv_purchase_rate);
            holder.tv_sale_rate = view.findViewById(R.id.tv_sale_rate);
            holder.tv_supp = view.findViewById(R.id.tv_supp);
            holder.tv_qty = view.findViewById(R.id.tv_qty);
           // holder.tv_amnt = view.findViewById(R.id.tv_amnt);
            holder.tv_hsn_code = view.findViewById(R.id.tv_hsn_code);
            holder.tv_cgst = view.findViewById(R.id.tv_cgst);
            holder.tv_sgst = view.findViewById(R.id.tv_sgst);
            holder.tv_igst = view.findViewById(R.id.tv_igst);
            holder.tv_gstper = view.findViewById(R.id.tv_gstper);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        InwardDetailClass detail = (InwardDetailClass) getItem(i);
        holder.tv_inward_no.setText(String.valueOf(detail.getInwardID()));
        holder.tv_final_prod.setText(detail.getFatherSKU());
        holder.tv_purchase_rate.setText(String.valueOf(detail.getPurchaseRate()));
        holder.tv_sale_rate.setText(String.valueOf(detail.getRate()));
        holder.tv_supp.setText(String.valueOf(new DBHandler(context).getInwardSupplierName((detail.getSuppid()))));
        holder.tv_qty.setText(String.valueOf(detail.getRecQty()));
        //holder.tv_amnt.setText(String.valueOf(detail.getTotalAmt()));
        holder.tv_hsn_code.setText(String.valueOf(detail.getHSNCode()));
        holder.tv_gstper.setText(String.valueOf(detail.getGSTPER()));
        holder.tv_cgst.setText(String.valueOf(detail.getCGSTAMT()));
        holder.tv_sgst.setText(String.valueOf(detail.getSGSTAMT()));
        holder.tv_igst.setText(String.valueOf(detail.getIGSTAMT()));
        return view;
    }

    private class ViewHolder{
        TextView tv_inward_no,tv_final_prod,tv_purchase_rate,tv_sale_rate,tv_supp,tv_qty,
                tv_amnt,tv_hsn_code,tv_cgst,tv_sgst,tv_igst,tv_gstper;
    }
}

