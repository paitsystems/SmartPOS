package com.pait.smartpos.adpaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pait.smartpos.R;
import com.pait.smartpos.model.CollectionClass;
import com.pait.smartpos.model.CollectionClassR;
import com.pait.smartpos.model.DailyPettyExpClass;

import java.util.List;

public class CollectionSumAdapter extends BaseAdapter {
    List<CollectionClass> list;
    Context context;

    public CollectionSumAdapter(List<CollectionClass> list, Context _context) {
        this.context = _context;
        this.list = list;
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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.colllection_item, null);
            holder = new ViewHolder();
            holder.tv_cashier = view.findViewById(R.id.tv_cashier);
            holder.tv_netcoll = view.findViewById(R.id.tv_netcoll);
            holder.tv_salecash = view.findViewById(R.id.tv_salecash);
            holder.tv_cashback = view.findViewById(R.id.tv_cashback);
            holder.tv_netcash = view.findViewById(R.id.tv_netcash);
            holder.tv_cheque = view.findViewById(R.id.tv_cheque);
            holder.tv_card = view.findViewById(R.id.tv_card);
            holder.tv_opa = view.findViewById(R.id.tv_opa);
            holder.tv_exprec = view.findViewById(R.id.tv_exprec);
            holder.tv_exppay = view.findViewById(R.id.tv_exppay);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        CollectionClass detail = (CollectionClass) getItem(i);
        holder.tv_cashier.setText(detail.getCashier());
        holder.tv_netcoll.setText(detail.getNetCollection());
        holder.tv_salecash.setText(detail.getSaleCash());
        holder.tv_cashback.setText(detail.getCashback());
        holder.tv_netcash.setText(detail.getNetcash());
        holder.tv_cheque.setText(detail.getCheck());
        holder.tv_card.setText(detail.getCard());
        holder.tv_opa.setText(detail.getOtherPayAmt());
        holder.tv_exprec.setText(detail.getExp_receipt());
        holder.tv_exppay.setText(detail.getExp_payment());

        return view;
    }

    private class ViewHolder {
        TextView tv_cashier, tv_netcoll, tv_salecash, tv_cashback,tv_netcash,tv_cheque,tv_card,
                tv_opa,tv_exprec,tv_exppay;
    }
}
