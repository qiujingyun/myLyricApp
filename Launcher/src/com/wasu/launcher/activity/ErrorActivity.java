package com.wasu.launcher.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.wasu.launcher.R;

public class ErrorActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lau_error);
		initRecriver();
	}

	private void initRecriver() {
		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(b, filter);
	}

	protected void onDestroy() {
		unregisterReceiver(b);
		super.onDestroy();
	};

	BroadcastReceiver b = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectMgr = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
			if (networkInfo == null || !networkInfo.isConnected()) {
			}
			NetworkInfo ethNetworkInfo = connectMgr
					.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
			if (ethNetworkInfo != null && ethNetworkInfo.isConnected()) {
				// 有线�?
				ErrorActivity.this.finish();
			}
			NetworkInfo wifiNetworkInfo = connectMgr
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifiNetworkInfo != null && wifiNetworkInfo.isConnected()) {
				// 无线�?
				ErrorActivity.this.finish();
			}
		}
	};
}
