/*  Semaphore Manager
 *  
 *   Copyright (c) 2012-2013 Stratos Karafotis (stratosk@semaphore.gr)
 *   
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 */
package com.semaphore.smproperties;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import com.semaphore.sm.Commander;
import java.util.ArrayList;
import java.util.List;

public class SemaN4Properties extends SemaCommonProperties {

    public SMStringProperty gov;
    public SMOndemandN4Property ondemand;
    public SMConservativeProperty conservative;
    public SMInteractiveProperty interactive;
    public SMSchedulerProperty scheduler;
    public SMIntProperty vibrator;
    public SMIntProperty touch_enable;
    public SMIntProperty touch;
    public SMTouchAccuracyProperty taccuracy;
    public SMTouchJitterProperty tjitter;
    public SMLoggerProperty logger;
    
    public SemaN4Properties() {

        gov = new SMStringProperty("gov", "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor", false, "ondemand");
        ondemand = new SMOndemandN4Property();
        
        conservative = new SMConservativeProperty();
        conservative.down_threshold.setDefault(20);
        conservative.up_threshold.setDefault(80);
        conservative.sampling_rate.setDefault(200000);
        
        interactive = new SMInteractiveProperty();
        interactive.min_sampling_time.setDefault(80000);
        interactive.timer_rate.setDefault(20000);
        interactive.above_hispeed_delay.setDefault(20000);
        interactive.go_hispeed_load.setDefault(85);
        interactive.hispeed_freq.setMaxValue(1512000);
        interactive.hispeed_freq.setDefault(1512000);
        
        scheduler = new SMSchedulerProperty("scheduler", "row");
        scheduler.basepath = "/sys/block/mmcblk0/queue/scheduler";

        vibrator = new SMIntProperty("vibrator", "/sys/class/timed_output/vibrator/amp", false, 0, 100, 70);        
        touch_enable = new SMIntProperty("touch_enable", "/sys/devices/virtual/misc/touchwake/enabled", false, 0, 1, 0);
        touch = new SMIntProperty("touch", "/sys/devices/virtual/misc/touchwake/delay", false, 0, 90000, 45000);
        //touchscreen = new SMTouchscreenProperty("touchscreen", "stock");
        
        taccuracy = new SMTouchAccuracyProperty();
        tjitter = new SMTouchJitterProperty();
        
        logger = new SMLoggerProperty("logger", "/system/lib/modules", false);
//        tun = new SMModuleProperty("tun", "/lib/modules/tun", false, false);
//        cifs = new SMModuleProperty("cifs", "/lib/modules/cifs", false, false);
//        configs = new SMModuleProperty("configs", "/lib/modules/configs", false, false);
    }

    @Override
    public void readValues() {
        gov.readValue();
        ondemand.readValue();
        conservative.readValue();
        interactive.readValue();

        scheduler.readValue();
        vibrator.readValue();
        touch_enable.readValue();
        touch.readValue();
        
        taccuracy.readValue();
        tjitter.readValue();
    }

    @Override
    public void writeBatch() {
        List<String> cmds = new ArrayList<String>();

        if (gov.getValue().equals(conservative.getName())) {
            conservative.cons.setValue(true);
            interactive.inter.setValue(false);
            conservative.cons.writeBatch(cmds);
        } else if (gov.getValue().equals(interactive.getName())) {
            conservative.cons.setValue(false);
            interactive.inter.setValue(true);
            interactive.inter.writeBatch(cmds);
        } else {
            conservative.cons.setValue(false);
            interactive.inter.setValue(false);            
        }
        gov.writeBatch(cmds);
        ondemand.writeBatch(cmds);
        conservative.writebatch(cmds);
        interactive.writebatch(cmds);

        scheduler.writeBatch(cmds);
        vibrator.writeBatch(cmds);
        touch_enable.writeBatch(cmds);
        touch.writeBatch(cmds);

        taccuracy.writeBatch(cmds);
        tjitter.writeBatch(cmds);
        
        //Log.d("semaphore cmds: ", cmds.toString());
        Commander.getInstance().runSuBatch(cmds);
    }

    @Override
    public void writeValues() {
        if (gov.getValue().equals(conservative.getName())) {
            conservative.cons.setValue(true);
            interactive.inter.setValue(false);
            conservative.cons.writeValue();
        } else if (gov.getValue().equals(interactive.getName())) {
            conservative.cons.setValue(false);
            interactive.inter.setValue(true);
            interactive.inter.writeValue();
        } else {
            conservative.cons.setValue(false);
            interactive.inter.setValue(false);            
        }
        gov.writeValue();
        ondemand.writeValue();
        conservative.writeValue();
        interactive.writeValue();

        scheduler.writeValue();
        vibrator.writeValue();
        touch_enable.writeValue();
        touch.writeValue();
        
        taccuracy.writeValue();
        tjitter.writeValue();
    }

    @Override
    public void setPreferences(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit = prefs.edit();

        edit.putString(gov.getName(), gov.getValue());
        // ondemand tunables
        edit.putBoolean(ondemand.io_is_busy.getName(), ondemand.io_is_busy.getBoolean());
        edit.putString(ondemand.sampling_down_factor.getName(), ondemand.sampling_down_factor.getValString());
        edit.putString(ondemand.sampling_rate.getName(), ondemand.sampling_rate.getValString());
        edit.putString(ondemand.up_threshold.getName(), ondemand.up_threshold.getValString());
        edit.putBoolean(ondemand.early_demand.getName(), ondemand.early_demand.getBoolean());
        edit.putString(ondemand.grad_up_threshold.getName(), ondemand.grad_up_threshold.getValString());
        // conservative
        edit.putBoolean(conservative.cons.getName(), conservative.cons.getValue());
        edit.putString(conservative.freq_step.getName(), conservative.freq_step.getValString());
        edit.putString(conservative.sampling_down_factor.getName(), conservative.sampling_down_factor.getValString());
        edit.putString(conservative.sampling_rate.getName(), conservative.sampling_rate.getValString());
        edit.putString(conservative.up_threshold.getName(), conservative.up_threshold.getValString());
        edit.putString(conservative.down_threshold.getName(), conservative.down_threshold.getValString());
        edit.putBoolean(conservative.smooth_ui.getName(), conservative.smooth_ui.getBoolean());
        // interactive
        edit.putBoolean(interactive.inter.getName(), interactive.inter.getValue());
        edit.putString(interactive.hispeed_freq.getName(), interactive.hispeed_freq.getValString());
        edit.putString(interactive.go_hispeed_load.getName(), interactive.go_hispeed_load.getValString());
        edit.putString(interactive.min_sampling_time.getName(), interactive.min_sampling_time.getValString());
        edit.putString(interactive.above_hispeed_delay.getName(), interactive.above_hispeed_delay.getValString());
        edit.putString(interactive.timer_rate.getName(), interactive.timer_rate.getValString());
        edit.putString(interactive.timer_slack.getName(), interactive.timer_slack.getValString());
        edit.putString(interactive.boostpulse_duration.getName(), interactive.boostpulse_duration.getValString());
        edit.putString(interactive.target_loads.getName(), interactive.target_loads.getValue());
        
        edit.putString(scheduler.getName(), scheduler.getValue());
        edit.putInt(vibrator.getName(), vibrator.getValue());
        edit.putBoolean(touch_enable.getName(), touch_enable.getBoolean());
        edit.putInt(touch.getName(), touch.getValue());
        
        edit.putBoolean(taccuracy.accuracy_filter_enable.getName(), taccuracy.accuracy_filter_enable.getBoolean());
        edit.putString(taccuracy.ignore_pressure_gap.getName(), taccuracy.ignore_pressure_gap.getValString());
        edit.putString(taccuracy.delta_max.getName(), taccuracy.delta_max.getValString());
        edit.putString(taccuracy.touch_max_count.getName(), taccuracy.touch_max_count.getValString());
        edit.putString(taccuracy.max_pressure.getName(), taccuracy.max_pressure.getValString());
        edit.putString(taccuracy.direction_count.getName(), taccuracy.direction_count.getValString());
        edit.putString(taccuracy.time_to_max_pressure.getName(), taccuracy.time_to_max_pressure.getValString());

        edit.putBoolean(tjitter.jitter_enable.getName(), tjitter.jitter_enable.getBoolean());
        edit.putString(tjitter.adjust_margin.getName(), tjitter.adjust_margin.getValString());
        
        edit.commit();
    }

    @Override
    public void getPreferences(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        gov.setValue(prefs.getString(gov.getName(), gov.getDefValue()));
        ondemand.io_is_busy.setValue(prefs.getBoolean(ondemand.io_is_busy.getName(), ondemand.io_is_busy.getDefault() == 1 ? true : false) == true ? 1 : 0);
        ondemand.sampling_down_factor.setValue(prefs.getString(ondemand.sampling_down_factor.getName(), ondemand.sampling_down_factor.getDefString()));
        ondemand.sampling_rate.setValue(prefs.getString(ondemand.sampling_rate.getName(), ondemand.sampling_rate.getDefString()));
        ondemand.up_threshold.setValue(prefs.getString(ondemand.up_threshold.getName(), ondemand.up_threshold.getDefString()));
        ondemand.early_demand.setValue(prefs.getBoolean(ondemand.early_demand.getName(), ondemand.early_demand.getDefBoolean()) == true ? 1 : 0);
        ondemand.grad_up_threshold.setValue(prefs.getString(ondemand.grad_up_threshold.getName(), ondemand.grad_up_threshold.getDefString()));

        conservative.cons.setValue(prefs.getBoolean(conservative.cons.getName(), conservative.cons.getDefValue()));
        conservative.freq_step.setValue(prefs.getString(conservative.freq_step.getName(), conservative.freq_step.getDefString()));
        conservative.sampling_down_factor.setValue(prefs.getString(conservative.sampling_down_factor.getName(), conservative.sampling_down_factor.getDefString()));
        conservative.sampling_rate.setValue(prefs.getString(conservative.sampling_rate.getName(), conservative.sampling_rate.getDefString()));
        conservative.up_threshold.setValue(prefs.getString(conservative.up_threshold.getName(), conservative.up_threshold.getDefString()));
        conservative.down_threshold.setValue(prefs.getString(conservative.down_threshold.getName(), conservative.down_threshold.getDefString()));
        conservative.smooth_ui.setValue(prefs.getBoolean(conservative.smooth_ui.getName(), conservative.smooth_ui.getDefBoolean()) == true ? 1 : 0);

        interactive.inter.setValue(prefs.getBoolean(interactive.inter.getName(), interactive.inter.getDefValue()));
        interactive.hispeed_freq.setValue(prefs.getString(interactive.hispeed_freq.getName(), interactive.hispeed_freq.getDefString()));
        interactive.go_hispeed_load.setValue(prefs.getString(interactive.go_hispeed_load.getName(), interactive.go_hispeed_load.getDefString()));
        interactive.min_sampling_time.setValue(prefs.getString(interactive.min_sampling_time.getName(), interactive.min_sampling_time.getDefString()));
        interactive.above_hispeed_delay.setValue(prefs.getString(interactive.above_hispeed_delay.getName(), interactive.above_hispeed_delay.getDefString()));
        interactive.timer_rate.setValue(prefs.getString(interactive.timer_rate.getName(), interactive.timer_rate.getDefString()));
        interactive.timer_slack.setValue(prefs.getString(interactive.timer_slack.getName(), interactive.timer_slack.getDefString()));
        interactive.boostpulse_duration.setValue(prefs.getString(interactive.boostpulse_duration.getName(), interactive.boostpulse_duration.getDefString()));
        interactive.target_loads.setValue(prefs.getString(interactive.target_loads.getName(), interactive.target_loads.getDefValue()));

        scheduler.setValue(prefs.getString(scheduler.getName(), scheduler.getDefValue()));
        vibrator.setValue(prefs.getInt(vibrator.getName(), vibrator.getDefault()));
        touch_enable.setValue(prefs.getBoolean(touch_enable.getName(), touch_enable.getDefBoolean()) == true ? 1 : 0);
        touch.setValue(prefs.getInt(touch.getName(), touch.getDefault()));

        taccuracy.accuracy_filter_enable.setValue(prefs.getBoolean(taccuracy.accuracy_filter_enable.getName(), taccuracy.accuracy_filter_enable.getDefBoolean()) == true ? 1 : 0);
        taccuracy.ignore_pressure_gap.setValue(prefs.getString(taccuracy.ignore_pressure_gap.getName(), taccuracy.ignore_pressure_gap.getDefString()));
        taccuracy.delta_max.setValue(prefs.getString(taccuracy.delta_max.getName(), taccuracy.delta_max.getDefString()));
        taccuracy.touch_max_count.setValue(prefs.getString(taccuracy.touch_max_count.getName(), taccuracy.touch_max_count.getDefString()));
        taccuracy.max_pressure.setValue(prefs.getString(taccuracy.max_pressure.getName(), taccuracy.max_pressure.getDefString()));
        taccuracy.direction_count.setValue(prefs.getString(taccuracy.direction_count.getName(), taccuracy.direction_count.getDefString()));
        taccuracy.time_to_max_pressure.setValue(prefs.getString(taccuracy.time_to_max_pressure.getName(), taccuracy.time_to_max_pressure.getDefString()));

        tjitter.jitter_enable.setValue(prefs.getBoolean(tjitter.jitter_enable.getName(), tjitter.jitter_enable.getDefBoolean()) == true ? 1 : 0);
        tjitter.adjust_margin.setValue(prefs.getString(tjitter.adjust_margin.getName(), tjitter.adjust_margin.getDefString()));

    }

    @Override
    public void resetDefaults() {
        gov.setValue(gov.getDefValue());
        ondemand.io_is_busy.setValue(ondemand.io_is_busy.getDefault());
        ondemand.sampling_down_factor.setValue(ondemand.sampling_down_factor.getDefString());
        ondemand.sampling_rate.setValue(ondemand.sampling_rate.getDefString());
        ondemand.up_threshold.setValue(ondemand.up_threshold.getDefString());
        ondemand.early_demand.setValue(ondemand.early_demand.getDefault());
        ondemand.grad_up_threshold.setValue(ondemand.grad_up_threshold.getDefString());

        conservative.cons.setValue(conservative.cons.getDefValue());
        conservative.freq_step.setValue(conservative.freq_step.getDefString());
        conservative.sampling_down_factor.setValue(conservative.sampling_down_factor.getDefString());
        conservative.sampling_rate.setValue(conservative.sampling_rate.getDefString());
        conservative.up_threshold.setValue(conservative.up_threshold.getDefString());
        conservative.down_threshold.setValue(conservative.down_threshold.getDefString());
        conservative.smooth_ui.setValue(conservative.smooth_ui.getDefault());

        interactive.inter.setValue(interactive.inter.getDefValue());
        interactive.hispeed_freq.setValue(interactive.hispeed_freq.getDefString());
        interactive.go_hispeed_load.setValue(interactive.go_hispeed_load.getDefString());
        interactive.min_sampling_time.setValue(interactive.min_sampling_time.getDefString());
        interactive.above_hispeed_delay.setValue(interactive.above_hispeed_delay.getDefString());
        interactive.timer_rate.setValue(interactive.timer_rate.getDefString());
        interactive.timer_slack.setValue(interactive.timer_slack.getDefString());
        interactive.boostpulse_duration.setValue(interactive.boostpulse_duration.getDefString());
        interactive.target_loads.setValue(interactive.target_loads.getDefValue());
        
        scheduler.setValue(scheduler.getDefValue());
        vibrator.setValue(vibrator.getDefault());
        touch_enable.setValue(touch_enable.getDefault());
        touch.setValue(touch.getDefault());
        
        taccuracy.setDefValues();
        tjitter.setDefValues();
    }
}
