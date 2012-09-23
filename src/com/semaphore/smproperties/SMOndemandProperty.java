/*  Semaphore Manager
 *  
 *   Copyright (c) 2012 Stratos Karafotis (stratosk@semaphore.gr)
 *   
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 */
package com.semaphore.smproperties;

import java.util.List;

public class SMOndemandProperty extends SMBatchProperty {

    public SMIntProperty io_is_busy;
    public SMIntProperty sampling_down_factor;
    public SMIntProperty sampling_down_max_momentum;
    public SMIntProperty sampling_rate;
    public SMIntProperty up_threshold;
    public SMIntProperty smooth_ui;

    public SMOndemandProperty() {
        super("ondemand");

        String basepath = "/sys/devices/system/cpu/cpufreq/ondemand/";

        io_is_busy = new SMIntProperty("o_io_is_busy", basepath.concat("io_is_busy"), false, 0, 1, 0);
        sampling_down_factor = new SMIntProperty("o_sampling_down_factor", basepath.concat("sampling_down_factor"), true, 1, 1000, 4);
        sampling_down_max_momentum = new SMIntProperty("o_sampling_down_max_momentum", basepath.concat("sampling_down_max_momentum"), true, 1, 1000, 16);
        sampling_rate = new SMIntProperty("o_sampling_rate", basepath.concat("sampling_rate"), false, 10000, 40000, 20000);
        up_threshold = new SMIntProperty("o_up_threshold", basepath.concat("up_threshold"), false, 1, 100, 85);
        smooth_ui = new SMIntProperty("o_smooth_ui", basepath.concat("smooth_ui"), false, 0, 1, 0);
    }

    @Override
    public void readValue() {
        io_is_busy.readValue();
        sampling_down_factor.readValue();
        sampling_down_max_momentum.readValue();
        sampling_rate.readValue();
        up_threshold.readValue();
        smooth_ui.readValue();
    }

    @Override
    public void writeValue() {
        io_is_busy.writeValue();
        sampling_down_factor.writeValue();
        sampling_down_max_momentum.writeValue();
        sampling_rate.writeValue();
        up_threshold.writeValue();
        smooth_ui.writeValue();
    }

    public void writeBatch(List<String> cmds) {
        io_is_busy.writeBatch(cmds);
        sampling_down_factor.writeBatch(cmds);
        sampling_down_max_momentum.writeBatch(cmds);
        sampling_rate.writeBatch(cmds);
        up_threshold.writeBatch(cmds);
        smooth_ui.writeBatch(cmds);
    }
}
