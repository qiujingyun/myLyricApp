package com.wasu.launcher.view;

import com.wasu.launcher.R;

import android.R.integer;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

public class SmoothHorizontalScrollView extends HorizontalScrollView {

	private static final String TAG = "SmoothHorizontalScrollView";

	private ScrollViewListener scrollViewListener = null;

	private int mFadingEdge;

	// 自定义ScrollView回调
	public interface ScrollViewListener {
		/**
		 * The view is not scrolling. Note navigating the list using the
		 * trackball counts as being in the idle state since these transitions
		 * are not animated.
		 */
		public static int SCROLL_STATE_IDLE = 0;

		/**
		 * The user is scrolling using touch, and their finger is still on the
		 * screen
		 */
		public static int SCROLL_STATE_SCROLL = 1;

		/**
		 * The user had previously been scrolling using touch and had performed
		 * a fling. The animation is now coasting to a stop
		 */
		public static int SCROLL_STATE_FLING = 2;

		/**
		 * 滑动状态回调
		 * 
		 * @param view
		 *            当前的scrollView
		 * @param scrollState
		 *            当前的状态
		 */
		public void onScrollStateChanged(SmoothHorizontalScrollView view,
				int scrollState);

		// 滚动距离
		void onScrollChanged(int x, int y, int oldx, int oldy);

		// 开始滚动
		void doScroll();

		// 滚动到底
		void onScrollEnd();
	}

	public void setScrollViewListener(ScrollViewListener scrollViewListener) {
		this.scrollViewListener = scrollViewListener;
	}

	public SmoothHorizontalScrollView(Context context) {
		this(context, null, 0);
	}

	public SmoothHorizontalScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SmoothHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
		if (getChildCount() == 0)
			return 0;

		int width = getWidth();
		int screenLeft = getScrollX();
		int screenRight = screenLeft + width;

		// leave room for left fading edge as long as rect isn't at very left
		if (rect.left > 0) {
			screenLeft += 182;
		}

		// leave room for right fading edge as long as rect isn't at very right
		if (rect.right < getChildAt(0).getWidth()) {
			screenRight -= mFadingEdge;
		}

		int scrollXDelta = 0;

		if (rect.right > screenRight && rect.left > screenLeft) {
			// need to move right to get it in view: move right just enough so
			// that the entire rectangle is in view (or at least the first
			// screen size chunk).

			if (rect.width() > width) {
				// just enough to get screen size chunk on
				scrollXDelta += (rect.left - screenLeft);
			} else {
				// get entire rect at right of screen
				/* 向右滚动距离为下一个焦点框的宽度 */
				scrollXDelta += rect.width() + getResources().getDimensionPixelSize(R.dimen.sm_8);
			}

			// make sure we aren't scrolling beyond the end of our content
			int right = getChildAt(0).getRight();
			int distanceToRight = right - screenRight;
			scrollXDelta = Math.min(scrollXDelta, distanceToRight);

		} else if (rect.left < screenLeft && rect.right < screenRight) {
			// need to move right to get it in view: move right just enough so
			// that
			// entire rectangle is in view (or at least the first screen
			// size chunk of it).

			if (rect.width() > width) {
				// screen size chunk
				scrollXDelta -= (screenRight - rect.right);
			} else {
				// entire rect at left
				/* 向左滚动距离要计算图片放大比例 */
				scrollXDelta -= (screenLeft - rect.left);
			}

			// make sure we aren't scrolling any further than the left our
			// content
			scrollXDelta = Math.max(scrollXDelta, -getScrollX());
		}

		return scrollXDelta;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		super.onScrollChanged(l, t, oldl, oldt);
		if (scrollViewListener != null) {
			scrollViewListener.onScrollChanged(l, t, oldl, oldt);
		}

		if (l != oldl) {
//			Log.w(TAG, "SCROLL_STATE_SCROLL");
			scrollViewListener.onScrollStateChanged(this,
					ScrollViewListener.SCROLL_STATE_SCROLL);
			// 记住上次滑动的最后位置
			lastL = l;
			checkStateHandler.removeMessages(CHECK_STATE);// 确保只在最后一次做这个check
			checkStateHandler.sendEmptyMessageDelayed(CHECK_STATE, 5);// 5毫秒检查一下
		}
	}
	
	// 检查ScrollView的最终状态
	private static final int CHECK_STATE = 0;
	// 上次滑动的最后位置
	private int lastL = 0;
	private Handler checkStateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (lastL == getScrollX()) {
				// 如果上次的位置和当前的位置相同，可认为是在空闲状态
//				Log.e(TAG, "SCROLL_STATE_IDLE");
				scrollViewListener.onScrollStateChanged(SmoothHorizontalScrollView.this,
						ScrollViewListener.SCROLL_STATE_IDLE);
			}
		}
	};

//	public boolean arrowScroll(int direction) {
//		// Log.d("chenchen", "arrowScroll direction:" + direction);
//
//		View currentFocused = findFocus();
//		if (currentFocused == this)
//			currentFocused = null;
//
//		View nextFocused = FocusFinder.getInstance().findNextFocus(this,
//				currentFocused, direction);
//
//		final int maxJump = getMaxScrollAmount();
//
//		if (nextFocused != null && isWithinDeltaOfScreen(nextFocused, maxJump)) {
//			nextFocused.getDrawingRect(mTempRect);
//			offsetDescendantRectToMyCoords(nextFocused, mTempRect);
//			int scrollDelta = computeScrollDeltaToGetChildRectOnScreen(mTempRect);
//			doScrollX(scrollDelta);
//			nextFocused.requestFocus(direction);
//			if (scrollViewListener != null) {
//				scrollViewListener.doScroll();
//			}
//			// Log.d("chenchen", "arrowScroll 111");
//		} else {
//			// no new focus
//			int scrollDelta = maxJump;
//
//			if (direction == View.FOCUS_LEFT && getScrollX() < scrollDelta) {
//				scrollDelta = getScrollX();
//			} else if (direction == View.FOCUS_RIGHT && getChildCount() > 0) {
//
//				int daRight = getChildAt(0).getRight();
//
//				int screenRight = getScrollX() + getWidth();
//
//				if (daRight - screenRight < maxJump) {
//					scrollDelta = daRight - screenRight;
//				}
//			}
//			if (scrollDelta == 0) {
//				if (scrollViewListener != null) {
//					scrollViewListener.onScrollEnd();
//				}
//				// Log.d("chenchen", "arrowScroll 222  scrollDelta == 0");
//				return false;
//			}
//			doScrollX(direction == View.FOCUS_RIGHT ? scrollDelta
//					: -scrollDelta);
//			// Log.d("chenchen", "arrowScroll 222  doScrollX");
//		}
//
//		if (currentFocused != null && currentFocused.isFocused()
//				&& isOffScreen(currentFocused)) {
//			// previously focused item still has focus and is off screen, give
//			// it up (take it back to ourselves)
//			// (also, need to temporarily force FOCUS_BEFORE_DESCENDANTS so we
//			// are
//			// sure to
//			// get it)
//			final int descendantFocusability = getDescendantFocusability(); // save
//			setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
//			requestFocus();
//			setDescendantFocusability(descendantFocusability); // restore
//		}
//		return true;
//	}
//
	public void doScrollX(int delta) {
		if (delta != 0) {
			if (isSmoothScrollingEnabled()) {
				smoothScrollBy(delta, 0);
			} else {
				scrollBy(delta, 0);
			}
		}
	}
//
//	private final Rect mTempRect = new Rect();
//
//	/**
//	 * @return whether the descendant of this scroll view is scrolled off
//	 *         screen.
//	 */
//	private boolean isOffScreen(View descendant) {
//		return !isWithinDeltaOfScreen(descendant, 0);
//	}
//
//	/**
//	 * @return whether the descendant of this scroll view is within delta pixels
//	 *         of being on the screen.
//	 */
//	private boolean isWithinDeltaOfScreen(View descendant, int delta) {
//		descendant.getDrawingRect(mTempRect);
//		offsetDescendantRectToMyCoords(descendant, mTempRect);
//
//		return (mTempRect.right + delta) >= getScrollX()
//				&& (mTempRect.left - delta) <= (getScrollX() + getWidth());
//	}

}