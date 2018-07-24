package com.pait.smartpos.model;

// Created by Android on 1/15/2016.

public class ProductClass {

    private int Product_ID, Product_Cat, stockQty;
    private double Product_Rate;
    private float Pprice, mrp, Wprice, ssp;
    private String Product_Name, Product_Barcode, Product_KArea, gstGroup, taxType, isActive, hsnCode,
            cat1, cat2,cat3,finalProduct, dispFSKU, gstType;
    private boolean isSelected;

    public String getProduct_Name() {
        return Product_Name;
    }

    public void setProduct_Name(String product_Name) {
        Product_Name = product_Name;
    }

    public int getProduct_ID() {
        return Product_ID;
    }

    public void setProduct_ID(int product_ID) {
        Product_ID = product_ID;
    }

    public int getProduct_Cat() {
        return Product_Cat;
    }

    public void setProduct_Cat(int product_Cat) {
        Product_Cat = product_Cat;
    }

    public double getProduct_Rate() {
        return Product_Rate;
    }

    public void setProduct_Rate(double product_Rate) {
        Product_Rate = product_Rate;
    }

    public String getProduct_Barcode() {
        return Product_Barcode;
    }

    public void setProduct_Barcode(String product_Barcode) {
        Product_Barcode = product_Barcode;
    }

    public String getProduct_KArea() {
        return Product_KArea;
    }

    public void setProduct_KArea(String product_KArea) {
        Product_KArea = product_KArea;
    }

    public String getGstGroup() {
        return gstGroup;
    }

    public void setGstGroup(String gstGroup) {
        this.gstGroup = gstGroup;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public float getPprice() {
        return Pprice;
    }

    public void setPprice(float pprice) {
        Pprice = pprice;
    }

    public float getMrp() {
        return mrp;
    }

    public void setMrp(float mrp) {
        this.mrp = mrp;
    }

    public float getWprice() {
        return Wprice;
    }

    public void setWprice(float wprice) {
        Wprice = wprice;
    }

    public float getSsp() {
        return ssp;
    }

    public void setSsp(float ssp) {
        this.ssp = ssp;
    }

    public String getHsnCode() {
        return hsnCode;
    }

    public void setHsnCode(String hsnCode) {
        this.hsnCode = hsnCode;
    }

    public String getCat1() {
        return cat1;
    }

    public void setCat1(String cat1) {
        this.cat1 = cat1;
    }

    public String getCat2() {
        return cat2;
    }

    public void setCat2(String cat2) {
        this.cat2 = cat2;
    }

    public String getCat3() {
        return cat3;
    }

    public void setCat3(String cat3) {
        this.cat3 = cat3;
    }

    public String getFinalProduct() {
        return finalProduct;
    }

    public void setFinalProduct(String finalProduct) {
        this.finalProduct = finalProduct;
    }

    public String getDispFSKU() {
        return dispFSKU;
    }

    public void setDispFSKU(String dispFSKU) {
        this.dispFSKU = dispFSKU;
    }

    public int getStockQty() {
        return stockQty;
    }

    public void setStockQty(int stockQty) {
        this.stockQty = stockQty;
    }

    public String getGstType() {
        return gstType;
    }

    public void setGstType(String gstType) {
        this.gstType = gstType;
    }
}
