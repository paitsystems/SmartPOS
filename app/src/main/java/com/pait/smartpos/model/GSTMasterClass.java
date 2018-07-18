package com.pait.smartpos.model;

//Created by lnb on 7/31/2017.

public class GSTMasterClass {

    String groupName, remark, eff_date, status;
    int crby;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEff_date() {
        return eff_date;
    }

    public void setEff_date(String eff_date) {
        this.eff_date = eff_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCrby() {
        return crby;
    }

    public void setCrby(int crby) {
        this.crby = crby;
    }
}
