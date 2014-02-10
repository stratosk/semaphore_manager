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

public class SMModuleProperty extends SMProperty {

	private boolean Value;
	private boolean defValue;

	public boolean getDefValue() {
		return defValue;
	}

	public boolean getValue() {
		return Value;
	}

	public void setValue(boolean Value) {
		this.Value = Value;
	}

	public void readValue() {
		if (isDynamic())
			return;
		Commander cm = Commander.getInstance();
		cm.run("lsmod | grep ".concat(getName()), false);
		if (cm.getOutResult().isEmpty())
			setValue(false);
		else
			setValue(true);
	}

	public void writeValue() {
		Commander cm = Commander.getInstance();
		if (Value)
			cm.run("insmod ".concat(getPath()).concat(".ko"), true);
		else if (!getName().equals("cpufreq_smartass2"))
			cm.run("rmmod ".concat(getName()), true);
	}

	public void writeBatch(List<String> cmds) {
		if (Value)
			cmds.add("insmod ".concat(getPath()).concat(".ko"));
		else if (!getName().equals("cpufreq_smartass2"))
			cmds.add("rmmod ".concat(getName()));
	}

	public SMModuleProperty(String name, String path, boolean defValue) {
		super(name, path, false);

		this.defValue = defValue;
	}
}
