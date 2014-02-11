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
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.semaphore.sai.SAIService;

public class TabSAIFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {
	private GestureDetectorCompat gestureDetector;
	private static final String ARG_SECTION_NUMBER = "section_number";

	Messenger mService = null;
	/**
	 * Flag indicating whether we have called bind on the service.
	 */
	boolean mBound;

	/**
	 * Class for interacting with the main interface of the service.
	 */
	public static TabSAIFragment newInstance(int sectionNumber) {
		TabSAIFragment fragment = new TabSAIFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the object we can use to
			// interact with the service.  We are communicating with the
			// service using a Messenger, so here we get a client-side
			// representation of that from the raw IBinder object.
			mService = new Messenger(service);
			mBound = true;
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			mService = null;
			mBound = false;
		}
	};

	public void sayHello(int near, int far, int vibrator, boolean pickupPhone,
						 boolean blinkLeds, int blinkInterval, boolean touchwakeDisable) {
		if (!mBound)
			return;
		Message msg;
		msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("vibrator_near", near);
		bundle.putInt("vibrator_far", far);
		bundle.putInt("vibrator", vibrator);
		bundle.putBoolean("blink_leds", blinkLeds);
		bundle.putInt("blink_interval", blinkInterval);
		bundle.putBoolean("pickup_phone", pickupPhone);
		bundle.putBoolean("touchwake_disable", touchwakeDisable);

		msg.setData(bundle);
		try {
			mService.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public TabSAIFragment() {
		super();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(false);

		if (MainActivity.Device == MainActivity.SemaDevices.Mako)
			addPreferencesFromResource(R.xml.preferences_sai_n4);
		else
			addPreferencesFromResource(R.xml.preferences_sai_i9000);

		updateSummaries();

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
		((MainActivity) activity).onSectionAttached(4);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (MainActivity.readingValues)
			return;

		Activity activity = getActivity();
		if (activity == null)
			return;

		if (key.equals("touch_enable")) {
			CheckBoxPreference pref = (CheckBoxPreference) findPreference("touchwake_disable");
			if (pref != null) {
				if (!sharedPreferences.getBoolean("touch_enable", false))
					pref.setChecked(false);
				pref.setEnabled(sharedPreferences.getBoolean("touch_enable", false));
			}
		}

		if (key.equals("sai_enable"))
			if (sharedPreferences.getBoolean(key, false)) {
				if (!isSAIServiceRunning()) {
					activity.startService(new Intent(activity, SAIService.class));
					activity.bindService(new Intent(activity, SAIService.class), mConnection, Context.BIND_AUTO_CREATE);
				}
			} else {
				if (mBound) {
					activity.unbindService(mConnection);
					mBound = false;
				}
				if (!isSAIServiceRunning())
					activity.stopService(new Intent(activity, SAIService.class));
			}

		if ((key.equals("vibrator_near") || key.equals("vibrator_far") || key.equals("pickup_phone")
				|| key.equals("blink_leds") || key.equals("blink_interval") || key.equals("touchwake_disable")) && isSAIServiceRunning())
			sayHello(sharedPreferences.getInt("vibrator_near", 100),
					sharedPreferences.getInt("vibrator_far", 25),
					sharedPreferences.getInt("vibrator", 100),
					sharedPreferences.getBoolean("pickup_phone", false),
					sharedPreferences.getBoolean("blink_leds", false),
					sharedPreferences.getInt("blink_interval", 200),
					sharedPreferences.getBoolean("touchwake_disable", false));
	}

	private boolean isSAIServiceRunning() {
		Activity activity = getActivity();
		ActivityManager manager = null;
		if (activity != null) {
			manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
		}
		if (manager != null) {
			for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
				if (SAIService.class.getName().equals(service.service.getClassName()))
					return true;
		}
		return false;
	}

	public boolean onPreferenceClick(Preference preference) {
		boolean ret = false;
		String key = preference.getKey();
		if (key != null && key.equals("sai_enable"))
			ret = true;
		return ret;
	}

	public void updateSummaries() {
		Preference pref = findPreference("vibrator_near");
		if (pref != null)
			pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));

		pref = findPreference("vibrator_far");
		if (pref != null)
			pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));
		pref = findPreference("blink_interval");
		if (pref != null)
			pref.setSummary(String.valueOf(((SeekBarPreference) pref).getValue()));
	}

	@Override
	public void onPause() {
		super.onPause();
		SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
		if (sp != null)
			sp.unregisterOnSharedPreferenceChangeListener(this);

		Activity activity = getActivity();
		if (activity == null)
			return;

		if (mBound) {
			activity.unbindService(mConnection);
			mBound = false;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
		if (sp != null)
			sp.registerOnSharedPreferenceChangeListener(this);

		Activity activity = getActivity();
		if (activity == null)
			return;

		if (isSAIServiceRunning())
			activity.bindService(new Intent(getActivity(), SAIService.class), mConnection,
					Context.BIND_AUTO_CREATE);

		CheckBoxPreference pref = (CheckBoxPreference) findPreference("touchwake_disable");
		if (pref != null && sp != null) {
			if (!sp.getBoolean("touch_enable", false))
				pref.setChecked(false);
			pref.setEnabled(sp.getBoolean("touch_enable", false));
		}
	}
}
