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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.semaphore.smproperties.SemaCommonProperties;
import com.semaphore.smproperties.SemaI9000Properties;
import com.semaphore.smproperties.SemaN4Properties;

public class TabModulesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SemaCommonProperties scp;
    private GestureDetectorCompat gestureDetector;
	private static final String ARG_SECTION_NUMBER = "section_number";

    public static TabModulesFragment newInstance(int sectionNumber) {
		TabModulesFragment fragment = new TabModulesFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setRetainInstance(false);
		if (MainActivity.Device == MainActivity.SemaDevices.Mako)
            addPreferencesFromResource(R.xml.preferences_modules_n4);
        else
            addPreferencesFromResource(R.xml.preferences_modules_i9000);

        gestureDetector = new GestureDetectorCompat(getActivity(), new SMGestureListener(getActivity()));
    }

    @Override
    public void onStart() {
        super.onStart();

        LinearLayout view = (LinearLayout) getView();
        if (view == null)
            return;

        View.OnTouchListener tl = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        };

        for (int i = 0; i < view.getChildCount(); i++) {
         	View child = view.getChildAt(i);
			if (child != null)
				child.setOnTouchListener(tl);
		}

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(3);
    }

    private void writeMako(SharedPreferences sharedPreferences, String key) {
        SemaN4Properties sp = (SemaN4Properties) scp;
        if (key.equals(sp.logger.getName())) {
            sp.logger.setValue(sharedPreferences.getBoolean(key, sp.logger.getDefValue()));
            sp.logger.writeValue();
        }
    }

    private void writeI9000(SharedPreferences sharedPreferences, String key) {
        SemaI9000Properties sp = (SemaI9000Properties) scp;

        if (key.equals(sp.logger.getName())) {
            sp.logger.setValue(sharedPreferences.getBoolean(key, sp.logger.getDefValue()));
            sp.logger.writeValue();
        } else if (key.equals(sp.tun.getName())) {
            sp.tun.setValue(sharedPreferences.getBoolean(key, sp.tun.getDefValue()));
            sp.tun.writeValue();
        } else if (key.equals(sp.cifs.getName())) {
            sp.cifs.setValue(sharedPreferences.getBoolean(key, sp.cifs.getDefValue()));
            sp.cifs.writeValue();
        } else if (key.equals(sp.configs.getName())) {
            sp.configs.setValue(sharedPreferences.getBoolean(key, sp.configs.getDefValue()));
            sp.configs.writeValue();
        } else if (key.equals(sp.radio_si4709_i2c.getName())) {
            sp.radio_si4709_i2c.setValue(sharedPreferences.getBoolean(key, sp.radio_si4709_i2c.getDefValue()));
            sp.radio_si4709_i2c.writeValue();
        } else if (key.equals(sp.mousedev.getName())) {
            sp.mousedev.setValue(sharedPreferences.getBoolean(key, sp.mousedev.getDefValue()));
            sp.mousedev.writeValue();
        } else if (key.equals(sp.xbox.getName())) {
            sp.xbox.setValue(sharedPreferences.getBoolean(key, sp.xbox.getDefValue()));
            sp.xbox.writeValue();
        } else if (key.equals(sp.usbhid.getName())) {
            sp.usbhid.setValue(sharedPreferences.getBoolean(key, sp.usbhid.getDefValue()));
            sp.usbhid.writeValue();
        } else if (key.equals(sp.uhid.getName())) {
            sp.uhid.setValue(sharedPreferences.getBoolean(key, sp.uhid.getDefValue()));
            sp.uhid.writeValue();
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (MainActivity.readingValues)
            return;
        scp = MainActivity.sp;

        if (MainActivity.Device == MainActivity.SemaDevices.Mako)
            writeMako(sharedPreferences, key);
        else
            writeI9000(sharedPreferences, key);
    }

    @Override
    public void onPause() {
        super.onPause();
		SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
		if (sp != null)
			sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
		SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
		if (sp != null)
			sp.registerOnSharedPreferenceChangeListener(this);
    }
}
