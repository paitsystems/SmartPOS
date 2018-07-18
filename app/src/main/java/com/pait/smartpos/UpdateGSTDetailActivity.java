package com.pait.smartpos;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
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
import java.util.List;
import java.util.Locale;

public class UpdateGSTDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private AutoCompleteTextView auto_group_name;
    private EditText ed_remark, ed_from_range, ed_to_range, ed_gstper, ed_cgstper,
            ed_sgstper, ed_cgstshare, ed_sgstshare, ed_cess_per;
    private Button btn_update;
    private Switch aSwitch;
    private TextView tv_eff_date;
    private DBHandlerR db;
    private Toast toast;
    private ArrayList<String> gstGroupList,gstGroupList1;
    private List<Integer> gstMastAutoList, gstDetAutoList;
    private int mastAuto,detAuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_gstdetail);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();

        String str = (String) getIntent().getExtras().get("str");
        String arg[] = str.split("-");
        mastAuto = Integer.parseInt(arg[0]);
        detAuto = Integer.parseInt(arg[1]);
        setData(mastAuto,detAuto);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update:
                updateGSTDetail();
                break;
        }
    }

    private void init(){
        auto_group_name = (AutoCompleteTextView) findViewById(R.id.auto_group_name);
        auto_group_name.setClickable(false);
        auto_group_name.setFocusable(false);
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
        btn_update = (Button) findViewById(R.id.btn_update);
        btn_update.setOnClickListener(this);
        aSwitch = (Switch) findViewById(R.id.switch1);
        db = new DBHandlerR(getApplicationContext());
        gstGroupList = new ArrayList<>();
        gstGroupList1 = new ArrayList<>();

        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        gstMastAutoList = new ArrayList<>();
        gstDetAutoList = new ArrayList<>();

        tv_eff_date.setText(new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime()));
    }

    private void setData(int mastAuto, int detAuto){
        Cursor res = db.getGSTDetail(mastAuto,detAuto);
        if(res.moveToFirst()) {
            do {
                ed_from_range.setText(res.getString(2));
                ed_to_range.setText(res.getString(3));
                ed_gstper.setText(res.getString(4));
                ed_cgstper.setText(res.getString(5));
                ed_sgstper.setText(res.getString(6));
                ed_cgstshare.setText(res.getString(7));
                ed_sgstshare.setText(res.getString(8));
                ed_cess_per.setText(res.getString(9));
                auto_group_name.setText(res.getString(10));
                if(res.getString(11).equals("Y")) {
                    aSwitch.setChecked(true);
                }else{
                    aSwitch.setChecked(false);
                }
            }while (res.moveToNext());
        }
        res.close();
    }

    private void doFinish(){
        finish();
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }

    private void updateGSTDetail() {
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
        db.updateGSTDetail(gstD,String.valueOf(mastAuto),String.valueOf(detAuto));
        toast.setText("Group Detail Updated Successfully");
        toast.show();
        updateGSTMaster();
    }

    private void updateGSTMaster(){
        String str;
        if(aSwitch.isChecked()){
            str = "Y";
        }else{
            str = "N";
        }
        GSTMasterClass gst = new GSTMasterClass();
        gst.setStatus(str);
        db.updateGSTMaster(gst,String.valueOf(mastAuto));
        showDia(0);
    }

    private float toFloat(String str){
        return Float.parseFloat(str);
    }

    private void showDia(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateGSTDetailActivity.this);
        builder.setCancelable(false);
        if(i==0) {
            builder.setMessage("GST Detail Updated Successfully");
            builder.setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
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

}
