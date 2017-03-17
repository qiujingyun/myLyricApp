package com.wasu.launcher.Fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.wasu.android.volley.toolbox.ImageLoader;
import com.wasu.launcher.BaseActivity;
import com.wasu.launcher.R;
import com.wasu.launcher.interfaces.MyFragmentListener;
import com.wasu.launcher.utils.Packages;
import com.wasu.launcher.utils.ScaleAnimEffect;
import com.wasu.launcher.utils.Utils;
import com.wasu.launcher.view.ScrollViewProgress;
import com.wasu.launcher.view.ScrollViewProgress.OnScrollProgressListener;
import com.wasu.launcher.view.SmoothHorizontalScrollView;
import com.wasu.launcher.view.SmoothHorizontalScrollView.ScrollViewListener;
import com.wasu.launcher.view.VerticalSeekBar;
import com.wasu.vod.aidl.SDKOperationManager;
import com.wasu.vod.aidl.SDKOperationManager.MyListener;
import com.wasu.vod.domain.CustomContent;
import com.wasu.vod.domain.RecommendResponseResult;
import com.wasu.vod.domain.WasuContent;
import com.wasu.vod.domain.WasuInfo;

/**
 * 
 * @author 77071
 * 
 */
public class VodFragment extends BaseFragment implements OnScrollProgressListener, OnClickListener, Callback {

	/** 取得数据 */
	private static final int GETDATA = 0x100010;
	/** set数据 */
	private static final int SETDATA = 0x100011;

	private static final String TAG = VodFragment.class.getSimpleName();

	private MyFragmentListener mCallback;
	private View view;
	List<ImageView> vd_bg_list;
	List<TextView> vd_tv_list;
	VerticalSeekBar SeekBar;
	ScrollViewProgress scrollViewProgress;
	Handr2 handr2;
	ImageView imageView1, imageView2;
	private LinkedHashMap<Integer, VodItem> mLinkedHashMap = new LinkedHashMap<Integer, VodItem>();

	Handler myvodhandler;
	HandlerThread handlerThread;
	
	private RelativeLayout mContentLayout;
	
	//左侧通栏
	private RelativeLayout rl_vod_left;
	private ImageView iv_vod_left;
	
	//电影
	private RelativeLayout rl_vod_movie;
	private ImageView iv_vod_movie_img;
	//电视剧
	private RelativeLayout rl_vod_series;
	private ImageView iv_vod_series_img;
	//新闻
	private RelativeLayout rl_vod_news;
	//少儿
	private RelativeLayout rl_vod_children;
	private ImageView iv_vod_children_img;
	//体育
	private RelativeLayout rl_vod_sports;
	//栏目
	private RelativeLayout rl_vod_column;
	//纪录
	private RelativeLayout rl_vod_documentary;
	//生活
	private RelativeLayout rl_vod_life;
	//娱乐
	private RelativeLayout rl_vod_entertainment;
	private ImageView iv_vod_entertainment_img;
	//音乐
	private RelativeLayout rl_vod_music;
	//财经
	private RelativeLayout rl_vod_finance;
	//教育
	private RelativeLayout rl_vod_education;
	//芒果TV
	private RelativeLayout rl_vod_mangotv;
	//点播右侧推荐位
	private RelativeLayout rl_vod_data;
	private ImageView iv_vod_data;
	
	/** 点播推荐系统数据 */
	private final static String VOD_REC_DATA_1 = "DVBOTT_HD_10002200";
	private final static String VOD_REC_DATA_2 = "DVBOTT_HD_10002300";
	//一体机去除全景
//	private final static String VOD_REC_DATA_3 = "DVBOTT_HD_10002400";
	//求索
	private final static String VOD_REC_DATA_4 = "DVBOTT_HD_10002500";
	/** 点播推荐系统动态数据 */
	private final static String VOD_REC_DYNAMIC_DATA = "DVBOTT_HD_10002800";
	
	private final static int VOD_DYNAMIC_DATA_ID = 0x100;

	public VodFragment(ScrollViewListener listener) {
		mScrollViewListener = listener;
	}
	
	private class VodItem {
		public String type;
		public String typeCollection;
		
		public VodItem(String type, String collection) {
			this.type = type;
			this.typeCollection = collection;
		}
	}
	
	@Override
	public void onStop() {
		Long timeLong = (System.currentTimeMillis() - startTime);
		String[] strs = new String[] { Utils.getWasuTime() + "|" + Utils.getTVID() + "|ott_db|ott|||点播||" + timeLong +"|||"};
		Log.e("handleMessage LI + Lin 700", "aaa" + strs[0].toString());
		Utils.sendMessage(VodFragment.this.getActivity(), strs, "wasu_page_access_info");
		super.onStop();
	}

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handlerThread = new HandlerThread("wasu-user");
		handlerThread.start();
		myvodhandler = new Handler(handlerThread.getLooper(), this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		if (null == view) {
			view = inflater.inflate(R.layout.fragment_vod, container, false);
			initview();
			handr2 = new Handr2(this);
		}
		return view;

	}

	private void initview() {
		animEffect = new ScaleAnimEffect();
		vd_bg_list = new ArrayList<ImageView>();
		vd_tv_list = new ArrayList<TextView>();
		findViewById();
		setListener();
		initDataList();
	}
	
	private void initDataList() {
		//所有栏目数据对应添加
		mLinkedHashMap.put(R.id.rl_vod_movie, new VodItem("电影", "dy"));
		mLinkedHashMap.put(R.id.rl_vod_series, new VodItem("电视剧", "ds"));
		mLinkedHashMap.put(R.id.rl_vod_entertainment, new VodItem("娱乐", "yl"));
		mLinkedHashMap.put(R.id.rl_vod_children, new VodItem("少儿", "se"));
		mLinkedHashMap.put(R.id.rl_vod_news, new VodItem("新闻", "xw"));
		mLinkedHashMap.put(R.id.rl_vod_sports, new VodItem("体育", "ty"));
		mLinkedHashMap.put(R.id.rl_vod_column, new VodItem("栏目", "lm"));
		mLinkedHashMap.put(R.id.rl_vod_music, new VodItem("音乐", "yy"));
		mLinkedHashMap.put(R.id.rl_vod_documentary, new VodItem("纪录", "jl"));
		mLinkedHashMap.put(R.id.rl_vod_finance, new VodItem("财经", "cj"));
		mLinkedHashMap.put(R.id.rl_vod_life, new VodItem("生活", "sh"));
		mLinkedHashMap.put(R.id.rl_vod_education, new VodItem("教育", "jy"));
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (myvodhandler != null) {
			myvodhandler.removeCallbacksAndMessages(null);
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
	protected void findViewById() {
		mScrollView = (SmoothHorizontalScrollView) view.findViewById(R.id.vod_scroll_view);
		mContentLayout = (RelativeLayout) view.findViewById(R.id.rl_vod_content);
		mShadow = (ImageView) view.findViewById(R.id.shadow);
		
		//左侧通栏海报
		rl_vod_left = (RelativeLayout) view.findViewById(R.id.rl_vod_left);
		iv_vod_left = (ImageView) view.findViewById(R.id.iv_vod_left);
		
		//点播栏目
		rl_vod_movie = (RelativeLayout) view.findViewById(R.id.rl_vod_movie);
		iv_vod_movie_img = (ImageView) view.findViewById(R.id.iv_vod_movie_img);
		rl_vod_series = (RelativeLayout) view.findViewById(R.id.rl_vod_series);
		iv_vod_series_img = (ImageView) view.findViewById(R.id.iv_vod_series_img);
		rl_vod_news = (RelativeLayout) view.findViewById(R.id.rl_vod_news);
		rl_vod_children = (RelativeLayout) view.findViewById(R.id.rl_vod_children);
		iv_vod_children_img = (ImageView) view.findViewById(R.id.iv_vod_children_img);
		rl_vod_sports = (RelativeLayout) view.findViewById(R.id.rl_vod_sports);
		rl_vod_column = (RelativeLayout) view.findViewById(R.id.rl_vod_column);
		rl_vod_documentary = (RelativeLayout) view.findViewById(R.id.rl_vod_documentary);
		rl_vod_life = (RelativeLayout) view.findViewById(R.id.rl_vod_life);
		rl_vod_entertainment = (RelativeLayout) view.findViewById(R.id.rl_vod_entertainment);
		iv_vod_entertainment_img = (ImageView) view.findViewById(R.id.iv_vod_entertainment_img);
		rl_vod_music = (RelativeLayout) view.findViewById(R.id.rl_vod_music);
		rl_vod_finance = (RelativeLayout) view.findViewById(R.id.rl_vod_finance);
		rl_vod_education = (RelativeLayout) view.findViewById(R.id.rl_vod_education);
		
		//右侧推荐位
		rl_vod_mangotv = (RelativeLayout) view.findViewById(R.id.rl_vod_mangotv);
		rl_vod_data = (RelativeLayout) view.findViewById(R.id.rl_vod_data);
		iv_vod_data = (ImageView) view.findViewById(R.id.iv_vod_data);
	}

	@Override
	protected void setListener() {
		mScrollView.setScrollViewListener(mScrollViewListener);
		
		//左侧通栏海报
		rl_vod_left.setOnFocusChangeListener(mFocusChangeListener);
		rl_vod_left.setOnClickListener(this);
		
		rl_vod_movie.setOnFocusChangeListener(mFocusChangeListener);
		rl_vod_movie.setOnClickListener(this);
		
		rl_vod_series.setOnFocusChangeListener(mFocusChangeListener);
		rl_vod_series.setOnClickListener(this);
		
		rl_vod_news.setOnFocusChangeListener(mFocusChangeListener);
		rl_vod_news.setOnClickListener(this);
		
		rl_vod_children.setOnFocusChangeListener(mFocusChangeListener);
		rl_vod_children.setOnClickListener(this);
		
		rl_vod_sports.setOnFocusChangeListener(mFocusChangeListener);
		rl_vod_sports.setOnClickListener(this);
		
		rl_vod_column.setOnFocusChangeListener(mFocusChangeListener);
		rl_vod_column.setOnClickListener(this);
		
		rl_vod_documentary.setOnFocusChangeListener(mFocusChangeListener);
		rl_vod_documentary.setOnClickListener(this);
		
		rl_vod_life.setOnFocusChangeListener(mFocusChangeListener);
		rl_vod_life.setOnClickListener(this);
		
		rl_vod_entertainment.setOnFocusChangeListener(mFocusChangeListener);
		rl_vod_entertainment.setOnClickListener(this);
		
		rl_vod_music.setOnFocusChangeListener(mFocusChangeListener);
		rl_vod_music.setOnClickListener(this);
		
		rl_vod_finance.setOnFocusChangeListener(mFocusChangeListener);
		rl_vod_finance.setOnClickListener(this);

		rl_vod_education.setOnFocusChangeListener(mFocusChangeListener);
		rl_vod_education.setOnClickListener(this);
		
		//右侧推荐位
		rl_vod_mangotv.setOnFocusChangeListener(mFocusChangeListener);
		rl_vod_mangotv.setOnClickListener(this);
		rl_vod_data.setOnFocusChangeListener(mFocusChangeListener);
		rl_vod_data.setOnClickListener(this);
	}

	@Override
	public void onScrollProgress(int progress) {
		SeekBar.setProgress(progress);
	}

	/**
	 * 点击事件 if (v == vd_bg_list.get(0)) { } } else if (v == vd_bg_list.get(1)) {
	 * 
	 * }
	 * 
	 * */
	@Override
	public void onClick(View v) {
		int id = v.getId();
		Intent intent;
		switch (id) {
		case R.id.rl_vod_mangotv:
			//芒果TV入口
			intent = new Intent();
			intent.setAction("com.wasu.vod.intent.mangguo");
			startActivity(intent);
			break;
		case R.id.rl_vod_left:
			//推荐位跳转详情页/专题页/栏目页
			if (v.getTag() instanceof WasuInfo) {
				WasuInfo wasuInfo = (WasuInfo) v.getTag();
				CustomContent customContent = new CustomContent(wasuInfo);
				((BaseActivity)getActivity()).openDetailsActivity(customContent, arg0, customContent.getContentId());
			}
			break;
		case R.id.rl_vod_data:
			//求索
			intent = new Intent();
			intent.setAction("com.wasu.vod.intent.seek");
			startActivity(intent);
			break;
		default:
			initintent(v);
			break;
		}
	}
	
	private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id >= VOD_DYNAMIC_DATA_ID && id <= VOD_DYNAMIC_DATA_ID + 100) {
				if (v.getTag() instanceof WasuInfo) {
					WasuInfo wasuInfo = (WasuInfo) v.getTag();
					CustomContent customContent = new CustomContent(wasuInfo);
					((BaseActivity)getActivity()).openDetailsActivity(customContent, arg0, customContent.getContentId());
				}
			}
		}
	};

	private void initrank(WasuInfo wasuInfo) {
		if (wasuInfo != null) {

			String contentTypeVal = wasuInfo.getFoddertype();

			String nameVal = wasuInfo.getText();
			String contentIdVal = wasuInfo.getFodderid();
			String programType = wasuInfo.getFoddertype();
			if ("7".equals(contentTypeVal) || "68".equals(contentTypeVal) || "13".equals(contentTypeVal)) {
				if ("7".equals(contentTypeVal)) {
					contentTypeVal = "13"; // it's news on the
					// com.wasu.vod.PlayerActivity
				}
				Packages.toActivityByClassName(getActivity().getApplicationContext(), "com.wasu.vod.PlayerActivity", null, new String[] {
						"contentId", "name", "contentType", "programType" }, new String[] { contentIdVal, nameVal, contentTypeVal,
						programType }, null);
			} else if ("1".equals(contentTypeVal) || "36".equals(contentTypeVal)) {
				contentTypeVal = "MOVIE";
				String[] str = { contentTypeVal, contentIdVal };

				if (contentIdVal != null && contentIdVal.startsWith("http:")) {
					Packages.toActivityByClassName(getActivity().getApplicationContext(), "com.wasu.vod.VodDetailsActivity", null,
							new String[] { "param" }, new String[] { contentIdVal }, null);
				} else {
					Packages.toActivityByClassName(getActivity().getApplicationContext(), "com.wasu.vod.VodDetailsActivity", null,
							new String[] { "TYPE", "param" }, str, null);
				}

			} else if ("2".equals(contentTypeVal) || "37".equals(contentTypeVal)) {
				contentTypeVal = "SERIES";
				String[] str = { contentTypeVal, contentIdVal };

				if (contentIdVal != null && contentIdVal.startsWith("http:")) {
					Packages.toActivityByClassName(getActivity().getApplicationContext(), "com.wasu.vod.VodDetailsActivity", null,
							new String[] { "param" }, new String[] { contentIdVal }, null);
				} else {
					Packages.toActivityByClassName(getActivity().getApplicationContext(), "com.wasu.vod.VodDetailsActivity", null,
							new String[] { "TYPE", "param" }, str, null);
				}

			}else if("102".equals(contentTypeVal)) {
				Packages.toActivityByClassName(getActivity().getApplicationContext(), "com.wasu.vod.TopicActivity", null,
						new String[] { "param" }, new String[] { contentIdVal }, null);
			}
		}
	}

	String[] strkey = new String[] { "TYPE", "param" };

	/** 跳转12 */
	private void initintent(View v) {
//		String text = (v.getTag()).toString();
//		
//		int i = 0;
//		for (String temp : TYPE) {
//			if (temp.equals(text)) {
//				break;
//			}
//			i++;
//		}
		if (mLinkedHashMap.containsKey(v.getId())) {
			VodItem vodItem = mLinkedHashMap.get(v.getId());
			myvodhandler.sendMessage(myvodhandler.obtainMessage(0x450, vodItem));
			
			String text = vodItem.type;
			if (text.equalsIgnoreCase("4K")) {
				
				Packages.toActivityByAction(getActivity().getApplicationContext(),
						"com.wasu.intent.VOD", null, new String[] { "TYPE" },
						new String[] { "4K" }, null);
				
			} else {
			    Packages.toActivityByAction(getActivity().getApplicationContext(), "com.wasu.intent.VOD", null, new String[] { "TYPE" },
					    new String[] { text }, null);
			}
		}
		
//		myvodhandler.sendMessage(myvodhandler.obtainMessage(0x450, String.valueOf(i)));
//		
//		if (text.equalsIgnoreCase("4K")) {
//			
//			Packages.toActivityByAction(getActivity().getApplicationContext(),
//					"com.wasu.intent.VOD", null, new String[] { "TYPE" },
//					new String[] { "4K" }, null);
//			
//		} else {
//		    Packages.toActivityByAction(getActivity().getApplicationContext(), "com.wasu.intent.VOD", null, new String[] { "TYPE" },
//				    new String[] { text }, null);
//		}

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
					if (id == R.id.vd_bg_10 || id == R.id.vd_bg_11 || id == R.id.vd_bg_12 || id == R.id.vd_bg_13) {
						
						if (mCallback != null){
							mCallback.replaceFragment(2, 3);
						}
						return true;
					}
				}else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
					int id = v.getId();
					if (id == R.id.vd_bg_0 || id == R.id.vd_bg_1 ) {
						if (mCallback != null) {
							mCallback.replaceFragment(2, 1);
						}
						return true;
					}
				}
				break;
			}
			return false;
		}
	}

	public void onResume() {
		super.onResume();
		//PlayUtils.getInstance().playstop();
		// myvodhandler.sendMessageDelayed(omyvodhandler.obtainMessage(1),500);
		if (arg0 != null) {
			//PlayUtils.getInstance().setOnPlay(null);
			myvodhandler.sendMessage(myvodhandler.obtainMessage(GETDATA));
		} else {
			smManager = new SDKOperationManager(getActivity());
			smManager.registerSDKOperationListener(this);
		}
	};

	MyListener arg0 = null;

	@Override
	public void onConnnected(MyListener arg0) {
		this.arg0 = arg0;
		if (myvodhandler != null && isVisible()) {
			myvodhandler.sendMessage(myvodhandler.obtainMessage(GETDATA));
		}
	}

	private void setImage(ImageView view, String paramUrl) {
		if (paramUrl != null) {
			imageLoader.get(paramUrl, ImageLoader.getImageListener(view, 0, 0));
		}
	}

	/** 线程 */
	static class Handr2 extends Handler {
		WeakReference<Fragment> wfactivity;

		public Handr2(Fragment fragment) {
			wfactivity = new WeakReference<Fragment>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			final VodFragment ragment = (VodFragment) wfactivity.get();
			if (ragment != null) {
				if (ragment.isVisible()) {
					if (msg.what == SETDATA) {
						RecommendResponseResult result = (RecommendResponseResult) msg.obj;
						int count  = result.getCount();
						for (int i = 0; i < count; i++) {
							WasuContent wasuContent = result.getContents().get(i);
							String placeCode = wasuContent.getPlaceCode();
							WasuInfo wasuInfo;
							List<WasuInfo> infoList = wasuContent.getInfoList();
							if (infoList == null || infoList.isEmpty()) {
								continue;
							}
							switch (placeCode) {
							case VOD_REC_DATA_1:
								//左侧通栏
								wasuInfo = infoList.get(0);
								if (wasuInfo != null) {
									ragment.setImage(ragment.iv_vod_left, wasuInfo.getImg());
									ragment.rl_vod_left.setTag(wasuInfo);
								}
								break;
							case VOD_REC_DATA_2:
								//中间4个栏目图片
								ImageView[] views = new ImageView[]{
										ragment.iv_vod_children_img,
										ragment.iv_vod_entertainment_img,
										ragment.iv_vod_series_img,
										ragment.iv_vod_movie_img
								};
								for (int j = 0; j < infoList.size() && j < views.length; j++) {
									wasuInfo = infoList.get(j);
									if (wasuInfo != null) {
										ragment.setImage(views[j], wasuInfo.getImg());
									}
								}
								break;
							case VOD_REC_DATA_4:
								//求索
								wasuInfo = infoList.get(0);
								if (wasuInfo != null) {
									ragment.setImage(ragment.iv_vod_data, wasuInfo.getImg());
									ragment.iv_vod_data.setTag(wasuInfo);
								}
								break;
							case VOD_REC_DYNAMIC_DATA:
								//动态创建后续推荐位
								LayoutParams params;
								for (int j = 0; j < infoList.size(); j++) {
									wasuInfo = infoList.get(j);
									if (wasuInfo != null) {
										RelativeLayout layout = (RelativeLayout) LayoutInflater.from(ragment.context).inflate(R.layout.lau_vod_data, null);
										layout.setId(VOD_DYNAMIC_DATA_ID + j);
										layout.setOnFocusChangeListener(ragment.mFocusChangeListener);
										layout.setOnClickListener(ragment.mClickListener);
										layout.setTag(wasuInfo);
										
										ImageView imageView = (ImageView) layout.findViewById(R.id.iv_vod_data);
										ragment.setImage(imageView, wasuInfo.getImg());
										params = new LayoutParams(380, 772);
										if (j == 0) {
											params.addRule(RelativeLayout.RIGHT_OF, R.id.rl_vod_data);
										}else {
											params.addRule(RelativeLayout.RIGHT_OF, VOD_DYNAMIC_DATA_ID + j - 1);
										}
										params.leftMargin = ragment.context.getResources().getDimensionPixelSize(R.dimen.sm_8);
										ragment.mContentLayout.addView(layout, params);
									}
								}
								break;
							default:
								break;
							}
						}
						
					}
				}
			}
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == GETDATA) {
			try {
				List<String> code = new ArrayList<String>();
				code.add(VOD_REC_DATA_1);
				code.add(VOD_REC_DATA_2);
//				code.add(VOD_REC_DATA_3);
				code.add(VOD_REC_DATA_4);
				code.add(VOD_REC_DYNAMIC_DATA);
				String string = arg0.wasu_recommsys_recommlist_get(Utils.CreateRecommendContentBundle(code).getString("param"));
//				Log.d("chenchen", "点播屏-推荐系统数据返回：" + string);
				if (string != null && !"".equals(string)) {
					RecommendResponseResult reconmmendBean = gson.fromJson(string, RecommendResponseResult.class);
					if (reconmmendBean != null) {
						if (handr2 != null) {
							handr2.sendMessage(handr2.obtainMessage(SETDATA, reconmmendBean));
						}
					}
				}
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
		} else if (msg.what == 0x450) {
			VodItem vodItem = (VodItem)msg.obj;
			String[] strs = { Utils.getWasuTime() +"|"+ Utils.getTVID() + "|ott_db_"+ vodItem.typeCollection +"|ott_db|||点播"+vodItem.type
					+ "|||||", };
			Utils.sendMessage(this.getActivity(), strs, "wasu_page_access_info");
		}
		return true;
	}

	public void Refresh() {
		if (myvodhandler != null) {
			myvodhandler.sendMessage(myvodhandler.obtainMessage(GETDATA));
		}
	}
}
