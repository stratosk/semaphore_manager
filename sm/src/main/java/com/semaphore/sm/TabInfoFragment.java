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
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class TabInfoFragment extends PreferenceFragment {
	private GestureDetectorCompat gestureDetector;
	private static final String ARG_SECTION_NUMBER = "section_number";

	public static TabInfoFragment newInstance(int sectionNumber) {
		TabInfoFragment fragment = new TabInfoFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(false);
		addPreferencesFromResource(R.xml.preferences_info);

		Commander cm = Commander.getInstance();
		cm.readFile("/proc/version");
		Preference pref = findPreference("kernel_version");
		if (pref != null) {
			pref.setSummary(cm.getOutResult().get(0));
		}

		pref = findPreference("Semaphore");
		if (pref != null) {
			String app_ver = "";
			try {
				app_ver = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
			} catch (PackageManager.NameNotFoundException e) {
			}
			pref.setSummary(app_ver);
		}

		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
		long totalMegs = mi.totalMem / 1048576L;

		pref = findPreference("system_memory");
		if (pref != null) {
			pref.setSummary(String.valueOf(totalMegs) + " MB");
		}
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

		for (int i = 0; i < view.getChildCount(); i++)
			view.getChildAt(i).setOnTouchListener(tl);

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
		((MainActivity) activity).onSectionAttached(5);
	}
}
