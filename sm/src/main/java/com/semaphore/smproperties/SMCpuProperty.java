/*  Semaphore Manager
 *  
 *   Copyright (c) 2012 - 2015 Stratos Karafotis (stratosk@semaphore.gr)
 *   
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 */
package com.semaphore.smproperties;

import com.semaphore.sm.Commander;

import java.util.List;

public class SMCpuProperty extends SMBatchProperty {

	private String dyn_path;
	private String dyn_prev_status;
	public SMStringProperty gov;
	public SMIntProperty scaling_min_freq;
	public SMIntProperty scaling_max_freq;
	public SMIntProperty util_threshold;
	public String basepath;

	public SMCpuProperty(String cpu) {
		super("cpu".concat(cpu));

		dyn_path = "/sys/module/dyn_hotplug/parameters/enabled";
		basepath = "/sys/devices/system/cpu/".concat(getName()).concat("/cpufreq/");

		gov = new SMStringProperty(getName().concat("_gov"), basepath.concat("scaling_governor"), false, "ondemand");
		scaling_min_freq = new SMIntProperty(getName().concat("_scaling_min_freq"), basepath.concat("scaling_min_freq"), false, 384000, 1512000, 384000);
		scaling_max_freq = new SMIntProperty(getName().concat("_scaling_max_freq"), basepath.concat("scaling_max_freq"), false, 384000, 1512000, 1512000);
		util_threshold = new SMIntProperty(getName().concat("_util_threshold"), basepath.concat("util_threshold"), false, 1, 100, 25);
	}

	@Override
	public void readValue() {
		stopHotplug();
		gov.readValue();
		scaling_min_freq.readValue();
		scaling_max_freq.readValue();
		util_threshold.readValue();
		startHotplug();
	}

	private void stopHotplug() {
		Commander cm = Commander.getInstance();
		int ret = cm.readFile(dyn_path);
		if (ret == 0)
			dyn_prev_status = cm.getOutResult().get(0);
		if (dyn_prev_status != null && dyn_prev_status.equals("Y")) {
			String cmd = "echo N > ".concat(dyn_path);
			cm.run(cmd, true);
		}
	}

	private void startHotplug() {
		if (dyn_prev_status != null && dyn_prev_status.equals("Y")) {
			Commander cm = Commander.getInstance();
			String cmd = "echo Y > ".concat(dyn_path);
			cm.run(cmd, true);
		}
	}

	@Override
	public void writeValue() {
		stopHotplug();
		gov.writeValue();
		scaling_min_freq.writeValue();
		scaling_max_freq.writeValue();
		util_threshold.writeValue();
		startHotplug();
	}

	public void writeBatch(List<String> cmds) {
		stopHotplug();
		gov.writeBatch(cmds);
		scaling_min_freq.writeBatch(cmds);
		scaling_max_freq.writeBatch(cmds);
		util_threshold.writeBatch(cmds);
		startHotplug();
	}
}
