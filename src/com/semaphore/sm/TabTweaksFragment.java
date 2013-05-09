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
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import com.semaphore.smproperties.SemaCommonProperties;
import com.semaphore.smproperties.SemaI9000Properties;
import com.semaphore.smproperties.SemaN4Properties;

public class TabTweaksFragment extends PreferenceListFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private SemaCommonProperties scp;

    public TabTweaksFragment() {
        super();
        
        if (MainActivity.Device == MainActivity.SemaDevices.Mako)
            super.setxmlId(R.xml.preferences_tweaks_n4);
        else
            super.setxmlId(R.xml.preferences_tweaks_i9000);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //addPreferencesFromResource(R.xml.preferences_tweaks);
        //getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        updateSummaries();
        Preference pref = findPreference("vibrator_test");
        if (pref != null)
                pref.setOnPreferenceClickListener(this);
    }

    private void updateSummariesI9000() {
        SemaI9000Properties sp = (SemaI9000Properties) scp;

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
        pref = findPreference(sp.autobr.max_br_threshold.getName());
        pref.setSummary(((EditTextPreference) pref).getText());        
    }
    
    private void updateSummariesN4() {
        SemaN4Properties sp = (SemaN4Properties) scp;

        Preference pref = findPreference("scheduler");
        if (pref == null) {
            return;
        }
        if (((ListPreference) pref).getEntry() != null) {
            pref.setSummary(((ListPreference) pref).getEntry().toString());
        }
        pref = findPreference("led_red");
        if (pref == null) {
            return;
        }
        if (((ListPreference) pref).getEntry() != null) {
            pref.setSummary(((ListPreference) pref).getEntry().toString());
        }
        pref = findPreference("led_green");
        if (pref == null) {
            return;
        }
        if (((ListPreference) pref).getEntry() != null) {
            pref.setSummary(((ListPreference) pref).getEntry().toString());
        }
        pref = findPreference("led_blue");
        if (pref == null) {
            return;
        }
        if (((ListPreference) pref).getEntry() != null) {
            pref.setSummary(((ListPreference) pref).getEntry().toString());
        }

        pref = findPreference(sp.vibrator.getName());
        pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));

        pref = findPreference(sp.touch.getName());
        pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));
        
        PreferenceScreen scPref = (PreferenceScreen) findPreference("accuracy_screen");
        if (scPref != null)
            scPref.setSummary(sp.taccuracy.accuracy_filter_enable.getValue() == 1 ? "Enabled" : "Disabled");
                
        pref = findPreference(sp.taccuracy.ignore_pressure_gap.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.taccuracy.delta_max.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.taccuracy.touch_max_count.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.taccuracy.max_pressure.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.taccuracy.direction_count.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.taccuracy.time_to_max_pressure.getName());
        pref.setSummary(((EditTextPreference) pref).getText());

        pref = findPreference(sp.tjitter.adjust_margin.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        
        pref = findPreference(sp.lcdtemp.lcd_red.getName());
        pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));
        pref = findPreference(sp.lcdtemp.lcd_green.getName());
        pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));
        pref = findPreference(sp.lcdtemp.lcd_blue.getName());
        pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));
        
    }

    public void updateSummaries() {
        scp = MainActivity.sp;

        if (scp == null)
            return;

        if (MainActivity.Device == MainActivity.SemaDevices.Mako)
            updateSummariesN4();
        else
            updateSummariesI9000();
    }

    private void writeMako(SharedPreferences sharedPreferences, String key) {
        SemaN4Properties sp = (SemaN4Properties) scp;        

        if (key.equals(sp.scheduler.getName())) {
            sp.scheduler.setValue(sharedPreferences.getString(key, sp.scheduler.getDefValue()));
            sp.scheduler.writeValue();
        } else if (key.equals(sp.vibrator.getName())) {
            sp.vibrator.setValue(sharedPreferences.getInt(key, sp.vibrator.getDefault()));
            sp.vibrator.writeValue();
        } else if (key.equals(sp.touch_enable.getName())) {
            sp.touch_enable.setValue(sharedPreferences.getBoolean(key, sp.touch_enable.getDefBoolean()) == true ? 1 : 0);
            sp.touch_enable.writeValue();
        } else if (key.equals(sp.touch.getName())) {
            sp.touch.setValue(sharedPreferences.getInt(key, sp.touch.getDefault()));
            sp.touch.writeValue();
        } else if (key.equals(sp.taccuracy.accuracy_filter_enable.getName())) {
            sp.taccuracy.accuracy_filter_enable.setValue(sharedPreferences.getBoolean(key, sp.taccuracy.accuracy_filter_enable.getDefBoolean()) == true ? 1 : 0);
            sp.taccuracy.writeValue();
        } else if (key.equals(sp.taccuracy.ignore_pressure_gap.getName())) {
            sp.taccuracy.ignore_pressure_gap.setValue(sharedPreferences.getString(key, sp.taccuracy.ignore_pressure_gap.getDefString()));
            sp.taccuracy.writeValue();
        } else if (key.equals(sp.taccuracy.delta_max.getName())) {
            sp.taccuracy.delta_max.setValue(sharedPreferences.getString(key, sp.taccuracy.delta_max.getDefString()));
            sp.taccuracy.writeValue();
        } else if (key.equals(sp.taccuracy.touch_max_count.getName())) {
            sp.taccuracy.touch_max_count.setValue(sharedPreferences.getString(key, sp.taccuracy.touch_max_count.getDefString()));
            sp.taccuracy.writeValue();
        } else if (key.equals(sp.taccuracy.max_pressure.getName())) {
            sp.taccuracy.max_pressure.setValue(sharedPreferences.getString(key, sp.taccuracy.max_pressure.getDefString()));
            sp.taccuracy.writeValue();
        } else if (key.equals(sp.taccuracy.direction_count.getName())) {
            sp.taccuracy.direction_count.setValue(sharedPreferences.getString(key, sp.taccuracy.direction_count.getDefString()));
            sp.taccuracy.writeValue();
        } else if (key.equals(sp.taccuracy.time_to_max_pressure.getName())) {
            sp.taccuracy.time_to_max_pressure.setValue(sharedPreferences.getString(key, sp.taccuracy.time_to_max_pressure.getDefString()));
            sp.taccuracy.writeValue();
        } else if (key.equals(sp.tjitter.jitter_enable.getName())) {
            sp.tjitter.jitter_enable.setValue(sharedPreferences.getBoolean(key, sp.tjitter.jitter_enable.getDefBoolean()) == true ? 1 : 0);
            sp.tjitter.writeValue();
        } else if (key.equals(sp.tjitter.adjust_margin.getName())) {
            sp.tjitter.adjust_margin.setValue(sharedPreferences.getString(key, sp.tjitter.adjust_margin.getDefString()));
            sp.tjitter.writeValue();
        } else if (key.equals(sp.led_red.getName())) {
            sp.led_red.setValue(sharedPreferences.getString(key, sp.led_red.getDefValue()));
            sp.led_red.writeValue();
        } else if (key.equals(sp.led_green.getName())) {
            sp.led_green.setValue(sharedPreferences.getString(key, sp.led_green.getDefValue()));
            sp.led_green.writeValue();
        } else if (key.equals(sp.led_blue.getName())) {
            sp.led_blue.setValue(sharedPreferences.getString(key, sp.led_blue.getDefValue()));
            sp.led_blue.writeValue();
        } else if (key.equals(sp.lcdtemp.lcd_red.getName())) {
            sp.lcdtemp.lcd_red.setValue(sharedPreferences.getInt(key, sp.lcdtemp.lcd_red.getDefault()));
            sp.lcdtemp.writeValue();
        } else if (key.equals(sp.lcdtemp.lcd_green.getName())) {
            sp.lcdtemp.lcd_green.setValue(sharedPreferences.getInt(key, sp.lcdtemp.lcd_green.getDefault()));
            sp.lcdtemp.writeValue();
        } else if (key.equals(sp.lcdtemp.lcd_blue.getName())) {
            sp.lcdtemp.lcd_blue.setValue(sharedPreferences.getInt(key, sp.lcdtemp.lcd_blue.getDefault()));
            sp.lcdtemp.writeValue();
        }
    }
    
    private void writeI9000(SharedPreferences sharedPreferences, String key) {
        SemaI9000Properties sp = (SemaI9000Properties) scp;        

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
        } else if (key.equals(sp.autobr.max_br_threshold.getName())) {
            sp.autobr.max_br_threshold.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.autobr.max_br_threshold.getDefault()))));
            sp.autobr.max_br_threshold.writeValue();
        } else if (key.equals("ab_profiles")) {
            if (sharedPreferences.getString("ab_profiles", "").equals("bright")) {
                ((EditTextPreference) findPreference(sp.autobr.max_brightness.getName())).setText("255");
                ((EditTextPreference) findPreference(sp.autobr.min_brightness.getName())).setText("25");
                ((EditTextPreference) findPreference(sp.autobr.max_lux.getName())).setText("2700");
                ((EditTextPreference) findPreference(sp.autobr.instant_update_thres.getName())).setText("30");
                ((EditTextPreference) findPreference(sp.autobr.effect_delay_ms.getName())).setText("0");
                ((EditTextPreference) findPreference(sp.autobr.max_br_threshold.getName())).setText("0");
            } else if (sharedPreferences.getString("ab_profiles", "").equals("normal")) {
                ((EditTextPreference) findPreference(sp.autobr.max_brightness.getName())).setText("255");
                ((EditTextPreference) findPreference(sp.autobr.min_brightness.getName())).setText("15");
                ((EditTextPreference) findPreference(sp.autobr.max_lux.getName())).setText("2900");
                ((EditTextPreference) findPreference(sp.autobr.instant_update_thres.getName())).setText("30");
                ((EditTextPreference) findPreference(sp.autobr.effect_delay_ms.getName())).setText("0");
                ((EditTextPreference) findPreference(sp.autobr.max_br_threshold.getName())).setText("0");
            } else if (sharedPreferences.getString("ab_profiles", "").equals("dark")) {
                ((EditTextPreference) findPreference(sp.autobr.max_brightness.getName())).setText("255");
                ((EditTextPreference) findPreference(sp.autobr.min_brightness.getName())).setText("10");
                ((EditTextPreference) findPreference(sp.autobr.max_lux.getName())).setText("3000");
                ((EditTextPreference) findPreference(sp.autobr.instant_update_thres.getName())).setText("30");
                ((EditTextPreference) findPreference(sp.autobr.effect_delay_ms.getName())).setText("0");
                ((EditTextPreference) findPreference(sp.autobr.max_br_threshold.getName())).setText("0");
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
    
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (MainActivity.readingValues) {
            return;
        }
        scp = MainActivity.sp;

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
        
        if (MainActivity.Device == MainActivity.SemaDevices.Mako) {
            if (pref != null && scp != null && pref.getKey().equals(((SemaN4Properties) scp).taccuracy.accuracy_filter_enable.getName())) {
            PreferenceScreen scPref = (PreferenceScreen) findPreference("accuracy_screen");
            if (scPref != null)
                scPref.setSummary(((SwitchPreference) pref).isChecked() ? "Enabled" : "Disabled");
            }
            writeMako(sharedPreferences, key);
        } else
            writeI9000(sharedPreferences, key);
        
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
