package com.pait.smartpos.model;

public class ReturnMemoDetailClass {

    private float qty,rate,amt,disper,disamt,vatper,vatamt,MRP,billdisper,billdisamt,GSTPER,
            CGSTAMT,SGSTAMT,CGSTPER,SGSTPER,CESSPER,CESSAMT,IGSTAMT,TaxableAmt;

    private int id, itemcode, returnID, branchID, autoID, empid, mastid, BillDetAuto, dtlid;

    private String rMemoNo,barcode,financialyr,counterNo,nonbarst,itemName;

    public float getQty() {
        return qty;
    }

    public void setQty(float qty) {
        this.qty = qty;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getAmt() {
        return amt;
    }

    public void setAmt(float amt) {
        this.amt = amt;
    }

    public float getDisper() {
        return disper;
    }

    public void setDisper(float disper) {
        this.disper = disper;
    }

    public float getDisamt() {
        return disamt;
    }

    public void setDisamt(float disamt) {
        this.disamt = disamt;
    }

    public float getVatper() {
        return vatper;
    }

    public void setVatper(float vatper) {
        this.vatper = vatper;
    }

    public float getVatamt() {
        return vatamt;
    }

    public void setVatamt(float vatamt) {
        this.vatamt = vatamt;
    }

    public float getMRP() {
        return MRP;
    }

    public void setMRP(float MRP) {
        this.MRP = MRP;
    }

    public float getBilldisper() {
        return billdisper;
    }

    public void setBilldisper(float billdisper) {
        this.billdisper = billdisper;
    }

    public float getBilldisamt() {
        return billdisamt;
    }

    public void setBilldisamt(float billdisamt) {
        this.billdisamt = billdisamt;
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

    public float getIGSTAMT() {
        return IGSTAMT;
    }

    public void setIGSTAMT(float IGSTAMT) {
        this.IGSTAMT = IGSTAMT;
    }

    public float getTaxableAmt() {
        return TaxableAmt;
    }

    public void setTaxableAmt(float taxableAmt) {
        TaxableAmt = taxableAmt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemcode() {
        return itemcode;
    }

    public void setItemcode(int itemcode) {
        this.itemcode = itemcode;
    }

    public int getReturnID() {
        return returnID;
    }

    public void setReturnID(int returnID) {
        this.returnID = returnID;
    }

    public int getBranchID() {
        return branchID;
    }

    public void setBranchID(int branchID) {
        this.branchID = branchID;
    }

    public int getAutoID() {
        return autoID;
    }

    public void setAutoID(int autoID) {
        this.autoID = autoID;
    }

    public int getEmpid() {
        return empid;
    }

    public void setEmpid(int empid) {
        this.empid = empid;
    }

    public int getMastid() {
        return mastid;
    }

    public void setMastid(int mastid) {
        this.mastid = mastid;
    }

    public int getBillDetAuto() {
        return BillDetAuto;
    }

    public void setBillDetAuto(int billDetAuto) {
        BillDetAuto = billDetAuto;
    }

    public int getDtlid() {
        return dtlid;
    }

    public void setDtlid(int dtlid) {
        this.dtlid = dtlid;
    }

    public String getrMemoNo() {
        return rMemoNo;
    }

    public void setrMemoNo(String rMemoNo) {
        this.rMemoNo = rMemoNo;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getFinancialyr() {
        return financialyr;
    }

    public void setFinancialyr(String financialyr) {
        this.financialyr = financialyr;
    }

    public String getCounterNo() {
        return counterNo;
    }

    public void setCounterNo(String counterNo) {
        this.counterNo = counterNo;
    }

    public String getNonbarst() {
        return nonbarst;
    }

    public void setNonbarst(String nonbarst) {
        this.nonbarst = nonbarst;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
