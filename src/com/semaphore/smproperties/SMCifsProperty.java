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

import java.util.List;

public class SMCifsProperty extends SMBatchProperty {

    SMModuleProperty cifs;
    SMModuleProperty md4;
    SMModuleProperty nls_utf8;
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
        cifs.setValue(Value);
        md4.setValue(Value);
        nls_utf8.setValue(Value);
    }

    public void readValue() {
        cifs.readValue();
        md4.readValue();
        nls_utf8.readValue();
        this.Value = cifs.getValue();
    }

    public void writeValue() {
        if (Value) {
            cifs.writeValue();
            md4.writeValue();
            nls_utf8.writeValue();
        } else {
            nls_utf8.writeValue();
            md4.writeValue();
            cifs.writeValue();
        }
    }

    public void writeBatch(List<String> cmds) {
        if (Value) {
            cifs.writeBatch(cmds);
            md4.writeBatch(cmds);
            nls_utf8.writeBatch(cmds);
        } else {
            nls_utf8.writeBatch(cmds);
            md4.writeBatch(cmds);
            cifs.writeBatch(cmds);
        }
    }

    public SMCifsProperty(boolean defValue) {
        super("cifs");

        this.defValue = defValue;
        cifs = new SMModuleProperty("cifs", "cifs", false, false);
        md4 = new SMModuleProperty("md4", "md4", false, false);
        nls_utf8 = new SMModuleProperty("nls_utf8", "nls_utf8", false, false);
    }
}
