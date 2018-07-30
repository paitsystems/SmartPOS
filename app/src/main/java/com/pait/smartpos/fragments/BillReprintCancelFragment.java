package com.pait.smartpos.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.hoin.btsdk.BluetoothService;
import com.pait.smartpos.CashMemoActivity;
import com.pait.smartpos.R;
import com.pait.smartpos.VerificationActivity;
import com.pait.smartpos.adpaters.BillReprintCancelAdapter;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.constant.PrinterCommands;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.model.AddToCartClass;
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

public class BillReprintCancelFragment extends Fragment {

    private List<BillReprintCancelClass> list;
    private ListView listView;
    private DBHandler db;
    private BillReprintCancelClass bill;
    private Toast toast;
    private BluetoothService mService;
    private BluetoothDevice con_dev = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.activity_bill_reprint_cancel, container, false);
        init(view1);
        setList();

        mService = new BluetoothService(getContext(), mHandler1);
        connectBT();

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            bill = (BillReprintCancelClass) listView.getItemAtPosition(i);
            showDia(1);
        });

        return view1;
    }


    @SuppressLint("ShowToast")
    private void init(View view){
        toast = Toast.makeText(getContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        listView = view.findViewById(R.id.listView);
        list = new ArrayList<>();
        db = new DBHandler(getContext());
    }

    private void setList(){
        list.clear();
        listView.setAdapter(null);
        list = db.getBillMasterData("","");
        listView.setAdapter(new BillReprintCancelAdapter(getContext(),list));
    }

    private void cancelBill(){
        if(bill!=null){
            if(!bill.getStatus().equals("C")) {
                db.cancelBill(bill, new Constant(getContext()).getDate());
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
            pd = new ProgressDialog(getContext());
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

                UserProfileClass user = new Constant(getContext()).getPref();

                mService.sendMessage(user.getFirmName(), "GBK");

                nameFontformat[2] = arrayOfByte1[2];
                mService.write(nameFontformat);

                mService.sendMessage(user.getCity(), "GBK");
                mService.sendMessage(user.getMobileNo(), "GBK");
                mService.sendMessage("TAX INVOICE", "GBK");

                byte[] left = {27, 97, 0};
                mService.write(PrinterCommands.ESC_ALIGN_LEFT);

                Date date1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(bill.getBillDate());
                String date = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).format(date1);
                String time = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                mService.sendMessage("BillNo : " + bill.getBillNo(), "GBK");
                String space_str13 = "             ";
                mService.sendMessage(date + space_str13 + time, "GBK");

                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x8 | arrayOfByte1[2]));
                mService.write(nameFontformat);
                String line_str = "--------------------------------";
                mService.sendMessage(line_str, "GBK");
                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                mService.write(nameFontformat);

                mService.sendMessage("Item           " + "Qty" + "  Rate" + "  Amnt", "GBK");
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

                StringBuilder data = new StringBuilder();
                for (int i = 0; i < cartList.size(); i++) {
                    BillDetailClass cart = cartList.get(i);
                    StringBuilder item = new StringBuilder(cart.getFatherSKU());
                    String item1 = cart.getFatherSKU();
                    int flag = 0;
                    if (item.length() >= 14) {
                        item = new StringBuilder(item.substring(0, 13));
                        item.append(" ");
                        flag = 1;
                    } else {
                        int size = 13 - item.length();
                        for (int j = 0; j < size; j++) {
                            item.append(" ");
                        }
                        item.append(" ");
                    }

                    String qty = cart.getQty();
                    totQty = Integer.parseInt(qty);
                    if (qty.length() == 1) {
                        qty = "  " + qty;
                    } else if (qty.length() == 2) {
                        qty = " " + qty;
                    }

                    String rate = cart.getRate();
                    if (rate.length() == 1) {
                        rate = "      " + rate;
                    }else if (rate.length() == 2) {
                        rate = "     " + rate;
                    }else if (rate.length() == 3) {
                        rate = "     " + rate;
                    }else if (rate.length() == 4) {
                        rate = "   " + rate;
                    } else if (rate.length() == 5) {
                        rate = "  " + rate;
                    }else if (rate.length() == 6) {
                        rate = " " + rate;
                    }

                    String amnt = cart.getTotal();
                    if (amnt.length() == 1) {
                        amnt = "      " + amnt;
                    }else if (amnt.length() == 2) {
                        amnt = "     " + amnt;
                    }else if (amnt.length() == 3) {
                        amnt = "    " + amnt;
                    }else if (amnt.length() == 4) {
                        amnt = "   " + amnt;
                    }else if (amnt.length() == 5) {
                        amnt = "  " + amnt;
                    }else if (amnt.length() == 6) {
                        amnt = " " + amnt;
                    }

                    cgstPerStr = cart.getCGSTPER();
                    sgstPerStr = cart.getSGSTPER();

                    if (flag != 1) {
                        data.append(item).append(qty).append(rate).append(amnt).append("\n");
                        textData.append("").append(item).append(qty).append(rate).append(amnt).append("\n");
                    } else {
                        String q = item1.substring(13, item1.length());
                        if (q.length() < 32) {
                            data.append(item).append(qty).append(rate).append(amnt).append("\n").append(q).append("\n");
                            textData.append("").append(item).append(qty).append(rate).append(amnt).append("\n").append(q).append("\n");
                        }
                    }
                    count++;
                }

                String _count = String.valueOf(count);

                mService.sendMessage(data.toString(), "GBK");
                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x8 | arrayOfByte1[2]));
                mService.write(nameFontformat);
                mService.sendMessage(line_str, "GBK");
                textData.delete(0, textData.length());

                String  totalamt = String.valueOf(totAmnt);
                String[] totArr = totalamt.split("\\.");
                if (totArr.length > 1) {
                    totalamt = totArr[0];
                }
                //textData.append("Total              ").append("  "+count).append("      ").append(totalamt).append("\n");
                if (_count.length() == 1 && totalamt.length() == 2) {
                    textData.append("Total          ").append("  ").append(totQty).append("        ").append(roundTwoDecimals(String.valueOf(totAmnt))).append("\n");
                } else if (_count.length() == 1 && totalamt.length() == 3) {
                    textData.append("Total          ").append("  ").append(totQty).append("       ").append(roundTwoDecimals(String.valueOf(totAmnt))).append("\n");
                } else if (_count.length() == 1 && totalamt.length() == 4) {
                    textData.append("Total          ").append(totQty).append("      ").append(roundTwoDecimals(String.valueOf(totAmnt))).append("\n");
                }
                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                mService.write(nameFontformat);
                mService.sendMessage(textData.toString(), "GBK");

                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                mService.write(nameFontformat);
                mService.sendMessage("CGST " + cgstPerStr + " % : " + roundTwoDecimals(bill.getCGSTAMNT()), "GBK");
                mService.sendMessage("SGST " + sgstPerStr + " % : " + roundTwoDecimals(bill.getSGSTAMNT()), "GBK");

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
                mService.sendMessage("    www.paitsystems.com", "GBK");

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    private void connectBT(){
        try {
            if(mService!=null) {
                if (mService.isBTopen()) {
                    UserProfileClass user = new Constant(getContext()).getPref();
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
