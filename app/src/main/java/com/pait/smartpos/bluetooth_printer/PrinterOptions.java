package com.pait.smartpos.bluetooth_printer;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pait.smartpos.VerificationActivity;
import com.hoin.btsdk.BluetoothService;
import com.pait.smartpos.constant.Utils;
import com.pait.smartpos.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PrinterOptions extends AppCompatActivity {

    BluetoothDevice con_dev = null;
    BluetoothService mService;
    Toast toast;
    String line_str = "--------------------------------";
    String space_str25 = "                         ";
    String space_str06 = "      ";
    String space_str13 = "             ";
    String space_str14 = "              ";
    String space_str15 = "               ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mService = new BluetoothService(this,mHandler);
        toast = Toast.makeText(getApplicationContext(),"", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /*Set<BluetoothDevice> set = mService.getPairedDev();
        if (!set.isEmpty()) {
            for (BluetoothDevice device : set) {
                con_dev = mService.getDevByMac(device.getAddress());
                Log.d("Log", "getDevByMac :- "+ con_dev);
                mService.connect(con_dev);
            }
        }*/

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serverIntent = new Intent(getApplicationContext(),SearchNewDevice.class);      //��������һ����Ļ
                startActivityForResult(serverIntent,1);
                overridePendingTransition(R.anim.enter,R.anim.exit);
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                testPrint();
                /*byte[] cmd = new byte[3];
                cmd[0] = 0x1b;
                cmd[1] = 0x21;
                cmd[2] |= 0x10;
                mService.write(cmd);
                mService.sendMessage("Congratulations!", "GBK");
                cmd[2] &= 0xEF;
                mService.write(cmd);
                mService.sendMessage("Printed Succesfully\n", "GBK");

                cmd[0] = 0x10;
                cmd[1] = 0x04;
                cmd[2] = 0x04;
                mService.write(cmd);
                mService.sendMessage("Congratulations1!\n", "GBK");
                cmd[2] &= 0xAB;
                mService.write(cmd);
                mService.sendMessage("Printed Succesfully1", "GBK");*/

                //byte[] arrayOfByte1 = { 27, 33, 0 };
                //byte[] format = { 27, 33,0 };
                /*mService.write(format);
                mService.sendMessage("FORMAT 0","GBK");*/

                /*format[2] = ((byte)(0x15|arrayOfByte1[2]));
                String s1 = "Format0 0x15:-{"+format[0]+","+format[1]+","+format[2]+"}";
                Log.d("Log",s1);
                mService.write(format);
                mService.sendMessage("FORMAT 0 "+ s1,"GBK");

                byte[] format1 = { 27, 33, 0 };
                format1[2] = ((byte)(5|arrayOfByte1[2]));
                String s2 = "Format1 5:-{"+format1[0]+","+format1[1]+","+format1[2]+"}";
                Log.d("Log",s2);
                mService.write(format1);
                mService.sendMessage("FORMAT 1 "+s2,"GBK");

                byte[] format2 = { 27, 0, 8 };
                format2[1] = ((byte)(0x21|arrayOfByte1[1]));
                String s3 = "Format2 0x21:-{"+format2[0]+","+format2[1]+","+format2[2]+"}";
                Log.d("Log",s3);
                mService.write(format2);
                mService.sendMessage("FORMAT 2 "+s3,"GBK");

                byte[] format3 = { 27, 33, 0 };
                format3[2] = ((byte)(0xB|arrayOfByte1[2]));
                String s4 = "Format3 0xB:-{"+format3[0]+","+format3[1]+","+format3[2]+"}";
                Log.d("Log",s4);
                mService.write(format3);
                mService.sendMessage("FORMAT 3 "+s4,"GBK");*/

                /*format[2] = ((byte)(0x15 | arrayOfByte1[2]));
                mService.write(format);
                mService.sendMessage("LNB INFOTECH","GBK");*/

                // Bold
                /*format[2] = ((byte)(0x8 | arrayOfByte1[2]));
                mService.write(format);
                mService.sendMessage(line_str, "GBK");*/

                // Underline
                /*format[2] = ((byte)(0x80 | arrayOfByte1[2]));
                mService.write(format);
                mService.sendMessage("UNDERLINE", "GBK");

                // Height
                format[2] = ((byte)(0x10 | arrayOfByte1[2]));
                mService.write(format);
                mService.sendMessage("HEIGHT", "GBK");

                // Width
                format[2] = ((byte) (0x20 | arrayOfByte1[2]));
                mService.write(format);
                mService.sendMessage("WIDTH", "GBK");


                // Small
                format[2] = ((byte)(0x1 | arrayOfByte1[2]));
                mService.write(format);
                mService.sendMessage("SMALL", "GBK");

                //EXTRA BLACK
                format[2] = ((byte)(0x6B | arrayOfByte1[2]));
                mService.write(format);
                mService.sendMessage("EXTRA BLACK", "GBK");*/

                //left
                //byte[] left =  { 0x1b, 0x21, 0x8 };

                //printPhoto();

                //center
                //byte[] center =  { 0x1b, 'a', 0x01 };
                /*byte[] center =  { 27, 97, 1};
                mService.write(center);
                //mService.sendMessage("CENTER","GBK");
                mService.sendMessage("LNB INFOTECH\n220,Nupur Apartment, Sadashiv Peth,Rambaug Colony\nPune-27 Phon no: 020-24339954", "GBK");

                byte[] left =  { 27, 97, 0 };
                mService.write(left);
                //mService.sendMessage("LEFT", "GBK");
                String date = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());

                //mService.sendMessage(date, "GBK");

                String line_str = "14/May/2017-------------14:05:10";*/

                //RIGHT
                //byte[] right =  {27,97,2};
                //mService.write(right);
                //mService.sendMessage("RIGHT", "GBK");
                //String time = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                //mService.sendMessage(time, "GBK");

                /*date = date+space_str13+time;
                mService.sendMessage(date, "GBK");

                format[2] = ((byte)(0x8 | arrayOfByte1[2]));
                mService.write(format);
                mService.sendMessage(line_str, "GBK");*/

            }
        });
    }

    void testPrint(){
        byte[] arrayOfByte1 = { 27, 33, 0 };
        byte[] format = { 27, 33,0 };

        byte[] center =  { 27, 97, 1};
        mService.write(center);
        byte nameFontformat[] = format;
        nameFontformat[2] = ((byte)(0x20|arrayOfByte1[2]));
        mService.write(nameFontformat);
        mService.sendMessage("LNB INFOTECH", "GBK");
        nameFontformat = format;
        nameFontformat[2] = ((byte)(0x0|arrayOfByte1[2]));
        mService.write(nameFontformat);
        mService.sendMessage("220,Nupur Apartment, Sadashiv Peth,Rambaug Colony Pune-27 Phone no: 020-24339954","GBK");

        byte[] left =  { 27, 97, 0 };
        mService.write(left);
        String date = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String time = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String datetime = date+space_str13+time;
        mService.sendMessage(datetime, "GBK");
        String billno = "Bill No : 0123456789";
        mService.sendMessage(billno, "GBK");
        String customername = "Cust Name : "+"Anup Patil";
        mService.sendMessage(customername, "GBK");
        String customerNo = "Cust No : 8237509835";
        mService.sendMessage(customerNo, "GBK");
        //RIGHT
        //byte[] right =  {27,97,2};
        format[2] = ((byte)(0x0 | arrayOfByte1[2]));
        mService.write(format);
        mService.sendMessage(line_str, "GBK");

        String str1 = Environment.getExternalStorageDirectory()+ File.separator+"Saptapadi/Text.txt";
        File file = new File(str1);

        String itemqtyrate = "Item"+space_str15+"Qty"+space_str06+"Rate";
        mService.sendMessage(itemqtyrate, "GBK");
        mService.sendMessage(line_str, "GBK");

        String item[]=new String[]{"Barcode Printer","Mi Note 4","Apple iPhone 6s"};
        for(String str:item){
            String itemspace="";
            for(int i=0;i<(19-str.length());i++){
                itemspace+=" ";
            }
            mService.sendMessage(str+itemspace+"001"+space_str06+"3333","GBK");
        }
        //String line_str = "--------------------------------";
        mService.sendMessage(line_str, "GBK");

        nameFontformat = format;
        nameFontformat[2] = ((byte)(0x8|arrayOfByte1[2]));
        mService.write(nameFontformat);
        String total = "Total"+space_str14+"003"+space_str06+"9999";
        mService.sendMessage(total, "GBK");

        nameFontformat = format;
        nameFontformat[2] = ((byte)(0x1|nameFontformat[2]));
        mService.write(nameFontformat);

        mService.write(center);
        String softwareBy = "Software By LNB Infotech 9370716834";
        mService.sendMessage(softwareBy, "GBK");
        //mService.write(center);
        //mService.sendMessage("9370716834", "GBK");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case  1:
                if (resultCode == Activity.RESULT_OK) {
                    VerificationActivity.mService = null;
                    String address = data.getExtras().getString("add");
                    con_dev = mService.getDevByMac(address);
                    VerificationActivity.mService.connect(con_dev);
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        doFinish();
    }

    void doFinish(){
        toast.cancel();
        finish();
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            toast.setText("Connect successfully");
                            toast.show();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            toast.setText("Connecting...");
                            toast.show();
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            Log.d("Log","None");
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:
                    toast.setText("Device connection was lost");
                    toast.show();
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:
                    toast.setText("Unable to connect device");
                    toast.show();
                    break;
            }
        }
    };

    public void printPhoto() {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.ws128);
            if(bmp!=null){
                byte[] command = Utils.decodeBitmap(bmp);
                mService.write(command);
            }else{
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }
}
