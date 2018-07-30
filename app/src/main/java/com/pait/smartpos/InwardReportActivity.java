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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.pait.smartpos.adpaters.CollectionSumAdapter;
import com.pait.smartpos.adpaters.InwardSummeryReportAdapter;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.log.WriteLog;
import com.pait.smartpos.model.CollectionClass;
import com.pait.smartpos.model.DailyPettyExpClass;
import com.pait.smartpos.model.InwardDetailClass;
import com.pait.smartpos.model.InwardMasterClass;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InwardReportActivity extends AppCompatActivity implements View.OnClickListener{

    private Constant constant, constant1;
    private Toast toast;
    private Calendar cal = Calendar.getInstance();
    private static final int vdt = 1;
    private int day, month, year;
    private DBHandler db;
    private AutoCompleteTextView auto_supplier;
    private TextView  tv_fromdate, tv_todate,tv_tot_qty,tv_tot_amnt,tv_tot_cgst,tv_tot_sgst,tv_tot_igst;
    private CheckBox cb_all;
    private String currentdate;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    private SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
    private Button btn_show;
    private int flag = 0;
    private ListView listView;
    private DecimalFormat flt_price;
    private List<String> suppList;
    private double totAmt=0,totqty=0,tot_cgst=0,tot_sgst=0,tot_igst=0;
    private int inwardid = 0,branchid = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inward_report);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Inward Summery Report");
        }

        init();

        setSupplier();

        auto_supplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auto_supplier.setThreshold(0);
                auto_supplier.clearListSelection();
                auto_supplier.showDropDown();
            }
        });

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InwardMasterClass mast = (InwardMasterClass) adapterView.getItemAtPosition(i);
                inwardid = mast.getId();
                branchid = mast.getBranchID();
                showDia(2);
            }
        });
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        //showDia(0);
        new Constant(InwardReportActivity.this).doFinish();
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
                new Constant(InwardReportActivity.this).doFinish();
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
        }
        return null;
    }

    private void init() {
        db = new DBHandler(InwardReportActivity.this);
        constant = new Constant(InwardReportActivity.this);
        constant1 = new Constant(getApplicationContext());
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        auto_supplier = findViewById(R.id.auto_supplier);
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        btn_show = findViewById(R.id.btn_show);
        listView = findViewById(R.id.listView);
        tv_tot_amnt = findViewById(R.id.tv_tot_amnt);
        tv_tot_qty = findViewById(R.id.tv_tot_qty);
        tv_tot_cgst = findViewById(R.id.tv_tot_cgst);
        tv_tot_sgst = findViewById(R.id.tv_tot_sgst);
        tv_tot_igst = findViewById(R.id.tv_tot_igst);
        flt_price = new DecimalFormat();
        flt_price.setMinimumFractionDigits(2);
        tv_fromdate = (TextView) findViewById(R.id.tv_fromdate);
        tv_todate = (TextView) findViewById(R.id.tv_todate);
        cb_all = findViewById(R.id.cb_all);
        suppList = new ArrayList<>();
    }

    private void setData(String fadate,String tadate) {

        String Supplier = auto_supplier.getText().toString();
        int supid = db.getInwardSupplierId(Supplier);
        listView.setAdapter(null);
        Cursor c = db.getInwradMaster(fadate,tadate,flag,Supplier,supid);
        List<InwardMasterClass> list = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                InwardMasterClass mastClass = new InwardMasterClass();

                mastClass.setAutoNo(c.getInt(c.getColumnIndex(DBHandler.IWM_Autono)));
                mastClass.setId(c.getInt(c.getColumnIndex(DBHandler.IWM_Id)));
                mastClass.setInwNo(c.getString(c.getColumnIndex(DBHandler.IWM_Inwno)));
                mastClass.setBranchID(c.getInt(c.getColumnIndex(DBHandler.IWM_Branchid)));
                mastClass.setTotalQty(c.getFloat(c.getColumnIndex(DBHandler.IWM_Totalqty)));
                mastClass.setTotalAmt(c.getFloat(c.getColumnIndex(DBHandler.IWM_Totalamt)));
                mastClass.setNetAmt(c.getFloat(c.getColumnIndex(DBHandler.IWM_Netamt)));
                mastClass.setOrderAmt(c.getFloat(c.getColumnIndex(DBHandler.IWM_Orderamt)));
                mastClass.setBalanceQty(c.getFloat(c.getColumnIndex(DBHandler.IWM_Balanceqty)));
                mastClass.setTotSuppDisAmt(c.getFloat(c.getColumnIndex(DBHandler.IWM_Totsuppdisamt)));
                mastClass.setReplColumns(c.getFloat(c.getColumnIndex(DBHandler.IWM_Replcolumns)));
                mastClass.setDisper(c.getFloat(c.getColumnIndex(DBHandler.IWM_Disper)));
                mastClass.setDisAmt(c.getFloat(c.getColumnIndex(DBHandler.IWM_Disamt)));
                mastClass.setGrossAmt(c.getFloat(c.getColumnIndex(DBHandler.IWM_Grossamt)));
                mastClass.setTotVat(c.getFloat(c.getColumnIndex(DBHandler.IWM_Totvat)));
                mastClass.setOtherAdd(c.getFloat(c.getColumnIndex(DBHandler.IWM_Otheradd)));
                mastClass.setRoundUppAmt(c.getFloat(c.getColumnIndex(DBHandler.IWM_Rounduppamt)));
                mastClass.setCSTVatPer(c.getFloat(c.getColumnIndex(DBHandler.IWM_Cstvatper)));
                mastClass.setCGSTAMT(c.getFloat(c.getColumnIndex(DBHandler.IWM_Cgstamt)));
                mastClass.setSGSTAMT(c.getFloat(c.getColumnIndex(DBHandler.IWM_Sgstamt)));
                mastClass.setIGSTAMT(c.getFloat(c.getColumnIndex(DBHandler.IWM_Igstamt)));
                mastClass.setSupplierID(c.getInt(c.getColumnIndex(DBHandler.IWM_Supplierid)));
                mastClass.setPoNo(c.getInt(c.getColumnIndex(DBHandler.IWM_Pono)));
                mastClass.setRebarCnt(c.getInt(c.getColumnIndex(DBHandler.IWM_Rebarcnt)));
                mastClass.setCreateby(c.getInt(c.getColumnIndex(DBHandler.IWM_Createby)));
                mastClass.setTransport_id(c.getInt(c.getColumnIndex(DBHandler.IWM_Transport_Id)));
                mastClass.setJobWrkDCid(c.getInt(c.getColumnIndex(DBHandler.IWM_Jobwrkdcid)));
                mastClass.setCancelledBy(c.getInt(c.getColumnIndex(DBHandler.IWM_Cancelledby)));
                mastClass.setHOCode(c.getInt(c.getColumnIndex(DBHandler.IWM_Hocode)));
                mastClass.setInwardDate(c.getString(c.getColumnIndex(DBHandler.IWM_Inwarddate)));
                mastClass.setAgainstPO(c.getString(c.getColumnIndex(DBHandler.IWM_Againstpo)));
                mastClass.setPoDate(c.getString(c.getColumnIndex(DBHandler.IWM_Podate)));
                mastClass.setInwardSt(c.getString(c.getColumnIndex(DBHandler.IWM_Inwardst)));
                mastClass.setRamark(c.getString(c.getColumnIndex(DBHandler.IWM_Ramark)));
                mastClass.setNetAmtInWord(c.getString(c.getColumnIndex(DBHandler.IWM_Netamtinword)));
                mastClass.setTotalAmtInWord(c.getString(c.getColumnIndex(DBHandler.IWM_Totalamtinword)));
                mastClass.setFinYr(c.getString(c.getColumnIndex(DBHandler.IWM_Finyr)));
                mastClass.setRebarcodeSt(c.getString(c.getColumnIndex(DBHandler.IWM_Rebarcodest)));
                mastClass.setBillNo(c.getString(c.getColumnIndex(DBHandler.IWM_Billno)) );
                mastClass.setBillgenerated(c.getString(c.getColumnIndex(DBHandler.IWM_Billgenerated)));
                mastClass.setBgenerateno(c.getString(c.getColumnIndex(DBHandler.IWM_Bgenerateno)));
                mastClass.setCreatedate(c.getString(c.getColumnIndex(DBHandler.IWM_Createdate)));
                mastClass.setLR_No(c.getString(c.getColumnIndex(DBHandler.IWM_Lr_No)));
                mastClass.setLr_Date(c.getString(c.getColumnIndex(DBHandler.IWM_Lr_Date)));
                mastClass.setRefund(c.getString(c.getColumnIndex(DBHandler.IWM_Refund)));
                mastClass.setRefundDate(c.getString(c.getColumnIndex(DBHandler.IWM_Refunddate)));
                mastClass.setPIMadeSt(c.getString(c.getColumnIndex(DBHandler.IWM_Pimadest)));
                mastClass.setBaleOpenNo(c.getString(c.getColumnIndex(DBHandler.IWM_Baleopenno)) );
                mastClass.setJobWorkTyp(c.getString(c.getColumnIndex(DBHandler.IWM_Jobworktyp)));
                mastClass.setBarcodeGenerate(c.getString(c.getColumnIndex(DBHandler.IWM_Barcodegenerate)));
                mastClass.setConsignmentPur(c.getString(c.getColumnIndex(DBHandler.IWM_Consignmentpur)));
                mastClass.setForBranch(c.getString(c.getColumnIndex(DBHandler.IWM_Forbranch)));
                mastClass.setNAN(c.getString(c.getColumnIndex(DBHandler.IWM_Nan)));
                mastClass.setInvNo(c.getString(c.getColumnIndex(DBHandler.IWM_Invno)));
                mastClass.setStatus(c.getString(c.getColumnIndex(DBHandler.IWM_Status)));
                mastClass.setCancelDate(c.getString(c.getColumnIndex(DBHandler.IWM_Canceldate)));
                mastClass.setCancelReson(c.getString(c.getColumnIndex(DBHandler.IWM_Cancelreson)));
                mastClass.setChkCST(c.getString(c.getColumnIndex(DBHandler.IWM_Chkcst)));
                mastClass.setEsugamNo(c.getString(c.getColumnIndex(DBHandler.IWM_Esugamno)));
                mastClass.setReason(c.getString(c.getColumnIndex(DBHandler.IWM_Reason)));
                mastClass.setIGSTAPP(c.getString(c.getColumnIndex(DBHandler.IWM_Igstapp)));
                list.add(mastClass);
            } while (c.moveToNext());
        }

        InwardSummeryReportAdapter adapter = new InwardSummeryReportAdapter(list, getApplicationContext());
        listView.setAdapter(adapter);
        setTotal(list);
        if(list.size() == 0){
            toast.setText("Data Not Available..");
            toast.show();
        }
        db.close();
        c.close();
    }

    private void setTotal(List<InwardMasterClass> list) {
        totAmt = 0;
        totqty = 0;
        tot_sgst = 0;
        tot_cgst = 0;
        tot_igst = 0;

        for (InwardMasterClass eClass : list) {
            totAmt = totAmt + eClass.getTotalAmt();
            totqty = totqty + eClass.getTotalQty();
            tot_cgst = tot_cgst + eClass.getCGSTAMT();
            tot_sgst = tot_sgst + eClass.getSGSTAMT();
            tot_igst = tot_igst + eClass.getIGSTAMT();
        }

        tv_tot_amnt.setText(flt_price.format(totAmt));
        tv_tot_qty.setText(flt_price.format(totqty));
        tv_tot_cgst.setText(flt_price.format(tot_cgst));
        tv_tot_sgst.setText(flt_price.format(tot_sgst));
        tv_tot_igst.setText(flt_price.format(tot_igst));
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
        res.close();

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),R.layout.adapter_item,suppList);
        auto_supplier.setAdapter(adapter);
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

    private void showDia(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(InwardReportActivity.this);
        builder.setCancelable(false);
        if (id == 0) {
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
        }else if (id == 1) {
            builder.setMessage("Do You Want to Logout?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new Constant(InwardReportActivity.this).doFinish();
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        } else if (id == 2) {
            builder.setMessage("Do you want to See Details?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent in = new Intent(InwardReportActivity.this,InwardDetailActivity.class);
                    in.putExtra("inwardid",inwardid);
                    in.putExtra("branchid",branchid);
                    startActivity(in);
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
