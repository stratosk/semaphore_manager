/*  Semaphore Manager
 *  
 *   Copyright (c) 2012-2013 Stratos Karafotis (stratosk@semaphore.gr)
 *   
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 */
package com.semaphore.sm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.semaphore.smproperties.SemaCommonProperties;
import com.semaphore.smproperties.SemaI9000Properties;
import com.semaphore.smproperties.SemaN4Properties;
import java.util.Iterator;

public class TabCPUFragment extends PreferenceListFragment implements OnSharedPreferenceChangeListener, OnPreferenceClickListener {
    private SemaCommonProperties scp;
    
    public TabCPUFragment() {
        super();
        
        if (MainActivity.Device == MainActivity.SemaDevices.Mako)
            super.setxmlId(R.xml.preferences_cpu_n4);
        else
            super.setxmlId(R.xml.preferences_cpu_i9000);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        addPreferencesFromResource(R.xml.preferences_cpu);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        
        if (MainActivity.Device == MainActivity.SemaDevices.I9000) {
            Preference pref = findPreference("deep_idle_stats_show");
            pref.setOnPreferenceClickListener(this);
            pref = findPreference("cv_apply");
            pref.setOnPreferenceClickListener(this);
            pref = findPreference("cv_reset");
            pref.setOnPreferenceClickListener(this);
        } else if (MainActivity.Device == MainActivity.SemaDevices.Mako) {
            Preference pref = findPreference("uv_apply");
            pref.setOnPreferenceClickListener(this);
            pref = findPreference("uv_reset");
            pref.setOnPreferenceClickListener(this);            
            pref = findPreference("uv_cpu_table");
            pref.setOnPreferenceClickListener(this);            
        }
        updateSummaries();
    }

    private void showIdleDialog() {
        Commander cm = Commander.getInstance();
        cm.readFile("/sys/class/misc/deepidle/idle_stats");
        
        if (cm.getOutResult().isEmpty())
            return;
        String idle = cm.getOutResult().get(2);
        idle = idle.substring(23, idle.indexOf("ms") + 2).trim();
        String topon = cm.getOutResult().get(3);
        topon = topon.substring(23, topon.indexOf("ms") + 2).trim();
        String topoff = cm.getOutResult().get(4);
        topoff = topoff.substring(23, topoff.indexOf("ms") + 2).trim();
        View view = getActivity().getLayoutInflater().inflate(R.layout.idle_dialog, null);

        ((TextView) view.findViewById(R.id.value1)).setText(idle);
        ((TextView) view.findViewById(R.id.value2)).setText(topon);
        ((TextView) view.findViewById(R.id.value3)).setText(topoff);

        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle(R.string.str_didle_dialog_title);
        ad.setView(view);
        ad.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Commander.getInstance().run("echo 1 > /sys/class/misc/deepidle/reset_stats", false);
                dialog.dismiss();
            }
        });
        ad.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }

    private void showCPUTableDialog() {
        Commander cm = Commander.getInstance();
        cm.readFile("/sys/kernel/debug/acpuclk/acpu_table");
        
        if (cm.getOutResult().isEmpty())
            return;
        View view = getActivity().getLayoutInflater().inflate(R.layout.cpu_table_dialog, null);
        ((TextView) view.findViewById(R.id.text_cpu_table)).setTypeface(Typeface.MONOSPACE);
        Iterator<String> i = cm.getOutResult().iterator();
        while (i.hasNext()) {
            ((TextView) view.findViewById(R.id.text_cpu_table)).append(i.next() + "\n");
        }
        
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle(R.string.str_cpu_table_dialog_title);
        ad.setView(view);
        ad.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }    
    
    public void onPreferenceChange(Preference preference, Object newValue) {
    }

    private void writeMako(SharedPreferences sharedPreferences, String key) {
        SemaN4Properties sp = (SemaN4Properties) scp;        

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

        if (key.equals(sp.ondemand.io_is_busy.getName())) {
            sp.ondemand.io_is_busy.setValue(sharedPreferences.getBoolean(key, sp.ondemand.io_is_busy.getDefBoolean()) == true ? 1 : 0);
            sp.ondemand.io_is_busy.writeValue();
        } else if (key.equals(sp.ondemand.sampling_down_factor.getName())) {
            sp.ondemand.sampling_down_factor.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.ondemand.sampling_down_factor.getDefault()))));
            sp.ondemand.sampling_down_factor.writeValue();
        } else if (key.equals(sp.ondemand.sampling_rate.getName())) {
            sp.ondemand.sampling_rate.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.ondemand.sampling_rate.getDefault()))));
            sp.ondemand.sampling_rate.writeValue();
        } else if (key.equals(sp.ondemand.up_threshold.getName())) {
            sp.ondemand.up_threshold.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.ondemand.up_threshold.getDefault()))));
            sp.ondemand.up_threshold.writeValue();
        } else if (key.equals(sp.ondemand.touch_load.getName())) {
            sp.ondemand.touch_load.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.ondemand.touch_load.getDefault()))));
            sp.ondemand.touch_load.writeValue();
        } else if (key.equals(sp.ondemand.touch_load_threshold.getName())) {
            sp.ondemand.touch_load_threshold.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.ondemand.touch_load_threshold.getDefault()))));
            sp.ondemand.touch_load_threshold.writeValue();
        } else if (key.equals(sp.ondemand.touch_load_duration.getName())) {
            sp.ondemand.touch_load_duration.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.ondemand.touch_load_duration.getDefault()))));
            sp.ondemand.touch_load_duration.writeValue();
        } else if (key.equals(sp.conservative.freq_step.getName())) { // Conservative
            sp.conservative.freq_step.setValue(Integer.parseInt(sharedPreferences.getString(key, sp.conservative.freq_step.getDefString())));
            sp.conservative.freq_step.writeValue();
        } else if (key.equals(sp.conservative.sampling_down_factor.getName())) {
            sp.conservative.sampling_down_factor.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.conservative.sampling_down_factor.getDefault()))));
            sp.conservative.sampling_down_factor.writeValue();
        } else if (key.equals(sp.conservative.sampling_rate.getName())) {
            sp.conservative.sampling_rate.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.conservative.sampling_rate.getDefault()))));
            sp.conservative.sampling_rate.writeValue();
        } else if (key.equals(sp.conservative.up_threshold.getName())) {
            sp.conservative.up_threshold.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.conservative.up_threshold.getDefault()))));
            sp.conservative.up_threshold.writeValue();
        } else if (key.equals(sp.conservative.down_threshold.getName())) {
            sp.conservative.down_threshold.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.conservative.down_threshold.getDefault()))));
            sp.conservative.down_threshold.writeValue();
        } else if (key.equals(sp.interactive.hispeed_freq.getName())) { // Interactive
            sp.interactive.hispeed_freq.setValue(Integer.parseInt(sharedPreferences.getString(key, sp.interactive.hispeed_freq.getDefString())));
            sp.interactive.hispeed_freq.writeValue();
        } else if (key.equals(sp.interactive.go_hispeed_load.getName())) {
            sp.interactive.go_hispeed_load.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.interactive.go_hispeed_load.getDefault()))));
            sp.interactive.go_hispeed_load.writeValue();
        } else if (key.equals(sp.interactive.min_sampling_time.getName())) {
            sp.interactive.min_sampling_time.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.interactive.min_sampling_time.getDefault()))));
            sp.interactive.min_sampling_time.writeValue();
        } else if (key.equals(sp.interactive.above_hispeed_delay.getName())) {
            sp.interactive.above_hispeed_delay.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.interactive.above_hispeed_delay.getDefault()))));
            sp.interactive.above_hispeed_delay.writeValue();
        } else if (key.equals(sp.interactive.timer_rate.getName())) {
            sp.interactive.timer_rate.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.interactive.timer_rate.getDefault()))));
            sp.interactive.timer_rate.writeValue();
        } else if (key.equals(sp.interactive.timer_slack.getName())) {
            sp.interactive.timer_slack.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.interactive.timer_slack.getDefault()))));
            sp.interactive.timer_slack.writeValue();
        } else if (key.equals(sp.interactive.boostpulse_duration.getName())) {
            sp.interactive.boostpulse_duration.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.interactive.boostpulse_duration.getDefault()))));
            sp.interactive.boostpulse_duration.writeValue();
        } else if (key.equals(sp.interactive.target_loads.getName())) {
            sp.interactive.target_loads.setValue(sharedPreferences.getString(key, String.valueOf(sp.interactive.target_loads.getDefValue())));
            sp.interactive.target_loads.writeValue();
        } else if (key.equals(sp.gov.getName())) {
            if (sharedPreferences.getString(sp.gov.getName(), sp.gov.getDefValue()).equals(sp.ondemand.getName())) {
                sp.gov.setValue(sharedPreferences.getString(key, sp.gov.getDefValue()));
                sp.gov.writeValue();
                ((SwitchPreference) findPreference(sp.ondemand.io_is_busy.getName())).setChecked(sp.ondemand.io_is_busy.getBoolean());
                ((EditTextPreference) findPreference(sp.ondemand.sampling_down_factor.getName())).setText(sp.ondemand.sampling_down_factor.getValString());
                ((EditTextPreference) findPreference(sp.ondemand.sampling_rate.getName())).setText(sp.ondemand.sampling_rate.getValString());
                ((EditTextPreference) findPreference(sp.ondemand.up_threshold.getName())).setText(sp.ondemand.up_threshold.getValString());
                sp.conservative.cons.setValue(false);
                sp.conservative.cons.writeValue();
            } else if (sharedPreferences.getString(sp.gov.getName(), sp.gov.getDefValue()).equals(sp.conservative.getName())) {
                sp.conservative.cons.setValue(true);
                sp.conservative.cons.writeValue();
                sp.gov.setValue(sharedPreferences.getString(key, sp.gov.getDefValue()));
                sp.gov.writeValue();
                ((EditTextPreference) findPreference(sp.conservative.freq_step.getName())).setText(sp.conservative.freq_step.getValString());
                ((EditTextPreference) findPreference(sp.conservative.sampling_down_factor.getName())).setText(sp.conservative.sampling_down_factor.getValString());
                ((EditTextPreference) findPreference(sp.conservative.sampling_rate.getName())).setText(sp.conservative.sampling_rate.getValString());
                ((EditTextPreference) findPreference(sp.conservative.up_threshold.getName())).setText(sp.conservative.up_threshold.getValString());
                ((EditTextPreference) findPreference(sp.conservative.down_threshold.getName())).setText(sp.conservative.down_threshold.getValString());
            } else if (sharedPreferences.getString(sp.gov.getName(), sp.gov.getDefValue()).equals(sp.interactive.getName())) {
                sp.interactive.inter.setValue(true);
                sp.interactive.inter.writeValue();
                sp.gov.setValue(sharedPreferences.getString(key, sp.gov.getDefValue()));
                sp.gov.writeValue();
                ((EditTextPreference) findPreference(sp.interactive.hispeed_freq.getName())).setText(sp.interactive.hispeed_freq.getValString());
                ((EditTextPreference) findPreference(sp.interactive.go_hispeed_load.getName())).setText(sp.interactive.go_hispeed_load.getValString());
                ((EditTextPreference) findPreference(sp.interactive.min_sampling_time.getName())).setText(sp.interactive.min_sampling_time.getValString());
                ((EditTextPreference) findPreference(sp.interactive.above_hispeed_delay.getName())).setText(sp.interactive.above_hispeed_delay.getValString());
                ((EditTextPreference) findPreference(sp.interactive.timer_rate.getName())).setText(sp.interactive.timer_rate.getValString());
                ((EditTextPreference) findPreference(sp.interactive.timer_slack.getName())).setText(sp.interactive.timer_slack.getValString());
                ((EditTextPreference) findPreference(sp.interactive.boostpulse_duration.getName())).setText(sp.interactive.boostpulse_duration.getValString());
                ((EditTextPreference) findPreference(sp.interactive.target_loads.getName())).setText(sp.interactive.target_loads.getValue());
                sp.conservative.cons.setValue(false);
                sp.conservative.cons.writeValue();
            }
        } else if (key.equals("uv_apply_boot")) {
            sp.uv.apply_boot = sharedPreferences.getBoolean(key, false);
        } else if (key.equals("uv_enabled")) {
            sp.uv.enabled = sharedPreferences.getBoolean(key, false);
            if (!sp.uv.enabled) {
                pref = findPreference("uv_apply_boot");
                ((CheckBoxPreference) pref).setChecked(false);
            }
        } else if (key.equals(sp.uv.uv_lower_uv.getName())) {
            sp.uv.uv_lower_uv.setValue(sharedPreferences.getInt(key, sp.uv.uv_lower_uv.getDefault()));
        } else if (key.equals(sp.uv.uv_higher_uv.getName())) {
            sp.uv.uv_higher_uv.setValue(sharedPreferences.getInt(key, sp.uv.uv_higher_uv.getDefault()));
        } else if (key.equals(sp.uv.uv_higher_khz_thres.getName())) {
            sp.uv.uv_higher_khz_thres.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.uv.uv_higher_khz_thres.getDefault()))));
        } else if (key.equals(sp.uv.uv_boost.getName())) {
            sp.uv.uv_boost.setValue(sharedPreferences.getBoolean(key, sp.uv.uv_boost.getDefBoolean()) == true ? 1 : 0);
        } else if (key.equals(sp.hp_enabled.getName())) {
            sp.hp_enabled.setValue(sharedPreferences.getBoolean(key, sp.hp_enabled.getDefBoolean()) == true ? 1 : 0);
            sp.hp_enabled.writeValue();
        } else if (key.equals(sp.hp_min_online.getName())) {
            sp.hp_min_online.setValue(sharedPreferences.getInt(key, sp.hp_min_online.getDefault()));
            sp.hp_min_online.writeValue();
        } else if (key.equals(sp.hp_max_online.getName())) {
            sp.hp_max_online.setValue(sharedPreferences.getInt(key, sp.hp_max_online.getDefault()));
            sp.hp_max_online.writeValue();
        } else if (key.equals(sp.hp_up_threshold.getName())) {
            sp.hp_up_threshold.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.hp_up_threshold.getDefault()))));
            sp.hp_up_threshold.writeValue();

//        } else if (key.equals(sp.active_cores.getName())) {
//            sp.active_cores.setValue(sharedPreferences.getInt(key,sp.active_cores.getDefault()));
//            sp.active_cores.writeValue();
        }
    }
    
    private void writeI9000(SharedPreferences sharedPreferences, String key) {
        SemaI9000Properties sp = (SemaI9000Properties) scp;

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

        if (key.equals(sp.oc.getName())) {
            sp.oc.setValue(sharedPreferences.getInt(key, sp.oc.getDefault()));
            sp.oc.writeValue();
            
            updateCVTitles();
        } else if (key.equals(sp.ondemand.io_is_busy.getName())) {
            sp.ondemand.io_is_busy.setValue(sharedPreferences.getBoolean(key, sp.ondemand.io_is_busy.getDefBoolean()) == true ? 1 : 0);
            sp.ondemand.io_is_busy.writeValue();
        } else if (key.equals(sp.ondemand.sampling_down_factor.getName())) {
            sp.ondemand.sampling_down_factor.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.ondemand.sampling_down_factor.getDefault()))));
            sp.ondemand.sampling_down_factor.writeValue();
        } else if (key.equals(sp.ondemand.sampling_down_max_momentum.getName())) {
            sp.ondemand.sampling_down_max_momentum.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.ondemand.sampling_down_max_momentum.getDefault()))));
            sp.ondemand.sampling_down_max_momentum.writeValue();
        } else if (key.equals(sp.ondemand.sampling_rate.getName())) {
            sp.ondemand.sampling_rate.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.ondemand.sampling_rate.getDefault()))));
            sp.ondemand.sampling_rate.writeValue();
        } else if (key.equals(sp.ondemand.up_threshold.getName())) {
            sp.ondemand.up_threshold.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.ondemand.up_threshold.getDefault()))));
            sp.ondemand.up_threshold.writeValue();
        } else if (key.equals(sp.ondemand.smooth_ui.getName())) {
            sp.ondemand.smooth_ui.setValue(sharedPreferences.getBoolean(key, sp.ondemand.smooth_ui.getDefBoolean()) == true ? 1 : 0);
            sp.ondemand.smooth_ui.writeValue();
        } else if (key.equals(sp.conservative.freq_step.getName())) { // Conservative
            sp.conservative.freq_step.setValue(Integer.parseInt(sharedPreferences.getString(key, sp.conservative.freq_step.getDefString())));
            sp.conservative.freq_step.writeValue();
        } else if (key.equals(sp.conservative.sampling_down_factor.getName())) {
            sp.conservative.sampling_down_factor.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.conservative.sampling_down_factor.getDefault()))));
            sp.conservative.sampling_down_factor.writeValue();
        } else if (key.equals(sp.conservative.sampling_rate.getName())) {
            sp.conservative.sampling_rate.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.conservative.sampling_rate.getDefault()))));
            sp.conservative.sampling_rate.writeValue();
        } else if (key.equals(sp.conservative.up_threshold.getName())) {
            sp.conservative.up_threshold.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.conservative.up_threshold.getDefault()))));
            sp.conservative.up_threshold.writeValue();
        } else if (key.equals(sp.conservative.down_threshold.getName())) {
            sp.conservative.down_threshold.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.conservative.down_threshold.getDefault()))));
            sp.conservative.down_threshold.writeValue();
        } else if (key.equals(sp.conservative.smooth_ui.getName())) {
            sp.conservative.smooth_ui.setValue(sharedPreferences.getBoolean(key, sp.conservative.smooth_ui.getDefBoolean()) == true ? 1 : 0);
            sp.conservative.smooth_ui.writeValue();
        } else if (key.equals(sp.smartass.awake_ideal_freq.getName())) { // Smartass
            sp.smartass.awake_ideal_freq.setValue(Integer.parseInt(sharedPreferences.getString(key, sp.smartass.awake_ideal_freq.getDefString())));
            sp.smartass.awake_ideal_freq.writeValue();
        } else if (key.equals(sp.smartass.up_rate.getName())) {
            sp.smartass.up_rate.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.smartass.up_rate.getDefault()))));
            sp.smartass.up_rate.writeValue();
        } else if (key.equals(sp.smartass.down_rate.getName())) {
            sp.smartass.down_rate.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.smartass.down_rate.getDefault()))));
            sp.smartass.down_rate.writeValue();
        } else if (key.equals(sp.smartass.max_cpu_load.getName())) {
            sp.smartass.max_cpu_load.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.smartass.max_cpu_load.getDefault()))));
            sp.smartass.max_cpu_load.writeValue();
        } else if (key.equals(sp.smartass.min_cpu_load.getName())) {
            sp.smartass.min_cpu_load.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.smartass.min_cpu_load.getDefault()))));
            sp.smartass.min_cpu_load.writeValue();
        } else if (key.equals(sp.smartass.ramp_up_step.getName())) {
            sp.smartass.ramp_up_step.setValue(Integer.parseInt(sharedPreferences.getString(key, sp.smartass.ramp_up_step.getDefString())));
            sp.smartass.ramp_up_step.writeValue();
        } else if (key.equals(sp.smartass.ramp_down_step.getName())) {
            sp.smartass.ramp_down_step.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.smartass.ramp_down_step.getDefault()))));
            sp.smartass.ramp_down_step.writeValue();
        } else if (key.equals(sp.smartass.sleep_wakeup_freq.getName())) {
            sp.smartass.sleep_wakeup_freq.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.smartass.sleep_wakeup_freq.getDefault()))));
            sp.smartass.sleep_wakeup_freq.writeValue();
        } else if (key.equals(sp.smartass.sleep_ideal_freq.getName())) {
            sp.smartass.sleep_ideal_freq.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.smartass.sleep_ideal_freq.getDefault()))));
            sp.smartass.sleep_ideal_freq.writeValue();
        } else if (key.equals(sp.smartass.sample_rate_jiffies.getName())) {
            sp.smartass.sample_rate_jiffies.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.smartass.sample_rate_jiffies.getDefault()))));
            sp.smartass.sample_rate_jiffies.writeValue();
        } else if (key.equals(sp.smartass.smooth_ui.getName())) {
            sp.smartass.smooth_ui.setValue(sharedPreferences.getBoolean(key, sp.smartass.smooth_ui.getDefBoolean()) == true ? 1 : 0);
            sp.smartass.smooth_ui.writeValue();
        } else if (key.equals(sp.interactive.hispeed_freq.getName())) { // Interactive
            sp.interactive.hispeed_freq.setValue(Integer.parseInt(sharedPreferences.getString(key, sp.interactive.hispeed_freq.getDefString())));
            sp.interactive.hispeed_freq.writeValue();
        } else if (key.equals(sp.interactive.go_hispeed_load.getName())) {
            sp.interactive.go_hispeed_load.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.interactive.go_hispeed_load.getDefault()))));
            sp.interactive.go_hispeed_load.writeValue();
        } else if (key.equals(sp.interactive.min_sampling_time.getName())) {
            sp.interactive.min_sampling_time.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.interactive.min_sampling_time.getDefault()))));
            sp.interactive.min_sampling_time.writeValue();
        } else if (key.equals(sp.interactive.above_hispeed_delay.getName())) {
            sp.interactive.above_hispeed_delay.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.interactive.above_hispeed_delay.getDefault()))));
            sp.interactive.above_hispeed_delay.writeValue();
        } else if (key.equals(sp.interactive.timer_rate.getName())) {
            sp.interactive.timer_rate.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.interactive.timer_rate.getDefault()))));
            sp.interactive.timer_rate.writeValue();
        } else if (key.equals(sp.interactive.timer_slack.getName())) {
            sp.interactive.timer_slack.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.interactive.timer_slack.getDefault()))));
            sp.interactive.timer_slack.writeValue();
        } else if (key.equals(sp.interactive.boostpulse_duration.getName())) {
            sp.interactive.boostpulse_duration.setValue(Integer.parseInt(sharedPreferences.getString(key, String.valueOf(sp.interactive.boostpulse_duration.getDefault()))));
            sp.interactive.boostpulse_duration.writeValue();
        } else if (key.equals(sp.interactive.target_loads.getName())) {
            sp.interactive.target_loads.setValue(sharedPreferences.getString(key, String.valueOf(sp.interactive.target_loads.getDefValue())));
            sp.interactive.target_loads.writeValue();
        } else if (key.equals(sp.gov.getName())) {
            if (sharedPreferences.getString(sp.gov.getName(), sp.gov.getDefValue()).equals(sp.ondemand.getName())) {
                sp.gov.setValue(sharedPreferences.getString(key, sp.gov.getDefValue()));
                sp.gov.writeValue();
                ((SwitchPreference) findPreference(sp.ondemand.io_is_busy.getName())).setChecked(sp.ondemand.io_is_busy.getBoolean());
                ((EditTextPreference) findPreference(sp.ondemand.sampling_down_factor.getName())).setText(sp.ondemand.sampling_down_factor.getValString());
                ((EditTextPreference) findPreference(sp.ondemand.sampling_down_max_momentum.getName())).setText(sp.ondemand.sampling_down_max_momentum.getValString());
                ((EditTextPreference) findPreference(sp.ondemand.sampling_rate.getName())).setText(sp.ondemand.sampling_rate.getValString());
                ((EditTextPreference) findPreference(sp.ondemand.up_threshold.getName())).setText(sp.ondemand.up_threshold.getValString());
                ((SwitchPreference) findPreference(sp.ondemand.smooth_ui.getName())).setChecked(sp.ondemand.smooth_ui.getBoolean());
                sp.conservative.cons.setValue(false);
                sp.conservative.cons.writeValue();
            } else if (sharedPreferences.getString(sp.gov.getName(), sp.gov.getDefValue()).equals(sp.conservative.getName())) {
                sp.conservative.cons.setValue(true);
                sp.conservative.cons.writeValue();
                sp.gov.setValue(sharedPreferences.getString(key, sp.gov.getDefValue()));
                sp.gov.writeValue();
                ((EditTextPreference) findPreference(sp.conservative.freq_step.getName())).setText(sp.conservative.freq_step.getValString());
                ((EditTextPreference) findPreference(sp.conservative.sampling_down_factor.getName())).setText(sp.conservative.sampling_down_factor.getValString());
                ((EditTextPreference) findPreference(sp.conservative.sampling_rate.getName())).setText(sp.conservative.sampling_rate.getValString());
                ((EditTextPreference) findPreference(sp.conservative.up_threshold.getName())).setText(sp.conservative.up_threshold.getValString());
                ((EditTextPreference) findPreference(sp.conservative.down_threshold.getName())).setText(sp.conservative.down_threshold.getValString());
                ((SwitchPreference) findPreference(sp.conservative.smooth_ui.getName())).setChecked(sp.conservative.smooth_ui.getBoolean());
            } else if (sharedPreferences.getString(sp.gov.getName(), sp.gov.getDefValue()).equals(sp.smartass.getName())) {
                sp.smartass.smart.setValue(true);
                sp.smartass.smart.writeValue();
                sp.gov.setValue(sharedPreferences.getString(key, sp.gov.getDefValue()));
                sp.gov.writeValue();
                ((EditTextPreference) findPreference(sp.smartass.awake_ideal_freq.getName())).setText(sp.smartass.awake_ideal_freq.getValString());
                ((EditTextPreference) findPreference(sp.smartass.up_rate.getName())).setText(sp.smartass.up_rate.getValString());
                ((EditTextPreference) findPreference(sp.smartass.down_rate.getName())).setText(sp.smartass.down_rate.getValString());
                ((EditTextPreference) findPreference(sp.smartass.max_cpu_load.getName())).setText(sp.smartass.max_cpu_load.getValString());
                ((EditTextPreference) findPreference(sp.smartass.min_cpu_load.getName())).setText(sp.smartass.min_cpu_load.getValString());
                ((EditTextPreference) findPreference(sp.smartass.ramp_up_step.getName())).setText(sp.smartass.ramp_up_step.getValString());
                ((EditTextPreference) findPreference(sp.smartass.ramp_down_step.getName())).setText(sp.smartass.ramp_down_step.getValString());
                ((EditTextPreference) findPreference(sp.smartass.sleep_wakeup_freq.getName())).setText(sp.smartass.sleep_wakeup_freq.getValString());
                ((EditTextPreference) findPreference(sp.smartass.sleep_ideal_freq.getName())).setText(sp.smartass.sleep_ideal_freq.getValString());
                ((EditTextPreference) findPreference(sp.smartass.sample_rate_jiffies.getName())).setText(sp.smartass.sample_rate_jiffies.getValString());
                ((SwitchPreference) findPreference(sp.smartass.smooth_ui.getName())).setChecked(sp.smartass.smooth_ui.getBoolean());
                sp.conservative.cons.setValue(false);
                sp.conservative.cons.writeValue();
            } else if (sharedPreferences.getString(sp.gov.getName(), sp.gov.getDefValue()).equals(sp.interactive.getName())) {
                sp.interactive.inter.setValue(true);
                sp.interactive.inter.writeValue();
                sp.gov.setValue(sharedPreferences.getString(key, sp.gov.getDefValue()));
                sp.gov.writeValue();
                ((EditTextPreference) findPreference(sp.interactive.hispeed_freq.getName())).setText(sp.interactive.hispeed_freq.getValString());
                ((EditTextPreference) findPreference(sp.interactive.go_hispeed_load.getName())).setText(sp.interactive.go_hispeed_load.getValString());
                ((EditTextPreference) findPreference(sp.interactive.min_sampling_time.getName())).setText(sp.interactive.min_sampling_time.getValString());
                ((EditTextPreference) findPreference(sp.interactive.above_hispeed_delay.getName())).setText(sp.interactive.above_hispeed_delay.getValString());
                ((EditTextPreference) findPreference(sp.interactive.timer_rate.getName())).setText(sp.interactive.timer_rate.getValString());
                ((EditTextPreference) findPreference(sp.interactive.timer_slack.getName())).setText(sp.interactive.timer_slack.getValString());
                ((EditTextPreference) findPreference(sp.interactive.boostpulse_duration.getName())).setText(sp.interactive.boostpulse_duration.getValString());
                ((EditTextPreference) findPreference(sp.interactive.target_loads.getName())).setText(sp.interactive.target_loads.getValue());
                sp.conservative.cons.setValue(false);
                sp.conservative.cons.writeValue();
            }
        } else if (key.equals(sp.deep_idle.getName())) {
            sp.deep_idle.setValue(sharedPreferences.getBoolean(key, sp.deep_idle.getDefBoolean()) == true ? 1 : 0);
            sp.deep_idle.writeValue();
        } else if (key.equals(sp.deep_idle_stats.getName())) {
            sp.deep_idle_stats.setValue(sharedPreferences.getBoolean(key, sp.deep_idle_stats.getDefBoolean()) == true ? 1 : 0);
            sp.deep_idle_stats.writeValue();
        } else if (key.equals(sp.lock_min.getName())) {
            sp.lock_min.setValue(sharedPreferences.getBoolean(key, sp.lock_min.getDefBoolean()) == true ? 1 : 0);
            sp.lock_min.writeValue();
        } else if (key.equals(sp.bluetooth.getName())) {
            sp.bluetooth.setValue(sharedPreferences.getBoolean(key, sp.bluetooth.getDefBoolean()) == true ? 1 : 0);
            if (sp.bluetooth.getBoolean())
                sp.bluetooth.writeValue();
        } else if (key.equals("cv_apply_boot")) {
            sp.cv.apply_boot = sharedPreferences.getBoolean(key, false);
        } else if (key.equals("cv_enable")) {
            sp.cv.enabled = sharedPreferences.getBoolean(key, false);
            if (!sp.cv.enabled) {
                pref = findPreference("cv_apply_boot");
                ((CheckBoxPreference) pref).setChecked(false);
            }
        } else if (key.equals("cv_max_arm")) {
            sp.cv.cv_max_arm.setValue(sharedPreferences.getInt(key, sp.cv.cv_max_arm.getDefault()));
            //((SeekBarPreference) findPreference(sp.cv.cv_max_arm.getName())).setProgress(sp.cv.cv_max_arm.getDefault());
        } else if (key.equals("cv_l0")) {
            sp.cv.cv_l0.setValue(sharedPreferences.getInt(key, sp.cv.cv_l0.getDefault()));
        } else if (key.equals("cv_l1")) {
            sp.cv.cv_l1.setValue(sharedPreferences.getInt(key, sp.cv.cv_l1.getDefault()));
        } else if (key.equals("cv_l2")) {
            sp.cv.cv_l2.setValue(sharedPreferences.getInt(key, sp.cv.cv_l2.getDefault()));
        } else if (key.equals("cv_l3")) {
            sp.cv.cv_l3.setValue(sharedPreferences.getInt(key, sp.cv.cv_l3.getDefault()));
        } else if (key.equals("cv_l4")) {
            sp.cv.cv_l4.setValue(sharedPreferences.getInt(key, sp.cv.cv_l4.getDefault()));
        }
        pref = findPreference("cv_cv");
        pref.setSummary(sp.cv.getVolts());        
    }
    
    public void updateCVTitles () {
        SemaI9000Properties sp = (SemaI9000Properties) scp;

        Resources res = getResources();
        Preference pref;

        pref = findPreference("cv_l0");
        pref.setTitle(res.getString(R.string.str_cv_l0_title) + " (" + String.valueOf(1000 * sp.oc.getValue() / 100) + "MHz)");
        pref = findPreference("cv_l1");
        pref.setTitle(res.getString(R.string.str_cv_l1_title) + " (" + String.valueOf(800 * sp.oc.getValue() / 100) + "MHz)");
        pref = findPreference("cv_l2");
        pref.setTitle(res.getString(R.string.str_cv_l2_title) + " (" + String.valueOf(400 * sp.oc.getValue() / 100) + "MHz)");
        pref = findPreference("cv_l3");
        pref.setTitle(res.getString(R.string.str_cv_l3_title) + " (" + String.valueOf(200 * sp.oc.getValue() / 100) + "MHz)");
        pref = findPreference("cv_l4");
        pref.setTitle(res.getString(R.string.str_cv_l4_title) + " (" + String.valueOf(100 * sp.oc.getValue() / 100) + "MHz)");
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (MainActivity.readingValues)
            return;

        scp = MainActivity.sp;
        
        if (MainActivity.Device == MainActivity.SemaDevices.Mako)
            writeMako(sharedPreferences, key);
        else
            writeI9000(sharedPreferences, key);
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
    
    private void updateSummariesN4() {
        SemaN4Properties sp = (SemaN4Properties) scp;
        
        Preference pref = findPreference(sp.gov.getName());
        if (pref == null) {
            return;
        }

        if (((ListPreference) pref).getEntry() != null) {
            pref.setSummary(((ListPreference) pref).getEntry().toString());
        }

        pref = findPreference(sp.ondemand.sampling_down_factor.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.ondemand.sampling_rate.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.ondemand.up_threshold.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.ondemand.touch_load.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.ondemand.touch_load_threshold.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.ondemand.touch_load_duration.getName());
        pref.setSummary(((EditTextPreference) pref).getText());

        pref = findPreference(sp.conservative.freq_step.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.conservative.sampling_down_factor.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.conservative.sampling_rate.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.conservative.up_threshold.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.conservative.down_threshold.getName());
        pref.setSummary(((EditTextPreference) pref).getText());

        pref = findPreference(sp.interactive.hispeed_freq.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.interactive.go_hispeed_load.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.interactive.min_sampling_time.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.interactive.above_hispeed_delay.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.interactive.timer_rate.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.interactive.timer_slack.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.interactive.boostpulse_duration.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.interactive.target_loads.getName());
        pref.setSummary(((EditTextPreference) pref).getText());

        pref = findPreference(sp.uv.uv_lower_uv.getName());
        pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));
        pref = findPreference(sp.uv.uv_higher_uv.getName());
        pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));
        pref = findPreference(sp.uv.uv_higher_khz_thres.getName());
        pref.setSummary(((EditTextPreference) pref).getText());

        pref = findPreference(sp.hp_up_threshold.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.hp_min_online.getName());
        pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));
        pref = findPreference(sp.hp_max_online.getName());
        pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));
        //pref = findPreference(sp.active_cores.getName());
        //pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));
    }
    
    private void updateSummariesI9000() {
        SemaI9000Properties sp = (SemaI9000Properties) scp;

        Preference pref = findPreference(sp.gov.getName());
        if (pref == null) {
            return;
        }

        if (((ListPreference) pref).getEntry() != null) {
            pref.setSummary(((ListPreference) pref).getEntry().toString());
        }

        pref = findPreference(sp.oc.getName());
        pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));

        pref = findPreference(sp.ondemand.sampling_down_factor.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.ondemand.sampling_down_max_momentum.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.ondemand.sampling_rate.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.ondemand.up_threshold.getName());
        pref.setSummary(((EditTextPreference) pref).getText());

        pref = findPreference(sp.conservative.freq_step.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.conservative.sampling_down_factor.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.conservative.sampling_rate.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.conservative.up_threshold.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.conservative.down_threshold.getName());
        pref.setSummary(((EditTextPreference) pref).getText());

        pref = findPreference(sp.smartass.awake_ideal_freq.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.smartass.up_rate.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.smartass.down_rate.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.smartass.max_cpu_load.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.smartass.min_cpu_load.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.smartass.ramp_up_step.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.smartass.ramp_down_step.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.smartass.sleep_wakeup_freq.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.smartass.sleep_ideal_freq.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.smartass.sample_rate_jiffies.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        
        pref = findPreference(sp.interactive.hispeed_freq.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.interactive.go_hispeed_load.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.interactive.min_sampling_time.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.interactive.above_hispeed_delay.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.interactive.timer_rate.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.interactive.timer_slack.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.interactive.boostpulse_duration.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        pref = findPreference(sp.interactive.target_loads.getName());
        pref.setSummary(((EditTextPreference) pref).getText());
        
        pref = findPreference(sp.cv.cv_max_arm.getName());
        pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));
        pref = findPreference(sp.cv.cv_l0.getName());
        pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));
        pref = findPreference(sp.cv.cv_l1.getName());
        pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));
        pref = findPreference(sp.cv.cv_l2.getName());
        pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));
        pref = findPreference(sp.cv.cv_l3.getName());
        pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));
        pref = findPreference(sp.cv.cv_l4.getName());
        pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));
        
        pref = findPreference("cv_cv");
        pref.setSummary(sp.cv.getVolts());
        
        updateCVTitles();
    }

    public boolean onPreferenceClick(Preference preference) {
        boolean ret = false;
        if (preference.getKey().equals("deep_idle_stats_show")) {
            showIdleDialog();
            ret = true;
        } else if (preference.getKey().equals("cv_apply")) {
            SemaI9000Properties sp = (SemaI9000Properties) scp;
            sp.cv.writeValue();
            Toast.makeText(getActivity(), "Custom voltages applied", Toast.LENGTH_SHORT).show();
        } else if (preference.getKey().equals("cv_reset")) {
            SemaI9000Properties sp = (SemaI9000Properties) scp;
            sp.cv.cv_max_arm.setValue(sp.cv.cv_max_arm.getDefault());
            sp.cv.cv_l0.setValue(sp.cv.cv_l0.getDefault());
            sp.cv.cv_l1.setValue(sp.cv.cv_l1.getDefault());
            sp.cv.cv_l2.setValue(sp.cv.cv_l2.getDefault());
            sp.cv.cv_l3.setValue(sp.cv.cv_l3.getDefault());
            sp.cv.cv_l4.setValue(sp.cv.cv_l4.getDefault());
            sp.cv.writeValue();
            sp.oc.writeValue(); // Also re-apply oc value to set L0, L1 voltages
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor edit = prefs.edit();            
            edit.putInt(sp.cv.cv_max_arm.getName(), sp.cv.cv_max_arm.getDefault());
            edit.putInt(sp.cv.cv_l0.getName(), sp.cv.cv_l0.getDefault());
            edit.putInt(sp.cv.cv_l1.getName(), sp.cv.cv_l1.getDefault());
            edit.putInt(sp.cv.cv_l2.getName(), sp.cv.cv_l2.getDefault());
            edit.putInt(sp.cv.cv_l3.getName(), sp.cv.cv_l3.getDefault());
            edit.putInt(sp.cv.cv_l4.getName(), sp.cv.cv_l4.getDefault());
            edit.commit();
            updateSummariesI9000();
            Toast.makeText(getActivity(), "Custom voltages reset to default", Toast.LENGTH_SHORT).show();
            getActivity().recreate();
        } else if (preference.getKey().equals("uv_apply")) {
            SemaN4Properties sp = (SemaN4Properties) scp;
            sp.uv.writeValue();
            Toast.makeText(getActivity(), "Undervolting applied", Toast.LENGTH_SHORT).show();
        } else if (preference.getKey().equals("uv_reset")) {
            AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
            ad.setMessage("Undervolting settings will be reset to default.\nDo you want to continue?");
            ad.setCancelable(false);
            ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SemaN4Properties sp = (SemaN4Properties) scp;
                sp.uv.uv_boost.setValue(sp.uv.uv_boost.getDefault());
                sp.uv.uv_higher_khz_thres.setValue(sp.uv.uv_higher_khz_thres.getDefault());
                sp.uv.uv_lower_uv.setValue(sp.uv.uv_lower_uv.getDefault());
                sp.uv.uv_higher_uv.setValue(sp.uv.uv_higher_uv.getDefault());
                sp.uv.writeValue();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor edit = prefs.edit();            
                edit.putBoolean(sp.uv.uv_boost.getName(), sp.uv.uv_boost.getDefBoolean());
                edit.putString(sp.uv.uv_higher_khz_thres.getName(), sp.uv.uv_higher_khz_thres.getDefString());
                edit.putInt(sp.uv.uv_lower_uv.getName(), sp.uv.uv_lower_uv.getDefault());
                edit.putInt(sp.uv.uv_higher_uv.getName(), sp.uv.uv_higher_uv.getDefault());
                edit.commit();
                updateSummariesN4();
                Toast.makeText(getActivity(), "Undervolting values reset to default", Toast.LENGTH_SHORT).show();
                getActivity().recreate();
            }
            });
            ad.setNegativeButton("Cancel", null);
            ad.show();
        } else if (preference.getKey().equals("uv_cpu_table")) {
            showCPUTableDialog();
            ret = true;
        }
        return ret;
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