package com.example.seasidelee.sportsclock;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.w3c.dom.ProcessingInstruction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Alarm extends Service {
    private Timer time;
    private TimerTask task;
    public Calendar mCalendar;
    public DatabaseHelper clockdatabese = new DatabaseHelper(this);
    private ArrayList<Clock> listClock = new ArrayList<Clock>();
    private int mHour;
    private int mMinuts;
    private int mSecond;
    private int mWeekday;

    public Alarm() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        mCalendar = Calendar.getInstance();
        time = new Timer();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        task = new TimerTask() {
            @Override
            public void run() {
                if(!Getup.mediaPlayer.isPlaying()){
                    mCalendar.setTimeInMillis(System.currentTimeMillis());
                    mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
                    mMinuts = mCalendar.get(Calendar.MINUTE);
                    mWeekday = mCalendar.get(Calendar.DAY_OF_WEEK);
                    listClock = clockdatabese.getAllClocks();
                    int num = listClock.size();
                    for (int i = 0; i < num; i++) {
                        if (listClock.get(i).hour == mHour && listClock.get(i).minute == mMinuts && listClock.get(i).day[mWeekday-2]) {
                            if (Getup.instance != null) Getup.instance.finish();
                            Intent intent = new Intent();
                            intent.putExtra("Uri", listClock.get(i).Uri);
                            intent.putExtra("hour", mHour);
                            intent.putExtra("minute", mMinuts);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(Alarm.this, Getup.class);
                            startActivity(intent);
                            break;
                        }
                    }
                }
            }
        };
        mSecond = mCalendar.get(Calendar.SECOND);
        time.schedule(task,(60-mSecond)*1000,1000*60);
    }

}
