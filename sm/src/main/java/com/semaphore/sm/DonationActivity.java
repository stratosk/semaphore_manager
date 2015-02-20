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
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class DonationActivity extends ActionBarActivity {

	private GestureDetectorCompat gestureDetector;
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String TAG = "Semaphore Donation: ";
	private static final int REQUEST_CODE_DONATE = 1;
	private IInAppBillingService mService;
	private ServiceConnection mServiceConn;
	private final String mDonationSmallSKU = "donation.small";
	private final String mDonationMediumSKU = "donation.medium";
	private final String mDonationLargeSKU = "donation.large";
	private final String mDonationXLargeSKU = "donation.xlarge";
	private String mDonationSmallTitle;
	private String mDonationMediumTitle;
	private String mDonationLargeTitle;
	private String mDonationXLargeTitle;
	private String mDonationSmallPrice;
	private String mDonationMediumPrice;
	private String mDonationLargePrice;
	private String mDonationXLargePrice;
	private RadioButton rbSmall;
	private RadioButton rbMedium;
	private RadioButton rbLarge;
	private RadioButton rbXLarge;
	private TextView tvSmallPrice;
	private TextView tvMediumPrice;
	private TextView tvLargePrice;
	private TextView tvXLargePrice;
	private String mSelectedSKU;
	private Button btnDonate;
	private String purchaseToken;
	private TextView tvDonationMessage;
	private TextView tvDonationHint;
	private RelativeLayout containerLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean darktheme = prefs.getBoolean("darktheme", false);
		if (darktheme)
			setTheme(R.style.AppThemeDark);
		else
			setTheme(R.style.AppTheme);

		super.onCreate(savedInstanceState);

		setupService();
		Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
		serviceIntent.setPackage("com.android.vending");
		bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

		setContentView(R.layout.activity_donation);

		tvDonationMessage = (TextView) findViewById(R.id.tvDonationMessage);
		tvDonationHint = (TextView) findViewById(R.id.tvDonationHint);
		containerLayout = (RelativeLayout) findViewById(R.id.container_layout);

		rbSmall = (RadioButton) findViewById(R.id.rb_small);
		rbMedium = (RadioButton) findViewById(R.id.rb_medium);
		rbLarge = (RadioButton) findViewById(R.id.rb_large);
		rbXLarge = (RadioButton) findViewById(R.id.rb_xlarge);

		tvSmallPrice = (TextView) findViewById(R.id.tvSmallPrice);
		tvMediumPrice = (TextView) findViewById(R.id.tvMediumPrice);
		tvLargePrice = (TextView) findViewById(R.id.tvLargePrice);
		tvXLargePrice = (TextView) findViewById(R.id.tvXLargePrice);

		btnDonate = (Button) findViewById(R.id.btnDonate);
		btnDonate.setEnabled(false);
		btnDonate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				donate();
			}
		});

		RBOnCheckChangeListener mOnCheckChangeListener = new RBOnCheckChangeListener();

		rbSmall.setOnCheckedChangeListener(mOnCheckChangeListener);
		rbMedium.setOnCheckedChangeListener(mOnCheckChangeListener);
		rbLarge.setOnCheckedChangeListener(mOnCheckChangeListener);
		rbXLarge.setOnCheckedChangeListener(mOnCheckChangeListener);

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
		}
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_STANDARD);
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_HOME_AS_UP | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE);

			if (savedInstanceState == null)
				actionBar.setTitle(getString(R.string.app_name));
		}

	}

	private class RBOnCheckChangeListener implements CompoundButton.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				btnDonate.setEnabled(true);
				if (buttonView == rbSmall)
					mSelectedSKU = mDonationSmallSKU;
				else if (buttonView == rbMedium)
					mSelectedSKU = mDonationMediumSKU;
				else if (buttonView == rbLarge)
					mSelectedSKU = mDonationLargeSKU;
				else if (buttonView == rbXLarge)
					mSelectedSKU = mDonationXLargeSKU;
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mService != null) {
			unbindService(mServiceConn);
		}
	}

	private void setupService() {
		mServiceConn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				mService = null;
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mService = IInAppBillingService.Stub.asInterface(service);
				if (mService != null) {
					getDonationItems();
				}
			}
		};
	}

	private void getDonationItems() {
		QueryDonationItems qdi = new QueryDonationItems();
		qdi.execute("");
	}

	private void donate() {
		try {
			Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), mSelectedSKU, "inapp", null);
			PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
			startIntentSenderForResult(pendingIntent.getIntentSender(), REQUEST_CODE_DONATE, new Intent(), 0, 0, 0);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (IntentSender.SendIntentException e) {
			e.printStackTrace();
		}
	}

	private void consumeDonate(String token) {
		ConsumeDonation cd = new ConsumeDonation(token);
		cd.execute("");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_DONATE) {
			int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
			String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
			String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

			Log.i(TAG, String.valueOf(resultCode));
			if (resultCode == Activity.RESULT_OK && purchaseData != null) {
				try {
					JSONObject jo = new JSONObject(purchaseData);
					Log.i(TAG, purchaseData);
					String sku = jo.getString("productId");
					Log.i(TAG, sku);
					purchaseToken = jo.getString("purchaseToken");
					Log.i(TAG, purchaseToken);
					consumeDonate(purchaseToken);
					showMessage(true);
				} catch (JSONException e) {
					showMessage(false);
					e.printStackTrace();
				}
			} else {
				showMessage(false);
			}
		}
	}

	private void showMessage(boolean success) {
		tvDonationHint.setVisibility(View.GONE);
		containerLayout.setVisibility(View.GONE);
		btnDonate.setVisibility(View.GONE);
		if (success)
			tvDonationMessage.setText(getResources().getString(R.string.strInfoDonateSuccess));
		else
			tvDonationMessage.setText(getResources().getString(R.string.strInfoDonateFail));
	}

	private class ConsumeDonation extends AsyncTask<String, Integer, Long> {

		private String mToken;

		public ConsumeDonation(String token) {
			mToken = token;
		}

		@Override
		protected Long doInBackground(String... params) {
			int response = -1;
			try {
				response = mService.consumePurchase(3, getPackageName(), mToken);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			return Long.valueOf(response);
		}

		@Override
		protected void onPostExecute(Long result) {
			if (result == 0) {
				Log.i(TAG, "Donation consumed");
			}
		}
	}

	private class QueryDonationItems extends AsyncTask<String, Integer, Long> {
		Bundle querySkus;

		public QueryDonationItems() {
			ArrayList<String> skuList = new ArrayList<String>();
			skuList.add(mDonationSmallSKU);
			skuList.add(mDonationMediumSKU);
			skuList.add(mDonationLargeSKU);
			skuList.add(mDonationXLargeSKU);
			Log.i("Semaphore Manager", "Query for items");
			querySkus = new Bundle();
			querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
		}

		@Override
		protected Long doInBackground(String... params) {
			try {
				Bundle skuDetails = mService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
				int response = skuDetails.getInt("RESPONSE_CODE");
				Log.i(TAG, "Response: " + response);
				if (response == 0) {
					ArrayList<String> responseList
							= skuDetails.getStringArrayList("DETAILS_LIST");
					Log.i(TAG, "responseList size: " + responseList.size());

					for (String thisResponse : responseList) {
						JSONObject object = null;
						try {
							object = new JSONObject(thisResponse);
							String sku = object.getString("productId");
							String price = object.getString("price");
							String title = object.getString("description");
							Log.i(TAG, "ProductID: " + sku + ", price: " + price);
							if (sku.equals(mDonationSmallSKU)) {
								mDonationSmallPrice = price;
								mDonationSmallTitle = title;
							} else if (sku.equals(mDonationMediumSKU)) {
								mDonationMediumPrice = price;
								mDonationMediumTitle = title;
							} else if (sku.equals(mDonationLargeSKU)) {
								mDonationLargePrice = price;
								mDonationLargeTitle = title;
							} else if (sku.equals(mDonationXLargeSKU)) {
								mDonationXLargePrice = price;
								mDonationXLargeTitle = title;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			tvSmallPrice.setText(mDonationSmallPrice);
			tvMediumPrice.setText(mDonationMediumPrice);
			tvLargePrice.setText(mDonationLargePrice);
			tvXLargePrice.setText(mDonationXLargePrice);
		}
	}

}
