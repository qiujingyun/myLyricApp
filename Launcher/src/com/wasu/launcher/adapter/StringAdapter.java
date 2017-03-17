package com.wasu.launcher.adapter;

import java.util.List;

import android.R.interpolator;
import android.content.Context;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.wasu.launcher.R;

public class StringAdapter extends BaseAdapter {

	private Context context;
	private List<String> data;
	private LayoutInflater mInflater;
	private ViewHolder holder;
	ListView i;

	public StringAdapter(Context context, List<String> data, ListView listView) {
		this.context = context;
		this.data = data;
		mInflater = LayoutInflater.from(context);
		this.i = listView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.lau_list_item, null);

			holder = new ViewHolder();
			holder.tv_text = (TextView) convertView.findViewById(R.id.item_text);
			LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) (((i.getHeight() - 8) / 4)));
			holder.tv_text.setLayoutParams(layoutParams);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String title = data.get(position);
		holder.tv_text.setText(title);

		return convertView;
	}

	class ViewHolder {
		TextView tv_text;
	}

}
