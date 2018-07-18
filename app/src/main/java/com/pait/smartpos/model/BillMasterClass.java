package com.pait.smartpos.model;

//Created by lnb on 8/5/2017.

public class BillMasterClass {

    private int auto, id,branchId,CustID,CrBy,ModBy,BankID,PrintNO,GVoucher,DCAutoNo,CancelBy,
            TableID,LocationID,SalesMan,WaiterID,NoOfPass;

    private float TotalQty,TotalAmt,CardAmt,CashAmt,CoupanAmt,PaidAmt,BalAmt,NetAmt,Vat12,Vat5,
            Chqamt,Advamt,Disper,DisAmt,PIAmt,GVoucherAmt,GrossAmt,GoodsReturn,Tender,RemainAmt,
            TRetQty,ServiceTax,ServiceAmt,Vatamt,Taxper,VatPer,TaxAmt,OtherAmt,CGSTAMT,SGSTAMT;

    private String Finyr,BillNo,BillDate,BillTime,BillSt,CrDate,ModDate,CardNo,ChqNo,Chqdt,
            paymenttype,BillPaySt,AmtInWords,refundpyst,PIno,CounterNo,MachineName,AgainstDC,
            Remark,CancelDate,CancelReason,KOTNo,Taxst,TotalAmtStr,CGSTAMTStr,SGSTAMTStr;

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

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getCustID() {
        return CustID;
    }

    public void setCustID(int custID) {
        CustID = custID;
    }

    public int getCrBy() {
        return CrBy;
    }

    public void setCrBy(int crBy) {
        CrBy = crBy;
    }

    public int getModBy() {
        return ModBy;
    }

    public void setModBy(int modBy) {
        ModBy = modBy;
    }

    public int getBankID() {
        return BankID;
    }

    public void setBankID(int bankID) {
        BankID = bankID;
    }

    public int getPrintNO() {
        return PrintNO;
    }

    public void setPrintNO(int printNO) {
        PrintNO = printNO;
    }

    public int getGVoucher() {
        return GVoucher;
    }

    public void setGVoucher(int GVoucher) {
        this.GVoucher = GVoucher;
    }

    public int getDCAutoNo() {
        return DCAutoNo;
    }

    public void setDCAutoNo(int DCAutoNo) {
        this.DCAutoNo = DCAutoNo;
    }

    public int getCancelBy() {
        return CancelBy;
    }

    public void setCancelBy(int cancelBy) {
        CancelBy = cancelBy;
    }

    public int getTableID() {
        return TableID;
    }

    public void setTableID(int tableID) {
        TableID = tableID;
    }

    public int getLocationID() {
        return LocationID;
    }

    public void setLocationID(int locationID) {
        LocationID = locationID;
    }

    public int getSalesMan() {
        return SalesMan;
    }

    public void setSalesMan(int salesMan) {
        SalesMan = salesMan;
    }

    public int getWaiterID() {
        return WaiterID;
    }

    public void setWaiterID(int waiterID) {
        WaiterID = waiterID;
    }

    public int getNoOfPass() {
        return NoOfPass;
    }

    public void setNoOfPass(int noOfPass) {
        NoOfPass = noOfPass;
    }

    public float getTotalQty() {
        return TotalQty;
    }

    public void setTotalQty(float totalQty) {
        TotalQty = totalQty;
    }

    public float getTotalAmt() {
        return TotalAmt;
    }

    public void setTotalAmt(float totalAmt) {
        TotalAmt = totalAmt;
    }

    public float getCardAmt() {
        return CardAmt;
    }

    public void setCardAmt(float cardAmt) {
        CardAmt = cardAmt;
    }

    public float getCashAmt() {
        return CashAmt;
    }

    public void setCashAmt(float cashAmt) {
        CashAmt = cashAmt;
    }

    public float getCoupanAmt() {
        return CoupanAmt;
    }

    public void setCoupanAmt(float coupanAmt) {
        CoupanAmt = coupanAmt;
    }

    public float getPaidAmt() {
        return PaidAmt;
    }

    public void setPaidAmt(float paidAmt) {
        PaidAmt = paidAmt;
    }

    public float getBalAmt() {
        return BalAmt;
    }

    public void setBalAmt(float balAmt) {
        BalAmt = balAmt;
    }

    public float getNetAmt() {
        return NetAmt;
    }

    public void setNetAmt(float netAmt) {
        NetAmt = netAmt;
    }

    public float getVat12() {
        return Vat12;
    }

    public void setVat12(float vat12) {
        Vat12 = vat12;
    }

    public float getVat5() {
        return Vat5;
    }

    public void setVat5(float vat5) {
        Vat5 = vat5;
    }

    public float getChqamt() {
        return Chqamt;
    }

    public void setChqamt(float chqamt) {
        Chqamt = chqamt;
    }

    public float getAdvamt() {
        return Advamt;
    }

    public void setAdvamt(float advamt) {
        Advamt = advamt;
    }

    public float getDisper() {
        return Disper;
    }

    public void setDisper(float disper) {
        Disper = disper;
    }

    public float getDisAmt() {
        return DisAmt;
    }

    public void setDisAmt(float disAmt) {
        DisAmt = disAmt;
    }

    public float getPIAmt() {
        return PIAmt;
    }

    public void setPIAmt(float PIAmt) {
        this.PIAmt = PIAmt;
    }

    public float getGVoucherAmt() {
        return GVoucherAmt;
    }

    public void setGVoucherAmt(float GVoucherAmt) {
        this.GVoucherAmt = GVoucherAmt;
    }

    public float getGrossAmt() {
        return GrossAmt;
    }

    public void setGrossAmt(float grossAmt) {
        GrossAmt = grossAmt;
    }

    public float getGoodsReturn() {
        return GoodsReturn;
    }

    public void setGoodsReturn(float goodsReturn) {
        GoodsReturn = goodsReturn;
    }

    public float getTender() {
        return Tender;
    }

    public void setTender(float tender) {
        Tender = tender;
    }

    public float getRemainAmt() {
        return RemainAmt;
    }

    public void setRemainAmt(float remainAmt) {
        RemainAmt = remainAmt;
    }

    public float getTRetQty() {
        return TRetQty;
    }

    public void setTRetQty(float TRetQty) {
        this.TRetQty = TRetQty;
    }

    public float getServiceTax() {
        return ServiceTax;
    }

    public void setServiceTax(float serviceTax) {
        ServiceTax = serviceTax;
    }

    public float getServiceAmt() {
        return ServiceAmt;
    }

    public void setServiceAmt(float serviceAmt) {
        ServiceAmt = serviceAmt;
    }

    public float getVatamt() {
        return Vatamt;
    }

    public void setVatamt(float vatamt) {
        Vatamt = vatamt;
    }

    public float getTaxper() {
        return Taxper;
    }

    public void setTaxper(float taxper) {
        Taxper = taxper;
    }

    public float getVatPer() {
        return VatPer;
    }

    public void setVatPer(float vatPer) {
        VatPer = vatPer;
    }

    public float getTaxAmt() {
        return TaxAmt;
    }

    public void setTaxAmt(float taxAmt) {
        TaxAmt = taxAmt;
    }

    public float getOtherAmt() {
        return OtherAmt;
    }

    public void setOtherAmt(float otherAmt) {
        OtherAmt = otherAmt;
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

    public String getFinyr() {
        return Finyr;
    }

    public void setFinyr(String finyr) {
        Finyr = finyr;
    }

    public String getBillNo() {
        return BillNo;
    }

    public void setBillNo(String billNo) {
        BillNo = billNo;
    }

    public String getBillDate() {
        return BillDate;
    }

    public void setBillDate(String billDate) {
        BillDate = billDate;
    }

    public String getBillTime() {
        return BillTime;
    }

    public void setBillTime(String billTime) {
        BillTime = billTime;
    }

    public String getBillSt() {
        return BillSt;
    }

    public void setBillSt(String billSt) {
        BillSt = billSt;
    }

    public String getCrDate() {
        return CrDate;
    }

    public void setCrDate(String crDate) {
        CrDate = crDate;
    }

    public String getModDate() {
        return ModDate;
    }

    public void setModDate(String modDate) {
        ModDate = modDate;
    }

    public String getCardNo() {
        return CardNo;
    }

    public void setCardNo(String cardNo) {
        CardNo = cardNo;
    }

    public String getChqNo() {
        return ChqNo;
    }

    public void setChqNo(String chqNo) {
        ChqNo = chqNo;
    }

    public String getChqdt() {
        return Chqdt;
    }

    public void setChqdt(String chqdt) {
        Chqdt = chqdt;
    }

    public String getPaymenttype() {
        return paymenttype;
    }

    public void setPaymenttype(String paymenttype) {
        this.paymenttype = paymenttype;
    }

    public String getBillPaySt() {
        return BillPaySt;
    }

    public void setBillPaySt(String billPaySt) {
        BillPaySt = billPaySt;
    }

    public String getAmtInWords() {
        return AmtInWords;
    }

    public void setAmtInWords(String amtInWords) {
        AmtInWords = amtInWords;
    }

    public String getRefundpyst() {
        return refundpyst;
    }

    public void setRefundpyst(String refundpyst) {
        this.refundpyst = refundpyst;
    }

    public String getPIno() {
        return PIno;
    }

    public void setPIno(String PIno) {
        this.PIno = PIno;
    }

    public String getCounterNo() {
        return CounterNo;
    }

    public void setCounterNo(String counterNo) {
        CounterNo = counterNo;
    }

    public String getMachineName() {
        return MachineName;
    }

    public void setMachineName(String machineName) {
        MachineName = machineName;
    }

    public String getAgainstDC() {
        return AgainstDC;
    }

    public void setAgainstDC(String againstDC) {
        AgainstDC = againstDC;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getCancelDate() {
        return CancelDate;
    }

    public void setCancelDate(String cancelDate) {
        CancelDate = cancelDate;
    }

    public String getCancelReason() {
        return CancelReason;
    }

    public void setCancelReason(String cancelReason) {
        CancelReason = cancelReason;
    }

    public String getKOTNo() {
        return KOTNo;
    }

    public void setKOTNo(String KOTNo) {
        this.KOTNo = KOTNo;
    }

    public String getTaxst() {
        return Taxst;
    }

    public void setTaxst(String taxst) {
        Taxst = taxst;
    }

    public String getCGSTAMTStr() {
        return CGSTAMTStr;
    }

    public void setCGSTAMTStr(String CGSTAMTStr) {
        this.CGSTAMTStr = CGSTAMTStr;
    }

    public String getSGSTAMTStr() {
        return SGSTAMTStr;
    }

    public void setSGSTAMTStr(String SGSTAMTStr) {
        this.SGSTAMTStr = SGSTAMTStr;
    }

    public String getTotalAmtStr() {
        return TotalAmtStr;
    }

    public void setTotalAmtStr(String totalAmtStr) {
        TotalAmtStr = totalAmtStr;
    }
}
