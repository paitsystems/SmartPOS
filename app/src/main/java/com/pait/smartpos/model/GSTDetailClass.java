package com.pait.smartpos.model;

//Created by lnb on 7/31/2017.

public class GSTDetailClass {

    private int mastAuto, auto;
    private float fromRange, toRange, gstPer, cgstPer, sgstPer, cgstShare, sgstShare, cessPer;

    public int getAuto() {
        return auto;
    }

    public void setAuto(int auto) {
        this.auto = auto;
    }

    public int getMastAuto() {
        return mastAuto;
    }

    public void setMastAuto(int mastAuto) {
        this.mastAuto = mastAuto;
    }

    public float getFromRange() {
        return fromRange;
    }

    public void setFromRange(float fromRange) {
        this.fromRange = fromRange;
    }

    public float getToRange() {
        return toRange;
    }

    public void setToRange(float toRange) {
        this.toRange = toRange;
    }

    public float getGstPer() {
        return gstPer;
    }

    public void setGstPer(float gstPer) {
        this.gstPer = gstPer;
    }

    public float getCgstPer() {
        return cgstPer;
    }

    public void setCgstPer(float cgstPer) {
        this.cgstPer = cgstPer;
    }

    public float getSgstPer() {
        return sgstPer;
    }

    public void setSgstPer(float sgstPer) {
        this.sgstPer = sgstPer;
    }

    public float getCgstShare() {
        return cgstShare;
    }

    public void setCgstShare(float cgstShare) {
        this.cgstShare = cgstShare;
    }

    public float getSgstShare() {
        return sgstShare;
    }

    public void setSgstShare(float sgstShare) {
        this.sgstShare = sgstShare;
    }

    public float getCessPer() {
        return cessPer;
    }

    public void setCessPer(float cessPer) {
        this.cessPer = cessPer;
    }

}
