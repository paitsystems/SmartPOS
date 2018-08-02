package com.pait.smartpos.model;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;
import com.pait.smartpos.R;
import com.pait.smartpos.constant.Constant;

public class MyMarkerView extends MarkerView {

    TextView tvContent,tvContent1 ;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerview only displays a textview
        tvContent = (TextView) findViewById(R.id.tvContent);
        //tvContent1 = (TextView) findViewById(R.id.tvContent1);


    }

// callbacks everytime the MarkerView is redrawn, can be used to update the
// content (user-interface)

    @Override
    public void refreshContent(Entry e, Highlight highlight)
    {
        //super.refreshContent(e, highlight);
        PieEntry pe = (PieEntry) e;
       // tvContent.setText(pe.getLabel());



        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvContent.setText("" + Utils.formatNumber(ce.getHigh(), 0, true));
        } else {
            tvContent.setText( String.valueOf(e.getY()) +"\n"+pe.getLabel());
            //tvContent1.setText(pe.getLabel());
            //tvContent.setText("" + Utils.formatNumber(e.getY(), 0, true));
        }

        super.refreshContent(e, highlight);
        // here you can change whatever you want to show in following line as x/y or both
        //tvContent.setText( String.valueOf(e.getY()) ); // set the entry-value as the display text

    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        super.draw(canvas, posX, posY);
        tvContent.setText("x: " + posX + " , y: " + posY); // draw the marker
    }
}
