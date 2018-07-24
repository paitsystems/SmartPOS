package com.pait.smartpos.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.model.BillDetailClass;
import com.pait.smartpos.model.BillDetailClassR;
import com.pait.smartpos.model.BillMasterClass;
import com.pait.smartpos.model.DailyPettyExpClass;
import com.pait.smartpos.model.ExpenseDetail;
import com.pait.smartpos.parse.BillReprintCancelClass;

import java.util.ArrayList;
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
    public static final String CPM_GSTType = "GSTType";

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
    public static final String PM_GSTType = "GSTType";
    public static final String PM_StockQty = "StockQty";

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

    public static final String Table_Payment = "PaymentTable";
    public static final String PY_Auto = "Auto";
    public static final String PY_TYPE = "Type";
    public static final String PY_Status = "Status";

    public static final String Table_InwardMaster = "InwardMaster";
    public static final String IWM_Autono = "Autono";
    public static final String IWM_Id = "Id";
    public static final String IWM_Inwarddate = "Inwarddate";
    public static final String IWM_Branchid = "Branchid";
    public static final String IWM_Supplierid = "Supplierid";
    public static final String IWM_Againstpo = "Againstpo";
    public static final String IWM_Pono = "Pono";
    public static final String IWM_Podate = "Podate";
    public static final String IWM_Totalqty = "Totalqty";
    public static final String IWM_Totalamt = "Totalamt";
    public static final String IWM_Netamt = "Netamt";
    public static final String IWM_Inwardst = "Inwardst";
    public static final String IWM_Ramark = "Ramark";
    public static final String IWM_Netamtinword = "Netamtinword";
    public static final String IWM_Totalamtinword = "Totalamtinword";
    public static final String IWM_Finyr = "Finyr";
    public static final String IWM_Rebarcodest = "Rebarcodest";
    public static final String IWM_Rebarcnt = "Rebarcnt";
    public static final String IWM_Billno = "Billno";
    public static final String IWM_Billgenerated = "Billgenerated";
    public static final String IWM_Bgenerateno = "Bgenerateno";
    public static final String IWM_Createby = "Createby";
    public static final String IWM_Createdate = "Createdate";
    public static final String IWM_Transport_Id = "Transport_Id";
    public static final String IWM_Lr_No = "Lr_No";
    public static final String IWM_Lr_Date = "Lr_Date";
    public static final String IWM_Refund = "Refund";
    public static final String IWM_Refunddate = "Refunddate";
    public static final String IWM_Pimadest = "Pimadest";
    public static final String IWM_Invno = "Invno";
    public static final String IWM_Baleopenno = "Baleopenno";
    public static final String IWM_Jobworktyp = "Jobworktyp";
    public static final String IWM_Jobwrkdcid = "Jobwrkdcid";
    public static final String IWM_Barcodegenerate = "Barcodegenerate";
    public static final String IWM_Orderamt = "Orderamt";
    public static final String IWM_Consignmentpur = "Consignmentpur";
    public static final String IWM_Balanceqty = "Balanceqty";
    public static final String IWM_Forbranch = "Forbranch";
    public static final String IWM_Nan = "Nan";
    public static final String IWM_Totsuppdisamt = "Totsuppdisamt";
    public static final String IWM_Inwno = "Inwno";
    public static final String IWM_Replcolumns = "Replcolumns";
    public static final String IWM_Disper = "Disper";
    public static final String IWM_Disamt = "Disamt";
    public static final String IWM_Grossamt = "Grossamt";
    public static final String IWM_Totvat = "Totvat";
    public static final String IWM_Otheradd = "Otheradd";
    public static final String IWM_Rounduppamt = "Rounduppamt";
    public static final String IWM_Status = "Status";
    public static final String IWM_Cancelledby = "Cancelledby";
    public static final String IWM_Canceldate = "Canceldate";
    public static final String IWM_Cancelreson = "Cancelreson";
    public static final String IWM_Chkcst = "Chkcst";
    public static final String IWM_Cstvatper = "Cstvatper";
    public static final String IWM_Hocode = "Hocode";
    public static final String IWM_Esugamno = "Esugamno";
    public static final String IWM_Reason = "Reason";
    public static final String IWM_Cgstamt = "Cgstamt";
    public static final String IWM_Sgstamt = "Sgstamt";
    public static final String IWM_Igstapp = "Igstapp";
    public static final String IWM_Igstamt = "Igstamt";

    public static final String Table_InwardDetail = "InwardDetail";
    public static final String IWD_Autono = "Autono";
    public static final String IWD_Id = "Id";
    public static final String IWD_Inwardid = "Inwardid";
    public static final String IWD_Productid = "Productid";
    public static final String IWD_Fathersku = "Fathersku";
    public static final String IWD_Recqty = "Recqty";
    public static final String IWD_Rate = "Rate";
    public static final String IWD_Totalamt = "Totalamt";
    public static final String IWD_Tax = "Tax";
    public static final String IWD_Taxamt = "Taxamt";
    public static final String IWD_Productnetamt = "Productnetamt";
    public static final String IWD_Barcode = "Barcode";
    public static final String IWD_Itemname = "Itemname";
    public static final String IWD_Rebarcodest = "Rebarcodest";
    public static final String IWD_Rebarcnt = "Rebarcnt";
    public static final String IWD_Refinwid = "Refinwid";
    public static final String IWD_Discamt = "Discamt";
    public static final String IWD_Branchid = "Branchid";
    public static final String IWD_Discfrompr = "Discfrompr";
    public static final String IWD_Purchaserate = "Purchaserate";
    public static final String IWD_Freeqty = "Freeqty";
    public static final String IWD_Designno = "Designno";
    public static final String IWD_Color = "Color";
    public static final String IWD_Itmimage = "Itmimage";
    public static final String IWD_Imagepath = "Imagepath";
    public static final String IWD_Mandalper = "Mandalper";
    public static final String IWD_Mandal = "Mandal";
    public static final String IWD_Custdisper = "Custdisper";
    public static final String IWD_Custdisamt = "Custdisamt";
    public static final String IWD_Itemsize = "Itemsize";
    public static final String IWD_Itemsaleper = "Itemsaleper";
    public static final String IWD_Jobworktyp = "Jobworktyp";
    public static final String IWD_Oldbarcode = "Oldbarcode";
    public static final String IWD_Expqty = "Expqty";
    public static final String IWD_Podetauto = "Podetauto";
    public static final String IWD_Balanceqty = "Balanceqty";
    public static final String IWD_Mrp = "Mrp";
    public static final String IWD_Nan = "Nan";
    public static final String IWD_Suppdisper = "Suppdisper";
    public static final String IWD_Suppdisamt = "Suppdisamt";
    public static final String IWD_Repcolumn = "Repcolumn";
    public static final String IWD_Suppbilldisper = "Suppbilldisper";
    public static final String IWD_Suppbilldisamt = "Suppbilldisamt";
    public static final String IWD_Otheraddded = "Otheraddded";
    public static final String IWD_Gvapp = "Gvapp";
    public static final String IWD_Scheme1 = "Scheme1";
    public static final String IWD_Scheme2 = "Scheme2";
    public static final String IWD_Barcodeqty = "Barcodeqty";
    public static final String IWD_Wsp = "Wsp";
    public static final String IWD_Hocode = "Hocode";
    public static final String IWD_Txincextyp = "Txincextyp";
    public static final String IWD_Netrate = "Netrate";
    public static final String IWD_Gstper = "Gstper";
    public static final String IWD_Cgstamt = "Cgstamt";
    public static final String IWD_Sgstamt = "Sgstamt";
    public static final String IWD_Igstamt = "Igstamt";
    public static final String IWD_Cgstper = "Cgstper";
    public static final String IWD_Sgstper = "Sgstper";
    public static final String IWD_Cessper = "Cessper";
    public static final String IWD_Cessamt = "Cessamt";
    public static final String IWD_Hsncode = "Hsncode";
    public static final String IWD_Attr1 = "Attr1";
    public static final String IWD_Attr2 = "Attr2";
    public static final String IWD_Atrr3 = "Atrr3";
    public static final String IWD_Attr4 = "Attr4";
    public static final String IWD_Atrr5 = "Atrr5";
    public static final String IWD_Suppdisper1 = "Suppdisper1";
    public static final String IWD_Suppdisamt1 = "Suppdisamt1";

    public static final String Table_ReturnGoodMaster = "ReturnGoodMaster";
    public static final String RGM_Autoid = "Autoid";
    public static final String RGM_Id = "Id";
    public static final String RGM_Branchid = "Branchid";
    public static final String RGM_Supplierid = "Supplierid";
    public static final String RGM_Retqty = "Retqty";
    public static final String RGM_Retamt = "Retamt";
    public static final String RGM_Finyr = "Finyr";
    public static final String RGM_Taxamt = "Taxamt";
    public static final String RGM_Otheramt = "Otheramt";
    public static final String RGM_Netpay = "Netpay";
    public static final String RGM_Inwardid = "Inwardid";
    public static final String RGM_Createdt = "Createdt";
    public static final String RGM_Createby = "Createby";
    public static final String RGM_Transport_Id = "Transport_Id";
    public static final String RGM_Lr_No = "Lr_No";
    public static final String RGM_Lr_Date = "Lr_Date";
    public static final String RGM_Retdate = "Retdate";
    public static final String RGM_Dncreated = "Dncreated";
    public static final String RGM_Remarks = "Remarks";
    public static final String RGM_Status = "Status";
    public static final String RGM_Cancelreason = "Cancelreason";
    public static final String RGM_Canceldate = "Canceldate";
    public static final String RGM_Cancelby = "Cancelby";
    public static final String RGM_Createtime = "Createtime";
    public static final String RGM_Returntype = "Returntype";
    public static final String RGM_Grrno = "Grrno";
    public static final String RGM_Authorisedby = "Authorisedby";
    public static final String RGM_Cstype = "Cstype";
    public static final String RGM_Parcelst = "Parcelst";
    public static final String RGM_Tlbtamt = "Tlbtamt";
    public static final String RGM_Amtinwrds = "Amtinwrds";
    public static final String RGM_Grossamt = "Grossamt";
    public static final String RGM_Totaldisamt = "Totaldisamt";
    public static final String RGM_Roundup = "Roundup";
    public static final String RGM_Hocode = "Hocode";
    public static final String RGM_Reason = "Reason";
    public static final String RGM_Othertax = "Othertax";
    public static final String RGM_Docpath = "Docpath";
    public static final String RGM_Cgstamt = "Cgstamt";
    public static final String RGM_Sgstamt = "Sgstamt";
    public static final String RGM_Igstapp = "Igstapp";
    public static final String RGM_Igstamt = "Igstamt";
    public static final String RGM_Trachar = "Trachar";
    public static final String RGM_Trataxamt = "Trataxamt";
    public static final String RGM_Tranamt = "Tranamt";

    public static final String Table_ReturnGoodDetail = "ReturnGoodDetail";
    public static final String RGD_Autoid = "Autoid";
    public static final String RGD_Id = "Id";
    public static final String RGD_Retautoid = "Retautoid";
    public static final String RGD_Branchid = "Branchid";
    public static final String RGD_Barcode = "Barcode";
    public static final String RGD_Itemid = "Itemid";
    public static final String RGD_Rate = "Rate";
    public static final String RGD_Retqty = "Retqty";
    public static final String RGD_Balqty = "Balqty";
    public static final String RGD_Finyr = "Finyr";
    public static final String RGD_Taxper = "Taxper";
    public static final String RGD_Taxamt = "Taxamt";
    public static final String RGD_Inwardid = "Inwardid";
    public static final String RGD_Lbtper = "Lbtper";
    public static final String RGD_Lbtamt = "Lbtamt";
    public static final String RGD_Lbtcalamt = "Lbtcalamt";
    public static final String RGD_Replcolumn = "Replcolumn";
    public static final String RGD_Amount = "Amount";
    public static final String RGD_Itmdisper = "Itmdisper";
    public static final String RGD_Itmdisamt = "Itmdisamt";
    public static final String RGD_Total = "Total";
    public static final String RGD_Disper = "Disper";
    public static final String RGD_Disamt = "Disamt";
    public static final String RGD_Otheradd = "Otheradd";
    public static final String RGD_Hocode = "Hocode";
    public static final String RGD_Gstper = "Gstper";
    public static final String RGD_Cgstamt = "Cgstamt";
    public static final String RGD_Sgstamt = "Sgstamt";
    public static final String RGD_Igstamt = "Igstamt";
    public static final String RGD_Cgstper = "Cgstper";
    public static final String RGD_Sgstper = "Sgstper";
    public static final String RGD_Cessper = "Cessper";
    public static final String RGD_Cessamt = "Cessamt";
    public static final String RGD_Supdisc1 = "Supdisc1";
    public static final String RGD_Supdiscamt1 = "Supdiscamt1";
    public static final String RGD_Reason = "Reason";

    public static final String Table_ReturnMemoMaster = "ReturnMemoMaster";
    public static final String RMM_Auto = "Auto";
    public static final String RMM_Id = "Id";
    public static final String RMM_Machinename = "Machinename";
    public static final String RMM_Counterno = "Counterno";
    public static final String RMM_Rmemono = "Rmemono";
    public static final String RMM_Billno = "Billno";
    public static final String RMM_Custcode = "Custcode";
    public static final String RMM_Netbillamt = "Netbillamt";
    public static final String RMM_Returnqty = "Returnqty";
    public static final String RMM_Returnamt = "Returnamt";
    public static final String RMM_Dis = "Dis";
    public static final String RMM_Tax = "Tax";
    public static final String RMM_Grossamt = "Grossamt";
    public static final String RMM_Netamt = "Netamt";
    public static final String RMM_Remark = "Remark";
    public static final String RMM_Empname = "Empname";
    public static final String RMM_Createby = "Createby";
    public static final String RMM_Createdt = "Createdt";
    public static final String RMM_Modifiedby = "Modifiedby";
    public static final String RMM_Modifieddt = "Modifieddt";
    public static final String RMM_Deletedby = "Deletedby";
    public static final String RMM_Deleteddt = "Deleteddt";
    public static final String RMM_Financialyr = "Financialyr";
    public static final String RMM_Maxno = "Maxno";
    public static final String RMM_Branchid = "Branchid";
    public static final String RMM_Inwrds = "Inwrds";
    public static final String RMM_Redeemst = "Redeemst";
    public static final String RMM_Status = "Status";
    public static final String RMM_Actualcreatedate = "Actualcreatedate";
    public static final String RMM_Createtime = "Createtime";
    public static final String RMM_Type = "Type";
    public static final String RMM_Redeemtype = "Redeemtype";
    public static final String RMM_Specialright = "Specialright";
    public static final String RMM_Balredeem = "Balredeem";
    public static final String RMM_Msreplclm = "Msreplclm";
    public static final String RMM_Billknockamt = "Billknockamt";
    public static final String RMM_Openst = "Openst";
    public static final String RMM_Openby = "Openby";
    public static final String RMM_Opendate = "Opendate";
    public static final String RMM_Openreason = "Openreason";
    public static final String RMM_Returnreason = "Returnreason";
    public static final String RMM_Gatepassno = "Gatepassno";
    public static final String RMM_Cntype = "Cntype";
    public static final String RMM_Createdfrom = "Createdfrom";
    public static final String RMM_Revgainpts = "Revgainpts";
    public static final String RMM_Revrdmpts = "Revrdmpts";
    public static final String RMM_Cgstamt = "Cgstamt";
    public static final String RMM_Sgstamt = "Sgstamt";
    public static final String RMM_Igstapp = "Igstapp";
    public static final String RMM_Igstamt = "Igstamt";

    public static final String Table_ReturnMemoDetails = "ReturnMemoDetails";
    public static final String RMD_Id = "Id";
    public static final String RMD_Rmemono = "Rmemono";
    public static final String RMD_Itemcode = "Itemcode";
    public static final String RMD_Barcode = "Barcode";
    public static final String RMD_Qty = "Qty";
    public static final String RMD_Rate = "Rate";
    public static final String RMD_Amt = "Amt";
    public static final String RMD_Returnid = "Returnid";
    public static final String RMD_Branchid = "Branchid";
    public static final String RMD_Financialyr = "Financialyr";
    public static final String RMD_Counterno = "Counterno";
    public static final String RMD_Autoid = "Autoid";
    public static final String RMD_Disper = "Disper";
    public static final String RMD_Disamt = "Disamt";
    public static final String RMD_Vatper = "Vatper";
    public static final String RMD_Vatamt = "Vatamt";
    public static final String RMD_Nonbarst = "Nonbarst";
    public static final String RMD_Itemname = "Itemname";
    public static final String RMD_Empid = "Empid";
    public static final String RMD_Mrp = "Mrp";
    public static final String RMD_Mastid = "Mastid";
    public static final String RMD_Billdetauto = "Billdetauto";
    public static final String RMD_Billdisper = "Billdisper";
    public static final String RMD_Billdisamt = "Billdisamt";
    public static final String RMD_Dtlid = "Dtlid";
    public static final String RMD_Gstper = "Gstper";
    public static final String RMD_Cgstamt = "Cgstamt";
    public static final String RMD_Sgstamt = "Sgstamt";
    public static final String RMD_Cgstper = "Cgstper";
    public static final String RMD_Sgstper = "Sgstper";
    public static final String RMD_Cessper = "Cessper";
    public static final String RMD_Cessamt = "Cessamt";
    public static final String RMD_Igstamt = "Igstamt";
    public static final String RMD_Taxableamt = "Taxableamt";

    public static final String Table_DailyPettyExp = "DailyPettyExp";
    public static final String DPE_Autoid = "Autoid";
    public static final String DPE_Branchid = "Branchid";
    public static final String DPE_Date = "Date";
    public static final String DPE_Toemp = "Toemp";
    public static final String DPE_Amount = "Amount";
    public static final String DPE_Exphead = "Exphead";
    public static final String DPE_Authoby = "Authoby";
    public static final String DPE_Remark = "Remark";
    public static final String DPE_Createdby = "Createdby";
    public static final String DPE_Createddate = "Createddate";
    public static final String DPE_Status = "Status";
    public static final String DPE_Cancleby = "Cancleby";
    public static final String DPE_Cancledate = "Cancledate";
    public static final String DPE_Cancelreason = "Cancelreason";
    public static final String DPE_Amtinwords = "Amtinwords";
    public static final String DPE_Voutype = "Voutype";
    public static final String DPE_Againstref = "Againstref";
    public static final String DPE_Id = "Id";
    public static final String DPE_Finyr = "Finyr";
    public static final String DPE_Voucherno = "Voucherno";
    public static final String DPE_Cstype = "Cstype";
    public static final String DPE_Counterno = "Counterno";
    public static final String DPE_Subhead = "Subhead";

    public static final String VIEW_BillCash = "VIEW_BillCash";


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
            CPM_Admin_Password + " text,"+CPM_GSTType + " text, primary key(" + CPM_Auto + "))";

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
            PM_Hsncode + " text," +PM_Btype + " text,"+PM_GSTType + " text,"+PM_StockQty + " float, primary key(" + PM_Id + "))";

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
            BM_Autono + " int not null," + BM_Id + " int," + BM_Branchid + " int," + BM_Finyr + " text," +
            BM_Billno + " text," + BM_Custid + " text," + BM_Jobcardid + " int," + BM_Billdate + " text," +
            BM_Totalqty + " int," + BM_Totalamt + " float," + BM_Retmemono + " text," + BM_Returnamt + " float," +
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
            BD_Finyr + " text," + BD_Itemid + " int," + BD_Rate + " float," + BD_Qty + " int," +
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

    public String table_paymenttable = "create table if not exists " + Table_Payment + "(" +
            PY_Auto + " int not null," + PY_TYPE + " text," + PY_Status + " text, primary key(" + PY_Auto + "))";

    private String table_inward_master = "create table if not exists " + Table_InwardMaster + "(" +
            IWM_Autono + " int not null," +
            IWM_Id + " int," +
            IWM_Inwarddate + " text," +
            IWM_Branchid + " int," +
            IWM_Supplierid + " int," +
            IWM_Againstpo + " text," +
            IWM_Pono + " int," +
            IWM_Podate + " text," +
            IWM_Totalqty + " float," +
            IWM_Totalamt + " float," +
            IWM_Netamt + " float," +
            IWM_Inwardst + " text," +
            IWM_Ramark + " text," +
            IWM_Netamtinword + " text," +
            IWM_Totalamtinword + " text," +
            IWM_Finyr + " text," +
            IWM_Rebarcodest + " text," +
            IWM_Rebarcnt + " int," +
            IWM_Billno + " text," +
            IWM_Billgenerated + " text," +
            IWM_Bgenerateno + " text," +
            IWM_Createby + " int," +
            IWM_Createdate + " text," +
            IWM_Transport_Id + " int," +
            IWM_Lr_No + " text," +
            IWM_Lr_Date + " text," +
            IWM_Refund + " text," +
            IWM_Refunddate + " text," +
            IWM_Pimadest + " text," +
            IWM_Invno + " text," +
            IWM_Baleopenno + " text," +
            IWM_Jobworktyp + " text," +
            IWM_Jobwrkdcid + " int," +
            IWM_Barcodegenerate + " text," +
            IWM_Orderamt + " float," +
            IWM_Consignmentpur + " text," +
            IWM_Balanceqty + " float," +
            IWM_Forbranch + " text," +
            IWM_Nan + " text," +
            IWM_Totsuppdisamt + " float," +
            IWM_Inwno + " text," +
            IWM_Replcolumns + " float," +
            IWM_Disper + " float," +
            IWM_Disamt + " float," +
            IWM_Grossamt + " float," +
            IWM_Totvat + " float," +
            IWM_Otheradd + " float," +
            IWM_Rounduppamt + " float," +
            IWM_Status + " text," +
            IWM_Cancelledby + " int," +
            IWM_Canceldate + " text," +
            IWM_Cancelreson + " text," +
            IWM_Chkcst + " text," +
            IWM_Cstvatper + " float," +
            IWM_Hocode + " int," +
            IWM_Esugamno + " text," +
            IWM_Reason + " text," +
            IWM_Cgstamt + " float," +
            IWM_Sgstamt + " float," +
            IWM_Igstapp + " text," +
            IWM_Igstamt + " float, primary key(" + IWM_Autono + "))";

    private String table_inward_detail = "create table if not exists " + Table_InwardDetail + "(" +
            IWD_Autono + " int not null," +
            IWD_Id + " int," +
            IWD_Inwardid + " int," +
            IWD_Productid + " text," +
            IWD_Fathersku + " text," +
            IWD_Recqty + " float," +
            IWD_Rate + " float," +
            IWD_Totalamt + " float," +
            IWD_Tax + " float," +
            IWD_Taxamt + " float," +
            IWD_Productnetamt + " float," +
            IWD_Barcode + " text," +
            IWD_Itemname + " text," +
            IWD_Rebarcodest + " text," +
            IWD_Rebarcnt + " int," +
            IWD_Refinwid + " int," +
            IWD_Discamt + " float," +
            IWD_Branchid + " int," +
            IWD_Discfrompr + " float," +
            IWD_Purchaserate + " float," +
            IWD_Freeqty + " float," +
            IWD_Designno + " text," +
            IWD_Color + " text," +
            IWD_Itmimage + " text," +
            IWD_Imagepath + " text," +
            IWD_Mandalper + " float," +
            IWD_Mandal + " float," +
            IWD_Custdisper + " float," +
            IWD_Custdisamt + " float," +
            IWD_Itemsize + " text," +
            IWD_Itemsaleper + " float," +
            IWD_Jobworktyp + " text," +
            IWD_Oldbarcode + " text," +
            IWD_Expqty + " float," +
            IWD_Podetauto + " int," +
            IWD_Balanceqty + " float," +
            IWD_Mrp + " float," +
            IWD_Nan + " float," +
            IWD_Suppdisper + " float," +
            IWD_Suppdisamt + " float," +
            IWD_Repcolumn + " text," +
            IWD_Suppbilldisper + " float," +
            IWD_Suppbilldisamt + " float," +
            IWD_Otheraddded + " float," +
            IWD_Gvapp + " text," +
            IWD_Scheme1 + " text," +
            IWD_Scheme2 + " text," +
            IWD_Barcodeqty + " float," +
            IWD_Wsp + " float," +
            IWD_Hocode + " int," +
            IWD_Txincextyp + " text," +
            IWD_Netrate + " float," +
            IWD_Gstper + " float," +
            IWD_Cgstamt + " float," +
            IWD_Sgstamt + " float," +
            IWD_Igstamt + " float," +
            IWD_Cgstper + " float," +
            IWD_Sgstper + " float," +
            IWD_Cessper + " float," +
            IWD_Cessamt + " float," +
            IWD_Hsncode + " text," +
            IWD_Attr1 + " text," +
            IWD_Attr2 + " text," +
            IWD_Atrr3 + " text," +
            IWD_Attr4 + " text," +
            IWD_Atrr5 + " text," +
            IWD_Suppdisper1 + " float," +
            IWD_Suppdisamt1 + " float, primary key(" + IWD_Autono + "))";

    private String table_returngood_master = "create table if not exists " + Table_ReturnGoodMaster + "(" +
            RGM_Autoid + " int not null," +
            RGM_Id + " int," +
            RGM_Branchid + " int," +
            RGM_Supplierid + " int," +
            RGM_Retqty + " float," +
            RGM_Retamt + " float," +
            RGM_Finyr + " text," +
            RGM_Taxamt + " float," +
            RGM_Otheramt + " float," +
            RGM_Netpay + " float," +
            RGM_Inwardid + " text," +
            RGM_Createdt + " text," +
            RGM_Createby + " int," +
            RGM_Transport_Id + " int," +
            RGM_Lr_No + " text," +
            RGM_Lr_Date + " text," +
            RGM_Retdate + " text," +
            RGM_Dncreated + " text," +
            RGM_Remarks + " text," +
            RGM_Status + " text," +
            RGM_Cancelreason + " text," +
            RGM_Canceldate + " text," +
            RGM_Cancelby + " int," +
            RGM_Createtime + " text," +
            RGM_Returntype + " text," +
            RGM_Grrno + " text," +
            RGM_Authorisedby + " int," +
            RGM_Cstype + " text," +
            RGM_Parcelst + " text," +
            RGM_Tlbtamt + " float," +
            RGM_Amtinwrds + " text," +
            RGM_Grossamt + " float," +
            RGM_Totaldisamt + " float," +
            RGM_Roundup + " float," +
            RGM_Hocode + " int," +
            RGM_Reason + " text," +
            RGM_Othertax + " float," +
            RGM_Docpath + " text," +
            RGM_Cgstamt + " float," +
            RGM_Sgstamt + " float," +
            RGM_Igstapp + " text," +
            RGM_Igstamt + " float," +
            RGM_Trachar + " float," +
            RGM_Trataxamt + " float," +
            RGM_Tranamt + " float, primary key(" + RGM_Autoid + "))";

    private String table_returngood_detail = "create table if not exists " + Table_ReturnGoodDetail + "(" +
            RGD_Autoid + " int not null," +
            RGD_Id + " int," +
            RGD_Retautoid + " int," +
            RGD_Branchid + " int," +
            RGD_Barcode + " text," +
            RGD_Itemid + " int," +
            RGD_Rate + " float," +
            RGD_Retqty + " float," +
            RGD_Balqty + " float," +
            RGD_Finyr + " text," +
            RGD_Taxper + " float," +
            RGD_Taxamt + " float," +
            RGD_Inwardid + " int," +
            RGD_Lbtper + " float," +
            RGD_Lbtamt + " float," +
            RGD_Lbtcalamt + " float," +
            RGD_Replcolumn + " text," +
            RGD_Amount + " float," +
            RGD_Itmdisper + " float," +
            RGD_Itmdisamt + " float," +
            RGD_Total + " float," +
            RGD_Disper + " float," +
            RGD_Disamt + " float," +
            RGD_Otheradd + " float," +
            RGD_Hocode + " int," +
            RGD_Gstper + " float," +
            RGD_Cgstamt + " float," +
            RGD_Sgstamt + " float," +
            RGD_Igstamt + " float," +
            RGD_Cgstper + " float," +
            RGD_Sgstper + " float," +
            RGD_Cessper + " float," +
            RGD_Cessamt + " float," +
            RGD_Supdisc1 + " float," +
            RGD_Supdiscamt1 + " float," +
            RGD_Reason + " text, primary key(" + RGD_Autoid + "))";

    private String table_returnmemo_master = "create table if not exists " + Table_ReturnMemoMaster + "(" +
            RMM_Auto + " int not null," +
            RMM_Id + " int," +
            RMM_Machinename + " text," +
            RMM_Counterno + " text," +
            RMM_Rmemono + " text," +
            RMM_Billno + " text," +
            RMM_Custcode + " text," +
            RMM_Netbillamt + " float," +
            RMM_Returnqty + " float," +
            RMM_Returnamt + " float," +
            RMM_Dis + " float," +
            RMM_Tax + " float," +
            RMM_Grossamt + " float," +
            RMM_Netamt + " float," +
            RMM_Remark + " text," +
            RMM_Empname + " int," +
            RMM_Createby + " int," +
            RMM_Createdt + " text," +
            RMM_Modifiedby + " int," +
            RMM_Modifieddt + " text," +
            RMM_Deletedby + " int," +
            RMM_Deleteddt + " text," +
            RMM_Financialyr + " text," +
            RMM_Maxno + " int," +
            RMM_Branchid + " int," +
            RMM_Inwrds + " text," +
            RMM_Redeemst + " text," +
            RMM_Status + " text," +
            RMM_Actualcreatedate + " text," +
            RMM_Createtime + " text," +
            RMM_Type + " text," +
            RMM_Redeemtype + " text," +
            RMM_Specialright + " text," +
            RMM_Balredeem + " float," +
            RMM_Msreplclm + " text," +
            RMM_Billknockamt + " float," +
            RMM_Openst + " text," +
            RMM_Openby + " int," +
            RMM_Opendate + " text," +
            RMM_Openreason + " text," +
            RMM_Returnreason + " text," +
            RMM_Gatepassno + " text," +
            RMM_Cntype + " text," +
            RMM_Createdfrom + " text," +
            RMM_Revgainpts + " float," +
            RMM_Revrdmpts + " float," +
            RMM_Cgstamt + " float," +
            RMM_Sgstamt + " float," +
            RMM_Igstapp + " text," +
            RMM_Igstamt + " float, primary key(" + RMM_Auto + "))";

    private String table_returnmemo_detail = "create table if not exists " + Table_ReturnMemoDetails + "(" +
            RMD_Id + " int not null," +
            RMD_Rmemono + " text," +
            RMD_Itemcode + " int," +
            RMD_Barcode + " text," +
            RMD_Qty + " float," +
            RMD_Rate + " float," +
            RMD_Amt + " float," +
            RMD_Returnid + " int," +
            RMD_Branchid + " int," +
            RMD_Financialyr + " text," +
            RMD_Counterno + " text," +
            RMD_Autoid + " int," +
            RMD_Disper + " float," +
            RMD_Disamt + " float," +
            RMD_Vatper + " float," +
            RMD_Vatamt + " float," +
            RMD_Nonbarst + " text," +
            RMD_Itemname + " text," +
            RMD_Empid + " int," +
            RMD_Mrp + " float," +
            RMD_Mastid + " int," +
            RMD_Billdetauto + " int," +
            RMD_Billdisper + " float," +
            RMD_Billdisamt + " float," +
            RMD_Dtlid + " int," +
            RMD_Gstper + " float," +
            RMD_Cgstamt + " float," +
            RMD_Sgstamt + " float," +
            RMD_Cgstper + " float," +
            RMD_Sgstper + " float," +
            RMD_Cessper + " float," +
            RMD_Cessamt + " float," +
            RMD_Igstamt + " float," +
            RMD_Taxableamt + " float, primary key(" + RMD_Id + "))";

    private String table_dailypatyexp = "create table if not exists " + Table_DailyPettyExp + "(" +
            DPE_Autoid + " int not null," +
            DPE_Branchid + " int," +
            DPE_Date + " text," +
            DPE_Toemp + " text," +
            DPE_Amount + " float," +
            DPE_Exphead + " int," +
            DPE_Authoby + " int," +
            DPE_Remark + " text," +
            DPE_Createdby + " int," +
            DPE_Createddate + " text," +
            DPE_Status + " text," +
            DPE_Cancleby + " int," +
            DPE_Cancledate + " text," +
            DPE_Cancelreason + " text," +
            DPE_Amtinwords + " text," +
            DPE_Voutype + " text," +
            DPE_Againstref + " int," +
            DPE_Id + " int," +
            DPE_Finyr + " text," +
            DPE_Voucherno + " text," +
            DPE_Cstype + " int," +
            DPE_Counterno + " text," +
            DPE_Subhead + " text, primary key(" + DPE_Autoid + "))";

    /*private String view_billcash = "CREATE VIEW if not exists  "+VIEW_BillCash+" as SELECT" +
            "     CASE WHEN "+Table_BillMaster+"."+BM_Cashamt+" < 0 THEN 0 ELSE "+Table_BillMaster+"."+BM_Cashamt+
            " END AS "+BM_Cashamt+", "+Table_BillMaster+"."+BM_Creditamt+" AS "+BM_Creditamt+", " +Table_BillMaster+
            "."+BM_Billdate+", " +Table_BillMaster+"."+BM_Billno+", "+Table_BillMaster+"."+BM_Chqamt +", "+Table_BillMaster+"."+
            BM_Createdby+", "+Table_BillMaster+"."+BM_Branchid+", "+Table_BillMaster+"."+BM_Billst
            +Table_BillMaster+"."+BM_Gvamt +", " +Table_BillMaster+"."+BM_Netamt+", "+Table_BillMaster+"."+BM_Gvamt+", CASE WHEN "+
            Table_BillMaster+"."+BM_Netamt+" < 0 THEN ABS("+Table_BillMaster+"."+BM_Netamt+") WHEN "+Table_BillMaster+"."+BM_Balamt+
            " < 0 THEN ABS("+Table_BillMaster+"."+BM_Balamt+") ELSE 0 END AS "+Table_BillMaster+"."+BM_Cashback+", "+Table_BillMaster+"."+BM_Counterno+
             Table_BillMaster+"."+BM_CurrencyAmt+
            " ,   CASE WHEN "+Table_BillMaster+"."+BM_Netamt+"  > 0 THEN ("+Table_BillMaster+"."+BM_Netamt +"+"+Table_BillMaster+"."+BM_Gvamt+") - ("+
            Table_BillMaster+"."+BM_Cashamt+" + "+Table_BillMaster+"."+BM_Creditamt+" + "+Table_BillMaster+"."+BM_Gvamt +" + "+Table_BillMaster+"."+BM_Chqamt+
            " ) ELSE 0 END AS Balance,"+Table_BillMaster+"."+BM_Autono+", "+Table_BillMaster+"."+BM_Billingtime+" , "+Table_BillMaster+"."+BM_Billst+
            ", CASE WHEN "+Table_BillMaster+"."+BM_Creditamt+" > 0 THEN Through '-'"+ BM_CardBank+" ELSE '' END AS BankName, "+Table_BillMaster+"."+BM_Returnamt+", "
            +Table_BillMaster+"."+BM_Piamt+", "+Table_BillMaster+"."+BM_TheirRemark+", ROUND("+Table_BillMaster+"."+BM_Totalamt +" - "
            +Table_BillMaster+"."+BM_Disamt+", "+Table_BillMaster+"."+ BM_Vat4 +", "+Table_BillMaster+"."+BM_Vat12+
            ", 0) AS GrossSale,"+Table_CustomerMaster+"."+ CSM_Name +", "+Table_BillMaster+"."+ BM_CardBank+
            " FROM  "+Table_BillMaster+" INNER JOIN "+Table_CustomerMaster+" ON "+Table_BillMaster+"."+BM_Custid+" = "+Table_CustomerMaster+"."+CSM_Id;*/
    private String view_b = "CREATE VIEW if not exists  viewBill as SELECT " +Table_BillMaster + "." + BM_Disamt + ", " + Table_BillMaster + "." + BM_Vat4 + ", " + Table_BillMaster + "." + BM_Vat12 +
            "," + Table_CustomerMaster + "." + CSM_Name + ", " + Table_BillMaster + "." + BM_CardBank +
            " FROM  " + Table_BillMaster + " INNER JOIN " + Table_CustomerMaster + " ON " + Table_BillMaster + "." + BM_Custid + " = " + Table_CustomerMaster + "." + CSM_Id;


    private String view_billcash = "CREATE VIEW if not exists  " + VIEW_BillCash + " as SELECT" +
            "     CASE WHEN " + Table_BillMaster + "." + BM_Cashamt + " < 0 THEN 0 ELSE " + Table_BillMaster + "." + BM_Cashamt +
            " END AS " + BM_Cashamt + ", " + Table_BillMaster + "." + BM_Creditamt + " AS " + BM_Creditamt + ", " + Table_BillMaster +
            "." + BM_Billdate + ", " + Table_BillMaster + "." + BM_Billno + ", " + Table_BillMaster + "." + BM_Chqamt + ", " + Table_BillMaster + "." +
            BM_Createdby + ", " + Table_BillMaster + "." + BM_Branchid + ", " + Table_BillMaster + "." + BM_Billst
            + Table_BillMaster + "." + BM_Gvamt + ", " + Table_BillMaster + "." + BM_Netamt + ", " + Table_BillMaster + "." + BM_Gvamt + ", CASE WHEN " +
            Table_BillMaster + "." + BM_Netamt + " < 0 THEN ABS(" + Table_BillMaster + "." + BM_Netamt + ") WHEN " + Table_BillMaster + "." + BM_Balamt +
            " < 0 THEN ABS(" + Table_BillMaster + "." + BM_Balamt + ") ELSE 0 END AS  CashReturn , " + Table_BillMaster + "." + BM_Counterno +
            Table_BillMaster + "." + BM_CurrencyAmt +
            " ,   CASE WHEN " + Table_BillMaster + "." + BM_Netamt + "  > 0 THEN (" + Table_BillMaster + "." + BM_Netamt + "+" + Table_BillMaster + "." + BM_Gvamt + ") - (" +
            Table_BillMaster + "." + BM_Cashamt + " + " + Table_BillMaster + "." + BM_Creditamt + " + " + Table_BillMaster + "." + BM_Gvamt + " + " + Table_BillMaster + "." + BM_Chqamt +
            " ) ELSE 0 END AS Balance," + Table_BillMaster + "." + BM_Autono + ", " + Table_BillMaster + "." + BM_Billingtime + " , " + Table_BillMaster + "." + BM_Billst +
            ", CASE WHEN " + Table_BillMaster + "." + BM_Creditamt + " > 0 THEN Through -" + BM_CardBank + " ELSE '' END AS BankName, " + Table_BillMaster + "." + BM_Returnamt + ", "
            + Table_BillMaster + "." + BM_Piamt + ", " + Table_BillMaster + "." + BM_TheirRemark + ", ROUND(" + Table_BillMaster + "." + BM_Totalamt + " - "
            + Table_BillMaster + "." + BM_Disamt + ", " + Table_BillMaster + "." + BM_Vat4 + ", " + Table_BillMaster + "." + BM_Vat12 +
            ", 0) AS GrossSale," + Table_CustomerMaster + "." + CSM_Name + ", " + Table_BillMaster + "." + BM_CardBank +
            " FROM  " + Table_BillMaster + " INNER JOIN " + Table_CustomerMaster + " ON " + Table_BillMaster + "." + BM_Custid + " = " + Table_CustomerMaster + "." + CSM_Id;

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
        db.execSQL(table_updatebill);
        db.execSQL(table_paymenttable);
        db.execSQL(table_inward_master);
        db.execSQL(table_inward_detail);
        db.execSQL(table_returngood_master);
        db.execSQL(table_returngood_detail);
        db.execSQL(table_returnmemo_master);
        db.execSQL(table_returnmemo_detail);
        db.execSQL(table_dailypatyexp);
       // db.execSQL(view_billcash);
        //db.execSQL(view_b);
        Constant.showLog("onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public Cursor getPaymentType() {
        return getWritableDatabase().rawQuery("select * from " + Table_Payment + " where " + PY_Status + " in ('Y','A') order by " + PY_TYPE, null);
    }

    public int getMaxAuto() {
        int id = 0;
        Cursor res = getWritableDatabase().rawQuery("select max(" + BM_Autono + ") from " + Table_BillMaster, null);
        if (res.moveToFirst()) {
            do {
                id = res.getInt(0);
            } while (res.moveToNext());
        }
        res.close();
        return ++id;
    }

    public int getMaxMastId(String finYR) {
        int id = 0;
        Cursor res = getWritableDatabase().rawQuery("select max(" + BM_Id + ") from " + Table_BillMaster + " where " + BM_Finyr + "='" + finYR + "'", null);
        if (res.moveToFirst()) {
            do {
                id = res.getInt(0);
            } while (res.moveToNext());
        }
        res.close();
        return ++id;
    }

    public int getMaxDetAuto() {
        int id = 0;
        Cursor res = getWritableDatabase().rawQuery("select max(" + BD_Auto + ") from " + Table_BillDetails, null);
        if (res.moveToFirst()) {
            do {
                id = res.getInt(0);
            } while (res.moveToNext());
        }
        res.close();
        return ++id;
    }

    public int getMaxDetId(int mastAuto) {
        int id = 0;
        Cursor res = getWritableDatabase().rawQuery("select max(" + BD_Id + ") from " + Table_BillDetails + " where " + BD_Billid + "=" + mastAuto, null);
        if (res.moveToFirst()) {
            do {
                id = res.getInt(0);
            } while (res.moveToNext());
        }
        res.close();
        return ++id;
    }

    public int getMaxCustAuto() {
        int id = 0;
        Cursor res = getWritableDatabase().rawQuery("select max(" + CSM_Auto + ") from " + Table_CustomerMaster, null);
        if (res.moveToFirst()) {
            do {
                id = res.getInt(0);
            } while (res.moveToNext());
        }
        res.close();
        return ++id;
    }

    public int getMaxCustId(int mastAuto) {
        int id = 0;
        Cursor res = getWritableDatabase().rawQuery("select max(" + CSM_Id + ") from " + Table_CustomerMaster, null);
        if (res.moveToFirst()) {
            do {
                id = res.getInt(0);
            } while (res.moveToNext());
        }
        res.close();
        return ++id;
    }

    public Cursor getProductData() {
        return getWritableDatabase().rawQuery("select * from " + Table_ProductMaster
                + " where " + PM_Active + " in('Y','A') order by " + PM_Cat3, null);
    }

    public int saveBillMaster(BillMasterClass mast) {
        ContentValues cv = new ContentValues();
        cv.put(BM_Autono, mast.getAutoNo());
        cv.put(BM_Id, mast.getId());
        cv.put(BM_Branchid, mast.getBranchID());
        cv.put(BM_Finyr, mast.getFinYr());
        cv.put(BM_Billno, mast.getBillNo());
        cv.put(BM_Custid, mast.getCustID());
        cv.put(BM_Jobcardid, mast.getJobCardID());
        cv.put(BM_Billdate, mast.getBillDate());
        cv.put(BM_Totalqty, mast.getTotalQty());
        cv.put(BM_Totalamt, mast.getTotalAmt());
        cv.put(BM_Retmemono, mast.getRetMemoNo());
        cv.put(BM_Returnamt, mast.getReturnAmt());
        cv.put(BM_Creditamt, mast.getCreditAmt());
        cv.put(BM_Cashamt, mast.getCashAmt());
        cv.put(BM_Paidamt, mast.getPaidAmt());
        cv.put(BM_Balamt, mast.getBalAmt());
        cv.put(BM_Brakeupamt, mast.getBrakeUpAmt());
        cv.put(BM_Billst, mast.getBillSt());
        cv.put(BM_Vehicleno, mast.getVehicleNo());
        cv.put(BM_Createdby, mast.getCreatedby());
        cv.put(BM_Createddt, mast.getCreateddt());
        cv.put(BM_Modifiedby, mast.getModifiedby());
        cv.put(BM_Modifieddt, mast.getModifieddt());
        cv.put(BM_Vehiclemake, mast.getVehiclemake());
        cv.put(BM_Vehiclecolor, mast.getVehiclecolor());
        cv.put(BM_Drivername, mast.getDrivername());
        cv.put(BM_Vat12, mast.getVat12());
        cv.put(BM_Vat4, mast.getVat4());
        cv.put(BM_Labour, mast.getLabour());
        cv.put(BM_Cardno, mast.getCardno());
        cv.put(BM_Chqno, mast.getChqno());
        cv.put(BM_Chqamt, mast.getChqamt());
        cv.put(BM_Chqdt, mast.getChqdt());
        cv.put(BM_Advanceamt, mast.getAdvanceamt());
        cv.put(BM_Paymenttype, mast.getPaymenttype());
        cv.put(BM_Billpayst, mast.getBillpayst());
        cv.put(BM_Netamt, mast.getNetamt());
        cv.put(BM_Bankid, mast.getBankID());
        cv.put(BM_Comminper, mast.getCommInPer());
        cv.put(BM_Comminrs, mast.getCommInRs());
        cv.put(BM_Printno, mast.getPrintno());
        cv.put(BM_Disper, mast.getDisper());
        cv.put(BM_Disamt, mast.getDisamt());
        cv.put(BM_Inwrds, mast.getInwrds());
        cv.put(BM_Refundpyst, mast.getRefundpyst());
        cv.put(BM_Pino, mast.getPino());
        cv.put(BM_Piamt, mast.getPiamt());
        cv.put(BM_Mon, mast.getMon());
        cv.put(BM_Gvoucher, mast.getGVoucher());
        cv.put(BM_Gvoucheramt, mast.getGVoucherAmt());
        cv.put(BM_Agentid, mast.getAgentid());
        cv.put(BM_Vouchergenerate, mast.getVouchergenerate());
        cv.put(BM_Mon, mast.getGVSchemeId());
        cv.put(BM_Scheme, mast.getScheme());
        cv.put(BM_Gv, mast.getGV());
        cv.put(BM_Gvamt, mast.getGVAmt());
        cv.put(BM_Gvno, mast.getGVNo());
        cv.put(BM_Issuegvst, mast.getIssueGVSt());
        cv.put(BM_Createdfrm, mast.getCreatedfrm());
        cv.put(BM_Grossamount, mast.getGrossAmount());
        cv.put(BM_Cancelledby, mast.getCancelledBy());
        cv.put(BM_Cancelleddate, mast.getCancelledDate());
        cv.put(BM_Billingtime, mast.getBillingtime());
        cv.put(BM_Delivered, mast.getDelivered());
        cv.put(BM_Deliveredby, mast.getDeliveredby());
        cv.put(BM_Delivereddate, mast.getDeliveredDate());
        cv.put(BM_Alteration, mast.getAlteration());
        cv.put(BM_Cashback, mast.getCashBack());
        cv.put(BM_Schemeamt, mast.getSchemeAmt());
        cv.put(BM_Goodsreturn, mast.getGoodsReturn());
        cv.put(BM_Counterno, mast.getCounterNo());
        cv.put(BM_Machinename, mast.getMachineName());
        cv.put(BM_Againstdc, mast.getAgainstDC());
        cv.put(BM_Dcautono, mast.getDCAutoNo());
        cv.put(BM_CancelReason, mast.getCancelReason());
        cv.put(BM_Tender, mast.getTender());
        cv.put(BM_RemainAmt, mast.getRemainAmt());
        cv.put(BM_TRetQty, mast.getTRetQty());
        cv.put(BM_Currencyid, mast.getCurrencyid());
        cv.put(BM_TotalCurrency, mast.getTotalCurrency());
        cv.put(BM_CurrencyAmt, mast.getCurrencyAmt());
        cv.put(BM_CardBank, mast.getCardBank());
        cv.put(BM_CardTyp, mast.getCardTyp());
        cv.put(BM_SwipReceiptNo, mast.getSwipReceiptNo());
        cv.put(BM_Reference, mast.getReference());
        cv.put(BM_Repl_Column, mast.getRepl_Column());
        cv.put(BM_Through, mast.getThrough());
        cv.put(BM_Authorised, mast.getAuthorised());
        cv.put(BM_Remark, mast.getRemark());
        cv.put(BM_TheirRemark, mast.getTheirRemark());
        cv.put(BM_CGSTAMT, mast.getCGSTAMT());
        cv.put(BM_SGSTAMT, mast.getSGSTAMT());
        cv.put(BM_IGSTAPP, mast.getIGSTAPP());
        cv.put(BM_IGSTAMT, mast.getIGSTAMT());
        cv.put(BM_Type, mast.getType());
        cv.put(BM_BothId, mast.getBothId());
        cv.put(BM_NewAgainstDC, mast.getNewAgainstDC());
        cv.put(BM_NewDCNo, mast.getNewDCNo());
        getWritableDatabase().insert(Table_BillMaster, null, cv);
        return mast.getAutoNo();
    }

    public void saveBillDetail(BillDetailClass det) {
        ContentValues cv = new ContentValues();
        cv.put(BD_Auto, det.getAuto());
        cv.put(BD_Id, det.getId());
        cv.put(BD_Billid, det.getBillID());
        cv.put(BD_Branchid, det.getBranchID());
        cv.put(BD_Finyr, det.getFinYr());
        cv.put(BD_Itemid, det.getItemId());
        cv.put(BD_Rate, det.getRate());
        cv.put(BD_Qty, det.getQty());
        cv.put(BD_Total, det.getTotal());
        cv.put(BD_Barcode, det.getBarcode());
        cv.put(BD_Fathersku, det.getFatherSKU());
        cv.put(BD_Empid, det.getEmpID());
        cv.put(BD_Incentamt, det.getIncentamt());
        cv.put(BD_Autobillid, det.getAutoBillId());
        cv.put(BD_Vat, det.getVat());
        cv.put(BD_Vatamt, det.getVatamt());
        cv.put(BD_Amtwithoutdisc, det.getAmtWithoutDisc());
        cv.put(BD_Disper, det.getDisper());
        cv.put(BD_Disamt, det.getDisamt());
        cv.put(BD_Nonbar, det.getNonBar());
        cv.put(BD_Mon, det.getMon());
        cv.put(BD_Ratewithtax, det.getRatewithtax());
        cv.put(BD_Purchaseqty, det.getPurchaseQty());
        cv.put(BD_Freeqty, det.getFreeqty());
        cv.put(BD_Alteredstat, det.getAlteredStat());
        cv.put(BD_Mandalper, det.getMandalper());
        cv.put(BD_Mandal, det.getMandal());
        cv.put(BD_Delivered, det.getDelivered());
        cv.put(BD_Deliveredby, det.getDeliveredby());
        cv.put(BD_Delivereddate, det.getDelivereddate());
        cv.put(BD_Bgtype, det.getBGType());
        cv.put(BD_Allotid, det.getAllotid());
        cv.put(BD_Schemeapp, det.getSchemeApp());
        cv.put(BD_Schemeid, det.getSchemeid());
        cv.put(BD_Dispfsku, det.getDispFSKU());
        cv.put(BD_Mrp, det.getMRP());
        cv.put(BD_Billdisper, det.getBilldisper());
        cv.put(BD_Billdisamt, det.getBilldisamt());
        cv.put(BD_Dcmastauto, det.getDcmastauto());
        cv.put(BD_Designno, det.getDesignNo());
        cv.put(BD_Retqty, det.getRetQty());
        cv.put(BD_Actrate, det.getActRate());
        cv.put(BD_Actmrp, det.getActMRP());
        cv.put(BD_Schmbenefit, det.getSchmBenefit());
        cv.put(BD_Gstper, det.getGSTPER());
        cv.put(BD_Cgstamt, det.getCGSTAMT());
        cv.put(BD_Sgstamt, det.getSGSTAMT());
        cv.put(BD_Cgstper, det.getCGSTPER());
        cv.put(BD_Sgstper, det.getSGSTPER());
        cv.put(BD_Cessper, det.getCESSPER());
        cv.put(BD_Cessamt, det.getCESSAMT());
        cv.put(BD_Igstamt, det.getIGSTAMT());
        cv.put(BD_Taxableamt, det.getTaxableAmt());
        cv.put(BD_Type, det.getType());
        cv.put(BD_Seqno, det.getSeqno());
        getWritableDatabase().insert(Table_BillDetails, null, cv);
    }

    public void addExpenseDetail(List<DailyPettyExpClass> detailList) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        ContentValues cv = new ContentValues();
        for (DailyPettyExpClass detail : detailList) {
            cv.put(DPE_Autoid, detail.getAutoid());
            cv.put(DPE_Date, detail.getDate());
            // cv.put(EXP_Time,detail.getTime());
            cv.put(DPE_Remark, detail.getRemark());
            cv.put(DPE_Amount, detail.getAmount());
            db.insert(Table_DailyPettyExp, null, cv);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public int getExpMaxAuto() {
        int a = 0;
        String str = "select MAX(" + DPE_Autoid + ") from " + Table_DailyPettyExp;
        Constant.showLog(str);
        Cursor cursor = getWritableDatabase().rawQuery(str, null);
        if (cursor.moveToFirst()) {
            a = cursor.getInt(0);
        }
        cursor.close();
        return a;
    }

    public Cursor getExpenseReportData(String fdate, String tdate, int flag) {
        String str = "";
        if (flag == 0) {
            str = "Select * from " + Table_DailyPettyExp + " where " + DPE_Date + ">= '" + fdate + "' and " + DPE_Date + "<= '" + tdate + "'";
        } else {
            str = "Select * from " + Table_DailyPettyExp;
        }
        return getWritableDatabase().rawQuery(str, null);
    }

    public Cursor getListExpRemark() {
        String str = "select distinct " + DPE_Remark + " from " + Table_DailyPettyExp + " where " + DPE_Remark + " <>'NA' AND " + DPE_Remark + " NOT LIKE ('')";
        Log.d("Log", str);
        return getWritableDatabase().rawQuery(str, null);
    }

    public Cursor getListExpHead() {
        String str = "select distinct " + EXM_Expdesc + " from " + Table_ExpenseHead + " where " + EXM_Expdesc + " <>'NA' AND " + EXM_Expdesc + " NOT LIKE ('')";
        Log.d("Log", str);
        return getWritableDatabase().rawQuery(str, null);
    }

    public Cursor getDistinctProduct() {
        String str = "Select distinct " + PM_Cat3 + " from " + Table_ProductMaster;
        return getWritableDatabase().rawQuery(str, null);
    }

    public Cursor getDistinctSupplier() {
        String str = "Select distinct " + SM_Name + " from " + Table_SupplierMaster;
        return getWritableDatabase().rawQuery(str, null);
    }

    public List<BillReprintCancelClass> getBillMasterData(String fromDate, String toData) {
        List<BillReprintCancelClass> list = new ArrayList<>();
        String str = "select * from " + Table_BillMaster + " order by " + BM_Autono + " desc";
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
            do {
                BillReprintCancelClass bill = new BillReprintCancelClass();
                bill.setAuto(res.getInt(res.getColumnIndex(BM_Autono)));
                bill.setBillNo(res.getString(res.getColumnIndex(BM_Billno)));
                bill.setStatus(res.getString(res.getColumnIndex(BM_Billst)));
                bill.setBillDate(res.getString(res.getColumnIndex(BM_Billdate)));
                bill.setBillTime(res.getString(res.getColumnIndex(BM_Billingtime)));
                bill.setNetAmt(res.getString(res.getColumnIndex(BM_Netamt)));
                bill.setCGSTAMNT(res.getString(res.getColumnIndex(BM_CGSTAMT)));
                bill.setSGSTAMNT(res.getString(res.getColumnIndex(BM_SGSTAMT)));
                list.add(bill);
            } while (res.moveToNext());
        }
        res.close();
        return list;
    }

    public List<BillDetailClassR> getBillDetailData(BillReprintCancelClass billM) {
        //String  str = "select * from "+BillMaster_Table+" where "+BillMaster_CrDate+">='"+fromDate+"' and "+BillMaster_CrDate+"<='"+fromDate+"'";
        List<BillDetailClassR> list = new ArrayList<>();
        String str = "select " + Table_ProductMaster + "." + PM_Cat3 + " as Prod, " + Table_BillDetails + "." + BD_Qty + "," +
                Table_BillDetails + "." + BD_Rate + "," + Table_BillDetails + "." + BD_Total +
                "," + Table_BillDetails + "." + BD_Cgstper + "," + Table_BillDetails + "." + BD_Sgstper +
                " from " + Table_BillDetails + "," + Table_ProductMaster +
                " where " + Table_BillDetails + "." + BD_Itemid + "=" + Table_ProductMaster + "." + PM_Id
                + " and " + Table_BillDetails + "." + BD_Billid + "=" + billM.getAuto();
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
            do {
                BillDetailClassR bill = new BillDetailClassR();
                bill.setProd(res.getString(res.getColumnIndex("Prod")));
                bill.setQty(res.getFloat(res.getColumnIndex(BD_Qty)));
                bill.setRateStr(res.getString(res.getColumnIndex(BD_Rate)));
                bill.setTotalStr(res.getString(res.getColumnIndex(BD_Total)));
                bill.setCGSTPER(res.getFloat(res.getColumnIndex(BD_Cgstper)));
                bill.setSGSTPER(res.getFloat(res.getColumnIndex(BD_Sgstper)));
                list.add(bill);
            } while (res.moveToNext());
        }
        res.close();
        return list;
    }

    public void cancelBill(BillReprintCancelClass bill, String date) {
        ContentValues cv = new ContentValues();
        cv.put(BM_Billst, "C");
        cv.put(BM_Modifiedby, 1);
        cv.put(BM_Modifieddt, date);
        getWritableDatabase().update(Table_BillMaster, cv, BM_Autono + "=? and " + BM_Billno + "=?", new String[]{String.valueOf(bill.getAuto()), bill.getBillNo()});
    }

    public Cursor getAllProduct() {
        return getWritableDatabase().rawQuery("select * from " + Table_ProductMaster + " order by " + PM_Cat3, null);
    }

    public void inActivateItem(String masterType, String tableName, int id, String cat) {
        ContentValues cv = new ContentValues();
        if (masterType.equals("product")) {
            cv.put(PM_Active, "N");
            getWritableDatabase().update(tableName, cv, PM_Id + "=? and " + PM_Cat3 + "=?", new String[]{String.valueOf(id), cat});
        }
    }

    public Cursor getCustomerData() {
        return getWritableDatabase().rawQuery("select " + CSM_Name + "," + CSM_Mobno + " from " + Table_CustomerMaster
                + " order by " + CSM_Name, null);
    }

    public int getExpHeadAuto(String name) {
        int a = 0;
        String str = "select id from exphead where name='name'";
        Constant.showLog(str);
        Cursor cursor = getWritableDatabase().rawQuery(str, null);
        if (cursor.moveToFirst()) {
            a = cursor.getInt(0);
        }
        cursor.close();
        return a;
    }

    public String getExpHeadName(int auto) {
        String a = "";
        String str = "select name from exphead where id = auto";
        Constant.showLog(str);
        Cursor cursor = getWritableDatabase().rawQuery(str, null);
        if (cursor.moveToFirst()) {
            a = cursor.getString(0);
        }
        cursor.close();
        return a;
    }

    public Cursor getCollectionSumData() {
        String str = "Select * from " + VIEW_BillCash;
        return getWritableDatabase().rawQuery(str, null);
    }

    public String getCustid(String custName, String mobNo){
        String id = "0";
        String str =  "select "+CSM_Id+" from "+Table_CustomerMaster+" where "+CSM_Name+"='"+custName+"' and "+
                    CSM_Mobno+"='"+mobNo+"'";
        Constant.showLog(str);
        Cursor res = getWritableDatabase().rawQuery(str,null);
        if(res.moveToFirst()){
            do{
                id = res.getString(0);
            }while (res.moveToNext());
        }
        res.close();
        return id;
    }

    public String saveCustomerMaster(String custName, String mobNo){
        int auto;
        String id;
        auto = getMaxCustAuto();
        id = auto+"#"+1;
        ContentValues cv = new ContentValues();
        cv.put(CSM_Auto,auto);
        cv.put(CSM_Id,id);
        cv.put(CSM_Name,custName);
        cv.put(CSM_Mobno,mobNo);
        getWritableDatabase().insert(Table_CustomerMaster,null,cv);
        return id;
    }

    public String getCompIni(){
        String ini = "PA";
        String str =  "select "+CPM_Initials+" from "+Table_CompanyMaster;
        Constant.showLog(str);
        Cursor res = getWritableDatabase().rawQuery(str,null);
        if(res.moveToFirst()){
            do{
                ini = res.getString(0);
            }while (res.moveToNext());
        }
        res.close();
        return ini;
    }

    public Cursor getGSTPer(String gstGroup){
        String str = "select * from GSTMaster,GSTDetail where GSTMaster.Auto=GSTDetail.MastAuto and GSTMaster.GroupName='"+gstGroup+"'";
        Constant.showLog(str);
        return getWritableDatabase().rawQuery(str,null);
    }

}
