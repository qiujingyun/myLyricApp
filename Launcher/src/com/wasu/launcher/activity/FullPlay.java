package com.wasu.launcher.activity;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.wasu.launcher.R;
import com.wasu.launcher.utils.PlayUtils;

public class FullPlay extends Activity implements Callback {
	PlayUtils playUtils;
	String url = "";
	private HandlerThread handlerThread;	
	Handler handler;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lau_layout_fullplay);
		handlerThread = new HandlerThread("FullPlay");
		handlerThread.start();
		handler = new Handler(handlerThread.getLooper(),this);
		playUtils = PlayUtils.getInstance();
		url = getIntent().getStringExtra("url");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("onResume", "url:" + url);
		if (!TextUtils.isEmpty(url)) {
			//playUtils.hplayer(url, new Rect(0, 0, 0, 0), false);
			handler.post(new Runnable(){
				
				@Override
				public void run() {
					playUtils.hplayer(url, new Rect(0, 0, 0, 0), false);					
				}
				
			});
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (handlerThread != null) {
			if (Build.VERSION.SDK_INT >= 18) {
				handlerThread.quitSafely();
			} else {
				handlerThread.quit();
			}
			handlerThread = null;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		//playUtils.playstop();
		handler.post(new Runnable(){
			
			@Override
			public void run() {
				playUtils.playstop();	
			}
			
		});
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}
}
