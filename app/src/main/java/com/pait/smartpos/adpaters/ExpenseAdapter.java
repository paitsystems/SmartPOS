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
import java.util.List;

public class ExpenseAdapter extends BaseAdapter {
    List<DailyPettyExpClass> list;
    Context context;

    public ExpenseAdapter(List<DailyPettyExpClass> list, Context _context){
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
             holder.tv_exphed = view.findViewById(R.id.tv_exphed);
             view.setTag(holder);
         }else {
             holder = (ViewHolder) view.getTag();
         }
         DailyPettyExpClass detail = (DailyPettyExpClass) getItem(i);
         holder.tv_date.setText(detail.getDate());
         holder.tv_remark.setText(detail.getRemark());
         holder.tv_amt.setText(String.valueOf(detail.getAmount()));
         String head = new DBHandler(context).getExpHeadName(detail.getExpHead());
         holder.tv_exphed.setText(head);
         //holder.tv_exphed.setText(String.valueOf(detail.getExpHead()));
        return view;
    }

    private class ViewHolder{
        TextView tv_date,tv_remark,tv_amt,tv_exphed;
    }
}
