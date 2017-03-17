package com.wasu.launcher.activity;

import java.util.ArrayList;

import org.ngb.ca.CAManager;

import com.wasu.launcher.BaseActivity;
import com.wasu.launcher.R;
import com.wasu.launcher.Fragment.BaseFragment;
import com.wasu.launcher.Fragment.LoginFragment;
import com.wasu.launcher.Fragment.RegisteredFragment;
import com.wasu.launcher.Fragment.ServiceAgreementFragment;
import com.wasu.launcher.Fragment.UpdateAddressFragment;
import com.wasu.launcher.Fragment.WasuRegisterFragment;
import com.wasu.launcher.Fragment.WelcomeFragment;
import com.wasu.launcher.interfaces.MyFragmentListener;
import com.wasu.launcher.utils.Aanimate;
import com.wasu.launcher.utils.ScaleAnimEffect;
import com.wasu.vod.aidl.SDKOperationManager;
import com.wasu.vod.aidl.SDKOperationManager.BindService;
import com.wasu.vod.aidl.SDKOperationManager.MyListener;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class UserActivity extends BaseActivity implements MyFragmentListener,BindService{

	private ArrayList<Fragment> fragments;
	//private RegisteredFragment    rf;
	private WasuRegisterFragment    rf;
	private LoginFragment lf;
	private UpdateAddressFragment uaf;
	MyListener ml;
	SDKOperationManager sm;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sm = new SDKOperationManager(this);
		sm.registerSDKOperationListener(this);
		setContentView(R.layout.activity_user);
		 Intent intent= getIntent();
	  	 form=intent.getStringExtra("form");
	  	 type=intent.getIntExtra("type", 0);
		 Log.e("gupu", "formUesr55555："+form);
		initView();
	}

	
	@Override
	protected void initView() {
		loadViewLayout();
		findViewById();
		setListener();
		
		// FragmentTransaction transaction = getFragmentManager().beginTransaction();
		 getIntent().putExtra("form", form);
		if(form.equals("agree")){
			 refreshFragment(0);
		}else if(form.equals("live")||form.equals("vod")||form.equals("user")){
			Log.e("gupu", "formUesr22222："+form);

			 refreshFragment(1);
			 //getIntent().putExtra("form", form);
		}else if(form.equals("update")){
			Log.e("gupu", "formUesr22222："+form);

			 refreshFragment(2);
		}
		 
		
			
		
	}


	
	FrameLayout fl;
	@Override
	protected void findViewById() {
	
		fl = (FrameLayout) findViewById(R.id.pager);
		
		fragments = new ArrayList<Fragment>();
		
		Bundle args = new Bundle();
		//rf = new RegisteredFragment();
		rf = new WasuRegisterFragment();
		args.putInt("num", 0);
		rf.setArguments(args);
		fragments.add(rf);

		lf = new LoginFragment();
		args.putInt("num", 1);
		lf.setArguments(args);
		fragments.add(lf);

		uaf = new UpdateAddressFragment();
		args.putInt("num", 2);
		uaf.setArguments(args);
		fragments.add(uaf);
	}

	@Override
	protected void setListener() {
	
		
	}

	

	String form;
     int type;
	@Override
	protected void loadViewLayout() {
	  	
	 SharedPreferences  spd = getSharedPreferences("mydevice",
				Context.MODE_APPEND);
	// String CAId= 	CAManager.getInstance().getCardSerialNumber();
	  	//Log.e("gupu", "CAId"+CAId);
	  	Editor	ed = spd.edit();
	    ed.putString("CAId", "user_test8");
	    ed.commit();
	}


	@Override
	public void replaceFragment(int old, int nev) {
		refreshFragment(nev);
		
	}
	@SuppressLint("NewApi")
	public void refreshFragment(int index) {
		

		if (fragments != null && fragments.size() > 0) {
			FragmentManager fm = getFragmentManager();

			try {
				if (fm != null /* && !fm.isDestroyed() */) {
					FragmentTransaction transaction = fm.beginTransaction();
					transaction.replace(R.id.pager, fragments.get(index));
					transaction.addToBackStack(null);
					transaction.commit();
					BaseFragment baseFragment = (BaseFragment) (fragments.get(index));
					if (baseFragment.isUp) {
						baseFragment.setMessage(1);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	protected void onDestroy() {
		sm.unRegisterSDKOperationListener();
		super.onDestroy();
		
	}

	@Override
	public void onConnnected(MyListener arg0) {
		// TODO Auto-generated method stub
		Log.e("gupu", "连接成功USER");
		 this.ml = arg0;
		try {
			String UserKey= ml.wasu_upm_getUserKey();
			Log.e("gupu", "UserKey_user:"+UserKey);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void onDisconnected(MyListener arg0) {
		// TODO Auto-generated method stub
		Log.e("gupu", "连接失败USER");
		 this.ml = arg0;
	}

}
