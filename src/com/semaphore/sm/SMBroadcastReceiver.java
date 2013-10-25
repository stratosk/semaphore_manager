/*  Semaphore Manager
 *  
 *   Copyright (c) 2012 - 2013 Stratos Karafotis (stratosk@semaphore.gr)
 *   
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 */
package com.semaphore.sm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.semaphore.sai.SAIService;

public class SMBroadcastReceiver extends BroadcastReceiver {

    private final static String LOG_TAG = "Semaphore.BroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(LOG_TAG, "onReceive");
        context.startService(new Intent(context, SemaphoreService.class));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean sai_enable = prefs.getBoolean("sai_enable", false);
        if (sai_enable)
            context.startService(new Intent(context, SAIService.class));
    }
}
