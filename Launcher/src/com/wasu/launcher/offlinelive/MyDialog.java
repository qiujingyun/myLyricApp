package com.wasu.launcher.offlinelive;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.wasu.launcher.R;

/**
 * 
 * @author 77071
 *
 */
public class MyDialog extends Dialog {
	private View view;

	/***
	 * 自定义 dialog
	 * 
	 * @param context
	 * @param view
	 *            控件
	 * @param rectf
	 *            宽度比例
	 */
	public MyDialog(Context context, View view, RectD rectd) {
		super(context, R.style.lau_dialog);
		this.view = view;
		this.setContentView(view);
		WindowManager windowManager = ((Activity) context).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		if (rectd.width != 0) {
			lp.width = (int) (display.getWidth() * rectd.width); // 设置宽度
		}
		if (rectd.height != 0) {
			lp.height = (int) (display.getHeight() * rectd.height); // 设置高度
		}
		if (rectd.y != 0) {
			lp.y = (int) (display.getHeight() * rectd.y); // 设置y位置
		}
		if (rectd.x != 0) {
			lp.x = (int) (display.getWidth() * rectd.x); // 设置x位置
		}
		if (rectd.alpha != 0) {
			lp.alpha = rectd.alpha;
		}
		this.getWindow().setAttributes(lp);

	}

	public View getView() {
		return view;
	}
}