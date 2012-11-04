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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import com.semaphore.sai.SAIService;
import com.semaphore.smproperties.SemaProperties;

public class TabSAIFragment extends PreferenceListFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

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
        SemaProperties sp = MainActivity.sp;

        if (key.equals("sai_enable")) {
            if (sharedPreferences.getBoolean(key, false)) {
                if (!isSAIServiceRunning()) {
                    TabSAIFragment.this.getActivity().startService(new Intent(TabSAIFragment.this.getActivity(), SAIService.class));
                }
            } else {
                TabSAIFragment.this.getActivity().stopService(new Intent(TabSAIFragment.this.getActivity(), SAIService.class));
            }
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
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
}
