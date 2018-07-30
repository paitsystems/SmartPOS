package com.pait.smartpos.adpaters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pait.smartpos.R;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.interfaces.RecyclerViewToActivityInterface;
import com.pait.smartpos.model.InwardDetailClass;

import java.util.List;

public class InwardRecyclerViewAdapter extends RecyclerView.Adapter<InwardRecyclerViewAdapter.ViewHolder>{

    List<InwardDetailClass> list;
    Context context;
    RecyclerViewToActivityInterface listener;

    public InwardRecyclerViewAdapter( List<InwardDetailClass> _list,Context _context){
        this.context = _context;
        this.list = _list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inward_rec_item,parent,false);

        //View view = inflater.inflate(R.layout.inward_rec_item,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       InwardDetailClass detailClass = list.get(position);
        holder.tv_pr_name.setText(detailClass.getFatherSKU());
        holder.tv_qty.setText(String.valueOf(detailClass.getRecQty()));
        holder.tv_rate.setText(String.valueOf(detailClass.getPurchaseRate()));
        holder.tv_ssp.setText(String.valueOf(detailClass.getRate()));
        holder.tv_cgst.setText(String.valueOf(detailClass.getCGSTAMT()));
        holder.tv_sgst.setText(String.valueOf(detailClass.getSGSTAMT()));
        holder.tv_gst_per.setText(String.valueOf(detailClass.getGSTPER()));

        if (position == list.size() - 1){
            listener.calculation(0,0);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnClickListener1(RecyclerViewToActivityInterface _listener){
        this.listener = _listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_pr_name,tv_qty,tv_rate,tv_ssp,tv_cgst,tv_sgst,tv_gst_per;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_pr_name = itemView.findViewById(R.id.tv_pr_name);
            tv_qty = itemView.findViewById(R.id.tv_qty);
            tv_rate = itemView.findViewById(R.id.tv_rate);
            tv_ssp = itemView.findViewById(R.id.tv_ssp);
            tv_cgst = itemView.findViewById(R.id.tv_cgst);
            tv_sgst = itemView.findViewById(R.id.tv_sgst);
            tv_gst_per = itemView.findViewById(R.id.tv_gst_per);
        }
    }
}
