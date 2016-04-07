package com.example.seasidelee.sportsclock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Shake extends Activity {
    private long initTime = 0;
    private long lastTime = 0;
    private long curTime = 0;
    private long duration = 0;

    private float last_x = 0.0f;
    private float last_y = 0.0f;
    private float last_z = 0.0f;
    private int   run=1;

    private float shake = 0.0f;
    private float totalShake = 0.0f;
    private int color=0xffffffff;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        final SensorManager sm=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        final Sensor acceleromererSensor = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        final SensorEventListener acceleromererListener= new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                curTime = System.currentTimeMillis();
                if ((curTime - lastTime) > 100) {

                    duration = (curTime - lastTime);


                    if (last_x == 0.0f && last_y == 0.0f && last_z == 0.0f) {
                        //last_x��last_y��last_zͬʱΪ0ʱ����ʾ�ոտ�ʼ��¼
                        initTime = System.currentTimeMillis();
                    } else {
                        // ���λζ�����
                        double xx = (double) Math.abs(x - last_x);
                        double yy = (double) Math.abs(y - last_y);
                        double zz = (double) Math.abs(z - last_z);
                        shake = (float) (Math.sqrt(xx * xx + yy * yy+ zz*zz));///duration*100;
                    }

                    //��ÿ�εĻζ�������ӣ���һ�������õ�����ζ���
                    if (shake > 15) {
                        totalShake += shake-totalShake/11;
                    }
                    else if(totalShake-15>0) //���ζ������
                        totalShake -= 15;
                    else if(totalShake-15<0)
                        totalShake=0;
                    // �ж��Ƿ�Ϊҡ����
                    if (totalShake > 1000) {
                        initShake();
                        run = 0;
                    }

                    last_x = x;
                    last_y = y;
                    last_z = z;
                    lastTime = curTime;
                    color = ((int) ((totalShake / 1000) * 0xff)) | 0xffffff00;
                    if (run == 1) {
                        ((LinearLayout) findViewById(R.id.fffff)).setBackgroundColor(color);
                    }
                    else if(run==0) {
                        ((LinearLayout) findViewById(R.id.fffff)).setBackgroundColor(0xffffffff);
                        sm.unregisterListener(this);
                        Getup.instance.finish();
                        Shake.this.finish();
                        Intent succeed = new Intent();
                        succeed.setClass(Shake.this, Succeed.class);
                        startActivity(succeed);
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy){
            }
        };
        sm.registerListener(acceleromererListener, acceleromererSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    public void initShake() {
        lastTime = 0;
        duration = 0;
        curTime = 0;
        initTime = 0;
        last_x = 0.0f;
        last_y = 0.0f;
        last_z = 0.0f;
        shake = 0.0f;
        totalShake = 0.0f;
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
