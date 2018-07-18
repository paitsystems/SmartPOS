package com.pait.smartpos.adpaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pait.smartpos.R;
import com.pait.smartpos.model.BackupRestoreClass;

import java.util.List;

public class BackupRestoreAdapter  extends BaseAdapter {

    private Context context;
    private List<BackupRestoreClass> list;

    public BackupRestoreAdapter(Context _context, List<BackupRestoreClass> _list){
        this.context = _context;
        this.list = _list;
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
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_backup_restore, null);
            holder.tv_name = view.findViewById(R.id.tv_name);
            holder.tv_date = view.findViewById(R.id.tv_date);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        BackupRestoreClass backup = (BackupRestoreClass) getItem(i);
        holder.tv_name.setText(backup.getName());
        holder.tv_date.setText(backup.getLastDate());
        return view;
    }

    private class ViewHolder{
        TextView tv_name, tv_date;
    }
}
