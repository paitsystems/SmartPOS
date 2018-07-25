package com.pait.smartpos.adpaters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import android.widget.Filter;

import com.pait.smartpos.R;
import com.pait.smartpos.model.BillMasterClass;

import java.util.ArrayList;

public class ReturnMemoBillNoAdapter extends ArrayAdapter<BillMasterClass> {
    
    private ArrayList<BillMasterClass> items;
    private ArrayList<BillMasterClass> itemsAll;
    private ArrayList<BillMasterClass> suggestions;
    private int viewResourceId;

    @SuppressWarnings("unchecked")
    public ReturnMemoBillNoAdapter(Context context, int viewResourceId,ArrayList<BillMasterClass> items) {
        super(context, viewResourceId, items);
        this.items = items;
        this.itemsAll = (ArrayList<BillMasterClass>) items.clone();
        this.suggestions = new ArrayList<>();
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        BillMasterClass master = items.get(position);
        if (master != null) {
            TextView productLabel = v.findViewById(R.id.tv_auto);
            if (productLabel != null) {
                productLabel.setText(master.getBillNo());
            }
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        public String convertResultToString(Object resultValue) {
            String str = ((BillMasterClass) (resultValue)).getBillNo();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (BillMasterClass product : itemsAll) {
                    if (product.getBillNo().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(product);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint,FilterResults results) {
            @SuppressWarnings("unchecked")
            ArrayList<BillMasterClass> filteredList = (ArrayList<BillMasterClass>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (BillMasterClass c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };
}
