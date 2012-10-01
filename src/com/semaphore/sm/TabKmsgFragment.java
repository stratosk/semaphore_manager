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

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TabKmsgFragment extends Fragment {

    private List<String> kmsg;
    private Thread outt;
    private TextView tv;
    private ScrollView sv;
    private int line = 0;
    private boolean kmsg_cleared = false;
    final Handler mHandler = new Handler();
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateResultsInUi();
        }
    };
    private DataOutputStream DataOutputStream;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*        setContentView(R.layout.kmsg);
         TextView view = (TextView) findViewById(R.id.kmsgview);
         String s = "";
         for (int i = 0; i < 100; i++) {
         s += "vogella.de ";
         }
         view.setText(s);*/

        setHasOptionsMenu(true);


        Commander cm = Commander.getInstance();
        cm.readKmsg();
        kmsg = cm.getKmsg();

        outt = new streamReader();
        outt.start();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem6: {
                clearKmsg();
            }
            break;
            case R.id.menuitem7: {
                saveKmsg();
            }
            break;
            default:
                break;
        }

        return false;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.kmsgmenu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.kmsg, container, false);
        tv = (TextView) view.findViewById(R.id.kmsgview);
        sv = (ScrollView) view.findViewById(R.id.scrollview);
        tv.setTextSize(10);
//        tv.setText("");

        line = 0;

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //outt.interrupt();
    }

    private void clearKmsg() {
        Commander.getInstance().clearKmsg();
        kmsg.clear();
        line = 0;
        kmsg_cleared = true;
    }

    private void saveKmsg() {
        FileWriter fstream;
        try {
            fstream = new FileWriter("/sdcard/kmsg.txt", false);
            BufferedWriter out = new BufferedWriter(fstream);

            for (int i = 0; i < kmsg.size(); i++) {
                out.write(kmsg.get(i) + "\n");
            }

            out.flush();
            out.close();

            Toast.makeText(this.getActivity().getApplicationContext(), "kmsg saved successfully in \n/sdcard/kmsg.txt", Toast.LENGTH_LONG).show();
        } catch (IOException ex) {
//            Logger.getLogger(TabKmsgFragment.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    private class streamReader extends Thread {

        @Override
        public void run() {

            while (!isInterrupted()) {
                if (tv != null && sv != null) {
                    mHandler.post(mUpdateResults);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    private void updateResultsInUi() {
        if (kmsg_cleared) {
            tv.setText("");
            kmsg_cleared = false;
        }
        if (kmsg.size() > line) {
            for (int i = line; i <= kmsg.size() - 1; i++) {
                tv.append(kmsg.get(i).concat("\n"));
                line++;
            }

            sv.post(new Runnable() {
                public void run() {

                    sv.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }
}
