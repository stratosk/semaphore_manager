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

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.semaphore.smproperties.SemaCommonProperties;
import com.semaphore.smproperties.SemaI9000Properties;
import com.semaphore.smproperties.SemaN4Properties;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity
		implements NavigationDrawerFragment.NavigationDrawerCallbacks, TabInfoFragment.OnDonationListener {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private boolean createFragments = true;
	private static final int NUMBER_OF_TABS = 5;
	protected static final int REQUEST_CODE_DONATION = 1;
	int fragmentPos = 0;
	ArrayList<String> fragmentTitles;
	private CharSequence mTitle;
	private int leftToRight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean darktheme = prefs.getBoolean("darktheme", false);
		if (darktheme)
			setTheme(R.style.AppThemeDark);
		else
			setTheme(R.style.AppTheme);

		if ("mako".equals(android.os.Build.DEVICE)) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
				Device = SemaDevices.MakoL;
			else
				Device = SemaDevices.Mako;
		} else
			Device = SemaDevices.I9000;

		if (Device == SemaDevices.Mako || Device == SemaDevices.MakoL)
			sp = new SemaN4Properties();
		else
			sp = new SemaI9000Properties();

		checkSU_BB();
		unpackScripts();

		checkFirstRun();

		fragmentTitles = new ArrayList<>();
		fragmentTitles.add(getString(R.string.title_section_cpu));
		fragmentTitles.add(getString(R.string.title_section_tweaks));
		fragmentTitles.add(getString(R.string.title_section_modules));
		fragmentTitles.add(getString(R.string.title_section_sai));
		fragmentTitles.add(getString(R.string.title_section_info));
		fragmentTitles.add(getString(R.string.title_section_kmsg));

		super.onCreate(savedInstanceState);

		createFragments = savedInstanceState == null;

		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment)
				getFragmentManager().findFragmentById(R.id.navigation_drawer);

		if (savedInstanceState == null)
			mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout)
		);

		// setup action bar for tabs
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ignored) {
		}

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
//			toolbar.setTitle(getString(R.string.app_name));
//			toolbar.setSubtitle(SemaphoreVer);
		}
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_STANDARD);
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_HOME_AS_UP | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE);

			if (savedInstanceState == null)
				actionBar.setTitle(getString(R.string.app_name));
			actionBar.setSubtitle(SemaphoreVer);
		}

		updateSummaries();
	}

	public void handleSwipeLeftToRight() {
		if (fragmentPos < NUMBER_OF_TABS) {
			leftToRight = 1;
			mNavigationDrawerFragment.selectItem(fragmentPos + 1);
		}
	}

	public void handleSwipeRightToLeft() {
		if (fragmentPos > 0) {
			leftToRight = 2;
			mNavigationDrawerFragment.selectItem(fragmentPos - 1);
		}
	}

	private Fragment createFragment(int i) {
		Fragment fragment;

		switch (i) {
			case 0:
				fragment = TabCPUFragment.newInstance(i);
				break;
			case 1:
				fragment = TabTweaksFragment.newInstance(i);
				break;
			case 2:
				fragment = TabModulesFragment.newInstance(i);
				break;
			case 3:
				fragment = TabSAIFragment.newInstance(i);
				break;
			case 4:
				fragment = TabInfoFragment.newInstance(i);
				break;
			case 5:
				fragment = TabKmsgFragment.newInstance(i);
				break;
			default:
				fragment = null;
		}

		return fragment;
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction()
				.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

		boolean modulesOn = true;
		if (MainActivity.Device == MainActivity.SemaDevices.MakoL)
			modulesOn = false;
		if (!modulesOn && position >= 2) {
			if (leftToRight == 0)
				position++;
			if (position == 2) {
				if (leftToRight == 1)
					position++;
				else if (leftToRight == 2)
					position--;
			}
			leftToRight = 0;
		}

		for (int i = NUMBER_OF_TABS; i >= 0; i--) {
			if (!modulesOn && i == 2)
				continue;

			Fragment fragment = fm.findFragmentByTag(String.valueOf(i));
			if (fragment == null && createFragments) {
				fragment = createFragment(i);
				ft.add(R.id.container, fragment, String.valueOf(i));
			}
			if (i != position)
				ft.hide(fragment);
			else
				ft.show(fragment);
		}
		ft.commit();

		createFragments = false;
		fragmentPos = position;

		onSectionAttached(fragmentPos + 1);
	}

	public void onSectionAttached(int number) {
		mTitle = fragmentTitles.get(number - 1);
		android.support.v7.app.ActionBar toolbar = getSupportActionBar();
		if (toolbar != null) {
			toolbar.setTitle(mTitle);
		}
	}

	public void restoreActionBar() {
		android.support.v7.app.ActionBar toolbar = getSupportActionBar();
		if (toolbar != null) {
			toolbar.setDisplayShowTitleEnabled(true);
			toolbar.setTitle(mTitle);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.mainmenu, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			case R.id.menuitem1:
				readProperties();
				break;
			case R.id.menuitem2:
				resetDefault();
				break;
			case R.id.menuitem3:
				clearInitd();
				break;
			case R.id.menuitem5:
				changeTheme();
				break;
			case R.id.menuitem4:
				startActivityDonation();
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void startActivityDonation() {
		Intent intent = new Intent(this, DonationActivity.class);
		startActivityForResult(intent, REQUEST_CODE_DONATION);
	}

	@Override
	public void onDonation() {
		startActivityDonation();
	}

	public enum SemaDevices {
		I9000, Mako, MakoL
	}

	public static SemaDevices Device;

	public static SemaCommonProperties sp;
	private String SemaphoreVer = "";
	public static boolean readingValues = false;

	private void checkFirstRun() {
		boolean needRead;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		needRead = !prefs.contains("gov");
		if (Device == SemaDevices.Mako || Device == SemaDevices.MakoL)
			needRead = needRead || !prefs.contains("led_red") || !prefs.contains("read_ahead")
					|| !prefs.contains("uv_lower_uv") || !prefs.contains("hp_enabled")
					|| !prefs.contains("hp_max_online") || !prefs.contains("scaling_min_freq")
					|| !prefs.contains("min_br") || !prefs.contains("dt_wake_enabled")
					|| !prefs.contains("hp_up_timer_cnt");
		if (Device == SemaDevices.I9000)
			needRead = needRead || !prefs.contains("ab_max_br_threshold") || !prefs.contains("scaling_min_freq") ||
					!prefs.contains("o_powersave_bias");
		if (needRead) {
			PropTask pt = new PropTask();
			pt.execute(sp);
		} else
			sp.getPreferences(this);
	}

	private void checkSU_BB() {
		Commander cm = Commander.getInstance();
		int ret = cm.run("uname -r", true);
		if (ret == 1) {
			AlertDialog.Builder ad = new AlertDialog.Builder(this);
			ad.setMessage(getString(R.string.strErrorRoot));
			ad.setCancelable(false);
			ad.setPositiveButton(getString(R.string.strBtnClose), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					MainActivity.this.finish();
				}
			});
			ad.show();
		}
		ret = cm.run("uname -r", false);
		if (ret == 1) {
			AlertDialog.Builder ad = new AlertDialog.Builder(this);
			ad.setMessage(getString(R.string.strErrorBusybox));
			ad.setCancelable(false);
			ad.setPositiveButton(R.string.strBtnClose, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					MainActivity.this.finish();
				}
			});
			ad.show();
		} else if (cm.getOutResult().size() > 0)
			SemaphoreVer = cm.getOutResult().get(0);
		else
			SemaphoreVer = "";
	}

	private void changeTheme() {
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setMessage(getString(R.string.strConfTheme));
		ad.setCancelable(false);
		ad.setPositiveButton(getString(R.string.strBtnOK), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
				boolean darktheme = prefs.getBoolean("darktheme", false);
				SharedPreferences.Editor edit = prefs.edit();
				edit.putBoolean("darktheme", !darktheme);
				edit.commit();
				MainActivity.this.recreate();
			}
		});
		ad.setNegativeButton(getString(R.string.strBtnCancel), null);
		ad.show();
	}

	private void readProperties() {
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setMessage(getString(R.string.strConfOverride));
		ad.setCancelable(false);
		ad.setPositiveButton(R.string.strBtnOK, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				PropTask pt = new PropTask();
				pt.execute(sp);
			}
		});
		ad.setNegativeButton(R.string.strBtnCancel, null);
		ad.show();
	}

	private void resetDefault() {
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setMessage(getString(R.string.strConfReset));
		ad.setCancelable(false);
		ad.setPositiveButton(R.string.strBtnOK, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				defaultTask dt = new defaultTask();
				dt.execute(sp);
			}
		});
		ad.setNegativeButton(R.string.strBtnCancel, null);
		ad.show();
	}

	private void clearInitd() {
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setMessage(getString(R.string.strConfInitd));
		ad.setCancelable(false);
		ad.setPositiveButton(R.string.strBtnOK, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				int ret = Commander.getInstance().run("rm -r /system/etc/init.d/*", true);
				if (ret == 0) {
					Context appCon = getApplicationContext();
					if (appCon != null) {
						Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.strMsgInitd), Toast.LENGTH_LONG);
						toast.show();
					}
				}
			}
		});
		ad.setNegativeButton(R.string.strBtnCancel, null);
		ad.show();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void updateSummaries() {
		FragmentManager fm = getFragmentManager();

		TabCPUFragment cpu = (TabCPUFragment) fm.findFragmentByTag("0");
		if (cpu != null)
			cpu.updateSummaries();
		TabTweaksFragment tweaks = (TabTweaksFragment) fm.findFragmentByTag("1");
		if (tweaks != null)
			tweaks.updateSummaries();
		TabSAIFragment sai = (TabSAIFragment) fm.findFragmentByTag("3");
		if (sai != null)
			sai.updateSummaries();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_DONATION) {
			if (resultCode == RESULT_OK) {
			}
			if (resultCode == RESULT_CANCELED) {
			}
		}
	}

	private class PropTask extends AsyncTask<SemaCommonProperties, Void, String> {

		ProgressDialog pd = null;
		SemaCommonProperties sp;

		protected String doInBackground(SemaCommonProperties... params) {
			readingValues = true;
			sp = params[0];
			sp.readValues();
			sp.setPreferences(MainActivity.this);
			return null;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(MainActivity.this, "", getString(R.string.strMsgReading), true);
		}

		@Override
		protected void onPostExecute(String result) {
			MainActivity.this.recreate();
			updateSummaries();
			if (this.pd != null)
				this.pd.dismiss();
			readingValues = false;
		}
	}

	private class defaultTask extends AsyncTask<SemaCommonProperties, Void, String> {

		ProgressDialog pd = null;
		SemaCommonProperties sp;

		protected String doInBackground(SemaCommonProperties... params) {
			sp = params[0];
			sp.resetDefaults();
			sp.setPreferences(MainActivity.this);
			sp.writeBatch();

			return null;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(MainActivity.this, "", getString(R.string.strMsgReset), true);
		}

		@Override
		protected void onPostExecute(String result) {
			MainActivity.this.recreate();
			if (this.pd != null)
				this.pd.dismiss();
		}

	}

	private void copyAsset(String srcPath, String dstPath) throws IOException {
		Context appCon = getApplicationContext();
		if (appCon == null)
			return;
		AssetManager assetManager = getApplicationContext().getAssets();
		InputStream is = assetManager.open(srcPath);
		FileOutputStream fos = new FileOutputStream(dstPath, false);

		byte[] buffer = new byte[256];
		int n;
		do {
			n = is.read(buffer);
			if (n != -1)
				fos.write(buffer, 0, n);
		} while (n != -1);
		fos.flush();
		fos.close();
		is.close();
	}

	private void unpackScripts() {
		String appPath = getResources().getString(R.string.app_path);

		Commander cm = Commander.getInstance();
		cm.run("rm -r ".concat(appPath), false);
		cm.run("mkdir ".concat(appPath), false);

		String[] scripts = getResources().getStringArray(R.array.scripts);
		for (String f : scripts) {
			try {
				copyAsset(f, appPath + f);
			} catch (IOException ignored) {
			}
			Commander.getInstance().run("chmod 0755 ".concat(appPath + f), false);
		}
	}
}
