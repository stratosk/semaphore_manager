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

public class SMReadaheadProperty extends SMBaseProperty {
	private final String script = "/data/data/com.semaphore.sm/scripts/read_ahead ";
	private String Value;
	private String defValue;

	public String getDefValue() {
		return defValue;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String Value) {
		this.Value = Value;
	}

	public SMReadaheadProperty(String name, String defValue) {
		super(name);

		this.defValue = defValue;
	}

	@Override
	public void readValue() {
		Commander cm = Commander.getInstance();
		int res = cm.readFile("/sys/devices/virtual/bdi/179:0/read_ahead_kb");
		if (res == 0) {
			String rt = cm.getOutResult().get(0);
			setValue(rt.concat("KB"));
		} else {
			setValue(defValue);
		}
	}

	@Override
	public void writeValue() {
		String p1 = "", p2 = "", p3 = "";

		Commander cm = Commander.getInstance();
		if (!Value.isEmpty()) {
			switch (Value) {
				case "128KB":
					p1 = "128";
					p2 = "128";
					p3 = "128";
					break;
				case "256KB":
					p1 = "256";
					p2 = "256";
					p3 = "64";
					break;
				case "512KB":
					p1 = "256";
					p2 = "512";
					p3 = "64";
					break;
				case "1024KB":
					p1 = "512";
					p2 = "1024";
					p3 = "64";
					break;
				case "3072KB":
					p1 = "512";
					p2 = "3072";
					p3 = "128";
					break;
			}
			cm.run(script + p1 + " " + p2 + " " + p3, true);
		}
	}

	public void writeBatch(List<String> cmds) {
		String p1 = "", p2 = "", p3 = "";

		if (!Value.isEmpty()) {
			switch (Value) {
				case "128KB":
					p1 = "128";
					p2 = "128";
					p3 = "128";
					break;
				case "256KB":
					p1 = "256";
					p2 = "256";
					p3 = "64";
					break;
				case "512KB":
					p1 = "256";
					p2 = "512";
					p3 = "64";
					break;
				case "1024KB":
					p1 = "512";
					p2 = "1024";
					p3 = "64";
					break;
				case "3072KB":
					p1 = "512";
					p2 = "3072";
					p3 = "128";
					break;
			}
			cmds.add(script + p1 + " " + p2 + " " + p3);
		}
	}
}
