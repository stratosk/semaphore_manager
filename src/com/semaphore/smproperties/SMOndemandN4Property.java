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

    public SMOndemandN4Property() {
        super();

        io_is_busy.setDefault(1);
        sampling_down_factor.setDefault(4);
        sampling_down_factor.setDynamic(false);
    }

    @Override
    public void readValue() {
        super.readValue();
    }

    @Override
    public void writeValue() {
        super.writeValue();
    }

    @Override
    public void writeBatch(List<String> cmds) {
        super.writeBatch(cmds);
    }
}
