package com.pait.smartpos.adpaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.pait.smartpos.R;
import com.pait.smartpos.model.RateMasterClass;

import java.util.ArrayList;

public class AllRateDialogAdapter extends ArrayAdapter<RateMasterClass> {

    private ArrayList<RateMasterClass> items;
    private ArrayList<RateMasterClass> itemsAll;
    private ArrayList<RateMasterClass> suggestions;
    private int viewResourceId;

    @SuppressWarnings("unchecked")
    public AllRateDialogAdapter(Context context, int viewResourceId, ArrayList<RateMasterClass> items) {
        super(context, viewResourceId, items);
        this.items = items;
        this.itemsAll = (ArrayList<RateMasterClass>) items.clone();
        this.suggestions = new ArrayList<>();
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        RateMasterClass det = items.get(position);
        if (det != null) {
            TextView productLabel = v.findViewById(android.R.id.text1);
            if (productLabel != null) {
                productLabel.setText(det.getRateStr());
                /*if(det.isSelected()){
                    productLabel.setTextColor(parent.getContext().getResources().getColor(R.color.red));
                }*/
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
            String str = ((RateMasterClass) (resultValue)).getRateStr();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (RateMasterClass product : itemsAll) {
                    if (product.getRateStr().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
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
            ArrayList<RateMasterClass> filteredList = (ArrayList<RateMasterClass>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (RateMasterClass c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };
}
