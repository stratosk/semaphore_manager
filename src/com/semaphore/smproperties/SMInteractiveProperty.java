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

import java.util.List;

public class SMInteractiveProperty extends SMBatchProperty {

    public SMModuleProperty inter;
    public SMIntProperty hispeed_freq;
    public SMIntProperty go_hispeed_load;
    public SMIntProperty min_sampling_time;
    public SMIntProperty above_hispeed_delay;
    public SMIntProperty timer_rate;
    public SMIntProperty timer_slack;
    public SMIntProperty boostpulse_duration;
    public SMStringProperty target_loads;

    public SMInteractiveProperty() {
        super("interactive");

        String basepath = "/sys/devices/system/cpu/cpufreq/interactive/";

        inter = new SMModuleProperty("cpufreq_interactive", "/system/lib/modules/cpufreq_interactive", false, false);

        hispeed_freq = new SMIntProperty("i_hispeed_freq", basepath.concat("hispeed_freq"), false, 100000, 1200000, 800000);
        go_hispeed_load = new SMIntProperty("i_go_hispeed_load", basepath.concat("go_hispeed_load"), false, 1, 100, 99);
        min_sampling_time = new SMIntProperty("i_min_sampling_time", basepath.concat("min_sampling_time"), false, 10000, 100000, 80000);
        above_hispeed_delay = new SMIntProperty("i_above_hispeed_delay", basepath.concat("above_hispeed_delay"), false, 10000, 100000, 20000);
        timer_rate = new SMIntProperty("i_timer_rate", basepath.concat("timer_rate"), false, 10000, 100000, 20000);
        timer_slack = new SMIntProperty("i_timer_slack", basepath.concat("timer_slack"), false, 10000, 200000, 80000);
        boostpulse_duration = new SMIntProperty("i_boostpulse_duration", basepath.concat("timer_slack"), false, 10000, 200000, 80000);
        target_loads = new SMStringProperty("i_target_loads", basepath.concat("target_loads"), false, "90");
    }

    public void readValue() {
        hispeed_freq.readValue();
        go_hispeed_load.readValue();
        min_sampling_time.readValue();
        above_hispeed_delay.readValue();
        timer_rate.readValue();
        timer_slack.readValue();
        boostpulse_duration.readValue();
        target_loads.readValue();
    }

    public void writeValue() {
        hispeed_freq.writeValue();
        go_hispeed_load.writeValue();
        min_sampling_time.writeValue();
        above_hispeed_delay.writeValue();
        timer_rate.writeValue();
        timer_slack.writeValue();
        boostpulse_duration.writeValue();
        target_loads.writeValue();
    }

    public void writebatch(List<String> cmds) {
        hispeed_freq.writeBatch(cmds);
        go_hispeed_load.writeBatch(cmds);
        min_sampling_time.writeBatch(cmds);
        above_hispeed_delay.writeBatch(cmds);
        timer_rate.writeBatch(cmds);
        timer_slack.writeBatch(cmds);
        boostpulse_duration.writeBatch(cmds);
        target_loads.writeBatch(cmds);
    }
}
