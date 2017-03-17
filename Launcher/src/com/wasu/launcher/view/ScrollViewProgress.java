package com.wasu.launcher.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ScrollViewProgress extends ScrollView {

	public ScrollViewProgress(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ScrollViewProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollViewProgress(Context context) {
		super(context);
	}

	boolean isFocusInDescendants = true;

	public boolean isFocusInDescendants() {
		return isFocusInDescendants;
	}

	/**
	 * 是否自动获得焦点
	 * 
	 * @param isFocusInDescendants
	 *            默认是true <code>true 自己手动处理</code> <code>false 系统处理</code>
	 */
	public void setFocusInDescendants(boolean isFocusInDescendants) {
		this.isFocusInDescendants = isFocusInDescendants;
		// setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
	}

	/* 自动获得焦点 */
	@Override
	protected boolean onRequestFocusInDescendants(int direction,
			Rect previouslyFocusedRect) {
		// 不让他自动获得焦点就return true
		if (isFocusInDescendants) {
			return true;
		} else {
			return super.onRequestFocusInDescendants(direction,
					previouslyFocusedRect);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		zheight = computeVerticalScrollRange();
		height = getHeight();

	}

	int zheight = 0;
	int height = 0;

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (zheight == 0) {
			zheight = computeVerticalScrollRange();
		}
		if (height == 0) {
			height = getHeight();
		}
		if (scrollViewListener != null) {
			scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
		}

		// Log.e("l",
		// t + ".......old....." + oldt + "this.getHeight()...."
		// + this.getHeight() + "...."
		// + computeVerticalScrollRange() + "..." + getScaleY());

		if (scrollProgressListener != null) {
			int i = (int) (t / (zheight - height - 0.0) * 100);
			// Log.e(this.getClass().toString() + zheight + ".." + height, "" +
			// i);
			isShowUp = false;
			isShowDown = false;
			if (i >= 100) {
				i = 100;
				isShowDown = true;
			}
			if (i <= 0) {
				i = 0;
				isShowUp = true;
			}
			scrollProgressListener.onScrollProgress(i);
		}
	}

	public boolean isShowUp() {
		return isShowUp;
	}

	boolean isShowUp = true;
	boolean isShowDown = false;

	public boolean isShowDown() {
		return isShowDown;
	}

	private OnScrollViewListener scrollViewListener;

	/**
	 * 
	 * @param scrollViewListener
	 */
	public void setOnScrollViewListener(OnScrollViewListener l) {
		this.scrollViewListener = l;
	}

	public interface OnScrollViewListener {
		/**
		 * This is called in response to an internal scroll in this view (i.e.,
		 * the view scrolled its own contents). This is typically as a result of
		 * scrollBy(int, int) or scrollTo(int, int) having been called.
		 * 
		 * Overrides: onScrollChanged(...) in View Parameters:
		 * 
		 * 
		 * 
		 * 
		 * @param ScrollViewProgress
		 * @param l
		 *            Current horizontal scroll origin.
		 * @param t
		 *            Current vertical scroll origin.
		 * @param oldl
		 *            Previous horizontal scroll origin.
		 * @param oldt
		 *            Previous vertical scroll origin.
		 */
		void onScrollChanged(ScrollViewProgress scrollView, int l, int t,
				int oldl, int oldt);
	}

	public interface OnScrollProgressListener {

		/**
		 * This is in response to an internal rolling schedule in this view
		 * Parameters: maximum is 100 minimum is 0
		 * 
		 * Overrides: onScrollProgress(...) in View
		 * 
		 * 
		 * 
		 * 
		 * @param ScrollViewProgress
		 * @param progress
		 *            This is progress
		 */
		void onScrollProgress(int progress);
	}

	OnScrollProgressListener scrollProgressListener;

	public void setOnScrollProgressListener(OnScrollProgressListener l) {
		this.scrollProgressListener = l;
	}

}