package com.wasu.launcher.Fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wasu.launcher.R;
import com.wasu.launcher.domain.MusicContent;
import com.wasu.launcher.domain.MusicContents;
import com.wasu.launcher.domain.MusicToView;
import com.wasu.launcher.interfaces.MyFragmentListener;
import com.wasu.launcher.utils.ScaleAnimEffect;
import com.wasu.launcher.utils.Utils;
import com.wasu.launcher.view.SmoothHorizontalScrollView;
import com.wasu.launcher.view.SmoothHorizontalScrollView.ScrollViewListener;
import com.wasu.nostra13.universalimageloader.core.ImageLoader;
import com.wasu.vod.aidl.SDKOperationManager;
import com.wasu.vod.aidl.SDKOperationManager.MyListener;

public class MusicFragment extends BaseFragment implements OnClickListener, Callback {

	/** 取得数据 */
	private static final int GETDATA_REC = 0x1011;
	/** set数据 */
	private static final int SETDATA_REC = 0x1101;

	private View view;
	Handler myhandler;
	HandlerThread handlerThread;	
	
	Handr3 handr3 = new Handr3(this);
	
	//点歌台
	private RelativeLayout rl_music_platform;
	//最近演唱
	private RelativeLayout rl_music_recent;
	//曲库
	private RelativeLayout rl_music_lib;
	//MV库
	private RelativeLayout rl_music_mv;
	//音乐排行
	private RelativeLayout rl_music_rank;
	//MV排行
	private RelativeLayout rl_music_mv_rank;
	//广告位
	private RelativeLayout rl_music_ad;
	
	private List<TextView> mMusicList;
	private List<TextView> mMVList;

	public MusicFragment(ScrollViewListener listener) {
		mScrollViewListener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (container == null) {
			return null;
		}
		if (null == view) {
			view = inflater.inflate(R.layout.fragment_music, container, false);
		}
		
		smManager = new SDKOperationManager(getActivity());
		smManager.registerSDKOperationListener(this);
		return view;
	}

	@Override
	protected void findViewById() {
		mScrollView = (SmoothHorizontalScrollView) view.findViewById(R.id.scroll_view_music);
		
		rl_music_platform = (RelativeLayout) view.findViewById(R.id.rl_music_platform);
		
		rl_music_recent = (RelativeLayout) view.findViewById(R.id.rl_music_recent);
		
		rl_music_lib = (RelativeLayout) view.findViewById(R.id.rl_music_lib);
		
		rl_music_mv = (RelativeLayout) view.findViewById(R.id.rl_music_mv);
		
		rl_music_ad = (RelativeLayout) view.findViewById(R.id.rl_music_ad);
		
		rl_music_rank = (RelativeLayout) view.findViewById(R.id.rl_music_rank);
		
		rl_music_mv_rank = (RelativeLayout) view.findViewById(R.id.rl_music_mv_rank);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handlerThread = new HandlerThread("wasu-user");
		handlerThread.start();
		myhandler = new Handler(handlerThread.getLooper(), this);
		SharedPreferences spd = getActivity().getSharedPreferences("mydevice", Context.MODE_APPEND);
	}

	String[] strkey = new String[] { "TYPE", "param" };


	private MyFragmentListener mCallback;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// 这是为了保证Activity容器实现了用以回调的接口。如果没有，它会抛出一个异常。
		try {
			mCallback = (MyFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement MyFragmentListener");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		animEffect = new ScaleAnimEffect();
		mMusicList = new ArrayList<TextView>();
		mMVList = new ArrayList<TextView>();
		findViewById();
		setListener();
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		smManager.unRegisterSDKOperationListener();
		super.onDestroyView();
	}

	@Override
	public void onStop() {
		Long timeLong = (System.currentTimeMillis() - startTime);
		String[] strs = new String[] { Utils.getWasuTime() + "|" + Utils.getTVID() + "|ott_gr|ott|||K歌||" + timeLong+"|||" };
		Utils.sendMessage(MusicFragment.this.getActivity(), strs, "wasu_page_access_info");
		super.onStop();
	}

	@SuppressLint("NewApi")
	public void onDestroy() {
		super.onDestroy();
		if (myhandler != null) {
			myhandler.removeCallbacksAndMessages(null);
		}
		if (handlerThread != null) {
			if (Build.VERSION.SDK_INT >= 18) {
				handlerThread.quitSafely();
			} else {
				handlerThread.quit();
			}
			handlerThread = null;
		}
	}

	@Override
	protected void loadViewLayout() {
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		mScrollView.setScrollViewListener(mScrollViewListener);
		
		rl_music_platform.setOnFocusChangeListener(mFocusChangeListener);
		rl_music_platform.setOnClickListener(this);
		
		rl_music_recent.setOnFocusChangeListener(mFocusChangeListener);
		rl_music_recent.setOnClickListener(this);
		
		rl_music_lib.setOnFocusChangeListener(mFocusChangeListener);
		rl_music_lib.setOnClickListener(this);
		
		rl_music_mv.setOnFocusChangeListener(mFocusChangeListener);
		rl_music_mv.setOnClickListener(this);
		
		rl_music_ad.setOnFocusChangeListener(mFocusChangeListener);
		rl_music_ad.setOnClickListener(this);
		
		for (int i = 0; i < rl_music_rank.getChildCount(); i++) {
			TextView tv = (TextView) rl_music_rank.getChildAt(i);
			tv.setOnFocusChangeListener(mFocusChangeListener);
			tv.setOnClickListener(this);
			mMusicList.add(tv);
		}
		
		for (int i = 0; i < rl_music_mv_rank.getChildCount(); i++) {
			TextView tv = (TextView) rl_music_mv_rank.getChildAt(i);
			tv.setOnFocusChangeListener(mFocusChangeListener);
			tv.setOnClickListener(this);
			mMVList.add(tv);
		}
	}

	@Override
	public void onClick(View v) {
		
		
		if (v.getTag() != null) {
			
			try {
		        MusicToView  toview = (MusicToView) v.getTag();
			    Intent intent = new Intent();
			    intent.setAction(toview.getType());
			    intent.addCategory(Intent.CATEGORY_DEFAULT);
			    
			    Log.e("liqxtest", "liqxtestmusic: action=" + toview.getType() + " content=" + toview.getMusicViewContent().toString());
			    intent.putExtra("content", toview.getMusicViewContent().toString());
			    
			    this.startActivity(intent);
			} catch (Exception e) {
				Log.e("liqx", e.getLocalizedMessage());
			}
		}


	}

	public void onResume() {
		super.onResume();
		//PlayUtils.getInstance().playstop();
		if (this.arg0 != null) {
		    myhandler.sendMessageDelayed(myhandler.obtainMessage(GETDATA_REC),500);
		}

	};

	MyListener arg0 = null;

	@Override
	public void onConnnected(MyListener arg0) {
		this.arg0 = arg0;
		if (myhandler != null && this.isVisible()) {
			myhandler.sendMessage(myhandler.obtainMessage(GETDATA_REC));
		}
	}

	class Handr3 extends Handler {
		WeakReference<Fragment> wfactivity = null;

		public Handr3(Fragment fragment) {
			wfactivity = new WeakReference<Fragment>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			final MusicFragment ragment = (MusicFragment) wfactivity.get();
			if (ragment != null) {
				if (ragment.isVisible()) {
					if (msg.what == SETDATA_REC) {
						
						if (msg.obj != null) {
							
							MusicContents mcs = (MusicContents) msg.obj;
							
							if (mcs.getList() != null) {
								
								for (int i = 0; i < mcs.getList().size(); i++) {
									final MusicContent mc = mcs.getList().get(i);
									if (mc != null) {
										if (mc.getShowType() == 1) {
											
											new Thread(new Runnable() {

												@Override
												public void run() {
													// TODO Auto-generated method stub
													
													Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);
													final Bitmap bm = ImageLoader.getInstance().loadImageSync(mc.getShowImage());
													
													handr3.post(new Runnable() {

														@Override
														public void run() {
															// TODO Auto-generated method stub
															try {
																if (ragment != null && ragment.isVisible() && bm != null) {
																	((ImageView) rl_music_platform.findViewById(R.id.iv_music_platform)).setImageDrawable(new BitmapDrawable(view.getResources(), bm));
																	rl_music_platform.setTag(mc.getToview());
																	//((TextView)(view.findViewById(R.id.us_vd_0))).setText(mc.getToview().getMusicViewContent().getName());
//															        bgList.get(0).setTag(mc.getToview());
																}
															} catch (Exception e) {
																
															}
															
														}
														
													});
													
												}}).start();
											
											
											
										} else if (mc.getShowType() == 2) {
											
											new Thread(new Runnable() {

												@Override
												public void run() {
													// TODO Auto-generated method stub
													
													Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);
													final Bitmap bm = ImageLoader.getInstance().loadImageSync(mc.getShowImage());
													
													handr3.post(new Runnable() {

														@Override
														public void run() {
															// TODO Auto-generated method stub
															try {
																if (ragment != null && ragment.isVisible() && bm != null) {
																	rl_music_recent.findViewById(R.id.iv_music_recent).setBackground(new BitmapDrawable(view.getResources(), bm));
																	rl_music_recent.setTag(mc.getToview());
																	//((TextView)(view.findViewById(R.id.us_vd_1))).setText(mc.getToview().getMusicViewContent().getName());
//															        bgList.get(1).setTag(mc.getToview());
																}
															} catch (Exception e) {
																
															}
															
														}
														
													});
													
												}}).start();
											
										} else if (mc.getShowType() == 3) {
											
											new Thread(new Runnable() {

												@Override
												public void run() {
													// TODO Auto-generated method stub
													
													Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);
													final Bitmap bm = ImageLoader.getInstance().loadImageSync(mc.getShowImage());
													
													handr3.post(new Runnable() {

														@Override
														public void run() {
															// TODO Auto-generated method stub
															try {
																if (ragment != null && ragment.isVisible() && bm != null) {
															        ((ImageView)rl_music_lib.findViewById(R.id.iv_music_lib)).setImageDrawable(new BitmapDrawable(view.getResources(), bm));
															        rl_music_lib.setTag(mc.getToview());
//															        bgList.get(2).setTag(mc.getToview());
															        for (int i = 0; i < mc.getSongList().size() && i < 4; i++) {
															        	TextView tv = mMusicList.get(i);
															        	tv.setText(mc.getSongList().get(i).getToview().getMusicViewContent().getName());
																		tv.setTag(mc.getSongList().get(i).getToview());
																	}
//															        mvList.get(i).setTag(mc.getSongList().get(i).getToview());
																}
															} catch (Exception e) {
																Log.e("liqxtesr", "liqxtest"+e.getLocalizedMessage());
																
															}
															
														}
														
													});
													
												}}).start();
											
										} else if (mc.getShowType() == 5) {
											
											new Thread(new Runnable() {

												@Override
												public void run() {
													// TODO Auto-generated method stub
													
													Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);
													final Bitmap bm = ImageLoader.getInstance().loadImageSync(mc.getShowImage());
													
													handr3.post(new Runnable() {

														@Override
														public void run() {
															// TODO Auto-generated method stub
															try {
																if (ragment != null && ragment.isVisible() && bm != null) {
																	((ImageView) rl_music_mv.findViewById(R.id.iv_music_mv)).setImageDrawable(new BitmapDrawable(view.getResources(), bm));
															        rl_music_mv.setTag(mc.getToview());
//																	bgList.get(3).setTag(mc.getToview());
															        //((TextView)(view.findViewById(R.id.us_vd_2))).setText(mc.getToview().getMusicViewContent().getName());
															        for (int i = 0; i < mc.getUgcList().size() && i <4; i++) {
															        	TextView tv = mMVList.get(i);
															        	tv.setText(mc.getUgcList().get(i).getToview().getMusicViewContent().getName());
															        	tv.setTag(mc.getUgcList().get(i).getToview());
//															        	ugcList.get(i).setTag(mc.getUgcList().get(i).getToview());
																	}
																}
															} catch (Exception e) {
																
															}
															
														}
														
													});
													
												}}).start();
											
											
											
											
										} else if (mc.getShowType() == 7) {
											ImageView imageView = (ImageView) rl_music_ad.findViewById(R.id.iv_music_ad);
											ImageLoader.getInstance().displayImage(mc.getShowImage(), imageView);
											rl_music_ad.setTag(mc.getToview());
//											ragment.setImage((ImageView) ((FrameLayout) (ragment.bgList.get(4).getParent())).getChildAt(1), mc.getShowImage());
//											ragment.bgList.get(4).setTag(mc.getToview());
											
										}
									}
								}
								
							}

							
						} else {

						}
					}

				}
			}
		}
	}

	@Override
	public boolean handleMessage(Message msg) {

		if (msg.what == GETDATA_REC) {
			
			try {
				
				/*InputStream fi = null;
				try {
					fi = MusicFragment.this.getActivity().getApplicationContext().getAssets().open("test.txt");
				} catch (IOException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				} 
				
				byte[] aa = AQUtility.toBytes(fi);*/

				String string = arg0.wasu_musicScreen_data_query();
				
//				Log.d("chenchen", "K歌屏 数据返回：" + string);
				if (!TextUtils.isEmpty(string)) {
					if (handr3 != null) {
						MusicContents reconmmendBean = gson.fromJson(string, MusicContents.class);
						if (reconmmendBean != null) {
							handr3.sendMessage(handr3.obtainMessage(SETDATA_REC, reconmmendBean));
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public void Refresh() {
		if (myhandler != null) {
			myhandler.sendMessage(myhandler.obtainMessage(GETDATA_REC));
		}
	}
}
