package com.wasu.launcher.application;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.enrich.evmtv.evmtvManager;
import com.wasu.launcher.R;
import com.wasu.launcher.activity.ErrorActivity;

public class BReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			evmtvManager.init(context.getApplicationContext());
		} else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			ConnectivityManager connectMgr = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
			if (networkInfo == null/* || !networkInfo.isConnected()*/) {
				// 没有网络
//				Intent newIntent = new Intent(context, ErrorActivity.class);
//				newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				// 注意，必须添加这个标记，否则启动会失败
//				context.startActivity(newIntent);
			}
			NetworkInfo ethNetworkInfo = connectMgr
					.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
			if (ethNetworkInfo != null && ethNetworkInfo.isConnected()) {
				// 有线�?
			}
			NetworkInfo wifiNetworkInfo = connectMgr
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifiNetworkInfo != null && wifiNetworkInfo.isConnected()) {
				// 无线�?
			}
		}

	}

}
