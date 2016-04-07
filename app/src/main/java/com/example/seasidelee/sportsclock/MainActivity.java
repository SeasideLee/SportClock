package com.example.seasidelee.sportsclock;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TimePicker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;


public class MainActivity extends Activity {
    public DatabaseHelper clockdatabese = new DatabaseHelper(this);
    private ListView listView;
    private ImageView addclk;
    private Switch onoff;
    private ArrayList<Clock> listClock = new ArrayList<Clock>();
    List<HashMap<String, String>> clocklist = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;
    public static MainActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = MainActivity.this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) this.findViewById(R.id.ClockList);

        listClock = clockdatabese.getAllClocks();
        int num = listClock.size();
        for(int i = 0; i < num; i++){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("Clocktime", ((listClock.get(i).hour < 10)?"0":"") + listClock.get(i).hour + ":"
                    + ((listClock.get(i).minute < 10)?"0":"") + listClock.get(i).minute);
            map.put("Lable", listClock.get(i).lable);
            map.put("Date", (listClock.get(i).day[0]?"周一 ":"") + (listClock.get(i).day[1]?"周二 ":"") + (listClock.get(i).day[2]?"周三 ":"")
                    + (listClock.get(i).day[3]?"周四 ":"") + (listClock.get(i).day[4]?"周五 ":"") + (listClock.get(i).day[5]?"周六 ":"")
                    + (listClock.get(i).day[6]?"周日 ":"") + ((!(listClock.get(i).day[0]||listClock.get(i).day[1]||listClock.get(i).day[2]
                    ||listClock.get(i).day[3]||listClock.get(i).day[4]||listClock.get(i).day[5]||listClock.get(i).day[6]))?"单次":""));
            clocklist.add(map);
        }
        adapter = new SimpleAdapter(this, clocklist, R.layout.clockitem, new String[] {"Clocktime","Lable","Date"}, new int[] {R.id.Clocktime, R.id.lable, R.id.date});
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Clock clock = listClock.get(position);
                    Intent createclock = new Intent();
                    createclock.setClass(MainActivity.this, CreateClock.class);
                    createclock.putExtra("clockposition", position);
                    createclock.putExtra("hour", clock.hour);
                    createclock.putExtra("minute", clock.minute);
                    createclock.putExtra("date", clock.day);
                    createclock.putExtra("lable", clock.lable);
                    createclock.putExtra("songtitle", clock.songtitle);
                    createclock.putExtra("Uri", clock.Uri);
                    createclock.putExtra("ID", clock.id);
                    startActivity(createclock);
            }
        });

        Intent alarm = new Intent();
        alarm.setClass(MainActivity.this, Alarm.class);
        startService(alarm);

        addclk = (ImageView)findViewById(R.id.addclock);
        addclk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createclock = new Intent();
                createclock.setClass(MainActivity.this, CreateClock.class);
                createclock.putExtra("iscreate", true);
                createclock.putExtra("ID", clockdatabese.nextID());
                startActivity(createclock);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
