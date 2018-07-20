package com.pait.smartpos.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.model.BillDetailClassR;
import com.pait.smartpos.model.BillMasterClassR;
import com.pait.smartpos.model.CategoryClass;
import com.pait.smartpos.model.GSTDetailClass;
import com.pait.smartpos.model.GSTMasterClass;
import com.pait.smartpos.model.ProductClass;
import com.pait.smartpos.model.TableClass;
import com.pait.smartpos.model.TaxClass;
import com.pait.smartpos.parse.BillReprintCancelClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// Created by Android on 1/15/2016.

public class DBHandlerR extends SQLiteOpenHelper {

    //TODO: Check Database Version
    public static final String Database_Name = "SmartPOS.db";
    private static final int Database_Version = 1;

    public static final String Table_Table = "Tables";
    public static final String Table_ID = "ID";
    public static final String Table_Name = "Tables";
    public static final String Table_Active = "Active";
    public static final String Table_CrDate = "CrDate";
    public static final String Table_CrTime = "CrTime";
    private static final String Table_ModDate = "ModDate";
    private static final String Table_ModTime = "ModTime";
    private static final String Table_GSTGroup = "GSTGroup";

    public static final String Category_Table = "Category";
    public static final String Category_ID = "ID";
    public static final String Category_Cat = "Category";
    public static final String Category_Active = "Active";
    public static final String Category_CrDate = "CrDate";
    public static final String Category_CrTime = "CrTime";
    private static final String Category_ModDate = "ModDate";
    private static final String Category_ModTime = "ModTime";

    public static final String Product_Table = "Product";
    public static final String Product_Name = "Product_Name";
    public static final String Product_ID = "ID";
    public static final String Product_Rate = "Product_Rate";
    public static final String Product_Cat = "Product_Cat";
    public static final String Product_Barcode = "Product_Barcode";
    public static final String Product_KArea = "Product_KArea";
    public static final String Product_Active = "Product_Active";
    public static final String Product_Disper = "DiscPer";
    public static final String Product_GSTGroup = "GSTGroup";
    public static final String Product_HSNCode = "HSNCode";
    public static final String Product_TaxTyp = "TaxTyp";
    public static final String Product_Branchid = "Branchid";
    public static final String Product_CrDate = "CrDate";
    public static final String Product_CrTime = "CrTime";
    private static final String Product_ModDate = "ModDate";
    private static final String Product_ModTime = "ModTime";

    public static final String TempTable_Table = "TempTableData";
    private static final String TempTable_Auto = "Auto";
    private static final String TempTable_ID = "ID";
    private static final String TempTable_TableID = "TableID";
    private static final String TempTable_LocationID = "LocationID";
    private static final String TempTable_ItemID = "ItemID";
    private static final String TempTable_Qty = "Qty";
    private static final String TempTable_Rate = "Rate";
    private static final String TempTable_CrBy = "CrBy";
    private static final String TempTable_CRDate = "CrDate";
    private static final String TempTable_CMPST = "CMPST";
    private static final String TempTable_Barcode = "Barcode";
    private static final String TempTable_PrintSt = "PrintST";
    private static final String TempTable_KOTID = "KOTID";
    private static final String TempTable_KOTNO = "KOTNO";
    private static final String TempTable_Remark = "Remark";
    private static final String TempTable_BillST = "BillSt";
    private static final String TempTable_CrTime = "CrTime";
    private static final String TempTable_PQty = "PQty";
    private static final String TempTable_WaiterID = "WaiterID";
    private static final String TempTable_Tax = "Tax";
    private static final String TempTable_DiscPer = "DiscPer";

    public static final String BillMaster_Table = "BillMaster";
    public static final String BillMaster_Auto = "Auto";
    private static final String BillMaster_Id = "ID";
    private static final String BillMaster_BranchId  = "BranchId";
    private static final String BillMaster_Finyr = "Finyr";
    private static final String BillMaster_BillNo = "BillNo";
    private static final String BillMaster_BillDate = "BillDate";
    private static final String BillMaster_BillTime = "BillTime";
    private static final String BillMaster_CustID = "CustID";
    private static final String BillMaster_TotalQty = "TotalQty";
    private static final String BillMaster_TotalAmt = "TotalAmt";
    private static final String BillMaster_CardAmt = "CardAmt";
    private static final String BillMaster_CashAmt = "CashAmt";
    private static final String BillMaster_CoupanAmt = "CoupanAmt";
    private static final String BillMaster_PaidAmt = "PaidAmt";
    private static final String BillMaster_BalAmt = "BalAmt";
    private static final String BillMaster_NetAmt = "NetAmt";
    private static final String BillMaster_BillSt  = "BillSt";
    private static final String BillMaster_CrBy  = "CrBy";
    private static final String BillMaster_CrDate  = "CrDate";
    private static final String BillMaster_ModBy  = "ModBy";
    private static final String BillMaster_ModDate  = "ModDate";
    private static final String BillMaster_Vat12 = "Vat12";
    private static final String BillMaster_Vat5 = "Vat5";
    private static final String BillMaster_CardNo = "CardNo";
    private static final String BillMaster_ChqNo = "ChqNo";
    private static final String BillMaster_Chqamt = "Chqamt";
    private static final String BillMaster_Chqdt  = "Chqdt";
    private static final String BillMaster_Advamt = "Advamt";
    private static final String BillMaster_paymenttype  = "paymenttype";
    private static final String BillMaster_BillPaySt  = "BillPaySt";
    private static final String BillMaster_BankID  = "BankID";
    private static final String BillMaster_PrintNO  = "PrintNO";
    private static final String BillMaster_Disper = "Disper";
    private static final String BillMaster_DisAmt = "DisAmt";
    private static final String BillMaster_AmtInWords = "AmtInWords";
    private static final String BillMaster_refundpyst  = "refundpyst";
    private static final String BillMaster_PIno = "PIno";
    private static final String BillMaster_PIAmt = "PIAmt";
    private static final String BillMaster_GVoucher  = "GVoucher";
    private static final String BillMaster_GVoucherAmt = "GVoucherAmt";
    private static final String BillMaster_GrossAmt = "GrossAmt";
    private static final String BillMaster_GoodsReturn = "GoodsReturn";
    private static final String BillMaster_CounterNo = "CounterNo";
    private static final String BillMaster_MachineName = "MachineName";
    private static final String BillMaster_AgainstDC  = "AgainstDC";
    private static final String BillMaster_Tender = "Tender";
    private static final String BillMaster_RemainAmt = "RemainAmt";
    private static final String BillMaster_DCAutoNo  = "DCAutoNo";
    private static final String BillMaster_TRetQty = "TRetQty";
    private static final String BillMaster_Remark = "Remark";
    private static final String BillMaster_CancelBy  = "CancelBy";
    private static final String BillMaster_CancelDate  = "CancelDate";
    private static final String BillMaster_CancelReason = "CancelReason";
    private static final String BillMaster_TableID  = "TableID";
    private static final String BillMaster_LocationID  = "LocationID";
    private static final String BillMaster_SalesMan  = "SalesMan";
    private static final String BillMaster_WaiterID  = "WaiterID";
    private static final String BillMaster_ServiceTax = "ServiceTax";
    private static final String BillMaster_ServiceAmt = "ServiceAmt";
    private static final String BillMaster_NoOfPass  = "NoOfPass ";
    private static final String BillMaster_KOTNo = "KOTNo";
    private static final String BillMaster_Vatamt = "Vatamt";
    private static final String BillMaster_Taxst  = "Taxst";
    private static final String BillMaster_Taxper = "Taxper";
    private static final String BillMaster_VatPer = "VatPer";
    private static final String BillMaster_TaxAmt = "TaxAmt";
    private static final String BillMaster_OtherAmt = "OtherAmt";
    private static final String BillMaster_CGSTAMT = "CGSTAMT";
    private static final String BillMaster_SGSTAMT = "SGSTAMT";

    public static final String BillDetail_Table = "BillDetail";
    public static final String BillDetail_Auto = "Auto";
    public static final String BillDetail_ID = "ID";
    public static final String BillDetail_BillID = "BillID";
    public static final String BillDetail_BranchId = "BranchId";
    public static final String BillDetail_Finyr = "Finyr";
    public static final String BillDetail_Itemid = "Itemid";
    private static final String BillDetail_Rate = "Rate";
    public static final String BillDetail_Qty = "Qty";
    private static final String BillDetail_Total = "Total";
    private static final String BillDetail_CmpSt = "CmpSt";
    private static final String BillDetail_Vat = "Vat";
    private static final String BillDetail_Vatamt = "Vatamt";
    private static final String BillDetail_Disper = "Disper";
    private static final String BillDetail_Disamt = "Disamt";
    private static final String BillDetail_NonBar = "NonBar";
    private static final String BillDetail_RateWithTax = "RateWithTax";
    private static final String BillDetail_FreeQty = "FreeQty";
    private static final String BillDetail_BillDiscper = "BillDiscper";
    private static final String BillDetail_BillDiscAmt = "BillDiscAmt";
    private static final String BillDetail_DCMastAuto = "DCMastAuto";
    private static final String BillDetail_RetQty = "RetQty";
    private static final String BillDetail_WaiterID = "WaiterID";
    private static final String BillDetail_Barcode = "Barcode";
    private static final String BillDetail_AmtWithDisc = "AmtWithDisc";
    public static final String BillDetail_NetAmt = "NetAmt";
    private static final String BillDetail_GSTPER = "GSTPER";
    private static final String BillDetail_CGSTAMT = "CGSTAMT";
    private static final String BillDetail_SGSTAMT = "SGSTAMT";
    private static final String BillDetail_CGSTPER = "CGSTPER";
    private static final String BillDetail_SGSTPER = "SGSTPER";
    private static final String BillDetail_CESSPER = "CESSPER";
    private static final String BillDetail_CESSAMT = "CESSAMT";

    public static final String Tax_Table = "Tax";
    private static final String Tax_ID = "ID";
    private static final String Tax_Name = "TaxName";
    private static final String Tax_Rate = "TaxRate";
    private static final String Tax_Active = "Active";
    private static final String Tax_CrDate = "CrDate";
    private static final String Tax_CrTime = "CrTime";
    private static final String Tax_ModDate = "ModDate";
    private static final String Tax_ModTime = "ModTime";

    public static final String GSTMaster_Table = "GSTMaster";
    public static final String GSTMaster_Auto = "Auto";
    public static final String GSTMaster_GroupName = "GroupName";
    public static final String GSTMaster_Status = "Status";
    private static final String GSTMaster_CrBy = "CrBy";
    public static final String GSTMaster_CrDate = "CrDate";
    public static final String GSTMaster_CrTime = "CrTime";
    private static final String GSTMaster_ModBy = "ModBy";
    private static final String GSTMaster_ModDate = "ModDate";
    private static final String GSTMaster_ModTime = "ModTime";
    private static final String GSTMaster_CanBy = "CanBy";
    private static final String GSTMaster_CanDate = "CanDate";
    private static final String GSTMaster_CanTime = "CanTime";
    private static final String GSTMaster_Remark = "Remark";
    private static final String GSTMaster_EffFrom = "EffFrom";
    private static final String GSTMaster_EffTo = "EffTo";

    public static final String GSTDetail_Table = "GSTDetail";
    public static final String GSTDetail_Auto = "Auto";
    public static final String GSTDetail_MastAuto = "MastAuto";
    public static final String GSTDetail_FromRange = "FromRange";
    public static final String GSTDetail_ToRange = "ToRange";
    public static final String GSTDetail_GSTPer = "GSTPer";
    public static final String GSTDetail_CGSTPer = "CGSTPer";
    public static final String GSTDetail_SGSTPer = "SGSTPer";
    public static final String GSTDetail_CGSTShare = "CGSTShare";
    public static final String GSTDetail_SGSTShare = "SGSTShare";
    public static final String GSTDetail_CessPer = "CessPer";

    private static final String Kitchen_Table = "KitchenMaster";
    private static final String Kitchen_Auto = "Auto";
    private static final String Kitchen_Id = "Id";
    private static final String Kitchen_Name = "Name";
    private static final String Kitchen_IpAddress = "IpAddress";
    private static final String Kitchen_Status = "Status";
    private static final String Kitchen_CrBy = "CrBy";
    private static final String Kitchen_MacAddress = "MacAddress";

    private static final String UB_Table = "UpdateBillTable";
    private static final String UB_Auto = "Auto";
    public static final String UB_BillNo = "BillNo";
    public static final String UB_ProdId = "ProdId";
    public static final String UB_OldQty = "OldQty";
    public static final String UB_NewQty = "NewQty";
    public static final String UB_OldRate = "OldRate";
    public static final String UB_NewRate = "NewRate";
    private static final String UB_ModBy = "ModBy";
    private static final String UB_ModDate = "ModDate";
    private static final String UB_ModTime = "ModTime";

    private String create_table = "create table if not exists " + Table_Table + "(" + Table_ID + " Integer not null," +
            Table_Name + " Text,"+Table_Active + " Text,"+Table_CrDate+ " text,"+Table_CrTime+" text,"+
            Table_ModDate+" text,"+Table_ModTime+" text,"+ Table_GSTGroup+" text, primary key("+Table_ID+"))";

    private String create_category = "create table if not exists " + Category_Table + "(" + Category_ID + " integer not null," +
            Category_Cat + " Text," + Category_Active + " Text,"+Category_CrDate+ " text,"+Category_CrTime+" text,"+
            Category_ModDate+" text,"+Category_ModTime+" text, primary key("+Category_ID+"))";

    private String create_tax = "create table if not exists " + Tax_Table + "(" + Tax_ID + " integer not null," + Tax_Name +
            " Text,"+ Tax_Rate + " float," + Tax_Active + " Text,"+Tax_CrDate+ " text,"+Tax_CrTime+" text,"+
            Tax_ModDate+" text,"+Tax_ModTime+" text, primary key("+Tax_ID+"))";

    private String create_product = "create table if not exists " + Product_Table + "(" + Product_ID + " Integer not null," +
            Product_Name + " Text," + Product_Cat + " Integer," + Product_Rate + " double," + Product_Barcode + " Text," +
            Product_KArea + " Text,"+ Product_Active+ " text,"+Product_CrDate+ " text,"+Product_CrTime+" text,"+
            Product_ModDate+" text,"+Product_ModTime+" text,"+Product_Disper+" text,"+Product_GSTGroup+" text,"+
            Product_HSNCode+" text,"+Product_TaxTyp+" text,"+Product_Branchid+" int, primary key("+Product_ID+"))";

    private String create_temptableData = "create table if not exists "
            + TempTable_Table + "(" + TempTable_Auto + " int not null," +
            TempTable_ID + " int," + TempTable_TableID + "  int," +
            TempTable_LocationID + "  int," + TempTable_ItemID + "  int," +
            TempTable_Qty + "  int," + TempTable_Rate + "  float," +
            TempTable_CrBy + "  int," + TempTable_CRDate + "  text," +
            TempTable_CMPST + "  text," + TempTable_Barcode + "  text," +
            TempTable_PrintSt + "  text," + TempTable_KOTID + "  int," +
            TempTable_KOTNO + "  text," + TempTable_Remark + "  text," +
            TempTable_BillST + "  text," + TempTable_CrTime + "  text," +
            TempTable_PQty + "  int," + TempTable_WaiterID + "  int," +
            TempTable_Tax + "  float," + TempTable_DiscPer + "  float, primary key("+TempTable_Auto+"))";

    private String create_billMaster = "create table if not exists " +
            BillMaster_Table + "(" +
            BillMaster_Auto + " int not null," + BillMaster_Id + " int," +
            BillMaster_BranchId + " int," + BillMaster_Finyr + " text," +
            BillMaster_BillNo + " text," + BillMaster_BillDate + " text," +
            BillMaster_BillTime + " text," + BillMaster_CustID + " int," +
            BillMaster_TotalQty + " float," + BillMaster_TotalAmt + " float," +
            BillMaster_CardAmt + " float," + BillMaster_CashAmt + " float," +
            BillMaster_CoupanAmt + " float," + BillMaster_PaidAmt + " float," +
            BillMaster_BalAmt + " float," + BillMaster_NetAmt + " float," +
            BillMaster_BillSt + " text," + BillMaster_CrBy + " int," +
            BillMaster_CrDate + " text," + BillMaster_ModBy + " int," +
            BillMaster_ModDate + " text," + BillMaster_Vat12 + " float," +
            BillMaster_Vat5 + " float," + BillMaster_CardNo + " text," +
            BillMaster_ChqNo + " text," + BillMaster_Chqamt + " float," +
            BillMaster_Chqdt + " text," + BillMaster_Advamt + " float," +
            BillMaster_paymenttype + " text," + BillMaster_BillPaySt + " text," +
            BillMaster_BankID + " int," + BillMaster_PrintNO + " int," +
            BillMaster_Disper + " float," + BillMaster_DisAmt + " float," +
            BillMaster_AmtInWords + " text," + BillMaster_refundpyst + " text," +
            BillMaster_PIno + " text," + BillMaster_PIAmt + " float," +
            BillMaster_GVoucher + " int," + BillMaster_GVoucherAmt + " float," +
            BillMaster_GrossAmt + " float," + BillMaster_GoodsReturn + " float," +
            BillMaster_CounterNo + " text," + BillMaster_MachineName + " text," +
            BillMaster_AgainstDC + " text," + BillMaster_Tender + " float," +
            BillMaster_RemainAmt + " float," + BillMaster_DCAutoNo + " int," +
            BillMaster_TRetQty + " float," + BillMaster_Remark + " text," +
            BillMaster_CancelBy + " int," + BillMaster_CancelDate + " text," +
            BillMaster_CancelReason + " text," + BillMaster_TableID + " int," +
            BillMaster_LocationID + " int," + BillMaster_SalesMan + " int," +
            BillMaster_WaiterID + " int," + BillMaster_ServiceTax + " float," +
            BillMaster_ServiceAmt + " float," + BillMaster_NoOfPass + " int," +
            BillMaster_KOTNo + " text," + BillMaster_Vatamt + " float," +
            BillMaster_Taxst + " text," + BillMaster_Taxper + " float," +
            BillMaster_VatPer + " float," + BillMaster_TaxAmt + " float," +
            BillMaster_OtherAmt + " float,"+BillMaster_CGSTAMT+ " float,"+
            BillMaster_SGSTAMT+ " float, primary key("+BillMaster_Auto+"))";

    private String create_billDetails = "create table if not exists " +
            BillDetail_Table + "(" +
            BillDetail_Auto + " int not null," +BillDetail_ID + " int," +
            BillDetail_BillID + " int," +BillDetail_BranchId + " int," +
            BillDetail_Finyr + " text ," +BillDetail_Itemid + " int," +
            BillDetail_Rate + " float," +BillDetail_Qty + " float," +
            BillDetail_Total + " float," +BillDetail_CmpSt + " text," +
            BillDetail_Vat + " float," +BillDetail_Vatamt + " float," +
            BillDetail_Disper + " float," +BillDetail_Disamt + " float," +
            BillDetail_NonBar + " text," +BillDetail_RateWithTax + " float," +
            BillDetail_FreeQty + " float," +BillDetail_BillDiscper + " float," +
            BillDetail_BillDiscAmt + " float," +BillDetail_DCMastAuto + " int," +
            BillDetail_RetQty + " float," +BillDetail_WaiterID + " int," +
            BillDetail_Barcode + " text," +BillDetail_AmtWithDisc + " float," +
            BillDetail_NetAmt + " float,"+BillDetail_GSTPER + " float,"+
            BillDetail_CGSTAMT + " float,"+BillDetail_SGSTAMT + " float,"+
            BillDetail_CGSTPER + " float,"+BillDetail_SGSTPER + " float,"+
            BillDetail_CESSPER + " float,"+BillDetail_CESSAMT + " float, primary key("+BillDetail_Auto+"))";

    private String create_GSTMaster_table = "Create table if not exists "+GSTMaster_Table+"("+GSTMaster_Auto+" int not null,"+
            GSTMaster_GroupName + " text,"+GSTMaster_Status+" text,"+GSTMaster_CrBy+" int,"+GSTMaster_CrDate+" text,"+
            GSTMaster_CrTime+" text,"+GSTMaster_ModBy + " int,"+GSTMaster_ModDate+" text,"+GSTMaster_ModTime+" text,"+
            GSTMaster_CanBy + " int,"+GSTMaster_CanDate+" text,"+GSTMaster_CanTime+" text,"+GSTMaster_Remark+" text,"+
            GSTMaster_EffFrom+ " text,"+GSTMaster_EffTo+ " text, primary key("+GSTMaster_Auto+"))";

    private String create_GSTDetail_table = "create table if not exists "+GSTDetail_Table+"("+GSTDetail_Auto+" int not null,"+
            GSTDetail_MastAuto+" int,"+GSTDetail_FromRange + " float,"+GSTDetail_ToRange+" float,"+
            GSTDetail_GSTPer+" float,"+GSTDetail_CGSTPer+" float,"+GSTDetail_SGSTPer+" float,"+
            GSTDetail_CGSTShare+" float,"+GSTDetail_SGSTShare+" float,"+GSTDetail_CessPer+" float, primary key("+GSTDetail_Auto+"))";

    private String create_kitchen_table = "create table if not exists "+Kitchen_Table+"("+Kitchen_Auto+" int not null,"+
            Kitchen_Id+" int,"+Kitchen_Name + " text,"+Kitchen_IpAddress+" text,"+
            Kitchen_Status+" text,"+Kitchen_CrBy+" int,"+Kitchen_MacAddress+" text, primary key("+Kitchen_Auto+"))";

    private String create_updatebill_table = "create table if not exists "+UB_Table+"("+
            UB_Auto+" int not null,"+
            UB_BillNo+" text,"+UB_ProdId + " int,"+UB_OldQty+" float,"+
            UB_NewQty+" float,"+UB_OldRate+" float,"+UB_NewRate+" float, " +
            UB_ModBy+" int,"+UB_ModDate+" text,"+UB_ModTime+" text, primary key("+UB_Auto+"))";

    public DBHandlerR(Context context) {
        super(context, Database_Name, null, Database_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_table);
        db.execSQL(create_category);
        db.execSQL(create_product);
        db.execSQL(create_temptableData);
        db.execSQL(create_billMaster);
        db.execSQL(create_billDetails);
        db.execSQL(create_GSTMaster_table);
        db.execSQL(create_GSTDetail_table);
        db.execSQL(create_updatebill_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void addTable(TableClass tableClass){
        String date = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String time = new SimpleDateFormat("HH:mm",Locale.ENGLISH).format(Calendar.getInstance().getTime());
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Table_ID, tableClass.getTable_ID());
        cv.put(Table_Name, tableClass.getTables());
        cv.put(Table_Active,"Y");
        cv.put(Table_CrDate,date);
        cv.put(Table_CrTime,time);
        cv.put(Table_GSTGroup,tableClass.getGstgroup());
        db.insert(Table_Table, null, cv);
    }

    public void addCategory(CategoryClass cat){
        String date = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String time = new SimpleDateFormat("HH:mm",Locale.ENGLISH).format(Calendar.getInstance().getTime());
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Category_ID, cat.getCategory_ID());
        cv.put(Category_Cat,cat.getCategory());
        cv.put(Category_Active,cat.getIsActive());
        cv.put(Category_CrDate,date);
        cv.put(Category_CrTime,time);
        db.insert(Category_Table, null, cv);
    }

    public void addTax(TaxClass tax){
        String date = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String time = new SimpleDateFormat("HH:mm",Locale.ENGLISH).format(Calendar.getInstance().getTime());
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Tax_ID,tax.getTax_ID());
        cv.put(Tax_Name,tax.getTaxName());
        cv.put(Tax_Rate,tax.getTaxRate());
        cv.put(Tax_Active,tax.getIsActive());
        cv.put(Tax_CrDate,date);
        cv.put(Tax_CrTime,time);
        db.insert(Tax_Table, null, cv);
    }

    public void setDefaultGSTToTable(){
        ContentValues cv = new ContentValues();
        cv.put(Table_GSTGroup,Constant.default_gst_name);
        getWritableDatabase().update(Table_Table,cv,null,null);
    }

    public void addProduct(ProductClass prod){
        String date = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String time = new SimpleDateFormat("HH:mm",Locale.ENGLISH).format(Calendar.getInstance().getTime());
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Product_ID,prod.getProduct_ID());
        cv.put(Product_Name,prod.getProduct_Name());
        cv.put(Product_Cat,prod.getProduct_Cat());
        cv.put(Product_Rate,prod.getProduct_Rate());
        cv.put(Product_Barcode,prod.getProduct_Barcode());
        cv.put(Product_KArea,prod.getProduct_KArea());
        cv.put(Product_GSTGroup,prod.getGstGroup());
        cv.put(Product_TaxTyp,prod.getTaxType());
        cv.put(Product_Active,"Y");
        cv.put(Product_CrDate,date);
        cv.put(Product_CrTime,time);
        db.insert(Product_Table, null, cv);
    }

    public String saveTemptableData(int tableID, int LocationId, int crby, String str){
        String crDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String crTime = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String cmpst = "N";
        String prinst = "Y";
        String kotno = "0";

        String dataArr[] = str.split("\\,");
        for(String data:dataArr) {
            //ItemID-Qty-Rate-Barcode-Remark-BillST
            String[] subdata = data.split("\\-");
            String ItemId = subdata[0];
            int qty = Integer.parseInt(subdata[1]);
            float rate = Float.parseFloat(subdata[2]);
            String barcode = subdata[3];
            String remark = subdata[4];
            String billST = subdata[5];

            String getCount = "Select COUNT(" + TempTable_Auto + ") from " + TempTable_Table + " where " + TempTable_TableID + "=" + tableID + " and " +
                    TempTable_ItemID + "=" + ItemId + " and " + TempTable_LocationID + "=" + LocationId + " and " + TempTable_Rate + "=" + rate + " and " + TempTable_BillST + "='N' and " +
                    TempTable_Barcode + "=" + barcode;
            int count = getCount(getCount);
            if (count <= 0) {
                //Insert Operations
                String getAuto = "select max(" + TempTable_Auto + ") from " + TempTable_Table;
                int auto = getTempColumn(getAuto);
                int id = getMax(TempTable_Table);
                //String getKotID = "Select "+ TempTable_KOTID + " from " + TempTable_Table +
                // " where " + TempTable_TableID + "=" + tableID + " and Billst='N'";
                String getKotID = "Select "+ TempTable_KOTID + " from (Select * from "+ TempTable_Table +
                        " where " + TempTable_TableID + "=" + tableID + " and Billst='N' order by "+TempTable_KOTID+" desc)  order by "+TempTable_KOTID+" asc limit 1";
                int kotid = getCount(getKotID);
                if(kotid==0){
                    String getKotID1 = "select max(" + TempTable_KOTID + ") from " + TempTable_Table;
                    kotid = getTempColumn(getKotID1);
                }
                kotno = kotid + "/" + tableID;
                int PQty = 0;
                float tax = 0;
                float discPr = 0;

                ContentValues cv = new ContentValues();
                cv.put(TempTable_Auto, auto);
                cv.put(TempTable_ID, id);
                cv.put(TempTable_TableID, tableID);
                cv.put(TempTable_ItemID, ItemId);
                cv.put(TempTable_Rate, rate);
                cv.put(TempTable_Qty, qty);
                cv.put(TempTable_CrBy, crby);
                cv.put(TempTable_LocationID, LocationId);
                cv.put(TempTable_CMPST, cmpst);
                cv.put(TempTable_Barcode, barcode);
                cv.put(TempTable_KOTID, kotid);
                cv.put(TempTable_KOTNO, kotno);
                cv.put(TempTable_Remark, remark);
                cv.put(TempTable_CRDate, crDate);
                cv.put(TempTable_CrTime, crTime);
                cv.put(TempTable_PQty, PQty);
                cv.put(TempTable_PrintSt, prinst);
                cv.put(TempTable_WaiterID, crby);
                cv.put(TempTable_Tax, tax);
                cv.put(TempTable_DiscPer, discPr);
                cv.put(TempTable_BillST, billST);
                getWritableDatabase().insert(TempTable_Table, null, cv);
            } else {
                // Update Operations
                String getAuto = "Select " + TempTable_Auto + " from " + TempTable_Table + " where " + TempTable_TableID + "=" + tableID + " and " + TempTable_ItemID + "=" + ItemId + " and " + TempTable_LocationID + "=" + LocationId + " and " + TempTable_BillST + "='N' and " + TempTable_Barcode + "=" + barcode;
                int auto = getCount(getAuto);
                String qtyStr = "Select " + TempTable_Qty + "-" + TempTable_PQty + " from " + TempTable_Table + " where " + TempTable_TableID + "=" + tableID + " and " + TempTable_ItemID + "=" + ItemId + " and " + TempTable_LocationID + "=" + LocationId + " and " + TempTable_BillST + "='N' and " + TempTable_Barcode + "=" + barcode;
                int ppqty = getCount(qtyStr);
                int pqty = qty - ppqty;
                ContentValues cv = new ContentValues();
                cv.put(TempTable_PQty, pqty);
                cv.put(TempTable_Qty, qty);
                cv.put(TempTable_Remark, remark);
                cv.put(TempTable_WaiterID, crby);
                cv.put(TempTable_PrintSt, prinst);
                getWritableDatabase().update(TempTable_Table, cv, TempTable_Auto + "=?", new String[]{String.valueOf(auto)});
                String getKotID = "Select "+ TempTable_KOTID + " from " + TempTable_Table + " where " + TempTable_TableID + "=" + tableID + " and Billst='N'";
                int kotid = getCount(getKotID);
                kotno = kotid + "/" + tableID;
            }
        }
        return kotno;
    }

    public int saveBillMaster(BillMasterClassR master, int auto, String kotno){
        ContentValues cv = new ContentValues();
        cv.put(BillMaster_Auto,master.getAuto());
        cv.put(BillMaster_Id,master.getId());
        cv.put(BillMaster_BranchId,master.getBranchId());
        cv.put(BillMaster_Finyr,master.getFinyr());
        cv.put(BillMaster_BillNo,master.getBillNo());
        cv.put(BillMaster_BillDate,master.getBillDate());
        cv.put(BillMaster_BillTime,master.getBillTime());
        cv.put(BillMaster_CustID,master.getCustID());
        cv.put(BillMaster_TotalQty,master.getTotalQty());
        cv.put(BillMaster_TotalAmt,master.getTotalAmtStr());
        cv.put(BillMaster_CardAmt,master.getCardAmt());
        cv.put(BillMaster_CashAmt,master.getCashAmt());
        cv.put(BillMaster_CoupanAmt,master.getCoupanAmt());
        cv.put(BillMaster_PaidAmt,master.getPaidAmt());
        cv.put(BillMaster_BalAmt,master.getBalAmt());
        cv.put(BillMaster_NetAmt,master.getNetAmt());
        cv.put(BillMaster_BillSt,master.getBillSt());
        cv.put(BillMaster_CrBy,master.getCrBy());
        cv.put(BillMaster_CrDate,master.getCrDate());
        cv.put(BillMaster_Vat12,master.getVat12());
        cv.put(BillMaster_Vat5,master.getVat5());
        cv.put(BillMaster_Disper,master.getDisper());
        cv.put(BillMaster_DisAmt,master.getDisAmt());
        cv.put(BillMaster_AmtInWords,master.getAmtInWords());
        cv.put(BillMaster_PIAmt,master.getPIAmt());
        cv.put(BillMaster_PIno,master.getPIno());
        cv.put(BillMaster_CounterNo,master.getCounterNo());
        cv.put(BillMaster_MachineName,master.getMachineName());
        cv.put(BillMaster_AgainstDC,master.getAgainstDC());
        cv.put(BillMaster_TableID,master.getTableID());
        cv.put(BillMaster_LocationID,master.getLocationID());
        cv.put(BillMaster_SalesMan,master.getSalesMan());
        cv.put(BillMaster_WaiterID,master.getWaiterID());
        cv.put(BillMaster_ServiceTax,master.getServiceTax());
        cv.put(BillMaster_ServiceAmt,master.getServiceAmt());
        cv.put(BillMaster_NoOfPass,master.getNoOfPass());
        cv.put(BillMaster_Advamt,master.getAdvamt());
        cv.put(BillMaster_KOTNo,master.getKOTNo());
        cv.put(BillMaster_DCAutoNo,master.getDCAutoNo());
        cv.put(BillMaster_Taxst,master.getTaxst());
        cv.put(BillMaster_Taxper,master.getTaxper());
        cv.put(BillMaster_Vatamt,master.getVatamt());
        cv.put(BillMaster_Tender,master.getTender());
        cv.put(BillMaster_RemainAmt,master.getRemainAmt());
        cv.put(BillMaster_BillPaySt,master.getBillPaySt());
        cv.put(BillMaster_VatPer,master.getVatPer());
        cv.put(BillMaster_TaxAmt,master.getTaxAmt());
        cv.put(BillMaster_OtherAmt,master.getOtherAmt());
        cv.put(BillMaster_CGSTAMT,master.getCGSTAMTStr());
        cv.put(BillMaster_SGSTAMT,master.getSGSTAMTStr());

        getWritableDatabase().insert(BillMaster_Table,null,cv);
        ContentValues cv1 = new ContentValues();
        cv1.put(TempTable_BillST,"Y");
        getWritableDatabase().update(TempTable_Table,cv1,TempTable_KOTNO+"=?",new String[]{kotno});

        return auto;
    }

    public void saveBillDetail(BillDetailClassR detail){
        ContentValues cv = new ContentValues();
        cv.put(BillDetail_Auto,detail.getAuto());
        cv.put(BillDetail_ID,detail.getID());
        cv.put(BillDetail_BillID,detail.getBillID());
        cv.put(BillDetail_BranchId,detail.getBranchId());
        cv.put(BillDetail_Finyr,detail.getFinyr());
        cv.put(BillDetail_Itemid,detail.getItemid());
        cv.put(BillDetail_Rate,detail.getRateStr());
        cv.put(BillDetail_Qty,detail.getQty());
        cv.put(BillDetail_CmpSt,detail.getCmpSt());
        cv.put(BillDetail_Total,detail.getTotalStr());
        cv.put(BillDetail_Vat,detail.getVat());
        cv.put(BillDetail_Vatamt,detail.getVatamt());
        cv.put(BillDetail_AmtWithDisc,detail.getAmtWithDiscStr());
        cv.put(BillDetail_Disper,detail.getDisper());
        cv.put(BillDetail_Disamt,detail.getDisamt());
        cv.put(BillDetail_BillDiscper,detail.getBillDiscper());
        cv.put(BillDetail_BillDiscAmt,detail.getBillDiscAmt());
        cv.put(BillDetail_WaiterID,detail.getWaiterID());
        cv.put(BillDetail_Barcode,detail.getBarcode());
        cv.put(BillDetail_NetAmt,detail.getNetAmtStr());
        cv.put(BillDetail_GSTPER,detail.getGSTPER());
        cv.put(BillDetail_CGSTAMT,detail.getCGSTAMTStr());
        cv.put(BillDetail_SGSTAMT,detail.getSGSTAMTStr());
        cv.put(BillDetail_CGSTPER,detail.getCGSTPER());
        cv.put(BillDetail_SGSTPER,detail.getSGSTPER());
        cv.put(BillDetail_CESSPER,detail.getCESSPER());
        cv.put(BillDetail_CESSAMT,detail.getCESSAMTStr());
        getWritableDatabase().insert(BillDetail_Table,null,cv);
    }

    public Cursor getTable(){
        return getWritableDatabase().rawQuery("select * from " + Table_Table + " where "+Table_Active+"='Y' or "+Table_Active+"='A' order by "+Table_ID, null);
    }

    public Cursor getCatgory(){
        return getWritableDatabase().rawQuery("select * from " + Category_Table + " where "+Category_Active+"='Y' or "+Category_Active+"='A' order by "+Category_Cat, null);
    }

    public Cursor getTax(){
        return getWritableDatabase().rawQuery("select * from " + Tax_Table + " order by "+Tax_Name, null);
    }

    public Cursor getActiveTax(){
        return getWritableDatabase().rawQuery("select * from " + Tax_Table + " where "+Tax_Active+"='Y' order by "+Tax_ID, null);
    }

    public Cursor getAllCatgory(){
        return getWritableDatabase().rawQuery("select * from " + Category_Table + " order by "+Category_Cat, null);
    }

    public Cursor getAllTable(){
        return getWritableDatabase().rawQuery("select * from " + Table_Table + " order by "+Table_Name, null);
    }

    public Cursor getProduct(int prod_cat){
        return getWritableDatabase().rawQuery("select * from " + Product_Table + " where "+Product_Cat+"=" + prod_cat +" order by "+Product_Name, null);
    }

    public Cursor getProduct(int prod_cat, String menuname){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from " + Product_Table + " where "+Product_Cat+"=" + prod_cat +" and "+Product_Name +" like '%" + menuname + "%' order by "+Product_Name, null);
    }

    public Cursor getProduct(){
        return getWritableDatabase().rawQuery("select * from " + Product_Table +" where "+Product_Active+"='Y' or "+Product_Active+"='A' order by "+Product_Name, null);
    }

    public Cursor getProductkArea(int i){
        return getWritableDatabase().rawQuery("select "+Product_ID+" from "+Product_Table+" where "+Product_KArea+"="+i, null);
    }

    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Table_Table);
    }

    public void deleteTable(String table_Name){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + table_Name);
    }

    public void deleteCategory(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Category_Table);
    }

    public void deleteProduct(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Product_Table);
    }

    public int getTempColumn(String qry){
        int id = 0;
        Cursor res = getWritableDatabase().rawQuery(qry,null);
        if(res.moveToFirst()){
            do{
                id = res.getInt(0);
            }while (res.moveToNext());
        }
        res.close();
        return ++id;
    }

    private int getCount(String qry){
        int id = 0;
        Cursor res = getWritableDatabase().rawQuery(qry,null);
        if(res.moveToFirst()){
            do{
                id = res.getInt(0);
            }while (res.moveToNext());
        }
        res.close();
        return id;
    }

    public int getMax(String tablename){
        int id = 0;
        Cursor res = getWritableDatabase().rawQuery("select max(ID) from "+tablename,null);
        if(res.moveToFirst()){
            do{
                id = res.getInt(0);
            }while (res.moveToNext());
        }
        res.close();
        return ++id;
    }

    public int getMaxId(String tablename){
        int id = 0;
        Cursor res = getWritableDatabase().rawQuery("select max(ID) from "+tablename,null);
        if(res.moveToFirst()){
            do{
                id = res.getInt(0);
            }while (res.moveToNext());
        }
        res.close();
        return id;
    }

    public int getTableMax(String tablename){
        int id = 0;
        Cursor res = getWritableDatabase().rawQuery("select max(ID) from "+tablename,null);
        if(res.moveToFirst()){
            do{
                id = res.getInt(0);
            }while (res.moveToNext());
        }
        res.close();
        return id;
    }

    public boolean isTableAlreadyPresent(String tablename){
        int tableid = 0;
        Cursor res = getWritableDatabase().query(Table_Table,new String[]{Table_ID},Table_Name+"=?",new String[]{tablename},null,null,null);
        if(res.moveToFirst()){
            do{
                tableid = res.getInt(0);
            }while (res.moveToNext());
        }
        res.close();
        return tableid != 0;
    }

    public boolean isCategoryAlreadyPresent(String categoryname){
        int catid = 0;
        String str = "SELECT "+Category_ID+" FROM Category WHERE "+Category_Cat+" like '"+categoryname+"'";
        //Cursor res = getWritableDatabase().query(Category_Table,new String[]{Category_ID},Category_Cat+" like=? ",new String[]{categoryname},null,null,null);
        Cursor res = getWritableDatabase().rawQuery(str,null);
        if(res.moveToFirst()){
            do{
                catid = res.getInt(0);
            }while (res.moveToNext());
        }
        res.close();
        return catid != 0;
    }

    public boolean isProductAlreadyPresent(String productname, String rate){
        int produId = 0;
        Cursor res = getWritableDatabase().query(Product_Table,new String[]{Product_ID},Product_Name+"=? and "+Product_Rate+"=?",new String[]{productname,rate},null,null,null);
        if(res.moveToFirst()){
            do{
                produId = res.getInt(0);
            }while (res.moveToNext());
        }
        res.close();
        return produId != 0;
    }

    public boolean isTaxAlreadyPresent(String taxname, String rate){
        int taxId = 0;
        Cursor res = getWritableDatabase().query(Tax_Table,new String[]{Tax_ID},Tax_Name+"=? and "+Tax_Rate+"=?",new String[]{taxname,rate},null,null,null);
        if(res.moveToFirst()){
            do{
                taxId = res.getInt(0);
            }while (res.moveToNext());
        }
        res.close();
        return taxId != 0;
    }

    public int getCategoryID(String categoryname){
        int catid = 0;
        Cursor res = getWritableDatabase().query(Category_Table,new String[]{Category_ID},Category_Cat+"=?",new String[]{categoryname},null,null,null);
        if(res.moveToFirst()){
            do{
                catid = res.getInt(0);
            }while (res.moveToNext());
        }
        res.close();
        return catid;
    }

    public Cursor getLocalRefreshData(int tableID){
        return getWritableDatabase().rawQuery("select * from "+TempTable_Table+" where "+TempTable_TableID+"="+tableID+" and Billst='N'",null);
    }

    public Cursor getBookedTableDataLocal(){
        return getWritableDatabase().rawQuery("select distinct "+TempTable_TableID+" from "+TempTable_Table+" where "+TempTable_BillST+"='N'",null);
    }

    public void updateProduct(String prodId, String prodName, String prodRate,ProductClass prod){
        ContentValues cv = new ContentValues();
        cv.put(Product_Name,prod.getProduct_Name());
        cv.put(Product_Rate,prod.getProduct_Rate());
        cv.put(Product_ModDate,getDate());
        cv.put(Product_ModTime,getTime());
        cv.put(Product_GSTGroup,prod.getGstGroup());
        cv.put(Product_TaxTyp,prod.getTaxType());
        cv.put(Product_Active,prod.getIsActive());
        getWritableDatabase().update(Product_Table,cv,Product_ID+"=?",new String[]{prodId});
    }

    private String getDate(){
        return  new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH).format(Calendar.getInstance().getTime());
    }

    private String getTime(){
        return new SimpleDateFormat("HH:mm",Locale.ENGLISH).format(Calendar.getInstance().getTime());
    }

    public void updateCategory(String catId, String catName, String _active){
        ContentValues cv = new ContentValues();
        cv.put(Category_Cat,catName);
        cv.put(Category_Active,_active);
        cv.put(Category_ModDate,getDate());
        cv.put(Category_ModTime,getTime());
        getWritableDatabase().update(Category_Table,cv,Category_ID+"=?",new String[]{catId});
    }

    public void updateTableName(String tableId, String tableName, String _active){
        ContentValues cv = new ContentValues();
        cv.put(Table_Name,tableName);
        cv.put(Table_Active,_active);
        cv.put(Table_ModDate,getDate());
        cv.put(Table_ModTime,getTime());
        getWritableDatabase().update(Table_Table,cv,Table_ID+"=?",new String[]{tableId});
    }

    public void updateTableGST(String tableId, String tableName, String gstgroup){
        ContentValues cv = new ContentValues();
        //cv.put(Table_Name,tableName);
        cv.put(Table_GSTGroup,gstgroup);
        cv.put(Table_ModDate,getDate());
        cv.put(Table_ModTime,getTime());
        getWritableDatabase().update(Table_Table,cv,Table_ID+"=?",new String[]{tableId});
    }

    public void updateTax(String taxId, String taxName,String taxRate, String _active){
        ContentValues cv = new ContentValues();
        cv.put(Tax_Name,taxName);
        cv.put(Tax_Rate,taxRate);
        cv.put(Tax_Active,_active);
        cv.put(Tax_ModDate,getDate());
        cv.put(Tax_ModTime,getTime());
        getWritableDatabase().update(Tax_Table,cv,Tax_ID+"=?",new String[]{taxId});
    }

    public Cursor getBillMaster(String fromDate, String toDate, boolean all){
        if(all) {
            return getWritableDatabase().query(BillMaster_Table, new String[]{BillMaster_BillNo, BillMaster_BillDate, BillMaster_BillTime, BillMaster_TotalAmt, BillMaster_CashAmt, BillMaster_CardAmt},null,null, null, null, BillMaster_BillTime);
        }else{
            return getWritableDatabase().query(BillMaster_Table, new String[]{BillMaster_BillNo, BillMaster_BillDate, BillMaster_BillTime, BillMaster_TotalAmt, BillMaster_CashAmt, BillMaster_CardAmt}, BillMaster_BillDate + ">=? and " + BillMaster_BillDate + "<=? ", new String[]{fromDate, toDate}, null, null, BillMaster_BillTime);
        }
    }

    public Cursor getItemwiseSale(String fromDate, String toDate, boolean all,String filter){
        String submainQry1 = "select "+Category_Table+"."+Category_Cat+","+Product_Table+"."+Product_Name+",Sum("+BillDetail_Table+"."+BillDetail_Qty+"),Sum("+BillDetail_Table+"."+BillDetail_Total+") from "+
                BillDetail_Table +" INNER JOIN "+Product_Table +" ON "+BillDetail_Table+"."+BillDetail_Itemid +"=" +Product_Table+"."+Product_ID +" INNER JOIN "+
                Category_Table +" ON "+Category_Table+"."+Category_ID +"="+Product_Table+"."+Product_Cat +
                " INNER JOIN "+ BillMaster_Table + " On "+BillMaster_Table+"."+BillMaster_Auto+"="+BillDetail_Table+"."+BillDetail_BillID;
        String subMainQry2 = " group by " + Product_Table + "." + Product_ID;
        String qry;
        if(all) {
            if(filter.equals("")) {
                 qry = submainQry1+subMainQry2;
            }else{
                qry = submainQry1+ " where 1=1 "+ filter + subMainQry2;
            }
        }else {
            String date_filter = BillMaster_Table + "." + BillMaster_BillDate + ">='" + fromDate + "' and " + BillMaster_Table + "." + BillMaster_BillDate + "<='" + toDate+"'";
            if (filter.equals("")) {
                qry = submainQry1 + " where 1=1 and " + date_filter + subMainQry2;
            } else {
                qry = submainQry1 + " where 1=1 " + filter + " and " + date_filter + subMainQry2;
            }
        }
        return getWritableDatabase().rawQuery(qry,null);
    }

    public Cursor setDropDown(String tableName, String colName){
        return getWritableDatabase().query(tableName, new String[]{colName},null,null, null, null, colName);
    }

    private int getMaxAuto(String tablename, String col){
        int id = 0;
        Cursor res = getWritableDatabase().rawQuery("select max("+col+") from "+tablename,null);
        if(res.moveToFirst()){
            do{
                id = res.getInt(0);
            }while (res.moveToNext());
        }
        res.close();
        return ++id;
    }

    public int addGSTMaster(GSTMasterClass gst){
        int id = getMaxAuto(GSTMaster_Table,GSTDetail_Auto);
        ContentValues cv = new ContentValues();
        cv.put(GSTMaster_Auto,id);
        cv.put(GSTMaster_GroupName,gst.getGroupName());
        cv.put(GSTMaster_Status,gst.getStatus());
        cv.put(GSTMaster_Remark,gst.getRemark());
        cv.put(GSTMaster_CrBy,gst.getCrby());
        cv.put(GSTMaster_CrDate,getDate());
        cv.put(GSTMaster_CrTime,getTime());
        cv.put(GSTMaster_EffFrom,gst.getEff_date());
        cv.put(GSTMaster_EffTo,"31/Dec/2050");
        getWritableDatabase().insert(GSTMaster_Table,null,cv);
        return id;
    }

    public void updateGSTMaster(GSTMasterClass gst,String mastAuto){
        ContentValues cv = new ContentValues();
        cv.put(GSTMaster_Status,gst.getStatus());
        cv.put(GSTMaster_EffFrom,gst.getEff_date());
        cv.put(GSTMaster_EffTo,"31/Dec/2050");
        getWritableDatabase().update(GSTMaster_Table,cv,GSTMaster_Auto+"=?",new String[]{mastAuto});
    }

    public void addGSTDetail(GSTDetailClass gstD){
        int id = getMaxAuto(GSTDetail_Table,GSTDetail_Auto);
        ContentValues cv = new ContentValues();
        cv.put(GSTDetail_Auto,id);
        cv.put(GSTDetail_MastAuto, gstD.getMastAuto());
        cv.put(GSTDetail_FromRange,gstD.getFromRange());
        cv.put(GSTDetail_ToRange,gstD.getToRange());
        cv.put(GSTDetail_GSTPer,gstD.getGstPer());
        cv.put(GSTDetail_CGSTPer,gstD.getCgstPer());
        cv.put(GSTDetail_SGSTPer,gstD.getSgstPer());
        cv.put(GSTDetail_CGSTShare,gstD.getCgstShare());
        cv.put(GSTDetail_SGSTShare,gstD.getSgstShare());
        cv.put(GSTDetail_CessPer,gstD.getCessPer());
        getWritableDatabase().insert(GSTDetail_Table,null,cv);
    }

    public void updateGSTDetail(GSTDetailClass gstD,String mastAuto,String detAuto){
        ContentValues cv = new ContentValues();
        cv.put(GSTDetail_MastAuto, gstD.getMastAuto());
        cv.put(GSTDetail_FromRange,gstD.getFromRange());
        cv.put(GSTDetail_ToRange,gstD.getToRange());
        cv.put(GSTDetail_GSTPer,gstD.getGstPer());
        cv.put(GSTDetail_CGSTPer,gstD.getCgstPer());
        cv.put(GSTDetail_SGSTPer,gstD.getSgstPer());
        cv.put(GSTDetail_CGSTShare,gstD.getCgstShare());
        cv.put(GSTDetail_SGSTShare,gstD.getSgstShare());
        cv.put(GSTDetail_CessPer,gstD.getCessPer());
        getWritableDatabase().update(GSTDetail_Table,cv,GSTDetail_MastAuto+"=? and "+GSTDetail_Auto+"=?",new String[]{mastAuto,detAuto});
    }

    public int isGroupNameAlreadyPresent(String groupname) {
        int groupid = 0;
        Cursor res = getWritableDatabase().query(GSTMaster_Table, new String[]{GSTMaster_Auto}, GSTMaster_GroupName + "=?", new String[]{groupname}, null, null, null);
        if (res.moveToFirst()) {
            do {
                groupid = res.getInt(0);
            } while (res.moveToNext());
        }
        res.close();
        return groupid;
    }

    public Cursor getActiveGSTGroup(){
        return getWritableDatabase().rawQuery("select * from " + GSTMaster_Table + " where "+GSTMaster_Status+"='Y' order by "+GSTMaster_GroupName, null);
    }

    public String getGSTGroupNameFromTable(String tableid, String toFromRange){
        String gstgropNameRate = "";
        String str = "select "+Table_Table+"."+Table_GSTGroup+","+ GSTDetail_Table+"."+GSTDetail_GSTPer
                +","+GSTDetail_Table+"."+GSTDetail_CGSTPer+","+GSTDetail_Table+"."+GSTDetail_SGSTPer
                +","+GSTDetail_Table+"."+GSTDetail_CessPer
                +" from "+Table_Table+","+GSTMaster_Table+","+GSTDetail_Table
                +" where "+Table_Table+"."+Table_GSTGroup +"="+ GSTMaster_Table+"."+GSTMaster_GroupName
                +" and "+GSTMaster_Table+"."+GSTMaster_Auto+"="+GSTDetail_Table+"."+GSTDetail_MastAuto
                +" and "+Table_Table+"."+Table_ID+"="+tableid
                +" and "+GSTMaster_Table+"."+GSTMaster_Status+"='Y'"
                +" and "+GSTDetail_Table+"."+GSTDetail_FromRange+"<="+toFromRange
                +" and "+GSTDetail_Table+"."+GSTDetail_ToRange+">="+toFromRange;

        Constant.showLog(str);
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if(res.moveToFirst()){
            do{
                //groupname-gstper-cgstper-sgstper-cessper
                gstgropNameRate = res.getString(0) + "-" + res.getString(1) + "-"
                        + res.getString(2) + "-" + res.getString(3) + "-" + res.getString(4);
            }while (res.moveToNext());
        }
        res.close();

        return gstgropNameRate;
    }

    public Cursor getGSTMastDet(){
        String str = "select "+GSTDetail_Table+"."+GSTDetail_Auto+","+GSTDetail_Table+"."+GSTDetail_MastAuto+","
                        +GSTDetail_Table+"."+GSTDetail_FromRange+","+GSTDetail_Table+"."+GSTDetail_ToRange+","
                        +GSTDetail_Table+"."+GSTDetail_GSTPer+","+GSTDetail_Table+"."+GSTDetail_CGSTPer+","
                        +GSTDetail_Table+"."+GSTDetail_SGSTPer+","+GSTDetail_Table+"."+GSTDetail_CGSTShare+","
                        +GSTDetail_Table+"."+GSTDetail_SGSTShare+","+GSTDetail_Table+"."+GSTDetail_CessPer
                        +","+GSTMaster_Table+"."+GSTMaster_GroupName
                        +" from "+GSTMaster_Table+","+GSTDetail_Table
                        +" where "+GSTMaster_Table+"."+GSTMaster_Auto+"="
                        +GSTDetail_Table+"."+GSTDetail_MastAuto;
        Constant.showLog(str);
        return getWritableDatabase().rawQuery(str,null);
    }

    public float getMinMaxGSTGroupRange(int mastAuto){
        float _toRange = 0;
        String str = "select "+GSTDetail_Table+"."+GSTDetail_ToRange
                +" from "+GSTDetail_Table
                +" where "+GSTDetail_MastAuto+"="+mastAuto
                +" order by "+GSTMaster_Auto+" asc";
        Cursor res = getWritableDatabase().rawQuery(str,null);
        if(res.moveToFirst()){
            do{
                _toRange = res.getFloat(0);
            }while (res.moveToNext());
        }
        res.close();
        return _toRange;
    }

    public float getMinMaxGSTGroupRange(String grNm){
        String str = "select "+GSTDetail_Table+"."+GSTDetail_FromRange+","+GSTDetail_Table+"."+GSTDetail_ToRange+
                " from "+GSTMaster_Table+","+GSTDetail_Table+" where "+GSTMaster_Table+"."+GSTMaster_Auto+"="+
                GSTDetail_Table+"."+GSTDetail_MastAuto+" and "+GSTMaster_GroupName+"='"+grNm+"' order by "+
                GSTDetail_Table+"."+GSTDetail_Auto+" asc";

        float _toRange = 0;
        Cursor res = getWritableDatabase().rawQuery(str,null);
        if(res.moveToFirst()){
            do{
                _toRange = res.getFloat(1);
            }while (res.moveToNext());
        }
        res.close();
        return _toRange;
    }

    public Cursor getGSTDetail(int mastAuto,int detAuto){
        //String str = "select * from "+GSTDetail_Table+" where "+GSTDetail_MastAuto+"="+mastAuto+" and "+GSTDetail_Auto+"="+detAuto;
        String str = "select "+GSTDetail_Table+"."+GSTDetail_Auto+","+GSTDetail_Table+"."+GSTDetail_MastAuto+","
                +GSTDetail_Table+"."+GSTDetail_FromRange+","+GSTDetail_Table+"."+GSTDetail_ToRange+","
                +GSTDetail_Table+"."+GSTDetail_GSTPer+","+GSTDetail_Table+"."+GSTDetail_CGSTPer+","
                +GSTDetail_Table+"."+GSTDetail_SGSTPer+","+GSTDetail_Table+"."+GSTDetail_CGSTShare+","
                +GSTDetail_Table+"."+GSTDetail_SGSTShare+","+GSTDetail_Table+"."+GSTDetail_CessPer
                +","+GSTMaster_Table+"."+GSTMaster_GroupName
                +","+GSTMaster_Table+"."+GSTMaster_Status
                +" from "+GSTMaster_Table+","+GSTDetail_Table
                +" where "+GSTMaster_Table+"."+GSTMaster_Auto+"="
                +GSTDetail_Table+"."+GSTDetail_MastAuto + " and "
                +GSTDetail_Table+"."+GSTDetail_MastAuto+"="+mastAuto+" and "+GSTDetail_Table+"."+GSTDetail_Auto+"="+detAuto;;
        return getWritableDatabase().rawQuery(str,null);
    }

    public Cursor getGSTGroupFromProduct(String prodName){
        String str = "select * from "+Product_Table+" where "+Product_Name+"='"+prodName+"'";
        return getWritableDatabase().rawQuery(str,null);
    }

    public Cursor getGSTPer(String gstGroup){
        String str = "select * from GSTMaster,GSTDetail where GSTMaster.Auto=GSTDetail.MastAuto and GSTMaster.GroupName='"+gstGroup+"'";
        return getWritableDatabase().rawQuery(str,null);
    }

    public List<String> getGSTGroup(){
        List<String> gstList = new ArrayList<>();
        String str = "select distinct "+GSTMaster_GroupName+" from "+GSTMaster_Table +" where "+GSTMaster_Status+"='Y'";
        Cursor res = getWritableDatabase().rawQuery(str,null);
        gstList.add("Select GSTGroup");
        if(res.moveToFirst()){
            do {
                gstList.add(res.getString(0));
            }while (res.moveToNext());
        }else{
            gstList.add("NA");
        }
        res.close();
        return gstList;
    }

    public Cursor getAllProduct(){
        return getWritableDatabase().rawQuery("select * from " + Product_Table +" order by "+Product_Name, null);
    }

    public String getCatgoryName(int catId){
        String cat = "";
        String str = "select "+Category_Cat+" from " + Category_Table + " where "+Category_ID+"="+catId;
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if(res.moveToFirst()){
            do{
                cat = res.getString(0);
            }while (res.moveToNext());
        }
        res.close();
        return cat;
    }

    public void inActivateItem(String masterType, String tableName, int id, String cat){
        ContentValues cv = new ContentValues();
        if(masterType.equals("product")) {
            cv.put(Product_Active, "N");
            getWritableDatabase().update(tableName, cv, Product_ID + "=? and " + Product_Cat + "=?", new String[]{String.valueOf(id), cat});
        }else if(masterType.equals("table")) {
            cv.put(Table_Active, "N");
            getWritableDatabase().update(tableName, cv, Table_ID + "=? and " + Table_Name + "=?", new String[]{String.valueOf(id), cat});
        }else if(masterType.equals("category")) {
            cv.put(Category_Active, "N");
            getWritableDatabase().update(tableName, cv, Category_ID + "=? and " + Category_Cat + "=?", new String[]{String.valueOf(id), cat});
        }
    }

    public int getMaxAuto(){
        int id = 0;
        Cursor res = getWritableDatabase().rawQuery("select max("+BillMaster_Auto+") from "+BillMaster_Table,null);
        if(res.moveToFirst()){
            do{
                id = res.getInt(0);
            }while (res.moveToNext());
        }
        res.close();
        return ++id;
    }

    public int getMaxMastId(String finYR){
        int id = 0;
        Cursor res = getWritableDatabase().rawQuery("select max("+BillMaster_Id+") from "+BillMaster_Table +" where "+BillMaster_Finyr+"='"+finYR+"'",null);
        if(res.moveToFirst()){
            do{
                id = res.getInt(0);
            }while (res.moveToNext());
        }
        res.close();
        return ++id;
    }

    public int getMaxDetAuto(){
        int id = 0;
        Cursor res = getWritableDatabase().rawQuery("select max("+BillDetail_Auto+") from "+BillDetail_Table,null);
        if(res.moveToFirst()){
            do{
                id = res.getInt(0);
            }while (res.moveToNext());
        }
        res.close();
        return ++id;
    }

    public int getMaxDetId(int mastAuto){
        int id = 0;
        Cursor res = getWritableDatabase().rawQuery("select max("+BillDetail_ID+") from "+BillDetail_Table +" where "+BillDetail_BillID+"="+mastAuto,null);
        if(res.moveToFirst()){
            do{
                id = res.getInt(0);
            }while (res.moveToNext());
        }
        res.close();
        return ++id;
    }

    public List<BillReprintCancelClass> getBillMasterData(String fromDate, String toData){
        //String  str = "select * from "+BillMaster_Table+" where "+BillMaster_CrDate+">='"+fromDate+"' and "+BillMaster_CrDate+"<='"+fromDate+"'";
        List<BillReprintCancelClass> list = new ArrayList<>();
        String  str = "select * from "+BillMaster_Table + " order by "+BillMaster_Auto +" desc";
        Cursor res = getWritableDatabase().rawQuery(str,null);
        if(res.moveToFirst()){
            do{
                BillReprintCancelClass bill = new BillReprintCancelClass();
                bill.setAuto(res.getInt(res.getColumnIndex(BillMaster_Auto)));
                bill.setBillNo(res.getString(res.getColumnIndex(BillMaster_BillNo)));
                bill.setStatus(res.getString(res.getColumnIndex(BillMaster_BillSt)));
                bill.setBillDate(res.getString(res.getColumnIndex(BillMaster_BillDate)));
                bill.setBillTime(res.getString(res.getColumnIndex(BillMaster_BillTime)));
                bill.setNetAmt(res.getString(res.getColumnIndex(BillMaster_NetAmt)));
                bill.setTableNo(res.getString(res.getColumnIndex(BillMaster_TableID)));
                bill.setCGSTAMNT(res.getString(res.getColumnIndex(BillMaster_CGSTAMT)));
                bill.setSGSTAMNT(res.getString(res.getColumnIndex(BillMaster_SGSTAMT)));
                list.add(bill);
            }while (res.moveToNext());
        }
        res.close();
        return list;
    }

    public void cancelBill(BillReprintCancelClass bill, String date){
        ContentValues cv = new ContentValues();
        cv.put(BillMaster_BillSt,"C");
        cv.put(BillMaster_ModBy,1);
        cv.put(BillMaster_ModDate,date);
        getWritableDatabase().update(BillMaster_Table,cv,BillMaster_Auto+"=? and "+BillMaster_BillNo+"=?",new String[]{String.valueOf(bill.getAuto()),bill.getBillNo()});
    }

    public List<BillDetailClassR> getBillDetailData(BillReprintCancelClass billM){
        //String  str = "select * from "+BillMaster_Table+" where "+BillMaster_CrDate+">='"+fromDate+"' and "+BillMaster_CrDate+"<='"+fromDate+"'";
        List<BillDetailClassR> list = new ArrayList<>();
        String  str = "select "+Product_Table+"."+Product_Name+" as Prod, "+BillDetail_Table+"."+BillDetail_Qty+","+
                BillDetail_Table+"."+BillDetail_Rate+","+BillDetail_Table+"."+BillDetail_Total+
                ","+BillDetail_Table+"."+BillDetail_CGSTPER+
                ","+BillDetail_Table+"."+BillDetail_SGSTPER+
                " from "+BillDetail_Table+","+Product_Table +
                " where "+BillDetail_Table+"."+BillDetail_Itemid+"="+Product_Table+"."+Product_ID
                +" and "+BillDetail_Table+"."+BillDetail_BillID+"="+billM.getAuto();
        Cursor res = getWritableDatabase().rawQuery(str,null);
        if(res.moveToFirst()){
            do{
                BillDetailClassR bill = new BillDetailClassR();
                bill.setProd(res.getString(res.getColumnIndex("Prod")));
                bill.setQty(res.getFloat(res.getColumnIndex(BillDetail_Qty)));
                bill.setRateStr(res.getString(res.getColumnIndex(BillDetail_Rate)));
                bill.setTotalStr(res.getString(res.getColumnIndex(BillDetail_Total)));
                bill.setCGSTPER(res.getFloat(res.getColumnIndex(BillDetail_CGSTPER)));
                bill.setSGSTPER(res.getFloat(res.getColumnIndex(BillDetail_SGSTPER)));
                list.add(bill);
            }while (res.moveToNext());
        }
        res.close();
        return list;
    }

    public void deleteMaster(int mastAuto){
        getWritableDatabase().execSQL("delete from "+BillMaster_Table+" where " + BillMaster_Auto+"="+mastAuto);
    }

    public void deleteDetail(int mastAuto){
        getWritableDatabase().execSQL("delete from "+BillDetail_Table+" where " + BillDetail_BillID+"="+mastAuto);
    }

    public Cursor getBillDetailData(int mastAuto){
        String str = "select * from "+BillDetail_Table +" where "+BillDetail_BillID+"="+mastAuto;
        return getWritableDatabase().rawQuery(str,null);
    }

    private int getMaxUBAuto(){
        int id = 0;
        Cursor res = getWritableDatabase().rawQuery("select max("+UB_Auto+") from "+UB_Table,null);
        if(res.moveToFirst()){
            do{
                id = res.getInt(0);
            }while (res.moveToNext());
        }
        res.close();
        return ++id;
    }

    public void addupdateBill(BillDetailClassR det){
        String date = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String time = new SimpleDateFormat("HH:mm",Locale.ENGLISH).format(Calendar.getInstance().getTime());
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(UB_Auto,getMaxUBAuto());
        cv.put(UB_BillNo,det.getBillNo());
        cv.put(UB_ProdId,det.getProd());
        cv.put(UB_OldQty,det.getQty());
        cv.put(UB_NewQty,det.getNewQty());
        cv.put(UB_OldRate,det.getRateStr());
        cv.put(UB_NewRate,det.getNewRate());
        cv.put(UB_ModBy,1);
        cv.put(UB_ModDate,date);
        cv.put(UB_ModTime,time);
        db.insert(UB_Table, null, cv);
    }

    public Cursor getUpdateBillData(String fromDate, String toDate, String all){
        String str = null;
        if(all.equals("A")) {
             str = "select * from " + UB_Table + " order by "+UB_Auto+" desc ";
        }
        return getWritableDatabase().rawQuery(str,null);
    }

    public String getProductNameFromId(int prodId){
        String prodName = "";
        String str = "select "+Product_Name+" from " + Product_Table +" where "+Product_ID+"="+prodId;
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if(res.moveToFirst()){
            do {
                prodName = res.getString(0);
            }while (res.moveToNext());
        }
        res.close();
        return prodName;
    }
}

