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

public class SMActCoresProperty extends SMIntProperty {

    public SMActCoresProperty(String name, String path, boolean dynamic, int min, int max, int defvalue) {
        super(name, path, dynamic, min, max, defvalue);
    }

    @Override
    public void readValue() {
        int res;
        int val = getDefault();
        Commander cm = Commander.getInstance();
        
        for (int i = 1; i < 4; i++) {
            res = cm.readFile(getPath().concat(String.valueOf(i)).concat("/online"));

            if (res == 0) {
                String rt = cm.getOutResult().get(0);
                if (rt.equals("0")) {
                    val = i;
                    break;
                }
            }
        }
        
        setValue(val);
    }

    @Override
    public void writeValue() {
        Commander cm = Commander.getInstance();
        int res;
        String cmd;

        for (int i = 1; i < 4; i++) {
            if (i < getValue())
                cmd = "echo 1 > ".concat(getPath()).concat(String.valueOf(i)).concat("/online");
            else
                cmd = "echo 0 > ".concat(getPath()).concat(String.valueOf(i)).concat("/online");
            res = cm.run(cmd, cm.needSU(getPath()));
        }
    }

    @Override
    public void writeBatch(List<String> cmds) {
        for (int i = 1; i < 4; i++) {
            if (i < getValue())
                cmds.add("echo 1 > ".concat(getPath()).concat(String.valueOf(i)).concat("/online"));
            else
                cmds.add("echo 0 > ".concat(getPath()).concat(String.valueOf(i)).concat("/online"));
        }
    }
}