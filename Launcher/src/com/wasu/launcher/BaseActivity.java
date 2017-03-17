package com.wasu.launcher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.wasu.bs.pay.TrackPayResultInteface;
import com.wasu.bs.pay.WasuPayParaUtils;
import com.wasu.sdk_ott.PayDialogFragment;
import com.wasu.sdk_ott.UILApplication;
import com.wasu.vod.PlayerActivity;
import com.wasu.vod.TopicActivity;
import com.wasu.vod.VodDetailsActivity;
import com.wasu.vod.aidl.SDKOperationManager.MyListener;
import com.wasu.vod.domain.CustomContent;
import com.wasu.vod.domain.PlaybackResponseResult;
import com.wasu.vod.domain.WasuSegment;
import com.wasu.vod.domain.WasuVideo;
import com.wasu.vod.purchase.PurchaseActivity;
import com.wasu.vod.utils.Constant;
import com.wasu.vod.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public abstract class BaseActivity extends FragmentActivity {

	private static final String TAG = "BaseActivity";
	protected Context context;
	protected SharedPreferences sp;
	protected int mScreenWidth;
	protected int mScreenHeight;
	protected double screenSize;
	
	Handler mMainHandler = new Handler();
	protected static final int REQUEST_BASE_QUICK_ORDER = 1001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 设置横屏
		context = BaseActivity.this;
		sp = getSharedPreferences("wasu", MODE_PRIVATE);
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;
		double diagonalPixels = Math.sqrt(Math.pow(dm.widthPixels, 2) + Math.pow(dm.heightPixels, 2));
		screenSize = diagonalPixels / (160 * dm.density);
	}

	/**
	 * 初始化
	 */
	protected abstract void initView();

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

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "BaseActivity... onStart");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "BaseActivity... onDestroy");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "BaseActivity... onPause");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "BaseActivity... onResume");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i(TAG, "BaseActivity... onStop");

	}

	protected void openActivity(Class<?> pClass) {
		openActivity(pClass, null);
	}

	protected void openActivity(Class<?> pClass, Bundle pBundle) {
		Intent intent = new Intent(this, pClass);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	protected void openActivity(String pAction) {
		openActivity(pAction, null);
	}

	protected void openActivity(String pAction, Bundle pBundle) {
		Intent intent = new Intent(pAction);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	/**
	 * 应用崩溃toast
	 */
	protected void handleFatalError() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(BaseActivity.this, "发生了一点意外，程序终止！", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
	}

	/**
	 * 内存空间不足
	 */
	protected void handleOutmemoryError() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(BaseActivity.this, "内存空间不足！", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
	}

	/**
	 * Activity关闭和启动动画
	 */
	public void finish() {
		super.finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
	
	public void openDetailsActivity(final CustomContent customContent, final MyListener ml, final String folderCode){
		if(customContent == null)
			return;
		final Intent intent = new Intent();
		String contentId = customContent.getContentId();
		final String contentType = customContent.getContentType();
		Log.d("xiexiegang","openDetailsActivity contentType:"+contentType+",contentId:"+customContent.getContentId());
		if(contentId != null && contentId.startsWith("http:") || contentType != null && contentType.equalsIgnoreCase("102")){
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
					
				} else {
				    intent.setAction("com.wasu.intent.folder");
				    intent.putExtra("foldCode", folderAliasName);
				    intent.putExtra("getDataStyle", getDataStyle.split(",")[1]);
				    intent.putExtra("style", templateType);
				    //全景VR栏目特殊处理
				    if ("p_23_16_3".equals(folderAliasName)) {
				    	intent.putExtra("folderName", "全部");
				    	intent.putExtra("TYPE", "全景VR");
				    } else {
				        intent.putExtra("folderName", "");
				        intent.putExtra("TYPE", "");
				    }
				}
			    
			} else {
				Log.e(TAG, "liqiuxuTest folder: getDataStyle==null");
				intent.setClass(context, VodDetailsActivity.class);
			    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			    
			    intent.putExtra("param", folderAliasName);
			    intent.putExtra("extra", customContent);
			    intent.putExtra("folderNameCurrent", folderAliasName);
				//return;
			}

		
		}else{
		if(contentType != null){
			if(Utils.isTypeMovie(contentType)){
				intent.setClass(context, VodDetailsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("TYPE", "MOVIE");
				intent.putExtra("param", customContent.getContentId());
			}else if(Utils.isTypeSeries(contentType)){
				intent.setClass(context, VodDetailsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("TYPE", "SERIES");
				intent.putExtra("param", customContent.getContentId());
			}else{
				intent.setClass(context, PlayerActivity.class);
				intent.putExtra("TYPE", Constant.PLAYER_VOD);
				intent.putExtra("contentId", customContent.getContentId());
				intent.putExtra("contentType", customContent.getContentType());
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
									// TODO Auto-generated catch block
									e.printStackTrace();
									Log.e(TAG,"wasu_interactive_auth_playUrlQuery failed: "
													+ e.getLocalizedMessage());
								}
								try {

									final JSONObject result = temp == null ? null
											: new JSONObject(temp);


									mMainHandler.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub

											if (result != null) {

												try {

													String AuthResult = result.getString("result");

													if (AuthResult.equalsIgnoreCase("1")) {

														Toast.makeText(BaseActivity.this, "由于服务端返回错误，无法鉴权！", Toast.LENGTH_LONG).show();
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
																				Toast.makeText(BaseActivity.this, "充值成功", Toast.LENGTH_LONG).show();
																			}
																			// 充值成功
																			fragment.unRegisterPayListener();

																		}

																	});
																	fragment.show(BaseActivity.this.getFragmentManager(), "wasupay");
																} catch (Exception e) {
																	
																}

															} else if (authResultCode.equalsIgnoreCase("9999")) {// 浏览用户跳注册
																
																Intent intent = new Intent();
																intent.setClassName("com.wasu.launcher", "com.wasu.launcher.activity.UserActivity");
																intent.putExtra("form", "agree");// 需加参数跳到对应页
																BaseActivity.this.startActivity(intent);
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
																			
																			mMainHandler.post(new Runnable() {

																				@Override
																				public void run() {
																					// TODO Auto-generated method stub
																					//跳到快捷订购界面
																					Intent intent = new Intent(BaseActivity.this, PurchaseActivity.class);
																					intent.putExtra("pValue", ppv);
																					BaseActivity.this.startActivityForResult(intent, REQUEST_BASE_QUICK_ORDER);
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
																overridePendingTransition(android.R.anim.fade_in,
																		android.R.anim.fade_out);
																return;

															} else if (authResultCode.equalsIgnoreCase("1")){// 弹出错误信息
																
																Toast.makeText(BaseActivity.this, "鉴权失败，无法播放！", Toast.LENGTH_LONG).show();
																
															} else {//// 弹出错误信息
																Toast.makeText(BaseActivity.this, "鉴权错误码："+authResultCode+"", Toast.LENGTH_LONG).show();
															}

														} else {

															// may network exception
															Toast.makeText(BaseActivity.this, "由于服务端问题无法鉴权，无法播放！", Toast.LENGTH_LONG).show();
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
		}else{
			intent.setClass(context, VodDetailsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("param", customContent.getContentId());
			intent.putExtra("extra", customContent);
		}
	}
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
		
	}

}
