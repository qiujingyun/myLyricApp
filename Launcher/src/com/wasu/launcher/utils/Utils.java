package com.wasu.launcher.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.ngb.broadcast.dvb.si.SIDatabase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.wasu.launcher.domain.RankListRequestBody;
import com.wasu.launcher.domain.RankListRequestHead;
import com.wasu.launcher.domain.RecommendRequestBody;
import com.wasu.vod.aidl.SDKOperationManager;
import com.wasu.vod.aidl.SDKOperationManager.BindService;
import com.wasu.vod.aidl.SDKOperationManager.MyListener;

public class Utils {
	public static String getStringTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		Date date = new Date(System.currentTimeMillis());// 获取当前时间
		return formatter.format(date);
	}

	public static String getStringData() {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date date = new Date(System.currentTimeMillis());// 获取当前时间
		return formatter.format(date);
	}

	public static boolean getWasuTimeIn24(String time) {
		if (time == null)
			return false;
		boolean ret = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			Date date = sdf.parse(time);
			long currentTime = System.currentTimeMillis();
			if (currentTime > date.getTime() + 24 * 60 * 60 * 1000) {
				ret = false;
			} else {
				ret = true;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	public static long getLongTimeFromString(String time){
		long result = -1;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date data;
		try {
			data = formatter.parse(time);
			result = data.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	

	public static boolean isLeapYear() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);

		return (0 == year % 4 && ((year % 100 != 0) || (year % 400 == 0)));
	}

	public static String installApk(String pkgPath) {
		String[] args = { "pm", "install", "-r", pkgPath };
		String result = "";
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		Process process = null;
		InputStream errIs = null;
		InputStream inIs = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			process = processBuilder.start();
			errIs = process.getErrorStream();
			while ((read = errIs.read()) != -1) {
				baos.write(read);
			}
			baos.write('\n');
			inIs = process.getInputStream();
			while ((read = inIs.read()) != -1) {
				baos.write(read);
			}
			byte[] data = baos.toByteArray();
			result = new String(data);
		} catch (IOException e) {
			Log.d("xiexiegang", "IOException");
			e.printStackTrace();
		} catch (Exception e) {
			Log.d("xiexiegang", "Exception");
			e.printStackTrace();
		} finally {
			try {
				if (errIs != null) {
					errIs.close();
				}
				if (inIs != null) {
					inIs.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (process != null) {
				process.destroy();
			}
		}
		return result;
	}

	/**
	 * 2 * 请求ROOT权限后执行命令（最好开启一个线程） 3 * @param cmd (pm install -r *.apk) 4 * @return
	 * 5
	 */
	public static boolean installApkInRoot(File path, Context context) {
		String cmd = "pm install -r " + path + "\n";
		Process process = null;
		DataOutputStream os = null;
		BufferedReader br = null;
		StringBuilder sb = null;
		Log.d("xiexiegang", "cmd:" + cmd);
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));

			sb = new StringBuilder();
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp + "\n");
				Log.e("xiexiegang", "temp==" + temp);
				if ("Success".equalsIgnoreCase(temp)) {
					Log.e("xiexiegang", "----------" + sb.toString());
					return true;
				}
			}
			process.waitFor();
		} catch (Exception e) {
			Log.e("xiexiegang", "异常：" + e.getMessage());
		} finally {
			try {
				if (os != null) {
					os.flush();
					os.close();
				}
				if (br != null) {
					br.close();
				}
				if (process != null) {
					process.destroy();
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public static Bundle CreateFolderContentBundle(String folderCode) {
		Bundle bundle = new Bundle();
		bundle.putString("folderCode", folderCode);
		bundle.putInt("pageIndex", 1);
		bundle.putInt("pageItems", -1);
		bundle.putString("cImageMode", "685,478,JPG");
		bundle.putInt("segmentIndex", -1);

		return bundle;
	}
	
	/**
	 * 请求栏目下内容参数
	 * @param folderCode 栏目编码 必填
	 * @param segmentIndex 拆条 非必填  不填写-1 拆条序号，取第几个拆条
	 * @param cImageMode 图片大小及格式 非必填 不填写 null. 内容目标图片适配参数，以逗号分隔，依次为：目标图片宽度(大于零的整数)、目标图片高度(大于零的整数)、目标图片格式
	 * @return
	 */
	public static Bundle CreateFolderContentBundle(String folderCode, int segmentIndex, String cImageMode) {
		Bundle bundle = new Bundle();
		bundle.putString("folderCode", folderCode);
		bundle.putInt("pageIndex", 1);
		bundle.putInt("pageItems", -1);
		if (cImageMode != null) {
		    bundle.putString("cImageMode", cImageMode);
		} else {
			bundle.putString("cImageMode", "685,478,JPG");
		}
		if (segmentIndex != -1) {
			bundle.putInt("segmentIndex", segmentIndex);
		} else {
		    bundle.putInt("segmentIndex", -1);
		}

		return bundle;
	}

	public static Bundle CreateChildFolderContentBundle(String folderCode) {
		Bundle bundle = new Bundle();
		bundle.putString("folderCode", folderCode);
		bundle.putInt("depth", 1);
		bundle.putInt("pageIndex", 1);
		bundle.putInt("pageItems", -1);

		return bundle;
	}

	public static Bundle CreateSearchFilterContentBundle(Bundle extra, String keyword) {
		Bundle bundle = new Bundle();

		bundle.putString("mode", "1");/* 1-模糊搜索，2-精确搜索 */
		bundle.putString("word", null);
		bundle.putString("searchMode", "1");/* 1-根据中文或首字母搜索，2-根据T9搜索 3-根据笔画搜索 */
		bundle.putString("field", "name,director,actors,keyword");
		bundle.putString("contentType", "36,37,13,15,38,68,40");
		bundle.putString("programType", "36,37,15,81,16,83,85,82,84");

		if (extra != null) {
			bundle.putString("actors", extra.getString("actors", null));
			bundle.putString("director", extra.getString("director", null));
			bundle.putString("region", extra.getString("region", null));
			bundle.putString("year", extra.getString("year", null));
		} else {
			bundle.putString("actors", null);
			bundle.putString("director", null);
			bundle.putString("region", null);
			bundle.putString("year", null);
		}
		bundle.putString("keyword", keyword);
		bundle.putString("category", null);
		bundle.putString("typeLargeItem", null);
		bundle.putString("typeSecondItem", null);

		bundle.putString("folderId", null);
		bundle.putString("incorporateTime", null);
		bundle.putString("createTime", null);
		bundle.putString("sortBy", null);

		bundle.putString("pageIndex", null);
		bundle.putString("pageItems", null);
		bundle.putString("x_region", null);
		bundle.putString("x_codec", null);
		bundle.putString("x_hd_support", "ALL");
		bundle.putString("encodingprofile", "H264");

		bundle.putString("ishighlight", null);
		bundle.putInt("highlightSnippet", 50);
		bundle.putString("highlightfields", null);
		bundle.putString("highlightPrefix", null);
		bundle.putString("highlightSuffix", null);
		bundle.putString("broadcastChannel", null);

		return bundle;
	}

	/**
	 * Constant.java // 排行榜类型
	 * ----PROGRAMRANK,影视剧榜单；MUSICRANK,音乐榜单;LIVERANK，直播榜单; // 影视剧内容内型 //
	 * ----ALL,所有；SERIES，电视剧非少儿;KIDSERIES，电视剧少儿;PROGRAM，电影非少儿;KIDPROGRAM,电影少儿;
	 * MEDIANEWS，视频新闻；MEDIACOLUMN，视频栏目; // 音乐内容内型
	 * ----ALL,所有;NEW，新歌榜;CHINESE，华语榜；OCCIDENT,欧美榜;JAPANKOREA，日韩榜; // 排行类型
	 * ----DAY，天榜单；WEEK,周榜单;MONTH,月榜单;REAL,实时榜单;
	 */
	public static Bundle CreateRankListContentBundle(int type) {
		Bundle bundle = new Bundle();

		RankListRequestHead head = new RankListRequestHead("VOD", "PC", "JHSG7328f", null);
		String assetType;
		String command;
		String picType = null;
		if (type == Constant.MOVIE) {
			assetType = "PROGRAM";
			command = "PROGRAMRANK";
			picType = "1";
		} else if (type == Constant.SERIES) {
			assetType = "SERIES";
			command = "PROGRAMRANK";
			picType = "2";
		} else if (type == Constant.MINOR) {
			assetType = "KIDPROGRAM";
			command = "PROGRAMRANK";
		} else if (type == Constant.MUSIC) {
			assetType = "ALL";
			command = "MUSICRANK";
			picType = "2";
		} else if (type == Constant.LIVE) {
			assetType = null;
			command = "LIVERANK";
			picType = "2";
		} else {
			picType = "1";
			assetType = "ALL";
			command = "PROGRAMRANK";
		}

		RankListRequestBody body = new RankListRequestBody(command, "iptvtest", "hzvsite", "hzdq", assetType, "WEEK", "H264");
		body.setPictype(picType);
		body.setDetailindex(1);
		body.setNumber(20);

		try {
			JSONObject object = new JSONObject();
			object.put("head", head);
			object.put("body", body);
			bundle.putString("param", object.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bundle;
	}

	public static Bundle CreateRecommendContentBundle(List<String> codes) {
		Bundle bundle = new Bundle();

		RankListRequestHead head = new RankListRequestHead("atq", "stb", "JHSG7328f", null);
		String regioncode=com.wasu.utils.Utils.getWasuRegionCode();
		RecommendRequestBody body = new RecommendRequestBody(codes, "iptvtest", "hzvsite", regioncode, "H264");

		try {
			JSONObject object = new JSONObject();
			object.put("head", head);
			object.put("body", body);
			bundle.putString("param", object.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return bundle;
	}

	/**
	 * 获取节目单
	 * 
	 * @return
	 */
	public static SIDatabase getDefaultSIDatabase() {
		SIDatabase database = null;
		try {
			SIDatabase[] siDatabase = SIDatabase.getDatabase();
			if (siDatabase != null && siDatabase.length > 0) {
				database = siDatabase[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return database;
	}

	/**
	 * 获取到时间 格式"yyyy-MM-dd HH:mm:ss.SSS"
	 * 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getWasuTime() {
		long currentTime = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return sdf.format(currentTime);
	}

	/**
	 * 得到IP
	 * 
	 * @return
	 */
	public static String getIP() {
		String IP = null;
		StringBuilder IPStringBuilder = new StringBuilder();
		try {
			Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaceEnumeration.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
				Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
				while (inetAddressEnumeration.hasMoreElements()) {
					InetAddress inetAddress = inetAddressEnumeration.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
						IPStringBuilder.append(inetAddress.getHostAddress().toString() + "\n");
					}
				}
			}
		} catch (SocketException ex) {

		}

		IP = IPStringBuilder.toString();
		return IP;
	}

	private static String deviceId;

	/**
	 * 得到 设备 deviceId
	 * */
	public static String getDeviceId(Context context) {
		if (deviceId == null) {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String tmDevice, tmSerial, tmPhone, androidId;
			tmDevice = "" + tm.getDeviceId();
			tmSerial = "" + tm.getSimSerialNumber();
			androidId = ""
					+ android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

			UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());

			deviceId = deviceUuid.toString();

			// System.err.println(deviceId);
		}
		return deviceId;
	}

	public static String TVID = null;

	public static String getTVID() {
		if (TVID != null) {
			return TVID;
		} else {
			return "";
		}
	}

	public static void initTVID(Context context) {
		final SDKOperationManager sm = new SDKOperationManager(context);
		sm.registerSDKOperationListener(new BindService() {

			@Override
			public void onDisconnected(MyListener ml) {
				try {
					TVID = ml.wasu_bsa_getTvid();
				} catch (RemoteException e) {
					System.err.println(e);
				}
				sm.unRegisterSDKOperationListener();
			}

			@Override
			public void onConnnected(MyListener ml) {

			}
		});
	}

	/**
	 * 数据采集
	 * 
	 * @param contexts
	 * @param msg
	 *            数据
	 * @param owner
	 */
	public static void sendMessage(Context contexts, String[] msg, String owner) {
		Intent i = new Intent("action_wasu_data_collection");
		Log.d("sendMessage", "sjcj:  " + msg[0]);
		i.putExtra("msg", msg);
		i.putExtra("owner", owner);
		if(contexts != null){
			contexts.sendBroadcast(i);			
		}

	}

	// 获取mac
	public static String getLocalMacAddressFromIp() {
		String mac_s = "";
		try {
			byte[] mac;
			NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress.getByName(getLocalIpAddress()));
			mac = ne.getHardwareAddress();
			mac_s = byte2hex(mac);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mac_s;
	}

	// 将字节转成字符
	public static String byte2hex(byte[] b) {
		StringBuffer hs = new StringBuffer(b.length);
		String stmp = "";
		int len = b.length;
		for (int n = 0; n < len; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			if (stmp.length() == 1)
				hs = hs.append("0").append(stmp);
			else {
				hs = hs.append(stmp);
			}
		}
		return String.valueOf(hs);
	}

	// 获取IP
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						if (inetAddress.getAddress().length == 4) {
							return inetAddress.getHostAddress().toString();
						}
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}

		return null;
	}

	// 获取CPU序列号
	public static String getCPUSerial() {
		String str = "", strCPU = "", cpuAddress = "0000000000000000";
		try {
			// 读取CPU信息
			Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			// 查找CPU序列号
			for (int i = 1; i < 100; i++) {
				str = input.readLine();
				if (str != null) {
					// 查找到序列号所在行
					if (str.indexOf("Serial") > -1) {
						// 提取序列号
						strCPU = str.substring(str.indexOf(":") + 1, str.length());
						// 去空格
						cpuAddress = strCPU.trim();
						break;
					}
				} else {
					// 文件结尾
					break;
				}
			}
		} catch (Exception ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		return cpuAddress;
	}
	
	public static Bundle CreateLiveRankListContentBundle(int type,String code) {

		Bundle bundle = new Bundle();

		RankListRequestHead head = new RankListRequestHead("VOD", "PC", "JHSG7328f", null);
		String assetType;
		String command;
		String picType = null;
		
        if (type == Constant.LIVE) {
			assetType = null;
			command = "LIVERANK";
			picType = "2";
		} else {
			picType = "1";
			assetType = "ALL";
			command = "PROGRAMRANK";
		}

		RankListRequestBody body = new RankListRequestBody(command, "iptvtest", "hzvsite", "hzdq", assetType, code, "H264");
		body.setPictype(picType);
		body.setDetailindex(1);
		body.setNumber(13);

		try {
			JSONObject object = new JSONObject();
			object.put("head", head);
			object.put("body", body);
			bundle.putString("param", object.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bundle;
	}
	
	/**
	 * 获取屏幕宽度
	 * @param context
	 * @return
	 */
	public static int getDeviceWidth(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(metrics);
		return metrics.widthPixels;
	}

	/**
	 * 获取屏幕高度
	 * @param context
	 * @return
	 */
	public static int getDeviceHeight(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(metrics);
		return metrics.heightPixels;
	}
}
