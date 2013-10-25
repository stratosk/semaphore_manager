/*  Semaphore Manager
 *  
 *   Copyright (c) 2012 - 2013 Stratos Karafotis (stratosk@semaphore.gr)
 *   
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 */
package com.semaphore.smproperties;

import java.util.List;

public class SMSmartassProperty extends SMBatchProperty {

    public SMModuleProperty smart;
    public SMIntProperty awake_ideal_freq;
    public SMIntProperty up_rate;
    public SMIntProperty down_rate;
    public SMIntProperty max_cpu_load;
    public SMIntProperty min_cpu_load;
    public SMIntProperty ramp_up_step;
    public SMIntProperty ramp_down_step;
    public SMIntProperty sleep_wakeup_freq;
    public SMIntProperty sleep_ideal_freq;
    public SMIntProperty sample_rate_jiffies;
    public SMIntProperty smooth_ui;

    public SMSmartassProperty() {
        super("smartassV2");

        String basepath = "/sys/devices/system/cpu/cpufreq/smartass/";

        smart = new SMModuleProperty("cpufreq_smartass2", "/system/lib/modules/cpufreq_smartass2", false, false);

        awake_ideal_freq = new SMIntProperty("s_awake_ideal_freq", basepath.concat("awake_ideal_freq"), false, 100000, 1200000, 800000);
        up_rate = new SMIntProperty("s_up_rate", basepath.concat("up_rate_us"), false, 0, 100000000, 10000);
        down_rate = new SMIntProperty("s_down_rate", basepath.concat("down_rate_us"), false, 0, 100000000, 99000);
        max_cpu_load = new SMIntProperty("s_max_cpu_load", basepath.concat("max_cpu_load"), false, 1, 100, 50);
        min_cpu_load = new SMIntProperty("s_min_cpu_load", basepath.concat("min_cpu_load"), false, 1, 100, 25);
        ramp_up_step = new SMIntProperty("s_ramp_up_step", basepath.concat("ramp_up_step"), false, 1, 1000000, 200000);
        ramp_down_step = new SMIntProperty("s_ramp_down_step", basepath.concat("ramp_down_step"), false, 1, 1000000, 200000);
        sleep_wakeup_freq = new SMIntProperty("s_sleep_wakeup_freq", basepath.concat("sleep_wakeup_freq"), false, 100000, 1200000, 800000);
        sleep_ideal_freq = new SMIntProperty("s_sleep_ideal_freq", basepath.concat("sleep_ideal_freq"), false, 100000, 1200000, 100000);
        sample_rate_jiffies = new SMIntProperty("s_sample_rate_jiffies", basepath.concat("sample_rate_jiffies"), false, 1, 1000, 2);
        smooth_ui = new SMIntProperty("s_smooth_ui", basepath.concat("smooth_ui"), false, 0, 1, 0);
    }

    @Override
    public void readValue() {
        smart.readValue();
        awake_ideal_freq.readValue();
        up_rate.readValue();
        down_rate.readValue();
        max_cpu_load.readValue();
        min_cpu_load.readValue();
        ramp_up_step.readValue();
        ramp_down_step.readValue();
        sleep_wakeup_freq.readValue();
        sleep_ideal_freq.readValue();
        sample_rate_jiffies.readValue();
        smooth_ui.readValue();
    }

    @Override
    public void writeValue() {
        smart.writeValue();
        awake_ideal_freq.writeValue();
        up_rate.writeValue();
        down_rate.writeValue();
        max_cpu_load.writeValue();
        min_cpu_load.writeValue();
        ramp_up_step.writeValue();
        ramp_down_step.writeValue();
        sleep_wakeup_freq.writeValue();
        sleep_ideal_freq.writeValue();
        sample_rate_jiffies.writeValue();
        smooth_ui.writeValue();
    }

    public void writebatch(List<String> cmds) {
        smart.writeBatch(cmds);
        awake_ideal_freq.writeBatch(cmds);
        up_rate.writeBatch(cmds);
        down_rate.writeBatch(cmds);
        max_cpu_load.writeBatch(cmds);
        min_cpu_load.writeBatch(cmds);
        ramp_up_step.writeBatch(cmds);
        ramp_down_step.writeBatch(cmds);
        sleep_wakeup_freq.writeBatch(cmds);
        sleep_ideal_freq.writeBatch(cmds);
        sample_rate_jiffies.writeBatch(cmds);
        smooth_ui.writeBatch(cmds);
    }
}
