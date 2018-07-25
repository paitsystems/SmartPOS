package com.pait.smartpos;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hoin.btsdk.BluetoothService;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.fragments.BillReprintCancelFragment;
import com.pait.smartpos.fragments.CategoryFragment;
import com.pait.smartpos.fragments.GSTViewFragment;
import com.pait.smartpos.fragments.MainFragment;
import com.pait.smartpos.fragments.MainOptionFragment;
import com.pait.smartpos.fragments.MasterUpdationFragment;
import com.pait.smartpos.fragments.PairedDeviceFragment;
import com.pait.smartpos.fragments.PrinterFragment;
import com.pait.smartpos.fragments.ReportListFragment;
import com.pait.smartpos.fragments.TableViewFragment;
import com.pait.smartpos.fragments.UpdateBillReportFragment;
import com.pait.smartpos.model.CircleTransform;
import com.pait.smartpos.model.UserProfileClass;
import com.pait.smartpos.permission.GetPermission;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

//Created by lnb on 7/21/2017.

public class DrawerTestActivity extends AppCompatActivity implements View.OnClickListener{

    private DrawerLayout drawer;
    //private ListView listView;
    private String[] navigationListNames;
    private Toolbar toolbar;
    private int menuflag = 0, flag = 0;
    private Menu mMenu;
    private MainFragment mainFragment;
    private CategoryFragment catFragment;
    private TableViewFragment tableViewFragment;
    private GSTViewFragment gstViewFragment;
    private InputMethodManager input;
    public static int isUpdated = 0;
    public static int navItemIndex = 0, isBTConnected = 0;
    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_TABLE = "table";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_CAT = "category";
    private static final String TAG_GST = "gst";
    private static final String TAG_PRINTER = "printer";
    private static final String TAG_REPORT = "report";
    private static final String TAG_LOGOUT = "logout";
    private static final String TAG_SUPPORT = "support";
    private static final String TAG_BILL = "bill";
    private static final String TAG_UB = "update";
    public static String CURRENT_TAG = TAG_HOME;
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private NavigationView navigationView;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toast toast;
    private BluetoothDevice con_dev = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawertest);

        init();

        setupToolbar();
        loadNavHeader();
        setUpNavigationView();
        //prepareMenuData();
        //populateExpandableList();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }

        /*if(VerificationActivity.mService!=null) {
            VerificationActivity.mService.stop();
        }
        VerificationActivity.mService = new BluetoothService(getApplicationContext(), mHandler1);
        connectBT();*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isUpdated==1) {
            isUpdated = 0;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if(VerificationActivity.mService!=null) {
            VerificationActivity.mService.stop();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        mMenu = menu;
        MenuInflater inflater = getMenuInflater();
        if(menuflag == 0) {
            inflater.inflate(R.menu.drawer_menu, menu);
        }else if(menuflag == 1) {
            inflater.inflate(R.menu.table_master_menu, menu);
        }else if(menuflag == 2) {
            inflater.inflate(R.menu.category_master_menu, menu);
        }else if(menuflag == 3) {
            inflater.inflate(R.menu.gst_master_menu, menu);
        }else if(menuflag == 11 || menuflag==12 || menuflag==13) {
            inflater.inflate(R.menu.masterupdate_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.bill:
                mainFragment.saveBill1();
                break;
            case R.id.addTax:
                Intent intent = new Intent(getApplicationContext(),AddNewTaxActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter,R.anim.exit);
                break;
            case R.id.addCat:
                catFragment.addCategory();
                break;
            case R.id.updateCat:
                catFragment.updateCategory();
                break;
            case R.id.viewCat:
                catFragment.viewCategory();
                break;
            case R.id.addTable:
                tableViewFragment.addTable();
                break;
            case R.id.updateTable:
                tableViewFragment.updateTable();
                break;
            /*case R.id.updateTableGST:
                tableViewFragment.updateTableGST();
                break;*/
            case R.id.viewTable:
                tableViewFragment.viewTable();
                break;
            case R.id.addGST:
                gstViewFragment.addGST();
                break;
            case R.id.updateGST:
                gstViewFragment.updateGST();
                break;
            case R.id.viewGST:
                gstViewFragment.viewGST();
                break;
            case R.id.add:
                Intent intent5 = null;
                if(menuflag==12) {
                    intent5 = new Intent(getApplicationContext(),AddProductMasterActivity.class);
                    intent5.putExtra("from","FromSettingsOptions");
                }else if(menuflag==11) {
                    intent5 = new Intent(getApplicationContext(),AddTableMasterActivity.class);
                    intent5.putExtra("from","FromSettingsOptions");
                }else if(menuflag==13) {
                    intent5 = new Intent(getApplicationContext(),AddCategoryMasterActivity.class);
                }
                startActivity(intent5);
                overridePendingTransition(R.anim.enter,R.anim.exit);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.img_profile:
                intent = new Intent(getApplicationContext(),UserProfileActivity.class);
                intent.putExtra("from","second");
                startActivity(intent);
                break;
            case R.id.name:
                intent = new Intent(getApplicationContext(),UserProfileActivity.class);
                intent.putExtra("from","second");
                startActivity(intent);
                break;
            case R.id.website:
                intent = new Intent(getApplicationContext(),UserProfileActivity.class);
                intent.putExtra("from","second");
                startActivity(intent);
                break;
        }
    }

    private void init(){
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        drawer = findViewById(R.id.drawer_layout);
        //listView = findViewById(R.id.left_drawer);
        drawer = findViewById(R.id.drawer_layout);
        input = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        navigationView = findViewById(R.id.nav_view);
        mHandler = new Handler();
        navHeader = navigationView.getHeaderView(0);
        txtName = navHeader.findViewById(R.id.name);
        txtWebsite = navHeader.findViewById(R.id.website);
        imgNavHeaderBg = navHeader.findViewById(R.id.img_header_bg);
        imgProfile = navHeader.findViewById(R.id.img_profile);
        navigationListNames = getResources().getStringArray(R.array.navigationName);
        imgProfile.setOnClickListener(this);
        txtName.setOnClickListener(this);
        txtWebsite.setOnClickListener(this);
        //expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
    }

    private String getTime() {
        String str = "";
        try{
            str = new SimpleDateFormat("HH", Locale.ENGLISH).format(new Date());
        }catch (Exception e){
            e.printStackTrace();
        }
        return str;
    }

    private void loadNavHeader(){
        UserProfileClass user = new Constant(getApplicationContext()).getPref();
        txtName.setText(user.getUserName());
        txtWebsite.setText(user.getEmailId());

        // loading header background image
        Glide.with(this).load(Constant.bgImgIPAddress)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image

        String str = user.getImgName();
        if(str!=null) {
            if (str.equals("NA")) {
                str = "pa.png";
            }
        }else{
            str = "pa.png";
        }
        //TODO: Change Here
        //str = Constant.imgIpaddress+str;
        Constant.showLog(str);
        Glide.with(this).load(Constant.imgIpaddress)
                .crossFade()
                .thumbnail(1f)
                .fitCenter()
                .centerCrop()
                .override(100,100)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_male)
                .into(imgProfile);
    }

    private void setUpNavigationView(){
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.home:
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_HOME;
                    break;
                /*case R.id.table:
                    navItemIndex = 1;
                    CURRENT_TAG = TAG_TABLE;
                    break;*/
                case R.id.product:
                    navItemIndex = 1;
                    CURRENT_TAG = TAG_PRODUCT;
                    break;
               /* case R.id.category:
                    navItemIndex = 3;
                    CURRENT_TAG = TAG_CAT;
                    break;*/
               /* case R.id.gst:
                    navItemIndex = 4;
                    CURRENT_TAG = TAG_GST;
                    break;*/
                case R.id.printer:
                    navItemIndex = 2;
                    CURRENT_TAG = TAG_PRINTER;
                    break;
                /*case R.id.report:
                    navItemIndex = 6;
                    CURRENT_TAG = TAG_REPORT;
                    break;*/
                case R.id.billreprint:
                    navItemIndex = 3;
                    CURRENT_TAG = TAG_BILL;
                    break;
                case R.id.billupdate:
                    navItemIndex = 4;
                    CURRENT_TAG = TAG_UB;
                    break;
                case R.id.logout:
                    drawer.closeDrawers();
                    showDia(2);
                    break;
                case R.id.nav_support:
                    startActivity(new Intent(DrawerTestActivity.this, ContactUsActivity.class));
                    return true;
                case R.id.nav_about_us:
                    startActivity(new Intent(DrawerTestActivity.this, AboutUsActivity.class));
                    return true;
                case R.id.import_data:
                    startActivity(new Intent(DrawerTestActivity.this, ImportMasterActivity.class));
                    return true;
                case R.id.nav_backup:
                    startActivity(new Intent(DrawerTestActivity.this, DataBackupActivity.class));
                    return true;
                default:
                    navItemIndex = 0;
            }
            if (menuItem.isChecked()) {
                menuItem.setChecked(false);
            } else {
                menuItem.setChecked(true);
            }
            menuItem.setChecked(true);
            loadHomeFragment();
            return true;
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void loadHomeFragment() {
        selectNavMenu();

        setToolbarTitle();
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                if(!CURRENT_TAG.equals(TAG_LOGOUT)) {
                    Fragment fragment = getHomeFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.content_frame, fragment, CURRENT_TAG);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            }
        };
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
        drawer.closeDrawers();
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        Fragment fragment = null;
        switch (navItemIndex) {
            case 0:
                fragment = new MainOptionFragment();
                //mainFragment = (MainOptionFragment) fragment;
                menuflag = -1;
                if(flag!=0) {
                    invalidateOptionsMenu();
                    onCreateOptionsMenu(mMenu);
                    flag = 1;
                }
                break;
            case 9:
                Bundle bundle3 = new Bundle();
                bundle3.putString("master","table");
                fragment = new MasterUpdationFragment();
                fragment.setArguments(bundle3);
                menuflag = 11;
                invalidateOptionsMenu();
                flag = 1;
                break;
            case 1:
                Bundle bundle2 = new Bundle();
                bundle2.putString("master","cat3");
                fragment = new MasterUpdationFragment();
                fragment.setArguments(bundle2);
                menuflag = 12;
                invalidateOptionsMenu();
                flag = 1;
                break;
            case 7:
                Bundle bundle1 = new Bundle();
                bundle1.putString("master","category");
                fragment = new MasterUpdationFragment();
                fragment.setArguments(bundle1);
                menuflag = 13;
                invalidateOptionsMenu();
                flag = 1;
                break;
            case 8:
                fragment = new GSTViewFragment();
                gstViewFragment = (GSTViewFragment) fragment;
                invalidateOptionsMenu();
                menuflag = 3;
                flag = 1;
                break;
            case 2:
                fragment = new PairedDeviceFragment();
                invalidateOptionsMenu();
                menuflag = -1;
                flag = 1;
                break;
            case 6:
                fragment = new ReportListFragment();
                invalidateOptionsMenu();
                menuflag = -1;
                flag = 1;
                break;
            case 4:
                fragment = new UpdateBillReportFragment();
                invalidateOptionsMenu();
                menuflag = -1;
                flag = 1;
                break;
            case 3:
                fragment = new BillReprintCancelFragment();
                invalidateOptionsMenu();
                menuflag = -1;
                flag = 1;
                break;
            case 5:
                showDia(2);
                break;
            case 10:
                showDia(2);
                break;
            default:
                break;
        }
        return fragment;
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(navigationListNames[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setupToolbar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void showDia(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DrawerTestActivity.this);
        builder.setCancelable(false);
        if (i == -1) {
            builder.setMessage("Recoonect Printer");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    connectBT();
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }
        if (i == 1) {
            builder.setMessage("Do You Want To Exit From App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    doFinish();
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }else if (i == 2) {
            builder.setMessage("Do You Want Logout From App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    doThis();
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }
        builder.create().show();
    }

    private void doThis(){
        SharedPreferences.Editor editor = VerificationActivity.pref.edit();
        editor.putBoolean(getString(R.string.pref_logged), false);
        editor.apply();
        doFinish();
    }

    private void doFinish(){
        finish();
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }

    /*private void prepareMenuData() {

        *//*MenuModel menuModel = new MenuModel("Home", true, false, ""); //Menu of Android Tutorial. No sub menus
        headerList.add(menuModel);
        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }*//*

        MenuModel menuModel = new MenuModel("Masters", true, true, ""); //Menu of Java Tutorials
        headerList.add(menuModel);
        List<MenuModel> childModelsList = new ArrayList<>();
        MenuModel childModel = new MenuModel("Table", false, false, "");
        childModelsList.add(childModel);
        childModel = new MenuModel("Product", false, false, "");
        childModelsList.add(childModel);
        childModel = new MenuModel("Category", false, false, "");
        childModelsList.add(childModel);
        childModel = new MenuModel("GST", false, false, "");
        childModelsList.add(childModel);
        if (menuModel.hasChildren) {
            Log.d("API123","here");
            childList.put(menuModel, childModelsList);
        }

        *//*menuModel = new MenuModel("Printer", true, true, ""); //Menu of Python Tutorials
        headerList.add(menuModel);
        if (menuModel.hasChildren) {
            childList.put(menuModel, null);
        }

        menuModel = new MenuModel("Reports", true, true, ""); //Menu of Python Tutorials
        headerList.add(menuModel);
        if (menuModel.hasChildren) {
            childList.put(menuModel, null);
        }

        menuModel = new MenuModel("Logout", true, true, ""); //Menu of Python Tutorials
        headerList.add(menuModel);
        if (menuModel.hasChildren) {
            childList.put(menuModel, null);
        }*//*
    }

    private void populateExpandableList() {

        expandableListAdapter = new ExpandableListAdapter(this, headerList, childList);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (headerList.get(groupPosition).isGroup) {
                    if (!headerList.get(groupPosition).hasChildren) {
                        *//*WebView webView = findViewById(R.id.webView);
                        webView.loadUrl(headerList.get(groupPosition).url);
                        onBackPressed();*//*
                    }
                }

                return false;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (childList.get(headerList.get(groupPosition)) != null) {
                    MenuModel model = childList.get(headerList.get(groupPosition)).get(childPosition);
                    if (model.url.length() > 0) {
                        *//*WebView webView = findViewById(R.id.webView);
                        webView.loadUrl(model.url);
                        onBackPressed();*//*
                    }
                }

                return false;
            }
        });
    }*/

    private void connectBT(){
        try {
            if(VerificationActivity.mService!=null) {
                if (VerificationActivity.mService.isBTopen()) {
                    UserProfileClass user = new Constant(getApplicationContext()).getPref();
                    if (user.getMacAddress() != null) {
                        Constant.showLog(user.getMacAddress());
                        con_dev = VerificationActivity.mService.getDevByMac(user.getMacAddress());
                        VerificationActivity.mService.connect(con_dev);
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
        /*Set<BluetoothDevice> set = VerificationActivity.mService.getPairedDev();
        if (!set.isEmpty()) {
            for (BluetoothDevice device : set) {
                con_dev = VerificationActivity.mService.getDevByMac(device.getAddress());
                Log.d("Log", "getDevByMac :- "+ con_dev);
                VerificationActivity.mService.connect(con_dev);
                DrawerTestActivity.isBTConnected = 1;
            }
        }*/
    }

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
