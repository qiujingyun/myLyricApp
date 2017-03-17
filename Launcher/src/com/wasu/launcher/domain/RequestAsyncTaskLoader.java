package com.wasu.launcher.domain;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

public class RequestAsyncTaskLoader extends AsyncTaskLoader<Object>{

	private LoadInBackgroundListener listener;
	private Object mResult;
	
	public RequestAsyncTaskLoader(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object loadInBackground() {
		// TODO Auto-generated method stub
		if(listener != null){
			mResult = listener.loadInBackgroundListener();
		}
		return mResult;
	}
	
    @Override
    protected void onStartLoading() {
    	super.onStartLoading();
    	
        if(mResult != null){
            deliverResult(mResult);
        }

        if (mResult == null || takeContentChanged()) {
            forceLoad();
        }
    }
    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        cancelLoad();
    }


	public void setLoadInBackgroundListener(LoadInBackgroundListener listener){
		this.listener = listener;
	}
	
	public interface LoadInBackgroundListener {
		Object loadInBackgroundListener();
	}

}
