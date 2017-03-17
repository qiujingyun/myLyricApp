package com.wasu.launcher.Fragment;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.wasu.json.JSONObject;
import com.wasu.launcher.R;
import com.wasu.launcher.Fragment.RegisteredFragment.SpinnerOnSelectedListener1;
import com.wasu.launcher.Fragment.RegisteredFragment.SpinnerOnSelectedListener2;
import com.wasu.launcher.Fragment.RegisteredFragment.SpinnerOnSelectedListener3;
import com.wasu.launcher.adapter.MyAdapter;
import com.wasu.launcher.entity.ExampleBean;
import com.wasu.launcher.entity.MyListItem;
import com.wasu.launcher.interfaces.MyFragmentListener;
import com.wasu.launcher.utils.ScaleAnimEffect;
import com.wasu.launcher.view.NetworkDialog;
import com.wasu.vod.aidl.SDKOperationManager;
import com.wasu.vod.aidl.SDKOperationManager.BindService;
import com.wasu.vod.aidl.SDKOperationManager.MyListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class UpdateAddressFragment extends BaseFragment implements  BindService ,OnClickListener {
        private View view;
        private MyFragmentListener mCallback;
	private TextView rb_address;
	private Button  post_btn;
	private Spinner provinceSpinner = null; // 省级（省、直辖市）
	private Spinner citySpinner = null; // 地级市
	private Spinner countySpinner = null;
	  MyListener ml;

SharedPreferences spd;
ExampleBean body;
private String province = null;//
private String city = null;
private String county = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (container == null) {
			return null;
		}
		if (null == view) {
                view = inflater.inflate(R.layout.layout_updateadress, container, false);
               
                initview();
		}
		
		return view;
	}
	private void initview() {
		//animEffect = new ScaleAnimEffect();
		loadViewLayout();
		findViewById();
		setListener();

	}
	
String userKey,tvId,deviceId,token;
	@Override
	protected void loadViewLayout() {
		spd = getActivity().getSharedPreferences("mydevice",
				Context.MODE_APPEND);
		userKey=spd.getString("userKey", null);
		tvId=spd.getString("tvId", null);
		deviceId=spd.getString("deviceId", null);
		token=spd.getString("token", null);
		
		//ml.wasu_bsa_query_area("0", null, null, null, false);
		
	}
	
    @Override
	protected void findViewById(){
		rb_address=(TextView) view.findViewById(R.id.rb_address);
		rb_address.requestFocus();
		rb_address.setSelected(true);
		post_btn = (Button) view.findViewById(R.id.post_btn);
		provinceSpinner = (Spinner) view.findViewById(R.id.spin_province);
		citySpinner = (Spinner) view.findViewById(R.id.spin_city);
		countySpinner = (Spinner) view.findViewById(R.id.spin_county);
	}

	@Override
	protected void setListener() {
		rb_address.requestFocus();
		rb_address.setSelected(true);
		rb_address.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					v.setSelected(true);
					//((CompoundButton) v).setChecked(true);
				} else {
					v.setSelected(false);
				}
				
			}
		});
		post_btn.setOnClickListener(this);
	}

	
	boolean connectfalg = false;
	NetworkDialog networkdialog;
	@Override
	public void onClick(View arg0) {
		if (connectfalg) {
			updateaddress();
		} else {
			networkdialog = new NetworkDialog(getActivity(), "系统异常，请稍后再试", 2);
		}
		
	}
	private void updateaddress() {
//		。    
		try {
			String Phone = spd.getString("Phone", null);
			String result = ml.wasu_upm_userinfo_post(userKey, tvId, deviceId, token, "asd", 1, 25,  province+city+county, null, Phone);
			JSONObject obj = new JSONObject(result);
			int address_code = obj.getInt("code");
		if(address_code==0){
			networkdialog = new NetworkDialog(getActivity(),
					"修改成功", 1);
		}
//		else{
//			networkdialog = new NetworkDialog(getActivity(),
//					"系统异常，请稍后再试", 2);
//		}
		
		} catch (Exception e) {
			networkdialog = new NetworkDialog(getActivity(),
					"系统异常，请稍后再试", 2);
		}
	}
	@Override
	public void onConnnected(MyListener arg0) {
		Log.e("gupu", "连接成功update");
		 this.ml = arg0;
		 
		 connectfalg=true;
		 try {
			  Log.e("gupu", "userinfo_getrsadsadasdesult");
			  Log.e("gupu", "1userKey:"+userKey+",tvId:"+tvId+",deviceId:"+deviceId+",token:"+token);
			  
			  String result_are=ml.wasu_bsa_query_area("-1", "gg", "gg", "gg", false);
				 
				 Log.e("gupu", "query_area_result:"+result_are);
				setSpin(result_are);
			  String result=ml.wasu_upm_userinfo_get(userKey, tvId, deviceId, token);
			  Log.e("gupu", "userinfo_getresult:"+result);
			  JSONObject obj = new JSONObject(result);
		     	int code=obj.getInt("code");
		     	if(code==0){
		     	 String address = obj.getString("address");
					 Log.e("gupu", "address:"+address);
		     	}
		
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void onDisconnected(MyListener arg0) {
		Log.e("gupu", "连接失败update");
		// TODO Auto-generated method stub
		 this.ml = arg0;
		 connectfalg=false;
	}
	

	private void setSpin(String resutl){
		 
		
				try{  
				
					  Gson gson=new Gson();
					 
					  JSONObject   obj=new JSONObject(resutl);
				
				  body= gson.fromJson(resutl, ExampleBean.class);
				  Log.e("gupu","body:"+body.toString());
				
				
				 initSpinner1();
				}catch(Exception ex){  
				    ex.printStackTrace();  
				}  
		
	}
	 public void initSpinner1(){
		
		 	List<MyListItem> list = new ArrayList<MyListItem>();
		 	// Log.e("gupu","provinceList:"+provinceList.get(9).);
			for(int i=0;i<body.getBody().getProviceList().size();i++ ){
				 
					//Log.e("gupu","provinceList.get(i):"+provinceList.get(i).toString());
					 String code=body.getBody().getProviceList().get(i).getAreaCode(); 
				     String name=body.getBody().getProviceList().get(i).getAreaName();
				//ProvinceBean pr=(ProvinceBean)body.getBody().getProviceList().get(i);
				 MyListItem myListItem=new MyListItem();
			        myListItem.setName(name);
			        myListItem.setPcode(code);
			        list.add(myListItem);
			}
		 	
		 	
		 	MyAdapter myAdapter = new MyAdapter(getActivity(),list);
		 	provinceSpinner.setAdapter(myAdapter);
		 	provinceSpinner.setOnItemSelectedListener(new SpinnerOnSelectedListener1());
		}
	    public void initSpinner2(String pcode){
	    	 
		 	List<MyListItem> list = new ArrayList<MyListItem>();
		 	for(int i=0;i<body.getBody().getCityList().size();i++ ){
				//CityBean cb=(CityBean) cityList.get(i);
		 		 String ParentId=body.getBody().getCityList().get(i).getAreaParentId(); 
				if(ParentId.equals(pcode)){
					 String code=body.getBody().getCityList().get(i).getAreaCode(); 
				     String name=body.getBody().getCityList().get(i).getAreaName();
					 MyListItem myListItem=new MyListItem();
				        myListItem.setName(name);
				        myListItem.setPcode(code);
				        list.add(myListItem);
				}
				
			}
		 
		 	
		 	MyAdapter myAdapter = new MyAdapter(getActivity(),list);
		 	citySpinner.setAdapter(myAdapter);
		 	citySpinner.setOnItemSelectedListener(new SpinnerOnSelectedListener2());
		}
	    public void initSpinner3(String pcode){
	    	
		 	List<MyListItem> list = new ArrayList<MyListItem>();
		 	for(int i=0;i<body.getBody().getCountyList().size();i++ ){
		 		 String ParentId=body.getBody().getCountyList().get(i).getAreaParentId(); 
					if(ParentId.equals(pcode)){
						 String code=body.getBody().getCountyList().get(i).getAreaCode(); 
					     String name=body.getBody().getCountyList().get(i).getAreaName();
						 MyListItem myListItem=new MyListItem();
					        myListItem.setName(name);
					        myListItem.setPcode(code);
					        list.add(myListItem);
					}
				
			}
		 	
		 	
		 	MyAdapter myAdapter = new MyAdapter(getActivity(),list);
		 	countySpinner.setAdapter(myAdapter);
		 	countySpinner.setOnItemSelectedListener(new SpinnerOnSelectedListener3());
		}
	    
		class SpinnerOnSelectedListener1 implements OnItemSelectedListener{
			
			public void onItemSelected(AdapterView<?> adapterView, View view, int position,
					long id) {
				province=((MyListItem) adapterView.getItemAtPosition(position)).getName();
				String pcode =((MyListItem) adapterView.getItemAtPosition(position)).getPcode();
				
				initSpinner2(pcode);
				initSpinner3(pcode);
			}

			public void onNothingSelected(AdapterView<?> adapterView) {
				// TODO Auto-generated method stub
			}		
		}
		class SpinnerOnSelectedListener2 implements OnItemSelectedListener{
			
			public void onItemSelected(AdapterView<?> adapterView, View view, int position,
					long id) {
				city=((MyListItem) adapterView.getItemAtPosition(position)).getName();
				String pcode =((MyListItem) adapterView.getItemAtPosition(position)).getPcode();

				initSpinner3(pcode);
			}

			public void onNothingSelected(AdapterView<?> adapterView) {
				// TODO Auto-generated method stub
			}		
		}
		
		class SpinnerOnSelectedListener3 implements OnItemSelectedListener{
			
			public void onItemSelected(AdapterView<?> adapterView, View view, int position,
					long id) {
				county=((MyListItem) adapterView.getItemAtPosition(position)).getName();
				//Toast.makeText(getActivity(), province+" "+city+" "+county, Toast.LENGTH_LONG).show();
			}

			public void onNothingSelected(AdapterView<?> adapterView) {
				// TODO Auto-generated method stub
			}		
		}
	@Override
	public void onDestroy() {
	// TODO Auto-generated method stub
		
	    super.onDestroy();
	
	}

}
