package com.wasu.launcher.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wasu.launcher.BaseActivity;
import com.wasu.launcher.R;
import com.wasu.launcher.utils.ScaleAnimEffect;
import com.wasu.launcher.view.TestView;
import com.wasu.vod.aidl.SDKOperationManager;
import com.wasu.vod.aidl.SDKOperationManager.BindService;
import com.wasu.vod.aidl.SDKOperationManager.MyListener;
/**
 * 帮助页面
 * @author 77071
 *
 */
public class HelpActivity extends BaseActivity implements
		OnFocusChangeListener, OnClickListener, BindService {
/*
	private String TAG = "qjy";
	private ListView mylv;
	private RelativeLayout vod_lv;
	private int currentID = 0;
	ArrayList<String> info;
	private ListViewAdpter mAdapter;
	private int selectColor = 0xffebec15, unselectColor = 0x00000000;//0xff334455;
	*/
	List<ImageView> helpImage;
	TextView textView2, textView1;
	String[] str = { "支付失败", "使用复杂", "程序卡顿", "程序闪退", "音画不同步", "影片不清晰", "播放卡顿",
			"无法播放" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lau_layout_help);
		animEffect = new ScaleAnimEffect();
		helpImage = new ArrayList<ImageView>();
		// TelephonyManager tm = (TelephonyManager)
		// getSystemService(Context.TELEPHONY_SERVICE);
		// String DEVICE_ID = tm.getDeviceId();
		initView();
		sm = new SDKOperationManager(context);
		sm.registerSDKOperationListener(this);
	}

	private SDKOperationManager sm;
	private MyListener ml;

	@Override
	protected void initView() {
		textView2 = (TextView) findViewById(R.id.textView2);
		textView1 = (TextView) findViewById(R.id.textView1);
		for (int i = 0; i < 8; i++) {
			helpImage.add((ImageView) findViewById(getResources()
					.getIdentifier("help_im_" + i, "id", getPackageName())));
			helpImage.get(i).setOnFocusChangeListener(this);
			helpImage.get(i).setOnClickListener(this);
		}
		textView2.post(new Runnable() {
			@Override
			public void run() {
				textView2.setText(textView2.getText() + getVersion());
			}
		});
		
	}
	
	@Override
	protected void onResume(){
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (sm != null) {
			sm.unRegisterSDKOperationListener();
		}
	}

	private void sendMessage(String[] msg) {
		Intent i = new Intent("action_wasu_data_collection");
		i.putExtra("msg", msg);
		i.putExtra("owner", /* "com.wasu.launcher" */getPackageName());
		sendBroadcast(i);
	}

	@Override
	protected void loadViewLayout() {

	}

	@Override
	protected void findViewById() {

	}

	@Override
	protected void setListener() {

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			showOnFocusTranslAnimation(v);
		} else {
			showLooseFocusTranslAinimation(v);
		}
	}

	private ScaleAnimEffect animEffect;

	private void showOnFocusTranslAnimation(View viwe) {
		viwe.bringToFront();
		Animation msAnimation = null;
		msAnimation = animEffect.ScaleAnimation(1.0F, 1.05F, 1.0F, 1.05F);
		AnimationSet set = new AnimationSet(true);
		set.addAnimation(msAnimation);
		set.setFillAfter(true);
		viwe.startAnimation(set);
	}

	private void showLooseFocusTranslAinimation(View viwe) {
		Animation msAnimation = null;
		AnimationSet set = new AnimationSet(true);
		msAnimation = animEffect.ScaleAnimation(1.05F, 1.0F, 1.05F, 1.0F);
		set = new AnimationSet(true);
		set.addAnimation(msAnimation);
		set.setFillAfter(true);
		viwe.startAnimation(set);
	}

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
	}

	@Override
	public void onClick(View v) {
		for (int i = 0; i < helpImage.size(); i++) {
			if (helpImage.get(i) == v) {
				String[] strings = { str[i] };
				sendMessage(strings);
				Toast.makeText(this, "提交成功", 0).show();
			}
		}
	}

	@Override
	public void onConnnected(MyListener ml) {
		this.ml = ml;
		
		textView1.post(new Runnable() {

			@Override
			public void run() {
				try {
					if (HelpActivity.this.ml != null) {
						textView1.setText("TVID:" + HelpActivity.this.ml.wasu_bsa_getTvid());
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onDisconnected(MyListener ml) {

	}

}
