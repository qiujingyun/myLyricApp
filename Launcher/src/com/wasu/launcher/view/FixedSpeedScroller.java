package com.wasu.launcher.view;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.OverScroller;

/**
 * 替代HorizontalScrollView中默认的OverScroller，用以修正滚动速度
 * @author chenchen
 */
public class FixedSpeedScroller extends OverScroller {

    private int mDuration =500;

    public void setTime(int scrollerTime){
        mDuration=scrollerTime;
    }
    public FixedSpeedScroller(Context context) {
        super(context);
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }


    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
}
