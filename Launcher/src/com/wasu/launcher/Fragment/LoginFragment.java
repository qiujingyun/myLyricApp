package com.wasu.launcher.Fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wasu.json.JSONObject;
import com.wasu.launcher.MainActivity;
import com.wasu.launcher.R;
import com.wasu.launcher.interfaces.MyFragmentListener;
import com.wasu.launcher.utils.Utils;
import com.wasu.launcher.view.NetworkDialog;
import com.wasu.vod.aidl.SDKOperationManager.MyListener;

public class LoginFragment extends BaseFragment implements OnFocusChangeListener, OnClickListener {
	private View view;
	private MyFragmentListener mCallback;
	private TextView rb_login, rb_prompt1;
	private EditText phone_edit, verification_edit;
	private Button code_btn, post_btn;
	MyListener ml;
	String tvId;
	String deviceId;
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
			view = inflater.inflate(R.layout.layout_login, container, false);

			Log.e("gupu", "formlogine3333：");
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

	private void initview() {
		// animEffect = new ScaleAnimEffect();

		loadViewLayout();
		findViewById();
		setListener();
		Log.e("gupu", "formlogin22222：" + form);
	}

	@Override
	protected void loadViewLayout() {
		// TODO Auto-generated method stub
		spd = getActivity().getSharedPreferences("mydevice", Context.MODE_APPEND);
		tvId = spd.getString("tvId", null);
		deviceId = spd.getString("deviceId", null);
		time = new TimeCount(60000, 1000);
		form = getActivity().getIntent().getStringExtra("form");
		Log.e("gupu", "formlogin11111：" + form);
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		rb_login = (TextView) view.findViewById(R.id.rb_login);
		rb_prompt1 = (TextView) view.findViewById(R.id.rb_prompt1);
		rb_login.setFocusable(true);
		rb_login.requestFocus();
		rb_login.setSelected(true);
		phone_edit = (EditText) view.findViewById(R.id.phone_edit);
		verification_edit = (EditText) view.findViewById(R.id.verification_edit);
		code_btn = (Button) view.findViewById(R.id.code_btn);
		post_btn = (Button) view.findViewById(R.id.post_btn);
	}

	@Override
	protected void setListener() {
		rb_login.setOnKeyListener(new MyOnKeyListener());
		rb_login.requestFocus();
		rb_login.setSelected(true);
		rb_login.setOnFocusChangeListener(new OnFocusChangeListener() {

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

				if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {

					if (v == rb_login) {
						if (mCallback != null) {
							mCallback.replaceFragment(0, 0);

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
	public void onStop() {
		time.cancel();
		super.onStop();
	}

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {// 计时完毕
			code_btn.setText("获取验证码");
			code_btn.setFocusable(true);
			code_btn.setBackgroundResource(R.drawable.bg_button);
			code_btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sm_22));
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
		// TODO Auto-generated method stub
		if (hasFocus) {
			phonef = true;
			// ((CompoundButton) v).setChecked(true);
		} else {
			if (phonef) {
				Matcher m = null;
				if (v == phone_edit) {
					m = p.matcher(phone_edit.getText().toString());

					if (!m.matches() || phone_edit.getText().toString().length() < 11) {

						rb_prompt1.setTextColor(getActivity().getResources().getColor(R.color.lau_red));

					} else {
						rb_prompt1.setTextColor(Color.parseColor("#6C6C6C"));
						phone = true;
					}
				}
				Matcher m1 = null;
				// 判断填写验证码是否正确
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
	public void onResume() {

		super.onResume();
		rb_login.requestFocus();
		rb_login.setSelected(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.code_btn:

			if (phone_edit.getText().toString().length() == 0) {

				networkdialog = new NetworkDialog(getActivity(), "手机号码不能为空！", 2);
				return;
			}

			if (connectfalg) {
				try {
					String result = ml.wasu_upm_SMS_verifycode_get(phone_edit.getText().toString(), tvId, deviceId, 2);
					Log.e("gupu", "resultget：" + result);
					JSONObject obj = new JSONObject(result);
					int code = obj.getInt("code");
					Log.e("gupu", "verifycode_lof_code：" + code);
					if (code == 0) {
						Editor ed = spd.edit();
						ed.putString("Phone", phone_edit.getText().toString());
						ed.commit();
						time.start();
					} else {
						networkdialog = new NetworkDialog(getActivity(), "该手机号码未注册", 2);
					}

				} catch (Exception e) {
					// TODO: handle exception
					networkdialog = new NetworkDialog(getActivity(), "请重新获取", 2);
				}
			} else {
				networkdialog = new NetworkDialog(getActivity(), "请重新获取3", 2);
				time.start();
			}

			break;
		case R.id.post_btn:
			if (verification_edit.getText().toString().length() == 0) {

				networkdialog = new NetworkDialog(getActivity(), "验证码不能为空！", 2);
				return;
			}
			if (connectfalg) {
				// if(phone&&verification){
				//
				// }

				login();
			} else {
				networkdialog = new NetworkDialog(getActivity(), "系统异常，请稍后再试", 2);
			}

			break;

		}

	}

	private void login() {
		try {

			int accountType = spd.getInt("accountType", 9);
			// String account= spd.getString("account", null);
			String caid = spd.getString("CAId", null);
			String result = ml.wasu_upm_other_user_login(caid, accountType, "999999", tvId, deviceId, Utils.getLocalMacAddressFromIp(),
					"2027");
			Log.e("gupu", "login：" + result);
			JSONObject obj = new JSONObject(result);
			String userKey = obj.getString("userKey");
			Log.e("gupu", "userKey：" + userKey);
			String token = obj.getString("token");
			Log.e("gupu", "token：" + token);
			int code1 = obj.getInt("code");
			Log.e("gupu", "codelogin：" + code1);
			Log.e("gupu", "form：" + form);
			if (code1 == 0) {

				Editor ed = spd.edit();
				ed.putString("userKey", userKey);
				ed.putString("token", token);
				ed.commit();

				if (form.equals("agree")) {
					getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
					getActivity().finish();
					// if (mCallback != null) {
					//
					// mCallback.replaceFragment(0, 2);
					//
					// }
				} else if (form.equals("live")) {

				} else if (form.equals("vod")) {

				} else if (form.equals("user")) {
					// us_lin_login
					getActivity().finish();
				}
			} else {
				networkdialog = new NetworkDialog(getActivity(), "系统异常，请稍后再试_2", 2);

			}
		} catch (Exception e) {
			// TODO: handle exception
			networkdialog = new NetworkDialog(getActivity(), "系统异常，请稍后再试_3", 2);

		}

	}

	@Override
	public void onConnnected(MyListener arg0) {
		// TODO Auto-generated method stub
		Log.e("gupu", "连接成功login");
		this.ml = arg0;
		connectfalg = true;
	}

	@Override
	public void onDisconnected(MyListener arg0) {
		Log.e("gupu", "连接失败login");
		// TODO Auto-generated method stub
		this.ml = arg0;
		connectfalg = false;
	}

}
