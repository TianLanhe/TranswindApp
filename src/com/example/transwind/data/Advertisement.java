package com.example.transwind.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

@SuppressWarnings("serial")
public class Advertisement implements Serializable {

	private String title; // ±ÍÃ‚
	private byte[] picture; // Õº∆¨
	private String url; // ¡¥Ω”

	public Advertisement() {
		
	}
	public Advertisement(String title, String url) {
		this.title = title;
		this.url = url;
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
			Log.d("Advertisement", "Bitmap Translate Error!");
		}
		this.picture = data;
	}

	public Bitmap getPicture() {
		return BitmapFactory.decodeByteArray(picture, 0, picture.length);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
}
