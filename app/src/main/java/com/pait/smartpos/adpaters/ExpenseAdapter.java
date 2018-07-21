package com.pait.smartpos.adpaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pait.smartpos.R;
import com.pait.smartpos.model.ExpenseDetail;
import java.util.List;

public class ExpenseAdapter extends BaseAdapter {
    List<ExpenseDetail> list;
    Context context;

    public ExpenseAdapter(List<ExpenseDetail> list,Context _context){
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
             view = inflater.inflate(R.layout.expense_item,null);
             holder = new ViewHolder();
             holder.tv_amt = view.findViewById(R.id.tv_amt);
             holder.tv_remark = view.findViewById(R.id.tv_remark);
             holder.tv_date = view.findViewById(R.id.tv_date);
             view.setTag(holder);
         }else {
             holder = (ViewHolder) view.getTag();
         }

         ExpenseDetail detail = (ExpenseDetail) getItem(i);
         holder.tv_date.setText(detail.getDate());
         holder.tv_remark.setText(detail.getRemark());
         holder.tv_amt.setText(String.valueOf(detail.getAmount()));

        return view;
    }

    private class ViewHolder{
        TextView tv_date,tv_remark,tv_amt;
    }
}