package com.wasu.launcher.Fragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.wasu.json.JSONObject;
import com.wasu.launcher.MainActivity;
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

public class RegisteredFragment extends BaseFragment implements OnClickListener, OnFocusChangeListener{
	private View view;
	private MyFragmentListener mCallback;// 回调接口
	// 标题、提示
	private TextView rb_registered, rb_prompt1;
	// 验证码按钮、注册按钮
	private Button code_btn, post_btn;
	private Spinner provinceSpinner = null; // 省级（省、直辖市）
	private Spinner citySpinner = null; // 地级市
	private Spinner countySpinner = null;// 县区级
	// 区域实体类
	ExampleBean body;
	private String province = null;//
	private String city = null;
	private String county = null;
	MyListener ml;
	String form;// 进入注册的方式
	SharedPreferences spd;
	private TimeCount time;// 验证码计时器
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

			view = inflater.inflate(R.layout.layout_registered, container, false);

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

		time = new TimeCount(60000, 1000);

	}

	private EditText phone_edit, verification_edit;

	@Override
	protected void findViewById() {
		rb_registered = (TextView) view.findViewById(R.id.rb_registered);
		rb_prompt1 = (TextView) view.findViewById(R.id.rb_prompt1);
		rb_registered.requestFocus();
		rb_registered.setSelected(true);
		phone_edit = (EditText) view.findViewById(R.id.phone_edit);
		verification_edit = (EditText) view.findViewById(R.id.verification_edit);
		code_btn = (Button) view.findViewById(R.id.code_btn);
		post_btn = (Button) view.findViewById(R.id.post_btn);
		provinceSpinner = (Spinner) view.findViewById(R.id.spin_province);
		citySpinner = (Spinner) view.findViewById(R.id.spin_city);
		countySpinner = (Spinner) view.findViewById(R.id.spin_county);

	}

	@Override
	public void onResume() {

		super.onResume();

		rb_registered.requestFocus();
		rb_registered.setSelected(true);
	}

	@Override
	protected void setListener() {
		rb_registered.setOnKeyListener(new MyOnKeyListener());
		rb_registered.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					v.setSelected(true);
					// ((CompoundButton) v).setChecked(true);
				} else {
					v.setSelected(false);
				}

			}
		});
		phone_edit.setOnFocusChangeListener(this);
		verification_edit.setOnFocusChangeListener(this);
		code_btn.setOnClickListener(this);
		post_btn.setOnClickListener(this);

	}

	private class MyOnKeyListener implements OnKeyListener {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {

			switch (event.getAction()) {
			case KeyEvent.ACTION_UP: // 键盘松开
				break;
			case KeyEvent.ACTION_DOWN: // 键盘按下

				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {

					if (v == rb_registered) {
						if (mCallback != null) {

							mCallback.replaceFragment(0, 1);

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
	public void onDestroyView() {
		time.cancel();
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.code_btn: {

			if (phone_edit.getText().toString().length() == 0) {

				networkdialog = new NetworkDialog(getActivity(), "手机号码不能为空！", 2);
				return;
			}
			if (connectfalg) {
				try {
					String tvId = spd.getString("tvId", null);
					String deviceId = spd.getString("deviceId", null);
					String result = ml.wasu_upm_SMS_verifycode_get(phone_edit.getText().toString(), tvId, deviceId, 0);
					// Log.e("gupu", "verifycode：" + result);
					obj = new JSONObject(result);
					// Log.e("gupu", "code_btn:" + obj.toString());
					int resendInterval = obj.getInt("resendInterval");
					// Log.e("gupu", "resendInterval：" + resendInterval);
					int code = obj.getInt("code");
					// Log.e("gupu", "verifycode_code：" + code);
					if (code == 0) {
						Editor ed = spd.edit();
						ed.putString("Phone", phone_edit.getText().toString());
						ed.commit();
						time.start();
					} else {
						networkdialog = new NetworkDialog(getActivity(), "请再次核对手机号码", 2);
					}

				} catch (Exception e) {
					networkdialog = new NetworkDialog(getActivity(), "请重新获取", 2);
				}
			} else {

				networkdialog = new NetworkDialog(getActivity(), "请重新获取", 2);
				time.start();

			}
			break;
		}
		case R.id.post_btn: {
			if (verification_edit.getText().toString().length() == 0) {

				networkdialog = new NetworkDialog(getActivity(), "验证码不能为空！", 2);
				return;
			}
			if (connectfalg) {
				// if(phone_edit.getText().toString()!=null&&verification_edit.getText().toString()!=null&&phone&&verification){
				//
				// }
				register();
			} else {
				networkdialog = new NetworkDialog(getActivity(), "请重新注册", 2);
			}

			break;
		}
		}

	}

	private void register() {

		try {
			String tvId = spd.getString("tvId", null);
			String deviceId = spd.getString("deviceId", null);
			String caid = spd.getString("CAId", null);
			String code = verification_edit.getText().toString();
			// Log.e("gupu", "tvId：" + tvId + ",deviceId:" + deviceId + ",caid:"
			// + caid);
			String result = ml.wasu_upm_other_user_register(deviceId, caid, 9, "999999", "jjj", "2000", tvId);
			Log.e("gupu", "register：" + result);
			obj = new JSONObject(result);
			int code1 = obj.getInt("code");
			Log.e("gupu", "codereg：" + code1);

			if (code1 == 0) {
				String userKey = obj.getString("userKey");
				Log.e("gupu", "userKey：" + userKey);
				String token = obj.getString("token");
				Log.e("gupu", "token：" + token);
				String account = obj.getString("account");
				Log.e("gupu", "account：" + account);
				int accountType = obj.getInt("accountType");
				Log.e("gupu", "accountType：" + accountType);
				Editor ed = spd.edit();
				ed.putString("userKey", userKey);
				ed.putString("token", token);
				ed.putString("account", account);
				ed.putInt("accountType", accountType);
				ed.commit();
				// String province= provinceSpinner.getSelectedItem().;

				// String city=citySpinner.getSelectedItem().toString();

				// String county=countySpinner.getSelectedItem().toString();
				Log.e("gupu", "provinceSpinner：" + province + ",citySpinner:" + city + ",countySpinner:" + county + ",phone:"
						+ phone_edit.getText().toString());
				String result_address = ml.wasu_upm_userinfo_post(userKey, tvId, deviceId, token, "asd", 1, 25, province + city + county,
						null, phone_edit.getText().toString());

				Log.e("gupu", "result_address：" + result_address);
				String result1 = ml.wasu_upm_userinfo_get(userKey, tvId, deviceId, token);
				Log.e("gupu", "userinfo_getresult:" + result1);

				obj = new JSONObject(result_address);
				int address_code = obj.getInt("code");
				Log.e("gupu", "address_code：" + address_code);
				if (address_code == 0) {
					networkdialog = new NetworkDialog(getActivity(), "注册成功", 2);
					if (form.equals("agree")) {
						getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
						getActivity().finish();
					} else if (form.equals("live")) {

					} else if (form.equals("vod")) {

					} else if (form.equals("user")) {
						getActivity().finish();
					}
				} else {
					networkdialog = new NetworkDialog(getActivity(), "系统异常，请稍后再试，或联系客服96371_1", 2);
				}
			} else if (code1 == 104) {
				networkdialog = new NetworkDialog(getActivity(), "该用户已经注册", 2);
			}

		} catch (Exception e) {
			networkdialog = new NetworkDialog(getActivity(), "系统异常，请稍后再试，或联系客服96371_3", 2);
		}

	}

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {

			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {// 计时完毕
			code_btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sm_22));
			code_btn.setText("获取验证码");
			code_btn.setFocusable(true);
			code_btn.setBackgroundResource(R.drawable.bg_button);
			code_btn.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程
			code_btn.setClickable(false);// 防止重复点击
			code_btn.setFocusable(false);
			code_btn.setBackgroundResource(R.drawable.bg_button_normal);
			code_btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sm_16));
			code_btn.setText(millisUntilFinished / 1000 + "秒后重新发送");

		}
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

					Log.d(this.getTag(),
							"m:" + m + ",phone_edit.size:" + phone_edit.getText().toString().length() + ",m.matches():" + m.matches());
					if (!m.matches() || phone_edit.getText().toString().length() < 11) {
						Log.d(this.getTag(), "phone_edit.size:" + phone_edit.getText().toString().length());
						rb_prompt1.setTextColor(getActivity().getResources().getColor(R.color.lau_red));

					} else {
						rb_prompt1.setTextColor(Color.parseColor("#6C6C6C"));
						phone = true;
					}
				}

				Matcher m1 = null;
				if (v == verification_edit) {
					m1 = p.matcher(verification_edit.getText().toString());
					Log.d(this.getTag(), "m:" + m1 + ",verification_edit.size:" + verification_edit.getText().toString().length()
							+ ",m1.matches():" + m1.matches());

					if (m1.matches() && verification_edit.getText().toString().length() == 4) {
						verification = true;
					}
				}

			}

		}

	}

	@Override
	public void onConnnected(MyListener arg0) {
		// TODO Auto-generated method stub
		Log.e("gupu", "连接成功re");
		this.ml = arg0;
		connectfalg = true;
		try {
			//Log.e("gupu", "query_area_resul8888888888888t");
			//String result_are = ml.wasu_bsa_query_area("0", "", "", "0x0000", false);

			//Log.e("gupu", "query_area_result:" + result_are);
			//setSpin(result_are);
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
			Log.e("gupu", "body:" + body.toString());

			initSpinner1();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void initSpinner1() {

		List<MyListItem> list = new ArrayList<MyListItem>();
		// Log.e("gupu","provinceList:"+provinceList.get(9).);
		for (int i = 0; i < body.getBody().getProviceList().size(); i++) {

			 //Log.e("gupu","provinceList.get(i):"+body.getBody().getProviceList().get(i).toString());
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
				MyListItem myListItem = new MyListItem();
				myListItem.setName(name);
				myListItem.setPcode(code);
				list.add(myListItem);
			}

		}

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
	private class RegQueryAsyncTask extends  AsyncTask<String,Integer,String>{

		@Override
		protected String doInBackground(String... params) {
			if(ml==null){
				return null;
			}
			String result = null;
			try {
				result=ml.wasu_bsa_query_area("0", "", "", "0x0000", false);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {		
			if(result!=null){
				setSpin(result);
			}			
		}
		
	}



}
