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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pait.smartpos.adpaters.ExpenseAdapter;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.log.WriteLog;
import com.pait.smartpos.model.DailyPettyExpClass;
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
    private TextView  tv_fromdate, tv_todate;
    private CheckBox cb_all;
    private String currentdate;
    private Constant constant;
    private int day, month, year;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    private SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
    private Button btn_show;
    private int flag = 0;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_report);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("Expense Report");
        }

        init();

        try {
            currentdate = sdf.format(sdf1.parse(constant.getDate()));
            tv_fromdate.setText(currentdate);
            tv_todate.setText(currentdate);
        }catch (Exception e){
            e.printStackTrace();
        }

        tv_fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(1);
            }
        });

        tv_todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(2);
            }
        });

        cb_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cb_all.isChecked()) {
                    flag = 1;
                    tv_fromdate.setFocusable(false);
                    tv_fromdate.setClickable(false);
                    tv_todate.setFocusable(false);
                    tv_todate.setClickable(false);
                } else {
                    flag = 0;
                    tv_fromdate.setFocusable(true);
                    tv_fromdate.setClickable(true);
                    tv_todate.setFocusable(true);
                    tv_todate.setClickable(true);
                }
            }
        });
        try {
            String fadate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(sdf.parse(currentdate));
            String tadate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(sdf.parse(currentdate));
            setData(fadate,tadate);
        }catch (Exception e){
            e.printStackTrace();
        }
        btn_show.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_show:
                try {
                    String fadate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(sdf.parse(tv_fromdate.getText().toString()));
                    String tadate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(sdf.parse(tv_todate.getText().toString()));
                    setData(fadate,tadate);
                }catch (Exception e){
                    e.printStackTrace();
                }
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
        tv_fromdate = (TextView) findViewById(R.id.tv_fromdate);
        tv_todate = (TextView) findViewById(R.id.tv_todate);
        cb_all = findViewById(R.id.cb_all);
        constant = new Constant(ExpenseReportActivity.this);
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        btn_show = findViewById(R.id.btn_show);
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
    }

    private String parseDate(String date){
        String str = "";
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
            str = new SimpleDateFormat("dd/MMM", Locale.ENGLISH).format(d);
        }catch (Exception e){
            e.printStackTrace();
        }
        return str;
    }

    private void setData(String fadate,String tadate) {
            listView.setAdapter(null);
            Cursor c = db.getExpenseReportData(fadate,tadate,flag);
            List<DailyPettyExpClass> list = new ArrayList<>();
            if (c.moveToFirst()) {
                do {
                    DailyPettyExpClass exp = new DailyPettyExpClass();
                    exp.setRemark(c.getString(c.getColumnIndex(DBHandler.DPE_Remark)));
                    exp.setAmount(c.getFloat(c.getColumnIndex(DBHandler.DPE_Amount)));
                    String expDate = c.getString(c.getColumnIndex(DBHandler.DPE_Date));
                    exp.setDate(parseDate(expDate));
                    exp.setExpHead(c.getInt(c.getColumnIndex(DBHandler.DPE_Exphead)));
                    // exp.setTime(c.getString(c.getColumnIndex(DBHandler.EXM_Remark)));
                    list.add(exp);
                } while (c.moveToNext());
            }

            ExpenseAdapter adapter = new ExpenseAdapter(list, getApplicationContext());
            listView.setAdapter(adapter);
            setTotal(list);
            if(list.size() == 0){
              toast.setText("Data Not Available..");
              toast.show();
            }
            db.close();
            c.close();
            //setTotal(list);
    }

    private void setTotal(List<DailyPettyExpClass> list) {
        totAmt = 0;

        for (DailyPettyExpClass eClass : list) {
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
                Date sdate = sdf.parse(selectedDate);
                Date tdate = sdf.parse(tv_fromdate.getText().toString());
                Date cdate = sdf.parse(currentdate);
                Toast toast = Toast.makeText(getApplicationContext(), "Please Select Proper Date", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);

                if (sdate.compareTo(cdate) <= 0) {
                    if (sdate.compareTo(tdate) <= 0) {
                        tv_fromdate.setText(selectedDate);
                    } else {
                        toast.show();
                        tv_fromdate.setText(currentdate);
                        tv_todate.setText(currentdate);
                    }
                } else {
                    toast.show();
                    tv_fromdate.setText(currentdate);
                    tv_todate.setText(currentdate);
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
                Date sdate = sdf.parse(selectedDate);
                Date fdate = sdf.parse(tv_fromdate.getText().toString());
                Date cdate = sdf.parse(currentdate);
                Toast toast = Toast.makeText(getApplicationContext(), "Please Select Proper Date", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);

                if (sdate.compareTo(cdate) <= 0) {
                    if (sdate.compareTo(fdate) >= 0) {
                        tv_todate.setText(selectedDate);
                        Constant.showLog("selectedDate:" + selectedDate);
                    } else {
                        toast.show();
                        tv_todate.setText(currentdate);
                    }
                } else {
                    toast.show();
                    tv_todate.setText(currentdate);
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
