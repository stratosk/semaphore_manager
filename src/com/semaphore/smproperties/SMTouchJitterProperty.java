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

public class SMTouchJitterProperty extends SMBatchProperty {

    public SMIntProperty jitter_enable;
    public SMIntProperty adjust_margin;
    public String basepath;
    
    public SMTouchJitterProperty() {
        super("tjitter");

        basepath = "/sys/devices/virtual/input/lge_touch/jitter";

        jitter_enable = new SMIntProperty("jit_enable", basepath, false, 0, 1, 0);
        adjust_margin = new SMIntProperty("jit_adjust_margin", basepath, false, 0, 255, 100);
    }

    private void setValues(String jitter) {
        int i = 0;
        String[] tokens = jitter.split("\\s+");
        
        for (String s: tokens) {
            switch (i) {
                    case 0: jitter_enable.setValue(s); break;
                    case 1: adjust_margin.setValue(s); break;
            }
            i++;
        }
    }
    
    private String getValues() {
        String s;
        
        s = jitter_enable.getValString() + " " +
                adjust_margin.getValString();
        return s;
    }

    public void setDefValues() {
        jitter_enable.setValue(jitter_enable.getDefault());
        adjust_margin.setValue(adjust_margin.getDefault());
    }
    
    @Override
    public void readValue() {
        Commander cm = Commander.getInstance();

        int res = cm.readFile(basepath);
        if (res == 0) {
            String rt = cm.getOutResult().get(0);
            setValues(rt);
        } else {
            setDefValues();
        }
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
