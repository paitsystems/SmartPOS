package com.pait.smartpos.adpaters;

//Created by lnb on 8/3/2017.

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.pait.smartpos.R;
import com.pait.smartpos.model.GSTDetailClass;

import java.util.HashMap;
import java.util.List;

public class GSTExpandableListAdapter extends BaseExpandableListAdapter{

    Context context;
    HashMap<Integer,List<List<GSTDetailClass>>> mastDetMap;
    List<String> gstGroupNameList;
    List<Integer> gstMastAutoList, gstDetAutoList;

    public GSTExpandableListAdapter(Context _context,HashMap<Integer,List<List<GSTDetailClass>>> _mastDetMap,
                                    List<Integer> _gstMastAutoList, List<Integer> _gstDetAutoList,
                                    List<String> _gstGroupNameList){
        this.context = _context;
        this.mastDetMap = _mastDetMap;
        this.gstGroupNameList = _gstGroupNameList;
        this.gstMastAutoList = _gstMastAutoList;
        this.gstDetAutoList = _gstDetAutoList;
    }

    @Override
    public int getGroupCount() {
        return gstMastAutoList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mastDetMap.get(gstMastAutoList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return gstGroupNameList.get(groupPosition);
    }

    @Override
    public List<List<GSTDetailClass>> getChild(int groupPosition, int childPosition) {
        //List<GSTDetailClass> l = mastDetMap.get(gstMastAutoList.get(groupPosition));
        //Constant.showLog(l.size()+"");
        return mastDetMap.get(gstMastAutoList.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {
        if(view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_exp_gst_group_name,null);
        }
        String groupName = (String) getGroup(groupPosition);
        TextView tv_groupName = (TextView) view.findViewById(R.id.tv_group_name);
        tv_groupName.setText(groupName);
        return view;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean b, View view, ViewGroup viewGroup) {
        if(view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_exp_gst_detail_row,null);
        }
        List<List<GSTDetailClass>> list = getChild(groupPosition,childPosition);
        List<GSTDetailClass> li = list.get(childPosition);
        for(int i=0;i< li.size();i++) {
            GSTDetailClass detailClass = li.get(i);
            TextView tv_from_range = (TextView) view.findViewById(R.id.tv_gst_from_range);
            TextView tv_to_range = (TextView) view.findViewById(R.id.tv_gst_to_range);
            TextView tv_gst_per = (TextView) view.findViewById(R.id.tv_gst_per);
            TextView tv_gst_cgstper = (TextView) view.findViewById(R.id.tv_gst_cgstper);
            TextView tv_gst_sgstper = (TextView) view.findViewById(R.id.tv_gst_sgstper);
            TextView tv_gst_cgstshare = (TextView) view.findViewById(R.id.tv_gst_cgstshare);
            TextView tv_gst_sgstshare = (TextView) view.findViewById(R.id.tv_gst_sgstshare);
            TextView tv_gst_cessper = (TextView) view.findViewById(R.id.tv_gst_cessper);

            tv_from_range.setText(String.valueOf(detailClass.getFromRange()));
            tv_to_range.setText(String.valueOf(detailClass.getToRange()));
            tv_gst_per.setText(String.valueOf(detailClass.getGstPer()));
            tv_gst_cgstper.setText(String.valueOf(detailClass.getCgstPer()));
            tv_gst_sgstper.setText(String.valueOf(detailClass.getSgstPer()));
            tv_gst_cgstshare.setText(String.valueOf(detailClass.getCgstShare()));
            tv_gst_sgstshare.setText(String.valueOf(detailClass.getSgstShare()));
            tv_gst_cessper.setText(String.valueOf(detailClass.getCessPer()));
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, final int childPosition) {
        return true;
    }
}
