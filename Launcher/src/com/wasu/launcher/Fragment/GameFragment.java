package com.wasu.launcher.Fragment;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.wasu.android.volley.toolbox.ImageLoader;
import com.wasu.json.JSONArray;
import com.wasu.launcher.R;
import com.wasu.launcher.domain.GameItem;
import com.wasu.launcher.domain.GameResponseBody;
import com.wasu.launcher.domain.RequestAsyncTaskLoader;
import com.wasu.launcher.domain.RequestAsyncTaskLoader.LoadInBackgroundListener;
import com.wasu.launcher.interfaces.MyFragmentListener;
import com.wasu.launcher.view.SmoothHorizontalScrollView;
import com.wasu.launcher.view.SmoothHorizontalScrollView.ScrollViewListener;
import com.wasu.vod.aidl.SDKOperationManager;
import com.wasu.vod.aidl.SDKOperationManager.BindService;
import com.wasu.vod.aidl.SDKOperationManager.MyListener;
import com.wasu.vod.application.MyVolley;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 游戏屏
 * @author chenchen
 * @date 2016年12月22日
 */
public class GameFragment extends BaseFragment implements android.app.LoaderManager.LoaderCallbacks<Object>,
												OnKeyListener ,BindService{
	
	private MyFragmentListener mCallback;
	private SDKOperationManager.MyListener sdkFunction;
	private SDKOperationManager sdkManager;
	private com.wasu.android.volley.toolbox.ImageLoader imageLoader;
	private Gson gson;
	
	//存放UI
	private List<RelativeLayout> mItemList = new ArrayList<RelativeLayout>();
	//存放游戏信息
	private List<GameItem> mGameList = new ArrayList<GameItem>();
	
	static class LoaderId{
		public static final int GAME_INDEX = 0x0001;
	}
	
	public GameFragment(ScrollViewListener listener) {
		mScrollViewListener = listener;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (container == null) {
			return null;
		}
		if (null == view) {
			view = inflater.inflate(R.layout.fragment_game_new, container, false);
			findViewById();
			setListener();
			gson = new Gson();
		}
		
		if(imageLoader == null){
			imageLoader = MyVolley.getImageLoader();
		}
		return view;
	}
	@Override
	public void onConnnected(MyListener arg0) {
		// TODO Auto-generated method stub
		sdkFunction = arg0;
		getActivity().getLoaderManager().restartLoader(LoaderId.GAME_INDEX, null, this);
	}
	@Override
	protected void loadViewLayout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() != KeyEvent.ACTION_DOWN){
			return true;
		}
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			break;

		default:
			break;
		}
		
		return false;
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// 这是为了保证Activity容器实现了用以回调的接口。如果没有，它会抛出一个异常。
		try {
			mCallback = (MyFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement MyFragmentListener");
		}
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		sdkManager = new SDKOperationManager(getActivity());
		sdkManager.registerSDKOperationListener(this);
//		setMessage(scrollViewProgress, 1); //滚动到顶部
		Log.d("winton", "start");
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

	@Override
	public android.content.Loader<Object> onCreateLoader(final int id, Bundle args) {
		// TODO Auto-generated method stub
		if(sdkFunction == null){
			return null;
		}
		RequestAsyncTaskLoader request = new RequestAsyncTaskLoader(getActivity());
		request.setLoadInBackgroundListener(new LoadInBackgroundListener() {
			
			@Override
			public Object loadInBackgroundListener() {
				// TODO Auto-generated method stub
				switch (id) {
					case LoaderId.GAME_INDEX:
						String data = null;
						try{
							Log.d("winton", "开始加载游戏人物");
							data = sdkFunction.wasu_gameScreen_data_query();
							return data;
						}catch(RemoteException e){
							e.printStackTrace();
							return null;
						}
					default:
						break;
				}
				return null;
			}
		});
		return request;
	}

	@Override
	public void onLoadFinished(android.content.Loader<Object> loader, Object data) {
		// TODO Auto-generated method stub
		if(loader == null){
			return;
		}
		switch (loader.getId()) {
		case LoaderId.GAME_INDEX:
			String strData = (String)data;
			Log.d("winton", "data:"+strData);
			try{
				GameResponseBody responseBody = gson.fromJson(strData, GameResponseBody.class);
				parseGameDescription(responseBody.getDescription());
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
			break;

		default:
			break;
		}
	}

	@Override
	public void onLoaderReset(android.content.Loader<Object> loader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mScrollView = (SmoothHorizontalScrollView) view.findViewById(R.id.scroll_view_game);
		
		//游戏屏11块内容
		for (int i = 0; i < 11; i++) {
			int id = getResources().getIdentifier("rl_game_" + i, "id", context.getPackageName());
			RelativeLayout item = (RelativeLayout) view.findViewById(id);
			mItemList.add(item);
		}
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		mScrollView.setScrollViewListener(mScrollViewListener);
		
		for (int i = 0; i < mItemList.size(); i++) {
			RelativeLayout item = mItemList.get(i);
			item.setOnFocusChangeListener(mFocusChangeListener);
			item.setOnClickListener(mClickListener);
		}
	}
	
	/**
	 * 解析游戏描述
	* @Title: parseGameDescription 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param data    设定文件 
	* @return void    返回类型 
	* @throws
	 */
	private void parseGameDescription(String data){
		try{
			JSONArray jsonArray = new JSONArray(data);
			for(int i=0;i<jsonArray.length();i++){
				GameItem item = new GameItem();
				item.setID(jsonArray.getJSONObject(i).getInt("ID"));
				item.setImage(jsonArray.getJSONObject(i).getString("Image"));
				item.setName(jsonArray.getJSONObject(i).getString("Name"));
				item.setArgument(jsonArray.getJSONObject(i).getString("Argument"));
				item.setDownloadUrl(jsonArray.getJSONObject(i).getString("DownloadUrl"));
				item.setPackageName(jsonArray.getJSONObject(i).getString("PackageName"));
				item.setStartActivity(jsonArray.getJSONObject(i).getString("StartActivity"));
				item.setType(jsonArray.getJSONObject(i).getInt("Type"));
				mGameList.add(item);
//				Log.d("chenchen", "GameItem ** id:" + item.getID() + ", Name:" + item.getName() + 
//						", Image:" + item.getImage() + ", Type:" + item.getType() + ", PackageName:" + 
//						item.getPackageName() + ", StartActivity:" + item.getStartActivity() + ", Argument:" + 
//						item.getArgument() + ", DownloadUrl:" + item.getDownloadUrl());
				bindDataToUI(item);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//将数据和UI绑定，UI资源的ID和GameItem的ID需一一对应
	private void bindDataToUI(GameItem item) {
		int id = item.getID();
		if (id > mItemList.size()) {
			return;
		}
		RelativeLayout relativeLayout = mItemList.get(id - 1);
		int imgId = getResources().getIdentifier("iv_game_" + (id - 1), "id", context.getPackageName());
		ImageView imageView = (ImageView) relativeLayout.findViewById(imgId);
		imageLoader.get(item.getImage(), ImageLoader.getImageListener(imageView, 0, 0));
	}
	
//	private void initImage(GameItem item){
//		int ID = item.getID();
//		ImageView view = itemContents.get(ID-1);
//		ImageView bg = itemBgs.get(ID-1);
//		bg.setTag(item);
//		Log.d("winton", "加载图片："+ID+"view:"+view.getId());
//		imageLoader.get(item.getImage(), ImageLoader.getImageListener(view, 0, 0));
//	}
	
	private void startGame(String packageName){
		try {
			Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(packageName);
			startActivity(intent);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Toast.makeText(getActivity(), "游戏未安装", Toast.LENGTH_LONG).show();
		}
	}
	
	private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int index = mItemList.indexOf(v);
			if (index < 0 || index > mGameList.size() - 1) {
				return;
			}
			GameItem item = mGameList.get(index);
			if(item != null){
				startGame(item.getPackageName());
			}
		}
	};
}
