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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
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
    private int vibratorDef;
    public static final int MSG_RELOAD = 1;

    @Override
    public void onCreate() {
//        Toast.makeText(this, "SAIService Created", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
//        Toast.makeText(this, "SAIService Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");

        sensorService.unregisterListener(mySensorEventListener);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
    }

    private void readSettings() {
        Context ctx = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        vibratorFar = prefs.getInt("vibrator_far", 25);
        vibratorNear = prefs.getInt("vibrator_near", 100);
        vibratorDef = prefs.getInt("vibrator", 100);        
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "SAIService Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");

        readSettings();
        
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        sensorService = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorService.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        return START_STICKY; //START_REDELIVER_INTENT
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
        setVibration(2);
    }

    private void setVibration(int state) {
        Commander cm = Commander.getInstance();
        switch (state) {
            case 0: // Far
                cm.run("echo " + String.valueOf(vibratorFar) + " > /sys/devices/virtual/misc/pwm_duty/pwm_duty", false);
                Log.d(TAG, "echo " + String.valueOf(vibratorFar) + " > /sys/devices/virtual/misc/pwm_duty/pwm_duty");
                break;
            case 1: // Near
                cm.run("echo " + String.valueOf(vibratorNear) + " > /sys/devices/virtual/misc/pwm_duty/pwm_duty", false);
                Log.d(TAG, "echo " + String.valueOf(vibratorNear) + " > /sys/devices/virtual/misc/pwm_duty/pwm_duty");
                break;
            case 2: // Default
                cm.run("echo " + String.valueOf(vibratorDef) + " > /sys/devices/virtual/misc/pwm_duty/pwm_duty", false);
                Log.d(TAG, "echo " + String.valueOf(vibratorDef) + " > /sys/devices/virtual/misc/pwm_duty/pwm_duty");
                break;
        }
    }
    
    private SensorEventListener mySensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                Log.i(TAG, "Proximity Sensor Reading:"
                        + String.valueOf(event.values[0]));
                if (event.values[0] > 0) {  // far
                    setVibration(0);
                } else {                    // near
                    setVibration(1);
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            
            vibratorNear = bundle.getInt("vibrator_near");
            vibratorFar = bundle.getInt("vibrator_far");
            vibratorDef = bundle.getInt("vibrator");
            Toast.makeText(getApplicationContext(), "SAI service notified with updated values\n" +
                            "vibrator near: " + String.valueOf(vibratorNear) + "\n" +                        
                            "vibrator far: " + String.valueOf(vibratorFar) + "\n" +
                            "vibrator default: " + String.valueOf(vibratorDef), 
                            Toast.LENGTH_SHORT).show();
            super.handleMessage(msg);
        }
    }
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    /**
     * When binding to the service, we return an interface to our messenger for
     * sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        //Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return mMessenger.getBinder();
    }
}
