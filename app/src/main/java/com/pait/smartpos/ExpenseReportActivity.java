package com.pait.smartpos;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pait.smartpos.adpaters.ExpenseAdapter;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.log.WriteLog;
import com.pait.smartpos.model.ExpenseDetail;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseReportActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView listView;
    private TextView tv_total_amt;
    private DBHandler db;
    private double totAmt=0;
    private DecimalFormat flt_price;
    private Button  btn_fromdate, btn_todate;
    private CheckBox cb_all;
    private String currentdate;
    private Constant constant;
    private int day, month, year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_report);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setTitle();
        }

        init();
        currentdate = constant.getDate();
        btn_fromdate.setText(currentdate);
        btn_todate.setText(currentdate);

        btn_fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(1);
            }
        });

        btn_todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(2);
            }
        });

        cb_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_all.isChecked()) {
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

        setData();
    }

    @Override
    public void onClick(View view) {

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1:
                return new DatePickerDialog(this, _from_date, year, month, day);
            case 2:
                return new DatePickerDialog(this, _to_date, year, month, day);
        }
        return null;
    }

    private void init(){
        listView = findViewById(R.id.listView);
        tv_total_amt = findViewById(R.id.tv_total_amt);
        db = new DBHandler(ExpenseReportActivity.this);
        flt_price = new DecimalFormat();
        flt_price.setMinimumFractionDigits(2);
        btn_fromdate = (Button) findViewById(R.id.btn_from_date);
        btn_todate = (Button) findViewById(R.id.btn_to_date);
        cb_all = findViewById(R.id.cb_all);
        constant = new Constant(ExpenseReportActivity.this);
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
    }

    private void setData() {
        listView.setAdapter(null);
        Cursor c = db.getExpenseReportData();
        List<ExpenseDetail> list = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                ExpenseDetail exp = new ExpenseDetail();
                exp.setRemark(c.getString(c.getColumnIndex(DBHandler.EXM_Remark)));
                exp.setAmount(c.getFloat(c.getColumnIndex(DBHandler.EXM_Costcentre)));
                exp.setDate(c.getString(c.getColumnIndex(DBHandler.EXM_Createddate)));
               // exp.setTime(c.getString(c.getColumnIndex(DBHandler.EXM_Remark)));
                list.add(exp);
            } while (c.moveToNext());
        }

        ExpenseAdapter adapter = new ExpenseAdapter(list,getApplicationContext());
        listView.setAdapter(adapter);
        setTotal(list);
        db.close();
        c.close();
        //setTotal(list);
    }

    private void setTotal(List<ExpenseDetail> list) {
        totAmt = 0;

        for (ExpenseDetail eClass : list) {
            totAmt = totAmt + eClass.getAmount();
        }

        tv_total_amt.setText(flt_price.format(totAmt));
    }

    private DatePickerDialog.OnDateSetListener _from_date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            try {
                String selectedDate = day + "/" + (month + 1) + "/" + year;
                //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date sdate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(selectedDate);
                Date tdate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(btn_todate.getText().toString());
                Date cdate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(currentdate);
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
                writeLog("DatePickerDialog.OnDateSetListener_from_date_exception" + e.getMessage());
            }
        }
    };

    private DatePickerDialog.OnDateSetListener _to_date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            try {
                String selectedDate = day + "/" + (month + 1) + "/" + year;
                // SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date sdate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(selectedDate);
                Date fdate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(btn_fromdate.getText().toString());
                Date cdate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(currentdate);
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
                writeLog("DatePickerDialog.OnDateSetListener_to_date_exception" + e.getMessage());
            }
        }
    };

    private void writeLog(String _data) {
        new WriteLog().writeLog(getApplicationContext(), "ExpenseReportActivity_" + _data);
    }
}
