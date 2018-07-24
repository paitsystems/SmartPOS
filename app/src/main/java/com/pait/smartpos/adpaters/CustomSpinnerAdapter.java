package com.pait.smartpos.adpaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.pait.smartpos.InwardActivity;
import com.pait.smartpos.R;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.interfaces.checkBoxListener;
import com.pait.smartpos.model.CustomSpinnerClass;

import java.util.ArrayList;
import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<CustomSpinnerClass> {

    private Context mContext;
    private ArrayList<CustomSpinnerClass> listState;
    private CustomSpinnerAdapter CustomSpinnerAdapter;
    private boolean isFromView = false;
    private List<Integer> posLs;
    private List<Integer> checkedValue;
    private checkBoxListener listener;


    public CustomSpinnerAdapter(Context context, int resource, List<CustomSpinnerClass> objects,List<Integer> _checkedValue) {
        super(context, resource, objects);
        this.mContext = context;
        this.listState = (ArrayList<CustomSpinnerClass>) objects;
        this.CustomSpinnerAdapter = this;
        this.checkedValue = _checkedValue;
        listener = (checkBoxListener)mContext;
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView,ViewGroup parent) {
        final ViewHolder holder;
        posLs = new ArrayList<>();
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.custom_spinner_option, null);
            holder = new ViewHolder();
            holder.mTextView = convertView.findViewById(R.id.text);
            holder.mCheckBox = convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTextView.setText(listState.get(position).getTitle());

        // To check weather checked event fire from getview() or user input
        isFromView = true;
        holder.mCheckBox.setChecked(listState.get(position).isSelected());
        isFromView = false;

        if ((position == 0)) {
            holder.mCheckBox.setVisibility(View.INVISIBLE);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        }
        holder.mCheckBox.setTag(position);


            for (int k=0;k<checkedValue.size();k++){
                holder.mCheckBox.setChecked(listState.get(k).isSelected());
        }

        holder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int getPosition = (Integer) buttonView.getTag();
            if(isChecked)
            Constant.showLog("getPosition: "+getPosition);
            if(!posLs.contains(getPosition)){
                posLs.add(getPosition);
            }
            listener.setPosition(posLs);
        });
        return convertView;
    }


    /*public List<Integer> setPosition(){
        List<Integer> ls = posLs;
        Constant.showLog(""+ls.size());
      return ls;
    }*/

    private class ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
    }
}
