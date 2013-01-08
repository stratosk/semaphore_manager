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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import com.semaphore.smproperties.SemaProperties;

public class TabTweaksFragment extends PreferenceListFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    public TabTweaksFragment() {
        super(R.xml.preferences_tweaks);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //addPreferencesFromResource(R.xml.preferences_tweaks);
        //getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        Preference pref = findPreference("vibrator_test");
        pref.setOnPreferenceClickListener(this);

        updateSummaries();
    }

    public void updateSummaries() {
        SemaProperties sp = MainActivity.sp;

        Preference pref = findPreference("scheduler");
        if (pref == null) {
            return;
        }
        if (((ListPreference) pref).getEntry() != null) {
            pref.setSummary(((ListPreference) pref).getEntry().toString());
        }

        pref = findPreference("ab_profiles");
        if (((ListPreference) pref).getEntry() != null) {
            pref.setSummary(((ListPreference) pref).getEntry().toString());
        }
        pref = findPreference(sp.read_ahead.getName());
        if (((ListPreference) pref).getEntry() != null) {
            pref.setSummary(((ListPreference) pref).getEntry().toString());
        }

        pref = findPreference(sp.vibrator.getName());
        pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));

        pref = findPreference(sp.touch.getName());
        pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));

        pref = findPreference(sp.autobr.min_brightness.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.autobr.max_brightness.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.autobr.max_lux.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.autobr.instant_update_thres.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.autobr.effect_delay_ms.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (MainActivity.readingValues) {
            return;
        }
        Preference pref = findPreference(key);
        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            if (listPref.getEntry() != null) {
                pref.setSummary(listPref.getEntry().toString());
            }
        } else if (pref instanceof EditTextPreference) {
            EditTextPreference epref = (EditTextPreference) pref;
            if (epref.getText() != null) {
                epref.setSummary(epref.getText());
            }
        }

        SemaProperties sp = MainActivity.sp;
        if (key.equals(sp.scheduler.getName())) {
            sp.scheduler.setValue(sharedPreferences.getString(key, sp.scheduler.getDefValue()));
            sp.scheduler.writeValue();
        } else if (key.equals(sp.autobr.sema_autobr.getName())) {
            sp.autobr.sema_autobr.setValue(sharedPreferences.getBoolean(key, sp.autobr.sema_autobr.getDefValue()));
            sp.autobr.sema_autobr.writeValue();
        } else if (key.equals(sp.autobr.max_brightness.getName())) {
            sp.autobr.max_brightness.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.autobr.max_brightness.getDefault()))));
            sp.autobr.max_brightness.writeValue();
        } else if (key.equals(sp.autobr.min_brightness.getName())) {
            sp.autobr.min_brightness.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.autobr.min_brightness.getDefault()))));
            sp.autobr.min_brightness.writeValue();
        } else if (key.equals(sp.autobr.max_lux.getName())) {
            sp.autobr.max_lux.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.autobr.max_lux.getDefault()))));
            sp.autobr.max_lux.writeValue();
        } else if (key.equals(sp.autobr.instant_update_thres.getName())) {
            sp.autobr.instant_update_thres.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.autobr.instant_update_thres.getDefault()))));
            sp.autobr.instant_update_thres.writeValue();
        } else if (key.equals(sp.autobr.effect_delay_ms.getName())) {
            sp.autobr.effect_delay_ms.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.autobr.effect_delay_ms.getDefault()))));
            sp.autobr.effect_delay_ms.writeValue();
        } else if (key.equals("ab_profiles")) {
            if (sharedPreferences.getString("ab_profiles", "").equals("bright")) {
                ((EditTextPreference) findPreference(sp.autobr.max_brightness.getName())).setText("255");
                ((EditTextPreference) findPreference(sp.autobr.min_brightness.getName())).setText("25");
                ((EditTextPreference) findPreference(sp.autobr.max_lux.getName())).setText("2700");
                ((EditTextPreference) findPreference(sp.autobr.instant_update_thres.getName())).setText("30");
                ((EditTextPreference) findPreference(sp.autobr.effect_delay_ms.getName())).setText("0");
            } else if (sharedPreferences.getString("ab_profiles", "").equals("normal")) {
                ((EditTextPreference) findPreference(sp.autobr.max_brightness.getName())).setText("255");
                ((EditTextPreference) findPreference(sp.autobr.min_brightness.getName())).setText("15");
                ((EditTextPreference) findPreference(sp.autobr.max_lux.getName())).setText("2900");
                ((EditTextPreference) findPreference(sp.autobr.instant_update_thres.getName())).setText("30");
                ((EditTextPreference) findPreference(sp.autobr.effect_delay_ms.getName())).setText("0");
            } else if (sharedPreferences.getString("ab_profiles", "").equals("dark")) {
                ((EditTextPreference) findPreference(sp.autobr.max_brightness.getName())).setText("255");
                ((EditTextPreference) findPreference(sp.autobr.min_brightness.getName())).setText("10");
                ((EditTextPreference) findPreference(sp.autobr.max_lux.getName())).setText("3000");
                ((EditTextPreference) findPreference(sp.autobr.instant_update_thres.getName())).setText("30");
                ((EditTextPreference) findPreference(sp.autobr.effect_delay_ms.getName())).setText("0");
            }
        } else if (key.equals(sp.vibrator.getName())) {
            sp.vibrator.setValue(sharedPreferences.getInt(key, sp.vibrator.getDefault()));
            sp.vibrator.writeValue();
        } else if (key.equals(sp.touch_enable.getName())) {
            sp.touch_enable.setValue(sharedPreferences.getBoolean(key, sp.touch_enable.getDefBoolean()) == true ? 1 : 0);
            sp.touch_enable.writeValue();
        } else if (key.equals(sp.touch.getName())) {
            sp.touch.setValue(sharedPreferences.getInt(key, sp.touch.getDefault()));
            sp.touch.writeValue();
        } else if (key.equals(sp.bigmem.getName())) {
            sp.bigmem.setValue(sharedPreferences.getBoolean(key, sp.bigmem.getDefBoolean()) == true ? 1 : 0);
            sp.bigmem.writeValue();
        } else if (key.equals(sp.wififast.getName())) {
            sp.wififast.setValue(sharedPreferences.getBoolean(key, sp.wififast.getDefBoolean()) == true ? 1 : 0);
            sp.wififast.writeValue();
        } else if (key.equals(sp.forcefastchg.getName())) {
            sp.forcefastchg.setValue(sharedPreferences.getBoolean(key, sp.forcefastchg.getDefBoolean()) == true ? 1 : 0);
            sp.forcefastchg.writeValue();
        } else if (key.equals(sp.read_ahead.getName())) {
            sp.read_ahead.setValue(sharedPreferences.getString(key, sp.read_ahead.getDefValue()));
            sp.read_ahead.writeValue();
        } else if (key.equals(sp.bln.getName())) {
            sp.bln.setValue(sharedPreferences.getBoolean(key, sp.bln.getDefValue()));
            sp.bln.writeValue();
        }
    }

    public boolean onPreferenceClick(Preference preference) {
        boolean ret = false;
        if (preference.getKey().equals("vibrator_test")) {
            vibratorTest();
            ret = true;
        }
        return ret;
    }

    public void vibratorTest() {
        Vibrator v = (Vibrator) this.getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(750);
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
