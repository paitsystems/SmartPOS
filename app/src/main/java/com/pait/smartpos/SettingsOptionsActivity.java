package com.pait.smartpos;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.pait.smartpos.bluetooth_printer.PrinterOptions;
import com.pait.smartpos.db.DBHandlerR;

import java.util.ArrayList;
import java.util.List;

public class SettingsOptionsActivity extends AppCompatActivity {

    ListView listView;
    List<String> optionList;
    DBHandlerR db;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_options);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();

        listView.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.list_item_option_setttings,optionList));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("Log",(String)adapterView.getItemAtPosition(i) + i);
                startActivity(i);
            }
        });
    }

    void init(){
        listView = (ListView) findViewById(R.id.listView);
        optionList = new ArrayList<>();
        optionList.add("TableClass Settings");//0
        optionList.add("CategoryClass Settings");//1
        optionList.add("Tax Settings");//2
        optionList.add("Bluetooth Settings");//3
        optionList.add("Reports");//4
        optionList.add("Company Info Settings");//5
        optionList.add("Credentials Settings");//6
        optionList.add("Clear Data");//7
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        db = new DBHandlerR(getApplicationContext());
    }

    void startActivity(int i){
        Intent intent1 = null;
        switch (i){
            case 0:
                intent1 = new Intent(getApplicationContext(), AddTableMasterActivity.class);
                intent1.putExtra("from","FromSettingsOptions");
                break;
            case 1:
                intent1 = new Intent(getApplicationContext(), AddProductMasterActivity.class);
                intent1.putExtra("from","FromSettingsOptions");
                break;
            case 2:startActivity(new Intent(getApplicationContext(),TaxMasterActivity.class));
                overridePendingTransition(R.anim.enter,R.anim.exit);
                break;
            case 3:
                intent1 = new Intent(getApplicationContext(),PrinterOptions.class);
                break;
            case 4:
                intent1 = new Intent(getApplicationContext(),ReportListActivity.class);
                break;
            case 7:
                showDia(1);
                break;
        }
        startActivity(intent1);
        overridePendingTransition(R.anim.enter,R.anim.exit);
    }

    void showDia(int i){
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsOptionsActivity.this);
        builder.setCancelable(false);
        if(i==1) {
            builder.setMessage("Do You Really Want To Clear All Data?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    db.deleteTable(DBHandlerR.TempTable_Table);
                    db.deleteTable(DBHandlerR.BillMaster_Table);
                    db.deleteTable(DBHandlerR.BillDetail_Table);
                    toast.setText("Data Clear Successfully");
                    toast.show();
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

    void doFinish(){
        toast.cancel();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
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

}
