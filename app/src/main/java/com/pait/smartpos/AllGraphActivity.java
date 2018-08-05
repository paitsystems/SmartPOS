package com.pait.smartpos;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pait.smartpos.adpaters.ViewPagerAdapter;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.fragments.AllGraphFragment1;
import com.pait.smartpos.fragments.AllGraphFragment2;
import com.pait.smartpos.log.WriteLog;
import com.pait.smartpos.fragments.AllGraphFragment3;

public class AllGraphActivity extends AppCompatActivity implements View.OnClickListener {

    private Constant constant, constant1;
    private Toast toast;
    private TabLayout tabLayout;
    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_graph);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        init();
        setViewPager();
        tabLayout.setupWithViewPager(pager);

        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#24bc96"));
        tabLayout.setSelectedTabIndicatorHeight((int) (5 * getResources().getDisplayMetrics().density));
        tabLayout.setTabTextColors(Color.parseColor("#d6157c"), Color.parseColor("#ffffff"));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case 0:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //showDia(0);
        new Constant(AllGraphActivity.this).doFinish();
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
                new Constant(AllGraphActivity.this).doFinish();
                break;
            case R.id.export:
                startActivity(new Intent(AllGraphActivity.this, ExportOthersActivity.class));
                // showDia(1);
                //  new Constant(ReportMenuActivity.this).doFinish();
                break;
            case R.id.report:
                startActivity(new Intent(AllGraphActivity.this, ReportMenuActivity.class));
                // showDia(1);
                //  new Constant(ReportMenuActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setViewPager() {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllGraphFragment1(), "Collection Summary");
        adapter.addFragment(new AllGraphFragment2(), "Productwise Stock");
        adapter.addFragment(new AllGraphFragment3(), "Expense Report");
        pager.setAdapter(adapter);
    }

    private void init() {
        tabLayout = (TabLayout) findViewById(R.id.tab);
        pager = (ViewPager) findViewById(R.id.pager);
    }

    private void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AllGraphActivity.this);
        if (a == 0) {
            builder.setMessage("Do You Want To Exit App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Constant(AllGraphActivity.this).doFinish();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        builder.create().show();
    }

    private void writeLog(String _data) {
        new WriteLog().writeLog(getApplicationContext(), "AllGraphActivity_" + _data);
    }
}