package com.wasu.launcher.Fragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.wasu.json.JSONObject;
import com.wasu.launcher.MainActivity;
import com.wasu.launcher.NewbieGuideActivity;
import com.wasu.launcher.R;
import com.wasu.launcher.Fragment.LoginFragment.TimeCount;
import com.wasu.launcher.adapter.MyAdapter;

import com.wasu.launcher.entity.ExampleBean;
import com.wasu.launcher.entity.MyListItem;

import com.wasu.launcher.interfaces.MyFragmentListener;
import com.wasu.launcher.utils.ScaleAnimEffect;
import com.wasu.launcher.view.NetworkDialog;
import com.wasu.utils.WasuNetworkOpeManager;
import com.wasu.vod.aidl.SDKOperationManager;
import com.wasu.vod.aidl.SDKOperationManager.BindService;
import com.wasu.vod.aidl.SDKOperationManager.MyListener;
import com.wasu.vod.domain.CustomContent;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class WasuRegisterFragment extends BaseFragment implements OnClickListener, OnFocusChangeListener {
	private View view;
	private MyFragmentListener mCallback;// 回调接口
	// 标题、提示
	private TextView rb_prompt1;
	// 验证码按钮、注册按钮
	private Button post_btn;
	private Button rb_prompt_skip;
	private Spinner provinceSpinner = null; // 省级（省、直辖市）
	private Spinner citySpinner = null; // 地级市
	private Spinner countySpinner = null;// 县区级
	// 区域实体类
	ExampleBean body;
	private String province = null;//
	private String city = null;
	private String county = null;
	private String bossAreaCode = null;
	MyListener ml;
	String form;// 进入注册的方式
	SharedPreferences spd;

	boolean connectfalg = false;// 判断是否连接
	NetworkDialog networkdialog;// 警告框
	JSONObject obj;
	boolean phonef = false;// 判断焦点是否在手机输入框上
	private boolean phone = false;// 判断手机输入格式是否正确
	private boolean verification = false;// 判断验证码输入格式是否正确

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		if (container == null) {
			return null;
		}
		if (null == view) {

			view = inflater.inflate(R.layout.layout_wasu_register, container, false);

			initview();
		}

		return view;
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
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d(this.getTag(), "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		initview();
	}

	private void initview() {

		loadViewLayout();
		findViewById();
		setListener();

	}

	@Override
	protected void loadViewLayout() {
		// TODO Auto-generated method stub
		form = getActivity().getIntent().getStringExtra("form");
		spd = getActivity().getSharedPreferences("mydevice", Context.MODE_APPEND);

	}

	private EditText phone_edit;

	@Override
	protected void findViewById() {

		phone_edit = (EditText) view.findViewById(R.id.phone_edit);
		rb_prompt1 = (TextView) view.findViewById(R.id.rb_prompt1);
		rb_prompt1.setVisibility(View.INVISIBLE);
		post_btn = (Button) view.findViewById(R.id.post_btn);
		rb_prompt_skip = (Button) view.findViewById(R.id.rb_prompt_skip);

		provinceSpinner = (Spinner) view.findViewById(R.id.spin_province);
		citySpinner = (Spinner) view.findViewById(R.id.spin_city);
		countySpinner = (Spinner) view.findViewById(R.id.spin_county);

	}

	@Override
	public void onResume() {

		super.onResume();

	}

	@Override
	protected void setListener() {

		phone_edit.setOnFocusChangeListener(this);
		phone_edit.addTextChangedListener(watcher);

		post_btn.setOnClickListener(this);
		rb_prompt_skip.setOnClickListener(this);

	}

	private TextWatcher watcher = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			// Log.d("yingjh", "onTextChanged s="+s+" start="+start+"
			// before="+before+" count="+count);
			if (s.length() == 11) {
				final Pattern p = Pattern.compile("[0-9]*");
				Matcher m = p.matcher(s.toString());
				if (m.matches()) {
					rb_prompt1.setTextColor(Color.parseColor("#6C6C6C"));
					rb_prompt1.setVisibility(View.INVISIBLE);
					return;
				}
			}
			rb_prompt1.setTextColor(Color.parseColor("#e8a02f"));
			rb_prompt1.setVisibility(View.VISIBLE);

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

	};

	private class MyOnKeyListener implements OnKeyListener {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {

			switch (event.getAction()) {
			case KeyEvent.ACTION_UP: // 键盘松开
				break;
			case KeyEvent.ACTION_DOWN: // 键盘按下

				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				}
				break;

			}
			return false;
		}
	}

	@Override
	public void onDestroyView() {

		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.post_btn) {

			if (connectfalg) {
				if (phone_edit.getText().toString() != null && phone) {
					wasu_register();
				}

			} else {
				networkdialog = new NetworkDialog(getActivity(), "请重新注册", 2);
			}
		} else if (id == R.id.rb_prompt_skip) {
			Log.d("yingjh", "skip button");
			getActivity().finish();
		}

	}

	private void wasu_register() {
		Log.d("yingjh", "wasu_register");
		String phonevalue = phone_edit.getText().toString();
		String vCode = "5312";// temp use
		String address = bossAreaCode;// "4001";
		String caid = null;
		Log.d("yingjh", "phone=" + phonevalue);
		Log.d("yingjh", "address=" + address);
		try {
			// String result = ml.wasu_bsa_do_register("13378667334",
			// "5312","4001", null);
			String result = ml.wasu_bsa_do_register(phonevalue, vCode, address, caid);
			Log.d("yingjh", "result" + result);
			if (!TextUtils.isEmpty(result)) {
				obj = new JSONObject(result);
				if (obj != null) {
					JSONObject body = obj.getJSONObject("body");
					if (body != null) {
						int resultcode = body.getInt("result");
						String desc = body.getString("desc");
						Log.d("yingjh", "resultcode=" + resultcode + " desc=" + desc);
						if (resultcode == 0) {
							networkdialog = new NetworkDialog(getActivity(), "注册成功", 2);
							Intent intent = new Intent(getActivity(), NewbieGuideActivity.class);
							intent.putExtra("isHaveActivie", false);
							getActivity().startActivityForResult(intent, 100, null);
							getActivity().finish();
							return;
						} else {
							networkdialog = new NetworkDialog(getActivity(), desc, 1);
							return;
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		networkdialog = new NetworkDialog(getActivity(), "系统异常，请稍后再试，或联系客服", 1);

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		final Pattern p = Pattern.compile("[0-9]*");
		if (hasFocus) {
			phonef = true;
			// ((CompoundButton) v).setChecked(true);
		} else {
			if (phonef) {
				Matcher m = null;
				if (v == phone_edit) {
					m = p.matcher(phone_edit.getText().toString());

					Log.d("yingjh", "m:" + m + ",phone_edit.size:" + phone_edit.getText().toString().length()
							+ ",m.matches():" + m.matches());
					if (!m.matches() || phone_edit.getText().toString().length() < 11) {
						Log.d("yingjh", "phone_edit.size:" + phone_edit.getText().toString().length());
						// rb_prompt1.setTextColor(Color.parseColor("#e8a02f"));
						// rb_prompt1.setVisibility(View.VISIBLE);

					} else {
						// rb_prompt1.setTextColor(Color.parseColor("#6C6C6C"));
						// rb_prompt1.setVisibility(View.INVISIBLE);
						phone = true;
					}
				}

			}

		}

	}

	@Override
	public void onConnnected(MyListener arg0) {
		// TODO Auto-generated method stub
		Log.e("yingjh", "onConnnected ");
		this.ml = arg0;
		connectfalg = true;
		try {
			// Log.e("gupu", "query_area_resul8888888888888t");
			// String result_are = ml.wasu_bsa_query_area("0", "", "", "0x0000",
			// false);

			// Log.e("gupu", "query_area_result:" + result_are);
			// setSpin(result_are);
			RegQueryAsyncTask regqueryaynctask = new RegQueryAsyncTask();
			regqueryaynctask.execute("prama_nouse_now");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onDisconnected(MyListener arg0) {
		Log.e("gupu", "连接失败re");
		// TODO Auto-generated method stub
		this.ml = arg0;
		connectfalg = false;
	}

	private void setSpin(String resutl) {

		try {

			Gson gson = new Gson();

			obj = new JSONObject(resutl);

			body = gson.fromJson(resutl, ExampleBean.class);
			Log.e("yingjh", "body:" + body.toString());

			// initSpinner1();
			initSpinner3("3301");// only display 杭州
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void initSpinner1() {

		List<MyListItem> list = new ArrayList<MyListItem>();
		// Log.e("gupu","provinceList:"+provinceList.get(9).);
		for (int i = 0; i < body.getBody().getProviceList().size(); i++) {

			// Log.e("gupu","provinceList.get(i):"+body.getBody().getProviceList().get(i).toString());
			String code = body.getBody().getProviceList().get(i).getAreaCode();
			String name = body.getBody().getProviceList().get(i).getAreaName();
			// ProvinceBean
			// pr=(ProvinceBean)body.getBody().getProviceList().get(i);
			MyListItem myListItem = new MyListItem();
			myListItem.setName(name);
			myListItem.setPcode(code);
			list.add(myListItem);
		}

		MyAdapter myAdapter = new MyAdapter(getActivity(), list);
		provinceSpinner.setAdapter(myAdapter);
		provinceSpinner.setOnItemSelectedListener(new SpinnerOnSelectedListener1());
	}

	public void initSpinner2(String pcode) {

		List<MyListItem> list = new ArrayList<MyListItem>();
		for (int i = 0; i < body.getBody().getCityList().size(); i++) {
			// CityBean cb=(CityBean) cityList.get(i);
			String ParentId = body.getBody().getCityList().get(i).getAreaParentId();
			if (ParentId.equals(pcode)) {
				String code = body.getBody().getCityList().get(i).getAreaCode();
				String name = body.getBody().getCityList().get(i).getAreaName();
				MyListItem myListItem = new MyListItem();
				myListItem.setName(name);
				myListItem.setPcode(code);
				list.add(myListItem);
			}

		}

		MyAdapter myAdapter = new MyAdapter(getActivity(), list);
		citySpinner.setAdapter(myAdapter);
		citySpinner.setOnItemSelectedListener(new SpinnerOnSelectedListener2());
	}

	public void initSpinner3(String pcode) {

		List<MyListItem> list = new ArrayList<MyListItem>();
		for (int i = 0; i < body.getBody().getCountyList().size(); i++) {
			String ParentId = body.getBody().getCountyList().get(i).getAreaParentId();
			if (ParentId.equals(pcode)) {
				String code = body.getBody().getCountyList().get(i).getAreaCode();
				String name = body.getBody().getCountyList().get(i).getAreaName();
				String bosscode = body.getBody().getCountyList().get(i).getBossAreaCode();
				String orderBy = body.getBody().getCountyList().get(i).getOrderBy();
				MyListItem myListItem = new MyListItem();
				myListItem.setName(name);
				myListItem.setPcode(code);
				myListItem.setBosscode(bosscode);
				myListItem.setOrderBy(orderBy);
				list.add(myListItem);
			}

		}
		Collections.sort(list, new Comparator<MyListItem>() {
			@Override
			public int compare(MyListItem lhs, MyListItem rhs) {
				if (null != lhs && null != rhs) {
					String lorderby = lhs.getOrderBy();
					String rorderby = rhs.getOrderBy();
					int lint = Integer.parseInt(lorderby);
					int rint = Integer.parseInt(rorderby);
					// 从小到大排
					return (lint - rint);
				}
				return 0;
			}

		});

		MyAdapter myAdapter = new MyAdapter(getActivity(), list);
		countySpinner.setAdapter(myAdapter);
		countySpinner.setOnItemSelectedListener(new SpinnerOnSelectedListener3());
	}

	class SpinnerOnSelectedListener1 implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
			province = ((MyListItem) adapterView.getItemAtPosition(position)).getName();
			String pcode = ((MyListItem) adapterView.getItemAtPosition(position)).getPcode();

			initSpinner2(pcode);
			initSpinner3(pcode);
		}

		public void onNothingSelected(AdapterView<?> adapterView) {
			// TODO Auto-generated method stub
		}
	}

	class SpinnerOnSelectedListener2 implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
			city = ((MyListItem) adapterView.getItemAtPosition(position)).getName();
			String pcode = ((MyListItem) adapterView.getItemAtPosition(position)).getPcode();

			initSpinner3(pcode);
		}

		public void onNothingSelected(AdapterView<?> adapterView) {
			// TODO Auto-generated method stub
		}
	}

	class SpinnerOnSelectedListener3 implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
			county = ((MyListItem) adapterView.getItemAtPosition(position)).getName();
			bossAreaCode = ((MyListItem) adapterView.getItemAtPosition(position)).getBosscode();
			Log.d("yingjh", "bossAreaCode=" + bossAreaCode);
			// Toast.makeText(getActivity(), province + " " + city + " " +
			// county,
			// Toast.LENGTH_LONG).show();
		}

		public void onNothingSelected(AdapterView<?> adapterView) {
			// TODO Auto-generated method stub
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.d(this.getTag(), "onDestroy");
		super.onDestroy();
	}

	private class RegQueryAsyncTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			if (ml == null) {
				return null;
			}
			String result = null;
			try {
				result = ml.wasu_bsa_query_area("0", "", "", "0x0000", false);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("yingjh", "result=" + result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				setSpin(result);
			}
		}

	}

}
