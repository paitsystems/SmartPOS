package com.pait.smartpos;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pait.smartpos.adpaters.InwardDetailReportAdapter;
import com.pait.smartpos.adpaters.InwardSummeryReportAdapter;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.model.InwardDetailClass;
import com.pait.smartpos.model.InwardMasterClass;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class InwardDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Constant constant, constant1;
    private Toast toast;
    private Calendar cal = Calendar.getInstance();
    private static final int vdt = 1;
    private int day, month, year;
    private DBHandler db;
    private TextView tv_fromdate, tv_todate,tv_tot_qty,tv_tot_amnt,tv_tot_cgst,tv_tot_sgst,tv_tot_igst,
            tv_tot_gstper,tv_tot_salerate,tv_tot_purchaserate;
    private String currentdate;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    private SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
    private ListView listView;
    private DecimalFormat flt_price;
    private List<String> suppList;
    private double totAmt=0,totqty=0,tot_cgst=0,tot_sgst=0,tot_igst=0,tot_salerate=0,tot_purchaserate=0,tot_gstper=0;
    private int branchid = 0,inwardid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inward_detail);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Inward Detail Report");
        }

        init();
        inwardid = getIntent().getExtras().getInt("inwardid");
        branchid = getIntent().getExtras().getInt("branchid");
        setData(inwardid,branchid);
    }

    @Override
    public void onClick(View view) {

    }

    private void init() {
        db = new DBHandler(InwardDetailActivity.this);
        constant = new Constant(InwardDetailActivity.this);
        constant1 = new Constant(getApplicationContext());
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        listView = findViewById(R.id.listView);
        tv_tot_amnt = findViewById(R.id.tv_tot_amnt);
        tv_tot_qty = findViewById(R.id.tv_tot_qty);
        tv_tot_cgst = findViewById(R.id.tv_tot_cgst);
        tv_tot_sgst = findViewById(R.id.tv_tot_sgst);
        tv_tot_igst = findViewById(R.id.tv_tot_igst);
        tv_tot_gstper = findViewById(R.id.tv_tot_gstper);
        tv_tot_salerate = findViewById(R.id.tv_tot_salerate);
        tv_tot_purchaserate = findViewById(R.id.tot_purchaserate);
        flt_price = new DecimalFormat();
        flt_price.setMinimumFractionDigits(2);
        suppList = new ArrayList<>();
    }

    private void setData(int inwarid,int branchid) {
        listView.setAdapter(null);
        Cursor c = db.getInwradDetail(inwarid,branchid);
        List<InwardDetailClass> list = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                InwardDetailClass detail = new InwardDetailClass();
                detail.setSuppid(c.getInt(0));
                detail.setInwno(c.getString(1));
                detail.setInwardID(c.getInt(2));
                detail.setFatherSKU(c.getString(3));
                detail.setRecQty(c.getFloat(4));
                detail.setRate(c.getFloat(5));
                detail.setTotalAmt(c.getFloat(6));
                detail.setProductNetAmt(c.getFloat(7));
                detail.setBarcode(c.getString(8));
                detail.setPurchaseRate(c.getFloat(9));
                detail.setNetRate(c.getFloat(10));
                detail.setGSTPER(c.getFloat(11));
                detail.setCGSTAMT(c.getFloat(12));
                detail.setSGSTAMT(c.getFloat(13));
                detail.setIGSTAMT(c.getFloat(14));
                detail.setHSNCode(c.getString(15));
                list.add(detail);
            } while (c.moveToNext());
        }

        InwardDetailReportAdapter adapter = new InwardDetailReportAdapter(list, getApplicationContext());
        listView.setAdapter(adapter);
        setTotal(list);
        if(list.size() == 0){
            toast.setText("Data Not Available..");
            toast.show();
        }
        db.close();
        c.close();
    }

    private void setTotal(List<InwardDetailClass> list) {
        totqty = 0;
        tot_sgst = 0;
        tot_cgst = 0;
        tot_igst = 0;
        tot_salerate = 0;
        tot_purchaserate=0;
        tot_gstper=0;

        for (InwardDetailClass eClass : list) {
            totqty = totqty + eClass.getRecQty();
            tot_cgst = tot_cgst + eClass.getCGSTAMT();
            tot_sgst = tot_sgst + eClass.getSGSTAMT();
            tot_igst = tot_igst + eClass.getIGSTAMT();
            tot_salerate = tot_salerate + eClass.getRate();
            tot_purchaserate = tot_purchaserate + eClass.getPurchaseRate();
            tot_gstper = tot_gstper + eClass.getGSTPER();
        }

        tv_tot_qty.setText(flt_price.format(totqty));
        tv_tot_cgst.setText(flt_price.format(tot_cgst));
        tv_tot_sgst.setText(flt_price.format(tot_sgst));
        tv_tot_igst.setText(flt_price.format(tot_igst));
        tv_tot_salerate.setText(flt_price.format(tot_salerate));
        tv_tot_purchaserate.setText(flt_price.format(tot_purchaserate));
        tv_tot_gstper.setText(flt_price.format(tot_gstper));
    }
}
