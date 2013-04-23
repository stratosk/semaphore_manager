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

public class SMSchedulerProperty extends SMBaseProperty {

    private String Value;
    private String defValue;
    public String basepath;
    
    public String getDefValue() {
        return defValue;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String Value) {
        this.Value = Value;
    }

    public SMSchedulerProperty(String name, String defValue) {
        super(name);

        this.defValue = defValue;
    }

    @Override
    public void readValue() {
        Commander cm = Commander.getInstance();
        int res = cm.readFile("/sys/block/mmcblk0/queue/scheduler");
        if (!cm.getOutResult().isEmpty()) {
            String s = cm.getOutResult().get(0);
            int start = s.indexOf('[');
            int end = s.indexOf(']');
            s = s.substring(start + 1, end);
            setValue(s);
        } else {
            setValue("");
        }
    }

    @Override
    public void writeValue() {
        Commander cm = Commander.getInstance();
        int res;
        if (!Value.isEmpty()) {
            res = cm.run("/data/data/com.semaphore.sm/scripts/" + getValue(), true);
        }
    }

    public void writeBatch(List<String> cmds) {
        if (!Value.isEmpty()) {
            cmds.add("/data/data/com.semaphore.sm/scripts/" + getValue());
        }
    }
}
