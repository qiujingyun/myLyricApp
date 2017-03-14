package com.android.test;

import java.util.ArrayList;
import java.util.List;

import com.android.test.view.LyricView;
import com.android.test.view.TitleView;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;

public class MActivity extends Activity {
    String TAG = "qjy";
    Button myButton;
    LyricView mv;
    TitleView mtv;
    Thread spThread;
    Thread lrcT;
    
	private ListView mylv;
	private int currentID = 0;
	ArrayList<String> info;
    
    private String mp3 = "file:///data/app/test.mp3";
    MediaPlayer myMP;
    
    Handler mHandler = new Handler(){
    	
    	public void handleMessage(Message msg){
    		Log.e(TAG, "handlerMessage");
    		switch (msg.what) {
			case 0:
				mv.setTime(msg.arg1);
				mv.invalidate();
				break;

			default:
				break;
			}
    		super.handleMessage(msg);
    	}
    };
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        
        setContentView(R.layout.main);
        Log.d(TAG, "onCreate");    
        
        myButton =(Button) findViewById(R.id.startbt);
        mv = (LyricView) findViewById(R.id.mylrc);
        mtv = (TitleView) findViewById(R.id.mytv);
        myButton.setOnClickListener(mListener);
        
        myMP = new MediaPlayer();
        
		mylv = (ListView) findViewById(R.id.mylistview);
		info = new ArrayList<String>(){{add("item1");add("item2");add("item3");add("item4");add("item5");}}; 
		mylv.setAdapter(new ListViewAdpter(getApplicationContext(), info));        
        
    }

	
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP) {
			switch(event.getKeyCode()){
			case KeyEvent.KEYCODE_DPAD_UP:
				if (currentID > 0) {
					currentID--;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				if (currentID < info.size()) {
					currentID++;
				}				
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
			case KeyEvent.KEYCODE_ENTER:
				
				break;				
			default:
				break;	
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
    
	public class ListViewAdpter extends BaseAdapter {
		ArrayList<String> myT;
		Context context;
		TextView[] mViews;
		
		public ListViewAdpter(Context context, ArrayList<String> mT) {
			this.context = context.getApplicationContext();
			mViews = new TextView[getCount()];
			myT = mT;
		}
		
		public int getCount(){
			return 3;
		}
		
        public View getItem(int position) {
			TextView mytv = mViews[position];
			mytv.setTextSize(50);
			mytv.setTextColor(Color.BLACK);
			
        	if (currentID != 0 && currentID != 1) {
				position = position + currentID - 1;
			}
			
			mytv.setText(myT.get(position));
            return mytv;    
        }    
    
        public long getItemId(int position) {
        	return position;
        }  
		
		public View getView(int position, View convertView, ViewGroup parent) {
				if (position == ((getCount()-1)/2)) {
					convertView.setBackgroundColor(Color.RED);
				}
				convertView.setBackgroundColor(Color.GRAY);
	            return convertView;  
	    }    
	}

    
    OnClickListener mListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			myButton.setVisibility(View.GONE);
			mtv.setVisibility(View.GONE);
			mv.setVisibility(View.VISIBLE);
			mv.invalidate();
			
			try {
				myMP.setDataSource(mp3);
				myMP.prepare();
			} catch (Exception e) {
				Log.e(TAG, "ERROR: can not start mediaplayer");
				e.printStackTrace();
			}
			
			startPlayer();

		}
	};
    
	private void startPlayer() {
		spThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				myMP.start();				
			}
		});
		
		lrcT = new Thread(new Runnable() {
			Message msg; 
			
			@Override
			public void run() {
				try {
					while (true) {
						Thread.sleep(1000);
						Log.e(TAG, "current position is " + myMP.getCurrentPosition());
						msg = mHandler.obtainMessage();
						msg.arg1 = myMP.getCurrentPosition();
						msg.what = 0;
						mHandler.sendMessage(msg);
					}					
				} catch (Exception e) {
					Log.e(TAG, "ERROR: set current position!");
					e.printStackTrace();
				}
			}
		});
		
		spThread.start();
		lrcT.start();
	}
	
    @Override
    public void onStart()
    {
    	super.onStart();
		Log.d(TAG, "onStart");
    }

    @Override
    public void onRestart()
    {   
        super.onRestart();
        Log.d(TAG, "onRestart");
    } 

    @Override
    public void onPause()
    {   
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onResume()
    {   
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onStop()
    {   
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroy()
    {   
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        
        spThread.interrupt();
        lrcT.interrupt();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
    	super.onSaveInstanceState(outState);
    	Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    public void onRestoreInstanceState(Bundle outState)
    {
        super.onRestoreInstanceState(outState);
        Log.d(TAG, "onRestoreInstanceState");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
    	super.onConfigurationChanged(newConfig);
    	Log.d(TAG, "onConfigurationChanged");
    }

}
