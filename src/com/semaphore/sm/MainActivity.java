/*  Semaphore Manager
 *  
 *   Copyright (c) 2012 Stratos Karafotis (stratosk@semaphore.gr)
 *   
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 */
package com.semaphore.sm;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
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
import java.util.List;

public class MainActivity extends FragmentActivity {

    public enum SemaDevices {I9000, Mako};
    public static SemaDevices Device;
    
    public class PagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragments;
        List<String> titles;
        private boolean kmsg_visible = false;
        private boolean logcat_visible = false;

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            fragments = new ArrayList<Fragment>();
            fragments.add(new TabCPUFragment());
            fragments.add(new TabTweaksFragment());
            fragments.add(new TabModulesFragment());
            fragments.add(new TabSAIFragment());
            fragments.add(new TabInfoFragment());

            titles = new ArrayList<String>();
            titles.add("CPU");
            titles.add("TWEAKS");
            titles.add("MODULES");
            titles.add("SAI");
            titles.add("INFO");
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        public void addKmsg() {
            if (!kmsg_visible) {
                fragments.add(new TabKmsgFragment());
                titles.add("KMSG");
                kmsg_visible = true;
            }
        }

        public int getFragment(Class<?> ftype) {
            for (int i = 0; i < fragments.size(); i++) {
                if (fragments.get(i).getClass() == ftype) {
                    return i;
                }
            }
            return -1;
        }
    }
    ViewPager mViewPager;
    PagerAdapter mPagerAdapter;
    public static SemaCommonProperties sp;
    private String SemaphoreVer = "";
    public static boolean readingValues = false; 

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem1: {
                readProperties();
            }
            break;
            case R.id.menuitem2: {
                resetDefault();
            }
            break;
            case R.id.menuitem3: {
                clearInitd();
            }
            break;
            case R.id.menuitem4: {
                mPagerAdapter.addKmsg();
                mViewPager.setCurrentItem(mPagerAdapter.getFragment(TabKmsgFragment.class), true);
            }
            break;
            default:
                break;
        }

        return false;
    }

    private void checkFirstRun() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if (!prefs.contains("oc") || !prefs.contains("cv_l0")) {
            PropTask pt = new PropTask();
            pt.execute(sp);
        } else {
            sp.getPreferences(this);
        }
    }

    private void checkSU() {
        Commander cm = Commander.getInstance();
        int ret = cm.run("uname -r", true);
        if (ret == 1) {
            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setMessage("No root access!\nSemaphore Manager needs root access to run.");
            ad.setCancelable(false);
            ad.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.this.finish();
                }
            });
            ad.show();
        } else {
            if (cm.getOutResult().size() > 0)
                SemaphoreVer = cm.getOutResult().get(0);
            else
                SemaphoreVer = "";
        }
    }

    private void readProperties() {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setMessage("System settings maybe will override your personal settings.\nDo you want to continue?");
        ad.setCancelable(false);
        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PropTask pt = new PropTask();
                pt.execute(sp);
            }
        });
        ad.setNegativeButton("Cancel", null);
        ad.show();
    }

    private void resetDefault() {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setMessage("All settings will be reset to default.\nDo you want to continue?");
        ad.setCancelable(false);
        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                defaultTask dt = new defaultTask();
                dt.execute(sp);
            }
        });
        ad.setNegativeButton("Cancel", null);
        ad.show();
    }

    private void clearInitd() {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setMessage("All files in init.d directory will be deleted.\nDo you want to continue?");
        ad.setCancelable(false);
        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int ret = Commander.getInstance().run("rm -r /system/etc/init.d/*", true);
                if (ret == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "init.d cleared successfully", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        ad.setNegativeButton("Cancel", null);
        ad.show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // setup action bar for tabs

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }

        if ("mako".equals(android.os.Build.DEVICE))
            Device = SemaDevices.Mako;
        else
            Device = SemaDevices.I9000;

        if (Device == SemaDevices.Mako)
            sp = new SemaN4Properties();
        else 
            sp = new SemaI9000Properties();
        
        checkSU();
        unpackScripts();

        checkFirstRun();

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), this);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

        PagerTabStrip pagerTitleStrip = (PagerTabStrip) findViewById(R.id.pager_title_strip);
        pagerTitleStrip.setDrawFullUnderline(true);
        pagerTitleStrip.setTabIndicatorColor(0x33b5e5);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);

        actionBar.setTitle("Semaphore Manager");

        actionBar.setSubtitle(SemaphoreVer);

        updateSummaries();

    }

    private void updateSummaries() {
        TabCPUFragment cpu = (TabCPUFragment) mPagerAdapter.fragments.get(0);
        if (cpu != null) {
            cpu.updateSummaries();
        }
        TabTweaksFragment tweaks = (TabTweaksFragment) mPagerAdapter.fragments.get(1);
        if (tweaks != null) {
            tweaks.updateSummaries();
        }
        TabSAIFragment sai = (TabSAIFragment) mPagerAdapter.fragments.get(3);
        if (sai != null) {
            sai.updateSummaries();
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
            pd = ProgressDialog.show(MainActivity.this, "", "Reading system settings, Please wait", true);

        }

        @Override
        protected void onPostExecute(String result) {
            MainActivity.this.recreate();
            updateSummaries();
            if (this.pd != null) {
                this.pd.dismiss();
            }
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
            pd = ProgressDialog.show(MainActivity.this, "", "Resetting system settings, Please wait", true);

        }

        @Override
        protected void onPostExecute(String result) {
            MainActivity.this.recreate();
            if (this.pd != null) {
                this.pd.dismiss();
            }

        }
    }

    private void copyAsset(String srcPath, String dstPath) throws IOException {
        AssetManager assetManager = getApplicationContext().getAssets();
        InputStream is = assetManager.open(srcPath);
        FileOutputStream fos = new FileOutputStream(dstPath, false);

        byte[] buffer = new byte[256];
        int n = -1;
        do {
            n = is.read(buffer);
            if (n != -1) {
                fos.write(buffer, 0, n);
            }
        } while (n != -1);
        fos.flush();
        fos.close();
        is.close();
    }

    private void unpackScripts() {
        String appPath = getResources().getString(R.string.app_path);

        Commander cm = Commander.getInstance();
        int res = cm.run("rm -r ".concat(appPath), false);
        res = cm.run("mkdir ".concat(appPath), false);

        String[] scripts = getResources().getStringArray(R.array.scripts);
        for (String f : scripts) {
            try {
                copyAsset(f, appPath + f);
            } catch (IOException ex) {
            }
            res = Commander.getInstance().run("chmod 0755 ".concat(appPath + f), false);
        }
    }
}