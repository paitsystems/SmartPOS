package com.pait.smartpos.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.pait.smartpos.AllGraphActivity;
import com.pait.smartpos.CashMemoActivity;
import com.pait.smartpos.ExpenseActivity;
import com.pait.smartpos.InwardActivity;
import com.pait.smartpos.InwardFirstScreenActivity;
import com.pait.smartpos.R;
import com.pait.smartpos.ReportMenuActivity;
import com.pait.smartpos.ReturnMemoActivity;

public class MainOptionFragment extends Fragment implements View.OnClickListener{

    private Button btn_cashMemo, btn_inward,btn_expense,btn_reports;
    private LinearLayout lay_cashmemo,lay_inward,lay_expense,lay_report,lay_returnmemo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.fragment_main_option,null);
        View view = inflater.inflate(R.layout.test_option_1,null);
        init(view);
        /*btn_cashMemo.setOnClickListener(this);
        btn_inward.setOnClickListener(this);
        btn_expense.setOnClickListener(this);
        btn_reports.setOnClickListener(this);*/
        lay_cashmemo.setOnClickListener(this);
        lay_inward.setOnClickListener(this);
        lay_expense.setOnClickListener(this);
        lay_report.setOnClickListener(this);
        lay_returnmemo.setOnClickListener(this);
        return view;
    }

    private void init(View view){
        /*btn_cashMemo = view.findViewById(R.id.btn_cashmemo);
        btn_inward = view.findViewById(R.id.btn_inward);
        btn_expense = view.findViewById(R.id.btn_expense);
        btn_reports = view.findViewById(R.id.btn_reports);*/
        lay_cashmemo = view.findViewById(R.id.lay_cashmemo);
        lay_inward = view.findViewById(R.id.lay_inward);
        lay_expense = view.findViewById(R.id.lay_expense);
        lay_report = view.findViewById(R.id.lay_report);
        lay_returnmemo = view.findViewById(R.id.lay_returnmemo);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            /*case R.id.btn_cashmemo:
                startActivity(new Intent(getContext(), CashMemoActivity.class));
                break;
            case R.id.btn_inward:
                startActivity(new Intent(getContext(), ReturnMemoActivity.class));
                break;
            case R.id.btn_expense:
                startActivity(new Intent(getContext(), ExpenseActivity.class));
                break;
            case R.id.btn_reports:
                startActivity(new Intent(getContext(), ReportMenuActivity.class));
                break;*/
            case R.id.lay_cashmemo:
                startActivity(new Intent(getContext(), CashMemoActivity.class));
                break;
            case R.id.lay_inward:
                startActivity(new Intent(getContext(), InwardFirstScreenActivity.class));
                break;
            case R.id.lay_expense:
                startActivity(new Intent(getContext(), ExpenseActivity.class));
                break;
            case R.id.lay_report:
                startActivity(new Intent(getContext(), AllGraphActivity.class));
                break;
            case R.id.lay_returnmemo:
                startActivity(new Intent(getContext(), ReturnMemoActivity.class));
                break;
        }
    }
}
