package com.pait.smartpos.adpaters;

//Created by ANUP on 01-06-2018.

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pait.smartpos.R;
import com.pait.smartpos.interfaces.OnItemClickListenerCustom;
import com.pait.smartpos.model.MasterUpdationClass;

import java.util.List;

public class MasterUpdationRecyclerAdapter extends RecyclerView.Adapter<MasterUpdationRecyclerAdapter.MyViewHolder>{

    private List<MasterUpdationClass> list;
    private OnItemClickListenerCustom listener;
    private Context context;

    public MasterUpdationRecyclerAdapter(Context _context,List<MasterUpdationClass> _list, OnItemClickListenerCustom _listener){
        this.list = _list;
        this.listener = _listener;
        this.context = _context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recycler_list_item_master_updation,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final MasterUpdationClass master = list.get(position);
        holder.tv_masterName.setText(master.getMasterName());
        holder.tv_masterName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(master);
            }
        });
        if(master.getMasterType().equals("P")){
            holder.thumbnail.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_four_black_squares));
        }else if(master.getMasterType().equals("S")){
            holder.thumbnail.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_four_black_squares));
        }else if(master.getMasterType().equals("T")){
            holder.thumbnail.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_table_icon_black));
        }else if(master.getMasterType().equals("C")){
            holder.thumbnail.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_restaurant_menu_black_24dp));
        }
       /* if(master.getIsMasterActive().equals("Y")||master.getIsMasterActive().equals("A")){
            holder.tv_active.setBackground(ContextCompat.getDrawable(context,R.drawable.active_draw));
        }else {
            holder.tv_active.setBackground(ContextCompat.getDrawable(context,R.drawable.inactive_draw));
        }*/
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(MasterUpdationClass item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_masterName, tv_active;
        public ImageView thumbnail;
        public RelativeLayout viewBackground, viewForeground, viewEditBackground;

        public MyViewHolder(View view) {
            super(view);
            tv_masterName = view.findViewById(R.id.tv_masterName);
            tv_active = view.findViewById(R.id.tv_active);
            thumbnail = view.findViewById(R.id.thumbnail);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
            viewEditBackground = view.findViewById(R.id.view_editbackground);
        }
    }
}
