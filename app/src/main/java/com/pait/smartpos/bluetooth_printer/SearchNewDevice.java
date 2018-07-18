package com.pait.smartpos.bluetooth_printer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hoin.btsdk.BluetoothService;
import com.pait.smartpos.R;
import com.pait.smartpos.permission.GetPermission;

import java.util.HashMap;
import java.util.Set;

public class SearchNewDevice extends AppCompatActivity {

    private Button btn_scan_device;
    private BluetoothService mService;
    private ArrayAdapter<String> list_paired_device, list_new_device;
    private ListView listView_paired, listView_newdevices;
    private ProgressDialog pd;
    private static final int REQUEST_ENABLE_BT = 2;
    private HashMap<String, String> new_device_hash_map;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_new_device);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        init();
        checkPermissionMannual();
    }

    private void init(){
        pd = new ProgressDialog(SearchNewDevice.this);
        pd.setMessage("Searching New Device");
        pd.setCancelable(false);
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);

        mService = new BluetoothService(this, handler);
        list_paired_device = new ArrayAdapter<>(this, R.layout.list_item_bluetooth_paired_device);
        list_new_device = new ArrayAdapter<>(this, R.layout.list_item_bluetooth_new_device);
        new_device_hash_map = new HashMap<>();

        listView_newdevices = findViewById(R.id.listView_newdevices);
        listView_paired = findViewById(R.id.listView_paired);

        if (!mService.isAvailable()) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(receiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(receiver, filter);


        Set<BluetoothDevice> set = mService.getPairedDev();
        if (!set.isEmpty()) {
            for (BluetoothDevice device : set) {
                list_paired_device.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            list_paired_device.add("No Paired Device Available");
        }
        listView_paired.setAdapter(list_paired_device);
        //listView_paired.setOnItemClickListener(mDeviceClickListener);
        //listView_newdevices.setOnItemClickListener(mDeviceClickListener);

        btn_scan_device = findViewById(R.id.btn_scan_device);
        btn_scan_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discoverNewDevice();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mService.isBTopen()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            mService.cancelDiscovery();
        }
        mService = null;
        this.unregisterReceiver(receiver);
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

    private void doFinish(){
        finish();
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }

    private void discoverNewDevice() {
        pd.show();
        list_new_device.clear();
        list_paired_device.clear();
        if (!mService.isBTopen()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {

            list_new_device.add("No New Device Available");
            listView_newdevices.setAdapter(list_new_device);
            new_device_hash_map.clear();
            if (mService.isDiscovering()) {
                mService.cancelDiscovery();
            }
            mService.startDiscovery();
        }
    }

    private void checkPermissionMannual() {
        GetPermission permission = new GetPermission();
        if (!permission.checkBluetoothPermission(getApplicationContext())) {
            permission.requestBluetoothPermission(getApplicationContext(), this);
        }
        if (!permission.checkBluetoothAdminPermission(getApplicationContext())) {
            permission.requestBluetoothAdminPermission(getApplicationContext(), this);
        }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {   //����б�������豸
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            mService.cancelDiscovery();
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            Intent intent = new Intent();
            intent.putExtra("add", address);
            Log.d("Log", address);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pd.dismiss();
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (new_device_hash_map.isEmpty()) {
                        new_device_hash_map.put(device.getAddress(), device.getName());
                        list_new_device.add(device.getName() + "\n" + device.getAddress());
                    } else {
                        if (!new_device_hash_map.containsKey(device.getAddress())) {
                            list_new_device.add(device.getName() + "\n" + device.getAddress());
                        }
                    }
                }
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                if (list_new_device.getCount() == 0) {
                    list_new_device.add("No Device Found");
                }
            }
        }
    };
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                checkPermissionMannual();
                break;
            case 2:
                discoverNewDevice();
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
