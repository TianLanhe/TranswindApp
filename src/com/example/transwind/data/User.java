package com.example.transwind.data;

import android.graphics.Bitmap;

public abstract class User {
	public static final int MERCHANT=0; //商户
	public static final int TRANSLATOR=2; //译者
	public static final int NORMAL=1;	//普通用户
	
	private String phonenum;
	private int type;
	private String username;
	private Bitmap icon;
	private int wind_bean;
	
	public String getPhonenum(){
		return phonenum;
	}
	public void setPhonenum(String phonenum){
		this.phonenum=phonenum;
	}
	
	//抽象方法，根据不同的用户类型返回不同值
	public abstract int getType();
	
	public String getUsername(){
		return username;
	}
	public void setUsername(String username){
		this.username=username;
	}
	
	public void setIcon(Bitmap icon){
		this.icon=icon;
	}
	public Bitmap getIcon(){
		return icon;
	}
}
