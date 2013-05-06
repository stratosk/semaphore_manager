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

public class SMLoggerProperty extends SMBaseProperty {

    private boolean Value;
    private boolean defValue;
    private String modPath;
    
    public boolean getDefValue() {
        return defValue;
    }

    public boolean getValue() {
        return Value;
    }

    public void setValue(boolean Value) {
        this.Value = Value;
    }

    public SMLoggerProperty(String name, String modPath, boolean defValue) {
        super(name);

        this.modPath = modPath;
        this.defValue = defValue;
    }

    @Override
    public void readValue() {
        Commander cm = Commander.getInstance();
        int res = cm.run("ls /data/local/logger.ko | grep logger.ko", true);
        if (cm.getOutResult().isEmpty()) {
            setValue(false);
        } else {
            setValue(true);
        }
    }

    @Override
    public void writeValue() {
        Commander cm = Commander.getInstance();
        int res;
        if (Value == true) {
            res = cm.run("cp " + modPath + "/logger.ko /data/local/logger.ko", true);
        } else {
            res = cm.run("rm /data/local/logger.ko", true);
        }
    }

    public void writeBatch(List<String> cmds) {
        if (Value == true) {
            cmds.add("cp " + modPath + "logger.ko /data/local/logger.ko");
        } else {
            cmds.add("rm /data/local/logger.ko");
        }
    }
}
