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

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;

public class TabInfoFragment extends PreferenceListFragment {

    public TabInfoFragment() {
        super(R.xml.preferences_info);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //addPreferencesFromResource(R.xml.preferences_info);
        //getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        Commander cm = Commander.getInstance();
        cm.readFile("/proc/version");
        Preference pref = findPreference("kernel_version");
        pref.setSummary(cm.getOutResult().get(0));
    }

//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//    }
}