package com.pait.smartpos.model;

//Created by ANUP on 01-06-2018.

import java.io.Serializable;

public class MasterUpdationClass implements Serializable{

    private int masterAuto, masterCatId;
    private String isMasterActive, masterName, masterRate, masterCat, masterGSTGroup, masterTaxType, masterType;

    public int getMasterAuto() {
        return masterAuto;
    }

    public void setMasterAuto(int masterAuto) {
        this.masterAuto = masterAuto;
    }

    public String getIsMasterActive() {
        return isMasterActive;
    }

    public void setIsMasterActive(String isMasterActive) {
        this.isMasterActive = isMasterActive;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getMasterType() {
        return masterType;
    }

    public void setMasterType(String masterType) {
        this.masterType = masterType;
    }

    public String getMasterRate() {
        return masterRate;
    }

    public void setMasterRate(String masterRate) {
        this.masterRate = masterRate;
    }

    public String getMasterCat() {
        return masterCat;
    }

    public void setMasterCat(String masterCat) {
        this.masterCat = masterCat;
    }

    public int getMasterCatId() {
        return masterCatId;
    }

    public void setMasterCatId(int masterCatId) {
        this.masterCatId = masterCatId;
    }

    public String getMasterGSTGroup() {
        return masterGSTGroup;
    }

    public void setMasterGSTGroup(String masterGSTGroup) {
        this.masterGSTGroup = masterGSTGroup;
    }

    public String getMasterTaxType() {
        return masterTaxType;
    }

    public void setMasterTaxType(String masterTaxType) {
        this.masterTaxType = masterTaxType;
    }
}
