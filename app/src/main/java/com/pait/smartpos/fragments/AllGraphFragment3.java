package com.pait.smartpos.fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.pait.smartpos.R;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.db.DBHandler;
import com.pait.smartpos.model.CollectionClass;
import com.pait.smartpos.model.DailyPettyExpClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AllGraphFragment3 extends Fragment {

    private ListView listView;
    private DBHandler db;
    private EditText ed_search;
    public static int selPos;
    private String searchText = null;
    private Button bt_today, bt_mtd, bt_ytd;
    private PieChart pieChart;
    private List<String> xValues;
    private List<PieEntry> yValues;
    private PieDataSet pieDataSet;
    private PieData pieData;
    private int flag = 0;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    private SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
    private String currentdate = "", fadate = "", todate = "";
    private Constant constant;

    public AllGraphFragment3() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.showLog("AllGraphFragment3_onResume");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1_all_graph, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        db = new DBHandler(getContext());
        ed_search = (EditText) view.findViewById(R.id.ed_search);
        bt_today = view.findViewById(R.id.bt_today);
        bt_mtd = view.findViewById(R.id.bt_mtd);
        bt_ytd = view.findViewById(R.id.bt_ytd);
        pieChart = (PieChart) view.findViewById(R.id.piechart);
        xValues = new ArrayList<>();
        yValues = new ArrayList<>();
        constant = new Constant(getContext());

        try {
            currentdate = sdf.format(sdf1.parse(constant.getDate()));
            fadate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(sdf.parse(currentdate));
            todate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(sdf.parse(currentdate));
        } catch (Exception e) {
            e.printStackTrace();
        }


        setPieChartData(fadate, todate,flag);

        bt_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    bt_today.setBackgroundResource(R.drawable.buton_color_border);
                    bt_mtd.setBackgroundResource(R.drawable.button__color_border2);
                    bt_ytd.setBackgroundResource(R.drawable.button__color_border2);
                    bt_today.setTextColor(getResources().getColor(R.color.white));
                    bt_mtd.setTextColor(getResources().getColor(R.color.ldblue));
                    bt_ytd.setTextColor(getResources().getColor(R.color.ldblue));

                    setPieChartData(fadate, todate,flag);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        bt_mtd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt_mtd.setBackgroundResource(R.drawable.buton_color_border);
                bt_today.setBackgroundResource(R.drawable.button__color_border2);
                bt_ytd.setBackgroundResource(R.drawable.button__color_border2);
                bt_mtd.setTextColor(getResources().getColor(R.color.white));
                bt_today.setTextColor(getResources().getColor(R.color.ldblue));
                bt_ytd.setTextColor(getResources().getColor(R.color.ldblue));
                getMonthFirstDate();
            }
        });

        bt_ytd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt_ytd.setBackgroundResource(R.drawable.buton_color_border);
                bt_today.setBackgroundResource(R.drawable.button__color_border2);
                bt_mtd.setBackgroundResource(R.drawable.button__color_border2);
                bt_ytd.setTextColor(getResources().getColor(R.color.white));
                bt_today.setTextColor(getResources().getColor(R.color.ldblue));
                bt_mtd.setTextColor(getResources().getColor(R.color.ldblue));
                getYearFirstDate();
            }
        });
        return view;
    }

    private void setPieChartData(String fadate, String tadate, int flg) {
        yValues.clear();
        xValues.clear();
        String label = "";
        Cursor c = db.getExpenseReportDataGraph(fadate, tadate, flg);
        List<DailyPettyExpClass> list = new ArrayList<>();
        List<Float> list2 = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                DailyPettyExpClass exp = new DailyPettyExpClass();
                //exp.setRemark(c.getString(c.getColumnIndex(DBHandler.DPE_Remark)));
                exp.setAmount(c.getFloat(c.getColumnIndex(DBHandler.DPE_Amount)));
                list2.add(c.getFloat(c.getColumnIndex(DBHandler.DPE_Amount)));
               // exp.setDate(c.getString(c.getColumnIndex(DBHandler.DPE_Date)));
                exp.setExpHead(c.getInt(c.getColumnIndex(DBHandler.DPE_Exphead)));
                xValues.add(new DBHandler(getContext()).getExpHeadName(exp.getExpHead()));
                list.add(exp);

            } while (c.moveToNext());
        }
        c.close();

        //xValues.add("Cashier");
        /*xValues.add("NetCollection");
        xValues.add("SaleCash");
        xValues.add("CashBack");
        xValues.add("NetCash");
        xValues.add("Cheque");
        xValues.add("Card");
        xValues.add("OtherPayAmt");
        xValues.add("ExpReceipt");
        xValues.add("ExpPayment");*/

        for (int i = 0; i < list2.size(); i++) {
            CollectionClass coll = new CollectionClass();
            /*yValues.add(new PieEntry(Float.valueOf(coll.getNetCollection()), xValues.get(i)));
            yValues.add(new PieEntry(Float.valueOf(coll.getSaleCash()), xValues.get(i)));
            yValues.add(new PieEntry(Float.valueOf(coll.getCashback()), xValues.get(i)));
            yValues.add(new PieEntry(Float.valueOf(coll.getNetcash()), xValues.get(i)));
            yValues.add(new PieEntry(Float.valueOf(coll.getCheck()), xValues.get(i)));
            yValues.add(new PieEntry(Float.valueOf(coll.getCard()), xValues.get(i)));
            yValues.add(new PieEntry(Float.valueOf(coll.getOtherPayAmt()), xValues.get(i)));
            yValues.add(new PieEntry(Float.valueOf(coll.getExp_receipt()), xValues.get(i)));
            yValues.add(new PieEntry(Float.valueOf(coll.getExp_payment()), xValues.get(i)));*/
            //yValues.add(new PieEntry(Float.valueOf(list2.get(i)), xValues.get(i)));
            String labelstr = xValues.get(i)+"-"+list2.get(i);
            Constant.showLog("labelstr"+labelstr);
            yValues.add(new PieEntry(Float.valueOf(list2.get(i)), labelstr));
        }
        setChartDataStock(xValues, yValues, "");
    }

    private void setChartDataStock(List<String> xValues, List<PieEntry> yValues, String label) {
        pieDataSet = new PieDataSet(yValues, label);
        int[] colors = {R.color.purpled, R.color.red, R.color.blue, R.color.pinkd,
                R.color.lblue, R.color.buttoncolor, R.color.lightyellow, R.color.lpurple,
                R.color.lightyellow2, R.color.greend, R.color.lightgray, R.color.lgreen,
                R.color.buttoncolor, R.color.light_green2, R.color.orange, R.color.morange
                , R.color.purpule, R.color.grey,R.color.G8color,R.color.pblue2,R.color.G9color};
        pieDataSet.setColors(colors, getContext());
        // pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        //pieDataSet.setValueFormatter(new PercentFormatter());                      // for display values in percentage
        //pieDataSet.setValueFormatter(new DefaultValueFormatter(0));              // default value
        pieDataSet.setValueFormatter(new DefaultValueFormatter(0));
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(15f);
        pieDataSet.setSliceSpace(3);
        Legend legend = pieChart.getLegend();
        //legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(15f);
        legend.setTextSize(15f);
        legend.setTextColor(Color.WHITE);
        legend.setWordWrapEnabled(true);

        pieChart.setDrawSliceText(false);
        //pieData = new PieData(xValues, pieDataSet);
        pieData = new PieData(pieDataSet);
        // pieData.setDataSet(pieDataSet);
        pieChart.setData(pieData);
        com.pait.smartpos.model.MyMarkerView mv = new com.pait.smartpos.model.MyMarkerView(getContext(),R.layout.custom_marker);
        pieChart.setMarker(mv);
        //pieChart.setDescription("This is  pichart..");
        //pieChart.setCenterText("Expense");
        pieChart.setCenterTextSize(20f);
        pieChart.setCenterTextColor(R.color.red);
        //pieChart.animateXY(1400,1400);
        pieChart.animateX(1400);
    }

    private void getMonthFirstDate() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = 1;
        c.set(year, month, day);
        int numOfDaysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        Constant.showLog("First Day of month: " + c.getTime());
        try {
            int flag = 0;
            String fdate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(c.getTime());
            Constant.showLog("First Day of month:" + fdate);
            String tdate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(sdf.parse(currentdate));
            setPieChartData(fdate, tdate, flag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getYearFirstDate() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = 3;
        int day = 1;
        c.set(year, month, day);
        int numOfYearInMonth = c.getActualMaximum(Calendar.YEAR);
        Constant.showLog("year of month: " + c.getTime());
        try {
            int flag = 0;
            String fdate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(c.getTime());
            Constant.showLog("year of month:" + fdate);
            String tdate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(sdf.parse(currentdate));
            setPieChartData(fdate, tdate, flag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyMarkerView extends MarkerView {

        TextView tvContent = new TextView(getContext());

        public MyMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);
            // this markerview only displays a textview
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)

        @Override
        public void refreshContent(Entry e, Highlight highlight)
        {
            // here you can change whatever you want to show in following line as x/y or both
            tvContent.setText("x: " + e.getX() + " , y: " + e.getY()); // set the entry-value as the display text
        }

    }
}

