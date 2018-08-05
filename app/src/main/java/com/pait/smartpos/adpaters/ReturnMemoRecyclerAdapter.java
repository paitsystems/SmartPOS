package com.pait.smartpos.adpaters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pait.smartpos.R;
import com.pait.smartpos.interfaces.RecyclerViewToActivityInterface;
import com.pait.smartpos.model.AddToCartClass;
import com.pait.smartpos.model.BillDetailClass;
import com.pait.smartpos.model.BillDetailClass;
import com.pait.smartpos.model.RateMasterClass;

import java.util.List;

public class ReturnMemoRecyclerAdapter  extends RecyclerView.Adapter<ReturnMemoRecyclerAdapter.ProductViewHolder>{

    private Context context;
    private List<BillDetailClass> detList;
    private RecyclerViewToActivityInterface listener;
    private int prevSelected = -1;

    public ReturnMemoRecyclerAdapter(Context _context, List<BillDetailClass> _detList){
        this.context = _context;
        this.detList = _detList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exp_list_item_return_memo,parent,false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReturnMemoRecyclerAdapter.ProductViewHolder holder, int position) {
        BillDetailClass det = detList.get(position);
        holder.tv_fsku.setText(det.getFatherSKU());
        holder.tv_qty.setText(det.getQty());
        holder.tv_rate.setText(det.getRate());
        holder.tv_barcode.setText(det.getBarcode());
        holder.tv_total.setText(det.getTotal());
        holder.tv_discAmnt.setText(det.getBilldisamt());
        holder.tv_cgstAmnt.setText(det.getCGSTAMT());
        holder.tv_sgstAmnt.setText(det.getSGSTAMT());
        
    }

    @Override
    public int getItemCount() {
        return detList.size();
    }

    public void setOnClickListener1(RecyclerViewToActivityInterface _listener){
        this.listener = _listener;
    }

    public void removeItem(int position) {
        detList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(BillDetailClass item, int position) {
        detList.add(position, item);
        notifyItemInserted(position);
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tv_fsku, tv_qty, tv_rate, tv_barcode, tv_total, tv_discAmnt, tv_cgstAmnt, tv_sgstAmnt;
        public RelativeLayout viewBackground, viewForeground;

        public ProductViewHolder(View itemView) {
            super(itemView);
            tv_fsku = itemView.findViewById(R.id.tv_fatherSKU);
            tv_qty = itemView.findViewById(R.id.tv_qty);
            tv_rate = itemView.findViewById(R.id.tv_rate);
            tv_barcode = itemView.findViewById(R.id.tv_barcode);
            tv_total = itemView.findViewById(R.id.tv_total);
            tv_discAmnt = itemView.findViewById(R.id.tv_discAmnt);
            tv_cgstAmnt = itemView.findViewById(R.id.tv_cgstAmnt);
            tv_sgstAmnt = itemView.findViewById(R.id.tv_sgstAmnt);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
        }

        @Override
        public void onClick(View view) {

        }
    }
}


