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

import com.pait.smartpos.CashMemoActivity;
import com.pait.smartpos.InwardActivity;
import com.pait.smartpos.R;

public class MainOptionFragment extends Fragment implements View.OnClickListener{

    private Button btn_cashMemo, btn_inward;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_option,null);
        init(view);
        btn_cashMemo.setOnClickListener(this);
        btn_inward.setOnClickListener(this);
        return view;
    }

    private void init(View view){
        btn_cashMemo = view.findViewById(R.id.btn_cashmemo);
        btn_inward = view.findViewById(R.id.btn_inward);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_cashmemo:
                startActivity(new Intent(getContext(), CashMemoActivity.class));
                break;
            case R.id.btn_inward:
                startActivity(new Intent(getContext(), InwardActivity.class));
                break;
        }
    }
}
