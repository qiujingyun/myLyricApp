package com.wasu.launcher.Fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wasu.launcher.R;
import com.wasu.launcher.Fragment.UserFragment.Handr3;
import com.wasu.launcher.adapter.ViewPagerAdapter;
import com.wasu.launcher.interfaces.MyFragmentListener;
import com.wasu.launcher.utils.ScaleAnimEffect;
import com.wasu.vod.aidl.SDKOperationManager.MyListener;

public class WelcomeFragment extends BaseFragment implements OnPageChangeListener {
	private View view;
	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;

	// 记录当前选中位置
	private int currentIndex;
	// 引导图片资源
	private static final int[] pics = { R.drawable.lau_zhibo, R.drawable.lau_huifang, R.drawable.lau_shipingbofang };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private MyFragmentListener mCallback;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (MyFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		if (null == view) {

			view = inflater.inflate(R.layout.layout_welcome, container, false);

			findViewById();
			setListener();
		}
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onConnnected(MyListener arg0) {

	}

	@Override
	protected void loadViewLayout() {

	}

	@Override
	protected void findViewById() {
		views = new ArrayList<View>();
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		// 初始化引导图片列表

		for (int i = 0; i < pics.length; i++) {
			ImageView iv = new ImageView(view.getContext());
			iv.setLayoutParams(mParams);
			iv.setImageResource(pics[i]);
			views.add(iv);
		}
		vp = (ViewPager) view.findViewById(R.id.viewpager);
		// 初始化Adapter
		vpAdapter = new ViewPagerAdapter(views);
		vp.setAdapter(vpAdapter);
		// 绑定回调
		vp.setOnPageChangeListener(this);
		vp.setOnKeyListener(new MyOnKeyListener());

	}

	/**
	 * 设置当前的引导页
	 */

	private void setCurView(int position) {
		System.out.println("--gup---position" + position);
		if (position < 0) {
			return;
		}
		if (position >= pics.length) {
			SharedPreferences sp = getActivity().getSharedPreferences("mywelcome", Context.MODE_APPEND);
			Editor e = sp.edit();
			e.putBoolean("isFrist", true);
			e.commit();
			if (mCallback != null) {
				mCallback.replaceFragment(0, 1);
			}

		}

		vp.setCurrentItem(position);
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		// for(int i){
		//
		// }

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		// System.out.println("--gup---onPageScrollStateChangedarg0"+arg0);

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		// /System.out.println("--gup---onPageScrolled arg0"+arg0+"---arg1"+arg1+"---arg2"+arg2);
	}

	@Override
	public void onPageSelected(int arg0) {
		setCurView(arg0);
		currentIndex = arg0;
		// TODO Auto-generated method stub
		if (arg0 >= pics.length) {
			SharedPreferences sp = getActivity().getSharedPreferences("mywelcome", Context.MODE_APPEND);
			Editor e = sp.edit();
			e.putBoolean("isFrist", true);
			e.commit();
			if (mCallback != null) {
				mCallback.replaceFragment(0, 1);
			}
		}
		// System.out.println("--gup---onPageSelectedarg0"+arg0);

	}

	private class MyOnKeyListener implements OnKeyListener {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {

			switch (event.getAction()) {
			case KeyEvent.ACTION_UP: // 键盘松开
				break;
			case KeyEvent.ACTION_DOWN: // 键盘按下

				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
					if (currentIndex + 1 >= views.size()) {

						if (mCallback != null) {
							SharedPreferences sp = getActivity().getSharedPreferences("mywelcome", Context.MODE_APPEND);
							Editor e = sp.edit();
							e.putBoolean("isFrist", true);
							e.commit();
							setMessage(1);
							mCallback.replaceFragment(0, 1);

						}
						return true;
					}

				}
				break;

			}
			return false;
		}
	}

}
