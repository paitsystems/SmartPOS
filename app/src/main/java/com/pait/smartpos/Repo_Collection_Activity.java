package com.pait.smartpos;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.adpaters.CollectionReportAdapter;
import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.model.CollectionClass;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Repo_Collection_Activity extends AppCompatActivity implements View.OnClickListener {

    private CheckBox cb_filter_all, cb_sync_alldate, cb_branch, cb_supplier, cb_item, cb_brand;
    private Button btn_filter, btn_fromdate, btn_todate, btn_sync, btn_show;
    private TextView tv_imp_date, tv_filter_fromdate, tv_filter_todate,
            tv_head_bill_no, tv_head_date, tv_head_time, tv_head_employee, tv_head_tot_sale,
            tv_head_net_amnt, tv_head_cash_amnt, tv_head_card_amnt, tv_total_bill_no, tv_total_date,
            tv_total_time, tv_total_employee, tv_total_tot_sale, tv_total_net_amnt, tv_total_cash_amnt, tv_total_card_amnt;
    private int day, month, year;
    private String currentdate, filter = "", name_of_cols = "", pref_from_date, pref_to_date, cballstr;
    private ScrollView filter_lay, sync_lay;
    private Animation animation, animation1;
    private DBHandlerR db;
    private List<String> branch_list, supplier_list, item_list, brand_list, cost_center_list;
    private AutoCompleteTextView auto_branch, auto_supplier, auto_item, auto_brand, auto_cost_center;
    private DecimalFormat flt_price;
    private ProgressDialog pDialog;
    private String writeFilename = "Write.txt";
    private ListView listView;
    private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    private RadioButton rdo_details, rdo_summary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo__collection_);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Collection Report");
        }
        init();

        btn_show.setOnClickListener(this);
        btn_filter.setOnClickListener(this);
        btn_fromdate.setOnClickListener(this);
        btn_todate.setOnClickListener(this);
        btn_sync.setOnClickListener(this);
        rdo_details.setOnClickListener(this);
        rdo_summary.setOnClickListener(this);

        cb_filter_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_filter_all.isChecked()) {
                    btn_fromdate.setFocusable(false);
                    btn_fromdate.setClickable(false);
                    btn_todate.setFocusable(false);
                    btn_todate.setClickable(false);
                } else {
                    btn_fromdate.setFocusable(true);
                    btn_fromdate.setClickable(true);
                    btn_todate.setFocusable(true);
                    btn_todate.setClickable(true);
                }
            }
        });

        synchideShow();

        /*if(VerificationActivity.pref.contains("itwscball")) {
            pref_from_date = VerificationActivity.pref.getString("itwsfromdate", "");
            pref_to_date = VerificationActivity.pref.getString("itwstodate", "");
            cballstr = VerificationActivity.pref.getString("itwscball", "");
            if (cballstr.equals("Y")) {
                String s = "All Data Imported";
                tv_imp_date.setText(s);
            } else {
                String s = pref_from_date+" - To - "+pref_to_date;
                tv_imp_date.setText(s);
            }
            showDia(5);
        }else{
            String s = "No Data Imported";
            tv_imp_date.setText(s);
            synchideShow();
        }*/

        auto_branch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if (actionID == EditorInfo.IME_ACTION_NEXT) {
                    auto_supplier.requestFocus();
                }
                return true;
            }
        });

        auto_supplier.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if (actionID == EditorInfo.IME_ACTION_NEXT) {
                    auto_item.requestFocus();
                }
                return true;
            }
        });

        auto_item.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if (actionID == EditorInfo.IME_ACTION_NEXT) {
                    auto_brand.requestFocus();
                }
                return true;
            }
        });

        auto_brand.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if (actionID == EditorInfo.IME_ACTION_NEXT) {
                    auto_cost_center.requestFocus();
                }
                return true;
            }
        });

        auto_cost_center.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if (actionID == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager input = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                    input.hideSoftInputFromWindow(auto_cost_center.getWindowToken(), 0);
                }
                return true;
            }
        });

        tv_filter_fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(tv_filter_fromdate.getWindowToken(), 0);
                showDialog(3);
            }
        });

        tv_filter_todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(tv_filter_todate.getWindowToken(), 0);
                showDialog(4);
            }
        });

        cb_sync_alldate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_sync_alldate.isChecked()) {
                    tv_filter_fromdate.setEnabled(false);
                    tv_filter_todate.setEnabled(false);
                } else {
                    tv_filter_fromdate.setEnabled(true);
                    tv_filter_todate.setEnabled(true);
                }
            }
        });

        Constant.showLog("Repo_Collection_Activity");
    }

    @Override
    public void onBackPressed() {
        finish();
        //overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.collection_report_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sync:
                synchideShow();
                break;
            case android.R.id.home:
                finish();
                //overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1:
                return new DatePickerDialog(this, _from_date, year, month, day);
            case 2:
                return new DatePickerDialog(this, _to_date, year, month, day);
            case 3:
                return new DatePickerDialog(this, _tv_fromdate, year, month, day);
            case 4:
                return new DatePickerDialog(this, _tv_todate, year, month, day);
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sync:
                btn_SyncFun();
                break;
            case R.id.btn_filter:
                btn_filterFun();
                break;
            case R.id.btn_show:
                btn_ShowFun();
                break;
            case R.id.btn_from_date:
                btn_fromDateFun();
                break;
            case R.id.btn_to_date:
                btn_toDateFun();
                break;
            case R.id.rdo_details:
                rdo_summary.setChecked(false);
                rdo_details.setChecked(true);
                break;
            case R.id.rdo_summary:
                rdo_details.setChecked(false);
                rdo_summary.setChecked(true);
                break;
        }
    }

    private void init() {
        cb_filter_all = findViewById(R.id.cb_filter_all);
        cb_sync_alldate = findViewById(R.id.cb_sync_alldate);
        cb_sync_alldate.setChecked(true);
        cb_branch = findViewById(R.id.cb_branch);
        cb_supplier = findViewById(R.id.cb_supplier);
        cb_item = findViewById(R.id.cb_item);
        cb_brand = findViewById(R.id.cb_brand);

        tv_imp_date = findViewById(R.id.tv_imp_date);
        tv_head_bill_no = findViewById(R.id.tv_head_bill_no);
        tv_head_date = findViewById(R.id.tv_head_date);
        tv_head_time = findViewById(R.id.tv_head_time);
        tv_head_employee = findViewById(R.id.tv_head_employee);
        tv_head_tot_sale = findViewById(R.id.tv_head_tot_sale);
        tv_head_net_amnt = findViewById(R.id.tv_head_net_amnt);
        tv_head_cash_amnt = findViewById(R.id.tv_head_cash_amnt);
        tv_head_card_amnt = findViewById(R.id.tv_head_card_amnt);

        tv_total_bill_no = findViewById(R.id.tv_total_bill_no);
        tv_total_date = findViewById(R.id.tv_total_date);
        tv_total_time = findViewById(R.id.tv_total_time);
        tv_total_employee = findViewById(R.id.tv_total_employee);
        tv_total_tot_sale = findViewById(R.id.tv_total_tot_sale);
        tv_total_net_amnt = findViewById(R.id.tv_total_net_amnt);
        tv_total_cash_amnt = findViewById(R.id.tv_total_cash_amnt);
        tv_total_card_amnt = findViewById(R.id.tv_total_card_amnt);

        btn_fromdate = findViewById(R.id.btn_from_date);
        btn_todate = findViewById(R.id.btn_to_date);
        btn_sync = findViewById(R.id.btn_sync);
        btn_filter = findViewById(R.id.btn_filter);
        btn_show = findViewById(R.id.btn_show);

        filter_lay = findViewById(R.id.filter_lay);
        sync_lay = findViewById(R.id.sync_lay);

        rdo_details = findViewById(R.id.rdo_details);
        rdo_summary = findViewById(R.id.rdo_summary);

        animation = AnimationUtils.loadAnimation(Repo_Collection_Activity.this, R.anim.up_down);
        animation1 = AnimationUtils.loadAnimation(Repo_Collection_Activity.this, R.anim.down_up);

        flt_price = new DecimalFormat();
        flt_price.setMaximumFractionDigits(2);

        auto_branch = findViewById(R.id.auto_branch);
        auto_supplier = findViewById(R.id.auto_supplier);
        auto_item = findViewById(R.id.auto_item);
        auto_brand = findViewById(R.id.auto_brand);
        auto_cost_center = findViewById(R.id.auto_cost_center);

        listView = findViewById(R.id.listView);

        db = new DBHandlerR(getApplicationContext());

        tv_filter_fromdate = findViewById(R.id.tv_filter_fromdate);
        tv_filter_todate = findViewById(R.id.tv_filter_todate);
        tv_filter_fromdate.setEnabled(false);
        tv_filter_todate.setEnabled(false);

        animation1.reset();
        animation.reset();

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);

        branch_list = new ArrayList<>();
        supplier_list = new ArrayList<>();
        item_list = new ArrayList<>();
        brand_list = new ArrayList<>();
        cost_center_list = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        currentdate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(c.getTime());
        Log.d("Log", currentdate);
        btn_fromdate.setText(currentdate);
        btn_todate.setText(currentdate);

        tv_filter_fromdate.setText(currentdate);
        tv_filter_todate.setText(currentdate);
    }

    private void loadData() {
        /*branch_list.clear();
        supplier_list.clear();
        brand_list.clear();
        item_list.clear();
        cost_center_list.clear();
        listView.setAdapter(null);

        Cursor branch_res = db.setDropDown(.ITWS_BRANCH,.TABLE_ITEMWISE_SALES);
        if(branch_res.moveToFirst()){
            do{
                branch_list.add(branch_res.getString(0));
            }while (branch_res.moveToNext());
        }else{
            branch_list.add("NA");
        }
        branch_res.close();

        Cursor supplier_res = db.setDropDown(.ITWS_SUPPLIER,.TABLE_ITEMWISE_SALES);
        if(supplier_res.moveToFirst()){
            do{
                supplier_list.add(supplier_res.getString(0));
            }while (supplier_res.moveToNext());
        }else{
            supplier_list.add("NA");
        }
        supplier_res.close();

        Cursor brand_res = db.setDropDown(.ITWS_BRAND,.TABLE_ITEMWISE_SALES);
        if(brand_res.moveToFirst()){
            do{
                brand_list.add(brand_res.getString(0));
            }while (brand_res.moveToNext());
        }else{
            brand_list.add("NA");
        }
        brand_res.close();

        Cursor item_res = db.setDropDown(.ITWS_ITEM,.TABLE_ITEMWISE_SALES);
        if(item_res.moveToFirst()){
            do{
                item_list.add(item_res.getString(0));
            }while (item_res.moveToNext());
        }else{
            item_list.add("NA");
        }
        item_res.close();

        Cursor cc_res = db.setDropDown(.ITWS_COSTCENTER,.TABLE_ITEMWISE_SALES);
        if(cc_res.moveToFirst()){
            do{
                cost_center_list.add(cc_res.getString(0));
            }while (cc_res.moveToNext());
        }else{
            cost_center_list.add("NA");
        }
        cc_res.close();

        auto_branch.setAdapter(new ArrayAdapter<>(this,R.layout.cust_drop_down,branch_list));
        *//*auto_branch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    auto_branch.setError(null);
                    auto_branch.showDropDown();
                    auto_branch.setThreshold(0);
                }
                return false;
            }
        });*//*

        auto_branch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auto_branch.setError(null);
                auto_branch.showDropDown();
                auto_branch.setThreshold(0);
            }
        });

        auto_supplier.setAdapter(new ArrayAdapter<>(this,R.layout.cust_drop_down,supplier_list));
        *//*auto_supplier.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    auto_supplier.setError(null);
                    auto_supplier.showDropDown();
                    auto_supplier.setThreshold(0);
                }
                return false;
            }
        });*//*

        auto_supplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auto_supplier.setError(null);
                auto_supplier.showDropDown();
                auto_supplier.setThreshold(0);
            }
        });

        auto_item.setAdapter(new ArrayAdapter<>(this,R.layout.cust_drop_down,item_list));
        *//*auto_item.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    auto_item.setError(null);
                    auto_item.showDropDown();
                    auto_item.setThreshold(0);
                }
                return false;
            }
        });*//*
        auto_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auto_item.setError(null);
                auto_item.showDropDown();
                auto_item.setThreshold(0);
            }
        });

        auto_brand.setAdapter(new ArrayAdapter<>(this,R.layout.cust_drop_down,brand_list));
        *//*auto_brand.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    auto_brand.setError(null);
                    auto_brand.showDropDown();
                    auto_brand.setThreshold(0);
                }
                return false;
            }
        });*//*
        auto_brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auto_brand.setError(null);
                auto_brand.showDropDown();
                auto_brand.setThreshold(0);
            }
        });

        auto_cost_center.setAdapter(new ArrayAdapter<>(this,R.layout.cust_drop_down,cost_center_list));
        auto_cost_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auto_cost_center.setError(null);
                auto_cost_center.showDropDown();
                auto_cost_center.setThreshold(0);
            }
        });

        if(SplashScreen.pref.contains("itwscball")) {
            if (cballstr.equals("Y")) {
                tv_imp_date.setText("All Data Imported");
            } else {
                tv_imp_date.setText(pref_from_date+" - To - "+pref_to_date);
            }
            //setData();
        }else{
            tv_imp_date.setText("No Data Imported");
        }*/
    }

    private void filterhideShow() {
        if (sync_lay.getVisibility() != View.GONE) {
            sync_lay.clearAnimation();
            sync_lay.startAnimation(animation1);
            sync_lay.setVisibility(View.GONE);
        }
        if (filter_lay.getVisibility() == View.GONE) {
            filter_lay.clearAnimation();
            filter_lay.startAnimation(animation);
            filter_lay.setVisibility(View.VISIBLE);
        } else {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(btn_filter.getWindowToken(), 0);
            filter_lay.clearAnimation();
            filter_lay.startAnimation(animation1);
            filter_lay.setVisibility(View.GONE);
        }
    }

    private void synchideShow() {
        if (filter_lay.getVisibility() != View.GONE) {
            filter_lay.clearAnimation();
            filter_lay.startAnimation(animation1);
            filter_lay.setVisibility(View.GONE);
        }
        if (sync_lay.getVisibility() == View.GONE) {
            sync_lay.clearAnimation();
            sync_lay.startAnimation(animation);
            sync_lay.setVisibility(View.VISIBLE);
        } else {
            sync_lay.clearAnimation();
            sync_lay.startAnimation(animation1);
            sync_lay.setVisibility(View.GONE);
        }
    }

    private void hideheaderfootercols() {
        tv_head_bill_no.setVisibility(View.GONE);
        tv_head_date.setVisibility(View.GONE);
        tv_head_time.setVisibility(View.GONE);
        tv_head_employee.setVisibility(View.GONE);
        tv_head_tot_sale.setVisibility(View.GONE);
        tv_total_bill_no.setVisibility(View.GONE);
        tv_total_date.setVisibility(View.GONE);
        tv_total_time.setVisibility(View.GONE);
        tv_total_employee.setVisibility(View.GONE);
        tv_total_tot_sale.setVisibility(View.GONE);
    }

    private void setData() {
        try {
            listView.setAdapter(null);
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date fdate1 = sdf1.parse(btn_fromdate.getText().toString());
            Date tdate1 = sdf1.parse(btn_todate.getText().toString());
            String fdate = sdf.format(fdate1);
            String tdate = sdf.format(tdate1);
            boolean bool;
            if (cb_filter_all.isChecked()) {
                cballstr = "Y";
                bool = true;
            } else {
                cballstr = "N";
                bool = false;
            }
            String emp = VerificationActivity.pref.getString(getString(R.string.pref_username), "");
            Cursor c = db.getBillMaster(fdate, tdate, bool);
            double _totalNetAmnt = 0, _totalCashAmnt = 0, _totCardAmnt = 0;
            if (c.moveToFirst()) {
                List<CollectionClass> list = new ArrayList<>();
                do {
                    CollectionClass collection = new CollectionClass();
                    //BillMaster_BillNo, BillMaster_BillDate, BillMaster_BillTime, BillMaster_TotalAmt,
                    // BillMaster_CashAmt, BillMaster_CardAmt
                    collection.setBillNo(c.getString(0));//BillNo
                    collection.setBillDate(c.getString(1));//BillDate
                    collection.setBilltime(c.getString(2));//BillTime
                    collection.setGenBy(emp);//Employee
                    collection.setTotSale(c.getString(3));//TotAmnt
                    collection.setNetAmnt(c.getString(3));//TotNetAmnt
                    collection.setCashAmnt(c.getString(4));//CashAmnt
                    collection.setCardAmnt(c.getString(5));//CardAmnt
                    list.add(collection);
                    _totalNetAmnt = _totalNetAmnt + Double.parseDouble(c.getString(3));
                    _totalCashAmnt = _totalCashAmnt + Double.parseDouble(c.getString(4));
                    _totCardAmnt = _totCardAmnt + Double.parseDouble(c.getString(5));
                } while (c.moveToNext());
                c.close();
                tv_total_tot_sale.setText(flt_price.format(_totalNetAmnt));
                tv_total_net_amnt.setText(flt_price.format(_totalNetAmnt));
                tv_total_cash_amnt.setText(flt_price.format(_totalCashAmnt));
                tv_total_card_amnt.setText(flt_price.format(_totCardAmnt));
                //hideheaderfootercols();
                synchideShow();
                listView.setAdapter(new CollectionReportAdapter(getApplicationContext(), list));
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                tv_total_net_amnt.setText("0");
                tv_total_cash_amnt.setText("0");
                tv_total_card_amnt.setText("0");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDia(int i) {
        //import android.support.v7.app.AlertDialog.Builder;
        AlertDialog.Builder builder = new AlertDialog.Builder(Repo_Collection_Activity.this);
        //builder.setTitle("Alert");
        builder.setCancelable(false);
        if (i == 1) {
            builder.setMessage("Please Try Again");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        } else if (i == 2) {
            //builder.setMessage("Error While Reading File");
            builder.setMessage("No Record Available");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        } else if (i == 3) {
            builder.setMessage("Data Imported Successfully");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    //db.setPeriod(fromdate, todate);
                    if (sync_lay.getVisibility() != View.GONE) {
                        sync_lay.clearAnimation();
                        sync_lay.startAnimation(animation1);
                        sync_lay.setVisibility(View.GONE);
                    }
                    loadData();
                }
            });
        } else if (i == 4) {
            //builder.setMessage("Error While Writing File");
            builder.setMessage("Error While Saving Data");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        } else if (i == 5) {
            builder.setMessage("Do You Want To Refresh Data?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (ConnectivityTest.getNetStat(getApplicationContext())) {
                        String url;
                        try {
                            String fdate = URLEncoder.encode(pref_from_date, "UTF-8");
                            String tdate = URLEncoder.encode(pref_to_date, "UTF-8");
                            if (cballstr.equals("Y")) {
                                url = Constant.ipaddress + "/GetItemwiseSaleReport?FromDate=" + fdate + "&ToDate=" + tdate + "&allDates=Y";
                            } else {
                                url = Constant.ipaddress + "/GetItemwiseSaleReport?FromDate=" + fdate + "&ToDate=" + tdate + "&allDates=N";
                            }
                            Log.d("Log", url);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "You Are Offline", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    loadData();
                }
            });
        }
        builder.create().show();
    }

    private void btn_ShowFun() {
        /*name_of_cols = "";
                if (filter_lay.getVisibility() != View.GONE) {
                    filter_lay.clearAnimation();
                    filter_lay.startAnimation(animation1);
                    filter_lay.setVisibility(View.GONE);
                }
                if (sync_lay.getVisibility() != View.GONE) {
                    sync_lay.clearAnimation();
                    sync_lay.startAnimation(animation1);
                    sync_lay.setVisibility(View.GONE);
                }

                int flag = 0;
                if (cb_branch.isChecked()) {
                    name_of_cols = name_of_cols + .ITWS_BRANCH + ",";
                    flag = 1;
                }
                if (cb_supplier.isChecked()) {
                    name_of_cols = name_of_cols + .ITWS_SUPPLIER + ",";
                    flag = 1;
                }
                if (cb_item.isChecked()) {
                    name_of_cols = name_of_cols + .ITWS_ITEM + ",";
                    flag = 1;
                }
                if (cb_brand.isChecked()) {
                    name_of_cols = name_of_cols + .ITWS_BRAND + ",";
                    flag = 1;
                }
                if (cb_cost_center.isChecked()) {
                    name_of_cols = name_of_cols + .ITWS_COSTCENTER + ",";
                    flag = 1;
                }
                if (flag != 1) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please Select Atleast One Option", Toast.LENGTH_LONG);
                    toast.show();
                    toast.setGravity(Gravity.CENTER, 0, 0);
                } else {
                    name_of_cols = name_of_cols.substring(0, name_of_cols.length() - 1);
                    setData();
                }*/
    }

    private void btn_filterFun() {
                /*filter = "";
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(btn_filter.getWindowToken(), 0);
                if(!auto_branch.getText().toString().isEmpty()){
                    filter = filter + " and "+.ITWS_BRANCH+"='"+auto_branch.getText().toString()+"'";
                }
                if(!auto_supplier.getText().toString().isEmpty()){
                    filter = filter + " and "+.ITWS_SUPPLIER+"='"+auto_supplier.getText().toString()+"'";
                }
                if(!auto_brand.getText().toString().isEmpty()){
                    filter = filter + " and "+.ITWS_BRAND+"='"+auto_brand.getText().toString()+"'";
                }
                if(!auto_item.getText().toString().isEmpty()){
                    filter = filter + " and "+.ITWS_ITEM+"='"+auto_item.getText().toString()+"'";
                }
                if(!auto_brand.getText().toString().isEmpty()){
                    filter = filter + " and "+.ITWS_BRAND+"='"+auto_brand.getText().toString()+"'";
                }
                if(!auto_cost_center.getText().toString().isEmpty()){
                    filter = filter + " and "+.ITWS_COSTCENTER+"='"+auto_cost_center.getText().toString()+"'";
                }
                if(!cb_sync_alldate.isChecked()){
                    try{
                        String _frdate = new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH).format(format.parse(tv_filter_fromdate.getText().toString()));
                        String _tdate = new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH).format(format.parse(tv_filter_todate.getText().toString()));
                        filter = filter + " and " + .ITWS_FILTER_DATE + " between '" + _frdate + "' and '"+_tdate+"'";
                        filterhideShow();
                        //setData();
                        *//*if (db.isRecAvail(.TABLE_CUST_ORDER,.CO_FILTER_DATE,_frdate) != 0 && db.isRecAvail(.TABLE_CUST_ORDER,.CO_FILTER_DATE,_tdate) != 0) {
                            filter = filter + " and " + .CO_FILTER_DATE + " between '" + _frdate + "' and '"+_tdate+"'";
                            filterhideShow();
                            setData();
                        }else{
                            Toast toast = Toast.makeText(getApplicationContext(), "Data Not Available For This Date", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }*//*
                    }catch (ParseException e){
                        e.printStackTrace();
                    }
                }else {
                    filterhideShow();
                    // setData();
                }*/
    }

    private void btn_fromDateFun() {
        showDialog(1);
    }

    private void btn_toDateFun() {
        showDialog(2);
    }

    private void btn_SyncFun() {
        /*if(ConnectivityTest.getNetStat(getApplicationContext())) {
            String url;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
            String fdate = btn_fromdate.getText().toString();
            String tdate = btn_todate.getText().toString();
            try {
                fdate = sdf1.format(sdf.parse(fdate));
                tdate = sdf1.format(sdf.parse(tdate));
                pref_from_date = fdate;
                pref_to_date = tdate;
                fdate = URLEncoder.encode(fdate, "UTF-8");
                tdate = URLEncoder.encode(tdate, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (cb_filter_all.isChecked()) {
                cballstr = "Y";
                url = Constant.ipaddress + "/GetItemwiseSaleReport?FromDate=" + fdate + "&ToDate=" + tdate + "&allDates=Y";
            } else {
                cballstr = "N";
                url = Constant.ipaddress + "/GetItemwiseSaleReport?FromDate=" + fdate + "&ToDate=" + tdate + "&allDates=N";
            }
            Log.d("Log", url);
        }else{
            Toast toast = Toast.makeText(getApplicationContext(),"You Are Offline",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }*/
        setData();
    }

    DatePickerDialog.OnDateSetListener _from_date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            try {
                String selectedDate = day + "/" + (month + 1) + "/" + year;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date sdate = sdf.parse(selectedDate);
                Date tdate = sdf.parse(btn_todate.getText().toString());
                Date cdate = sdf.parse(currentdate);
                Toast toast = Toast.makeText(getApplicationContext(), "Please Select Proper Date", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);

                if (sdate.compareTo(cdate) <= 0) {
                    if (sdate.compareTo(tdate) <= 0) {
                        btn_fromdate.setText(selectedDate);
                    } else {
                        toast.show();
                        btn_fromdate.setText(currentdate);
                        btn_todate.setText(currentdate);
                    }
                } else {
                    toast.show();
                    btn_fromdate.setText(currentdate);
                    btn_todate.setText(currentdate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    DatePickerDialog.OnDateSetListener _to_date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            try {
                String selectedDate = day + "/" + (month + 1) + "/" + year;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date sdate = sdf.parse(selectedDate);
                Date fdate = sdf.parse(btn_fromdate.getText().toString());
                Date cdate = sdf.parse(currentdate);
                Toast toast = Toast.makeText(getApplicationContext(), "Please Select Proper Date", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);

                if (sdate.compareTo(cdate) <= 0) {
                    if (sdate.compareTo(fdate) >= 0) {
                        btn_todate.setText(selectedDate);
                    } else {
                        toast.show();
                        btn_todate.setText(currentdate);
                    }
                } else {
                    toast.show();
                    btn_todate.setText(currentdate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    private DatePickerDialog.OnDateSetListener _tv_fromdate = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int yearSelected, int monthOfYear, int dayOfMonth) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                String _date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + yearSelected;
                Date fdate = sdf.parse(_date);
                Date tdate = sdf.parse(tv_filter_todate.getText().toString());
                Date mCurrent_Date = sdf.parse(currentdate);
                if (fdate.compareTo(mCurrent_Date) <= 0) {
                    if (fdate.compareTo(tdate) > 0) {
                        tv_filter_fromdate.setText(_date);
                        tv_filter_todate.setText(_date);
                    } else if (fdate.compareTo(tdate) <= 0) {
                        tv_filter_fromdate.setText(_date);
                        tv_filter_todate.setText(tv_filter_todate.getText().toString());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Select Proper Date", Toast.LENGTH_LONG).show();
                    tv_filter_fromdate.setText(tv_filter_fromdate.getText().toString());
                    tv_filter_todate.setText(tv_filter_todate.getText().toString());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    private DatePickerDialog.OnDateSetListener _tv_todate = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int yearSelected, int monthOfYear, int dayOfMonth) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                String _date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + yearSelected;
                Date fdate = sdf.parse(tv_filter_fromdate.getText().toString());
                Date tdate = sdf.parse(dayOfMonth + "/" + (monthOfYear + 1) + "/" + yearSelected);
                Date mCurrent_Date = sdf.parse(currentdate);
                if (tdate.compareTo(mCurrent_Date) <= 0) {
                    if (tdate.compareTo(fdate) < 0) {
                        Toast.makeText(getApplicationContext(), "Please Select Proper Date", Toast.LENGTH_LONG).show();
                        tv_filter_todate.setText(currentdate);
                    } else {
                        tv_filter_todate.setText(_date);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Select Proper Date...", Toast.LENGTH_LONG).show();
                    tv_filter_todate.setText(currentdate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

}
