package com.pait.smartpos.model;

public class RateMasterClass {

    private String rateStr, active;
    private boolean isSelected;
    private float rate;
    private int auto;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getRateStr() {
        return rateStr;
    }

    public void setRateStr(String rateStr) {
        this.rateStr = rateStr;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public int getAuto() {
        return auto;
    }

    public void setAuto(int auto) {
        this.auto = auto;
    }
}
