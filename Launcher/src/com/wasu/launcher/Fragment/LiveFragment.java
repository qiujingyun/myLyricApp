package com.wasu.launcher.Fragment;

import java.util.ArrayList;
import java.util.List;

import org.ngb.media.PlayerEvent;
import org.ngb.media.PlayerListener;
import org.ngb.media.VODEvent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Build;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wasu.android.volley.RequestQueue;
import com.wasu.android.volley.toolbox.ImageLoader;
import com.wasu.launcher.R;
import com.wasu.launcher.domain.LiveTotalCountResult;
import com.wasu.launcher.domain.LiveTotalCountResult.Items;
import com.wasu.launcher.domain.RankListResponseBody;
import com.wasu.launcher.domain.RecommendResponseResult;
import com.wasu.launcher.domain.WasuContent;
import com.wasu.launcher.domain.WasuInfo;
import com.wasu.launcher.domain.WasuItem;
import com.wasu.launcher.interfaces.MyFragmentListener;
import com.wasu.launcher.utils.Constant;
import com.wasu.launcher.utils.Packages;
import com.wasu.launcher.utils.PlayUtils.onPlayListener;
import com.wasu.launcher.utils.Utils;
import com.wasu.launcher.view.HotItemView;
import com.wasu.launcher.view.MarqueeTextView;
import com.wasu.launcher.view.ScrollViewProgress.OnScrollProgressListener;
import com.wasu.launcher.view.SmoothHorizontalScrollView;
import com.wasu.launcher.view.SmoothHorizontalScrollView.ScrollViewListener;
import com.wasu.launcher.view.VerticalSeekBar;
import com.wasu.vod.VodDetailsActivity;
import com.wasu.vod.aidl.SDKOperationManager;
import com.wasu.vod.aidl.SDKOperationManager.MyListener;
import com.wasu.vod.domain.ChildFolderResponseResult;
import com.wasu.vod.domain.CustomContent;
import com.wasu.vod.domain.FolderResponseResult;
import com.wasu.vod.domain.RequestAsyncTaskLoader;
import com.wasu.vod.domain.WasuFolder;
import com.wasu.vod.utils.SwitchUtils;
import com.wasu.vod.utils.WasuUtils;
import com.wasu.vod.aidl.WasuBouChInfor;
import com.wasu.vod.aidl.WasuBouInfor;
import com.wasu.vod.aidl.WasuChInfor;
import com.wasu.vod.aidl.WasuEdit;
import com.wasu.vod.aidl.WasuEditChRelated;
import com.wasu.vod.aidl.WasuEditInfor;

public class LiveFragment extends BaseFragment implements OnFocusChangeListener, OnClickListener, OnScrollProgressListener, Callback,
		onPlayListener ,LoaderManager.LoaderCallbacks<Object>{

	/** This is get playURL and Get BIG Data */
	private static final int GETPLAYURIANDBIGDATA = 0x10070;
	/** This is PLay */
	private static final int PLAY = 0x10071;
	/** This is PLayStop */
	private static final int PLAYSTOP = 0x10072;
	/** SET DATA hot and 大家都在看 */
	private static final int SETHOTANDSEE = 0x10073;
	/** 第一次播放 */
	private static final int ONEPLAY = 0x10074;
	/** 看吧频道头部 */
	private static final String LIVE_KANBA_ID_1 = "DVBOTT_HD_10003300";
	/** 看吧频道列表 */
	private static final String LIVE_KANBA_ID_2 = "DVBOTT_HD_10003400";
	public class LiveLoaderID {
		public static final int GETPLAYURI = 0x20170;
		public static final int GETBOUCHALLCH = 0x20171;
		public static final int GETHOTANDSEE = 0x20172;
		public static final int GETEVERYONESEE = 0x20173;
		public static final int GETREALITEM = 0x20180;
		public static final int GETREALITEM_END = 0x2018E;
		public static final int GETKANBA = 0x2018F;
		public static final int GETKANBAEPG = 0x20190;
		
		public static final int GETEPG = 0x20196;
	}
	private class WindowMessageID {		
		public static final int MEDIA_PLAY = 0x000010008;		
		public static final int MEDIA_STOP = 0x000010009;		
		public static final int MEDIA_PAUSE = 0x00001000A;		
		public static final int MEDIA_RESUME = 0x00001000B;		
		public static final int MEDIA_SEEK = 0x00001000C;		
		public static final int MEDIA_DESTROY = 0x00001000D;
	}

	private static final String TAG = LiveFragment.class.getSimpleName();

	private View view;
	/** 背景图片 */
	private List<ImageView> lv_bg_list = null;
	/** 图片 */
	private List<ImageView> lv_im_list = null;
	/** 节目文字 */
	private List<TextView> lv_txt_list = null;
	/** include背景图片 */
	// private SurfaceView surfaceView;
	//private PlayUtils playUtils;
	private WasuBouChInfor live_channels;

	private VerticalSeekBar verticalSeekBar;
	private MyFragmentListener mCallback;
	/** 播放视频的位置 */
	private Rect rect;
	/** 当前是否显示 */
	private boolean isHint = false;
	/** imageLoader */
	//private ImageLoader imageLoader;

	private RequestQueue mQueue;
	/** 播放地址 */
	private String string = "";

	/** gson解析 */
	private Gson gson;
	/** 右侧的控件 */
	private List<HotItemView> hotItemViewList;
	/** 要传的参数数组 */
	private String[] strkey = new String[] { "TYPE", "param", "chid" };

	boolean isRunOne = false;
	
//	private Player mPlayer;
//	private int mPlayerStatus;


	/** 获取数据刷新线程 */
	private Handler handler;
	private HandlerThread handlerThread;
	/** 单独用于过滤节目单的子线程 */
	private Handler childHandler;
	private HandlerThread childThread;

	List<Items> itmItems;
	//常看频道列表
	private List<LinearLayout> mOftenChList;

	//视频窗
	private SurfaceView surfaceView;
	//视频窗加载遮罩
	private ImageView im_surfaceView;
	//视频窗获焦遮罩
	private ImageView surface_mask;
	//视频窗布局
	private RelativeLayout rl_live_video_view;
	//常看频道
	private MarqueeTextView live_often_epg_0;
	private MarqueeTextView live_often_epg_1;
	private MarqueeTextView live_often_epg_2;
	//电视回放
	private RelativeLayout rl_live_tv_playback;
	//热剧回放
	private RelativeLayout rl_live_hot_playback;
	private ImageView iv_live_hot_playback;
	//看吧频道
	private RelativeLayout rl_live_kanba;
	private ImageView iv_live_kanba;
	private MarqueeTextView live_kanba_epg0;
	private MarqueeTextView live_kanba_epg1;
	private MarqueeTextView live_kanba_epg2;
	private MarqueeTextView live_kanba_epg3;
	private MarqueeTextView live_kanba_epg4;

	
	//热剧回放子栏目
	private List<LinearLayout> mHotPlayBackList;
	private List<LinearLayout> mKanBaList;
	
	/** 热剧回放栏目编码 */
	private final static String HOT_SERIES_PLAYBACK_FOLDER_CODE = "124899_1";
	
	/** 在子线程中过滤出当前节目 */
	private final static int MSG_GET_KANBA_EPG = 0x110;
	/** 在主线程中刷新当前节目 */
	private final static int MSG_REFRESH_KANBA_EPG = 0x111;
	/** 在子线程中过滤出当前节目 */
	private final static int MSG_GET_HOT_LIVE_EPG = 0x112;
	/** 在主线程中刷新当前节目 */
	private final static int MSG_REFRESH_HOT_LIVE_EPG = 0x113;

	public LiveFragment(ScrollViewListener listener) {
		mScrollViewListener = listener;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("Live fr Line 80 ", "onCreate");
		handlerThread = new HandlerThread("live");
		handlerThread.start();
		childThread = new HandlerThread("live_thread");
		childThread.start();
		handler = new Handler(handlerThread.getLooper(), this);
		childHandler = new Handler(childThread.getLooper(), this);
	}

	public void onStart() {
		super.onStart();
		Log.e("Live fr Line 84 ", "onStart");
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.e("Live fr Line 91 ", "onAttach");
		// 这是为了保证Activity容器实现了用以回调的接口。如果没有，它会抛出一个异常。
		try {
			mCallback = (MyFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.e("Live fr Line 105 ", "onCreateView");
		if (container == null) {
			return null;
		}
		if (null == view) {
			view = inflater.inflate(R.layout.fragment_live, container, false);
			initview();
			gson = new Gson();
			rect = new Rect();
			//mQueue = Volley.newRequestQueue(context);
			//imageLoader = MyVolley.getImageLoader();
//			playUtils = PlayUtils.getInstance();
			playUtils.setOnPlay(this);
			itmItems = new ArrayList<LiveTotalCountResult.Items>();
		}
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.e("Live fr Line 124", "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		//Log.e(this.getClass().getSimpleName(), "Line 155 onResume");
		Log.d("yingjh","onResume on Livefragment ");
		super.onResume();
		isHint = true;
		playUtils.setOnPlay(this);
		//if (handler != null) {
		//	handler.sendMessage(handler.obtainMessage(PLAYSTOP));
		//}
		im_surfaceView.setVisibility(View.VISIBLE);
		if (arg0 != null) {
			getLoaderManager().restartLoader(LiveLoaderID.GETPLAYURI,null,this);
			//getLoaderManager().restartLoader(LiveLoaderID.GETBOUCHALLCH,null,this);
			getLoaderManager().restartLoader(LiveLoaderID.GETHOTANDSEE,null,this);	
//			getLoaderManager().restartLoader(LiveLoaderID.GETEVERYONESEE,null,this);
			//热剧回放
			getLoaderManager().restartLoader(Constant.QUERY_CHILDFOLDER, 
					Utils.CreateChildFolderContentBundle(HOT_SERIES_PLAYBACK_FOLDER_CODE), this);
			//推荐系统取看吧频道
			getLoaderManager().restartLoader(LiveLoaderID.GETKANBA, null, this);
		} else {
			smManager = new SDKOperationManager(getActivity());
			smManager.registerSDKOperationListener(this);
		}
/*
		if (!TextUtils.isEmpty(string)) {
			try {
				if (handler != null)
					handler.sendMessageDelayed(handler.obtainMessage(PLAY), 500);
			} catch (InstantiationError e) {
				Log.e("InstantiationError Live Lin 173", e.toString());
			}
		}
		*/
		if (handler != null){
			handler.sendMessage(handler.obtainMessage(WindowMessageID.MEDIA_RESUME));
		}
		
	}

	@Override
	public void onStop() {
		Log.e("Live fr Line 155", "onStop");
		isHint = false;
		if (null != mQueue) {
			mQueue.stop();
		}

		Long timeLong = (System.currentTimeMillis() - startTime);
		String[] strs = new String[] { Utils.getWasuTime() + "|" + Utils.getTVID() + "|ott_zb|ott|||直播||"+timeLong+"|||"};
		Utils.sendMessage(LiveFragment.this.getActivity(), strs, "wasu_page_access_info");
		if (handler != null){
			//handler.sendMessage(handler.obtainMessage(PLAYSTOP));
			handler.sendMessage(handler.obtainMessage(WindowMessageID.MEDIA_STOP));
			handler.removeMessages(WindowMessageID.MEDIA_PLAY);
		}
		super.onStop();
	}

	@Override
	public void onPause() {
		playUtils.setOnPlay(null);
		if (handler != null){
			//handler.sendMessage(handler.obtainMessage(PLAYPAUSE));
			handler.removeCallbacksAndMessages(null);
			handler.sendMessage(handler.obtainMessage(WindowMessageID.MEDIA_PAUSE));
		}
		super.onPause();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
	}

	@SuppressLint("NewApi")
	@Override
	public void onDestroy() {
		super.onDestroy();

		if (handler != null){
			handler.removeCallbacksAndMessages(null);
		}
		if(handler2!=null){
			handler2.removeCallbacksAndMessages(null);
		}
		if (childHandler != null) {
			childHandler.removeCallbacksAndMessages(null);
		}
		if (handlerThread != null) {
			if (Build.VERSION.SDK_INT >= 18) {
			    handlerThread.quitSafely();
			} else {
			handlerThread.quit();
			}
			handlerThread = null;
		}
		if (childThread != null) {
			if (Build.VERSION.SDK_INT >= 18) {
				childThread.quitSafely();
			} else {
				childThread.quit();
			}
			childThread = null;
		}
	}

	MyListener arg0 = null;

	@Override
	public void onConnnected(MyListener arg0) {
		this.arg0 = arg0;
			if (this.isVisible()) {

				getLoaderManager().restartLoader(LiveLoaderID.GETPLAYURI,null,this);
				//getLoaderManager().restartLoader(LiveLoaderID.GETBOUCHALLCH,null,this);
				getLoaderManager().restartLoader(LiveLoaderID.GETHOTANDSEE,null,this);
//				getLoaderManager().restartLoader(LiveLoaderID.GETEVERYONESEE,null,this);
				//取编排-热剧回放
				getLoaderManager().restartLoader(Constant.QUERY_CHILDFOLDER, 
						Utils.CreateChildFolderContentBundle(HOT_SERIES_PLAYBACK_FOLDER_CODE), this);
				//推荐系统取看吧频道
				getLoaderManager().restartLoader(LiveLoaderID.GETKANBA, null, this);
			}

	}

	String fdid = "";

	private void setImage(final int paramInt, final String paramUrl, final String paramTxt, Object obj) {
		if (paramInt >= lv_im_list.size())
			return;
		if (paramUrl != null) {
			lv_im_list.get(paramInt).post(new Runnable() {
				@Override
				public void run() {
					
//					try {
//						Bitmap image = arg0.getImageFromSDK(paramUrl);
//						lv_im_list.get(paramInt).setImageBitmap(image);
//					} catch (RemoteException e) {
//						e.printStackTrace();
//					}
					imageLoader.get(paramUrl, ImageLoader.getImageListener(lv_im_list.get(paramInt), 0, 0));
				}
			});
		} else {
			lv_im_list.get(paramInt).setImageResource(android.R.color.transparent);
		}
		lv_txt_list.get(paramInt).setText(paramTxt);
		lv_bg_list.get(paramInt + 3).setTag(obj);
	}
	/*该setImage不设置paramTxt，后续取得数据后通过lv_txt_list.get(paramInt).setText(paramTxt)再设置*/
	private void setImage(final int paramInt, final String paramUrl, Object obj) {
		if (paramInt >= lv_im_list.size())
			return;
		if (paramUrl != null) {
			lv_im_list.get(paramInt).post(new Runnable() {
				@Override
				public void run() {

					imageLoader.get(paramUrl, ImageLoader.getImageListener(lv_im_list.get(paramInt), 0, 0));
				}
			});
		} else {
			lv_im_list.get(paramInt).setImageResource(android.R.color.transparent);
		}

		lv_bg_list.get(paramInt + 3).setTag(obj);
	}

	/** 初始化 */
	private void initview() {
		lv_bg_list = new ArrayList<ImageView>();
		hotItemViewList = new ArrayList<HotItemView>();
		//常看频道
		mOftenChList = new ArrayList<LinearLayout>();
		lv_im_list = new ArrayList<ImageView>();
		lv_txt_list = new ArrayList<TextView>();
		//热剧回放
		mHotPlayBackList = new ArrayList<LinearLayout>();
		//看吧频道
		mKanBaList = new ArrayList<LinearLayout>();
		findViewById();
		setListener();
	}

	@Override
	public void onDetach() {
		Log.e("Live fr Line 367", "onDetach");
		super.onDetach();
	}

	@Override
	protected void loadViewLayout() {

	}

	@Override
	protected void findViewById() {
		mScrollView = (SmoothHorizontalScrollView) view.findViewById(R.id.live_scroll_view);
		mScrollView.setScrollViewListener(mScrollViewListener);
		
		mShadow = (ImageView) view.findViewById(R.id.shadow);
		
		//直播
		rl_live_video_view = (RelativeLayout) view.findViewById(R.id.rl_live_video_view);
		surfaceView = (SurfaceView) view.findViewById(R.id.live_surface_view);
		im_surfaceView = (ImageView) view.findViewById(R.id.live_img_surface_view);
		surface_mask = (ImageView) view.findViewById(R.id.live_img_surface_mask);
		
		//常看频道
		for (int i = 0; i < 3; i++) {
			LinearLayout layout = (LinearLayout) view.findViewById(getResources().getIdentifier("ll_live_often_item_" + i, "id", context.getPackageName()));
			mOftenChList.add(layout);
		}
		live_often_epg_0 = (MarqueeTextView) view.findViewById(R.id.live_often_epg_0);
		live_often_epg_1 = (MarqueeTextView) view.findViewById(R.id.live_often_epg_1);
		live_often_epg_2 = (MarqueeTextView) view.findViewById(R.id.live_often_epg_2);
		
		//电视回放
		rl_live_tv_playback = (RelativeLayout) view.findViewById(R.id.rl_live_tv_playback);
		
		//热剧回放
		rl_live_hot_playback = (RelativeLayout) view.findViewById(R.id.rl_live_hot_playback);
		iv_live_hot_playback = (ImageView) view.findViewById(R.id.iv_live_hot_playback);
		for (int i = 0; i < 5; i++) {
			LinearLayout layout = (LinearLayout) view.findViewById(getResources().getIdentifier("ll_live_hot_playback_item_" + i, "id", context.getPackageName()));
			mHotPlayBackList.add(layout);
		}
		
		//看吧频道
		rl_live_kanba = (RelativeLayout) view.findViewById(R.id.rl_live_kanba);
		iv_live_kanba = (ImageView) view.findViewById(R.id.iv_live_kanba);
		for (int i = 0; i < 5; i++) {
			LinearLayout layout = (LinearLayout) view.findViewById(getResources().getIdentifier("ll_live_kanba_item_" + i, "id", context.getPackageName()));
			mKanBaList.add(layout);
		}
		live_kanba_epg0 = (MarqueeTextView) view.findViewById(R.id.live_kanba_epg0);
		live_kanba_epg1 = (MarqueeTextView) view.findViewById(R.id.live_kanba_epg1);
		live_kanba_epg2 = (MarqueeTextView) view.findViewById(R.id.live_kanba_epg2);
		live_kanba_epg3 = (MarqueeTextView) view.findViewById(R.id.live_kanba_epg3);
		live_kanba_epg4 = (MarqueeTextView) view.findViewById(R.id.live_kanba_epg4);
		
	}

	@Override
	protected void setListener() {
		// 设置监听事件
		//直播窗
		rl_live_video_view.setOnFocusChangeListener(mFocusChangeListener);
		rl_live_video_view.setOnClickListener(this);
		//常看频道
		for (LinearLayout layout : mOftenChList) {
			layout.setOnFocusChangeListener(mFocusChangeListener);
			layout.setOnClickListener(this);
		}
		//电视回放
		rl_live_tv_playback.setOnFocusChangeListener(mFocusChangeListener);
		rl_live_tv_playback.setOnClickListener(this);
		//热剧回放
		rl_live_hot_playback.setOnFocusChangeListener(mFocusChangeListener);
		rl_live_hot_playback.setOnClickListener(this);
		//热剧回放子栏目
		for (LinearLayout layout : mHotPlayBackList) {
			layout.setOnFocusChangeListener(mFocusChangeListener);
			layout.setOnClickListener(this);
		}
		//看吧频道
		rl_live_kanba.setOnFocusChangeListener(mFocusChangeListener);
		rl_live_kanba.setOnClickListener(this);
		for (LinearLayout layout : mKanBaList) {
			layout.setOnFocusChangeListener(mFocusChangeListener);
			layout.setOnClickListener(this);
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			if (v == hotItemViewList.get(4))
				hotItemViewList.get(4).setHONGColor();
			int id = v.getId();
			if (id == R.id.lv_bg_0) {
				if (isUp)
					setMessage(v, 1);
			} else if (id == R.id.lv_bg_1 || id == R.id.lv_bg_2 || id == R.id.lv_include_5) {
				if (isUp)
					setMessage(v, 1);
			} else if (id == R.id.lv_bg_3 || id == R.id.lv_bg_4 || id == R.id.lv_bg_5 || id == R.id.lv_bg_6) {
				if (!isUp)
					setMessage(0);
			} else {
			}
			if (v != lv_bg_list.get(0) && v != hotItemViewList.get(4))
				showOnFocusTranslAnimation((View) v.getParent());
		} else {
			if (v == hotItemViewList.get(4))
				hotItemViewList.get(4).setHEIColor();

			if (v != lv_bg_list.get(0) && v != hotItemViewList.get(4))
				showLooseFocusTranslAinimation((View) v.getParent());
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.rl_live_video_view:
			//直播
			//if (!TextUtils.isEmpty(fdid)) {
			Packages.toActivityByClassName(getActivity().getApplicationContext(), "com.wasu.live.MainLiveActivity", null,
						new String[] { "chid" }, new String[] { fdid }, null);
			//}
			break;
		case R.id.ll_live_often_item_0:
		case R.id.ll_live_often_item_1:
		case R.id.ll_live_often_item_2:
			//常看频道
			if (v.getTag() instanceof WasuItem) {
				initpaihang((WasuItem) v.getTag());
			}
			break;
		case R.id.rl_live_tv_playback:
			//电视回放入口
			if(SwitchUtils.isFreeAuthSwitchOn(this.getActivity().getApplicationContext())){
				Toast.makeText(this.getActivity().getApplicationContext(), "服务暂不提供", Toast.LENGTH_SHORT).show();
			}else{
				intentApp("PLAYBACK");
			}
			break;
		case R.id.rl_live_hot_playback:
			//热剧回放主栏目入口
			if(SwitchUtils.isFreeAuthSwitchOn(this.getActivity().getApplicationContext())){
				Toast.makeText(this.getActivity().getApplicationContext(), "服务暂不提供", Toast.LENGTH_SHORT).show();
			}else{
				Packages.toActivityByClassName(getActivity().getApplicationContext(), "com.wasu.vod.PlaybackActivity", null, new String[]{"TYPE"}, new String[]{"热剧回放"}, null);
			}
			break;
		case R.id.ll_live_hot_playback_item_0:
		case R.id.ll_live_hot_playback_item_1:
		case R.id.ll_live_hot_playback_item_2:
		case R.id.ll_live_hot_playback_item_3:
		case R.id.ll_live_hot_playback_item_4:
			//热剧回放子栏目入口，跳转到栏目详情页
			if (v.getTag() instanceof CustomContent) {
				CustomContent customContent = (CustomContent) v.getTag();
				Intent intent = new Intent();
				intent.setClass(context, VodDetailsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("param", customContent.getContentId());
				intent.putExtra("extra", customContent);
				intent.putExtra("TYPE", "HOTSERIESPLAYBACK");
				intent.putExtra("folderNameCurrent", customContent.getContentId());
				intent.putExtra("isFromFolder", 15);
				startActivity(intent);
			}
			break;
		case R.id.rl_live_kanba:
		case R.id.ll_live_kanba_item_0:
		case R.id.ll_live_kanba_item_1:
		case R.id.ll_live_kanba_item_2:
		case R.id.ll_live_kanba_item_3:
		case R.id.ll_live_kanba_item_4:
			if (v.getTag() instanceof WasuInfo) {
				if (handler != null) {
					handler.sendEmptyMessage(WindowMessageID.MEDIA_STOP);
				}
				WasuInfo wasuInfo = (WasuInfo) v.getTag();
				String fodderid = wasuInfo.getFodderid();
				Packages.toActivityByClassName(getActivity().getApplicationContext(), "com.wasu.live.MainLiveActivity", null,
						new String[] { "chid" }, new String[] { fodderid }, null);
			}
			break;
		default:
			break;
		}
	}

	// TODO
	private void initpaihang(WasuItem it) {

		if (it != null) {
			// Log.e("yingjh","initpaihang to com.wasu.live id="+it.getContentId());
			// Packages.startIntentMAIN(getActivity().getApplicationContext(),
			// "com.wasu.live", null,
			// new String[] { "chid" }, new String[] { it.getContentId() },
			// null);
			Packages.toActivityByClassName(getActivity().getApplicationContext(), "com.wasu.live.MainLiveActivity", null,
					new String[] { "chid" }, new String[] { it.getContentid() }, null);
		} else {
			Toast.makeText(getActivity(), "数据没取到", 0).show();
		}

	}

	// TODO
	private void initrank(WasuInfo wasuInfo) {
		/*
		 * String string = null; if ("1".equals(wasuInfo.getFoddertype())) {
		 * string = "MOVIE"; } else if ("2".equals(wasuInfo.getFoddertype())) {
		 * string = "SERIES"; } else { string = null; }
		 * //Log.e("yingjh","initrank to com.wasu.live id="
		 * +wasuInfo.getFodderid()); String[] str = { string, "",
		 * wasuInfo.getFodderid() };
		 * Packages.startIntentMAIN(getActivity().getApplicationContext(),
		 * "com.wasu.live", null, strkey, str, null); //
		 * Packages.startIntent(getActivity().getApplicationContext(), //
		 * "com.wasu.vod", "com.wasu.vod.VodDetailsActivity", // null, strkey,
		 * str, null);
		 */
		// Log.e("yingjh", "initrank="+wasuInfo.getFodderid());
		if (wasuInfo != null) {
			// Packages.startIntentMAIN(getActivity().getApplicationContext(),
			// "com.wasu.live", null,
			// new String[] { "chid" }, new String[] { wasuInfo.getFodderid() },
			// null);
			Packages.toActivityByClassName(getActivity().getApplicationContext(), "com.wasu.live.MainLiveActivity", null,
					new String[] { "chid" }, new String[] { wasuInfo.getFodderid() }, null);
		} else {
			Toast.makeText(getActivity(), "数据没取到", 0).show();
		}

	}

	private void intentApp(String msg) {
		// Intent intent =
		// getActivity().getPackageManager().getLaunchIntentForPackage("com.wasu.vod");
		// if (intent != null) {
		// Intent inten = new Intent();
		// inten.setComponent(new ComponentName("com.wasu.vod",
		// "com.wasu.vod.PlaybackActivity"));
		// startActivity(inten);
		// } else {
		// Toast.makeText(getActivity().getApplicationContext(),
		// "哟，赶紧下载安装这个APP吧", Toast.LENGTH_SHORT).show();
		// }

		Packages.toActivityByClassName(getActivity().getApplicationContext(), "com.wasu.vod.PlaybackActivity", null, null, null, null);
	}

	@Override
	public void onScrollProgress(int progress) {
		verticalSeekBar.setProgress(progress);
	}

	private class MyOnKeyListener implements OnKeyListener {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// Log.d(TAG, "MyOnKeyListener=== keyCode:" + keyCode +
			// " === event:"
			// + event + " === v.getId():" + v.getId());
			switch (event.getAction()) {
			case KeyEvent.ACTION_UP: // 键盘松开
				break;
			case KeyEvent.ACTION_DOWN: // 键盘按下
				if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
					int id = v.getId();
					if (id == R.id.lv_bg_7 || id == R.id.lv_bg_8 || id == R.id.lv_bg_9 || id == R.id.lv_bg_10) {
						if (mCallback != null) {
							setMessage(1);
							mCallback.replaceFragment(1, 2);
						}
						return true;
					}
				}else if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
					int id = v.getId();
					if ((id == R.id.lv_bg_0 )||(id== R.id.lv_include_1)) {
						if (mCallback != null) {
							//setMessage(1);
							mCallback.replaceFragment(1, 0);
						}
						return true;
					}
				}
				break;
			}
			return false;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Toast.makeText(getActivity().getApplicationContext(), "" +
		// resultCode, 0).show();
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == ONEPLAY) {
			// 播放地址
//			if (!isRunOne) {
				if (!TextUtils.isEmpty(string)) {
					if (isHint) {
						try {
							/** 显示就播放 */
							if (LiveFragment.this.isVisible()) {
								Log.d("yingjh", "ONEPLAY start");
								handler.removeMessages(WindowMessageID.MEDIA_PLAY);
								handler.sendMessage(handler.obtainMessage(WindowMessageID.MEDIA_PLAY, string));
							}
						} catch (InstantiationError e) {
						}
					}
//					isRunOne = true;
//				}
			}
		}else if(msg.what ==WindowMessageID.MEDIA_PLAY){
			Log.d("yingjh", "WindowMessageID.MEDIA_PLAY start");
			if (isHint) {
				if (msg.obj != null) {
					int location[] = new int[2];
					surfaceView.getLocationInWindow(location);
					rect = new Rect(location[0], location[1], location[0] + surfaceView.getWidth() + 1, location[1] + surfaceView.getHeight() + 1);
					playUtils.hplayer(String.valueOf(msg.obj), rect, true);
				}
			}
		}else if(msg.what ==WindowMessageID.MEDIA_STOP){
			playUtils.playdeallocate();
		}else if(msg.what ==WindowMessageID.MEDIA_PAUSE){
			playUtils.playpause();
		}else if(msg.what ==WindowMessageID.MEDIA_RESUME){
//			if(mPlayer != null){
//				Log.d("yingjh", "WindowMessageID.MEDIA_RESUME ");
//				mPlayerStatus = PlayerEvent.TYPE_START;
//				mPlayer.resume();
//			}else{
//				Object object = surfaceView.getTag();
//				if(object != null){
//					handler.removeMessages(WindowMessageID.MEDIA_PLAY);
//					handler.sendMessageDelayed(handler.obtainMessage(WindowMessageID.MEDIA_PLAY, object),1000);
//				}else{
//					isRunOne = false;//如果suraceView没有保存信息，当作第一次进入
//				}
//			}
		}else if(msg.what ==WindowMessageID.MEDIA_SEEK){
			
		}else if(msg.what ==WindowMessageID.MEDIA_DESTROY){
//			if(mPlayer != null){
//				mPlayer.deallocate();
//				mPlayer = null;
//			}
		}else if (msg.what == MSG_GET_KANBA_EPG) {
			WasuEdit itemEdit = null;
			WasuEditChRelated wasuEditChRelated = (WasuEditChRelated) msg.obj;
			int index = msg.arg1;
			if (wasuEditChRelated != null) {
				// 筛选和当前时间一致的节目单
				for (WasuEdit wasuEdit : wasuEditChRelated.wes) {
					if(wasuEdit != null) {
						if (isInCurrentTime(wasuEdit.startTime, wasuEdit.endTime)) {
							itemEdit = wasuEdit;
							break;
						}
					}
				}
			}
			
			//通知主线程刷新节目单
			if (handler2 != null) {
				Message message = handler2.obtainMessage();
				message.what = MSG_REFRESH_KANBA_EPG;
				message.arg1 = index;
				message.obj = itemEdit;
				handler2.sendMessage(message);
			}
		}else if (msg.what == MSG_GET_HOT_LIVE_EPG) {
			WasuEdit itemEdit = null;
			WasuEditChRelated wasuEditChRelated = (WasuEditChRelated) msg.obj;
			int index = msg.arg1;
			if (wasuEditChRelated != null) {
				// 筛选和当前时间一致的节目单
				for (WasuEdit wasuEdit : wasuEditChRelated.wes) {
					if (wasuEdit != null) {
						if (isInCurrentTime(wasuEdit.startTime, wasuEdit.endTime)) {
							itemEdit = wasuEdit;
							break;
						}
					}
				}
			}
			
			//通知主线程刷新节目单
			if (handler2 != null) {
				Message message = handler2.obtainMessage();
				message.what = MSG_REFRESH_HOT_LIVE_EPG;
				message.arg1 = index;
				message.obj = itemEdit;
				handler2.sendMessage(message);
			}
		}
		return true;
	}
	
	private PlayerListener mPlayerListener = new PlayerListener() {
		@Override
		public void OnPlayerEvent(PlayerEvent playerEvent) {
			// TODO Auto-generated method stub

			// TODO Auto-generated method stub
			Log.d("yingjh","###  type:"+playerEvent.getType()+",reason:"+playerEvent.getReason());
			if(playerEvent != null){
				if(playerEvent.getType() == VODEvent.TYPE_VOD_SUCCESS && playerEvent.getReason() == VODEvent.REASON_VOD_PAUSE){//播放暂停
					
				}else if(playerEvent.getType() == PlayerEvent.TYPE_SUCCESS ){//播放成功
					if(surfaceView != null){
						surfaceView.post(new Runnable() {							
							@Override
							public void run() {
								im_surfaceView.setVisibility(View.INVISIBLE);
								surfaceView.setVisibility(View.VISIBLE);
							}
						});
					}
				}
			}			
		}
		
	};

	private final static int UPDATE_HOTIEM_FROM_BIGDATA = 0x001212;
	private final static int UPDATE_HOTITEM_FROM_IPEPG = 0x0112121;
	private final static int UPDATE_HOTIEM_FROM_RECOMMSYS_REAL = 0x001210;
	
	private Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (LiveFragment.this.isVisible()) {
				switch (msg.what) {
//				case UPDATE_HOTIEM_FROM_BIGDATA:
//					LiveTotalCountResult ltcr = (LiveTotalCountResult) msg.obj;
//					fillHotItemsData(ltcr);
//					break;
				case UPDATE_HOTIEM_FROM_RECOMMSYS_REAL:
					RankListResponseBody rankreal = (RankListResponseBody) msg.obj;
					fillHotItemsDataReal(rankreal);
					//fillHotItemsDataWeek(rankreal);
					break;
//				case UPDATE_HOTIEM_FROM_RECOMMSYS_WEEK:
//					RankListResponseResult rankweek = (RankListResponseResult) msg.obj;
//					fillHotItemsDataWeek(rankweek);
//					break;
				case UPDATE_HOTITEM_FROM_IPEPG:
					if (msg.obj != null) {
						WasuEdit wasuEdit = (WasuEdit) msg.obj;
						int position = msg.arg1;
						if(position<hotItemViewList.size()){//更新hotItem
							HotItemView hotItemView = hotItemViewList.get(position);
							String st = ((wasuEdit.startTime).split(" "))[1];
							String end = ((wasuEdit.endTime).split(" "))[1];
							hotItemView.setTexttime(st + "~" + end);
							hotItemView.setTextDetails(wasuEdit.eventName);
							long currentTime = System.currentTimeMillis();
							long startTime = Utils.getLongTimeFromString(wasuEdit.startTime);
							long endTime = Utils.getLongTimeFromString(wasuEdit.endTime);
							hotItemView.setMaxProgress((int) (endTime - startTime));
							hotItemView.setProgress((int) (currentTime - startTime));
						}else{//更新大家都在看
							lv_txt_list.get(position-hotItemViewList.size()).setText(wasuEdit.eventName);
						}
					}else {
						Log.e(this.getClass().getSimpleName(), "WasuEdit is null");
					}

					break;
				case MSG_REFRESH_KANBA_EPG:
					int index = msg.arg1;
					WasuEdit itemEdit = (WasuEdit) msg.obj;
					if (itemEdit != null) {
						String startTime = itemEdit.getStartTime();
						String endTime = itemEdit.getEndTime();
						if (startTime != null) {
							 if (startTime.contains(" ")) {
								 startTime = startTime.split(" ")[1];
							}
							 if (startTime.contains(":")) {
								 startTime = startTime.substring(0, startTime.lastIndexOf(":"));
							}
						}
						
						if (endTime != null) {
							 if (endTime.contains(" ")) {
								 endTime = endTime.split(" ")[1];
							}
							 if (endTime.contains(":")) {
								 endTime = endTime.substring(0, endTime.lastIndexOf(":"));
							}
						}
						LinearLayout layout = mKanBaList.get(index);
						String epgString = startTime + "-" + endTime + " " + itemEdit.getEventName();
						TextView epg = (TextView) layout.getChildAt(1);
						epg.setText(epgString);
					}
					break;
				case MSG_REFRESH_HOT_LIVE_EPG:
					int chIndex = msg.arg1;
					WasuEdit edit = (WasuEdit) msg.obj;
					if (edit != null) {
						String startTime = edit.getStartTime();
						String endTime = edit.getEndTime();
						if (startTime != null) {
							 if (startTime.contains(" ")) {
								 startTime = startTime.split(" ")[1];
							}
							 if (startTime.contains(":")) {
								 startTime = startTime.substring(0, startTime.lastIndexOf(":"));
							}
						}
						
						if (endTime != null) {
							 if (endTime.contains(" ")) {
								 endTime = endTime.split(" ")[1];
							}
							 if (endTime.contains(":")) {
								 endTime = endTime.substring(0, endTime.lastIndexOf(":"));
							}
						}
						LinearLayout layout = mOftenChList.get(chIndex);
						String epgString = startTime + "-" + endTime + " " + edit.getEventName();
						TextView epg = (TextView) layout.getChildAt(1);
						epg.setText(epgString);
					}
					break;
				}

			}
		}
	};
//
//	private void fillHotItemsData(@NonNull LiveTotalCountResult ltcr) {
//		if (ltcr.getItems() != null) {
//			itmItems.addAll(ltcr.getItems());
//			for (int i = 0; i < ltcr.getItems().size(); i++) {
//				if (i < hotItemViewList.size()) {
//					
//					String st = ((ltcr.getItems().get(i).getBeginTime()).split(" "))[1];
//					String end = ((ltcr.getItems().get(i).getEndTime()).split(" "))[1];
//					hotItemViewList.get(i).setText(ltcr.getItems().get(i).getName(), st + "~" + end, ltcr.getItems().get(i).getCount(),
//							ltcr.getItems().get(i).getProgramName());
//					hotItemViewList.get(i).setTag(ltcr.getItems().get(i));
//					
//					// 从IPEPG中加载数据
//					Log.e("www", ltcr.getItems().get(i).getContentId() + "位置ChannelID" + i);
//					new Thread(new UpdateHomeItemRunnable(ltcr.getItems().get(i).getContentId(), i)).start();
//
//				} else {
//					// this is for "大家都在看"
//					String jpgurl = get_jpgurl_for_channelID(ltcr.getItems().get(i).getContentId());
//					String txt = ltcr.getItems().get(i).getProgramName();
//					setImage(i - hotItemViewList.size(), jpgurl, txt, ltcr.getItems().get(i));
//				}
//			}
//		}
//	}

	private void fillHotItemsDataReal(@NonNull RankListResponseBody rankreal) {
		if (rankreal == null
				|| rankreal.getItems() == null
				|| rankreal.getItems().size() == 0)
			return;

		for (int i = 0; i < mOftenChList.size()
				&& i < rankreal.getItems().size(); i++) {
			
			WasuItem wasuitem = rankreal.getItems().get(i);
			if (wasuitem != null) {
				//直播-常看频道 数据填充
				LinearLayout linearLayout = mOftenChList.get(i);
				linearLayout.setTag(wasuitem);
				TextView title = (TextView) linearLayout.findViewById(getResources().getIdentifier("live_often_tv_" + i, "id", getActivity().getPackageName()));
				title.setText(wasuitem.getName());
				
				//调用IPEPG接口获取节目单
				Bundle bundle = new Bundle();
				bundle.putString("chid", wasuitem.getContentid());
				getLoaderManager().restartLoader(LiveLoaderID.GETEPG + i, bundle, this);
			}
		}
	}
	
	private void fillHotItemsDataWeek(@NonNull RankListResponseBody rankweek) {
		if (rankweek == null
				|| rankweek.getItems() == null
				|| rankweek.getItems().size() == 0)
			return;
		
		for (int i = hotItemViewList.size(); i < rankweek.getItems()
				.size(); i++) {

				WasuItem wasuitem = new WasuItem();
				wasuitem = rankweek.getItems().get(i);

					// this is for "大家都在看"
			String jpgurl = get_jpgurl_for_channelID(wasuitem.getContentid());
				String txt = wasuitem.getProgramname();
				setImage(i - hotItemViewList.size(), jpgurl, txt, wasuitem);
				}
		
			}
	private void fillEveryoneSeeItems(RecommendResponseResult data){
		if (data == null || data.getContents() == null
				|| data.getContents().size() == 0){
			return;
		}	
			List<WasuInfo> infoList = data.getContents().get(0)
					.getInfoList();
			if (infoList != null&& infoList.size() != 0){
				for(int j=0;j<infoList.size();j++){
					WasuInfo wasuinfo = infoList.get(j);
					String jpgurl = wasuinfo.getImg();
					String chId = wasuinfo.getFodderid();
					//Log.d("yingjh","jpgurl="+jpgurl);
					//Log.d("yingjh","chId="+chId);
					setImage(j, jpgurl,wasuinfo);
					
					Bundle bundle = new Bundle();
					bundle.putString("chID", chId);
					bundle.putInt("position", j+hotItemViewList.size());
					getLoaderManager().restartLoader(LiveLoaderID.GETREALITEM+j+hotItemViewList.size(),bundle,this);
					
				}

			}

	}
	
	
	
//	private class UpdateHomeItemRunnable implements Runnable {
//		private String channelId = null;
//		private int position = 0;
//
//		public UpdateHomeItemRunnable(@NonNull final String chId, int pos) {
//
//			channelId = chId;
//			position = pos;
//		}
//
//		@Override
//		public void run() {
//
//			WasuEdit wasuEdit = updateHotItemsDataFromIPEPG(channelId);
//			Message msg = handler2.obtainMessage();
//			msg.obj = wasuEdit;
//			msg.what = UPDATE_HOTITEM_FROM_IPEPG;
//			msg.arg1 = position;
//			handler2.sendMessage(msg);
//		}
//
//	}

	/**
	 * @Title: updateHotItemsDataFromIPEPG
	 * @Description: TODO(获取指定频道的当前节目信息)
	 * @param @param chId
	 * @param @return 设定文件
	 * @return WasuEdit 返回类型
	 * @throws
	 */
	private WasuEdit updateHotItemsDataFromIPEPG(@NonNull String chId) {
		WasuEdit itemEdit = null;
		WasuEditInfor wasuEditInfor = null;
		try {
			wasuEditInfor = arg0.wasu_ip_query_edits(null, 2, chId);
			if (wasuEditInfor != null) {
				WasuEditChRelated wasuEditChRelated = wasuEditInfor.chList.get(0);
				if (wasuEditChRelated != null) {
					// 筛选和当前时间一致的节目单
					for (WasuEdit wasuEdit : wasuEditChRelated.wes) {
						if (isCurrentTimeSpace(wasuEdit.startTime, wasuEdit.endTime)) {
							itemEdit = wasuEdit;
							break;
						}
					}
				}
			}

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemEdit;
	}

	/**
	 * @Title: isCurrentTimeSpace
	 * @Description: TODO(判断当前时间是否在所给时间段)
	 * @param @param startTime
	 * @param @param endTime
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	private boolean isCurrentTimeSpace(String startTime, String endTime) {
		boolean inTimeSpace = false;
		long currentTime = System.currentTimeMillis();
		long st = Utils.getLongTimeFromString(startTime);
		long end = Utils.getLongTimeFromString(endTime);
		if (st != -1 && end != -1) {
			if (currentTime > st && currentTime < end) {
				inTimeSpace = true;
			}
		}
		return inTimeSpace;
	}
	
	/**
	 * @Title: isInCurrentTime
	 * @Description: TODO(判断当前时间是否在所给时间段)
	 * @param @param startTime
	 * @param @param endTime
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	private boolean isInCurrentTime(String startTime, String endTime) {
		boolean inTimeSpace = false;
		long currentTime = System.currentTimeMillis();
		long end = Utils.getLongTimeFromString(endTime);
		if (end != -1 && currentTime < end) {
			long st = Utils.getLongTimeFromString(startTime);
			if (st != -1 && st > currentTime) {
				inTimeSpace = true;
			}
		}
		return inTimeSpace;
	}

	public void Refresh() {
		if(handler!=null){
			getLoaderManager().restartLoader(LiveLoaderID.GETPLAYURI,null,this);
			//getLoaderManager().restartLoader(LiveLoaderID.GETBOUCHALLCH,null,this);
			getLoaderManager().restartLoader(LiveLoaderID.GETHOTANDSEE,null,this);
			getLoaderManager().restartLoader(LiveLoaderID.GETEVERYONESEE,null,this);
		}
	}

	public String get_jpgurl_for_channelID(String ContentID) {
		String ret = null;
		// Log.d("yingjh", "ContentID="+ContentID);
		if ((live_channels != null) && (live_channels.getResult() == 0)) {
			for (WasuBouInfor wsinfor : live_channels.getBous()) {
				for (WasuChInfor wcinfor : wsinfor.getChs()) {
					if (wcinfor.getChId().equals(ContentID)) {
						String str = wcinfor.getChLogoURL();
						if ((str != null) && (str.startsWith("http"))) {
							return str;
						}
					}
				}
			}
		}
		return ret;

	}

	@Override
	public void OnPlayListener(int i) {
		Log.e("liveOnPlayListener", "::" + i);
		Log.d("yingjh", "--------------OnPlayListener-----------");
		
		if (i == 1) {// 播放开始
			if (isHint) {
				im_surfaceView.post(new Runnable() {
					@Override
					public void run() {
						im_surfaceView.setVisibility(View.GONE);
					}
				});
			}
		} else if (i == 0) {// 播放完成

		}

	}

	@Override
	public Loader<Object> onCreateLoader(final int id, final Bundle args) {
		RequestAsyncTaskLoader request = new RequestAsyncTaskLoader(context);
		request.setLoadInBackgroundListener(new RequestAsyncTaskLoader.LoadInBackgroundListener() {

			@Override
			public Object loadInBackgroundListener() {
				if(arg0==null){
					return null;
				}
				if (id == LiveLoaderID.GETPLAYURI){
					List<String> strings = new ArrayList<String>();
					strings.add("DVBOTT_HD_10003000");
					try {
						String aaString = arg0.wasu_recommsys_recommlist_get(Utils.CreateRecommendContentBundle(strings).getString("param"));
						if (!TextUtils.isEmpty(aaString)) {
						RecommendResponseResult reconmmendBean = gson.fromJson(aaString, RecommendResponseResult.class);
						if (reconmmendBean != null && reconmmendBean.getContents() != null && reconmmendBean.getContents().size() != 0
								&& reconmmendBean.getContents().get(0).getInfoList() != null
								&& reconmmendBean.getContents().get(0).getInfoList().size() != 0) {
							WasuChInfor ws = arg0.wasu_ip_query_chInfor(reconmmendBean.getContents().get(0).getInfoList().get(0).getFodderid());
							fdid = reconmmendBean.getContents().get(0).getInfoList().get(0).getFodderid();
							if (ws != null && ws.getWdl() != null && ws.getWdl().getFreq() != null && ws.getWdl().getRate() != null
									&& ws.getWdl().getMod() != null && ws.getWdl().getSid() != null) {

								int freq = Integer.valueOf(ws.getWdl().getFreq()).intValue() / 1000;// 1
								String sfreq = Integer.toHexString(freq);

								int rate = Integer.valueOf(ws.getWdl().getRate()).intValue() / 1000;// 2
								String srate = Integer.toHexString(rate);

								String arre2 = ws.getWdl().getMod();// 3
								int qam = 0;
								if (arre2.equals("QAM16")) {
									qam = 1;
								} else if (arre2.equals("QAM32")) {
									qam = 2;
								} else if (arre2.equals("QAM64")) {
									qam = 3;
								} else if (arre2.equals("QAM128")) {
									qam = 4;
								} else if (arre2.equals("QAM256")) {
									qam = 5;
								}
								int sid = Integer.valueOf(ws.getWdl().getSid()).intValue();// 4
								String ssid = Integer.toHexString(sid);

								string = "DELIVERY://" + sfreq + "." + srate + "." + qam + "." + ssid + ".0.0.0.0.0.0" + "";
								return string;
							}
							return null;
						}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}else if(id == LiveLoaderID.GETBOUCHALLCH ){
					try {
						return arg0.wasu_ip_query_bouch(null);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}else if (id == LiveLoaderID.GETHOTANDSEE){
					String rankreal;
					try {
						rankreal = arg0.wasu_recommsys_ranklist_get(Utils.CreateLiveRankListContentBundle(Constant.LIVE,Constant.RANK_CODE_REAL).getString("param"));
						if(!TextUtils.isEmpty(rankreal)){
							return gson.fromJson(rankreal, RankListResponseBody.class);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if (id == LiveLoaderID.GETEVERYONESEE){
					List<String> strings = new ArrayList<String>();
					strings.add("DVBOTT_HD_10003200");
					try {
						String 	aaString = arg0.wasu_recommsys_recommlist_get(Utils.CreateRecommendContentBundle(strings).getString("param"));
						if (!TextUtils.isEmpty(aaString)) {
							return gson.fromJson(aaString, RecommendResponseResult.class);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					

				}else if((id>=LiveLoaderID.GETREALITEM)&&(id<LiveLoaderID.GETREALITEM_END)){
					if(args!=null){
					WasuEdit itemEdit = null;
					WasuEditInfor wasuEditInfor = null;
					try {
						wasuEditInfor = arg0.wasu_ip_query_edits(null, 2, args.getString("chID"));
						if (wasuEditInfor != null) {
							WasuEditChRelated wasuEditChRelated = wasuEditInfor.chList.get(0);
							if (wasuEditChRelated != null) {
								// 筛选和当前时间一致的节目单
								for (WasuEdit wasuEdit : wasuEditChRelated.wes) {
									if (isCurrentTimeSpace(wasuEdit.startTime, wasuEdit.endTime)) {
										itemEdit = wasuEdit;
										break;
									}
								}
							}
						}
						if(itemEdit!=null){
							int position = args.getInt("position", 0);
							Message msg = new Message();
							msg.obj = itemEdit;
							msg.what = UPDATE_HOTITEM_FROM_IPEPG;
							msg.arg1 = position;
							return msg;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}

				} else if (id == Constant.QUERY_CHILDFOLDER) {
					return WasuUtils.wasu_sdk_content_query(arg0, id, args);
				} else if (id >= Constant.QUERY_FOLDER && id < Constant.QUERY_FOLDER + mHotPlayBackList.size()) {
					return WasuUtils.wasu_sdk_content_query(arg0, id, args);
				} else if (id == LiveLoaderID.GETKANBA) {
					List<String> str = new ArrayList<String>();
					str.add(LIVE_KANBA_ID_1);
					str.add(LIVE_KANBA_ID_2);
					try {
						//请求看吧频道数据
						String result = arg0.wasu_recommsys_recommlist_get(Utils
								.CreateRecommendContentBundle(str).getString(
										"param"));
//						Log.d("chenchen", "直播-看吧频道：" + result);
						if (!TextUtils.isEmpty(result)) {
							return gson.fromJson(result, RecommendResponseResult.class);
							
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else if (id >= LiveLoaderID.GETKANBAEPG && id < LiveLoaderID.GETKANBAEPG + 5) {
					if(args!=null){
						String id = (String) args.get("chid");
						WasuEditInfor wasuEditInfor = null;
						try {
							wasuEditInfor = arg0.wasu_ip_query_edits(null, 2, id);
							return wasuEditInfor;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}else if (id >= LiveLoaderID.GETEPG && id < LiveLoaderID.GETEPG + 3) {
					if(args!=null){
						String chid = (String) args.get("chid");
						WasuEditInfor wasuEditInfor = null;
						try {
							wasuEditInfor = arg0.wasu_ip_query_edits(null, 2, chid);
							return wasuEditInfor;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				return null;
			}
			
		});
		return request;
	}

	@Override
	public void onLoadFinished(Loader<Object> loader, Object data) {
		if (loader == null)
			return;
		if(data!=null){
			int id = loader.getId();
			if (id == LiveLoaderID.GETPLAYURI){
					String result =(String)data;
					if(!TextUtils.isEmpty(result)){
						if (handler != null) {
						handler.removeMessages(ONEPLAY);
						handler.sendMessageDelayed(handler.obtainMessage(ONEPLAY), 200);
						}		
					}		
			}else if(id == LiveLoaderID.GETBOUCHALLCH ){
				live_channels = (WasuBouChInfor)data;
			}else if (id == LiveLoaderID.GETHOTANDSEE){
				RankListResponseBody ranklistreal = (RankListResponseBody)data;
				if (handler2 != null) {
				handler2.sendMessage(handler2.obtainMessage(UPDATE_HOTIEM_FROM_RECOMMSYS_REAL, ranklistreal));
				}
			}else if (id == LiveLoaderID.GETEVERYONESEE){
				RecommendResponseResult result = (RecommendResponseResult)data;
				fillEveryoneSeeItems(result);
				
			}else if((id>=LiveLoaderID.GETREALITEM)&&(id<LiveLoaderID.GETREALITEM_END)){
				Message datamsg  = (Message)data;
				Message msg = handler2.obtainMessage();
				msg.obj = datamsg.obj;
				msg.what = datamsg.what;
				msg.arg1 = datamsg.arg1;
				handler2.sendMessage(msg);
			}else if (id == Constant.QUERY_CHILDFOLDER) {
				//热剧回放数据
				ChildFolderResponseResult result = (ChildFolderResponseResult) data;
				filterChildFolderData(result);
			}else if (id >= Constant.QUERY_FOLDER && id < Constant.QUERY_FOLDER + mHotPlayBackList.size()) {
				//热剧回放子栏目数据
				FolderResponseResult result = (FolderResponseResult) data;
				int index = id - Constant.QUERY_FOLDER;
				filterFolderData(result, index);
			}else if (id == LiveLoaderID.GETKANBA) {
				//看吧频道数据返回
				RecommendResponseResult result = (RecommendResponseResult) data;
				filterKanBaData(result);
			}else if (id >= LiveLoaderID.GETKANBAEPG && id < LiveLoaderID.GETKANBAEPG + 5) {
				int index = id - LiveLoaderID.GETKANBAEPG;
				WasuEditInfor wasuEditInfor = (WasuEditInfor) data;
				filterKanBaEpgData(wasuEditInfor ,index);
			}else if (id >= LiveLoaderID.GETEPG && id < LiveLoaderID.GETEPG + 3) {
				int index = id - LiveLoaderID.GETEPG;
				WasuEditInfor wasuEditInfor = (WasuEditInfor) data;
				filterEpgData(wasuEditInfor ,index);
			}
		}
	}
	
	/**
	 * 填充电视回放栏目数据信息，并获取电视回放子栏目数据
	 * @param result
	 */
	private void filterChildFolderData(ChildFolderResponseResult result) {
		if (result == null) {
			return;
		}
		List<WasuFolder> list = result.getFolders();
		if (list == null) {
			return;
		}
		for (int i = 0; i < list.size() && i < mHotPlayBackList.size(); i++) {
			WasuFolder wasuFolder = list.get(i);
			if (wasuFolder != null) {
				LinearLayout layout = mHotPlayBackList.get(i);
				//热剧回放取剧照
				CustomContent customContent = new CustomContent(wasuFolder, "2");
				layout.setTag(customContent);
				
				//取栏目下第一部资产的海报图作为栏目入口的图片
				if (i == 0) {
					String url = customContent.getUrl();
					if (!TextUtils.isEmpty(url)) {
						imageLoader.get(url, ImageLoader.getImageListener(iv_live_hot_playback, 0, 0));
					}
				}
				
				//填充栏目标题
				TextView title = (TextView) layout.getChildAt(0);
				title.setText(wasuFolder.getFolderName());
				
				//依次获取各子栏目数据
				getLoaderManager().restartLoader(Constant.QUERY_FOLDER + i, Utils.CreateFolderContentBundle(wasuFolder.getFolderCode(), -1, null), this);
			}
		}
	}
	
	/**
	 * 填充子栏目数据信息
	 * @param result 子栏目数据
	 * @param index 子栏目索引
	 */
	private void filterFolderData(FolderResponseResult result, int index) {
		//填充剧集更新信息
		LinearLayout layout = mHotPlayBackList.get(index);
		TextView subTitle = (TextView) layout.getChildAt(1);
		subTitle.setText(getString(R.string.live_tv_update, result.getTotalItems()));
	}
	
	/**
	 * 填充看吧频道数据
	 * @param result 推荐系统返回数据
	 */
	private void filterKanBaData(RecommendResponseResult result) {
		WasuInfo wasuInfo;
		for (int i = 0; i < result.getCount(); i++) {
			WasuContent wasuContent = result.getContents().get(i);
			List<WasuInfo> wasuInfos = wasuContent.getInfoList();
			if (wasuContent == null || wasuInfos == null || wasuInfos.isEmpty()) {
				continue;
			}
			switch (wasuContent.getPlaceCode()) {
			case LIVE_KANBA_ID_1:
				//看吧频道头部数据
				wasuInfo = wasuInfos.get(0);
				if (wasuInfo != null) {
					if(!TextUtils.isEmpty(wasuInfo.getImg())) {
						imageLoader.get(wasuInfo.getImg(), ImageLoader.getImageListener(iv_live_kanba, 0, 0));
					}
					rl_live_kanba.setTag(wasuInfo);
				}
				break;
			case LIVE_KANBA_ID_2:
				//看吧频道列表数据
				for (int j = 0; j < wasuInfos.size() && j < mKanBaList.size(); j++) {
					wasuInfo = wasuInfos.get(j);
					if (wasuInfo != null) {
						LinearLayout layout = mKanBaList.get(j);
						layout.setTag(wasuInfo);
						//频道名称
						TextView title = (TextView) layout.getChildAt(0);
						if (!TextUtils.isEmpty(wasuInfo.getText())) {
							title.setText(wasuInfo.getText());
						}
						
						//获取节目单信息
						Bundle bundle = new Bundle();
						bundle.putString("chid", wasuInfo.getFodderid());
						getLoaderManager().restartLoader(LiveLoaderID.GETKANBAEPG + j, bundle, this);
					}
				}
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * 填充看吧频道节目单
	 * @param wasuEditInfor
	 * @param index
	 */
	private void filterKanBaEpgData(WasuEditInfor wasuEditInfor, int index) {
		WasuEditChRelated wasuEditChRelated = wasuEditInfor.chList.get(0);
		if (childHandler != null) {
			Message message = childHandler.obtainMessage();
			message.what = MSG_GET_KANBA_EPG;
			message.arg1 = index;
			message.obj = wasuEditChRelated;
			childHandler.sendMessage(message);
		}
	}
	
	/**
	 * 填充热门频道节目单
	 * @param wasuEditInfor
	 * @param index
	 */
	private void filterEpgData(WasuEditInfor wasuEditInfor, int index) {
		WasuEditChRelated wasuEditChRelated = wasuEditInfor.chList.get(0);
		if (childHandler != null) {
			Message message = childHandler.obtainMessage();
			message.what = MSG_GET_HOT_LIVE_EPG;
			message.arg1 = index;
			message.obj = wasuEditChRelated;
			childHandler.sendMessage(message);
		}
	}

	@Override
	public void onLoaderReset(Loader<Object> loader) {
		// TODO Auto-generated method stub
		
	}
	
	protected void showOnFocusTranslAnimation(View v) {
		switch (v.getId()) {
		case R.id.rl_live_video_view:
			if (surface_mask != null) {
				surface_mask.setVisibility(View.VISIBLE);
			}
			rl_live_video_view.bringToFront();
			return;
		case R.id.ll_live_often_item_0:
			if (live_often_epg_0 != null) {
				live_often_epg_0.setFocuse(true);
				live_often_epg_0.setText(live_often_epg_0.getText());
			}
			break;
		case R.id.ll_live_often_item_1:
			if (live_often_epg_1 != null) {
				live_often_epg_1.setFocuse(true);
				live_often_epg_1.setText(live_often_epg_1.getText());
			}
			break;
		case R.id.ll_live_often_item_2:
			if (live_often_epg_2 != null) {
				live_often_epg_2.setFocuse(true);
				live_often_epg_2.setText(live_often_epg_2.getText());
			}
			break;
		case R.id.ll_live_hot_playback_item_0:
		case R.id.ll_live_hot_playback_item_1:
		case R.id.ll_live_hot_playback_item_2:
		case R.id.ll_live_hot_playback_item_3:
		case R.id.ll_live_hot_playback_item_4:
			break;
		case R.id.ll_live_kanba_item_1:
			if (live_kanba_epg1 != null) {
				live_kanba_epg1.setFocuse(true);
				live_kanba_epg1.setText(live_kanba_epg1.getText());
			}
			break;
		case R.id.ll_live_kanba_item_2:
			if (live_kanba_epg2 != null) {
				live_kanba_epg2.setFocuse(true);
				live_kanba_epg2.setText(live_kanba_epg2.getText());
			}
			break;
		case R.id.ll_live_kanba_item_3:
			if (live_kanba_epg3 != null) {
				live_kanba_epg3.setFocuse(true);
				live_kanba_epg3.setText(live_kanba_epg3.getText());
			}
			break;
		case R.id.ll_live_kanba_item_4:
			if (live_kanba_epg4 != null) {
				live_kanba_epg4.setFocuse(true);
				live_kanba_epg4.setText(live_kanba_epg4.getText());
			}
			break;
		case R.id.ll_live_kanba_item_0:
			if (live_kanba_epg0 != null) {
				live_kanba_epg0.setFocuse(true);
				live_kanba_epg0.setText(live_kanba_epg0.getText());
			}
			break;
		default:
			break;
		}
		super.showOnFocusTranslAnimation(v);
	}

	@Override
	protected void showLooseFocusTranslAinimation(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_live_video_view:
			if (surface_mask != null) {
				surface_mask.setVisibility(View.INVISIBLE);
			}
			return;
		case R.id.ll_live_often_item_0:
			if (live_often_epg_0 != null) {
				live_often_epg_0.setFocuse(false);
				live_often_epg_0.setText(live_often_epg_0.getText());
			}
			break;
		case R.id.ll_live_often_item_1:
			if (live_often_epg_1 != null) {
				live_often_epg_1.setFocuse(false);
				live_often_epg_1.setText(live_often_epg_1.getText());
			}
			break;
		case R.id.ll_live_often_item_2:
			if (live_often_epg_2 != null) {
				live_often_epg_2.setFocuse(false);
				live_often_epg_2.setText(live_often_epg_2.getText());
			}
			break;
		case R.id.ll_live_kanba_item_1:
			if (live_kanba_epg1 != null) {
				live_kanba_epg1.setFocuse(false);
				live_kanba_epg1.setText(live_kanba_epg1.getText());
			}
			break;
		case R.id.ll_live_kanba_item_2:
			if (live_kanba_epg2 != null) {
				live_kanba_epg2.setFocuse(false);
				live_kanba_epg2.setText(live_kanba_epg2.getText());
			}
			break;
		case R.id.ll_live_kanba_item_3:
			if (live_kanba_epg3 != null) {
				live_kanba_epg3.setFocuse(false);
				live_kanba_epg3.setText(live_kanba_epg3.getText());
			}
			break;
		case R.id.ll_live_kanba_item_4:
			if (live_kanba_epg4 != null) {
				live_kanba_epg4.setFocuse(false);
				live_kanba_epg4.setText(live_kanba_epg4.getText());
			}
			break;
		case R.id.ll_live_kanba_item_0:
			if (live_kanba_epg0 != null) {
				live_kanba_epg0.setFocuse(false);
				live_kanba_epg0.setText(live_kanba_epg0.getText());
			}
			break;
		default:
			break;
		}
		super.showLooseFocusTranslAinimation(v);
	}
	
	@Override
	public void scrollVideoView() {
		// TODO Auto-generated method stub
		if (surfaceView != null && playUtils != null) {
			int location[] = new int[2];
			surfaceView.getLocationInWindow(location);
			rect = new Rect(location[0], location[1], location[0] + surfaceView.getWidth() + 1, location[1] + surfaceView.getHeight() + 1);
//			mPlayer.getVideoControl().setBounds(rect);
			playUtils.setPlayArea(rect);
		}
	}
}
