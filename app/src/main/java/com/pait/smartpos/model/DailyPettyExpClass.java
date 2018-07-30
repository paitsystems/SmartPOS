package com.pait.smartpos.model;

public class DailyPettyExpClass {

    private int Autoid,Branchid,AuthoBy,CreatedBy,cancleBy,AgainstRef,Id,CSType,ExpHead;

    private float Amount;

    private String Date,ToEmp,Remark,CreatedDate,Status,CancleDate,Cancelreason,AmtInWords,
            VouType,finyr,Voucherno,CounterNo,SubHead;

    public int getAutoid() {
        return Autoid;
    }

    public void setAutoid(int autoid) {
        Autoid = autoid;
    }

    public int getBranchid() {
        return Branchid;
    }

    public void setBranchid(int branchid) {
        Branchid = branchid;
    }

    public int getExpHead() {
        return ExpHead;
    }

    public void setExpHead(int expHead) {
        ExpHead = expHead;
    }

    public int getAuthoBy() {
        return AuthoBy;
    }

    public void setAuthoBy(int authoBy) {
        AuthoBy = authoBy;
    }

    public int getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(int createdBy) {
        CreatedBy = createdBy;
    }

    public int getCancleBy() {
        return cancleBy;
    }

    public void setCancleBy(int cancleBy) {
        this.cancleBy = cancleBy;
    }

    public int getAgainstRef() {
        return AgainstRef;
    }

    public void setAgainstRef(int againstRef) {
        AgainstRef = againstRef;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getCSType() {
        return CSType;
    }

    public void setCSType(int CSType) {
        this.CSType = CSType;
    }

    public float getAmount() {
        return Amount;
    }

    public void setAmount(float amount) {
        Amount = amount;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getToEmp() {
        return ToEmp;
    }

    public void setToEmp(String toEmp) {
        ToEmp = toEmp;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getCancleDate() {
        return CancleDate;
    }

    public void setCancleDate(String cancleDate) {
        CancleDate = cancleDate;
    }

    public String getCancelreason() {
        return Cancelreason;
    }

    public void setCancelreason(String cancelreason) {
        Cancelreason = cancelreason;
    }

    public String getAmtInWords() {
        return AmtInWords;
    }

    public void setAmtInWords(String amtInWords) {
        AmtInWords = amtInWords;
    }

    public String getVouType() {
        return VouType;
    }

    public void setVouType(String vouType) {
        VouType = vouType;
    }

    public String getFinyr() {
        return finyr;
    }

    public void setFinyr(String finyr) {
        this.finyr = finyr;
    }

    public String getVoucherno() {
        return Voucherno;
    }

    public void setVoucherno(String voucherno) {
        Voucherno = voucherno;
    }

    public String getCounterNo() {
        return CounterNo;
    }

    public void setCounterNo(String counterNo) {
        CounterNo = counterNo;
    }

    public String getSubHead() {
        return SubHead;
    }

    public void setSubHead(String subHead) {
        SubHead = subHead;
    }
}
