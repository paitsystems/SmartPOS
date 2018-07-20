package com.pait.smartpos.model;

public class AddToCartClass {

    private String prodName, barcode, fatherSKU, dispFSKU, designNo, gstGroup, hsnCode;
    private float  qty, rate, total, amnt, mrp, actRate, actMRP;
    private int itemId;

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getAmnt() {
        return amnt;
    }

    public void setAmnt(float amnt) {
        this.amnt = amnt;
    }

    public float getQty() {
        return qty;
    }

    public void setQty(float qty) {
        this.qty = qty;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getFatherSKU() {
        return fatherSKU;
    }

    public void setFatherSKU(String fatherSKU) {
        this.fatherSKU = fatherSKU;
    }

    public String getDispFSKU() {
        return dispFSKU;
    }

    public void setDispFSKU(String dispFSKU) {
        this.dispFSKU = dispFSKU;
    }

    public String getDesignNo() {
        return designNo;
    }

    public void setDesignNo(String designNo) {
        this.designNo = designNo;
    }

    public String getGstGroup() {
        return gstGroup;
    }

    public void setGstGroup(String gstGroup) {
        this.gstGroup = gstGroup;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getMrp() {
        return mrp;
    }

    public void setMrp(float mrp) {
        this.mrp = mrp;
    }

    public float getActRate() {
        return actRate;
    }

    public void setActRate(float actRate) {
        this.actRate = actRate;
    }

    public float getActMRP() {
        return actMRP;
    }

    public void setActMRP(float actMRP) {
        this.actMRP = actMRP;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getHsnCode() {
        return hsnCode;
    }

    public void setHsnCode(String hsnCode) {
        this.hsnCode = hsnCode;
    }
}
