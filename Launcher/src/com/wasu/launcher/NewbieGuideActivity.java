package com.wasu.launcher;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import com.wasu.vod.view.CnmDialog;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wasu.launcher.Fragment.BaseFragment;
import com.wasu.launcher.Fragment.ServiceAgreementFragment;
import com.wasu.launcher.Fragment.WelcomeFragment;
import com.wasu.launcher.application.MyApplication;
import com.wasu.launcher.interfaces.MyFragmentListener;
import com.wasu.launcher.view.NetworkDialog;
import com.wasu.vod.aidl.ActiveMessage;
import com.wasu.vod.aidl.AuthMessage;
import com.wasu.vod.aidl.SDKOperationManager;
import com.wasu.vod.aidl.SDKOperationManager.BindService;
import com.wasu.vod.aidl.SDKOperationManager.MyListener;
import com.wasu.vod.utils.Constant;

public class NewbieGuideActivity extends BaseActivity implements MyFragmentListener, BindService, Callback {
	private ArrayList<Fragment> fragments;
	private WelcomeFragment wf;
	private ServiceAgreementFragment saf;
	Boolean isFrist, isagreed;
	String deviceId, tvId;
	MyListener ml;
	SDKOperationManager sm;
	SharedPreferences spd;
	SharedPreferences sp;
	NetworkDialog networkdialog;
	
	boolean isServicesConnect = false;
	
	boolean isHaveInitData = false;
	
	boolean isActiveSuccessfully = false;
	
	boolean isAuthComplete = false;
	
	boolean isBussinessInitComplete = false;
	
	HandlerThread handlerThread;
	
	private Handler mThreadHandler;
	
	boolean isToDoAuthfromActive = false;
	
	public static final String TAG = NewbieGuideActivity.class.getSimpleName();
	
	int retryCount = 1;
	
	TextView pager = null;
	
	
	static class NetworkWindowMessageID {
		
		public static final int NETWORK_UNLINK = 1900;
		
		public static final int NETWORK_LINK = 1901;
		
		public static final int FINISH_DIALOG = 1902;

	}
	
	private CnmDialog baseNetworkDialog;
	
	Handler mNetworkHandler = new Handler() {
		@Override
		public void handleMessage(Message msg){
			switch (msg.what) {
			case NetworkWindowMessageID.NETWORK_LINK:
				
				mNetworkHandler.removeMessages(NetworkWindowMessageID.NETWORK_LINK);
				if (baseNetworkDialog != null && baseNetworkDialog.isShowing()) {
					
					baseNetworkDialog.dismiss();
					baseNetworkDialog = null;
				}
				initData();
				break;
				
			case NetworkWindowMessageID.NETWORK_UNLINK:
				
				mNetworkHandler.removeMessages(NetworkWindowMessageID.NETWORK_UNLINK);
				
				if(baseNetworkDialog == null){
					CnmDialog.Builder builder = new CnmDialog.Builder(context);
					View mView = View.inflate(context, R.layout.cnm_dialog_notice, null);
					TextView tv = (TextView) mView.findViewById(R.id.dialog_content);
					tv.setText("网络未连接，请等待...");
					builder.setContentView(mView);					
					builder.setPositiveButton(getString(R.string.vod_play_confirm), new DialogInterface.OnClickListener() {			
						@Override
						public void onClick(DialogInterface dialog, int which) {
							baseNetworkDialog = null;
						}
					});									
					baseNetworkDialog = builder.create();		
					baseNetworkDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

								@Override
								public boolean onKey(DialogInterface dialog, int keyCode,
										KeyEvent event) {
									// TODO Auto-generated method stub
									if (event.getAction() == KeyEvent.ACTION_UP) {

									}else{
										switch(keyCode){
										case KeyEvent.KEYCODE_BACK:
										case Constant.SKYWORTH_QUIT:
											return true;
										}
									}
									return false;
								}
							});
					baseNetworkDialog.setCancelable(true);
					
					if(baseNetworkDialog !=null && !baseNetworkDialog.isShowing() && !isFinishing()) {
						baseNetworkDialog.show();
					}
					
				}
				break;
			case NetworkWindowMessageID.FINISH_DIALOG:
				
				mNetworkHandler.removeMessages(NetworkWindowMessageID.NETWORK_LINK);
	
				if (baseNetworkDialog != null && baseNetworkDialog.isShowing()) {
					baseNetworkDialog.dismiss();
					baseNetworkDialog = null;
				}
				break;

			default:
				break;
				
			}
			
		}
		
	};
	
	private BroadcastReceiver networkChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
				boolean isHaveNetwork = false;
				if (networkInfo != null && networkInfo.isConnected()) {
					isHaveNetwork = true;
				}
				NetworkInfo ethNetworkInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
				if (ethNetworkInfo != null && ethNetworkInfo.isConnected()) {
					isHaveNetwork = true;
				}
				NetworkInfo wifiNetworkInfo = connectMgr
						.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				if (wifiNetworkInfo != null && wifiNetworkInfo.isConnected()) {
					isHaveNetwork = true;
				}
				if (!isHaveNetwork) {
					mNetworkHandler.removeMessages(NetworkWindowMessageID.NETWORK_UNLINK);
					mNetworkHandler.sendMessageDelayed(mNetworkHandler.obtainMessage(NetworkWindowMessageID.NETWORK_UNLINK), 1000L);
				} else {
					mNetworkHandler.removeMessages(NetworkWindowMessageID.NETWORK_LINK);
					mNetworkHandler.sendMessageDelayed(mNetworkHandler.obtainMessage(NetworkWindowMessageID.NETWORK_LINK), 1000L);
					mNetworkHandler.removeMessages(NetworkWindowMessageID.FINISH_DIALOG);
				}
			}
		}
	};
	
	private BroadcastReceiver businessComplteBroadcast = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				
				isBussinessInitComplete = true;
				Log.e(TAG, "liqxtestactive: isBussinessInitComplete=" + isBussinessInitComplete);

			}
		}
	};
	
	private void registerNetworkReceiverSpecial() {
		
		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		this.registerReceiver(networkChangedReceiver, filter);
		
	}
	private void unregisterNetworkReceiverSpecial() {
		this.unregisterReceiver(networkChangedReceiver);
	}
	
    private void registerBusinessInitCompleteBroadcast() {
		
		IntentFilter filter = new IntentFilter(
				"com.wasu.business_initialization_completed");
		this.registerReceiver(businessComplteBroadcast, filter);
		
	}
    private void unRegisterBusinessInitCompleteBroadcast() {
		
		this.unregisterReceiver(businessComplteBroadcast);
		
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// View.inflate(this, R.layout.activity_main, null);
		
		isToDoAuthfromActive = this.getIntent().getExtras().getBoolean("isHaveActivie", false);

		sp = getSharedPreferences("mywelcome", Context.MODE_APPEND);
		spd = getSharedPreferences("mydevice", Context.MODE_APPEND);
		isFrist = sp.getBoolean("isFrist", false);
		isagreed = sp.getBoolean("isagreed", false);


		setContentView(R.layout.activity_newbieguide);
		
		pager = (TextView)this.findViewById(R.id.pager);
		pager.setVisibility(View.GONE);
		
		handlerThread = new HandlerThread("active");
		handlerThread.start();
		mThreadHandler = new Handler(handlerThread.getLooper(), this);
		
		//registerNetworkReceiverSpecial();
		registerBusinessInitCompleteBroadcast();
		
		initView();
		
		ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			
			initData();
			
		} else {
			
			MyApplication.setIsAuth(false);
			mNetworkHandler.post(finishAcivitySuccess);
			
		}

		

	}
	
	private void initData() {
		
		if (!isServicesConnect) {
			isServicesConnect = true;
		    sm = new SDKOperationManager(this);
		    sm.registerSDKOperationListener(this);
		}
		
	}

	@Override
	protected void onDestroy() {
		
		//unregisterNetworkReceiverSpecial();
		unRegisterBusinessInitCompleteBroadcast();
		if (sm != null) {
		   sm.unRegisterSDKOperationListener();
		}
		if (handlerThread != null) {
			handlerThread.quitSafely();
		}
		super.onDestroy();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void initView() {

		// TODO Auto-generated method stub
		loadViewLayout();
		findViewById();
		setListener();
/*		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		if (!isFrist) {
			transaction.replace(R.id.pager, fragments.get(0));
			transaction.addToBackStack(null);
			transaction.commit();
		}
		if (!isagreed && isFrist) {
			transaction.replace(R.id.pager, fragments.get(1));
			transaction.addToBackStack(null);
			transaction.commit();
		}*/

	}

	@Override
	protected void loadViewLayout() {

		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
		int screenHeight = dm.heightPixels; // 屏幕高（像素，如：800px）
		Log.e("gupu", "宽度：" + screenWidth + ";高度：" + screenHeight);

		fragments = new ArrayList<Fragment>();

	}

	FrameLayout fl;
	
	TextView jihuo;

	@Override
	protected void findViewById() {
		jihuo = (TextView) findViewById(R.id.pager);
		/*fl = (FrameLayout) findViewById(R.id.pager);

		Bundle args = new Bundle();
		wf = new WelcomeFragment();
		args.putInt("num", 0);
		wf.setArguments(args);
		fragments.add(wf);

		saf = new ServiceAgreementFragment();
		args.putInt("num", 1);
		saf.setArguments(args);
		fragments.add(saf);*/

	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void replaceFragment(int old, int nev) {
		refreshFragment(nev);

	}
	
	public boolean getIsActiveSuccessfully() {
		
		return isActiveSuccessfully;
	}
	
	public boolean getIsAuthComplete() {
		
		return isAuthComplete;
	}
	public boolean getIsBussinessInitComplete() {
		
		return isBussinessInitComplete;
	}


	public void refreshFragment(int index) {

/*		if (fragments != null && fragments.size() > 0) {
			FragmentManager fm = getFragmentManager();
			try {
				if (fm != null  && !fm.isDestroyed() ) {
					FragmentTransaction transaction = fm.beginTransaction();
					transaction.replace(R.id.pager, fragments.get(index));
					transaction.addToBackStack(null);
					transaction.commit();
					BaseFragment baseFragment = (BaseFragment) (fragments.get(index));
					if (baseFragment.isUp) {
						baseFragment.setMessage(1);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
	}

	@Override
	public void onConnnected(MyListener arg0) {
		// TODO Auto-generated method stub
		Log.e("gupu", "连接成功new");
		this.ml = arg0;
		// SharedPreferences spd = getSharedPreferences("mydevice",
		// Context.MODE_APPEND);
		

		try {
			
			if (isToDoAuthfromActive) {
				
				mThreadHandler.sendEmptyMessage(WindowMessageID.DO_AUTH);
				
			} else {
			    mThreadHandler.sendEmptyMessage(WindowMessageID.DO_ACTIVE);
			}

		} catch (Exception e) {
			// TODO: handle exception

		}

	}

	@Override
	public void onDisconnected(MyListener arg0) {
		// TODO Auto-generated method stub
		Log.e("gupu", "连接失败new");
		this.ml = arg0;
	}
	
	static class WindowMessageID {

		public static final int DO_ACTIVE = 0x00001000;

		public static final int DO_ACTIVE_WRITE_SUCCESS = 0x000002000;

		public static final int DO_AUTH = 0x000003000;
		
		public static final int DO_AUTH_VERIFY = 0x000004000;
	}
	
	Runnable activefailed = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			jihuo.setVisibility(View.INVISIBLE);
			networkdialog = new NetworkDialog(NewbieGuideActivity.this, "激活失败", 1);
		}
		
	};
	
	Runnable finishAcivitySuccess = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			jihuo.setVisibility(View.INVISIBLE);
			setResult(Activity.RESULT_OK, null);
	        finish();;
		}
		
	};

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		
			case WindowMessageID.DO_ACTIVE:
				
				mNetworkHandler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						if (pager != null) {
							pager.setText("激活中...请等待");
							pager.setVisibility(View.VISIBLE);
						}
						
					}});
				
				if (ml != null) {
					try {
						ActiveMessage am = ml.wasu_doActive();
						if (am != null) {
							String result = am.ab.getActiveMsgWriteResp().getResult();
							if (result.equalsIgnoreCase("0")) {// do active sucessfully
								
								mThreadHandler.sendEmptyMessage(WindowMessageID.DO_ACTIVE_WRITE_SUCCESS);
								
							} else {
								isActiveSuccessfully = false;
								mNetworkHandler.post(finishAcivitySuccess);
							}
						} else {
							isActiveSuccessfully = false;
							mNetworkHandler.post(finishAcivitySuccess);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e(TAG, "liqxtestactive:" + e.getLocalizedMessage());
						mNetworkHandler.post(finishAcivitySuccess);
					}
				} else {
					isActiveSuccessfully = false;
					mNetworkHandler.post(finishAcivitySuccess);
				}
				break;
				
			case WindowMessageID.DO_ACTIVE_WRITE_SUCCESS:
				
				if (ml != null) {
					
				    boolean writeNoti;
					try {
						writeNoti = ml.wasu_doActive_write_success();
						Log.e("writeNoti","liqxtest::::::: return"+ writeNoti);
						if (writeNoti) {
						    isActiveSuccessfully = true;
						    mThreadHandler.sendEmptyMessage(WindowMessageID.DO_AUTH);
					    } else {
					    	isActiveSuccessfully = false;
					    	mNetworkHandler.post(finishAcivitySuccess);
						    Log.e(TAG, "liqxtestactive: wasu_doActive_write_success failed");
					    }
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e(TAG, "liqxtestactive:" + e.getLocalizedMessage());
						mNetworkHandler.post(finishAcivitySuccess);
					}
				    
				} else {
					
					mNetworkHandler.post(finishAcivitySuccess);
				}
				break;
			case WindowMessageID.DO_AUTH:
				
				mNetworkHandler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						if (pager != null) {
							pager.setText("认证中...请等待");
							pager.setVisibility(View.VISIBLE);
						}
						
					}});
				
				if (ml != null) {
					
				    AuthMessage authM;
					try {
						authM = ml.wasu_doAuth();
						
						if (authM != null && (authM.getAuthBody().getAUTHMsgWriteResp().getResult().equalsIgnoreCase("0"))) {

							mThreadHandler.sendEmptyMessage(WindowMessageID.DO_AUTH_VERIFY);
							
						} else if (authM != null && authM.getAuthBody().getAUTHMsgWriteResp().getResult().equalsIgnoreCase("99")) {
							
							/*MyApplication.setIsAuth(true);
							ml.wasu_bsa_business_initialization(null);
							isAuthComplete = true;
							mNetworkHandler.post(finishAcivitySuccess);*/
							
							if (retryCount == 0) {
								
								MyApplication.setIsAuth(false);
							    ml.wasu_bsa_business_initialization(null);
							    isAuthComplete = true;
							    mNetworkHandler.post(finishAcivitySuccess);
							    
							} else {
								retryCount--;
								mThreadHandler.sendEmptyMessage(WindowMessageID.DO_ACTIVE);
							}
							
						} else {
							//auth failed
							if (retryCount == 0) {
								
								MyApplication.setIsAuth(false);
							    ml.wasu_bsa_business_initialization(null);
							    isAuthComplete = true;
							    mNetworkHandler.post(finishAcivitySuccess);
							    
							} else {
								retryCount--;
								mThreadHandler.sendEmptyMessage(WindowMessageID.DO_ACTIVE);
							}
						}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

					if (retryCount == 0) {
						MyApplication.setIsAuth(false);
						try {
							ml.wasu_bsa_business_initialization(null);
						} catch (Exception ee) {

						}
						isAuthComplete = true;
						mNetworkHandler.post(finishAcivitySuccess);
					} else {
						
						retryCount--;
						mThreadHandler.sendEmptyMessage(WindowMessageID.DO_ACTIVE);
					}
				}
				
				}
				break;
			case WindowMessageID.DO_AUTH_VERIFY:
				
			if (ml != null) {

				try {
					boolean verifyEncrypt = ml.wasu_verify_encrypt_string();

					if (verifyEncrypt) {
						// auth sucessfuuly
						MyApplication.setIsAuth(true);
						ml.wasu_bsa_business_initialization(null);
						isAuthComplete = true;
						mNetworkHandler.post(finishAcivitySuccess);

					} else {
						if (retryCount == 0) {
							MyApplication.setIsAuth(false);
							ml.wasu_bsa_business_initialization(null);
							isAuthComplete = true;
							mNetworkHandler.post(finishAcivitySuccess);
						} else {
							retryCount--;
							mThreadHandler
									.sendEmptyMessage(WindowMessageID.DO_ACTIVE);
						}
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if (retryCount == 0) {
						
						MyApplication.setIsAuth(false);
						try {
							ml.wasu_bsa_business_initialization(null);
						} catch (RemoteException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						isAuthComplete = true;
						mNetworkHandler.post(finishAcivitySuccess);

					} else {
						
						retryCount--;
						mThreadHandler
								.sendEmptyMessage(WindowMessageID.DO_ACTIVE);
						
					}
				}

			}
				break;
			default:
				break;
		}
		
		return true;
	}
}
