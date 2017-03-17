package com.wasu.launcher.onlinebusinesshall.business;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wasu.launcher.BaseActivity;
import com.wasu.launcher.R;
import com.wasu.launcher.Fragment.UserFragment;
import com.wasu.vod.aidl.SDKOperationManager;
import com.wasu.vod.aidl.SDKOperationManager.BindService;
import com.wasu.vod.aidl.SDKOperationManager.MyListener;
/***
 * 业务办理
 * @author 77071
 *
 */
public class BusinessManagement extends BaseActivity implements BindService, Callback {
	private SDKOperationManager smManager;
	private Handler handler;
	private MyListener ml;
	private HandlerThread handlerThread;
	private String tvId, siteId;
	private SharedPreferences spd;
	private GridView bmgridview;
	private BusinessAdapter busAdapter;
	private List<BusinessResult.BusinessData> data;
	private Gson gson;
	TextView tv_un;
	HandlerStatic handlerstatic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lau_businessmanagement);
		smManager = new SDKOperationManager(this);
		smManager.registerSDKOperationListener(this);
		gson = new Gson();
		spd = this.getSharedPreferences("mydevice", Context.MODE_APPEND);
		initView();
		handlerThread = new HandlerThread("consumer");
		handlerThread.start();
		handler = new Handler(handlerThread.getLooper(), this);
		handlerstatic = new HandlerStatic(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (handlerThread != null)
			if (Build.VERSION.SDK_INT >= 18) {
				handlerThread.quitSafely();
			} else {
				handlerThread.quit();
			}
		
		if (smManager != null)
		smManager.unRegisterSDKOperationListener();
	}

	@Override
	protected void initView() {
		loadViewLayout();
		findViewById();
		setListener();
	}

	@Override
	protected void loadViewLayout() {
		data = new ArrayList<BusinessResult.BusinessData>();
		busAdapter = new BusinessAdapter(this, data);
	}

	@Override
	protected void findViewById() {
		bmgridview = (GridView) findViewById(R.id.lau_gv_businessmanagement);
		tv_un = (TextView) findViewById(R.id.tv_un);

	}

	@Override
	protected void setListener() {
		bmgridview.setAdapter(busAdapter);
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
		case WindouManager.WASU_UPM_PLANBIZ_ALL_GET:
			if (ml != null) {
				try {

					String planbiz = ml.wasu_upm_planbiz_topnum_get(siteId, tvId, 4);// ml.wasu_upm_planbiz_all_get(siteId,
																						// tvId);
					Log.e("", "aa" + planbiz);
					if (planbiz != null) {
						BusinessResult businessResult = gson.fromJson(planbiz, BusinessResult.class);
						if (businessResult != null && businessResult.getCode() == 0) {
							tv_un.post(new Runnable() {
								@Override
								public void run() {
									tv_un.setVisibility(View.GONE);
								}
							});
							data.clear();
							if (businessResult.getData() != null) {
								data.addAll(businessResult.getData());
								handlerstatic.sendMessage(handlerstatic.obtainMessage(WindouManager.GRIDVIEW));
							}
						}
					}
				} catch (RemoteException e) {
					Log.e("AndroidRuntime" + this.getClass().getSimpleName(), e.toString());
				} catch (com.google.gson.JsonSyntaxException e) {
					Log.e("AndroidRuntime" + this.getClass().getSimpleName(), e.toString());
				}
			}
			break;
		case WindouManager.WASU_UPM_GETSITEID:
			if (ml != null) {
				try {
					siteId = ml.wasu_upm_getSiteId();
					tvId = ml.wasu_bsa_getTvid();
					handler.sendMessage(handler.obtainMessage(WindouManager.WASU_UPM_PLANBIZ_ALL_GET));
				} catch (RemoteException e) {
					Log.e(this.getClass().getSimpleName(), e.toString());
				}
			} else {
				Log.e(this.getClass().getSimpleName(), "MyListener ml is null");
			}
			break;

		default:
			break;
		}
		return false;
	}

	/***/
	class WindouManager {
		/** 得到 siteid */
		public static final int WASU_UPM_GETSITEID = 2002;
		/** 获取全部产品包 */
		public static final int WASU_UPM_PLANBIZ_ALL_GET = 2001;
		/** GridView 刷新 */
		public static final int GRIDVIEW = 2003;
	}

	/** google 建议这样使用 防止内存泄漏 */
	static class HandlerStatic extends Handler {
		WeakReference<Activity> wfactivity;

		public HandlerStatic(Activity cActivity) {
			wfactivity = new WeakReference<Activity>(cActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			final BusinessManagement cActivity = (BusinessManagement) wfactivity.get();
			if (cActivity != null) {
				switch (msg.what) {
				case WindouManager.GRIDVIEW:
					cActivity.busAdapter.notifyDataSetChanged();
					break;
				default:
					break;
				}
			}
		}
	}

}
