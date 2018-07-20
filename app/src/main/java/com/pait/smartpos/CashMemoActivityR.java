package com.pait.smartpos;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pait.smartpos.adpaters.CMOrderItemListAdapter;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.constant.PrinterCommands;
import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.interfaces.UpdateValue;
import com.pait.smartpos.log.WriteLog;
import com.pait.smartpos.model.BillDetailClassR;
import com.pait.smartpos.model.BillMasterClassR;
import com.pait.smartpos.model.UserProfileClass;
import com.hoin.btsdk.BluetoothService;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CashMemoActivityR extends AppCompatActivity implements UpdateValue{

    private TextView tv_tableno, tv_user, tv_return, tv_balance, tv_netamt, tv_totalqty, tv_totalamt, tv_gst_group, tv_gst_rate, tv_gst_amnt;//tv_tax_rate, tv_tax_amnt,
    private EditText ed_cash, ed_credit, ed_tender;
    private ListView listView;
    private float cashAmt, cardAmt, tenderAmt, retAmt, balAmt, netAmt;
    private Button btn_save, btn_reset, btn_cancel;
    private String totalqty, totalamt, tableid, kotno, gstPer, cgstAmt,
            sgstAmt, cgstPer, sgstPer, cessPer, cessAmt,
            cgstPerStr = "", sgstPerStr = "", billNo, oldBillNo;
    private int userid, branchid, updateSel = -1, isOrderSaved = 0, selMastAuto = 0;
    private HashMap<Integer, List<Integer>> order_hash_map;
    private HashMap<Integer, List<String>> order_prod_hash_map, old_order_prod_hash_map;
    private DBHandlerR db;
    private Toast toast;
    private double totcgstAmt = 0, totsgstAmt = 0, totalRateAmnt = 0;
    public static float totalqtyFl, totalamtFl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_memo_r);

        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }

        init();

        setData();

        /*if(VerificationActivity.mService!=null) {
            VerificationActivity.mService.stop();
        }
        VerificationActivity.mService = new BluetoothService(getApplicationContext(), mHandler1);
        connectBT();*/

        ed_cash.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!ed_cash.getText().toString().equals("")) {
                    cashAmt = Float.parseFloat(ed_cash.getText().toString());
                    cardAmt = Float.parseFloat(ed_credit.getText().toString());
                    netAmt  = Float.parseFloat(tv_netamt.getText().toString());
                    balAmt = netAmt - (cashAmt + cardAmt);
                    tv_balance.setText(String.valueOf(balAmt));
                    retAmt = (cashAmt + cardAmt) - netAmt;
                    tv_return.setText(String.valueOf(retAmt));
                    if (tenderAmt != 0) {
                        tenderAmt = Float.parseFloat(ed_tender.getText().toString());
                        retAmt = tenderAmt - cashAmt;
                        tv_return.setText(String.valueOf(retAmt));
                    }
                } else {
                    ed_cash.setText("0");
                    ed_cash.setSelection(0);
                    tv_return.setText("0");
                    cashAmt = 0;
                }
            }
        });

        ed_credit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!ed_credit.getText().toString().equals("")) {
                    cashAmt = Float.parseFloat(ed_cash.getText().toString());
                    cardAmt = Float.parseFloat(ed_credit.getText().toString());
                    retAmt = (cashAmt + cardAmt) - netAmt;
                    tv_return.setText(String.valueOf(retAmt));
                    balAmt = netAmt - (cashAmt + cardAmt);
                    tv_balance.setText(String.valueOf(balAmt));
                } else {
                    ed_credit.setText("0");
                    ed_credit.setSelection(0);
                    cardAmt = 0;
                }
            }
        });

        ed_tender.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!ed_tender.getText().toString().equals("")) {
                    cashAmt = Float.parseFloat(ed_cash.getText().toString());
                    tenderAmt = Float.parseFloat(ed_tender.getText().toString());
                    retAmt = tenderAmt - cashAmt;
                    if (retAmt < 0) {
                        tv_return.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.maroon));
                    } else if (retAmt >= 0) {
                        tv_return.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    }
                    if (tenderAmt != 0) {
                        tv_return.setText(String.valueOf(retAmt));
                    } else {
                        retAmt = 0;
                        tv_return.setText(String.valueOf(retAmt));
                    }
                } else {
                    ed_tender.setText("0");
                    ed_tender.setSelected(true);
                    ed_tender.setSelection(0);
                    tenderAmt = 0;
                    retAmt = 0;
                }
            }
        });

        btn_save.setOnClickListener(view -> {
            if(isOrderSaved==0) {
                showDia(1);
            }else{
                showDia(8);
            }

        });

        btn_reset.setOnClickListener(view -> showDia(2));

        btn_cancel.setOnClickListener(view -> showDia(3));

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            List<Integer> list = order_hash_map.get(Integer.parseInt(tableid));
            updateSel = list.get(i);
            List<String> listStr = order_prod_hash_map.get(updateSel);
            Constant.showLog(listStr.get(0)+"-"+listStr.get(1)+"-"+listStr.get(2)+"-"+listStr.get(3));
            updateQty(listStr);
        });
    }

    @Override
    public void onBackPressed() {
        showDia(3);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.cashmemo_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                doThing();
                break;
            case R.id.cust_bill:
                new CashMemoPrint().execute();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateValue() {
        tv_totalqty.setText(String.valueOf(totalqtyFl));
        tv_totalamt.setText(String.valueOf(totalamtFl));
        tv_netamt.setText(String.valueOf(totalamtFl));
        ed_cash.setText(String.valueOf(totalamtFl));
        ed_cash.setSelection(String.valueOf(totalamtFl).length());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if(VerificationActivity.mService!=null) {
            VerificationActivity.mService.stop();
        }*/
    }

    private void init() {
        tv_tableno = findViewById(R.id.tv_tableno);
        tv_user = findViewById(R.id.tv_user);
        tv_return = findViewById(R.id.tv_returnamnt);
        tv_balance = findViewById(R.id.tv_balance);
        tv_netamt = findViewById(R.id.tv_netamnt);
        tv_totalqty = findViewById(R.id.tv_totalqty);
        tv_totalamt = findViewById(R.id.tv_totalamt);
        ed_tender = findViewById(R.id.ed_tender);
        ed_cash = findViewById(R.id.ed_cash);
        ed_credit = findViewById(R.id.ed_creditcard);
        btn_save = findViewById(R.id.btn_save);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_reset = findViewById(R.id.btn_reset);
        listView = findViewById(R.id.listview);
        tv_gst_group = findViewById(R.id.tv_gst_group);
        tv_gst_rate = findViewById(R.id.tv_gst_rate);
        tv_gst_amnt = findViewById(R.id.tv_gst_amnt);

        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);

        Bundle bundle = getIntent().getExtras();
        order_hash_map = (HashMap<Integer, List<Integer>>) getIntent().getSerializableExtra("proditemid");
        order_prod_hash_map = (HashMap<Integer, List<String>>) getIntent().getSerializableExtra("prodlist");
        assert bundle != null;
        totalqty = bundle.getString("totalqty");
        totalamt = bundle.getString("totalamt");
        String user = bundle.getString("user");
        tv_user.setText(user);
        branchid = bundle.getInt("branchid");
        userid = bundle.getInt("id");
        tableid = bundle.getString("tableid");
        kotno = bundle.getString("kotno");

        db = new DBHandlerR(getApplicationContext());

        old_order_prod_hash_map = new HashMap<>();
    }

    private void setData() {
        try {
            listView.setAdapter(null);

            cashAmt = Float.parseFloat(totalamt);
            netAmt = Float.parseFloat(totalamt);
            balAmt = 0;
            cardAmt = 0;
            tenderAmt = 0;
            retAmt = 0;
            totalamtFl = 0;
            totalqtyFl = 0;

            CMOrderItemListAdapter adapter = new CMOrderItemListAdapter(getApplicationContext(), order_hash_map, order_prod_hash_map, Integer.parseInt(tableid));
            adapter.initInterface(CashMemoActivityR.this);
            listView.setAdapter(adapter);

            tv_tableno.setText(tableid);

            float _gstPer, _totalAmnt, _cgstPer, _sgstPer, _cessPer;
            String namerate = db.getGSTGroupNameFromTable(tableid, totalamt);
            String str[];
            if (!namerate.equals("")) {
                //groupname-gstper-cgstper-sgstper-cessper
                str = namerate.split("-");
            } else {
                namerate = "NA-0-0-0-0";
                str = namerate.split("-");
            }
            String gstgroupName = str[0];
            gstPer = str[1];
            cgstPer = str[2];
            sgstPer = str[3];
            cessPer = str[4];
            tv_gst_rate.setText(gstPer);
            tv_gst_group.setText(gstgroupName);
            _gstPer = Float.parseFloat(gstPer);
            _cgstPer = Float.parseFloat(cgstPer);
            _sgstPer = Float.parseFloat(sgstPer);
            _cessPer = Float.parseFloat(cessPer);

            _totalAmnt = Float.parseFloat(totalamt);
            float gstAmnt = (_totalAmnt * _gstPer) / 100;
            float _cgstAmnt = (_totalAmnt * _cgstPer) / 100;
            float _sgstAmnt = (_totalAmnt * _sgstPer) / 100;
            float _cessAmnt = (_totalAmnt * _cessPer) / 100;
            gstAmnt = gstAmnt + _cessAmnt;
            cgstAmt = String.valueOf(_cgstAmnt);
            sgstAmt = String.valueOf(_sgstAmnt);
            cessAmt = String.valueOf(_cessAmnt);
            tv_gst_amnt.setText(String.valueOf(gstAmnt));
            //setTotalNetAmnt();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void showDia(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CashMemoActivityR.this);
        builder.setCancelable(false);
        if (i == 1) {
            builder.setMessage("Do You Want Save Bill?");
            builder.setPositiveButton("Yes", (dialogInterface, i1) -> {
                dialogInterface.dismiss();
                saveBillMasterLocal();
            });
            builder.setNegativeButton("No", (dialogInterface, i12) -> dialogInterface.dismiss());
        } else if (i == 2) {
            builder.setMessage("Do You Want To Reset Data?");
            builder.setPositiveButton("Yes", (dialogInterface, i13) -> {
                resetData();
                dialogInterface.dismiss();
            });
            builder.setNegativeButton("No", (dialogInterface, i14) -> dialogInterface.dismiss());
        } else if (i == 3) {
            builder.setMessage("Do You Want To Cancel Transaction?");
            builder.setPositiveButton("Yes", (dialogInterface, i15) -> {
                doThing();
                dialogInterface.dismiss();
            });
            builder.setNegativeButton("No", (dialogInterface, i16) -> dialogInterface.dismiss());
        } else if (i == 4) {
            builder.setMessage("Bill Saved Successfully");
            builder.setPositiveButton("Ok", (dialogInterface, i17) -> {
                dialogInterface.dismiss();
                isOrderSaved = 1;
                showDia(7);
            });
        } else if (i == 5) {
            builder.setMessage("Error While Saving Bill");
            builder.setPositiveButton("Try Again", (dialogInterface, i18) -> {
                //saveBill();
                saveBillMasterLocal();
                dialogInterface.dismiss();
            });
            builder.setNegativeButton("Cancel", (dialogInterface, i19) -> dialogInterface.dismiss());
        } else if (i == 6) {
            builder.setMessage("Bill Saved Successfully");
            builder.setPositiveButton("Ok", (dialogInterface, i110) -> {
                dialogInterface.dismiss();
                doThing();
            });
        }else if (i == 7) {
            builder.setMessage("Do You Want To Update Order?");
            builder.setPositiveButton("Yes", (dialogInterface, i111) -> {
                dialogInterface.dismiss();
                toast.setText("Click On Item To Update Order");
                toast.show();
                btn_save.setText("Update");
            });
            builder.setNegativeButton("No", (dialogInterface, i111) -> {
                dialogInterface.dismiss();
                MainActivity.checkedBeforeCM = 0;
                doThing();
            });

        }else if (i == 8) {
            builder.setMessage("Do You Want To Update Saved Bill");
            builder.setPositiveButton("Yes", (dialogInterface, i110) -> {
                dialogInterface.dismiss();
                saveUpdateBillMasterLocal();
            });
            builder.setNegativeButton("No", (dialogInterface, i111) -> {
                dialogInterface.dismiss();
            });
        }else if (i == 9) {
            builder.setMessage("Bill Updated Successfully");
            builder.setPositiveButton("Ok", (dialogInterface, i17) -> {
                dialogInterface.dismiss();
                isOrderSaved = 1;
                showDia(7);
            });
        }
        builder.create().show();
    }

    private void updateQty(List<String> listStr) {
        final EditText ed_remark = new EditText(getApplicationContext());
        ed_remark.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.black));
        ed_remark.setText(listStr.get(1));
        ed_remark.setInputType(InputType.TYPE_CLASS_NUMBER);
        ed_remark.setSelected(true);
        ed_remark.setSelection(listStr.get(1).length());
        AlertDialog.Builder builder = new AlertDialog.Builder(CashMemoActivityR.this,R.style.AlertDialogCustom);
        builder.setCancelable(false);
        builder.setView(ed_remark);
        builder.setMessage(listStr.get(0)+"\nAmnt : "+listStr.get(3));
        builder.setPositiveButton("Update", (dialog, which) -> {
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(ed_remark.getWindowToken(), 0);
            if(!ed_remark.getText().toString().equals("") && !ed_remark.getText().toString().equals("0")) {
                String _qty = ed_remark.getText().toString();
                float qtyFl = Float.parseFloat(_qty);
                float amtFl = Float.parseFloat(listStr.get(2));
                amtFl = amtFl * qtyFl;
                listStr.set(1, _qty);
                listStr.set(3, String.valueOf(amtFl));
                order_prod_hash_map.put(updateSel, listStr);
                totalqtyFl = 0;
                totalamtFl = 0;
                setData();
                Constant.showLog(_qty);
                dialog.dismiss();
            }else {
                toast.setText("Please Enter Values");
                toast.show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(ed_remark.getWindowToken(), 0);
            dialog.dismiss();
        });
        builder.create().show();
    }

    private void doThing() {
        finish();
        Intent intent = new Intent(getApplicationContext(), DrawerTestActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    private void resetData() {
        cashAmt = netAmt;
        balAmt = 0;
        cardAmt = 0;
        tenderAmt = 0;
        retAmt = 0;
        ed_cash.setText(tv_netamt.getText().toString());
        ed_cash.requestFocus();
        ed_credit.setText("0");
        ed_tender.setText("0");
        tv_return.setText("0");
        tv_balance.setText("0");
    }

    private void saveBillMasterLocal() {
        String data;
        try {
            /*Bundle bundle = getIntent().getExtras();
            order_hash_map = (HashMap<Integer, List<Integer>>) getIntent().getSerializableExtra("proditemid");
            order_prod_hash_map = (HashMap<Integer, List<String>>) getIntent().getSerializableExtra("prodlist");
            totalqty = bundle.getString("totalqty");
            totalamt = tv_netamt.getText().toString();
            String user = bundle.getString("user");
            branchid = bundle.getInt("branchid");
            userid = bundle.getInt("id");
            tableid = bundle.getString("tableid");
            kotno = bundle.getString("kotno");
            Date date = Calendar.getInstance().getTime();
            String st1 = ed_cash.getText().toString();
            if (st1.equals("")) {
                st1 = "0";
            }
            cashAmt = Float.parseFloat(st1);
            netAmt = Float.parseFloat(totalamt);
            balAmt = 0;
            String st = ed_credit.getText().toString();
            if (st.equals("")) {
                st = "0";
            }
            cardAmt = Float.parseFloat(st);
            tenderAmt = 0;
            retAmt = 0;
            tv_totalqty.setText(totalqty);
            tv_totalamt.setText(totalamt);
            tv_tableno.setText(tableid);
            tv_user.setText(user);*/
            //tv_netamt.setText(totalamt);
            //ed_cash.setText(totalamt);

            int counterNo = 1, DCAuto = 0;
            float paidAmnt,coupanAmnt,vat12,vat5,AdvAmt,PIAmnt = 0,
                    Tender,Taxper,VatPer,TaxAmt,OtherAmt,Disper = 0,
                    DiscAmt = 0, SCTaxper = 0, SCTaxamt = 0, TotalTax = 0, Return = 0;
            double discPer,totdiscAmt = 0, totGstAmt = 0;

            String machineName = "Mob", AgainstDC = "NA", TaxSt = "Y",
                    CmpSt = "N", barcode = "NA", BillPaySt = "Y", finyr;

            float BDiscper = 0, BDiscAmt = 0;

            int year1 = new Date().getYear();
            finyr = year1 + "-" + (year1+1);
            Constant.showLog("FINYR "+finyr);

            retAmt = Float.parseFloat(tv_return.getText().toString());
            totcgstAmt = 0;
            totsgstAmt = 0;

            int mastAuto = db.getMaxAuto();
            selMastAuto = mastAuto;

            String date1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
            String crDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Calendar.getInstance().getTime());
            String time = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime());
            String[] dateArr = date1.split("/");
            String day = dateArr[0];
            String month = dateArr[1];
            String year = dateArr[2];
            int y = Integer.parseInt(year.substring(2, 4));
            String finYr = y + "-" + String.valueOf(y + 1);

            List<Integer> list = order_hash_map.get(Integer.parseInt(tableid));
            for (int i = 0; i < list.size(); i++) {
                List<String> listDet = order_prod_hash_map.get(list.get(i));
                int qty = Integer.parseInt(listDet.get(1));
                Cursor res = db.getGSTGroupFromProduct(listDet.get(0));
                res.moveToFirst();
                discPer = res.getDouble(res.getColumnIndex(DBHandlerR.Product_Disper));
                double rate = res.getDouble(res.getColumnIndex(DBHandlerR.Product_Rate));
                String gstGroup = res.getString(res.getColumnIndex(DBHandlerR.Product_GSTGroup));
                String taxType = res.getString(res.getColumnIndex(DBHandlerR.Product_TaxTyp));
                res.close();
                Cursor cursor = db.getGSTPer(gstGroup);
                cursor.moveToFirst();
                double gstPer = cursor.getDouble(cursor.getColumnIndex(DBHandlerR.GSTDetail_GSTPer));
                double cgstPer = cursor.getDouble(cursor.getColumnIndex(DBHandlerR.GSTDetail_CGSTPer));
                double sgstPer = cursor.getDouble(cursor.getColumnIndex(DBHandlerR.GSTDetail_SGSTPer));
                double cgstShare = cursor.getDouble(cursor.getColumnIndex(DBHandlerR.GSTDetail_CGSTShare));
                double sgstShare = cursor.getDouble(cursor.getColumnIndex(DBHandlerR.GSTDetail_SGSTShare));
                cursor.close();

                cgstPerStr = String.valueOf(cgstPer);
                sgstPerStr = String.valueOf(sgstPer);

                double cgstAmt = 0, sgstAmt = 0, discAmt = 0, gstAmt = 0, total = 0, netAmt = 0;

                if (taxType.equals("E")) {
                    double amt = (rate * qty);
                    netAmt = amt;
                    netAmt = (netAmt * discPer) / 100;
                    netAmt = netAmt - discAmt;
                    gstAmt = (netAmt * gstPer) / 100;
                    cgstAmt = (gstAmt * cgstShare) / 100;
                    sgstAmt = (gstAmt * sgstShare) / 100;
                    netAmt = netAmt + gstAmt;
                    listDet.set(3, roundTwoDecimals(String.valueOf(amt)));
                    listDet.set(4, roundTwoDecimals(String.valueOf(netAmt)));
                    //order_prod_hash_map.put(list.get(i), listDet);
                    Constant.showLog(listDet.get(0) + "-" + discPer + "-" + qty + "-" + rate + "-" + discAmt + "-" + amt + "-" + gstAmt + "-" + cgstAmt + "-" + sgstAmt);
                } else if (taxType.equals("I")) {
                    double accValue = ((gstPer * 100) / (gstPer + 100));
                    double rate1 = (rate * accValue) / 100;
                    rate = rate - rate1;
                    double amt = (rate * qty);
                    netAmt = amt;
                    discAmt = (netAmt * discPer) / 100;
                    netAmt = netAmt - discAmt;
                    gstAmt = (netAmt * gstPer) / 100;
                    cgstAmt = (gstAmt * cgstShare) / 100;
                    sgstAmt = (gstAmt * sgstShare) / 100;
                    netAmt = netAmt + gstAmt;
                    listDet.set(2, roundTwoDecimals(String.valueOf(rate)));
                    listDet.set(3, roundTwoDecimals(String.valueOf(amt)));
                    listDet.set(4, roundTwoDecimals(String.valueOf(netAmt)));
                    //order_prod_hash_map.put(list.get(i), listDet);
                    Constant.showLog(listDet.get(0) + "-" + discPer + "-" + qty + "-" + rate + "-" + discAmt + "-" + amt + "-" + gstAmt + "-" + cgstAmt + "-" + sgstAmt + "-" + accValue + "-" + netAmt);
                }

                double a = rate * qty;
                total = total + a;
                totalRateAmnt = totalRateAmnt + total;
                totGstAmt = totGstAmt + gstAmt;
                totcgstAmt = totcgstAmt + cgstAmt;
                totsgstAmt = totsgstAmt + sgstAmt;
                totdiscAmt = totdiscAmt + discAmt;

                int detAuto = db.getMaxDetAuto();
                int detId = db.getMaxDetId(mastAuto);
                BillDetailClassR detail = new BillDetailClassR();
                detail.setAuto(detAuto);
                detail.setID(detId);
                detail.setBillID(mastAuto);
                detail.setBranchId(branchid);
                detail.setFinyr(finYr);
                detail.setItemid(list.get(i));
                detail.setRateStr(roundTwoDecimals(String.valueOf(rate)));
                detail.setQty(Float.parseFloat(String.valueOf(qty)));
                detail.setCmpSt(CmpSt);
                detail.setTotalStr(roundTwoDecimals(String.valueOf(total)));
                detail.setVat(0);
                detail.setVatamt(0);
                detail.setAmtWithDiscStr(roundTwoDecimals(String.valueOf(total)));
                detail.setDisper(Float.parseFloat(String.valueOf(discPer)));
                detail.setDisamt(DiscAmt);
                detail.setBillDiscper(BDiscper);
                detail.setBillDiscAmt(BDiscAmt);
                detail.setWaiterID(userid);
                detail.setBarcode(barcode);
                detail.setNetAmtStr(roundTwoDecimals(String.valueOf(netAmt)));
                detail.setGSTPER(Float.parseFloat(String.valueOf(gstPer)));
                detail.setCGSTAMTStr(roundTwoDecimals(String.valueOf(cgstAmt)));
                detail.setSGSTAMTStr(roundTwoDecimals(String.valueOf(sgstAmt)));
                detail.setCGSTPER(Float.parseFloat(String.valueOf(cgstPer)));
                detail.setSGSTPER(Float.parseFloat(String.valueOf(sgstPer)));
                detail.setCESSPER(Float.parseFloat(cessPer));
                detail.setCESSAMTStr(roundTwoDecimals(String.valueOf(cessAmt)));
                db.saveBillDetail(detail);
            }
            int id = db.getMaxMastId(finYr);
            billNo = "LI/B/" + finYr + "/" + month + day + mastAuto;
            oldBillNo = billNo;

            coupanAmnt = 0;
            AdvAmt = 0;
            Taxper = 0;
            Tender = 0;
            VatPer = 0;
            TaxAmt = 0;
            OtherAmt = 0;
            paidAmnt = cardAmt + cashAmt + coupanAmnt;
            vat5 = 0;
            vat12 = 0;
            int NOP = 0;
            String OrderNo = "0";
            balAmt = netAmt-paidAmnt;

            BillMasterClassR master = new BillMasterClassR();
            master.setAuto(mastAuto);
            master.setId(id);
            master.setBranchId(branchid);
            master.setFinyr(finYr);
            master.setBillNo(billNo);
            master.setBillDate(crDate);
            master.setBillTime(time);
            master.setCustID(userid);
            master.setTotalQty(totalqtyFl);
            master.setTotalAmtStr(roundTwoDecimals(String.valueOf(totalRateAmnt)));
            master.setCardAmt(cardAmt);
            master.setCashAmt(cashAmt);
            master.setCoupanAmt(coupanAmnt);
            master.setPaidAmt(paidAmnt);
            master.setBalAmt(balAmt);
            master.setNetAmt(netAmt);
            master.setBillSt("A");
            master.setCrBy(userid);
            master.setCrDate(crDate);
            master.setVat12(vat12);
            master.setVat5(vat5);
            master.setDisper(Disper);
            master.setDisAmt(DiscAmt);
            master.setAmtInWords("");
            master.setPIAmt(PIAmnt);
            master.setPIno(OrderNo);
            master.setCounterNo(String.valueOf(counterNo));
            master.setMachineName(machineName);
            master.setAgainstDC(AgainstDC);
            master.setTableID(Integer.parseInt(tableid));
            master.setLocationID(1);
            master.setSalesMan(userid);
            master.setWaiterID(userid);
            master.setServiceTax(SCTaxper);
            master.setServiceAmt(SCTaxamt);
            master.setNoOfPass(NOP);
            master.setAdvamt(AdvAmt);
            master.setKOTNo(kotno);
            master.setDCAutoNo(DCAuto);
            master.setTaxst(TaxSt);
            master.setTaxper(Taxper);
            master.setVatamt(TotalTax);
            master.setTender(Tender);
            master.setRemainAmt(Return);
            master.setBillPaySt(BillPaySt);
            master.setVatPer(VatPer);
            master.setTaxAmt(TaxAmt);
            master.setOtherAmt(OtherAmt);
            master.setCGSTAMTStr(roundTwoDecimals(String.valueOf(totcgstAmt)));
            master.setSGSTAMTStr(roundTwoDecimals(String.valueOf(totsgstAmt)));

            db.saveBillMaster(master, mastAuto, kotno);
            new CashMemoPrint().execute();

            //int mastID = db.saveBillMaster(master,auto,kotno);
            //saveBillDetail(mastID);

            /*if(ConnectivityTest.getNetStat(getApplicationContext())){
                new SaveBills().execute(url);
            }else{
                toast.setText("You Are Offline");
                toast.show();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            data = "CashMemoActivity_savebill_" + e.getMessage();
            writeLog(data);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CashMemoPrint extends AsyncTask<Void, Void, String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(CashMemoActivityR.this);
            pd.setCancelable(false);
            pd.setMessage("Please Wait...");
            pd.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String str;
            //Bitmap logoData = BitmapFactory.decodeResource(getResources(), R.drawable.store);
            StringBuilder textData = new StringBuilder();
            try {
                byte[] arrayOfByte1 = {27, 33, 0};
                byte[] format = {27, 33, 0};

                byte[] center = {27, 97, 1};
                VerificationActivity.mService.write(center);
                byte nameFontformat[] = format;
                nameFontformat[2] = ((byte) (0x20 | arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);

                UserProfileClass user = new Constant(getApplicationContext()).getPref();

                VerificationActivity.mService.sendMessage(user.getFirmName(), "GBK");

                nameFontformat[2] = arrayOfByte1[2];
                VerificationActivity.mService.write(nameFontformat);

                VerificationActivity.mService.sendMessage(user.getCity(), "GBK");
                VerificationActivity.mService.sendMessage(user.getMobileNo(), "GBK");
                VerificationActivity.mService.sendMessage("TAX INVOICE", "GBK");

                byte[] left = {27, 97, 0};
                VerificationActivity.mService.write(left);

                String date = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                String time = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                VerificationActivity.mService.sendMessage("BillNo : " + billNo, "GBK");
                String space_str13 = "             ";
                VerificationActivity.mService.sendMessage(date + space_str13 + time, "GBK");

                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x8 | arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                String line_str = "--------------------------------";
                VerificationActivity.mService.sendMessage(line_str, "GBK");
                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                VerificationActivity.mService.write(nameFontformat);

                VerificationActivity.mService.sendMessage("Item           " + "Qty" + "  Rate" + "  Amnt", "GBK");
                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x8 | arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage(line_str, "GBK");
                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                VerificationActivity.mService.write(nameFontformat);

                int count = 0, totQty = 0;
                int a = Integer.parseInt(tableid);
                List<Integer> _list = order_hash_map.get(a);
                StringBuilder data = new StringBuilder();
                for (int i = 0; i < _list.size(); i++) {
                    List<String> list = order_prod_hash_map.get(_list.get(i));
                    StringBuilder item = new StringBuilder(list.get(0));
                    String item1 = list.get(0);
                    int flag = 0;

                    /*double qt = Double.parseDouble(list.get(1));
                    double r = Double.parseDouble(list.get(2));
                    double gstper = Double.parseDouble(list.get(6));
                    double ta = ((qt*r)*gstper)/100;*/
                    //String testPrintLine = "Pizza-Capsicum Oni-Qty-Rate-Amnt";
                    if (item.length() >= 14) {
                        item = new StringBuilder(item.substring(0, 13));
                        item.append(" ");
                        flag = 1;
                    } else {
                        int size = 13 - item.length();
                        for (int j = 0; j < size; j++) {
                            item.append(" ");
                        }
                        item.append(" ");
                    }

                    String qty = list.get(1);
                    //totQty = totQty + Integer.parseInt(qty);
                    if (qty.length() == 1) {
                        qty = "  " + qty;
                    } else if (qty.length() == 2) {
                        qty = " " + qty;
                    }
                    //qty += " ";
                    String rate = list.get(2);
                    /*String[] rateArr = rate1.split("\\.");
                    if (rateArr.length > 1) {
                        rate = rateArr[0];
                    } else {
                        rate = rate1;
                    }

                    if (rate.length() == 2) {
                        rate = "  " + rate;
                    } else if (rate.length() == 3) {
                        rate = " " + rate;
                    }
                    rate += " ";*/

                    if (rate.length() == 4) {
                        rate = "   " + rate;
                    } else if (rate.length() == 5) {
                        rate = "  " + rate;
                    }else if (rate.length() == 6) {
                        rate = " " + rate;
                    }
                    String amnt = list.get(3);
                    /*String[] amntArr = amnt1.split("\\.");
                    if (amntArr.length > 1) {
                        amnt = amntArr[0];
                    } else {
                        amnt = amnt1;
                    }
                    if (amnt.length() == 2) {
                        amnt = "  " + amnt;
                    } else if (amnt.length() == 3) {
                        amnt = " " + amnt;
                    }*/
                    if (amnt.length() == 4) {
                        amnt = "   " + amnt;
                    } else if (amnt.length() == 5) {
                        amnt = "  " + amnt;
                    }else if (amnt.length() == 6) {
                        amnt = " " + amnt;
                    }

                    if (flag != 1) {
                        data.append(item).append(qty).append(rate).append(amnt).append("\n");
                        textData.append("").append(item).append(qty).append(rate).append(amnt).append("\n");
                    } else {
                        String q = item1.substring(13, item1.length());
                        if (q.length() < 32) {
                            data.append(item).append(qty).append(rate).append(amnt).append("\n").append(q).append("\n");
                            textData.append("").append(item).append(qty).append(rate).append(amnt).append("\n").append(q).append("\n");
                        }
                    }
                    count++;
                }

                String _count = String.valueOf(count);

                VerificationActivity.mService.sendMessage(data.toString(), "GBK");
                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x8 | arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage(line_str, "GBK");
                textData.delete(0, textData.length());

                totalamt = tv_totalamt.getText().toString();
                String[] totArr = totalamt.split("\\.");
                if (totArr.length > 1) {
                    totalamt = totArr[0];
                }
                //textData.append("Total              ").append("  "+count).append("      ").append(totalamt).append("\n");
                if (_count.length() == 1 && totalamt.length() == 2) {
                    textData.append("Total          ").append("  ").append(count).append("        ").append(roundTwoDecimals(String.valueOf(totalRateAmnt))).append("\n");
                } else if (_count.length() == 1 && totalamt.length() == 3) {
                    textData.append("Total          ").append("  ").append(count).append("       ").append(roundTwoDecimals(String.valueOf(totalRateAmnt))).append("\n");
                } else if (_count.length() == 1 && totalamt.length() == 4) {
                    textData.append("Total          ").append(count).append("      ").append(roundTwoDecimals(String.valueOf(totalRateAmnt))).append("\n");
                }
                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage(textData.toString(), "GBK");

                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage("CGST " + cgstPerStr + " % : " + roundTwoDecimals(String.valueOf(totcgstAmt)), "GBK");
                VerificationActivity.mService.sendMessage("SGST " + sgstPerStr + " % : " + roundTwoDecimals(String.valueOf(totsgstAmt)), "GBK");
                /*VerificationActivity.mService.sendMessage("GST Summary", "GBK");

                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x8 | arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage(line_str, "GBK");

                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x0 | arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage("Description " + " GSTPER " + " CGST " + " SGST ", "GBK");

                //VerificationActivity.mService.sendMessage("GST 5%      "+"   1000  "+"  25  "+"  25  ","GBK");
                //VerificationActivity.mService.sendMessage("GST 12%     "+"   4000  "+"  240 "+"  240 ","GBK");
                //VerificationActivity.mService.sendMessage("GST 18%     "+"   8000  "+"  720 "+"  720 ","GBK");
                //VerificationActivity.mService.sendMessage("GST 24%     "+"  11000  "+"  1320"+"  1320","GBK");

                for (int i = 0; i < gstDesc.size(); i++) {
                    VerificationActivity.mService.sendMessage(gstDesc.get(i), "GBK");
                }*/

                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x8 | arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage(line_str, "GBK");

                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x16 | arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage("NET AMNT              " + totalamt, "GBK");


                byte[] left2 = {27, 97, 0};
                VerificationActivity.mService.write(left2);
                nameFontformat[2] = arrayOfByte1[2];
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage("    www.paitsystems.com", "GBK");

                VerificationActivity.mService.write(PrinterCommands.ESC_ENTER);
                String space_str = "                        ";
                VerificationActivity.mService.sendMessage(space_str, "GBK");

                Log.d("Log", textData.toString());
            } catch (Exception e) {
                e.printStackTrace();
                str = "Printer 3 May Not Be Connected ";
                return str;
            }
            return "Order Received By Kitchen 3";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("Print3", result);
            pd.dismiss();
            showDia(4);
        }
    }

    private String roundTwoDecimals(String d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return twoDForm.format(Double.parseDouble(d));
    }

    private void saveUpdateBillMasterLocal() {
        String data;
        try {
            old_order_prod_hash_map.clear();
            Cursor detRes = db.getBillDetailData(selMastAuto);
            if(detRes.moveToFirst()){
                do{
                    List<String> list = new ArrayList<>();
                    int prodId = detRes.getInt(detRes.getColumnIndex(DBHandlerR.BillDetail_Itemid));
                    String qty = detRes.getString(detRes.getColumnIndex(DBHandlerR.BillDetail_Qty));
                    String netAmt = detRes.getString(detRes.getColumnIndex(DBHandlerR.BillDetail_NetAmt));
                    list.add(qty);
                    list.add(netAmt);
                    old_order_prod_hash_map.put(prodId,list);
                }while (detRes.moveToNext());
            }
            detRes.close();

            db.deleteMaster(selMastAuto);
            db.deleteDetail(selMastAuto);
            int counterNo = 1, DCAuto = 0;
            float paidAmnt,coupanAmnt,vat12,vat5,AdvAmt,PIAmnt = 0,
                    Tender,Taxper,VatPer,TaxAmt,OtherAmt,Disper = 0,
                    DiscAmt = 0, SCTaxper = 0, SCTaxamt = 0, TotalTax = 0, Return = 0;
            double discPer,totdiscAmt = 0, totGstAmt = 0;

            String machineName = "Mob", AgainstDC = "NA", TaxSt = "Y",
                    CmpSt = "N", barcode = "NA", BillPaySt = "Y", finyr;

            float BDiscper = 0, BDiscAmt = 0;

            int year1 = new Date().getYear();
            finyr = year1 + "-" + (year1+1);
            Constant.showLog("FINYR "+finyr);

            retAmt = Float.parseFloat(tv_return.getText().toString());
            totalRateAmnt = 0;
            totcgstAmt = 0;
            totsgstAmt = 0;

            int mastAuto = db.getMaxAuto();

            String date1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
            String crDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Calendar.getInstance().getTime());
            String time = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime());
            String[] dateArr = date1.split("/");
            String day = dateArr[0];
            String month = dateArr[1];
            String year = dateArr[2];
            int y = Integer.parseInt(year.substring(2, 4));
            String finYr = y + "-" + String.valueOf(y + 1);

            List<Integer> list = order_hash_map.get(Integer.parseInt(tableid));
            for (int i = 0; i < list.size(); i++) {
                List<String> listDet = order_prod_hash_map.get(list.get(i));
                int qty = Integer.parseInt(listDet.get(1));

                List<String> oldListDet = old_order_prod_hash_map.get(list.get(i));
                int oldQty = Integer.parseInt(oldListDet.get(0));

                if(qty!=oldQty){
                    BillDetailClassR det = new BillDetailClassR();
                    det.setBillNo(oldBillNo);
                    det.setQty(Float.parseFloat(String.valueOf(oldQty)));
                    det.setNewQty(Float.parseFloat(String.valueOf(qty)));
                    det.setProd(String.valueOf(list.get(i)));
                    det.setRateStr(roundTwoDecimals(oldListDet.get(1)));
                    det.setNewRate(roundTwoDecimals(listDet.get(3)));
                    db.addupdateBill(det);
                }

                Cursor res = db.getGSTGroupFromProduct(listDet.get(0));
                res.moveToFirst();
                discPer = res.getDouble(res.getColumnIndex(DBHandlerR.Product_Disper));
                double rate = res.getDouble(res.getColumnIndex(DBHandlerR.Product_Rate));
                String gstGroup = res.getString(res.getColumnIndex(DBHandlerR.Product_GSTGroup));
                String taxType = res.getString(res.getColumnIndex(DBHandlerR.Product_TaxTyp));
                res.close();

                Cursor cursor = db.getGSTPer(gstGroup);
                cursor.moveToFirst();
                double gstPer = cursor.getDouble(cursor.getColumnIndex(DBHandlerR.GSTDetail_GSTPer));
                double cgstPer = cursor.getDouble(cursor.getColumnIndex(DBHandlerR.GSTDetail_CGSTPer));
                double sgstPer = cursor.getDouble(cursor.getColumnIndex(DBHandlerR.GSTDetail_SGSTPer));
                double cgstShare = cursor.getDouble(cursor.getColumnIndex(DBHandlerR.GSTDetail_CGSTShare));
                double sgstShare = cursor.getDouble(cursor.getColumnIndex(DBHandlerR.GSTDetail_SGSTShare));
                cursor.close();

                cgstPerStr = String.valueOf(cgstPer);
                sgstPerStr = String.valueOf(sgstPer);

                double cgstAmt = 0, sgstAmt = 0, discAmt = 0, gstAmt = 0, total = 0, netAmt = 0;

                if (taxType.equals("E")) {
                    double amt = (rate * qty);
                    netAmt = amt;
                    netAmt = (netAmt * discPer) / 100;
                    netAmt = netAmt - discAmt;
                    gstAmt = (netAmt * gstPer) / 100;
                    cgstAmt = (gstAmt * cgstShare) / 100;
                    sgstAmt = (gstAmt * sgstShare) / 100;
                    netAmt = netAmt + gstAmt;
                    //listDet.set(3, roundTwoDecimals(String.valueOf(amt)));
                    //listDet.set(4, roundTwoDecimals(String.valueOf(netAmt)));
                    //order_prod_hash_map.put(list.get(i), listDet);
                    Constant.showLog(listDet.get(0) + "-" + discPer + "-" + qty + "-" + rate + "-" + discAmt + "-" + amt + "-" + gstAmt + "-" + cgstAmt + "-" + sgstAmt);
                } else if (taxType.equals("I")) {
                    double accValue = ((gstPer * 100) / (gstPer + 100));
                    double rate1 = (rate * accValue) / 100;
                    rate = rate - rate1;
                    double amt = (rate * qty);
                    netAmt = amt;
                    discAmt = (netAmt * discPer) / 100;
                    netAmt = netAmt - discAmt;
                    gstAmt = (netAmt * gstPer) / 100;
                    cgstAmt = (gstAmt * cgstShare) / 100;
                    sgstAmt = (gstAmt * sgstShare) / 100;
                    netAmt = netAmt + gstAmt;
                    //listDet.set(2, roundTwoDecimals(String.valueOf(rate)));
                    //listDet.set(3, roundTwoDecimals(String.valueOf(amt)));
                    //listDet.set(4, roundTwoDecimals(String.valueOf(netAmt)));
                    //order_prod_hash_map.put(list.get(i), listDet);
                    Constant.showLog(listDet.get(0) + "-" + discPer + "-" + qty + "-" + rate + "-" + discAmt + "-" + amt + "-" + gstAmt + "-" + cgstAmt + "-" + sgstAmt + "-" + accValue + "-" + netAmt);
                }

                double a = rate * qty;
                total = total + a;
                totalRateAmnt = totalRateAmnt + total;
                totGstAmt = totGstAmt + gstAmt;
                totcgstAmt = totcgstAmt + cgstAmt;
                totsgstAmt = totsgstAmt + sgstAmt;
                totdiscAmt = totdiscAmt + discAmt;

                int detAuto = db.getMaxDetAuto();
                int detId = db.getMaxDetId(mastAuto);
                BillDetailClassR detail = new BillDetailClassR();
                detail.setAuto(detAuto);
                detail.setID(detId);
                detail.setBillID(mastAuto);
                detail.setBranchId(branchid);
                detail.setFinyr(finYr);
                detail.setItemid(list.get(i));
                detail.setRateStr(roundTwoDecimals(String.valueOf(rate)));
                detail.setQty(Float.parseFloat(String.valueOf(qty)));
                detail.setCmpSt(CmpSt);
                detail.setTotalStr(roundTwoDecimals(String.valueOf(total)));
                detail.setVat(0);
                detail.setVatamt(0);
                detail.setAmtWithDiscStr(roundTwoDecimals(String.valueOf(total)));
                detail.setDisper(Float.parseFloat(String.valueOf(discPer)));
                detail.setDisamt(DiscAmt);
                detail.setBillDiscper(BDiscper);
                detail.setBillDiscAmt(BDiscAmt);
                detail.setWaiterID(userid);
                detail.setBarcode(barcode);
                detail.setNetAmtStr(roundTwoDecimals(String.valueOf(netAmt)));
                detail.setGSTPER(Float.parseFloat(String.valueOf(gstPer)));
                detail.setCGSTAMTStr(roundTwoDecimals(String.valueOf(cgstAmt)));
                detail.setSGSTAMTStr(roundTwoDecimals(String.valueOf(sgstAmt)));
                detail.setCGSTPER(Float.parseFloat(String.valueOf(cgstPer)));
                detail.setSGSTPER(Float.parseFloat(String.valueOf(sgstPer)));
                detail.setCESSPER(Float.parseFloat(cessPer));
                detail.setCESSAMTStr(roundTwoDecimals(String.valueOf(cessAmt)));
                db.saveBillDetail(detail);
            }
            int id = db.getMaxMastId(finYr);
            billNo = "LI/B/" + finYr + "/" + month + day + mastAuto;

            coupanAmnt = 0;
            AdvAmt = 0;
            Taxper = 0;
            Tender = 0;
            VatPer = 0;
            TaxAmt = 0;
            OtherAmt = 0;
            paidAmnt = cardAmt + cashAmt + coupanAmnt;
            vat5 = 0;
            vat12 = 0;
            int NOP = 0;
            String OrderNo = "0";
            balAmt = netAmt-paidAmnt;

            BillMasterClassR master = new BillMasterClassR();
            master.setAuto(mastAuto);
            master.setId(id);
            master.setBranchId(branchid);
            master.setFinyr(finYr);
            master.setBillNo(billNo);
            master.setBillDate(crDate);
            master.setBillTime(time);
            master.setCustID(userid);
            master.setTotalQty(totalqtyFl);
            master.setTotalAmtStr(roundTwoDecimals(String.valueOf(totalRateAmnt)));
            master.setCardAmt(cardAmt);
            master.setCashAmt(cashAmt);
            master.setCoupanAmt(coupanAmnt);
            master.setPaidAmt(paidAmnt);
            master.setBalAmt(balAmt);
            master.setNetAmt(netAmt);
            master.setBillSt("A");
            master.setCrBy(userid);
            master.setCrDate(crDate);
            master.setVat12(vat12);
            master.setVat5(vat5);
            master.setDisper(Disper);
            master.setDisAmt(DiscAmt);
            master.setAmtInWords("");
            master.setPIAmt(PIAmnt);
            master.setPIno(OrderNo);
            master.setCounterNo(String.valueOf(counterNo));
            master.setMachineName(machineName);
            master.setAgainstDC(AgainstDC);
            master.setTableID(Integer.parseInt(tableid));
            master.setLocationID(1);
            master.setSalesMan(userid);
            master.setWaiterID(userid);
            master.setServiceTax(SCTaxper);
            master.setServiceAmt(SCTaxamt);
            master.setNoOfPass(NOP);
            master.setAdvamt(AdvAmt);
            master.setKOTNo(kotno);
            master.setDCAutoNo(DCAuto);
            master.setTaxst(TaxSt);
            master.setTaxper(Taxper);
            master.setVatamt(TotalTax);
            master.setTender(Tender);
            master.setRemainAmt(Return);
            master.setBillPaySt(BillPaySt);
            master.setVatPer(VatPer);
            master.setTaxAmt(TaxAmt);
            master.setOtherAmt(OtherAmt);
            master.setCGSTAMTStr(roundTwoDecimals(String.valueOf(totcgstAmt)));
            master.setSGSTAMTStr(roundTwoDecimals(String.valueOf(totsgstAmt)));

            db.saveBillMaster(master, mastAuto, kotno);
            new CashMemoPrint().execute();

        } catch (Exception e) {
            e.printStackTrace();
            data = "CashMemoActivity_savebill_" + e.getMessage();
            writeLog(data);
        }
    }

    private void writeLog(String data) {
        new WriteLog().writeLog(getApplicationContext(), "CashMemoActivity_" + data);
    }

    private void setTotalNetAmnt() {
        float _totalAmnt = Float.parseFloat(tv_totalamt.getText().toString());
        String gstAmnt = tv_gst_amnt.getText().toString();
        String gstAmntArr[] = gstAmnt.split("\\.");
        int realValue, pointValue;
        if (gstAmntArr.length > 1) {
            realValue = Integer.parseInt(gstAmntArr[0]);
            pointValue = Integer.parseInt(gstAmntArr[1]);
            if (pointValue >= 5) {
                realValue++;
            }
        } else {
            realValue = Integer.parseInt(gstAmnt);
        }
        float _totalTaxAmnt = Float.parseFloat(String.valueOf(realValue));
        netAmt = _totalAmnt + _totalTaxAmnt;
        tv_netamt.setText(String.valueOf(netAmt));
        ed_cash.setText(String.valueOf(netAmt));
    }

    private void saveBill() {
        String IMEINo = VerificationActivity.pref.getString(getString(R.string.pref_IMEI), "");
        String getAuto = "select max(" + DBHandlerR.BillMaster_Auto + ") from " + DBHandlerR.BillMaster_Table;
        int auto = db.getTempColumn(getAuto);
        int id = db.getMax(DBHandlerR.BillMaster_Table);

        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String crDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String time = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String[] dateArr = date.split("/");
        String day = dateArr[0];
        String month = dateArr[1];
        String year = dateArr[2];
        int y = Integer.parseInt(year.substring(2, 4));
        String finYr = y + "-" + String.valueOf(y + 1);
        String billNo = "LI/B/" + finYr + "/" + month + day + id;

        String PIAmnt = "0", BillPaySt = "Y",
                TaxSt = "Y", OrderNo = "0",
                counterNo = "1",
                AgainstDC = "N";

        int NOP = 0, DCAuto = 0;

        cardAmt = Float.parseFloat(ed_credit.getText().toString());
        cashAmt = Float.parseFloat(ed_cash.getText().toString());
        float coupanAmnt = 0,
                paidAmnt = cardAmt + cashAmt + coupanAmnt,
                vat5 = 0, vat12 = 0, Disper = 0, DiscAmt = 0,
                SCTaxper = 0, SCTaxamt = 0,
                AdvAmt = 0, Taxper = 0,
                TotalTax = 0, Tender = 0,
                Return = 0, VatPer = 0,
                TaxAmt = 0, OtherAmt = 0;

        BillMasterClassR master = new BillMasterClassR();
        master.setAuto(auto);
        master.setId(id);
        master.setBranchId(branchid);
        master.setFinyr(finYr);
        master.setBillNo(billNo);
        master.setBillDate(crDate);
        master.setBillTime(time);
        master.setCustID(userid);
        master.setTotalQty(Float.parseFloat(totalqty));
        master.setTotalAmt(Float.parseFloat(tv_totalamt.getText().toString()));
        master.setCardAmt(cardAmt);
        master.setCashAmt(cashAmt);
        master.setCoupanAmt(coupanAmnt);
        master.setPaidAmt(paidAmnt);
        master.setBalAmt(balAmt);
        master.setNetAmt(netAmt);
        master.setBillSt("A");
        master.setCrBy(userid);
        master.setCrDate(crDate);
        master.setVat12(vat12);
        master.setVat5(vat5);
        master.setDisper(Disper);
        master.setDisAmt(DiscAmt);
        master.setAmtInWords("");
        master.setPIAmt(Float.parseFloat(PIAmnt));
        master.setPIno(OrderNo);
        master.setCounterNo(counterNo);
        master.setMachineName(IMEINo);
        master.setAgainstDC(AgainstDC);
        master.setTableID(Integer.parseInt(tableid));
        master.setLocationID(1);
        master.setSalesMan(userid);
        master.setWaiterID(userid);
        master.setServiceTax(SCTaxper);
        master.setServiceAmt(SCTaxamt);
        master.setNoOfPass(NOP);
        master.setAdvamt(AdvAmt);
        master.setKOTNo(kotno);
        master.setDCAutoNo(DCAuto);
        master.setTaxst(TaxSt);
        master.setTaxper(Taxper);
        master.setVatamt(TotalTax);
        master.setTender(Tender);
        master.setRemainAmt(Return);
        master.setBillPaySt(BillPaySt);
        master.setVatPer(VatPer);
        master.setTaxAmt(TaxAmt);
        master.setOtherAmt(OtherAmt);
        master.setCGSTAMT(Float.parseFloat(cgstAmt));
        master.setSGSTAMT(Float.parseFloat(sgstAmt));

        int mastID = db.saveBillMaster(master, auto, kotno);
        //saveBillDetail(mastID);

        /*try {
            todaysDate = URLEncoder.encode(todaysDate, "UTF-8");
            String url = LoginActivity.ipaddress + "/SaveBillData?BranchId=" + branchid + "&TableId=" + tableid + "&LocationId=1&BillDate=" + todaysDate +
                    "&CrBy=" + userid + "&TotalQty=" + totalqty + "&Total=" + totalamt + "&CashAmt=" + ed_cash.getText().toString() + "&CardAmt=" + ed_credit.getText().toString() + "&BalAmt=" + tv_balance.getText().toString()
                    + "&NetAmt=" + totalamt + "&WaiterId=" + userid + "&KOTNo=" + kotno;
            Log.d("Log", url);
            new SaveBillMaster().execute(url);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }*/
    }

    private void saveBillDetail(int mastid) {
        List<Integer> list = order_hash_map.get(Integer.parseInt(tableid));
        StringBuilder data = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            List<String> list1 = order_prod_hash_map.get(list.get(i));
            //Itemid-Rate-Qty-Total-Barcode
            float _amnt = Float.parseFloat(list1.get(3));
            float _gstPer = Float.parseFloat(gstPer);
            float _cgstPer = Float.parseFloat(cgstPer);
            float _sgstPer = Float.parseFloat(sgstPer);
            float _gstAmt = (_amnt * _gstPer) / 100;
            float _cgstAmt = (_amnt * _cgstPer) / 100;
            float _sgstAmt = (_amnt * _sgstPer) / 100;
            data.append(list.get(i)).append("-").append(list1.get(2)).append("-").append(list1.get(1)).append("-").append(list1.get(3)).append("-").append(list1.get(4)).append(",");
            Constant.showLog(data.toString());
            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
            String[] dateArr = date.split("/");
            String year = dateArr[2];
            int y = Integer.parseInt(year.substring(2, 4));
            String finYr = y + "-" + String.valueOf(y + 1);

            String getAuto = "select max(" + DBHandlerR.BillDetail_Auto + ") from " + DBHandlerR.BillDetail_Table + " where " + DBHandlerR.BillDetail_BranchId + "=" + branchid;
            int auto = db.getTempColumn(getAuto);

            String getID = "select max(" + DBHandlerR.BillDetail_ID + ") from " + DBHandlerR.BillDetail_Table + " where " + DBHandlerR.BillDetail_BillID + "=" + mastid + " and " + DBHandlerR.BillDetail_BranchId + "=" + branchid + " and " + DBHandlerR.BillDetail_Finyr + "='" + finYr + "'";
            int id = db.getTempColumn(getID);

            String CmpSt = "N";
            float Disc = 0;
            float DiscAmt = 0;
            float TaxPer = 0;
            float TaxAmt = 0;
            float BDiscper = 0;
            float BDiscAmt = 0;

            String rate = list1.get(2), qty = list1.get(1), total = list1.get(3), barcode = list1.get(4);
            BillDetailClassR detail = new BillDetailClassR();
            detail.setAuto(auto);
            detail.setID(id);
            detail.setBillID(mastid);
            detail.setBranchId(branchid);
            detail.setFinyr(finYr);
            detail.setItemid(list.get(i));
            detail.setRate(Float.parseFloat(rate));
            detail.setQty(Float.parseFloat(qty));
            detail.setCmpSt(CmpSt);
            detail.setTotal(Float.parseFloat(total));
            detail.setVat(TaxPer);
            detail.setVatamt(TaxAmt);
            detail.setAmtWithDisc(Float.parseFloat(total));
            detail.setDisper(Disc);
            detail.setDisamt(DiscAmt);
            detail.setBillDiscper(BDiscper);
            detail.setBillDiscAmt(BDiscAmt);
            detail.setWaiterID(userid);
            detail.setBarcode(barcode);
            detail.setNetAmt(Float.parseFloat(total));
            detail.setGSTPER(_gstPer);
            detail.setCGSTAMT(_cgstAmt);
            detail.setSGSTAMT(_sgstAmt);
            detail.setCGSTPER(_cgstPer);
            detail.setSGSTPER(_sgstPer);
            detail.setCESSPER(Float.parseFloat(cessPer));
            detail.setCESSAMT(Float.parseFloat(cessAmt));
            db.saveBillDetail(detail);

        }
        showDia(4);
        /*try {
            data = URLEncoder.encode(data, "UTF-8");
            String url = LoginActivity.ipaddress + "/SaveBillDetails?Mastid=" + mastid + "&Branchid=" + branchid + "&Waiterid=" + userid + "&Data=" + data;
            Log.d("Log", url);
            new SaveBillDetail().execute(url);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }*/
    }

    private void connectBT() {
        Set<BluetoothDevice> set = VerificationActivity.mService.getPairedDev();
        if (!set.isEmpty()) {
            for (BluetoothDevice device : set) {
                BluetoothDevice con_dev = VerificationActivity.mService.getDevByMac(device.getAddress());
                //Log.d("Log", "getDevByMac :- "+ con_dev);
                VerificationActivity.mService.connect(con_dev);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            toast.setText("Bluetooth Printer Connected");
                            toast.show();
                            //btnClose.setEnabled(true);
                            //btnSend.setEnabled(true);
                            //qrCodeBtnSend.setEnabled(true);
                            //btnSendDraw.setEnabled(true);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            break;
                        case BluetoothService.STATE_LISTEN:
                            break;
                        case BluetoothService.STATE_NONE:
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:
                    //connectBT();
                    toast.setText("Device connection was lost");
                    toast.show();
                    //btnClose.setEnabled(false);
                    //btnSend.setEnabled(false);
                    //qrCodeBtnSend.setEnabled(false);
                    //btnSendDraw.setEnabled(false);
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:
                    //connectBT();
                    toast.setText("Unable to Connect Bluetooth Printer");
                    toast.show();
                    break;
            }
        }

    };

    /*private class SaveBillMaster extends AsyncTask<String,Void, String>{

        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(CashMemoActivityR.this);
            pd.setCancelable(false);
            pd.setMessage("Please Wait...");
            pd.show();
        }

        @Override
        protected String doInBackground(String... url) {
            return Post.POST(url[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(!result.equals("")){
                result = result.replace("\\","");
                result = result.replace("''","");
                result = result.replace("\"","");
                Log.d("Log",result);
                pd.dismiss();
                if(!result.equals("0")){
                    //showDia(4);
                    saveBillDetail(Integer.parseInt(result));
                    //new Print3().execute();
                    //Toast.makeText(getApplicationContext(),"Bill Saved Successfully",Toast.LENGTH_LONG).show();
                }else{
                    showDia(5);
                    //Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                }
            }
        }
    }*/

    /*private class SaveBillDetail extends AsyncTask<String,Void, String>{

        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(CashMemoActivityR.this);
            pd.setCancelable(false);
            pd.setMessage("Please Wait...");
            pd.show();
        }

        @Override
        protected String doInBackground(String... url) {
            return Post.POST(url[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null && result.length() != 0) {
                try {
                    String str = new JSONObject(result).getString("SaveBillDetailsResult");
                    if (!str.equals("\"0\"")) {
                        showDia(4);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showDia(5);
                }
            }else{
                showDia(5);
            }
            pd.dismiss();
        }
    }*/

    /*private class Print3 extends AsyncTask<Void, Void, String> {

        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(CashMemoActivityR.this);
            pd.setCancelable(false);
            pd.setMessage("Please Wait...");
            pd.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String str;//method
            //Bitmap logoData = BitmapFactory.decodeResource(getResources(), R.drawable.store);
            StringBuilder textData = new StringBuilder();
            String _dots = "----------------------------------------------\n";
            try {
                Printer _mPrinter = new Printer(0, 0, CashMemoActivityR.this);
                _mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                _mPrinter.addFeedLine(1);
                //textData.append("\t\t\t\t").append("Cake Studio").append("\n");
                //textData.append("\t\t").append("Tilak Road, Sadashiv Peth,").append("\n");

                textData.append("\t\t\t\t").append("LNB Infotech").append("\n");
                textData.append("\t\t").append("230, Sadashiv Peth,").append("\n");

                textData.append("\t\t").append("Pune, Maharashtra - 411030").append("\n");
                textData.append("\t\t\t").append("Ph No. 9370716834").append("\n");
                textData.append("\t\t\t\t").append("Cash Memo").append("\n");
                textData.append("KOT No: ").append(kotno).append("\t\t ").append(todaysDate).append("    ").append("Time").append("\n");
                textData.append("TableClass: ").append(tableid).append("\t\t Waiter: ").append(userid).append("\n");
                textData.append(_dots);
                textData.append("Item                     Qty      Rate    Amnt\n");
                textData.append(_dots);
                _mPrinter.addText(textData.toString());
                Log.d("Print",textData.toString());
                textData.delete(0, textData.length());
                int count = 0;
                int a = Integer.parseInt(tableid);
                List<Integer> _list = order_hash_map.get(a);
                for (int i = 0; i < _list.size(); i++) {
                    List<String> list = order_prod_hash_map.get(_list.get(i));
                    String item = list.get(0);
                    String item1 = list.get(0);
                    int flag = 0;
                    if(item.length()>24) {
                        item = item.substring(0, 23);
                        flag = 1;
                    }else{
                        int size = 23-item.length();
                        for(int j=0;j<size;j++){
                            item = item+" ";
                        }
                    }
                    String qty = list.get(1);
                    if(qty.length()==1){
                        qty = "    " + qty;
                    }else if(qty.length()==2){
                        qty = "    " + qty;
                    }
                    String rate = list.get(2);
                    if(rate.length()>4){
                        for(int j=0;j<5;j++){
                            qty = qty+" ";
                        }
                    }else{
                        for(int j=0;j<6;j++){
                            qty = qty+" ";
                        }
                    }
                    String amnt = list.get(3);
                    if(amnt.length()>4){
                        for(int j=0;j<3;j++){
                            rate = rate+" ";
                        }
                    }else{
                        for(int j=0;j<4;j++){
                            rate = rate+" ";
                        }
                    }
                    if(flag!=1) {
                        textData.append("").append(item).append(qty).append(rate).append(amnt).append("\n");
                    }else{
                        String q = item1.substring(23,item1.length());
                        if(q.length()<24){
                            textData.append("").append(item).append(qty).append(rate).append(amnt).append("\n").append(q).append("\n");
                        }
                    }
                    count++;
                }
                String _count = String.valueOf(count);
                textData.append(_dots);
                if(_count.length()==2 && totalamt.length()==6) {
                    textData.append("Total                     ").append(count).append("            ").append(totalamt).append("\n");
                    //textData.append("Total                     " + count + "            " + totalamt + "\n");
                }else if(_count.length()==2 && totalamt.length()==5) {
                    textData.append("Total                     ").append(count).append("             ").append(totalamt).append("\n");
                    //textData.append("Total                     " + count + "             " + totalamt + "\n");
                }else if(_count.length()==1 && totalamt.length()==6) {
                    textData.append("Total                      ").append(count).append("           ").append(totalamt).append("\n");
                    //textData.append("Total                      " + count + "           " + totalamt + "\n");
                }else if(_count.length()==1 && totalamt.length()==5) {
                    textData.append("Total                      ").append(count).append("             ").append(totalamt).append("\n");
                    //textData.append("Total                      " + count + "             " + totalamt + "\n");
                }else if(_count.length()==2 && totalamt.length()==4) {
                    textData.append("Total                     ").append(count).append("              ").append(totalamt).append("\n");
                    //textData.append("Total                     " + count + "              " + totalamt + "\n");
                }else if(_count.length()==1 && totalamt.length()==4) {
                    textData.append("Total                      ").append(count).append("              ").append(totalamt).append("\n");
                    //textData.append("Total                      " + count + "              " + totalamt + "\n");
                }
                _mPrinter.addText(textData.toString());
                _mPrinter.addText(textData.toString());
                Log.d("Print",textData.toString());
                textData.delete(0, textData.length());
                _mPrinter.addCut(Printer.CUT_FEED);
                *//*if (order_hash_map.size() != 0) {
                    _mPrinter.connect("TCP:192.168.11.33", Printer.PARAM_DEFAULT);
                    _mPrinter.beginTransaction();
                    _mPrinter.sendData(Printer.PARAM_DEFAULT);
                }*//*
                _mPrinter.clearCommandBuffer();
                _mPrinter.disconnect();
            } catch (Epos2Exception e) {
                e.printStackTrace();
                str = "Printer 3 May Not Be Connected ";
                return str;
            }
            return "Order Received By Kitchen 3";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("Print3", result);
            pd.dismiss();
            //saveBill();
        }
    }*/

    /*private class Print1 extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String str;//method
            //Bitmap logoData = BitmapFactory.decodeResource(getResources(), R.drawable.store);
            StringBuilder textData = new StringBuilder();
            int item_length = 21;
            int netAmnt=0;

            try {
                Printer _mPrinter = new Printer(0, 0, CashMemoActivityR.this);
                //method = "addTextAlign";
                _mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                //method = "addFeedLine";
                _mPrinter.addFeedLine(1);

                byte[] arrayOfByte1 = { 27, 33, 0 };
                byte[] format = { 27, 33,0 };

                byte[] center =  { 27, 97, 1};
                VerificationActivity.mService.write(center);
                byte nameFontformat[] = format;
                nameFontformat[2] = ((byte)(0x20|arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage("Cash Memo","GBK");

                nameFontformat[2] = ((byte)(0x0|arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);

                byte[] left =  { 27, 97, 0 };
                VerificationActivity.mService.write(left);

                //VerificationActivity.mService.sendMessage("KOT No: "+kotno+"   "+date+"   "+getTime(),"GBK");
                VerificationActivity.mService.sendMessage(date+space_str13+getTime(),"GBK");
                //VerificationActivity.mService.sendMessage("TableClass: "+table_hash_map1.get(table_id)+"   "+"Waiter: "+pref.getString("uname", "NA"),"GBK");

                nameFontformat = format;
                nameFontformat[2] = ((byte)(0x8 | arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage(line_str,"GBK");
                //String line_str = "Item                   Qty  Amnt";
                nameFontformat = format;
                nameFontformat[2] = ((byte)(0x0|arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage("Item                   "+"Qty  "+"Amnt","GBK");
                nameFontformat = format;
                nameFontformat[2] = ((byte)(0x8 | arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage(line_str,"GBK");
                nameFontformat = format;
                nameFontformat[2] = ((byte)(0x0|arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);

                *//*textData.append("\t\t").append("Kitchen 1").append("\n");
                textData.append("KOT No: ").append(kotno).append("\t\t ").append(date).append("    ").append(getTime()).append("\n");
                textData.append("TableClass: ").append(table_hash_map1.get(table_id)).append("\t\t Waiter: ").append(pref.getString("uname", "NA")).append("\n");
                textData.append(line_str);
                textData.append("Item"+space_str+"QTY\n");
                textData.append(line_str);
                //method = "addText";
                _mPrinter.addText(textData.toString());
                Log.d("Print1",textData.toString());
                textData.delete(0, textData.length());*//*

                int count = 0;
                for (int i = 0; i < kArea1print_list.size(); i++) {
                    String _value = kArea1print_list.get(i);
                    String[] _item_remark_qty = _value.split("!");
                    String _qty = _item_remark_qty[1];
                    netAmnt += Integer.parseInt(_qty);
                    if(_qty.length()==1){
                        _qty = "  " + _qty;
                    }else if(_qty.length()==2){
                        _qty = " " + _qty;
                    }
                    String[] _item_remark =  _item_remark_qty[0].split("@");
                    if(_item_remark.length>1){
                        String[] _item = _item_remark[0].split("#");
                        String[] _remark = _item_remark[1].split("#");
                        if(_item.length>1 && _remark.length>1){
                            textData.append("").append(_item[0]).append(_remark[0]).append(_qty).append("\n").append(_item[1]).append(_remark[1]).append("\n");
                        }else if(_item.length==1 && _remark.length>1){
                            String space = "";
                            for(int j=0;j<item_length;j++){
                                space = space + " ";
                            }
                            textData.append("").append(_item[0]).append(_remark[0]).append(_qty).append("\n").append(space).append(_remark[1]).append("\n");
                        }else if(_item.length>1&&_remark.length==1){
                            textData.append("").append(_item[0]).append(_remark[0]).append(_qty).append("\n").append(_item[1]).append("\n");
                        }else if(_item.length==1&&_remark.length==1) {
                            textData.append("").append(_item[0]).append(_remark[0]).append(_qty).append("\n");
                        }
                    }else if(_item_remark.length==1){
                        String[] _item = _item_remark[0].split("#");
                        if(_item.length>1){
                            textData.append("").append(_item[0]).append(_qty).append("\n").append(_item[1]).append("\n");
                        }else if(_item.length==1){
                            textData.append("").append(_item_remark[0]).append(_qty).append("\n");
                        }
                    }
                    count++;
                }
                String _count = String.valueOf(count);
                if(_count.length()==1){
                    _count = "  "+_count;
                }else if(_count.length()==2){
                    _count = " "+_count;
                }
                VerificationActivity.mService.sendMessage(textData.toString(),"GBK");
                nameFontformat = format;
                nameFontformat[2] = ((byte)(0x8 | arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage(line_str,"GBK");
                nameFontformat = format;
                nameFontformat[2] = ((byte)(0x0|arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage("Total "+space_str21+netAmnt,"GBK");
                nameFontformat = format;
                nameFontformat[2] = ((byte)(0x8 | arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage(line_str,"GBK");

                nameFontformat = format;
                nameFontformat[2] = ((byte)(0x20|arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                String netAmts = "Net Amnt    "+netAmnt;
                VerificationActivity.mService.sendMessage(netAmts,"GBK");

                VerificationActivity.mService.write(center);
                nameFontformat[2] = ((byte)(0x1|arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                String softwareBy = "Software By LNB Infotech 9370716834";
                //VerificationActivity.mService.sendMessage(softwareBy,"GBK");
                VerificationActivity.mService.write(PrinterCommands.ESC_ENTER);
                VerificationActivity.mService.sendMessage(space_str,"GBK");
                VerificationActivity.mService.sendMessage(space_str,"GBK");

                textData.append(line_str);
                textData.append(space_str+"Total "+_count+"\n");
                //method = "addText";
                _mPrinter.addText(textData.toString());
                Log.d("Print1",textData.toString());
                //textData.delete(0, textData.length());
                //method = "addCut";
                _mPrinter.addCut(Printer.CUT_FEED);
                if (kArea1print_list.size() != 0) {
                    //_mPrinter.connect("BT:DC:0D:30:04:29:55",Printer.PARAM_DEFAULT);
                    //_mPrinter.connect("TCP:192.168.11.11", Printer.PARAM_DEFAULT);
                    //VerificationActivity.mService.sendMessage(textData.toString(), "GBK");
                    //VerificationActivity.mService.sendMessage("Kitchen1", "GBK");
                    //_mPrinter.beginTransaction();
                    //_mPrinter.sendData(Printer.PARAM_DEFAULT);
                }
                //_mPrinter.clearCommandBuffer();
                //_mPrinter.disconnect();
                //Log.d("Log",method);
            } catch (Epos2Exception e) {
                e.printStackTrace();
                str = "Printer 1 May Not Be Connected ";
                return str;
            }
            return "Order Received By Kitchen 1";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("Print1", result);
            Toast toast = Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            //toast.show();
        }
    }*/

}
