package com.pait.smartpos;

import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pait.smartpos.adpaters.AddToCartRecyclerAdapter;
import com.pait.smartpos.adpaters.CheckableSpinnerAdapter;
import com.pait.smartpos.adpaters.CustomSpinnerAdapter;
import com.pait.smartpos.adpaters.InwardRecyclerViewAdapter;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.interfaces.RecyclerViewToActivityInterface;
import com.pait.smartpos.interfaces.checkBoxListener;
import com.pait.smartpos.model.AddToCartClass;
import com.pait.smartpos.model.CustomSpinnerClass;
import com.pait.smartpos.model.InwardDetailClass;
import com.pait.smartpos.model.InwardMasterClass;
import com.pait.smartpos.model.ProductClass;
import com.pait.smartpos.model.RateMasterClass;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class InwardActivity extends AppCompatActivity implements checkBoxListener, View.OnClickListener, RecyclerViewToActivityInterface {

    private AutoCompleteTextView auto_supplier, auto_product;
    private Spinner spinner;
    private List<String> prodList;
    private List<String> suppList;
    private ArrayList<CustomSpinnerClass> listVOs;
    private DBHandler db;
    private Button btn_add;
    private EditText ed_qty, ed_rate, ed_ssp, ed_discPer, ed_discAmnt, ed_otherAdd;
    private List<Integer> posLs;
    private CustomSpinnerAdapter myAdapter;
    private RecyclerView rv_data;
    private List<InwardDetailClass> cartLs;
    private List<InwardDetailClass> cartLsTempDisc;
    private TextView tv_gstgroup, tv_total_qty, tv_total_amount, tv_tot_cgst, tv_tot_sgst, tv_tot_igst, tv_final_amnt;
    private String supplier = "", inward_date = "", invoice_no = "", invoice_date = "", remark = "";
    private Toast toast;
    private String billNo, gstPerStr, cgstPerStr, sgstPerStr, gstgroup;
    private float rate = 0, totQty = 0, totAmnt = 0, totCGSTAmnt, totSGSTAmnt, totnetAmt, grossAmt;
    private AppCompatButton btn_cancel, btn_reset, btn_save;
    private SlidingUpPanelLayout sliding_layout;
    private String current_date = "", curr_time = "";
    private Constant constant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inward);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            //getSupportActionBar().setTitle();
        }
        init();

        supplier = getIntent().getExtras().getString("supplier");
        inward_date = getIntent().getExtras().getString("inward_date");
        invoice_no = getIntent().getExtras().getString("invoice_no");
        invoice_date = getIntent().getExtras().getString("invoice_date");
        remark = getIntent().getExtras().getString("remark");

        try {
            current_date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).parse(constant.getDate()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        auto_supplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auto_supplier.clearListSelection();
                auto_supplier.setThreshold(0);
                auto_supplier.showDropDown();
            }
        });

        auto_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auto_product.clearListSelection();
                auto_product.setThreshold(0);
                auto_product.showDropDown();
            }
        });

        auto_product.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tv_gstgroup.setText("");
                if (!auto_product.getText().toString().equals("")) {
                    String gstGroup = db.getGSTGroup(auto_product.getText().toString());
                    tv_gstgroup.setText(gstGroup);
                }
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
                    float disc = Float.parseFloat(ed_discPer.getText().toString());
                    float discAmt = (totAmnt * disc) / 100;
                    ed_discAmnt.setText(roundTwoDecimals(discAmt));

                    setItemwiseDiscount(roundTwoDecimals(discAmt));
                } else {
                    ed_discPer.setText("0");
                    ed_discAmnt.setText("0");
                    tv_tot_cgst.setText(String.valueOf(totCGSTAmnt));
                    tv_tot_sgst.setText(String.valueOf(totSGSTAmnt));
                    //tv_tot_cgst.setText(String.valueOf(totCGSTAmnt));
                }*/
            }
        });

        ed_otherAdd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().equals("-") && !charSequence.toString().equals("+")) {
                    finalCalculation();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                /*if (!ed_otherAdd.getText().toString().equals("")) {
                    float othAmnt = Float.parseFloat(ed_otherAdd.getText().toString());
                    float fina_tot_amnt = totAmnt - othAmnt;
                    tv_final_amnt.setText(roundDecimals(fina_tot_amnt));
                } else {
                    ed_otherAdd.setText("0");
                }*/
            }
        });

        btn_add.setOnClickListener(this::onClick);
        btn_save.setOnClickListener(this::onClick);
        btn_cancel.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                setData();
                break;
            case R.id.btn_save:
                saveInward();
                break;
            case R.id.btn_cancel:
                showDia(1);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setProduct();
        //setSupplier();
        // setSpinnerValueChecked();
    }

    @Override
    public void onBackPressed() {
        new Constant(InwardActivity.this).doFinish();
    }

    private void init() {
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        auto_supplier = findViewById(R.id.auto_supplier);
        auto_product = findViewById(R.id.auto_product);
        spinner = findViewById(R.id.sp_prod);
        prodList = new ArrayList<>();
        suppList = new ArrayList<>();
        listVOs = new ArrayList<>();
        db = new DBHandler(InwardActivity.this);
        btn_add = findViewById(R.id.btn_add);
        ed_qty = findViewById(R.id.ed_qty);
        ed_rate = findViewById(R.id.ed_rate);
        ed_ssp = findViewById(R.id.ed_ssp);
        posLs = new ArrayList<>();
        rv_data = findViewById(R.id.rv_data);
        cartLs = new ArrayList<>();
        cartLsTempDisc = new ArrayList<>();
        tv_gstgroup = findViewById(R.id.tv_gstgroup);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_reset = findViewById(R.id.btn_reset);
        btn_save = findViewById(R.id.btn_save);
        tv_tot_cgst = findViewById(R.id.tv_tot_cgst);
        tv_tot_sgst = findViewById(R.id.tv_tot_sgst);
        tv_tot_igst = findViewById(R.id.tv_tot_igst);
        tv_total_amount = findViewById(R.id.tv_total_amount);
        tv_total_qty = findViewById(R.id.tv_total_qty);
        ed_discAmnt = findViewById(R.id.ed_discAmnt);
        ed_discPer = findViewById(R.id.ed_discPer);
        ed_otherAdd = findViewById(R.id.ed_otherAdd);
        tv_final_amnt = findViewById(R.id.tv_final_amnt);
        sliding_layout = findViewById(R.id.sliding_layout);
        constant = new Constant(InwardActivity.this);
    }

    private void setSupplier() {
        Cursor res = db.getDistinctSupplier();
        if (res != null) {
            if (res.moveToFirst()) {
                do {
                    suppList.add(res.getString(res.getColumnIndex(DBHandler.SM_Name)));
                } while (res.moveToNext());
            }
        }
        res.close();
        ArrayAdapter adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.adapter_item, suppList);
        auto_supplier.setAdapter(adapter);
    }

    private void setProduct() {
        prodList.clear();
        auto_product.setAdapter(null);
        Cursor res = db.getDistinctProduct();
        if (res != null) {
            if (res.moveToFirst()) {
                do {
                    prodList.add(res.getString(res.getColumnIndex(DBHandler.PM_Finalproduct)));
                } while (res.moveToNext());
            }
        }
        res.close();
        ArrayAdapter adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.adapter_item, prodList);
        auto_product.setAdapter(adapter);

    }

    private void setSpinnerValueChecked() {
        final List<CheckableSpinnerAdapter.SpinnerItem<CustomSpinnerClass>> spinner_items = new ArrayList<>();
        final Set<CustomSpinnerClass> selected_items = new HashSet<>();
        prodList.clear();
        listVOs.clear();

        //prodList.add("Select Product");
        Cursor res = db.getDistinctProduct();
        if (res != null) {
            if (res.moveToFirst()) {
                do {
                    prodList.add(res.getString(res.getColumnIndex(DBHandler.PM_Cat3)));
                } while (res.moveToNext());
            }
        }
        res.close();

        for (String product : prodList) {
            CustomSpinnerClass stateVO = new CustomSpinnerClass();
            stateVO.setTitle(product);
            stateVO.setSelected(false);
            listVOs.add(stateVO);
        }

        //List<CustomSpinnerClass> all_objects = getMyObjects();
        for (CustomSpinnerClass o : listVOs) {
            spinner_items.add(new CheckableSpinnerAdapter.SpinnerItem<>(o, o.getTitle()));
        }

        String headerText = "Select Product";
        CheckableSpinnerAdapter adapter = new CheckableSpinnerAdapter<>(this, headerText, spinner_items, selected_items);
        spinner.setAdapter(adapter);
    }

    private void setSpinnerValue1() {
        prodList.clear();
        listVOs.clear();

        prodList.add("Select Product");
        Cursor res = db.getDistinctProduct();
        if (res != null) {
            if (res.moveToFirst()) {
                do {
                    prodList.add(res.getString(res.getColumnIndex(DBHandler.PM_Cat3)));
                } while (res.moveToNext());
            }
        }
        res.close();

        for (String product : prodList) {
            CustomSpinnerClass stateVO = new CustomSpinnerClass();
            stateVO.setTitle(product);
            stateVO.setSelected(false);
            listVOs.add(stateVO);
        }

        myAdapter = new CustomSpinnerAdapter(InwardActivity.this, 0, listVOs, posLs);
        spinner.setAdapter(myAdapter);
    }

    private void setData() {
        if (auto_product.getText().toString().equals("")) {
            toast.setText("Please,Enter Product");
            toast.show();
        } else if (ed_qty.getText().toString().equals("")) {
            toast.setText("Please,Enter Qty");
            toast.show();
        } else if (ed_rate.getText().toString().equals("")) {
            toast.setText("Please,Enter Rate");
            toast.show();
        } else if (ed_ssp.getText().toString().equals("")) {
            toast.setText("Please,Enter SSP");
            toast.show();
        } else {
            addInCart();
        }
    }

    private void addInCart() {
        String product = auto_product.getText().toString();
        gstgroup = tv_gstgroup.getText().toString();
        String _rate = ed_rate.getText().toString();
        String qty = ed_qty.getText().toString();
        String ssp = ed_ssp.getText().toString();

        InwardDetailClass detailClass = new InwardDetailClass();
         /*detailClass.setFatherSKU(product);
        detailClass.setRecQty(qty);
        detailClass.setRate(ssp);
        detailClass.setPurchaseRate(_rate);
        detailClass.setCGSTAMT(5000);
        detailClass.setSGSTAMT(900);
        detailClass.setCGSTPER(12);
        cartLs.add(detailClass);*/

        Cursor cursor = db.getGSTPer(gstgroup, stringToFloat(_rate));
        cursor.moveToFirst();
        float gstPer = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_GSTPer));
        float cgstPer = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_CGSTPer));
        float sgstPer = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_SGSTPer));
        float cgstShare = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_CGSTShare));
        float sgstShare = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_SGSTShare));
        cursor.close();

        int qty1 = stringToInt(qty);
        float _Rate = stringToFloat(_rate);
        float _ssp = stringToFloat(ssp);
        float _total = qty1 * _Rate;
        totQty = totQty + qty1;

        float taxableRate = _Rate;
        float total = (taxableRate * qty1);
        float billdiscPer = stringToFloat(ed_discPer.getText().toString());
        float billDiscAmnt = (total * billdiscPer) / 100;
        float disctedTotal = total - billDiscAmnt;
        float totalGST = (disctedTotal * gstPer) / 100;
        float cgstAmt = (disctedTotal * cgstPer) / 100;
        float sgstAmt = (disctedTotal * sgstPer) / 100;
        float netAmt = disctedTotal + cgstAmt + sgstAmt;
        totAmnt = totAmnt + total;

        cgstPerStr = roundTwoDecimals(cgstPer);
        sgstPerStr = roundTwoDecimals(sgstPer);
        gstPerStr = roundTwoDecimals(gstPer);
        totCGSTAmnt = totCGSTAmnt + cgstAmt;
        totSGSTAmnt = totSGSTAmnt + sgstAmt;

        detailClass.setFatherSKU(product);
        detailClass.setRecQty(Float.valueOf(qty));
        detailClass.setRate(_ssp);
        detailClass.setPurchaseRate(_Rate);
        detailClass.setCGSTAMT(cgstAmt);
        detailClass.setSGSTAMT(sgstAmt);
        detailClass.setGSTPER(gstPer);
        detailClass.setTotalAmt(total);
        detailClass.setProductNetAmt(netAmt);

        cartLs.add(detailClass);
        cartLsTempDisc.add(detailClass);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        rv_data.setLayoutManager(layoutManager);
        InwardRecyclerViewAdapter adapter = new InwardRecyclerViewAdapter(cartLs, getApplicationContext());
        rv_data.setAdapter(adapter);
        adapter.setOnClickListener1(this);

        auto_product.setText(null);
        tv_gstgroup.setText(null);
        ed_rate.setText(null);
        ed_qty.setText(null);
        ed_ssp.setText(null);
        auto_product.requestFocus();

    }

    private void saveInward() {
        try {
            //master variables
            float totalQty = 0, totalAmt = 0, netAmt = 0, OrderAmt = 0, BalanceQty = 0, TotSuppDisAmt = 0, replColumns = 0,
                    Disper = 0, DisAmt = 0, GrossAmt = 0, TotVat = 0, OtherAdd = 0, RoundUppAmt = 0, CSTVatPer = 0, CGSTAMT = 0,
                    SGSTAMT = 0, IGSTAMT = 0;

            int autoNo = 0, id = 0, branchID = 0, supplierID = 0, poNo = 0, rebarCnt = 0, createby = 0, Transport_id = 0, JobWrkDCid,
                    CancelledBy = 0, HOCode = 0;
            ;

            String inwardDate = "", againstPO = "", poDate = "", inwardSt = "", ramark = "", netAmtInWord = "", totalAmtInWord = "",
                    finYr = "", rebarcodeSt = "", billNo = "",
                    billgenerated = "", bgenerateno = "", createdate = "", LR_No = "", Lr_Date = "", Refund = "", RefundDate = "", PIMadeSt = "",
                    InvNo = "", BaleOpenNo = "", JobWorkTyp = "", BarcodeGenerate = "", ConsignmentPur = "", ForBranch = "", NAN = "", InwNo = "",
                    Status = "", CancelDate = "", CancelReson = "", ChkCST = "", EsugamNo = "", Reason = "", IGSTAPP = "";

            //detail variables
            float recQty = 0, rate = 0, totalAmt_d = 0, tax = 0, taxAmt = 0, productNetAmt = 0, discAmt = 0, discFromPr = 0,
                    purchaseRate = 0, FreeQty = 0, MandalPer = 0, Mandal = 0, CustDisper = 0, CustDisAmt = 0, itemSalePer = 0,
                    ExpQty = 0, BalanceQty_d = 0, MRP = 0, NAN_d = 0, SuppDisPer = 0, SuppDisAmt = 0, SuppBillDisPer = 0, SuppBillDisAmt = 0, OtherAddDed = 0,
                    BarcodeQty = 0, WSP = 0, NetRate = 0, GSTPER = 0, CGSTAMT_d = 0, SGSTAMT_d = 0, IGSTAMT_d = 0, CGSTPER = 0, SGSTPER = 0, CESSPER = 0, CESSAMT = 0, SuppDisPer1 = 0, SuppDisAmt1 = 0;

            int autoNo_d = 0, id_d = 0, inwardID = 0, rebarCnt_d = 0, refInwId = 0, branchId = 0, podetauto = 0, HOCode_d = 0;

            String productID = "", fatherSKU = "", barcode = "", itemName = "", rebarcodeSt_d = "", designno = "NA", color = "NA",
                    itmImage = "", ImagePath = "", ItemSize = "", JobWorkTyp_d = "",
                    OldBarcode = "", repColumn = "", GVApp = "", Scheme1 = "", Scheme2 = "", TxIncExTyp = "", HSNCode = "", Attr1 = "",
                    Attr2 = "", Atrr3 = "", Attr4 = "", Atrr5 = "";

            finYr = new Constant(getApplicationContext()).getFinYr();
            int mastAuto = db.getInwardMaxAuto();
            int mastId = db.getInwardMaxMastId(finYr);
            InwNo = db.getCompIni() + finYr + mastId;
            branchID = 1;
            totalQty = totQty;
            totalAmt = totAmnt;

            for (InwardDetailClass detailClass : cartLs) {
                totnetAmt = totnetAmt + detailClass.getProductNetAmt();
                grossAmt = grossAmt + (detailClass.getRecQty() * detailClass.getPurchaseRate());
            }

            netAmt = totnetAmt;
            OrderAmt = 0;
            BalanceQty = totalQty;
            TotSuppDisAmt = 0;
            replColumns = 0;
            Disper = stringToFloat(ed_discPer.getText().toString());
            DisAmt = stringToFloat(ed_discAmnt.getText().toString());
            //GrossAmt = stringToFloat(tv_final_amnt.getText().toString());
            GrossAmt = grossAmt;
            TotVat = 0;
            OtherAdd = stringToFloat(ed_otherAdd.getText().toString());
            ;
            RoundUppAmt = 0;
            CSTVatPer = 0;
            CGSTAMT = stringToFloat(tv_tot_cgst.getText().toString());
            SGSTAMT = stringToFloat(tv_tot_sgst.getText().toString());
            IGSTAMT = stringToFloat(tv_tot_igst.getText().toString());

            supplierID = db.getInwardSupplierId(supplier);
            poNo = 0;
            rebarCnt = 0;
            createby = 0;   //login person id
            Transport_id = 0;
            JobWrkDCid = 0;
            CancelledBy = 0;
            HOCode = 1;
            inwardDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(inward_date));
            againstPO = "";
            poDate = "";
            inwardSt = "Y";
            ramark = remark;
            netAmtInWord = "";
            totalAmtInWord = "";
            finYr = finYr;
            rebarcodeSt = "";
            billNo = "";
            billgenerated = "";
            bgenerateno = "";
            createdate = current_date;
            LR_No = "";
            Lr_Date = "";
            Refund = "";
            RefundDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(invoice_date));
            PIMadeSt = "N";
            BaleOpenNo = "";
            JobWorkTyp = "N";
            BarcodeGenerate = "Y";
            ConsignmentPur = "N";
            ForBranch = "";  //COMPANY NAME PASS
            NAN = "";
            InvNo = invoice_no;
            Status = "A";
            CancelDate = "";
            CancelReson = "";
            ChkCST = "N";
            EsugamNo = "";
            Reason = "";
            IGSTAPP = "N";
            InwardMasterClass mastClass = new InwardMasterClass();
            mastClass.setAutoNo(mastAuto);
            mastClass.setId(mastId);
            mastClass.setInwNo(InwNo);
            mastClass.setBranchID(branchID);
            mastClass.setTotalQty(totalQty);
            mastClass.setTotalAmt(totalAmt);
            mastClass.setNetAmt(netAmt);
            mastClass.setOrderAmt(OrderAmt);
            mastClass.setBalanceQty(BalanceQty);
            mastClass.setTotSuppDisAmt(TotSuppDisAmt);
            mastClass.setReplColumns(replColumns);
            mastClass.setDisper(Disper);
            mastClass.setDisAmt(DisAmt);
            mastClass.setGrossAmt(GrossAmt);
            mastClass.setTotVat(TotVat);
            mastClass.setOtherAdd(OtherAdd);
            mastClass.setRoundUppAmt(RoundUppAmt);
            mastClass.setCSTVatPer(CSTVatPer);
            mastClass.setCGSTAMT(CGSTAMT);
            mastClass.setSGSTAMT(SGSTAMT);
            mastClass.setIGSTAMT(IGSTAMT);
            mastClass.setSupplierID(supplierID);
            mastClass.setPoNo(poNo);
            mastClass.setRebarCnt(rebarCnt);
            mastClass.setCreateby(createby);
            mastClass.setTransport_id(Transport_id);
            mastClass.setJobWrkDCid(JobWrkDCid);
            mastClass.setCancelledBy(CancelledBy);
            mastClass.setHOCode(HOCode);
            mastClass.setInwardDate(inwardDate);
            mastClass.setAgainstPO(againstPO);
            mastClass.setPoDate(poDate);
            mastClass.setInwardSt(inwardSt);
            mastClass.setRamark(ramark);
            mastClass.setNetAmtInWord(netAmtInWord);
            mastClass.setTotalAmtInWord(totalAmtInWord);
            mastClass.setFinYr(finYr);
            mastClass.setRebarcodeSt(rebarcodeSt);
            mastClass.setBillNo(billNo);
            mastClass.setBillgenerated(billgenerated);
            mastClass.setBgenerateno(bgenerateno);
            mastClass.setCreatedate(createdate);
            mastClass.setLR_No(LR_No);
            mastClass.setLr_Date(Lr_Date);
            mastClass.setRefund(Refund);
            mastClass.setRefundDate(RefundDate);
            mastClass.setPIMadeSt(PIMadeSt);
            mastClass.setBaleOpenNo(BaleOpenNo);
            mastClass.setJobWorkTyp(JobWorkTyp);
            mastClass.setBarcodeGenerate(BarcodeGenerate);
            mastClass.setConsignmentPur(ConsignmentPur);
            mastClass.setForBranch(ForBranch);
            mastClass.setNAN(NAN);
            mastClass.setInvNo(InvNo);
            mastClass.setStatus(Status);
            mastClass.setCancelDate(CancelDate);
            mastClass.setCancelReson(CancelReson);
            mastClass.setChkCST(ChkCST);
            mastClass.setEsugamNo(EsugamNo);
            mastClass.setReason(Reason);
            mastClass.setIGSTAPP(IGSTAPP);
            db.saveInwardMaster(mastClass);

            for (InwardDetailClass detailClass : cartLs) {
                autoNo_d = db.getInwardMaxDetAuto();
                id_d = db.getInwardMaxDetId(mastId);
                inwardID = mastId;
                branchId = 1;
                productID = String.valueOf(db.getInwardProdId(detailClass.getFatherSKU()));
                fatherSKU = detailClass.getFatherSKU();
                barcode = detailClass.getProductID();
                recQty = detailClass.getRecQty();
                rate = detailClass.getPurchaseRate();
                totalAmt_d = detailClass.getTotalAmt();
                tax = 0;
                taxAmt = 0;
                productNetAmt = detailClass.getProductNetAmt();
                discAmt = detailClass.getDiscAmt();
                discFromPr = 0;
                purchaseRate = detailClass.getPurchaseRate();
                FreeQty = 0;
                MandalPer = 0;
                Mandal = 0;
                CustDisper = 0;
                CustDisAmt = 0;
                itemSalePer = 0;
                ExpQty = 0;
                BalanceQty_d = 0;
                MRP = detailClass.getRate();  //ssp
                NAN_d = detailClass.getPurchaseRate();
                SuppDisPer = 0;
                SuppDisAmt = 0;
                SuppBillDisPer = 0;
                SuppBillDisAmt = 0;
                OtherAddDed = 0;
                BarcodeQty = detailClass.getRecQty();
                WSP = 0;
                NetRate = productNetAmt / recQty;
                GSTPER = detailClass.getGSTPER();
                CGSTAMT_d = detailClass.getCGSTAMT();
                SGSTAMT_d = detailClass.getSGSTAMT();
                IGSTAMT_d = detailClass.getIGSTAMT();
                CGSTPER = detailClass.getCGSTPER();
                SGSTPER = detailClass.getSGSTPER();
                CESSPER = 0;
                CESSAMT = 0;
                SuppDisPer1 = Disper;
                SuppDisAmt1 = DisAmt;
                HSNCode = db.getInwardHSNCode(detailClass.getFatherSKU());

                InwardDetailClass dtClass = new InwardDetailClass();
                dtClass.setAutoNo(autoNo_d);
                dtClass.setId(id_d);
                dtClass.setInwardID(inwardID);
                dtClass.setBranchId(branchId);
                dtClass.setProductID(productID);
                dtClass.setFatherSKU(fatherSKU);
                dtClass.setBarcode(barcode);
                dtClass.setRecQty(recQty);
                dtClass.setRate(rate);
                dtClass.setTotalAmt(totalAmt_d);
                dtClass.setTax(tax);
                dtClass.setTaxAmt(taxAmt);
                dtClass.setProductNetAmt(productNetAmt);
                dtClass.setDiscAmt(discAmt);
                dtClass.setDiscFromPr(discFromPr);
                dtClass.setPurchaseRate(purchaseRate);
                dtClass.setFreeQty(FreeQty);
                dtClass.setMandal(Mandal);
                dtClass.setMandalPer(MandalPer);
                dtClass.setCustDisAmt(CustDisAmt);
                dtClass.setCustDisper(CustDisper);
                dtClass.setItemSalePer(itemSalePer);
                dtClass.setExpQty(ExpQty);
                dtClass.setBalanceQty(BalanceQty_d);
                dtClass.setMRP(MRP);
                dtClass.setNAN(NAN_d);
                dtClass.setSuppDisAmt(SuppDisAmt);
                dtClass.setSuppDisPer(SuppDisPer);
                dtClass.setSuppBillDisPer(SuppBillDisPer);
                dtClass.setSuppBillDisAmt(SuppBillDisAmt);
                dtClass.setOtherAddDed(OtherAddDed);
                dtClass.setBarcodeQty(BarcodeQty);
                dtClass.setWSP(WSP);
                dtClass.setNetRate(NetRate);
                dtClass.setGSTPER(GSTPER);
                dtClass.setCGSTAMT(CGSTAMT_d);
                dtClass.setSGSTAMT(SGSTAMT_d);
                dtClass.setIGSTAMT(IGSTAMT_d);
                dtClass.setCGSTPER(CGSTPER);
                dtClass.setSGSTPER(SGSTPER);
                dtClass.setCESSAMT(CESSAMT);
                dtClass.setCESSPER(CESSPER);
                dtClass.setSuppDisAmt1(SuppDisAmt1);
                dtClass.setSuppDisPer1(SuppDisPer1);
                dtClass.setRebarCnt(rebarCnt_d);
                dtClass.setRefInwId(refInwId);
                dtClass.setPodetauto(podetauto);
                dtClass.setHOCode(HOCode_d);
                dtClass.setItemName(itemName);
                dtClass.setRebarcodeSt(rebarcodeSt_d);
                dtClass.setDesignno(designno);
                dtClass.setColor(color);
                dtClass.setItmImage(itmImage);
                dtClass.setImagePath(ImagePath);
                dtClass.setItemSize(ItemSize);
                dtClass.setJobWorkTyp(JobWorkTyp_d);
                dtClass.setOldBarcode(OldBarcode);
                dtClass.setRepColumn(repColumn);
                dtClass.setGVApp(GVApp);
                dtClass.setScheme1(Scheme1);
                dtClass.setScheme2(Scheme2);
                dtClass.setTxIncExTyp(TxIncExTyp);
                dtClass.setHSNCode(HSNCode);
                dtClass.setAttr1(Attr1);
                dtClass.setAttr2(Attr2);
                dtClass.setAtrr3(Atrr3);
                dtClass.setAttr4(Attr4);
                dtClass.setAtrr5(Atrr5);
                db.saveInwardDetail(dtClass);
                int recqty = (int) dtClass.getRecQty();
                db.updateInwardProductQty(Integer.valueOf(dtClass.getProductID()), recqty);
            }
            ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(sliding_layout.getWindowToken(),0);
            showDia(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int stringToInt(String value) {
        if (value.equalsIgnoreCase("")) {
            return 0;
        } else {
            return Integer.parseInt(value);
        }
    }

    private float stringToFloat(String value) {
        if (value.equalsIgnoreCase("")) {
            return 0;
        } else {
            return Float.parseFloat(value);
        }
    }

    private String roundTwoDecimals(float d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return twoDForm.format(Double.parseDouble(String.valueOf(d)));
    }

    private String roundDecimals(float d) {
        DecimalFormat twoDForm = new DecimalFormat("#");
        return twoDForm.format(Double.parseDouble(String.valueOf(d)));
    }

    private String roundTwoDecimals(String d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        //return twoDForm.format(Double.parseDouble(d));
        return twoDForm.format(Float.parseFloat(d));
    }

    @Override
    public List<Integer> setPosition(List<Integer> ls) {
        posLs = ls;
        //myAdapter.notifyDataSetChanged();
        Constant.showLog("ls.size" + posLs.size());
        return ls;
    }

    @Override
    public void onItemClick(ProductClass prod) {

    }

    @Override
    public void onItemClick(RateMasterClass prod) {

    }

    @Override
    public void calculation(float qty, float amnt) {
        /*if (!ed_discPer.getText().toString().equals("")) {
            float disc = Float.parseFloat(ed_discPer.getText().toString());
            float discAmt = (totAmnt * disc) / 100;
            ed_discAmnt.setText(String.valueOf(discAmt));
        }*/
        tv_total_qty.setText(String.valueOf(totQty));
        tv_total_amount.setText(roundTwoDecimals(totAmnt));
        tv_tot_cgst.setText(roundTwoDecimals(totCGSTAmnt));
        tv_tot_sgst.setText(roundTwoDecimals(totSGSTAmnt));
        finalCalculation();
    }

    private void finalCalculation() {
        float discAmnt = 0, otherSpDiscAmt = 0, totAmnt = 0;

        totAmnt = stringToFloat(tv_total_amount.getText().toString());

        if (!ed_discPer.getText().toString().equalsIgnoreCase("")) {
            float discPer = stringToFloat(ed_discPer.getText().toString());
            discAmnt = (totAmnt * discPer) / 100;
        }
        if (!ed_otherAdd.getText().toString().equalsIgnoreCase("")) {
            otherSpDiscAmt = stringToFloat(ed_otherAdd.getText().toString());
        }

        totAmnt = totAmnt + totCGSTAmnt + totSGSTAmnt;
        totAmnt = totAmnt - discAmnt + otherSpDiscAmt;
        tv_final_amnt.setText(roundDecimals(totAmnt));
        ed_discAmnt.setText(roundTwoDecimals(discAmnt));
    }

    private void setItemwiseDiscount(String discPer) {
        cartLsTempDisc.clear();
        totAmnt = 0;
        totQty = 0;
        totCGSTAmnt = 0;
        totSGSTAmnt = 0;
        rv_data.setAdapter(null);
        for (int i = 0; i < cartLs.size(); i++) {
            InwardDetailClass dcart = cartLs.get(i);
            InwardDetailClass addToCart = new InwardDetailClass();
            addToCart.setFatherSKU(dcart.getFatherSKU());
            addToCart.setRate(dcart.getRate());
            addToCart.setPurchaseRate(dcart.getPurchaseRate());
            addToCart.setRecQty(dcart.getRecQty());
            //addToCart.setCGSTAMT(dcart.getCGSTAMT());
            //addToCart.setSGSTAMT(dcart.getSGSTAMT());
            //addToCart.setGSTPER(dcart.getGSTPER());

            Cursor cursor = db.getGSTPer(gstgroup, dcart.getPurchaseRate());
            cursor.moveToFirst();
            float gstPer = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_GSTPer));
            float cgstPer = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_CGSTPer));
            float sgstPer = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_SGSTPer));
            float cgstShare = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_CGSTShare));
            float sgstShare = cursor.getFloat(cursor.getColumnIndex(DBHandlerR.GSTDetail_SGSTShare));
            cursor.close();

            float q = dcart.getRecQty();
            int qty1 = (int) q;
            float _rate = dcart.getPurchaseRate();
            float _total = qty1 * _rate;
            totQty = totQty + qty1;

            float taxableRate = _rate;
            float total = (taxableRate * qty1);
            //float billdiscPer = stringToFloat(ed_discPer.getText().toString());
            if (discPer.equals("")) {
                discPer = "0";
            }
            float billdiscPer = stringToFloat(discPer);
            float billDiscAmnt = (total * billdiscPer) / 100;
            float disctedTotal = total - billDiscAmnt;
            float totalGST = (disctedTotal * gstPer) / 100;
            float cgstAmt = (disctedTotal * cgstPer) / 100;
            float sgstAmt = (disctedTotal * sgstPer) / 100;
            float netAmt = disctedTotal + cgstAmt + sgstAmt;
            totAmnt = totAmnt + total;

            cgstPerStr = roundTwoDecimals(cgstPer);
            sgstPerStr = roundTwoDecimals(sgstPer);
            totCGSTAmnt = totCGSTAmnt + cgstAmt;
            totSGSTAmnt = totSGSTAmnt + sgstAmt;

           /* addToCart.setFatherSKU(product);
            addToCart.setRecQty(Float.valueOf(qty));
            addToCart.setRate(_ssp);
            addToCart.setPurchaseRate(_Rate);*/
            addToCart.setCGSTAMT(cgstAmt);
            addToCart.setSGSTAMT(sgstAmt);
            addToCart.setGSTPER(gstPer);
            addToCart.setTotalAmt(_total);
            //cartLsTempDisc.set(i, addToCart);
            cartLsTempDisc.add(addToCart);
        }

        rv_data.setAdapter(null);
        InwardRecyclerViewAdapter adapter = new InwardRecyclerViewAdapter(cartLsTempDisc, getApplicationContext());
        adapter.setOnClickListener1(this);
        rv_data.setAdapter(adapter);
        rv_data.scrollToPosition(cartLsTempDisc.size() - 1);
    }

    private void clearField() {
        //setProduct();
        auto_product.setText(null);
        cartLs.clear();
        cartLsTempDisc.clear();
        totQty = 0;
        totAmnt = 0;
        totCGSTAmnt = 0;
        totSGSTAmnt = 0;
        cgstPerStr = "0";
        sgstPerStr = "0";
        ed_qty.setText(null);
        ed_rate.setText(null);
        ed_ssp.setText(null);
        cartLs.clear();
        rv_data.setAdapter(null);
        tv_total_qty.setText("0");
        tv_total_amount.setText("0");
        ed_discPer.setText(null);
        ed_discAmnt.setText(null);
        auto_product.setText(null);
        ed_discPer.setText(null);
        ed_discAmnt.setText("");
        ed_otherAdd.setText("");
        tv_tot_cgst.setText("0");
        tv_tot_sgst.setText("0");
        tv_tot_igst.setText("0");
        sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        auto_product.requestFocus();
    }

    private void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(InwardActivity.this);
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
        } else if (a == 1) {
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
        } else if (a == 2) {
            builder.setMessage("Inward Saved Successfully");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //  new CashMemoActivity.CashMemoPrint().execute();
                    clearField();
                    dialog.dismiss();
                }
            });
            /*builder.setNegativeButton("New Order", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //   clearField();
                    dialog.dismiss();
                }
            });*/
        }
        builder.create().show();
    }
}
