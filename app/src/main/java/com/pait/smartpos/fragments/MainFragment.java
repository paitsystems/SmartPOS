package com.pait.smartpos.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hoin.btsdk.BluetoothService;
import com.hoin.wfsdk.WifiCommunication;
import com.pait.smartpos.CashMemoActivityR;
import com.pait.smartpos.ConnectivityTest;
import com.pait.smartpos.DrawerTestActivity;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.constant.PrinterCommands;
import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.LoginActivity;
import com.pait.smartpos.Post;
import com.pait.smartpos.R;
import com.pait.smartpos.VerificationActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MainFragment extends Fragment {

    private List<String> table_list = new ArrayList<>();
    private List<Integer> occupy_table_list = new ArrayList<>();
    private List<String> cat_list = new ArrayList<>();
    private List<String> prod_list = new ArrayList<>();
    private LinearLayout mContainerView;
    private TextView total_qty, total_amnt, tablename, waiter;
    private Button placeorder;
    private HashMap<String, Integer> table_hash_map = new HashMap<>();
    private HashMap<Integer, Integer> table_btn_pos_hash_map = new HashMap<>();
    private HashMap<Integer, Integer> menu_btn_pos_hash_map = new HashMap<>();
    private HashMap<Integer, String> table_hash_map1 = new HashMap<>();
    private HashMap<String, Integer> category_hash_map = new HashMap<>();
    private HashMap<Integer, List<String>> prod_hash_map = new HashMap<>();
    private HashMap<Integer, List<String>> remove_prod_hash_map = new HashMap<>();
    private HashMap<String, Integer> product_hash_map = new HashMap<>();
    private HashMap<Integer, String> web_prod_hash_map = new HashMap<>();
    private HashMap<String, Integer> web_product_hash_map = new HashMap<>();
    private HashMap<Integer, List<Integer>> order_hash_map = new HashMap<>();
    private HashMap<Integer, Integer> prod_view_map = new HashMap<>();
    private HashMap<Integer, Integer> prodid_qty_hash_map = new HashMap<>();
    private List<Integer> prodid_qty_list = new ArrayList<>();
    private List<Integer> prod_id_track = new ArrayList<>();
    private List<Integer> remove_prod_id_track = new ArrayList<>();
    private HashMap<Integer, String> remark_hash_map = new HashMap<>();
    private HashMap<Integer, List<String>> order_prod_hash_map = new HashMap<>();
    private HashMap<Integer, List<HashMap<Integer, List<String>>>> table_orders = new HashMap<>();
    private int table_id = 0, counter = -1, order_repeat_flag = 0, total_qty_counter = 0, menu_item_id = 0;
    private String kotno, url_kotno;
    private DBHandlerR db;
    private ProgressDialog pd;
    private InputMethodManager input;
    //Handler handler = new Handler();
    private List<Integer> KArea1_list = new ArrayList<>();
    private List<Integer> KArea2_list = new ArrayList<>();
    private List<Integer> KArea3_list = new ArrayList<>();
    private List<String> kArea1print_list = new ArrayList<>();
    private List<String> kArea2print_list = new ArrayList<>();
    private List<String> kArea3print_list = new ArrayList<>();
    private HashMap<Integer, Integer> prev_id_qty_hash_map = new HashMap<>();
    private String date = null;
    //protected ImageLoader loader = ImageLoader.getInstance();
    private DisplayImageOptions op;
    private EditText search_text;
    private Button btn_search, btn_refresh;
    private LinearLayout search_lay;

    //static BluetoothService VerificationActivity.mService;
    private String line_str = "--------------------------------";
    private String space_str = "                        ";
    //String space_str13 = "             ";
    private String space_str21 = "                     ";
    private BluetoothDevice con_dev = null;
    //private static final int REQUEST_ENABLE_BT = 2;

    private String compName, address, compAlias, user, custName = "";
    private Toast toast;
    public static int checkedBeforeCM = 0;
    private WifiCommunication wfComm;
    private View view;

    private Snackbar snackbar;
    private LinearLayout lay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_main,null);
        init(view);

        //wfComm = new WifiCommunication(mHandler);
        //TODO: Change IP
        //wfComm.initSocket("172.30.1.7",9100);

        waiter.setText(user);
        table_list.clear();
        cat_list.clear();
        prod_list.clear();
        table_hash_map1.clear();
        table_hash_map.clear();
        category_hash_map.clear();
        prod_hash_map.clear();
        product_hash_map.clear();
        web_prod_hash_map.clear();
        web_product_hash_map.clear();
        order_hash_map.clear();
        table_orders.clear();
        order_prod_hash_map.clear();
        remark_hash_map.clear();
        occupy_table_list.clear();
        prodid_qty_hash_map.clear();
        prodid_qty_list.clear();
        menu_btn_pos_hash_map.clear();
        remove_prod_hash_map.clear();
        KArea1_list.clear();
        KArea2_list.clear();
        KArea3_list.clear();
        kArea1print_list.clear();
        kArea2print_list.clear();
        kArea3print_list.clear();
        prev_id_qty_hash_map.clear();

        /*if(DrawerTestActivity.isBTConnected==0) {
            VerificationActivity.mService = new BluetoothService(getActivity().getApplicationContext(), mHandler1);
            connectBT();
        }*/

        /*try {
            com.epson.epos2.Log.setLogSettings(getContext(), com.epson.epos2.Log.PERIOD_TEMPORARY, com.epson.epos2.Log.OUTPUT_STORAGE, null, 0, 1, com.epson.epos2.Log.LOGLEVEL_LOW);
        } catch (Epos2Exception e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(getContext(),"Error In Log",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }*/

        date = "" + (Calendar.getInstance()).get(Calendar.DATE) + "/" + ((Calendar.getInstance()).get(Calendar.MONTH) + 1) + "/" + (Calendar.getInstance()).get(Calendar.YEAR);

        op = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.drawable.ic_drawer)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();

        Cursor res_table = db.getTable();
        if (res_table.moveToFirst()) {
            do {
                table_list.add(res_table.getString(1));
                table_hash_map1.put(res_table.getInt(0), res_table.getString(1));
                table_hash_map.put(res_table.getString(1), res_table.getInt(0));
            } while (res_table.moveToNext());
        }
        res_table.close();

        Cursor res_cat = db.getCatgory();
        if (res_cat.moveToFirst()) {
            do {
                cat_list.add(res_cat.getString(1));
                category_hash_map.put(res_cat.getString(1), res_cat.getInt(0));
            } while (res_cat.moveToNext());
        }
        res_cat.close();

        Cursor res_prod = db.getProduct();
        if (res_prod.moveToFirst()) {
            do {
                web_prod_hash_map.put(res_prod.getInt(0), res_prod.getString(1));
                web_product_hash_map.put(res_prod.getString(1), res_prod.getInt(0));
            } while (res_prod.moveToNext());
        }
        res_prod.close();

        Cursor k1_res_prod = db.getProductkArea(1);
        if (k1_res_prod.moveToFirst()) {
            do {
                KArea1_list.add(k1_res_prod.getInt(0));
            } while (k1_res_prod.moveToNext());
        }
        k1_res_prod.close();

        Cursor k2_res_prod = db.getProductkArea(2);
        if (k2_res_prod.moveToFirst()) {
            do {
                KArea2_list.add(k2_res_prod.getInt(0));
            } while (k2_res_prod.moveToNext());
        }
        k2_res_prod.close();

        Cursor k3_res_prod = db.getProductkArea(3);
        if (k3_res_prod.moveToFirst()) {
            do {
                KArea3_list.add(k3_res_prod.getInt(0));
            } while (k3_res_prod.moveToNext());
        }
        k3_res_prod.close();

        // TODO: Bluetooth
        //setTable();
        getBookedTableDataLocal();

        /*if (ConnectivityTest.getNetStat(getActivity())) {
            String url = LoginActivity.ipaddress + "/GetTableBooked";
            new GetTableBooked().execute(url);
        } else {
            Toast toast = Toast.makeText(getContext(),"You Are Offline",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }*/

        /*handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO: Change Refresh Time
                handler.postDelayed(getActivity(), (30 * 1000));
                if (ConnectivityTest.getNetStat(getActivity())) {
                    String url = LoginActivity.ipaddress + "/GetTableBooked";
                    new GetTableBooked().execute(url);
                } else {
                    Toast toast = Toast.makeText(getContext(),"You Are Offline",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
            }
        }, 0);*/

        setMenu();

        search_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_text.setError(null);
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input.hideSoftInputFromWindow(btn_search.getWindowToken(), 0);
                if(search_text.length()!=0 && !search_text.getText().toString().equals(" ")){
                    mContainerView.removeAllViews();
                    prod_list.clear();
                    prod_hash_map.clear();
                    product_hash_map.clear();
                    btn_refresh.setClickable(true);
                    btn_refresh.setEnabled(true);
                    addMenuItems(menu_item_id);
                }else{
                    search_text.setError("Required");
                }
            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input.hideSoftInputFromWindow(btn_refresh.getWindowToken(), 0);
                mContainerView.removeAllViews();
                prod_list.clear();
                prod_hash_map.clear();
                search_text.setText(null);
                product_hash_map.clear();
                btn_refresh.setClickable(false);
                btn_refresh.setEnabled(false);
                addMenuItems(menu_item_id);
            }
        });

        placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //placeOrderM();
                if(table_id!=1) {
                    placeOrderChnaged();
                }else{
                    showRemark(1);
                }
            }
        });
        return view;
    }

    private void init(View view){
        search_text = view.findViewById(R.id.edit_text_search);
        btn_search = view.findViewById(R.id.btn_search);
        btn_refresh = view.findViewById(R.id.refresh);
        search_lay = view.findViewById(R.id.search_lay);

        tablename = view.findViewById(R.id.tablename);
        total_qty = view.findViewById(R.id.total_qty);
        total_amnt = view.findViewById(R.id.total_amount);
        placeorder = view.findViewById(R.id.placeorder);
        mContainerView = view.findViewById(R.id.container);
        waiter = view.findViewById(R.id.waitername);
        VerificationActivity.pref = getContext().getSharedPreferences(VerificationActivity.PREF_NAME,Context.MODE_PRIVATE);
        compName = VerificationActivity.pref.getString(getString(R.string.pref_compName),"");
        address = VerificationActivity.pref.getString(getString(R.string.pref_address),"");
        compAlias = VerificationActivity.pref.getString(getString(R.string.pref_contactNo),"");
        user = VerificationActivity.pref.getString(getString(R.string.pref_username),"");

        toast = Toast.makeText(getContext().getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);

        ((LinearLayout) view.findViewById(R.id.table_layout)).removeAllViews();
        ((LinearLayout) view.findViewById(R.id.menu_layout)).removeAllViews();
        db = new DBHandlerR(getActivity());
        input = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        lay = view.findViewById(R.id.lay);
        //snackbar = Snackbar.make(lay, "", Snackbar.LENGTH_LONG);
    }

    @SuppressLint("HandlerLeak")
    private final  Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WifiCommunication.WFPRINTER_CONNECTED:
                    //toast.setText("Connect the WIFI-printer successful");
                    showSnackBar("Connect the WIFI-printer successful");
                    break;
                case WifiCommunication.WFPRINTER_DISCONNECTED:
                    //toast.setText("Disconnect the WIFI-printer successful");
                    showSnackBar("Disconnect the WIFI-printer successful");
                    break;
                case WifiCommunication.SEND_FAILED:
                    //toast.setText("Send Data Failed,please reconnect");
                    showSnackBar("Send Data Failed,please reconnect");
                    break;
                case WifiCommunication.WFPRINTER_CONNECTEDERR:
                    //toast.setText("Connect the WIFI-printer error");
                    showSnackBar("Connect the WIFI-printer error");
                    break;
                case WifiCommunication.WFPRINTER_REVMSG:
                    byte revData = (byte)Integer.parseInt(msg.obj.toString());
                    if(((revData >> 6) & 0x01) == 0x01)
                        //toast.setText("The printer has no paper");
                        showSnackBar("The printer has no paper");
                    break;
                default:
                    break;
            }
            //toast.show();
        }
    };

    /*@Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            showDia(2);
        }else if (item.getItemId() == R.id.bill) {
            //ToDO: UncommentCOde
            //handler.removeCallbacksAndMessages(null);
            if(table_id!=0) {
                if(checkedBeforeCM == 1) {
                    if (!order_hash_map.isEmpty() && !order_prod_hash_map.isEmpty()) {
                        Intent intent = new Intent(getActivity(), CashMemoActivityR.class);
                        intent.putExtra("proditemid", order_hash_map);
                        intent.putExtra("prodlist", order_prod_hash_map);
                        intent.putExtra("totalqty", total_qty.getText());
                        intent.putExtra("totalamt", total_amnt.getText());
                        intent.putExtra("user", user);
                        intent.putExtra("branchid", 1);
                        intent.putExtra("id", 1);
                        intent.putExtra("tableid", String.valueOf(table_id));
                        intent.putExtra("kotno", kotno);
                        startActivity(intent);
                        Runtime.getRuntime().gc();
                        Runtime.getRuntime().freeMemory();
                        getActivity().finish();
                        getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                    } else {
                        toast.setText("Please Select Booked Table");
                        toast.show();
                    }
                } else {
                    toast.setText("Please Save Item First");
                    toast.show();
                }
            }else{
                toast.setText("Please Select Table");
                toast.show();
            }
        }else if(R.id.settings==item.getItemId()){
            startActivity(new Intent(getContext(), SettingsOptionsActivity.class));
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.enter,R.anim.exit);
        }*//*else if(R.id.add==item.getItemId()){
            db.deleteTable(DBHandlerR.TempTable_Table);
            db.deleteTable(DBHandlerR.BillMaster_Table);
            db.deleteTable(DBHandlerR.BillDetail_Table);
            getBookedTableDataLocal();
            toast.setText("Data Cleared");
            toast.show();
        }*//*
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showDia(1);
    }*/

    private void connectBT(){
        Set<BluetoothDevice> set = VerificationActivity.mService.getPairedDev();
        if (!set.isEmpty()) {
            for (BluetoothDevice device : set) {
                con_dev = VerificationActivity.mService.getDevByMac(device.getAddress());
                Log.d("Log", "getDevByMac :- "+ con_dev);
                VerificationActivity.mService.connect(con_dev);
                DrawerTestActivity.isBTConnected = 1;
            }
        }
    }

    public void saveBill1(){
        if(table_id!=0) {
            if(checkedBeforeCM == 1) {
                if (!order_hash_map.isEmpty() && !order_prod_hash_map.isEmpty()) {
                    Intent intent = new Intent(getActivity(), CashMemoActivityR.class);
                    intent.putExtra("proditemid", order_hash_map);
                    intent.putExtra("prodlist", order_prod_hash_map);
                    intent.putExtra("totalqty", total_qty.getText());
                    intent.putExtra("totalamt", total_amnt.getText());
                    intent.putExtra("user", user);
                    intent.putExtra("branchid", 1);
                    intent.putExtra("id", 1);
                    intent.putExtra("tableid", String.valueOf(table_id));
                    intent.putExtra("kotno", kotno);
                    startActivity(intent);
                    Runtime.getRuntime().gc();
                    Runtime.getRuntime().freeMemory();
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                } else {
                    /*toast.setText("Please Select Booked Table");
                    toast.show();*/
                    showSnackBar("Please Select Booked Table");
                }
            } else {
                /*toast.setText("Please Save Item First");
                toast.show();*/
                showSnackBar("Please Save Item First");
            }
        }else{
            /*toast.setText("Please Select Table");
            toast.show();*/
            showSnackBar("Please Select Table");
        }
    }

    public static String getTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        if (hour == 0) {
            hour = 12;
        }
        String hour1 = String.valueOf(hour);
        if (hour1.length() == 1) {
            hour1 = "0" + hour1;
        }
        String minute1 = String.valueOf(calendar.get(Calendar.MINUTE));
        if (minute1.length() == 1) {
            minute1 = "0" + minute1;
        }
        return hour1 + ":" + minute1;
    }

    private void setTable() {
        ((LinearLayout) view.findViewById(R.id.table_layout)).removeAllViews();
        table_btn_pos_hash_map.clear();
        int j = 0;
        for (int i = 0; i < table_list.size(); i++) {
            ViewGroup newView;
            if (occupy_table_list.size() != 0) {
                if (occupy_table_list.contains(table_hash_map.get(table_list.get(i)))) {
                    if (occupy_table_list.get(j) == table_id) {
                        newView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.table_select, null, false);
                    } else {
                        newView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.table_occupy, null, false);
                    }
                    j++;
                } else {
                    if (table_id == table_hash_map.get(table_list.get(i)) && ((total_qty_counter != 0) ||(total_qty_counter == 0))) {
                        newView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.table_select, null, false);
                        if(!prodid_qty_list.isEmpty()){
                            counter = -1;
                            total_qty_counter = 0;
                            total_qty.setText("0");
                            total_amnt.setText("0");
                            order_hash_map.clear();
                            order_prod_hash_map.clear();
                            remark_hash_map.clear();
                            prod_id_track.clear();
                            prod_view_map.clear();
                            prev_id_qty_hash_map.clear();
                            prodid_qty_hash_map.clear();
                            ((LinearLayout) view.findViewById(R.id.order_container)).removeAllViews();
                        }
                    } else {
                        newView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.table_release, null, false);
                    }
                }
            } else {
                if (table_id == table_hash_map.get(table_list.get(i)) && ((total_qty_counter != 0) ||(total_qty_counter == 0))) {
                    newView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.table_select, null, false);
                    if(!prodid_qty_list.isEmpty()){
                        counter = -1;
                        total_qty_counter = 0;
                        total_qty.setText("0");
                        total_amnt.setText("0");
                        order_hash_map.clear();
                        order_prod_hash_map.clear();
                        remark_hash_map.clear();
                        prod_id_track.clear();
                        prod_view_map.clear();
                        prev_id_qty_hash_map.clear();
                        prodid_qty_hash_map.clear();
                        ((LinearLayout) view.findViewById(R.id.order_container)).removeAllViews();
                    }
                } else {
                    newView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.table_release, null, false);
                }
            }
            final Button btn = newView.findViewById(R.id.btn_id);
            btn.setId(table_hash_map.get(table_list.get(i)));
            btn.setText(table_list.get(i));
            table_btn_pos_hash_map.put(btn.getId(), i);
            btn.setOnClickListener(v -> {
                //table_id = btn.getId();
                if (!occupy_table_list.contains(table_id) && table_id != 0) {
                    if(table_btn_pos_hash_map.containsKey(table_id)) {
                        ViewGroup vg = (ViewGroup) ((LinearLayout) view.findViewById(R.id.table_layout)).getChildAt(table_btn_pos_hash_map.get(table_id));
                        vg.findViewById(table_id).setBackgroundResource(R.drawable.table_release_draw);
                    }else{
                        /*toast.setText("Something  Went Wrong");
                        toast.show();*/
                        showSnackBar("Something  Went Wrong");
                    }
                }
                if (occupy_table_list.contains(table_id)) {
                    if(table_btn_pos_hash_map.containsKey(table_id)) {
                        ViewGroup vg = (ViewGroup) ((LinearLayout) view.findViewById(R.id.table_layout)).getChildAt(table_btn_pos_hash_map.get(table_id));
                        vg.findViewById(table_id).setBackgroundResource(R.drawable.table_occu_draw);
                    }else{
                        /*toast.setText("Something  Went Wrong");
                        toast.show();*/
                        showSnackBar("Something  Went Wrong");
                    }
                }
                btn.setBackgroundResource(R.drawable.table_select_draw);
                counter = -1;
                total_qty_counter = 0;
                total_qty.setText("0");
                total_amnt.setText("0");
                order_hash_map.clear();
                order_prod_hash_map.clear();
                remark_hash_map.clear();
                prod_id_track.clear();
                prod_view_map.clear();
                prev_id_qty_hash_map.clear();
                prodid_qty_hash_map.clear();
                ((LinearLayout) view.findViewById(R.id.order_container)).removeAllViews();
                table_id = btn.getId();
                tablename.setText(table_hash_map1.get(btn.getId()));

                getLocalTableData(db.getLocalRefreshData(table_id));

                /*String url = LoginActivity.ipaddress + "/getTableData?TableId=" + table_id;
                Log.d("Log",url);
                if (ConnectivityTest.getNetStat(getActivity())) {
                    new getTableData().execute(url);
                }*/
            });
            ((LinearLayout) view.findViewById(R.id.table_layout)).addView(newView, i);
        }
    }

    private void setMenu() {
        ((LinearLayout) view.findViewById(R.id.menu_layout)).removeAllViews();
        for (int i = 0; i < cat_list.size(); i++) {
            //search_lay.setVisibility(View.INVISIBLE);
            final ViewGroup newView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.table_button, null, false);
            final Button btn = newView.findViewById(R.id.btn_id);
            btn.setId(category_hash_map.get(cat_list.get(i)));
            btn.setBackgroundResource(R.drawable.menu_item_deselect_draw);
            btn.setText(cat_list.get(i));
            menu_btn_pos_hash_map.put(btn.getId(), i);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContainerView.removeAllViews();
                    search_text.setText(null);
                    if (menu_item_id != 0) {
                        ViewGroup vg = (ViewGroup) ((LinearLayout) view.findViewById(R.id.menu_layout)).getChildAt(menu_btn_pos_hash_map.get(menu_item_id));
                        vg.findViewById(menu_item_id).setBackgroundResource(R.drawable.menu_item_deselect_draw);
                    }
                    menu_item_id = btn.getId();
                    btn.setBackgroundResource(R.drawable.menu_item_select_draw);
                    addMenuItems(btn.getId());
                    //search_lay.setVisibility(View.VISIBLE);
                    btn_refresh.setClickable(false);
                    btn_refresh.setEnabled(false);
                    search_text.setError(null);
                }
            });
            ((LinearLayout) view.findViewById(R.id.menu_layout)).addView(newView, i);
        }
    }

    private void addMenuItems(int j) {
        List<String> rateList = new ArrayList<>();
        prod_list.clear();
        prod_hash_map.clear();
        product_hash_map.clear();
        Cursor res_prod;
        if(search_text.getText()!=null && search_text.length()!=0 && !search_text.getText().toString().equals("")) {
            res_prod = db.getProduct(j,search_text.getText().toString());
        }else{
            res_prod = db.getProduct(j);
        }
        if (res_prod.moveToFirst()) {
            do {
                List<String> list = new ArrayList<>();
                list.add(res_prod.getString(0));
                list.add(res_prod.getString(1));
                list.add(res_prod.getString(2));
                list.add(res_prod.getString(3));
                list.add(res_prod.getString(4));
                list.add("N");
                prod_hash_map.put(res_prod.getInt(0), list);
                prod_list.add(res_prod.getString(1));
                rateList.add(res_prod.getString(3));
                product_hash_map.put(res_prod.getString(1),res_prod.getInt(0));
            } while(res_prod.moveToNext());
        }else{
            /*toast.setText("No Record Available");
            toast.show();*/
            showSnackBar("No Record Available");
        }
        res_prod.close();
        for (int i = 0; i < prod_list.size(); i++) {
            //final ViewGroup newView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.list_item_menu_item, mContainerView, false);
            final ViewGroup newView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.test, mContainerView, false);
            ((TextView) newView.findViewById(R.id.item_name)).setText(prod_list.get(i));
            ((TextView) newView.findViewById(R.id.item_rate)).setText(rateList.get(i));
            final EditText remark = newView.findViewById(R.id.remark);
            remark.setId(product_hash_map.get(prod_list.get(i)));
            remark.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    boolean handled = false;
                    if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                        input.hideSoftInputFromWindow(remark.getWindowToken(), 0);
                        handled = true;
                    }
                    return handled;
                }
            });
            final Button btn = newView.findViewById(R.id.add);
            btn.setId(product_hash_map.get(prod_list.get(i)));
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    input.hideSoftInputFromWindow(btn.getWindowToken(), 0);
                    if (table_id != 0) {
                        if (((EditText) remark.findViewById(btn.getId())).getText().length() != 0) {
                            remark_hash_map.put(btn.getId(), ((EditText) remark.findViewById(btn.getId())).getText().toString());
                        } else {
                            remark_hash_map.put(btn.getId(), "");
                        }
                        if (order_hash_map.containsKey(table_id)) {
                            List<Integer> list = order_hash_map.get(table_id);
                            if (!list.contains(btn.getId())) {
                                list.add(btn.getId());
                                order_hash_map.put(table_id, list);
                                order_repeat_flag = 0;
                                counter++;
                                total_qty.setText(String.valueOf(++total_qty_counter));
                            } else {
                                order_repeat_flag = 1;
                            }
                        } else {
                            List<Integer> list = new ArrayList<>();
                            list.add(btn.getId());
                            order_hash_map.put(table_id, list);
                            counter++;
                            total_qty.setText(String.valueOf(++total_qty_counter));
                            order_repeat_flag = 0;
                        }
                        ((EditText) remark.findViewById(btn.getId())).setText("");
                        addOrderItem(prod_hash_map.get(btn.getId()), btn.getId());
                    } else {
                        /*toast.setText("Please Select Table");
                        toast.show();*/
                        showSnackBar("Please Select Table");
                    }
                }
            });
            mContainerView.addView(newView, i);
        }
    }

    private void addOrderItem(List<String> list, int i) {
        if (order_repeat_flag == 0) {
            checkedBeforeCM = 0;
            final ViewGroup newView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.list_item_order_item, null, false);
            ((TextView) newView.findViewById(R.id.order_item_name)).setText(list.get(1));
            ((TextView) newView.findViewById(R.id.order_qty)).setText("1");
            ((TextView) newView.findViewById(R.id.order_rate)).setText(list.get(3));
            ((TextView) newView.findViewById(R.id.order_amount)).setText(String.valueOf(Double.parseDouble(list.get(3))));
            final Button btn = newView.findViewById(R.id.remove);
            btn.setId(i);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove_prod_id_track.clear();
                    remove_item(btn.getId(), 0);
                }
            });
            double toadd = Double.parseDouble(list.get(3));
            double tot_amnt = Double.parseDouble(total_amnt.getText().toString());
            total_amnt.setText(String.valueOf((toadd + tot_amnt)));
            ((LinearLayout) view.findViewById(R.id.order_container)).addView(newView, counter);
            List<String> list1 = new ArrayList<>();
            list1.add(0, ((TextView) newView.findViewById(R.id.order_item_name)).getText().toString());
            list1.add(1, ((TextView) newView.findViewById(R.id.order_qty)).getText().toString());
            list1.add(2, ((TextView) newView.findViewById(R.id.order_rate)).getText().toString());
            list1.add(3, ((TextView) newView.findViewById(R.id.order_amount)).getText().toString());
            list1.add(4, list.get(4));
            list1.add(5, list.get(5));
            order_prod_hash_map.put(i, list1);
            prod_id_track.add(i);
            int count = counter;
            prod_view_map.put(i, count);
        } else if (order_repeat_flag == 1) {
            checkedBeforeCM = 0;
            View newView = ((LinearLayout) view.findViewById(R.id.order_container)).getChildAt(prod_view_map.get(i));
            newView.setBackgroundResource(R.drawable.order_item_list_item_cover);
            int qty = Integer.parseInt(((TextView) newView.findViewById(R.id.order_qty)).getText().toString());
            double d = Double.parseDouble((((TextView) newView.findViewById(R.id.order_rate)).getText().toString()));
            ((TextView) newView.findViewById(R.id.order_item_name)).setText(list.get(1));
            ((TextView) newView.findViewById(R.id.order_item_name)).setTextColor(Color.parseColor("#000000"));
            ((TextView) newView.findViewById(R.id.order_qty)).setText(String.valueOf(++qty));
            ((TextView) newView.findViewById(R.id.order_qty)).setTextColor(Color.parseColor("#000000"));
            ((TextView) newView.findViewById(R.id.order_rate)).setText(list.get(3));
            ((TextView) newView.findViewById(R.id.order_rate)).setTextColor(Color.parseColor("#000000"));
            ((TextView) newView.findViewById(R.id.order_amount)).setText(String.valueOf(d * (qty)));
            ((TextView) newView.findViewById(R.id.order_amount)).setTextColor(Color.parseColor("#000000"));
            double tot_amnt = Double.parseDouble(total_amnt.getText().toString());
            total_amnt.setText(String.valueOf((Double.parseDouble(list.get(3)) + tot_amnt)));
            ((LinearLayout) view.findViewById(R.id.order_container)).removeViewAt(prod_view_map.get(i));
            ((LinearLayout) view.findViewById(R.id.order_container)).addView(newView, prod_view_map.get(i));
            List<String> list1 = new ArrayList<>();
            list1.add(0, ((TextView) newView.findViewById(R.id.order_item_name)).getText().toString());
            list1.add(1, ((TextView) newView.findViewById(R.id.order_qty)).getText().toString());
            list1.add(2, ((TextView) newView.findViewById(R.id.order_rate)).getText().toString());
            list1.add(3, ((TextView) newView.findViewById(R.id.order_amount)).getText().toString());
            list1.add(4, list.get(4));
            list1.add(5, list.get(5));
            order_prod_hash_map.put(i, list1);
        } else if (order_repeat_flag == 3) {
            checkedBeforeCM = 1;
            final ViewGroup newView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.list_item_db_order_item, null, false);
            ((TextView) newView.findViewById(R.id.order_item_name)).setText(list.get(0));
            ((TextView) newView.findViewById(R.id.order_qty)).setText(list.get(1));
            final Button btn = newView.findViewById(R.id.remove);
            btn.setId(i);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (order_prod_hash_map.size() == 0) {
                        /*toast.setText("Could Not Changed Qty");
                        toast.show();*/
                        showSnackBar("Can't Change Qty");
                    } else {
                        int original_qty = prodid_qty_hash_map.get(btn.getId());
                        List<String> hm_list = order_prod_hash_map.get(btn.getId());
                        int added_qty = Integer.parseInt(hm_list.get(1));
                        if (original_qty >= added_qty) {
                            /*toast.setText("Could Not Changed Qty");
                            toast.show();*/
                            showSnackBar("Can't Change Qty");
                        } else if (original_qty < added_qty) {
                            remove_item(btn.getId(), 1);
                        }
                    }
                }
            });
            ((TextView) newView.findViewById(R.id.order_rate)).setText(list.get(2));
            ((TextView) newView.findViewById(R.id.order_amount)).setText(String.valueOf(Double.parseDouble(list.get(3))));
            double toadd = Double.parseDouble(list.get(3));
            double tot_amnt = Double.parseDouble(total_amnt.getText().toString());
            total_amnt.setText(String.valueOf((toadd + tot_amnt)));
            ((LinearLayout) view.findViewById(R.id.order_container)).addView(newView, counter);
            List<String> list1 = new ArrayList<>();
            list1.add(0,((TextView) newView.findViewById(R.id.order_item_name)).getText().toString());
            list1.add(1,((TextView) newView.findViewById(R.id.order_qty)).getText().toString());
            list1.add(2,((TextView) newView.findViewById(R.id.order_rate)).getText().toString());
            list1.add(3,((TextView) newView.findViewById(R.id.order_amount)).getText().toString());
            list1.add(4,list.get(4));
            list1.add(5,list.get(5));
            order_prod_hash_map.put(i,list1);
            prod_id_track.add(i);
            remark_hash_map.put(i,"");
            int count = counter;
            prod_view_map.put(i, count);
        } else if (order_repeat_flag == 4) {
            checkedBeforeCM = 0;
            final ViewGroup newView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.list_item_remove_order, null, false);
            ((TextView) newView.findViewById(R.id.order_item_name)).setText(list.get(0));
            ((TextView) newView.findViewById(R.id.order_qty)).setText(list.get(1));
            ((TextView) newView.findViewById(R.id.order_rate)).setText(list.get(2));
            ((TextView) newView.findViewById(R.id.order_amount)).setText(String.valueOf(Double.parseDouble(list.get(3))));
            double toadd = Double.parseDouble(list.get(3));
            double tot_amnt = Double.parseDouble(total_amnt.getText().toString());
            total_amnt.setText(String.valueOf((toadd + tot_amnt)));
            final Button btn = newView.findViewById(R.id.remove);
            btn.setId(i);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove_item(btn.getId(), 0);
                }
            });
            ((LinearLayout) view.findViewById(R.id.order_container)).addView(newView, counter);
            List<String> list1 = new ArrayList<>();
            list1.add(0, ((TextView) newView.findViewById(R.id.order_item_name)).getText().toString());
            list1.add(1, ((TextView) newView.findViewById(R.id.order_qty)).getText().toString());
            list1.add(2, ((TextView) newView.findViewById(R.id.order_rate)).getText().toString());
            list1.add(3, ((TextView) newView.findViewById(R.id.order_amount)).getText().toString());
            list1.add(4, list.get(4));
            list1.add(5, list.get(5));
            order_prod_hash_map.put(i, list1);
            prod_id_track.add(i);
            remark_hash_map.put(i, "");
            prod_view_map.put(i, counter);
        }
    }

    private void remove_item(int btn_id, int type) {
        Log.d("Log",""+type);
        counter = -1;
        total_amnt.setText("0");
        remove_prod_hash_map.clear();
        remove_prod_id_track.clear();
        for (int i = 0; i < prod_id_track.size(); i++) {
            remove_prod_id_track.add(prod_id_track.get(i));
        }
        for (int i = 0; i < order_prod_hash_map.size(); i++) {
            remove_prod_hash_map.put(prod_id_track.get(i), order_prod_hash_map.get(prod_id_track.get(i)));
        }
        order_prod_hash_map.clear();
        prod_id_track.clear();
        prod_view_map.clear();
        List<String> list = remove_prod_hash_map.get(btn_id);
        int qty = Integer.parseInt(list.get(1));
        if (qty == 1) {
            total_qty_counter--;
            total_qty.setText(String.valueOf(total_qty_counter));
            List<Integer> order_list = order_hash_map.get(table_id);
            for (int i = 0; i < order_list.size(); i++) {
                if (order_list.get(i) == btn_id) {
                    order_list.remove(i);
                }
            }
            order_hash_map.remove(table_id);
            order_hash_map.put(table_id, order_list);
            remove_prod_hash_map.remove(btn_id);

            for (int i = 0; i < remove_prod_id_track.size(); i++) {
                if (remove_prod_id_track.get(i) == btn_id) {
                    remove_prod_id_track.remove(i);
                }
            }
            ((LinearLayout) view.findViewById(R.id.order_container)).removeAllViews();
            for (int g = 0; g < remove_prod_hash_map.size(); g++) {
                counter++;
                if (prodid_qty_hash_map.size() != 0) {
                    if (prodid_qty_list.contains(remove_prod_id_track.get(g))) {
                        int original_qty = prodid_qty_hash_map.get(remove_prod_id_track.get(g));
                        List<String> l =remove_prod_hash_map.get(remove_prod_id_track.get(g));
                        int b = Integer.parseInt(l.get(1));
                        if(b>original_qty){
                            order_repeat_flag = 4;
                            addOrderItem(remove_prod_hash_map.get(remove_prod_id_track.get(g)), remove_prod_id_track.get(g));
                        }else{
                            order_repeat_flag = 3;
                            addOrderItem(remove_prod_hash_map.get(remove_prod_id_track.get(g)), remove_prod_id_track.get(g));
                        }
                    } else {
                        order_repeat_flag = 4;
                        addOrderItem(remove_prod_hash_map.get(remove_prod_id_track.get(g)), remove_prod_id_track.get(g));
                    }
                } else {
                    order_repeat_flag = 4;
                    addOrderItem(remove_prod_hash_map.get(remove_prod_id_track.get(g)), remove_prod_id_track.get(g));
                }
            }
        } else if (qty > 1) {
            qty = qty - 1;
            Double d = Double.parseDouble(list.get(2));
            double loc_total_amnt = qty * d;
            list.remove(1);
            list.add(1, String.valueOf(qty));
            list.remove(3);
            list.add(3, String.valueOf(loc_total_amnt));
            remove_prod_hash_map.remove(btn_id);
            remove_prod_hash_map.put(btn_id, list);
            ((LinearLayout) view.findViewById(R.id.order_container)).removeAllViews();
            for (int g = 0; g < remove_prod_hash_map.size(); g++) {
                counter++;
                if (prodid_qty_hash_map.size() != 0) {
                    if (prodid_qty_list.contains(remove_prod_id_track.get(g))) {
                        int original_qty = prodid_qty_hash_map.get(remove_prod_id_track.get(g));
                        List<String> l =remove_prod_hash_map.get(remove_prod_id_track.get(g));
                        int b = Integer.parseInt(l.get(1));
                        if(b>original_qty){
                            order_repeat_flag = 4;
                            addOrderItem(remove_prod_hash_map.get(remove_prod_id_track.get(g)), remove_prod_id_track.get(g));
                        }else{
                            order_repeat_flag = 3;
                            addOrderItem(remove_prod_hash_map.get(remove_prod_id_track.get(g)), remove_prod_id_track.get(g));
                        }
                    } else {
                        order_repeat_flag = 4;
                        addOrderItem(remove_prod_hash_map.get(remove_prod_id_track.get(g)), remove_prod_id_track.get(g));
                    }
                } else {
                    order_repeat_flag = 4;
                    addOrderItem(remove_prod_hash_map.get(remove_prod_id_track.get(g)), remove_prod_id_track.get(g));
                }
            }
        }
    }

    private void showDia(int a){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(a==1) {
            builder.setMessage("Do You Want To Exit App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dofinish();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else if(a==2) {
            builder.setMessage("Do You Want To Logout App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = VerificationActivity.pref.edit();
                    editor.putBoolean(getString(R.string.pref_logged), false);
                    editor.apply();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    dofinish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        builder.create().show();
    }

    private void dofinish(){
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }

    private void placeOrderM(){
        kArea1print_list.clear();
        kArea2print_list.clear();
        kArea3print_list.clear();
        remove_prod_hash_map.clear();
        String str = "", remark_str;

        int item_length = 25, remark_length = 0, total_length = 24;
        int flag = 0;
        for (int i = 0; i < prod_id_track.size(); i++) {
            if (remark_hash_map.size() != 0 && !remark_hash_map.isEmpty()) {
                remark_str = remark_hash_map.get(prod_id_track.get(i));
                if (remark_str.length() == 0) {
                    remark_str = " ";
                }
            } else {
                remark_str = " ";
            }
            List<String> list = order_prod_hash_map.get(prod_id_track.get(i));

            if (KArea1_list.contains(prod_id_track.get(i))) {
                if (!prev_id_qty_hash_map.isEmpty()) {
                    if (prev_id_qty_hash_map.containsKey(prod_id_track.get(i))) {
                        int a = prev_id_qty_hash_map.get(prod_id_track.get(i));
                        int b = Integer.parseInt(list.get(1));
                        if (a < b) {
                            if (remark_hash_map.containsKey(prod_id_track.get(i))) {
                                String print_remark = remark_hash_map.get(prod_id_track.get(i));
                                String print_remark1 = print_remark;
                                if (print_remark.length() != 0) {
                                    String data = list.get(0);
                                    String data1 = list.get(0);
                                    if (data.length() > item_length) {
                                        data = data.substring(0,(item_length-1));
                                        data = data + " ";
                                        data1 = data1.substring(item_length,data1.length());
                                        if(data1.length()>item_length){
                                            data1 = data1.substring(0,(item_length-1));
                                            data1 = data1 + " ";
                                        }
                                        int w = item_length-data1.length();
                                        for(int e=0;e<w;e++){
                                            data1 = data1 + " ";
                                        }
                                        data = data + "#" + data1;
                                    }else if(data.length()<=(item_length-1)){
                                        int q = item_length-data.length();
                                        for(int j=0;j<q;j++){
                                            data = data + " ";
                                        }
                                    }
                                    if (print_remark.length() > remark_length) {
                                        //print_remark = print_remark.substring(0,(remark_length-1));
                                        //print_remark = print_remark + " ";
                                        print_remark1 = print_remark1.substring(remark_length,print_remark1.length());
                                        if(print_remark1.length()>remark_length){
                                            print_remark1 = print_remark1.substring(0,(remark_length-1));
                                            print_remark1 = print_remark1 + " ";
                                        }
                                        int w = remark_length-print_remark1.length();
                                        for(int e=0;e<w;e++){
                                            print_remark1 = print_remark1 + " ";
                                        }
                                        //print_remark = print_remark + "#" + print_remark1;
                                    }else if(print_remark.length() <= remark_length){
                                        int q = remark_length-print_remark.length();
                                        for(int j=0;j<q;j++){
                                            print_remark = print_remark + " ";
                                        }
                                    }
                                    //kArea1print_list.add(data + "@" + print_remark + "!" + (b - a));
                                    String amnt = list.get(3);
                                    String s[] = amnt.split("\\.");
                                    if(s.length>1) {
                                        kArea1print_list.add(data + "@" + list.get(1)+"  " + "!" + s[0]);
                                    }else{
                                        kArea1print_list.add(data + "@" + list.get(1)+"  " + "!" + amnt);
                                    }
                                } else {
                                    String data = list.get(0);
                                    if (data.length() > total_length) {
                                        data = data.substring(0, (total_length-1));
                                        data = data + " ";
                                    }else if(data.length() <= (total_length-1)){
                                        int q = total_length-data.length();
                                        for(int k=0;k<q;k++){
                                            data = data + " ";
                                        }
                                    }

                                    //kArea1print_list.add(data + "!" + (b - a));
                                    String amnt = list.get(3);
                                    String s[] = amnt.split("\\.");
                                    if(s.length>1) {
                                        kArea1print_list.add(data + "@" + list.get(1)+"  " + "!" + s[0]);
                                    }else{
                                        kArea1print_list.add(data + "@" + list.get(1)+"  " + "!" + amnt);
                                    }
                                }
                            }
                        }
                    } else {
                        if (remark_hash_map.containsKey(prod_id_track.get(i))) {
                            String print_remark = remark_hash_map.get(prod_id_track.get(i));
                            String print_remark1 = print_remark;
                            if (print_remark.length() != 0) {
                                String data = list.get(0);
                                String data1 = list.get(0);
                                if (data.length() > item_length) {
                                    data = data.substring(0, (item_length-1));
                                    data = data + " ";
                                    data1 = data1.substring((item_length-1),data1.length());
                                    if(data1.length()>item_length){
                                        data1 = data1.substring(0,(item_length-1));
                                        data1 = data1 + " ";
                                    }
                                    int w = item_length-data1.length();
                                    for(int e=0;e<w;e++){
                                        data1 = data1 + " ";
                                    }
                                    data = data + "#" + data1;
                                }else if(data.length()<=(item_length-1)){
                                    int q = item_length-data.length();
                                    for(int j=0;j<q;j++){
                                        data = data + " ";
                                    }
                                }
                                if (print_remark.length() > remark_length) {
                                    //print_remark = print_remark.substring(0,(remark_length-1));
                                    //print_remark = print_remark + " ";
                                    print_remark1 = print_remark1.substring((remark_length-1),print_remark1.length());
                                    if(print_remark1.length()>remark_length){
                                        print_remark1 = print_remark1.substring(0,(remark_length-1));
                                        print_remark1 = print_remark1 + " ";
                                    }
                                    int w = remark_length-print_remark1.length();
                                    for(int e=0;e<w;e++){
                                        print_remark1 = print_remark1 + " ";
                                    }
                                    //print_remark = print_remark + "#" + print_remark1;
                                }else if(print_remark.length() <= (remark_length-1)){
                                    int q = remark_length-print_remark.length();
                                    for(int j=0;j<q;j++){
                                        print_remark = print_remark + " ";
                                    }
                                }
                                //kArea1print_list.add(data + "@" + print_remark + "!" + list.get(1));
                                String amnt = list.get(3);
                                String s[] = amnt.split("\\.");
                                if(s.length>1) {
                                    kArea1print_list.add(data + "@" + list.get(1)+"  " + "!" + s[0]);
                                }else{
                                    kArea1print_list.add(data + "@" + list.get(1)+"  " + "!" + amnt);
                                }
                            } else {
                                String data = list.get(0);
                                if (data.length() > total_length) {
                                    data = data.substring(0, (total_length-1));
                                    data = data + " ";
                                }else if(data.length() <= (total_length-1)){
                                    int q = total_length-data.length();
                                    for(int k=0;k<q;k++){
                                        data = data + " ";
                                    }
                                }
                                //kArea1print_list.add(data + "!" + list.get(1));
                                String amnt = list.get(3);
                                String s[] = amnt.split("\\.");
                                if(s.length>1) {
                                    kArea1print_list.add(data + "@" + list.get(1)+"  " + "!" + s[0]);
                                }else{
                                    kArea1print_list.add(data + "@" + list.get(1)+"  " + "!" + amnt);
                                }
                            }
                        }
                    }
                } else {
                    if (remark_hash_map.containsKey(prod_id_track.get(i))) {
                        String print_remark = remark_hash_map.get(prod_id_track.get(i));
                        String print_remark1 = print_remark;
                        if (print_remark.length() != 0) {
                            String data = list.get(0);
                            String data1 = list.get(0);
                            if (data.length() > item_length) {
                                data = data.substring(0, (item_length-1));
                                data = data + " ";
                                data1 = data1.substring((item_length-1),data1.length());
                                if(data1.length()>item_length){
                                    data1 = data1.substring(0,(item_length-1));
                                    data1 = data1 + " ";
                                }
                                int w = item_length-data1.length();
                                for(int e=0;e<w;e++){
                                    data1 = data1 + " ";
                                }
                                data = data + "#" + data1;
                            }else if(data.length()<=(item_length-1)){
                                int q = item_length-data.length();
                                for(int j=0;j<q;j++){
                                    data = data + " ";
                                }
                            }
                            if (print_remark.length() > remark_length) {
                                //print_remark = print_remark.substring(0,(remark_length-1));
                                //print_remark = print_remark + " ";
                                print_remark1 = print_remark1.substring((remark_length-1),print_remark1.length());
                                if(print_remark1.length()>remark_length){
                                    print_remark1 = print_remark1.substring(0,(remark_length-1));
                                    print_remark1 = print_remark1 + " ";
                                }
                                int w = remark_length-print_remark1.length();
                                for(int e=0;e<w;e++){
                                    print_remark1 = print_remark1 + " ";
                                }
                                //print_remark = print_remark + "#" + print_remark1;
                            }else if(print_remark.length()<=(remark_length-1)){
                                int q = remark_length-print_remark.length();
                                for(int j=0;j<q;j++){
                                    print_remark = print_remark + " ";
                                }
                            }
                            //kArea1print_list.add(data + "@" + print_remark + "!" + list.get(1));
                            String amnt = list.get(3);
                            String s[] = amnt.split("\\.");
                            if(s.length>1) {
                                kArea1print_list.add(data + "@" + list.get(1)+"  " + "!" + s[0]);
                            }else{
                                kArea1print_list.add(data + "@" + list.get(1)+"  " + "!" + amnt);
                            }
                        } else {
                            String data = list.get(0);
                            if (data.length() > total_length) {
                                data = data.substring(0,(total_length-1));
                                data = data + " ";
                            }else if(data.length() <= (total_length-1)){
                                int q = total_length-data.length();
                                for(int k=0;k<q;k++){
                                    data = data + " ";
                                }
                            }
                            //kArea1print_list.add(data + "!" + list.get(1));
                            String amnt = list.get(3);
                            String s[] = amnt.split("\\.");
                            if(s.length>1) {
                                kArea1print_list.add(data + "@" + list.get(1)+"  " + "!" + s[0]);
                            }else{
                                kArea1print_list.add(data + "@" + list.get(1)+"  " + "!" + amnt);
                            }
                        }
                    }
                }
            }
            else if (KArea2_list.contains(prod_id_track.get(i))) {
                if (!prev_id_qty_hash_map.isEmpty()) {
                    if (prev_id_qty_hash_map.containsKey(prod_id_track.get(i))) {
                        int a = prev_id_qty_hash_map.get(prod_id_track.get(i));
                        int b = Integer.parseInt(list.get(1));
                        if (a < b) {
                            if (remark_hash_map.containsKey(prod_id_track.get(i))) {
                                String print_remark = remark_hash_map.get(prod_id_track.get(i));
                                String print_remark1 = print_remark;
                                if (print_remark.length() != 0) {
                                    String data = list.get(0);
                                    String data1 = list.get(0);
                                    if (data.length() > item_length) {
                                        data = data.substring(0,(item_length-1));
                                        data = data + " ";
                                        data1 = data1.substring(item_length,data1.length());
                                        if(data1.length()>item_length){
                                            data1 = data1.substring(0,(item_length-1));
                                            data1 = data1 + " ";
                                        }
                                        int w = item_length-data1.length();
                                        for(int e=0;e<w;e++){
                                            data1 = data1 + " ";
                                        }
                                        data = data + "#" + data1;
                                    }else if(data.length()<=(item_length-1)){
                                        int q = item_length-data.length();
                                        for(int j=0;j<q;j++){
                                            data = data + " ";
                                        }
                                    }
                                    if (print_remark.length() > remark_length) {
                                        print_remark = print_remark.substring(0,(remark_length-1));
                                        print_remark = print_remark + " ";
                                        print_remark1 = print_remark1.substring(remark_length,print_remark1.length());
                                        if(print_remark1.length()>remark_length){
                                            print_remark1 = print_remark1.substring(0,(remark_length-1));
                                            print_remark1 = print_remark1 + " ";
                                        }
                                        int w = remark_length-print_remark1.length();
                                        for(int e=0;e<w;e++){
                                            print_remark1 = print_remark1 + " ";
                                        }
                                        print_remark = print_remark + "#" + print_remark1;
                                    }else if(print_remark.length() <= remark_length){
                                        int q = remark_length-print_remark.length();
                                        for(int j=0;j<q;j++){
                                            print_remark = print_remark + " ";
                                        }
                                    }
                                    kArea2print_list.add(data + "@" + print_remark + "!" + (b - a));
                                } else {
                                    String data = list.get(0);
                                    if (data.length() > total_length) {
                                        data = data.substring(0, (total_length-1));
                                        data = data + " ";
                                    }else if(data.length() <= (total_length-1)){
                                        int q = total_length-data.length();
                                        for(int k=0;k<q;k++){
                                            data = data + " ";
                                        }
                                    }
                                    kArea2print_list.add(data + "!" + (b - a));
                                }
                            }
                        }
                    } else {
                        if (remark_hash_map.containsKey(prod_id_track.get(i))) {
                            String print_remark = remark_hash_map.get(prod_id_track.get(i));
                            String print_remark1 = print_remark;
                            if (print_remark.length() != 0) {
                                String data = list.get(0);
                                String data1 = list.get(0);
                                if (data.length() > item_length) {
                                    data = data.substring(0, (item_length-1));
                                    data = data + " ";
                                    data1 = data1.substring((item_length-1),data1.length());
                                    if(data1.length()>item_length){
                                        data1 = data1.substring(0,(item_length-1));
                                        data1 = data1 + " ";
                                    }
                                    int w = item_length-data1.length();
                                    for(int e=0;e<w;e++){
                                        data1 = data1 + " ";
                                    }
                                    data = data + "#" + data1;
                                }else if(data.length()<=(item_length-1)){
                                    int q = item_length-data.length();
                                    for(int j=0;j<q;j++){
                                        data = data + " ";
                                    }
                                }
                                if (print_remark.length() > remark_length) {
                                    print_remark = print_remark.substring(0,(remark_length-1));
                                    print_remark = print_remark + " ";
                                    print_remark1 = print_remark1.substring((remark_length-1),print_remark1.length());
                                    if(print_remark1.length()>remark_length){
                                        print_remark1 = print_remark1.substring(0,(remark_length-1));
                                        print_remark1 = print_remark1 + " ";
                                    }
                                    int w = remark_length-print_remark1.length();
                                    for(int e=0;e<w;e++){
                                        print_remark1 = print_remark1 + " ";
                                    }
                                    print_remark = print_remark + "#" + print_remark1;
                                }else if(print_remark.length() <= (remark_length-1)){
                                    int q = remark_length-print_remark.length();
                                    for(int j=0;j<q;j++){
                                        print_remark = print_remark + " ";
                                    }
                                }
                                kArea2print_list.add(data + "@" + print_remark + "!" + list.get(1));
                            } else {
                                String data = list.get(0);
                                if (data.length() > total_length) {
                                    data = data.substring(0, (total_length-1));
                                    data = data + " ";
                                }else if(data.length() <= (total_length-1)){
                                    int q = total_length-data.length();
                                    for(int k=0;k<q;k++){
                                        data = data + " ";
                                    }
                                }
                                kArea2print_list.add(data + "!" + list.get(1));
                            }
                        }
                    }
                } else {
                    if (remark_hash_map.containsKey(prod_id_track.get(i))) {
                        String print_remark = remark_hash_map.get(prod_id_track.get(i));
                        String print_remark1 = print_remark;
                        if (print_remark.length() != 0) {
                            String data = list.get(0);
                            String data1 = list.get(0);
                            if (data.length() > item_length) {
                                data = data.substring(0, (item_length-1));
                                data = data + " ";
                                data1 = data1.substring((item_length-1),data1.length());
                                if(data1.length()>item_length){
                                    data1 = data1.substring(0,(item_length-1));
                                    data1 = data1 + " ";
                                }
                                int w = item_length-data1.length();
                                for(int e=0;e<w;e++){
                                    data1 = data1 + " ";
                                }
                                data = data + "#" + data1;
                            }else if(data.length()<=(item_length-1)){
                                int q = item_length-data.length();
                                for(int j=0;j<q;j++){
                                    data = data + " ";
                                }
                            }
                            if (print_remark.length() > remark_length) {
                                print_remark = print_remark.substring(0,(remark_length-1));
                                print_remark = print_remark + " ";
                                print_remark1 = print_remark1.substring((remark_length-1),print_remark1.length());
                                if(print_remark1.length()>remark_length){
                                    print_remark1 = print_remark1.substring(0,(remark_length-1));
                                    print_remark1 = print_remark1 + " ";
                                }
                                int w = remark_length-print_remark1.length();
                                for(int e=0;e<w;e++){
                                    print_remark1 = print_remark1 + " ";
                                }
                                print_remark = print_remark + "#" + print_remark1;
                            }else if(print_remark.length()<=(remark_length-1)){
                                int q = remark_length-print_remark.length();
                                for(int j=0;j<q;j++){
                                    print_remark = print_remark + " ";
                                }
                            }
                            kArea2print_list.add(data + "@" + print_remark + "!" + list.get(1));
                        } else {
                            String data = list.get(0);
                            if (data.length() > total_length) {
                                data = data.substring(0,(total_length-1));
                                data = data + " ";
                            }else if(data.length() <= (total_length-1)){
                                int q = total_length-data.length();
                                for(int k=0;k<q;k++){
                                    data = data + " ";
                                }
                            }
                            kArea2print_list.add(data + "!" + list.get(1));
                        }
                    }
                }
            }else if (KArea3_list.contains(prod_id_track.get(i))) {
                if (!prev_id_qty_hash_map.isEmpty()) {
                    if (prev_id_qty_hash_map.containsKey(prod_id_track.get(i))) {
                        int a = prev_id_qty_hash_map.get(prod_id_track.get(i));
                        int b = Integer.parseInt(list.get(1));
                        if (a < b) {
                            if (remark_hash_map.containsKey(prod_id_track.get(i))) {
                                String print_remark = remark_hash_map.get(prod_id_track.get(i));
                                String print_remark1 = print_remark;
                                if (print_remark.length() != 0) {
                                    String data = list.get(0);
                                    String data1 = list.get(0);
                                    if (data.length() > item_length) {
                                        data = data.substring(0,(item_length-1));
                                        data = data + " ";
                                        data1 = data1.substring(item_length,data1.length());
                                        if(data1.length()>item_length){
                                            data1 = data1.substring(0,(item_length-1));
                                            data1 = data1 + " ";
                                        }
                                        int w = item_length-data1.length();
                                        for(int e=0;e<w;e++){
                                            data1 = data1 + " ";
                                        }
                                        data = data + "#" + data1;
                                    }else if(data.length()<=(item_length-1)){
                                        int q = item_length-data.length();
                                        for(int j=0;j<q;j++){
                                            data = data + " ";
                                        }
                                    }
                                    if (print_remark.length() > remark_length) {
                                        print_remark = print_remark.substring(0,(remark_length-1));
                                        print_remark = print_remark + " ";
                                        print_remark1 = print_remark1.substring(remark_length,print_remark1.length());
                                        if(print_remark1.length()>remark_length){
                                            print_remark1 = print_remark1.substring(0,(remark_length-1));
                                            print_remark1 = print_remark1 + " ";
                                        }
                                        int w = remark_length-print_remark1.length();
                                        for(int e=0;e<w;e++){
                                            print_remark1 = print_remark1 + " ";
                                        }
                                        print_remark = print_remark + "#" + print_remark1;
                                    }else if(print_remark.length() <= remark_length){
                                        int q = remark_length-print_remark.length();
                                        for(int j=0;j<q;j++){
                                            print_remark = print_remark + " ";
                                        }
                                    }
                                    kArea3print_list.add(data + "@" + print_remark + "!" + (b - a));
                                } else {
                                    String data = list.get(0);
                                    if (data.length() > total_length) {
                                        data = data.substring(0, (total_length-1));
                                        data = data + " ";
                                    }else if(data.length() <= (total_length-1)){
                                        int q = total_length-data.length();
                                        for(int k=0;k<q;k++){
                                            data = data + " ";
                                        }
                                    }
                                    kArea3print_list.add(data + "!" + (b - a));
                                }
                            }
                        }
                    } else {
                        if (remark_hash_map.containsKey(prod_id_track.get(i))) {
                            String print_remark = remark_hash_map.get(prod_id_track.get(i));
                            String print_remark1 = print_remark;
                            if (print_remark.length() != 0) {
                                String data = list.get(0);
                                String data1 = list.get(0);
                                if (data.length() > item_length) {
                                    data = data.substring(0, (item_length-1));
                                    data = data + " ";
                                    data1 = data1.substring((item_length-1),data1.length());
                                    if(data1.length()>item_length){
                                        data1 = data1.substring(0,(item_length-1));
                                        data1 = data1 + " ";
                                    }
                                    int w = item_length-data1.length();
                                    for(int e=0;e<w;e++){
                                        data1 = data1 + " ";
                                    }
                                    data = data + "#" + data1;
                                }else if(data.length()<=(item_length-1)){
                                    int q = item_length-data.length();
                                    for(int j=0;j<q;j++){
                                        data = data + " ";
                                    }
                                }
                                if (print_remark.length() > remark_length) {
                                    print_remark = print_remark.substring(0,(remark_length-1));
                                    print_remark = print_remark + " ";
                                    print_remark1 = print_remark1.substring((remark_length-1),print_remark1.length());
                                    if(print_remark1.length()>remark_length){
                                        print_remark1 = print_remark1.substring(0,(remark_length-1));
                                        print_remark1 = print_remark1 + " ";
                                    }
                                    int w = remark_length-print_remark1.length();
                                    for(int e=0;e<w;e++){
                                        print_remark1 = print_remark1 + " ";
                                    }
                                    print_remark = print_remark + "#" + print_remark1;
                                }else if(print_remark.length() <= (remark_length-1)){
                                    int q = remark_length-print_remark.length();
                                    for(int j=0;j<q;j++){
                                        print_remark = print_remark + " ";
                                    }
                                }
                                kArea3print_list.add(data + "@" + print_remark + "!" + list.get(1));
                            } else {
                                String data = list.get(0);
                                if (data.length() > total_length) {
                                    data = data.substring(0, (total_length-1));
                                    data = data + " ";
                                }else if(data.length() <= (total_length-1)){
                                    int q = total_length-data.length();
                                    for(int k=0;k<q;k++){
                                        data = data + " ";
                                    }
                                }
                                kArea3print_list.add(data + "!" + list.get(1));
                            }
                        }
                    }
                } else {
                    if (remark_hash_map.containsKey(prod_id_track.get(i))) {
                        String print_remark = remark_hash_map.get(prod_id_track.get(i));
                        String print_remark1 = print_remark;
                        if (print_remark.length() != 0) {
                            String data = list.get(0);
                            String data1 = list.get(0);
                            if (data.length() > item_length) {
                                data = data.substring(0, (item_length-1));
                                data = data + " ";
                                data1 = data1.substring((item_length-1),data1.length());
                                if(data1.length()>item_length){
                                    data1 = data1.substring(0,(item_length-1));
                                    data1 = data1 + " ";
                                }
                                int w = item_length-data1.length();
                                for(int e=0;e<w;e++){
                                    data1 = data1 + " ";
                                }
                                data = data + "#" + data1;
                            }else if(data.length()<=(item_length-1)){
                                int q = item_length-data.length();
                                for(int j=0;j<q;j++){
                                    data = data + " ";
                                }
                            }
                            if (print_remark.length() > remark_length) {
                                print_remark = print_remark.substring(0,(remark_length-1));
                                print_remark = print_remark + " ";
                                print_remark1 = print_remark1.substring((remark_length-1),print_remark1.length());
                                if(print_remark1.length()>remark_length){
                                    print_remark1 = print_remark1.substring(0,(remark_length-1));
                                    print_remark1 = print_remark1 + " ";
                                }
                                int w = remark_length-print_remark1.length();
                                for(int e=0;e<w;e++){
                                    print_remark1 = print_remark1 + " ";
                                }
                                print_remark = print_remark + "#" + print_remark1;
                            }else if(print_remark.length()<=(remark_length-1)){
                                int q = remark_length-print_remark.length();
                                for(int j=0;j<q;j++){
                                    print_remark = print_remark + " ";
                                }
                            }
                            kArea3print_list.add(data + "@" + print_remark + "!" + list.get(1));
                        } else {
                            String data = list.get(0);
                            if (data.length() > total_length) {
                                data = data.substring(0,(total_length-1));
                                data = data + " ";
                            }else if(data.length() <= (total_length-1)){
                                int q = total_length-data.length();
                                for(int k=0;k<q;k++){
                                    data = data + " ";
                                }
                            }
                            kArea3print_list.add(data + "!" + list.get(1));
                        }
                    }
                }
            }
            str = str + prod_id_track.get(i) + "-" + list.get(1) + "-" + list.get(2) + "-" + list.get(4) + "-" + remark_str + "-" + list.get(5) + ",";// + "-" + list.get(6)) + ",";
            if(list.get(5).equals("N")){
                flag = 1;
            }
        }
        if(!str.equals("") && flag == 1) {
            str = str.substring(0, str.length() - 1);
            String kotno = db.saveTemptableData(table_id, 1, 1, str);
            if (!kotno.equals("0")) {
                checkedBeforeCM = 1;
                localPlaceOrder(kotno);
                localRefreshData(db.getLocalRefreshData(table_id));
                if (kArea1print_list.size() != 0) {
                    new Print1().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                }
                if (kArea2print_list.size() != 0) {
                    new Print2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                }
                if (kArea3print_list.size() != 0) {
                    new Print3().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                }
            } else {
                /*toast.setText("Error While Saving Order");
                toast.show();*/
                showSnackBar("Error While Saving Order");
            }
        }else{
            /*toast.setText("Please Take Order First");
            toast.show();*/
            showSnackBar("Please Take Order First");
        }
        /*try {
            str = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (kArea1print_list.size() != 0) {
            new Print1().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
        }
        if (ConnectivityTest.getNetStat(getActivity())) {
            String url = LoginActivity.ipaddress + "/SaveTempTableDetails?TableId=" + table_id + "&LocationId=1&CrBy=" + pref.getInt("id", 0) + "&Data=" + str + "&KOTNo=" + url_kotno;
            Log.d("PO URL", url);
            new placeOrder().execute(url);
        } else {
            Toast toast = Toast.makeText(getContext(),"Could Not Connect To Internet...",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }*/
    }

    private void placeOrderChnaged(){
        kArea1print_list.clear();
        kArea2print_list.clear();
        kArea3print_list.clear();
        remove_prod_hash_map.clear();
        String str = "", remark_str;

        int item_length = 25, remark_length = 0, total_length = 24;
        int flag = 0;
        for (int i = 0; i < prod_id_track.size(); i++) {
            if (remark_hash_map.size() != 0 && !remark_hash_map.isEmpty()) {
                remark_str = remark_hash_map.get(prod_id_track.get(i));
                if (remark_str.length() == 0) {
                    remark_str = " ";
                }
            } else {
                remark_str = " ";
            }
            List<String> list = order_prod_hash_map.get(prod_id_track.get(i));

            if (KArea1_list.contains(prod_id_track.get(i))) {
                if (!prev_id_qty_hash_map.isEmpty()) {
                    if (prev_id_qty_hash_map.containsKey(prod_id_track.get(i))) {
                        int a = prev_id_qty_hash_map.get(prod_id_track.get(i));
                        int b = Integer.parseInt(list.get(1));
                        if (a < b) {
                            if (remark_hash_map.containsKey(prod_id_track.get(i))) {
                                String print_remark = remark_hash_map.get(prod_id_track.get(i));
                                String print_remark1 = print_remark;
                                if (print_remark.length() != 0) {
                                    String data = list.get(0);
                                    String data1 = list.get(0);
                                    if (data.length() > item_length) {
                                        data = data.substring(0, (item_length - 1));
                                        data = data + " ";
                                        data1 = data1.substring(item_length, data1.length());
                                        if (data1.length() > item_length) {
                                            data1 = data1.substring(0, (item_length - 1));
                                            data1 = data1 + " ";
                                        }
                                        int w = item_length - data1.length();
                                        for (int e = 0; e < w; e++) {
                                            data1 = data1 + " ";
                                        }
                                        data = data + "#" + data1;
                                    } else if (data.length() <= (item_length - 1)) {
                                        int q = item_length - data.length();
                                        for (int j = 0; j < q; j++) {
                                            data = data + " ";
                                        }
                                    }
                                    if (print_remark.length() > remark_length) {
                                        print_remark = print_remark.substring(0, (remark_length - 1));
                                        print_remark = print_remark + " ";
                                        print_remark1 = print_remark1.substring(remark_length, print_remark1.length());
                                        if (print_remark1.length() > remark_length) {
                                            print_remark1 = print_remark1.substring(0, (remark_length - 1));
                                            print_remark1 = print_remark1 + " ";
                                        }
                                        int w = remark_length - print_remark1.length();
                                        for (int e = 0; e < w; e++) {
                                            print_remark1 = print_remark1 + " ";
                                        }
                                        print_remark = print_remark + "#" + print_remark1;
                                    } else if (print_remark.length() <= remark_length) {
                                        int q = remark_length - print_remark.length();
                                        for (int j = 0; j < q; j++) {
                                            print_remark = print_remark + " ";
                                        }
                                    }
                                    kArea1print_list.add(data + "@" + print_remark + "!" + (b - a));
                                } else {
                                    String data = list.get(0);
                                    if (data.length() > total_length) {
                                        data = data.substring(0, (total_length - 1));
                                        data = data + " ";
                                    } else if (data.length() <= (total_length - 1)) {
                                        int q = total_length - data.length();
                                        for (int k = 0; k < q; k++) {
                                            data = data + " ";
                                        }
                                    }
                                    kArea1print_list.add(data + "!" + (b - a));
                                }
                            }
                        }
                    } else {
                        if (remark_hash_map.containsKey(prod_id_track.get(i))) {
                            String print_remark = remark_hash_map.get(prod_id_track.get(i));
                            String print_remark1 = print_remark;
                            if (print_remark.length() != 0) {
                                String data = list.get(0);
                                String data1 = list.get(0);
                                if (data.length() > item_length) {
                                    data = data.substring(0, (item_length - 1));
                                    data = data + " ";
                                    data1 = data1.substring((item_length - 1), data1.length());
                                    if (data1.length() > item_length) {
                                        data1 = data1.substring(0, (item_length - 1));
                                        data1 = data1 + " ";
                                    }
                                    int w = item_length - data1.length();
                                    for (int e = 0; e < w; e++) {
                                        data1 = data1 + " ";
                                    }
                                    data = data + "#" + data1;
                                } else if (data.length() <= (item_length - 1)) {
                                    int q = item_length - data.length();
                                    for (int j = 0; j < q; j++) {
                                        data = data + " ";
                                    }
                                }
                                if (print_remark.length() > remark_length) {
                                    print_remark = print_remark.substring(0, (remark_length - 1));
                                    print_remark = print_remark + " ";
                                    print_remark1 = print_remark1.substring((remark_length - 1), print_remark1.length());
                                    if (print_remark1.length() > remark_length) {
                                        print_remark1 = print_remark1.substring(0, (remark_length - 1));
                                        print_remark1 = print_remark1 + " ";
                                    }
                                    int w = remark_length - print_remark1.length();
                                    for (int e = 0; e < w; e++) {
                                        print_remark1 = print_remark1 + " ";
                                    }
                                    print_remark = print_remark + "#" + print_remark1;
                                } else if (print_remark.length() <= (remark_length - 1)) {
                                    int q = remark_length - print_remark.length();
                                    for (int j = 0; j < q; j++) {
                                        print_remark = print_remark + " ";
                                    }
                                }
                                kArea1print_list.add(data + "@" + print_remark + "!" + list.get(1));
                            } else {
                                String data = list.get(0);
                                if (data.length() > total_length) {
                                    data = data.substring(0, (total_length - 1));
                                    data = data + " ";
                                } else if (data.length() <= (total_length - 1)) {
                                    int q = total_length - data.length();
                                    for (int k = 0; k < q; k++) {
                                        data = data + " ";
                                    }
                                }
                                kArea1print_list.add(data + "!" + list.get(1));
                            }
                        }
                    }
                } else {
                    if (remark_hash_map.containsKey(prod_id_track.get(i))) {
                        String print_remark = remark_hash_map.get(prod_id_track.get(i));
                        String print_remark1 = print_remark;
                        if (print_remark.length() != 0) {
                            String data = list.get(0);
                            String data1 = list.get(0);
                            if (data.length() > item_length) {
                                data = data.substring(0, (item_length - 1));
                                data = data + " ";
                                data1 = data1.substring((item_length - 1), data1.length());
                                if (data1.length() > item_length) {
                                    data1 = data1.substring(0, (item_length - 1));
                                    data1 = data1 + " ";
                                }
                                int w = item_length - data1.length();
                                for (int e = 0; e < w; e++) {
                                    data1 = data1 + " ";
                                }
                                data = data + "#" + data1;
                            } else if (data.length() <= (item_length - 1)) {
                                int q = item_length - data.length();
                                for (int j = 0; j < q; j++) {
                                    data = data + " ";
                                }
                            }
                            if (print_remark.length() > remark_length) {
                                print_remark = print_remark.substring(0, (remark_length - 1));
                                print_remark = print_remark + " ";
                                print_remark1 = print_remark1.substring((remark_length - 1), print_remark1.length());
                                if (print_remark1.length() > remark_length) {
                                    print_remark1 = print_remark1.substring(0, (remark_length - 1));
                                    print_remark1 = print_remark1 + " ";
                                }
                                int w = remark_length - print_remark1.length();
                                for (int e = 0; e < w; e++) {
                                    print_remark1 = print_remark1 + " ";
                                }
                                print_remark = print_remark + "#" + print_remark1;
                            } else if (print_remark.length() <= (remark_length - 1)) {
                                int q = remark_length - print_remark.length();
                                for (int j = 0; j < q; j++) {
                                    print_remark = print_remark + " ";
                                }
                            }
                            kArea1print_list.add(data + "@" + print_remark + "!" + list.get(1));
                        } else {
                            String data = list.get(0);
                            if (data.length() > total_length) {
                                data = data.substring(0, (total_length - 1));
                                data = data + " ";
                            } else if (data.length() <= (total_length - 1)) {
                                int q = total_length - data.length();
                                for (int k = 0; k < q; k++) {
                                    data = data + " ";
                                }
                            }
                            kArea1print_list.add(data + "!" + list.get(1));
                        }
                    }
                }
            } else if (KArea2_list.contains(prod_id_track.get(i))) {
                if (!prev_id_qty_hash_map.isEmpty()) {
                    if (prev_id_qty_hash_map.containsKey(prod_id_track.get(i))) {
                        int a = prev_id_qty_hash_map.get(prod_id_track.get(i));
                        int b = Integer.parseInt(list.get(1));
                        if (a < b) {
                            if (remark_hash_map.containsKey(prod_id_track.get(i))) {
                                String print_remark = remark_hash_map.get(prod_id_track.get(i));
                                String print_remark1 = print_remark;
                                if (print_remark.length() != 0) {
                                    String data = list.get(0);
                                    String data1 = list.get(0);
                                    if (data.length() > item_length) {
                                        data = data.substring(0, (item_length - 1));
                                        data = data + " ";
                                        data1 = data1.substring(item_length, data1.length());
                                        if (data1.length() > item_length) {
                                            data1 = data1.substring(0, (item_length - 1));
                                            data1 = data1 + " ";
                                        }
                                        int w = item_length - data1.length();
                                        for (int e = 0; e < w; e++) {
                                            data1 = data1 + " ";
                                        }
                                        data = data + "#" + data1;
                                    } else if (data.length() <= (item_length - 1)) {
                                        int q = item_length - data.length();
                                        for (int j = 0; j < q; j++) {
                                            data = data + " ";
                                        }
                                    }
                                    if (print_remark.length() > remark_length) {
                                        print_remark = print_remark.substring(0, (remark_length - 1));
                                        print_remark = print_remark + " ";
                                        print_remark1 = print_remark1.substring(remark_length, print_remark1.length());
                                        if (print_remark1.length() > remark_length) {
                                            print_remark1 = print_remark1.substring(0, (remark_length - 1));
                                            print_remark1 = print_remark1 + " ";
                                        }
                                        int w = remark_length - print_remark1.length();
                                        for (int e = 0; e < w; e++) {
                                            print_remark1 = print_remark1 + " ";
                                        }
                                        print_remark = print_remark + "#" + print_remark1;
                                    } else if (print_remark.length() <= remark_length) {
                                        int q = remark_length - print_remark.length();
                                        for (int j = 0; j < q; j++) {
                                            print_remark = print_remark + " ";
                                        }
                                    }
                                    kArea2print_list.add(data + "@" + print_remark + "!" + (b - a));
                                } else {
                                    String data = list.get(0);
                                    if (data.length() > total_length) {
                                        data = data.substring(0, (total_length - 1));
                                        data = data + " ";
                                    } else if (data.length() <= (total_length - 1)) {
                                        int q = total_length - data.length();
                                        for (int k = 0; k < q; k++) {
                                            data = data + " ";
                                        }
                                    }
                                    kArea2print_list.add(data + "!" + (b - a));
                                }
                            }
                        }
                    } else {
                        if (remark_hash_map.containsKey(prod_id_track.get(i))) {
                            String print_remark = remark_hash_map.get(prod_id_track.get(i));
                            String print_remark1 = print_remark;
                            if (print_remark.length() != 0) {
                                String data = list.get(0);
                                String data1 = list.get(0);
                                if (data.length() > item_length) {
                                    data = data.substring(0, (item_length - 1));
                                    data = data + " ";
                                    data1 = data1.substring((item_length - 1), data1.length());
                                    if (data1.length() > item_length) {
                                        data1 = data1.substring(0, (item_length - 1));
                                        data1 = data1 + " ";
                                    }
                                    int w = item_length - data1.length();
                                    for (int e = 0; e < w; e++) {
                                        data1 = data1 + " ";
                                    }
                                    data = data + "#" + data1;
                                } else if (data.length() <= (item_length - 1)) {
                                    int q = item_length - data.length();
                                    for (int j = 0; j < q; j++) {
                                        data = data + " ";
                                    }
                                }
                                if (print_remark.length() > remark_length) {
                                    print_remark = print_remark.substring(0, (remark_length - 1));
                                    print_remark = print_remark + " ";
                                    print_remark1 = print_remark1.substring((remark_length - 1), print_remark1.length());
                                    if (print_remark1.length() > remark_length) {
                                        print_remark1 = print_remark1.substring(0, (remark_length - 1));
                                        print_remark1 = print_remark1 + " ";
                                    }
                                    int w = remark_length - print_remark1.length();
                                    for (int e = 0; e < w; e++) {
                                        print_remark1 = print_remark1 + " ";
                                    }
                                    print_remark = print_remark + "#" + print_remark1;
                                } else if (print_remark.length() <= (remark_length - 1)) {
                                    int q = remark_length - print_remark.length();
                                    for (int j = 0; j < q; j++) {
                                        print_remark = print_remark + " ";
                                    }
                                }
                                kArea2print_list.add(data + "@" + print_remark + "!" + list.get(1));
                            } else {
                                String data = list.get(0);
                                if (data.length() > total_length) {
                                    data = data.substring(0, (total_length - 1));
                                    data = data + " ";
                                } else if (data.length() <= (total_length - 1)) {
                                    int q = total_length - data.length();
                                    for (int k = 0; k < q; k++) {
                                        data = data + " ";
                                    }
                                }
                                kArea2print_list.add(data + "!" + list.get(1));
                            }
                        }
                    }
                } else {
                    if (remark_hash_map.containsKey(prod_id_track.get(i))) {
                        String print_remark = remark_hash_map.get(prod_id_track.get(i));
                        String print_remark1 = print_remark;
                        if (print_remark.length() != 0) {
                            String data = list.get(0);
                            String data1 = list.get(0);
                            if (data.length() > item_length) {
                                data = data.substring(0, (item_length - 1));
                                data = data + " ";
                                data1 = data1.substring((item_length - 1), data1.length());
                                if (data1.length() > item_length) {
                                    data1 = data1.substring(0, (item_length - 1));
                                    data1 = data1 + " ";
                                }
                                int w = item_length - data1.length();
                                for (int e = 0; e < w; e++) {
                                    data1 = data1 + " ";
                                }
                                data = data + "#" + data1;
                            } else if (data.length() <= (item_length - 1)) {
                                int q = item_length - data.length();
                                for (int j = 0; j < q; j++) {
                                    data = data + " ";
                                }
                            }
                            if (print_remark.length() > remark_length) {
                                print_remark = print_remark.substring(0, (remark_length - 1));
                                print_remark = print_remark + " ";
                                print_remark1 = print_remark1.substring((remark_length - 1), print_remark1.length());
                                if (print_remark1.length() > remark_length) {
                                    print_remark1 = print_remark1.substring(0, (remark_length - 1));
                                    print_remark1 = print_remark1 + " ";
                                }
                                int w = remark_length - print_remark1.length();
                                for (int e = 0; e < w; e++) {
                                    print_remark1 = print_remark1 + " ";
                                }
                                print_remark = print_remark + "#" + print_remark1;
                            } else if (print_remark.length() <= (remark_length - 1)) {
                                int q = remark_length - print_remark.length();
                                for (int j = 0; j < q; j++) {
                                    print_remark = print_remark + " ";
                                }
                            }
                            kArea2print_list.add(data + "@" + print_remark + "!" + list.get(1));
                        } else {
                            String data = list.get(0);
                            if (data.length() > total_length) {
                                data = data.substring(0, (total_length - 1));
                                data = data + " ";
                            } else if (data.length() <= (total_length - 1)) {
                                int q = total_length - data.length();
                                for (int k = 0; k < q; k++) {
                                    data = data + " ";
                                }
                            }
                            kArea2print_list.add(data + "!" + list.get(1));
                        }
                    }
                }
            } else if (KArea3_list.contains(prod_id_track.get(i))) {
                if (!prev_id_qty_hash_map.isEmpty()) {
                    if (prev_id_qty_hash_map.containsKey(prod_id_track.get(i))) {
                        int a = prev_id_qty_hash_map.get(prod_id_track.get(i));
                        int b = Integer.parseInt(list.get(1));
                        if (a < b) {
                            if (remark_hash_map.containsKey(prod_id_track.get(i))) {
                                String print_remark = remark_hash_map.get(prod_id_track.get(i));
                                String print_remark1 = print_remark;
                                if (print_remark.length() != 0) {
                                    String data = list.get(0);
                                    String data1 = list.get(0);
                                    if (data.length() > item_length) {
                                        data = data.substring(0, (item_length - 1));
                                        data = data + " ";
                                        data1 = data1.substring(item_length, data1.length());
                                        if (data1.length() > item_length) {
                                            data1 = data1.substring(0, (item_length - 1));
                                            data1 = data1 + " ";
                                        }
                                        int w = item_length - data1.length();
                                        for (int e = 0; e < w; e++) {
                                            data1 = data1 + " ";
                                        }
                                        data = data + "#" + data1;
                                    } else if (data.length() <= (item_length - 1)) {
                                        int q = item_length - data.length();
                                        for (int j = 0; j < q; j++) {
                                            data = data + " ";
                                        }
                                    }
                                    if (print_remark.length() > remark_length) {
                                        print_remark = print_remark.substring(0, (remark_length - 1));
                                        print_remark = print_remark + " ";
                                        print_remark1 = print_remark1.substring(remark_length, print_remark1.length());
                                        if (print_remark1.length() > remark_length) {
                                            print_remark1 = print_remark1.substring(0, (remark_length - 1));
                                            print_remark1 = print_remark1 + " ";
                                        }
                                        int w = remark_length - print_remark1.length();
                                        for (int e = 0; e < w; e++) {
                                            print_remark1 = print_remark1 + " ";
                                        }
                                        print_remark = print_remark + "#" + print_remark1;
                                    } else if (print_remark.length() <= remark_length) {
                                        int q = remark_length - print_remark.length();
                                        for (int j = 0; j < q; j++) {
                                            print_remark = print_remark + " ";
                                        }
                                    }
                                    kArea3print_list.add(data + "@" + print_remark + "!" + (b - a));
                                } else {
                                    String data = list.get(0);
                                    if (data.length() > total_length) {
                                        data = data.substring(0, (total_length - 1));
                                        data = data + " ";
                                    } else if (data.length() <= (total_length - 1)) {
                                        int q = total_length - data.length();
                                        for (int k = 0; k < q; k++) {
                                            data = data + " ";
                                        }
                                    }
                                    kArea3print_list.add(data + "!" + (b - a));
                                }
                            }
                        }
                    } else {
                        if (remark_hash_map.containsKey(prod_id_track.get(i))) {
                            String print_remark = remark_hash_map.get(prod_id_track.get(i));
                            String print_remark1 = print_remark;
                            if (print_remark.length() != 0) {
                                String data = list.get(0);
                                String data1 = list.get(0);
                                if (data.length() > item_length) {
                                    data = data.substring(0, (item_length - 1));
                                    data = data + " ";
                                    data1 = data1.substring((item_length - 1), data1.length());
                                    if (data1.length() > item_length) {
                                        data1 = data1.substring(0, (item_length - 1));
                                        data1 = data1 + " ";
                                    }
                                    int w = item_length - data1.length();
                                    for (int e = 0; e < w; e++) {
                                        data1 = data1 + " ";
                                    }
                                    data = data + "#" + data1;
                                } else if (data.length() <= (item_length - 1)) {
                                    int q = item_length - data.length();
                                    for (int j = 0; j < q; j++) {
                                        data = data + " ";
                                    }
                                }
                                if (print_remark.length() > remark_length) {
                                    print_remark = print_remark.substring(0, (remark_length - 1));
                                    print_remark = print_remark + " ";
                                    print_remark1 = print_remark1.substring((remark_length - 1), print_remark1.length());
                                    if (print_remark1.length() > remark_length) {
                                        print_remark1 = print_remark1.substring(0, (remark_length - 1));
                                        print_remark1 = print_remark1 + " ";
                                    }
                                    int w = remark_length - print_remark1.length();
                                    for (int e = 0; e < w; e++) {
                                        print_remark1 = print_remark1 + " ";
                                    }
                                    print_remark = print_remark + "#" + print_remark1;
                                } else if (print_remark.length() <= (remark_length - 1)) {
                                    int q = remark_length - print_remark.length();
                                    for (int j = 0; j < q; j++) {
                                        print_remark = print_remark + " ";
                                    }
                                }
                                kArea3print_list.add(data + "@" + print_remark + "!" + list.get(1));
                            } else {
                                String data = list.get(0);
                                if (data.length() > total_length) {
                                    data = data.substring(0, (total_length - 1));
                                    data = data + " ";
                                } else if (data.length() <= (total_length - 1)) {
                                    int q = total_length - data.length();
                                    for (int k = 0; k < q; k++) {
                                        data = data + " ";
                                    }
                                }
                                kArea3print_list.add(data + "!" + list.get(1));
                            }
                        }
                    }
                } else {
                    if (remark_hash_map.containsKey(prod_id_track.get(i))) {
                        String print_remark = remark_hash_map.get(prod_id_track.get(i));
                        String print_remark1 = print_remark;
                        if (print_remark.length() != 0) {
                            String data = list.get(0);
                            String data1 = list.get(0);
                            if (data.length() > item_length) {
                                data = data.substring(0, (item_length - 1));
                                data = data + " ";
                                data1 = data1.substring((item_length - 1), data1.length());
                                if (data1.length() > item_length) {
                                    data1 = data1.substring(0, (item_length - 1));
                                    data1 = data1 + " ";
                                }
                                int w = item_length - data1.length();
                                for (int e = 0; e < w; e++) {
                                    data1 = data1 + " ";
                                }
                                data = data + "#" + data1;
                            } else if (data.length() <= (item_length - 1)) {
                                int q = item_length - data.length();
                                for (int j = 0; j < q; j++) {
                                    data = data + " ";
                                }
                            }
                            if (print_remark.length() > remark_length) {
                                print_remark = print_remark.substring(0, (remark_length - 1));
                                print_remark = print_remark + " ";
                                print_remark1 = print_remark1.substring((remark_length - 1), print_remark1.length());
                                if (print_remark1.length() > remark_length) {
                                    print_remark1 = print_remark1.substring(0, (remark_length - 1));
                                    print_remark1 = print_remark1 + " ";
                                }
                                int w = remark_length - print_remark1.length();
                                for (int e = 0; e < w; e++) {
                                    print_remark1 = print_remark1 + " ";
                                }
                                print_remark = print_remark + "#" + print_remark1;
                            } else if (print_remark.length() <= (remark_length - 1)) {
                                int q = remark_length - print_remark.length();
                                for (int j = 0; j < q; j++) {
                                    print_remark = print_remark + " ";
                                }
                            }
                            kArea3print_list.add(data + "@" + print_remark + "!" + list.get(1));
                        } else {
                            String data = list.get(0);
                            if (data.length() > total_length) {
                                data = data.substring(0, (total_length - 1));
                                data = data + " ";
                            } else if (data.length() <= (total_length - 1)) {
                                int q = total_length - data.length();
                                for (int k = 0; k < q; k++) {
                                    data = data + " ";
                                }
                            }
                            kArea3print_list.add(data + "!" + list.get(1));
                        }
                    }
                }
            }
            str = str + prod_id_track.get(i) + "-" + list.get(1) + "-" + list.get(2) + "-" + list.get(4) + "-" + remark_str + "-" + list.get(5) + ",";// + "-" + list.get(6)) + ",";
            if(list.get(5).equals("N")){
                flag = 1;
            }
        }
        if(!str.equals("") && flag == 1) {
            str = str.substring(0, str.length() - 1);
            String kotno = db.saveTemptableData(table_id, 1, 1, str);
            if (!kotno.equals("0")) {
                checkedBeforeCM = 1;
                localPlaceOrder(kotno);
                localRefreshData(db.getLocalRefreshData(table_id));
                if (kArea1print_list.size() != 0) {
                    new Print1().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                }
                if (kArea2print_list.size() != 0) {
                    new Print2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                }
                if (kArea3print_list.size() != 0) {
                    new Print3().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                }
            } else {
                /*toast.setText("Error While Saving Order");
                toast.show();*/
                showSnackBar("Error While Saving Order");
            }
        }else{
            /*toast.setText("Please Take Order First");
            toast.show();*/
            showSnackBar("Please Take Order First");
        }

        /*try {
            str = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (kArea1print_list.size() != 0) {
            new Print1().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
        }
        if (ConnectivityTest.getNetStat(getActivity())) {
            String url = LoginActivity.ipaddress + "/SaveTempTableDetails?TableId=" + table_id + "&LocationId=1&CrBy=" + pref.getInt("id", 0) + "&Data=" + str + "&KOTNo=" + url_kotno;
            Log.d("PO URL", url);
            new placeOrder().execute(url);
        } else {
            Toast toast = Toast.makeText(getContext(),"Could Not Connect To Internet...",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }*/
    }

    /**
     * Offline
     */
    private void localPlaceOrder(String str) {
        if (!str.equals("0")) {
            String _kotno;
            kotno = str;
            _kotno = str;
            _kotno = _kotno.replace("\"", "");
            kotno = kotno.replace("\"", "");
            if (!_kotno.equals("1")) {
                url_kotno = _kotno;
            }
            /*toast.setText("Order Place Successfully...");
            toast.show();*/
            showSnackBar("Order Place Successfully...");
            counter = -1;
            total_qty_counter = 0;
            total_qty.setText("0");
            total_amnt.setText("0");
            order_hash_map.clear();
            order_prod_hash_map.clear();
            remark_hash_map.clear();
            prod_id_track.clear();
            prod_view_map.clear();
            prev_id_qty_hash_map.clear();
            prodid_qty_hash_map.clear();
            ((LinearLayout) view.findViewById(R.id.order_container)).removeAllViews();
            if (!occupy_table_list.isEmpty()) {
                if (!occupy_table_list.contains(table_id)) {
                    occupy_table_list.add(table_id);
                }
            } else {
                occupy_table_list.add(table_id);
            }
            setTable();
            /*toast.setText("Order Placed Successfully");
            toast.show();*/
        } else if (str.equals("0")) {
            url_kotno = "0";
            /*toast.setText("Could Not Placed Order");
            toast.show();
            toast.setText("Please Do Refresh");
            toast.show();*/
            showSnackBar("Can't Placed Order");
        }
    }

    private void localRefreshData(Cursor res){
        if(res.moveToFirst()){
            ((LinearLayout) view.findViewById(R.id.order_container)).removeAllViews();
            prodid_qty_hash_map.clear();
            prodid_qty_list.clear();
            prev_id_qty_hash_map.clear();
            do {
                int itemID = res.getInt(4);
                String _kotno = res.getString(13);
                int qty = res.getInt(5);
                String rate = res.getString(6);
                String barcode = res.getString(10);

                int j;
                j = itemID;
                kotno = _kotno;
                kotno = kotno.replace("\"", "");
                List<String> list = new ArrayList<>();
                list.add(web_prod_hash_map.get(itemID));
                prev_id_qty_hash_map.put(itemID, qty);
                prodid_qty_hash_map.put(j, qty);
                prodid_qty_list.add(j);
                list.add(String.valueOf(qty));
                list.add(rate);
                double d = Double.parseDouble(rate);
                list.add(String.valueOf((qty * d)));
                list.add(barcode);
                list.add("O");
                if (order_hash_map.containsKey(table_id)) {
                    List<Integer> list1 = order_hash_map.get(table_id);
                    if (!list1.contains(itemID)) {
                        list1.add(itemID);
                        order_hash_map.put(table_id, list1);
                        order_repeat_flag = 3;
                        counter++;
                    } else {
                        order_repeat_flag = 3;
                    }
                } else {
                    List<Integer> list1 = new ArrayList<>();
                    list1.add(itemID);
                    order_hash_map.put(table_id, list1);
                    counter++;
                    order_repeat_flag = 3;
                }
                total_qty_counter++;
                addOrderItem(list, j);
            }while (res.moveToNext());
            res.close();
            total_qty.setText(String.valueOf(total_qty_counter));
            /*if (ConnectivityTest.getNetStat(getActivity())) {
                if (kArea1print_list.size() != 0) {
                    new Print1().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                }
                if (kArea2print_list.size() != 0) {
                    //new Print2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                }
                if (kArea3print_list.size() != 0) {
                    //new Print3().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                }
            } else {
                toast.setText("Could Not Print Order Network Connection Error");
                toast.show();
            }*/
        } else{
            /*toast.setText("Please Take Login Again RefreshData");
            toast.show();*/
            showSnackBar("Please Take Login Again RefreshData");
            order_repeat_flag = 0;
            counter = -1;
            total_qty_counter = 0;
            total_qty.setText("0");
            total_amnt.setText("0");
            order_hash_map.clear();
            order_prod_hash_map.clear();
            prev_id_qty_hash_map.clear();
            prod_id_track.clear();
            prod_view_map.clear();
        }
    }

    private void getLocalTableData(Cursor res){
        if(res.moveToFirst()){
            ((LinearLayout) view.findViewById(R.id.order_container)).removeAllViews();
            prodid_qty_hash_map.clear();
            prodid_qty_list.clear();
            prev_id_qty_hash_map.clear();
            do {
                int itemID = res.getInt(4);
                String _kotno = res.getString(13);
                int qty = res.getInt(5);
                String rate = res.getString(6);
                String barcode = res.getString(10);
                int j;
                j = itemID;
                kotno = _kotno;
                kotno = kotno.replace("\"", "");
                List<String> list = new ArrayList<>();
                list.add(web_prod_hash_map.get(itemID));
                prev_id_qty_hash_map.put(itemID, qty);
                prodid_qty_hash_map.put(j, qty);
                prodid_qty_list.add(j);
                list.add(String.valueOf(qty));
                list.add(rate);
                double d = Double.parseDouble(rate);
                list.add(String.valueOf((qty * d)));
                list.add(barcode);
                list.add("O");
                if (order_hash_map.containsKey(table_id)) {
                    List<Integer> list1 = order_hash_map.get(table_id);
                    if (!list1.contains(itemID)) {
                        list1.add(itemID);
                        order_hash_map.put(table_id, list1);
                        order_repeat_flag = 3;
                        counter++;
                    } else {
                        order_repeat_flag = 3;
                    }
                } else {
                    List<Integer> list1 = new ArrayList<>();
                    list1.add(itemID);
                    order_hash_map.put(table_id, list1);
                    counter++;
                    order_repeat_flag = 3;
                }
                total_qty_counter++;
                addOrderItem(list, j);
            }while (res.moveToNext());
            res.close();
            total_qty.setText(String.valueOf(total_qty_counter));
        } else{
            /*toast.setText("Please Take Login Again TableData");
            toast.show();*/
            order_repeat_flag = 0;
            counter = -1;
            total_qty_counter = 0;
            total_qty.setText("0");
            total_amnt.setText("0");
            order_hash_map.clear();
            order_prod_hash_map.clear();
            prev_id_qty_hash_map.clear();
            prod_id_track.clear();
            prod_view_map.clear();
        }
    }

    private void getBookedTableDataLocal(){
        occupy_table_list.clear();
        Cursor res = db.getBookedTableDataLocal();
        if (res.moveToFirst()) {
            do {
                int x = res.getInt(0);
                if (x != 0) {
                    occupy_table_list.add(x);
                }
            }while (res.moveToNext());
        }
        res.close();
        setTable();
    }

    //------------------------------------------------------------------------------------

    /***
     *  Online
     */
    private class GetTableBooked extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Refreshing Data");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... url) {
            return Post.POST(url[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null && result.length() != 0) {
                try {
                    occupy_table_list.clear();
                    result = result.replace("\\", "");
                    result = result.replace("''", "");
                    result = result.substring(1, result.length() - 1);
                    JSONArray jsonArray = new JSONArray(result);
                    if (jsonArray.length() >= 1) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            int x = jsonArray.getJSONObject(i).getInt("TableID");
                            if(x!=0) {
                                occupy_table_list.add(jsonArray.getJSONObject(i).getInt("TableID"));
                                //Log.d("TableBooked",""+occupy_table_list.get(i));
                            }
                        }
                    }
                    setTable();
                } catch (Exception e) {
                    e.printStackTrace();
                    pd.dismiss();
                }
            } else {
                /*toast.setText("Could Not Retrieve Data");
                toast.show();*/
                showSnackBar("Could Not Retrieve Data");
                pd.dismiss();
            }
            pd.dismiss();
        }
    }

    private class getTableData extends AsyncTask<String, Void, String> {

        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Getting Table Data");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... url) {
            return Post.POST(url[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null && result.length() != 0) {
                try {
                    result = result.replace("\\", "");
                    result = result.replace("''", "");
                    result = result.substring(1, result.length() - 1);
                    JSONArray jsonArray = new JSONArray(result);
                    if (jsonArray.length() >= 1) {
                        prodid_qty_hash_map.clear();
                        prodid_qty_list.clear();
                        prev_id_qty_hash_map.clear();
                        ((LinearLayout) view.findViewById(R.id.order_container)).removeAllViews();
                        int j;
                        String _kotno;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            j = jsonArray.getJSONObject(i).getInt("ItemID");
                            kotno = jsonArray.getJSONObject(i).getString("KOTNO");
                            kotno = kotno.replace("\"", "");
                            _kotno = jsonArray.getJSONObject(i).getString("KOTNO");
                            _kotno = _kotno.replace("\"", "");
                            url_kotno = _kotno;
                            List<String> list = new ArrayList<>();
                            list.add(web_prod_hash_map.get(jsonArray.getJSONObject(i).getInt("ItemID")));
                            Double d1 = jsonArray.getJSONObject(i).getDouble("Qty");
                            int qty = d1.intValue();
                            prev_id_qty_hash_map.put(jsonArray.getJSONObject(i).getInt("ItemID"), qty);
                            prodid_qty_hash_map.put(j, qty);
                            prodid_qty_list.add(j);
                            list.add(String.valueOf(qty));
                            list.add(jsonArray.getJSONObject(i).get("Rate").toString());
                            double d = Double.parseDouble(jsonArray.getJSONObject(i).get("Rate").toString());
                            list.add(String.valueOf((qty * d)));
                            list.add(jsonArray.getJSONObject(i).get("Barcode").toString());
                            list.add("O");
                            if (order_hash_map.containsKey(table_id)) {
                                List<Integer> list1 = order_hash_map.get(table_id);
                                if (!list1.contains(jsonArray.getJSONObject(i).getInt("ItemID"))) {
                                    list1.add(jsonArray.getJSONObject(i).getInt("ItemID"));
                                    order_hash_map.put(table_id, list1);
                                    order_repeat_flag = 3;
                                    counter++;
                                } else {
                                    order_repeat_flag = 3;
                                }
                            } else {
                                List<Integer> list1 = new ArrayList<>();
                                list1.add(jsonArray.getJSONObject(i).getInt("ItemID"));
                                order_hash_map.put(table_id, list1);
                                counter++;
                                order_repeat_flag = 3;
                            }
                            total_qty_counter++;
                            addOrderItem(list, j);
                        }total_qty.setText(String.valueOf(total_qty_counter));
                        pd.dismiss();
                    } else if (jsonArray.length() == 0) {
                        order_repeat_flag = 0;
                        counter = -1;
                        url_kotno = "0";
                        total_qty_counter = 0;
                        total_qty.setText("0");
                        total_amnt.setText("0");
                        order_hash_map.clear();
                        order_prod_hash_map.clear();
                        prod_id_track.clear();
                        prod_view_map.clear();
                    }
                    pd.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    pd.dismiss();
                }
            }
            pd.dismiss();
        }
    }

    private class placeOrder extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Placing The Order");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... url) {
            return Post.POST(url[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null && result.length() != 0) {
                try {
                    JSONObject Jobject = new JSONObject(result);
                    String str = Jobject.getString("SaveTempTableDetailsResult");
                    if (!str.equals("\"0\"")) {
                        String _kotno;
                        kotno = str;
                        _kotno = str;
                        _kotno = _kotno.replace("\"", "");
                        kotno = kotno.replace("\"", "");
                        if(!_kotno.equals("1")) {
                            url_kotno = _kotno;
                        }
                        toast.setText("Order Place Successfully...");
                        toast.show();
                        counter = -1;
                        total_qty_counter = 0;
                        total_qty.setText("0");
                        total_amnt.setText("0");
                        order_hash_map.clear();
                        order_prod_hash_map.clear();
                        remark_hash_map.clear();
                        prod_id_track.clear();
                        prod_view_map.clear();
                        prev_id_qty_hash_map.clear();
                        prodid_qty_hash_map.clear();
                        ((LinearLayout) view.findViewById(R.id.order_container)).removeAllViews();
                        if(!occupy_table_list.isEmpty()) {
                            if (!occupy_table_list.contains(table_id)) {
                                occupy_table_list.add(table_id);
                            }
                        }else{
                            occupy_table_list.add(table_id);
                        }
                        setTable();
                        if (ConnectivityTest.getNetStat(getActivity())) {
                            String url = Constant.ipaddress + "/getTableData?TableId=" + table_id;
                            //Online
                            //new refreshData().execute(url);
                        }
                        pd.dismiss();
                    } else if (str.equals("\"0\"")){
                        url_kotno = "0";
                        toast.setText("Could Not Placed Order");
                        toast.show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toast.setText("Error In Place Order");
                    toast.show();
                    pd.dismiss();
                }
            }
            pd.dismiss();
        }
    }

    private class refreshData extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Getting Table Data");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... url) {
            return Post.POST(url[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null && result.length() != 0) {
                try {
                    result = result.replace("\\", "");
                    result = result.replace("''", "");
                    result = result.substring(1, result.length() - 1);
                    JSONArray jsonArray = new JSONArray(result);
                    Log.d("jsonArray.length()", "" + jsonArray.length());
                    if (jsonArray.length() >= 1) {
                        prodid_qty_hash_map.clear();
                        prodid_qty_list.clear();
                        prev_id_qty_hash_map.clear();
                        ((LinearLayout) view.findViewById(R.id.order_container)).removeAllViews();
                        int j;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            j = jsonArray.getJSONObject(i).getInt("ItemID");
                            kotno = jsonArray.getJSONObject(i).getString("KOTNO");
                            kotno = kotno.replace("\"", "");
                            List<String> list = new ArrayList<>();
                            list.add(web_prod_hash_map.get(jsonArray.getJSONObject(i).getInt("ItemID")));
                            Double d1 = jsonArray.getJSONObject(i).getDouble("Qty");
                            int qty = d1.intValue();
                            prev_id_qty_hash_map.put(jsonArray.getJSONObject(i).getInt("ItemID"), qty);
                            prodid_qty_hash_map.put(j, qty);
                            prodid_qty_list.add(j);
                            list.add(String.valueOf(qty));
                            list.add(jsonArray.getJSONObject(i).get("Rate").toString());
                            double d = Double.parseDouble(jsonArray.getJSONObject(i).get("Rate").toString());
                            list.add(String.valueOf((qty * d)));
                            list.add(jsonArray.getJSONObject(i).get("Barcode").toString());
                            list.add("O");
                            if (order_hash_map.containsKey(table_id)) {
                                List<Integer> list1 = order_hash_map.get(table_id);
                                if (!list1.contains(jsonArray.getJSONObject(i).getInt("ItemID"))) {
                                    list1.add(jsonArray.getJSONObject(i).getInt("ItemID"));
                                    order_hash_map.put(table_id, list1);
                                    order_repeat_flag = 3;
                                    counter++;
                                } else {
                                    order_repeat_flag = 3;
                                }
                            } else {
                                List<Integer> list1 = new ArrayList<>();
                                list1.add(jsonArray.getJSONObject(i).getInt("ItemID"));
                                order_hash_map.put(table_id, list1);
                                counter++;
                                order_repeat_flag = 3;
                            }
                            total_qty_counter++;
                            addOrderItem(list, j);
                        }
                        total_qty.setText(String.valueOf(total_qty_counter));
                    } else if (jsonArray.length() == 0) {
                        order_repeat_flag = 0;
                        counter = -1;
                        total_qty_counter = 0;
                        total_qty.setText("0");
                        total_amnt.setText("0");
                        order_hash_map.clear();
                        order_prod_hash_map.clear();
                        prev_id_qty_hash_map.clear();
                        prod_id_track.clear();
                        prod_view_map.clear();
                    }
                    if (ConnectivityTest.getNetStat(getActivity())) {
                        if (kArea1print_list.size() != 0) {
                            new Print1().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                        }
                        if (kArea2print_list.size() != 0) {
                            //new Print2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                        }
                        if (kArea3print_list.size() != 0) {
                            //new Print3().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                        }
                    } else {
                        toast.setText("Could Not Print Order Network Connection Error");
                        toast.show();
                    }
                    pd.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    toast.setText("Please Take Login Again");
                    toast.show();
                    pd.dismiss();
                }
            }
            pd.dismiss();
        }
    }

    private class Print1 extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String str;//method
            //Bitmap logoData = BitmapFactory.decodeResource(getResources(), R.drawable.store);
            StringBuilder textData = new StringBuilder();
            int item_length = 21;
            int netAmnt=0;

            try {
                /*Printer _mPrinter = new Printer(0, 0, getActivity());
                //method = "addTextAlign";
                _mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                //method = "addFeedLine";
                _mPrinter.addFeedLine(1);*/

                byte[] arrayOfByte1 = { 27, 33, 0 };
                byte[] format = { 27, 33,0 };

                byte[] center =  { 27, 97, 1};
                VerificationActivity.mService.write(center);
                byte nameFontformat[] = format;
                nameFontformat[2] = ((byte)(0x20|arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage("Kitchen","GBK");

                nameFontformat[2] = ((byte)(0x0|arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);

                byte[] left =  { 27, 97, 0 };
                VerificationActivity.mService.write(left);

                VerificationActivity.mService.sendMessage("KOT No:"+kotno+"   "+date+"  "+getTime(),"GBK");
                //VerificationActivity.mService.sendMessage(date+space_str13+getTime(),"GBK");
                if(table_id!=1) {
                    VerificationActivity.mService.sendMessage("Table: " + table_hash_map1.get(table_id) + "   " + "Waiter: " + user, "GBK");
                }else{
                    VerificationActivity.mService.sendMessage("Table: Parcel   "+"CustName: "+custName,"GBK");
                }

                nameFontformat = format;
                nameFontformat[2] = ((byte)(0x8 | arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage(line_str,"GBK");

                nameFontformat = format;
                nameFontformat[2] = ((byte)(0x0|arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage("Item"+space_str+"Qty","GBK");
                nameFontformat = format;
                nameFontformat[2] = ((byte)(0x8 | arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage(line_str,"GBK");
                nameFontformat = format;
                nameFontformat[2] = ((byte)(0x0|arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);

                /*textData.append("\t\t").append("Kitchen 1").append("\n");
                textData.append("KOT No: ").append(kotno).append("\t\t ").append(date).append("    ").append(getTime()).append("\n");
                textData.append("Table: ").append(table_hash_map1.get(table_id)).append("\t\t Waiter: ").append(user).append("\n");
                textData.append(line_str);
                textData.append("Item"+space_str+"QTY\n");
                textData.append(line_str);
                //method = "addText";
                //_mPrinter.addText(textData.toString());
                Log.d("Print1",textData.toString());
                textData.delete(0, textData.length());*/

                int count = 0;
                for (int i = 0; i < kArea1print_list.size(); i++) {
                    String _value = kArea1print_list.get(i);
                    String[] _item_remark_qty = _value.split("!");
                    String _qty = _item_remark_qty[1];
                    netAmnt += Integer.parseInt(_qty);
                    if(_qty.length()==1){
                        _qty = "  " + _qty;
                    }else if(_qty.length()==2){
                        _qty = " " + _qty;
                    }
                    String[] _item_remark =  _item_remark_qty[0].split("@");
                    if(_item_remark.length>1){
                        String[] _item = _item_remark[0].split("#");
                        String[] _remark = _item_remark[1].split("#");
                        if(_item.length>1 && _remark.length>1){
                            textData.append("").append(_item[0]).append(_remark[0]).append(_qty).append("\n").append(_item[1]).append(_remark[1]).append("\n");
                        }else if(_item.length==1 && _remark.length>1){
                            String space = "";
                            for(int j=0;j<item_length;j++){
                                space = space + " ";
                            }
                            textData.append("").append(_item[0]).append(_remark[0]).append(_qty).append("\n").append(space).append(_remark[1]).append("\n");
                        }else if(_item.length>1&&_remark.length==1){
                            textData.append("").append(_item[0]).append(_remark[0]).append(_qty).append("\n").append(_item[1]).append("\n");
                        }else if(_item.length==1&&_remark.length==1) {
                            textData.append("").append(_item[0]).append(_remark[0]).append(_qty).append("\n");
                        }
                    }else if(_item_remark.length==1){
                        String[] _item = _item_remark[0].split("#");
                        if(_item.length>1){
                            textData.append("").append(_item[0]).append(_qty).append("\n").append(_item[1]).append("\n");
                        }else if(_item.length==1){
                            textData.append("").append(_item_remark[0]).append(_qty).append("\n");
                        }
                    }
                    count++;
                }
                String _count = String.valueOf(count);
                if(_count.length()==1){
                    _count = "  "+_count;
                }else if(_count.length()==2){
                    _count = " "+_count;
                }
                VerificationActivity.mService.sendMessage(textData.toString(),"GBK");
                nameFontformat = format;
                nameFontformat[2] = ((byte)(0x8 | arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage(line_str,"GBK");
                nameFontformat = format;
                nameFontformat[2] = ((byte)(0x0|arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage("Total "+space_str21+_count,"GBK");
                nameFontformat = format;
                nameFontformat[2] = ((byte)(0x8 | arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage(line_str,"GBK");

                /*nameFontformat = format;
                nameFontformat[2] = ((byte)(0x20|arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                String netAmts = "Net Amnt    "+netAmnt;
                VerificationActivity.mService.sendMessage(netAmts,"GBK");*/

                VerificationActivity.mService.write(center);
                nameFontformat[2] = ((byte)(0x1|arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                //String softwareBy = "Software By LNB Infotech 9370716834";
                //VerificationActivity.mService.sendMessage(softwareBy,"GBK");
                VerificationActivity.mService.write(PrinterCommands.ESC_ENTER);
                //VerificationActivity.mService.sendMessage(space_str,"GBK");
                //VerificationActivity.mService.sendMessage(space_str,"GBK");

                //textData.append(line_str);
                //textData.append(space_str+"Total "+_count+"\n");
                //method = "addText";
                //_mPrinter.addText(textData.toString());
                Log.d("Log",textData.toString());
                //textData.delete(0, textData.length());
                //method = "addCut";
                //_mPrinter.addCut(Printer.CUT_FEED);
                /*if (kArea1print_list.size() != 0) {
                    mPrinter.connect("TCP:192.168.11.11", Printer.PARAM_DEFAULT);
                    _mPrinter.beginTransaction();
                    _mPrinter.sendData(Printer.PARAM_DEFAULT);
                }*/
                //_mPrinter.clearCommandBuffer();
                //_mPrinter.disconnect();
                //Log.d("Log",method);
            } catch (Exception e) {
                e.printStackTrace();
                str = "Printer 1 May Not Be Connected ";
                return str;
            }
            return "Order Received By Kitchen 1";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("Log", result);
            toast.setText(result);
            toast.show();
        }
    }

    private class Print2 extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String str;//method
            //Bitmap logoData = BitmapFactory.decodeResource(getResources(), R.drawable.store);
            StringBuilder textData = new StringBuilder();
            int item_length = 21;
            try {
                /*Printer _mPrinter = new Printer(0, 0, getActivity());
                //method = "addTextAlign";
                _mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                //method = "addFeedLine";
                _mPrinter.addFeedLine(1);*/

                byte[] arrayOfByte1 = { 27, 33, 0 };
                byte[] format = { 27, 33,0 };

                byte[] center =  { 27, 97, 1};
                VerificationActivity.mService.write(center);
                byte nameFontformat[] = format;
                nameFontformat[2] = ((byte)(0x20|arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage("Kitchen 2","GBK");

                nameFontformat[2] = ((byte)(0x0|arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);

                byte[] left =  { 27, 97, 0 };
                VerificationActivity.mService.write(left);

                VerificationActivity.mService.sendMessage("KOT No: "+kotno+"   "+date+"   "+getTime(),"GBK");

                if(table_id!=1) {
                    VerificationActivity.mService.sendMessage("Table: " + table_hash_map1.get(table_id) + "   " + "Waiter: " + user, "GBK");
                }else{
                    VerificationActivity.mService.sendMessage("Table: Parcel   "+"CustName: "+custName,"GBK");
                }

                VerificationActivity.mService.sendMessage(line_str,"GBK");
                VerificationActivity.mService.sendMessage("Item"+space_str+"QTY","GBK");
                VerificationActivity.mService.sendMessage(line_str,"GBK");

                /*textData.append("\t\t").append("Kitchen 2").append("\n");
                textData.append("KOT No: ").append(kotno).append("\t\t ").append(date).append("    ").append(getTime()).append("\n");
                textData.append("Table: ").append(table_hash_map1.get(table_id)).append("\t\t Waiter: ").append(user).append("\n");
                textData.append(line_str);
                textData.append("Item                                       QTY\n");
                textData.append(line_str);
                //method = "addText";
                _mPrinter.addText(textData.toString());
                Log.d("Print2",textData.toString());
                textData.delete(0, textData.length());*/
                int count = 0;
                for (int i = 0; i < kArea2print_list.size(); i++) {
                    String _value = kArea2print_list.get(i);
                    String[] _item_remark_qty = _value.split("!");
                    String _qty = _item_remark_qty[1];
                    if(_qty.length()==1){
                        _qty = "  " + _qty;
                    }else if(_qty.length()==2){
                        _qty = " " + _qty;
                    }
                    String[] _item_remark =  _item_remark_qty[0].split("@");
                    if(_item_remark.length>1){
                        String[] _item = _item_remark[0].split("#");
                        String[] _remark = _item_remark[1].split("#");
                        if(_item.length>1 && _remark.length>1){
                            textData.append("").append(_item[0]).append(_remark[0]).append(_qty).append("\n").append(_item[1]).append(_remark[1]).append("\n");
                        }else if(_item.length==1 && _remark.length>1){
                            String space = "";
                            for(int j=0;j<item_length;j++){
                                space = space + " ";
                            }
                            textData.append("").append(_item[0]).append(_remark[0]).append(_qty).append("\n").append(space).append(_remark[1]).append("\n");
                        }else if(_item.length>1&&_remark.length==1){
                            textData.append("").append(_item[0]).append(_remark[0]).append(_qty).append("\n").append(_item[1]).append("\n");
                        }else if(_item.length==1&&_remark.length==1) {
                            textData.append("").append(_item[0]).append(_remark[0]).append(_qty).append("\n");
                        }
                    }else if(_item_remark.length==1){
                        String[] _item = _item_remark[0].split("#");
                        if(_item.length>1){
                            textData.append("").append(_item[0]).append(_qty).append("\n").append(_item[1]).append("\n");
                        }else if(_item.length==1){
                            textData.append("").append(_item_remark[0]).append(_qty).append("\n");
                        }
                    }
                    count++;
                }
                String _count = String.valueOf(count);
                if(_count.length()==1){
                    _count = "  "+_count;
                }else if(_count.length()==2){
                    _count = " "+_count;
                }
                textData.append(line_str);
                VerificationActivity.mService.sendMessage("Total "+space_str21+_count,"GBK");
                //method = "addText";
                //_mPrinter.addText(textData.toString());
                Log.d("Log",textData.toString());
                //textData.delete(0, textData.length());
                // method = "addCut";
                //_mPrinter.addCut(Printer.CUT_FEED);
                /*if (kArea2print_list.size() != 0) {
                    _mPrinter.connect("TCP:192.168.11.22", Printer.PARAM_DEFAULT);
                    _mPrinter.beginTransaction();
                    _mPrinter.sendData(Printer.PARAM_DEFAULT);
                }*/
                //_mPrinter.clearCommandBuffer();
                //_mPrinter.disconnect();
                //Log.d("Log",method);
                VerificationActivity.mService.sendMessage(textData.toString(),"GBK");
                VerificationActivity.mService.sendMessage(line_str,"GBK");
                VerificationActivity.mService.sendMessage("Total "+space_str21+_count,"GBK");
            } catch (Exception e) {
                e.printStackTrace();
                str = "Printer 2 May Not Be Connected ";
                return str;
            }
            return "Order Received By Kitchen 2";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("Log", result);
            toast.setText(result);
            toast.show();
        }
    }

    private class Print3 extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String str;//method
            //Bitmap logoData = BitmapFactory.decodeResource(getResources(), R.drawable.store);
            StringBuilder textData = new StringBuilder();
            int item_length = 21;
            try {
                /*Printer _mPrinter = new Printer(0, 0, getActivity());
                //method = "addTextAlign";
                _mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                //method = "addFeedLine";
                _mPrinter.addFeedLine(1);*/

                byte[] arrayOfByte1 = { 27, 33, 0 };
                byte[] format = { 27, 33,0 };

                byte[] center =  { 27, 97, 1};
                VerificationActivity.mService.write(center);
                byte nameFontformat[] = format;
                nameFontformat[2] = ((byte)(0x20|arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);
                VerificationActivity.mService.sendMessage("Kitchen 3","GBK");

                nameFontformat[2] = ((byte)(0x0|arrayOfByte1[2]));
                VerificationActivity.mService.write(nameFontformat);

                byte[] left =  { 27, 97, 0 };
                VerificationActivity.mService.write(left);

                VerificationActivity.mService.sendMessage("KOT No: "+kotno+"   "+date+"   "+getTime(),"GBK");
                if(table_id!=1) {
                    VerificationActivity.mService.sendMessage("Table: " + table_hash_map1.get(table_id) + "   " + "Waiter: " + user, "GBK");
                }else{
                    VerificationActivity.mService.sendMessage("Table: Parcel   "+"CustName: "+custName,"GBK");
                }
                VerificationActivity.mService.sendMessage(line_str,"GBK");
                VerificationActivity.mService.sendMessage("Item"+space_str+"QTY","GBK");
                VerificationActivity.mService.sendMessage(line_str,"GBK");

                /*textData.append("\t\t").append("Kitchen 3").append("\n");
                textData.append("KOT No: ").append(kotno).append("\t\t ").append(date).append("    ").append(getTime()).append("\n");
                textData.append("Table: ").append(table_hash_map1.get(table_id)).append("\t\t Waiter: ").append(user).append("\n");
                textData.append(line_str);
                textData.append("Item                                       QTY\n");
                textData.append(line_str);
                //method = "addText";
                //_mPrinter.addText(textData.toString());
                Log.d("Log",textData.toString());
                textData.delete(0, textData.length());*/
                int count = 0;
                for (int i = 0; i < kArea3print_list.size(); i++) {
                    String _value = kArea3print_list.get(i);
                    String[] _item_remark_qty = _value.split("!");
                    String _qty = _item_remark_qty[1];
                    if(_qty.length()==1){
                        _qty = "  " + _qty;
                    }else if(_qty.length()==2){
                        _qty = " " + _qty;
                    }
                    String[] _item_remark =  _item_remark_qty[0].split("@");
                    if(_item_remark.length>1){
                        String[] _item = _item_remark[0].split("#");
                        String[] _remark = _item_remark[1].split("#");
                        if(_item.length>1 && _remark.length>1){
                            textData.append("").append(_item[0]).append(_remark[0]).append(_qty).append("\n").append(_item[1]).append(_remark[1]).append("\n");
                        }else if(_item.length==1 && _remark.length>1){
                            String space = "";
                            for(int j=0;j<item_length;j++){
                                space = space + " ";
                            }
                            textData.append("").append(_item[0]).append(_remark[0]).append(_qty).append("\n").append(space).append(_remark[1]).append("\n");
                        }else if(_item.length>1&&_remark.length==1){
                            textData.append("").append(_item[0]).append(_remark[0]).append(_qty).append("\n").append(_item[1]).append("\n");
                        }else if(_item.length==1&&_remark.length==1) {
                            textData.append("").append(_item[0]).append(_remark[0]).append(_qty).append("\n");
                        }
                    }else if(_item_remark.length==1){
                        String[] _item = _item_remark[0].split("#");
                        if(_item.length>1){
                            textData.append("").append(_item[0]).append(_qty).append("\n").append(_item[1]).append("\n");
                        }else if(_item.length==1){
                            textData.append("").append(_item_remark[0]).append(_qty).append("\n");
                        }
                    }
                    count++;
                }
                String _count = String.valueOf(count);
                if(_count.length()==1){
                    _count = "  "+_count;
                }else if(_count.length()==2){
                    _count = " "+_count;
                }
                textData.append(line_str);
                textData.append("Total "+space_str+_count+"\n");
                //method = "addText";
                //_mPrinter.addText(textData.toString());
                Log.d("Log",textData.toString());
                //textData.delete(0, textData.length());
                // method = "addCut";
                //_mPrinter.addCut(Printer.CUT_FEED);
                /*if (kArea3print_list.size() != 0) {
                    _mPrinter.connect("TCP:192.168.11.33", Printer.PARAM_DEFAULT);
                    _mPrinter.beginTransaction();
                    _mPrinter.sendData(Printer.PARAM_DEFAULT);
                }*/
                //_mPrinter.clearCommandBuffer();
                //_mPrinter.disconnect();
                //Log.d("Log",method);
                VerificationActivity.mService.sendMessage(textData.toString(),"GBK");
                VerificationActivity.mService.sendMessage(line_str,"GBK");
                VerificationActivity.mService.sendMessage("Total "+space_str21+_count,"GBK");
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
            Log.d("Log", result);
            toast.setText(result);
            toast.show();
        }
    }

    //------------------------------------------------------------------------------------

    @SuppressLint("HandlerLeak")
    private final  Handler mHandler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            toast.setText("Bluetooth Printer Connected");
                            toast.show();
                            //btnClose.setEnabled(true);
                            //btnSend.setEnabled(true);
                            //qrCodeBtnSend.setEnabled(true);
                            //btnSendDraw.setEnabled(true);
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
                    //connectBT();
                    toast.setText("Device connection was lost");
                    toast.show();
                    //btnClose.setEnabled(false);
                    //btnSend.setEnabled(false);
                    //qrCodeBtnSend.setEnabled(false);
                    //btnSendDraw.setEnabled(false);
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:
                    //connectBT();
                    toast.setText("Unable to Connect Bluetooth Printer");
                    toast.show();
                    break;
            }
        }

    };

    private void showRemark(int a) {
        final EditText ed_remark = new EditText(getActivity().getApplicationContext());
        ed_remark.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(),R.color.black));
        ed_remark.setText(custName);
        ed_remark.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AlertDialogCustom);
        builder.setCancelable(false);
        builder.setView(ed_remark);
        builder.setMessage("Please Enter Customer Name");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(ed_remark.getWindowToken(), 0);
                custName = ed_remark.getText().toString();
                Constant.showLog(custName);
                dialog.dismiss();
                placeOrderChnaged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(ed_remark.getWindowToken(), 0);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void showSnackBar(String msg){
        toast.setText(msg);
        toast.show();
        /*if(snackbar!=null) {
            snackbar = Snackbar.make(lay, msg, Snackbar.LENGTH_LONG);
            snackbar.show();
        }*/
    }
}
