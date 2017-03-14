package com.android.test.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.w3c.dom.Text;

import android.R;
import android.R.integer;
import android.R.string;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LyricView extends RelativeLayout {
	
	private static String TAG = "qjy";
	private TextPaint mPaint; 
	private int currentIndex = 0;
	private String currentLrc = null;
	private static TreeMap<Integer, LyricObject> lrc_map;
	private Context context;

	public LyricView(Context context)
	{
		super(context);
		this.context = context;
		init();
	}
	
	public LyricView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context = context;
		init();
	}
	
	private void init()
	{

		lrc_map = new TreeMap<Integer, LyricView.LyricObject>();
		
		mPaint = new TextPaint();
		mPaint.setColor(Color.RED);
		mPaint.setTextSize(20);
//		mPaint.setTextAlign(Paint.Align.CENTER);
		Thread myThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					read("/data/app/test.lrc");
				} catch (Exception e) {
					Log.e(TAG, "ERROR: can not read test.lrc");
				}
								
			}
		});
		
		myThread.start();		
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		Log.e(TAG, "LyricView onDraw start");
		
//		LyricObject ones = lrc_map.get(5);
		if (currentIndex < 6) {
			StringBuilder  testString = new StringBuilder();

			for (int j = 0; j < 6; j++) {
				if (testString.toString() != null) {
					testString.append("\n\r");
				}			
				testString.append(lrc_map.get(j).lrc);
			}

			StaticLayout layout = new StaticLayout(testString.toString(), mPaint, getMeasuredWidth()-getPaddingLeft()-getPaddingRight(), Alignment.ALIGN_CENTER, 1, 0, true);
			canvas.save();
			layout.draw(canvas);
			canvas.restore();
			return;
		}

		currentLrc = lrc_map.get(currentIndex).lrc;
		
		Rect boundRect = new Rect();
		mPaint.getTextBounds(currentLrc, 0,currentLrc.length(), boundRect);
		canvas.drawText(currentLrc, getMeasuredWidth()/2 - boundRect.width()/2 , getMeasuredHeight()/2 + boundRect.height()/2, mPaint);
	}
	
	public void setTime(int time){
		int c;
		if (currentIndex == (lrc_map.size()-1)) {
			return;
		}
		
		for (c = currentIndex; c < lrc_map.size()-1; c++) {
			if (time > lrc_map.get(c).begintime && time < lrc_map.get(c+1).begintime) {
				currentIndex = c;
				break;
			}else if (time > lrc_map.get(lrc_map.size()-1).begintime) {
				currentIndex = lrc_map.size()-1;
				break;
			}			
		}		
		Log.e(TAG, "current index is "+currentIndex);
	}
	
	public static void read(String file)
	{
		String data;
		String mTime;
		int min;
		int sec;
		int ms;
		int i = 0;
		LyricObject item;
		
		Log.e(TAG, "start to read ! ");
		lrc_map.clear();
		
		try {
			File f = new File(file);
			FileInputStream fis = new FileInputStream(f);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(fis, "GB2312"));
			Pattern pattern = Pattern.compile("[0-9]*");
			
			while ((data = br.readLine()) != null) {
				item = new LyricObject();				
				mTime = data.substring(data.indexOf("[") + 1, data.indexOf("]"));
				if (pattern.matcher(mTime.substring(0, mTime.indexOf(":"))).matches()) {
					min = Integer.parseInt(mTime.substring(0, mTime.indexOf(":")));
					sec = Integer.parseInt(mTime.substring(mTime.indexOf(":")+1, mTime.indexOf(".")));
					ms = Integer.parseInt(mTime.substring(mTime.indexOf(".")+1));
					item.begintime = (min*60 + sec)*1000 + ms;
					item.lrc = data.substring(data.indexOf("]")+1);
				} else {
					item.begintime = 0;
					item.lrc = mTime;
				}
				
				Log.e(TAG, "i IS " + i);
				lrc_map.put(new Integer(i), item);				
				Log.e(TAG, "begintime IS " + lrc_map.get(i).begintime);
				Log.e(TAG, "LRC IS " + lrc_map.get(i).lrc);
				i++;
			}
			fis.close();			
		} catch (Exception e) {
			Log.e(TAG, "ERROR can not read! ");
			e.printStackTrace();
		}
	}
	
	protected static class LyricObject {
		protected int begintime;
		protected int endtime;
		protected int consume;
		protected String lrc;
	}
}
