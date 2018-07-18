package com.pait.smartpos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Spinner;

import com.pait.smartpos.adpaters.CustomSpinnerAdapter;
import com.pait.smartpos.model.CustomSpinnerClass;

import java.util.ArrayList;

public class InwardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inward);

        final String[] select_qualification = {
                "Select Qualification", "10th / Below", "12th", "Diploma", "UG","PG", "Phd"};
        Spinner spinner = findViewById(R.id.sp_prod);

        ArrayList<CustomSpinnerClass> listVOs = new ArrayList<>();

        for (String aSelect_qualification : select_qualification) {
            CustomSpinnerClass stateVO = new CustomSpinnerClass();
            stateVO.setTitle(aSelect_qualification);
            stateVO.setSelected(false);
            listVOs.add(stateVO);
        }
        CustomSpinnerAdapter myAdapter = new CustomSpinnerAdapter(InwardActivity.this, 0,listVOs);
        spinner.setAdapter(myAdapter);
    }
}
