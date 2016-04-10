package com.prim8software.app.prim8datalogger;

// Part of Prim8 Data Logger
// Copyright 2016, Scott Johnson, Prim8 Software (scott.johnson@prim8software.com)

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LogListViewAdapter extends BaseAdapter{

    public ArrayList<HashMap<String, String>> list;
    Activity activity;

    TextView txtFirst;
//    TextView txtSecond;
//    TextView txtThird;
//    TextView txtFourth;

    public LogListViewAdapter(Activity activity, ArrayList<HashMap<String, String>> list)
    {
        super();
        this.activity=activity;
        this.list=list;
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater=activity.getLayoutInflater();
        if(convertView == null){

            convertView=inflater.inflate(R.layout.log_layout, null);

            txtFirst=(TextView) convertView.findViewById(R.id.textView1);
            txtFirst.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Constants.text_dip_size);
//            txtSecond=(TextView) convertView.findViewById(R.id.textView2);
//            txtThird=(TextView) convertView.findViewById(R.id.textView3);
//            txtFourth=(TextView) convertView.findViewById(R.id.textView4);

        }

        HashMap<String, String> map=list.get(position);
        String s = map.get(Constants.DATA_KEY);
        txtFirst.setText(s);
//        txtSecond.setText(map.get(Constants.TIME_KEY));
//        txtThird.setText(map.get(Constants.LAT_KEY));
//        txtFourth.setText(map.get(Constants.LONG_KEY));

        return convertView;
    }

}
