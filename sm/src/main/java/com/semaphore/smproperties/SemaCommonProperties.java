/*  Semaphore Manager
 *  
 *   Copyright (c) 2012-2014 Stratos Karafotis (stratosk@semaphore.gr)
 *   
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 */
package com.semaphore.smproperties;

import android.content.Context;

public class SemaCommonProperties {

	public SMConservativeProperty conservative;
	public SMIntProperty touch_enable;
	public SMIntProperty touch;
	public SMSchedulerProperty scheduler;
	public SMIntProperty vibrator;
	public SMLoggerProperty logger;
	public SMInteractiveProperty interactive;

	public SemaCommonProperties() {
		conservative = new SMConservativeProperty();
		interactive = new SMInteractiveProperty();

		touch = new SMIntProperty("touch", "/sys/devices/virtual/misc/touchwake/delay", false, 0, 300000, 30000);
		touch_enable = new SMIntProperty("touch_enable", "/sys/devices/virtual/misc/touchwake/enabled", false, 0, 1, 0);
		scheduler = new SMSchedulerProperty("scheduler", "noop");
		vibrator = new SMIntProperty("vibrator", "/sys/devices/virtual/misc/pwm_duty/pwm_duty", false, 0, 100, 100);
		logger = new SMLoggerProperty("logger", "/system/lib/modules", false);
	}

	public void readValues() {
	}

	public void writeBatch() {
	}

	public void writeValues() {
	}

	public void setPreferences(Context context) {
	}

	public void getPreferences(Context context) {
	}

	public void resetDefaults() {
	}
}
