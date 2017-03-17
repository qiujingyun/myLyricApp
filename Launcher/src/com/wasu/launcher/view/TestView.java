package com.wasu.launcher.view;

import java.util.ArrayList;
import com.wasu.launcher.R;

import android.R.color;
import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class TestView extends RelativeLayout implements OnClickListener{
	Context context;
	private String TAG = "qjy";
	private ListView mylv;
	private RelativeLayout vod_lv;
	
	private LinearLayout xz;
	private TextView t1;
	private TextView t2;
	private TextView t3;
	
	private int currentID = 0;
	ArrayList<String> info;
	private ListViewAdpter mAdapter;
	private int selectColor = 0xffebec15, unselectColor = 0x00000000;//0xff334455;
	
	public TestView(Context context) {
		super(context);
		this.context = context;
		
		View.inflate(context, R.layout.test, this);
		init(this);
	}

	public TestView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	
		View.inflate(context, R.layout.test, this);
		init(this);
	}
	

	private void init(View view) {
		vod_lv = (RelativeLayout)view.findViewById(R.id.vod_lv);
		mylv = (ListView) view.findViewById(R.id.mylistview);
		xz = (LinearLayout) view.findViewById(R.id.xz);
		t1 = (TextView) view.findViewById(R.id.t1);
		t2 = (TextView) view.findViewById(R.id.t2);
		t3 = (TextView) view.findViewById(R.id.t3);
		
		info = new ArrayList<String>(){{add("item1forlonglonglongcontent");add("item1forlonglonglongcontent");add("item3");add("item4");add("item5");add("item6");add("item7");add("item8");add("item9");}};
		mAdapter = new ListViewAdpter(context.getApplicationContext(), info);
		mylv.setAdapter(mAdapter);
	}
	
    @Override  
    public void onClick(View v) {   
    	 switch(v.getId()){  
         case R.id.t1:  
             break;
         case R.id.t2:  
             break;
    	 }
	}
    
	public class ListViewAdpter extends BaseAdapter {
		ArrayList<String> myT;
		Context context;
		private int mSelectId = 0;
		private ArrayList<View> tv = new ArrayList<View>();
		
		public ListViewAdpter(Context context, ArrayList<String> mT) {
			this.context = context.getApplicationContext();
			myT = mT;
		}
		
		public int getCount(){
			return 5;
		}
		
        public View getItem(int position) {
        	Log.e(TAG, "getItem position is " + position);
            return tv.get(position);    
        }    
    
        public long getItemId(int position) {
        	Log.e(TAG, "getItemId position is " + position);
        	return position;
        }  
        
        private void setSelectId(int id) {
        	mSelectId = id;
        }
        
        public int getSelectId(){
        	return mSelectId;
        }        
		
		public View getView(int position, View convertView, ViewGroup parent) {
				Log.e(TAG, "getView currentID position is " + currentID + "," +position);
				int mid = (getCount()-1)/2;
				int cur = position;
				
				if (convertView == null) {
					Log.e(TAG, "getView position is " + position + " convertView is null");
					
					convertView = new TextView(context);
					((TextView) convertView).setTextSize(30);
					((TextView) convertView).setTextColor(Color.BLACK);	
					((TextView) convertView).setGravity(Gravity.CENTER);
			        ((TextView) convertView).setEllipsize(TextUtils.TruncateAt.valueOf("MARQUEE"));
			        ((TextView) convertView).setSingleLine(true);
					((TextView) convertView).setPadding(5, 10 , convertView.getRight(), 10);
				}	

				if (currentID <= mid) { 
					if (currentID == position) {
						setSelectId(position);
						convertView.setBackgroundColor(selectColor);
					}else {
						convertView.setBackgroundColor(unselectColor);
					}
				}else if (currentID >= (myT.size() - mid)){
					if ((myT.size() - currentID) == (getCount() - position)) {
						setSelectId(position);
						convertView.setBackgroundColor(selectColor);
					}else {
						convertView.setBackgroundColor(unselectColor);
					}
				}else if (position == ((getCount()-1)/2)) {
					setSelectId(position);
					convertView.setBackgroundColor(selectColor);
				}else {
					convertView.setBackgroundColor(unselectColor);
				}

				if (currentID > mid && currentID < (myT.size()-mid)) {
					cur = position + currentID - 2;
				}else if (currentID >= (myT.size()-mid)) {
					cur = myT.size() - getCount() + position;
				}
				
		        ((TextView) convertView).setText(myT.get(cur));
		        
		        tv.add(convertView);
		        
	            return convertView;  
	    }    
	}

    @Override
    public boolean isFocused() {
        return true;
    }
    
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP) {
			switch(event.getKeyCode()){
			case KeyEvent.KEYCODE_DPAD_UP:
				Log.e(TAG, "keycode is KEYCODE_DPAD_UP");
				if (currentID > 0 && mylv.isFocused()) {
					currentID--;
					mAdapter.notifyDataSetChanged();
					mylv.setSelection(mAdapter.getSelectId());
				
					if (xz.getVisibility() == View.VISIBLE) {
						xz.setVisibility(View.GONE);
					}
					return true;
				}				
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				Log.e(TAG, "keycode is KEYCODE_DPAD_DOWN");
				if (currentID < (info.size()-1) && mylv.isFocused()) {
					currentID++;
					mAdapter.notifyDataSetChanged();
					mylv.setSelection(mAdapter.getSelectId());
								
					if (xz.getVisibility() == View.VISIBLE) {
						xz.setVisibility(View.GONE);
					}
					return true;
				}				
				View oldFocusView = this.getRootView().findFocus();
				View newFocusView = oldFocusView.focusSearch(View.FOCUS_DOWN);
				newFocusView.requestFocus();
				return true;	
			case KeyEvent.KEYCODE_DPAD_RIGHT:
			case KeyEvent.KEYCODE_DPAD_CENTER:
			case KeyEvent.KEYCODE_ENTER:
				Log.e(TAG, "keycode is KEYCODE_ENTER");
				int n = mAdapter.getSelectId();
				TextView  a = (TextView) mylv.getItemAtPosition(n);
				if (a != null && mylv.isFocused()) {
					RelativeLayout.LayoutParams lp =(RelativeLayout.LayoutParams) xz.getLayoutParams();
					
			        int left = mylv.getRight();
			        int bottom = mylv.getTop() + a.getHeight() * n + a.getHeight()/2 + lp.height/2;

			        if (bottom > mylv.getBottom()) {
						bottom = mylv.getBottom();
					}
			            
					lp.setMargins(left, bottom-lp.height, left+lp.width, bottom);    
					xz.setLayoutParams(lp); 

					xz.setVisibility(View.VISIBLE);
					xz.postInvalidate();
					return true;
				}
				break;				
			default:
				break;	
			}
		}
		
		Log.e(TAG, "start super.dispatchKeyEvent(event);");
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
	
}