/*  Semaphore Manager
 *  
 *   Copyright (c) 2012 Stratos Karafotis (stratosk@semaphore.gr)
 *   
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 */
package com.semaphore.smproperties;

import com.semaphore.sm.Commander;
import java.util.List;

public class SMIntProperty extends SMProperty {

    private int Value;
    private int minValue;
    private int maxValue;
    private int defValue;

    public int getDefault() {
        return defValue;
    }

    public String getDefString() {
        return String.valueOf(defValue);
    }

    public String getValString() {
        return String.valueOf(Value);
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getValue() {
        return Value;
    }

    public boolean getBoolean() {
        return Value > 0 ? true : false;
    }

    public boolean getDefBoolean() {
        return defValue > 0 ? true : false;
    }

    public void setValue(int Value) {
        if (Value <= maxValue && Value >= minValue) {
            this.Value = Value;
        }
    }

    public void setValue(String Value) {
        if (Value == null || Value.isEmpty())
            setValue(getDefault());
        else
            setValue(Integer.parseInt(Value));
    }

    public void readValue() {
        if (isDynamic()) {
            setValue(defValue);
            return;
        }
        Commander cm = Commander.getInstance();
//        int res = cm.runSu("cat ".concat(getPath()));
        int res = cm.readFile(getPath());
        if (res == 0) {
            String rt = cm.getOutResult().get(0);
            setValue(Integer.parseInt(rt));
        } else {
            setValue(defValue);
        }
    }

    public void writeValue() {
        Commander cm = Commander.getInstance();
        String cmd = "echo \"".concat(String.valueOf(getValue())).concat("\" > ").concat(getPath());
        
        int res = cm.run(cmd, cm.needSU(getPath()));
        
        if (getName().equals("oc")) {
                res = cm.run("echo \"".concat(String.valueOf(getValue() * 10000)).concat("\" > ").concat("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq"),
                            cm.needSU("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq"));
        }
    }

    public void writeBatch(List<String> cmds) {
        cmds.add("echo \"".concat(String.valueOf(getValue())).concat("\" > ").concat(getPath()));
        if (getName().equals("oc")) {
            cmds.add("echo \"".concat(String.valueOf(getValue() * 10000)).concat("\" > ").concat("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq"));
        }
    }

    public SMIntProperty(String name, String path, boolean dynamic, int min, int max, int defvalue) {
        super(name, path, dynamic);

        minValue = min;
        maxValue = max;
        defValue = defvalue;
    }
}
