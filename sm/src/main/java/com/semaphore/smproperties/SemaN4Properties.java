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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.semaphore.sm.Commander;

import java.util.ArrayList;
import java.util.List;

public class SemaN4Properties extends SemaCommonProperties {

	public SMCpufreqProperty cpufreq;
	public SMOndemandN4Property ondemand;
	public SMUVProperty uv;
	public SMIntProperty dt_wake_enabled;
	public SMStringProperty tcp_congestion;
	public SMTouchAccuracyProperty taccuracy;
	public SMTouchJitterProperty tjitter;
	public SMLEDTrigProperty led_red;
	public SMLEDTrigProperty led_green;
	public SMLEDTrigProperty led_blue;
	public SMLCDTempProperty lcdtemp;
	public SMStringProperty gamma_r;
	public SMStringProperty gamma_g;
	public SMStringProperty gamma_b;
	public SMStringProperty read_ahead;
	public SMIntProperty hp_enabled;
	public SMIntProperty hp_up_threshold;
	public SMIntProperty hp_min_online;
	public SMIntProperty hp_max_online;
	public SMIntProperty hp_up_timer_cnt;
	public SMIntProperty hp_down_timer_cnt;
	public SMIntProperty min_br;
	public SMIntProperty max_br;
	public SMIntProperty br_mode;

	public SemaN4Properties() {
		super();

		cpufreq = new SMCpufreqProperty();
		ondemand = new SMOndemandN4Property();

		conservative.down_threshold.setDefault(20);
		conservative.up_threshold.setDefault(80);
		conservative.sampling_rate.setDefault(20000);

		interactive.hispeed_freq.setMaxValue(1512000);
		interactive.hispeed_freq.setDefault(1512000);

		uv = new SMUVProperty();

		hp_enabled = new SMIntProperty("hp_enabled", "/sys/module/dyn_hotplug/parameters/enabled", false, 0, 1, 1);
		hp_up_threshold = new SMIntProperty("hp_up_threshold", "/sys/module/dyn_hotplug/parameters/up_threshold", false, 1, 100, 25);
		hp_min_online = new SMIntProperty("hp_min_online", "/sys/module/dyn_hotplug/parameters/min_online", false, 1, 4, 2);
		hp_max_online = new SMIntProperty("hp_max_online", "/sys/module/dyn_hotplug/parameters/max_online", false, 1, 4, 4);
		hp_up_timer_cnt = new SMIntProperty("hp_up_timer_cnt", "/sys/module/dyn_hotplug/parameters/up_timer_cnt", false, 1, 20, 2);
		hp_down_timer_cnt = new SMIntProperty("hp_down_timer_cnt", "/sys/module/dyn_hotplug/parameters/down_timer_cnt", false, 1, 30, 10);

		scheduler.basepath = "/sys/block/mmcblk0/queue/scheduler";

		vibrator.setPath("/sys/class/timed_output/vibrator/amp");
		vibrator.setDefault(70);
		dt_wake_enabled = new SMIntProperty("dt_wake_enabled", "/sys/devices/virtual/input/lge_touch/dt_wake_enabled", false, 0, 1, 0);

		tcp_congestion = new SMStringProperty("tcp_congestion", "/proc/sys/net/ipv4/tcp_congestion_control", false, "cubic");

		taccuracy = new SMTouchAccuracyProperty();
		tjitter = new SMTouchJitterProperty();

		led_red = new SMLEDTrigProperty("led_red", "/sys/class/leds/red/trigger", false, "thermal");
		led_green = new SMLEDTrigProperty("led_green", "/sys/class/leds/green/trigger", false, "battery-full");
		led_blue = new SMLEDTrigProperty("led_blue", "/sys/class/leds/blue/trigger", false, "touchwake");

		lcdtemp = new SMLCDTempProperty();
		gamma_r = new SMStringProperty("gamma_r", "/sys/devices/virtual/misc/gamma_control/gamma_r", false, "64 68 118 1 0 0 48 32 1");
		gamma_g = new SMStringProperty("gamma_g", "/sys/devices/virtual/misc/gamma_control/gamma_g", false, "64 68 118 1 0 0 48 32 1");
		gamma_b = new SMStringProperty("gamma_b", "/sys/devices/virtual/misc/gamma_control/gamma_b", false, "32 35 116 0 31 16 80 51 3");
		read_ahead = new SMStringProperty("read_ahead", "sys/devices/platform/msm_sdcc.1/mmc_host/mmc0/mmc0:0001/block/mmcblk0/queue/read_ahead_kb", false, "128");

		min_br = new SMIntProperty("min_br", "/sys/devices/i2c-0/0-0038/lm3530_min_br", false, 2, 114, 2);
		max_br = new SMIntProperty("max_br", "/sys/devices/i2c-0/0-0038/lm3530_max_br", false, 2, 114, 114);
		br_mode = new SMIntProperty("br_mode", "/sys/devices/i2c-0/0-0038/lm3530_br_mode", false, 0, 1, 1);

		logger.setModPath("/lib/modules");
	}

	@Override
	public void readValues() {
		cpufreq.readValue();
		ondemand.readValue();
		conservative.readValue();
		interactive.readValue();

		uv.readValue();

		hp_enabled.readValue();
		hp_up_threshold.readValue();
		hp_min_online.readValue();
		hp_max_online.readValue();
		hp_up_timer_cnt.readValue();
		hp_down_timer_cnt.readValue();

		scheduler.readValue();
		vibrator.readValue();
		touch_enable.readValue();
		touch.readValue();
		dt_wake_enabled.readValue();

		tcp_congestion.readValue();
		taccuracy.readValue();
		tjitter.readValue();

		led_red.readValue();
		led_green.readValue();
		led_blue.readValue();

		lcdtemp.readValue();
		gamma_r.readValue();
		gamma_g.readValue();
		gamma_b.readValue();

		read_ahead.readValue();

		min_br.readValue();
		max_br.readValue();
		br_mode.readValue();

		logger.readValue();
	}

	@Override
	public void writeBatch() {
		List<String> cmds = new ArrayList<>();

		cpufreq.writeBatch(cmds);
		ondemand.writeBatch(cmds);
		conservative.writebatch(cmds);
		interactive.writebatch(cmds);

		uv.writeBatch(cmds);

		hp_enabled.writeBatch(cmds);
		hp_min_online.writeBatch(cmds);
		hp_max_online.writeBatch(cmds);
		hp_up_threshold.writeBatch(cmds);
		hp_up_timer_cnt.writeBatch(cmds);
		hp_down_timer_cnt.writeBatch(cmds);

		scheduler.writeBatch(cmds);
		vibrator.writeBatch(cmds);
		touch_enable.writeBatch(cmds);
		touch.writeBatch(cmds);
		dt_wake_enabled.writeBatch(cmds);

		tcp_congestion.writeBatch(cmds);
		taccuracy.writeBatch(cmds);
		tjitter.writeBatch(cmds);

		led_red.writeBatch(cmds);
		led_green.writeBatch(cmds);
		led_blue.writeBatch(cmds);

		lcdtemp.writeBatch(cmds);
		gamma_r.writeBatch(cmds);
		gamma_g.writeBatch(cmds);
		gamma_b.writeBatch(cmds);

		read_ahead.writeBatch(cmds);

		min_br.writeBatch(cmds);
		max_br.writeBatch(cmds);
		br_mode.writeBatch(cmds);

		logger.writeBatch(cmds);
		Commander.getInstance().runSuBatch(cmds);
	}

	@Override
	public void writeValues() {
		cpufreq.writeValue();
		ondemand.writeValue();
		conservative.writeValue();
		interactive.writeValue();

		uv.writeValue();

		hp_enabled.writeValue();
		hp_up_threshold.writeValue();
		hp_min_online.writeValue();
		hp_max_online.writeValue();
		hp_up_timer_cnt.writeValue();
		hp_down_timer_cnt.writeValue();

		scheduler.writeValue();
		vibrator.writeValue();
		touch_enable.writeValue();
		touch.writeValue();
		dt_wake_enabled.writeValue();

		tcp_congestion.writeValue();
		taccuracy.writeValue();
		tjitter.writeValue();

		led_red.writeValue();
		led_green.writeValue();
		led_blue.writeValue();

		lcdtemp.writeValue();
		gamma_r.writeValue();
		gamma_g.writeValue();
		gamma_b.writeValue();

		read_ahead.writeValue();

		min_br.writeValue();
		max_br.writeValue();
		br_mode.writeValue();

		logger.writeValue();
	}

	@Override
	public void setPreferences(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = prefs.edit();

		edit.putString(cpufreq.cpu[0].gov.getName(), cpufreq.cpu[0].gov.getValue());
		edit.putString(cpufreq.cpu[0].scaling_min_freq.getName(), cpufreq.cpu[0].scaling_min_freq.getValString());
		edit.putString(cpufreq.cpu[0].scaling_max_freq.getName(), cpufreq.cpu[0].scaling_max_freq.getValString());
		edit.putString(cpufreq.cpu[0].util_threshold.getName(), cpufreq.cpu[0].util_threshold.getValString());
		edit.putString(cpufreq.cpu[1].gov.getName(), cpufreq.cpu[1].gov.getValue());
		edit.putString(cpufreq.cpu[1].scaling_min_freq.getName(), cpufreq.cpu[1].scaling_min_freq.getValString());
		edit.putString(cpufreq.cpu[1].scaling_max_freq.getName(), cpufreq.cpu[1].scaling_max_freq.getValString());
		edit.putString(cpufreq.cpu[1].util_threshold.getName(), cpufreq.cpu[1].util_threshold.getValString());
		edit.putString(cpufreq.cpu[2].gov.getName(), cpufreq.cpu[2].gov.getValue());
		edit.putString(cpufreq.cpu[2].scaling_min_freq.getName(), cpufreq.cpu[2].scaling_min_freq.getValString());
		edit.putString(cpufreq.cpu[2].scaling_max_freq.getName(), cpufreq.cpu[2].scaling_max_freq.getValString());
		edit.putString(cpufreq.cpu[2].util_threshold.getName(), cpufreq.cpu[2].util_threshold.getValString());
		edit.putString(cpufreq.cpu[3].gov.getName(), cpufreq.cpu[3].gov.getValue());
		edit.putString(cpufreq.cpu[3].scaling_min_freq.getName(), cpufreq.cpu[3].scaling_min_freq.getValString());
		edit.putString(cpufreq.cpu[3].scaling_max_freq.getName(), cpufreq.cpu[3].scaling_max_freq.getValString());
		edit.putString(cpufreq.cpu[3].util_threshold.getName(), cpufreq.cpu[3].util_threshold.getValString());
		// ondemand tunables
		edit.putBoolean(ondemand.io_is_busy.getName(), ondemand.io_is_busy.getBoolean());
		edit.putString(ondemand.sampling_down_factor.getName(), ondemand.sampling_down_factor.getValString());
		edit.putString(ondemand.sampling_rate.getName(), ondemand.sampling_rate.getValString());
		edit.putString(ondemand.up_threshold.getName(), ondemand.up_threshold.getValString());
		edit.putString(ondemand.down_threshold.getName(), ondemand.down_threshold.getValString());
		edit.putString(ondemand.powersave_bias.getName(), ondemand.powersave_bias.getValString());
		edit.putString(ondemand.touch_load.getName(), ondemand.touch_load.getValString());
		edit.putString(ondemand.touch_load_threshold.getName(), ondemand.touch_load_threshold.getValString());
		edit.putString(ondemand.touch_load_duration.getName(), ondemand.touch_load_duration.getValString());
		// conservative
		edit.putBoolean(conservative.cons.getName(), conservative.cons.getValue());
		edit.putString(conservative.freq_step.getName(), conservative.freq_step.getValString());
		edit.putString(conservative.sampling_down_factor.getName(), conservative.sampling_down_factor.getValString());
		edit.putString(conservative.sampling_rate.getName(), conservative.sampling_rate.getValString());
		edit.putString(conservative.up_threshold.getName(), conservative.up_threshold.getValString());
		edit.putString(conservative.down_threshold.getName(), conservative.down_threshold.getValString());
		edit.putBoolean(conservative.smooth_ui.getName(), conservative.smooth_ui.getBoolean());
		// interactive
		edit.putBoolean(interactive.inter.getName(), interactive.inter.getValue());
		edit.putString(interactive.hispeed_freq.getName(), interactive.hispeed_freq.getValString());
		edit.putString(interactive.go_hispeed_load.getName(), interactive.go_hispeed_load.getValString());
		edit.putString(interactive.min_sampling_time.getName(), interactive.min_sampling_time.getValString());
		edit.putString(interactive.above_hispeed_delay.getName(), interactive.above_hispeed_delay.getValString());
		edit.putString(interactive.timer_rate.getName(), interactive.timer_rate.getValString());
		edit.putString(interactive.timer_slack.getName(), interactive.timer_slack.getValString());
		edit.putString(interactive.boostpulse_duration.getName(), interactive.boostpulse_duration.getValString());
		edit.putString(interactive.target_loads.getName(), interactive.target_loads.getValue());
		edit.putBoolean(interactive.io_is_busy.getName(), interactive.io_is_busy.getBoolean());
		edit.putBoolean(interactive.input_boost.getName(), interactive.input_boost.getBoolean());

		edit.putString(uv.uv_higher_khz_thres.getName(), uv.uv_higher_khz_thres.getValString());
		edit.putBoolean(uv.uv_boost.getName(), uv.uv_boost.getBoolean());
		edit.putInt(uv.uv_lower_uv.getName(), uv.uv_lower_uv.getValue());
		edit.putInt(uv.uv_higher_uv.getName(), uv.uv_higher_uv.getValue());

		edit.putBoolean(hp_enabled.getName(), hp_enabled.getBoolean());
		edit.putInt(hp_min_online.getName(), hp_min_online.getValue());
		edit.putInt(hp_max_online.getName(), hp_max_online.getValue());
		edit.putString(hp_up_threshold.getName(), hp_up_threshold.getValString());
		edit.putInt(hp_up_timer_cnt.getName(), hp_up_timer_cnt.getValue());
		edit.putInt(hp_down_timer_cnt.getName(), hp_down_timer_cnt.getValue());

		edit.putString(scheduler.getName(), scheduler.getValue());
		edit.putInt(vibrator.getName(), vibrator.getValue());
		edit.putBoolean(touch_enable.getName(), touch_enable.getBoolean());
		edit.putInt(touch.getName(), touch.getValue());
		edit.putBoolean(dt_wake_enabled.getName(), dt_wake_enabled.getBoolean());

		edit.putString(tcp_congestion.getName(), tcp_congestion.getValue());
		edit.putBoolean(taccuracy.accuracy_filter_enable.getName(), taccuracy.accuracy_filter_enable.getBoolean());
		edit.putString(taccuracy.ignore_pressure_gap.getName(), taccuracy.ignore_pressure_gap.getValString());
		edit.putString(taccuracy.delta_max.getName(), taccuracy.delta_max.getValString());
		edit.putString(taccuracy.touch_max_count.getName(), taccuracy.touch_max_count.getValString());
		edit.putString(taccuracy.max_pressure.getName(), taccuracy.max_pressure.getValString());
		edit.putString(taccuracy.direction_count.getName(), taccuracy.direction_count.getValString());
		edit.putString(taccuracy.time_to_max_pressure.getName(), taccuracy.time_to_max_pressure.getValString());

		edit.putBoolean(tjitter.jitter_enable.getName(), tjitter.jitter_enable.getBoolean());
		edit.putString(tjitter.adjust_margin.getName(), tjitter.adjust_margin.getValString());

		edit.putString(led_red.getName(), led_red.getValue());
		edit.putString(led_green.getName(), led_green.getValue());
		edit.putString(led_blue.getName(), led_blue.getValue());

		edit.putInt(lcdtemp.lcd_red.getName(), lcdtemp.lcd_red.getValue());
		edit.putInt(lcdtemp.lcd_green.getName(), lcdtemp.lcd_green.getValue());
		edit.putInt(lcdtemp.lcd_blue.getName(), lcdtemp.lcd_blue.getValue());
		edit.putString(gamma_r.getName(), gamma_r.getValue());
		edit.putString(gamma_g.getName(), gamma_g.getValue());
		edit.putString(gamma_b.getName(), gamma_b.getValue());

		edit.putString(read_ahead.getName(), read_ahead.getValue());

		edit.putInt(min_br.getName(), min_br.getValue());
		edit.putInt(max_br.getName(), max_br.getValue());
		edit.putBoolean(br_mode.getName(), br_mode.getBoolean());

		edit.putBoolean(logger.getName(), logger.getValue());

		edit.commit();
	}

	@Override
	public void getPreferences(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		cpufreq.cpu[0].gov.setValue(prefs.getString(cpufreq.cpu[0].gov.getName(), cpufreq.cpu[0].gov.getDefValue()));
		cpufreq.cpu[0].scaling_min_freq.setValue(prefs.getString(cpufreq.cpu[0].scaling_min_freq.getName(), cpufreq.cpu[0].scaling_min_freq.getDefString()));
		cpufreq.cpu[0].scaling_max_freq.setValue(prefs.getString(cpufreq.cpu[0].scaling_max_freq.getName(), cpufreq.cpu[0].scaling_max_freq.getDefString()));
		cpufreq.cpu[0].util_threshold.setValue(prefs.getString(cpufreq.cpu[0].util_threshold.getName(), cpufreq.cpu[0].util_threshold.getDefString()));

		cpufreq.cpu[1].gov.setValue(prefs.getString(cpufreq.cpu[1].gov.getName(), cpufreq.cpu[1].gov.getDefValue()));
		cpufreq.cpu[1].scaling_min_freq.setValue(prefs.getString(cpufreq.cpu[1].scaling_min_freq.getName(), cpufreq.cpu[1].scaling_min_freq.getDefString()));
		cpufreq.cpu[1].scaling_max_freq.setValue(prefs.getString(cpufreq.cpu[1].scaling_max_freq.getName(), cpufreq.cpu[1].scaling_max_freq.getDefString()));
		cpufreq.cpu[1].util_threshold.setValue(prefs.getString(cpufreq.cpu[1].util_threshold.getName(), cpufreq.cpu[1].util_threshold.getDefString()));

		cpufreq.cpu[2].gov.setValue(prefs.getString(cpufreq.cpu[2].gov.getName(), cpufreq.cpu[2].gov.getDefValue()));
		cpufreq.cpu[2].scaling_min_freq.setValue(prefs.getString(cpufreq.cpu[2].scaling_min_freq.getName(), cpufreq.cpu[2].scaling_min_freq.getDefString()));
		cpufreq.cpu[2].scaling_max_freq.setValue(prefs.getString(cpufreq.cpu[2].scaling_max_freq.getName(), cpufreq.cpu[2].scaling_max_freq.getDefString()));
		cpufreq.cpu[2].util_threshold.setValue(prefs.getString(cpufreq.cpu[2].util_threshold.getName(), cpufreq.cpu[2].util_threshold.getDefString()));

		cpufreq.cpu[3].gov.setValue(prefs.getString(cpufreq.cpu[3].gov.getName(), cpufreq.cpu[3].gov.getDefValue()));
		cpufreq.cpu[3].scaling_min_freq.setValue(prefs.getString(cpufreq.cpu[3].scaling_min_freq.getName(), cpufreq.cpu[3].scaling_min_freq.getDefString()));
		cpufreq.cpu[3].scaling_max_freq.setValue(prefs.getString(cpufreq.cpu[3].scaling_max_freq.getName(), cpufreq.cpu[3].scaling_max_freq.getDefString()));
		cpufreq.cpu[3].util_threshold.setValue(prefs.getString(cpufreq.cpu[3].util_threshold.getName(), cpufreq.cpu[3].util_threshold.getDefString()));

		ondemand.io_is_busy.setValue(prefs.getBoolean(ondemand.io_is_busy.getName(), ondemand.io_is_busy.getDefBoolean()) ? 1 : 0);
		ondemand.sampling_down_factor.setValue(prefs.getString(ondemand.sampling_down_factor.getName(), ondemand.sampling_down_factor.getDefString()));
		ondemand.sampling_rate.setValue(prefs.getString(ondemand.sampling_rate.getName(), ondemand.sampling_rate.getDefString()));
		ondemand.up_threshold.setValue(prefs.getString(ondemand.up_threshold.getName(), ondemand.up_threshold.getDefString()));
		ondemand.down_threshold.setValue(prefs.getString(ondemand.down_threshold.getName(), ondemand.down_threshold.getDefString()));
		ondemand.powersave_bias.setValue(prefs.getString(ondemand.powersave_bias.getName(), ondemand.powersave_bias.getDefString()));
		ondemand.touch_load.setValue(prefs.getString(ondemand.touch_load.getName(), ondemand.touch_load.getDefString()));
		ondemand.touch_load_threshold.setValue(prefs.getString(ondemand.touch_load_threshold.getName(), ondemand.touch_load_threshold.getDefString()));
		ondemand.touch_load_duration.setValue(prefs.getString(ondemand.touch_load_duration.getName(), ondemand.touch_load_duration.getDefString()));

		conservative.cons.setValue(prefs.getBoolean(conservative.cons.getName(), conservative.cons.getDefValue()));
		conservative.freq_step.setValue(prefs.getString(conservative.freq_step.getName(), conservative.freq_step.getDefString()));
		conservative.sampling_down_factor.setValue(prefs.getString(conservative.sampling_down_factor.getName(), conservative.sampling_down_factor.getDefString()));
		conservative.sampling_rate.setValue(prefs.getString(conservative.sampling_rate.getName(), conservative.sampling_rate.getDefString()));
		conservative.up_threshold.setValue(prefs.getString(conservative.up_threshold.getName(), conservative.up_threshold.getDefString()));
		conservative.down_threshold.setValue(prefs.getString(conservative.down_threshold.getName(), conservative.down_threshold.getDefString()));
		conservative.smooth_ui.setValue(prefs.getBoolean(conservative.smooth_ui.getName(), conservative.smooth_ui.getDefBoolean()) ? 1 : 0);

		interactive.inter.setValue(prefs.getBoolean(interactive.inter.getName(), interactive.inter.getDefValue()));
		interactive.hispeed_freq.setValue(prefs.getString(interactive.hispeed_freq.getName(), interactive.hispeed_freq.getDefString()));
		interactive.go_hispeed_load.setValue(prefs.getString(interactive.go_hispeed_load.getName(), interactive.go_hispeed_load.getDefString()));
		interactive.min_sampling_time.setValue(prefs.getString(interactive.min_sampling_time.getName(), interactive.min_sampling_time.getDefString()));
		interactive.above_hispeed_delay.setValue(prefs.getString(interactive.above_hispeed_delay.getName(), interactive.above_hispeed_delay.getDefString()));
		interactive.timer_rate.setValue(prefs.getString(interactive.timer_rate.getName(), interactive.timer_rate.getDefString()));
		interactive.timer_slack.setValue(prefs.getString(interactive.timer_slack.getName(), interactive.timer_slack.getDefString()));
		interactive.boostpulse_duration.setValue(prefs.getString(interactive.boostpulse_duration.getName(), interactive.boostpulse_duration.getDefString()));
		interactive.target_loads.setValue(prefs.getString(interactive.target_loads.getName(), interactive.target_loads.getDefValue()));
		interactive.io_is_busy.setValue(prefs.getBoolean(interactive.io_is_busy.getName(), interactive.io_is_busy.getDefBoolean()) ? 1 : 0);
		interactive.input_boost.setValue(prefs.getBoolean(interactive.input_boost.getName(), interactive.input_boost.getDefBoolean()) ? 1 : 0);

		uv.uv_higher_khz_thres.setValue(prefs.getString(uv.uv_higher_khz_thres.getName(), uv.uv_higher_khz_thres.getDefString()));
		uv.uv_boost.setValue(prefs.getBoolean(uv.uv_boost.getName(), uv.uv_boost.getDefBoolean()) ? 1 : 0);
		uv.uv_lower_uv.setValue(prefs.getInt(uv.uv_lower_uv.getName(), uv.uv_lower_uv.getDefault()));
		uv.uv_higher_uv.setValue(prefs.getInt(uv.uv_higher_uv.getName(), uv.uv_higher_uv.getDefault()));
		uv.enabled = prefs.getBoolean("uv_enabled", false);
		uv.apply_boot = prefs.getBoolean("uv_apply_boot", false);

		hp_enabled.setValue(prefs.getBoolean(hp_enabled.getName(), hp_enabled.getDefBoolean()) ? 1 : 0);
		hp_min_online.setValue(prefs.getInt(hp_min_online.getName(), hp_min_online.getDefault()));
		hp_max_online.setValue(prefs.getInt(hp_max_online.getName(), hp_max_online.getDefault()));
		hp_up_threshold.setValue(prefs.getString(hp_up_threshold.getName(), hp_up_threshold.getDefString()));
		hp_up_timer_cnt.setValue(prefs.getInt(hp_up_timer_cnt.getName(), hp_up_timer_cnt.getDefault()));
		hp_down_timer_cnt.setValue(prefs.getInt(hp_down_timer_cnt.getName(), hp_down_timer_cnt.getDefault()));

		scheduler.setValue(prefs.getString(scheduler.getName(), scheduler.getDefValue()));
		vibrator.setValue(prefs.getInt(vibrator.getName(), vibrator.getDefault()));
		touch_enable.setValue(prefs.getBoolean(touch_enable.getName(), touch_enable.getDefBoolean()) ? 1 : 0);
		touch.setValue(prefs.getInt(touch.getName(), touch.getDefault()));
		dt_wake_enabled.setValue(prefs.getBoolean(dt_wake_enabled.getName(), dt_wake_enabled.getDefBoolean()) ? 1 : 0);

		tcp_congestion.setValue(prefs.getString(tcp_congestion.getName(), tcp_congestion.getDefValue()));
		taccuracy.accuracy_filter_enable.setValue(prefs.getBoolean(taccuracy.accuracy_filter_enable.getName(), taccuracy.accuracy_filter_enable.getDefBoolean()) ? 1 : 0);
		taccuracy.ignore_pressure_gap.setValue(prefs.getString(taccuracy.ignore_pressure_gap.getName(), taccuracy.ignore_pressure_gap.getDefString()));
		taccuracy.delta_max.setValue(prefs.getString(taccuracy.delta_max.getName(), taccuracy.delta_max.getDefString()));
		taccuracy.touch_max_count.setValue(prefs.getString(taccuracy.touch_max_count.getName(), taccuracy.touch_max_count.getDefString()));
		taccuracy.max_pressure.setValue(prefs.getString(taccuracy.max_pressure.getName(), taccuracy.max_pressure.getDefString()));
		taccuracy.direction_count.setValue(prefs.getString(taccuracy.direction_count.getName(), taccuracy.direction_count.getDefString()));
		taccuracy.time_to_max_pressure.setValue(prefs.getString(taccuracy.time_to_max_pressure.getName(), taccuracy.time_to_max_pressure.getDefString()));

		tjitter.jitter_enable.setValue(prefs.getBoolean(tjitter.jitter_enable.getName(), tjitter.jitter_enable.getDefBoolean()) ? 1 : 0);
		tjitter.adjust_margin.setValue(prefs.getString(tjitter.adjust_margin.getName(), tjitter.adjust_margin.getDefString()));

		led_red.setValue(prefs.getString(led_red.getName(), led_red.getDefValue()));
		led_green.setValue(prefs.getString(led_green.getName(), led_green.getDefValue()));
		led_blue.setValue(prefs.getString(led_blue.getName(), led_blue.getDefValue()));

		lcdtemp.lcd_red.setValue(prefs.getInt(lcdtemp.lcd_red.getName(), lcdtemp.lcd_red.getDefault()));
		lcdtemp.lcd_green.setValue(prefs.getInt(lcdtemp.lcd_green.getName(), lcdtemp.lcd_green.getDefault()));
		lcdtemp.lcd_blue.setValue(prefs.getInt(lcdtemp.lcd_blue.getName(), lcdtemp.lcd_blue.getDefault()));
		gamma_r.setValue(prefs.getString(gamma_r.getName(), gamma_r.getDefValue()));
		gamma_g.setValue(prefs.getString(gamma_g.getName(), gamma_g.getDefValue()));
		gamma_b.setValue(prefs.getString(gamma_b.getName(), gamma_b.getDefValue()));

		read_ahead.setValue(prefs.getString(read_ahead.getName(), read_ahead.getDefValue()));

		min_br.setValue(prefs.getInt(min_br.getName(), min_br.getDefault()));
		max_br.setValue(prefs.getInt(max_br.getName(), max_br.getDefault()));
		br_mode.setValue(prefs.getBoolean(br_mode.getName(), br_mode.getDefBoolean()) ? 1 : 0);

		logger.setValue(prefs.getBoolean(logger.getName(), logger.getDefValue()));
	}

	@Override
	public void resetDefaults() {
		cpufreq.cpu[0].gov.setValue(cpufreq.cpu[0].gov.getDefValue());
		cpufreq.cpu[0].scaling_min_freq.setValue(cpufreq.cpu[0].scaling_min_freq.getDefault());
		cpufreq.cpu[0].scaling_max_freq.setValue(cpufreq.cpu[0].scaling_max_freq.getDefault());
		cpufreq.cpu[0].util_threshold.setValue(cpufreq.cpu[0].util_threshold.getDefault());

		cpufreq.cpu[1].gov.setValue(cpufreq.cpu[1].gov.getDefValue());
		cpufreq.cpu[1].scaling_min_freq.setValue(cpufreq.cpu[1].scaling_min_freq.getDefault());
		cpufreq.cpu[1].scaling_max_freq.setValue(cpufreq.cpu[1].scaling_max_freq.getDefault());
		cpufreq.cpu[1].util_threshold.setValue(cpufreq.cpu[1].util_threshold.getDefault());

		cpufreq.cpu[2].gov.setValue(cpufreq.cpu[2].gov.getDefValue());
		cpufreq.cpu[2].scaling_min_freq.setValue(cpufreq.cpu[2].scaling_min_freq.getDefault());
		cpufreq.cpu[2].scaling_max_freq.setValue(cpufreq.cpu[2].scaling_max_freq.getDefault());
		cpufreq.cpu[2].util_threshold.setValue(cpufreq.cpu[2].util_threshold.getDefault());

		cpufreq.cpu[3].gov.setValue(cpufreq.cpu[3].gov.getDefValue());
		cpufreq.cpu[3].scaling_min_freq.setValue(cpufreq.cpu[3].scaling_min_freq.getDefault());
		cpufreq.cpu[3].scaling_max_freq.setValue(cpufreq.cpu[3].scaling_max_freq.getDefault());
		cpufreq.cpu[3].util_threshold.setValue(cpufreq.cpu[3].util_threshold.getDefault());

		ondemand.io_is_busy.setValue(ondemand.io_is_busy.getDefault());
		ondemand.sampling_down_factor.setValue(ondemand.sampling_down_factor.getDefault());
		ondemand.sampling_rate.setValue(ondemand.sampling_rate.getDefault());
		ondemand.up_threshold.setValue(ondemand.up_threshold.getDefault());
		ondemand.down_threshold.setValue(ondemand.down_threshold.getDefault());
		ondemand.powersave_bias.setValue(ondemand.powersave_bias.getDefault());
		ondemand.touch_load.setValue(ondemand.touch_load.getDefault());
		ondemand.touch_load_threshold.setValue(ondemand.touch_load_threshold.getDefault());
		ondemand.touch_load_duration.setValue(ondemand.touch_load_duration.getDefault());

		conservative.cons.setValue(conservative.cons.getDefValue());
		conservative.freq_step.setValue(conservative.freq_step.getDefault());
		conservative.sampling_down_factor.setValue(conservative.sampling_down_factor.getDefault());
		conservative.sampling_rate.setValue(conservative.sampling_rate.getDefault());
		conservative.up_threshold.setValue(conservative.up_threshold.getDefault());
		conservative.down_threshold.setValue(conservative.down_threshold.getDefault());
		conservative.smooth_ui.setValue(conservative.smooth_ui.getDefault());

		interactive.inter.setValue(interactive.inter.getDefValue());
		interactive.hispeed_freq.setValue(interactive.hispeed_freq.getDefault());
		interactive.go_hispeed_load.setValue(interactive.go_hispeed_load.getDefault());
		interactive.min_sampling_time.setValue(interactive.min_sampling_time.getDefault());
		interactive.above_hispeed_delay.setValue(interactive.above_hispeed_delay.getDefault());
		interactive.timer_rate.setValue(interactive.timer_rate.getDefault());
		interactive.timer_slack.setValue(interactive.timer_slack.getDefault());
		interactive.boostpulse_duration.setValue(interactive.boostpulse_duration.getDefault());
		interactive.target_loads.setValue(interactive.target_loads.getDefValue());
		interactive.io_is_busy.setValue(interactive.io_is_busy.getDefault());
		interactive.input_boost.setValue(interactive.input_boost.getDefault());

		uv.uv_higher_khz_thres.setValue(uv.uv_higher_khz_thres.getDefault());
		uv.uv_boost.setValue(uv.uv_boost.getDefault());
		uv.uv_lower_uv.setValue(uv.uv_lower_uv.getDefault());
		uv.uv_higher_uv.setValue(uv.uv_higher_uv.getDefault());

		hp_enabled.setValue(hp_enabled.getDefault());
		hp_up_threshold.setValue(hp_up_threshold.getDefault());
		hp_min_online.setValue(hp_min_online.getDefault());
		hp_max_online.setValue(hp_max_online.getDefault());
		hp_up_timer_cnt.setValue(hp_up_timer_cnt.getDefault());
		hp_down_timer_cnt.setValue(hp_down_timer_cnt.getDefault());

		scheduler.setValue(scheduler.getDefValue());
		vibrator.setValue(vibrator.getDefault());
		touch_enable.setValue(touch_enable.getDefault());
		touch.setValue(touch.getDefault());
		dt_wake_enabled.setValue(dt_wake_enabled.getDefault());

		tcp_congestion.setValue(tcp_congestion.getDefValue());
		taccuracy.setDefValues();
		tjitter.setDefValues();

		led_red.setValue(led_red.getDefValue());
		led_green.setValue(led_green.getDefValue());
		led_blue.setValue(led_blue.getDefValue());
		gamma_r.setValue(gamma_r.getDefValue());
		gamma_g.setValue(gamma_g.getDefValue());
		gamma_b.setValue(gamma_b.getDefValue());

		read_ahead.setValue(read_ahead.getDefValue());

		lcdtemp.setDefValues();

		min_br.setValue(min_br.getDefault());
		max_br.setValue(max_br.getDefault());
		br_mode.setValue(br_mode.getDefault());

		logger.setValue(logger.getDefValue());
	}
}
