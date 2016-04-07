package com.example.seasidelee.sportsclock;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;


public class CreateClock extends Activity {
    public DatabaseHelper clockdatabese = new DatabaseHelper(this);
    private TimePicker timepick;
    private LinearLayout ima;
    private CheckBox[] week = new CheckBox[7];
    private CheckBox danci;
    private CheckBox gongzuo;
    private CheckBox meitian;
    private CheckBox shuangxiu;
    private EditText Lable;
    private TextView songtitle;
    private ImageView queren;
    private ImageView quxiao;
    private Button shanchu;
    private int clockid;
    private long songUri;
    boolean flag = true;
    public static CreateClock instance = null;
    public boolean iscreate;
    public boolean isreturnfromselect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_clock);
        timepick = (TimePicker)findViewById(R.id.timepick);
        timepick.setIs24HourView(true);
        instance = CreateClock.this;


        week[0] = (CheckBox)findViewById(R.id.zhouyi);
        week[1] = (CheckBox)findViewById(R.id.zhouer);
        week[2] = (CheckBox)findViewById(R.id.zhousan);
        week[3] = (CheckBox)findViewById(R.id.zhousi);
        week[4] = (CheckBox)findViewById(R.id.zhouwu);
        week[5] = (CheckBox)findViewById(R.id.zhouliu);
        week[6] = (CheckBox)findViewById(R.id.zhouri);

        danci = (CheckBox)findViewById(R.id.danci);
        gongzuo = (CheckBox)findViewById(R.id.gongzuori);
        meitian = (CheckBox)findViewById(R.id.meitian);
        shuangxiu = (CheckBox)findViewById(R.id.shuangxiu);

        Lable = (EditText)findViewById(R.id.lable);
        songtitle = (TextView)findViewById(R.id.songtitle);
        queren = (ImageView)findViewById(R.id.queren);
        quxiao = (ImageView)findViewById(R.id.quxiao);
        shanchu = (Button)findViewById(R.id.shanchu);

        Checkboxlistener1 listener1 = new Checkboxlistener1();
        Checkboxlistener2 listener2 = new Checkboxlistener2();
        danci.setOnCheckedChangeListener(listener2);
        gongzuo.setOnCheckedChangeListener(listener2);
        meitian.setOnCheckedChangeListener(listener2);
        shuangxiu.setOnCheckedChangeListener(listener2);
        for(int i = 0; i < 7; i++){
            week[i].setOnCheckedChangeListener(listener1);
        }
        danci.setChecked(true);

        Intent intent = this.getIntent();
        int clockposition = intent.getIntExtra("clockposition", -1);
        timepick.setCurrentHour(intent.getIntExtra("hour", 7));
        timepick.setCurrentMinute(intent.getIntExtra("minute", 30));
        if(clockposition >= 0){
            boolean[] date = intent.getBooleanArrayExtra("date");
            for(int i = 0; i < 7; i++)
                week[i].setChecked(date[i]);
            Lable.setText((CharSequence) intent.getStringExtra("lable"));
            songtitle.setText((CharSequence) intent.getStringExtra("songtitle"));
        }
        iscreate = intent.getBooleanExtra("iscreate",false);
        if(iscreate) shanchu.setVisibility(View.INVISIBLE);
        isreturnfromselect = intent.getBooleanExtra("isreturnfromselect", false);
        clockid = intent.getIntExtra("ID", 0);

        songUri = intent.getLongExtra("Uri", -1);
        if(songUri >= 0) {
            songtitle.setText((CharSequence) intent.getStringExtra("songtitle"));
            if(isreturnfromselect) SelectSongs.instance.finish();
        }

        queren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songUri < 0){
                    dialog();
                }
                else {
                    Clock newclock = new Clock();
                    newclock.hour = timepick.getCurrentHour();
                    newclock.minute = timepick.getCurrentMinute();
                    for (int i = 0; i < 7; i++)
                        newclock.day[i] = week[i].isChecked();
                    newclock.lable = Lable.getText().toString();
                    newclock.songtitle = songtitle.getText().toString();
                    newclock.Uri = songUri;
                    newclock.id = clockid;
                    if (iscreate) clockdatabese.addContact(newclock);
                    else clockdatabese.updateContact(newclock);
                    MainActivity.instance.finish();
                    Intent save = new Intent();
                    save.setClass(CreateClock.this, MainActivity.class);
                    startActivity(save);
                    CreateClock.this.finish();
                }
            }
        });

        quxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateClock.this.finish();
            }
        });

        shanchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteclock();
            }
        });

        Songlistener songlistener = new Songlistener();
        ima = (LinearLayout)findViewById(R.id.xuange);
        ima.setOnClickListener(songlistener);
    }

    class Songlistener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            boolean[] date = new boolean[7];
            String lable = Lable.getText().toString();
            for(int i = 0; i < 7; i++)
                date[i] = week[i].isChecked();
            Intent selectsong = new Intent();
            selectsong.setClass(CreateClock.this, SelectSongs.class);
            selectsong.putExtra("hour", timepick.getCurrentHour());
            selectsong.putExtra("minute", timepick.getCurrentMinute());
            selectsong.putExtra("date", date);
            selectsong.putExtra("lable", lable);
            selectsong.putExtra("Uri", songUri);
            selectsong.putExtra("ID", clockid);
            selectsong.putExtra("iscreate", iscreate);
            startActivity(selectsong);
        }
    }

    class Checkboxlistener1 implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            boolean flagdanci = true;
            boolean flaggongzuo = true;
            boolean flagmeitian = true;
            boolean flagshuangxiu = true;

            for(int i = 0; i < 5; i++){
                if(!week[i].isChecked()){
                    flaggongzuo = false;
                    flagmeitian = false;
                }
                else{
                    flagshuangxiu = false;
                    flagdanci = false;
                }
            }
            for(int i = 5; i < 7; i++){
                if(!week[i].isChecked()){
                    flagshuangxiu = false;
                    flagmeitian = false;
                }
                else{
                    flaggongzuo = false;
                    flagdanci = false;
                }
            }
            flag = false;
            if(flagdanci) danci.setChecked(true);
            else danci.setChecked(false);
            if(flaggongzuo) gongzuo.setChecked(true);
            else gongzuo.setChecked(false);
            if(flagmeitian) meitian.setChecked(true);
            else meitian.setChecked(false);
            if(flagshuangxiu) shuangxiu.setChecked(true);
            else shuangxiu.setChecked(false);
            flag = true;
        }
    }

    class Checkboxlistener2 implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(buttonView.getId() == danci.getId()){
                if(isChecked && flag)
                    for (int i = 0; i < 7; i++)
                        week[i].setChecked(false);
                else if(flag) meitian.setChecked(true);
            }
            else if(buttonView.getId() == gongzuo.getId()){
                if(isChecked && flag){
                    for (int i = 0; i < 5; i++)
                        week[i].setChecked(true);
                    for (int i = 5; i < 7; i++)
                        week[i].setChecked(false);
                }
                else if(flag) danci.setChecked(true);
            }
            else if(buttonView.getId() == meitian.getId()){
                if(isChecked && flag)
                    for (int i = 0; i < 7; i++)
                        week[i].setChecked(true);
                else if(flag) danci.setChecked(true);
            }
            else{
                if(isChecked && flag){
                    for (int i = 0; i < 5; i++)
                        week[i].setChecked(false);
                    for (int i = 5; i < 7; i++)
                        week[i].setChecked(true);
                }
                else if(flag) danci.setChecked(true);
            }
        }
    }

    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateClock.this);
        builder.setMessage("请选择铃声！");
        builder.setTitle("蠢死了……");
        builder.setPositiveButton("哦！", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    protected void deleteclock() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateClock.this);
        builder.setMessage("删除此闹钟？");
        builder.setTitle("suibianjiade");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                clockdatabese.deleteclock(clockid);
                Intent save = new Intent();
                MainActivity.instance.finish();
                save.setClass(CreateClock.this, MainActivity.class);
                startActivity(save);
                CreateClock.this.finish();
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


}
