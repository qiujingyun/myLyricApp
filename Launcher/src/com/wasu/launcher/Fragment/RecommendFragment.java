package com.wasu.launcher.Fragment;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Rect;
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
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.wasu.android.volley.RequestQueue;
import com.wasu.android.volley.toolbox.ImageLoader;
import com.wasu.bs.pay.TrackPayResultInteface;
import com.wasu.bs.pay.WasuPayParaUtils;
import com.wasu.launcher.BaseActivity;
import com.wasu.launcher.MainActivity;
import com.wasu.launcher.R;
import com.wasu.launcher.activity.FullPlay;
import com.wasu.launcher.domain.RankListResponseBody;
import com.wasu.launcher.domain.WasuItem;
import com.wasu.launcher.domain.WasuPlayUrl;
import com.wasu.launcher.interfaces.MyFragmentListener;
import com.wasu.launcher.utils.Constant;
import com.wasu.launcher.utils.Packages;
import com.wasu.launcher.utils.PlayUtils.onPlayListener;
import com.wasu.launcher.utils.ScaleAnimEffect;
import com.wasu.launcher.utils.Utils;
import com.wasu.launcher.view.MarqueeTextView;
import com.wasu.launcher.view.ScrollViewProgress.OnScrollProgressListener;
import com.wasu.launcher.view.SmoothHorizontalScrollView;
import com.wasu.launcher.view.SmoothHorizontalScrollView.ScrollViewListener;
import com.wasu.launcher.view.VerticalSeekBar;
import com.wasu.sdk_ott.PayDialogFragment;
import com.wasu.sdk_ott.UILApplication;
import com.wasu.vod.PlayerActivity;
import com.wasu.vod.TopicActivity;
import com.wasu.vod.VodDetailsActivity;
import com.wasu.vod.aidl.SDKOperationManager;
import com.wasu.vod.aidl.WasuChInfor;
import com.wasu.vod.aidl.SDKOperationManager.MyListener;
import com.wasu.vod.application.MyVolley;
import com.wasu.vod.domain.ContentItem;
import com.wasu.vod.domain.Contents;
import com.wasu.vod.domain.CustomContent;
import com.wasu.vod.domain.FolderResponseResult;
import com.wasu.vod.domain.HistoryResponseResult;
import com.wasu.vod.domain.PlaybackResponseResult;
import com.wasu.vod.domain.RecommendResponseResult;
import com.wasu.vod.domain.RequestAsyncTaskLoader;
import com.wasu.vod.domain.SeriesResponseResult;
import com.wasu.vod.domain.WasuContent;
import com.wasu.vod.domain.WasuContentId;
import com.wasu.vod.domain.WasuContentIds;
import com.wasu.vod.domain.WasuInfo;
import com.wasu.vod.domain.WasuSegment;
import com.wasu.vod.domain.WasuVideo;
import com.wasu.vod.purchase.PurchaseActivity;
import com.wasu.vod.utils.WasuUtils;

public class RecommendFragment extends BaseFragment implements OnClickListener,
		OnItemClickListener, OnScrollProgressListener,
		Callback, onPlayListener,LoaderManager.LoaderCallbacks<Object> {

	protected static final String TAG = RecommendFragment.class.getSimpleName();

	private FrameLayout[] recommend_fls;
	private ImageView[] recommend_typeLogs;
	private RequestQueue mQueue;
	private ImageLoader imageLoader;

	private List<String> title; // 标题
	private VerticalSeekBar seekBar;
	private ScaleAnimEffect animEffect;
	MyListener arg0 = null;
	/** 是否为第一次加载视频 */
	boolean isRunOne = false;
	/*需鉴权的播放数据，鉴权后播放*/
	WasuInfo palyer_wasuInfo=null;
	CustomContent player_CustomContent = null;
	
	ImageView im_surfaceView;

	private MyFragmentListener mCallback;

	private static JsonParser mJsonParser;
	
	SurfaceView surfaceView;

	String playurlall = "";// "rtsp://125.210.227.234:5541/hdds=_ipqam4//icms_icds_pub08/opnewsps08/Video/2015/05/04/15/20150430111654_tianshiyumogui1_1129257575_1129333955.ts?Contentid=CP23010020150429087203&token=683F2461ADA932B11498C980F74BC8DD27F18D468FF06BFCCAF383AEB0B5C66B9BDE8137AF832D70F4312287C6B11AC71A795D4A97000596BDDBF9B2D3939AB305671F2C15CE322C375E6EA0EB930C35F02066F5052C2654CA524A80D9382A71A409A62460A7296175BAC348E2E823954672D3E6861AAF42BDDD6ADA4DE14D8F961BCA9E733AE71C12F7371CD21B28B534E43AAAC7487D333F3FBCBE430894C8AC34657520706CCD80D4109AA4131097EAC4CDA96078DE13F998C5C97AD317305973E4EC5FE6A0D546EEFAAC6AC33CE01366EB195B9E448CA5F6313D2BD6ABAB89365CDCE41764AF637B8B29B0D3E4255FFF3FB87905ECD626EBCB0B23C32295&isHD=1&isIpqam=1";

	String lowurl = "";
	String back_playurl = "";
	String fiid ="";//推荐屏小窗口contentID
	public int play_stream_type =0; //0= 点播视频流，1=直播视频流

	/** 静态 UI 线程 **/
	PlayHandler playhandler = null;
	/** 非UI线程 */
	Handler childHandler;
	private HandlerThread handlerThread;
	private RelativeLayout mContentLayout;
	private RelativeLayout rl_rec_video_view;
	//获焦后视频窗口遮罩
	private ImageView surface_mask;
	//最近收看
	private RelativeLayout rl_rec_history;
	private TextView tv_rec_history_title;
	private View rec_history_divide_line;
	private LinearLayout ll_rec_history_item_0;
	private LinearLayout ll_rec_history_item_1;
	private MarqueeTextView history_tv_0;
	private MarqueeTextView history_tv_1;
	private MarqueeTextView history_epg_0;
	private MarqueeTextView history_epg_1;
	private ArrayList<LinearLayout> mHistoryLayoutArray;
	private ArrayList<MarqueeTextView> mHistoryTvArray;
	private ArrayList<MarqueeTextView> mHistoryEpgArray;
	//最近收藏
	private RelativeLayout rl_rec_collection;
	private TextView tv_rec_collection_title;
	private View rec_collection_divide_line;
	private TextView tv_collection_item_0;
	private TextView tv_collection_item_1;
	private ArrayList<TextView> mCollectionTvArray;
	
	private RelativeLayout rl_rec_search;
	//推荐位
	private RelativeLayout rl_rec_data_0;
	private ImageView iv_rec_data_0;
	private RelativeLayout rl_rec_data_1;
	private ImageView iv_rec_data_1;
	private RelativeLayout rl_rec_data_2;
	private ImageView iv_rec_data_2;
	private RelativeLayout rl_rec_data_3;
	private ImageView iv_rec_data_3;
//	private RelativeLayout rl_rec_data_4;
//	private ImageView iv_rec_data_4;
//	private RelativeLayout rl_rec_data_5;
//	private ImageView iv_rec_data_5;
	
	//热点头条
	private List<TextView> mHeadLineList;
	
	//编排电影
	private RelativeLayout rl_rec_movie;
	private ImageView iv_rec_movie;
	private List<TextView> mMovieList;
	//编排电视剧
	private RelativeLayout rl_rec_series;
	private ImageView iv_rec_series;
	private List<TextView> mSeriesList;
	
	protected static final int REQUEST_BASE_QUICK_ORDER = 1001;
	
	//推荐屏_通栏热点
	private final static String REC_DATA_1 = "DVBOTT_HD_10001500";
	//推荐屏_右上角热点
	private final static String REC_DATA_2 = "DVBOTT_HD_10001600";
	//推荐屏_最右侧点播
	private final static String REC_DATA_3 = "DVBOTT_HD_10001700";
	//推荐屏_最右侧游戏
//	private final static String REC_DATA_4 = "DVBOTT_HD_10001800";
	/** 推荐屏_动态数据 **/
	private final static String REC_DYNAMIC_DATA = "DVBOTT_HD_10001900";
	/** 动态数据布局初始ID **/
	private final static int REC_DYNAMIC_DATA_ID = 0x100;

	public RecommendFragment(ScrollViewListener listener) {
		mScrollViewListener = listener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handlerThread = new HandlerThread("RE");
		handlerThread.start();
		childHandler = new Handler(handlerThread.getLooper(), this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		if (null == view) {
			view = inflater.inflate(R.layout.fragment_recommend, container,
					false);
			mHistoryLayoutArray = new ArrayList<LinearLayout>();
			mHistoryTvArray = new ArrayList<MarqueeTextView>();
			mHistoryEpgArray = new ArrayList<MarqueeTextView>();
			mCollectionTvArray = new ArrayList<TextView>();
			mHeadLineList = new ArrayList<TextView>();
			mMovieList = new ArrayList<TextView>();
			mSeriesList = new ArrayList<TextView>();
			initView();
			imageLoader = MyVolley.getImageLoader();
			rect = new Rect();
			playhandler = new PlayHandler(this);
			
			playUtils.setOnPlay(this);
			//内容加载完毕后显示底部阴影
			((MainActivity) getActivity()).showShadowBg();
		}
		return view;
	}

	private boolean isHint = true;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void onStart() {
		super.onStart();
		Log.e("RecommendFragment", "onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		isHint = true;
		playUtils.setOnPlay(this);
		/** 播放视频 */
		if (playurlall != null && playurlall.length() != 0) {
			try {
				childHandler.removeMessages(REMessage.PLAY);
				childHandler.sendMessageDelayed(
						childHandler.obtainMessage(REMessage.PLAY, playurlall),
						300);
			} catch (InstantiationError e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		im_surfaceView.setVisibility(View.VISIBLE);

		/** 刷新 */
		if (arg0 != null) {
//			childHandler.sendMessageDelayed(
//					childHandler.obtainMessage(REMessage.GETIMAGEANDTEXTDATA),
//					300);
//			getLoaderManager().restartLoader(RELoaderID.GETIMAGEANDTEXTDATA, null, this);
			//getLoaderManager().restartLoader(RELoaderID.GETTEXTDATA, null, this);
//			getLoaderManager().restartLoader(RELoaderID.GETTEXTDATA_IPS, null, this);
			getLoaderManager().restartLoader(RELoaderID.GETPLAYURLANDPLAY, null, this);
//			getLoaderManager().restartLoader(RELoaderID.GETEVERYONEISWATCHINGDATA, null, this);
			
			//请求热点头条数据
			getLoaderManager().restartLoader(RELoaderID.GET_REC_HEADLINE, 
					Utils.CreateFolderContentBundle(REC_HEADLINE_FOLDER_CODE, 3, null), this);
			//请求电影推荐栏目数据
			getLoaderManager().restartLoader(RELoaderID.GET_REC_MOVIE, 
					Utils.CreateFolderContentBundle(REC_MOVIE_FOLDER_CODE, -1, "410,543,JPG"), this);
			//请求电视剧推荐栏目数据
			getLoaderManager().restartLoader(RELoaderID.GET_REC_SERIES,
					Utils.CreateFolderContentBundle(REC_SERIES_FOLDER_CODE, -1, "410,543,JPG"), this);
			
			//请求播放历史
			//bundle在request中创建
			getLoaderManager().restartLoader(Constant.QUERY_HISTORY, null, this);
			//请求收藏
			//bundle在request中创建
			getLoaderManager().restartLoader(Constant.QUERY_COLLECT, null, this);
			//请求推荐系统数据
			getLoaderManager().restartLoader(RELoaderID.GET_REC_DATA, null, this);
		} else {
			smManager = new SDKOperationManager(getActivity());
			smManager.registerSDKOperationListener(this);
		}
	}

	@Override
	public void onPause() {
		Log.d("RecommendFragment", "onPause");
		playUtils.setOnPlay(null);
		if (childHandler != null) {
			childHandler.removeCallbacksAndMessages(null);
			childHandler.sendMessage(childHandler.obtainMessage(REMessage.PLAYPAUSE));
		}
		super.onPause();
	}

	@Override
	public void onStop() {
		isHint = false;

		childHandler.sendMessageDelayed(
				childHandler.obtainMessage(REMessage.SCADA), 10);		
		childHandler.sendMessage(childHandler.obtainMessage(REMessage.PLAYSTOP));
		childHandler.removeMessages(REMessage.PLAY);
		
		super.onStop();

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		// playUtils.playstop();
		// mmManager = null;

		getLoaderManager().destroyLoader(com.wasu.vod.utils.Constant.QUERY_AUTH_PLAYURL);//该loader数据需destroy,否则重新进入就播放
		
		Log.e("RecommendFragment", "onDestroyView");
	}

	@SuppressLint("NewApi")
	@Override
	public void onDestroy() {
		super.onDestroy();
		// playUtils.playstop();
		Log.e("RecommendFragment", "onDestroy");
		if(playhandler!=null){
			playhandler.removeCallbacksAndMessages(null);
		}
		if(childHandler!=null){
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
	}

	private Rect rect;

	/** 线程 */
	static class PlayHandler extends Handler {
		WeakReference<Fragment> wfactivity;

		public PlayHandler(Fragment fragment) {
			wfactivity = new WeakReference<Fragment>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			final RecommendFragment ragment = (RecommendFragment) wfactivity
					.get();
			Log.e("handleMessage", "" + msg.what);

//			if (ragment != null && ragment.isVisible()) {
//				if (msg.what == REMessage.REFRESHIMAGEANDTEXTDATA) {
//					if (msg.obj != null) {
//						RecommendResponseResult reconmmendBean = (RecommendResponseResult) msg.obj;
//						if (reconmmendBean.getContents() != null) {
//							// 内容
//							List<WasuContent> contents = reconmmendBean
//									.getContents();
//							for (int i = 0; i < contents.size(); i++) {
//								// 详细内容
//								List<WasuInfo> infoList = contents.get(i)
//										.getInfoList();
//
//								if (contents.get(i).getInfoList() != null
//										&& infoList.size() != 0)
//
//									ragment.setImage(i, infoList.get(0)
//											.getImg(), infoList.get(0));
//							}
//						}
//					}
//				} else if (msg.what == REMessage.REFRESHTEXTDATA) {
//					if (msg.obj != null) {
//						RecommendResponseResult reconmmendBean = (RecommendResponseResult) msg.obj;
//						ragment.title.clear();
//						if (reconmmendBean != null)
//							if (reconmmendBean.getContents() != null&&reconmmendBean.getContents().size()!=0
//									&& reconmmendBean.getContents().get(0)
//											.getInfoList() != null) {
//								for (int i = 0; i < reconmmendBean
//										.getContents().get(0).getInfoList()
//										.size(); i++) {
//
//									ragment.setText(i, reconmmendBean
//											.getContents().get(0).getInfoList()
//											.get(i).getText(), reconmmendBean
//											.getContents().get(0).getInfoList()
//											.get(i));
//								}
//							}
//					}
//				} else if (msg.what == REMessage.REFRESHTEXTDATA_IPS) {
//					if (msg.obj != null) {
//						FolderResponseResult result = (FolderResponseResult)msg.obj;
//						ragment.title.clear();
//						if(result!=null){
//							 List<Contents> contents = result.getContents();
//							if(contents!=null){
//								int contentsize = contents.size();
//								for(int i=0;(i<TV_RE_SZIE)&&(i<contentsize);i++)
//								{
//									Contents content = contents.get(i);
//									if(content!=null){
//										CustomContent customContent = new CustomContent(content);
//										String text  = content.getName();										
//										if(content.getSegment()!=null){
//											if(!TextUtils.isEmpty(content.getSegment().getName())){
//												text = content.getSegment().getName();												
//											}
//										}
//										ragment.setText(i,text,customContent);
//									}
//								};
//								
//							}
//						}
//					}
//				} else if (msg.what == REMessage.REFRESHEVERYONEISWATCHINGDATA) {
//					if (msg.obj != null) {
//						RankListResponseBody rankListResponseResult = (RankListResponseBody) msg.obj;
//						if (rankListResponseResult != null)
//						{
//							List<WasuItem> itmItems = rankListResponseResult.getItems();
//							if (itmItems != null)
//								for (int i = 0; i < itmItems.size(); i++) {
//									ragment.setImage(i + 3, itmItems.get(i).getPicurl(), itmItems.get(i));
//								}
//						}
//					}
//				}
//			}
		}
	}

	public void setImage(int paramInt, String picurl, WasuItem wasuItem) {
		if (paramInt >= recommend_typeLogs.length)
			return;
		imageLoader.get(picurl, ImageLoader.getImageListener(
				recommend_typeLogs[paramInt], 0, 0));
		recommend_typeLogs[paramInt].setTag(wasuItem);
	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.re_bg_0) {
				if(play_stream_type==1){
					Packages.toActivityByClassName(getActivity().getApplicationContext(), "com.wasu.live.MainLiveActivity", null,
							new String[] { "chid" }, new String[] { fiid }, null);
				}else{
					playUtils.setDeallocate(false);
					Intent intent = new Intent(
							RecommendFragment.this.getActivity(), FullPlay.class);
					// if (!string.equals("")) {
					intent.putExtra("url", playurlall);
					startActivity(intent);
					// }
				}
			} else {
				if (v.getTag() != null) {
					//WasuInfo wasuInfo = (WasuInfo) v.getTag();
					Object object = v.getTag();
					Log.d("yingjh", "onClick wasu info");
					if (object instanceof WasuInfo) {
						ClickJump((WasuInfo) object);						
					}else if(object instanceof CustomContent){
						ClickJump((CustomContent) object);	
					}
				}
			}

		}
	};

	private void setImage(int paramInt, String paramUrl, WasuInfo wasuInfo) {
		if (paramInt >= recommend_typeLogs.length)
			return;
		imageLoader.get(paramUrl, ImageLoader.getImageListener(
				recommend_typeLogs[paramInt], 0, 0));
		recommend_typeLogs[paramInt].setTag(wasuInfo);
	}

	@Override
	public void onClick(View v) {
		Log.e("RecommendFragment", "bbb" + v.toString());
		int id = v.getId();
		Intent intent;
		switch (id) {
		case R.id.rl_rec_video_view:
			//进入全屏播放
			if(play_stream_type==1){
				Packages.toActivityByClassName(getActivity().getApplicationContext(), "com.wasu.live.MainLiveActivity", null,
						new String[] { "chid" }, new String[] { fiid }, null);
			}else{
				playUtils.setDeallocate(false);
				intent = new Intent(RecommendFragment.this.getActivity(), FullPlay.class);
				// if (!string.equals("")) {
				intent.putExtra("url", playurlall);
				startActivity(intent);
				// }
			}
			break;
		case R.id.rl_rec_history:
		case R.id.tv_rec_history_title:
			//进入播放历史列表页
			intent = new Intent("com.wasu.vod.OTHERS");
			intent.putExtra("TYPE", "历史");
			startActivity(intent);
			break;
		case R.id.ll_rec_history_item_0:
		case R.id.ll_rec_history_item_1:
			//播放历史
			openDetailsActivity((CustomContent) v.getTag(), arg0, null, Constant.HISTORY, null);
			break;
		case R.id.rl_rec_search:
			//搜索
			intent = new Intent("com.wasu.vod.SEARCH");
			startActivity(intent);
			break;
		case R.id.rl_rec_collection:
		case R.id.tv_rec_collection_title:
			//收藏
			intent = new Intent("com.wasu.vod.OTHERS");
			intent.putExtra("TYPE", "收藏");
			startActivity(intent);
			break;
		case R.id.tv_collection_item_0:
		case R.id.tv_collection_item_1:
			//收藏，跳转详情页
			openDetailsActivity((CustomContent) v.getTag(), arg0, null, Constant.COLLECT, null);
			break;
		case R.id.rl_rec_data_0:
		case R.id.rl_rec_data_1:
		case R.id.rl_rec_data_2:
		case R.id.rl_rec_data_3:
			//跳转专题页/栏目页/详情页
			if (v.getTag() instanceof WasuInfo) {
				WasuInfo wasuInfo = (WasuInfo) v.getTag();
				CustomContent customContent = new CustomContent(wasuInfo);
				((BaseActivity)this.getActivity()).openDetailsActivity(customContent, this.arg0, customContent.getContentId());
			}
			break;
//		case R.id.rl_rec_data_4:
//			//游戏-漫画岛
//			startGame("com.yuanju.comicsisland.tv");
//			break;
//		case R.id.rl_rec_data_5:
//			//游戏-斗龙战士
//			startGame("com.holyblade.dragon");
//			break;
		case R.id.tv_rec_headline_item_0:
		case R.id.tv_rec_headline_item_1:
		case R.id.tv_rec_headline_item_2:
		case R.id.tv_rec_headline_item_3:
		case R.id.tv_rec_headline_item_4:
			//热点头条
			Contents contents = (Contents) v.getTag();
			CustomContent customContent = new CustomContent(contents);
			ClickJump(customContent);
			break;
		case R.id.rl_rec_movie:
		case R.id.tv_rec_movie_0:
		case R.id.tv_rec_movie_1:
		case R.id.tv_rec_movie_2:
		case R.id.tv_rec_movie_3:
		case R.id.rl_rec_series:
		case R.id.tv_rec_series_0:
		case R.id.tv_rec_series_1:
		case R.id.tv_rec_series_2:
		case R.id.tv_rec_series_3:
			openDetailsActivity((CustomContent) v.getTag(), arg0, null, 0, null);
			break;
		default:
			break;
		}
		
		if (v.getParent() instanceof FrameLayout) {
			FrameLayout lfFrameLayout = (FrameLayout) v.getParent();
			// lfFrameLayout.getChildAt(2);
			Object object = lfFrameLayout.getChildAt(1).getTag();
			if (object != null) {
				// Bundle bundle = (Bundle)
				// lfFrameLayout.getChildAt(1).getTag();
				if (object instanceof WasuInfo) {
					ClickJump((WasuInfo) object);
				} else if (object instanceof WasuItem) {
					ClickJump((WasuItem) object);
				}else if(object instanceof CustomContent){
					ClickJump((CustomContent) object);
				}

				// WasuItem wasuItem

				// String string = bundle.getString("type");
				// if (string != null) {
				// String strid = bundle.getString("id");
				// if (strid == null) {
				// Toast.makeText(getActivity(), "当前栏目已下线!", 0).show();
				// } else {
				//
				// String[] str = { string, strid };
				//
				// Packages.toActivityByClassName(getActivity().getApplicationContext(),
				// "com.wasu.vod.VodDetailsActivity", null,
				// new String[] { "TYPE", "param" }, str, null);
				// Log.e("onClick", "String" + string + " ... " + strid);
				// }
				// } else {
				// String contentId = bundle.getString("id");
				// if (contentId == null) {
				// Toast.makeText(getActivity(), "当前栏目已下线!", 0).show();
				// return;
				// }
				//
				// if (contentId != null && contentId.startsWith("http:")) {
				//
				// Packages.toActivityByClassName(getActivity().getApplicationContext(),
				// "com.wasu.vod.VodDetailsActivity", null,
				// new String[] { "param" }, new String[] { contentId }, null);
				//
				// }
				// }
			}
		}
	}

	private void ClickJump(WasuItem wasuItem) {
		String id = wasuItem.getContentid();

		String contentTypeVal = "MOVIE";
		String[] str = { contentTypeVal, id };

		if (id != null && id.startsWith("http:")) {
			Packages.toActivityByClassName(getActivity()
					.getApplicationContext(),
					"com.wasu.vod.VodDetailsActivity", null,
					new String[] { "param" }, new String[] { id }, null);
		} else {
			Packages.toActivityByClassName(getActivity()
					.getApplicationContext(),
					"com.wasu.vod.VodDetailsActivity", null, new String[] {
							"TYPE", "param" }, str, null);
		}

	}

	private void ClickJump(WasuInfo wasuInfo) {
		if (wasuInfo != null) {

			String contentTypeVal = wasuInfo.getFoddertype();

			String nameVal = wasuInfo.getText();
			String contentIdVal = wasuInfo.getFodderid();
			String programType = wasuInfo.getFoddertype();
			if ("7".equals(contentTypeVal) || "68".equals(contentTypeVal)
					|| "13".equals(contentTypeVal) || "15".equals(contentTypeVal)) {
				if ("7".equals(contentTypeVal)) {
					contentTypeVal = "13"; // it's news on the
					// com.wasu.vod.PlayerActivity
				}
				palyer_wasuInfo = wasuInfo;//保存数据，鉴权通过后播放
				player_CustomContent = null;
				Bundle bun = new Bundle();
				bun.putString("contentId", contentIdVal);
				bun.putString("contentType", contentTypeVal);
				getLoaderManager().restartLoader(com.wasu.vod.utils.Constant.QUERY_AUTH_PLAYURL, bun, this);
				/*
				Packages.toActivityByClassName(getActivity()
						.getApplicationContext(),
						"com.wasu.vod.PlayerActivity", null, new String[] {
								"contentId", "name", "contentType",
								"programType" }, new String[] { contentIdVal,
								nameVal, contentTypeVal, programType }, null);
								*/
			} else if ("1".equals(contentTypeVal)
					|| "36".equals(contentTypeVal)) {
				contentTypeVal = "MOVIE";
				String[] str = { contentTypeVal, contentIdVal };

				if (contentIdVal != null && contentIdVal.startsWith("http:")) {
					Packages.toActivityByClassName(getActivity()
							.getApplicationContext(),
							"com.wasu.vod.VodDetailsActivity", null,
							new String[] { "param" },
							new String[] { contentIdVal }, null);
				} else {
					Packages.toActivityByClassName(getActivity()
							.getApplicationContext(),
							"com.wasu.vod.VodDetailsActivity", null,
							new String[] { "TYPE", "param" }, str, null);
				}

			} else if ("2".equals(contentTypeVal)
					|| "37".equals(contentTypeVal)) {
				contentTypeVal = "SERIES";
				String[] str = { contentTypeVal, contentIdVal };

				if (contentIdVal != null && contentIdVal.startsWith("http:")) {
					Packages.toActivityByClassName(getActivity()
							.getApplicationContext(),
							"com.wasu.vod.VodDetailsActivity", null,
							new String[] { "param" },
							new String[] { contentIdVal }, null);
				} else {
					Packages.toActivityByClassName(getActivity()
							.getApplicationContext(),
							"com.wasu.vod.VodDetailsActivity", null,
							new String[] { "TYPE", "param" }, str, null);
				}


			} else if ("102".equals(contentTypeVal)) {
				Packages.toActivityByClassName(getActivity().getApplicationContext(), "com.wasu.vod.TopicActivity", null,
						new String[] { "param" }, new String[] { contentIdVal }, null);

			}
		}
	}
	
	private void ClickJump(CustomContent customcontent) {
		player_CustomContent = customcontent;//保存数据，鉴权通过后播放
		palyer_wasuInfo = null;
		String contentIdVal = customcontent.getContentId();
		String contentTypeVal = customcontent.getContentType();
		Bundle bun = new Bundle();
		bun.putString("contentId", contentIdVal);
		bun.putString("contentType", contentTypeVal);
		getLoaderManager().restartLoader(com.wasu.vod.utils.Constant.QUERY_AUTH_PLAYURL, bun, this);
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// 这是为了保证Activity容器实现了用以回调的接口。如果没有，它会抛出一个异常。
		try {
			mCallback = (MyFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	private void initView() {
		loadViewLayout();
		findViewById();
		setListener();
//		animEffect = new ScaleAnimEffect();
	}

	// List<ImageView> re_bg;

	@Override
	protected void loadViewLayout() {
		recommend_fls = new FrameLayout[13];
		recommend_typeLogs = new ImageView[11];
		// re_bg = new ArrayList<ImageView>();
	}

	@Override
	protected void findViewById() {
		mScrollView = (SmoothHorizontalScrollView) view.findViewById(R.id.scroll_view);
		mContentLayout = (RelativeLayout) view.findViewById(R.id.scroll_view_content);
		
		mShadow = (ImageView) view.findViewById(R.id.shadow);
		
		surfaceView = (SurfaceView) view.findViewById(R.id.rec_surface_view);
		//视频窗加载图
		im_surfaceView = (ImageView) view.findViewById(R.id.rec_img_surface_view);
		//视频窗获焦遮罩
		surface_mask = (ImageView) view.findViewById(R.id.rec_img_surface_mask);
		
		rl_rec_video_view = (RelativeLayout) view.findViewById(R.id.rl_rec_video_view);
		
		//播放历史单入口形式
		rl_rec_history = (RelativeLayout) view.findViewById(R.id.rl_rec_history);
		
		//播放历史列表入口形式
		tv_rec_history_title = (TextView) view.findViewById(R.id.tv_rec_history_title);
		rec_history_divide_line = view.findViewById(R.id.rec_history_divide_line);
		ll_rec_history_item_0 = (LinearLayout) view.findViewById(R.id.ll_rec_history_item_0);
		mHistoryLayoutArray.add(ll_rec_history_item_0);
		ll_rec_history_item_1 = (LinearLayout) view.findViewById(R.id.ll_rec_history_item_1);
		mHistoryLayoutArray.add(ll_rec_history_item_1);
		history_tv_0 = (MarqueeTextView) view.findViewById(R.id.rec_history_tv_0);
		mHistoryTvArray.add(history_tv_0);
		history_tv_1 = (MarqueeTextView) view.findViewById(R.id.rec_history_tv_1);
		mHistoryTvArray.add(history_tv_1);
		history_epg_0 = (MarqueeTextView) view.findViewById(R.id.rec_history_epg_0);
		mHistoryEpgArray.add(history_epg_0);
		history_epg_1 = (MarqueeTextView) view.findViewById(R.id.rec_history_epg_1);
		mHistoryEpgArray.add(history_epg_1);
		
		//收藏
		rl_rec_collection = (RelativeLayout) view.findViewById(R.id.rl_rec_collection);
		tv_rec_collection_title = (TextView) view.findViewById(R.id.tv_rec_collection_title);
		rec_collection_divide_line = view.findViewById(R.id.rec_collection_divide_line);
		tv_collection_item_0 = (TextView) view.findViewById(R.id.tv_collection_item_0);
		mCollectionTvArray.add(tv_collection_item_0);
		tv_collection_item_1 = (TextView) view.findViewById(R.id.tv_collection_item_1);
		mCollectionTvArray.add(tv_collection_item_1);
		
		//搜索
		rl_rec_search = (RelativeLayout) view.findViewById(R.id.rl_rec_search);

		//推荐位
		rl_rec_data_0 = (RelativeLayout) view.findViewById(R.id.rl_rec_data_0);
		iv_rec_data_0 = (ImageView) view.findViewById(R.id.iv_rec_data_0);

		rl_rec_data_1 = (RelativeLayout) view.findViewById(R.id.rl_rec_data_1);
		iv_rec_data_1 = (ImageView) view.findViewById(R.id.iv_rec_data_1);

		rl_rec_data_2 = (RelativeLayout) view.findViewById(R.id.rl_rec_data_2);
		iv_rec_data_2 = (ImageView) view.findViewById(R.id.iv_rec_data_2);
		
		rl_rec_data_3 = (RelativeLayout) view.findViewById(R.id.rl_rec_data_3);
		iv_rec_data_3 = (ImageView) view.findViewById(R.id.iv_rec_data_3);
		
//		rl_rec_data_4 = (RelativeLayout) view.findViewById(R.id.rl_rec_data_4);
//		iv_rec_data_4 = (ImageView) view.findViewById(R.id.iv_rec_data_4);
//		
//		rl_rec_data_5 = (RelativeLayout) view.findViewById(R.id.rl_rec_data_5);
//		iv_rec_data_5 = (ImageView) view.findViewById(R.id.iv_rec_data_5);

		//热点头条
		for (int i = 0; i < 5; i++) {
			TextView tv = (TextView) view.findViewById(getResources().getIdentifier("tv_rec_headline_item_" + i, "id", context.getPackageName()));
			mHeadLineList.add(tv);
		}
		
		//编排电影
		rl_rec_movie = (RelativeLayout) view.findViewById(R.id.rl_rec_movie);
		iv_rec_movie = (ImageView) view.findViewById(R.id.iv_rec_movie);
		for (int i = 0; i < 4; i++) {
			TextView tv = (TextView) view.findViewById(getResources().getIdentifier("tv_rec_movie_" + i, "id", context.getPackageName()));
			mMovieList.add(tv);
		}
		
		//编排电视剧
		rl_rec_series = (RelativeLayout) view.findViewById(R.id.rl_rec_series);
		iv_rec_series = (ImageView) view.findViewById(R.id.iv_rec_series);
		for (int i = 0; i < 4; i++) {
			TextView tv = (TextView) view.findViewById(getResources().getIdentifier("tv_rec_series_" + i, "id", context.getPackageName()));
			mSeriesList.add(tv);
		}
	}

	@Override
	protected void setListener() {
		mScrollView.setScrollViewListener(mScrollViewListener);
		
		//视频窗
		rl_rec_video_view.setOnFocusChangeListener(mFocusChangeListener);
		rl_rec_video_view.setOnClickListener(this);
		
		//播放历史单入口形式
		rl_rec_history.setOnFocusChangeListener(mFocusChangeListener);
		rl_rec_history.setOnClickListener(this);
		
		tv_rec_history_title.setOnFocusChangeListener(mFocusChangeListener);
		tv_rec_history_title.setOnClickListener(this);
		ll_rec_history_item_0.setOnFocusChangeListener(mFocusChangeListener);
		ll_rec_history_item_0.setOnClickListener(this);
		ll_rec_history_item_1.setOnFocusChangeListener(mFocusChangeListener);
		ll_rec_history_item_1.setOnClickListener(this);
		
		//收藏
		rl_rec_collection.setOnFocusChangeListener(mFocusChangeListener);
		rl_rec_collection.setOnClickListener(this);
		
		//收藏列表形式
		tv_rec_collection_title.setOnFocusChangeListener(mFocusChangeListener);
		tv_rec_collection_title.setOnClickListener(this);
		tv_collection_item_0.setOnFocusChangeListener(mFocusChangeListener);
		tv_collection_item_0.setOnClickListener(this);
		tv_collection_item_1.setOnFocusChangeListener(mFocusChangeListener);
		tv_collection_item_1.setOnClickListener(this);
		
		//搜索
		rl_rec_search.setOnFocusChangeListener(mFocusChangeListener);
		rl_rec_search.setOnClickListener(this);
		
		//推荐位
		rl_rec_data_0.setOnFocusChangeListener(mFocusChangeListener);
		rl_rec_data_0.setOnClickListener(this);
		
		rl_rec_data_1.setOnFocusChangeListener(mFocusChangeListener);
		rl_rec_data_1.setOnClickListener(this);
		
		rl_rec_data_2.setOnFocusChangeListener(mFocusChangeListener);
		rl_rec_data_2.setOnClickListener(this);
		
		rl_rec_data_3.setOnFocusChangeListener(mFocusChangeListener);
		rl_rec_data_3.setOnClickListener(this);
		
//		rl_rec_data_4.setOnFocusChangeListener(mFocusChangeListener);
//		rl_rec_data_4.setOnClickListener(this);
//		
//		rl_rec_data_5.setOnFocusChangeListener(mFocusChangeListener);
//		rl_rec_data_5.setOnClickListener(this);
		
		//热点头条
		for (TextView tv : mHeadLineList) {
			tv.setOnFocusChangeListener(mFocusChangeListener);
			tv.setOnClickListener(this);
		}

		//编排电影
		rl_rec_movie = (RelativeLayout) view.findViewById(R.id.rl_rec_movie);
		rl_rec_movie.setOnFocusChangeListener(mFocusChangeListener);
		rl_rec_movie.setOnClickListener(this);
		for(TextView tv : mMovieList) {
			tv.setOnFocusChangeListener(mFocusChangeListener);
			tv.setOnClickListener(this);
		}
		
		//编排电视剧
		rl_rec_series = (RelativeLayout) view.findViewById(R.id.rl_rec_series);
		rl_rec_series.setOnFocusChangeListener(mFocusChangeListener);
		rl_rec_series.setOnClickListener(this);
		for(TextView tv : mSeriesList) {
			tv.setOnFocusChangeListener(mFocusChangeListener);
			tv.setOnClickListener(this);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

	}

	@Override
	public void onScrollProgress(int progress) {
		seekBar.setProgress(progress);
	}

	@Override
	public void onConnnected(MyListener arg0) {
		if (this.isVisible()) {
			RecommendFragment.this.arg0 = arg0;
			//childHandler.sendMessage(childHandler
			//		.obtainMessage(REMessage.GETIMAGEANDTEXTDATA));
//			getLoaderManager().restartLoader(RELoaderID.GETIMAGEANDTEXTDATA, null, this);
			//getLoaderManager().restartLoader(RELoaderID.GETTEXTDATA, null, this);
//			getLoaderManager().restartLoader(RELoaderID.GETTEXTDATA_IPS, null, this);
			getLoaderManager().restartLoader(RELoaderID.GETPLAYURLANDPLAY, null, this);
//			getLoaderManager().restartLoader(RELoaderID.GETEVERYONEISWATCHINGDATA, null, this);
			
			//请求热点头条数据
			getLoaderManager().restartLoader(RELoaderID.GET_REC_HEADLINE, 
					Utils.CreateFolderContentBundle(REC_HEADLINE_FOLDER_CODE, 3, null), this);
			//请求电影推荐栏目数据
			getLoaderManager().restartLoader(RELoaderID.GET_REC_MOVIE, 
					Utils.CreateFolderContentBundle(REC_MOVIE_FOLDER_CODE, -1, "410,543,JPG"), this);
			//请求电视剧推荐栏目数据
			getLoaderManager().restartLoader(RELoaderID.GET_REC_SERIES,
					Utils.CreateFolderContentBundle(REC_SERIES_FOLDER_CODE, -1, "410,543,JPG"), this);
			
			//请求播放历史
			//bundle在request中创建
			getLoaderManager().restartLoader(Constant.QUERY_HISTORY, null, this);
			//请求收藏
			//bundle在request中创建
			getLoaderManager().restartLoader(Constant.QUERY_COLLECT, null, this);
			
			//请求推荐系统数据
			getLoaderManager().restartLoader(RELoaderID.GET_REC_DATA, null, this);
		}
	}

	public void onActivityResult(int requestCode, int resultCode,
			android.content.Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 非UI线程
	 */
	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == REMessage.SCADA) {
			Long timeLong = (System.currentTimeMillis() - startTime);
			String[] strs = new String[] { Utils.getWasuTime() + "|"
					+ Utils.getTVID() + "|ott_tj|ott|||推荐||" + timeLong+"|||" };
			Utils.sendMessage(RecommendFragment.this.getActivity(), strs,
					"wasu_page_access_info");
		}  else if (msg.what == REMessage.PLAY) {
			if (isHint) {
				if (msg.obj != null) {
					int location[] = new int[2];
					surfaceView.getLocationInWindow(location);
					rect = new Rect(location[0], location[1], location[0] + surfaceView.getWidth() + 1, location[1] + surfaceView.getHeight() + 1);
					playUtils.hplayer(String.valueOf(msg.obj), rect, false);
				}
			}
		}  else if (msg.what == REMessage.PLAYPAUSE) {
			playUtils.playpause();
		}  else if (msg.what == REMessage.PLAYSTOP) {
			//playUtils.playstop();//playdeallocate会调用playstop
			playUtils.playdeallocate();
		} else if (msg.what == REMessage.REFRSHGETPLAYURLANDPLAY) {
			getLoaderManager().restartLoader(RELoaderID.GETPLAYURLANDPLAY, null, this);
		}
		return true;
	};

	public void Refresh() {
//		childHandler.sendMessage(childHandler
//				.obtainMessage(REMessage.GETIMAGEANDTEXTDATA));
		if(childHandler!=null){
		getLoaderManager().restartLoader(RELoaderID.GETIMAGEANDTEXTDATA, null, this);
		//getLoaderManager().restartLoader(RELoaderID.GETTEXTDATA, null, this);
		getLoaderManager().restartLoader(RELoaderID.GETTEXTDATA_IPS, null, this);
		//getLoaderManager().restartLoader(RELoaderID.GETPLAYURLANDPLAY, null, this);
		getLoaderManager().restartLoader(RELoaderID.GETEVERYONEISWATCHINGDATA, null, this);
		childHandler.sendMessageDelayed(
				childHandler.obtainMessage(REMessage.REFRSHGETPLAYURLANDPLAY), 5000);
		}
	}

	@Override
	public void OnPlayListener(int i) {
		Log.e("OnPlayListener", "::" + i);
		if (i == 1) {// 播放开始
			if (RecommendFragment.this.isVisible()) {
				im_surfaceView.post(new Runnable() {
					@Override
					public void run() {
						im_surfaceView.setVisibility(View.GONE);
					}
				});
			}

		} else if (i == 0) {// 播放完成
			if (RecommendFragment.this.isVisible()) {
				im_surfaceView.post(new Runnable() {
					@Override
					public void run() {
						im_surfaceView.setVisibility(View.VISIBLE);
					}
				});
			}

		}
	}

	/***
	 * 定义 Handler 的常量
	 * 
	 * @author 77071
	 *
	 */
	public class REMessage {

		/** 数据采集 **/
		public final static int SCADA = 0x10013;

		/** 播放 **/
		public final static int PLAY = 0x10015;
		
		/** 播放停止 */
		public static final int PLAYSTOP = 0x10016;
		/*播放战艇*/
		public static final int PLAYPAUSE = 0x10017;
		
		/*刷新小窗口播放地址，开始播放*/
		public final static int REFRSHGETPLAYURLANDPLAY = 0x10014;

		/** 刷新3个图文数据Image & Text */
		public final static int REFRESHIMAGEANDTEXTDATA = 0x20016;

		/** 刷新4个 文字数据 */
		public final static int REFRESHTEXTDATA = 0x20017;
		/** 刷新4个 文字数据 ,从IPS取数据*/
		public final static int REFRESHTEXTDATA_IPS = 0x20019;
		/** 刷新大家都在看数据 */
		public final static int REFRESHEVERYONEISWATCHINGDATA = 0x20018;

	}
	public class RELoaderID {
		/** 得到播放地址 **/
		public final static int GETPLAYURLANDPLAY = 0x30014;
		/** 根据播放ID地址查询 ,并播放**/
		public final static int GET_VODURL_STARTPLAY = 0x30015;
		
		/** 得到3个图文数据Image & Text */
		public final static int GETIMAGEANDTEXTDATA = 0x30016;
		
		/** 得到4个 文字数据 */
		public final static int GETTEXTDATA = 0x30017;
		/** 得到4个 文字数据  ,从IPS栏目取得*/
		public final static int GETTEXTDATA_IPS = 0x30020;
		/** 得到大家都在看数据 */
		public final static int GETEVERYONEISWATCHINGDATA = 0x30018;
		/** 根据直播ID取得参数并播放**/
		public final static int GET_LIVEURL_STARTPLAY = 0x30019;
		
		/** 获取推荐系统数据 **/
		public final static int GET_REC_DATA = 0x30021;
		/** 获取编排-热点头条 **/
		public final static int GET_REC_HEADLINE = 0x30022;
		/** 获取编排-电影推荐 **/
		public final static int GET_REC_MOVIE = 0x30023;
		/** 获取编排-电视剧推荐 **/
		public final static int GET_REC_SERIES = 0x30024;
	}
	
	/** 热点头条栏目别名 */
	private final static String REC_HEADLINE_FOLDER_CODE = "mhtt";
	/** 电影栏目别名 */
	private final static String REC_MOVIE_FOLDER_CODE = "mov_31";
	/** 电视剧栏目别名 */
	private final static String REC_SERIES_FOLDER_CODE = "ser_27";

	@Override
	public Loader<Object> onCreateLoader(final int id, final Bundle args) {
		RequestAsyncTaskLoader request = new RequestAsyncTaskLoader(context);
			request.setLoadInBackgroundListener(new RequestAsyncTaskLoader.LoadInBackgroundListener() {
			@Override
			public Object loadInBackgroundListener() {
					if(arg0==null){
						return null;
					}
					if (id ==com.wasu.vod.utils.Constant.QUERY_AUTH_PLAYURL) {
						if (args != null) {
							 return WasuUtils.wasu_sdk_content_query(arg0, id, args);
						}
					}else if(id ==RELoaderID.GETIMAGEANDTEXTDATA){
						List<String> string1 = new ArrayList<String>();
						string1.add("DVBOTT_HD_10001100");
						string1.add("DVBOTT_HD_10001200");
						string1.add("DVBOTT_HD_10001300");
						/** 3个推荐位置 */
							try {
								String aa1 = arg0.wasu_recommsys_recommlist_get(Utils
										.CreateRecommendContentBundle(string1).getString(
												"param"));
								if (!TextUtils.isEmpty(aa1)) {
									return gson.fromJson(aa1, RecommendResponseResult.class);
									
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}


					}else if(id ==RELoaderID.GETTEXTDATA){
						/** TEXT数据 */
						List<String> string2 = new ArrayList<String>();
						string2.add("DVBOTT_HD_10001400");
							String aa2;
							try {
								aa2 = arg0.wasu_recommsys_recommlist_get(Utils
										.CreateRecommendContentBundle(string2).getString(
												"param"));								
								if (!TextUtils.isEmpty(aa2)) {
									return gson.fromJson(aa2, RecommendResponseResult.class);
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

					}else if(id ==RELoaderID.GETTEXTDATA_IPS){
						String foldername = "mhtt";
						 return WasuUtils.wasu_sdk_content_query(arg0, Constant.QUERY_FOLDER, Utils.CreateFolderContentBundle(foldername));
			
					}else if(id ==RELoaderID.GETPLAYURLANDPLAY){
						/** 播放地址 */
						List<String> string3 = new ArrayList<String>();
						string3.add("DVBOTT_HD_10001000");
						try {
							String aa3 = arg0.wasu_recommsys_recommlist_get(Utils
									.CreateRecommendContentBundle(string3).getString(
											"param"));
							if (!TextUtils.isEmpty(aa3)) {
								return gson.fromJson(aa3, RecommendResponseResult.class);
								}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
					}else if(id ==RELoaderID.GET_VODURL_STARTPLAY){
						/** 查询小窗口播放地址 */
						try {
							String playurl= arg0.wasu_interactive_playUrl_query(null, fiid,
											"13", null, null, null, -1, null,
											null, null, null, null, null, null);	
							if (!TextUtils.isEmpty(playurl)) {								
									return gson.fromJson(playurl, WasuPlayUrl.class);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}else if(id ==RELoaderID.GETEVERYONEISWATCHINGDATA){
						/** 大家都在看 **/
						try {
							String bbString =  arg0.wasu_recommsys_ranklist_get(Utils
									.CreateRankListContentBundle(Constant.MOVIE).getString(
											"param"));
							//Log.d("yingjh", "bbString="+bbString);
							if (!TextUtils.isEmpty(bbString)) {
								return gson.fromJson(bbString,RankListResponseBody.class);
								}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else if(id ==RELoaderID.GET_LIVEURL_STARTPLAY){
						try {
							return arg0.wasu_ip_query_chInfor(fiid);
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else if (id == Constant.QUERY_HISTORY) {
						//请求播放历史数据
						Bundle bundle = new Bundle();
						bundle.putString("type", "2"); //1：收藏，2：历史
						bundle.putString("contentId", null);
						bundle.putString("programType", null);
						bundle.putString("dateNum", "0"); //0：所有、1：今天、2：昨天、3：前天、4：三天以前；默认为0 
						return WasuUtils.wasu_sdk_content_query(arg0, id, bundle);
					}else if (id == Constant.QUERY_COLLECT) {
						//请求全部收藏记录
						String result = null;
						try {
							result = arg0.wasu_interactive_queryUserAllFavorites();
							if(result != null){
								if(mJsonParser == null){
									mJsonParser = new JsonParser();
								}
								JsonElement jEl = mJsonParser.parse(result);
								if(jEl.isJsonArray()){
									JsonArray jArray = jEl.getAsJsonArray();
									ArrayList<WasuContentIds> lcs = new ArrayList<WasuContentIds>();
								    for(JsonElement obj : jArray ){
								    	WasuContentIds cse = gson.fromJson( obj , WasuContentIds.class);
								        lcs.add(cse);
								    }  
								    return lcs;
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else if (id == RELoaderID.GET_REC_DATA) {
						//请求推荐系统数据
						List<String> string = new ArrayList<String>();
						string.add(REC_DATA_1);
						string.add(REC_DATA_2);
						string.add(REC_DATA_3);
//						string.add(REC_DATA_4);
						string.add(REC_DYNAMIC_DATA);
						try {
							String result = arg0.wasu_recommsys_recommlist_get(Utils
									.CreateRecommendContentBundle(string).getString(
											"param"));
//							Log.d("chenchen", "推荐屏-推荐系统数据返回：" + result);
							if (!TextUtils.isEmpty(result)) {
								return gson.fromJson(result, RecommendResponseResult.class);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}	
					}else if (id == RELoaderID.GET_REC_HEADLINE ||
							id == RELoaderID.GET_REC_MOVIE ||
							id == RELoaderID.GET_REC_SERIES) {
						//编排数据--热点头条/电影/电视剧
						try {
							String result = arg0.wasu_interactive_folder_content_query(
									args.getString("folderCode"),args.getInt("pageIndex"), 
									args.getInt("pageItems"), args.getString("cImageMode"), 
									args.getString("fields",null), args.getInt("segmentIndex"));
							if(result != null){
								return gson.fromJson(result, FolderResponseResult.class);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else if (id >= Constant.QUERY_SERIESDETAILS && id < Constant.QUERY_SERIESDETAILS + 3) {
						//获取电视剧详情信息，用于展现观看历史中电视剧观看至第几集信息
						String result = null;
						if (args != null) {
							try {
								result = arg0.wasu_interactive_tv_content_query(
										args.getString("contentId"), args.getString("cImageMode"), 
										args.getString("epImageMode"), args.getString("fields"), 
										args.getString("folderCode"));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if(result != null){
							return gson.fromJson(result, SeriesResponseResult.class);
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
		if (data != null) {
			int id = loader.getId();
			//Log.d("yingjh", "onLoadFinished id="+id);
			if (id == com.wasu.vod.utils.Constant.QUERY_AUTH_PLAYURL) {
				JSONObject result = (JSONObject) data;
				try {
					String AuthResult = result.getString("result");
					if (AuthResult.equalsIgnoreCase("0") && result.has("authResult") && result.getString("authResult").equalsIgnoreCase("0") ) {
						Log.d("yingjh", "AuthResult OK");
						if(player_CustomContent!=null){
							String contentTypeVal = player_CustomContent.getContentType();
							String nameVal = player_CustomContent.getName();
							String contentIdVal = player_CustomContent.getContentId();
							String programType = player_CustomContent.getProgramType();
							Packages.toActivityByClassName(getActivity()
									.getApplicationContext(),
									"com.wasu.vod.PlayerActivity", null, new String[] {
											"contentId", "name", "contentType",
											"programType" }, new String[] { contentIdVal,
											nameVal, contentTypeVal, programType }, null);
							player_CustomContent = null;
						}else if(palyer_wasuInfo!=null){
							String contentTypeVal = palyer_wasuInfo.getFoddertype();
							String nameVal = palyer_wasuInfo.getText();
							String contentIdVal = palyer_wasuInfo.getFodderid();
							String programType = palyer_wasuInfo.getFoddertype();
							Packages.toActivityByClassName(getActivity()
									.getApplicationContext(),
									"com.wasu.vod.PlayerActivity", null, new String[] {
											"contentId", "name", "contentType",
											"programType" }, new String[] { contentIdVal,
											nameVal, contentTypeVal, programType }, null);
							palyer_wasuInfo = null;
						}
					} else {
						Log.d("yingjh", "AuthResult Failed");
						//Toast.makeText(context, "该节目未授权", Toast.LENGTH_SHORT);
						if (result.has("authResult")) {
							String authResultCode = result.getString("authResult");
							if (authResultCode.equalsIgnoreCase("-2119")) {// 欠费停机用户
																			// 跳充值
								
								final PayDialogFragment fragment = PayDialogFragment
										.newInstance(
												-1, WasuPayParaUtils.getPayParameterList(null,"1"),
												"fe55b3f461394242a95858a8b6cbfd2d");
								fragment.registerPayListener(new TrackPayResultInteface() {

									@Override
									public void onPayResult(
											boolean arg0) {
										// TODO
										// Auto-generated
										// method
										// stub
										if (arg0) {
											Toast.makeText(RecommendFragment.this.getActivity().getApplicationContext(), "充值成功", Toast.LENGTH_LONG).show();
										}
										// 充值成功
										fragment.unRegisterPayListener();

									}

								});
								fragment.show(RecommendFragment.this.getFragmentManager(), "wasupay");
								
								
							} else if (authResultCode.equalsIgnoreCase("9999") || authResultCode.equalsIgnoreCase("-9999")) {// 浏览用户
																					// 跳订购
								
								Intent intent = new Intent();
								intent.setClassName("com.wasu.launcher",
										"com.wasu.launcher.activity.UserActivity");
								intent.putExtra("form", "agree");// 需加参数跳到对应页
								RecommendFragment.this.getActivity().startActivity(intent);
								
								
							} else if (authResultCode.equalsIgnoreCase("-2103")
									|| authResultCode.equalsIgnoreCase("-2126")) {

								Intent intent = new Intent(RecommendFragment.this.getActivity(),PurchaseActivity.class);
								intent.putExtra("pValue", "");

								this.startActivity(intent);

							} else if (authResultCode.equalsIgnoreCase("1")) {
								
								Toast.makeText(this.getActivity(), "鉴权失败，无法播放！", Toast.LENGTH_LONG).show();
							} else {// 弹出错误信息
								
								Toast.makeText(this.getActivity(), "鉴权错误码："+authResultCode+"", Toast.LENGTH_LONG).show();

							}

						} else {

							// may network exception
							Toast.makeText(this.getActivity(), "由于服务端问题无法鉴权，无法播放！", Toast.LENGTH_LONG).show();
						}

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(id ==RELoaderID.GETIMAGEANDTEXTDATA){
					RecommendResponseResult reconmmendBean1 =(RecommendResponseResult)data;
					if (reconmmendBean1 != null) {
						if (playhandler != null) {
							playhandler.sendMessage(playhandler.obtainMessage(
									REMessage.REFRESHIMAGEANDTEXTDATA,
									reconmmendBean1));
						}
					}
			}else if(id ==RELoaderID.GETTEXTDATA){
					RecommendResponseResult reconmmendBean2 =(RecommendResponseResult)data;
					if (reconmmendBean2 != null){
						if (playhandler != null) {
						playhandler.sendMessage(playhandler.obtainMessage(
								REMessage.REFRESHTEXTDATA, reconmmendBean2));
						}
					}
			}else if(id ==RELoaderID.GETTEXTDATA_IPS){
				FolderResponseResult result = (FolderResponseResult)data;
				if(result!=null){
					 if(result.getContents() == null){
							return;
					 }
					 if (playhandler != null) {
							playhandler.sendMessage(playhandler.obtainMessage(
									REMessage.REFRESHTEXTDATA_IPS, result));
					 }
				}
			}else if(id ==RELoaderID.GETPLAYURLANDPLAY){

					RecommendResponseResult reconmmendBean3 = (RecommendResponseResult)data;
					if (reconmmendBean3 != null
							&& reconmmendBean3.getContents() != null) {
						if (reconmmendBean3.getContents().get(0).getInfoList() != null
								&& reconmmendBean3.getContents().get(0)
										.getInfoList().size() != 0) {
							// Fodderid
							fiid = reconmmendBean3.getContents().get(0)
									.getInfoList().get(0).getFodderid();
							int stream_type = reconmmendBean3.getContents().get(0).getType();
							if(stream_type == 5){//直播
								play_stream_type = 1;
							}else{
								play_stream_type = 0;
							}
							//play_stream_type = 1;
							//fiid ="005901";
							//fiid ="00000000000000000000000000000200";
								if (!TextUtils.isEmpty(fiid)) {
									if(play_stream_type == 1){
										getLoaderManager().restartLoader(RELoaderID.GET_LIVEURL_STARTPLAY, null, this);
									}else{
										getLoaderManager().restartLoader(RELoaderID.GET_VODURL_STARTPLAY, null, this);
									}
								}
						
							
						}
					}
				
			}else if(id ==RELoaderID.GETEVERYONEISWATCHINGDATA){

				RankListResponseBody ranklist = (RankListResponseBody) data;
					if (ranklist != null) {
						playhandler.sendMessage(playhandler.obtainMessage(
								REMessage.REFRESHEVERYONEISWATCHINGDATA,
								ranklist));
					}
				
			}else if(id==RELoaderID.GET_VODURL_STARTPLAY){
						WasuPlayUrl wasuPlayUrl = (WasuPlayUrl)data;
						if (wasuPlayUrl != null
								&& wasuPlayUrl.getPlayUrl() != null
								&& wasuPlayUrl.getPlayUrl()
										.length() != 0) {
							playurlall = wasuPlayUrl.getPlayUrl();// "delivery://1ffb8.1adb.3.170d.c9.6.0.ca.4.0";
							if (!isRunOne) {
								childHandler.removeMessages(REMessage.PLAY);
								childHandler
										.sendMessage(childHandler
												.obtainMessage(
														REMessage.PLAY,
														playurlall));
								lowurl = fiid;
								back_playurl = playurlall;
								isRunOne = true;
							}
							if ((!"".equals(lowurl)
									&& !lowurl.equals(fiid))||(!"".equals(back_playurl)
											&& !back_playurl.equals(playurlall))) {
								Log.e("zh", "zh 视频源换了");
								playUtils.setTime(0);
								childHandler.removeMessages(REMessage.PLAY);
								childHandler
										.sendMessage(childHandler
												.obtainMessage(
														REMessage.PLAY,
														playurlall));
								lowurl = fiid;
								back_playurl = playurlall;
							}
						}
			}else if(id ==RELoaderID.GET_LIVEURL_STARTPLAY){
				WasuChInfor ws = (WasuChInfor)data;
				if (ws.getWdl() != null && ws.getWdl().getFreq() != null && ws.getWdl().getRate() != null
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

					playurlall = "DELIVERY://" + sfreq + "." + srate + "." + qam + "." + ssid + ".0.0.0.0.0.0" + "";
					Log.d("yingjh", "playurlall="+playurlall);
					if (!isRunOne) {
						childHandler.removeMessages(REMessage.PLAY);
						childHandler.sendMessage(childHandler.obtainMessage(REMessage.PLAY,
												playurlall));
						lowurl = fiid;
						back_playurl = playurlall;
						isRunOne = true;
					}else{
						if ((!"".equals(lowurl)&& !lowurl.equals(fiid))||(!"".equals(back_playurl)
								&& !back_playurl.equals(playurlall))) {
							playUtils.setTime(0);
							childHandler.removeMessages(REMessage.PLAY);
							childHandler.sendMessage(childHandler.obtainMessage(REMessage.PLAY,
													playurlall));
							lowurl = fiid;
							back_playurl = playurlall;
						}
					}
				}
			}else if (id == Constant.QUERY_HISTORY) {
				//播放历史数据返回
				HistoryResponseResult result = (HistoryResponseResult)data;
			    if(result.getContents() == null){
			    	return;
			    }
//			    for (int i = 0; i < result.getContents().size(); i++) {
//			    	WasuContentId wasuContentId = result.getContents().get(i);
//			    	Log.d("chenchen", "ContentId:" + wasuContentId.getContentId()
//			    			+ ", ContentType:" + wasuContentId.getContentType()
//			    			+ ", FolderCode:" + wasuContentId.getFolderCode()
//			    			+ ", FolderName:" + wasuContentId.getFolderName()
//			    			+ ", Name:" + wasuContentId.getName()
//			    			+ ", PlayExitTime:" + wasuContentId.getPlayExitTime()
//			    			+ ", ProgramId:" + wasuContentId.getProgramId()
//			    			+ ", ProgramType:" + wasuContentId.getProgramType()
//			    			+ ", RecordTime:" + wasuContentId.getRecordTime());
//				}
			    
			    filterPlayHistory(result);
			}else if (id == Constant.QUERY_COLLECT) {
				//收藏数据返回
				ArrayList<WasuContentIds> result = (ArrayList<WasuContentIds>) data;
				if (result == null || result.size() < 2) {
					rl_rec_collection.setVisibility(View.VISIBLE);
					rl_rec_collection.setFocusable(true);
					tv_rec_collection_title.setVisibility(View.INVISIBLE);
					tv_collection_item_0.setVisibility(View.INVISIBLE);
					tv_collection_item_1.setVisibility(View.INVISIBLE);
					rec_collection_divide_line.setVisibility(View.INVISIBLE);
					return;
				}
				
				rl_rec_collection.setVisibility(View.INVISIBLE);
				tv_rec_collection_title.setVisibility(View.VISIBLE);
				tv_collection_item_0.setVisibility(View.VISIBLE);
				tv_collection_item_1.setVisibility(View.VISIBLE);
				rec_collection_divide_line.setVisibility(View.VISIBLE);
				
				for (int i = 0; i < 2; i++) {
					WasuContentIds wasuContentIds = result.get(i);
					if (wasuContentIds  == null) {
						continue;
					}
					CustomContent customContent = new CustomContent(wasuContentIds);
					TextView title = mCollectionTvArray.get(i);
					title.setText(customContent.getName());
					title.setTag(customContent);
				}
			}else if (id == RELoaderID.GET_REC_DATA) {
				RecommendResponseResult result = (RecommendResponseResult) data;
				if(result == null || result.getContents() == null){
                    return;
                }
				filterRecData(result);
			}else if (id == RELoaderID.GET_REC_HEADLINE) {
				//编排数据-热点头条
				FolderResponseResult result = (FolderResponseResult) data;
				if(result == null || result.getContents() == null){
                    return;
                }
				for (int i = 0; i < result.getContents().size() && i < mHeadLineList.size(); i++) {
					Contents contents = result.getContents().get(i);
					if (contents  == null) {
						continue;
					}
					WasuSegment wasuSegment = contents.getSegment();
					TextView tv = mHeadLineList.get(i);
					if (wasuSegment != null && !TextUtils.isEmpty(wasuSegment.getName())) {
						tv.setText(wasuSegment.getName());
					}
					tv.setTag(contents);
				}
			}else if (id == RELoaderID.GET_REC_MOVIE) {
				//编排数据-电影
				FolderResponseResult result = (FolderResponseResult) data;
				if(result == null || result.getContents() == null){
                    return;
                }
				for (int i = 0; i < result.getContents().size() && i < (mMovieList.size() + 1); i++) {
					Contents contents = result.getContents().get(i);
					if (contents  == null) {
						continue;
					}
					CustomContent customContent = new CustomContent(contents);
					if (i == 0) {
						//第一行以海报形式展现
						if (customContent.getUrl() != null) {
							imageLoader.get(customContent.getUrl(), ImageLoader.getImageListener(iv_rec_movie, 0, 0));
						}
						rl_rec_movie.setTag(customContent);
					}else {
						//从第二行开始以文字位形式展现
						TextView tv = (TextView) mMovieList.get(i - 1);
						if (!TextUtils.isEmpty(customContent.getName())) {
							tv.setText(customContent.getName());
						}
						tv.setTag(customContent);
					}
				}
			}else if (id == RELoaderID.GET_REC_SERIES) {
				//编排数据-电视剧
				FolderResponseResult result = (FolderResponseResult) data;
				if(result == null || result.getContents() == null){
                    return;
                }
				for (int i = 0; i < result.getContents().size() && i < (mSeriesList.size() + 1); i++) {
					Contents contents = result.getContents().get(i);
					if (contents  == null) {
						continue;
					}
					CustomContent customContent = new CustomContent(contents);
					if (i == 0) {
						//第一行以海报形式展现
						if (customContent.getUrl() != null) {
							imageLoader.get(customContent.getUrl(), ImageLoader.getImageListener(iv_rec_series, 0, 0));
						}
						rl_rec_series.setTag(customContent);
					}else {
						//从第二行开始以文字位形式展现
						TextView tv = (TextView) mSeriesList.get(i - 1);
						if (!TextUtils.isEmpty(customContent.getName())) {
							tv.setText(customContent.getName());
						}
						tv.setTag(customContent);
					}
				}
			}else if (id >= Constant.QUERY_SERIESDETAILS && id < Constant.QUERY_SERIESDETAILS + 3) {
				//播放历史中电视剧的观看历史数据信息
				SeriesResponseResult result = (SeriesResponseResult) data;
				if(result == null || result.getContentItems() == null){
                    return;
                }
				int index = id - Constant.QUERY_SERIESDETAILS;
				for (ContentItem item : result.getContentItems()) {
					if (item.getIsPlay() == 1) {
//						LinearLayout layout = (LinearLayout) rl_rec_history_list.findViewById(
//								getResources().getIdentifier("ll_rec_history_item_" + index, "id", context.getPackageName()));
//						TextView subTitle = (TextView) layout.getChildAt(1);
						String str = getString(R.string.rec_play_history_series, item.getIndex());
//						subTitle.setText(str);
						if (mHistoryEpgArray != null && mHistoryEpgArray.size() > index) {
							mHistoryEpgArray.get(index).setText(str);
						}
					}
				}
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Object> loader) {
		// TODO Auto-generated method stub
		
	}
	protected void showOnFocusTranslAnimation(View v) {
		switch (v.getId()) {
		case R.id.rl_rec_video_view:
			if (surface_mask != null) {
				surface_mask.setVisibility(View.VISIBLE);
			}
			rl_rec_video_view.bringToFront();
			return;
		case R.id.ll_rec_history_item_0:
			history_tv_0.setFocuse(true);
			history_tv_0.setText(history_tv_0.getText());
			history_epg_0.setFocuse(true);
			history_epg_0.setText(history_epg_0.getText());
			break;
		case R.id.ll_rec_history_item_1:
			history_tv_1.setFocuse(true);
			history_tv_1.setText(history_tv_1.getText());
			history_epg_1.setFocuse(true);
			history_epg_1.setText(history_epg_1.getText());
			break;
		default:
			break;
		}
		super.showOnFocusTranslAnimation(v);
	}

	@Override
	protected void showLooseFocusTranslAinimation(View v) {
		switch (v.getId()) {
		case R.id.rl_rec_video_view:
			if (surface_mask != null) {
				surface_mask.setVisibility(View.INVISIBLE);
			}
			return;
		case R.id.ll_rec_history_item_0:
			history_tv_0.setFocuse(false);
			history_tv_0.setText(history_tv_0.getText());
			history_epg_0.setFocuse(false);
			history_epg_0.setText(history_epg_0.getText());
			break;
		case R.id.ll_rec_history_item_1:
			history_tv_1.setFocuse(false);
			history_tv_1.setText(history_tv_1.getText());
			history_epg_1.setFocuse(false);
			history_epg_1.setText(history_epg_1.getText());
			break;
		default:
			break;
		}
		super.showLooseFocusTranslAinimation(v);
	}

	@Override
	public void scrollVideoView() {
		if (surfaceView != null) {
			int location[] = new int[2];
			surfaceView.getLocationInWindow(location);
			rect = new Rect(location[0], location[1], location[0] + surfaceView.getWidth() + 1, location[1] + surfaceView.getHeight() + 1);
			playUtils.setPlayArea(rect);
		}
	}
	
	/**
	 * 填充播放历史数据
	 * 过滤视频新闻类后，历史记录大于2条用列表形式展现，不足2条以入口形式展现
	 * @TODO 电视剧无法获取看到第几集
	 * @param result 播放历史数据
	 */
	private void filterPlayHistory(HistoryResponseResult result) {
		int[] cusors = new int[2]; // 记录前两条非视频新闻类资产的索引
	    int size = 0;
	    for (int i = 0; i < result.getContents().size(); i++) {
	    	WasuContentId wasuContentId = result.getContents().get(i);
	    	if (wasuContentId == null) {
				continue;
			}
	    	//过滤视频新闻类型(contentType:13)
	    	if (!wasuContentId.getContentType().equals("13")) {
	    		cusors[size++] = i;
	    		if (size == 2) {
					break;
				}
			}
		}
	    
	    if (size < 2) {
	    	//播放历史数量少于2条时不需要更改成列表形式
	    	rl_rec_history.setVisibility(View.VISIBLE);
	    	rl_rec_video_view.setNextFocusDownId(R.id.rl_rec_history);
	    	
	    	tv_rec_history_title.setVisibility(View.INVISIBLE);
	    	rec_history_divide_line.setVisibility(View.INVISIBLE);
	    	ll_rec_history_item_0.setVisibility(View.INVISIBLE);
	    	ll_rec_history_item_1.setVisibility(View.INVISIBLE);
	    	return;
		}
	    
	    rl_rec_video_view.setNextFocusDownId(R.id.tv_rec_history_title);
	    rl_rec_history.setVisibility(View.INVISIBLE);
	    
	    tv_rec_history_title.setVisibility(View.VISIBLE);
    	rec_history_divide_line.setVisibility(View.VISIBLE);
    	ll_rec_history_item_0.setVisibility(View.VISIBLE);
    	ll_rec_history_item_1.setVisibility(View.VISIBLE);
	    
	    //取最新两条数据展现
		for (int i = 0; i < cusors.length; i++) {
			WasuContentId wasuContentId = result.getContents().get(cusors[i]);
			CustomContent customContent = new CustomContent(wasuContentId);
			LinearLayout layout = mHistoryLayoutArray.get(i);
			layout.setTag(customContent);
			
			//片名
			MarqueeTextView title = mHistoryTvArray.get(i);
			//副标题
			MarqueeTextView subTitle = mHistoryEpgArray.get(i);
			long time;
			String str;
			
			String contentType = wasuContentId.getContentType();
			String name = wasuContentId.getName();
			String playExitTime = wasuContentId.getPlayExitTime();
			if (contentType == null || name == null || playExitTime == null) {
				continue;
			}
			switch (contentType) {
			case "36":
				//影片类(contentType:36)
				title.setText(name);
				time = Long.parseLong(playExitTime);
				str = getString(R.string.rec_play_history, formatDateTime(time));
				subTitle.setText(str);
				break;
			case "37":
				//剧集类(contentType:37)
				title.setText(name);
				time = Long.parseLong(playExitTime);
//				str = getString(R.string.rec_play_history, formatDateTime(time));
//				subTitle.setText(str);
				
				//电视剧的副标题为观看至第x集，播放历史返回数据中没有，需调用电视剧详情接口获取
				Bundle bundle = new Bundle();
				bundle.putString("contentId", customContent.getContentId());
				getLoaderManager().restartLoader(Constant.QUERY_SERIESDETAILS + i, bundle, this);
				break;
			case "15":
				//栏目类(contentType:15)
				String[] strs = null;
				/**
				 * 栏目类分三种形式：
				 * 1、xxx 第x集（直播热剧回放）：根据空格截取栏目标题，展示主标题 xxx 副标题观看至第x集；
				 * 2、xxx_yyyymmdd（点播栏目）：根据下划线截取栏目标题，展示主标题xxx 副标题观看至yyyymmdd；
				 * 3、xxx（求索）：主标题：xxx，副标题为空
				 * */
				if (name.contains(" ")) {
					strs = name.split(" ");
				}else if (name.contains("_")) {
					strs = name.split("_");
				}else {
					title.setText(name);
					subTitle.setText("");
				}
				if (strs != null) {
					title.setText(strs[0]);
					str = getString(R.string.rec_play_history, strs[1]);
					subTitle.setText(str);
				}
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * 将秒数转化成小时分秒（xx:xx:xx）格式返回
	 * @param mss
	 * @return
	 */
	public static String formatDateTime(long mss) {
		long hours = (mss % ( 60 * 60 * 24)) / (60 * 60);
		long minutes = (mss % ( 60 * 60)) /60;
		long seconds = mss % 60;
		String strTime = String.format(Locale.getDefault(), 
				"%1$02d:%2$02d:%3$02d", hours, minutes, seconds);
		
		return strTime;
	}
	
	/**
	 * 基于VOD工程中BaseActivity移植
	 * @TODO 回放栏目类需跳转到播放界面，不然会提示栏目已下线（待修改）
	 * @param customContent
	 * @param ml
	 * @param folderCode
	 * @param isFromFolder
	 * @param backtype
	 */
	
	public void openDetailsActivity(final CustomContent customContent, final MyListener ml, final String folderCode, final int isFromFolder, String backtype){
		if(customContent == null)
			return;
		final Intent intent = new Intent();
		String contentId = customContent.getContentId();
		final String contentType = customContent.getContentType();
		Log.d("xiexiegang","openDetailsActivity contentType:"+contentType+",contentId:"+customContent.getContentId());
		if(contentId != null && contentId.startsWith("http:") ||  !TextUtils.isEmpty(contentType) && contentType.equals("102")){
			intent.setClass(context, TopicActivity.class);
			intent.putExtra("param", contentId);
		} else if (!TextUtils.isEmpty(contentType) && contentType.equals("100")) {//go to folder
			
			
			String folderAliasName = customContent.getFolderUrl();
			if (!TextUtils.isEmpty(folderAliasName)) {
				int index = folderAliasName.indexOf("/a?f=");
				if (index != -1) {
				    folderAliasName = folderAliasName.substring(index + "/a?f=".length());
				} else {
					folderAliasName = customContent.getContentId();
				}
			}
			
			String getDataStyle = UILApplication.getFolderStyleContentMap().get(folderAliasName != null ? folderAliasName : customContent.getContentId());
			
			if (getDataStyle != null) {
				
				int templateType = Integer.valueOf(getDataStyle.split(",")[0]);
				if (templateType == 15) {
					
					intent.setClass(context, VodDetailsActivity.class);
				    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				    
				    intent.putExtra("param", folderAliasName);
				    intent.putExtra("extra", customContent);
				    intent.putExtra("folderNameCurrent", folderAliasName);
				    intent.putExtra("isFromFolder", isFromFolder);
//				    intent.putExtra(VodDetailsActivity.BACKGROUD_TYPE, backtype);
					
				} else {
				    intent.setAction("com.wasu.intent.folder");
				    intent.putExtra("foldCode", folderAliasName);
				    intent.putExtra("getDataStyle", getDataStyle.split(",")[1]);
				    intent.putExtra("folderName", "");
				    intent.putExtra("style", templateType);
				    intent.putExtra("TYPE", "");
				}
			    
			} else {
				Log.e(TAG, "liqiuxuTest folder: getDataStyle==null");
			}

		
		} else{
		if(contentType != null){
			if(com.wasu.vod.utils.Utils.isTypeMovie(contentType)){
				intent.setClass(context, VodDetailsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("TYPE", "MOVIE");
				intent.putExtra("param", customContent.getContentId());
				intent.putExtra("folderNameCurrent", folderCode);
				intent.putExtra("isFromFolder", isFromFolder);
//				intent.putExtra(VodDetailsActivity.BACKGROUD_TYPE, backtype);
			}else if(com.wasu.vod.utils.Utils.isTypeSeries(contentType)){
				intent.setClass(context, VodDetailsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("TYPE", "SERIES");
				intent.putExtra("param", customContent.getContentId());
				intent.putExtra("folderNameCurrent", folderCode);
				intent.putExtra("isFromFolder", isFromFolder);
//				intent.putExtra(VodDetailsActivity.BACKGROUD_TYPE, backtype);
				
			} else if (com.wasu.vod.utils.Utils.isTypeSeries(customContent.getProgramType()) && isFromFolder != Constant.HISTORY) {
				
				intent.setClass(context, VodDetailsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("TYPE", "SERIES");
				intent.putExtra("param", customContent.getContentId());
				intent.putExtra("folderNameCurrent", folderCode);
				intent.putExtra("isFromFolder", isFromFolder);
//				intent.putExtra(VodDetailsActivity.BACKGROUD_TYPE, backtype);
				
			} else{
				intent.setClass(context, PlayerActivity.class);
				intent.putExtra("TYPE", Constant.PLAYER_VOD);
				intent.putExtra("contentId", customContent.getContentId());
				intent.putExtra("contentType", customContent.getContentType());
				intent.putExtra("folderCode", folderCode);
				if(customContent.getProgramType() == null)
					intent.putExtra("programType", customContent.getContentType());
				else
				intent.putExtra("programType", customContent.getProgramType());
				intent.putExtra("name", customContent.getName());
				intent.putExtra("hdFlag", customContent.getHdFlag());		
				Object object = customContent.getExtra();
				
				String startTime = null;
				String endTime = null;
				if(object != null && object instanceof WasuSegment){
					WasuSegment segment = (WasuSegment)object;
					intent.putExtra("name", segment.getName());
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
					try {
						Date startDate = sdf.parse(segment.getBeginTime());
						Date endDate = sdf.parse(segment.getEndTime());
						startTime = startDate.getHours()*3600+startDate.getMinutes()*60+startDate.getSeconds()+"";
						endTime = endDate.getHours()*3600+endDate.getMinutes()*60+endDate.getSeconds()+"";
						intent.putExtra("startTime", startTime);
						intent.putExtra("endTime", endTime);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					 
					intent.putExtra("segmentId", segment.getSegmentId());	
				}
				
				if (ml != null) {
					
					final String tempStartTime = startTime;
					final String tempEndTime = endTime;
					
					new Thread(new Runnable() {

						@Override
							public void run() {
								// TODO Auto-generated method stub
								Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);
								String temp = null;
								try {
									temp = ml.wasu_interactive_auth_playUrlQuery(folderCode, customContent.getContentId(),
													contentType, null, null,
													null, -1, tempStartTime,
													tempEndTime, null, null,
													null, null, null);
								} catch (Exception e) {
									e.printStackTrace();
									Log.e(TAG,"wasu_interactive_auth_playUrlQuery failed: "
													+ e.getLocalizedMessage());
								}
								try {

									final JSONObject result = temp == null ? null
											: new JSONObject(temp);

									childHandler.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub

											if (result != null) {

												try {

													String AuthResult = result.getString("result");

													if (AuthResult.equalsIgnoreCase("1")) {

														Toast.makeText(context, "由于服务端返回错误，无法鉴权！", Toast.LENGTH_LONG).show();
														return;

													} else {

														if (result.has("authResult")) {

															String authResultCode = result
																	.getString("authResult");

															if (authResultCode.equalsIgnoreCase("-2119")) {// 欠费停机用户跳充值
																
																try {

																	final PayDialogFragment fragment = PayDialogFragment
																			.newInstance(
																					-1, WasuPayParaUtils.getPayParameterList(ml.wasu_bsa_getTvid(),"1"),
																					"fe55b3f461394242a95858a8b6cbfd2d");
																	fragment.registerPayListener(new TrackPayResultInteface() {

																		@Override
																		public void onPayResult(
																				boolean arg0) {
																			// TODO
																			// Auto-generated
																			// method
																			// stub
																			if (arg0) {
																				Toast.makeText(context, "充值成功", Toast.LENGTH_LONG).show();
																			}
																			// 充值成功
																			fragment.unRegisterPayListener();

																		}

																	});
																	fragment.show(getFragmentManager(), "wasupay");
																} catch (Exception e) {
																	
																}

															} else if (authResultCode.equalsIgnoreCase("9999")) {// 浏览用户跳注册
																
																Intent intent = new Intent();
																intent.setClassName("com.wasu.launcher", "com.wasu.launcher.activity.UserActivity");
																intent.putExtra("form", "agree");
																context.startActivity(intent);
																return;

															} else if (authResultCode.equalsIgnoreCase("-2103") ||
																	authResultCode.equalsIgnoreCase("-2126")) {
																
																new Thread(new Runnable(){

																	@Override
																	public void run() {
																		// TODO Auto-generated method stub
																		
																		try {
																			
																			String vedio = ml.wasu_interactive_vedionews_content_query(customContent.getContentId(), null, null, null, null);
																			PlaybackResponseResult playbackResponseResult =  new Gson().fromJson(vedio, PlaybackResponseResult.class); 
																			final String ppv = getMostBitrateServiceCodeAsPPV(playbackResponseResult.getVideos()).getServiceCode();
																			
																			childHandler.post(new Runnable() {

																				@Override
																				public void run() {
																					// TODO Auto-generated method stub
																					//跳到快捷订购界面
																					Intent intent = new Intent(context, PurchaseActivity.class);
																					intent.putExtra("pValue", ppv);
																					startActivityForResult(intent, REQUEST_BASE_QUICK_ORDER);
																				}
																				
																			});
																			
																		} catch (Exception e) {
																			// TODO Auto-generated catch block
																			e.printStackTrace();
																		}
																		
																	}
																	
																}).start();
																
																

															} else if (authResultCode.equalsIgnoreCase("0")){
																
																startActivity(intent);
																getActivity().overridePendingTransition(android.R.anim.fade_in,
																		android.R.anim.fade_out);
																return;

															} else if (authResultCode.equalsIgnoreCase("1")){// 弹出错误信息
																
																Toast.makeText(context, "鉴权失败，无法播放！", Toast.LENGTH_LONG).show();
																
															} else {//// 弹出错误信息
																Toast.makeText(context, "鉴权错误码："+authResultCode+"", Toast.LENGTH_LONG).show();
															}

														} else {

															// may network exception
															Toast.makeText(context, "由于服务端问题无法鉴权，无法播放！", Toast.LENGTH_LONG).show();
														}
														return;
													}

												} catch (Exception e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}
											}

										}

									});
								} catch (Exception e) {
									
									Log.e(TAG,"wasu_interactive_auth_playUrlQuery failed: "
											+ e.getLocalizedMessage());

								}

							}
						
					}).start();
					
				}
				
				return;

                }
            }  else {
                intent.setClass(context, VodDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("param", customContent.getContentId());
                intent.putExtra("extra", customContent);
                intent.putExtra("folderNameCurrent", customContent.getContentId());
                intent.putExtra("isFromFolder", isFromFolder);
//                intent.putExtra(VodDetailsActivity.BACKGROUD_TYPE, backtype);
            }
        }

//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		getActivity().overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
		
	}
	
	public WasuVideo getMostBitrateServiceCodeAsPPV(List<WasuVideo> wasuVideos) {
		
		WasuVideo wv = wasuVideos.get(0);
		
		for (int j = 1; j < wasuVideos.size(); j++) {
			if (Long.valueOf(wasuVideos.get(j).getBitrate()) > Long.valueOf(wv.getBitrate())) {
				wv = wasuVideos.get(j);
			}
		}
		return wv;
	}
	
	/**
	 * 填充推荐系统返回数据
	 * @param result
	 */
	private void filterRecData(RecommendResponseResult result) {
		for (int i = 0; i < result.getCount(); i++) {
			WasuContent wasuContent = result.getContents().get(i);
			if (wasuContent == null) {
				return;
			}
			List<WasuInfo> infoList = wasuContent.getInfoList();
			if (infoList == null || infoList.isEmpty()) {
				continue;
			}
			WasuInfo wasuInfo;
			switch (wasuContent.getPlaceCode()) {
			case REC_DATA_1:
				wasuInfo = infoList.get(0);
				if (wasuInfo != null) {
					if (wasuInfo.getImg() != null) {
						imageLoader.get(wasuInfo.getImg(), ImageLoader.getImageListener(
								iv_rec_data_0, 0, 0));
					}
					rl_rec_data_0.setTag(wasuInfo);
				}
				break;
			case REC_DATA_2:
				wasuInfo = infoList.get(0);
				if (wasuInfo != null) {
					if (wasuInfo.getImg() != null) {
						imageLoader.get(wasuInfo.getImg(), ImageLoader.getImageListener(
								iv_rec_data_1, 0, 0));
					}
					rl_rec_data_1.setTag(wasuInfo);
				}
				break;
			case REC_DATA_3:
				for (int j = 0; j < infoList.size(); j++) {
					if (j == 0) {
						wasuInfo = wasuContent.getInfoList().get(j);
						if (wasuInfo != null) {
							if (wasuInfo.getImg() != null) {
								imageLoader.get(wasuInfo.getImg(), ImageLoader.getImageListener(
										iv_rec_data_2, 0, 0));
							}
							rl_rec_data_2.setTag(wasuInfo);
						}
					}
					if (j == 1) {
						wasuInfo = wasuContent.getInfoList().get(j);
						if (wasuInfo != null) {
							if (wasuInfo.getImg() != null) {
								imageLoader.get(wasuInfo.getImg(), ImageLoader.getImageListener(
										iv_rec_data_3, 0, 0));
							}
							rl_rec_data_3.setTag(wasuInfo);
						}
					}
				}
				break;
//			case REC_DATA_4:
//				for (int j = 0; j < infoList.size(); j++) {
//					if (j == 0) {
//						wasuInfo = wasuContent.getInfoList().get(j);
//						if (wasuInfo != null) {
//							if (wasuInfo.getImg() != null) {
//								imageLoader.get(wasuInfo.getImg(), ImageLoader.getImageListener(
//										iv_rec_data_4, 0, 0));
//							}
//							rl_rec_data_4.setTag(wasuInfo);
//						}
//					}
//					if (j == 1) {
//						wasuInfo = wasuContent.getInfoList().get(j);
//						if (wasuInfo != null) {
//							if (wasuInfo.getImg() != null) {
//								imageLoader.get(wasuInfo.getImg(), ImageLoader.getImageListener(
//										iv_rec_data_5, 0, 0));
//							}
//							rl_rec_data_5.setTag(wasuInfo);
//						}
//					}
//				}
//				break;
			case REC_DYNAMIC_DATA:
				LayoutParams params;
				for (int j = 0; j < infoList.size(); j++) {
					wasuInfo = infoList.get(j);
					if (wasuInfo != null && wasuInfo.getImg() != null) {
						RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.lau_rec_data, null);
						layout.setId(REC_DYNAMIC_DATA_ID + j);
						layout.setOnFocusChangeListener(mFocusChangeListener);
						layout.setOnClickListener(mClickListener);
						layout.setTag(wasuInfo);
						
						ImageView imageView = (ImageView) layout.findViewById(R.id.iv_rec_data);
						imageLoader.get(wasuInfo.getImg(), ImageLoader.getImageListener(
								imageView, 0, 0));
						params = new LayoutParams(380, 380);
						if (j == 0) {
							params.addRule(RelativeLayout.RIGHT_OF, R.id.rl_rec_data_2);
						}else if (j % 2 == 0) {
							params.addRule(RelativeLayout.RIGHT_OF, REC_DYNAMIC_DATA_ID + j - 2);
						}
						if (j == 1) {
							params.addRule(RelativeLayout.RIGHT_OF, R.id.rl_rec_data_3);
							params.addRule(RelativeLayout.BELOW, R.id.rl_rec_data_2);
							params.topMargin = context.getResources().getDimensionPixelSize(R.dimen.sm_8);
						}else if (j % 2 == 1) {
							params.addRule(RelativeLayout.RIGHT_OF, REC_DYNAMIC_DATA_ID + j - 2);
							params.addRule(RelativeLayout.BELOW, REC_DYNAMIC_DATA_ID + j - 1);
							params.topMargin = context.getResources().getDimensionPixelSize(R.dimen.sm_8);
						}
						params.leftMargin = context.getResources().getDimensionPixelSize(R.dimen.sm_8);
						mContentLayout.addView(layout, params);
					}
				}
				break;
			default:
				break;
			}
		}
	}
	
	private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id >= REC_DYNAMIC_DATA_ID && id <= REC_DYNAMIC_DATA_ID + 100) {
				if (v.getTag() instanceof WasuInfo) {
					WasuInfo wasuInfo = (WasuInfo) v.getTag();
					CustomContent customContent = new CustomContent(wasuInfo);
					((BaseActivity)getActivity()).openDetailsActivity(customContent, arg0, customContent.getContentId());
				}
			}
		}
	};
	
	private void startGame(String packageName){
		try {
			Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(packageName);
			startActivity(intent);
			
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getActivity(), "游戏未安装", Toast.LENGTH_LONG).show();
		}
	}
}
