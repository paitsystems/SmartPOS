package com.pait.smartpos.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.model.ExpenseDetail;

import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    //TODO: Check Database Version
    public static final String Database_Name = "SmartPOS.db";
    public static final int Database_Version = 1;

    public static final String Table_CompanyMaster = "CompanyMaster";
    public static final String CPM_Auto = "Auto";
    public static final String CPM_Id = "Id";
    public static final String CPM_CompanyName = "CompanyName";
    public static final String CPM_Mgrname = "Mgrname";
    public static final String CPM_City = "City";
    public static final String CPM_State = "State";
    public static final String CPM_Address = "Address";
    public static final String CPM_Phone = "Phone";
    public static final String CPM_Altpnone = "Altpnone";
    public static final String CPM_Email = "Email";
    public static final String CPM_Vatno = "Vatno";
    public static final String CPM_Cstno = "Cstno";
    public static final String CPM_Websitename = "Websitename";
    public static final String CPM_Createby = "Createby";
    public static final String CPM_Createdt = "Createdt";
    public static final String CPM_Initials = "Initials";
    public static final String CPM_Modifiedby = "Modifiedby";
    public static final String CPM_Modifieddate = "Modifieddate";
    public static final String CPM_Cancelby = "Cancelby";
    public static final String CPM_Canceldate = "Canceldate";
    public static final String CPM_Com_Identity = "Com_Identity";
    public static final String CPM_Com_Type = "Com_Type";
    public static final String CPM_Img = "Img";
    public static final String CPM_License = "License";
    public static final String CPM_PrintMsg = "PrintMsg";
    public static final String CPM_Ddecomm = "Ddecomm";
    public static final String CPM_Hocode = "Hocode";
    public static final String CPM_Negbill = "Negbill";
    public static final String CPM_Ipadd = "Ipadd";
    public static final String CPM_Imagepath = "Imagepath";
    public static final String CPM_Imagepathlocal = "Imagepathlocal";
    public static final String CPM_Othbranchadd = "Othbranchadd";
    public static final String CPM_PrintName = "PrintName";
    public static final String CPM_Applylbt = "Applylbt";
    public static final String CPM_Lbtno = "Lbtno";
    public static final String CPM_Logcolor = "Logcolor";
    public static final String CPM_Clientid = "Clientid";
    public static final String CPM_Txtype = "Txtype";
    public static final String CPM_Discountvat = "Discountvat";
    public static final String CPM_Transittime = "Transittime";
    public static final String CPM_Saleup = "Saleup";
    public static final String CPM_Ledgerbal = "Ledgerbal";
    public static final String CPM_Enddays = "Enddays";
    public static final String CPM_PANNo = "PANNo";
    public static final String CPM_Dcode = "Dcode";
    public static final String CPM_GSTNo = "GSTNo";
    public static final String CPM_Tendoramtcomp = "Tendoramtcomp";
    public static final String CPM_MailUname = "MailUname";
    public static final String CPM_MailPwd = "MailPwd";
    public static final String CPM_Mailfrom = "Mailfrom";
    public static final String CPM_SMTPServer = "SMTPServer";
    public static final String CPM_Cardpointvalue = "Cardpointvalue";
    public static final String CPM_Ctype = "Ctype";
    public static final String CPM_Printmsgcomp = "Printmsgcomp";
    public static final String CPM_Nprint = "Nprint";
    public static final String CPM_Colsizecomp = "Colsizecomp";
    public static final String CPM_Bartyp = "Bartyp";
    public static final String CPM_Wantmrp = "Wantmrp";
    public static final String CPM_Barcodecount = "Barcodecount";
    public static final String CPM_Stdcack = "Stdcack";
    public static final String CPM_Custdeftyp = "Custdeftyp";
    public static final String CPM_Gstdisc = "Gstdisc";
    public static final String CPM_Prodeftyp = "Prodeftyp";
    public static final String CPM_Attendcodet = "Attendcodet";
    public static final String CPM_Tokeny = "Tokeny";
    public static final String CPM_Gstex = "Gstex";
    public static final String CPM_Returnprintmessage = "Returnprintmessage";
    public static final String CPM_Prtype = "Prtype";
    public static final String CPM_Admin_Password = "Admin_Password";

    public static final String Table_EmployeeMaster = "EmployeeMaster";
    public static final String EM_Auto = "Auto";
    public static final String EM_Id = "Id";
    public static final String EM_Branchcode = "Branchcode";
    public static final String EM_Dept = "Dept";
    public static final String EM_Subdepet = "Subdepet";
    public static final String EM_Empname = "Empname";
    public static final String EM_Address = "Address";
    public static final String EM_Empdob = "Empdob";
    public static final String EM_Applicabledob = "Applicabledob";
    public static final String EM_Joindt = "Joindt";
    public static final String EM_Resgdt = "Resgdt";
    public static final String EM_Phone = "Phone";
    public static final String EM_Mobile = "Mobile";
    public static final String EM_Designation = "Designation";
    public static final String EM_Roleid = "Roleid";
    public static final String EM_Salary = "Salary";
    public static final String EM_Incentive = "Incentive";
    public static final String EM_Active = "Active";
    public static final String EM_Identitytype = "Identitytype";
    public static final String EM_Identityvalue = "Identityvalue";
    public static final String EM_Usernm = "Usernm";
    public static final String EM_Pass = "Pass";
    public static final String EM_Createdby = "Createdby";
    public static final String EM_Createddate = "Createddate";
    public static final String EM_Modifiedby = "Modifiedby";
    public static final String EM_Modifieddate = "Modifieddate";
    public static final String EM_Cancelby = "Cancelby";
    public static final String EM_Canceldate = "Canceldate";
    public static final String EM_Bloodgroup = "Bloodgroup";
    public static final String EM_Gender = "Gender";
    public static final String EM_Shift = "Shift";
    public static final String EM_Weekelyoff = "Weekelyoff";
    public static final String EM_Pfno = "Pfno";
    public static final String EM_Esino = "Esino";
    public static final String EM_Accountno = "Accountno";
    public static final String EM_Emp_Id = "Emp_Id";
    public static final String EM_Empini = "Empini";
    public static final String EM_Srno = "Srno";
    public static final String EM_Sections = "Sections";
    public static final String EM_Contract = "Contract";
    public static final String EM_Basic = "Basic";
    public static final String EM_Da = "Da";
    public static final String EM_Hra = "Hra";
    public static final String EM_Others = "Others";
    public static final String EM_Spallow = "Spallow";
    public static final String EM_Washcharge = "Washcharge";
    public static final String EM_Retamt = "Retamt";
    public static final String EM_Remark = "Remark";
    public static final String EM_Retledger = "Retledger";
    public static final String EM_Emp_Type = "Emp_Type";
    public static final String EM_Bonus_Type = "Bonus_Type";
    public static final String EM_Bonus_Amt = "Bonus_Amt";
    public static final String EM_Emptype = "Emptype";
    public static final String EM_Imagepath = "Imagepath";
    public static final String EM_Authorised = "Authorised";
    public static final String EM_Cstype = "Cstype";
    public static final String EM_Points = "Points";
    public static final String EM_Awp = "Awp";
    public static final String EM_Cityid = "Cityid";
    public static final String EM_Taskapp = "Taskapp";
    public static final String EM_Intime = "Intime";
    public static final String EM_Outtime = "Outtime";
    public static final String EM_Docpath = "Docpath";
    public static final String EM_Email = "Email";
    public static final String EM_Bonusledger = "Bonusledger";
    public static final String EM_Bankid = "Bankid";
    public static final String EM_Resproof = "Resproof";
    public static final String EM_Residno = "Residno";
    public static final String EM_Permaddress = "Permaddress";
    public static final String EM_Empesiname = "Empesiname";
    public static final String EM_Desc1 = "Desc1";
    public static final String EM_Plbalance = "Plbalance";
    public static final String EM_Clbalance = "Clbalance";
    public static final String EM_Pwd = "Pwd";

    public static final String Table_ProductMaster = "ProductMaster";
    public static final String PM_Id = "Id";
    public static final String PM_Cat1 = "Cat1";
    public static final String PM_Cat2 = "Cat2";
    public static final String PM_Cat3 = "Cat3";
    public static final String PM_Cat4 = "Cat4";
    public static final String PM_Cat5 = "Cat5";
    public static final String PM_Cat6 = "Cat6";
    public static final String PM_Itemname = "Itemname";
    public static final String PM_Finalproduct = "Finalproduct";
    public static final String PM_Unit = "Unit";
    public static final String PM_Type = "Type";
    public static final String PM_Pprice = "Pprice";
    public static final String PM_Mrp = "Mrp";
    public static final String PM_Wprice = "Wprice";
    public static final String PM_Vat = "Vat";
    public static final String PM_Incenappli = "Incenappli";
    public static final String PM_Incentper = "Incentper";
    public static final String PM_Minlevel = "Minlevel";
    public static final String PM_Maxlevel = "Maxlevel";
    public static final String PM_Reorderlevel = "Reorderlevel";
    public static final String PM_Active = "Active";
    public static final String PM_Minrange = "Minrange";
    public static final String PM_Maxrange = "Maxrange";
    public static final String PM_Createdby = "Createdby";
    public static final String PM_Createddt = "Createddt";
    public static final String PM_Modifiedby = "Modifiedby";
    public static final String PM_Modifieddt = "Modifieddt";
    public static final String PM_Deletedby = "Deletedby";
    public static final String PM_Deleteddt = "Deleteddt";
    public static final String PM_Barcode = "Barcode";
    public static final String PM_Auto = "Auto";
    public static final String PM_Fyear = "Fyear";
    public static final String PM_Inventory = "Inventory";
    public static final String PM_Incenttype = "Incenttype";
    public static final String PM_Discount = "Discount";
    public static final String PM_Branch = "Branch";
    public static final String PM_Loyaltycardno = "Loyaltycardno";
    public static final String PM_Cs_Type = "Cs_Type";
    public static final String PM_Lbtper = "Lbtper";
    public static final String PM_Considerlength = "Considerlength";
    public static final String PM_Cat7 = "Cat7";
    public static final String PM_Cat8 = "Cat8";
    public static final String PM_Cat9 = "Cat9";
    public static final String PM_Cat10 = "Cat10";
    public static final String PM_Cat11 = "Cat11";
    public static final String PM_Cat12 = "Cat12";
    public static final String PM_Ssp = "Ssp";
    public static final String PM_Gstgroup = "Gstgroup";
    public static final String PM_Hsncode = "Hsncode";
    public static final String PM_Btype = "Btype";

    public static final String Table_SupplierMaster = "SupplierMaster";
    public static final String SM_Auto = "Auto";
    public static final String SM_Id = "Id";
    public static final String SM_Name = "Name";
    public static final String SM_Address = "Address";
    public static final String SM_Phone1 = "Phone1";
    public static final String SM_Phone2 = "Phone2";
    public static final String SM_Mobile = "Mobile";
    public static final String SM_Email = "Email";
    public static final String SM_Contactperson = "Contactperson";
    public static final String SM_Vattinno = "Vattinno";
    public static final String SM_Csttinno = "Csttinno";
    public static final String SM_Ecctinno = "Ecctinno";
    public static final String SM_Disc = "Disc";
    public static final String SM_Crdays = "Crdays";
    public static final String SM_Crlimit = "Crlimit";
    public static final String SM_Interest = "Interest";
    public static final String SM_Remark = "Remark";
    public static final String SM_Extinct = "Extinct";
    public static final String SM_Ledgerbal = "Ledgerbal";
    public static final String SM_Createdby = "Createdby";
    public static final String SM_Createddate = "Createddate";
    public static final String SM_Modifiedby = "Modifiedby";
    public static final String SM_Modifieddate = "Modifieddate";
    public static final String SM_Cancelby = "Cancelby";
    public static final String SM_Canceldate = "Canceldate";
    public static final String SM_Alias = "Alias";
    public static final String SM_Chqprintname = "Chqprintname";
    public static final String SM_City = "City";
    public static final String SM_Supptype = "Supptype";
    public static final String SM_Spcldisc = "Spcldisc";
    public static final String SM_Applylbt = "Applylbt";
    public static final String SM_Lbtno = "Lbtno";
    public static final String SM_Agentid = "Agentid";
    public static final String SM_Appst = "Appst";
    public static final String SM_Appby = "Appby";
    public static final String SM_Appdt = "Appdt";
    public static final String SM_Usernm = "Usernm";
    public static final String SM_Passwrd = "Passwrd";
    public static final String SM_Banknm = "Banknm";
    public static final String SM_Bankbranch = "Bankbranch";
    public static final String SM_Ifsc = "Ifsc";
    public static final String SM_Accno = "Accno";
    public static final String SM_Markup = "Markup";
    public static final String SM_Markdown = "Markdown";
    public static final String SM_Brokerage = "Brokerage";
    public static final String SM_Perpcdisc = "Perpcdisc";
    public static final String SM_Spdisc = "Spdisc";
    public static final String SM_Tnovrdisc = "Tnovrdisc";
    public static final String SM_Buyer = "Buyer";
    public static final String SM_Department = "Department";
    public static final String SM_Supptinno = "Supptinno";
    public static final String SM_Holdstat = "Holdstat";
    public static final String SM_Gstno = "Gstno";

    public static final String Table_CustomerMaster = "CustomerMaster";
    public static final String CSM_Auto = "Auto";
    public static final String CSM_Id = "Id";
    public static final String CSM_Name = "Name";
    public static final String CSM_Custtype = "Custtype";
    public static final String CSM_Cityid = "Cityid";
    public static final String CSM_Address = "Address";
    public static final String CSM_Area = "Area";
    public static final String CSM_Active = "Active";
    public static final String CSM_Phone = "Phone";
    public static final String CSM_Mobno = "Mobno";
    public static final String CSM_Dob = "Dob";
    public static final String CSM_Balamt = "Balamt";
    public static final String CSM_Remark = "Remark";
    public static final String CSM_Dobapplicable = "Dobapplicable";
    public static final String CSM_Areaid = "Areaid";
    public static final String CSM_Createdby = "Createdby";
    public static final String CSM_Createddate = "Createddate";
    public static final String CSM_Modifiedby = "Modifiedby";
    public static final String CSM_Modifieddate = "Modifieddate";
    public static final String CSM_Cancelby = "Cancelby";
    public static final String CSM_Canceldate = "Canceldate";
    public static final String CSM_Loyaltycardno = "Loyaltycardno";
    public static final String CSM_Email = "Email";
    public static final String CSM_Anniversarydate = "Anniversarydate";
    public static final String CSM_Anniapplicable = "Anniapplicable";
    public static final String CSM_Enteredfrom = "Enteredfrom";
    public static final String CSM_Branchid = "Branchid";
    public static final String CSM_Applylbt = "Applylbt";
    public static final String CSM_Lbtno = "Lbtno";
    public static final String CSM_Empid = "Empid";
    public static final String CSM_Panno = "Panno";
    public static final String CSM_Lcardpoint = "Lcardpoint";
    public static final String CSM_Gstno = "Gstno";
    public static final String CSM_Usernm = "Usernm";
    public static final String CSM_Password = "Password";
    public static final String CSM_Notificationst = "Notificationst";
    public static final String CSM_Custrwtyp = "Custrwtyp";
    public static final String CSM_Ledgerbal = "Ledgerbal";

    public static final String Table_BillMaster = "BillMaster";
    public static final String BM_Autono = "Autono";
    public static final String BM_Id = "Id";
    public static final String BM_Branchid = "Branchid";
    public static final String BM_Finyr = "Finyr";
    public static final String BM_Billno = "Billno";
    public static final String BM_Custid = "Custid";
    public static final String BM_Jobcardid = "Jobcardid";
    public static final String BM_Billdate = "Billdate";
    public static final String BM_Totalqty = "Totalqty";
    public static final String BM_Totalamt = "Totalamt";
    public static final String BM_Retmemono = "Retmemono";
    public static final String BM_Returnamt = "Returnamt";
    public static final String BM_Creditamt = "Creditamt";
    public static final String BM_Cashamt = "Cashamt";
    public static final String BM_Paidamt = "Paidamt";
    public static final String BM_Balamt = "Balamt";
    public static final String BM_Brakeupamt = "Brakeupamt";
    public static final String BM_Billst = "Billst";
    public static final String BM_Vehicleno = "Vehicleno";
    public static final String BM_Createdby = "Createdby";
    public static final String BM_Createddt = "Createddt";
    public static final String BM_Modifiedby = "Modifiedby";
    public static final String BM_Modifieddt = "Modifieddt";
    public static final String BM_Vehiclemake = "Vehiclemake";
    public static final String BM_Vehiclecolor = "Vehiclecolor";
    public static final String BM_Drivername = "Drivername";
    public static final String BM_Vat12 = "Vat12";
    public static final String BM_Vat4 = "Vat4";
    public static final String BM_Labour = "Labour";
    public static final String BM_Cardno = "Cardno";
    public static final String BM_Chqno = "Chqno";
    public static final String BM_Chqamt = "Chqamt";
    public static final String BM_Chqdt = "Chqdt";
    public static final String BM_Advanceamt = "Advanceamt";
    public static final String BM_Paymenttype = "Paymenttype";
    public static final String BM_Billpayst = "Billpayst";
    public static final String BM_Netamt = "Netamt";
    public static final String BM_Bankid = "Bankid";
    public static final String BM_Comminper = "Comminper";
    public static final String BM_Comminrs = "Comminrs";
    public static final String BM_Printno = "Printno";
    public static final String BM_Disper = "Disper";
    public static final String BM_Disamt = "Disamt";
    public static final String BM_Inwrds = "Inwrds";
    public static final String BM_Refundpyst = "Refundpyst";
    public static final String BM_Pino = "Pino";
    public static final String BM_Piamt = "Piamt";
    public static final String BM_Mon = "Mon";
    public static final String BM_Gvoucher = "Gvoucher";
    public static final String BM_Gvoucheramt = "Gvoucheramt";
    public static final String BM_Agentid = "Agentid";
    public static final String BM_Vouchergenerate = "Vouchergenerate";
    public static final String BM_Gvschemeid = "Gvschemeid";
    public static final String BM_Scheme = "Scheme";
    public static final String BM_Gv = "Gv";
    public static final String BM_Gvamt = "Gvamt";
    public static final String BM_Gvno = "Gvno";
    public static final String BM_Issuegvst = "Issuegvst";
    public static final String BM_Createdfrm = "Createdfrm";
    public static final String BM_Grossamount = "Grossamount";
    public static final String BM_Cancelledby = "Cancelledby";
    public static final String BM_Cancelleddate = "Cancelleddate";
    public static final String BM_Billingtime = "Billingtime";
    public static final String BM_Delivered = "Delivered";
    public static final String BM_Deliveredby = "Deliveredby";
    public static final String BM_Delivereddate = "Delivereddate";
    public static final String BM_Alteration = "Alteration";
    public static final String BM_Cashback = "Cashback";
    public static final String BM_Schemeamt = "Schemeamt";
    public static final String BM_Goodsreturn = "Goodsreturn";
    public static final String BM_Counterno = "Counterno";
    public static final String BM_Machinename = "Machinename";
    public static final String BM_Againstdc = "Againstdc";
    public static final String BM_Dcautono = "Dcautono";
    public static final String BM_CancelReason = "CancelReason";
    public static final String BM_Tender = "Tender";
    public static final String BM_RemainAmt = "RemainAmt";
    public static final String BM_TRetQty = "TRetQty";
    public static final String BM_Currencyid = "Currencyid";
    public static final String BM_TotalCurrency = "TotalCurrency";
    public static final String BM_CurrencyAmt = "CurrencyAmt";
    public static final String BM_CardBank = "CardBank";
    public static final String BM_CardTyp = "CardTyp";
    public static final String BM_SwipReceiptNo = "SwipReceiptNo";
    public static final String BM_Reference = "Reference";
    public static final String BM_Repl_Column = "Repl_Column";
    public static final String BM_Through = "Through";
    public static final String BM_Authorised = "Authorised";
    public static final String BM_Remark = "Remark";
    public static final String BM_TheirRemark = "TheirRemark";
    public static final String BM_CGSTAMT = "CGSTAMT";
    public static final String BM_SGSTAMT = "SGSTAMT";
    public static final String BM_IGSTAPP = "IGSTAPP";
    public static final String BM_IGSTAMT = "IGSTAMT";
    public static final String BM_Type = "Type";
    public static final String BM_BothId = "BothId";
    public static final String BM_NewAgainstDC = "NewAgainstDC";
    public static final String BM_NewDCNo = "NewDCNo";

    public static final String Table_BillDetails = "BillDetails";
    public static final String BD_Auto = "Auto";
    public static final String BD_Id = "Id";
    public static final String BD_Billid = "Billid";
    public static final String BD_Branchid = "Branchid";
    public static final String BD_Finyr = "Finyr";
    public static final String BD_Itemid = "Itemid";
    public static final String BD_Rate = "Rate";
    public static final String BD_Qty = "Qty";
    public static final String BD_Total = "Total";
    public static final String BD_Barcode = "Barcode";
    public static final String BD_Fathersku = "Fathersku";
    public static final String BD_Empid = "Empid";
    public static final String BD_Incentamt = "Incentamt";
    public static final String BD_Autobillid = "Autobillid";
    public static final String BD_Vat = "Vat";
    public static final String BD_Vatamt = "Vatamt";
    public static final String BD_Amtwithoutdisc = "Amtwithoutdisc";
    public static final String BD_Disper = "Disper";
    public static final String BD_Disamt = "Disamt";
    public static final String BD_Nonbar = "Nonbar";
    public static final String BD_Mon = "Mon";
    public static final String BD_Ratewithtax = "Ratewithtax";
    public static final String BD_Purchaseqty = "Purchaseqty";
    public static final String BD_Freeqty = "Freeqty";
    public static final String BD_Alteredstat = "Alteredstat";
    public static final String BD_Mandalper = "Mandalper";
    public static final String BD_Mandal = "Mandal";
    public static final String BD_Delivered = "Delivered";
    public static final String BD_Deliveredby = "Deliveredby";
    public static final String BD_Delivereddate = "Delivereddate";
    public static final String BD_Bgtype = "Bgtype";
    public static final String BD_Allotid = "Allotid";
    public static final String BD_Schemeapp = "Schemeapp";
    public static final String BD_Schemeid = "Schemeid";
    public static final String BD_Dispfsku = "Dispfsku";
    public static final String BD_Mrp = "Mrp";
    public static final String BD_Billdisper = "Billdisper";
    public static final String BD_Billdisamt = "Billdisamt";
    public static final String BD_Dcmastauto = "Dcmastauto";
    public static final String BD_Designno = "Designno";
    public static final String BD_Retqty = "Retqty";
    public static final String BD_Actrate = "Actrate";
    public static final String BD_Actmrp = "Actmrp";
    public static final String BD_Schmbenefit = "Schmbenefit";
    public static final String BD_Gstper = "Gstper";
    public static final String BD_Cgstamt = "Cgstamt";
    public static final String BD_Sgstamt = "Sgstamt";
    public static final String BD_Cgstper = "Cgstper";
    public static final String BD_Sgstper = "Sgstper";
    public static final String BD_Cessper = "Cessper";
    public static final String BD_Cessamt = "Cessamt";
    public static final String BD_Igstamt = "Igstamt";
    public static final String BD_Taxableamt = "Taxableamt";
    public static final String BD_Type = "Type";
    public static final String BD_Seqno = "Seqno";

    public static final String GSTMaster_Table = "GSTMaster";
    public static final String GSTMaster_Auto = "Auto";
    public static final String GSTMaster_GroupName = "GroupName";
    public static final String GSTMaster_Status = "Status";
    public static final String GSTMaster_CrBy = "CrBy";
    public static final String GSTMaster_CrDate = "CrDate";
    public static final String GSTMaster_CrTime = "CrTime";
    public static final String GSTMaster_ModBy = "ModBy";
    public static final String GSTMaster_ModDate = "ModDate";
    public static final String GSTMaster_ModTime = "ModTime";
    public static final String GSTMaster_CanBy = "CanBy";
    public static final String GSTMaster_CanDate = "CanDate";
    public static final String GSTMaster_CanTime = "CanTime";
    public static final String GSTMaster_Remark = "Remark";
    public static final String GSTMaster_EffFrom = "EffFrom";
    public static final String GSTMaster_EffTo = "EffTo";
    public static final String GSTMaster_GSTDisc = "GSTDisc";

    public static final String GSTDetail_Table = "GSTDetail";
    public static final String GSTDetail_Auto = "Auto";
    public static final String GSTDetail_MastAuto = "MastAuto";
    public static final String GSTDetail_FromRange = "FromRange";
    public static final String GSTDetail_ToRange = "ToRange";
    public static final String GSTDetail_GSTPer = "GSTPer";
    public static final String GSTDetail_CGSTPer = "CGSTPer";
    public static final String GSTDetail_SGSTPer = "SGSTPer";
    public static final String GSTDetail_CessPer = "CessPer";
    public static final String GSTDetail_CGSTShare = "CGSTShare";
    public static final String GSTDetail_SGSTShare = "SGSTShare";
    public static final String GSTDetail_FromAmnt = "FromAmnt";
    public static final String GSTDetail_ToAmnt = "ToAmnt";
    public static final String GSTDetail_PrevGST = "PrevGST";
    public static final String GSTDetail_GFROMAMT = "GFROMAMT";
    public static final String GSTDetail_GTOAMT = "GTOAMT";

    public static final String Table_ExpenseHead = "ExpenseHead";
    public static final String EXM_Auto = "Auto";
    public static final String EXM_Id = "Id";
    public static final String EXM_Name = "Name";
    public static final String EXM_Expdesc = "Expdesc";
    public static final String EXM_Costcentre = "Costcentre";
    public static final String EXM_Remark = "Remark";
    public static final String EXM_Active = "Active";
    public static final String EXM_Createdby = "Createdby";
    public static final String EXM_Createddate = "Createddate";
    public static final String EXM_Modifiedby = "Modifiedby";
    public static final String EXM_Modifieddate = "Modifieddate";
    public static final String EXM_Cancelby = "Cancelby";
    public static final String EXM_Canceldate = "Canceldate";

    public static final String UB_Table = "UpdateBillTable";
    public static final String UB_Auto = "Auto";
    public static final String UB_BillNo = "BillNo";
    public static final String UB_ProdId = "ProdId";
    public static final String UB_OldQty = "OldQty";
    public static final String UB_NewQty = "NewQty";
    public static final String UB_OldRate = "OldRate";
    public static final String UB_NewRate = "NewRate";
    public static final String UB_ModBy = "ModBy";
    public static final String UB_ModDate = "ModDate";
    public static final String UB_ModTime = "ModTime";

    String table_company_master = "create table if not exists " + Table_CompanyMaster + "(" +
            CPM_Auto + " int not null," + CPM_Id + " int," + CPM_CompanyName + " text," + CPM_Mgrname + " int," +
            CPM_City + " int," + CPM_State + " int," + CPM_Address + " text," + CPM_Phone + " text," +
            CPM_Altpnone + " text," + CPM_Email + " text," + CPM_Vatno + " text," + CPM_Cstno + " text," +
            CPM_Websitename + " text," + CPM_Createby + " text," + CPM_Createdt + " text," +
            CPM_Initials + " text," + CPM_Modifiedby + " int," + CPM_Modifieddate + " text," +
            CPM_Cancelby + " int," + CPM_Canceldate + " text," + CPM_Com_Identity + " text," +
            CPM_Com_Type + " text," + CPM_Img + " text," + CPM_License + " text," +
            CPM_PrintMsg + " text," + CPM_Ddecomm + " float," + CPM_Hocode + " int," + CPM_Negbill + " text," +
            CPM_Ipadd + " text," + CPM_Imagepath + " text," + CPM_Imagepathlocal + " text," +
            CPM_Othbranchadd + " text," + CPM_PrintName + " text," + CPM_Applylbt + " text," +
            CPM_Lbtno + " text," + CPM_Logcolor + " text," + CPM_Clientid + " int," + CPM_Txtype + " text," +
            CPM_Discountvat + " text," + CPM_Transittime + " float," + CPM_Saleup + " float," +
            CPM_Ledgerbal + " float," + CPM_Enddays + " float," + CPM_PANNo + " text," + CPM_Dcode + " text," +
            CPM_GSTNo + " text," + CPM_Tendoramtcomp + " text," + CPM_MailUname + " text," +
            CPM_MailPwd + " text," + CPM_Mailfrom + " text," + CPM_SMTPServer + " text," +
            CPM_Cardpointvalue + " float," + CPM_Ctype + " text," + CPM_Printmsgcomp + " text," +
            CPM_Nprint + " int," + CPM_Colsizecomp + " text," + CPM_Bartyp + " text," +
            CPM_Wantmrp + " text," + CPM_Barcodecount + " int," + CPM_Stdcack + " text," +
            CPM_Custdeftyp + " text," + CPM_Gstdisc + " text," + CPM_Prodeftyp + " text," +
            CPM_Attendcodet + " text," + CPM_Tokeny + " text," + CPM_Gstex + " text," +
            CPM_Returnprintmessage + " text," + CPM_Prtype + " text," +
            CPM_Admin_Password + " text, primary key(" + CPM_Auto + "))";

    String table_employee_master = "create table if not exists " + Table_EmployeeMaster + "(" +
            EM_Auto + " int not null," + EM_Id + " int," + EM_Branchcode + " int," +
            EM_Dept + " int," + EM_Subdepet + " int," + EM_Empname + " text," +
            EM_Address + " text," + EM_Empdob + " text," +
            EM_Applicabledob + " text," + EM_Joindt + " text," +
            EM_Resgdt + " text," + EM_Phone + " text," +
            EM_Mobile + " text," + EM_Designation + " int," + EM_Roleid + " int," + EM_Salary + " float," +
            EM_Incentive + " text," + EM_Active + " text," + EM_Identitytype + " text," + EM_Identityvalue + " text," +
            EM_Usernm + " text," + EM_Pass + " text," + EM_Createdby + " int," + EM_Createddate + " text," +
            EM_Modifiedby + " int," + EM_Modifieddate + " text," + EM_Cancelby + " int," + EM_Canceldate + " text," +
            EM_Bloodgroup + " text," + EM_Gender + " text," + EM_Shift + " int," + EM_Weekelyoff + " int," +
            EM_Pfno + " text," + EM_Esino + " text," + EM_Accountno + " text," + EM_Emp_Id + " text," +
            EM_Empini + " text," + EM_Srno + " int," + EM_Sections + " text," + EM_Contract + " text," +
            EM_Basic + " float," + EM_Da + " float," + EM_Hra + " float," + EM_Others + " float," +
            EM_Spallow + " float," + EM_Washcharge + " float," + EM_Retamt + " float," +
            EM_Remark + " text," + EM_Retledger + " float," + EM_Emp_Type + " text," +
            EM_Bonus_Type + " text," + EM_Bonus_Amt + " float," + EM_Emptype + " int," + EM_Imagepath + " text," +
            EM_Authorised + " text," + EM_Cstype + " text," + EM_Points + " float," + EM_Awp + " text," +
            EM_Cityid + " int," + EM_Taskapp + " text," + EM_Intime + " text," + EM_Outtime + " text," + EM_Docpath + " text," +
            EM_Email + " text," + EM_Bonusledger + " text," + EM_Bankid + " int," + EM_Resproof + " text," +
            EM_Residno + " text," + EM_Permaddress + " text," + EM_Empesiname + " text," + EM_Desc1 + " text," +
            EM_Plbalance + " float," + EM_Clbalance + " float," +
            EM_Pwd + " text, primary key(" + EM_Auto + "))";


    String table_product_master = "create table if not exists " + Table_ProductMaster + "(" +
            PM_Id + " int not null," + PM_Cat1 + " text," + PM_Cat2 + " text," + PM_Cat3 + " text," +
            PM_Cat4 + " text," + PM_Cat5 + " text," + PM_Cat6 + " text," + PM_Itemname + " text," +
            PM_Finalproduct + " text," + PM_Unit + " text," + PM_Type + " text," + PM_Pprice + " float," +
            PM_Mrp + " float," + PM_Wprice + " float," + PM_Vat + " float," + PM_Incenappli + " text," +
            PM_Incentper + " float," + PM_Minlevel + " float," + PM_Maxlevel + " float," +
            PM_Reorderlevel + " float," + PM_Active + " text," + PM_Minrange + " float," + PM_Maxrange + " float," + PM_Createdby + " int," +
            PM_Createddt + " text," + PM_Modifiedby + " int," + PM_Modifieddt + " text," + PM_Deletedby + " int," +
            PM_Deleteddt + " text," + PM_Barcode + " text," + PM_Auto + " int," + PM_Fyear + " text," +
            PM_Inventory + " text," + PM_Incenttype + " text," + PM_Discount + " float," + PM_Branch + " int," +
            PM_Loyaltycardno + " text," + PM_Cs_Type + " text," + PM_Lbtper + " float," + PM_Considerlength + " text," +
            PM_Cat7 + " text," + PM_Cat8 + " text," + PM_Cat9 + " text," + PM_Cat10 + " text," +
            PM_Cat11 + " text," + PM_Cat12 + " text," + PM_Ssp + " float," + PM_Gstgroup + " text," +
            PM_Hsncode + " text," +
            PM_Btype + " text, primary key(" + PM_Id + "))";

    String table_supplier_master = "create table if not exists " + Table_SupplierMaster + "(" +
            SM_Auto + " int not null," + SM_Id + " int," + SM_Name + " text," + SM_Address + " text," +
            SM_Phone1 + " text," + SM_Phone2 + " text," + SM_Mobile + " text," + SM_Email + " text," +
            SM_Contactperson + " text," + SM_Vattinno + " text," + SM_Csttinno + " text," +
            SM_Ecctinno + " text," + SM_Disc + " float," + SM_Crdays + " int," + SM_Crlimit + " float," +
            SM_Interest + " float," + SM_Remark + " text," + SM_Extinct + " text," + SM_Ledgerbal + " float," +
            SM_Createdby + " int," + SM_Createddate + " text," + SM_Modifiedby + " int," + SM_Modifieddate + " text," +
            SM_Cancelby + " int," + SM_Canceldate + " text," + SM_Alias + " text," + SM_Chqprintname + " text," +
            SM_City + " int," + SM_Supptype + " int," + SM_Spcldisc + " float," + SM_Applylbt + " text," +
            SM_Lbtno + " text," + SM_Agentid + " int," + SM_Appst + " text," + SM_Appby + " int," +
            SM_Appdt + " text," + SM_Usernm + " text," + SM_Passwrd + " int," + SM_Banknm + " text," +
            SM_Bankbranch + " text," + SM_Ifsc + " text," + SM_Accno + " text," + SM_Markup + " float," +
            SM_Markdown + " float," + SM_Brokerage + " float," + SM_Perpcdisc + " float," + SM_Spdisc + " float," +
            SM_Tnovrdisc + " float," + SM_Buyer + " int," + SM_Department + " text," + SM_Supptinno + " text," +
            SM_Holdstat + " float," +
            SM_Gstno + " text, primary key(" + SM_Auto + "))";

    String table_customer_master = "create table if not exists " + Table_CustomerMaster + "(" +
            CSM_Auto + " int not null," + CSM_Id + " text," + CSM_Name + " text," + CSM_Custtype + " text," +
            CSM_Cityid + " int," + CSM_Address + " text," + CSM_Area + " text," + CSM_Active + " text," +
            CSM_Phone + " text," + CSM_Mobno + " text," + CSM_Dob + " text," + CSM_Balamt + " float," +
            CSM_Remark + " text," + CSM_Dobapplicable + " text," + CSM_Areaid + " int," + CSM_Createdby + " int," +
            CSM_Createddate + " text," + CSM_Modifiedby + " int," + CSM_Modifieddate + " text," +
            CSM_Cancelby + " int," + CSM_Canceldate + " text," + CSM_Loyaltycardno + " text," +
            CSM_Email + " text," + CSM_Anniversarydate + " text," + CSM_Anniapplicable + " text," +
            CSM_Enteredfrom + " text," + CSM_Branchid + " int," + CSM_Applylbt + " text," +
            CSM_Lbtno + " text," + CSM_Empid + " int," + CSM_Panno + " text," + CSM_Lcardpoint + " float," +
            CSM_Gstno + " text," + CSM_Usernm + " text," + CSM_Password + " text," + CSM_Notificationst + " text," +
            CSM_Custrwtyp + " text," +
            CSM_Ledgerbal + " float, primary key(" + CSM_Auto + "))";

    String table_expense_master = "create table if not exists " + Table_ExpenseHead + "(" +
            EXM_Auto + " int not null," + EXM_Id + " int," + EXM_Name + " text," + EXM_Expdesc + " text," +
            EXM_Costcentre + " text," + EXM_Remark + " text," + EXM_Active + " text," +
            EXM_Createdby + " int," + EXM_Createddate + " text," + EXM_Modifiedby + " int," + EXM_Modifieddate + " text," +
            EXM_Cancelby + " int," +
            EXM_Canceldate + " text, primary key(" + EXM_Auto + "))";

    String table_billmaster_master = "create table if not exists " + Table_BillMaster + "(" +
            BM_Autono + " int not null," + BM_Id + " int," + BM_Branchid + " int," + BM_Finyr + " int," +
            BM_Billno + " int," + BM_Custid + " text," + BM_Jobcardid + " int," + BM_Billdate + " text," +
            BM_Totalqty + " float," + BM_Totalamt + " float," + BM_Retmemono + " text," + BM_Returnamt + " float," +
            BM_Creditamt + " float," + BM_Cashamt + " float," + BM_Paidamt + " float," +
            BM_Balamt + " float," + BM_Brakeupamt + " text," + BM_Billst + " text," +
            BM_Vehicleno + " text," + BM_Createdby + " int," + BM_Createddt + " text," +
            BM_Modifiedby + " int," + BM_Modifieddt + " text," +
            BM_Vehiclemake + " text," + BM_Vehiclecolor + " text," +
            BM_Drivername + " text," + BM_Vat12 + " float," + BM_Vat4 + " float," + BM_Labour + " float," +
            BM_Cardno + " text," + BM_Chqno + " text," + BM_Chqamt + " float," +
            BM_Chqdt + " text," + BM_Advanceamt + " float," + BM_Paymenttype + " text," +
            BM_Billpayst + " text," + BM_Netamt + " float," + BM_Bankid + " int," +
            BM_Comminper + " float," + BM_Comminrs + " float," + BM_Printno + " int," + BM_Disper + " float," +
            BM_Disamt + " float," + BM_Inwrds + " text," + BM_Refundpyst + " text," +
            BM_Pino + " text," + BM_Piamt + " float," + BM_Mon + " text," +
            BM_Gvoucher + " int," + BM_Gvoucheramt + " float," + BM_Agentid + " int," +
            BM_Vouchergenerate + " text," + BM_Gvschemeid + " int," + BM_Scheme + " text," +
            BM_Gv + " text," + BM_Gvamt + " float," + BM_Gvno + " text," + BM_Issuegvst + " text," +
            BM_Createdfrm + " text," + BM_Grossamount + " float," + BM_Cancelledby + " int," +
            BM_Cancelleddate + " text," + BM_Billingtime + " text," + BM_Delivered + " text," +
            BM_Deliveredby + " int," + BM_Delivereddate + " text," + BM_Alteration + " float," + BM_Cashback + " float," +
            BM_Schemeamt + " float," + BM_Goodsreturn + " float," + BM_Counterno + " text," +
            BM_Machinename + " text," + BM_Againstdc + " text," + BM_Dcautono + " text," +
            BM_CancelReason + " text," + BM_Tender + " text," + BM_RemainAmt + " float," +
            BM_TRetQty + " float," + BM_Currencyid + " int," + BM_TotalCurrency + " float," +
            BM_CurrencyAmt + " text," + BM_CardBank + " text," + BM_CardTyp + " text," +
            BM_SwipReceiptNo + " text," + BM_Reference + " text," + BM_Repl_Column + " text," +
            BM_Through + " text," + BM_Authorised + " text," + BM_Remark + " text," +
            BM_TheirRemark + " text," + BM_CGSTAMT + " float," + BM_SGSTAMT + " float," + BM_IGSTAPP + " text," +
            BM_IGSTAMT + " float," + BM_Type + " int," + BM_BothId + " int," +
            BM_NewAgainstDC + " text," +
            BM_NewDCNo + " int, primary key(" + BM_Autono + "))";

    String table_billdetail = "create table if not exists " + Table_BillDetails + "(" +
            BD_Auto + " int not null," + BD_Id + " int," + BD_Billid + " int," + BD_Branchid + " int," +
            BD_Finyr + " text," + BD_Itemid + " float," + BD_Rate + " float," + BD_Qty + " float," +
            BD_Total + " float," + BD_Barcode + " text," + BD_Fathersku + " text," +
            BD_Empid + " int," + BD_Incentamt + " float," + BD_Autobillid + " int," + BD_Vat + " float," +
            BD_Vatamt + " float," + BD_Amtwithoutdisc + " float," + BD_Disper + " float," + BD_Disamt + " float," +
            BD_Nonbar + " float," + BD_Mon + " text," + BD_Ratewithtax + " float," +
            BD_Purchaseqty + " float," + BD_Freeqty + " float," + BD_Alteredstat + " text," +
            BD_Mandalper + " float," + BD_Mandal + " float," + BD_Delivered + " text," +
            BD_Deliveredby + " int," + BD_Delivereddate + " text," + BD_Bgtype + " text," +
            BD_Allotid + " int," + BD_Schemeapp + " text," + BD_Schemeid + " int," +
            BD_Dispfsku + " text," + BD_Mrp + " float," +
            BD_Billdisper + " float," + BD_Billdisamt + " float," +
            BD_Dcmastauto + " int," + BD_Designno + " text," + BD_Retqty + " float," +
            BD_Actrate + " float," + BD_Actmrp + " float," + BD_Schmbenefit + " float," +
            BD_Gstper + " float," + BD_Cgstamt + " float," + BD_Sgstamt + " float," +
            BD_Cgstper + " float," + BD_Sgstper + " float," + BD_Cessper + " float," + BD_Cessamt + " float," +
            BD_Igstamt + " float," + BD_Taxableamt + " float," + BD_Type + " int," +
            BD_Seqno + " int, primary key(" + BD_Auto + "))";

    public String table_gstmaster = "Create table if not exists " + GSTMaster_Table + "(" + GSTMaster_Auto + " int not null," +
            GSTMaster_GroupName + " text," + GSTMaster_Status + " text," + GSTMaster_CrBy + " int," + GSTMaster_CrDate + " text," +
            GSTMaster_CrTime + " text," + GSTMaster_ModBy + " int," + GSTMaster_ModDate + " text," + GSTMaster_ModTime + " text," +
            GSTMaster_CanBy + " int," + GSTMaster_CanDate + " text," + GSTMaster_CanTime + " text," + GSTMaster_Remark + " text," +
            GSTMaster_EffFrom + " text," + GSTMaster_EffTo + " text," + GSTMaster_GSTDisc + " text, primary key(" + GSTMaster_Auto + "))";

    public String table_gstdetail = "create table if not exists " + GSTDetail_Table + "(" + GSTDetail_Auto + " int not null," +
            GSTDetail_MastAuto + " int," + GSTDetail_FromRange + " float," + GSTDetail_ToRange + " float," +
            GSTDetail_GSTPer + " float," + GSTDetail_CGSTPer + " float," + GSTDetail_SGSTPer + " float," +
            GSTDetail_CGSTShare + " float," + GSTDetail_SGSTShare + " float," + GSTDetail_CessPer + " float," +
            GSTDetail_FromAmnt + " float," + GSTDetail_ToAmnt + " float," + GSTDetail_PrevGST + " float," +
            GSTDetail_GFROMAMT + " float," + GSTDetail_GTOAMT + " float,primary key(" + GSTDetail_Auto + "))";

    public String table_updatebill = "create table if not exists " + UB_Table + "(" +
            UB_Auto + " int not null," +
            UB_BillNo + " text," + UB_ProdId + " int," + UB_OldQty + " float," +
            UB_NewQty + " float," + UB_OldRate + " float," + UB_NewRate + " float, " +
            UB_ModBy + " int," + UB_ModDate + " text," + UB_ModTime + " text, primary key(" + UB_Auto + "))";


    public DBHandler(Context context) {
        super(context, Database_Name, null, Database_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(table_company_master);
        db.execSQL(table_employee_master);
        db.execSQL(table_product_master);
        db.execSQL(table_supplier_master);
        db.execSQL(table_customer_master);
        db.execSQL(table_expense_master);
        db.execSQL(table_billmaster_master);
        db.execSQL(table_billdetail);
        db.execSQL(table_gstmaster);
        db.execSQL(table_gstdetail);
        Constant.showLog("onCreate");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public void updateTable(){

    }

    public void addExpenseDetail(List<ExpenseDetail> detailList) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        ContentValues cv = new ContentValues();
        for (ExpenseDetail detail : detailList) {
            cv.put(EXM_Auto,detail.getAuto());
            cv.put(EXM_Createddate,detail.getDate());
           // cv.put(EXP_Time,detail.getTime());
            cv.put(EXM_Remark,detail.getRemark());
            cv.put(EXM_Costcentre,detail.getAmount());
            db.insert(Table_ExpenseHead, null, cv);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public int getMaxAuto(){
        int a = 0;
        String str = "select MAX("+EXM_Auto+") from "+Table_ExpenseHead;
        Constant.showLog(str);
        Cursor cursor  = getWritableDatabase().rawQuery(str,null);
        if (cursor.moveToFirst()) {

            a = cursor.getInt(0);
        }
        cursor.close();
        return a;
    }

    public Cursor getExpenseReportData(){
        String str = "Select * from "+Table_ExpenseHead;
        return getWritableDatabase().rawQuery(str,null);
    }

    public Cursor getListExpRemark(){
        String str = "select distinct "+EXM_Remark+" from "+Table_ExpenseHead+" where "+EXM_Remark+" <>'NA' AND "+EXM_Remark+" NOT LIKE ('')";
        Log.d("Log",str);
        return getWritableDatabase().rawQuery(str, null);
    }
}
