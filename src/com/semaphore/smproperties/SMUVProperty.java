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

import java.util.List;

public class SMUVProperty extends SMBatchProperty {

    public SMIntProperty uv_lower_uv;
    public SMIntProperty uv_higher_uv;
    public SMIntProperty uv_higher_khz_thres;
    public SMIntProperty uv_boost;
    public boolean apply_boot;
    public boolean enabled;

    public SMUVProperty() {
        super("uv");

        String basepath = "/";

        uv_lower_uv = new SMIntProperty("uv_lower_uv", "/sys/module/acpuclock_krait/parameters/lower_uV", false, 0, 150000, 0);
        uv_higher_uv = new SMIntProperty("uv_higher_uv", "/sys/module/acpuclock_krait/parameters/higher_uV", false, 0, 150000, 0);
        uv_higher_khz_thres = new SMIntProperty("uv_higher_khz_thres", "/sys/module/acpuclock_krait/parameters/higher_khz_thres", false, 384000, 1512000, 1350000);
        uv_boost = new SMIntProperty("uv_boost", "/sys/module/acpuclock_krait/parameters/boost", false, 0, 1, 1);

        apply_boot = false;
        enabled = false;
    }

    public void readValue() {
        uv_lower_uv.readValue();
        uv_higher_uv.readValue();
        uv_higher_khz_thres.readValue();
        uv_boost.readValue();
    }

    public void writeValue() {
        if (enabled) {
            uv_higher_khz_thres.writeValue();
            uv_boost.writeValue();
            uv_lower_uv.writeValue();
            uv_higher_uv.writeValue();
        }
    }

    public void writeBatch(List<String> cmds) {
        if (enabled && apply_boot) {
            uv_higher_khz_thres.writeBatch(cmds);
            uv_boost.writeBatch(cmds);
            uv_lower_uv.writeBatch(cmds);
            uv_higher_uv.writeBatch(cmds);
        }
    }
}
