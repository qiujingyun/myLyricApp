package com.wasu.launcher.utils;

import com.wasu.launcher.MainActivity;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class Packages {

	private static final String TAG = Packages.class.getSimpleName();
	/**
	 * setPackageClassName
	 * 
	 * @param context
	 *            Context object
	 * @param PackageName
	 *            PackageName
	 * @param PackageClassName
	 *            PackageClassName
	 * @param key
	 *            intkey
	 * @param Strkey
	 *            Strkey
	 * @param Strvalue
	 *            Strvalue
	 * @param value
	 *            intvalue
	 */

	public static Intent startIntent(Context context, String PackageName, String PackageClassName, String[] key,
			String[] Strkey, String[] Strvalue, int... value) {
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(PackageName.trim());
		if (intent != null) {
			intent = new Intent();
			ComponentName comp = new ComponentName(PackageName.trim(), PackageClassName.trim());
			intent.setComponent(comp);
			if (key != null) {
				for (int i = 0; i < key.length; i++) {
					if (i > value.length - 1)
						break;
					intent.putExtra(key[i], value[i]);
				}
			}
			if (Strkey != null) {
				for (int i = 0; i < Strkey.length; i++) {
					if (i > Strvalue.length - 1)
						break;
					intent.putExtra(Strkey[i], Strvalue[i]);
				}
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} else {
			Toast.makeText(context.getApplicationContext(), "没有安装app", Toast.LENGTH_LONG).show();
		}
		return intent;
	}

	/**
	 * setAction
	 * 
	 * @param context
	 *            Context object
	 * @param PackageName
	 *            PackageName
	 * @param PackageClassName
	 *            PackageClassName
	 * @param key
	 *            intkey
	 * @param Strkey
	 *            Strkey
	 * @param Strvalue
	 *            Strvalue
	 * @param value
	 *            intvalue
	 */
	public static void startIntentAction(Context context, String PackageName, String action, String[] key,
			String[] Strkey, String[] Strvalue, int... value) {
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(PackageName.trim());
		if (intent != null) {
			intent = new Intent(action);
			if (key != null) {
				for (int i = 0; i < key.length; i++) {
					if (i > value.length - 1)
						break;
					intent.putExtra(key[i], value[i]);
				}
			}
			if (Strkey != null) {
				for (int i = 0; i < Strkey.length; i++) {
					if (i > Strvalue.length - 1)
						break;
					intent.putExtra(Strkey[i], Strvalue[i]);
				}
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} else {
			Toast.makeText(context.getApplicationContext(), "没有安装app", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * setMAIN
	 * 
	 * @param context
	 *            Context object
	 * @param PackageName
	 *            PackageName
	 * @param PackageClassName
	 *            PackageClassName
	 * @param key
	 *            intkey
	 * @param Strkey
	 *            Strkey
	 * @param Strvalue
	 *            Strvalue
	 * @param value
	 *            intvalue
	 */
	public static void startIntentMAIN(Context context, String PackageName, String[] key, String[] Strkey,
			String[] Strvalue, int... value) {
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(PackageName.trim());
		if (intent != null) {
			if (key != null) {
				for (int i = 0; i < key.length; i++) {
					if (i > value.length - 1)
						break;
					intent.putExtra(key[i], value[i]);
				}
			}
			if (Strkey != null) {
				for (int i = 0; i < Strkey.length; i++) {
					if (i > Strvalue.length - 1)
						break;
					intent.putExtra(Strkey[i], Strvalue[i]);
				}
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} else {
			Toast.makeText(context.getApplicationContext(), "没有安装app", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * openClass
	 * 
	 * @param context
	 *            Context object
	 * @param Class
	 *            Class<?>
	 * @param PackageClassName
	 *            PackageClassName
	 * @param key
	 *            intkey
	 * @param Strkey
	 *            Strkey
	 * @param Strvalue
	 *            Strvalue
	 * @param value
	 *            intvalue
	 */
	public static void openClass(Context context, Class<?> cls, String[] key, String[] Strkey, String[] Strvalue,
			int... value) {
		Intent intent = new Intent(context, cls);
		if (intent != null) {
			if (key != null) {
				for (int i = 0; i < key.length; i++) {
					if (i > value.length - 1)
						break;
					intent.putExtra(key[i], value[i]);
				}
			}
			if (Strkey != null) {
				for (int i = 0; i < Strkey.length; i++) {
					if (i > Strvalue.length - 1)
						break;
					intent.putExtra(Strkey[i], Strvalue[i]);
				}
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}

	/**
	 * 
	 * 如果填出消息提示，说明跳转有异常
	 * 
	 */
	public static void toActivityByAction(Context context, String action,
			String[] key, String[] Strkey, String[] Strvalue, int... value) {

		try {
			Intent intent = new Intent(action);
			if (key != null) {
				for (int i = 0; i < key.length; i++) {
					if (i > value.length - 1)
						break;
					intent.putExtra(key[i], value[i]);
				}
			}
			if (Strkey != null) {
				for (int i = 0; i < Strkey.length; i++) {
					if (i > Strvalue.length - 1)
						break;
					intent.putExtra(Strkey[i], Strvalue[i]);
				}
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			
		} catch (Exception e) {
			Toast.makeText(context.getApplicationContext(), "没有安装app",
					Toast.LENGTH_SHORT).show();
			Log.e(TAG, e.getMessage());
		}
	}
	
	/**
	 * 
	 * 如果填出消息提示，说明跳转有异常
	 * 
	 */
	public static void toActivityByClassName(Context context, String classname,
			String[] key, String[] Strkey, String[] Strvalue, int... value) {

		try {
			Intent intent = new Intent();
			
			intent.setClassName(context, classname);
			
			if (key != null) {
				for (int i = 0; i < key.length; i++) {
					if (i > value.length - 1)
						break;
					intent.putExtra(key[i], value[i]);
				}
			}
			if (Strkey != null) {
				for (int i = 0; i < Strkey.length; i++) {
					if (i > Strvalue.length - 1)
						break;
					intent.putExtra(Strkey[i], Strvalue[i]);
				}
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);

		} catch (Exception e) {
			Toast.makeText(context.getApplicationContext(), "没有安装app",
					Toast.LENGTH_SHORT).show();
			Log.e(TAG, e.getMessage());
		}
	}
	
}
