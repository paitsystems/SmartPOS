package com.pait.smartpos.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hoin.btsdk.BluetoothService;
import com.pait.smartpos.DrawerTestActivity;
import com.pait.smartpos.R;
import com.pait.smartpos.bluetooth_printer.SearchNewDevice;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.model.UserProfileClass;
import com.pait.smartpos.permission.GetPermission;

import java.util.HashMap;
import java.util.Set;

public class PairedDeviceFragment extends Fragment {

    private Button btn_scan_device;
    private BluetoothService mService;
    private ArrayAdapter<String> list_paired_device, list_new_device;
    private ListView listView_paired, listView_newdevices;
    private ProgressDialog pd;
    private static final int REQUEST_ENABLE_BT = 2;
    private HashMap<String, String> new_device_hash_map;
    private Toast toast;
    private String selectedPrinter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paired_devices, container, false);
        init(view);
        checkPermissionMannual();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mService!=null) {
            if (!mService.isBTopen()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private void init(View view){
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Searching New Device");
        pd.setCancelable(false);

        toast = Toast.makeText(getContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);

        listView_newdevices = view.findViewById(R.id.listView_newdevices);
        listView_paired = view.findViewById(R.id.listView_paired);

        mService = new BluetoothService(getActivity(), handler);
        list_paired_device = new ArrayAdapter<>(getActivity(), R.layout.list_item_bluetooth_paired_device);
        list_new_device = new ArrayAdapter<>(getContext(), R.layout.list_item_bluetooth_new_device);
        new_device_hash_map = new HashMap<>();

        if (!mService.isAvailable()) {
            toast.setText("Bluetooth is not available");
            toast.show();
        }

        btn_scan_device = view.findViewById(R.id.btn_scan_device);
        btn_scan_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPairedDevice();
            }
        });

        listView_paired.setOnItemClickListener(mDeviceClickListener);
    }

    private void checkPermissionMannual() {
        GetPermission permission = new GetPermission();
        if (!permission.checkBluetoothPermission(getContext())) {
            permission.requestBluetoothPermission(getContext(), getActivity());
        }else if (!permission.checkBluetoothAdminPermission(getContext())) {
            permission.requestBluetoothAdminPermission(getContext(), getActivity());
        }else{
            getPairedDevice();
        }
    }

    private void getPairedDevice(){
        try{
            list_paired_device.clear();
            Set<BluetoothDevice> set = mService.getPairedDev();
            if (!set.isEmpty()) {
                for (BluetoothDevice device : set) {
                    list_paired_device.add(device.getName() + "-" + device.getAddress());
                }
            } else {
                list_paired_device.add("No Paired Device Available");
            }
            listView_paired.setAdapter(list_paired_device);
        }catch (Exception e){
            list_paired_device.add("No Paired Device Available");
            e.printStackTrace();
        }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            mService.cancelDiscovery();
            selectedPrinter = list_paired_device.getItem(arg2);
            Constant.showLog(selectedPrinter);
            assert selectedPrinter != null;
            if(!selectedPrinter.equals("No Paired Device Available")) {
                showDia(1);
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
                checkPermissionMannual();
                //discoverNewDevice();
                break;
        }
    }

    private void showDia(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        if (i == 1) {
            builder.setMessage("Do You Want To Set Default Printer?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String[] str = selectedPrinter.split("-");
                    Constant.showLog(str[0]+"-"+str[1]);
                    UserProfileClass user = new Constant(getContext()).getPref();
                    user.setMacAddress(str[1]);
                     new Constant(getContext()).saveToPref(user);
                    dialogInterface.dismiss();
                    showDia(2);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }else if (i == 2) {
            builder.setTitle("Printer Set As Default");
            builder.setMessage("Please Reopen App");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    getActivity().finishAffinity();
                    getActivity().overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                }
            });
        }
        builder.create().show();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
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
