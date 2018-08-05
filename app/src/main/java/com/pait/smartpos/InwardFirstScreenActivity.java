package com.pait.smartpos;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.log.WriteLog;
import com.pait.smartpos.model.DailyPettyExpClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InwardFirstScreenActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_next;
    private TextView tv_inward_date,tv_invoice_date;
    private EditText ed_inv_no,ed_remark;
    private DBHandler db;
    private Constant constant;
    private AutoCompleteTextView auto_supplier;
    private Toast toast;
    private Calendar cal = Calendar.getInstance();
    private static final int vdt = 1,wdt = 2;
    private int day, month, year;
    private String  current_date = "", curr_time = "";
    private List<String> suppList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inward_first_screen);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("Inward");
        }

        init();

        setSupplier();

        try {
            current_date = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).parse(constant.getDate()));
            tv_inward_date.setText(current_date);
            tv_invoice_date.setText(current_date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tv_inward_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(wdt);
            }
        });

        tv_invoice_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(vdt);
            }
        });

        auto_supplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auto_supplier.setThreshold(0);
                auto_supplier.clearListSelection();
                auto_supplier.showDropDown();
            }
        });
        btn_next.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_next:
                if(auto_supplier.getText().toString().equals("")){
                    toast.setText("Please,Enter Supplier");
                    toast.show();
                }else if(ed_inv_no.getText().toString().equals("")){
                    toast.setText("Please,Enter Invoice No");
                    toast.show();
                }else {
                    Intent intent = new Intent(InwardFirstScreenActivity.this, InwardActivity.class);
                    intent.putExtra("supplier", auto_supplier.getText().toString());
                    intent.putExtra("invoice_no", ed_inv_no.getText().toString());
                    intent.putExtra("invoice_date", tv_invoice_date.getText().toString());
                    intent.putExtra("inward_date", tv_inward_date.getText().toString());
                    intent.putExtra("remark", ed_remark.getText().toString());
                    startActivity(intent);
                }
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
        new Constant(InwardFirstScreenActivity.this).doFinish();
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
                new Constant(InwardFirstScreenActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case wdt:
                return new DatePickerDialog(this, wDate, year, month, day);
            case vdt:
                return new DatePickerDialog(this, vDate, year, month, day);

        }
        return null;
    }

    private void init() {
        db = new DBHandler(InwardFirstScreenActivity.this);
        constant = new Constant(InwardFirstScreenActivity.this);
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        tv_invoice_date = findViewById(R.id.tv_invoice_date);
        tv_inward_date = findViewById(R.id.tv_inward_date);
        ed_inv_no = findViewById(R.id.ed_inv_no);
        ed_remark = findViewById(R.id.ed_remark);
        auto_supplier = findViewById(R.id.auto_supplier);
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        btn_next = findViewById(R.id.btn_next);
        suppList = new ArrayList<>();
    }

    private void setSupplier(){
        auto_supplier.setAdapter(null);
        Cursor res = db.getDistinctSupplier();
        if(res != null){
            if (res.moveToFirst()){
                do{
                    suppList.add(res.getString(res.getColumnIndex(DBHandler.SM_Name)));
                }while (res.moveToNext());
            }
        }
        res.close();   //todo uncomment for live data

        ArrayAdapter adapter = new ArrayAdapter<>(getApplicationContext(),R.layout.adapter_item,suppList);
        auto_supplier.setAdapter(adapter);
    }

    private DatePickerDialog.OnDateSetListener wDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            try {
                Date mtodaydate = Calendar.getInstance().getTime();
                Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                //tv_nxt_fdate.setText( Constant.format_display.format(date));
                if (date.compareTo(mtodaydate) < 0) {
                    Log.d("Log", date.toString());
                    Log.d("LOG", mtodaydate.toString());
                    tv_inward_date.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(date));
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
                    tv_invoice_date.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(date));
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
        AlertDialog.Builder builder = new AlertDialog.Builder(InwardFirstScreenActivity.this);
        builder.setCancelable(false);
        if (id == 1) {

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

        } else if (id == 1) {
            builder.setMessage("Do You Want to Logout?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new Constant(InwardFirstScreenActivity.this).doFinish();
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
        new WriteLog().writeLog(getApplicationContext(), "InwardFirstScreenActivity_" + _data);
    }
}
