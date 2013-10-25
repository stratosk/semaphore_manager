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

public class SMLEDTrigProperty extends SMStringProperty {

    @Override
    public void readValue() {
        String s;
        int i, j;

        if (isDynamic()) {
            setValue(getDefValue());
            return;
        }
        Commander cm = Commander.getInstance();
        int res = cm.readFile(getPath());
        if (res == 0) {
            s = cm.getOutResult().get(0);
            i = s.indexOf("[");
            j = s.indexOf("]");
            if (i != -1 && j != -1)
                s = s.substring(i + 1, j);
            else
                s = "";
            setValue(s);
        } else
            setValue(getDefValue());
    }

    public SMLEDTrigProperty(String name, String path, boolean dynamic, String defvalue) {
        super(name, path, dynamic, defvalue);
    }
}
