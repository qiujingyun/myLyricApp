package com.wasu.launcher.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.evmtv.jni.EvmPortingNative;
import com.enrich.evmtv.evmtvManager;
import com.tcl.matrix.itv.contracts.ITVProperties;
import com.tcl.matrix.porting.apis.ISysEventListener;
import com.tcl.matrix.porting.apis.ITVPortingMgr;
import com.wasu.launcher.BaseActivity;
import com.wasu.launcher.MainActivity;
import com.wasu.launcher.NewbieGuideActivity;
import com.wasu.launcher.R;
import com.wasu.launcher.utils.PlayUtils;
import com.wasu.launcher.utils.SwitchUtils;
import com.wasu.sdk_ott.UILApplication;
import com.wasu.sdk_ott.upgrade.UpgradeActivity;
import com.wasu.utils.ViewUtils;
import com.wasu.vod.aidl.SDKOperationManager;
import com.wasu.vod.aidl.SDKOperationManager.BindService;
import com.wasu.vod.aidl.SDKOperationManager.MyListener;
import com.wasu.vod.domain.RecommendResponseResult;
import com.wasu.vod.domain.RequestAsyncTaskLoader;
import com.wasu.vod.domain.WasuContent;
import com.wasu.vod.domain.WasuInfo;
import com.wasu.vod.utils.Constant;
import com.wasu.vod.utils.FileUtils;
import com.wasu.vod.utils.LruCacheUtils;
import com.wasu.vod.utils.Utils;
import com.wasu.vod.utils.WasuUtils;
import com.evm.evmtv.tvbox.CallBackInterface;
import com.evm.evmtv.tvbox.TvBoxManage;

public class SplashActivity extends BaseActivity implements BindService, LoaderManager.LoaderCallbacks<Object>, Callback {

	private final long maxTime =  6 * 1000;

	private SDKOperationManager sm;
	private MyListener ml;

	private LruCacheUtils lruCacheUtils;
	private ImageView iv_adv;
	private Handler mThreadHandler;
	private Handler handler;
	
	private int REQUEST_DO_ACTIVE = 100;
	
	private static  SharedPreferences sp;
	
	static ISysEventListener iSysEventListen;
	TvBoxManage tvb;
	
	static class ISysEventListenerImp implements ISysEventListener {

		@Override
		public void onITVStop(String mode) {
			// TODO Auto-generated method stub
			if (mode == ITVProperties.ITV_TYPE_DTV) {
				Log.i("TCL_MTK5507", "onITVStop begin mode = " + mode);
///				ITVPortingMgr.getInstance().getUtilsCtrl().enableHotKey(true);
 //               ITVPortingMgr.getInstance().getUtilsCtrl().enableHomeKey(true);
//				EvmPortingNative.stopTV();
				Log.i("xiexiegang", "stopITV");
				//ITVPortingMgr.getInstance().getSysCtrl().stopITV();
				Log.i("xiexiegang", "removeSysEventListener");
//				ITVPortingMgr.getInstance().getSysCtrl().removeSysEventListener(iSysEventListen);
				
				if (UpgradeActivity.isEnterUpgrade) {
					UpgradeActivity.isEnterUpgrade = false;
				} else {

				    Log.i("xiexiegang", "killProcess1");				
				    Log.i("xiexiegang", "Launcher.pid:" + android.os.Process.myPid());
				    android.os.Process.killProcess(android.os.Process.myPid());
				    System.exit(0);
				}
			}
		}

		@Override
		public void onITVStart(String mode) {
			Log.i("TCL_MTK5507", "onITVStart begin mode = " + mode);
			if (mode == ITVProperties.ITV_TYPE_DTV) {
				Log.i("TCL_MTK5507", "begin to init");
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (!sp.getBoolean("unzip", false)) {
							boolean result = false;
							try {
								long startTime = System.currentTimeMillis();
								unZip(UILApplication.getApplication(), "data",
										UILApplication.getApplication().getApplicationInfo().dataDir, true);
								Log.d("xiexiegang", "time:" + (System.currentTimeMillis() - startTime));
								result = true;
							} catch (IOException e) {
								e.printStackTrace();
							}
							sp.edit().putBoolean("unzip", result).commit();
						}
						EvmPortingNative.startTV(UILApplication.getApplication().getApplicationInfo().dataDir);
					}
				}, "evmtv").start();

			}
		}

		@Override
		public void onITVResume(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onITVPause(String arg0) {
			// TODO Auto-generated method stub

		}

	}
	
	
	private static  void unZip(Context context, String assetName, String outputDirectory, boolean isReWrite) throws IOException {

		File file = new File(outputDirectory);

		if (!file.exists()) {
			file.mkdirs();
		}
		InputStream inputStream = context.getAssets().open(assetName);
		ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		ZipEntry zipEntry = zipInputStream.getNextEntry();

		byte[] buffer = new byte[1024 * 1024];

		int count = 0;
		while (zipEntry != null) {
			if (zipEntry.isDirectory()) {
				file = new File(outputDirectory + File.separator + zipEntry.getName());
				if (isReWrite || !file.exists()) {
					file.mkdir();
				}
			} else {
				file = new File(outputDirectory + File.separator + zipEntry.getName());
				if (isReWrite || !file.exists()) {
					if(!file.getParentFile().exists())
						file.getParentFile().mkdirs();
					file.createNewFile();
					FileOutputStream fileOutputStream = new FileOutputStream(file);
					while ((count = zipInputStream.read(buffer)) > 0) {
						fileOutputStream.write(buffer, 0, count);
					}
					fileOutputStream.close();
				}
			}
			zipEntry = zipInputStream.getNextEntry();
		}
		zipInputStream.close();
	}


	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.lau_splash);
		sp = this.getSharedPreferences("wasu", Activity.MODE_PRIVATE);
		initView();
		 iSysEventListen = new ISysEventListenerImp();
//		ITVPortingMgr.getInstance(getApplicationContext());
//		ITVPortingMgr.getInstance().getUtilsCtrl().enableHomeKey(false);
		//long stsrtTime = System.currentTimeMillis();
//		Set<Integer> hotkeyList = new HashSet<Integer>();
//		hotkeyList.add(KeyEvent.KEYCODE_TV);//互动电视键处理
//		ITVPortingMgr.getInstance().getUtilsCtrl().setHotKeyList(hotkeyList);
//		ITVPortingMgr.getInstance().getUtilsCtrl().enableHotKey(false);

//		ITVPortingMgr.getInstance().getSysCtrl().addSysEventListener(iSysEventListen);
//		ITVPortingMgr.getInstance().getSysCtrl().startITV(ITVProperties.ITV_TYPE_DTV);
//		ITVPortingMgr.getInstance().getVideoCtrl().setNLEnable(false);
//	    evmtvManager.init(getApplicationContext());

		PlayUtils.getInstance();
		
		if (SwitchUtils.isFreeAuthSwitchOn(this)) {
			
			initData();
			
		    handler.postDelayed(new Runnable() {

		    	@Override
			    public void run() {
				    loadMainUI();
			    }
		    }, maxTime);
		    
		} else {
			
			Cursor localCursor = null;
			try {

				localCursor = getContentResolver()
						.query(Uri.parse("content://com.wasu.live.provider/wasu_auth_info_path"),
								null, null, null, null);
				if ((localCursor != null) && (localCursor.moveToFirst())) {
					
					String str1 = localCursor.getString(localCursor
							.getColumnIndexOrThrow("Key"));
					String str2 = localCursor.getString(localCursor
							.getColumnIndexOrThrow("UserId"));
					String str3 = localCursor.getString(localCursor
							.getColumnIndexOrThrow("UserProfile"));
					String str4 = localCursor.getString(localCursor
							.getColumnIndexOrThrow("Region"));
					String str5 = localCursor.getString(localCursor
							.getColumnIndexOrThrow("TerminalId"));
					String str6 = localCursor.getString(localCursor
							.getColumnIndexOrThrow("AuthToken"));
					String str7 = localCursor.getString(localCursor
							.getColumnIndexOrThrow("EncryptString"));
					Log.d("test", "liqxtestactive:Key: " + str1);
					Log.d("test", "liqxtestactive:UserId: " + str2);
					Log.d("test", "liqxtestactive:UserProfile: " + str3);
					Log.d("test", "liqxtestactive:Region: " + str4);
					Log.d("test", "liqxtestactive:TerminalId: " + str5);
					Log.d("test", "liqxtestactive:AuthToken: " + str6);
					Log.d("test", "liqxtestactive:EncryptString: " + str7);
					
					if ((!TextUtils.isEmpty(str2)) && (!TextUtils.isEmpty(str1))) {// already  active
						
						Intent intent = new Intent(this, NewbieGuideActivity.class);
						intent.putExtra("isHaveActivie", true);
						this.startActivityForResult(intent, REQUEST_DO_ACTIVE, null);

/*						initData();

						handler.postDelayed(new Runnable() {

							@Override
							public void run() {
								loadMainUI();
							}
						}, maxTime);*/

					} else {
						
						Intent intent = new Intent(this, NewbieGuideActivity.class);
						intent.putExtra("isHaveActivie", false);
						this.startActivityForResult(intent, REQUEST_DO_ACTIVE, null);
					}
					

				}
				
			} catch (Exception ee) {
				
				Log.e("liqxtest", ee.getLocalizedMessage());

			} finally {
				if (localCursor != null) {
					localCursor.close();
				}
			}

		}
		tvbox(this);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		if (requestCode == REQUEST_DO_ACTIVE) {
			
			if (resultCode == Activity.RESULT_OK) {
				
				// do active successfully
				// go to launcher
				initData();
				
			    handler.postDelayed(new Runnable() {

			    	@Override
				    public void run() {
					    loadMainUI();
				    }
			    }, maxTime);
			    
			} else {
				
				//do active failed
				Toast.makeText(this.getApplicationContext(), "激活失败，无法进入lancher应用", Toast.LENGTH_LONG).show();
	//			ITVPortingMgr.getInstance().getUtilsCtrl().enableHomeKey(true);
				Set<Integer> hotkeyList = new HashSet<Integer>();
				hotkeyList.add(KeyEvent.KEYCODE_TV);//互动电视键处理
//				ITVPortingMgr.getInstance().getUtilsCtrl().setHotKeyList(hotkeyList);
//				ITVPortingMgr.getInstance().getUtilsCtrl().enableHotKey(true);
				
				Intent startMain = new Intent(Intent.ACTION_MAIN);
				startMain.addCategory(Intent.CATEGORY_HOME);
				startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(startMain);
				
//				ITVPortingMgr.getInstance().getSysCtrl().stopITV();
				
				/*initData();
				
			    handler.postDelayed(new Runnable() {

			    	@Override
				    public void run() {
					    loadMainUI();
				    }
			    }, maxTime);*/
				
			}
		} else {
		    super.onActivityResult(requestCode, resultCode, data);
		}
	}


	@SuppressLint("NewApi")
	@Override
	protected void onDestroy() {

		super.onDestroy();
		if (handlerThread != null) {
			if (Build.VERSION.SDK_INT >= 18) {
				handlerThread.quitSafely();
			} else {
				handlerThread.quit();
			}
		}
		if (sm != null)
			sm.unRegisterSDKOperationListener();
		
		ViewUtils.unbindDrawables(this.findViewById(R.id.rl_splash));
		
	}

	@Override
	protected void initView() {

		loadViewLayout();
		findViewById();
		setListener();
	}

	private void initData() {

		sm = new SDKOperationManager(context);
		sm.registerSDKOperationListener(this);
		// PlayUtils.getInstance();
	}

	@Override
	protected void onResume() {

		super.onResume();
		if (lruCacheUtils != null)
			lruCacheUtils.clearAllImageCache();
	}
	
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == KeyEvent.ACTION_UP) {

		} else {
			int keyCode = event.getKeyCode();
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				return true;
			case KeyEvent.KEYCODE_HOME:
				return true;
			default:
				break;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	HandlerThread handlerThread;

	@Override
	protected void loadViewLayout() {

		handlerThread = new HandlerThread("adv");
		handlerThread.start();
		mThreadHandler = new Handler(handlerThread.getLooper(), this);
		mThreadHandler.post(new Runnable() {

			@Override
			public void run() {
				
				mThreadHandler.sendMessage(mThreadHandler.obtainMessage(45));
				if (!sp.getBoolean("unzip", false)) {
					boolean result = false;
					try {
						long startTime = System.currentTimeMillis();
						FileUtils.unZip(context, "data", getApplicationInfo().dataDir, true);
						Log.d("xiexiegang", "time:" + (System.currentTimeMillis() - startTime));
						result = true;
					} catch (IOException e) {
						e.printStackTrace();
					}
					sp.edit().putBoolean("unzip", result).commit();
				}

			}
		});
		handler = new Handler(this);
		lruCacheUtils = LruCacheUtils.getInstance();
	}

	@Override
	protected void findViewById() {

		iv_adv = (ImageView) findViewById(R.id.iv_adv);
	}

	@Override
	protected void setListener() {

	}

	private void loadMainUI() {
		
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		//openActivity(MainActivity.class);
		finish();// 把当前的activity从任务栈里面移除
	}

	@Override
	public void onConnnected(MyListener listener) {

		this.ml = listener;
		try {
			com.wasu.launcher.utils.Utils.TVID = ml.wasu_bsa_getTvid();
		} catch (RemoteException e) {
			System.err.println(e);
		}
		List<String> codes = null;

		String[] liveAdv = new String[] { "DVBOTT_HD_70001000", "DVBOTT_HD_70001100", "DVBOTT_HD_70001200", "DVBOTT_HD_70001300" };
		codes = Arrays.asList(liveAdv);
		getLoaderManager().initLoader(Constant.QUERY_RECOMMEND, Utils.CreateRecommendContentBundle(codes), this);

		String[] playbackAdv = new String[] { "DVBOTT_HD_70002000", "DVBOTT_HD_70002100", "DVBOTT_HD_70002200", "DVBOTT_HD_70002300",
				"DVBOTT_HD_70002400", "DVBOTT_HD_70002500", "DVBOTT_HD_70002600", "DVBOTT_HD_70002700" };
		codes = Arrays.asList(playbackAdv);
		getLoaderManager().initLoader(Constant.QUERY_RECOMMEND + 1, Utils.CreateRecommendContentBundle(codes), this);

		String[] vodAdv = new String[] { "DVBOTT_HD_70003000", "DVBOTT_HD_70003100", "DVBOTT_HD_70003200", "DVBOTT_HD_70003300",
				"DVBOTT_HD_70003400", "DVBOTT_HD_70003500", "DVBOTT_HD_70003600", "DVBOTT_HD_70003700", "DVBOTT_HD_70003800" };
		codes = Arrays.asList(vodAdv);
		getLoaderManager().initLoader(Constant.QUERY_RECOMMEND + 2, Utils.CreateRecommendContentBundle(codes), this);

		String[] otherAdv = new String[] { "DVBOTT_HD_70004000", "DVBOTT_HD_70004100", "DVBOTT_HD_70004200", "DVBOTT_HD_30008200",
				"DVBOTT_HD_30010300" };
		codes = Arrays.asList(otherAdv);
		getLoaderManager().initLoader(Constant.QUERY_RECOMMEND + 3, Utils.CreateRecommendContentBundle(codes), this);

	}

	@Override
	public void onDisconnected(MyListener listener) {
		// TODO Auto-generated method stub
		ml = null;
	}

	@Override
	public Loader<Object> onCreateLoader(final int id, final Bundle bundle) {

		if (bundle != null) {
			RequestAsyncTaskLoader request = new RequestAsyncTaskLoader(context);

			request.setLoadInBackgroundListener(new RequestAsyncTaskLoader.LoadInBackgroundListener() {

				@Override
				public Object loadInBackgroundListener() {

					if (ml == null)
						return null;
					
					Object obj = WasuUtils.wasu_sdk_content_query(ml, id, bundle);
					 int type = WasuUtils.checkQueryContentType(id);
					 int index = id - type;
					 switch(index) {
					 
						case 3:
							
                           if (obj == null || ((RecommendResponseResult)obj).getContents() == null ||
                        		   ((RecommendResponseResult)obj).getContents().size()==0 ||
                           		((RecommendResponseResult)obj).getContents().get(0).getInfoList().size()==0) {
                           	
                           	    handler.post(new Runnable() {

									    @Override
								    	public void run() {
										// TODO Auto-generated method stub
									    	ViewGroup.LayoutParams lp = iv_adv.getLayoutParams();
									    	lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
									    	lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
									    	iv_adv.setLayoutParams(lp);
									    	iv_adv.setImageDrawable(UILApplication.getApplication().getResources().getDrawable(R.drawable.boot_adv));
								    	}
                           		
                               	});
                            }
							break;
						default :
							break;
					  }
					 
					 return obj;
				}
			});
			return request;
		} else {
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Object> loader, Object data) {

		if (loader == null)
			return;

		if (data != null) {
			int id = loader.getId();
			int type = WasuUtils.checkQueryContentType(id);
			int index = id - type;
			if (type == Constant.QUERY_RECOMMEND) {
				RecommendResponseResult result = (RecommendResponseResult) data;
				for (int i = 0; i < result.getCount(); i++) {
					if (i >= result.getContents().size())
						break;
					WasuContent content = result.getContents().get(i);
					if (content == null)
						continue;
					String advName = null;
					switch (index) {
					case 0:
						if (i < liveAdvNameArray.length)
							advName = liveAdvNameArray[i];
						break;
					case 1:
						if (i < playbackAdvNameArray.length)
							advName = playbackAdvNameArray[i];
						break;
					case 2:
						if (i < vodAdvNameArray.length)
							advName = vodAdvNameArray[i];
						break;
					case 3:
						if (i < otherAdvNameArray.length)
							advName = otherAdvNameArray[i];
						break;
					}

					if (advName != null) {
						for (WasuInfo info : content.getInfoList()) {
							if (info.getImg() != null) {
								Bundle bundle = new Bundle();
								bundle.putString("name", advName);
								bundle.putString("url", info.getImg());
								mThreadHandler.sendMessage(mThreadHandler.obtainMessage(WindowMessageID.LOADING, bundle));
								break;
							}
						}
					}
				}

			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Object> loader) {
		// TODO Auto-generated method stub

	}

	private String[] liveAdvNameArray = new String[] { "live_pgBarAdv", "live_volumeAdv", "live_timeshiftAdv", "live_rewindAdv" };

	private String[] playbackAdvNameArray = new String[] { "playback_videoAdv", "playback_loadingAdv", "playback_enterAdv",
			"playback_pauseAdv", "playback_volumeAdv", "playback_timeshiftAdv", "playback_rewindAdv", "playback_exitAdv" };

	private String[] vodAdvNameArray = new String[] { "vod_videoAdv", "vod_loadingAdv", "vod_enterAdv", "vod_rewindAdv", "vod_volumeAdv",
			"vod_pauseAdv", "vod_timeshiftAdv", "vod_exitAdv", "vod_continueAdv" };

	private String[] otherAdvNameArray = new String[] { "bootAdv", "listAdv", "playbackDetailsAdv", "minorAdv", "musicAdv" };

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case WindowMessageID.LOADING:
			if (msg.obj != null) {
				Bundle bundle = (Bundle) msg.obj;
				final String url = bundle.getString("url");
				String name = bundle.getString("name");
				if (url != null && name != null) {
					Bitmap bitmap = Utils.getBitmapFromHttp(url);
					if (bitmap != null) {
						Bitmap bmp = lruCacheUtils.getBitmapFromMemCache(name);
						if ("bootAdv".equals(name)) {
							
							try {
								if (ml != null) {
									ml.getImageFromSDK(url);
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Log.e("liqxtest","lqixtest:::network success cant not get bootup adv "+ e.getLocalizedMessage());
							}
							
						}
						if (bmp == null) {
							if ("bootAdv".equals(name)) {
								handler.sendMessage(handler.obtainMessage(WindowMessageID.UPDATE_ADV, bitmap));
							} else {
								lruCacheUtils.addBitmapToMemoryCache(name, bitmap);
								Log.d("xiexiegang", "name:" + name + ",bitmap:" + lruCacheUtils.getBitmapFromMemCache(name));
							}
						}
					} else {
						
						Bitmap bt = null;
						if ("bootAdv".equals(name)) {

							try {
								if (ml != null) {
									bt = ml.getImageFromSDK(url);
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Log.e("liqxtest","lqixtest:::cant not get bootup adv "+ e.getLocalizedMessage());
							}

							final Bitmap temp = bt;
							handler.post(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									ViewGroup.LayoutParams lp = iv_adv
											.getLayoutParams();
									lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
									lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
									iv_adv.setLayoutParams(lp);

									if (temp != null) {
										iv_adv.setImageBitmap(temp);
									} else {
										iv_adv.setImageDrawable(UILApplication.getApplication().getResources().getDrawable(R.drawable.boot_adv));
									}
								}

							});

						}
					}
				}
			}
			break;
		case WindowMessageID.LOADING_COMPLETE:
		case WindowMessageID.LOADING_TIMEOUT:
			loadMainUI();
			break;
		case WindowMessageID.UPDATE_ADV: {
			if (msg.obj != null) {
				ViewGroup.LayoutParams lp = iv_adv.getLayoutParams();
				lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
				lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
				iv_adv.setLayoutParams(lp);
				iv_adv.setImageBitmap((Bitmap) msg.obj);
			}
		}
			break;
		case 45:
		
			StringBuffer sb = new StringBuffer();

			// Time
			String time = com.wasu.launcher.utils.Utils.getWasuTime();
			if (!TextUtils.isEmpty(time)) {
				sb.append(time);
				sb.append("|");
			}

			// UUID
			String uuid = com.wasu.launcher.utils.Utils.getTVID();
			if (!TextUtils.isEmpty(uuid)) {
				sb.append(uuid);
				sb.append("|");
			}
			sb.append("|");
			// IP
			String ip = com.wasu.launcher.utils.Utils.getIP();
			if (!TextUtils.isEmpty(ip)) {
				sb.append(ip);
				sb.append("|");
			}

			String device_model = Build.MODEL; // 设备型号
			if (!TextUtils.isEmpty(device_model)) {
				sb.append(device_model);
				sb.append("|");
			}

			int version_sdk = Build.VERSION.SDK_INT; // 设备SDK版本//暂时代替固件版本
			if (!TextUtils.isEmpty(version_sdk + "")) {
				sb.append(version_sdk);
				sb.append("|");
			}

			String version_release = Build.VERSION.RELEASE; // 设备的系统版本
			if (!TextUtils.isEmpty(version_release)) {
				sb.append(version_release);
				sb.append("|");
				sb.append(com.wasu.launcher.utils.Utils.getDeviceId(getApplicationContext()));
				sb.append("|");
			}
			sb.append("|");
			sb.append("|");
			sb.append("|");

			// 去掉最后一个 |
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
				String[] msgs = { sb.toString() };
				com.wasu.launcher.utils.Utils.sendMessage(this, msgs, "wasu_bootup_info");
			} else {
				// Toast.makeText(this, "怎么都是空的？", Toast.LENGTH_SHORT).show();
			}
		

			// String[] msgs = { ca, uuid };
			// String strings = setStringsOfString(msgs);
			// Toast.makeText(this, strings, Toast.LENGTH_SHORT).show();
			// String[] msg11 = { strings };
			// sendMessage(msg11)
		
			break;
		default:
			break;
		}

		return false;
	}

	String setStringsOfString(String[] msg) {
		String time = "";
		if (msg != null && msg.length != 0) {
			for (int i = 0; i < msg.length - 1; i++) {
				if (!TextUtils.isEmpty(msg[i])) {
					time += msg[i] + "|";
				}
			}
			time += msg[msg.length - 1];
		}
		return time;
	}
	private void tvbox(Context context) {
		SharedPreferences sp = getSharedPreferences("userinfo",
				Context.MODE_APPEND);
		String sn=sp.getString("sn", null);
		 tvb = new TvBoxManage(context);
		//tvb.StartKeyMonitoring(sn);
		 Log.d("yingjh", "tvbox this");
		 tvb.StartTvBoxManageService();
         tvb.setTvBoxListener(new CallBackInterface() {
			@Override
			public void GetUUID(String uuid) {

				System.out.println("---hjq---wasutv----uuid:" + uuid);
				SharedPreferences sp = getSharedPreferences("uuidinfo",
						Context.MODE_APPEND);
				Editor ed = sp.edit();
				ed.putString("uuid", uuid);
				ed.commit();
			}

			@Override
			public void GetSN(String arg0) {
				// TODO Auto-generated method stub
				
			}
		});     
        

	} 

	private class WindowMessageID {

		/**
		 * @brief 刷新数据�?		 */
		public static final int LOADING = 0x000000004;

		public static final int LOADING_COMPLETE = 0x000000005;

		public static final int LOADING_TIMEOUT = 0x000000006;
		public static final int UPDATE_ADV = 0x000000007;
	}

}
