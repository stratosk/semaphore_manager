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

public class SMOndemandI9000Property extends SMOndemandProperty {

    public SMIntProperty sampling_down_max_momentum;
    public SMIntProperty smooth_ui;

    public SMOndemandI9000Property() {
        super();

        io_is_busy.setDefault(0);
        sampling_down_factor.setDefault(4);
        sampling_down_factor.setDynamic(true);
        sampling_down_max_momentum = new SMIntProperty("o_sampling_down_max_momentum", basepath.concat("sampling_down_max_momentum"), true, 1, 1000, 16);
        smooth_ui = new SMIntProperty("o_smooth_ui", basepath.concat("smooth_ui"), false, 0, 1, 0);
    }

    @Override
    public void readValue() {
        super.readValue();

        sampling_down_max_momentum.readValue();
        smooth_ui.readValue();
    }

    @Override
    public void writeValue() {
        super.writeValue();
        
        sampling_down_max_momentum.writeValue();
        smooth_ui.writeValue();
    }

    @Override
    public void writeBatch(List<String> cmds) {
        super.writeBatch(cmds);

        sampling_down_max_momentum.writeBatch(cmds);
        smooth_ui.writeBatch(cmds);
    }
}
