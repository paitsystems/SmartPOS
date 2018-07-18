package com.pait.smartpos;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.pait.smartpos.adpaters.AddToCartRecyclerAdapter;
import com.pait.smartpos.adpaters.AllProductAdapter;
import com.pait.smartpos.adpaters.AllRateAdapter;
import com.pait.smartpos.adpaters.ProductListRecyclerAdapter;
import com.pait.smartpos.adpaters.ProductRateRecyclerAdapter;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.interfaces.OnItemClickListener;
import com.pait.smartpos.interfaces.RecyclerViewToActivityInterface;
import com.pait.smartpos.model.AddToCartClass;
import com.pait.smartpos.model.MasterUpdationClass;
import com.pait.smartpos.model.ProductClass;
import com.pait.smartpos.model.RateMasterClass;

import java.util.ArrayList;
import java.util.List;

public class CashMemoActivity extends AppCompatActivity  implements View.OnClickListener, RecyclerViewToActivityInterface {

    private RecyclerView rv_Prod, rv_Price, rv_Order;
    private TextView tv_prodAll, tv_priceAll, tv_remove, tv_add, tv_totalQty, tv_totalAmnt;
    private EditText ed_Qty, ed_Amnt;
    private Button btn_Add;
    private Constant constant;
    private DBHandlerR db;
    private Toast toast;
    private List<ProductClass> prodList;
    private List<RateMasterClass> rateList;
    private List<AddToCartClass> cartList;
    private float rate = 0, totQty = 0, totAmnt = 0;
    private ProductClass productClass = null;
    private RateMasterClass rateMasterClass = null;
    private AddToCartRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_memo);
        init();
        setProdData();

        ed_Qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String qty = ed_Qty.getText().toString();
                String amnt = ed_Amnt.getText().toString();
                if(!amnt.equals("")) {
                    if (!qty.equals("")) {
                        int value = stringToInt(qty);
                        if (value > 0) {
                            float _rate = rate * value;
                            ed_Amnt.setText(String.valueOf(_rate));
                        } else {
                            toast.setText("Qty Can Not Be Zero");
                            toast.show();
                        }
                    }
                }else {
                    toast.setText("Please Select Rate First");
                    toast.show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                addToCart();
                break;
            case R.id.tv_prodAll:
                showProdAlertDialog();
                break;
            case R.id.tv_priceAll:
                showRateAlertDialog();
                break;
            case R.id.tv_remove:
                removeQty();
                break;
            case R.id.tv_add:
                addQty();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        new Constant(CashMemoActivity.this).doFinish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.userprofile_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                new Constant(CashMemoActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(ProductClass prod) {
        productClass = prod;
    }

    @Override
    public void onItemClick(RateMasterClass _rate) {
        rateMasterClass = _rate;
        rate = _rate.getRate();
        ed_Amnt.setText(String.valueOf(_rate.getRate()));
    }

    @Override
    public void calculation(float qty, float amnt) {
        tv_totalQty.setText(String.valueOf(totQty));
        tv_totalAmnt.setText(String.valueOf(totAmnt));
    }

    private void init(){
        constant = new Constant(CashMemoActivity.this);
        db = new DBHandlerR(getApplicationContext());
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        rv_Prod = findViewById(R.id.rv_prod);
        rv_Price = findViewById(R.id.rv_price);
        rv_Order = findViewById(R.id.rv_Order);
        tv_prodAll = findViewById(R.id.tv_prodAll);
        tv_priceAll = findViewById(R.id.tv_priceAll);
        tv_remove = findViewById(R.id.tv_remove);
        tv_add = findViewById(R.id.tv_add);
        tv_totalQty = findViewById(R.id.total_qty);
        tv_totalAmnt = findViewById(R.id.total_amount);
        ed_Qty = findViewById(R.id.ed_Qty);
        ed_Amnt = findViewById(R.id.ed_amnt);
        btn_Add = findViewById(R.id.btn_add);

        tv_prodAll.setOnClickListener(this);
        tv_priceAll.setOnClickListener(this);
        tv_remove.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        tv_remove.setOnClickListener(this);
        btn_Add.setOnClickListener(this);

        prodList = new ArrayList<>();
        rateList = new ArrayList<>();
        cartList = new ArrayList<>();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_Order.setLayoutManager(mLayoutManager);
        rv_Order.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager mLayoutManager1 =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_Prod.setLayoutManager(mLayoutManager1);
        rv_Prod.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager mLayoutManager2 =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_Price.setLayoutManager(mLayoutManager2);
        rv_Price.setItemAnimator(new DefaultItemAnimator());
    }

    private void setProdData(){
        prodList.clear();
        for(int i=0;i<11;i++){
            ProductClass prod = new ProductClass();
            prod.setProduct_Name("Prod "+i);
            prod.setSelected(false);
            prodList.add(prod);
            RateMasterClass rate = new RateMasterClass();
            rate.setRate(1000 + i);
            rate.setSelected(false);
            rateList.add(rate);
        }
        ProductListRecyclerAdapter adapter = new ProductListRecyclerAdapter(getApplicationContext(),prodList);
        adapter.setOnClickListener1(this);
        rv_Prod.setAdapter(adapter);
        ProductRateRecyclerAdapter adapter1 = new ProductRateRecyclerAdapter(getApplicationContext(),rateList);
        adapter1.setOnClickListener1(this);
        rv_Price.setAdapter(adapter1);

    }

    private void showProdAlertDialog() {
        GridView gridView = new GridView(this);
        gridView.setAdapter(new AllProductAdapter(getApplicationContext(),prodList));
        gridView.setNumColumns(5);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            // do something here
        });
        // Set grid view to alertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(gridView);
        builder.setTitle("All Product");
        builder.show();
    }

    private void showRateAlertDialog() {
        GridView gridView = new GridView(this);
        gridView.setAdapter(new AllRateAdapter(getApplicationContext(),rateList));
        gridView.setNumColumns(5);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            // do something here
        });
        // Set grid view to alertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(gridView);
        builder.setTitle("All Rate");
        builder.show();
    }

    private void removeQty(){
        String qty = ed_Qty.getText().toString();
        String amnt = ed_Amnt.getText().toString();
        if(!amnt.equals("")) {
            if (!qty.equals("")) {
                int value = stringToInt(qty);
                if (value > 1) {
                    value = value - 1;
                    float _rate = rate * value;
                    ed_Qty.setText(String.valueOf(value));
                    ed_Amnt.setText(String.valueOf(_rate));
                } else {
                    toast.setText("Qty Can Not Be Zero");
                    toast.show();
                }
            } else {
                toast.setText("Please Enter Quantity");
                toast.show();
            }
        }else {
            toast.setText("Please Select Rate First");
            toast.show();
        }
    }

    private void addQty() {
        String qty = ed_Qty.getText().toString();
        String amnt = ed_Amnt.getText().toString();
        if (!amnt.equals("")) {
            if (!qty.equals("")) {
                int value = stringToInt(qty);
                if (value >= 0) {
                    value = value + 1;
                    float _rate = rate * value;
                    ed_Qty.setText(String.valueOf(value));
                    ed_Amnt.setText(String.valueOf(_rate));
                } else {
                    toast.setText("Please Enter Proper Value");
                    toast.show();
                }
            } else {
                toast.setText("Please Enter Quantity");
                toast.show();
            }
        } else {
            toast.setText("Please Select Rate First");
            toast.show();
        }
    }

    private void addToCart(){
        if(productClass!=null){
            if(rateMasterClass!=null){
                String qty = ed_Qty.getText().toString();
                String amnt = ed_Amnt.getText().toString();
                if (!amnt.equals("")) {
                    if (!qty.equals("")) {
                        AddToCartClass addToCart = new AddToCartClass();
                        addToCart.setProdName(productClass.getProduct_Name());
                        addToCart.setQty(stringToInt(qty));
                        addToCart.setRate(rateMasterClass.getRate());
                        addToCart.setAmnt(Float.parseFloat(amnt));
                        totQty = totQty + stringToInt(qty);
                        totAmnt = totAmnt + Float.parseFloat(amnt);
                        cartList.add(addToCart);
                        if(cartList.size()==1) {
                            adapter = new AddToCartRecyclerAdapter(getApplicationContext(), cartList);
                            adapter.setOnClickListener1(this);
                            rv_Order.setAdapter(adapter);
                        }else{
                            adapter.notifyDataSetChanged();
                        }
                        /*adapter = new AddToCartRecyclerAdapter(getApplicationContext(), cartList);
                        adapter.setOnClickListener1(this);
                        rv_Order.setAdapter(adapter);*/
                        rv_Order.scrollToPosition(cartList.size()-1);
                    } else {
                        toast.setText("Please Enter Quantity");
                        toast.show();
                    }
                } else {
                    toast.setText("Please Enter Amnt");
                    toast.show();
                }
            }else{
                toast.setText("Please Select Rate First");
                toast.show();
            }
        }else{
            toast.setText("Please Select Product First");
            toast.show();
        }
    }

    private int stringToInt(String value){
        return Integer.parseInt(value);
    }

    private float stringToFloat(String value){
        return Float.parseFloat(value);
    }

    private void showDia(int a){
        AlertDialog.Builder builder = new AlertDialog.Builder(CashMemoActivity.this);
        builder.setCancelable(false);
        if (a == 0) {
            builder.setMessage("Do You Want To Exit App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
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

}
