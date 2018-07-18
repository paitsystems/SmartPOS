package com.pait.smartpos.adpaters;

//Created by lnb on 7/28/2017.

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pait.smartpos.R;

import java.util.List;

public class TableNameListAdapter extends BaseAdapter {

    Context context;
    List<String> tableList;

    public TableNameListAdapter(Context _context, List<String> _tableList){
        this.context = _context;
        this.tableList = _tableList;
    }

    @Override
    public int getCount() {
        return tableList.size();
    }

    @Override
    public String getItem(int i) {
        return tableList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(view==null) {
            view = inflater.inflate(R.layout.list_item_table_view, null);
            holder = new ViewHolder();
            holder.tv_tablename = (TextView) view.findViewById(R.id.tv_tablename);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        holder.tv_tablename.setText(getItem(i));
        return view;
    }

    private class ViewHolder{
        TextView tv_tablename;
    }
}
