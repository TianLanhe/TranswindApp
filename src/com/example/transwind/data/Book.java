package com.example.transwind.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

@SuppressWarnings("serial")
public class Book implements Serializable {

	private String name;// 书名
	private String description;// 书的简短描述
	private byte[] picture; // 图片

	public Book() {
		name = "";
		description = "";
		picture = null;
	}

	public Book(String name, String description) {
		this.name = name;
		this.description = description;
		picture = null;
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
			Log.e("Book", "Bitmap Translate Error!");
		}
		this.picture = data;
	}

	public Bitmap getPicture() {
		if (picture != null)
			return BitmapFactory.decodeByteArray(picture, 0, picture.length);
		else
			return null;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
