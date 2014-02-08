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

public class SMBLNProperty extends SMBaseProperty {

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

    public SMBLNProperty(String name, boolean defValue) {
        super(name);

        this.defValue = defValue;
    }

    @Override
    public void readValue() {
        Commander cm = Commander.getInstance();
        cm.run("ls /data/local/.bln | grep .bln", true);
        if (cm.getOutResult().isEmpty())
            setValue(false);
        else
            setValue(true);
    }

    @Override
    public void writeValue() {
        Commander cm = Commander.getInstance();
        if (Value)
            cm.run("touch /data/local/.bln", true);
        else
            cm.run("rm /data/local/.bln", true);
    }

    public void writeBatch(List<String> cmds) {
        if (Value)
            cmds.add("touch /data/local/.bln");
        else
            cmds.add("rm /data/local/.bln");
    }
}
