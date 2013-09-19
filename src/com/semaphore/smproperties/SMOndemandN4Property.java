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

public class SMOndemandN4Property extends SMOndemandProperty {

    public SMIntProperty touch_load;
    public SMIntProperty touch_load_threshold;
    public SMIntProperty touch_load_duration;

    public SMOndemandN4Property() {
        super();

        io_is_busy.setDefault(1);
        sampling_down_factor.setDefault(4);
        sampling_down_factor.setDynamic(false);
        sampling_rate.setDefault(20000);
        sampling_rate.setValue(20000);
        up_threshold.setDefault(95);
        up_threshold.setValue(95);
     
        touch_load = new SMIntProperty("o_touch_load", basepath.concat("touch_load"), false, 0, 100, 75);
        touch_load_threshold = new SMIntProperty("o_touch_load_threshold", basepath.concat("touch_load_threshold"), false, 0, 100, 10);
        touch_load_duration = new SMIntProperty("o_touch_load_duration", basepath.concat("touch_load_duration"), false, 1, 10000, 1100);
    }

    @Override
    public void readValue() {
        super.readValue();
        touch_load.readValue();
        touch_load_threshold.readValue();
        touch_load_duration.readValue();
    }

    @Override
    public void writeValue() {
        super.writeValue();
        touch_load.writeValue();
        touch_load_threshold.writeValue();
        touch_load_duration.writeValue();
    }

    @Override
    public void writeBatch(List<String> cmds) {
        super.writeBatch(cmds);
        touch_load.writeBatch(cmds);
        touch_load_threshold.writeBatch(cmds);
        touch_load_duration.writeBatch(cmds);
    }
}
