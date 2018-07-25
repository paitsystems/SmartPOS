package com.pait.smartpos.adpaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.pait.smartpos.R;
import com.pait.smartpos.model.BillDetailClass;
import com.pait.smartpos.model.BillDetailClass;

import java.util.ArrayList;

public class ReturnMemoBarcodeAdapter extends ArrayAdapter<BillDetailClass> {

    private ArrayList<BillDetailClass> items;
    private ArrayList<BillDetailClass> itemsAll;
    private ArrayList<BillDetailClass> suggestions;
    private int viewResourceId;

    @SuppressWarnings("unchecked")
    public ReturnMemoBarcodeAdapter(Context context, int viewResourceId, ArrayList<BillDetailClass> items) {
        super(context, viewResourceId, items);
        this.items = items;
        this.itemsAll = (ArrayList<BillDetailClass>) items.clone();
        this.suggestions = new ArrayList<>();
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        BillDetailClass det = items.get(position);
        if (det != null) {
            TextView productLabel = v.findViewById(R.id.tv_auto);
            if (productLabel != null) {
                productLabel.setText(det.getFatherSKU());
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
            String str = ((BillDetailClass) (resultValue)).getFatherSKU();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (BillDetailClass product : itemsAll) {
                    if (product.getFatherSKU().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
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
            ArrayList<BillDetailClass> filteredList = (ArrayList<BillDetailClass>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (BillDetailClass c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };
}

