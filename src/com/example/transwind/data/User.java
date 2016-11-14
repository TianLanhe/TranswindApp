package com.example.transwind.data;

import android.graphics.Bitmap;

public class User {
	public static final int MERCHANT=0; //商户
	public static final int TRANSLATOR=2; //译者
	public static final int NORMAL=1;	//普通用户
	
	private String phonenum;
	private int type;
	private String username;
	private String level;
	private String email;
	private String address;
	private String sex;
	private String qq;
	private String wechat;
	private String birthday;
	private int wind_bean;
	private String school;
	private Bitmap icon;
	
	public User(){
		phonenum="";
	}
	public User(String phonenum,String username,int type){
		this.phonenum=phonenum;
		this.username=username;
		this.type=type;
	}
	
	public String getPhonenum(){
		return phonenum;
	}
	public void setPhonenum(String phonenum){
		this.phonenum=phonenum;
	}
	
	public int getType(){
		return type;
	}
	public void setType(int type){
		this.type=type;
	}
	
	public String getUsername(){
		return username;
	}
	public void setUsername(String username){
		this.username=username;
	}
}
