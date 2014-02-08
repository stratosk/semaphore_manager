/*  Semaphore Manager
 *  
 *   Copyright (c) 2012 - 2013 Stratos Karafotis (stratosk@semaphore.gr)
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
import android.view.KeyEvent;
import android.widget.Toast;
import com.semaphore.sm.Commander;

public class SAIService extends Service {

    private static final String TAG = "SAIService:";
    private static SensorManager sensorService;
    private Sensor sensor;
    private Sensor mAccSensor;
    private Sensor mMagSensor;
    private TelephonyManager telephonyManager;
    private int vibratorNear;
    private int vibratorFar;
    private int vibratorDef;
    private boolean blinkLeds;
    private int blinkInterval;
    private boolean pickupPhone;
    private boolean touchwake_disable;
    private int touchwake_initial;
    public static final int MSG_RELOAD = 1;
    private SAIBlinkLED blink;
    private float mAzimuth;
    private float mPitch;
    private float mRoll;
    private final float rad2deg = 57.2957795f;
    float[] I = new float[16];
    float[] R = new float[16];
    private float[] mMagneticValues = new float[3];
    private float[] mAccelerometerValues = new float[3];
    private boolean proximityNear = false;
    private boolean almostFlat = false;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        sensorService.unregisterListener(mySensorEventListener);
        sensorService.unregisterListener(orSensorEventListener);

        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
    }

    private void readSettings() {
        Context ctx = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        vibratorFar = prefs.getInt("vibrator_far", 25);
        vibratorNear = prefs.getInt("vibrator_near", 100);
        vibratorDef = prefs.getInt("vibrator", 100);
        blinkLeds = prefs.getBoolean("blink_leds", false);
        blinkInterval = prefs.getInt("blink_interval", 200);
        pickupPhone = prefs.getBoolean("pickup_phone", false);
        touchwake_disable = prefs.getBoolean("touchwake_disable", false);
        getTouchwake();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStart");

        readSettings();

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        sensorService = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorService.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        mAccSensor = sensorService.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagSensor = sensorService.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        return START_STICKY; //START_REDELIVER_INTENT
    }

    private void answerCall() {
        Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);
        buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
        this.sendOrderedBroadcast(buttonDown, "android.permission.CALL_PRIVILEGED");

        Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
        buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
        this.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");

        disableOrientationListener();
    }

    private void startBlink() {
        blink = new SAIBlinkLED(blinkInterval);
        blink.start();
    }

    private void stopBlink() {
        if (blink != null)
            blink.interrupt();
    }

    private void getTouchwake() {
        String path = "/sys/devices/virtual/misc/touchwake/enabled";

        Commander cm = Commander.getInstance();
        touchwake_initial = cm.readFile(path);
    }

    private void touchwake(int enabled) {
        String path = "/sys/devices/virtual/misc/touchwake/enabled";
        Commander cm = Commander.getInstance();

        cm.writeFile(path, String.valueOf(enabled));
    }

    private void disableTouchwake() {
        if (touchwake_disable) {
            getTouchwake();
            touchwake(0);
        }
    }

    private void enableTouchwake() {
        if (touchwake_disable && touchwake_initial == 1)
            touchwake(1);
    }

    private PhoneStateListener phoneListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            String stateString = "N/A";
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    stateString = "Idle";

                    enableTouchwake();

                    disableVibrationListener();

                    if (pickupPhone)
                        disableOrientationListener();

                    if (blinkLeds)
                        stopBlink();

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    stateString = "Off Hook";

                    disableTouchwake();

                    enableVibrationListener();

                    if (pickupPhone)
                        disableOrientationListener();

                    if (blinkLeds)
                        stopBlink();

                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    stateString = "Ringing";

                    disableTouchwake();

                    enableVibrationListener();

                    if (pickupPhone)
                        enableOrientationListener();

                    if (blinkLeds)
                        startBlink();
                    break;
            }
            Log.d(TAG, "onCallStateChanged: " + stateString);
        }
    };

    private void enableOrientationListener() {
        sensorService.registerListener(orSensorEventListener, mAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "Registerered for Accelerometer Sensor");
        sensorService.registerListener(orSensorEventListener, mMagSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "Registerered for Magnetic Field Sensor");
    }

    private void disableOrientationListener() {
        if (mAccSensor != null) {
            sensorService.unregisterListener(orSensorEventListener);
            Log.d(TAG, "Unregisterered for Accelerometer Sensor");
        }
        if (mMagSensor != null) {
            sensorService.unregisterListener(orSensorEventListener);
            Log.d(TAG, "Unregisterered for Magnetic Field Sensor");
        }
        almostFlat = false;
    }

    private void enableVibrationListener() {
        if (sensor != null) {
            sensorService.registerListener(mySensorEventListener, sensor,
                    SensorManager.SENSOR_DELAY_FASTEST);
            Log.d(TAG, "Registerered for PROXIMITY Sensor");
        }
    }

    private void disableVibrationListener() {
        if (sensor != null) {
            sensorService.unregisterListener(mySensorEventListener);
            Log.d(TAG, "Unregisterered for PROXIMITY Sensor");
        }
        setVibration(2);
        proximityNear = false;
    }

    private void setVibration(int state) {
        String path;
        Commander cm = Commander.getInstance();
        if ("mako".equals(android.os.Build.DEVICE))
            path = "/sys/class/timed_output/vibrator/amp";
        else
            path = "/sys/devices/virtual/misc/pwm_duty/pwm_duty";
        switch (state) {
            case 0: // Far
                cm.writeFile(path, String.valueOf(vibratorFar));
                break;
            case 1: // Near
                cm.writeFile(path, String.valueOf(vibratorNear));
                break;
            case 2: // Default
                cm.writeFile(path, String.valueOf(vibratorDef));
                break;
        }
    }

    private SensorEventListener mySensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                Log.d(TAG, "Proximity Sensor Reading: "
                        + String.valueOf(event.values[0]));
                if (event.values[0] > 0) {  // far
                    setVibration(0);
                    proximityNear = false;
                } else {                    // near
                    setVibration(1);
                    proximityNear = true;
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    private SensorEventListener orSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mMagneticValues = event.values.clone();
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    mAccelerometerValues = event.values.clone();
                    break;
            }

            if (mMagneticValues != null && mAccelerometerValues != null) {
                boolean success = SensorManager.getRotationMatrix(R, I, mAccelerometerValues, mMagneticValues);
                if (success) {
                    float[] orientation = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    //mAzimuth = orientation[0] * rad2deg;
                    mPitch = orientation[1] * rad2deg;
                    mRoll = orientation[2] * rad2deg;

                    //Log.d("SAIOrientation: Azimuth: ", String.valueOf(mAzimuth));
                    //Log.d("SAIOrientation: Pitch: ", String.valueOf(mPitch));
                    //Log.d("SAIOrientation: Roll: ", String.valueOf(mRoll));
                    if (!proximityNear && Math.abs(mRoll) < 30 && Math.abs(mPitch) < 30)
                        almostFlat = true;
                    if (almostFlat && proximityNear && Math.abs(mRoll) > 40 && Math.abs(mPitch) < 70) {
                        almostFlat = false;
                        answerCall();
                    }
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
            blinkLeds = bundle.getBoolean("blink_leds");
            blinkInterval = bundle.getInt("blink_interval");
            pickupPhone = bundle.getBoolean("pickup_phone");
            touchwake_disable = bundle.getBoolean("touchwake_disable");
            Toast.makeText(getApplicationContext(), "SAI service notified with new values", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "vibrator near: " + String.valueOf(vibratorNear));
            Log.d(TAG, "vibrator far: " + String.valueOf(vibratorFar));
            Log.d(TAG, "vibrator default: " + String.valueOf(vibratorDef));
            Log.d(TAG, "pickup phone: " + (pickupPhone ? "true" : "false"));
            Log.d(TAG, "blink leds: " + (blinkLeds ? "true" : "false"));
            Log.d(TAG, "blink interval: " + String.valueOf(blinkInterval));
            Log.d(TAG, "touchwake disable: " + String.valueOf(touchwake_disable));
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
     *
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        //Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return mMessenger.getBinder();
    }
}
