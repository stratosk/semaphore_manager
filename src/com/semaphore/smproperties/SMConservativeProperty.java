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

public class SMConservativeProperty extends SMBatchProperty {

    public SMModuleProperty cons;
    public SMIntProperty freq_step;
    public SMIntProperty sampling_down_factor;
    public SMIntProperty sampling_rate;
    public SMIntProperty up_threshold;
    public SMIntProperty down_threshold;
    public SMIntProperty smooth_ui;

    public SMConservativeProperty() {
        super("conservative");

        String basepath = "/sys/devices/system/cpu/cpufreq/conservative/";

        cons = new SMModuleProperty("cpufreq_conservative", "/system/lib/modules/cpufreq_conservative", false, false);

        freq_step = new SMIntProperty("c_freq_step", basepath.concat("freq_step"), false, 1, 100, 5);
        sampling_down_factor = new SMIntProperty("c_sampling_down_factor", basepath.concat("sampling_down_factor"), true, 1, 1000, 1);
        sampling_rate = new SMIntProperty("c_sampling_rate", basepath.concat("sampling_rate"), false, 10000, 300000, 78124);
        up_threshold = new SMIntProperty("c_up_threshold", basepath.concat("up_threshold"), false, 1, 100, 90);
        down_threshold = new SMIntProperty("c_down_threshold", basepath.concat("down_threshold"), false, 1, 100, 40);
        smooth_ui = new SMIntProperty("c_smooth_ui", basepath.concat("smooth_ui"), false, 0, 1, 0);
    }

    public void readValue() {
        cons.readValue();
        freq_step.readValue();
        sampling_down_factor.readValue();
        sampling_rate.readValue();
        up_threshold.readValue();
        down_threshold.readValue();
        smooth_ui.readValue();
    }

    public void writeValue() {
        cons.writeValue();
        freq_step.writeValue();
        sampling_down_factor.writeValue();
        sampling_rate.writeValue();
        up_threshold.writeValue();
        down_threshold.writeValue();
        smooth_ui.writeValue();
    }

    public void writebatch(List<String> cmds) {
        cons.writeBatch(cmds);
        freq_step.writeBatch(cmds);
        sampling_down_factor.writeBatch(cmds);
        sampling_rate.writeBatch(cmds);
        up_threshold.writeBatch(cmds);
        down_threshold.writeBatch(cmds);
        smooth_ui.writeBatch(cmds);
    }
}
