package com.pait.smartpos.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.R;
import com.pait.smartpos.Repo_Collection_Activity;
import com.pait.smartpos.Repo_Itemwise_SaleActivity;
import com.pait.smartpos.Repo_Todays_SaleActivity;

public class ReportListFragment extends Fragment {

    private Button btn1_collection_report, btn2_itemwise_sale, btn3_todays_sale;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_report_list,null);

        init(view);

        btn1_collection_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),Repo_Collection_Activity.class));
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        btn2_itemwise_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),Repo_Itemwise_SaleActivity.class));
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        btn3_todays_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),Repo_Todays_SaleActivity.class));
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        Constant.showLog("ReportListFragment");

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init(View view){
        btn1_collection_report = view.findViewById(R.id.btn1_collection_report);
        btn2_itemwise_sale = view.findViewById(R.id.btn2_itemwise_sale);
        btn3_todays_sale = view.findViewById(R.id.btn3_todays_sale);
    }

}
