package com.prim8software.app.prim8datalogger;

// Part of Prim8 Data Logger
// Copyright 2016, Scott Johnson, Prim8 Software (scott.johnson@prim8software.com)

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity implements LocationListener {

    ArrayList<String> labels = new ArrayList<String>();
    ArrayList<HashMap<String,String>> listViewItems=new ArrayList<HashMap<String,String>>();
    //LogListViewAdapter listViewAdapter;
    ArrayAdapter<String> listViewAdapter;
    LocationManager locationManager;
    String latitudeString, longitudeString, locationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //--LISTVIEW--
        ListView lv = (ListView) this.findViewById(R.id.listView);
        //listViewAdapter=new LogListViewAdapter(this, listViewItems);
        listViewAdapter = new ArrayAdapter<String>(  this,
                android.R.layout.simple_list_item_1,
                labels
        );
        lv.setAdapter(listViewAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                HashMap<String,String> entry = listViewItems.get(position);
                String ss = "Name:\n" + (String)entry.get(Constants.DATA_KEY) +
                        "\nTime:\n" + (String)entry.get(Constants.TIME_KEY) +
                        "\nLatitude:\n" + (String)entry.get(Constants.LAT_KEY) +
                        "\nLongitude\n" + (String)entry.get(Constants.LONG_KEY) +
                        "\n";
                Log.d("MainActivity",ss);
                Toast.makeText(getBaseContext(), ss, Toast.LENGTH_LONG).show();
            }
        });



        //--TEXT LISTENER--
        EditText etUserInput = (EditText) findViewById(R.id.editText);
        {
            etUserInput.addTextChangedListener(new UserEntryWatcher(this));
            //, findViewById(R.id.listView)));
            etUserInput.setFocusable(true);
            etUserInput.setFocusableInTouchMode(true);
            etUserInput.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Constants.text_dip_size);
        }




        //--LOCATION--
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        locationProvider = locationManager.getBestProvider(criteria, false);
        if (locationProvider != null && !locationProvider.equals(""))
        {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                return;
            }
            Location location = locationManager.getLastKnownLocation(locationProvider);
            locationManager.requestLocationUpdates(locationProvider, 15000, 1, this);
            if (location != null)
                onLocationChanged(location);
            else
                Toast.makeText(getBaseContext(), "No Location Provider Found", Toast.LENGTH_SHORT).show();
        }



        //--INITIALIZE LIST--
        LoadListItems();



        //--SAVE FILES--
        Button button = (Button) findViewById(R.id.button);
        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Constants.text_dip_size);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    boolean ok = isExternalStorageWritable();
                    if(ok)
                    {
                        File file = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOCUMENTS), Constants.OUTPUT_FILE_NAME + String.valueOf(Common.CurrentTime()) + ".csv");
                        if(file.createNewFile())
                        {

                            if (file.canWrite() || file.setWritable(true)) {
                                FileWriter fw = new FileWriter(file);
                                BufferedWriter bw = new BufferedWriter(fw);
                                WriteListItems(bw);
                                bw.close();
                            } else {
                                Toast.makeText(getBaseContext(), "Cannot write file", Toast.LENGTH_SHORT).show();
                                ok = false;
                            }
                        }
                        else {
                            Toast.makeText(getBaseContext(), "Cannot create file", Toast.LENGTH_SHORT).show();
                            ok = false;
                        }
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        longitudeString = String.valueOf(location.getLongitude());
        latitudeString = String.valueOf(location.getLatitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void UpdateListView(String dataString, Integer time)
    {
        String timeString = Common.TimeToString(time);

        //--CREATE NEW ENTRY AND ADD TO listViewItems--
        HashMap<String,String> newEntry = new HashMap<>();
        newEntry.put(Constants.DATA_KEY, dataString);
        newEntry.put(Constants.TIME_KEY, timeString);
        newEntry.put(Constants.LAT_KEY, latitudeString);
        newEntry.put(Constants.LONG_KEY, longitudeString);

        //--CACHE IN SHARED PREFERENCES--
        SaveListItem(dataString, timeString, latitudeString, longitudeString);

        listViewItems.add(0, newEntry);
        labels.add(0, dataString);
        listViewAdapter.notifyDataSetChanged();
    }

    private boolean SaveListItem(String dataString, String timeString, String latitudeString, String longitudeString) {
        SharedPreferences prefs = getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        String sizeString = String.valueOf(listViewItems.size());
        try {
            String s = Constants.ARRAY_ITEM_PREFIX + "_LAST_INDEX";
            editor.putInt(s, listViewItems.size());
            s = Constants.ARRAY_ITEM_PREFIX + "_" + Constants.DATA_KEY + "_" + sizeString;
            editor.putString(s, dataString);
            s = Constants.ARRAY_ITEM_PREFIX + "_" + Constants.TIME_KEY + "_" + sizeString;
            editor.putString(s, timeString);
            s = Constants.ARRAY_ITEM_PREFIX + "_" + Constants.LAT_KEY + "_" + sizeString;
            editor.putString(s, latitudeString);
            s = Constants.ARRAY_ITEM_PREFIX + "_" + Constants.LONG_KEY + "_" + sizeString;
            editor.putString(s, longitudeString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return editor.commit();
    }

    private void LoadListItems()
    {
        SharedPreferences prefs = getDefaultSharedPreferences(this);
        try {
            listViewItems.clear();
            labels.clear();
            String s = Constants.ARRAY_ITEM_PREFIX + "_LAST_INDEX";
            int ilast = prefs.getInt(s, -1);
            for(int i = 0; i <= ilast; i++)
            {
                HashMap<String,String> newEntry =new HashMap<>();
                String sizeString = String.valueOf(i);
                s = Constants.ARRAY_ITEM_PREFIX + "_" + Constants.DATA_KEY + "_" + sizeString;
                String ss = prefs.getString(s,null);
                newEntry.put(Constants.DATA_KEY, prefs.getString(s,null));
                s = Constants.ARRAY_ITEM_PREFIX + "_" + Constants.TIME_KEY + "_" + sizeString;
                newEntry.put(Constants.TIME_KEY, prefs.getString(s,null));
                s = Constants.ARRAY_ITEM_PREFIX + "_" + Constants.LAT_KEY + "_" + sizeString;
                newEntry.put(Constants.LAT_KEY, prefs.getString(s,null));
                s = Constants.ARRAY_ITEM_PREFIX + "_" + Constants.LONG_KEY + "_" + sizeString;
                newEntry.put(Constants.LONG_KEY, prefs.getString(s,null));
                if(prefs.getString(s,null).length()>0) {
                    listViewItems.add(0, newEntry);
                    labels.add(0,ss);
                }
            }
            listViewAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean WriteListItems(BufferedWriter bw)
    {
        Iterator<HashMap<String,String>> iter = listViewItems.iterator();
        boolean ok = true;
        try {
            while (iter.hasNext()) {
                HashMap<String, String> item = iter.next();
                String dataString = (String) item.get(Constants.DATA_KEY);
                if (dataString.contains(",")) {
                    dataString = "\"" + dataString + "\"";
                }
                String timeString = (String) item.get(Constants.TIME_KEY);
                String latitudeString = (String) item.get(Constants.LAT_KEY);
                String longitudeString = (String) item.get(Constants.LONG_KEY);
                String s = dataString + "," + timeString + "," + latitudeString + "," + longitudeString + "\n";
                bw.write(s);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            ok = false;
        }
        if(ok)
            ClearListItems();
        return ok;
    }

    private boolean ClearListItems()
    {
        SharedPreferences prefs = getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            listViewItems.clear();
            labels.clear();
            String s = Constants.ARRAY_ITEM_PREFIX + "_LAST_INDEX";
            int ilast = prefs.getInt(s, listViewItems.size());
            editor.putInt(s, 0);
            for(int i = 0; i <= ilast; i++)
            {
                String sizeString = String.valueOf(i);
                s = Constants.ARRAY_ITEM_PREFIX + "_" + Constants.DATA_KEY + "_" + sizeString;
                editor.remove(s);
                s = Constants.ARRAY_ITEM_PREFIX + "_" + Constants.TIME_KEY + "_" + sizeString;
                editor.remove(s);
                s = Constants.ARRAY_ITEM_PREFIX + "_" + Constants.LAT_KEY + "_" + sizeString;
                editor.remove(s);
                s = Constants.ARRAY_ITEM_PREFIX + "_" + Constants.LONG_KEY + "_" + sizeString;
                editor.remove(s);
            }
            listViewAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return editor.commit();
    }

}
