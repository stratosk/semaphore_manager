/*  Semaphore Manager
 *  
 *   Copyright (c) 2012 - 2014 Stratos Karafotis (stratosk@semaphore.gr)
 *   
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 */
package com.semaphore.smproperties;

import com.semaphore.sm.Commander;

import java.util.List;

public class SMSchedulerProperty extends SMBaseProperty {

	private final String scriptsPath = "/data/data/com.semaphore.sm/scripts/";
	private String Value;
	private String defValue;
	public String basepath;

	public String getDefValue() {
		return defValue;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String Value) {
		this.Value = Value;
	}

	public SMSchedulerProperty(String name, String defValue) {
		super(name);

		this.defValue = defValue;
	}

	@Override
	public void readValue() {
		Commander cm = Commander.getInstance();
		String schedulerPath = "/sys/block/mmcblk0/queue/scheduler";
		cm.readFile(schedulerPath);
		if (!cm.getOutResult().isEmpty()) {
			String s = cm.getOutResult().get(0);
			int start = s.indexOf('[');
			int end = s.indexOf(']');
			s = s.substring(start + 1, end);
			setValue(s);
		} else
			setValue("");
	}

	@Override
	public void writeValue() {
		Commander cm = Commander.getInstance();
		if (!Value.isEmpty())
			cm.run(scriptsPath + getValue(), true);
	}

	public void writeBatch(List<String> cmds) {
		if (!Value.isEmpty())
			cmds.add(scriptsPath + getValue());
	}
}
