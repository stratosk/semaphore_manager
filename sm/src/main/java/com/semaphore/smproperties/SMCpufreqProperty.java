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

import java.util.List;

public class SMCpufreqProperty extends SMBatchProperty {

	public SMCpuProperty[] cpu;
	public String basepath;

	public SMCpufreqProperty() {
		super("cpufreq");

		basepath = "/sys/devices/system/cpu/cpu0/cpufreq/";

		cpu = new SMCpuProperty[4];
		cpu[0] = new SMCpuProperty("0");
		cpu[1] = new SMCpuProperty("1");
		cpu[2] = new SMCpuProperty("2");
		cpu[3] = new SMCpuProperty("3");
	}

	@Override
	public void readValue() {
		cpu[0].readValue();
		cpu[1].readValue();
		cpu[2].readValue();
		cpu[3].readValue();
	}

	@Override
	public void writeValue() {
		cpu[0].writeValue();
		cpu[1].writeValue();
		cpu[2].writeValue();
		cpu[3].writeValue();
	}

	public void writeBatch(List<String> cmds) {
		cpu[0].writeBatch(cmds);
		cpu[1].writeBatch(cmds);
		cpu[2].writeBatch(cmds);
		cpu[3].writeBatch(cmds);
	}
}
