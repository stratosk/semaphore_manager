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

public class SMAbProperty extends SMBatchProperty {

    public SMModuleProperty sema_autobr;
    public SMIntProperty min_brightness;
    public SMIntProperty max_brightness;
    public SMIntProperty max_lux;
    public SMIntProperty instant_update_thres;
    public SMIntProperty effect_delay_ms;
    public SMIntProperty max_br_threshold;

    public SMAbProperty() {
        super("sautobr");

        String basepath = "/sys/devices/virtual/misc/sema_autobr/";

        sema_autobr = new SMModuleProperty("sema_autobr", "/system/lib/modules/sema_autobr", false, false);

        min_brightness = new SMIntProperty("ab_min_brightness", basepath.concat("min_brightness"), false, 1, 255, 15);
        max_brightness = new SMIntProperty("ab_max_brightness", basepath.concat("max_brightness"), false, 1, 255, 255);
        max_lux = new SMIntProperty("ab_max_lux", basepath.concat("max_lux"), false, 1, 6000, 2900);
        instant_update_thres = new SMIntProperty("ab_instant_update_thres", basepath.concat("instant_update_thres"), false, 1, 100, 30);
        effect_delay_ms = new SMIntProperty("ab_effect_delay_ms", basepath.concat("effect_delay_ms"), false, 0, 10, 0);
        max_br_threshold = new SMIntProperty("ab_max_br_threshold", basepath.concat("max_br_threshold"), false, 0, 3000, 0);
    }

    public void readValue() {
        sema_autobr.readValue();
        min_brightness.readValue();
        max_brightness.readValue();
        max_lux.readValue();
        instant_update_thres.readValue();
        effect_delay_ms.readValue();
        max_br_threshold.readValue();
    }

    public void writeValue() {
        sema_autobr.writeValue();
        min_brightness.writeValue();
        max_brightness.writeValue();
        max_lux.writeValue();
        instant_update_thres.writeValue();
        effect_delay_ms.writeValue();
        max_br_threshold.writeValue();
    }

    public void writeBatch(List<String> cmds) {
        sema_autobr.writeBatch(cmds);
        min_brightness.writeBatch(cmds);
        max_brightness.writeBatch(cmds);
        max_lux.writeBatch(cmds);
        instant_update_thres.writeBatch(cmds);
        effect_delay_ms.writeBatch(cmds);
        max_br_threshold.writeBatch(cmds);
    }
}
