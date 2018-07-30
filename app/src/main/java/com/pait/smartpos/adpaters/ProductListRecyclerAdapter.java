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
import com.pait.smartpos.model.ProductClass;

import java.util.List;

public class ProductListRecyclerAdapter extends RecyclerView.Adapter<ProductListRecyclerAdapter.ProductViewHolder>{

    private Context context;
    private List<ProductClass> prodList;
    private RecyclerViewToActivityInterface listener;
    private int prevSelected = -1;

    public ProductListRecyclerAdapter(Context _context, List<ProductClass> _prodList){
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
        ProductClass prod = prodList.get(position);
        holder.btn_prodName.setText(prod.getProduct_Name());
        if(prod.isSelected()){
            prevSelected = position;
            holder.btn_prodName.setBackground(context.getResources().getDrawable(R.drawable.table_occu_draw));
        }else{
            holder.btn_prodName.setBackground(context.getResources().getDrawable(R.drawable.table_release_draw));
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

        private Button btn_prodName;

        public ProductViewHolder(View itemView) {
            super(itemView);
            btn_prodName = itemView.findViewById(R.id.btn_id);
            btn_prodName.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int value = getAdapterPosition();
            ProductClass prod = prodList.get(value);
            prod.setSelected(true);
            prodList.set(value,prod);
            if(prevSelected!=-1 && prevSelected!=value){
                ProductClass prod1 = prodList.get(prevSelected);
                prod1.setSelected(false);
                prodList.set(prevSelected,prod1);
            }
            prevSelected = value;
            listener.onItemClick(prod);
            notifyDataSetChanged();
        }
    }
}
