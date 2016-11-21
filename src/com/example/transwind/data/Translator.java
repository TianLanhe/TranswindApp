package com.example.transwind.data;

public class Translator extends User {

	private String school;
	private String major;
	private String sex;
	
	public String getSchool(){
		return school;
	}
	public void setSchool(String school){
		this.school=school;
	}
	
	public String getMajor(){
		return major;
	}
	public void setMajor(String major){
		this.major=major;
	}
}
