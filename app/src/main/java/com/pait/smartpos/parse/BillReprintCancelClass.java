package com.pait.smartpos.parse;

public class BillReprintCancelClass {

    private int auto, qty;
    private String billNo, status, billDate, billTime, CGSTAMNT, SGSTAMNT, netAmt, tableNo, billAmnt, custId;

    public int getAuto() {
        return auto;
    }

    public void setAuto(int auto) {
        this.auto = auto;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getBillTime() {
        return billTime;
    }

    public void setBillTime(String billTime) {
        this.billTime = billTime;
    }

    public String getCGSTAMNT() {
        return CGSTAMNT;
    }

    public void setCGSTAMNT(String CGSTAMNT) {
        this.CGSTAMNT = CGSTAMNT;
    }

    public String getSGSTAMNT() {
        return SGSTAMNT;
    }

    public void setSGSTAMNT(String SGSTAMNT) {
        this.SGSTAMNT = SGSTAMNT;
    }

    public String getNetAmt() {
        return netAmt;
    }

    public void setNetAmt(String netAmt) {
        this.netAmt = netAmt;
    }

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    public String getBillAmnt() {
        return billAmnt;
    }

    public void setBillAmnt(String billAmnt) {
        this.billAmnt = billAmnt;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }
}
