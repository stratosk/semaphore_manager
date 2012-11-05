/*  Semaphore Manager
 *  
 *   Copyright (c) 2012 Stratos Karafotis (stratosk@semaphore.gr)
 *   
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 */
package com.semaphore.sm;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.Preference;
import android.view.View;
import com.semaphore.sai.SAIService;
import com.semaphore.smproperties.SemaProperties;

public class TabSAIFragment extends PreferenceListFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    Messenger mService = null;
    /**
     * Flag indicating whether we have called bind on the service.
     */
    boolean mBound;
    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mService = new Messenger(service);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mBound = false;
        }
    };

    public void sayHello(View v) {
        if (!mBound) {
            return;
        }
        // Create and send a message to the service, using a supported 'what' value
        Message msg;
        msg = Message.obtain(null, SAIService.MSG_RELOAD, 0, 0);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public TabSAIFragment() {
        super(R.xml.preferences_sai);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //addPreferencesFromResource(R.xml.preferences_modules);
        //getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        updateSummaries();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (MainActivity.readingValues) {
            return;
        }
//        SemaProperties sp = MainActivity.sp;

        if (key.equals("sai_enable")) {
            if (sharedPreferences.getBoolean(key, false)) {
                if (!isSAIServiceRunning()) {
                    TabSAIFragment.this.getActivity().startService(new Intent(TabSAIFragment.this.getActivity(), SAIService.class));
                    TabSAIFragment.this.getActivity().bindService(new Intent(TabSAIFragment.this.getActivity(), SAIService.class), mConnection,
                            Context.BIND_AUTO_CREATE);
                }
            } else {
                if (mBound) {
                    TabSAIFragment.this.getActivity().unbindService(mConnection);
                    mBound = false;
                }
                if (!isSAIServiceRunning()) 
                    TabSAIFragment.this.getActivity().stopService(new Intent(TabSAIFragment.this.getActivity(), SAIService.class));
            }
        }

        if ((key.equals("vibrator_near") || key.equals("vibrator_far")) && isSAIServiceRunning()) {
            sayHello(null);
        }
    }

    private boolean isSAIServiceRunning() {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SAIService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public boolean onPreferenceClick(Preference preference) {
        boolean ret = false;
        if (preference.getKey().equals("sai_enable")) {
            ret = true;
        }
        return ret;
    }

    public void updateSummaries() {
        Preference pref = findPreference("vibrator_near");
        if (pref != null) {
            pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));
        }

        pref = findPreference("vibrator_far");
        if (pref != null) {
            pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        if (mBound) {
            TabSAIFragment.this.getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        if (isSAIServiceRunning())
            TabSAIFragment.this.getActivity().bindService(new Intent(TabSAIFragment.this.getActivity(), SAIService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }
}
