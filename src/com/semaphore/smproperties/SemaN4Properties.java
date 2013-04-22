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
    public SMOndemandProperty ondemand;
    public SMConservativeProperty conservative;
    public SMInteractiveProperty interactive;
    public SMSchedulerProperty scheduler;
    public SMIntProperty touch_enable;
    public SMIntProperty touch;

    public SemaN4Properties() {

        gov = new SMStringProperty("gov", "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor", false, "ondemand");
        ondemand = new SMOndemandProperty();
        conservative = new SMConservativeProperty();
        interactive = new SMInteractiveProperty();

        scheduler = new SMSchedulerProperty("scheduler", "row");
        touch_enable = new SMIntProperty("touch_enable", "/sys/devices/virtual/misc/touchwake/enabled", false, 0, 1, 0);
        touch = new SMIntProperty("touch", "/sys/devices/virtual/misc/touchwake/delay", false, 0, 90000, 45000);
        //touchscreen = new SMTouchscreenProperty("touchscreen", "stock");
    }

    @Override
    public void readValues() {
        gov.readValue();
        ondemand.readValue();
        conservative.readValue();
        interactive.readValue();

        scheduler.readValue();
        touch_enable.readValue();
        touch.readValue();
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
        touch_enable.writeBatch(cmds);
        touch.writeBatch(cmds);

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
        touch_enable.writeValue();
        touch.writeValue();
    }

    @Override
    public void setPreferences(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit = prefs.edit();

        edit.putString(gov.getName(), gov.getValue());
        // ondemand tunables
        edit.putBoolean(ondemand.io_is_busy.getName(), ondemand.io_is_busy.getBoolean());
        edit.putString(ondemand.sampling_down_factor.getName(), ondemand.sampling_down_factor.getValString());
        edit.putString(ondemand.sampling_down_max_momentum.getName(), ondemand.sampling_down_max_momentum.getValString());
        edit.putString(ondemand.sampling_rate.getName(), ondemand.sampling_rate.getValString());
        edit.putString(ondemand.up_threshold.getName(), ondemand.up_threshold.getValString());
        edit.putBoolean(ondemand.smooth_ui.getName(), ondemand.smooth_ui.getBoolean());
        edit.putBoolean(ondemand.smooth_ui.getName(), ondemand.early_demand.getBoolean());
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
        
        edit.putBoolean(touch_enable.getName(), touch_enable.getBoolean());
        edit.putInt(touch.getName(), touch.getValue());
        edit.commit();
    }

    @Override
    public void getPreferences(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        gov.setValue(prefs.getString(gov.getName(), gov.getDefValue()));
        ondemand.io_is_busy.setValue(prefs.getBoolean(ondemand.io_is_busy.getName(), ondemand.io_is_busy.getDefault() == 1 ? true : false) == true ? 1 : 0);
        ondemand.sampling_down_factor.setValue(prefs.getString(ondemand.sampling_down_factor.getName(), ondemand.sampling_down_factor.getDefString()));
        ondemand.sampling_down_max_momentum.setValue(prefs.getString(ondemand.sampling_down_max_momentum.getName(), ondemand.sampling_down_max_momentum.getDefString()));
        ondemand.sampling_rate.setValue(prefs.getString(ondemand.sampling_rate.getName(), ondemand.sampling_rate.getDefString()));
        ondemand.up_threshold.setValue(prefs.getString(ondemand.up_threshold.getName(), ondemand.up_threshold.getDefString()));
        ondemand.smooth_ui.setValue(prefs.getBoolean(ondemand.smooth_ui.getName(), ondemand.smooth_ui.getDefBoolean()) == true ? 1 : 0);
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
        touch_enable.setValue(prefs.getBoolean(touch_enable.getName(), touch_enable.getDefBoolean()) == true ? 1 : 0);
        touch.setValue(prefs.getInt(touch.getName(), touch.getDefault()));
    }

    @Override
    public void resetDefaults() {
        gov.setValue(gov.getDefValue());
        ondemand.io_is_busy.setValue(ondemand.io_is_busy.getDefault());
        ondemand.sampling_down_factor.setValue(ondemand.sampling_down_factor.getDefString());
        ondemand.sampling_down_max_momentum.setValue(ondemand.sampling_down_max_momentum.getDefString());
        ondemand.sampling_rate.setValue(ondemand.sampling_rate.getDefString());
        ondemand.up_threshold.setValue(ondemand.up_threshold.getDefString());
        ondemand.smooth_ui.setValue(ondemand.smooth_ui.getDefault());
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
        touch_enable.setValue(touch_enable.getDefault());
        touch.setValue(touch.getDefault());
    }
}
