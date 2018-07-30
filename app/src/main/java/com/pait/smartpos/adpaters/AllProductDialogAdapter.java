package com.pait.smartpos.adpaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;

import com.pait.smartpos.R;
import com.pait.smartpos.model.ProductClass;
import java.util.ArrayList;

public class AllProductDialogAdapter extends ArrayAdapter<ProductClass> {

    private ArrayList<ProductClass> items;
    private ArrayList<ProductClass> itemsAll;
    private ArrayList<ProductClass> suggestions;
    private int viewResourceId;

    @SuppressWarnings("unchecked")
    public AllProductDialogAdapter(Context context, int viewResourceId, ArrayList<ProductClass> items) {
        super(context, viewResourceId, items);
        this.items = items;
        this.itemsAll = (ArrayList<ProductClass>) items.clone();
        this.suggestions = new ArrayList<>();
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        ProductClass det = items.get(position);
        if (det != null) {
            TextView productLabel = v.findViewById(android.R.id.text1);
            if (productLabel != null) {
                productLabel.setText(det.getCat3());
               /* if(det.isSelected()){
                    productLabel.setTextColor(parent.getContext().getResources().getColor(R.color.red));
                }*/
            }
            /*Button productLabel = v.findViewById(R.id.btn_id);
            if (productLabel != null) {
                productLabel.setText(det.getCat3());
            }*/
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        public String convertResultToString(Object resultValue) {
            String str = ((ProductClass) (resultValue)).getCat3();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (ProductClass product : itemsAll) {
                    if (product.getCat3().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
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
            ArrayList<ProductClass> filteredList = (ArrayList<ProductClass>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (ProductClass c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };
}
