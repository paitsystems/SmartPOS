package com.pait.smartpos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.pait.smartpos.interfaces.RecyclerViewToActivityInterface;
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
import java.util.Objects;

import static android.support.v7.widget.RecyclerView.HORIZONTAL;

public class ReturnMemoActivity extends AppCompatActivity implements View.OnClickListener,
        CashMemoRecyclerItemTouchHelper.RecyclerItemTouchHelperListener,ReturnMemoRecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

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
    private String memoNo, cgstPerStr, sgstPerStr, compName = "PA", compAddress="PUNE", compPhone="02024339957",
            compInit="PA", compGSTNo = "27ABCD1234EFGH2", custName = "CashSale-0";
    private int gstApplicable = 0;

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
                if(stringToInt(billMaster.getTotalQty())!=stringToInt(billMaster.getTRetQty())) {
                    System.out.println("Position " + j + "-" + billMaster.getBillNo());
                    String custName = db.getCustName(billMaster.getCustID());
                    tv_custName.setText(custName);
                    tv_billAmnt.setText(billMaster.getTotalAmt());
                    tv_paidAmnt.setText(billMaster.getPaidAmt());
                    setBillDet();
                }else{
                    toast.setText("All Items Are Returned");
                    toast.show();
                    clearFields();
                }
            }
        });

        auto_barcode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int j, long l) {
                billDetail = (BillDetailClass) adapterView.getItemAtPosition(j);
                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(auto_barcode.getWindowToken(),0);
                auto_barcode.setText(null);
                if(stringToInt(billDetail.getQty())!= stringToInt(billDetail.getRetQty())) {
                    if(!retMemoList.contains(billDetail)) {

                        totQty = totQty + stringToInt(billDetail.getQty());
                        totAmnt = totAmnt + stringToFloat(billDetail.getTotal());
                        totAmnt = totAmnt - stringToFloat(billDetail.getBilldisamt());
                        totAmnt = totAmnt + stringToFloat(billDetail.getCGSTAMT()) +
                                stringToFloat(billDetail.getSGSTAMT());
                        totDiscAmnt = totDiscAmnt + stringToFloat(billDetail.getBilldisamt());
                        totCGSTAmnt = totCGSTAmnt + stringToFloat(billDetail.getCGSTAMT());
                        totSGSTAmnt = totSGSTAmnt + stringToFloat(billDetail.getSGSTAMT());
                        Constant.showLog(billDetail.getCGSTAMT()+"-"+billDetail.getSGSTAMT()+"-"+
                        totCGSTAmnt+"-"+totSGSTAmnt);
                        tv_totQty.setText(roundTwoDecimals(totQty));
                        tv_totAmnt.setText(roundTwoDecimals(totAmnt));

                        System.out.println("Position " + j + "-" + billDetail.getFatherSKU());
                        retMemoList.add(billDetail);
                        if (retMemoList.size() == 1) {
                            adapter = new ReturnMemoRecyclerAdapter(getApplicationContext(), retMemoList);
                            rv_returnMemo.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        rv_returnMemo.scrollToPosition(retMemoList.size() - 1);
                    }else{
                        toast.setText("This Item Is Already Added In List");
                        toast.show();
                    }
                }else{
                    toast.setText("This Item Is Already Returned");
                    toast.show();
                }
            }
        });

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ReturnMemoRecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv_returnMemo);

        Cursor res = db.getCompanyDetail();
        if (res.moveToFirst()) {
            do {
                compName = res.getString(res.getColumnIndex(DBHandler.CPM_CompanyName));
                compAddress = res.getString(res.getColumnIndex(DBHandler.CPM_Address));
                compPhone = res.getString(res.getColumnIndex(DBHandler.CPM_Phone));
                compInit = res.getString(res.getColumnIndex(DBHandler.CPM_Initials));
                compGSTNo = res.getString(res.getColumnIndex(DBHandler.CPM_GSTNo));
            } while (res.moveToNext());
        }
        res.close();

        if(compGSTNo.length()==15) {
            gstApplicable = 1;
        }
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

    @Override
    protected void onDestroy() {
        if (mService != null) {
            mService.stop();
        }
        super.onDestroy();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ReturnMemoRecyclerAdapter.ProductViewHolder) {
            final int[] flag = {0};
            String name = retMemoList.get(viewHolder.getAdapterPosition()).getFatherSKU();
            final BillDetailClass det = retMemoList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();
            adapter.removeItem(viewHolder.getAdapterPosition());
            int qty = stringToInt(det.getQty());
            float rate = stringToFloat(det.getRate());
            float billdisper = stringToFloat(det.getBilldisper());
            float detCGSTAMT=0,detSGSTAMT=0,taxableRate=0;
            if(qty>1) {
                totQty = totQty - stringToInt(det.getQty());
                totAmnt = totAmnt - stringToFloat(det.getTotal());
                totAmnt = totAmnt + stringToFloat(det.getBilldisamt());
                totAmnt = totAmnt - stringToFloat(det.getCGSTAMT()) - stringToFloat(det.getSGSTAMT());
                totDiscAmnt = totDiscAmnt - stringToFloat(det.getBilldisamt());
                totCGSTAmnt = totCGSTAmnt - stringToFloat(det.getCGSTAMT());
                totSGSTAmnt = totSGSTAmnt - stringToFloat(det.getSGSTAMT());
                tv_totQty.setText(roundTwoDecimals(totQty));
                tv_totAmnt.setText(roundTwoDecimals(totAmnt));

                qty = qty - 1;
                String str = db.getGstGroupFromProdId(det.getItemId());
                String arr[] = str.split("-");
                String gstGroup = arr[0];
                String gstType = arr[1];
                Cursor cursor = db.getGSTPer(gstGroup,stringToFloat(det.getRate()));
                cursor.moveToFirst();
                float gstPer = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_GSTPer));
                float cgstPer = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_CGSTPer));
                float sgstPer = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_SGSTPer));
                cursor.close();

                if (gstType.equals("I")) {
                    float accValue = ((gstPer * 100) / (gstPer + 100));
                    float gstAmnt = (rate * accValue) / 100;
                    taxableRate  = rate - gstAmnt;
                    float total = (taxableRate * qty);
                    float billdiscPer = billdisper;
                    float billDiscAmnt = (total * billdiscPer) / 100;
                    float disctedTotal = total - billDiscAmnt;
                    float cgstAmt = (disctedTotal * cgstPer) / 100;
                    float sgstAmt = (disctedTotal * sgstPer) / 100;
                    float netAmt = disctedTotal + cgstAmt + sgstAmt;
                    detCGSTAMT = cgstAmt;
                    detSGSTAMT = sgstAmt;
                } else if (gstType.equals("E")) {
                    taxableRate = rate;
                    float total = (taxableRate * qty);
                    float billdiscPer = billdisper;
                    float billDiscAmnt = (total * billdiscPer) / 100;
                    float disctedTotal = total - billDiscAmnt;
                    float cgstAmt = (disctedTotal * cgstPer) / 100;
                    float sgstAmt = (disctedTotal * sgstPer) / 100;
                    float netAmt = disctedTotal + cgstAmt + sgstAmt;
                    detCGSTAMT = cgstAmt;
                    detSGSTAMT = sgstAmt;
                }
                //BillDetailClass _det = new BillDetailClass();
                det.setFatherSKU(det.getFatherSKU());
                det.setQty(String.valueOf(qty));
                det.setItemId(det.getItemId());
                det.setBarcode(det.getBarcode());
                det.setRate(det.getRate());
                det.setTotal(String.valueOf(qty*rate));
                det.setDisper(det.getDisper());
                det.setDisamt(det.getDisamt());
                det.setMRP(det.getMRP());
                det.setBilldisper(det.getBilldisper());
                det.setBilldisamt(det.getBilldisamt());
                det.setCGSTAMT(roundTwoDecimals(detCGSTAMT));
                det.setSGSTAMT(roundTwoDecimals(detSGSTAMT));
                det.setTaxableAmt(roundTwoDecimals(taxableRate));
                det.setAuto(det.getAuto());
                det.setId(det.getId());
                det.setBillID(det.getBillID());
                det.setRetQty(det.getRetQty());
                adapter.restoreItem(det, deletedIndex);
                totQty = totQty + stringToInt(det.getQty());
                totAmnt = totAmnt + stringToFloat(det.getTotal());
                totAmnt = totAmnt - stringToFloat(det.getBilldisamt());
                totAmnt = totAmnt + stringToFloat(det.getCGSTAMT()) +
                        stringToFloat(det.getSGSTAMT());
                totDiscAmnt = totDiscAmnt + stringToFloat(det.getBilldisamt());
                totCGSTAmnt = totCGSTAmnt + stringToFloat(det.getCGSTAMT());
                totSGSTAmnt = totSGSTAmnt + stringToFloat(det.getSGSTAMT());
                tv_totQty.setText(roundTwoDecimals(totQty));
                tv_totAmnt.setText(roundTwoDecimals(totAmnt));

            }else {
                totQty = totQty - stringToInt(det.getQty());
                totAmnt = totAmnt - stringToFloat(det.getTotal());
                totAmnt = totAmnt + stringToFloat(det.getBilldisamt());
                totAmnt = totAmnt - stringToFloat(det.getCGSTAMT()) - stringToFloat(det.getSGSTAMT());
                totDiscAmnt = totDiscAmnt - stringToFloat(det.getBilldisamt());
                totCGSTAmnt = totCGSTAmnt - stringToFloat(det.getCGSTAMT());
                totSGSTAmnt = totSGSTAmnt - stringToFloat(det.getSGSTAMT());
                tv_totQty.setText(roundTwoDecimals(totQty));
                tv_totAmnt.setText(roundTwoDecimals(totAmnt));
            }

            /*Snackbar snackbar = Snackbar.make(rdo_cashback, name + " removed from list!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    flag[0] = 1;
                    adapter.restoreItem(det, deletedIndex);
                    totQty = totQty + stringToInt(billDetail.getQty());
                    totAmnt = totAmnt + stringToFloat(billDetail.getTotal());
                    totAmnt = totAmnt - stringToFloat(billDetail.getBilldisamt());
                    totAmnt = totAmnt + stringToFloat(billDetail.getCGSTAMT()) +
                            stringToFloat(billDetail.getSGSTAMT());
                    totDiscAmnt = totDiscAmnt + stringToFloat(billDetail.getBilldisamt());
                    totCGSTAmnt = totCGSTAmnt + stringToFloat(billDetail.getCGSTAMT());
                    totSGSTAmnt = totSGSTAmnt + stringToFloat(billDetail.getSGSTAMT());
                    tv_totQty.setText(roundTwoDecimals(totQty));
                    tv_totAmnt.setText(roundTwoDecimals(totAmnt));
                }
            });
            snackbar.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {

                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    if(flag[0] !=1) {
                        Constant.showLog("3-deletedIndex-"+deletedIndex+"-cartList-"+retMemoList.size());
                    }
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();*/
        }
    }

    @Override
    public void startNewActivty(RecyclerView.ViewHolder viewHolder, int direction, int position) {

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

                mService.sendMessage(compName.toUpperCase(), "GBK");

                nameFontformat[2] = arrayOfByte1[2];
                mService.write(nameFontformat);

                mService.sendMessage(compAddress, "GBK");
                mService.sendMessage(compPhone, "GBK");
                if(gstApplicable == 1) {
                    mService.sendMessage("GSTIN : " + compGSTNo, "GBK");
                }
                mService.sendMessage("Credit Note", "GBK");

                byte[] left = {27, 97, 0};
                mService.write(PrinterCommands.ESC_ALIGN_LEFT);

                String date = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                String time = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime());

                String space_str13 = "             ";
                mService.sendMessage(date + space_str13 + time, "GBK");
                mService.sendMessage("Memo No : " + memoNo, "GBK");
                mService.sendMessage("BillNo : " + billMaster.getBillNo(), "GBK");
                String str1 = tv_custName.getText().toString();
                String arr[] = str1.split("-");
                if(arr.length>1){
                    mService.sendMessage("Customer Name : " + arr[0], "GBK");
                    mService.sendMessage("Mobile No     : " + arr[1], "GBK");
                }else {
                    mService.sendMessage("Customer Name : " + str1, "GBK");
                    mService.sendMessage("Mobile No     : " , "GBK");
                }
                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x8 | arrayOfByte1[2]));
                mService.write(nameFontformat);
                String line_str = "--------------------------------";
                mService.sendMessage(line_str, "GBK");
                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                mService.write(nameFontformat);

                String _heading = String.format("%1$-10s %2$3s %3$8s %4$8s","Item", "Qty", "Rate", "Amnt");
                Constant.showLog(_heading);
                mService.sendMessage(_heading,"GBK");
                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x8 | arrayOfByte1[2]));
                mService.write(nameFontformat);
                mService.sendMessage(line_str, "GBK");
                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                mService.write(nameFontformat);

                int count = 0, totQty = 0;
                float _totAmnt = 0;

                StringBuilder data = new StringBuilder();
                for (int i = 0; i < retMemoList.size(); i++) {
                    String _itemData;
                    BillDetailClass cart = retMemoList.get(i);
                    StringBuilder item = new StringBuilder(cart.getFatherSKU());
                    String item1 = cart.getFatherSKU();
                    int flag = 0;
                    String qty = cart.getQty();
                    totQty = Integer.parseInt(qty);
                    _totAmnt = _totAmnt + stringToFloat(cart.getTotal());
                    if (item.length() >= 10) {
                        item = new StringBuilder(item.substring(0, 9));
                        item.append(" ");
                        flag = 1;
                    } else {
                        int size = 9 - item.length();
                        for (int j = 0; j < size; j++) {
                            item.append(" ");
                        }
                        item.append(" ");
                    }
                    if (flag != 1) {
                        _itemData = String.format("%1$-10s %2$3s %3$8s %4$8s",item, cart.getQty(),
                                cart.getRate(),cart.getTotal());
                        mService.sendMessage(_itemData,"GBK");
                    } else {
                        _itemData = String.format("%1$-10s %2$3s %3$8s %4$8s",item, cart.getQty(),
                                cart.getRate(),cart.getTotal());
                        mService.sendMessage(_itemData,"GBK");
                        String q = item1.substring(9, item1.length());
                        _itemData = String.format("%1$-10s %2$1s %3$1s %4$1s",q,"","","");
                        mService.sendMessage(_itemData,"GBK");
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

                String  totalamt = roundDecimals(tv_totAmnt.getText().toString());
                String _totalData = String.format("%1$-10s %2$3s %3$8s %4$8s","Total", _count,"",roundTwoDecimals(String.valueOf(_totAmnt)));
                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                mService.write(nameFontformat);
                mService.sendMessage(_totalData, "GBK");

                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                mService.write(nameFontformat);
                if(gstApplicable == 1) {
                    mService.sendMessage("CGST " + cgstPerStr + " % : " + roundTwoDecimals(totCGSTAmnt), "GBK");
                    mService.sendMessage("SGST " + sgstPerStr + " % : " + roundTwoDecimals(totSGSTAmnt), "GBK");
                }
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
                mService.sendMessage("Contact No : 020 24339954", "GBK");

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
            clearFields();
        }
    }

    private void setBillNo(){
        billMastList = db.getBillNo("","");
        auto_billNo.setAdapter(new ReturnMemoBillNoAdapter(getApplicationContext(),R.layout.adapter_item,billMastList));
        auto_billNo.setThreshold(0);
    }

    private void setBillDet(){
        billDetList.clear();
        billDetList = db.getBillDet(billMaster.getId());
        auto_barcode.setAdapter(new ReturnMemoBarcodeAdapter(getApplicationContext(),R.layout.adapter_item,billDetList));
        auto_barcode.setThreshold(0);
    }

    private void saveReturnMemo(){
        totAmnt = 0;
        totDiscAmnt = 0;
        totCGSTAmnt = 0;
        totSGSTAmnt = 0;

        float netbillamt,returnqty,returnamt,dis,tax,grossamt = 0,netamt,BalRedeem,
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
            memoNo = rMemoNo = "RM"+financialyr+"/"+db.getCompIni()+"/"+id;
            detRMemoNo = rMemoNo;
            type = "R";
        }else{
            memoNo = rMemoNo = "CB"+financialyr+"/"+db.getCompIni()+"/"+id;
            detRMemoNo = rMemoNo;
            type = "C";
        }
        machineName = new Constant(getApplicationContext()).getIMEINo1();
        counterNo = "1";
        detCounterNo = counterNo;
        billNo = billMaster.getBillNo();
        custcode = billMaster.getCustID();
        netbillamt = stringToFloat(billMaster.getNetamt());
        returnqty = totQty;
        dis = totDiscAmnt;
        tax = 0;
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
        IGSTAPP = "N";
        IGSTAMT = 0;

        if(retMemoList!=null && retMemoList.size()!=0) {
            for (BillDetailClass det : retMemoList) {
                detId = db.getMaxRMDetAuto();
                mastid = db.getMaxRMDetId(detId);
                itemcode = det.getItemId();
                barcode = det.getBarcode();
                qty = stringToInt(det.getQty());
                rate = stringToFloat(det.getRate());
                grossamt = grossamt + (rate*qty);
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
                BillDetAuto = billDetail.getAuto();
                dtlid = billDetail.getId();
                billdisper = stringToFloat(det.getBilldisper());
                billdisamt = stringToFloat(det.getBilldisamt());

                String str = db.getGstGroupFromProdId(itemcode);
                String arr[] = str.split("-");
                String gstGroup = arr[0];
                String gstType = arr[1];

                float gstPer = 0, cgstPer = 0, sgstPer = 0,cgstShare = 0, sgstShare = 0;
                if(gstApplicable == 1) {
                    Cursor cursor = db.getGSTPer(gstGroup, rate);
                    cursor.moveToFirst();
                    gstPer = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_GSTPer));
                    cgstPer = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_CGSTPer));
                    sgstPer = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_SGSTPer));
                    cgstShare = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_CGSTShare));
                    sgstShare = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_SGSTShare));
                    cursor.close();
                }

                if (gstType.equals("I")) {
                    float accValue = ((gstPer * 100) / (gstPer + 100));
                    float gstAmnt = (rate * accValue) / 100;
                    //float taxableRate = rate - gstAmnt;
                    float total = (rate * qty);
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
                    TaxableAmt = disctedTotal;
                    cgstPerStr = roundTwoDecimals(cgstPer);
                    sgstPerStr = roundTwoDecimals(sgstPer);
                    totCGSTAmnt = totCGSTAmnt + cgstAmt;
                    totSGSTAmnt = totSGSTAmnt + sgstAmt;
                } else if (gstType.equals("E")) {
                    float taxableRate = rate;
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
                    TaxableAmt = disctedTotal;
                    cgstPerStr = roundTwoDecimals(cgstPer);
                    sgstPerStr = roundTwoDecimals(sgstPer);
                    totCGSTAmnt = totCGSTAmnt + cgstAmt;
                    totSGSTAmnt = totSGSTAmnt + sgstAmt;
                }
                ReturnMemoDetailClass retDet = new ReturnMemoDetailClass();
                retDet.setId(detId);
                retDet.setMastid(auto);
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
                db.updateInwardProductQty(itemcode,stringToInt(det.getQty()));
            }
            returnamt = totAmnt;
            netamt = returnamt;
            BillKnockAmt = 0;
            CGSTAMT = totCGSTAmnt;
            SGSTAMT = totSGSTAmnt;
            if(type.equals("R")){
                BalRedeem = totAmnt;
            }else{
                BalRedeem = 0;
            }
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
            master.setCGSTAMT(roundTwoDecimals(CGSTAMT));
            master.setSGSTAMT(roundTwoDecimals(SGSTAMT));
            master.setIGSTAPP(IGSTAPP);
            master.setIGSTAPP(roundTwoDecimals(IGSTAMT));
            db.saveReturnMemoMaster(master);
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
            builder.setPositiveButton("Print", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    new CashMemoPrint().execute();
                }
            });
            builder.setNegativeButton("New Return Memo", new DialogInterface.OnClickListener() {
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
