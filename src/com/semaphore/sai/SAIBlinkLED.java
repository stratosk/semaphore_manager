/*  Semaphore Manager
 *  
 *   Copyright (c) 2012 Stratos Karafotis (stratosk@semaphore.gr)
 *   
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 */
package com.semaphore.sai;

import com.semaphore.sm.Commander;

public class SAIBlinkLED extends Thread {
    int mBlinkInterval;
    
    public SAIBlinkLED(int interval) {
        mBlinkInterval = interval;
    }
    
    @Override
    public void run() {
        String path = "/sys/devices/virtual/misc/notification/led";
        
        Commander cm = Commander.getInstance();
        
        while (!isInterrupted()) {
            cm.writeFile(path, "1");
            try {
                sleep(mBlinkInterval);
            } catch (InterruptedException ex) {
                break;
            }
            cm.writeFile(path, "0");
            try {
                sleep(mBlinkInterval);
            } catch (InterruptedException ex) {
                break;
            }            
        }
        cm.writeFile(path, "0");
    }
}
