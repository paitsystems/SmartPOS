package com.pait.smartpos.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.pait.smartpos.R;
import com.pait.smartpos.VerificationActivity;
import com.pait.smartpos.adpaters.BillReprintCancelAdapter;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.constant.PrinterCommands;
import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.model.BillDetailClassR;
import com.pait.smartpos.model.UserProfileClass;
import com.pait.smartpos.parse.BillReprintCancelClass;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BillReprintCancelFragment extends Fragment {

    private List<BillReprintCancelClass> list;
    private ListView listView;
    private DBHandlerR db;
    private BillReprintCancelClass bill;
    private Toast toast;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.activity_bill_reprint_cancel, container, false);
        init(view1);
        setList();

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
        db = new DBHandlerR(getContext());
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
        List<BillDetailClassR> detList = db.getBillDetailData(bill);
        new CashMemoPrint(detList).execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class CashMemoPrint extends AsyncTask<Void, Void, String> {

        private ProgressDialog pd;
        private List<BillDetailClassR> detList;

        private CashMemoPrint(List<BillDetailClassR> _detList){
            this.detList = _detList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.setMessage("Please Wait...");
            pd.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String str;
            StringBuilder textData = new StringBuilder();
            double totalRateAmnt = 0;

            try {
                byte[] arrayOfByte1 = {27, 33, 0};
                byte[] format = {27, 33, 0};

                byte[] center = {27, 97, 1};
                VerificationActivity.mService.write(center);
                byte nameFontformat[] = format;
                nameFontformat[2] = ((byte) (0x20 | arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);

                UserProfileClass user = new Constant(getContext()).getPref();

                VerificationActivity.mService.sendMessage(user.getFirmName(), "GBK");

                nameFontformat[2] = arrayOfByte1[2];
                VerificationActivity.mService.write(nameFontformat);

                VerificationActivity.mService.sendMessage(user.getCity(), "GBK");
                VerificationActivity.mService.sendMessage(user.getMobileNo(), "GBK");
                VerificationActivity.mService.sendMessage("TAX INVOICE", "GBK");

                byte[] left = {27, 97, 0};
                VerificationActivity.mService.write(left);

                VerificationActivity.mService.sendMessage("BillNo : " + bill.getBillNo(), "GBK");
                String d = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).format(new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH).parse(bill.getBillDate()));
                VerificationActivity.mService.sendMessage( d +"       "+ bill.getBillTime(), "GBK");

                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x8 | arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                String line_str = "--------------------------------";
                VerificationActivity.mService.sendMessage(line_str, "GBK");
                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                VerificationActivity.mService.write(nameFontformat);

                VerificationActivity.mService.sendMessage("Item           " + "Qty" + "  Rate" + "  Amnt", "GBK");
                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x8 | arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage(line_str, "GBK");
                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                VerificationActivity.mService.write(nameFontformat);

                int count = 0;

                StringBuilder data = new StringBuilder();
                String cgstPer = "0";
                String sgstPer = "0";
                for (int i = 0; i < detList.size(); i++) {
                    BillDetailClassR det = detList.get(i);
                    StringBuilder item = new StringBuilder(det.getProd());
                    String item1 = det.getProd();
                    int flag = 0;
                    cgstPer = String.valueOf(det.getCGSTPER());
                    sgstPer = String.valueOf(det.getSGSTPER());

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

                    String qty = String.valueOf(det.getQty());
                    if (qty.length() == 1) {
                        qty = "  " + qty;
                    } else if (qty.length() == 2) {
                        qty = " " + qty;
                    }

                    String rate = det.getRateStr();
                    if (rate.length() == 4) {
                        rate = "   " + rate;
                    } else if (rate.length() == 5) {
                        rate = "  " + rate;
                    }else if (rate.length() == 6) {
                        rate = " " + rate;
                    }

                    String amnt = det.getTotalStr();
                    totalRateAmnt = totalRateAmnt + Double.parseDouble(amnt);

                    if (amnt.length() == 4) {
                        amnt = "   " + amnt;
                    } else if (amnt.length() == 5) {
                        amnt = "  " + amnt;
                    }else if (amnt.length() == 6) {
                        amnt = " " + amnt;
                    }

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
                VerificationActivity.mService.sendMessage(data.toString(), "GBK");
                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x8 | arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage(line_str, "GBK");
                textData.delete(0, textData.length());
                textData.append("Total          ").append(count).append("      ").append(roundTwoDecimals(String.valueOf(totalRateAmnt))).append("\n");

                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage(textData.toString(), "GBK");

                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage("CGST " + cgstPer + " % : " + bill.getCGSTAMNT(), "GBK");
                VerificationActivity.mService.sendMessage("SGST " + sgstPer + " % : " + bill.getSGSTAMNT(), "GBK");

                nameFontformat = format;
                nameFontformat[2] = arrayOfByte1[2];
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage(line_str, "GBK");

                nameFontformat = format;
                nameFontformat[2] = ((byte) (0x16 | arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage("NET AMNT              " + bill.getNetAmt(), "GBK");

                byte[] left2 = {27, 97, 0};
                VerificationActivity.mService.write(left2);
                nameFontformat[2] = arrayOfByte1[2];
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage("   www.paitsystems.com", "GBK");

                VerificationActivity.mService.write(PrinterCommands.ESC_ENTER);
                String space_str = "                        ";
                VerificationActivity.mService.sendMessage(space_str, "GBK");

                Log.d("Log", textData.toString());
            } catch (Exception e) {
                e.printStackTrace();
                str = "Printer 3 May Not Be Connected ";
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


}
