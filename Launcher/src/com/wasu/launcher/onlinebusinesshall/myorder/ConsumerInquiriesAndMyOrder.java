package com.wasu.launcher.onlinebusinesshall.myorder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.TextView;

import com.wasu.launcher.BaseActivity;
import com.wasu.launcher.R;
import com.wasu.vod.aidl.SDKOperationManager;
import com.wasu.vod.aidl.SDKOperationManager.BindService;
import com.wasu.vod.aidl.SDKOperationManager.MyListener;
/**
 * 业务订购
 * @author 77071
 *
 */
public class ConsumerInquiriesAndMyOrder extends BaseActivity implements BindService, Callback {
	private ListView listView1;
	private TextView tv_title;
	private int title_id;
	private ViewStub viewStub = null;
	private SDKOperationManager smManager;
	private MyListener ml;
	private Handler handler;
	private String userKey, siteId, token;
	private SharedPreferences spd;
	HandlerThread handlerThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lau_consumerinquiriesandmyorder);
		initView();
		spd = this.getSharedPreferences("mydevice", Context.MODE_APPEND);

		smManager = new SDKOperationManager(this);
		smManager.registerSDKOperationListener(this);
		handlerThread = new HandlerThread("consumer");
		handlerThread.start();
		handler = new Handler(handlerThread.getLooper(), this);
	}

	@Override
	protected void onDestroy() {
		if (handlerThread != null)
		handlerThread.quit();
		
		if (smManager != null)
		smManager.unRegisterSDKOperationListener();
		super.onDestroy();
	}

	@Override
	protected void initView() {
		loadViewLayout();
		findViewById();
		setListener();
	}

	@Override
	protected void loadViewLayout() {
	}

	@Override
	protected void findViewById() {
		listView1 = (ListView) findViewById(R.id.listView1);
		title_id = getIntent().getIntExtra("title", 0);
		viewStub = (ViewStub) findViewById(R.id.lau_us_in);// 头
		tv_title = (TextView) findViewById(R.id.tv_title);// 头TEXT

		if (title_id == 0) {
			tv_title.setText("我的订购");
			viewStub.setLayoutResource(R.layout.lau_item_consumerinquiriesandmyorder_wddg);
		}

		if (title_id == 1) {
			tv_title.setText("消费查询");
			viewStub.setLayoutResource(R.layout.lau_item_consumerinquiriesandmyorder_wdxf);
		}

		viewStub.inflate();
	}

	@Override
	protected void setListener() {
	}

	@Override
	public void onConnnected(MyListener ml) {
		this.ml = ml;
		handler.sendMessage(handler.obtainMessage(WindouManager.WASU_UPM_GETSITEID));
	}

	@Override
	public void onDisconnected(MyListener ml) {
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case WindouManager.WASU_UPM_GETSITEID:
			try {
				siteId = ml.wasu_upm_getSiteId();
				userKey = ml.wasu_upm_getUserKey();
				token = ml.wasu_upm_getToken();
				handler.sendMessage(handler.obtainMessage(WindouManager.WASU_UPM_GBOOKINGLIST_QUERY));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		case WindouManager.WASU_UPM_GBOOKINGLIST_QUERY:
			try {
				String order_history = "";
				if (userKey != null && siteId != null && token != null) {
					order_history = ml.wasu_upm_bookinglist_query(userKey, siteId, token);
				} else {
					userKey = spd.getString("userKey", "");
					order_history = ml.wasu_upm_bookinglist_query(userKey, siteId, token);
				}
				Log.e("", order_history);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		return true;
	}

	// wasu_upm_getSiteId();
	class WindouManager {
		public static final int WASU_UPM_GETSITEID = 1001;
		public static final int WASU_UPM_GBOOKINGLIST_QUERY = 1002;
	}

}
