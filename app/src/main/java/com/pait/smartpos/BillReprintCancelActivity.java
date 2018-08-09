package com.pait.smartpos;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.hoin.btsdk.BluetoothService;
import com.pait.smartpos.adpaters.BillReprintCancelAdapter;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.constant.PrinterCommands;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.fragments.BillReprintCancelFragment;
import com.pait.smartpos.model.BillDetailClass;
import com.pait.smartpos.model.BillDetailClassR;
import com.pait.smartpos.model.UserProfileClass;
import com.pait.smartpos.parse.BillReprintCancelClass;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BillReprintCancelActivity extends AppCompatActivity {

    private List<BillReprintCancelClass> list;
    private ListView listView;
    private DBHandler db;
    private BillReprintCancelClass bill;
    private Toast toast;
    private BluetoothService mService;
    private BluetoothDevice con_dev = null;
    private String compName = "PA", compAddress="PUNE", compPhone="02024339957",
            compInit="PA", compGSTNo = "27ABCD1234EFGH2", custName = "CashSale-0";
    private int gstApplicable = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_reprint_cancel);
        init();
        setList();

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            bill = (BillReprintCancelClass) listView.getItemAtPosition(i);
            custName = db.getCustName(bill.getCustId());
            showDia(1);
        });

        mService = new BluetoothService(getApplicationContext(), mHandler1);
        connectBT();

        Cursor res = db.getCompanyDetail();
        if (res.moveToFirst()) {
            do {
                compName = res.getString(res.getColumnIndex(DBHandler.CPM_CompanyName));
                compAddress = res.getString(res.getColumnIndex(DBHandler.CPM_Address));
                compPhone = res.getString(res.getColumnIndex(DBHandler.CPM_Phone));
                compInit = res.getString(res.getColumnIndex(DBHandler.CPM_Initials));
                compGSTNo = res.getString(res.getColumnIndex(DBHandler.CPM_GSTNo));
            } while (res.moveToNext());
        }
        res.close();

        if(compGSTNo.length()==15) {
            gstApplicable = 1;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            mService.stop();
        }
    }

    @SuppressLint("ShowToast")
    private void init(){
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        listView = findViewById(R.id.listView);
        list = new ArrayList<>();
        db = new DBHandler(getApplicationContext());

    }

    private void setList(){
        list.clear();
        listView.setAdapter(null);
        list = db.getBillMasterData("","");
        listView.setAdapter(new BillReprintCancelAdapter(getApplicationContext(),list));
    }

    private void cancelBill(){
        if(bill!=null){
            if(!bill.getStatus().equals("C")) {
                db.cancelBill(bill, new Constant(getApplicationContext()).getDate());
                toast.setText("Bill Cancel Successfully");
                toast.show();
                setList();
            }else{
                toast.setText("Bill Already Cancelled");
                toast.show();
            }
        }
    }

    private void reprintBill(){
        List<BillDetailClass> detList = db.getBillDetailData(bill);
        new CashMemoPrint(detList).execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class CashMemoPrint extends AsyncTask<Void, Void, String> {

        private ProgressDialog pd;
        private List<BillDetailClass> cartList;

        public CashMemoPrint(List<BillDetailClass> _castList){
            this.cartList = _castList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(BillReprintCancelActivity.this);
            pd.setCancelable(false);
            pd.setMessage("Please Wait...");
            pd.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String str;
            StringBuilder textData = new StringBuilder();
            try {
                byte[] arrayOfByte1 = {27, 33, 0};
                byte[] format = {27, 33, 0};

                byte[] center = {27, 97, 1};
                mService.write(PrinterCommands.ESC_ALIGN_CENTER);
                byte nameFontformat[] = format;
                nameFontformat[2] = ((byte) (0x20 | arrayOfByte1[2]));
                mService.write(nameFontformat);

                mService.sendMessage(compName.toUpperCase(), "GBK");

                nameFontformat[2] = arrayOfByte1[2];
                mService.write(nameFontformat);

                mService.sendMessage(compAddress, "GBK");
                mService.sendMessage(compPhone, "GBK");
                if(gstApplicable == 1) {
                    mService.sendMessage("GSTIN : " + compGSTNo, "GBK");
                }
                mService.sendMessage("TAX INVOICE", "GBK");

                byte[] left = {27, 97, 0};
                mService.write(PrinterCommands.ESC_ALIGN_LEFT);

                Date date1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(bill.getBillDate());
                String date = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).format(date1);
                String time = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                mService.sendMessage("BillNo : " + bill.getBillNo(), "GBK");
                String space_str13 = "             ";
                mService.sendMessage(date + space_str13 + time, "GBK");
                String[] arr = custName.split("-");
                mService.sendMessage("Cust Name : " + arr[0], "GBK");
                mService.sendMessage("Mob No    : " + arr[1], "GBK");

                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x8 | arrayOfByte1[2]));
                mService.write(nameFontformat);
                String line_str = "--------------------------------";
                mService.sendMessage(line_str, "GBK");
                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                mService.write(nameFontformat);

                String _heading = String.format("%1$-10s %2$3s %3$8s %4$8s","Item", "Qty", "Rate", "Amnt");
                Constant.showLog(_heading);
                mService.sendMessage(_heading,"GBK");
                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x8 | arrayOfByte1[2]));
                mService.write(nameFontformat);
                mService.sendMessage(line_str, "GBK");
                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                mService.write(nameFontformat);

                int count = 0, totQty = 0;
                float totAmnt = 0;
                String cgstPerStr = "", sgstPerStr = "";

                for (int i = 0; i < cartList.size(); i++) {
                    String _itemData;
                    BillDetailClass cart = cartList.get(i);
                    StringBuilder item = new StringBuilder(cart.getFatherSKU());
                    String item1 = cart.getFatherSKU();
                    int flag = 0;
                    String qty = cart.getQty();
                    totQty = Integer.parseInt(qty);
                    String amnt = cart.getTotal();
                    totAmnt = totAmnt + Float.parseFloat(amnt);
                    if (item.length() >= 10) {
                        item = new StringBuilder(item.substring(0, 9));
                        item.append(" ");
                        flag = 1;
                    } else {
                        int size = 9 - item.length();
                        for (int j = 0; j < size; j++) {
                            item.append(" ");
                        }
                        item.append(" ");
                    }
                    if (flag != 1) {
                        _itemData = String.format("%1$-10s %2$3s %3$8s %4$8s",item, cart.getQty(),
                                cart.getRate(),cart.getTotal());
                        mService.sendMessage(_itemData,"GBK");
                    } else {
                        _itemData = String.format("%1$-10s %2$3s %3$8s %4$8s",item, cart.getQty(),
                                cart.getRate(),cart.getTotal());
                        mService.sendMessage(_itemData,"GBK");
                        String q = item1.substring(9, item1.length());
                        _itemData = String.format("%1$-10s %2$1s %3$1s %4$1s",q,"","","");
                        mService.sendMessage(_itemData,"GBK");
                    }
                    cgstPerStr = cart.getCGSTPER();
                    sgstPerStr = cart.getSGSTPER();
                    count++;
                }

                String _count = String.valueOf(count);

                /*mService.sendMessage(data.toString(), "GBK");
                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x8 | arrayOfByte1[2]));
                mService.write(nameFontformat);
                textData.delete(0, textData.length());*/
                mService.sendMessage(line_str, "GBK");
                String  totalamt = roundDecimals(bill.getNetAmt());
                String _totalData = String.format("%1$-10s %2$3s %3$8s %4$8s","Total", _count,"",roundTwoDecimals(String.valueOf(totAmnt)));
                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                mService.write(nameFontformat);
                mService.sendMessage(_totalData, "GBK");

                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                mService.write(nameFontformat);
                if(gstApplicable == 1) {
                    mService.sendMessage("CGST " + cgstPerStr + " % : " + roundTwoDecimals(bill.getCGSTAMNT()), "GBK");
                    mService.sendMessage("SGST " + sgstPerStr + " % : " + roundTwoDecimals(bill.getSGSTAMNT()), "GBK");
                }

                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x8 | arrayOfByte1[2]));
                mService.write(nameFontformat);
                mService.sendMessage(line_str, "GBK");

                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x16 | arrayOfByte1[2]));
                mService.write(nameFontformat);
                mService.sendMessage("NET AMNT              " + totalamt, "GBK");


                byte[] left2 = {27, 97, 0};
                mService.write(left2);
                nameFontformat[2] = arrayOfByte1[2];
                mService.write(nameFontformat);
                mService.sendMessage("Contact No : 020 24339954", "GBK");

                mService.write(PrinterCommands.ESC_ENTER);
                String space_str = "                        ";
                mService.sendMessage(space_str, "GBK");

                Log.d("Log", textData.toString());
            } catch (Exception e) {
                e.printStackTrace();
                str = "Printer May Not Be Connected ";
                return str;
            }
            return "Order Received By Kitchen 3";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("Print3", result);
            pd.dismiss();
        }
    }

    private void showDia(int a){
        AlertDialog.Builder builder = new AlertDialog.Builder(BillReprintCancelActivity.this);
        builder.setCancelable(false);
        if(a==1) {
            builder.setTitle("Bill Options");
            builder.setMessage("What Do You Want To Do?");
            builder.setPositiveButton("Bill Reprint", (dialog, which) -> {
                dialog.dismiss();
                reprintBill();
            });
            builder.setNegativeButton("Bill Cancel", (dialog, which) -> {
                dialog.dismiss();
                cancelBill();
            });
            builder.setNeutralButton("Close", (dialog, which) -> dialog.dismiss());
        }
        builder.create().show();
    }

    private String roundTwoDecimals(String d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return twoDForm.format(Double.parseDouble(d));
    }

    private String roundTwoDecimals(float d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return twoDForm.format(Double.parseDouble(String.valueOf(d)));
    }

    private String roundDecimals(String d) {
        DecimalFormat twoDForm = new DecimalFormat("#");
        return twoDForm.format(Double.parseDouble(d));
    }

    private String roundDecimals(float d) {
        DecimalFormat twoDForm = new DecimalFormat("#");
        return twoDForm.format(Double.parseDouble(String.valueOf(d)));
    }

    private void connectBT(){
        try {
            if(mService!=null) {
                if (mService.isBTopen()) {
                    UserProfileClass user = new Constant(getApplicationContext()).getPref();
                    if (user.getMacAddress() != null) {
                        Constant.showLog(user.getMacAddress());
                        con_dev = mService.getDevByMac(user.getMacAddress());
                        mService.connect(con_dev);
                    } else {
                        toast.setText("Set Default Printer First");
                        toast.show();
                    }
                }else{
                    toast.setText("Bluetooth Is Off");
                    toast.show();
                }
            }else{
                toast.setText("Something Went Wrong");
                toast.show();
            }
        }catch (Exception e){
            e.printStackTrace();
            toast.setText("Set Default Printer First");
            toast.show();
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            toast.setText("Bluetooth Printer Connected");
                            toast.show();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            break;
                        case BluetoothService.STATE_LISTEN:
                            break;
                        case BluetoothService.STATE_NONE:
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:
                    toast.setText("Device connection was lost");
                    toast.show();
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:
                    toast.setText("Unable to Connect Bluetooth Printer");
                    toast.show();
                    break;
            }
        }
    };
}
