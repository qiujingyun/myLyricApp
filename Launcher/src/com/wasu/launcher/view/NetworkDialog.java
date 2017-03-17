package com.wasu.launcher.view;



import com.wasu.launcher.R;
import com.wasu.launcher.utils.ScaleAnimEffect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class NetworkDialog  {
	//private Context context;
  private Activity activity;
    private ScaleAnimEffect mScaleAnimEffect;
    private String name;
    private int form=0;
	 public NetworkDialog(Activity activity ,String name,int form ){
	    	this.activity=activity;
	    	this.name=name;
	    	this.form=form;
	    	setView();
	    }
	 
	 public void setView(){
		 mScaleAnimEffect = new ScaleAnimEffect();
		 final Dialog dialog = new Dialog(activity);
		
		      Window window = dialog.getWindow();
		    
				WindowManager.LayoutParams lp = window.getAttributes();
				// 设置透明度为0.3

				lp.alpha = 0.8f;
				//dialog.setTitle("温馨提示");j
				  dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			      dialog.setContentView(R.layout.dialog_network_layout);
			     dialog.setCancelable(false);
				TextView title = (TextView) dialog.findViewById(R.id.content);
				title.setText(name);
		  
				ImageView positiveButton = (ImageView) dialog
						.findViewById(R.id.positiveButton);
				     positiveButton.bringToFront();
					//Animation msAnimation = null;
//					msAnimation = mScaleAnimEffect.ScaleAnimation(1.0F, 1.1F, 1.0F,
//							1.1F);
//					AnimationSet set = new AnimationSet(true);
//					set.addAnimation(msAnimation);
//					set.setFillAfter(true);
//					positiveButton.startAnimation(set);
				dialog.setOnKeyListener(new OnKeyListener() {
					
					@Override
					public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
						  if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0)
		                  {
							  dialog.dismiss();
		                  }
						return false;
					}
				});
	 	  
	 	 positiveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(form==1){
					dialog.cancel();
					activity.finish();
				}else if(form==2){
					dialog.cancel();
				}
				
				
			}
		});
			
			
			dialog.show();
	    }

	
	

	

}
