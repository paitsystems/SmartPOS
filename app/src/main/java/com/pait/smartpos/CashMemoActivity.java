package com.pait.smartpos;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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

import com.pait.smartpos.adpaters.AddToCartRecyclerAdapter;
import com.pait.smartpos.adpaters.AllProductAdapter;
import com.pait.smartpos.adpaters.AllRateAdapter;
import com.pait.smartpos.adpaters.ProductListRecyclerAdapter;
import com.pait.smartpos.adpaters.ProductRateRecyclerAdapter;
import com.pait.smartpos.constant.Constant;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CashMemoActivity extends AppCompatActivity implements View.OnClickListener, RecyclerViewToActivityInterface {

    private RecyclerView rv_Prod, rv_Price, rv_Order;
    private TextView tv_prodAll, tv_priceAll, tv_remove, tv_add, tv_totalQty, tv_totalAmnt, tv_retAmnt, tv_netAmnt;
    private EditText ed_Qty, ed_Amnt, ed_cash, ed_cardNo, ed_custName, ed_custMobNo, ed_remark;
    private Button btn_Add, btn_Save;
    private Spinner sp_paymentType;
    private AutoCompleteTextView auto_CustName;
    private Constant constant;
    private DBHandler db;
    private Toast toast;
    private List<ProductClass> prodList;
    private List<RateMasterClass> rateList;
    private List<AddToCartClass> cartList;
    private List<String> custList;
    private float rate = 0, totQty = 0, totAmnt = 0;
    private ProductClass productClass = null;
    private RateMasterClass rateMasterClass = null;
    private AddToCartRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_memo);
        init();
        setProdData();
        setSpinnerData();
        setCustData();
        sp_paymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String str = (String) adapterView.getItemAtPosition(i);
                Constant.showLog(str);
                if(str.contains("Credit")){
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
                String qty = ed_Qty.getText().toString();
                String amnt = ed_Amnt.getText().toString();
                if(!amnt.equals("")) {
                    if (!qty.equals("")) {
                        int value = stringToInt(qty);
                        if (value > 0) {
                            float _rate = rate * value;
                            ed_Amnt.setText(String.valueOf(_rate));
                        } else {
                            toast.setText("Qty Can Not Be Zero");
                            toast.show();
                        }
                    }
                }else {
                    toast.setText("Please Select Rate First");
                    toast.show();
                }
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
                    float retAmt = totAmnt - cashAmt;
                    tv_retAmnt.setText(String.valueOf(retAmt));
                } else {
                    ed_cash.setText("0");
                    ed_cash.setSelection(0);
                    tv_retAmnt.setText("0");
                }
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
        ed_Amnt.setText(String.valueOf(_rate.getRate()));
    }

    @Override
    public void calculation(float qty, float amnt) {
        tv_totalQty.setText(String.valueOf(totQty));
        tv_totalAmnt.setText(String.valueOf(totAmnt));
        ed_cash.setText(String.valueOf(totAmnt));
        tv_netAmnt.setText(String.valueOf(totAmnt));
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
        ed_Amnt = findViewById(R.id.ed_amnt);
        ed_cardNo = findViewById(R.id.ed_cardNo);
        ed_custName = findViewById(R.id.ed_custName);
        ed_custMobNo = findViewById(R.id.ed_custMobNo);
        ed_remark = findViewById(R.id.ed_remark);
        btn_Add = findViewById(R.id.btn_add);
        btn_Save = findViewById(R.id.btn_save);
        auto_CustName = findViewById(R.id.auto_Cust);

        tv_prodAll.setOnClickListener(this);
        tv_priceAll.setOnClickListener(this);
        tv_remove.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        tv_remove.setOnClickListener(this);
        btn_Add.setOnClickListener(this);
        btn_Save.setOnClickListener(this);

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
                prodList.add(prod);
            } while (res.moveToNext());
        }
        res.close();

        for(int i=0;i<11;i++){
            RateMasterClass rate = new RateMasterClass();
            rate.setRate(1000 + i);
            rate.setSelected(false);
            rateList.add(rate);
        }
        ProductListRecyclerAdapter adapter = new ProductListRecyclerAdapter(getApplicationContext(),prodList);
        adapter.setOnClickListener1(this);
        rv_Prod.setAdapter(adapter);
        ProductRateRecyclerAdapter adapter1 = new ProductRateRecyclerAdapter(getApplicationContext(),rateList);
        adapter1.setOnClickListener1(this);
        rv_Price.setAdapter(adapter1);

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
            // do something here
        });
        // Set grid view to alertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(gridView);
        builder.setTitle("All Product");
        builder.show();
    }

    private void showRateAlertDialog() {
        GridView gridView = new GridView(this);
        gridView.setAdapter(new AllRateAdapter(getApplicationContext(),rateList));
        gridView.setNumColumns(5);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            // do something here
        });
        // Set grid view to alertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(gridView);
        builder.setTitle("All Rate");
        builder.show();
    }

    private void removeQty(){
        String qty = ed_Qty.getText().toString();
        String amnt = ed_Amnt.getText().toString();
        if(!amnt.equals("")) {
            if (!qty.equals("")) {
                int value = stringToInt(qty);
                if (value > 1) {
                    value = value - 1;
                    float _rate = rate * value;
                    ed_Qty.setText(String.valueOf(value));
                    ed_Amnt.setText(String.valueOf(_rate));
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
        String amnt = ed_Amnt.getText().toString();
        if (!amnt.equals("")) {
            if (!qty.equals("")) {
                int value = stringToInt(qty);
                if (value >= 0) {
                    value = value + 1;
                    float _rate = rate * value;
                    ed_Qty.setText(String.valueOf(value));
                    ed_Amnt.setText(String.valueOf(_rate));
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
                String amnt = ed_Amnt.getText().toString();
                if (!amnt.equals("")) {
                    if (!qty.equals("")) {
                        AddToCartClass addToCart = new AddToCartClass();
                        addToCart.setItemId(productClass.getProduct_ID());
                        addToCart.setProdName(productClass.getProduct_Name());
                        addToCart.setMrp(productClass.getMrp());
                        addToCart.setAmnt(productClass.getSsp());
                        addToCart.setActRate(productClass.getSsp());
                        addToCart.setActMRP(productClass.getMrp());
                        addToCart.setFatherSKU(productClass.getFinalProduct());
                        addToCart.setDispFSKU(productClass.getDispFSKU());
                        addToCart.setGstGroup(productClass.getGstGroup());
                        addToCart.setHsnCode(productClass.getHsnCode());
                        addToCart.setAmnt(Float.parseFloat(amnt));
                        addToCart.setTotal(Float.parseFloat(amnt));

                        addToCart.setQty(Float.parseFloat(qty));
                        addToCart.setRate(rateMasterClass.getRate());
                        totQty = totQty + stringToFloat(qty);
                        totAmnt = totAmnt + Float.parseFloat(amnt);
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

    private void saveBill() {
        int jobCardId = 0, creadtedBy = 1, modifiedBy=0, bankID = 0, printno = 1,
                GVoucher=0, agentid = 0, GVSchemeId = 0, CancelledBy=0, Deliveredby=0, Currencyid = 0, Type = 1,
                BothId = 0, NewDCNo = 0;

        float totalQty=0, totalAmnt=0, creditAmnt=0, cashAmnt=0, paidAmnt=0, balAmnt = 0, vat12 = 0, vat4 = 0, labour=0, cheqAmnt = 0,
                advanceamt=0, netamt=0, commInPer = 0, commInRs = 0, disper = 0, disamt = 0, piamt = 0, GVoucherAmt=0,
                GVAmt = 0, GrossAmount=0, Alteration = 0, CashBack = 0, SchemeAmt = 0, GoodsReturn = 0, RemainAmt=0,
                TRetQty = 0, TotalCurrency = 0, CGSTAMT=0, SGSTAMT=0, IGSTAMT = 0;

        String finyr, custId = "1#1", retMemoNo="", breakUpAmnt="", billSt = "A", vehicleNo="", modifieDdt="",
                vehicleMake="", vehicleColor="", driverName="", cardNo="", cheqNo="", cheqDate="", paymenttype = "A",
                billpayst = "Y", inwrds="", refundpyst = "N", pino = "0", mon, vouchergenerate = "N", Scheme="", GV="",
                GVNo="", IssueGVSt = "N", Createdfrm = "C", CancelledDate="", Billingtime, Delivered = "Y",
                DeliveredDate = "", CounterNo = "Mob",machineName = new Constant(getApplicationContext()).getIMEINo1(),
                AgainstDC = "N", DCAutoNo = "0", CancelReason="",Tender="", CurrencyAmt = "0", CardBank="",
                CardTyp="", SwipReceiptNo="", Reference="", Repl_Column = "Normal",Through="", Authorised="",
                Remark="", TheirRemark="", IGSTAPP = "N", NewAgainstDC = "N";

        float rate=0, qty=0, total=0, incentamt=0, vat = 0, vatamt = 0, amtWithoutDisc=0, detdisper = 0, detdisamt = 0,
                nonBar = 0, ratewithtax=0,purchaseQty = 0, freeqty = 0, Mandalper = 0, Mandal = 0, MRP=0,
                billdisper = 0, billdisamt = 0, RetQty = 0,ActRate=0, ActMRP=0, SchmBenefit = 0, DetGSTPER=0,
                DetCGSTAMT = 0, DetSGSTAMT = 0, DetCGSTPER = 0, DetSGSTPER = 0,
                DetCESSPER = 0, DetCESSAMT = 0, DetIGSTAMT = 0, TaxableAmt = 0;

        int branchID = 1, empID=1, DetDeliveredby = 0, Allotid = 0, Schemeid = 0, dcmastauto = 0,
                DetType = 1, seqno = 0;

        String finYr, barcode="", fatherSKU="", detMon, AlteredStat = "N", DetDelivered = "Y", DetDelivereddate="",
                BGType = "N", SchemeApp = "N", DispFSKU="", DesignNo="";

        float retAmt = Float.parseFloat(tv_retAmnt.getText().toString());
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
        String billNo = "PA" + finyr + mastId;
        totalQty = totQty;
        totalAmnt = totAmnt;
        cashAmnt = Float.parseFloat(ed_cash.getText().toString());
        creditAmnt = 0;
        paidAmnt = cashAmnt;
        netamt = Float.parseFloat(String.valueOf(tv_netAmnt.getText().toString()));
        GrossAmount = netamt;
        Tender = "0";
        RemainAmt = 0;
        retAmt = Float.parseFloat(String.valueOf(tv_retAmnt.getText().toString()));

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
            det.setQty(cart.getQty());
            det.setTotal(cart.getTotal());
            det.setBarcode(cart.getBarcode());
            det.setEmpID(empID);
            det.setIncentamt(incentamt);
            det.setFatherSKU(cart.getFatherSKU());
            det.setAutoBillId(mastAuto);
            det.setVat(vat);
            det.setVatamt(vatamt);
            det.setAmtWithoutDisc(cart.getAmnt());
            det.setDisper(detdisper);
            det.setDisamt(detdisamt);
            det.setNonBar(nonBar);
            det.setMon(detMon);
            det.setRatewithtax(cart.getRate());
            det.setPurchaseQty(purchaseQty);
            det.setFreeqty(freeqty);
            det.setAlteredStat(AlteredStat);
            det.setMandalper(Mandalper);
            det.setMandal(Mandal);
            det.setDelivered(Delivered);
            det.setDeliveredby(DetDeliveredby);
            det.setDelivereddate(DeliveredDate);
            det.setBGType(BGType);
            det.setAllotid(Allotid);
            det.setSchemeApp(SchemeApp);
            det.setSchemeid(Schemeid);
            det.setDispFSKU(cart.getDispFSKU());
            det.setMRP(cart.getMrp());
            det.setBilldisper(billdisper);
            det.setBilldisamt(billdisamt);
            det.setDcmastauto(dcmastauto);
            det.setDesignNo(cart.getDesignNo());
            det.setRetQty(RetQty);
            det.setActRate(cart.getActRate());
            det.setActMRP(cart.getActMRP());
            det.setSchmBenefit(SchmBenefit);
            det.setGSTPER(DetCGSTPER);
            det.setCGSTAMT(DetCGSTAMT);
            det.setSGSTAMT(DetSGSTAMT);
            det.setCGSTPER(DetCGSTPER);
            det.setSGSTPER(DetSGSTPER);
            det.setCESSPER(DetCESSPER);
            det.setCESSAMT(DetCESSAMT);
            det.setIGSTAMT(IGSTAMT);
            det.setTaxableAmt(TaxableAmt);
            det.setType(Type);
            det.setSeqno(seqno);
            db.saveBillDetail(det);
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
        mast.setTotalQty(totalQty);
        mast.setTotalAmt(totalAmnt);
        mast.setRetMemoNo(retMemoNo);
        mast.setReturnAmt(retAmt);
        mast.setCreditAmt(creditAmnt);
        mast.setCashAmt(cashAmnt);
        mast.setPaidAmt(paidAmnt);
        mast.setBalAmt(balAmnt);
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
        mast.setVat12(vat12);
        mast.setVat4(vat4);
        mast.setLabour(labour);
        mast.setCardno(cardNo);
        mast.setChqno(cheqNo);
        mast.setChqamt(cheqAmnt);
        mast.setChqdt(cheqDate);
        mast.setAdvanceamt(advanceamt);
        mast.setPaymenttype(paymenttype);
        mast.setBillpayst(billpayst);
        mast.setNetamt(netamt);
        mast.setBankID(bankID);
        mast.setCommInPer(commInPer);
        mast.setCommInRs(commInRs);
        mast.setPrintno(printno);
        mast.setDisper(disper);
        mast.setDisamt(disamt);
        mast.setInwrds(inwrds);
        mast.setRefundpyst(refundpyst);
        mast.setPino(pino);
        mast.setPiamt(piamt);
        mast.setMon(mon);
        mast.setGVoucher(GVoucher);
        mast.setGVoucherAmt(GVoucherAmt);
        mast.setAgentid(agentid);
        mast.setVouchergenerate(vouchergenerate);
        mast.setGVSchemeId(GVSchemeId);
        mast.setScheme(Scheme);
        mast.setGV(GV);
        mast.setGVAmt(GVAmt);
        mast.setGVNo(GVNo);
        mast.setIssueGVSt(IssueGVSt);
        mast.setCreatedfrm(Createdfrm);
        mast.setGrossAmount(GrossAmount);
        mast.setCancelledBy(CancelledBy);
        mast.setCancelledDate(CancelledDate);
        mast.setBillingtime(Billingtime);
        mast.setDelivered(Delivered);
        mast.setDeliveredby(Deliveredby);
        mast.setDeliveredDate(DeliveredDate);
        mast.setAlteration(Alteration);
        mast.setCashBack(CashBack);
        mast.setSchemeAmt(SchemeAmt);
        mast.setGoodsReturn(GoodsReturn);
        mast.setCounterNo(CounterNo);
        mast.setMachineName(machineName);
        mast.setAgainstDC(AgainstDC);
        mast.setDCAutoNo(DCAutoNo);
        mast.setCancelReason(CancelReason);
        mast.setTender(Tender);
        mast.setRemainAmt(RemainAmt);
        mast.setTRetQty(TRetQty);
        mast.setCurrencyid(Currencyid);
        mast.setTotalCurrency(TotalCurrency);
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
        mast.setCGSTAMT(CGSTAMT);
        mast.setSGSTAMT(SGSTAMT);
        mast.setIGSTAPP(IGSTAPP);
        mast.setIGSTAMT(IGSTAMT);
        mast.setType(Type);
        mast.setBothId(BothId);
        mast.setNewAgainstDC(NewAgainstDC);
        mast.setNewDCNo(NewDCNo);
        db.saveBillMaster(mast);
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
        }
        builder.create().show();
    }

}
