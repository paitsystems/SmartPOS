package com.pait.smartpos.adpaters;// Created by anup on 5/18/2017.

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.pait.smartpos.R;

import java.util.HashMap;
import java.util.List;
//http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
public class CategoryExpandableListAdapter extends BaseExpandableListAdapter {

    Context context;
    List<Integer> catIdList;
    HashMap<Integer,String> catIdNameMap;
    HashMap<String,List<String>> catNameProdName;

    public CategoryExpandableListAdapter(Context _context,
                                         List<Integer> _catIdList,
                                         HashMap<Integer,String> _catIdNameMap,
                                         HashMap<String,List<String>> _catNameProdName){

        this.context = _context;
        this.catIdList = _catIdList;
        this.catIdNameMap = _catIdNameMap;
        this.catNameProdName = _catNameProdName;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return catNameProdName.get(catIdNameMap.get(catIdList.get(groupPosition))).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent){
        String str = (String)getChild(groupPosition,childPosition);
        String[] prodArr = str.split("\\^");
        String prodText, rateText = "";
        if(prodArr.length>1){
            prodText = prodArr[0];
            rateText = prodArr[1];
        }else{
            prodText = str;
        }
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_exp_product_name,null);
        }
        TextView tv_prodName = (TextView) convertView.findViewById(R.id.tv_prodName);
        TextView tv_prodRate = (TextView) convertView.findViewById(R.id.tv_prodRate);
        tv_prodName.setText(prodText);
        tv_prodRate.setText(rateText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return catNameProdName.get(catIdNameMap.get(catIdList.get(groupPosition))).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return catIdNameMap.get(catIdList.get(groupPosition));
    }

    @Override
    public int getGroupCount() {
        return catIdList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpandable, View convertView, ViewGroup viewGroup) {
        String header = (String) getGroup(groupPosition);
        String catArr[] = header.split("\\^");
        String catName = catArr[0];
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_exp_category_name,null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.lblListHeader);
        textView.setText(catName);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
