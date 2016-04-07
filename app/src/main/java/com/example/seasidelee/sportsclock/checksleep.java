package com.example.seasidelee.sportsclock;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;

public class checksleep extends Service {
    private long initTime = 0;
    private long lastTime = 0;
    private long lastTime2 = 0;
    private long curTime = 0;
    private long duration = 0;

    private float last_x = 0.0f;
    private float last_y = 0.0f;
    private float last_z = 0.0f;
    private int run = 1;

    private float shake = 0.0f;
    private float totalShake = 0.0f;

    public checksleep() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        final SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        final Sensor GravitySensor = sm.getDefaultSensor(Sensor.TYPE_GRAVITY);
        final SensorEventListener GravityListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                curTime = System.currentTimeMillis();
                if ((curTime - lastTime) > 100) {
                    duration = (curTime - lastTime);
                    if (last_x == 0.0f && last_y == 0.0f && last_z == 0.0f) {
                        initTime = System.currentTimeMillis();
                        lastTime2 = System.currentTimeMillis();
                    } else {
                        double xx = (double) Math.abs(x - last_x);
                        double yy = (double) Math.abs(y - last_y);
                        double zz = (double) Math.abs(z - last_z);
                        shake = (float) (Math.sqrt(xx * xx + yy * yy + zz * zz));///duration*100;
                    }

                    totalShake += shake;
                    last_x = x;
                    last_y = y;
                    last_z = z;
                    lastTime = curTime;

                    if (curTime - initTime > 1000*5*60)
                        if (totalShake / (curTime - initTime) * 100 > 1) sm.unregisterListener(this);
                        else{
                            if (Getup.instance != null) Getup.instance.finish();
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(checksleep.this, Getup.class);
                            startActivity(intent);
                            stopSelf();
                        }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        sm.registerListener(GravityListener, GravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
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
}
