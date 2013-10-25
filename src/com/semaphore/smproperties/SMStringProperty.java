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

public class SMStringProperty extends SMProperty {

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

    public void readValue() {
        if (isDynamic()) {
            setValue(defValue);
            return;
        }
        Commander cm = Commander.getInstance();
        int res = cm.readFile(getPath());
        if (res == 0)
            setValue(cm.getOutResult().get(0));
        else
            setValue(defValue);
    }

    public void writeValue(String path) {
        Commander cm = Commander.getInstance();
        String cmd = "echo \"".concat(getValue()).concat("\" > ").concat(path);

        int res = cm.run(cmd, cm.needSU(path));
    }

    public void writeValue() {
        writeValue(getPath());
    }

    public void writeBatch(List<String> cmds, String path) {
        cmds.add("echo \"".concat(getValue()).concat("\" > ").concat(path));
    }

    public void writeBatch(List<String> cmds) {
        writeBatch(cmds, getPath());
    }

    public SMStringProperty(String name, String path, boolean dynamic, String defvalue) {
        super(name, path, dynamic);

        defValue = defvalue;
    }
}
