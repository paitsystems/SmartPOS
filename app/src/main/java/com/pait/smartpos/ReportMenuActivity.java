package com.pait.smartpos;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import com.pait.smartpos.constant.Constant;
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
        switch (view.getId()) {
            case R.id.lay_collection:
                startActivity(new Intent(ReportMenuActivity.this, CollectionSumReportActivity.class));
                break;
            case R.id.lay_inward:
                startActivity(new Intent(ReportMenuActivity.this, InwardReportActivity.class));
                break;
            case R.id.lay_prod_stock:
                startActivity(new Intent(ReportMenuActivity.this, ProductwiseStockReportActivity.class));
                break;
            case R.id.lay_expense:
                startActivity(new Intent(ReportMenuActivity.this, ExpenseReportActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        writeLog("onCreateOptionsMenu Called");
        getMenuInflater().inflate(R.menu.report_menu_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        writeLog("onOptionsItemSelected Called");
        switch (item.getItemId()) {
            case android.R.id.home:
                //showDia(0);
                new Constant(ReportMenuActivity.this).doFinish();
                break;
            case R.id.export:
                startActivity(new Intent(ReportMenuActivity.this,ExportOthersActivity.class));
               // showDia(1);
              //  new Constant(ReportMenuActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
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

    private void showDia(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportMenuActivity.this);
        builder.setCancelable(false);
         if (id == 0) {
            builder.setMessage("Do You Want to Logout?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new Constant(ReportMenuActivity.this).doFinish();
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }else if (id == 1) {
             builder.setMessage("Do You Want to Export Reports to Excel?");
             builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialogInterface, int i) {
                     //new Constant(ReportMenuActivity.this).doFinish();
                     startActivity(new Intent(ReportMenuActivity.this,ExportOthersActivity.class));
                     dialogInterface.dismiss();
                 }
             });
             builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialogInterface, int i) {
                     dialogInterface.dismiss();
                 }
             });
         }
        builder.create().show();
    }

    private void writeLog(String _data) {
        new WriteLog().writeLog(getApplicationContext(), "ReportMenuActivity_" + _data);
    }
}
