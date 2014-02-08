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

import com.semaphore.sm.Commander;

import java.util.List;

public class SMCVProperty extends SMBatchProperty {

    public SMIntProperty cv_max_arm;
    public SMIntProperty cv_l0;
    public SMIntProperty cv_l1;
    public SMIntProperty cv_l2;
    public SMIntProperty cv_l3;
    public SMIntProperty cv_l4;
    public boolean apply_boot;
    public boolean enabled;

    public SMCVProperty() {
        super("cv");

        cv_max_arm = new SMIntProperty("cv_max_arm", "/sys/devices/virtual/misc/customvoltage/max_arm_volt", false, 1200, 1500, 1350);
        cv_l0 = new SMIntProperty("cv_l0", "", false, 850, 1500, 1275);
        cv_l1 = new SMIntProperty("cv_l1", "", false, 850, 1500, 1200);
        cv_l2 = new SMIntProperty("cv_l2", "", false, 850, 1500, 1050);
        cv_l3 = new SMIntProperty("cv_l3", "", false, 850, 1500, 950);
        cv_l4 = new SMIntProperty("cv_l4", "", false, 850, 1500, 950);

        apply_boot = false;
        enabled = false;
    }

    public String getVolts() {
        return cv_l0.getValString() + " " + cv_l1.getValString() + " " + cv_l2.getValString() + " "
                + cv_l3.getValString() + " " + cv_l4.getValString();
    }

    public void readValue() {
        Commander cm = Commander.getInstance();

        int res = cm.readFile("/sys/devices/virtual/misc/customvoltage/max_arm_volt");
        if (res == 0) {
            String rt = cm.getOutResult().get(0);
            cv_max_arm.setValue(Integer.parseInt(rt.substring(0, rt.length() - 3)));
        } else
            cv_max_arm.setValue(cv_max_arm.getDefault());

        res = cm.readFile("/sys/devices/virtual/misc/customvoltage/arm_volt");
        if (res == 0)
            for (int i = 0; i < cm.getOutResult().size(); i++) {
                String line = cm.getOutResult().get(i);
                String[] volts = line.split(":");

                int volt = Integer.parseInt(volts[1].substring(1, volts[1].length() - 3));

                if (i == 0)
                    cv_l0.setValue(volt);
                else if (i == 1)
                    cv_l1.setValue(volt);
                else if (i == 2)
                    cv_l2.setValue(volt);
                else if (i == 3)
                    cv_l3.setValue(volt);
                else if (i == 4)
                    cv_l4.setValue(volt);
            }
    }

    public void writeValue() {
        if (enabled) {
            cv_max_arm.writeValue();

            Commander cm = Commander.getInstance();
            String cmd = "echo \"" + getVolts() + "\" > /sys/devices/virtual/misc/customvoltage/arm_volt";

            cm.run(cmd, cm.needSU("/sys/devices/virtual/misc/customvoltage/arm_volt"));
        }
    }

    public void writeBatch(List<String> cmds) {
        if (enabled && apply_boot) {
            cv_max_arm.writeBatch(cmds);

            String cmd = "echo \"" + getVolts() + "\" > /sys/devices/virtual/misc/customvoltage/arm_volt";
            cmds.add(cmd);
        }
    }
}
