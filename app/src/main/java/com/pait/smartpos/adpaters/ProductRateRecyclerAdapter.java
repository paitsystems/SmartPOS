package com.pait.smartpos.adpaters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pait.smartpos.R;
import com.pait.smartpos.interfaces.RecyclerViewToActivityInterface;
import com.pait.smartpos.model.RateMasterClass;

import java.util.List;

public class ProductRateRecyclerAdapter extends RecyclerView.Adapter<ProductRateRecyclerAdapter.ProductViewHolder>{

    private Context context;
    private List<RateMasterClass> prodList;
    private RecyclerViewToActivityInterface listener;
    private int prevSelected = -1;

    public ProductRateRecyclerAdapter(Context _context, List<RateMasterClass> _prodList){
        this.context = _context;
        this.prodList = _prodList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_product,parent,false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        RateMasterClass rate = prodList.get(position);
        holder.btn_rate.setText(String.valueOf(rate.getRate()));
        if(rate.isSelected()){
            prevSelected = position;
            holder.btn_rate.setBackground(context.getResources().getDrawable(R.drawable.table_occu_draw));
        }else{
            holder.btn_rate.setBackground(context.getResources().getDrawable(R.drawable.table_release_draw));
        }
    }

    @Override
    public int getItemCount() {
        return prodList.size();
    }

    public void setOnClickListener1(RecyclerViewToActivityInterface _listener){
        this.listener = _listener;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Button btn_rate;

        public ProductViewHolder(View itemView) {
            super(itemView);
            btn_rate = itemView.findViewById(R.id.btn_id);
            btn_rate.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int value = getAdapterPosition();
            RateMasterClass rate = prodList.get(value);
            rate.setSelected(true);
            prodList.set(value,rate);
            if(prevSelected!=-1 && prevSelected!=value){
                RateMasterClass prod1 = prodList.get(prevSelected);
                prod1.setSelected(false);
                prodList.set(prevSelected,prod1);
            }
            prevSelected = value;
            listener.onItemClick(rate);
            notifyDataSetChanged();
        }
    }
}

