package com.pait.smartpos.model;

// Created by Android on 1/15/2016.

public class ProductClass {

    private int Product_ID, Product_Cat;
    private double Product_Rate;
    private String Product_Name, Product_Barcode, Product_KArea, gstGroup, taxType, isActive;
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
}
