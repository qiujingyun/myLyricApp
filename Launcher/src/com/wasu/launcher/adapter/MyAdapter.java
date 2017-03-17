package com.wasu.launcher.adapter;

import java.util.List;

import com.wasu.launcher.R;
import com.wasu.launcher.entity.MyListItem;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	private Context context;
	private List<MyListItem> myList;

	public MyAdapter(Context context, List<MyListItem> myList) {
		this.context = context;
		this.myList = myList;
	}

	public int getCount() {
		return myList.size();
	}

	public Object getItem(int position) {
		return myList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		MyListItem myListItem = myList.get(position);
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.lau_register_spinner_view, parent, false);
		}
		TextView txtvwDropdown = (TextView) convertView.findViewById(R.id.spinner_viewtext);
		txtvwDropdown.setText(myListItem.getName()+" ∨");
		return convertView;
		// return new MyAdapterView(this.context, myListItem );
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		MyListItem myListItem = myList.get(position);
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.lau_register_spinner_item, parent, false);
		}
		TextView txtvwDropdown = (TextView) convertView.findViewById(R.id.spinner_text);
		txtvwDropdown.setText(myListItem.getName());
		return convertView;
	}

	class MyAdapterView extends LinearLayout {
		public static final String LOG_TAG = "MyAdapterView";

		public MyAdapterView(Context context, MyListItem myListItem) {
			super(context);
			this.setOrientation(HORIZONTAL);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.setMargins(1, 1, 1, 1);

			TextView name = new TextView(context);
			name.setText(myListItem.getName());
			name.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sm_30));
			name.setBackgroundResource(R.drawable.lau_purchase_bt_selector);
			name.setTextColor(getResources().getColor(R.color.lau_no_thanks_textcolor_selector));
			addView(name, params);

			// LinearLayout.LayoutParams params2 = new
			// LinearLayout.LayoutParams(200, LayoutParams.WRAP_CONTENT);
			// params2.setMargins(1, 1, 1, 1);
			//
			// TextView pcode = new TextView(context);
			// pcode.setText(myListItem.getPcode());
			// addView( pcode, params2);
			// pcode.setVisibility(GONE);

		}

	}

}