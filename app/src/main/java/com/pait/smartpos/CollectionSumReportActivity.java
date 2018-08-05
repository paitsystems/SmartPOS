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

import com.pait.smartpos.adpaters.CollectionSumAdapter;
import com.pait.smartpos.adpaters.ExpenseAdapter;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.log.WriteLog;
import com.pait.smartpos.model.CollectionClass;
import com.pait.smartpos.model.CollectionClassR;
import com.pait.smartpos.model.DailyPettyExpClass;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CollectionSumReportActivity extends AppCompatActivity implements View.OnClickListener{
    private ListView listView;
    private TextView tv_total_amt;
    private DBHandler db;
    private double totnetcoll=0,totsalecash=0,totcashback=0,totnetcash=0,totcheque=0,totothpayamt=0;
    private DecimalFormat flt_price;
    private TextView  tv_fromdate, tv_todate,tv_total_netcoll,tv_total_salecash,tv_total_cashback,tv_total_netcash,
            tv_total_cheque,tv_total_card,tv_total_opa,tv_total_expreceipt,tv_total_exppay;
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
        setContentView(R.layout.activity_collection_sum_report);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("Collection Summary Report");
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
        db = new DBHandler(CollectionSumReportActivity.this);
        flt_price = new DecimalFormat();
        flt_price.setMinimumFractionDigits(2);
        tv_fromdate = (TextView) findViewById(R.id.tv_fromdate);
        tv_todate = (TextView) findViewById(R.id.tv_todate);
        cb_all = findViewById(R.id.cb_all);
        constant = new Constant(CollectionSumReportActivity.this);
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        btn_show = findViewById(R.id.btn_show);
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);

        tv_total_netcoll = (TextView) findViewById(R.id.tv_total_netcoll);
        tv_total_salecash= (TextView) findViewById(R.id.tv_total_salecash);
        tv_total_cashback = (TextView) findViewById(R.id.tv_total_cashback);
        tv_total_netcash = (TextView) findViewById(R.id.tv_total_netcash);
        tv_total_cheque = (TextView) findViewById(R.id.tv_total_cheque);
        tv_total_card = (TextView) findViewById(R.id.tv_total_card);
        tv_total_opa = (TextView) findViewById(R.id.tv_total_opa);
        tv_total_expreceipt = (TextView) findViewById(R.id.tv_total_expreceipt);
        tv_total_exppay = (TextView) findViewById(R.id.tv_total_exppay);
    }

    private void setData(String fadate,String tadate) {
       //Cashier,Net Collection,Sale Cash,CashBack, Net Cash,Cheque,CRRef.Cheque,Card,Other,
        // CN Redeem,Exp Receipt, Exp Payment
        listView.setAdapter(null);
        Cursor c = db.getCollectionSumData(fadate,tadate,flag);
        List<CollectionClass> list = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                CollectionClass coll = new CollectionClass();
                coll.setCashier(c.getString(0));
                coll.setNetCollection(c.getString(1));
                coll.setSaleCash(c.getString(2));
                coll.setCashback(c.getString(3));
                coll.setNetcash(c.getString(4));
                coll.setCheck(c.getString(5));
                coll.setCRRef_Cheque(c.getString(6));
                coll.setCard(c.getString(7));
                coll.setOtherPayAmt(c.getString(8));
                coll.setCN_Redeem(c.getString(9));
                coll.setExp_receipt(c.getString(10));
                coll.setExp_payment(c.getString(11));
                list.add(coll);
            } while (c.moveToNext());
        }

        CollectionSumAdapter adapter = new CollectionSumAdapter(list, getApplicationContext());
        listView.setAdapter(adapter);
        //setTotal(list);                             //no need
        if(list.size() == 0){
            toast.setText("Data Not Available..");
            toast.show();
        }
        db.close();
        c.close();
        //setTotal(list);

    }

    private void setTotal(List<CollectionClass> list) {
        totnetcoll=0;
        totsalecash=0;
        totcashback=0;
        totnetcash=0;
        totcheque=0;
        totothpayamt=0;

        for (CollectionClass eClass : list) {

        }

        //tv_total_amt.setText(flt_price.format(totAmt));
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
        new WriteLog().writeLog(getApplicationContext(), "CollectionSumReportActivity_" + _data);
    }
}
