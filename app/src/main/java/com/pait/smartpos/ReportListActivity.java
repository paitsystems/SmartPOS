package com.pait.smartpos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

public class ReportListActivity extends AppCompatActivity {

    private Button btn1_collection_report, btn2_itemwise_sale, btn3_todays_sale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Reports");
        }

        init();
        
        btn1_collection_report.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(),Repo_Collection_Activity.class));
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });

        btn2_itemwise_sale.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(),Repo_Itemwise_SaleActivity.class));
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });

        btn3_todays_sale.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(),Repo_Todays_SaleActivity.class));
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                Intent intent = new Intent(getApplicationContext(), DrawerTestActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(getApplicationContext(), DrawerTestActivity.class);
        startActivity(intent);
    }

    private void init(){
        btn1_collection_report = findViewById(R.id.btn1_collection_report);
        btn2_itemwise_sale = findViewById(R.id.btn2_itemwise_sale);
        btn3_todays_sale = findViewById(R.id.btn3_todays_sale);
    }
}
