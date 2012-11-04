/*  Semaphore Manager
 *  
 *   Copyright (c) 2012 Stratos Karafotis (stratosk@semaphore.gr)
 *   
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 */
package com.semaphore.sai;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.semaphore.sm.Commander;

public class SAIService extends Service {

    private static final String TAG = "SAIService";
    private static SensorManager sensorService;
    private Sensor sensor;
    private TelephonyManager telephonyManager;
    private int vibratorNear;
    private int vibratorFar;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "SAIService Created", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "SAIService Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
        
        sensorService.unregisterListener(mySensorEventListener);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "SAIService Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        vibratorFar = prefs.getInt("vibrator_far", 25);
        vibratorNear = prefs.getInt("vibrator_near", 100);
        
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        
        sensorService = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorService.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        return 0;
    }
    
    private PhoneStateListener phoneListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            String stateString = "N/A";
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    stateString = "Idle";
                    disableVibrationListener();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    stateString = "Off Hook";
                    disableVibrationListener();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    stateString = "Ringing";
                    enableVibrationListener();
                    break;
            }
            Log.d(TAG, "onCallStateChanged: " + stateString);
        }
    };
    
    private void enableVibrationListener() {
        if (sensor != null) {
            sensorService.registerListener(mySensorEventListener, sensor,
                    SensorManager.SENSOR_DELAY_FASTEST);
            Log.i(TAG, "Registerered for PROXIMITY Sensor");

        }
    }
    
    private void disableVibrationListener() {
        if (sensor != null) {
            sensorService.unregisterListener(mySensorEventListener);
            Log.i(TAG, "Unregisterered for PROXIMITY Sensor");

        }
    }
    
    private void setVibration(int state) {
        Commander cm = Commander.getInstance();
        switch (state) {
            case 0:
                cm.run("echo 100 > /sys/devices/virtual/misc/pwm_duty/pwm_duty");
                break;
            case 1:
                cm.run("echo 15 > /sys/devices/virtual/misc/pwm_duty/pwm_duty");
                break;
        }
    }
    
    private SensorEventListener mySensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                Log.i(TAG, "Proximity Sensor Reading:"
                        + String.valueOf(event.values[0]));
//                Toast.makeText(SAIService.this, "Proximity Sensor Reading:" + String.valueOf(event.values[0]), Toast.LENGTH_SHORT).show();
                if (event.values[0] > 0) {  // far
                    setVibration(1);
                } else {                    // near
                    setVibration(0);
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }
    };
}
