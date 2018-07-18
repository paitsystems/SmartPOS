package com.pait.smartpos.model;// Created by anup on 5/20/2017.

public class TaxClass {

    public int Tax_ID;
    public String TaxName;
    public float TaxRate;
    public String isActive;

    public int getTax_ID() {
        return Tax_ID;
    }

    public void setTax_ID(int tax_ID) {
        Tax_ID = tax_ID;
    }

    public String getTaxName() {
        return TaxName;
    }

    public void setTaxName(String taxName) {
        TaxName = taxName;
    }

    public float getTaxRate() {
        return TaxRate;
    }

    public void setTaxRate(float taxRate) {
        TaxRate = taxRate;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
}
