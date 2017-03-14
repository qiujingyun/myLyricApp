package com.android.test.view;

import com.android.test.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

public class TitleView extends RelativeLayout {
	
	private String TAG = "qjy";
	
	private int mTitleTextColor;
	private String mTitleText;
	private int mTitleTextSize;
	
	private Paint mPaint;
	private Rect mBound;
	
	public TitleView(Context context, AttributeSet attrs){
		this(context, attrs, 0);	
	}
	
	public TitleView(Context context ){
		this(context, null);
	}

	public TitleView(Context context, AttributeSet attrs, int defStyple){
		super(context, attrs, defStyple);
		
		this.setWillNotDraw(false);
		
		TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TitleView, defStyple, 0);
		
		try{
			mTitleTextColor = ta.getColor(R.styleable.TitleView_titleTextColor, 0xff0000);
			mTitleText = ta.getString(R.styleable.TitleView_titleText);
			mTitleTextSize = ta.getDimensionPixelSize(R.styleable.TitleView_titleTextSize, 40);
		}finally{
			ta.recycle();
		}
		
		Log.d(TAG, "mTitleTextColor mTitleText mTitleTextSize is " +Integer.toHexString(mTitleTextColor) +";"+mTitleText+";"+mTitleTextSize);
		
		mPaint = new Paint();
		mPaint.setTextSize(mTitleTextSize);
		mPaint.setColor(mTitleTextColor);
		mBound = new Rect();
		mPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mBound);	
		Log.d(TAG, "mBound.width mBound.height is " + mBound.width() + ";" + mBound.height());
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
		
		mPaint.setColor(Color.YELLOW);
		Log.d(TAG, "getMeasuredWidth getMeasuredHeight is " + getMeasuredWidth() + ";" + getMeasuredHeight());
		canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);
		
		mPaint.setColor(mTitleTextColor);
		Log.d(TAG, "getWidth getHeight is " + getWidth() + ";" + getHeight());
		canvas.drawText(mTitleText, getWidth() / 2 - mBound.width() / 2, getHeight() / 2 + mBound.height() / 2, mPaint);
		
	}
	

	
	
}
