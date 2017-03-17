package com.wasu.launcher.Fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

import com.wasu.bs.pay.TrackPayResultInteface;
import com.wasu.bs.pay.WasuPayParaUtils;
import com.wasu.launcher.BaseActivity;
import com.wasu.launcher.R;
import com.wasu.launcher.activity.HelpActivity;
import com.wasu.launcher.domain.RecommendResponseResult;
import com.wasu.launcher.interfaces.MyFragmentListener;
import com.wasu.launcher.utils.Packages;
import com.wasu.launcher.utils.ScaleAnimEffect;
import com.wasu.launcher.utils.SwitchUtils;
import com.wasu.launcher.utils.Utils;
import com.wasu.launcher.view.SmoothHorizontalScrollView;
import com.wasu.launcher.view.SmoothHorizontalScrollView.ScrollViewListener;
import com.wasu.nostra13.universalimageloader.core.ImageLoader;
import com.wasu.sdk_ott.PayDialogFragment;
import com.wasu.vod.aidl.SDKOperationManager;
import com.wasu.vod.aidl.SDKOperationManager.MyListener;
import com.wasu.vod.domain.CustomContent;
import com.wasu.vod.domain.WasuInfo;
import com.wasu.vod.purchase.PurchaseActivity;
import com.wasu.wisdomfamily.Welcome;

public class UserFragment extends BaseFragment implements OnFocusChangeListener, OnClickListener, Callback {

	/** 取得数据 */
	private static final int GETDATA = 0x1010;
	private static final int GETDATA_REC = 0x1011;
	/** set数据 */
	private static final int SETDATA = 0x1100;
	private static final int SETDATA_REC = 0x1101;

	private View view;
	List<ImageView> bgList;
	Handr3 handr3;
	Handler myhandler;
	HandlerThread handlerThread;

	String phone, balance;
	
	public TextView contentDisplay;
	
	//余额
	private TextView txt_user_balance_value;
	private LinearLayout ll_user_balance;
	//缴费充值
	private RelativeLayout rl_personal_payment;
	//帮助
	private RelativeLayout rl_personal_help;
	//产品订购
	private RelativeLayout rl_personal_order;
	//我的历史
	private RelativeLayout rl_personal_history;
	//智能家居
	private RelativeLayout rl_personal_smart;
	//我的收藏
	private RelativeLayout rl_personal_collection;
	//推荐资产
	private RelativeLayout rl_personal_rec;

	public UserFragment(ScrollViewListener listener) {
		mScrollViewListener = listener;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (container == null) {
			return null;
		}
		if (null == view) {
			view = inflater.inflate(R.layout.fragment_user, container, false);
			handr3 = new Handr3(this);
		}
		
		smManager = new SDKOperationManager(getActivity());
		smManager.registerSDKOperationListener(this);
		return view;
	}

	@Override
	protected void findViewById() {
		mScrollView =  (SmoothHorizontalScrollView) view.findViewById(R.id.personal_scroll_view);
		
		mShadow = (ImageView) view.findViewById(R.id.shadow);
		
		//缴费充值
		rl_personal_payment = (RelativeLayout) view.findViewById(R.id.rl_personal_payment);
		ll_user_balance = (LinearLayout) view.findViewById(R.id.ll_user_balance);
		txt_user_balance_value = (TextView) view.findViewById(R.id.txt_user_balance_value);
		
		//设置
		rl_personal_help = (RelativeLayout) view.findViewById(R.id.rl_personal_help);
		
		//产品订购
		rl_personal_order = (RelativeLayout) view.findViewById(R.id.rl_personal_order);
		
		//我的历史
		rl_personal_history = (RelativeLayout) view.findViewById(R.id.rl_personal_history);
		
		//华数享看
		rl_personal_smart = (RelativeLayout) view.findViewById(R.id.rl_personal_smart);
		
		//我的收藏
		rl_personal_collection = (RelativeLayout) view.findViewById(R.id.rl_personal_collection);
		
		//推荐位
		rl_personal_rec = (RelativeLayout) view.findViewById(R.id.rl_personal_rec);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handlerThread = new HandlerThread("wasu-user");
		handlerThread.start();
		myhandler = new Handler(handlerThread.getLooper(), this);
		SharedPreferences spd = getActivity().getSharedPreferences("mydevice", Context.MODE_APPEND);
		phone = spd.getString("Phone", "");
	}

	String[] strkey = new String[] { "TYPE", "param" };

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

			}else if("103".equals(contentTypeVal)) {
				Packages.toActivityByClassName(getActivity().getApplicationContext(), "com.wasu.vod.TopicActivity", null,
						new String[] { "param" }, new String[] { contentIdVal }, null);
			}
		}
	}

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
		bgList = new ArrayList<ImageView>();
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
		String[] strs = new String[] { Utils.getWasuTime() + "|" + Utils.getTVID() + "|ott_gr|ott|||个人||" + timeLong+"|||" };
		Utils.sendMessage(UserFragment.this.getActivity(), strs, "wasu_page_access_info");
		super.onStop();
	}

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
		
		//缴费充值
		rl_personal_payment.setOnFocusChangeListener(mFocusChangeListener);
		rl_personal_payment.setOnClickListener(this);
		
		//设置
		rl_personal_help.setOnFocusChangeListener(mFocusChangeListener);
		rl_personal_help.setOnClickListener(this);
		
		//产品订购
		rl_personal_order.setOnFocusChangeListener(mFocusChangeListener);
		rl_personal_order.setOnClickListener(this);

		
		//我的历史
		rl_personal_history.setOnFocusChangeListener(mFocusChangeListener);
		rl_personal_history.setOnClickListener(this);
		
		//智能家居
		rl_personal_smart.setOnFocusChangeListener(mFocusChangeListener);
		rl_personal_smart.setOnClickListener(this);
		
		//我的收藏
		rl_personal_collection.setOnFocusChangeListener(mFocusChangeListener);
		rl_personal_collection.setOnClickListener(this);
		
		//推荐位
		rl_personal_rec.setOnFocusChangeListener(mFocusChangeListener);
		rl_personal_rec.setOnClickListener(this);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {

		if (hasFocus) {
			showOnFocusTranslAnimation((View) v.getParent());
		} else {
			showLooseFocusTranslAinimation((View) v.getParent());
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		if (v.getId() == R.id.rl_personal_payment) {
			/* 缴费充值 */
			if (SwitchUtils.isFreeAuthSwitchOn(this.getActivity().getApplicationContext())) {
				Toast.makeText(this.getActivity().getApplicationContext(), "正在建设中，敬请期待", Toast.LENGTH_LONG).show();
				
			} else {
				//if (arg0 != null) {
					
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
							Toast.makeText(UserFragment.this.getActivity(), "充值成功", Toast.LENGTH_LONG).show();
							myhandler.sendMessageDelayed(myhandler.obtainMessage(GETDATA),500);
						}
						// 充值成功
						fragment.unRegisterPayListener();

					}

				});
				fragment.show(UserFragment.this.getFragmentManager(), "wasupay");
				//}
			}
		}else if (v.getId() == R.id.rl_personal_help) {
			/* 设置 */
//			intent = new Intent(getActivity(), LauSettingActivity.class);
//			startActivity(intent);
			/* 帮助 */
			intent = new Intent(getActivity(), HelpActivity.class);
			startActivity(intent);
		}else if (v.getId() == R.id.rl_personal_order) {
			/* 产品订购 */
			if (SwitchUtils.isFreeAuthSwitchOn(this.getActivity().getApplicationContext())) {
				Toast.makeText(this.getActivity().getApplicationContext(), "正在建设中，敬请期待", Toast.LENGTH_LONG).show();
			} else {
				intent = new Intent(UserFragment.this.getActivity(), PurchaseActivity.class);
				intent.putExtra("pValue", "");
				UserFragment.this.startActivity(intent);
			}
		}else if (v.getId() == R.id.rl_personal_history) {
			/* 我的历史 */
			intent = new Intent("com.wasu.vod.OTHERS");
			intent.putExtra("TYPE", "历史");
			startActivity(intent);
		}else if (v.getId() == R.id.rl_personal_smart) {
			/* 智能家居 */
			intent = new Intent(getActivity(), Welcome.class);
			startActivity(intent);
		}else if (v.getId() == R.id.rl_personal_collection) {
			/* 我的收藏 */
			intent = new Intent("com.wasu.vod.OTHERS");
			intent.putExtra("TYPE", "收藏");
			startActivity(intent);
		}else if (v.getId() == R.id.rl_personal_rec) {
			/* 推荐位 */
			if ((WasuInfo) v.getTag() != null) {
				((BaseActivity)this.getActivity()).openDetailsActivity(new CustomContent((WasuInfo) v.getTag()), this.arg0, null);
			}
		}
	}
/*    void addStringParameter(List<KeyValue> params) {
		
		if (params != null) {
			params.clear();
		} else {
			
			return;
		}
		//在线运营厅 开始
		addQueryStringParameter("resourceNo", "11040010100052544C1A0099");
		addQueryStringParameter("type", "23");
		//addQueryStringParameter("indexUrl", "http://www.baidu.com");//PointAdd   PlanBuy
		addQueryStringParameter("epgCode", "010105");
		//在线运营厅 结束

		
	}
    public void addQueryStringParameter(String name, String value) {
        if (!TextUtils.isEmpty(name)) {
            this.params.add(new KeyValue(name, value));
        }
    }*/


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
					if (/*id == R.id.us_bg_5 ||*/ id == R.id.us_bg_3 || id == R.id.us_bg_2 || id == R.id.us_bg_4) {
						if (mCallback != null) {
							mCallback.replaceFragment(3, 4);
						}
						return true;
					}
				}else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
					int id = v.getId();
					if (id == R.id.us_bg_0 || id == R.id.us_bg_1) {
						if (mCallback != null) {
							mCallback.replaceFragment(3, 2);
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
//		if (this.arg0 != null) {
//		    myhandler.sendMessageDelayed(myhandler.obtainMessage(GETDATA),500);
//		    myhandler.sendMessageDelayed(myhandler.obtainMessage(GETDATA_REC),500);
//		}

	};

	MyListener arg0 = null;

	@Override
	public void onConnnected(MyListener arg0) {
		this.arg0 = arg0;
		if (myhandler != null && this.isVisible()) {
			myhandler.sendMessage(myhandler.obtainMessage(GETDATA));
			myhandler.sendMessage(myhandler.obtainMessage(GETDATA_REC));
		}
	}

	private void setImage(ImageView view, String paramUrl) {
		//imageLoader.get(paramUrl, ImageLoader.getImageListener(view, 0, 0));
		ImageLoader.getInstance().displayImage(paramUrl, view);
	}

	/** 线程 */
	static class Handr3 extends Handler {
		WeakReference<Fragment> wfactivity = null;

		public Handr3(Fragment fragment) {
			wfactivity = new WeakReference<Fragment>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			final UserFragment ragment = (UserFragment) wfactivity.get();
			if (ragment != null) {
				if (ragment.isVisible()) {
					if (msg.what == SETDATA) {
						if (msg.obj != null) {
							if (ragment.txt_user_balance_value != null) {
								ragment.txt_user_balance_value.setText((String)msg.obj);
							}
							if (ragment.ll_user_balance != null) {
								ragment.ll_user_balance.setVisibility(View.VISIBLE);
							}
						} else {

						}
					} else if (msg.what == SETDATA_REC) {
						if (msg.obj != null) {
							
							RecommendResponseResult rrr = (RecommendResponseResult) msg.obj;
							if (rrr.getContents() != null) {
								for (int i = 0; i < rrr.getContents().size(); i++) {
									
									if (rrr.getContents().get(i).getInfoList() != null
											&& rrr.getContents().get(i).getInfoList().size() != 0) {
										
										ImageView imageView = (ImageView) ragment.rl_personal_rec.findViewById(R.id.iv_personal_rec);
										String imgUrl = rrr.getContents().get(i).getInfoList().get(0).getImg();
										if (!TextUtils.isEmpty(imgUrl)) {
											ragment.setImage(imageView, imgUrl);
										}
										ragment.rl_personal_rec.setTag(rrr.getContents().get(i).getInfoList().get(0));
//										ragment.setImage((ImageView) ((FrameLayout) (ragment.bgList.get(6).getParent())).getChildAt(1), rrr
//												.getContents().get(i).getInfoList().get(0).getImg());
//										ragment.bgList.get(6).setTag(rrr.getContents().get(i).getInfoList().get(0));
										break;
										
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
		if (msg.what == GETDATA) {
			//账户数据信息
			try {
//				code.add("DVBOTT_HD_30007000");
//				code.add("DVBOTT_HD_30010100");
//				code.add("DVBOTT_HD_10002000");
//				code.add("DVBOTT_HD_10002100");
				JSONObject jo;
				String realYuer = null;
				try {
					jo = new JSONObject(arg0.wasu_tvp_payEntranceQuery(null));
//					Log.d("chenchen", "个人屏 数据1返回：" + jo.toString());
					String yuer = jo.getString("accountAmount"); 
					String dianka = jo.getString("pointAmount");
					realYuer = String.valueOf((Float.valueOf(yuer) / 100 - Float.valueOf(dianka)));
					int index = realYuer.indexOf("."); 
					if (index != -1) {
						if (index + 2 > realYuer.length()-1) {
						   realYuer = realYuer.substring(0, index + 1 + 1);
						} else {
							realYuer = realYuer.substring(0, index + 2 + 1);
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!TextUtils.isEmpty(realYuer)) {
					if (handr3 != null) {
						realYuer = realYuer + "元";
						//将余额信息更新到UI
						handr3.sendMessage(handr3.obtainMessage(SETDATA, realYuer));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (msg.what == GETDATA_REC) {
		//	PlayUtils.getInstance().playstop();
			try {
				List<String> code = new ArrayList<String>();
				code.add("DVBOTT_HD_10004002");
				//code.add("DVBOTT_HD_30008000");

				String string = arg0.wasu_recommsys_recommlist_get(Utils.CreateRecommendContentBundle(code).getString("param"));
//				Log.d("chenchen", "个人屏 数据2返回：" + string.toString());
				if (!TextUtils.isEmpty(string)) {
					if (handr3 != null) {
						RecommendResponseResult reconmmendBean = gson.fromJson(string, RecommendResponseResult.class);
						if (reconmmendBean != null) {
							handr3.sendMessage(handr3.obtainMessage(SETDATA_REC, reconmmendBean));
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (msg.what == 1) {
		//	PlayUtils.getInstance().playstop();
		}
		return true;
	}

	public void Refresh() {
		if (myhandler != null) {
			myhandler.sendMessage(myhandler.obtainMessage(GETDATA));
			myhandler.sendMessage(myhandler.obtainMessage(GETDATA_REC));
		}
	}
}
