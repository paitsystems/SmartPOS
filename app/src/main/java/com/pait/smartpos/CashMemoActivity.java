package com.pait.smartpos;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hoin.btsdk.BluetoothService;
import com.pait.smartpos.adpaters.AddToCartRecyclerAdapter;
import com.pait.smartpos.adpaters.AllProductAdapter;
import com.pait.smartpos.adpaters.AllRateAdapter;
import com.pait.smartpos.adpaters.ProductListRecyclerAdapter;
import com.pait.smartpos.adpaters.ProductRateRecyclerAdapter;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.constant.PrinterCommands;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.interfaces.OnItemClickListener;
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

public class CashMemoActivity extends AppCompatActivity implements View.OnClickListener, RecyclerViewToActivityInterface {

    private RecyclerView rv_Prod, rv_Price, rv_Order;
    private TextView tv_prodAll, tv_priceAll, tv_remove, tv_add, tv_totalQty, tv_totalAmnt,
            tv_retAmnt, tv_netAmnt;
    private EditText ed_Qty, ed_rate, ed_cash, ed_cardNo, ed_custName, ed_custMobNo,
            ed_remark, ed_discPer, ed_discAmnt;
    private Button btn_Add, btn_Save, btn_Cancel;
    private Spinner sp_paymentType;
    private AutoCompleteTextView auto_CustName;
    private Constant constant;
    private DBHandler db;
    private Toast toast;
    private List<ProductClass> prodList;
    private List<RateMasterClass> rateList;
    private List<AddToCartClass> cartList;
    private List<String> custList;
    private float rate = 0, totQty = 0, totAmnt = 0, totCGSTAmnt, totSGSTAmnt;
    private ProductClass productClass = null;
    private RateMasterClass rateMasterClass = null;
    private AddToCartRecyclerAdapter adapter;
    private SlidingUpPanelLayout sliding_layout;
    public static BluetoothService mService;
    private BluetoothDevice con_dev = null;
    private String billNo, cgstPerStr, sgstPerStr;

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

        if(mService!=null) {
            mService.stop();
        }
        mService = new BluetoothService(getApplicationContext(), mHandler1);
        connectBT();

        sp_paymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String str = (String) adapterView.getItemAtPosition(i);
                Constant.showLog(str);
                if(str.equalsIgnoreCase("Card") || str.equalsIgnoreCase("Credit")
                        || str.equalsIgnoreCase(" Credit Card") || str.contains("CreditCard") ){
                    ed_cardNo.setVisibility(View.VISIBLE);
                }else{
                    ed_cardNo.setVisibility(View.GONE);
                }
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
                    float cashAmt = Float.parseFloat(ed_cash.getText().toString());
                    float netAmt = Float.parseFloat(tv_netAmnt.getText().toString());
                    float retAmt = netAmt - cashAmt;
                    tv_retAmnt.setText(roundTwoDecimals(retAmt));
                } else {
                    ed_cash.setText("0");
                    ed_cash.setSelection(ed_cash.getText().toString().length());
                    tv_retAmnt.setText("0");
                }
            }
        });

        ed_discPer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!ed_discPer.getText().toString().equals("")) {
                    float disc = Float.parseFloat(ed_discPer.getText().toString());
                    float discAmt = (totAmnt * disc)/100;
                    ed_discAmnt.setText(String.valueOf(discAmt));
                    ed_cash.setText(String.valueOf(totAmnt - discAmt));
                    tv_netAmnt.setText(String.valueOf(totAmnt - discAmt));
                    //setItemwiseDiscount();
                } else {
                    ed_cash.setText(roundTwoDecimals(totAmnt));
                    tv_netAmnt.setText(roundTwoDecimals(totAmnt));
                    ed_discPer.setText("0");
                    ed_discAmnt.setText("0");
                }
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

        auto_CustName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        tv_totalQty.setText(String.valueOf(totQty));
        tv_totalAmnt.setText(roundTwoDecimals(totAmnt));
        ed_cash.setText(String.valueOf(totAmnt));
        tv_netAmnt.setText(roundTwoDecimals(totAmnt));
    }

    private void init(){
        constant = new Constant(CashMemoActivity.this);
        db = new DBHandler(getApplicationContext());
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
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
        sliding_layout = findViewById(R.id.sliding_layout);

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

    private void setProdData(){
        prodList.clear();
        rv_Prod.setAdapter(null);
        Cursor res = db.getProductData();
        if (res.moveToFirst()) {
            do {
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
                prod.setSelected(false);
                prod.setGstType(res.getString(res.getColumnIndex(DBHandler.PM_GSTType)));
                prodList.add(prod);
            } while (res.moveToNext());
        }
        res.close();
        ProductListRecyclerAdapter adapter = new ProductListRecyclerAdapter(getApplicationContext(),prodList);
        adapter.setOnClickListener1(this);
        rv_Prod.setAdapter(adapter);
    }

    private void setRateList() {
        rateList.clear();
        rv_Price.setAdapter(null);
        for (int i = 0; i < 11; i++) {
            RateMasterClass rate = new RateMasterClass();
            rate.setRate(1000 + i);
            rate.setSelected(false);
            rateList.add(rate);

            ProductRateRecyclerAdapter adapter1 = new ProductRateRecyclerAdapter(getApplicationContext(), rateList);
            adapter1.setOnClickListener1(this);
            rv_Price.setAdapter(adapter1);
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

    }

    private void showProdAlertDialog() {
        GridView gridView = new GridView(this);
        gridView.setAdapter(new AllProductAdapter(getApplicationContext(),prodList));
        gridView.setNumColumns(5);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
                productClass = prodList.get(position);
                toast.setText(productClass.getProduct_Cat());
                toast.show();
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(gridView);
        builder.setTitle("All Product");
        builder.show();
    }

    private void showRateAlertDialog() {
        GridView gridView = new GridView(this);
        gridView.setAdapter(new AllRateAdapter(getApplicationContext(),rateList));
        gridView.setNumColumns(5);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Dialog dialog = builder.create();
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            rateMasterClass = rateList.get(position);
            toast.setText(""+rateMasterClass.getRate());
            toast.show();
            dialog.dismiss();
        });
        builder.setView(gridView);
        builder.setTitle("All Rate");
        builder.show();
    }

    private void removeQty(){
        String qty = ed_Qty.getText().toString();
        String amnt = ed_rate.getText().toString();
        if(!amnt.equals("")) {
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
        }else {
            toast.setText("Please Select Rate First");
            toast.show();
        }
    }

    private void addQty() {
        String qty = ed_Qty.getText().toString();
        String amnt = ed_rate.getText().toString();
        if (!amnt.equals("")) {
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
        } else {
            toast.setText("Please Select Rate First");
            toast.show();
        }
    }

    private void addToCart(){
        if(productClass!=null){
            if(rateMasterClass!=null){
                String qty = ed_Qty.getText().toString();
                String rate = ed_rate.getText().toString();
                if (!rate.equals("")) {
                    if (!qty.equals("")) {
                        AddToCartClass addToCart = new AddToCartClass();
                        addToCart.setItemId(productClass.getProduct_ID());
                        addToCart.setProdName(productClass.getProduct_Name());
                        addToCart.setRate(roundTwoDecimals(rate));
                        addToCart.setMrp(roundTwoDecimals(productClass.getMrp()));
                        addToCart.setAmnt(roundTwoDecimals(productClass.getSsp()));
                        addToCart.setActRate(roundTwoDecimals(productClass.getSsp()));
                        addToCart.setActMRP(roundTwoDecimals(productClass.getMrp()));
                        addToCart.setFatherSKU(productClass.getFinalProduct());
                        addToCart.setDispFSKU(productClass.getDispFSKU());
                        addToCart.setGstGroup(productClass.getGstGroup());
                        addToCart.setHsnCode(productClass.getHsnCode());

                        Cursor cursor = db.getGSTPer(productClass.getGstGroup());
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
                            float billdiscPer = stringToFloat(ed_discPer.getText().toString());
                            float billDiscAmnt = (total * billdiscPer)/100;
                            float disctedTotal = total - billDiscAmnt;
                            float totalGST = (disctedTotal * gstPer)/100;
                            float cgstAmt = (disctedTotal * cgstPer) / 100;
                            float sgstAmt = (disctedTotal * sgstPer) / 100;
                            float netAmt = billDiscAmnt + cgstAmt + sgstAmt;
                            totAmnt = totAmnt + total;
                            addToCart.setTotal(roundTwoDecimals(total));
                            addToCart.setBillDiscPer(roundTwoDecimals(billdiscPer));
                            addToCart.setBillDiscAmnt(roundTwoDecimals(billDiscAmnt));
                            addToCart.setTaxableRate(roundTwoDecimals(taxableRate));
                            addToCart.setGstPer(roundTwoDecimals(gstPer));
                            addToCart.setCgstAmnt(roundTwoDecimals(cgstAmt));
                            addToCart.setSgstAmnt(roundTwoDecimals(sgstAmt));
                            addToCart.setCgstPer(roundTwoDecimals(cgstPer));
                            addToCart.setSgstPer(roundTwoDecimals(sgstPer));
                            addToCart.setCessAmnt(roundTwoDecimals(0));
                            addToCart.setCessPer(roundTwoDecimals(0));
                            addToCart.setIgstAmnt(roundTwoDecimals(0));
                            cgstPerStr = roundTwoDecimals(cgstPer);
                            sgstPerStr = roundTwoDecimals(sgstPer);
                            totCGSTAmnt = totCGSTAmnt + cgstAmt;
                            totSGSTAmnt = totSGSTAmnt + sgstAmt;

                        }else if(productClass.getGstType().equals("E")){
                            float taxableRate = _rate;
                            float total = (taxableRate * qty1);
                            float billdiscPer = stringToFloat(ed_discPer.getText().toString());
                            float billDiscAmnt = (total * billdiscPer)/100;
                            float disctedTotal = total - billDiscAmnt;
                            float totalGST = (disctedTotal * gstPer)/100;
                            float cgstAmt = (disctedTotal * cgstPer) / 100;
                            float sgstAmt = (disctedTotal * sgstPer) / 100;
                            float netAmt = billDiscAmnt + cgstAmt + sgstAmt;
                            totAmnt = totAmnt + total;
                            addToCart.setTotal(roundTwoDecimals(total));
                            addToCart.setBillDiscPer(roundTwoDecimals(billdiscPer));
                            addToCart.setBillDiscAmnt(roundTwoDecimals(billDiscAmnt));
                            addToCart.setTaxableRate(roundTwoDecimals(taxableRate));
                            addToCart.setGstPer(roundTwoDecimals(gstPer));
                            addToCart.setCgstAmnt(roundTwoDecimals(cgstAmt));
                            addToCart.setSgstAmnt(roundTwoDecimals(sgstAmt));
                            addToCart.setCgstPer(roundTwoDecimals(cgstPer));
                            addToCart.setSgstPer(roundTwoDecimals(sgstPer));
                            addToCart.setCessAmnt(roundTwoDecimals(0));
                            addToCart.setCessPer(roundTwoDecimals(0));
                            addToCart.setIgstAmnt(roundTwoDecimals(0));
                            cgstPerStr = roundTwoDecimals(cgstPer);
                            sgstPerStr = roundTwoDecimals(sgstPer);
                            totCGSTAmnt = totCGSTAmnt + cgstAmt;
                            totSGSTAmnt = totSGSTAmnt + sgstAmt;
                        }
                        addToCart.setQty(stringToInt(qty));
                        addToCart.setAmnt(roundTwoDecimals(_total));
                        cartList.add(addToCart);
                        if(cartList.size()==1) {
                            adapter = new AddToCartRecyclerAdapter(getApplicationContext(), cartList);
                            adapter.setOnClickListener1(this);
                            rv_Order.setAdapter(adapter);
                        }else{
                            adapter.notifyDataSetChanged();
                        }
                        rv_Order.scrollToPosition(cartList.size()-1);
                    } else {
                        toast.setText("Please Enter Quantity");
                        toast.show();
                    }
                } else {
                    toast.setText("Please Enter Amnt");
                    toast.show();
                }
            }else{
                toast.setText("Please Select Rate First");
                toast.show();
            }
        }else{
            toast.setText("Please Select Product First");
            toast.show();
        }
    }

    private void setItemwiseDiscount() {
        String qty = ed_Qty.getText().toString();
        String rate = ed_rate.getText().toString();
        cartList.clear();
        rv_Order.setAdapter(null);
        for(ProductClass _productClass : prodList) {
            AddToCartClass addToCart = new AddToCartClass();
            addToCart.setItemId(_productClass.getProduct_ID());
            addToCart.setProdName(_productClass.getProduct_Name());
            addToCart.setRate(roundTwoDecimals(String.valueOf(_productClass.getProduct_Rate())));
            addToCart.setMrp(roundTwoDecimals(_productClass.getMrp()));
            addToCart.setAmnt(roundTwoDecimals(_productClass.getSsp()));
            addToCart.setActRate(roundTwoDecimals(_productClass.getSsp()));
            addToCart.setActMRP(roundTwoDecimals(_productClass.getMrp()));
            addToCart.setFatherSKU(_productClass.getFinalProduct());
            addToCart.setDispFSKU(_productClass.getDispFSKU());
            addToCart.setGstGroup(_productClass.getGstGroup());
            addToCart.setHsnCode(_productClass.getHsnCode());

            Cursor cursor = db.getGSTPer(_productClass.getGstGroup());
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

            if (_productClass.getGstType().equals("I")) {
                float accValue = ((gstPer * 100) / (gstPer + 100));
                float gstAmnt = (_rate * accValue) / 100;
                float taxableRate = _rate - gstAmnt;
                float total = (taxableRate * qty1);
                float billdiscPer = stringToFloat(ed_discPer.getText().toString());
                float billDiscAmnt = (total * billdiscPer) / 100;
                float disctedTotal = total - billDiscAmnt;
                float totalGST = (disctedTotal * gstPer) / 100;
                float cgstAmt = (disctedTotal * cgstPer) / 100;
                float sgstAmt = (disctedTotal * sgstPer) / 100;
                float netAmt = billDiscAmnt + cgstAmt + sgstAmt;
                totAmnt = totAmnt + total;
                addToCart.setTotal(roundTwoDecimals(total));
                addToCart.setBillDiscPer(roundTwoDecimals(billdiscPer));
                addToCart.setBillDiscAmnt(roundTwoDecimals(billDiscAmnt));
                addToCart.setTaxableRate(roundTwoDecimals(taxableRate));
                addToCart.setGstPer(roundTwoDecimals(gstPer));
                addToCart.setCgstAmnt(roundTwoDecimals(cgstAmt));
                addToCart.setSgstAmnt(roundTwoDecimals(sgstAmt));
                addToCart.setCgstPer(roundTwoDecimals(cgstPer));
                addToCart.setSgstPer(roundTwoDecimals(sgstPer));
                addToCart.setCessAmnt(roundTwoDecimals(0));
                addToCart.setCessPer(roundTwoDecimals(0));
                addToCart.setIgstAmnt(roundTwoDecimals(0));
                cgstPerStr = roundTwoDecimals(cgstPer);
                sgstPerStr = roundTwoDecimals(sgstPer);
                totCGSTAmnt = totCGSTAmnt + cgstAmt;
                totSGSTAmnt = totSGSTAmnt + sgstAmt;

            } else if (_productClass.getGstType().equals("E")) {
                float taxableRate = _rate;
                float total = (taxableRate * qty1);
                float billdiscPer = stringToFloat(ed_discPer.getText().toString());
                float billDiscAmnt = (total * billdiscPer) / 100;
                float disctedTotal = total - billDiscAmnt;
                float totalGST = (disctedTotal * gstPer) / 100;
                float cgstAmt = (disctedTotal * cgstPer) / 100;
                float sgstAmt = (disctedTotal * sgstPer) / 100;
                float netAmt = billDiscAmnt + cgstAmt + sgstAmt;
                totAmnt = totAmnt + total;
                addToCart.setTotal(roundTwoDecimals(total));
                addToCart.setBillDiscPer(roundTwoDecimals(billdiscPer));
                addToCart.setBillDiscAmnt(roundTwoDecimals(billDiscAmnt));
                addToCart.setTaxableRate(roundTwoDecimals(taxableRate));
                addToCart.setGstPer(roundTwoDecimals(gstPer));
                addToCart.setCgstAmnt(roundTwoDecimals(cgstAmt));
                addToCart.setSgstAmnt(roundTwoDecimals(sgstAmt));
                addToCart.setCgstPer(roundTwoDecimals(cgstPer));
                addToCart.setSgstPer(roundTwoDecimals(sgstPer));
                addToCart.setCessAmnt(roundTwoDecimals(0));
                addToCart.setCessPer(roundTwoDecimals(0));
                addToCart.setIgstAmnt(roundTwoDecimals(0));
                cgstPerStr = roundTwoDecimals(cgstPer);
                sgstPerStr = roundTwoDecimals(sgstPer);
                totCGSTAmnt = totCGSTAmnt + cgstAmt;
                totSGSTAmnt = totSGSTAmnt + sgstAmt;
            }
            addToCart.setQty(stringToInt(qty));
            addToCart.setAmnt(roundTwoDecimals(_total));
            cartList.add(addToCart);
            if (cartList.size() == 1) {
                adapter = new AddToCartRecyclerAdapter(getApplicationContext(), cartList);
                adapter.setOnClickListener1(this);
                rv_Order.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
            rv_Order.scrollToPosition(cartList.size() - 1);
        }
    }

    private void saveBill() {
        int jobCardId = 0, creadtedBy = 1, modifiedBy=0, bankID = 0, printno = 1,
                GVoucher=0, agentid = 0, GVSchemeId = 0, CancelledBy=0, Deliveredby=0, Currencyid = 0, Type = 1,
                BothId = 0, NewDCNo = 0;

        float totalQty=0, totalAmnt=0, creditAmnt=0, cashAmnt=0, paidAmnt=0, balAmnt = 0, vat12 = 0, vat4 = 0, labour=0, cheqAmnt = 0,
                advanceamt=0, netamt=0, commInPer = 0, commInRs = 0, disper = 0, disamt = 0, piamt = 0, GVoucherAmt=0,
                GVAmt = 0, GrossAmount=0, Alteration = 0, CashBack = 0, SchemeAmt = 0, GoodsReturn = 0, RemainAmt=0,
                TRetQty = 0, TotalCurrency = 0, CGSTAMT=0, SGSTAMT=0, IGSTAMT = 0;

        String finyr, custId , retMemoNo="", breakUpAmnt="", billSt = "A", vehicleNo="", modifieDdt="",
                vehicleMake="", vehicleColor="", driverName="", cardNo="", cheqNo="", cheqDate="", paymenttype = "A",
                billpayst = "Y", inwrds="", refundpyst = "N", pino = "0", mon, vouchergenerate = "N", Scheme="", GV="",
                GVNo="", IssueGVSt = "N", Createdfrm = "C", CancelledDate="", Billingtime, Delivered = "Y",
                DeliveredDate = "", CounterNo = "Mob",machineName = new Constant(getApplicationContext()).getIMEINo1(),
                AgainstDC = "N", DCAutoNo = "0", CancelReason="",Tender="", CurrencyAmt = "0", CardBank="",
                CardTyp="", SwipReceiptNo="", Reference="", Repl_Column = "Normal",Through="", Authorised="",
                Remark="", TheirRemark="", IGSTAPP = "N", NewAgainstDC = "N";

        float rate=0, qty=0, total=0, incentamt=0, vat = 0, vatamt = 0, amtWithoutDisc=0, detdisper = 0, detdisamt = 0,
                ratewithtax=0,purchaseQty = 0, freeqty = 0, Mandalper = 0, Mandal = 0, MRP=0,
                billdisper = 0, billdisamt = 0, RetQty = 0,ActRate=0, ActMRP=0, SchmBenefit = 0, DetGSTPER=0,
                DetCGSTAMT = 0, DetSGSTAMT = 0, DetCGSTPER = 0, DetSGSTPER = 0,
                DetCESSPER = 0, DetCESSAMT = 0, DetIGSTAMT = 0, TaxableAmt = 0, RetAmt = 0;

        int branchID = 1, empID=1, DetDeliveredby = 0, Allotid = 0, Schemeid = 0, dcmastauto = 0,
                DetType = 1, seqno = 0;

        String finYr, barcode="", fatherSKU="", detMon, AlteredStat = "N", DetDelivered = "Y",
                DetDelivereddate="",nonBar = "0",BGType = "N", SchemeApp = "N", DispFSKU="", DesignNo="";

        float totcgstAmt = 0;
        float totsgstAmt = 0;

        String date1 = new Constant(getApplicationContext()).getDate();
        String billDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        Billingtime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String crDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String[] dateArr = date1.split("/");
        String year = dateArr[2];
        int y = Integer.parseInt(year.substring(2, 4));
        finyr = finYr = y + "-" + String.valueOf(y + 1);
        DateFormat dateFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
        Date date = new Date();
        mon = dateFormat.format(date);
        detMon = dateFormat.format(date);
        Constant.showLog(mon);

        int mastAuto = db.getMaxAuto();
        int mastId = db.getMaxMastId(finyr);
        billNo = db.getCompIni() + finyr + mastId;
        totalQty = totQty;
        totalAmnt = stringToFloat(tv_totalAmnt.getText().toString());
        paidAmnt = totalAmnt;
        netamt = Float.parseFloat(String.valueOf(tv_netAmnt.getText().toString()));
        GrossAmount = netamt;
        Tender = roundTwoDecimals(ed_cash.getText().toString());
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
            creditAmnt = 0;
            GVAmt = 0;
        }else if(paymenttype.equalsIgnoreCase("Card") || paymenttype.equalsIgnoreCase("Credit")
                || paymenttype.equalsIgnoreCase("Credit Card") || paymenttype.contains("CreditCard")){
            cashAmnt = 0;
            creditAmnt = Float.parseFloat(ed_cash.getText().toString());
            GVAmt = 0;
        }else {
            cashAmnt = 0;
            creditAmnt = 0;
            GVAmt = Float.parseFloat(ed_cash.getText().toString());
        }

        BillMasterClass mast = new BillMasterClass();
        mast.setAutoNo(mastAuto);
        mast.setId(mastId);
        mast.setBranchID(branchID);
        mast.setFinYr(finYr);
        mast.setBillNo(billNo);
        mast.setCustID(custId);
        mast.setJobCardID(jobCardId);
        mast.setBillDate(billDate);
        mast.setTotalQty(roundTwoDecimals(totalQty));
        mast.setTotalAmt(roundTwoDecimals(totalAmnt));
        mast.setRetMemoNo(retMemoNo);
        mast.setReturnAmt(roundTwoDecimals(RetAmt));
        mast.setCreditAmt(roundTwoDecimals(creditAmnt));
        mast.setCashAmt(roundTwoDecimals(cashAmnt));
        mast.setPaidAmt(roundTwoDecimals(paidAmnt));
        mast.setBalAmt(roundTwoDecimals(balAmnt));
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
        mast.setVat12(roundTwoDecimals(vat12));
        mast.setVat4(roundTwoDecimals(vat4));
        mast.setLabour(roundTwoDecimals(labour));
        mast.setCardno(cardNo);
        mast.setChqno(cheqNo);
        mast.setChqamt(roundTwoDecimals(cheqAmnt));
        mast.setChqdt(cheqDate);
        mast.setAdvanceamt(roundTwoDecimals(advanceamt));
        mast.setPaymenttype(paymenttype);
        mast.setBillpayst(billpayst);
        mast.setNetamt(roundTwoDecimals(netamt));
        mast.setBankID(bankID);
        mast.setCommInPer(roundTwoDecimals(commInPer));
        mast.setCommInRs(roundTwoDecimals(commInRs));
        mast.setPrintno(printno);
        mast.setDisper(roundTwoDecimals(disper));
        mast.setDisamt(roundTwoDecimals(disamt));
        mast.setInwrds(inwrds);
        mast.setRefundpyst(refundpyst);
        mast.setPino(pino);
        mast.setPiamt(roundTwoDecimals(piamt));
        mast.setMon(mon);
        mast.setGVoucher(GVoucher);
        mast.setGVoucherAmt(roundTwoDecimals(GVoucherAmt));
        mast.setAgentid(agentid);
        mast.setVouchergenerate(vouchergenerate);
        mast.setGVSchemeId(GVSchemeId);
        mast.setScheme(Scheme);
        mast.setGV(GV);
        mast.setGVAmt(roundTwoDecimals(GVAmt));
        mast.setGVNo(GVNo);
        mast.setIssueGVSt(IssueGVSt);
        mast.setCreatedfrm(Createdfrm);
        mast.setGrossAmount(roundTwoDecimals(GrossAmount));
        mast.setCancelledBy(CancelledBy);
        mast.setCancelledDate(CancelledDate);
        mast.setBillingtime(Billingtime);
        mast.setDelivered(Delivered);
        mast.setDeliveredby(Deliveredby);
        mast.setDeliveredDate(DeliveredDate);
        mast.setAlteration(roundTwoDecimals(Alteration));
        mast.setCashBack(roundTwoDecimals(CashBack));
        mast.setSchemeAmt(roundTwoDecimals(SchemeAmt));
        mast.setGoodsReturn(roundTwoDecimals(GoodsReturn));
        mast.setCounterNo(CounterNo);
        mast.setMachineName(machineName);
        mast.setAgainstDC(AgainstDC);
        mast.setDCAutoNo(DCAutoNo);
        mast.setCancelReason(CancelReason);
        mast.setTender(Tender);
        mast.setRemainAmt(roundTwoDecimals(RemainAmt));
        mast.setTRetQty(roundTwoDecimals(TRetQty));
        mast.setCurrencyid(Currencyid);
        mast.setTotalCurrency(roundTwoDecimals(TotalCurrency));
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
        mast.setCGSTAMT(roundTwoDecimals(totCGSTAmnt));
        mast.setSGSTAMT(roundTwoDecimals(totSGSTAmnt));
        mast.setIGSTAPP(IGSTAPP);
        mast.setIGSTAMT(roundTwoDecimals(IGSTAMT));
        mast.setType(Type);
        mast.setBothId(BothId);
        mast.setNewAgainstDC(NewAgainstDC);
        mast.setNewDCNo(NewDCNo);
        db.saveBillMaster(mast);

        for (AddToCartClass cart : cartList) {
            BillDetailClass det = new BillDetailClass();
            int detAuto = db.getMaxDetAuto();
            int detId = db.getMaxDetId(mastAuto);
            det.setAuto(detAuto);
            det.setId(detId);
            det.setBillID(mastId);
            det.setBranchID(branchID);
            det.setFinYr(finYr);
            det.setItemId(cart.getItemId());
            det.setRate(cart.getRate());
            det.setQty(String.valueOf(cart.getQty()));
            det.setTotal(cart.getTotal());
            det.setBarcode(String.valueOf(cart.getItemId()));
            det.setEmpID(empID);
            det.setIncentamt(roundTwoDecimals(incentamt));
            det.setFatherSKU(cart.getFatherSKU());
            det.setAutoBillId(mastAuto);
            det.setVat(roundTwoDecimals(vat));
            det.setVatamt(roundTwoDecimals(vatamt));
            det.setAmtWithoutDisc(cart.getAmnt());
            det.setDisper(roundTwoDecimals(detdisper));
            det.setDisamt(roundTwoDecimals(detdisamt));
            det.setNonBar(nonBar);
            det.setMon(detMon);
            det.setRatewithtax(cart.getRate());
            det.setPurchaseQty(String.valueOf(cart.getQty()));
            det.setFreeqty(roundTwoDecimals(freeqty));
            det.setAlteredStat(AlteredStat);
            det.setMandalper(roundTwoDecimals(Mandalper));
            det.setMandal(roundTwoDecimals(Mandal));
            det.setDelivered(Delivered);
            det.setDeliveredby(DetDeliveredby);
            det.setDelivereddate(DeliveredDate);
            det.setBGType(BGType);
            det.setAllotid(Allotid);
            det.setSchemeApp(SchemeApp);
            det.setSchemeid(Schemeid);
            det.setDispFSKU(cart.getDispFSKU());
            det.setMRP(cart.getMrp());
            det.setBilldisper(cart.getBillDiscPer());
            det.setBilldisamt(cart.getBillDiscAmnt());
            det.setDcmastauto(dcmastauto);
            det.setDesignNo(cart.getDesignNo());
            det.setRetQty(String.valueOf(RetQty));
            det.setActRate(cart.getActRate());
            det.setActMRP(cart.getActMRP());
            det.setSchmBenefit(roundTwoDecimals(SchmBenefit));
            det.setGSTPER(cart.getGstPer());
            det.setCGSTAMT(cart.getCgstAmnt());
            det.setSGSTAMT(cart.getSgstAmnt());
            det.setCGSTPER(cart.getCgstPer());
            det.setSGSTPER(cart.getSgstPer());
            det.setCESSPER(cart.getCessPer());
            det.setCESSAMT(cart.getCessAmnt());
            det.setIGSTAMT(cart.getIgstAmnt());
            det.setTaxableAmt(cart.getTaxableRate());
            det.setType(Type);
            det.setSeqno(seqno);
            db.saveBillDetail(det);
        }

        showDia(2);
    }

    private int stringToInt(String value){
        return Integer.parseInt(value);
    }

    private float stringToFloat(String value){
        return Float.parseFloat(value);
    }

    private void setSpinnerData(){
        Cursor res = db.getPaymentType();
        List<String> list = new ArrayList<>();
        if(res.moveToFirst()){
            do{
                list.add(res.getString(1));
            }while (res.moveToNext());
        }
        res.close();
        sp_paymentType.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.list_item_option_setttings,list));

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
                    clearField();
                    dialog.dismiss();
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
                    clearField();
                    dialog.dismiss();
                }
            });
        }
        builder.create().show();
    }

    private void clearField(){
        setProdData();
        setRateList();
        totQty = 0;
        totAmnt = 0;
        totCGSTAmnt = 0;
        totSGSTAmnt = 0;
        cgstPerStr = "0";
        sgstPerStr = "0";
        ed_Qty.setText("1");
        ed_rate.setText(null);
        cartList.clear();
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
        ed_custMobNo.setText(null);
        tv_retAmnt.setText("0");
        tv_netAmnt.setText("0");
        sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        sp_paymentType.setSelection(0);
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
                    if (rate.length() == 4) {
                        rate = "   " + rate;
                    } else if (rate.length() == 5) {
                        rate = "  " + rate;
                    }else if (rate.length() == 6) {
                        rate = " " + rate;
                    }

                    String amnt = String.valueOf(cart.getAmnt());
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
