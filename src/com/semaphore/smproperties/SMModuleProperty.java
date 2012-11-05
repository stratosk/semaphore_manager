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

public class SMModuleProperty extends SMProperty {

    private boolean Value;
    private boolean defValue;
    private String modpath = "/system/lib/modules/";

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
        if (isDynamic()) {
            return;
        }
        Commander cm = Commander.getInstance();
        int res = cm.run("lsmod | grep ".concat(getName()), false);
//        int res = cm.readFile(getPath());
        if (cm.getOutResult().isEmpty()) {
            setValue(false);
        } else {
            setValue(true);
        }
    }

    public void writeValue() {
        Commander cm = Commander.getInstance();
        int res;
        if (Value == true) {
            res = cm.run("insmod ".concat(modpath).concat(getPath()).concat(".ko"), true);
        } else {
            if (!getName().equals("cpufreq_smartass2")) {
                res = cm.run("rmmod ".concat(getName()), true);
            }
        }
    }

    public void writeBatch(List<String> cmds) {
        if (Value == true) {
            cmds.add("insmod ".concat(modpath).concat(getPath()).concat(".ko"));
        } else {
            if (!getName().equals("cpufreq_smartass2")) {
                cmds.add("rmmod ".concat(getName()));
            }
        }
    }

    public SMModuleProperty(String name, String path, boolean dynamic, boolean defValue) {
        super(name, path, false);

        this.defValue = defValue;
    }
}
