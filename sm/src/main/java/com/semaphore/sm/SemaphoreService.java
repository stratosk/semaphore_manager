/*  Semaphore Manager
 *  
 *   Copyright (c) 2012 - 2014 Stratos Karafotis (stratosk@semaphore.gr)
 *   
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 */
package com.semaphore.sm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.semaphore.smproperties.SemaCommonProperties;
import com.semaphore.smproperties.SemaI9000Properties;
import com.semaphore.smproperties.SemaN4Properties;

public class SemaphoreService extends IntentService {

    private final static String LOG_TAG = "Semaphore.SemaphoreService";

    public SemaphoreService() {
        super("SemaphoreService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "Applying settings");

        SemaCommonProperties sp;
        if ("mako".equals(android.os.Build.DEVICE))
            sp = new SemaN4Properties();
        else
            sp = new SemaI9000Properties();
        sp.getPreferences(this);
        sp.writeBatch();

        Log.d(LOG_TAG, "Settings applied successfully");
    }
}
