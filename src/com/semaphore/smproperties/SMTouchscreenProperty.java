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

public class SMTouchscreenProperty extends SMBaseProperty {

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

    public SMTouchscreenProperty(String name, String defValue) {
        super(name);

        this.defValue = defValue;
    }

    @Override
    public void readValue() {
        setValue(defValue);
    }

    @Override
    public void writeValue() {
        Commander cm = Commander.getInstance();
        int res;
        if (!Value.isEmpty()) {
            res = cm.run("/data/data/com.semaphore.sm/scripts/" + getValue(), true);
        }
    }
}