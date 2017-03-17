package com.wasu.launcher.application;

import java.io.File;
import java.io.IOException;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.callback.InitResultCallback;
import com.aliyun.alink.business.alink.ALinkConfigure;
import com.aliyun.alink.business.login.TaoLoginBusiness;
import com.aliyun.alink.sdk.net.anet.wsf.WSFConfigure;
import com.aliyun.alink.sdk.net.anet.wsf.WSFNet;
import com.aliyun.alink.utils.ALog;
import com.enrich.evmtv.evmtvManager;
import com.tcl.matrix.porting.apis.ITVPortingMgr;
//import com.wasu.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.wasu.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.wasu.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.wasu.nostra13.universalimageloader.core.ImageLoader;
import com.wasu.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.wasu.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.wasu.nostra13.universalimageloader.utils.StorageUtils;
import com.wasu.sdk_ott.UILApplication;
import com.wasu.vod.application.MyVolley;
import com.wasu.vod.utils.SwitchUtils;
import com.wasu.wisdomfamily.entity.CustomLoginBusness;
import com.enrich.evmtv.evmtvManager;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

public final class MyApplication extends UILApplication {

	private final static String TAG = "MyApplication";

	private File cacheDir;

	public static class Config {
		public static final boolean DEVELOPER_MODE = false;// 策略监控 true开启
															// false关闭
		public static final boolean isSupportMusic = true;
	}

	public static boolean isHaveAuth = false;

	public static void setIsAuth(boolean isAuth) {
		isHaveAuth = isAuth;
	}

	public static boolean getIsAuth() {

		return isHaveAuth;
	}

	@Override
	public void onCreate() {
		if (isMainProcess(this, android.os.Process.myPid())) {

			// ITVPortingMgr.getInstance(getApplicationContext());
			// ITVPortingMgr.getInstance().getUtilsCtrl().enableHomeKey(false);
			init();
		}

		super.onCreate();
		// evmtvManager.init(getApplicationContext());

		if (isMainProcess(this, android.os.Process.myPid())) {
			if (SwitchUtils.isSmartHomeOn(this)) {
				this.initTaeSDK();
				this.initAlinkSDK();
				ALog.setLevel(ALog.LEVEL_DEBUG);
			}
		}

	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	//	evmtvManager.getInstance().deInit();
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	protected void init() {

		if (Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(
					new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().penaltyDeath().build());

		}
		// bug捕获和bug信息收集
		// CrashHandler crashHandler = CrashHandler.getInstance();
		// crashHandler.init(this);
		MyVolley.init(this);// 初始化MyVolley

		/** vod start **/
		// initImageLoader1(this);
		/** vod end **/

		// evmtvManager.init(getApplicationContext());

	}

	/** vod start **/
	public void initImageLoader1(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		if (cacheDir == null)
			cacheDir = StorageUtils.getOwnCacheDirectory(context.getApplicationContext(), "UniversalImageLoader/Cache");

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				// 线程优先级
				.threadPoolSize(3)
				// 线程3个
				.denyCacheImageMultipleSizesInMemory()
				// 当你显示一个图像在一个小的ImageView后来你试图显示这个图像（从相同的URI）在一个大的，大尺寸的ImageView解码图像将被缓存在内存中为先前解码图像的小尺寸。
				.memoryCache(new UsingFreqLimitedMemoryCache(4 * 1024 * 1024))
				// 设置内存缓存大小
				// .discCache(new UnlimitedDisoncCache(cacheDir))
				// 硬盘缓存目录
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 生产缓存的名称
				.tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs() // Remove
																					// for
																					// release
																					// app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	/** vod end **/

	// 百川SDK相关
	private String certName = "cacert.pem";// 放在assets 目录下的SSL 证书名
	private String alinkKey = "3OQS1YZpBmk9KDSYULRT";// alink 申请的key
	private String alinkSecret = "bySWF8tKhIBsZUY4go0qLsqFx0yBTgekTS0VarnE";// alink
																			// 申请的secret

	private void initTaeSDK() {
		AlibabaSDK.turnOnDebug();
		AlibabaSDK.asyncInit(this, new InitResultCallback() {

			@Override
			public void onSuccess() {

				Log.d("yingjh", "initTaeSDK onSuccess");
				// System.out.println("hjq---初始化成功");
			}

			@Override
			public void onFailure(int code, String message) {

				// System.out.println("hjq---code:"+code);
				// System.out.println("hjq---message:"+message);
				Log.d("yingjh", "initTaeSDK onFailure");
			}

		});
	}

	// Alink SDK 相关

	private void initAlinkSDK() {
		// 配置Log输出
		ALog.setLevel(ALog.LEVEL_DEBUG);

		// 长连接配置及初始化
		WSFConfigure.wsfHostAddress = "alink.tcp.aliyun.com:443";
		WSFConfigure.wsfTempWorkPath = this.getFilesDir().getPath() + "/temp";
		try {
			WSFConfigure.wsfCertificationFile = this.getAssets().open(this.certName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		WSFNet.init(getApplicationContext());
		// System.out.println("hjq---WSFNet初始化成功");
		Log.d("yingjh", "initAlinkSDK ok");
		// Alink 业务配置
		ALinkConfigure.alinkKey = this.alinkKey;
		ALinkConfigure.alinkSecurity = this.alinkSecret;
		ALinkConfigure.appVersion = "1.0.0";// app 版本
		ALinkConfigure.alinkHost = "com.wasu.launcher"; // 应用包名

		// 初始化登录业务模块
		TaoLoginBusiness.init(new CustomLoginBusness());

	}
}
