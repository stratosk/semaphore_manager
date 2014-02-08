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

import java.util.List;

public class SMXboxProperty extends SMBatchProperty {

    SMModuleProperty joydev;
    SMModuleProperty xpad;
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
        joydev.setValue(Value);
        xpad.setValue(Value);
    }

    public void readValue() {
        joydev.readValue();
        xpad.readValue();
        this.Value = xpad.getValue();
    }

    public void writeValue() {
        if (Value) {
            joydev.writeValue();
            xpad.writeValue();
        } else {
            xpad.writeValue();
            joydev.writeValue();
        }
    }

    public void writeBatch(List<String> cmds) {
        if (Value) {
            joydev.writeBatch(cmds);
            xpad.writeBatch(cmds);
        } else {
            xpad.writeBatch(cmds);
            joydev.writeBatch(cmds);
        }
    }

    public SMXboxProperty(boolean defValue) {
        super("xbox");

        this.defValue = defValue;
        joydev = new SMModuleProperty("joydev", "/system/lib/modules/joydev", false, false);
        xpad = new SMModuleProperty("xpad", "/system/lib/modules/xpad", false, false);
    }
}
