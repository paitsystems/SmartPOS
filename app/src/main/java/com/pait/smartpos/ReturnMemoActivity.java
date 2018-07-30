package com.pait.smartpos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hoin.btsdk.BluetoothService;
import com.pait.smartpos.adpaters.AddToCartRecyclerAdapter;
import com.pait.smartpos.adpaters.ReturnMemoBarcodeAdapter;
import com.pait.smartpos.adpaters.ReturnMemoBillNoAdapter;
import com.pait.smartpos.adpaters.ReturnMemoRecyclerAdapter;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.constant.PrinterCommands;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.model.AddToCartClass;
import com.pait.smartpos.model.BillDetailClass;
import com.pait.smartpos.model.BillMasterClass;
import com.pait.smartpos.model.ReturnMemoDetailClass;
import com.pait.smartpos.model.ReturnMemoMasterClass;
import com.pait.smartpos.model.UserProfileClass;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.support.v7.widget.RecyclerView.HORIZONTAL;

public class ReturnMemoActivity extends AppCompatActivity implements View.OnClickListener{

    private RadioButton rdo_retMemo, rdo_cashback;
    private TextView tv_billAmnt, tv_paidAmnt, tv_totQty, tv_totAmnt, tv_custName, tv_mobNo;
    private Button btn_save;
    private AutoCompleteTextView auto_billNo, auto_barcode;
    private RecyclerView rv_returnMemo;
    private Constant constant;
    private DBHandler db;
    private Toast toast;
    private ArrayList<BillMasterClass> billMastList;
    private ArrayList<BillDetailClass> billDetList, retMemoList;
    private BillMasterClass billMaster;
    private BillDetailClass billDetail;
    private ReturnMemoRecyclerAdapter adapter;
    private float totQty = 0, totAmnt = 0, totDiscAmnt = 0, totCGSTAmnt = 0, totSGSTAmnt = 0;
    private BluetoothService mService;
    private BluetoothDevice con_dev = null;
    private String memoNo, customerName;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_memo);

        init();

        setBillNo();

        if (mService != null) {
            mService.stop();
        }
        mService = new BluetoothService(getApplicationContext(), mHandler1);
        connectBT();

        auto_billNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    auto_billNo.showDropDown();
                    auto_billNo.setThreshold(0);
                }
                return false;
            }
        });

        auto_barcode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    auto_barcode.showDropDown();
                    auto_barcode.setThreshold(0);
                }
                return false;
            }
        });

        auto_billNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int j, long l) {
                totQty = 0;
                totAmnt = 0;
                totSGSTAmnt = 0;
                totCGSTAmnt = 0;
                totDiscAmnt = 0;
                auto_barcode.setText(null);
                retMemoList.clear();
                rv_returnMemo.setAdapter(null);
                tv_totQty.setText("0");
                tv_totAmnt.setText("0");
                tv_custName.setText("");
                tv_billAmnt.setText("0");
                tv_paidAmnt.setText("0");
                billMaster = (BillMasterClass) adapterView.getItemAtPosition(j);
                System.out.println("Position " + j+"-"+billMaster.getBillNo());
                String custName = db.getCustName(billMaster.getCustID());
                tv_custName.setText(custName);
                tv_billAmnt.setText(billMaster.getTotalAmt());
                tv_paidAmnt.setText(billMaster.getPaidAmt());
                setBillDet();
            }
        });

        auto_barcode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int j, long l) {
                auto_barcode.setText(null);
                billDetail = (BillDetailClass) adapterView.getItemAtPosition(j);
                totQty = totQty + stringToInt(billDetail.getQty());
                totAmnt = totAmnt + stringToFloat(billDetail.getTotal())+
                        stringToFloat(billDetail.getCGSTAMT())+
                        stringToFloat(billDetail.getSGSTAMT());
                totDiscAmnt = totDiscAmnt + stringToFloat(billDetail.getBilldisamt());
                totCGSTAmnt = totCGSTAmnt + stringToFloat(billDetail.getCGSTAMT());
                totSGSTAmnt = totSGSTAmnt + stringToFloat(billDetail.getSGSTAMT());
                tv_totQty.setText(roundTwoDecimals(totQty));
                tv_totAmnt.setText(roundTwoDecimals(totAmnt));
                System.out.println("Position " + j+"-"+billDetail.getFatherSKU());
                retMemoList.add(billDetail);
                if(retMemoList.size()==1) {
                    adapter = new ReturnMemoRecyclerAdapter(getApplicationContext(), retMemoList);
                    rv_returnMemo.setAdapter(adapter);
                }else{
                    adapter.notifyDataSetChanged();
                }
                rv_returnMemo.scrollToPosition(retMemoList.size()-1);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rdo_retMemo:
                rdo_retMemo.setChecked(true);
                rdo_cashback.setChecked(false);
                break;
            case R.id.rdo_cashback:
                rdo_retMemo.setChecked(false);
                rdo_cashback.setChecked(true);
                break;
            case R.id.btn_save:
                if(billMaster!=null) {
                    showDia(1);
                }else {
                    toast.setText("Please Select Bill Number");
                    toast.show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        new Constant(ReturnMemoActivity.this).doFinish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                new Constant(ReturnMemoActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    private class CashMemoPrint extends AsyncTask<Void, Void, String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ReturnMemoActivity.this);
            pd.setCancelable(false);
            pd.setMessage("Please Wait...");
            pd.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String str;
            StringBuilder textData = new StringBuilder();
            try {
                byte[] arrayOfByte1 = {27, 33, 0};
                byte[] format = {27, 33, 0};

                byte[] center = {27, 97, 1};
                mService.write(PrinterCommands.ESC_ALIGN_CENTER);
                byte nameFontformat[] = format;
                nameFontformat[2] = ((byte) (0x20 | arrayOfByte1[2]));
                mService.write(nameFontformat);

                UserProfileClass user = new Constant(getApplicationContext()).getPref();

                mService.sendMessage(user.getFirmName(), "GBK");

                nameFontformat[2] = arrayOfByte1[2];
                mService.write(nameFontformat);

                mService.sendMessage(user.getCity(), "GBK");
                mService.sendMessage(user.getMobileNo(), "GBK");
                mService.sendMessage("Credit Note", "GBK");

                byte[] left = {27, 97, 0};
                mService.write(PrinterCommands.ESC_ALIGN_LEFT);

                String date = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                String time = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime());

                String space_str13 = "             ";
                mService.sendMessage(date + space_str13 + time, "GBK");
                mService.sendMessage("Memo No : " + memoNo, "GBK");
                mService.sendMessage("BillNo : " + billMaster.getBillNo(), "GBK");
                mService.sendMessage("Customer Name : " + tv_custName.getText().toString(), "GBK");

                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x8 | arrayOfByte1[2]));
                mService.write(nameFontformat);
                String line_str = "--------------------------------";
                mService.sendMessage(line_str, "GBK");
                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                mService.write(nameFontformat);

                mService.sendMessage("Item           " + "Qty" + "  Rate" + "  Amnt", "GBK");
                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x8 | arrayOfByte1[2]));
                mService.write(nameFontformat);
                mService.sendMessage(line_str, "GBK");
                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                mService.write(nameFontformat);

                int count = 0, totQty = 0;

                StringBuilder data = new StringBuilder();
                for (int i = 0; i < retMemoList.size(); i++) {
                    BillDetailClass cart = retMemoList.get(i);
                    StringBuilder item = new StringBuilder(cart.getFatherSKU());
                    String item1 = cart.getFatherSKU();
                    int flag = 0;
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

                    String qty = String.valueOf(cart.getQty());
                    if (qty.length() == 1) {
                        qty = "  " + qty;
                    } else if (qty.length() == 2) {
                        qty = " " + qty;
                    }

                    String rate = String.valueOf(cart.getRate());
                    if (rate.length() == 1) {
                        rate = "      " + rate;
                    }else if (rate.length() == 2) {
                        rate = "     " + rate;
                    }else if (rate.length() == 3) {
                        rate = "     " + rate;
                    }else if (rate.length() == 4) {
                        rate = "   " + rate;
                    } else if (rate.length() == 5) {
                        rate = "  " + rate;
                    }else if (rate.length() == 6) {
                        rate = " " + rate;
                    }

                    String amnt = String.valueOf(cart.getTotal());
                    if (amnt.length() == 1) {
                        amnt = "      " + amnt;
                    }else if (amnt.length() == 2) {
                        amnt = "     " + amnt;
                    }else if (amnt.length() == 3) {
                        amnt = "    " + amnt;
                    }else if (amnt.length() == 4) {
                        amnt = "   " + amnt;
                    }else if (amnt.length() == 5) {
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

                mService.sendMessage(data.toString(), "GBK");
                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x8 | arrayOfByte1[2]));
                mService.write(nameFontformat);
                mService.sendMessage(line_str, "GBK");
                textData.delete(0, textData.length());

                String  totalamt = String.valueOf(totAmnt);
                String[] totArr = totalamt.split("\\.");
                if (totArr.length > 1) {
                    totalamt = totArr[0];
                }
                //textData.append("Total              ").append("  "+count).append("      ").append(totalamt).append("\n");
                if (_count.length() == 1 && totalamt.length() == 2) {
                    textData.append("Total          ").append("  ").append(count).append("        ").append(roundTwoDecimals(String.valueOf(totAmnt))).append("\n");
                } else if (_count.length() == 1 && totalamt.length() == 3) {
                    textData.append("Total          ").append("  ").append(count).append("       ").append(roundTwoDecimals(String.valueOf(totAmnt))).append("\n");
                } else if (_count.length() == 1 && totalamt.length() == 4) {
                    textData.append("Total          ").append(count).append("      ").append(roundTwoDecimals(String.valueOf(totAmnt))).append("\n");
                }
                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                mService.write(nameFontformat);
                mService.sendMessage(textData.toString(), "GBK");

                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                mService.write(nameFontformat);
                //mService.sendMessage("CGST " + cgstPerStr + " % : " + roundTwoDecimals(totCGSTAmnt), "GBK");
                //mService.sendMessage("SGST " + sgstPerStr + " % : " + roundTwoDecimals(totSGSTAmnt), "GBK");

                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x8 | arrayOfByte1[2]));
                mService.write(nameFontformat);
                mService.sendMessage(line_str, "GBK");

                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x16 | arrayOfByte1[2]));
                mService.write(nameFontformat);
                mService.sendMessage("NET AMNT              " + totalamt, "GBK");


                byte[] left2 = {27, 97, 0};
                mService.write(left2);
                nameFontformat[2] = arrayOfByte1[2];
                mService.write(nameFontformat);
                mService.sendMessage("    www.paitsystems.com", "GBK");

                mService.write(PrinterCommands.ESC_ENTER);
                String space_str = "                        ";
                mService.sendMessage(space_str, "GBK");

                Log.d("Log", textData.toString());
            } catch (Exception e) {
                e.printStackTrace();
                str = "Printer May Not Be Connected ";
                return str;
            }
            return "Order Received By Kitchen 3";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("Print3", result);
            pd.dismiss();
        }
    }

    private void setBillNo(){
        billMastList = db.getBillNo("","");
        auto_billNo.setAdapter(new ReturnMemoBillNoAdapter(getApplicationContext(),R.layout.custom_autocomplete_list_item,billMastList));
        auto_billNo.setThreshold(0);
    }

    private void setBillDet(){
        billDetList.clear();
        billDetList = db.getBillDet(billMaster.getId());
        auto_barcode.setAdapter(new ReturnMemoBarcodeAdapter(getApplicationContext(),R.layout.custom_autocomplete_list_item,billDetList));
        auto_barcode.setThreshold(0);
    }

    private void saveReturnMemo(){
        float netbillamt,returnqty,returnamt,dis,tax,grossamt,netamt,BalRedeem,
                BillKnockAmt,RevGainPts,RevRdmPts,CGSTAMT,SGSTAMT,IGSTAMT;

        int auto,id,empname,createby,modifiedby,deletedby,maxno,branchID,OpenBy, validate = -1;

        String machineName,counterNo,rMemoNo,billNo,custcode,remark,createdt,modifieddt,
                deleteddt,financialyr,inwrds,redeemst,
                Status,ActualCreateDate,createtime,type,redeemtype,SpecialRight,msreplclm,OpenSt,
                OpenDate,OpenReason,ReturnReason,GatePassNo,CNType,CreatedFrom,IGSTAPP;

        float qty,rate,amt,disper,disamt,vatper,vatamt,MRP,billdisper,billdisamt,detGSTPER=0,
                detCGSTAMT=0,detSGSTAMT=0,detCGSTPER=0,detSGSTPER=0,detCESSPER=0,detCESSAMT=0,
                detIGSTAMT=0,TaxableAmt = 0;

        int detId, itemcode, returnID, autoID, empid, mastid, BillDetAuto, dtlid;

        String detRMemoNo,barcode,detFinancialyr,detCounterNo,nonbarst,itemName;

        financialyr = detFinancialyr = new Constant(getApplicationContext()).getFinYr();

        auto = db.getMaxRMAuto();
        id = db.getMaxRMMastId(financialyr);
        if(rdo_retMemo.isChecked()){
            rMemoNo = "RM"+financialyr+"/"+db.getCompIni()+"/"+id;
            detRMemoNo = rMemoNo;
            type = "R";
            BalRedeem = totAmnt;
        }else{
            rMemoNo = "CB"+financialyr+"/"+db.getCompIni()+"/"+id;
            detRMemoNo = rMemoNo;
            type = "C";
            BalRedeem = 0;
        }
        machineName = new Constant(getApplicationContext()).getIMEINo1();
        counterNo = "1";
        detCounterNo = counterNo;
        billNo = billMaster.getBillNo();
        custcode = billMaster.getCustID();
        netbillamt = stringToFloat(billMaster.getTotalAmt());
        returnqty = totQty;
        returnamt = totAmnt;
        dis = totDiscAmnt;
        tax = 0;
        grossamt = netbillamt;
        netamt = netbillamt;
        remark = "";
        empname = 1;
        createby = 1;
        createdt = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        modifiedby = 0;
        modifieddt = "";
        deletedby = 0;
        deleteddt = "";
        maxno = 0;
        branchID = 1;
        inwrds = "";
        redeemst = "N";
        Status = "A";
        ActualCreateDate = createdt;
        createtime = new Constant(getApplicationContext()).getTime();
        redeemtype = "CN";
        SpecialRight = "N";
        msreplclm = "";
        BillKnockAmt = returnamt;
        OpenSt = "Open";
        OpenBy = 0;
        OpenDate = "";
        OpenReason = "";
        ReturnReason = "";
        GatePassNo = "";
        CNType = "G";
        CreatedFrom = "C";
        RevGainPts = 0;
        RevRdmPts = 0;
        CGSTAMT = totCGSTAmnt;
        SGSTAMT = totSGSTAmnt;
        IGSTAPP = "N";
        IGSTAMT = 0;

        if(retMemoList!=null && retMemoList.size()!=0) {
            ReturnMemoMasterClass master = new ReturnMemoMasterClass();
            master.setAuto(auto);
            master.setId(id);
            master.setMachineName(machineName);
            master.setCounterNo(counterNo);
            master.setrMemoNo(rMemoNo);
            master.setBillNo(billNo);
            master.setCustcode(custcode);
            master.setNetbillamt(roundDecimals(netbillamt));
            master.setReturnqty(roundDecimals(returnqty));
            master.setReturnamt(roundDecimals(returnamt));
            master.setDis(roundDecimals(dis));
            master.setTax(roundDecimals(tax));
            master.setNetamt(roundDecimals(netamt));
            master.setGrossamt(roundDecimals(grossamt));
            master.setRemark(remark);
            master.setEmpname(empname);
            master.setCreateby(createby);
            master.setCreatedt(createdt);
            master.setModifiedby(modifiedby);
            master.setModifieddt(modifieddt);
            master.setDeletedby(deletedby);
            master.setDeleteddt(deleteddt);
            master.setFinancialyr(financialyr);
            master.setMaxno(maxno);
            master.setBranchID(branchID);
            master.setInwrds(inwrds);
            master.setRedeemst(redeemst);
            master.setStatus(Status);
            master.setActualCreateDate(ActualCreateDate);
            master.setCreatetime(createtime);
            master.setType(type);
            master.setRedeemtype(redeemtype);
            master.setSpecialRight(SpecialRight);
            master.setBalRedeem(roundDecimals(BalRedeem));
            master.setMsreplclm(msreplclm);
            master.setBillKnockAmt(roundDecimals(BillKnockAmt));
            master.setOpenSt(OpenSt);
            master.setOpenBy(OpenBy);
            master.setOpenDate(OpenDate);
            master.setOpenReason(OpenReason);
            master.setReturnReason(ReturnReason);
            master.setGatePassNo(GatePassNo);
            master.setCNType(CNType);
            master.setCreatedFrom(CreatedFrom);
            master.setRevGainPts(roundDecimals(RevGainPts));
            master.setRevRdmPts(roundDecimals(RevRdmPts));
            master.setCGSTAMT(roundDecimals(CGSTAMT));
            master.setSGSTAMT(roundDecimals(SGSTAMT));
            master.setIGSTAPP(IGSTAPP);
            master.setIGSTAPP(roundDecimals(IGSTAMT));
            db.saveReturnMemoMaster(master);

            for (BillDetailClass det : retMemoList) {
                detId = db.getMaxRMDetAuto();
                mastid = db.getMaxRMDetId(detId);
                itemcode = det.getItemId();
                barcode = det.getBarcode();
                qty = stringToInt(det.getQty());
                rate = stringToFloat(det.getRate());
                amt = stringToFloat(det.getTotal());
                returnID = mastid;
                branchID = 1;
                detFinancialyr = financialyr;
                counterNo = "1";
                autoID = 0;
                disper = stringToFloat(det.getDisper());
                disamt = stringToFloat(det.getDisamt());
                vatper = 0;
                vatamt = 0;
                nonbarst = "N";
                itemName = det.getFatherSKU();
                empid = 1;
                MRP = stringToFloat(det.getMRP());
                mastid = det.getAuto();
                BillDetAuto = det.getId();
                dtlid = mastid;
                billdisper = stringToFloat(det.getBilldisper());
                billdisamt = stringToFloat(det.getBilldisamt());

                String str = db.getGstGroupFromProdId(itemcode);
                String arr[] = str.split("-");
                String gstGroup = arr[0];
                String gstType = arr[1];

                Cursor cursor = db.getGSTPer(gstGroup);
                cursor.moveToFirst();
                float gstPer = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_GSTPer));
                float cgstPer = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_CGSTPer));
                float sgstPer = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_SGSTPer));
                float cgstShare = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_CGSTShare));
                float sgstShare = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_SGSTShare));
                cursor.close();

                if (gstType.equals("I")) {
                    float accValue = ((gstPer * 100) / (gstPer + 100));
                    float gstAmnt = (rate * accValue) / 100;
                    float taxableRate = rate - gstAmnt;
                    float total = (taxableRate * qty);
                    float billdiscPer = billdisper;
                    float billDiscAmnt = (total * billdiscPer) / 100;
                    float disctedTotal = total - billDiscAmnt;
                    float totalGST = (disctedTotal * gstPer) / 100;
                    float cgstAmt = (disctedTotal * cgstPer) / 100;
                    float sgstAmt = (disctedTotal * sgstPer) / 100;
                    float netAmt = disctedTotal + cgstAmt + sgstAmt;
                    totAmnt = totAmnt + netAmt;
                    detGSTPER = gstPer;
                    detCGSTAMT = cgstAmt;
                    detSGSTAMT = sgstAmt;
                    detCGSTPER = cgstPer;
                    detSGSTPER = sgstPer;
                    detCESSPER = 0;
                    detCESSAMT = 0;
                    detIGSTAMT = 0;
                    TaxableAmt = taxableRate;
                    totCGSTAmnt = totCGSTAmnt + cgstAmt;
                    totSGSTAmnt = totSGSTAmnt + sgstAmt;
                } else if (gstType.equals("E")) {
                    float taxableRate = rate;
                    float total = (taxableRate * qty);
                    float billdiscPer = disper;
                    float billDiscAmnt = (total * billdiscPer) / 100;
                    float disctedTotal = total - billDiscAmnt;
                    float totalGST = (disctedTotal * gstPer) / 100;
                    float cgstAmt = (disctedTotal * cgstPer) / 100;
                    float sgstAmt = (disctedTotal * sgstPer) / 100;
                    float netAmt = disctedTotal + cgstAmt + sgstAmt;
                    totAmnt = totAmnt + netAmt;
                    detGSTPER = gstPer;
                    detCGSTAMT = cgstAmt;
                    detSGSTAMT = sgstAmt;
                    detCGSTPER = cgstPer;
                    detSGSTPER = sgstPer;
                    detCESSPER = 0;
                    detCESSAMT = 0;
                    detIGSTAMT = 0;
                    TaxableAmt = taxableRate;
                    totCGSTAmnt = totCGSTAmnt + cgstAmt;
                    totSGSTAmnt = totSGSTAmnt + sgstAmt;
                }
                ReturnMemoDetailClass retDet = new ReturnMemoDetailClass();
                retDet.setId(id);
                retDet.setMastid(mastid);
                retDet.setrMemoNo(detRMemoNo);
                retDet.setItemcode(itemcode);
                retDet.setBarcode(barcode);
                retDet.setQty(String.valueOf(qty));
                retDet.setRate(String.valueOf(rate));
                retDet.setAmt(roundTwoDecimals(amt));
                retDet.setReturnID(returnID);
                retDet.setBranchID(branchID);
                retDet.setFinancialyr(detFinancialyr);
                retDet.setCounterNo(detCounterNo);
                retDet.setAutoID(autoID);
                retDet.setDisper(String.valueOf(disper));
                retDet.setDisamt(String.valueOf(disamt));
                retDet.setVatper(String.valueOf(vatper));
                retDet.setVatamt(String.valueOf(vatamt));
                retDet.setNonbarst(nonbarst);
                retDet.setItemName(itemName);
                retDet.setEmpid(empid);
                retDet.setMRP(roundTwoDecimals(MRP));
                retDet.setMastid(mastid);
                retDet.setBillDetAuto(BillDetAuto);
                retDet.setDtlid(dtlid);
                retDet.setBilldisper(roundTwoDecimals(billdisper));
                retDet.setBilldisamt(roundTwoDecimals(billdisamt));

                retDet.setGSTPER(roundTwoDecimals(detGSTPER));
                retDet.setCGSTAMT(roundTwoDecimals(detCGSTAMT));
                retDet.setSGSTAMT(roundTwoDecimals(detSGSTAMT));
                retDet.setCGSTPER(roundTwoDecimals(detCGSTPER));
                retDet.setSGSTPER(roundTwoDecimals(detSGSTPER));
                retDet.setCESSPER(roundTwoDecimals(detCESSPER));
                retDet.setCESSAMT(roundTwoDecimals(detCESSAMT));
                retDet.setIGSTAMT(roundTwoDecimals(detIGSTAMT));
                retDet.setTaxableAmt(roundTwoDecimals(TaxableAmt));

                db.saveReturnMemoDetail(retDet);

                db.updateRetQty(String.valueOf(qty), det);
            }
            db.updateTRetQty(String.valueOf(totQty), billMaster);
            showDia(2);
        }else{
            toast.setText("Please Select Atleast One Item");
            toast.show();
        }
    }

    private int stringToInt(String value){
        return Integer.parseInt(value);
    }

    private float stringToFloat(String value){
        return Float.parseFloat(value);
    }

    private String roundTwoDecimals(String d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return twoDForm.format(Double.parseDouble(d));
    }

    private String roundTwoDecimals(float d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return twoDForm.format(Double.parseDouble(String.valueOf(d)));
    }

    private String roundDecimals(String d) {
        DecimalFormat twoDForm = new DecimalFormat("#");
        return twoDForm.format(Double.parseDouble(d));
    }

    private String roundDecimals(float d) {
        DecimalFormat twoDForm = new DecimalFormat("#");
        return twoDForm.format(Double.parseDouble(String.valueOf(d)));
    }

    private void init(){
        constant = new Constant(ReturnMemoActivity.this);
        db = new DBHandler(getApplicationContext());
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        rdo_retMemo = findViewById(R.id.rdo_retMemo);
        rdo_cashback = findViewById(R.id.rdo_cashback);
        tv_billAmnt = findViewById(R.id.tv_billAmnt);
        tv_paidAmnt = findViewById(R.id.tv_paidAmnt);
        tv_totQty = findViewById(R.id.total_qty);
        tv_totAmnt = findViewById(R.id.total_amount);
        tv_custName = findViewById(R.id.tv_custName);
        tv_mobNo = findViewById(R.id.tv_mobNo);
        btn_save = findViewById(R.id.btn_save);
        auto_billNo = findViewById(R.id.auto_billNo);
        auto_barcode = findViewById(R.id.auto_barcode);
        rv_returnMemo = findViewById(R.id.rv_returnMemo);

        rdo_cashback.setOnClickListener(this);
        rdo_retMemo.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        billMastList = new ArrayList<>();
        billDetList = new ArrayList<>();
        retMemoList = new ArrayList<>();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        DividerItemDecoration itemDecor = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        rv_returnMemo.addItemDecoration(itemDecor);
        rv_returnMemo.setLayoutManager(mLayoutManager);
        rv_returnMemo.setItemAnimator(new DefaultItemAnimator());
    }

    private void showDia(int a){
        AlertDialog.Builder builder = new AlertDialog.Builder(ReturnMemoActivity.this);
        builder.setCancelable(false);
        if (a == 0) {
            builder.setMessage("Do You Want To Exit App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else if (a == 1) {
            builder.setMessage("Do You Want To Save?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    saveReturnMemo();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else if (a == 2) {
            builder.setMessage("Return Memo Saved Sucessfully");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    clearFields();
                }
            });
        }
        builder.create().show();
    }

    private void clearFields(){
        auto_billNo.setText(null);
        auto_billNo.requestFocus();
        totQty = 0;
        totAmnt = 0;
        totSGSTAmnt = 0;
        totCGSTAmnt = 0;
        totDiscAmnt = 0;
        auto_barcode.setText(null);
        retMemoList.clear();
        rv_returnMemo.setAdapter(null);
        tv_totQty.setText("0");
        tv_totAmnt.setText("0");
        tv_custName.setText("");
        tv_billAmnt.setText("0");
        tv_paidAmnt.setText("0");
    }

    private void connectBT(){
        try {
            if(mService!=null) {
                if (mService.isBTopen()) {
                    UserProfileClass user = new Constant(getApplicationContext()).getPref();
                    if (user.getMacAddress() != null) {
                        Constant.showLog(user.getMacAddress());
                        con_dev = mService.getDevByMac(user.getMacAddress());
                        mService.connect(con_dev);
                    } else {
                        toast.setText("Set Default Printer First");
                        toast.show();
                    }
                }else{
                    toast.setText("Bluetooth Is Off");
                    toast.show();
                }
            }else{
                toast.setText("Something Went Wrong");
                toast.show();
            }
        }catch (Exception e){
            e.printStackTrace();
            toast.setText("Set Default Printer First");
            toast.show();
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
                    toast.setText("Device connection was lost");
                    toast.show();
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:
                    toast.setText("Unable to Connect Bluetooth Printer");
                    toast.show();
                    break;
            }
        }
    };
}
