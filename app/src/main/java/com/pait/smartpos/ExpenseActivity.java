package com.pait.smartpos;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.log.WriteLog;
import com.pait.smartpos.model.DailyPettyExpClass;
import com.pait.smartpos.model.ExpenseDetail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseActivity extends AppCompatActivity implements View.OnClickListener {

    private Constant constant, constant1;
    private Toast toast;
    private GoogleApiClient googleApiClient;
    private FloatingActionButton fabEdit, fabDelete, fabAdd;
    private TextView tv_cat, tv_date, bal, expense, income;
    private String selcatName = "", current_date = "", curr_time = "";
    private int flag = 0;
    private Calendar cal = Calendar.getInstance();
    private static final int vdt = 1;
    private int day, month, year;
    private LinearLayout layout_hide, linear_lay, amount_menu_lay, lay_progress, lay_add;
    private AppCompatButton bt_add_money, bt_income, bt_expense, bt_add, bt_100, bt_500, bt_1000, bt_2000, bt_5000, bt_10000,
            bt_home, bt_food, bt_entertainment, bt_education, bt_transport, bt_shopping, bt_medical, bt_savings, bt_borrow,
            bt_other;
    private int money = 0, amt_money, total_money, check = 0;
    private AutoCompleteTextView auto_remark,auto_exphead;
    private DBHandler db;
    private EditText ed_amount;
    private List<DailyPettyExpClass> list;
    private Spinner sp_exphead;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setTitle();
        }
        init();
        try {
            current_date = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).parse(constant.getDate()));
            tv_date.setText(current_date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        lay_add.setOnClickListener(this);
        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(vdt);
            }
        });
        get_auto_remark();
        auto_remark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auto_remark.setThreshold(0);
                auto_remark.clearListSelection();
                auto_remark.showDropDown();
            }
        });

        moneyMenu();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lay_add:
                showDia(6);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        //showDia(0);
        new Constant(ExpenseActivity.this).doFinish();
        writeLog("onBackPressed Called");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        writeLog("onCreateOptionsMenu Called");
        //getMenuInflater().inflate(R.menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        writeLog("onOptionsItemSelected Called");
        switch (item.getItemId()) {
            case android.R.id.home:
                //showDia(0);
                new Constant(ExpenseActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case vdt:
                return new DatePickerDialog(this, vDate, year, month, day);

        }
        return null;
    }

    private void init() {
        db = new DBHandler(ExpenseActivity.this);
        constant = new Constant(ExpenseActivity.this);
        constant1 = new Constant(getApplicationContext());
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        tv_date = (TextView) findViewById(R.id.tv_date);
        ed_amount = (EditText) findViewById(R.id.ed_amount);
        auto_exphead = (AutoCompleteTextView) findViewById(R.id.auto_exphead);
        amount_menu_lay = (LinearLayout) findViewById(R.id.amount_menu);
        lay_add = (LinearLayout) findViewById(R.id.lay_add);
        bt_add_money = (AppCompatButton) findViewById(R.id.bt_addmoney);
        bt_100 = (AppCompatButton) findViewById(R.id.bt_100);
        bt_500 = (AppCompatButton) findViewById(R.id.bt_500);
        bt_1000 = (AppCompatButton) findViewById(R.id.bt_1000);
        bt_2000 = (AppCompatButton) findViewById(R.id.bt_2000);
        bt_5000 = (AppCompatButton) findViewById(R.id.bt_5000);
        bt_10000 = (AppCompatButton) findViewById(R.id.bt_10000);
        auto_remark = findViewById(R.id.auto_remark);
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        list = new ArrayList<>();
        sp_exphead = findViewById(R.id.sp_exphead);
    }

    private void moneyMenu() {
        bt_100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt_100.setBackgroundResource(R.color.red);
                bt_500.setBackgroundResource(R.color.lightblue);
                bt_1000.setBackgroundResource(R.color.lightblue);
                bt_2000.setBackgroundResource(R.color.lightblue);
                bt_5000.setBackgroundResource(R.color.lightblue);
                bt_10000.setBackgroundResource(R.color.lightblue);
                bt_100.setPadding(2, 2, 2, 2);
                bt_500.setPadding(2, 2, 2, 2);
                bt_1000.setPadding(2, 2, 2, 2);
                bt_2000.setPadding(2, 2, 2, 2);
                bt_5000.setPadding(2, 2, 2, 2);
                bt_10000.setPadding(2, 2, 2, 2);

                //ed_amount.setText(bt_100.getText().toString());
                amt_money = 100;
                if (ed_amount.getText().toString().equals("")) {
                    money = 0;
                } else {
                    money = Integer.parseInt(ed_amount.getText().toString());
                }
                total_money = money + amt_money;
                ed_amount.setText(String.valueOf(total_money));

            }
        });

        bt_500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bt_500.setBackgroundColor(getResources().getColor(R.color.red));
                bt_100.setBackgroundColor(getResources().getColor(R.color.lightblue));
                bt_1000.setBackgroundColor(getResources().getColor(R.color.lightblue));
                bt_2000.setBackgroundColor(getResources().getColor(R.color.lightblue));
                bt_5000.setBackgroundColor(getResources().getColor(R.color.lightblue));
                bt_10000.setBackgroundColor(getResources().getColor(R.color.lightblue));
                bt_100.setPadding(2, 2, 2, 2);
                bt_500.setPadding(2, 2, 2, 2);
                bt_1000.setPadding(2, 2, 2, 2);
                bt_2000.setPadding(2, 2, 2, 2);
                bt_5000.setPadding(2, 2, 2, 2);
                bt_10000.setPadding(2, 2, 2, 2);
                //ed_amount.setText(bt_500.getText().toString());
                amt_money = 500;
                if (ed_amount.getText().toString().equals("")) {
                    money = 0;
                } else {
                    money = Integer.parseInt(ed_amount.getText().toString());
                }
                total_money = money + amt_money;
                ed_amount.setText(String.valueOf(total_money));
                /*if (add == 0){
                    money = Integer.parseInt(ed_amount.getText().toString());
                    total_money = amt_money + money;
                    Log.d("Log","total_money:"+total_money);
                    ed_amount.setText(String.valueOf(total_money));
                }*/
            }
        });

        bt_1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bt_1000.setBackgroundResource(R.color.red);
                bt_100.setBackgroundResource(R.color.lightblue);
                bt_500.setBackgroundResource(R.color.lightblue);
                bt_2000.setBackgroundResource(R.color.lightblue);
                bt_5000.setBackgroundResource(R.color.lightblue);
                bt_10000.setBackgroundResource(R.color.lightblue);
                bt_100.setPadding(2, 2, 2, 2);
                bt_500.setPadding(2, 2, 2, 2);
                bt_1000.setPadding(2, 2, 2, 2);
                bt_2000.setPadding(2, 2, 2, 2);
                bt_5000.setPadding(2, 2, 2, 2);
                bt_10000.setPadding(2, 2, 2, 2);
                //ed_amount.setText(bt_1000.getText().toString());
                amt_money = 1000;
                if (ed_amount.getText().toString().equals("")) {
                    money = 0;
                } else {
                    money = Integer.parseInt(ed_amount.getText().toString());
                }
                total_money = money + amt_money;
                ed_amount.setText(String.valueOf(total_money));
                /*if (add == 0){
                    money = Integer.parseInt(ed_amount.getText().toString());
                    total_money = amt_money + money;
                    Log.d("Log","total_money:"+total_money);
                    ed_amount.setText(String.valueOf(total_money));
                }*/
            }
        });

        bt_2000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bt_2000.setBackgroundResource(R.color.red);
                bt_100.setBackgroundResource(R.color.lightblue);
                bt_500.setBackgroundResource(R.color.lightblue);
                bt_1000.setBackgroundResource(R.color.lightblue);
                bt_5000.setBackgroundResource(R.color.lightblue);
                bt_10000.setBackgroundResource(R.color.lightblue);
                bt_100.setPadding(2, 2, 2, 2);
                bt_500.setPadding(2, 2, 2, 2);
                bt_1000.setPadding(2, 2, 2, 2);
                bt_2000.setPadding(2, 2, 2, 2);
                bt_5000.setPadding(2, 2, 2, 2);
                bt_10000.setPadding(2, 2, 2, 2);
                //ed_amount.setText(bt_2000.getText().toString());
                amt_money = 2000;
                if (ed_amount.getText().toString().equals("")) {
                    money = 0;
                } else {
                    money = Integer.parseInt(ed_amount.getText().toString());
                }
                total_money = money + amt_money;
                ed_amount.setText(String.valueOf(total_money));
                /*if (add == 0){
                    money = Integer.parseInt(ed_amount.getText().toString());
                    total_money = amt_money + money;
                    Log.d("Log","total_money:"+total_money);
                    ed_amount.setText(String.valueOf(total_money));
                }*/
            }
        });

        bt_5000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bt_5000.setBackgroundResource(R.color.red);
                bt_100.setBackgroundResource(R.color.lightblue);
                bt_500.setBackgroundResource(R.color.lightblue);
                bt_1000.setBackgroundResource(R.color.lightblue);
                bt_2000.setBackgroundResource(R.color.lightblue);
                bt_10000.setBackgroundResource(R.color.lightblue);
                bt_100.setPadding(2, 2, 2, 2);
                bt_500.setPadding(2, 2, 2, 2);
                bt_1000.setPadding(2, 2, 2, 2);
                bt_2000.setPadding(2, 2, 2, 2);
                bt_5000.setPadding(2, 2, 2, 2);
                bt_10000.setPadding(2, 2, 2, 2);
                //ed_amount.setText(bt_5000.getText().toString());
                amt_money = 5000;
                if (ed_amount.getText().toString().equals("")) {
                    money = 0;
                } else {
                    money = Integer.parseInt(ed_amount.getText().toString());
                }
                total_money = money + amt_money;
                ed_amount.setText(String.valueOf(total_money));
                /*if (add == 0){
                    money = Integer.parseInt(ed_amount.getText().toString());
                     total_money = amt_money + money;
                    Log.d("Log","total_money:"+total_money);
                    ed_amount.setText(String.valueOf(total_money));
                }*/
            }
        });

        bt_10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bt_10000.setBackgroundResource(R.color.red);
                bt_100.setBackgroundResource(R.color.lightblue);
                bt_500.setBackgroundResource(R.color.lightblue);
                bt_1000.setBackgroundResource(R.color.lightblue);
                bt_2000.setBackgroundResource(R.color.lightblue);
                bt_5000.setBackgroundResource(R.color.lightblue);
                bt_100.setPadding(2, 2, 2, 2);
                bt_500.setPadding(2, 2, 2, 2);
                bt_1000.setPadding(2, 2, 2, 2);
                bt_2000.setPadding(2, 2, 2, 2);
                bt_5000.setPadding(2, 2, 2, 2);
                bt_10000.setPadding(2, 2, 2, 2);
                //ed_amount.setText(bt_10000.getText().toString());
                amt_money = 10000;
                if (ed_amount.getText().toString().equals("")) {
                    money = 0;
                } else {
                    money = Integer.parseInt(ed_amount.getText().toString());
                }
                total_money = money + amt_money;
                ed_amount.setText(String.valueOf(total_money));

            }
        });
    }

    private void saveData() {
        list.clear();
        int maxauto = 0;
        if (auto_remark.getText().toString().equals("")) {
            auto_remark.setError("Please enter remark..");
        } else if (ed_amount.getText().toString().equals("")) {
            ed_amount.setError("Please enter amount..");
        } else {
            ed_amount.setError(null);
            auto_remark.setAdapter(null);

            DailyPettyExpClass detail = new DailyPettyExpClass();
            detail.setAmount(Float.valueOf(ed_amount.getText().toString()));

            try {
                String date = new SimpleDateFormat("yyyy-dd-MM", Locale.ENGLISH).format(new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(tv_date.getText().toString()));
                Log.d("Log", "date:" + date);
                detail.setDate(date);
            } catch (Exception e) {
                e.printStackTrace();
            }

            curr_time = constant.getTime();
            Log.d("Log", "curr_time:" + curr_time);
            //detail.settime(curr_time);
            String expHead = sp_exphead.getSelectedItem().toString();
            /*int head = db.getExpHeadAuto(expHead);
            detail.setExpHead(head);*/    //uncomment

            detail.setExpHead(1);    //todo find id of expense head function using expHead
            detail.setRemark(auto_remark.getText().toString());

            maxauto = db.getExpMaxAuto();

            if (maxauto == 0) {
                maxauto = 1;
            } else {
                maxauto = maxauto + 1;
            }
            detail.setAutoid(maxauto);
            list.add(detail);
            db.addExpenseDetail(list);
            toast.setText("Expense added successfully.");
            toast.show();
            ed_amount.setText("");
            auto_remark.setText("");

        }
    }

    private void get_auto_remark(){
        ArrayAdapter adapter,adapter2;
        auto_remark.setAdapter(null);
        List<String> uList = new ArrayList<>();
            Cursor cursor = db.getListExpRemark();
            while (cursor.moveToNext()) {
                uList.add(cursor.getString(cursor.getColumnIndex(DBHandler.DPE_Remark)));
            }
            cursor.close();

        adapter = new ArrayAdapter(getApplicationContext(),R.layout.adapter_item,uList);
        auto_remark.setAdapter(adapter);

        List<String> eList = new ArrayList<>();
       /*
         Cursor cursor1 = db.getListExpRemark();
        while (cursor1.moveToNext()) {
            eList.add(cursor1.getString(cursor1.getColumnIndex(DBHandler.EXM_Expdesc)));
        }
        cursor1.close();*/    //TODO UNCOMENT AND ADD VALUE FROM Table

        eList.add("GIFTS");
        eList.add("OFFICE");
        eList.add("PETROL");
        eList.add("WORK");
        eList.add("TEA");
        eList.add("PEN");
        eList.add("BOOK");
        eList.add("NOTE");
        eList.add("BIRHDAY");

        adapter2 = new ArrayAdapter(getApplicationContext(),R.layout.spinner_item,eList);
        sp_exphead.setAdapter(adapter2);
    }

    private DatePickerDialog.OnDateSetListener vDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            try {
                Date mtodaydate = Calendar.getInstance().getTime();
                Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                //tv_nxt_fdate.setText( Constant.format_display.format(date));
                if (date.compareTo(mtodaydate) < 0) {
                    Log.d("Log", date.toString());
                    Log.d("LOG", mtodaydate.toString());
                    tv_date.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(date));
                } else {
                    toast.setText("Please,Select Date Less Than Today's Date");
                    toast.show();
                    // tv_nxt_fdate.setText( Constant.format_display.format(mtodaydate));
                }
            } catch (Exception e) {
                e.printStackTrace();
                writeLog("datePickerDialog.OnDateSetListener vDate exception:" + e.getMessage());
            }
        }
    };

    private void showDia(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ExpenseActivity.this);
        builder.setCancelable(false);
        if (id == 0) {
            builder.setMessage("Please select category.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        } else if (id == 1) {
            builder.setMessage("Income added successfully.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ed_amount.setText(null);
                    auto_remark.setText(null);
                    dialogInterface.dismiss();
                }
            });
        } else if (id == 2) {
            builder.setMessage("Expense added successfully.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ed_amount.setText(null);
                    auto_remark.setText(null);
                    dialogInterface.dismiss();
                }
            });
        } else if (id == 3) {
            builder.setMessage("Do you want to exit?.");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        } else if (id == 4) {
            builder.setMessage("Please enter remark.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        } else if (id == 5) {
            builder.setMessage("Do You Want to Logout?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new Constant(ExpenseActivity.this).doFinish();
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        } else if (id == 6) {
            builder.setMessage("Do you want to Add Expense?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    saveData();
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
        new WriteLog().writeLog(getApplicationContext(), "ExpenseActivity_" + _data);
    }

}
