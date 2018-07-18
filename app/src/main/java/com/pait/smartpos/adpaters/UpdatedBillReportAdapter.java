package com.pait.smartpos.adpaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.pait.smartpos.R;

import java.util.HashMap;
import java.util.List;

public class UpdatedBillReportAdapter extends BaseExpandableListAdapter{

    private Context context;
    private List<String> parentList;
    private HashMap<String,List<List<String>>> childMap;

    public UpdatedBillReportAdapter(Context _context, List<String> _parentList,
                                    HashMap<String,List<List<String>>> _childMap){
        this.context = _context;
        this.parentList = _parentList;
        this.childMap = _childMap;
    }

    @Override
    public int getGroupCount() {
        return parentList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return childMap.get(parentList.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return parentList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return childMap.get(parentList.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.exp_list_item_ub_parent,null);
        }
        TextView tv_parent = convertView.findViewById(R.id.tv_parent);
        tv_parent.setText(parentList.get(i));
        return convertView;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View convertView, ViewGroup viewGroup) {
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.exp_list_item_ub_child,null);
        }
        TextView tv_prodName = convertView.findViewById(R.id.tv_prodName);
        TextView tv_oldQty = convertView.findViewById(R.id.tv_oldQty);
        TextView tv_oldRate = convertView.findViewById(R.id.tv_oldRate);
        TextView tv_newQty = convertView.findViewById(R.id.tv_newQty);
        TextView tv_newRate = convertView.findViewById(R.id.tv_newRate);
        List<List<String>> list = childMap.get(parentList.get(i));
        List<String> list1 = list.get(i1);
        tv_oldQty.setText(list1.get(0));
        tv_oldRate.setText(list1.get(2));
        tv_newQty.setText(list1.get(1));
        tv_newRate.setText(list1.get(3));
        tv_prodName.setText(list1.get(4));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
