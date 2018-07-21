package com.pait.smartpos.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.pait.smartpos.db.DBHandlerR;

//Created by ANUP on 28-05-2018.

public class XlsxCon {

    private SQLiteDatabase db;
    private DBHandlerR dbHelper;

    public static final String Sheet_CPM = "CompanyMaster";
    public static final String Sheet_EM = "EmployeeMaster";
    public static final String Sheet_PM = "ProductMaster";
    public static final String Sheet_SM = "SupplierMaster";
    public static final String Sheet_CSM = "CustomerMaster";
    public static final String Sheet_GM = "GSTMaster";
    public static final String Sheet_GD = "GSTDetail";
    public static final String Sheet_PY = "PaymentType";

    /*public static final String TM_ID = "ID";
    public static final String TM_Tables = "Tables";
    public static final String TM_Active = "Active";

    public static final String PM_ID = "ID";
    public static final String PM_Name = "Product_Name";
    public static final String PM_Cat = "Product_Cat";
    public static final String PM_Rate = "Product_Rate";
    public static final String PM_Barcode1 = "Product_Barcode";
    public static final String PM_Kitchen = "Product_Kitchen";
    public static final String PM_Active1 = "Product_Active";
    public static final String PM_DiscPer = "DiscPer";
    public static final String PM_GSTGroup = "GSTGroup";
    public static final String PM_HSNCode = "HSNCode";
    public static final String PM_TaxType = "TaxType";
    public static final String PM_Branchid = "Branchid";

    public static final String CM_ID = "ID";
    public static final String CM_Category = "Category";
    public static final String CM_Active = "Active";*/

    public static final String GM_Auto = "Auto";
    public static final String GM_GroupName = "GroupName";
    public static final String GM_Status = "Status";

    public static final String GD_Auto = "Auto";
    public static final String GD_MastAuto = "MastAuto";
    public static final String GD_FromRange = "FromRange";
    public static final String GD_ToRange = "ToRange";
    public static final String GD_GSTPer = "GSTPer";
    public static final String GD_CGSTPer = "CGSTPer";
    public static final String GD_SGSTPer = "SGSTPer";
    public static final String GD_CGSTShare = "CGSTShare";
    public static final String GD_SGSTShare = "SGSTShare";
    public static final String GD_CESSPer = "CESSPer";

    public static final String CPM_Auto = "Auto";
    public static final String CPM_Id = "Id";
    public static final String CPM_CompanyName = "CompanyName";
    public static final String CPM_City = "City";
    public static final String CPM_State = "State";
    public static final String CPM_Address = "Address";
    public static final String CPM_Phone = "Phone";
    public static final String CPM_Email = "Email";
    public static final String CPM_Initials = "Initials";
    public static final String CPM_PrintMsg = "PrintMsg";
    public static final String CPM_PrintName = "PrintName";
    public static final String CPM_PANNo = "PANNo";
    public static final String CPM_GSTNo = "GSTNo";
    public static final String CPM_MailUname = "MailUname";
    public static final String CPM_MailPwd = "MailPwd";
    public static final String CPM_Mailfrom = "Mailfrom";
    public static final String CPM_SMTPServer = "SMTPServer";

    public static final String EM_Auto = "Auto";
    public static final String EM_Id = "Id";
    public static final String EM_Branchcode = "Branchcode";
    public static final String EM_Empname = "Empname";
    public static final String EM_Address = "Address";
    public static final String EM_Empdob = "Empdob";
    public static final String EM_Mobile = "Mobile";
    public static final String EM_Usernm = "Usernm";
    public static final String EM_Pass = "Pass";
    public static final String EM_Desc1 = "Desc1";

    public static final String PM_Id = "Id";
    public static final String PM_Cat1 = "Cat1";
    public static final String PM_Cat2 = "Cat2";
    public static final String PM_Cat3 = "Cat3";
    public static final String PM_Cat4 = "Cat4";
    public static final String PM_Cat5 = "Cat5";
    public static final String PM_Cat6 = "Cat6";
    public static final String PM_Finalproduct = "Finalproduct";
    public static final String PM_Unit = "Unit";
    public static final String PM_Pprice = "Pprice";
    public static final String PM_Mrp = "Mrp";
    public static final String PM_Wprice = "Wprice";
    public static final String PM_Active = "Active";
    public static final String PM_Barcode = "Barcode";
    public static final String PM_Ssp = "Ssp";
    public static final String PM_Gstgroup = "Gstgroup";
    public static final String PM_Hsncode = "Hsncode";

    public static final String SM_Auto = "Auto";
    public static final String SM_Id = "Id";
    public static final String SM_Name = "Name";
    public static final String SM_Address = "Address";
    public static final String SM_Phone1 = "Phone1";
    public static final String SM_Phone2 = "Phone2";
    public static final String SM_Mobile = "Mobile";
    public static final String SM_Email = "Email";
    public static final String SM_CP = "Contactperson";
    public static final String SM_Remark = "Remark";
    public static final String SM_City = "City";
    public static final String SM_GSTNO = "Gstno";

    public static final String CSM_Auto = "Auto";
    public static final String CSM_Id = "Id";
    public static final String CSM_Name = "Name";
    public static final String CSM_Cityid = "Cityid";
    public static final String CSM_Address = "Address";
    public static final String CSM_Area = "Area";
    public static final String CSM_Active = "Active";
    public static final String CSM_Phone = "Phone";
    public static final String CSM_Mobno = "Mobno";
    public static final String CSM_Dob = "Dob";
    public static final String CSM_Anniversarydate = "Anniversarydate";
    public static final String CSM_Panno = "Panno";
    public static final String CSM_Gstno = "Gstno";

    public static final String PY_Auto = "Auto";
    public static final String PY_TYPE = "Type";
    public static final String PY_Status = "Status";

    public XlsxCon(Context context){
        dbHelper = new DBHandlerR(context);
    }

    public void open(){
        if(db==null||db.isOpen()){
            try{
                db = dbHelper.getWritableDatabase();
            }catch (SQLiteException e){
                e.printStackTrace();
            }
        }
    }

    public void delete(String tableName){
        db.execSQL("delete from " + tableName);
    }

    public int insert(String table, ContentValues cv){
        return (int) db.insert(table,null,cv);
    }

}
