package com.wasu.launcher.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

/**
 * <pre>
 * 日志的功能操作类 (可开关打印日志、开关异常、将日志保存至SD卡)
 * </pre>
 * @author  
 * @version 1.0,
 */
public class LogUtil
{

	/**
	 * 定义当前日志打印级别
	 */
	private static int logLevel = 0;

	private static final int VERBOSE = 1;

	private static final int DEBUG = 2;

	private static final int INFO = 3;

	private static final int WARNING = 4;

	private static final int ERROR = 5;

	/**
	 * 日志打印控制开关
	 */
	private final static boolean IS_PRINT_LOG = false;

	/**
	 * 是否保存至SD卡
	 */
	private final static boolean SAVE_TO_SD = false;

	/**
	 * 是否打印异常日志
	 */
	private final static boolean IS_PRINT_STACK_INFO = false;

	
	/**
	 * SD卡保存路劲
	 */
	private final static String STORE_DIR_IN_SDCARD = "WasuTv";

	/**
	 * 保存LOG日志的目录
	 */
	private static final String SAVE_LOG_DIR_PATH = Environment.getExternalStorageDirectory().getPath() + "/"
			+ STORE_DIR_IN_SDCARD;

	/**
	 * 保存LOG日志的路径
	 */
	private static final String SAVE_LOG_PATH = SAVE_LOG_DIR_PATH + "/log.txt";

	/**
	 * 日志打印时间Format
	 */
	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 用于打印error级的日志信息
	 * @param strModule LOG TAG
	 * @param strErrMsg 打印信息
	 */
	public static void e(String strModule, String strErrMsg)
	{
		if (ERROR >= logLevel)
		{
			if (IS_PRINT_LOG)
			{
				Log.e(strModule, ">>" + strErrMsg + "<<");
			}
			if (SAVE_TO_SD)
			{
				storeLog(strModule, strErrMsg);
			}
		}
	}

	/**
	 * 用于打印debug级的日志信息
	 * @param strModule LOG TAG
	 * @param strErrMsg 打印信息
	 */
	public static void d(String strModule, String strErrMsg)
	{
		if (DEBUG >= logLevel)
		{
			if (IS_PRINT_LOG)
			{
				Log.d(strModule, (strErrMsg == null || strErrMsg.trim().length() < 1) ? "" : strErrMsg);
			}
			if (SAVE_TO_SD)
			{
				storeLog(strModule, strErrMsg);
			}
		}
	}

	/**
	 * 用于打印info级别的日志信息
	 * @param strModule LOG TAG
	 * @param strErrMsg 打印信息
	 */
	public static void i(String strModule, String strErrMsg)
	{
		if (INFO >= logLevel)
		{
			if (IS_PRINT_LOG)
			{
				Log.i(strModule, strErrMsg);
			}
			if (SAVE_TO_SD)
			{
				storeLog(strModule, strErrMsg);
			}
		}
	}

	/**
	 * 用于打印warning级别的日志信息
	 * @param strModule LOG TAG
	 * @param strErrMsg 打印信息
	 */
	public static void w(String strModule, String strErrMsg)
	{
		if (WARNING >= logLevel)
		{
			if (IS_PRINT_LOG)
			{
				Log.w(strModule, strErrMsg);
			}
			if (SAVE_TO_SD)
			{
				storeLog(strModule, strErrMsg);
			}
		}
	}

	/**
	 * 用于打印verbose级别的日志信息
	 * @param strModule LOG TAG
	 * @param strErrMsg 打印信息
	 */
	public static void v(String strModule, String strErrMsg)
	{
		if (VERBOSE >= logLevel)
		{
			if (IS_PRINT_LOG)
			{
				Log.v(strModule, strErrMsg);
			}
			if (SAVE_TO_SD)
			{
				storeLog(strModule, strErrMsg);
			}
		}
	}

	/**
	 * 打印异常栈信息 <BR>
	 * @param strModule LOG TAG
	 * @param e Exception
	 */
	public static void e(String strModule, Exception e)
	{
		if (ERROR >= logLevel)
		{
			if (IS_PRINT_STACK_INFO)
			{
				if (e != null)
				{
					e.printStackTrace();
				}
			}
			if (SAVE_TO_SD)
			{
				storeLog(strModule, e.getMessage());
			}
		}
	}

	/**
	 * 打印异常栈信息 <BR>
	 * @param strModule LOG TAG
	 * @param strErrMsg msg
	 * @param e Exception
	 */
	public static void e(String strModule, String strErrMsg, Exception e)
	{
		if (ERROR >= logLevel)
		{
			Log.e(strModule, strErrMsg);

			if (IS_PRINT_STACK_INFO)
			{
				if (e != null)
				{
					e.printStackTrace();
				}
			}
			if (SAVE_TO_SD)
			{
				storeLog(strModule, e.getMessage());
			}
		}
	}

	/**
	 * 打印error级别的日志
	 * @param tag 标记
	 * @param text 日志内容
	 * @param throwable 异常信息
	 */
	public static void e(String strModule, String strErrMsg, Throwable throwable)
	{
		if (ERROR >= logLevel)
		{
			if (SAVE_TO_SD)
			{
				storeLog(strModule, strErrMsg);
			}

			if (IS_PRINT_LOG)
			{
				Log.e(strModule, strErrMsg, throwable);
			}
		}
	}

	/**
	 * 将日志信息保存至SD卡
	 * @param strModule LOG TAG
	 * @param strErrMsg 保存的打印信息
	 */
	public static void storeLog(String strModule, String strErrMsg)
	{
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			File fileDir = new File(SAVE_LOG_DIR_PATH);
			// 判断目录是否已经存在
			if (!fileDir.exists())
			{
				if (!fileDir.mkdir())
				{
					Log.e(strModule, "Failed to create directory " + SAVE_LOG_DIR_PATH);
					return;
				}
			}
			File file = new File(SAVE_LOG_PATH);
			// 判断日志文件是否已经存在
			if (!file.exists())
			{
				try
				{
					file.createNewFile();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// 输出
				FileOutputStream fos = new FileOutputStream(file, true);
				PrintWriter out = new PrintWriter(fos);
				out.println(fmt.format(System.currentTimeMillis()) + "  >>" + strModule + "<<  " + strErrMsg + '\r');
				out.flush();
				out.close();
			}
			catch (FileNotFoundException e1)
			{
				e1.printStackTrace();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
