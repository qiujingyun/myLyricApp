package com.wasu.launcher.offlinelive;

import java.util.List;

import org.ngb.broadcast.dvb.si.SIService;

import com.wasu.launcher.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class PBaseAdapter extends BaseAdapter {
	Context context;
	List<SIService> applist;
	viewid mHolder = null;

	// public PBaseAdapter(Context context, List<Bean> applist) {
	// this.context = context;
	// this.applist = applist;
	// }

	public PBaseAdapter(Context context, List<SIService> applist) {
		this.context = context;
		this.applist = applist;
	}

	@Override
	public int getCount() {
		return applist.size();
	}

	@Override
	public Object getItem(int position) {
		return applist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			mHolder = new viewid();

			convertView = LayoutInflater.from(context).inflate(R.layout.item,
					null);
			mHolder.id = (TextView) convertView.findViewById(R.id.textView1);
			mHolder.name = (TextView) convertView.findViewById(R.id.textView2);
			convertView.setTag(mHolder);
		} else {
			mHolder = (viewid) convertView.getTag();
		}

		mHolder.name.setText(applist.get(position).getServiceProviderName());
		mHolder.id.setText(String.valueOf(position) + ".");

		return convertView;
	}

	class viewid {
		TextView name;
		TextView id;
	}

}
