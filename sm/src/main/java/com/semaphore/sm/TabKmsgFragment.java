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
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.GestureDetectorCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TabKmsgFragment extends Fragment {

	private List<String> kmsg;
	private TextView tv;
	private ScrollView sv;
	private GestureDetectorCompat gestureDetector;
	private static final String ARG_SECTION_NUMBER = "section_number";

	public static TabKmsgFragment newInstance(int sectionNumber) {
		TabKmsgFragment fragment = new TabKmsgFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(false);

		setHasOptionsMenu(true);

		gestureDetector = new GestureDetectorCompat(getActivity(), new SMGestureListener(getActivity()));
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(6);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		if (!hidden)
			refreshKmsg();
	}

	@Override
	public void onStart() {
		super.onStart();

		ScrollView view = (ScrollView) getView();
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

	private void refreshKmsg() {
		new ReadTask().execute();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menuitem6:
				refreshKmsg();
				break;
			case R.id.menuitem7:
				saveKmsg();
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
		if (view != null) {
			tv = (TextView) view.findViewById(R.id.kmsgview);
			sv = (ScrollView) view.findViewById(R.id.scrollview);
			tv.setTextSize(10);
		}

		return view;
	}

	private void saveKmsg() {
		String kmsg_file = Environment.getExternalStorageDirectory().getPath().concat("/kmsg.txt");
		FileWriter fstream;
		try {
			fstream = new FileWriter(kmsg_file, false);
			BufferedWriter out = new BufferedWriter(fstream);

			for (String aKmsg : kmsg) out.write(aKmsg + "\n");

			out.flush();
			out.close();

			Activity activity = getActivity();
			if (activity != null) {
				Context context = activity.getApplicationContext();
				if (context != null)
					Toast.makeText(context, getString(R.string.strMsgKmsgSaved) + kmsg_file, Toast.LENGTH_LONG).show();
			}
		} catch (IOException ignored) {
		}
	}

	private class ReadTask extends AsyncTask<Void, Void, String> {


		protected String doInBackground(Void... params) {
			Commander cm = Commander.getInstance();
			cm.run("dmesg", true);
			kmsg = cm.getOutResult();

			final StringBuilder sb = new StringBuilder();
			int i = kmsg.size() - kmsg.size() / 10;

			while (i < kmsg.size()) {
				sb.append(kmsg.get(i).concat("\n"));
				i++;
			}

			tv.post(new Runnable() {
				@Override
				public void run() {
					tv.setText(sb.toString());
					sv.fullScroll(View.FOCUS_DOWN);
				}
			});

			return null;
		}
	}
}
