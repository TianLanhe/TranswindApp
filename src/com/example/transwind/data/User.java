package com.example.transwind.data;

import android.graphics.Bitmap;

public abstract class User {
	public static final int MERCHANT=0; //�̻�
	public static final int TRANSLATOR=2; //����
	public static final int NORMAL=1;	//��ͨ�û�
	
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
	
	//���󷽷������ݲ�ͬ���û����ͷ��ز�ֵͬ
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
