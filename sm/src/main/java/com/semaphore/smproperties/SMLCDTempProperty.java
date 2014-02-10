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

public class SMLCDTempProperty extends SMBatchProperty {

	public SMIntProperty lcd_red;
	public SMIntProperty lcd_green;
	public SMIntProperty lcd_blue;
	public String basepath;

	public SMLCDTempProperty() {
		super("lcd_temp");

		basepath = "/sys/devices/platform/kcal_ctrl.0/kcal";

		lcd_red = new SMIntProperty("lcd_red", basepath, true, 0, 255, 255);
		lcd_green = new SMIntProperty("lcd_green", basepath, true, 0, 255, 255);
		lcd_blue = new SMIntProperty("lcd_blue", basepath, true, 0, 255, 255);
	}

	private void setValues(String jitter) {
		int i = 0;
		String[] tokens = jitter.split("\\s+");

		for (String s : tokens) {
			switch (i) {
				case 0:
					lcd_red.setValue(s);
					break;
				case 1:
					lcd_green.setValue(s);
					break;
				case 2:
					lcd_blue.setValue(s);
					break;
			}
			i++;
		}
	}

	private String getValues() {
		String s;

		s = lcd_red.getValString() + " "
				+ lcd_green.getValString() + " "
				+ lcd_blue.getValString();
		return s;
	}

	public void setDefValues() {
		lcd_red.setValue(lcd_red.getDefault());
		lcd_green.setValue(lcd_green.getDefault());
		lcd_blue.setValue(lcd_blue.getDefault());
	}

	@Override
	public void readValue() {
		Commander cm = Commander.getInstance();

		int res = cm.readFile(basepath);
		if (res == 0) {
			String rt = cm.getOutResult().get(0);
			setValues(rt);
		} else
			setDefValues();
	}

	@Override
	public void writeValue() {
		Commander cm = Commander.getInstance();
		String cmd = "echo \"".concat(getValues()).concat("\" > ").concat(basepath);

		cm.run(cmd, cm.needSU(basepath));
		cmd = "echo 1 > \"/sys/devices/platform/kcal_ctrl.0/kcal_ctrl\"";
		cm.run(cmd, cm.needSU("/sys/devices/platform/kcal_ctrl.0/kcal_ctrl"));
	}

	public void writeBatch(List<String> cmds) {
		String cmd = "echo \"".concat(getValues()).concat("\" > ").concat(basepath);
		cmds.add(cmd);
		cmds.add("echo 1 > \"/sys/devices/platform/kcal_ctrl.0/kcal_ctrl\"");
	}
}
