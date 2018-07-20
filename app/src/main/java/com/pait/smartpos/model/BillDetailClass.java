package com.pait.smartpos.model;

public class BillDetailClass {

    private float rate,qty, total, incentamt, vat, vatamt, amtWithoutDisc, disper, disamt, nonBar, ratewithtax,
            purchaseQty,freeqty, Mandalper, Mandal, MRP, billdisper, billdisamt, RetQty, ActRate, ActMRP,
            SchmBenefit,GSTPER, CGSTAMT, SGSTAMT, CGSTPER, SGSTPER, CESSPER, CESSAMT, IGSTAMT, TaxableAmt;

    private int auto, id, billID, branchID, itemId, empID, autoBillId, Deliveredby, Allotid, Schemeid, dcmastauto, Type, seqno;

    private String finYr, barcode, fatherSKU, mon, AlteredStat, Delivered, Delivereddate, BGType, SchemeApp, DispFSKU, DesignNo;

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getQty() {
        return qty;
    }

    public void setQty(float qty) {
        this.qty = qty;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getIncentamt() {
        return incentamt;
    }

    public void setIncentamt(float incentamt) {
        this.incentamt = incentamt;
    }

    public float getVat() {
        return vat;
    }

    public void setVat(float vat) {
        this.vat = vat;
    }

    public float getVatamt() {
        return vatamt;
    }

    public void setVatamt(float vatamt) {
        this.vatamt = vatamt;
    }

    public float getAmtWithoutDisc() {
        return amtWithoutDisc;
    }

    public void setAmtWithoutDisc(float amtWithoutDisc) {
        this.amtWithoutDisc = amtWithoutDisc;
    }

    public float getDisper() {
        return disper;
    }

    public void setDisper(float disper) {
        this.disper = disper;
    }

    public float getDisamt() {
        return disamt;
    }

    public void setDisamt(float disamt) {
        this.disamt = disamt;
    }

    public float getNonBar() {
        return nonBar;
    }

    public void setNonBar(float nonBar) {
        this.nonBar = nonBar;
    }

    public float getRatewithtax() {
        return ratewithtax;
    }

    public void setRatewithtax(float ratewithtax) {
        this.ratewithtax = ratewithtax;
    }

    public float getPurchaseQty() {
        return purchaseQty;
    }

    public void setPurchaseQty(float purchaseQty) {
        this.purchaseQty = purchaseQty;
    }

    public float getFreeqty() {
        return freeqty;
    }

    public void setFreeqty(float freeqty) {
        this.freeqty = freeqty;
    }

    public float getMandalper() {
        return Mandalper;
    }

    public void setMandalper(float mandalper) {
        Mandalper = mandalper;
    }

    public float getMandal() {
        return Mandal;
    }

    public void setMandal(float mandal) {
        Mandal = mandal;
    }

    public float getMRP() {
        return MRP;
    }

    public void setMRP(float MRP) {
        this.MRP = MRP;
    }

    public float getBilldisper() {
        return billdisper;
    }

    public void setBilldisper(float billdisper) {
        this.billdisper = billdisper;
    }

    public float getBilldisamt() {
        return billdisamt;
    }

    public void setBilldisamt(float billdisamt) {
        this.billdisamt = billdisamt;
    }

    public float getRetQty() {
        return RetQty;
    }

    public void setRetQty(float retQty) {
        RetQty = retQty;
    }

    public float getActRate() {
        return ActRate;
    }

    public void setActRate(float actRate) {
        ActRate = actRate;
    }

    public float getActMRP() {
        return ActMRP;
    }

    public void setActMRP(float actMRP) {
        ActMRP = actMRP;
    }

    public float getSchmBenefit() {
        return SchmBenefit;
    }

    public void setSchmBenefit(float schmBenefit) {
        SchmBenefit = schmBenefit;
    }

    public float getGSTPER() {
        return GSTPER;
    }

    public void setGSTPER(float GSTPER) {
        this.GSTPER = GSTPER;
    }

    public float getCGSTAMT() {
        return CGSTAMT;
    }

    public void setCGSTAMT(float CGSTAMT) {
        this.CGSTAMT = CGSTAMT;
    }

    public float getSGSTAMT() {
        return SGSTAMT;
    }

    public void setSGSTAMT(float SGSTAMT) {
        this.SGSTAMT = SGSTAMT;
    }

    public float getCGSTPER() {
        return CGSTPER;
    }

    public void setCGSTPER(float CGSTPER) {
        this.CGSTPER = CGSTPER;
    }

    public float getSGSTPER() {
        return SGSTPER;
    }

    public void setSGSTPER(float SGSTPER) {
        this.SGSTPER = SGSTPER;
    }

    public float getCESSPER() {
        return CESSPER;
    }

    public void setCESSPER(float CESSPER) {
        this.CESSPER = CESSPER;
    }

    public float getCESSAMT() {
        return CESSAMT;
    }

    public void setCESSAMT(float CESSAMT) {
        this.CESSAMT = CESSAMT;
    }

    public float getIGSTAMT() {
        return IGSTAMT;
    }

    public void setIGSTAMT(float IGSTAMT) {
        this.IGSTAMT = IGSTAMT;
    }

    public float getTaxableAmt() {
        return TaxableAmt;
    }

    public void setTaxableAmt(float taxableAmt) {
        TaxableAmt = taxableAmt;
    }

    public int getAuto() {
        return auto;
    }

    public void setAuto(int auto) {
        this.auto = auto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBillID() {
        return billID;
    }

    public void setBillID(int billID) {
        this.billID = billID;
    }

    public int getBranchID() {
        return branchID;
    }

    public void setBranchID(int branchID) {
        this.branchID = branchID;
    }

    public int getEmpID() {
        return empID;
    }

    public void setEmpID(int empID) {
        this.empID = empID;
    }

    public int getAutoBillId() {
        return autoBillId;
    }

    public void setAutoBillId(int autoBillId) {
        this.autoBillId = autoBillId;
    }

    public int getDeliveredby() {
        return Deliveredby;
    }

    public void setDeliveredby(int deliveredby) {
        Deliveredby = deliveredby;
    }

    public int getAllotid() {
        return Allotid;
    }

    public void setAllotid(int allotid) {
        Allotid = allotid;
    }

    public int getSchemeid() {
        return Schemeid;
    }

    public void setSchemeid(int schemeid) {
        Schemeid = schemeid;
    }

    public int getDcmastauto() {
        return dcmastauto;
    }

    public void setDcmastauto(int dcmastauto) {
        this.dcmastauto = dcmastauto;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getSeqno() {
        return seqno;
    }

    public void setSeqno(int seqno) {
        this.seqno = seqno;
    }

    public String getFinYr() {
        return finYr;
    }

    public void setFinYr(String finYr) {
        this.finYr = finYr;
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

    public String getMon() {
        return mon;
    }

    public void setMon(String mon) {
        this.mon = mon;
    }

    public String getAlteredStat() {
        return AlteredStat;
    }

    public void setAlteredStat(String alteredStat) {
        AlteredStat = alteredStat;
    }

    public String getDelivered() {
        return Delivered;
    }

    public void setDelivered(String delivered) {
        Delivered = delivered;
    }

    public String getDelivereddate() {
        return Delivereddate;
    }

    public void setDelivereddate(String delivereddate) {
        Delivereddate = delivereddate;
    }

    public String getBGType() {
        return BGType;
    }

    public void setBGType(String BGType) {
        this.BGType = BGType;
    }

    public String getSchemeApp() {
        return SchemeApp;
    }

    public void setSchemeApp(String schemeApp) {
        SchemeApp = schemeApp;
    }

    public String getDispFSKU() {
        return DispFSKU;
    }

    public void setDispFSKU(String dispFSKU) {
        DispFSKU = dispFSKU;
    }

    public String getDesignNo() {
        return DesignNo;
    }

    public void setDesignNo(String designNo) {
        DesignNo = designNo;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}
