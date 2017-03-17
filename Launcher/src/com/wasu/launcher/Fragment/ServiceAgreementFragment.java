package com.wasu.launcher.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.Toast;

import com.wasu.launcher.NewbieGuideActivity;
import com.wasu.launcher.R;
import com.wasu.launcher.interfaces.MyFragmentListener;
import com.wasu.launcher.utils.ScaleAnimEffect;
import com.wasu.launcher.view.ScrollViewProgress;
import com.wasu.launcher.view.ScrollViewProgress.OnScrollProgressListener;
import com.wasu.launcher.view.VerticalSeekBar;
import com.wasu.vod.aidl.SDKOperationManager.MyListener;

public class ServiceAgreementFragment extends BaseFragment implements OnFocusChangeListener, OnScrollProgressListener, OnClickListener {
	private View view;
	private Button agreed, no_agreed;
	private VerticalSeekBar SeekBar;
	private ScrollViewProgress scrollView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	private MyFragmentListener mCallback;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// 这是为了保证Activity容器实现了用以回调的接口。如果没有，它会抛出一个异常。
		try {
			mCallback = (MyFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (container == null) {
			return null;
		}
		if (null == view) {

			view = inflater.inflate(R.layout.layout_agreement, container, false);
		}
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		// views = new ArrayList<View>();
		animEffect = new ScaleAnimEffect();
		findViewById();
		setListener();
	}

	@Override
	public void onConnnected(MyListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void loadViewLayout() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void findViewById() {
		agreed = (Button) view.findViewById(R.id.agreed_btn);
		no_agreed = (Button) view.findViewById(R.id.no_agreed_btn);
		SeekBar = (VerticalSeekBar) view.findViewById(R.id.seekBar);
		SeekBar.setMax(100);
		scrollView = (ScrollViewProgress) view.findViewById(R.id.scrollview_agre);
		scrollView.setFocusInDescendants(true);
		setScrollViewProgress(scrollView);

	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		agreed.setOnClickListener(this);
		no_agreed.setOnClickListener(this);
		agreed.setFocusable(true);
		no_agreed.setFocusable(true);
		scrollView.setOnScrollProgressListener(this);
		// agreed.setOnFocusChangeListener(this);
		// no_agreed.setOnFocusChangeListener(this);
		// scrollViewProgress.setOnScrollProgressListener(this);
	}

	private ScaleAnimEffect animEffect;

//	private void showOnFocusTranslAnimation(View viwe) {
//		viwe.bringToFront();
//		Animation msAnimation = null;
//		msAnimation = animEffect.ScaleAnimation(1.0F, 1.05F, 1.0F, 1.05F);
//		AnimationSet set = new AnimationSet(true);
//		set.addAnimation(msAnimation);
//		set.setFillAfter(true);
//		viwe.startAnimation(set);
//	}

//	private void showLooseFocusTranslAinimation(View viwe) {
//		Animation msAnimation = null;
//		AnimationSet set = new AnimationSet(true);
//		msAnimation = animEffect.ScaleAnimation(1.05F, 1.0F, 1.05F, 1.0F);
//		set = new AnimationSet(true);
//		set.addAnimation(msAnimation);
//		set.setFillAfter(true);
//		viwe.startAnimation(set);
//		// int width = (scrollView.getChildAt(0).getWidth())
//		// - (scrollView.getWidth());
//		// if (x == width) {
//		//
//		//
//		// } else if (x == 0) {
//		//
//		//
//		//
//		// } else {
//		//
//		// }
//	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {

		if (hasFocus) {
			showOnFocusTranslAnimation(v);
		} else {
			showLooseFocusTranslAinimation(v);
		}
	}

	@Override
	public void onClick(View v) {
		SharedPreferences sp = getActivity().getSharedPreferences("mywelcome", Context.MODE_APPEND);
		Editor e = sp.edit();
		if (v == agreed) {
			e.putBoolean("isagreed", true);
			e.commit();
			if (mCallback != null) {
				mCallback.replaceFragment(1, 2);
			}
			if (((NewbieGuideActivity) getActivity()).getIsActiveSuccessfully()) {
				
				if (((NewbieGuideActivity) getActivity()).getIsAuthComplete()) {
					
					if (((NewbieGuideActivity) getActivity()).getIsBussinessInitComplete()) {

			    	    ((NewbieGuideActivity) getActivity()).setResult(
				      		    Activity.RESULT_OK, null);
				        ((NewbieGuideActivity) getActivity()).finishActivity(100);
				        
					} else {
						Toast.makeText(getActivity().getApplicationContext(),
								"请等待，业务初始化还未完成！", Toast.LENGTH_LONG).show();
					}
				    
				} else {
					
					Toast.makeText(getActivity().getApplicationContext(),
							"请等待，还未认证！", Toast.LENGTH_LONG).show();
				}

			} else {

				Toast.makeText(getActivity().getApplicationContext(),
						"请等待，还未激活！", Toast.LENGTH_LONG).show();
			}

		} else if (v == no_agreed) {
			e.putBoolean("isagreed", false);
			e.commit();

			// int pid = android.os.Process.myPid(); //获取当前应用程序的PID
			// android.os.Process.killProcess(pid); //杀死当前进程

		}
		e.commit();
	}

	@Override
	public void onScrollProgress(int progress) {
		SeekBar.setProgress(progress);

	}

}
