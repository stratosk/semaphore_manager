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

import android.app.Activity;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class SMGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_DISTANCE_THRESHOLD = 120; // was 100
    private static final int SWIPE_VELOCITY_THRESHOLD = 80; // was 100
    private Activity activity;

    public SMGestureListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float distanceX = e2.getX() - e1.getX();
        if (Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD
                && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            if (distanceX > 0)
                ((MainActivity) activity).handleSwipeRightToLeft();
            else
                ((MainActivity) activity).handleSwipeLeftToRight();
            return true;
        }
        return false;
    }
}
