package com.pait.smartpos.adpaters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pait.smartpos.R;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.interfaces.RecyclerViewToActivityInterface;
import com.pait.smartpos.model.AddToCartClass;
import com.pait.smartpos.model.MasterUpdationClass;
import com.pait.smartpos.model.ProductClass;

import java.util.List;

public class AddToCartRecyclerAdapter extends RecyclerView.Adapter<AddToCartRecyclerAdapter.AddToCartViewHolder>{

    private Context context;
    private List<AddToCartClass> cartList;
    private RecyclerViewToActivityInterface listener;

    public AddToCartRecyclerAdapter(Context _context, List<AddToCartClass> _cartList){
        this.context = _context;
        this.cartList = _cartList;
    }

    @NonNull
    @Override
    public AddToCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_addtocart,parent,false);
        return new AddToCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddToCartViewHolder holder, int position) {
        AddToCartClass cart = cartList.get(position);
        holder.tv_prodName.setText(cart.getProdName());
        holder.tv_qty.setText(String.valueOf(cart.getQty()));
        //holder.tv_rate.setText(String.valueOf(cart.getRate()));
        //holder.tv_amnt.setText(String.valueOf(cart.getAmnt()));
        holder.tv_rate.setText(cart.getRate());
        holder.tv_amnt.setText(cart.getTotal());
        if(position == cartList.size()-1){
            listener.calculation(0,0);
        }
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void setOnClickListener1(RecyclerViewToActivityInterface _listener){
        this.listener = _listener;
    }

    public void removeItem(int position) {
        cartList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(AddToCartClass item, int position) {
        cartList.add(position, item);
        notifyItemInserted(position);
    }

    public class AddToCartViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_prodName, tv_qty, tv_rate, tv_amnt;
        public RelativeLayout viewBackground, viewForeground;

        public AddToCartViewHolder(View itemView) {
            super(itemView);
            tv_prodName = itemView.findViewById(R.id.tv_prodName);
            tv_qty = itemView.findViewById(R.id.tv_qty);
            tv_rate = itemView.findViewById(R.id.tv_rate);
            tv_amnt = itemView.findViewById(R.id.tv_amnt);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
        }
    }
}

