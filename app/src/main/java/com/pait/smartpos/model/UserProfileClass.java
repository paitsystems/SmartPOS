package com.pait.smartpos.model;

import java.io.Serializable;

public class UserProfileClass implements Serializable{

    private int registerValidFlag;
    private String userName, emailId, mobileNo, firmName, city, imeiNo,
            userid, OTP, expiryDate, startDate, userType, lastBackup, lastRestore, imgName, macAddress;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getImeiNo() {
        return imeiNo;
    }

    public void setImeiNo(String imeiNo) {
        this.imeiNo = imeiNo;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getOTP() {
        return OTP;
    }

    public void setOTP(String OTP) {
        this.OTP = OTP;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public int getRegisterValidFlag() {
        return registerValidFlag;
    }

    public void setRegisterValidFlag(int registerValidFlag) {
        this.registerValidFlag = registerValidFlag;
    }

    public String getLastBackup() {
        return lastBackup;
    }

    public void setLastBackup(String lastBackup) {
        this.lastBackup = lastBackup;
    }

    public String getLastRestore() {
        return lastRestore;
    }

    public void setLastRestore(String lastRestore) {
        this.lastRestore = lastRestore;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}
