package com.wasu.launcher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.wasu.vod.view.CnmDialog;
import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.tcl.matrix.porting.apis.ITVPortingMgr;
import com.wasu.inner.activeauthenticator.Config;
import com.wasu.launcher.Fragment.BaseFragment;
import com.wasu.launcher.Fragment.GameFragment;
import com.wasu.launcher.Fragment.LiveFragment;
import com.wasu.launcher.Fragment.MusicFragment;
import com.wasu.launcher.Fragment.RecommendFragment;
import com.wasu.launcher.Fragment.UserFragment;
import com.wasu.launcher.Fragment.VodFragment;
import com.wasu.launcher.application.MyApplication;
import com.wasu.launcher.interfaces.MyFragmentListener;
import com.wasu.launcher.utils.Aanimate;
import com.wasu.launcher.utils.PinYin2Abbreviation;
import com.wasu.launcher.utils.Utils;
import com.wasu.launcher.view.SmoothHorizontalScrollView;
import com.wasu.launcher.view.SmoothHorizontalScrollView.ScrollViewListener;
import com.wasu.plugin.PluginManager;
import com.wasu.sdk_ott.upgrade.UpgradeService;
import com.wasu.utils.WasuNetworkOpeManager;
import com.wasu.vod.aidl.Apkinfo;
import com.wasu.vod.utils.Constant;
import com.wasu.vod.utils.SwitchUtils;
import com.wasu.vod.smarthome.MarqueeTextView;
import com.wasu.vod.smarthome.SmartHomeReceiver;

@SuppressLint("NewApi")
public class MainActivity extends BaseActivity
		implements OnClickListener, OnFocusChangeListener, MyFragmentListener, Callback {

	private static final String TAG = MainActivity.class.getSimpleName();
	private static final String GET_COOKIE_COMPLETE = "com.wasu.getcookie.complete";
	/** 更新Fragment */
	private static final int UPDATAFRAGMENT = 0x879788;
	private static final int ALTERNETWORKDIALOG = 1;
	// private FrameLayout fl_main;
	private RelativeLayout rl_bg;
	private RadioGroup title_group;
	private ImageView im_shadow_bg;
	private LayoutParams params;
	// private RadioButton rb_commend;
	// private RadioButton rb_live;
	// private RadioButton rb_vod;
	// private RadioButton rb_user;
	private TextView tv_main_date;
	private TextView tv_main_time;
	private RecommendFragment rf;
	private LiveFragment lf;
	private VodFragment vf;
//	private MusicFragment mf;
//	private GameFragment gameFragment;
	private UserFragment uf;
	//private SmartHomeFragment smartHomeFragment; // 智能家庭页面
	// private FragAdapter adapter;
	private ArrayList<BaseFragment> fragments;
	ImageView iv_net_state;

//	LinearLayout ll_search, ll_history, ll_collect;

	Handler childHandler = null;
	HandlerThread handlerThread = null;
	boolean isFirstInNetworkJudge = true;
	boolean isFirstInNetworkJudge_active = true;
	private static boolean isActiveing = false;
	Handler mMainHandler = new Handler();

	private SmartHomeReceiver mSmartHomeReceiver = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// View.inflate(this, R.layout.activity_main, null);
		setContentView(R.layout.lau_activity_main);
		initView();
		initData();
		registerNetworkReceiver();

		handlerThread = new HandlerThread("Main");
		handlerThread.start();
		childHandler = new Handler(handlerThread.getLooper(), this);

		Log.e("onCreate", "onCreate++++++");

		Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {

			@Override
			public boolean queueIdle() {
				// TODO Auto-generated method stub
				// 自升级
				updateapk();
				
				return false;
			}

		});
		
		//左侧导航栏进入动画
        doAnimation();
		
		final RadioButton rb1 = (RadioButton) title_group.getChildAt(0);
        rb1.post(new Runnable() {
            @Override
            public void run() {
                try {
                    rb1.setChecked(true);
                    rb1.setSelected(true);
                    rb1.requestFocus();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

		// 阿里小智
		if (SwitchUtils.isSmartHomeOn(this)) {
			MarqueeTextView.Config config = MarqueeTextView.createConfig();
			config.bgColor = getResources().getColor(R.color.smart_home_notice_bg);
			config.circulationCount = 4;
			config.speed = 4;
			config.textColor = getResources().getColor(R.color.smart_home_notice_text);
			config.textSize = 28;
			mSmartHomeReceiver = new SmartHomeReceiver(this, config);
			mSmartHomeReceiver.attachWindow();
		}

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (mSmartHomeReceiver != null) {
			IntentFilter filter = new IntentFilter("BROADCAST_SMART_HOME");
			registerReceiver(mSmartHomeReceiver, filter);
		}
		
	}

	@Override
	protected void initView() {
		// loadViewLayout();
		findViewById();
		setListener();
	}

	@Override
	protected void loadViewLayout() {
		// TODO Auto-generated method stub
	}

	private void initData() {
		updateTime();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterNetworkReceiver();
		// PlayUtils.getInstance().deallocate();

		if (childHandler != null) {
			childHandler.removeCallbacksAndMessages(null);
		}
		if (handlerThread != null) {
			if (Build.VERSION.SDK_INT >= 18) {
				handlerThread.quitSafely();
			} else {
				handlerThread.quit();
			}
			handlerThread = null;
		}
	}

	private CnmDialog networkDialog;

	private void alterNetworkDialog(int flag) {
		if (networkDialog == null) {

			CnmDialog.Builder builder = new CnmDialog.Builder(MainActivity.this);
			View mView = View.inflate(MainActivity.this, R.layout.cnm_dialog_notice, null);
			TextView tv = (TextView) mView.findViewById(R.id.dialog_content);
			tv.setText("网络已断开，请尽快连接网络！");
			builder.setContentView(mView);
			builder.setPositiveButton(getString(R.string.lau_play_confirm), new DialogInterface.OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					networkDialog.dismiss();
				}
			});			
			networkDialog = builder.create();				
			networkDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					// TODO Auto-generated method stub
					if (event.getAction() == KeyEvent.ACTION_UP) {

					} else {
						switch (keyCode) {
						case KeyEvent.KEYCODE_BACK:
							return true;
						}
					}
					return false;
				}
			});
			networkDialog.setCancelable(true);
		}
		if (!isFinishing() && networkDialog != null) {
			if (!networkDialog.isShowing() && flag == 1) {
				networkDialog.show();
			}
			if (networkDialog.isShowing() && flag == 0) {
				networkDialog.dismiss();
				networkDialog = null;
			}
		}
	}

	@Override
	protected void findViewById() {
		// FrameLayout fl = (FrameLayout) findViewById(R.id.pager);
		// fl_main = (FrameLayout) findViewById(R.id.fl_main);
		rl_bg = (RelativeLayout) findViewById(R.id.rl_bg);
		rl_bg.setBackgroundResource(R.drawable.lau_wasu_background);
		
		// 滚动区域阴影初始化
		im_shadow_bg = (ImageView) findViewById(R.id.scrollview_bg);
		params = new LayoutParams(Utils.getDeviceWidth(this) - (int) (getResources().getDimension(R.dimen.sm_100)),
				LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.topMargin = (int) getResources().getDimension(R.dimen.sm_35);
		im_shadow_bg.setLayoutParams(params);
		
		title_group = (RadioGroup) findViewById(R.id.title_group);
		RadioButton rb = (RadioButton) title_group.findViewById(R.id.rb_user);
		if (SwitchUtils.isFreeAuthSwitchOn(this)) {
			title_group.removeView(rb);
		}
		
		// K歌、游戏屏UI开关控制
		RadioButton rbVod = (RadioButton) title_group.findViewById(R.id.rb_vod);
		RadioButton rbmusic = (RadioButton) title_group.findViewById(R.id.rb_music);
		RadioButton rbgame = (RadioButton) title_group.findViewById(R.id.rb_game);
		if (!MyApplication.Config.isSupportMusic) {
			rbVod.setNextFocusDownId(R.id.rb_user);
			title_group.removeView(rbmusic);
			title_group.removeView(rbgame);
		}

		/*
		 * RadioButton rbSmarthome = (RadioButton)
		 * title_group.findViewById(R.id.rb_smart_home); if
		 * (!SwitchUtils.isSmartHomeOn(this)) {
		 * title_group.removeView(rbSmarthome); }
		 */

		// rb_commend = (RadioButton) findViewById(R.id.rb_recommend);
		// rb_live = (RadioButton) findViewById(R.id.rb_live);
		// rb_vod = (RadioButton) findViewById(R.id.rb_vod);
		// rb_user = (RadioButton) findViewById(R.id.rb_user);
		iv_net_state = (ImageView) findViewById(R.id.iv_net_state);

//		ll_search = (LinearLayout) findViewById(R.id.ll_search);
//		ll_search.setOnFocusChangeListener(this);
//		ll_search.setOnClickListener(this);
//		ll_history = (LinearLayout) findViewById(R.id.ll_history);
//		ll_history.setOnFocusChangeListener(this);
//		ll_history.setOnClickListener(this);
//		ll_collect = (LinearLayout) findViewById(R.id.ll_collect);
//		ll_collect.setOnFocusChangeListener(this);
//		ll_collect.setOnClickListener(this);

		tv_main_date = (TextView) findViewById(R.id.tv_main_date);
		tv_main_time = (TextView) findViewById(R.id.tv_main_time);

		fragments = new ArrayList<BaseFragment>();
		Bundle args = new Bundle();

		if (rf == null) {
			rf = new RecommendFragment(mScrollViewListener);
			args.putInt("num", 0);
			rf.setArguments(args);
			fragments.add(rf);
		}
		if (lf == null) {
			lf = new LiveFragment(mScrollViewListener);
			args.putInt("num", 1);
			lf.setArguments(args);
			fragments.add(lf);
		}
		if (vf == null) {
			vf = new VodFragment(mScrollViewListener);
			args.putInt("num", 2);
			vf.setArguments(args);
			fragments.add(vf);
		}
//		if (mf == null && MyApplication.Config.isSupportMusic) {
//			mf = new MusicFragment(mScrollViewListener);
//			args.putInt("num", 3);
//			mf.setArguments(args);
//			fragments.add(mf);
//			
//		}
//		if(gameFragment == null){
//			gameFragment = new GameFragment(mScrollViewListener);
//			args.putInt("num", 4);
//			gameFragment.setArguments(args);
//			fragments.add(gameFragment);
//		}
		if (uf == null && !SwitchUtils.isFreeAuthSwitchOn(this)) {

			uf = new UserFragment(mScrollViewListener);
			args.putInt("num", 3);
			uf.setArguments(args);
			fragments.add(uf);
		}
		/*
		 * if((smartHomeFragment == null)&& SwitchUtils.isSmartHomeOn(this)){
		 * smartHomeFragment = new SmartHomeFragment(); args.putInt("num", 4);
		 * smartHomeFragment.setArguments(args);
		 * fragments.add(smartHomeFragment); }
		 */

		// refreshFragment(0);
	}

	OnCheckedChangeListener cclist = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			Log.e("", "......." + checkedId);
		}
	};
	/** 点击第一个得到焦点 测试 */
	OnClickListener rlvu = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			int id = v.getNextFocusRightId();
			if (id != 0) {
				final View view = findViewById(id);
				if (view != null) {
					view.post(new Runnable() {
						@Override
						public void run() {
							view.requestFocus();
						}
					});
				}

				v.post(new Runnable() {
					@Override
					public void run() {
						v.clearFocus();
					}
				});
			}
		}
	};
	protected boolean updatafragmentisdelay = true;

	@Override
	protected void setListener() {
		int count = title_group.getChildCount();
		for (int i = 0; i < count; i++) {
			View v = (RadioButton) title_group.getChildAt(i);
			v.setFocusable(true);
//			v.setOnClickListener(rlvu);
			v.setOnKeyListener(mKeyListener);
			v.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(final View v, boolean hasFocus) {
					if (hasFocus) {

						v.post(new Runnable() {
							@Override
							public void run() {
								v.setSelected(true);
								((CompoundButton) v).setChecked(true);
							}
						});

					} else {
						v.post(new Runnable() {
							@Override
							public void run() {
								v.setSelected(false);
							}
						});

					}
				}
			});
		}

		title_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			int count = title_group.getChildCount();

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// Log.e("", "" + ((RadioButton)
				// (group.findViewById(checkedId))).getText());
				for (int i = 0; i < count; i++) {
					// final int index = i;
					final View v = (RadioButton) group.getChildAt(i);

					if (checkedId == v.getId()) {
						v.post(new Runnable() {
							@Override
							public void run() {
								((TextView) v).setShadowLayer(2, 2, 2, Color.DKGRAY);
								if (v.getId() == R.id.rb_music || v.getId() == R.id.rb_game) {
									//添加限免标签
									Drawable dra = v.getResources().getDrawable(R.drawable.free);	
									dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumHeight());
									((RadioButton)v).setCompoundDrawables(dra, null, null, null);
								}
								Aanimate.showOnFocusTranslAnimation(v);
							}
						});
						childHandler.removeMessages(UPDATAFRAGMENT);
//						if (updatafragmentisdelay) {
//							childHandler.sendMessageDelayed(childHandler.obtainMessage(UPDATAFRAGMENT, i, 0), 500);
//						} else {
							childHandler.sendMessageDelayed(childHandler.obtainMessage(UPDATAFRAGMENT, i, 0), 0);
//						}

					} else {
						v.post(new Runnable() {
							@Override
							public void run() {
								((TextView) v).setShadowLayer(0, 0, 0, Color.TRANSPARENT);
								((RadioButton)v).setCompoundDrawables(null, null, null, null);
								Aanimate.showLooseFocusTranslAinimation(v);
							}
						});
					}

				}

			}
		});

	}

	public View mFocusView;
	int i = 0;

	@SuppressLint("NewApi")
	public void refreshFragment(int index, int flag) {
		i = index;
		Log.d(TAG, "refreshFragment=== index:" + index);

		if (fragments != null && fragments.size() > 0) {
			FragmentManager fm = getFragmentManager();

			try {
				if (fm != null /* && !fm.isDestroyed() */) {
					FragmentTransaction transaction = fm.beginTransaction();
					transaction.replace(R.id.pager, fragments.get(index));
					transaction.addToBackStack(null);
					// transaction.hide(fragments.get(index));
					transaction.commit();
					BaseFragment baseFragment = (BaseFragment) (fragments.get(index));
					if (baseFragment.isUp) {
						baseFragment.setMessage(1);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (flag == 1) {
			RadioButton rb1 = (RadioButton) title_group.getChildAt(index);

			final int id1 = rb1.getNextFocusRightId();
			Log.d(TAG, "id1:" + id1);
			rb1.post(new Runnable() {
				@Override
				public void run() {
					// 保证在rb1显示后 id1 再去获得焦点
					try {
						findViewById(id1).requestFocus();
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
				}
			});
			updatafragmentisdelay = true;
		}
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
				if (networkInfo == null || !networkInfo.isConnected()) {
					// 没有网络

					iv_net_state.setImageResource(R.drawable.lau_wifi_n);

					if (isFirstInNetworkJudge) {
						isFirstInNetworkJudge = false;
						isRefresh = true;
						return;
					}
					// 网络异常提示
					childHandler.removeMessages(ALTERNETWORKDIALOG);
					childHandler.sendMessageDelayed(childHandler.obtainMessage(ALTERNETWORKDIALOG, 1, 0), 3000);

					// Intent newIntent = new Intent(context,
					// ErrorActivity.class);
					// newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// // 注意，必须添加这个标记，否则启动会失败
					// context.startActivity(newIntent);
				} else {
					
					if (!Config.hasUserData()) {
						
						
						if (isActiveing == false) {
							isActiveing = true;

						    mMainHandler.postDelayed(new Runnable(){

							    @Override
							    public void run() {
								   // TODO Auto-generated method stub
								    Intent intentTemp = new Intent(MainActivity.this, NewbieGuideActivity.class);
								    intentTemp.putExtra("isHaveActivie", false);
								    MainActivity.this.startActivityForResult(intentTemp, 100, null);
							    }
							
						    }, 1000L);
						}
						
					}
					
				}
				NetworkInfo wifiNetworkInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				if (wifiNetworkInfo != null && wifiNetworkInfo.isConnected()) {
					// 无线�?
					Refresh();
					// isRefresh = false;
					iv_net_state.setImageResource(R.drawable.lau_wifi);
					// 网络异常提示
					childHandler.removeMessages(ALTERNETWORKDIALOG);
					childHandler.sendMessageDelayed(childHandler.obtainMessage(ALTERNETWORKDIALOG, 0, 0), 0);
				}

				NetworkInfo ethNetworkInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
				if (ethNetworkInfo != null && ethNetworkInfo.isConnected()) {
					// 有线�?
					Refresh();
					// isRefresh = false;
					iv_net_state.setImageResource(R.drawable.lau_eth);
					// 网络异常提示
					childHandler.removeMessages(ALTERNETWORKDIALOG);
					childHandler.sendMessageDelayed(childHandler.obtainMessage(ALTERNETWORKDIALOG, 0, 0), 0);
				}

			}
			if (action.equals(GET_COOKIE_COMPLETE)) {
				// 得到cookie后，重新刷新页面
				Log.d("yingjh", "GET_COOKIE OK OKOKOKOKOKOKO");
				Refresh();
			}
			if (isFirstInNetworkJudge) {
				isFirstInNetworkJudge = false;
				isRefresh = true;
				return;
			}
		}
	};

	boolean isRefresh = false;

	private void Refresh() {
		if (isRefresh) {
			if (i == 0) {
				((RecommendFragment) (fragments.get(0))).Refresh();
			} else if (i == 1) {
				((LiveFragment) (fragments.get(1))).Refresh();
			} else if (i == 2) {
				((VodFragment) (fragments.get(2))).Refresh();
			} else if (i == 3) {
				// 个人屏不需要网络刷新
				// ((UserFragment) (fragments.get(3))).Refresh();
			}
		}

	}

	private void registerNetworkReceiver() {
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(GET_COOKIE_COMPLETE);// cookie get
		this.registerReceiver(receiver, filter);
	}

	private void unregisterNetworkReceiver() {
		this.unregisterReceiver(receiver);
	}

	@Override
	public void onClick(View v) {
		// Intent intent = getPackageManager().getLaunchIntentForPackage(
		// "com.wasu.vod");
		// if (intent != null) {
//		if (v == ll_search) {
//			sendmessage("ss");
//			Intent intent2 = new Intent("com.wasu.vod.SEARCH");
//			startActivity(intent2);
//		} else if (ll_history == v) {
//			sendmessage("ll");
//			Intent intent3 = new Intent("com.wasu.vod.OTHERS");
//			intent3.putExtra("TYPE", "历史");
//			startActivity(intent3);
//		} else if (ll_collect == v) {
//			sendmessage("sc");
//			Intent intent4 = new Intent("com.wasu.vod.OTHERS");
//			intent4.putExtra("TYPE", "收藏");
//			startActivity(intent4);
//		}

		// } else {
		// Toast.makeText(this, "没有安装应用, 请安装!", 0).show();
		// }
	}

	private void sendmessage(String tr) {
		String[] strs = { Utils.getWasuTime() + "|" + Utils.getTVID() + "|ott_"
				+ PinYin2Abbreviation.cn2py(String.valueOf(tr)) + "|ott|||" + tr + "|||||", };
		Utils.sendMessage(this, strs, "wasu_page_access_info");
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		int id = v.getId();
		if (id == R.id.ll_search || id == R.id.ll_history || id == R.id.ll_collect) {
			TextView view = (TextView) ((ViewGroup) v).getChildAt(1);
			if (hasFocus) {
				view.setTextColor(getResources().getColor(R.color.lau_yellow));
			} else {
				view.setTextColor(getResources().getColor(R.color.lau_white1));
			}
		}
	}

	@Override
	protected void onResume() {
		Log.e("onResume", "onResume");
		super.onResume();
		String[] strs = new String[] { Utils.getWasuTime() + "|" + Utils.getTVID() + "|ott||||" + "|首页|||||" };
		Utils.sendMessage(this, strs, "wasu_default_page_info");

		handler.postDelayed(timerRun, 1000); // 开始Timer
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mSmartHomeReceiver != null) {
			unregisterReceiver(mSmartHomeReceiver);			
		}

	}

	protected void onPause() {
		// Log.d("yingh", "launcher main onpause");
		// PlayUtils.getInstance().playdeallocate();

		handler.removeCallbacks(timerRun); // 停止Timer
		super.onPause();
	}

	/* 通过Handler来接收进程所传递的信息并更新TextView：[mw_shl_code=java,true] */
	Handler handler = new Handler();

	private Runnable timerRun = new Runnable() {
		public void run() {
			updateTime();
			handler.postDelayed(this, 1000); // postDelayed(this,1000)方法安排一个Runnable对象到主线程队列中
		}
	};

	private void updateTime() {
		// // 取得系统时间
		long time = System.currentTimeMillis();
		String texttime = new SimpleDateFormat("HH:mm").format(time);
		String textData = new SimpleDateFormat("MM/dd/yyyy").format(time);
		tv_main_time.setText(texttime);
		tv_main_date.setText(textData);
	}

	private long timeStamp = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e("AndroidRunTime keyCode=== ",""+keyCode);
		long curTime = System.currentTimeMillis();
		if (curTime - timeStamp < 200) {
			//点击过快，会导致页面错乱，将该事件屏蔽
			return true;
		}else {
			timeStamp = curTime;
		}
		MagicKeyProcess(keyCode);// 按键可清除激活信息
		if (keyCode == 5011) {
			try {
				if (this.getWindow().getDecorView().getRootView().findFocus().getId() == title_group
						.getCheckedRadioButtonId()) {
					finish();
				} else {
					findViewById(title_group.getCheckedRadioButtonId()).requestFocus();
					return true;
				}
			} catch (Exception e) {
				Log.e("AndroidRunTime", e.toString());
				return super.onKeyDown(keyCode, event);
			}
		}

		else if (keyCode == KeyEvent.KEYCODE_HOME) {

			ITVPortingMgr.getInstance().getUtilsCtrl().enableHotKey(true);
			ITVPortingMgr.getInstance().getUtilsCtrl().enableHomeKey(true);

			Intent startMain = new Intent(Intent.ACTION_MAIN);
			startMain.addCategory(Intent.CATEGORY_HOME);
			startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startMain);
			// System.exit(0);
			// finish();
			ITVPortingMgr.getInstance().getSysCtrl().stopITV();
			return true;
		}

		else if (keyCode == KeyEvent.KEYCODE_BACK) {
			try {
				if (this.getWindow().getDecorView().getRootView().findFocus().getId() == title_group
						.getCheckedRadioButtonId()) {

					// return super.onKeyDown(keyCode, event);

					ITVPortingMgr.getInstance().getUtilsCtrl().enableHotKey(true);
					ITVPortingMgr.getInstance().getUtilsCtrl().enableHomeKey(true);

					Intent startMain = new Intent(Intent.ACTION_MAIN);
					startMain.addCategory(Intent.CATEGORY_HOME);
					startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(startMain);
					ITVPortingMgr.getInstance().getSysCtrl().stopITV();
					// finish();
					// System.exit(0);
					return true;
				} else {
					if (fragments != null && !fragments.isEmpty()) {
						((BaseFragment) fragments.get(i)).getScrollView().smoothScrollTo(0, 0);
					}
					findViewById(title_group.getCheckedRadioButtonId()).requestFocus();
					return true;
				}
			} catch (Exception e) {
				Log.e("AndroidRunTime", e.toString());
				return super.onKeyDown(keyCode, event);
			}
		} else if (keyCode == Constant.TCL_SEARCH) {
			com.wasu.vod.utils.Utils.gotoSearchPage(this, Constant.ALL);
			return true;
		} else if (keyCode == Constant.TCL_REPLAY) {
			com.wasu.vod.utils.Utils.gotoReplayPage(this);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
//			int curid = this.getWindow().getDecorView().getRootView().findFocus().getId();
//			int radio_number = title_group.getChildCount();
//			int titleid = title_group.getChildAt(radio_number - 1).getId();
//			// 当前为最后一个Radio button
//			if (curid == titleid) {
//				ll_search.requestFocus();
//				return true;
//			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
//			int curid = this.getWindow().getDecorView().getRootView().findFocus().getId();
//			int serachid = ll_search.getId();
//			if (curid == serachid) {
//				int radio_number = title_group.getChildCount();
//				// 上移到最后一个Radio button
//				title_group.getChildAt(radio_number - 1).requestFocus();
//				return true;
//			}
		}else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			// 第一次点击右键，滚动页面覆盖导航栏区域，并隐藏导航栏
			if (title_group.getVisibility() == View.VISIBLE) {
				final SmoothHorizontalScrollView scrollView = fragments.get(i).getScrollView();
				AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
				alphaAnimation.setDuration(50);
				alphaAnimation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						title_group.setVisibility(View.INVISIBLE);
					}
				});
				title_group.startAnimation(alphaAnimation);
				if (scrollView != null) {
					scrollView.smoothScrollTo(getResources().getDimensionPixelSize(R.dimen.sm_69), 0);
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void replaceFragment(int old, int nev) {
		if (nev < title_group.getChildCount()) {
			updatafragmentisdelay = false;
			RadioButton rb1 = (RadioButton) title_group.getChildAt(nev);
			rb1.requestFocus();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 100) {
			
			isActiveing = false;
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == UPDATAFRAGMENT) {
			refreshFragment(msg.arg1, msg.arg2);
		}
		if (msg.what == ALTERNETWORKDIALOG) {
			alterNetworkDialog(msg.arg1);
		}
		return true;
	}

	/**
	 * 自升级
	 */
	private void updateapk() {
		Log.d(TAG, "MainActivity onCreate  ++Intent++   UpgradeService");

		childHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				final ArrayList<Apkinfo> apklist = PluginManager
						.getPluginManager(MainActivity.this.getApplicationContext()).getPluginList();
				mMainHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Intent service = new Intent(MainActivity.this, UpgradeService.class);
						service.putParcelableArrayListExtra("apklist", apklist);// 需要升级的apk
																				// 及
																				// 包名
						MainActivity.this.startService(service);

					}

				}, 200L);
			}

		}, 100L);

		/*
		 * Apkinfo apkinfo = new Apkinfo();
		 * 
		 * String apkName = "Launcher";// 应用名称 SDK_OTT , Launcher String
		 * packageName = this.getPackageName();// 应用 包全名称 //
		 * 如：{com.wasu.sdk_ott,com.wasu.launcher} apkinfo.setApkName(apkName);
		 * apkinfo.setPackageName(packageName);
		 * 
		 * apklist.add(apkinfo);
		 */

	}

	public void sendMessage(String[] msg) {
		Intent i = new Intent("action_wasu_data_collection");
		i.putExtra("msg", msg);
		i.putExtra("owner", "wasu_bootup_info");
		sendBroadcast(i);
	}

	public final static int MAGICNUMBER = 5;
	public final static int MAGIC_KEYS[] = { KeyEvent.KEYCODE_9, KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_3,
			KeyEvent.KEYCODE_7, KeyEvent.KEYCODE_1 };
	public static int[] magickeys = new int[MAGICNUMBER];

	/**
	 * 
	 * magic按键处理,按96371清除激活信息
	 * 
	 * @param keyCode
	 */
	public void MagicKeyProcess(int keyCode) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_0:
		case KeyEvent.KEYCODE_1:
		case KeyEvent.KEYCODE_2:
		case KeyEvent.KEYCODE_3:
		case KeyEvent.KEYCODE_4:
		case KeyEvent.KEYCODE_5:
		case KeyEvent.KEYCODE_6:
		case KeyEvent.KEYCODE_7:
		case KeyEvent.KEYCODE_8:
		case KeyEvent.KEYCODE_9:

			for (int i = 0; i < (MAGICNUMBER - 1); i++) {
				magickeys[i] = magickeys[i + 1];
			}
			magickeys[MAGICNUMBER - 1] = keyCode;

			int i = 0;
			for (i = 0; i < MAGICNUMBER; i++) {
				if (magickeys[i] != MAGIC_KEYS[i]) {
					break;
				}
			}
			if (i == MAGICNUMBER) {
				// 得到按键和MAGIC_KEYS[MAGICNUMBER]一致
				Log.d("yingjh", "get the magic key");
				WasuNetworkOpeManager.getInstance(this.getApplicationContext()).clearAuthInfor();
				Toast.makeText(this, "激活信息清除成功", Toast.LENGTH_SHORT).show();
				// clearAuthInfor();
			}
			break;
		}
	}
	private ScrollViewListener mScrollViewListener = new ScrollViewListener() {

		@Override
		public void onScrollChanged(int x, int y, int oldx, int oldy) {
//			 Log.d("chenchen", "onScrollChanged ** x=" + x + ", y=" + y +
//			 ", oldx=" + oldx + ", oldy=" + oldy);
			// 根据页面滚动的距离来计算阴影的宽度和左边距
			if (im_shadow_bg != null) {
				params = new LayoutParams(Utils.getDeviceWidth(MainActivity.this) - (int) (getResources().getDimension(R.dimen.sm_100)) + x, LayoutParams.MATCH_PARENT);
				params.leftMargin = (int) (getResources().getDimension(R.dimen.sm_100) - x);
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.topMargin = (int) getResources().getDimension(R.dimen.sm_35);
				im_shadow_bg.setLayoutParams(params);
			}
			
			//视频窗口移动
			if (fragments != null) {
				fragments.get(i).scrollVideoView();
			}

			// 当滚动到x=0位置时，显示左侧导航栏
			if (x == 0 && title_group.getVisibility() != View.VISIBLE) {
				View tabView = fragments.get(i).getTabView();
				if (tabView != null) {
					tabView.requestFocus();
				}
				AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
				alphaAnimation.setDuration(50);
				alphaAnimation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
						title_group.setVisibility(View.VISIBLE);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						
					}
				});
				title_group.startAnimation(alphaAnimation);
			}
		}

		@Override
		public void doScroll() {
			// 滚动回调
		}

		@Override
		public void onScrollEnd() {
			// 滚动到底回调
		}

		@Override
		public void onScrollStateChanged(SmoothHorizontalScrollView view,
				int scrollState) {
			// TODO Auto-generated method stub
//			Log.d("chenchen", "onScrollStateChanged  scrollState" + ((scrollState == ScrollViewListener.SCROLL_STATE_SCROLL) ? "SCROLL_STATE_SCROLL" : "SCROLL_STATE_IDLE"));
		}
	};
	
	//底部阴影需在首页内容之后显示
	public void showShadowBg() {
		if (im_shadow_bg != null) {
			im_shadow_bg.setVisibility(View.VISIBLE);
		}
	}

	// 进入动画
	private void doAnimation() {
		Animation animation;
		int tagSize = title_group.getChildCount();
		for (int i = 0; i < tagSize; i++) {
			animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.left_outside_in);
			animation.setStartOffset(i * 100);
			animation.setInterpolator(new AccelerateInterpolator());
			if (i == (tagSize - 1)) {
				animation.setAnimationListener(new AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						// TODO Auto-generated method stub
						//进入动画完成后默认第一个标签获焦
						title_group.getChildAt(0).requestFocus();
					}
				});
			}
			
			title_group.getChildAt(i).startAnimation(animation);
		}
	}
	
	private OnKeyListener mKeyListener = new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
					if (fragments != null) {
						fragments.get(i).getScrollView().smoothScrollTo(0, 0);
						Log.d("chenchen", "OnKeyListener");
					}
				}
			}
			return false;
		}
	};
}
