package com.wasu.launcher.utils;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewPropertyAnimator;

public class Aanimate {
	public static float scale_ratio = 1.5f;

	@SuppressLint("NewApi")
	public static void showOnFocusTranslAnimation(final View view) {
		ViewPropertyAnimator propertyAnimation = view.animate();
		propertyAnimation.setDuration(400L);
		propertyAnimation.scaleX(scale_ratio);
		propertyAnimation.scaleY(scale_ratio);
		float offset = (view.getMeasuredWidth() * scale_ratio - view.getMeasuredWidth()) / 2;
		propertyAnimation.translationX(-offset);
		propertyAnimation.start();
	}

	@SuppressLint("NewApi")
	public static void showLooseFocusTranslAinimation(final View view) {
		ViewPropertyAnimator propertyAnimation = view.animate();
		propertyAnimation.setDuration(400L);
		propertyAnimation.scaleX(1.0f);
		propertyAnimation.scaleY(1.0f);
		// float offset = (view.getMeasuredWidth() * scale_ratio -
		// view.getMeasuredWidth()) / 2;
		propertyAnimation.translationX(0.0f);
		propertyAnimation.start();
	}

}
