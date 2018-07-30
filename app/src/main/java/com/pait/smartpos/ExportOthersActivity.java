package com.pait.smartpos;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;

import com.pait.smartpos.ConnectivityTest;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.log.CopyLog;
import com.pait.smartpos.log.WriteLog;
import com.pait.smartpos.permission.GetPermission;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class ExportOthersActivity  extends AppCompatActivity implements View.OnClickListener {

    private Constant constant, constant1;
    private Toast toast;
    private String currMonth, prevMonth1, prevMonth2, prevMonth3, prevMonth4, prevMonth5, prevMonth6;
    private TextView tv_filename;
    private ListView lv_list;
    private String[] file;

    private GetPermission permission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_excel);

        init();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        showDia(1);

        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Constant.showLog("file :" + file[i]);
                Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                String myFilePath = Constant.checkFolder(Constant.folder_name).getAbsolutePath() + File.separator + file[i];
                Constant.showLog("file path:" + myFilePath);

                // intentShareFile.setType("application/xls");
                // intentShareFile.setType("text/plain");

               /* <intent-filter>
                <action android:name="android.intent.action.SEND"/>
                <category android:name="android.intent.category.DEFAULT"/>
                <data android:mimeType="audio*//*"/>
                <data android:mimeType="video*//*"/>
                <data android:mimeType="image*//*"/>
                <data android:mimeType="text/plain"/>
                <data android:mimeType="text/x-vcard"/>
                <data android:mimeType="application/pdf"/>
                <data android:mimeType="application/vnd.openxmlformats-officedocument.wordprocessingml.document"/>
                <data android:mimeType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"/>
                <data android:mimeType="application/vnd.openxmlformats-officedocument.presentationml.presentation"/>
                <data android:mimeType="application/msword"/>
                <data android:mimeType="application/vnd.ms-excel"/>
                <data android:mimeType="application/vnd.ms-powerpoint"/>
                </intent-filter>*/

                intentShareFile.setType("application/vnd.ms-excel");
                intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + myFilePath));
                intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File...");
                intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
                startActivity(Intent.createChooser(intentShareFile, "Share File"));

            }
        });
    }

    private void checkpermmission(){
        if(!permission.checkReadExternalStoragePermission(getApplicationContext())){
            permission.requestReadExternalPermission(getApplicationContext(),ExportOthersActivity.this);//2
        }else if(!permission.checkWriteExternalStoragePermission(getApplicationContext())){
            permission.requestWriteExternalPermission(getApplicationContext(),ExportOthersActivity.this);//3
        }else{
            exportToExcel();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 2:
                checkpermmission();
                break;
            case 3:
                checkpermmission();
                break;
            case 4:
                checkpermmission();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case 0:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //showDia(0);
        new Constant(ExportOthersActivity.this).doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //showDia(0);
                new Constant(ExportOthersActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        constant = new Constant(ExportOthersActivity.this);
        constant1 = new Constant(ExportOthersActivity.this);
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        lv_list = (ListView) findViewById(R.id.lv_list);
        permission = new GetPermission();
    }

    private void exportToExcel(){
        getMonths();
        long datetime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MMM_yyyy_hh_mm_ss", Locale.ENGLISH);
        Date resultdate = new Date(datetime);
        String cur_date = sdf.format(resultdate);
        String exportFileName = "";
        exportFileName = "Report_"+cur_date + ".xls";
        writeLog("exportToExcel_"+exportFileName);
        new writeFile().execute(exportFileName);
    }

    private class writeFile extends AsyncTask<String, Void, String> {
        ProgressDialog pd1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd1 = new ProgressDialog(ExportOthersActivity.this);
            pd1.setMessage("Exporting Data Please Wait");
            pd1.setCancelable(false);
            pd1.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String status = "";
            try {
                File directory = Constant.checkFolder(Constant.folder_name);
                if (!directory.exists()) {
                    if (directory.mkdirs()) {
                        Constant.showLog("TAG");
                    }
                }
                File outputFile = new File(directory, strings[0]);

                Workbook wb = new HSSFWorkbook();
                CellStyle cs = wb.createCellStyle();
                Font font = wb.createFont();
                //InputStream stream = getResources().openRawResource(R.raw.template);
                //XSSFWorkbook wb = new XSSFWorkbook(stream);
                //XSSFWorkbook wb = new XSSFWorkbook (stream);
                //XSSFCellStyle cs = xssfwb.createCellStyle();
                //XSSFFont font = xssfwb.createFont();
                font.setBoldweight(Font.BOLDWEIGHT_BOLD);
                cs.setFont(font);
                writeLog("exportToExcel_writeFile_exportSaleReport_called");
                // exportSaleReport(wb, cs);
                //exportSaleReportXSSF(xssfwb, cs);

                exportProductStockReport(wb,cs);

                FileOutputStream os = new FileOutputStream(outputFile);
                //xssfwb.write(os);
                wb.write(os);
                os.close();
                writeLog("ExportToExcel_writeFile_Success");
            } catch (Exception e) {
                status = null;
                e.printStackTrace();
                writeLog("ExportToExcel_writeFile_"+e.getMessage());
            }
            return status;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd1.dismiss();
            if (result != null) {
                getAllFiles();
                //showDia(4);
            } else {
                writeLog("ExportToExcel_Error_While_Exportin_Data");
                toast.setText("Error While Exporting Data. Please Try Again");
                toast.show();
            }
        }
    }

    private void exportProductStockReport(Workbook wb, CellStyle cs){
        Constant.showLog("Export_product_Report");
        Sheet sheet = wb.createSheet("ProductWise Stock Report");

        cs.setAlignment(CellStyle.ALIGN_LEFT);
        String arr[] = new String[]{DBHandler.PM_Finalproduct,DBHandler.PM_Pprice,DBHandler.PM_Mrp,
                DBHandler.PM_Wprice,DBHandler.PM_Ssp,DBHandler.PM_StockQty};

        Row row = sheet.createRow(2);
        for(int i=0;i<arr.length;i++){
            Cell c = row.createCell(i);
            c.setCellValue(arr[i]);
            c.setCellStyle(cs);
        }

        int count = 2;
        Cursor res = new DBHandler(getApplicationContext()).getExpProductReportData();
        if (res.moveToFirst()) {
            do {
                count++;
                Row row1 = sheet.createRow(count);
                for (int j = 0; j < res.getColumnCount(); j++) {
                    Cell cell = row1.createCell(j);
                    cell.setCellValue(res.getString(j));
                }
            } while (res.moveToNext());
        }
        writeLog("exportToExcel_writeFile_exportProductStockReport_called");
        exportExpenseReport(wb,cs);
    }

    private void exportExpenseReport(Workbook wb, CellStyle cs){
        Constant.showLog("exportExpenseReport");
        Sheet sheet = wb.createSheet("Expense Report");

        String arr[] = new String[]{DBHandler.DPE_Createddate,DBHandler.DPE_Exphead,
                DBHandler.DPE_Remark,DBHandler.DPE_Amount};
        Row row = sheet.createRow(1);
        for(int i=0;i<arr.length;i++){
            Cell c = row.createCell(i);
            c.setCellValue(arr[i]);
            c.setCellStyle(cs);
        }
        int count = 1;
        Cursor res = new DBHandler(getApplicationContext()).getExExpenseReportData();
        if(res.moveToFirst()){
            do{
                count++;
                Row row1 = sheet.createRow(count);
                for (int j = 0; j < res.getColumnCount(); j++) {
                    Cell cell = row1.createCell(j);
                    if(j==1){
                        cell.setCellValue(new DBHandler(getApplicationContext()).getExpHeadName(res.getInt(j)));
                    }else {
                        cell.setCellValue(res.getString(j));
                    }
                }
            }while (res.moveToNext());
        }
        res.close();
        writeLog("exportToExcel_writeFile_exportExpenseReport_called");
        exportCollectionReport(wb,cs);
    }

    private void exportCollectionReport(Workbook wb, CellStyle cs){
        Constant.showLog("exportCollectionReport");
        Sheet sheet = wb.createSheet("Collection Summary Report");

        String arr[] = new String[]{"Cashier","NetCollection",
                "SaleCash","CashBack","NetCash","Cheque","Card","OtherPayment","ExpenseReceipt","ExpensePayment"};

        Row row = sheet.createRow(1);
        for(int i=0;i<arr.length;i++){
            Cell c = row.createCell(i);
            c.setCellValue(arr[i]);
            c.setCellStyle(cs);
        }

        int count = 1;
        Cursor res = new DBHandler(getApplicationContext()).getExpCollectionSumData("","");
        if(res.moveToFirst()){
            do{
                count++;
                Row row1 = sheet.createRow(count);
                for (int j = 0; j < res.getColumnCount(); j++) {
                    Cell cell = row1.createCell(j);
                    cell.setCellValue(res.getString(j));
                }
            }while (res.moveToNext());
        }
        res.close();
        writeLog("exportToExcel_exportCollectionReport_called");
        //exportGrowthReport(wb,cs);
    }

    /*private void exportGrowthReport(Workbook wb, CellStyle cs){
        Constant.showLog("exportGrowthReport");
        Sheet sheet = wb.createSheet("GROWTH REPORT");

        cs.setAlignment(CellStyle.ALIGN_CENTER);
        Row headerRow = sheet.createRow(1);
        Cell hc = headerRow.createCell(1);

        hc.setCellValue(prevMonth6);
        sheet.addMergedRegion(new CellRangeAddress(1,1,1,2));
        hc.setCellStyle(cs);

        hc = headerRow.createCell(3);
        hc.setCellValue(prevMonth5);
        sheet.addMergedRegion(new CellRangeAddress(1,1,3,5));
        hc.setCellStyle(cs);

        hc = headerRow.createCell(6);
        hc.setCellValue(prevMonth4);
        sheet.addMergedRegion(new CellRangeAddress(1,1,6,8));
        hc.setCellStyle(cs);

        hc = headerRow.createCell(9);
        hc.setCellValue(prevMonth3);
        sheet.addMergedRegion(new CellRangeAddress(1,1,9,11));
        hc.setCellStyle(cs);

        hc = headerRow.createCell(12);
        hc.setCellValue(prevMonth2);
        sheet.addMergedRegion(new CellRangeAddress(1,1,12,14));
        hc.setCellStyle(cs);

        hc = headerRow.createCell(15);
        hc.setCellValue(prevMonth1);
        sheet.addMergedRegion(new CellRangeAddress(1,1,15,17));
        hc.setCellStyle(cs);

        hc = headerRow.createCell(18);
        hc.setCellValue(currMonth);
        sheet.addMergedRegion(new CellRangeAddress(1,1,18,20));
        hc.setCellStyle(cs);

        cs.setAlignment(CellStyle.ALIGN_LEFT);

        String arr[] = new String[]{DBHandler.G_Branch,DBHandler.G_Qty7,DBHandler.G_Amt7,
                DBHandler.G_Qty6,DBHandler.G_Amt6,DBHandler.G_Growth6,
                DBHandler.G_Qty5,DBHandler.G_Amt5,DBHandler.G_Growth5,
                DBHandler.G_Qty4,DBHandler.G_Amt4,DBHandler.G_Growth4,
                DBHandler.G_Qty3,DBHandler.G_Amt3,DBHandler.G_Growth3,
                DBHandler.G_Qty2,DBHandler.G_Amt2,DBHandler.G_Growth2,
                DBHandler.G_Qty1,DBHandler.G_Amt1,DBHandler.G_Growth1};

        Row row = sheet.createRow(2);
        for(int i=0;i<arr.length;i++){
            Cell c = row.createCell(i);
            c.setCellValue(arr[i]);
            c.setCellStyle(cs);
        }

        int count = 2;
        Cursor res = new DBHandler(getApplicationContext()).getGrowthReportForExcel();
        if(res.moveToFirst()){
            do{
                count++;
                Row row1 = sheet.createRow(count);
                for (int j = 0; j < res.getColumnCount(); j++) {
                    Cell cell = row1.createCell(j);
                    cell.setCellValue(res.getString(j));
                }
            }while (res.moveToNext());
        }
        res.close();
        writeLog("exportToExcel_exportSubBrandReport_called");
        exportSubBrandReport(wb,cs);
    }

    private void exportSubBrandReport(Workbook wb, CellStyle cs){
        Constant.showLog("exportSubBrandReport");
        Sheet sheet = wb.createSheet("SUBBRAND SALE REPORT");

        cs.setAlignment(CellStyle.ALIGN_CENTER);
        Row headerRow = sheet.createRow(1);
        Cell hc = headerRow.createCell(2);

        hc.setCellValue(prevMonth2);
        sheet.addMergedRegion(new CellRangeAddress(1,1,2,6));
        hc.setCellStyle(cs);

        hc = headerRow.createCell(7);
        hc.setCellValue(prevMonth1);
        sheet.addMergedRegion(new CellRangeAddress(1,1,7,11));
        hc.setCellStyle(cs);

        hc = headerRow.createCell(12);
        hc.setCellValue(currMonth);
        sheet.addMergedRegion(new CellRangeAddress(1,1,12,16));
        hc.setCellStyle(cs);

        cs.setAlignment(CellStyle.ALIGN_LEFT);

        String arr[] = new String[]{DBHandler.SM_Branch,DBHandler.SM_Subbrand,
                DBHandler.SM_OpeningStock3,DBHandler.SM_Inw3,DBHandler.SM_StockIn3,DBHandler.SM_Sale3,DBHandler.SM_ClosingStock3,
                DBHandler.SM_OpeningStock2,DBHandler.SM_Inw2,DBHandler.SM_StockIn2,DBHandler.SM_Sale2,DBHandler.SM_ClosingStock2,
                DBHandler.SM_OpeningStock1,DBHandler.SM_Inw1,DBHandler.SM_StockIn1,DBHandler.SM_Sale1,DBHandler.SM_ClosingStock1};

        Row row = sheet.createRow(2);
        for(int i=0;i<arr.length;i++){
            Cell c = row.createCell(i);
            c.setCellValue(arr[i]);
            c.setCellStyle(cs);
        }

        int count = 2;
        Cursor res = new DBHandler(getApplicationContext()).getSubBrandReportForExcel();
        if(res.moveToFirst()){
            do{
                count++;
                Row row1 = sheet.createRow(count);
                for (int j = 0; j < res.getColumnCount(); j++) {
                    Cell cell = row1.createCell(j);
                    cell.setCellValue(res.getString(j));
                }
            }while (res.moveToNext());
        }
        res.close();
        writeLog("exportToExcel_exportTodaySaleReport_called");
        exportTodaySaleReport(wb,cs);
    }

    private void exportTodaySaleReport(Workbook wb, CellStyle cs){
        Constant.showLog("exportTodaySaleReport");
        Sheet sheet = wb.createSheet("TODAYS SALE REPORT");

        String arr[] = new String[]{DBHandler.TS_Branch,DBHandler.TS_Qty,DBHandler.TS_Amt,DBHandler.TS_Qty1,DBHandler.TS_Amt1,DBHandler.TS_Qty2,DBHandler.TS_Amt2};

        Row row = sheet.createRow(1);
        for(int i=0;i<arr.length;i++){
            Cell c = row.createCell(i);
            c.setCellValue(arr[i]);
            c.setCellStyle(cs);
        }

        int count = 1;
        Cursor res = new DBHandler(getApplicationContext()).getTodaysSaleReportForExcel();
        if(res.moveToFirst()){
            do{
                count++;
                Row row1 = sheet.createRow(count);
                for (int j = 0; j < res.getColumnCount(); j++) {
                    Cell cell = row1.createCell(j);
                    cell.setCellValue(res.getString(j));
                }
            }while (res.moveToNext());
        }
        res.close();
        writeLog("exportToExcel_exportReplenishmentReport_called");
        exportReplenishmentReport(wb,cs);
        //exportStockEdgeReport(wb,cs);
    }

    private void exportReplenishmentReport(Workbook wb, CellStyle cs){
        Constant.showLog("exportReplenishmentReport");
        Sheet sheet = wb.createSheet("REPLENISHMENT REPORT");

        String arr[] = new String[]{DBHandler.RR_Supplier,DBHandler.RR_Department,DBHandler.RR_Section,DBHandler.RR_Item,
                DBHandler.RR_Subbrand,DBHandler.RR_OpnStock,DBHandler.RR_NetPurchase,DBHandler.RR_NetSale,DBHandler.RR_OtherIn,
                DBHandler.RR_OtherOut,DBHandler.RR_GIT,DBHandler.RR_ClStock,DBHandler.RR_ActStock,DBHandler.RR_PurAmt,DBHandler.RR_StockAmt,
                DBHandler.RR_SaleAmt};

        Row row = sheet.createRow(1);
        for(int i=0;i<arr.length;i++){
            Cell c = row.createCell(i);
            c.setCellValue(arr[i]);
            c.setCellStyle(cs);
        }

        int count = 1;
        Cursor res = new DBHandler(getApplicationContext()).getReplenishmentReportForExcel();
        if(res.moveToFirst()){
            do{
                count++;
                Row row1 = sheet.createRow(count);
                for (int j = 0; j < res.getColumnCount(); j++) {
                    Cell cell = row1.createCell(j);
                    cell.setCellValue(res.getString(j));
                }
            }while (res.moveToNext());
        }
        res.close();

        exportStockEdgeReport(wb,cs);
    }

    private void exportStockEdgeReport(Workbook wb, CellStyle cs){
        Constant.showLog("exportStockEdgeReport");
        Sheet sheet = wb.createSheet("STOCK EDGE REPORT");

        String arr[] = new String[]{DBHandler.SA_Cat1,DBHandler.SA_Cat2,DBHandler.SA_Cat3,DBHandler.SA_SubBrand,"0-90","90-180",">180"};

        Row row = sheet.createRow(1);
        for(int i=0;i<arr.length;i++){
            Cell c = row.createCell(i);
            c.setCellValue(arr[i]);
            c.setCellStyle(cs);
        }

        int count = 1;
        Cursor res = new DBHandler(getApplicationContext()).getStockAgeReportForExcel();
        if(res.moveToFirst()){
            do{
                count++;
                Row row1 = sheet.createRow(count);
                for (int j = 0; j < res.getColumnCount(); j++) {
                    Cell cell = row1.createCell(j);
                    cell.setCellValue(res.getString(j));
                }
            }while (res.moveToNext());
        }

        res.close();
    }*/

    private void getAllFiles(){
        File directory = Constant.checkFolder(Constant.folder_name);
        File[] f = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File directory, String name) {
                return name.endsWith(".xls");
            }
        });

        Arrays.sort( f, new Comparator(){
            public int compare(Object o1, Object o2) {

                if (((File)o1).lastModified() > ((File)o2).lastModified()) {
                    return -1;
                } else if (((File)o1).lastModified() < ((File)o2).lastModified()) {
                    return +1;
                } else {
                    return 0;
                }
            }
        });
        file = new String[f.length];
        for(int i = 0;i < file.length;i++){
            file[i] = f[i].getName();
            Constant.showLog("filename"+f[i].getName());
        }
        ArrayAdapter adapter = new ArrayAdapter(this,R.layout.custom_spinner,file);
        lv_list.setAdapter(adapter);
    }

    private void getMonths() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM", Locale.ENGLISH);
        currMonth = sdf.format(c.getTime());
        c.add(Calendar.MONTH, -1);
        prevMonth1= sdf.format(c.getTime());
        c.add(Calendar.MONTH, -1);
        prevMonth2 = sdf.format(c.getTime());
        c.add(Calendar.MONTH, -1);
        prevMonth3 = sdf.format(c.getTime());
        c.add(Calendar.MONTH, -1);
        prevMonth4 = sdf.format(c.getTime());
        c.add(Calendar.MONTH, -1);
        prevMonth5 = sdf.format(c.getTime());
        c.add(Calendar.MONTH, -1);
        prevMonth6 = sdf.format(c.getTime());
        Constant.showLog(currMonth+"-"+prevMonth1+"-"+prevMonth2+"-"+prevMonth3+"-"+
                prevMonth4+"-"+prevMonth5+"-"+prevMonth6);
    }

    private void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ExportOthersActivity.this);
        builder.setCancelable(false);
        if (a == 0) {
            builder.setMessage("Do You Want To Exit App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Constant(ExportOthersActivity.this).doFinish();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else if (a == 1) {
            builder.setMessage("Do You Want To Export Data?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    checkpermmission();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getAllFiles();
                    dialog.dismiss();
                }
            });
        }
        builder.create().show();
    }

    private void writeLog(String _data) {
        new WriteLog().writeLog(getApplicationContext(), "ExportExcelActivity_" + _data);
    }
}
