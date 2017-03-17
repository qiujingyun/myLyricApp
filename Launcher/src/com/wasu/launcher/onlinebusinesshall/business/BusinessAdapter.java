package com.wasu.launcher.onlinebusinesshall.business;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wasu.android.volley.toolbox.ImageLoader;
import com.wasu.launcher.R;
import com.wasu.launcher.onlinebusinesshall.business.BusinessResult.BusinessData;
import com.wasu.vod.application.MyVolley;

public class BusinessAdapter extends BaseAdapter {
	Context context;
	List<BusinessData> data;
	LayoutInflater mInflater;
	ImageLoader imageLoader;

	public BusinessAdapter(Context context, List<BusinessData> data) {
		this.context = context;
		this.data = data;
		mInflater = LayoutInflater.from(context);
		imageLoader = MyVolley.getImageLoader();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.lau_item_businessmanagement, null);

			holder = new ViewHolder();
			holder.tv_text = (TextView) convertView.findViewById(R.id.item_tv_bus);
			holder.im_iew = (ImageView) convertView.findViewById(R.id.item_im_bus);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_text.setText(data.get(position).getName());
		if (data.get(position).getLogoPath() != null)
			imageLoader.get(data.get(position).getLogoPath(),
					ImageLoader.getImageListener(holder.im_iew, R.drawable.lau_default_logo, R.drawable.lau_default_logo));
		return convertView;
	}

	ViewHolder holder;

	class ViewHolder {
		TextView tv_text;
		ImageView im_iew;
	}
}
