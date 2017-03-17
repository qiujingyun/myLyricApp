package com.wasu.launcher.offlinelive;

import android.os.Parcel;
import android.os.Parcelable;

public class RectD implements Parcelable {
	public float width;
	public float height;
	public float x;
	public float y;
	public float alpha;

	/**
	 * Create a new empty RectF. All coordinates are initialized to 0.
	 */
	public RectD() {
	}

	/**
	 * Create a new rectangle with the specified coordinates. Note: no range
	 * checking is performed, so the caller must ensure that x <= width and y <=
	 * height.
	 *
	 * @param x
	 *            The X coordinate of the x side of the rectangle
	 * @param y
	 *            The Y coordinate of the y of the rectangle
	 * @param width
	 *            The X coordinate of the width side of the rectangle
	 * @param height
	 *            The Y coordinate of the height of the rectangle
	 */
	public RectD(float height, float width, float x, float y) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(x);
		dest.writeFloat(y);
		dest.writeFloat(width);
		dest.writeFloat(height);
	}

}
