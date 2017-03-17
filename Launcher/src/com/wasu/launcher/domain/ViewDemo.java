package com.wasu.launcher.domain;

import com.wasu.launcher.R;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ViewDemo {
	View view;
	ProgressBar progressBar;
	TextView textView1, time, Number, Details;

	public View getView() {
		return view;
	}

	public void setView(View view) {
		textView1 = (TextView) view.findViewById(R.id.textView1);
		time = (TextView) view.findViewById(R.id.textView2);
		Number = (TextView) view.findViewById(R.id.textView4);
		Details = (TextView) view.findViewById(R.id.textView3);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
		this.view = view;
	}

	public TextView getTime() {
		return time;
	}

	public void setTime(TextView time) {
		this.time = time;
	}

	public TextView getNumber() {
		return Number;
	}

	public void setNumber(TextView number) {
		Number = number;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	public TextView getDetails() {
		return Details;
	}

	public void setDetails(TextView details) {
		Details = details;
	}

	public TextView getTable() {
		return textView1;
	}

	public void setable(TextView textView1) {
		this.textView1 = textView1;
	}

}
