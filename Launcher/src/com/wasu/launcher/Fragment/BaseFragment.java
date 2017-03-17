package com.wasu.launcher.Fragment;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wasu.android.volley.RequestQueue;
import com.wasu.android.volley.toolbox.ImageLoader;
import com.wasu.launcher.R;
import com.wasu.launcher.utils.PlayUtils;
import com.wasu.launcher.utils.ScaleAnimEffect;
import com.wasu.launcher.utils.Utils;
import com.wasu.launcher.view.FixedSpeedScroller;
import com.wasu.launcher.view.ScrollViewProgress;
import com.wasu.launcher.view.SmoothHorizontalScrollView;
import com.wasu.launcher.view.SmoothHorizontalScrollView.ScrollViewListener;
import com.wasu.vod.aidl.SDKOperationManager;
import com.wasu.vod.aidl.SDKOperationManager.BindService;
import com.wasu.vod.aidl.SDKOperationManager.MyListener;
import com.wasu.vod.application.MyVolley;

public abstract class BaseFragment extends Fragment implements BindService {

	protected Context context;
	protected String params;
	protected int mWidth;
	protected int mHeight;
	protected SDKOperationManager smManager;
	protected Gson gson;
	protected ImageLoader imageLoader;
	protected RequestQueue mQueue;
	protected Long startTime = 0l;// 记录开始时间
	
	//公用的外层布局
	protected View view;
	//滚动页
	protected SmoothHorizontalScrollView mScrollView;
	//滚动回调
	protected ScrollViewListener mScrollViewListener;
	//缩放动效
	protected ScaleAnimEffect animEffect;
	//ScrollView滚动
	private FixedSpeedScroller mScroller = null;
	//ScrollView滚动时长
	private final static int SCROLL_VIEW_SPEED = 500;
	//播放器
	protected PlayUtils playUtils;
	
	protected ImageView mShadow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = getActivity();
		mWidth = Utils.getDeviceWidth(context);
		mHeight = Utils.getDeviceHeight(context);
		smManager = new SDKOperationManager(getActivity());
		smManager.registerSDKOperationListener(this);
		imageLoader = MyVolley.getImageLoader();
		//mQueue = Volley.newRequestQueue(context);
		gson = new Gson();
		
		animEffect = new ScaleAnimEffect();
		playUtils = PlayUtils.getInstance();
//		fixScrollViewSpeed();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDisconnected(MyListener arg0) {
		Log.e("BaseFragment Lin 67 ", "arg0 : " + arg0);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		startTime = System.currentTimeMillis();
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if (smManager != null) {
			smManager.unRegisterSDKOperationListener();
		}
		if (playUtils != null) {
			playUtils.deallocate();
			playUtils = null;
		}
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		// try {
		// Field childFragmentManager = Fragment.class
		// .getDeclaredField("mChildFragmentManager");
		// childFragmentManager.setAccessible(true);
		// childFragmentManager.set(this, null);
		//
		// } catch (NoSuchFieldException e) {
		// /* throw new RuntimeException(e); */
		// e.printStackTrace();
		// } catch (IllegalAccessException e) {
		// throw new RuntimeException(e);
		// }
	}

	/**
	 * 加载布局文件
	 */
	protected abstract void loadViewLayout();

	/**
	 * 初始化控件
	 */
	protected abstract void findViewById();

	/**
	 * 设置监听器
	 */
	protected abstract void setListener();

	private ScrollViewProgress shview;

	/** 设置一个 ScrollViewProgress */
	public void setScrollViewProgress(ScrollViewProgress shview) {
		this.shview = shview;
	}

	/**
	 * 
	 * 注意是否设置scrollview
	 * 
	 * @param what
	 *            设置滑动的位置0 底 1顶
	 */
	public void setMessage(int what) {
		Message message = myhHandler.obtainMessage();
		message.what = what;
		myhHandler.sendMessage(message);
	}

	/**
	 * 注意是否设置scrollview
	 * 
	 * @param what
	 *            设置滑动的位置0 底 1顶
	 * @param v
	 *            如果焦点没有 使用这个方法指定焦点
	 */
	protected void setMessage(View v, int what) {
		Message message = myhHandler.obtainMessage();
		message.what = what;
		message.obj = v;
		myhHandler.sendMessage(message);
	}

	/**
	 * 判断 是否在顶部 ,一般卟修改 ,只做一个判断
	 * <p>
	 * 注意是否设置scrollview
	 * </p>
	 * 
	 * <p>
	 * 用法 滑动到顶部
	 * </p>
	 * 
	 * <pre>
	 * if(isUp)
	 * { 
	 * 
	 * setMessage(1)
	 * 
	 * }
	 * </pre>
	 * 
	 */
	public boolean isUp = false;

	Handler myhHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			View view = (View) msg.obj;
			if (shview != null) {// 卟为空执行
				// if (msg.what == 0) {
				// shview.smoothScrollTo(0, shview.getMeasuredHeight());
				// //shview.fullScroll(ScrollView.FOCUS_DOWN);// 滑动到底部
				// if (view != null) {
				// view.requestFocus();
				// }
				// isUp = true;
				// } else if (msg.what == 1) {
				// shview.smoothScrollTo(0, 0);
				// //shview.fullScroll(ScrollView.FOCUS_UP);// 滑动到顶
				// if (view != null) {
				// view.requestFocus();
				// }
				// isUp = false;
				// }

				switch (msg.what) {
				case 0:
					shview.smoothScrollTo(0, shview.getMeasuredHeight());
					// shview.fullScroll(ScrollView.FOCUS_DOWN);// 滑动到底部
					if (view != null) {
						view.requestFocus();
					}
					isUp = true;
					break;
				case 1:
					shview.smoothScrollTo(0, 0);
					// shview.fullScroll(ScrollView.FOCUS_UP);// 滑动到顶
					if (view != null) {
						view.requestFocus();
					}
					isUp = false;
					break;
				case 2:
				case 3:
				case 4:
				case 5:

					break;
				default:
					break;
				}
			}

		}
	};

	/**
	 * 获取滚动页
	 * @return
	 */
	public SmoothHorizontalScrollView getScrollView() {
		return mScrollView;
	};

	/**
	 * 获取当前页面对应的左侧导航栏页签
	 * @return
	 */
	public View getTabView() {
		View nextFocusView = null;
		if (mScrollView != null) {
			View focusView = mScrollView.findFocus();
			if (focusView != null) {
				nextFocusView = focusView.focusSearch(View.FOCUS_LEFT);
			}
		}
		return nextFocusView;
	};
	
	/**
	 * 修正ScrollView控件滚动速度
	 */
	protected void fixScrollViewSpeed() {
		if (mScrollView == null) {
			return;
		}
		try {
            if(mScroller != null){
            	mScroller.setTime(SCROLL_VIEW_SPEED);
            }else{
                Field scroller;
                scroller = HorizontalScrollView.class.getDeclaredField("mScroller");
                scroller.setAccessible(true);
                mScroller = new FixedSpeedScroller(mScrollView.getContext(), new DecelerateInterpolator());
                mScroller.setTime(SCROLL_VIEW_SPEED);
                scroller.set(mScrollView, mScroller);
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	protected OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener() {

		@SuppressLint("NewApi")
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				if (mShadow != null) {
					LayoutParams params = (LayoutParams) mShadow.getLayoutParams();
					int width = v.getWidth();
					int height = v.getHeight();
					float wRatio; //宽度拉伸比例
					float hRatio; //高度拉伸比例
					int id = v.getId();
					if (id == R.id.rl_rec_video_view || id == R.id.rl_live_video_view) {
						wRatio = 0.08f;
						hRatio = 0.12f;
					}else {
						if (width > 300) {
							wRatio = 0.2f;
						} else if (width > 200) {
							wRatio = 0.22f;
						}else {
							wRatio = 0.25f;
						}
						
						if (height > 700) {
							hRatio = 0.12f;
						} else if (height > 380) {
							hRatio = 0.16f;
						} else if (height > 300){
							hRatio = 0.2f;
						} else if (height > 200) {
							hRatio = 0.3f;
						} else if (height > 100){
							hRatio = 0.5f;
						} else {
							hRatio = 0.65f;
						}
					}
					
//					params.leftMargin = v.getLeft() - (int) (width * wRatio) - getResources().getDimensionPixelSize(R.dimen.sm_190);
//					params.topMargin = v.getTop() - (int) (height * hRatio) - getResources().getDimensionPixelSize(R.dimen.sm_110);
//					params.width = (int) (width * (1 + wRatio * 2));
//					params.height = (int) (height * (1 + hRatio * 2));
					params.leftMargin = v.getLeft() - getResources().getDimensionPixelSize(R.dimen.sm_190);
					params.topMargin = v.getTop() - getResources().getDimensionPixelSize(R.dimen.sm_110);;
					params.width = width;
					params.height = height;
					mShadow.setLayoutParams(params);
					mShadow.setPivotX(width / 2);
					mShadow.setPivotY(height / 2);
					mShadow.setScaleX(1 + wRatio * 2);
					mShadow.setScaleY(1 + hRatio * 2);
					mShadow.bringToFront();
				}
				showOnFocusTranslAnimation(v);
				if (mShadow != null) {
					mShadow.setVisibility(View.VISIBLE);
				}
//				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
//					v.setElevation(10);
//					v.setOutlineProvider(new ViewOutlineProvider() {
//
//						@Override
//						public void getOutline(View view, Outline outline) {
//							// TODO Auto-generated method stub
//							outline.setRect(0, 0, view.getWidth(), view.getHeight());
//						}
//					});
//				} else {
//					ViewCompat.setElevation(v, 10);
//				}
			} else {
				mShadow.setVisibility(View.INVISIBLE);
				showLooseFocusTranslAinimation(v);
//				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
//					v.setOutlineProvider(null);
//					v.setElevation(0);
//				} else {
//					ViewCompat.setElevation(v, 0);
//				}
			}
		}
	};
	
	protected void showOnFocusTranslAnimation(View v) {
		if (v instanceof TextView) {
			View view = (View) v.getParent();
			view.bringToFront();
		}
		
		v.bringToFront();
		Animation msAnimation = null;
		msAnimation = animEffect.ScaleAnimation(1.0F, 1.1F, 1.0F, 1.1F, 400);
		AnimationSet set = new AnimationSet(true);
		set.addAnimation(msAnimation);
		set.setFillAfter(true);
		set.setInterpolator(new OvershootInterpolator());
		v.startAnimation(set);
	}

	protected void showLooseFocusTranslAinimation(View v) {
		Animation msAnimation = null;
		AnimationSet set = new AnimationSet(true);
		msAnimation = animEffect.ScaleAnimation(1.1F, 1.0F, 1.1F, 1.0F, 400);
		set = new AnimationSet(true);
		set.addAnimation(msAnimation);
		set.setFillAfter(true);
		set.setInterpolator(new OvershootInterpolator());
		v.startAnimation(set);
	}
	
	/**
	 * 在带有视频播放窗口的子类中实现该方法
	 */
	public void scrollVideoView() {};
}
