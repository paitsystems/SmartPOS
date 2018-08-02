package com.pait.smartpos;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hoin.btsdk.BluetoothService;
import com.pait.smartpos.adpaters.AddToCartRecyclerAdapter;
import com.pait.smartpos.adpaters.AllProductDialogAdapter;
import com.pait.smartpos.adpaters.AllRateAdapter;
import com.pait.smartpos.adpaters.AllRateDialogAdapter;
import com.pait.smartpos.adpaters.MasterUpdationRecyclerAdapter;
import com.pait.smartpos.adpaters.ProductListRecyclerAdapter;
import com.pait.smartpos.adpaters.ProductRateRecyclerAdapter;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.constant.PrinterCommands;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.interfaces.RecyclerViewToActivityInterface;
import com.pait.smartpos.model.AddToCartClass;
import com.pait.smartpos.model.BillDetailClass;
import com.pait.smartpos.model.BillMasterClass;
import com.pait.smartpos.model.MasterUpdationClass;
import com.pait.smartpos.model.ProductClass;
import com.pait.smartpos.model.RateMasterClass;
import com.pait.smartpos.model.UserProfileClass;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.widget.AdapterView.OnItemClickListener;

public class CashMemoActivity extends AppCompatActivity implements View.OnClickListener,
        RecyclerViewToActivityInterface,OnItemClickListener,CashMemoRecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private RecyclerView rv_Prod, rv_Price, rv_Order;
    private TextView tv_prodAll, tv_priceAll, tv_remove, tv_add, tv_totalQty, tv_totalAmnt,
            tv_retAmnt, tv_netAmnt, tv_totcgstAmnt, tv_sgstAmnt;
    private EditText ed_Qty, ed_rate, ed_cash, ed_cardNo, ed_custName, ed_custMobNo,
            ed_remark, ed_discPer, ed_discAmnt, ed_returnMemo, ed_tenderAmnt, ed_chequeNo;
    private Button btn_Add, btn_Save, btn_Cancel;
    private Spinner sp_paymentType, sp_returnMemo;
    private AutoCompleteTextView auto_CustName;
    private Constant constant;
    private DBHandler db;
    private Toast toast;
    private ArrayList<ProductClass> prodList;
    private ArrayList<RateMasterClass> rateList;
    private List<AddToCartClass> cartList;
    private List<AddToCartClass> cartListFinal;
    private List<String> custList;
    private float rate = 0, totQty = 0, totAmnt = 0, totCGSTAmnt, totSGSTAmnt, actRetMemoAmnt;
    private ProductClass productClass = null;
    private RateMasterClass rateMasterClass = null;
    private AddToCartRecyclerAdapter adapter;
    private SlidingUpPanelLayout sliding_layout;
    private BluetoothService mService;
    private BluetoothDevice con_dev = null;
    private String billNo, cgstPerStr, sgstPerStr;
    private Dialog prodDialog = null;
    private int allSelected = -1, prodSelected = -1, rateSelected = -1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_memo);
        init();
        setProdData();
        setRateList();
        setSpinnerData();
        setCustData();

        if (mService != null) {
            mService.stop();
        }
        mService = new BluetoothService(getApplicationContext(), mHandler1);
        connectBT();

        sp_paymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String str = (String) adapterView.getItemAtPosition(i);
                Constant.showLog(str);
                if (str.equalsIgnoreCase("Card") || str.equalsIgnoreCase("Credit")
                        || str.equalsIgnoreCase(" Credit Card") || str.contains("CreditCard")) {
                    ed_cardNo.setVisibility(View.VISIBLE);
                    ed_chequeNo.setVisibility(View.GONE);
                } else if (str.equalsIgnoreCase("Cheque")) {
                    ed_cardNo.setVisibility(View.GONE);
                    ed_chequeNo.setVisibility(View.VISIBLE);
                }else {
                    ed_cardNo.setVisibility(View.GONE);
                    ed_chequeNo.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_returnMemo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String str = (String) adapterView.getItemAtPosition(i);
                Constant.showLog(str);
                String balAmnt = db.getBalRedeemOfMemo(str);
                actRetMemoAmnt = stringToFloat(balAmnt);
                ed_returnMemo.setText(roundDecimals(balAmnt));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ed_Qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                /*String qty = ed_Qty.getText().toString();
                String amnt = ed_rate.getText().toString();
                if(!amnt.equals("")) {
                    if (!qty.equals("")) {
                        int value = stringToInt(qty);
                        if (value > 0) {
                            float _rate = rate * value;
                            ed_rate.setText(String.valueOf(_rate));
                        } else {
                            toast.setText("Qty Can Not Be Zero");
                            toast.show();
                        }
                    }
                }else {
                    toast.setText("Please Select Rate First");
                    toast.show();
                }*/
            }
        });

        ed_returnMemo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //if(sp_returnMemo.getSelectedItemPosition()!=0) {
                    finalCalculation();
                //}
            }
        });

        ed_tenderAmnt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                /*if (!ed_tenderAmnt.getText().toString().equals("")) {
                    float cashAmt = Float.parseFloat(ed_cash.getText().toString());
                    float netAmt = Float.parseFloat(tv_netAmnt.getText().toString());
                    float retAmt = netAmt - cashAmt;
                    tv_retAmnt.setText(roundTwoDecimals(retAmt));
                } else {
                    ed_tenderAmnt.setText("");
                    tv_retAmnt.setText("0");
                }*/
                finalCalculation();
            }
        });

        ed_discPer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setItemwiseDiscount(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                /*if (!ed_discPer.getText().toString().equals("")) {
                    setItemwiseDiscount();
                } else {
                    ed_cash.setText(roundDecimals(totAmnt));
                    tv_netAmnt.setText(roundDecimals(totAmnt));
                    ed_tenderAmnt.setText(roundDecimals(totAmnt));
                    ed_discPer.setText("");
                    ed_discAmnt.setText("0");
                }*/
            }
        });

        auto_CustName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    auto_CustName.showDropDown();
                    auto_CustName.setThreshold(0);
                }
                return false;
            }
        });

        auto_CustName.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int j, long l) {
                String selection = (String) adapterView.getItemAtPosition(j);
                String arr[] = selection.split("-");
                auto_CustName.setText(arr[0]);
                ed_custMobNo.setText(arr[1]);
                ed_remark.requestFocus();
                Constant.showLog(selection);
                int pos = -1;
                for (int i = 0; i < custList.size(); i++) {
                    if (custList.get(i).equals(selection)) {
                        pos = i;
                        break;
                    }
                }
                System.out.println("Position " + pos); //check it now in Logcat
            }
        });

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new CashMemoRecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv_Order);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                addToCart();
                break;
            case R.id.btn_save:
                saveBill();
                break;
            case R.id.btn_cancel:
                showDia(1);
                break;
            case R.id.tv_prodAll:
                showProdAlertDialog();
                break;
            case R.id.tv_priceAll:
                showRateAlertDialog();
                break;
            case R.id.tv_remove:
                removeQty();
                break;
            case R.id.tv_add:
                addQty();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        new Constant(CashMemoActivity.this).doFinish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.userprofile_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                new Constant(CashMemoActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(ProductClass prod) {
        productClass = prod;
    }

    @Override
    public void onItemClick(RateMasterClass _rate) {
        rateMasterClass = _rate;
        rate = _rate.getRate();
        ed_rate.setText(String.valueOf(_rate.getRate()));
    }

    @Override
    public void calculation(float qty, float amnt) {
        /*if (!ed_discPer.getText().toString().equals("")) {
            float disc = Float.parseFloat(ed_discPer.getText().toString());
            float discAmt = (totAmnt * disc) / 100;
            ed_discAmnt.setText(roundTwoDecimals(discAmt));
            ed_cash.setText(roundDecimals(totAmnt - discAmt));
            tv_netAmnt.setText(roundDecimals(totAmnt - discAmt));
            ed_tenderAmnt.setText(roundDecimals(totAmnt));
        } else {
            ed_cash.setText(roundDecimals(totAmnt));
            tv_netAmnt.setText(roundDecimals(totAmnt));
            ed_tenderAmnt.setText(roundDecimals(totAmnt));
        }*/
        tv_totalQty.setText(String.valueOf(totQty));
        tv_totalAmnt.setText(roundTwoDecimals(totAmnt));
        tv_retAmnt.setText("0");
        finalCalculation();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(prodDialog!=null) {
            if(allSelected==1) {
                productClass = (ProductClass) adapterView.getItemAtPosition(i);
                setProdData();
                Constant.showLog("Position is " + i+" - "+prodSelected);
            }else if(allSelected == 2){
                rateMasterClass = (RateMasterClass) adapterView.getItemAtPosition(i);
                rate = rateMasterClass.getRate();
                ed_rate.setText(rateMasterClass.getRateStr());
                setRateList();
                Constant.showLog("Position is " + i+" - "+rateSelected);
            }
            prodDialog.dismiss();
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof AddToCartRecyclerAdapter.AddToCartViewHolder) {
            final int[] flag = {0};
            String name = cartListFinal.get(viewHolder.getAdapterPosition()).getProdName();
            final AddToCartClass master = cartListFinal.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();
            totQty = totQty - master.getQty();
            totAmnt = totAmnt - stringToFloat(master.getAmnt());
            totCGSTAmnt = totCGSTAmnt -  stringToFloat(master.getCgstAmnt());
            totSGSTAmnt = totSGSTAmnt -  stringToFloat(master.getSgstAmnt());
            calculation(0,0);
            adapter.removeItem(viewHolder.getAdapterPosition());
            cartList.remove(deletedIndex);
            Constant.showLog("1-deletedIndex-"+deletedIndex+"-cartList-"+cartList.size()+"-cartListFinal-"+cartListFinal.size());
            Snackbar snackbar = Snackbar.make(sliding_layout, name + " removed from list!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    flag[0] = 1;
                    adapter.restoreItem(master, deletedIndex);
                    cartList.add(deletedIndex,master);
                    Constant.showLog("2-deletedIndex-"+deletedIndex+"-cartList-"+cartList.size()+"-cartListFinal-"+cartListFinal.size());
                    totQty = totQty + master.getQty();
                    totAmnt = totAmnt + stringToFloat(master.getAmnt());
                    totCGSTAmnt = totCGSTAmnt + stringToFloat(master.getCgstAmnt());
                    totSGSTAmnt = totSGSTAmnt + stringToFloat(master.getSgstAmnt());
                    calculation(0,0);
                }
            });
            snackbar.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {

                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    if(flag[0] !=1) {
                        //Constant.showLog("Item Deleted "+deletedIndex);
                        Constant.showLog("3-deletedIndex-"+deletedIndex+"-cartList-"+cartList.size()+"-cartListFinal-"+cartListFinal.size());
                        //cartList.remove(viewHolder.getAdapterPosition());
                    }
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public void startNewActivty(RecyclerView.ViewHolder viewHolder, int direction, int position) {

    }

    @Override
    protected void onDestroy() {
        if (mService != null) {
            mService.stop();
        }
        super.onDestroy();
    }

    private void finalCalculation(){
        float discAmnt = 0, returnMemoAmnt = 0, tenderAmnt = 0,
                tenderRetAmnt = 0, totAmnt = 0, netAmnt = 0;

        totAmnt = stringToFloat(tv_totalAmnt.getText().toString());

        if(!ed_discPer.getText().toString().equalsIgnoreCase("")){
             float discPer = stringToFloat(ed_discPer.getText().toString());
             discAmnt = (totAmnt*discPer)/100;
        }
        if(!ed_returnMemo.getText().toString().equalsIgnoreCase("")){
            returnMemoAmnt = stringToFloat(ed_returnMemo.getText().toString());
        }
        if(!ed_tenderAmnt.getText().toString().equalsIgnoreCase("")){
            tenderAmnt = stringToFloat(ed_tenderAmnt.getText().toString());
        }
        netAmnt = totAmnt - discAmnt - returnMemoAmnt;
        netAmnt = netAmnt + totCGSTAmnt + totSGSTAmnt;
        tenderRetAmnt = tenderAmnt - netAmnt;
        tv_netAmnt.setText(roundDecimals(netAmnt));
        ed_cash.setText(roundDecimals(netAmnt));
        tv_totcgstAmnt.setText(roundTwoDecimals(totCGSTAmnt));
        tv_sgstAmnt.setText(roundTwoDecimals(totSGSTAmnt));
        ed_discAmnt.setText(roundTwoDecimals(discAmnt));
        if(tenderAmnt!=0) {
            tv_retAmnt.setText(roundDecimals(tenderRetAmnt));
        }else{
            tv_retAmnt.setText("0");
        }
    }

    private void init() {
        constant = new Constant(CashMemoActivity.this);
        db = new DBHandler(getApplicationContext());
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        rv_Prod = findViewById(R.id.rv_prod);
        rv_Price = findViewById(R.id.rv_price);
        rv_Order = findViewById(R.id.rv_Order);
        tv_prodAll = findViewById(R.id.tv_prodAll);
        tv_priceAll = findViewById(R.id.tv_priceAll);
        tv_remove = findViewById(R.id.tv_remove);
        tv_add = findViewById(R.id.tv_add);
        tv_totalQty = findViewById(R.id.total_qty);
        tv_totalAmnt = findViewById(R.id.total_amount);
        tv_retAmnt = findViewById(R.id.tv_returnamnt);
        tv_netAmnt = findViewById(R.id.tv_netamnt);
        tv_totcgstAmnt = findViewById(R.id.tv_cgstamnt);
        tv_sgstAmnt = findViewById(R.id.tv_sgstamnt);
        sp_paymentType = findViewById(R.id.sp_payment);
        ed_cash = findViewById(R.id.ed_cash);
        ed_Qty = findViewById(R.id.ed_Qty);
        ed_rate = findViewById(R.id.ed_rate);
        ed_cardNo = findViewById(R.id.ed_cardNo);
        ed_custName = findViewById(R.id.ed_custName);
        ed_custMobNo = findViewById(R.id.ed_custMobNo);
        ed_remark = findViewById(R.id.ed_remark);
        btn_Add = findViewById(R.id.btn_add);
        btn_Save = findViewById(R.id.btn_save);
        btn_Cancel = findViewById(R.id.btn_cancel);
        auto_CustName = findViewById(R.id.auto_Cust);
        ed_discPer = findViewById(R.id.ed_discPer);
        ed_discAmnt = findViewById(R.id.ed_discAmnt);
        ed_tenderAmnt = findViewById(R.id.ed_tenderAmnt);
        ed_chequeNo = findViewById(R.id.ed_chequeNo);
        sliding_layout = findViewById(R.id.sliding_layout);
        sp_returnMemo = findViewById(R.id.sp_retMemo);
        ed_returnMemo = findViewById(R.id.ed_retMemo);

        tv_prodAll.setOnClickListener(this);
        tv_priceAll.setOnClickListener(this);
        tv_remove.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        tv_remove.setOnClickListener(this);
        btn_Add.setOnClickListener(this);
        btn_Save.setOnClickListener(this);
        btn_Cancel.setOnClickListener(this);


        prodList = new ArrayList<>();
        rateList = new ArrayList<>();
        cartList = new ArrayList<>();
        cartListFinal = new ArrayList<>();
        custList = new ArrayList<>();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_Order.setLayoutManager(mLayoutManager);
        rv_Order.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager mLayoutManager1 =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_Prod.setLayoutManager(mLayoutManager1);
        rv_Prod.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager mLayoutManager2 =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_Price.setLayoutManager(mLayoutManager2);
        rv_Price.setItemAnimator(new DefaultItemAnimator());

        auto_CustName.setOnItemClickListener((adapterView, view, i, l) -> {
            String str = (String) adapterView.getSelectedItem();
            Constant.showLog(str);
            /*String arr[] = str.split("-");
            auto_CustName.setText(arr[0]);
            ed_custMobNo.setText(arr[1]);
            ed_remark.requestFocus();*/
        });
    }

    private void setProdData() {
        prodList.clear();
        rv_Prod.setAdapter(null);
        Cursor res = db.getProductData();
        int position = -1;
        if (res.moveToFirst()) {
            do {
                position++;
                ProductClass prod = new ProductClass();
                prod.setProduct_ID(res.getInt(res.getColumnIndex(DBHandler.PM_Id)));
                prod.setCat1(res.getString(res.getColumnIndex(DBHandler.PM_Cat1)));
                prod.setCat2(res.getString(res.getColumnIndex(DBHandler.PM_Cat2)));
                prod.setCat3(res.getString(res.getColumnIndex(DBHandler.PM_Cat3)));
                prod.setProduct_Name(res.getString(res.getColumnIndex(DBHandler.PM_Cat3)));
                prod.setFinalProduct(res.getString(res.getColumnIndex(DBHandler.PM_Finalproduct)));
                prod.setDispFSKU(res.getString(res.getColumnIndex(DBHandler.PM_Finalproduct)));
                prod.setPprice(res.getFloat(res.getColumnIndex(DBHandler.PM_Pprice)));
                prod.setMrp(res.getFloat(res.getColumnIndex(DBHandler.PM_Mrp)));
                prod.setMrp(res.getFloat(res.getColumnIndex(DBHandler.PM_Mrp)));
                prod.setWprice(res.getFloat(res.getColumnIndex(DBHandler.PM_Wprice)));
                prod.setSsp(res.getFloat(res.getColumnIndex(DBHandler.PM_Ssp)));
                prod.setProduct_Barcode(res.getString(res.getColumnIndex(DBHandler.PM_Barcode)));
                prod.setGstGroup(res.getString(res.getColumnIndex(DBHandler.PM_Gstgroup)));
                prod.setHsnCode(res.getString(res.getColumnIndex(DBHandler.PM_Hsncode)));
                if(productClass!=null){
                 if(res.getInt(res.getColumnIndex(DBHandler.PM_Id))==productClass.getProduct_ID()){
                     prodSelected = position;
                     prod.setSelected(true);
                 }else{
                     prod.setSelected(false);
                 }
                }else {
                    prod.setSelected(false);
                }
                prod.setGstType(res.getString(res.getColumnIndex(DBHandler.PM_GSTType)));
                prodList.add(prod);
            } while (res.moveToNext());
        }
        res.close();
        ProductListRecyclerAdapter adapter = new ProductListRecyclerAdapter(getApplicationContext(), prodList);
        adapter.setOnClickListener1(this);
        rv_Prod.setAdapter(adapter);
        if(prodSelected!=-1) {
            rv_Prod.scrollToPosition(prodSelected);
        }
    }

    private void setRateList() {
        rateList.clear();
        rv_Price.setAdapter(null);
        Cursor res = db.getRateMaster();
        int position = -1;
        if (res.moveToFirst()) {
            do {
                position++;
                RateMasterClass rate = new RateMasterClass();
                rate.setAuto(res.getInt(res.getColumnIndex(DBHandler.RTM_Auto)));
                rate.setRate(res.getFloat(res.getColumnIndex(DBHandler.RTM_Rates)));
                rate.setRateStr(res.getString(res.getColumnIndex(DBHandler.RTM_Rates)));
                if(rateMasterClass!=null){
                    if(res.getInt(res.getColumnIndex(DBHandler.RTM_Auto))== rateMasterClass.getAuto()){
                        rate.setSelected(true);
                        rateSelected = position;
                    }else{
                        rate.setSelected(false);
                    }
                }else {
                    rate.setSelected(false);
                }
                rateList.add(rate);
            } while (res.moveToNext());
        }
        res.close();
        ProductRateRecyclerAdapter adapter1 = new ProductRateRecyclerAdapter(getApplicationContext(), rateList);
        adapter1.setOnClickListener1(this);
        rv_Price.setAdapter(adapter1);
        if(rateSelected!=-1) {
            rv_Price.scrollToPosition(rateSelected);
        }
    }

    private void setCustData(){
        custList.clear();
        Cursor res = db.getCustomerData();
        if (res.moveToFirst()) {
            do {
                String str;
                String name = res.getString(res.getColumnIndex(DBHandler.CSM_Name));
                String mob = res.getString(res.getColumnIndex(DBHandler.CSM_Mobno));
                str = name + "-"+mob;
                custList.add(str);
            } while (res.moveToNext());
        }
        res.close();
        auto_CustName.setAdapter(new ArrayAdapter<>(getApplication(),R.layout.cust_drop_down,custList));
        auto_CustName.setThreshold(0);
        auto_CustName.setSelection(0);

    }

    private void showProdAlertDialog() {
        allSelected = 1;
        prodDialog = new Dialog(this);
        prodDialog.setTitle("Select Item");
        /*LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        final View layout = inflater.inflate(R.layout.categorydialog,null,false);
        prodDialog.setContentView(layout);
        prodDialog.setTitle("All Product");

        ListView list1 = prodDialog.findViewById(R.id.listview);
        AllProductDialogAdapter adapter = new AllProductDialogAdapter(this,
                android.R.layout.simple_list_item_1, prodList);
        EditText ed_search = prodDialog.findViewById(R.id.ed_search);
        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.getFilter().filter(ed_search.getText().toString());
            }
        });

        list1.setOnItemClickListener(this);
        list1.setAdapter(adapter);
        prodDialog.show();*/
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        final View layout = inflater.inflate(R.layout.categorydialog,null,false);
        prodDialog.setContentView(layout);
        prodDialog.setTitle("All Product");

        GridView list1 = prodDialog.findViewById(R.id.gridview);
        AllProductDialogAdapter adapter = new AllProductDialogAdapter(this,
                R.layout.grid_item_all_product, prodList);
        EditText ed_search = prodDialog.findViewById(R.id.ed_search);
        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.getFilter().filter(ed_search.getText().toString());
            }
        });

        list1.setOnItemClickListener(this);
        list1.setAdapter(adapter);
        prodDialog.show();

    }

    private void showRateAlertDialog() {
        allSelected = 2;
        prodDialog = new Dialog(this);
        prodDialog.setTitle("Select Item");
        /*LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        final View layout = inflater.inflate(R.layout.categorydialog,null,false);
        prodDialog.setContentView(layout);
        prodDialog.setTitle("All Product");

        ListView list1 = prodDialog.findViewById(R.id.listview);
        AllRateDialogAdapter adapter = new AllRateDialogAdapter(this,
                android.R.layout.simple_list_item_1, rateList);
        EditText ed_search = prodDialog.findViewById(R.id.ed_search);
        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.getFilter().filter(ed_search.getText().toString());
            }
        });

        list1.setOnItemClickListener(this);
        list1.setAdapter(adapter);
        prodDialog.show();*/
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        final View layout = inflater.inflate(R.layout.categorydialog,null,false);
        prodDialog.setContentView(layout);
        prodDialog.setTitle("All Product");

        GridView list1 = prodDialog.findViewById(R.id.gridview);
        AllRateDialogAdapter adapter = new AllRateDialogAdapter(this,
                R.layout.grid_item_all_product, rateList);
        EditText ed_search = prodDialog.findViewById(R.id.ed_search);
        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.getFilter().filter(ed_search.getText().toString());
            }
        });

        list1.setOnItemClickListener(this);
        list1.setAdapter(adapter);
        prodDialog.show();
    }

    private void removeQty(){
        String qty = ed_Qty.getText().toString();
        String amnt = ed_rate.getText().toString();
        //if(!amnt.equals("")) {
            if (!qty.equals("")) {
                int value = stringToInt(qty);
                if (value > 1) {
                    value = value - 1;
                    float _rate = rate * value;
                    ed_Qty.setText(String.valueOf(value));
                    //ed_rate.setText(String.valueOf(_rate));
                } else {
                    toast.setText("Qty Can Not Be Zero");
                    toast.show();
                }
            } else {
                toast.setText("Please Enter Quantity");
                toast.show();
            }
        /*}else {
            toast.setText("Please Select Rate First");
            toast.show();
        }*/
    }

    private void addQty() {
        String qty = ed_Qty.getText().toString();
        String amnt = ed_rate.getText().toString();
        //if (!amnt.equals("")) {
            if (!qty.equals("")) {
                int value = stringToInt(qty);
                if (value >= 0) {
                    value = value + 1;
                    float _rate = rate * value;
                    ed_Qty.setText(String.valueOf(value));
                    //ed_rate.setText(String.valueOf(_rate));
                } else {
                    toast.setText("Please Enter Proper Value");
                    toast.show();
                }
            } else {
                toast.setText("Please Enter Quantity");
                toast.show();
            }
        /*} else {
            toast.setText("Please Select Rate First");
            toast.show();
        }*/
    }

    private void addToCart(){
        if(productClass!=null){
            //if(rateMasterClass!=null){
                String qty = ed_Qty.getText().toString();
                String rate = ed_rate.getText().toString();
                if (!rate.equals("")) {
                    if (!qty.equals("")) {
                        AddToCartClass addToCart = new AddToCartClass();
                        addToCart.setItemId(productClass.getProduct_ID());
                        addToCart.setProdName(productClass.getProduct_Name());
                        addToCart.setMrp(roundTwoDecimals(productClass.getMrp()));
                        addToCart.setActRate(roundTwoDecimals(productClass.getSsp()));
                        addToCart.setActMRP(roundTwoDecimals(productClass.getMrp()));
                        addToCart.setFatherSKU(productClass.getFinalProduct());
                        addToCart.setDispFSKU(productClass.getDispFSKU());
                        addToCart.setGstGroup(productClass.getGstGroup());
                        addToCart.setHsnCode(productClass.getHsnCode());

                        Cursor cursor = db.getGSTPer(productClass.getGstGroup(), stringToFloat(rate));
                        cursor.moveToFirst();
                        float gstPer = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_GSTPer));
                        float cgstPer = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_CGSTPer));
                        float sgstPer = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_SGSTPer));
                        float cgstShare = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_CGSTShare));
                        float sgstShare = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_SGSTShare));
                        cursor.close();

                        int qty1 = stringToInt(qty);
                        float _rate = stringToFloat(rate);
                        float _total = qty1 * _rate;
                        totQty = totQty + qty1;

                        if(productClass.getGstType().equals("I")){
                            float accValue = ((gstPer * 100) / (gstPer + 100));
                            float gstAmnt = (_rate * accValue) / 100;
                            float taxableRate = _rate - gstAmnt;
                            float total = (taxableRate * qty1);
                            float billdiscPer = 0;
                            if(!ed_discPer.getText().toString().equalsIgnoreCase("")){
                                billdiscPer = stringToFloat(ed_discPer.getText().toString());
                            }
                            float billDiscAmnt = (total * billdiscPer)/100;
                            float disctedTotal = total - billDiscAmnt;
                            float totalGST = (disctedTotal * gstPer)/100;
                            float cgstAmt = (disctedTotal * cgstPer) / 100;
                            float sgstAmt = (disctedTotal * sgstPer) / 100;
                            float netAmt = disctedTotal + cgstAmt + sgstAmt;
                            totAmnt = totAmnt + total;
                            addToCart.setEnteredRate(roundTwoDecimals(_rate));
                            addToCart.setAmnt(roundTwoDecimals(total));
                            addToCart.setRate(roundTwoDecimals(taxableRate));
                            addToCart.setTotal(roundTwoDecimals(total));
                            addToCart.setDiscountedTotal(roundTwoDecimals(disctedTotal));
                            addToCart.setTotalGST(roundTwoDecimals(totalGST));
                            addToCart.setNetAmnt(roundTwoDecimals(netAmt));
                            addToCart.setBillDiscPer(roundTwoDecimals(billdiscPer));
                            addToCart.setBillDiscAmnt(roundTwoDecimals(billDiscAmnt));
                            addToCart.setTaxableRate(roundTwoDecimals(disctedTotal));
                            addToCart.setGstPer(roundTwoDecimals(gstPer));
                            addToCart.setCgstAmnt(roundTwoDecimals(cgstAmt));
                            addToCart.setSgstAmnt(roundTwoDecimals(sgstAmt));
                            addToCart.setCgstPer(roundTwoDecimals(cgstPer));
                            addToCart.setSgstPer(roundTwoDecimals(sgstPer));
                            addToCart.setCessAmnt(roundTwoDecimals(0));
                            addToCart.setCessPer(roundTwoDecimals(0));
                            addToCart.setIgstAmnt(roundTwoDecimals(0));
                            addToCart.setGstType(productClass.getGstType());
                            cgstPerStr = roundTwoDecimals(cgstPer);
                            sgstPerStr = roundTwoDecimals(sgstPer);
                            totCGSTAmnt = totCGSTAmnt + cgstAmt;
                            totSGSTAmnt = totSGSTAmnt + sgstAmt;

                        }else if(productClass.getGstType().equals("E")){
                            float taxableRate = _rate;
                            float total = (taxableRate * qty1);
                            float billdiscPer = 0;
                            if(!ed_discPer.getText().toString().equalsIgnoreCase("")){
                                billdiscPer = stringToFloat(ed_discPer.getText().toString());
                            }
                            float billDiscAmnt = (total * billdiscPer)/100;
                            float disctedTotal = total - billDiscAmnt;
                            float totalGST = (disctedTotal * gstPer)/100;
                            float cgstAmt = (disctedTotal * cgstPer) / 100;
                            float sgstAmt = (disctedTotal * sgstPer) / 100;
                            float netAmt = disctedTotal + cgstAmt + sgstAmt;
                            totAmnt = totAmnt + total;
                            addToCart.setEnteredRate(roundTwoDecimals(_rate));
                            addToCart.setAmnt(roundTwoDecimals(total));
                            addToCart.setRate(roundTwoDecimals(taxableRate));
                            addToCart.setDiscountedTotal(roundTwoDecimals(disctedTotal));
                            addToCart.setTotalGST(roundTwoDecimals(totalGST));
                            addToCart.setNetAmnt(roundTwoDecimals(netAmt));
                            addToCart.setTotal(roundTwoDecimals(total));
                            addToCart.setBillDiscPer(roundTwoDecimals(billdiscPer));
                            addToCart.setBillDiscAmnt(roundTwoDecimals(billDiscAmnt));
                            addToCart.setTaxableRate(roundTwoDecimals(disctedTotal));
                            addToCart.setGstPer(roundTwoDecimals(gstPer));
                            addToCart.setCgstAmnt(roundTwoDecimals(cgstAmt));
                            addToCart.setSgstAmnt(roundTwoDecimals(sgstAmt));
                            addToCart.setCgstPer(roundTwoDecimals(cgstPer));
                            addToCart.setSgstPer(roundTwoDecimals(sgstPer));
                            addToCart.setCessAmnt(roundTwoDecimals(0));
                            addToCart.setCessPer(roundTwoDecimals(0));
                            addToCart.setIgstAmnt(roundTwoDecimals(0));
                            addToCart.setGstType(productClass.getGstType());
                            cgstPerStr = roundTwoDecimals(cgstPer);
                            sgstPerStr = roundTwoDecimals(sgstPer);
                            totCGSTAmnt = totCGSTAmnt + cgstAmt;
                            totSGSTAmnt = totSGSTAmnt + sgstAmt;
                        }
                        addToCart.setQty(stringToInt(qty));
                        cartList.add(addToCart);
                        cartListFinal.add(addToCart);
                        if(cartList.size()==1) {
                            adapter = new AddToCartRecyclerAdapter(getApplicationContext(), cartListFinal);
                            adapter.setOnClickListener1(this);
                            rv_Order.setAdapter(adapter);
                        }else{
                            adapter.notifyDataSetChanged();
                        }
                        rv_Order.scrollToPosition(cartListFinal.size()-1);
                    } else {
                        toast.setText("Please Enter Quantity");
                        toast.show();
                    }
                } else {
                    toast.setText("Please Enter Amnt");
                    toast.show();
                }
            /*}else{
                toast.setText("Please Select Rate First");
                toast.show();
            }*/
        }else{
            toast.setText("Please Select Product First");
            toast.show();
        }
    }

    private void setItemwiseDiscount(String billdiscPerStr) {
        Constant.showLog(billdiscPerStr);
        cartListFinal.clear();
        totAmnt = 0;
        totQty = 0;
        totCGSTAmnt = 0;
        totSGSTAmnt = 0;
        //rv_Order.setAdapter(null);
        for (int i = 0; i < cartList.size(); i++) {
            AddToCartClass cart = cartList.get(i);
            AddToCartClass addToCart = new AddToCartClass();
            addToCart.setItemId(cart.getItemId());
            addToCart.setProdName(cart.getProdName());
            addToCart.setMrp(cart.getMrp());
            addToCart.setAmnt(cart.getAmnt());
            addToCart.setActRate(cart.getActRate());
            addToCart.setActMRP(cart.getActMRP());
            addToCart.setFatherSKU(cart.getFatherSKU());
            addToCart.setDispFSKU(cart.getDispFSKU());
            addToCart.setGstGroup(cart.getGstGroup());
            addToCart.setHsnCode(cart.getHsnCode());

            Cursor cursor = db.getGSTPer(cart.getGstGroup(),stringToFloat(cart.getEnteredRate()));
            cursor.moveToFirst();
            float gstPer = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_GSTPer));
            float cgstPer = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_CGSTPer));
            float sgstPer = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_SGSTPer));
            float cgstShare = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_CGSTShare));
            float sgstShare = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_SGSTShare));
            cursor.close();

            int qty1 = cart.getQty();
            float _rate = stringToFloat(cart.getEnteredRate());
            float _total = qty1 * _rate;
            totQty = totQty + qty1;

            if (cart.getGstType().equals("I")) {
                float accValue = ((gstPer * 100) / (gstPer + 100));
                float gstAmnt = (_rate * accValue) / 100;
                float taxableRate = _rate - gstAmnt;
                float total = (taxableRate * qty1);
                /*float billdiscPer = 0;
                if(!ed_discPer.getText().toString().equalsIgnoreCase("")){
                    billdiscPer = stringToFloat(ed_discPer.getText().toString());
                }*/
                float billdiscPer = stringToFloat(billdiscPerStr);
                float billDiscAmnt = (total * billdiscPer) / 100;
                float disctedTotal = total - billDiscAmnt;
                float totalGST = (disctedTotal * gstPer) / 100;
                float cgstAmt = (disctedTotal * cgstPer) / 100;
                float sgstAmt = (disctedTotal * sgstPer) / 100;
                float netAmt = disctedTotal + cgstAmt + sgstAmt;
                totAmnt = totAmnt + total;
                addToCart.setRate(roundTwoDecimals(taxableRate));
                addToCart.setAmnt(roundTwoDecimals(total));
                addToCart.setDiscountedTotal(roundTwoDecimals(disctedTotal));
                addToCart.setTotalGST(roundTwoDecimals(totalGST));
                addToCart.setNetAmnt(roundTwoDecimals(netAmt));
                addToCart.setTotal(roundTwoDecimals(total));
                addToCart.setBillDiscPer(roundTwoDecimals(billdiscPer));
                addToCart.setBillDiscAmnt(roundTwoDecimals(billDiscAmnt));
                addToCart.setTaxableRate(roundTwoDecimals(disctedTotal));
                addToCart.setGstPer(roundTwoDecimals(gstPer));
                addToCart.setCgstAmnt(roundTwoDecimals(cgstAmt));
                addToCart.setSgstAmnt(roundTwoDecimals(sgstAmt));
                addToCart.setCgstPer(roundTwoDecimals(cgstPer));
                addToCart.setSgstPer(roundTwoDecimals(sgstPer));
                addToCart.setCessAmnt(roundTwoDecimals(0));
                addToCart.setCessPer(roundTwoDecimals(0));
                addToCart.setIgstAmnt(roundTwoDecimals(0));
                addToCart.setGstType(cart.getGstType());
                cgstPerStr = roundTwoDecimals(cgstPer);
                sgstPerStr = roundTwoDecimals(sgstPer);
                totCGSTAmnt = totCGSTAmnt + cgstAmt;
                totSGSTAmnt = totSGSTAmnt + sgstAmt;

            } else if (cart.getGstType().equals("E")) {
                float taxableRate = _rate;
                float total = (taxableRate * qty1);
                /*float billdiscPer = 0;
                if(!ed_discPer.getText().toString().equalsIgnoreCase("")){
                    billdiscPer = stringToFloat(ed_discPer.getText().toString());
                }*/
                float billdiscPer = stringToFloat(billdiscPerStr);
                float billDiscAmnt = (total * billdiscPer) / 100;
                float disctedTotal = total - billDiscAmnt;
                float totalGST = (disctedTotal * gstPer) / 100;
                float cgstAmt = (disctedTotal * cgstPer) / 100;
                float sgstAmt = (disctedTotal * sgstPer) / 100;
                float netAmt = disctedTotal + cgstAmt + sgstAmt;
                totAmnt = totAmnt + total;
                addToCart.setRate(roundTwoDecimals(taxableRate));
                addToCart.setAmnt(roundTwoDecimals(total));
                addToCart.setDiscountedTotal(roundTwoDecimals(disctedTotal));
                addToCart.setTotalGST(roundTwoDecimals(totalGST));
                addToCart.setNetAmnt(roundTwoDecimals(netAmt));
                addToCart.setTotal(roundTwoDecimals(total));
                addToCart.setBillDiscPer(roundTwoDecimals(billdiscPer));
                addToCart.setBillDiscAmnt(roundTwoDecimals(billDiscAmnt));
                addToCart.setTaxableRate(roundTwoDecimals(disctedTotal));
                addToCart.setGstPer(roundTwoDecimals(gstPer));
                addToCart.setCgstAmnt(roundTwoDecimals(cgstAmt));
                addToCart.setSgstAmnt(roundTwoDecimals(sgstAmt));
                addToCart.setCgstPer(roundTwoDecimals(cgstPer));
                addToCart.setSgstPer(roundTwoDecimals(sgstPer));
                addToCart.setCessAmnt(roundTwoDecimals(0));
                addToCart.setCessPer(roundTwoDecimals(0));
                addToCart.setIgstAmnt(roundTwoDecimals(0));
                addToCart.setGstType(cart.getGstType());
                cgstPerStr = roundTwoDecimals(cgstPer);
                sgstPerStr = roundTwoDecimals(sgstPer);
                totCGSTAmnt = totCGSTAmnt + cgstAmt;
                totSGSTAmnt = totSGSTAmnt + sgstAmt;
            }
            addToCart.setQty(cart.getQty());
            cartListFinal.add(addToCart);
        }

        rv_Order.setAdapter(null);
        adapter = new AddToCartRecyclerAdapter(getApplicationContext(), cartListFinal);
        adapter.setOnClickListener1(this);
        rv_Order.setAdapter(adapter);
        rv_Order.scrollToPosition(cartListFinal.size() - 1);
    }

    private void saveBill() {
        int branchId, jobCardId = 0, creadtedBy = 1, modifiedBy=0, bankID = 0, printno = 1,
                GVoucher=0, agentid = 0, GVSchemeId = 0, CancelledBy=0, Deliveredby=0, Currencyid = 0, Type = 1,
                BothId = 0, NewDCNo = 0, updateBalRedeem = 0, validate = -1;

        float totalQty=0, totalAmnt=0, returnamnt = 0, creditAmnt=0, cashAmnt=0, paidAmnt=0, balAmnt = 0, vat12 = 0, vat4 = 0, labour=0, cheqAmnt = 0,
                advanceamt=0, netamt=0, commInPer = 0, commInRs = 0, disper = 0, disamt = 0, piamt = 0, GVoucherAmt=0,
                GVAmt = 0, GrossAmount=0, Alteration = 0, CashBack = 0, SchemeAmt = 0, GoodsReturn = 0, RemainAmt=0,
                TRetQty = 0, TotalCurrency = 0, CGSTAMT=0, SGSTAMT=0, IGSTAMT = 0, balRedeemAmnt = 0;

        String finyr, custId , retMemoNo="", breakUpAmnt="", billSt = "A", vehicleNo="", modifieDdt="",
                vehicleMake="", vehicleColor="", driverName="", cardNo="", cheqNo="", cheqDate="", paymenttype = "A",
                billpayst = "Y", inwrds="", refundpyst = "N", pino = "0", mon, vouchergenerate = "N", Scheme="", GV="",
                GVNo="", IssueGVSt = "N", Createdfrm = "C", CancelledDate="", Billingtime, Delivered = "Y",
                DeliveredDate = "", CounterNo = "Mob",machineName = new Constant(getApplicationContext()).getIMEINo1(),
                AgainstDC = "N", DCAutoNo = "0", CancelReason="",Tender="", CurrencyAmt = "0", CardBank="",
                CardTyp="", SwipReceiptNo="", Reference="", Repl_Column = "Normal",Through="", Authorised="",
                Remark="", TheirRemark="", IGSTAPP = "N", NewAgainstDC = "N";

        int detAuto, detId, billID, detbranchID, itemId, empID, autoBillId, detDeliveredby, Allotid,
                Schemeid, dcmastauto, detType, seqno,detqty = 0;

        float  detrate = 0, dettotal = 0, detincentamt = 0, detvat = 0, detvatamt = 0,
                detamtWithoutDisc = 0, detdisper = 0, detdisamt = 0, detratewithtax = 0,
                detpurchaseQty = 0, detfreeqty = 0, detMandalper = 0, detMandal = 0, detMRP = 0,
                detbilldisper = 0, detbilldisamt = 0,detRetQty = 0, detActRate = 0, detActMRP = 0,
                GSTPER = 0, detCGSTAMT = 0,detSGSTAMT = 0, CGSTPER = 0, SGSTPER = 0,
                CESSPER = 0, CESSAMT = 0,detIGSTAMT = 0, detTaxableAmt = 0;

        String detfinYr, barcode, fatherSKU, detmon, nonBar, AlteredStat, detDelivered, detDelivereddate, BGType,
                SchemeApp, DispFSKU, DesignNo, detSchmBenefit;

        String billDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        Billingtime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String crDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Calendar.getInstance().getTime());

        finyr = detfinYr = new Constant(getApplicationContext()).getFinYr();
        DateFormat dateFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
        Date date = new Date();
        mon = dateFormat.format(date);
        detmon = dateFormat.format(date);
        Constant.showLog(mon);

        int mastAuto = db.getMaxAuto();
        int mastId = db.getMaxMastId(finyr);
        billNo = db.getCompIni() + finyr + mastId;
        totalQty = totQty;
        totalAmnt = stringToFloat(tv_totalAmnt.getText().toString());
        netamt = Float.parseFloat(String.valueOf(tv_netAmnt.getText().toString()));
        GrossAmount = totalAmnt;
        Tender = roundTwoDecimals(ed_tenderAmnt.getText().toString());
        RemainAmt = stringToFloat(tv_retAmnt.getText().toString());

        String custName = auto_CustName.getText().toString();
        String custMobNo = ed_custMobNo.getText().toString();
        if(custName.equals("") && custMobNo.equals("")){
            custId = "1#1";
        }else{
            custId = db.getCustid(custName,custMobNo);
            if(custId.equals("0")){
                custId = db.saveCustomerMaster(custName,custMobNo);
            }
        }
        Remark = ed_remark.getText().toString();

        paymenttype = (String) sp_paymentType.getSelectedItem();
        CardTyp = paymenttype;
        if(paymenttype.equalsIgnoreCase("Cash")){
            cashAmnt = Float.parseFloat(ed_cash.getText().toString());
            paidAmnt = cashAmnt;
            creditAmnt = 0;
            GVAmt = 0;
        }else if(paymenttype.equalsIgnoreCase("Card") || paymenttype.equalsIgnoreCase("Credit")
                || paymenttype.equalsIgnoreCase("Credit Card") || paymenttype.contains("CreditCard")){
            cashAmnt = 0;
            creditAmnt = Float.parseFloat(ed_cash.getText().toString());
            paidAmnt = creditAmnt;
            GVAmt = 0;
        }else if(paymenttype.equalsIgnoreCase("Cheque")){
            cashAmnt = 0;
            creditAmnt = 0;
            cheqAmnt = Float.parseFloat(ed_cash.getText().toString());
            paidAmnt = cheqAmnt;
            cheqNo = ed_chequeNo.getText().toString();
            GVAmt = 0;
        }else {
            cashAmnt = 0;
            creditAmnt = 0;
            GVAmt = Float.parseFloat(ed_cash.getText().toString());
        }

        if(sp_returnMemo.getSelectedItemPosition()!=0) {
            retMemoNo = (String) sp_returnMemo.getSelectedItem();
            returnamnt = stringToFloat(ed_returnMemo.getText().toString());
            balRedeemAmnt = actRetMemoAmnt - returnamnt;
            updateBalRedeem = 1;
        }

        disper = stringToFloat(ed_discPer.getText().toString());
        disamt = stringToFloat(ed_discAmnt.getText().toString());

        CGSTAMT = totCGSTAmnt;
        SGSTAMT = totSGSTAmnt;
        branchId = 1;

        if(updateBalRedeem == 0){
            if(!ed_returnMemo.getText().toString().equalsIgnoreCase("0")
                    && !ed_returnMemo.getText().toString().equalsIgnoreCase("")) {
                validate = 99;
            }else {
                validate = 1;
            }
        }else {
            if(returnamnt >actRetMemoAmnt) {
                validate = 11;
            }else{
                validate = 1;
            }
        }

        if(validate == 1 && cartListFinal.size()!=0) {
            BillMasterClass mast = new BillMasterClass();
            mast.setAutoNo(mastAuto);
            mast.setId(mastId);
            mast.setBranchID(branchId);
            mast.setFinYr(finyr);
            mast.setBillNo(billNo);
            mast.setCustID(custId);
            mast.setJobCardID(jobCardId);
            mast.setBillDate(billDate);
            mast.setTotalQty(roundDecimals(totalQty));
            mast.setTotalAmt(roundDecimals(totalAmnt));
            mast.setRetMemoNo(retMemoNo);
            mast.setReturnAmt(roundDecimals(returnamnt));
            mast.setCreditAmt(roundDecimals(creditAmnt));
            mast.setCashAmt(roundDecimals(cashAmnt));
            mast.setPaidAmt(roundDecimals(paidAmnt));
            mast.setBalAmt(roundDecimals(balAmnt));
            mast.setBrakeUpAmt(breakUpAmnt);
            mast.setBillSt(billSt);
            mast.setVehicleNo(vehicleNo);
            mast.setCreatedby(creadtedBy);
            mast.setCreateddt(crDate);
            mast.setModifiedby(modifiedBy);
            mast.setModifieddt(modifieDdt);
            mast.setVehiclemake(vehicleMake);
            mast.setVehiclecolor(vehicleColor);
            mast.setDrivername(driverName);
            mast.setVat12(roundDecimals(vat12));
            mast.setVat4(roundDecimals(vat4));
            mast.setLabour(roundDecimals(labour));
            mast.setCardno(cardNo);
            mast.setChqno(cheqNo);
            mast.setChqamt(roundDecimals(cheqAmnt));
            mast.setChqdt(cheqDate);
            mast.setAdvanceamt(roundDecimals(advanceamt));
            mast.setPaymenttype(paymenttype);
            mast.setBillpayst(billpayst);
            mast.setNetamt(roundDecimals(netamt));
            mast.setBankID(bankID);
            mast.setCommInPer(roundDecimals(commInPer));
            mast.setCommInRs(roundDecimals(commInRs));
            mast.setPrintno(printno);
            mast.setDisper(roundDecimals(disper));
            mast.setDisamt(roundDecimals(disamt));
            mast.setInwrds(inwrds);
            mast.setRefundpyst(refundpyst);
            mast.setPino(pino);
            mast.setPiamt(roundDecimals(piamt));
            mast.setMon(mon);
            mast.setGVoucher(GVoucher);
            mast.setGVoucherAmt(roundDecimals(GVoucherAmt));
            mast.setAgentid(agentid);
            mast.setVouchergenerate(vouchergenerate);
            mast.setGVSchemeId(GVSchemeId);
            mast.setScheme(Scheme);
            mast.setGV(GV);
            mast.setGVAmt(roundDecimals(GVAmt));
            mast.setGVNo(GVNo);
            mast.setIssueGVSt(IssueGVSt);
            mast.setCreatedfrm(Createdfrm);
            mast.setGrossAmount(roundDecimals(GrossAmount));
            mast.setCancelledBy(CancelledBy);
            mast.setCancelledDate(CancelledDate);
            mast.setBillingtime(Billingtime);
            mast.setDelivered(Delivered);
            mast.setDeliveredby(Deliveredby);
            mast.setDeliveredDate(DeliveredDate);
            mast.setAlteration(roundDecimals(Alteration));
            mast.setCashBack(roundDecimals(CashBack));
            mast.setSchemeAmt(roundDecimals(SchemeAmt));
            mast.setGoodsReturn(roundDecimals(GoodsReturn));
            mast.setCounterNo(CounterNo);
            mast.setMachineName(machineName);
            mast.setAgainstDC(AgainstDC);
            mast.setDCAutoNo(DCAutoNo);
            mast.setCancelReason(CancelReason);
            mast.setTender(Tender);
            mast.setRemainAmt(roundDecimals(RemainAmt));
            mast.setTRetQty(roundDecimals(TRetQty));
            mast.setCurrencyid(Currencyid);
            mast.setTotalCurrency(roundDecimals(TotalCurrency));
            mast.setCurrencyAmt(CurrencyAmt);
            mast.setCardBank(CardBank);
            mast.setCardTyp(CardTyp);
            mast.setSwipReceiptNo(SwipReceiptNo);
            mast.setReference(Reference);
            mast.setRepl_Column(Repl_Column);
            mast.setThrough(Through);
            mast.setAuthorised(Authorised);
            mast.setRemark(Remark);
            mast.setTheirRemark(TheirRemark);
            mast.setCGSTAMT(roundTwoDecimals(CGSTAMT));
            mast.setSGSTAMT(roundTwoDecimals(SGSTAMT));
            mast.setIGSTAPP(IGSTAPP);
            mast.setIGSTAMT(roundTwoDecimals(IGSTAMT));
            mast.setType(Type);
            mast.setBothId(BothId);
            mast.setNewAgainstDC(NewAgainstDC);
            mast.setNewDCNo(NewDCNo);
            db.saveBillMaster(mast);
            if (updateBalRedeem == 1) {
                db.updateBalRedeem(balRedeemAmnt, retMemoNo);
            }

            for (AddToCartClass cart : cartListFinal) {
                detAuto = db.getMaxDetAuto();
                detId = db.getMaxDetId(mastAuto);
                billID = mastId;
                detbranchID = 1;
                itemId = cart.getItemId();
                detrate = stringToFloat(cart.getRate());
                detqty = cart.getQty();
                dettotal = stringToFloat(cart.getTotal());
                barcode = cart.getBarcode();
                fatherSKU = cart.getFatherSKU();
                empID = 1;
                detincentamt = 0;
                autoBillId = mastId;
                detvat = 0;
                detvatamt = 0;
                detamtWithoutDisc = stringToFloat(cart.getAmnt());
                detdisper = 0;
                detdisamt = 0;
                nonBar = "";
                detratewithtax = stringToFloat(cart.getRate());
                detpurchaseQty = cart.getQty();
                detfreeqty = 0;
                AlteredStat = "N";
                detMandalper = 0;
                detDelivered = "Y";
                detDeliveredby = 0;
                detDelivereddate = "";
                BGType = "N";
                Allotid = 0;
                SchemeApp = "N";
                Schemeid = 0;
                DispFSKU = cart.getDispFSKU();
                detMRP = stringToFloat(cart.getMrp());
                detbilldisper = stringToFloat(cart.getBillDiscPer());
                detbilldisamt = stringToFloat(cart.getBillDiscAmnt());
                dcmastauto = 0;
                DesignNo = "";
                detRetQty = 0;
                detActRate = stringToFloat(cart.getActRate());
                detActMRP = stringToFloat(cart.getActMRP());
                detSchmBenefit = "0";
                GSTPER = stringToFloat(cart.getGstPer());
                detCGSTAMT = stringToFloat(cart.getCgstAmnt());
                detSGSTAMT = stringToFloat(cart.getSgstAmnt());
                CGSTPER = stringToFloat(cart.getCgstPer());
                SGSTPER = stringToFloat(cart.getSgstPer());
                CESSPER = stringToFloat(cart.getCessPer());
                CESSAMT = stringToFloat(cart.getCessAmnt());
                detIGSTAMT = stringToFloat(cart.getIgstAmnt());
                detTaxableAmt = stringToFloat(cart.getTaxableRate());
                detType = 1;
                seqno = 0;

                BillDetailClass det = new BillDetailClass();
                det.setAuto(detAuto);
                det.setId(detId);
                det.setBillID(billID);
                det.setBranchID(detbranchID);
                det.setFinYr(detfinYr);
                det.setItemId(itemId);
                det.setRate(roundTwoDecimals(detrate));
                det.setQty(String.valueOf(detqty));
                det.setTotal(roundTwoDecimals(dettotal));
                det.setBarcode(barcode);
                det.setEmpID(empID);
                det.setIncentamt(roundTwoDecimals(detincentamt));
                det.setFatherSKU(fatherSKU);
                det.setAutoBillId(autoBillId);
                det.setVat(roundTwoDecimals(detvat));
                det.setVatamt(roundTwoDecimals(detvatamt));
                det.setAmtWithoutDisc(roundTwoDecimals(detamtWithoutDisc));
                det.setDisper(roundTwoDecimals(detdisper));
                det.setDisamt(roundTwoDecimals(detdisamt));
                det.setNonBar(nonBar);
                det.setMon(detmon);
                det.setRatewithtax(roundTwoDecimals(detratewithtax));
                det.setPurchaseQty(String.valueOf(detpurchaseQty));
                det.setFreeqty(roundTwoDecimals(detfreeqty));
                det.setAlteredStat(AlteredStat);
                det.setMandalper(roundTwoDecimals(detMandalper));
                det.setMandal(roundTwoDecimals(detMandal));
                det.setDelivered(detDelivered);
                det.setDeliveredby(detDeliveredby);
                det.setDelivereddate(detDelivereddate);
                det.setBGType(BGType);
                det.setAllotid(Allotid);
                det.setSchemeApp(SchemeApp);
                det.setSchemeid(Schemeid);
                det.setDispFSKU(DispFSKU);
                det.setMRP(roundTwoDecimals(detMRP));
                det.setBilldisper(roundTwoDecimals(detbilldisper));
                det.setBilldisamt(roundTwoDecimals(detbilldisamt));
                det.setDcmastauto(dcmastauto);
                det.setDesignNo(DesignNo);
                det.setRetQty(roundTwoDecimals(detRetQty));
                det.setActRate(roundTwoDecimals(detActRate));
                det.setActMRP(roundTwoDecimals(detActMRP));
                det.setSchmBenefit(roundTwoDecimals(detSchmBenefit));
                det.setGSTPER(roundTwoDecimals(GSTPER));
                det.setCGSTAMT(roundTwoDecimals(detCGSTAMT));
                det.setSGSTAMT(roundTwoDecimals(detSGSTAMT));
                det.setCGSTPER(roundTwoDecimals(CGSTPER));
                det.setSGSTPER(roundTwoDecimals(SGSTPER));
                det.setCESSPER(roundTwoDecimals(CESSPER));
                det.setCESSAMT(roundTwoDecimals(CESSAMT));
                det.setIGSTAMT(roundTwoDecimals(detIGSTAMT));
                det.setTaxableAmt(roundTwoDecimals(detTaxableAmt));
                det.setType(detType);
                det.setSeqno(seqno);
                db.saveBillDetail(det);
                db.updateProductQty(cart.getItemId(), cart.getQty());
            }
            showDia(2);
        }else {
            if(validate == 99){
                toast.setText("Please Select Return Memo Number");
                toast.show();
            }else if(validate == 11){
                toast.setText("Please Enter Proper Return Memo Amount");
                toast.show();
            }else if(cartListFinal.size()==0){
                toast.setText("Please Enter Atleast One Order");
                toast.show();
            }
        }
    }

    private int stringToInt(String value){
        if(value.equalsIgnoreCase("")){
            return 0;
        }else {
            return Integer.parseInt(value);
        }
    }

    private float stringToFloat(String value){
        if(value.equalsIgnoreCase("")){
            return 0;
        }else {
            return Float.parseFloat(value);
        }
    }

    private void setSpinnerData(){
        Cursor res = db.getPaymentType();
        List<String> list = new ArrayList<>();
        if(res.moveToFirst()){
            do{
                list.add(res.getString(res.getColumnIndex(DBHandler.PY_TYPE)));
            }while (res.moveToNext());
        }
        res.close();
        sp_paymentType.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.list_item_option_setttings,list));

        Cursor retRes = db.getReturnMemo();
        List<String> list1 = new ArrayList<>();
        list1.add("Select Return Memo");
        if(retRes.moveToFirst()){
            do{
                list1.add(retRes.getString(retRes.getColumnIndex(DBHandler.RMM_Rmemono)));
            }while (retRes.moveToNext());
        }
        retRes.close();
        sp_returnMemo.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.list_item_option_setttings,list1));
    }

    private void showDia(int a){
        AlertDialog.Builder builder = new AlertDialog.Builder(CashMemoActivity.this);
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
            builder.setMessage("Do You Want To Cancel Operation?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    clearField();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else if (a == 2) {
            builder.setMessage("Bill Saved Successfully");
            builder.setPositiveButton("Print", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new CashMemoPrint().execute();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("New Order", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    clearField();
                }
            });
        }
        builder.create().show();
    }

    private void clearField(){
        productClass = null;
        rateMasterClass = null;
        prodSelected = -1;
        rateSelected = -1;
        setProdData();
        setRateList();
        setSpinnerData();
        totQty = 0;
        totAmnt = 0;
        actRetMemoAmnt = 0;
        totCGSTAmnt = 0;
        totSGSTAmnt = 0;
        cgstPerStr = "0";
        sgstPerStr = "0";
        ed_Qty.setText("1");
        ed_rate.setText(null);
        cartList.clear();
        cartListFinal.clear();
        rv_Order.setAdapter(null);
        productClass = null;
        rateMasterClass = null;
        tv_totalQty.setText("0");
        tv_totalAmnt.setText("0");
        ed_discPer.setText(null);
        ed_discAmnt.setText(null);
        ed_cash.setText("0");
        ed_cardNo.setText(null);
        ed_custName.setText(null);
        auto_CustName.setSelection(0);
        ed_custMobNo.setText(null);
        ed_returnMemo.setText("0");
        tv_retAmnt.setText("0");
        tv_netAmnt.setText("0");
        ed_tenderAmnt.setText("0");
        sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        sp_paymentType.setSelection(0);
        sp_returnMemo.setSelection(0);
    }

    @SuppressLint("StaticFieldLeak")
    private class CashMemoPrint extends AsyncTask<Void, Void, String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(CashMemoActivity.this);
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
                mService.sendMessage("TAX INVOICE", "GBK");

                byte[] left = {27, 97, 0};
                mService.write(PrinterCommands.ESC_ALIGN_LEFT);

                String date = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                String time = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                mService.sendMessage("BillNo : " + billNo, "GBK");
                String space_str13 = "             ";
                mService.sendMessage(date + space_str13 + time, "GBK");
                mService.sendMessage("Cust Name : " + ed_custName.getText().toString(), "GBK");
                mService.sendMessage("Mob No    : " + ed_custMobNo.getText().toString(), "GBK");

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
                for (int i = 0; i < cartList.size(); i++) {
                    AddToCartClass cart = cartList.get(i);
                    StringBuilder item = new StringBuilder(cart.getProdName());
                    String item1 = cart.getProdName();
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

                    String amnt = String.valueOf(cart.getAmnt());
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

                String  totalamt = roundDecimals(tv_netAmnt.getText().toString());
                /*String[] totArr = totalamt.split("\\.");
                if (totArr.length > 1) {
                    totalamt = totArr[0];
                }*/
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
                mService.sendMessage("CGST " + cgstPerStr + " % : " + roundTwoDecimals(totCGSTAmnt), "GBK");
                mService.sendMessage("SGST " + sgstPerStr + " % : " + roundTwoDecimals(totSGSTAmnt), "GBK");

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
            clearField();
        }
    }

    private String roundTwoDecimals(String d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return twoDForm.format(Double.parseDouble(d));
    }

    private String roundDecimals(String d) {
        DecimalFormat twoDForm = new DecimalFormat("#");
        return twoDForm.format(Double.parseDouble(d));
    }

    private String roundDecimals(float d) {
        DecimalFormat twoDForm = new DecimalFormat("#");
        return twoDForm.format(Double.parseDouble(String.valueOf(d)));
    }

    private String roundTwoDecimals(float d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return twoDForm.format(Double.parseDouble(String.valueOf(d)));
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
