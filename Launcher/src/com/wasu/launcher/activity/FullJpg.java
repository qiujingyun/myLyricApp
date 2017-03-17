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
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wasu.android.volley.toolbox.ImageLoader;
import com.wasu.launcher.R;
import com.wasu.launcher.utils.PlayUtils;
import com.wasu.sdk_ott.UILApplication;
import com.wasu.vod.application.MyVolley;

public class FullJpg extends Activity {
	String jpgurl = null;
	private ImageView iv_fulljpg;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lau_layout_fulljpg);
		jpgurl = getIntent().getStringExtra("jpg");
		iv_fulljpg = (ImageView) findViewById(R.id.iv_fulljpg);
		ViewGroup.LayoutParams lp = iv_fulljpg.getLayoutParams();
		lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
		lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
		iv_fulljpg.setLayoutParams(lp);
		if (jpgurl != null) {
			setImage(iv_fulljpg, jpgurl);
		} else {
			iv_fulljpg.setImageResource(R.drawable.boot_adv);
		}
	}

	private void setImage(ImageView view, String paramUrl) {
		MyVolley.getImageLoader().get(paramUrl, ImageLoader.getImageListener(view, 0, 0));
	}
}
