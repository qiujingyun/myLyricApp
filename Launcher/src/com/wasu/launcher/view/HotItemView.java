package com.wasu.launcher.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wasu.launcher.R;

public class HotItemView extends RelativeLayout {

	private ProgressBar progressBar;
	private TextView title, time, Number, Details;
	private String namespace = "http://schemas.android.com/apk/res/com.wasu.launcher";
	private String texttitle, texttime, textNumber, textDetails;
	private int MaxProgress, Progress, Progress_bg;
	RelativeLayout relativeLayout;
	ImageView image_in;

	public void setView(View view) {
		title = (TextView) view.findViewById(R.id.textView1);
		time = (TextView) view.findViewById(R.id.textView2);
		Number = (TextView) view.findViewById(R.id.textView4);
		Details = (TextView) view.findViewById(R.id.textView3);
		relativeLayout = (RelativeLayout) view.findViewById(R.id.re);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
		image_in = (ImageView) view.findViewById(R.id.image_in);
	}

	public void setHEIColor() {
		title.setTextColor(0xffffffff);
		time.setTextColor(0xffffffff);
		Number.setTextColor(0xffffffff);
		Details.setTextColor(0xffffffff);
		relativeLayout.setBackgroundColor(0xA0747272);
		image_in.setImageResource(R.drawable.lau_people_u);
	}

	public void setHONGColor() {
		title.setTextColor(0xFF000000);
		time.setTextColor(0xff000000);
		Number.setTextColor(0xff000000);
		Details.setTextColor(0xff000000);
		relativeLayout.setBackgroundColor(0xFFffff00);
		image_in.setImageResource(R.drawable.lau_people_u);
	}

	public HotItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initValues(context);
	}

	public HotItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initValues(context);
		title.setText(attrs.getAttributeValue(namespace, "title"));
		time.setText(attrs.getAttributeValue(namespace, "time"));
		Number.setText(attrs.getAttributeValue(namespace, "nubmr"));
		Details.setText(attrs.getAttributeValue(namespace, "details"));
		progressBar.setMax(attrs.getAttributeIntValue(namespace, "maxprogress", 100));
		int imageid = attrs.getAttributeResourceValue(namespace, "progressbar_bg", R.drawable.lau_progressbar_bg_1);
		progressBar.setProgressDrawable(getResources().getDrawable(imageid));
		progressBar.setProgress(attrs.getAttributeIntValue(namespace, "progress", 0));
		relativeLayout.setBackgroundColor(0xA0747272);
	}

	public HotItemView(Context context) {
		super(context);
		initValues(context);
	}

	private void initValues(Context context) {
		View.inflate(context, R.layout.lau_include_live, this);
		setView(this);
	}

	public void setText(String texttitle, String texttime, String textNumber, String textDetails) {
		title.setText(texttitle);
		time.setText(texttime);
		Number.setText(textNumber);
		Details.setText(textDetails);
		this.texttitle = texttitle;
		this.texttime = texttime;
		this.textNumber = textNumber;
		this.textDetails = textDetails;
	}
	public void setText(String texttitle, String textNumber) {
		title.setText(texttitle);
		Number.setText(textNumber);
		this.texttitle = texttitle;
		this.textNumber = textNumber;
	}

	public String getTexttitle() {
		return texttitle;
	}

	public void setTexttitle(String texttitle) {
		title.setText(texttitle);
		this.texttitle = texttitle;
	}

	public String getTexttime() {
		return texttime;
	}

	public void setTexttime(String texttime) {
		time.setText(texttime);
		this.texttime = texttime;
	}

	public String getTextNumber() {
		return textNumber;
	}

	public void setTextNumber(String textNumber) {
		Number.setText(textNumber);
		this.textNumber = textNumber;
	}

	public String getTextDetails() {
		return textDetails;
	}

	public void setTextDetails(String textDetails) {
		Details.setText(textDetails);
		this.textDetails = textDetails;
	}

	public int getMaxProgress() {
		return MaxProgress;
	}

	public void setMaxProgress(int maxProgress) {
		progressBar.setMax(maxProgress);
		MaxProgress = maxProgress;
	}

	public int getProgress() {
		return Progress;
	}

	public void setProgress(int progress) {
		progressBar.setProgress(progress);
		Progress = progress;
	}

	public int getProgress_bg() {
		return Progress_bg;
	}

	public void setProgress_bg(int progress_bg) {
		progressBar.setProgressDrawable(getResources().getDrawable(progress_bg));
		Progress_bg = progress_bg;
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		if (gainFocus) {
			setHONGColor();
		} else {
			setHEIColor();
		}
	}
}
