package com.pait.smartpos;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pait.smartpos.db.DBHandlerR;
import com.pait.smartpos.fragments.GSTViewFragment;
import com.pait.smartpos.model.GSTDetailClass;
import com.pait.smartpos.model.GSTMasterClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddGSTActivity extends AppCompatActivity implements View.OnClickListener{

    private AutoCompleteTextView auto_group_name;
    private EditText ed_remark, ed_from_range, ed_to_range, ed_gstper, ed_cgstper, ed_sgstper, ed_cgstshare, ed_sgstshare, ed_cess_per;
    private Button btn_add, btn_change;
    private Switch aSwitch;
    private TextView tv_eff_date;
    private DBHandlerR db;
    private Toast toast;
    private ArrayList<String> gstGroupList,gstGroupList1;
    private List<Integer> gstMastAutoList, gstDetAutoList;
    public static int isUpdated = 0;
    /*
    {
        1-[
        [0,0,0,0,0,0,0,0]
        ],
        2-[
        [1,1000,5,2.5,2.5,50,50,0],
        [1001,2000,5,2.5,2.5,50,50,0],
        [2001,5000,12,6,6,50,50,0]
        ],
        3-[
        [1,1000,18,9,9,50,50,0]
        ]
    }
*/

    HashMap<Integer,List<List<GSTDetailClass>>> mastDetMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gstadd_fragment);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();

        auto_group_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auto_group_name.showDropDown();
                auto_group_name.setThreshold(0);
            }
        });

        auto_group_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                float _toRange = db.getMinMaxGSTGroupRange(gstGroupList.get(i));
                _toRange++;
                ed_from_range.setText(String.valueOf(_toRange));
                ed_to_range.requestFocus();
            }
        });

        /*ed_cgstshare.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ed_gstper.setText(null);
                ed_cgstper.setText(null);
                ed_sgstper.setText(null);
                String _cgstShare = ed_cgstshare.getText().toString();
                if(!_cgstShare.equals("")){
                    ed_gstper.setClickable(true);
                    ed_gstper.setFocusable(true);
                    float _cshare = Float.parseFloat(_cgstShare);
                    float _sshare = 100 - _cshare;
                    ed_sgstshare.setText(String.valueOf(_sshare));
                }else{
                    ed_sgstshare.setText(null);
                    ed_gstper.setClickable(false);
                    ed_gstper.setFocusable(false);
                }
            }
        });

        ed_gstper.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String _gstper = ed_gstper.getText().toString();
                if(!_gstper.equals("")){
                    float _gper = Float.parseFloat(_gstper);
                    float _cgstshare = Float.parseFloat(ed_cgstshare.getText().toString());
                    float _sgstshare = Float.parseFloat(ed_sgstshare.getText().toString());
                    float _cgstper = (_gper*_cgstshare)/100;
                    float _sgstper = (_gper*_sgstshare)/100;
                    ed_cgstper.setText(String.valueOf(_cgstper));
                    ed_sgstper.setText(String.valueOf(_sgstper));
                }else{
                    ed_cgstper.setText(null);
                    ed_sgstper.setText(null);
                }
            }
        });*/
    }

    void init(){
        auto_group_name = (AutoCompleteTextView) findViewById(R.id.auto_group_name);
        tv_eff_date = (TextView) findViewById(R.id.tv_eff_date);
        ed_remark = (EditText) findViewById(R.id.ed_remark);
        ed_from_range = (EditText) findViewById(R.id.ed_from_range);
        ed_to_range = (EditText) findViewById(R.id.ed_to_range);
        ed_gstper = (EditText) findViewById(R.id.ed_gst_per);
        ed_cgstper = (EditText) findViewById(R.id.ed_cgst_per);
        ed_sgstper = (EditText) findViewById(R.id.ed_sgst_per);
        ed_cgstshare = (EditText) findViewById(R.id.ed_cgst_share);
        ed_sgstshare = (EditText) findViewById(R.id.ed_sgst_share);
        ed_cess_per = (EditText) findViewById(R.id.ed_cess_per);
        btn_change = (Button) findViewById(R.id.btn_change_groupname);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        btn_change.setOnClickListener(this);
        aSwitch = (Switch) findViewById(R.id.switch1);
        db = new DBHandlerR(getApplicationContext());
        gstGroupList = new ArrayList<>();
        gstGroupList1 = new ArrayList<>();
        setList();
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        gstMastAutoList = new ArrayList<>();
        gstDetAutoList = new ArrayList<>();
        mastDetMap = new HashMap<>();
        tv_eff_date.setText(new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add:
                validate();
                break;
            case R.id.btn_change_groupname:
                setList();
                clearField(0);
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

    void setList(){
        gstGroupList.clear();
        Cursor res = db.getActiveGSTGroup();
        if(res.moveToFirst()){
            do{
                gstGroupList.add(res.getString(1));
            }while (res.moveToNext());
        }
        auto_group_name.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.cust_drop_down,gstGroupList));
    }

    void validate(){
        boolean stat = true;
        EditText view = null;
        String msg = "Required";
        float _from = 0, _to = 0;
        if(auto_group_name.getText().toString().equals("") && auto_group_name.getText().toString()!=null){
            auto_group_name.setError("Required");
            auto_group_name.requestFocus();
        }
        if(isEmpty(ed_from_range)){
            stat = false;
            view = ed_from_range;
        }
        else if(isEmpty(ed_to_range)){
            stat = false;
            view = ed_to_range;
        }else if(isEmpty(ed_gstper)){
            stat = false;
            view = ed_gstper;
        }else if(isEmpty(ed_cgstper)){
            stat = false;
            view = ed_cgstper;
        }else if(isEmpty(ed_sgstper)){
            stat = false;
            view = ed_sgstper;
        }else if(isEmpty(ed_cgstshare)){
            stat = false;
            view = ed_cgstshare;
        }else if(isEmpty(ed_sgstshare)){
            stat = false;
            view = ed_sgstshare;
        }else if(isEmpty(ed_cess_per)){
            stat = false;
            view = ed_cess_per;
        }

        if(!isEmpty(ed_from_range) && !isEmpty(ed_to_range)){
            _from = Float.parseFloat(ed_from_range.getText().toString());
            _to = Float.parseFloat(ed_to_range.getText().toString());
            if(_from>=_to){
                stat = false;
                view = ed_to_range;
                msg = "Enter Proper Value";
            }
        }

        if(!stat){
            if(view!=null) {
                view.setError(msg);
                view.requestFocus();
            }
        }else{
            int mastAuto = db.isGroupNameAlreadyPresent(auto_group_name.getText().toString());
            if(mastAuto==0){
                mastAuto = addGSTMaster();
                addGSTDetail(mastAuto);
            }else{
                float _toRange = db.getMinMaxGSTGroupRange(mastAuto);
                if(_toRange==0){
                    addGSTDetail(mastAuto);
                }else{
                    if(_from>_toRange && _to>_toRange){
                        addGSTDetail(mastAuto);
                    }else{
                        toast.setText("This Range Already Present For This Group");
                        toast.show();
                    }
                }
            }
        }
    }

    int addGSTMaster(){
        String str;
        if(aSwitch.isChecked()){
            str = "Y";
        }else{
            str = "N";
        }
        GSTMasterClass gst = new GSTMasterClass();
        gst.setGroupName(auto_group_name.getText().toString());
        gst.setEff_date(tv_eff_date.getText().toString());
        //TODO: set CrBy
        gst.setCrby(1);
        gst.setRemark(ed_remark.getText().toString());
        gst.setStatus(str);
        return db.addGSTMaster(gst);
    }

    void addGSTDetail(int mastAuto) {
        GSTDetailClass gstD = new GSTDetailClass();
        gstD.setMastAuto(mastAuto);
        gstD.setFromRange(toFloat(ed_from_range.getText().toString()));
        gstD.setToRange(toFloat(ed_to_range.getText().toString()));
        gstD.setGstPer(toFloat(ed_gstper.getText().toString()));
        gstD.setCgstPer(toFloat(ed_cgstper.getText().toString()));
        gstD.setSgstPer(toFloat(ed_sgstper.getText().toString()));
        gstD.setCgstShare(toFloat(ed_cgstshare.getText().toString()));
        gstD.setSgstShare(toFloat(ed_sgstshare.getText().toString()));
        gstD.setCessPer(toFloat(ed_cess_per.getText().toString()));
        db.addGSTDetail(gstD);
        showDia(0);
    }

    void clearField(int i){
        ed_remark.setText(null);
        ed_from_range.setText(null);
        ed_to_range.setText(null);
        ed_gstper.setText(null);
        ed_cgstper.setText(null);
        ed_sgstper.setText(null);
        ed_cgstshare.setText(null);
        ed_sgstshare.setText(null);
        ed_cess_per.setText(null);
        ed_from_range.requestFocus();
        if(i==0) {
            auto_group_name.setText(null);
            auto_group_name.requestFocus();
        }
    }

    float toFloat(String str){
        return Float.parseFloat(str);
    }

    boolean isEmpty(EditText ed){
        return ed.getText().toString().isEmpty() || ed.getText().toString().equals("") || ed.getText().toString()==null;
    }

    void showDia(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddGSTActivity.this);
        builder.setCancelable(false);
        builder.setMessage("GST Added Successfully");
        if(i==0) {
            builder.setPositiveButton("Add More", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    clearField(1);
                    dialogInterface.dismiss();
                }
            });
            builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    GSTViewFragment.isUpdated = 1;
                    doFinish();
                    dialogInterface.dismiss();
                }
            });
        }
        builder.create().show();
    }

    void doFinish(){
        finish();
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }
}
