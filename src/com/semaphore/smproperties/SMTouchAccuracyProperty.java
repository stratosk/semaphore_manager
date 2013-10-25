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

public class SMTouchAccuracyProperty extends SMBatchProperty {

    public SMIntProperty accuracy_filter_enable;
    public SMIntProperty ignore_pressure_gap;
    public SMIntProperty delta_max;
    public SMIntProperty touch_max_count;
    public SMIntProperty max_pressure;
    public SMIntProperty direction_count;
    public SMIntProperty time_to_max_pressure;
    public String basepath;

    public SMTouchAccuracyProperty() {
        super("taccuracy");

        basepath = "/sys/devices/virtual/input/lge_touch/accuracy";

        accuracy_filter_enable = new SMIntProperty("ac_enable", basepath, false, 0, 1, 0);
        ignore_pressure_gap = new SMIntProperty("ac_ignore_pressure_gap", basepath, false, 0, 255, 5);
        delta_max = new SMIntProperty("ac_delta_max", basepath, false, 0, 255, 100);
        touch_max_count = new SMIntProperty("ac_touch_max_count", basepath, false, 0, 255, 40);
        max_pressure = new SMIntProperty("ac_max_pressure", basepath, false, 0, 255, 255);
        direction_count = new SMIntProperty("ac_direction_count", basepath, false, 0, 255, 13);
        time_to_max_pressure = new SMIntProperty("ac_time_to_max_pressure", basepath, false, 0, 255, 4);
    }

    private void setValues(String accuracy) {
        int i = 0;
        String[] tokens = accuracy.split("\\s+");

        for (String s : tokens) {
            switch (i) {
                case 0:
                    accuracy_filter_enable.setValue(s);
                    break;
                case 1:
                    ignore_pressure_gap.setValue(s);
                    break;
                case 2:
                    delta_max.setValue(s);
                    break;
                case 3:
                    touch_max_count.setValue(s);
                    break;
                case 4:
                    max_pressure.setValue(s);
                    break;
                case 5:
                    direction_count.setValue(s);
                    break;
                case 6:
                    time_to_max_pressure.setValue(s);
                    break;
            }
            i++;
        }
    }

    private String getValues() {
        String s;

        s = accuracy_filter_enable.getValString() + " "
                + ignore_pressure_gap.getValString() + " "
                + delta_max.getValString() + " "
                + touch_max_count.getValString() + " "
                + max_pressure.getValString() + " "
                + direction_count.getValString() + " "
                + time_to_max_pressure.getValString();
        return s;
    }

    public void setDefValues() {
        accuracy_filter_enable.setValue(accuracy_filter_enable.getDefault());
        ignore_pressure_gap.setValue(ignore_pressure_gap.getDefault());
        delta_max.setValue(delta_max.getDefault());
        touch_max_count.setValue(touch_max_count.getDefault());
        max_pressure.setValue(max_pressure.getDefault());
        direction_count.setValue(direction_count.getDefault());
        time_to_max_pressure.setValue(time_to_max_pressure.getDefault());
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

        int res = cm.run(cmd, cm.needSU(basepath));
    }

    public void writeBatch(List<String> cmds) {
        String cmd = "echo \"".concat(getValues()).concat("\" > ").concat(basepath);
        cmds.add(cmd);
    }
}
