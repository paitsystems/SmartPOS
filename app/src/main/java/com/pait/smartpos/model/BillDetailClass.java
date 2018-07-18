package com.pait.smartpos.model;

//Created by lnb on 8/5/2017.

public class BillDetailClass {

    private int Auto,ID,BillID,BranchId,Itemid,DCMastAuto,WaiterID;

    private float Rate,Qty,Total,Vat,Vatamt,Disper,Disamt,
            RateWithTax,FreeQty,BillDiscper,BillDiscAmt,RetQty,
            AmtWithDisc,NetAmt,GSTPER,CGSTAMT,SGSTAMT,CGSTPER,
            SGSTPER,CESSPER,CESSAMT, newQty;

    private String Finyr,CmpSt,NonBar,Barcode,RateStr, TotalStr,AmtWithDiscStr,
            NetAmtStr,CGSTAMTStr,SGSTAMTStr,CESSAMTStr, prod, billNo, newRate;

    public int getAuto() {
        return Auto;
    }

    public void setAuto(int auto) {
        Auto = auto;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getBillID() {
        return BillID;
    }

    public void setBillID(int billID) {
        BillID = billID;
    }

    public int getBranchId() {
        return BranchId;
    }

    public void setBranchId(int branchId) {
        BranchId = branchId;
    }

    public int getItemid() {
        return Itemid;
    }

    public void setItemid(int itemid) {
        Itemid = itemid;
    }

    public int getDCMastAuto() {
        return DCMastAuto;
    }

    public void setDCMastAuto(int DCMastAuto) {
        this.DCMastAuto = DCMastAuto;
    }

    public int getWaiterID() {
        return WaiterID;
    }

    public void setWaiterID(int waiterID) {
        WaiterID = waiterID;
    }

    public float getRate() {
        return Rate;
    }

    public void setRate(float rate) {
        Rate = rate;
    }

    public float getQty() {
        return Qty;
    }

    public void setQty(float qty) {
        Qty = qty;
    }

    public float getTotal() {
        return Total;
    }

    public void setTotal(float total) {
        Total = total;
    }

    public float getVat() {
        return Vat;
    }

    public void setVat(float vat) {
        Vat = vat;
    }

    public float getVatamt() {
        return Vatamt;
    }

    public void setVatamt(float vatamt) {
        Vatamt = vatamt;
    }

    public float getDisper() {
        return Disper;
    }

    public void setDisper(float disper) {
        Disper = disper;
    }

    public float getDisamt() {
        return Disamt;
    }

    public void setDisamt(float disamt) {
        Disamt = disamt;
    }

    public float getRateWithTax() {
        return RateWithTax;
    }

    public void setRateWithTax(float rateWithTax) {
        RateWithTax = rateWithTax;
    }

    public float getFreeQty() {
        return FreeQty;
    }

    public void setFreeQty(float freeQty) {
        FreeQty = freeQty;
    }

    public float getBillDiscper() {
        return BillDiscper;
    }

    public void setBillDiscper(float billDiscper) {
        BillDiscper = billDiscper;
    }

    public float getBillDiscAmt() {
        return BillDiscAmt;
    }

    public void setBillDiscAmt(float billDiscAmt) {
        BillDiscAmt = billDiscAmt;
    }

    public float getRetQty() {
        return RetQty;
    }

    public void setRetQty(float retQty) {
        RetQty = retQty;
    }

    public float getAmtWithDisc() {
        return AmtWithDisc;
    }

    public void setAmtWithDisc(float amtWithDisc) {
        AmtWithDisc = amtWithDisc;
    }

    public float getNetAmt() {
        return NetAmt;
    }

    public void setNetAmt(float netAmt) {
        NetAmt = netAmt;
    }

    public float getGSTPER() {
        return GSTPER;
    }

    public void setGSTPER(float GSTPER) {
        this.GSTPER = GSTPER;
    }

    public float getCGSTAMT() {
        return CGSTAMT;
    }

    public void setCGSTAMT(float CGSTAMT) {
        this.CGSTAMT = CGSTAMT;
    }

    public float getSGSTAMT() {
        return SGSTAMT;
    }

    public void setSGSTAMT(float SGSTAMT) {
        this.SGSTAMT = SGSTAMT;
    }

    public float getCGSTPER() {
        return CGSTPER;
    }

    public void setCGSTPER(float CGSTPER) {
        this.CGSTPER = CGSTPER;
    }

    public float getSGSTPER() {
        return SGSTPER;
    }

    public void setSGSTPER(float SGSTPER) {
        this.SGSTPER = SGSTPER;
    }

    public float getCESSPER() {
        return CESSPER;
    }

    public void setCESSPER(float CESSPER) {
        this.CESSPER = CESSPER;
    }

    public float getCESSAMT() {
        return CESSAMT;
    }

    public void setCESSAMT(float CESSAMT) {
        this.CESSAMT = CESSAMT;
    }

    public String getFinyr() {
        return Finyr;
    }

    public void setFinyr(String finyr) {
        Finyr = finyr;
    }

    public String getCmpSt() {
        return CmpSt;
    }

    public void setCmpSt(String cmpSt) {
        CmpSt = cmpSt;
    }

    public String getNonBar() {
        return NonBar;
    }

    public void setNonBar(String nonBar) {
        NonBar = nonBar;
    }

    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public String getRateStr() {
        return RateStr;
    }

    public void setRateStr(String rateStr) {
        RateStr = rateStr;
    }

    public String getTotalStr() {
        return TotalStr;
    }

    public void setTotalStr(String totalStr) {
        TotalStr = totalStr;
    }

    public String getNetAmtStr() {
        return NetAmtStr;
    }

    public void setNetAmtStr(String netAmtStr) {
        NetAmtStr = netAmtStr;
    }

    public String getCGSTAMTStr() {
        return CGSTAMTStr;
    }

    public void setCGSTAMTStr(String CGSTAMTStr) {
        this.CGSTAMTStr = CGSTAMTStr;
    }

    public String getSGSTAMTStr() {
        return SGSTAMTStr;
    }

    public void setSGSTAMTStr(String SGSTAMTStr) {
        this.SGSTAMTStr = SGSTAMTStr;
    }

    public String getCESSAMTStr() {
        return CESSAMTStr;
    }

    public void setCESSAMTStr(String CESSAMTStr) {
        this.CESSAMTStr = CESSAMTStr;
    }

    public String getAmtWithDiscStr() {
        return AmtWithDiscStr;
    }

    public void setAmtWithDiscStr(String amtWithDiscStr) {
        AmtWithDiscStr = amtWithDiscStr;
    }

    public String getProd() {
        return prod;
    }

    public void setProd(String prod) {
        this.prod = prod;
    }

    public float getNewQty() {
        return newQty;
    }

    public void setNewQty(float newQty) {
        this.newQty = newQty;
    }

    public String getNewRate() {
        return newRate;
    }

    public void setNewRate(String newRate) {
        this.newRate = newRate;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }
}
