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

import com.semaphore.sm.Commander;
import java.util.List;

public class SMCpufreqProperty extends SMBatchProperty {

    private String dyn_path;
    private String dyn_prev_status;
    public SMStringProperty gov;
    public SMIntProperty scaling_min_freq;
    public SMIntProperty scaling_max_freq;
    public String basepath;

    public SMCpufreqProperty() {
        super("cpufreq");

        dyn_path = "/sys/module/dyn_hotplug/parameters/enabled";
        basepath = "/sys/devices/system/cpu/cpu0/cpufreq/";

        gov = new SMStringProperty("gov", basepath.concat("scaling_governor"), false, "ondemand");
        scaling_min_freq = new SMIntProperty("scaling_min_freq", basepath.concat("scaling_min_freq"), false, 384000, 1512000, 384000);
        scaling_max_freq = new SMIntProperty("scaling_max_freq", basepath.concat("scaling_max_freq"), false, 384000, 1512000, 1512000);
    }

    @Override
    public void readValue() {
        gov.readValue();
        scaling_min_freq.readValue();
        scaling_max_freq.readValue();
    }

    private void stopHotplug() {
        Commander cm = Commander.getInstance();
        int ret = cm.readFile(dyn_path);
        if (ret == 0) {
            dyn_prev_status = cm.getOutResult().get(0);
        }
        if (dyn_prev_status.equals("Y")) {
            String cmd = "echo N > ".concat(dyn_path);
            int res = cm.run(cmd, true);
        }
    }

    private void startHotplug() {
        if (dyn_prev_status.equals("Y")) {
            Commander cm = Commander.getInstance();
            String cmd = "echo Y > ".concat(dyn_path);
            int res = cm.run(cmd, true);
        }
    }

    @Override
    public void writeValue() {
        stopHotplug();
        for (int i = 0; i <= 3; i++) {
            String path = "/sys/devices/system/cpu/cpu".concat(String.valueOf(i)).concat("/cpufreq/scaling_governor");
            gov.writeValue(path);
            path = "/sys/devices/system/cpu/cpu".concat(String.valueOf(i)).concat("/cpufreq/scaling_min_freq");
            scaling_min_freq.writeValue(path);
            path = "/sys/devices/system/cpu/cpu".concat(String.valueOf(i)).concat("/cpufreq/scaling_max_freq");
            scaling_max_freq.writeValue(path);
        }
        startHotplug();
    }

    public void writeBatch(List<String> cmds) {
        stopHotplug();
        for (int i = 0; i <= 3; i++) {
            String path = "/sys/devices/system/cpu/cpu".concat(String.valueOf(i)).concat("/cpufreq/scaling_governor");
            gov.writeBatch(cmds, path);
            path = "/sys/devices/system/cpu/cpu".concat(String.valueOf(i)).concat("/cpufreq/scaling_min_freq");
            scaling_min_freq.writeBatch(cmds, path);
            path = "/sys/devices/system/cpu/cpu".concat(String.valueOf(i)).concat("/cpufreq/scaling_max_freq");
            scaling_max_freq.writeBatch(cmds, path);
        }
        startHotplug();
    }
}
