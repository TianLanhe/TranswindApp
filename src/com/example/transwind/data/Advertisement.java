package com.example.transwind.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

@SuppressWarnings("serial")
public class Advertisement implements Serializable {

	private String title; // ����
	private byte[] picture; // ͼƬ
	private String url; // ����

	public Advertisement() {
		title = "";
		url = "";
		picture = null;
	}

	public Advertisement(String title, String url) {
		this.title = title;
		this.url = url;
		picture = null;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setPicture(Bitmap picture) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] data = null;
		picture.compress(Bitmap.CompressFormat.PNG, 100, out);
		try {
			out.flush();
			data = out.toByteArray();
			out.close();
		} catch (IOException e) {
			Log.e("Advertisement", "Bitmap Translate Error!");
		}
		this.picture = data;
	}

	public Bitmap getPicture() {
		if (picture != null)
			return BitmapFactory.decodeByteArray(picture, 0, picture.length);
		else
			return null;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
}
