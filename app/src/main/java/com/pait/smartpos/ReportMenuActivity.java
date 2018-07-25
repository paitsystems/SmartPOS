package com.pait.smartpos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.pait.smartpos.log.WriteLog;

public class ReportMenuActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout lay_collection,lay_inward,lay_prod_stock,lay_expense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_menu);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setTitle();
        }

        init();
        lay_expense.setOnClickListener(this);
        lay_prod_stock.setOnClickListener(this);
        lay_inward.setOnClickListener(this);
        lay_collection.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      switch (view.getId()){
          case R.id.lay_collection:
              startActivity(new Intent(ReportMenuActivity.this,CollectionSumReportActivity.class));
              break;
          case R.id.lay_inward:
              break;
          case R.id.lay_prod_stock:
              startActivity(new Intent(ReportMenuActivity.this,ProductwiseStockReportActivity.class));
              break;
          case R.id.lay_expense:
              startActivity(new Intent(ReportMenuActivity.this,ExpenseReportActivity.class));
              break;
      }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init(){
     lay_collection = findViewById(R.id.lay_collection);
     lay_inward = findViewById(R.id.lay_inward);
     lay_prod_stock = findViewById(R.id.lay_prod_stock);
     lay_expense = findViewById(R.id.lay_expense);
    }

    private void writeLog(String _data) {
        new WriteLog().writeLog(getApplicationContext(), "ReportMenuActivity_" + _data);
    }
}
