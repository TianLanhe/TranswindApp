package com.example.transwind;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegistActivity extends Activity {
	
	//UI相关
	EditText edt_phonenum;
	EditText edt_password;
	EditText edt_password_confirm;
	Spinner spn_user_type;
	EditText edt_verificate;
	TextView txt_get_verification;
	Button btn_regist;
	ImageView img_back;
	TextView txt_title;
	
	//数据相关
	String user_type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);
		
		edt_phonenum=(EditText) findViewById(R.id.edt_regist_phonenum);
		edt_password=(EditText) findViewById(R.id.edt_regist_password);
		edt_password_confirm=(EditText) findViewById(R.id.edt_regist_password_confirm);
		spn_user_type=(Spinner) findViewById(R.id.spn_regist_user_type);
		edt_verificate=(EditText) findViewById(R.id.edt_regist_verificate);
		txt_get_verification=(TextView) findViewById(R.id.txt_regist_get_verification);
		btn_regist=(Button) findViewById(R.id.btn_regist_regist);
		img_back=(ImageView) findViewById(R.id.imgv_header_back);
		txt_title=(TextView) findViewById(R.id.txt_header_title);
		
		txt_title.setText("立即注册");
		
		//返回按钮
		img_back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		//下拉列表选择事件
		spn_user_type.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				switch(arg2){
				case 0:
					user_type="商户";
					break;
				case 1:
					user_type="译者";
					break;
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		//注册按钮
		btn_regist.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				String phonenum = edt_phonenum.getText().toString();
				String password = edt_password.getText().toString();
				String password_confirm=edt_password_confirm.getText().toString();
				String verification=edt_verificate.getText().toString();
				if (phonenum.equals("")) {
					Toast.makeText(RegistActivity.this, "请输入手机号码",
							Toast.LENGTH_SHORT).show();
				} else if (password.equals("")) {
					Toast.makeText(RegistActivity.this, "请输入密码",
							Toast.LENGTH_SHORT).show();
				}else if(password_confirm.equals("")){
					Toast.makeText(RegistActivity.this, "请输入确认密码",
							Toast.LENGTH_SHORT).show();
				}else if(verification.equals("")){
					Toast.makeText(RegistActivity.this, "请输入验证码",
							Toast.LENGTH_SHORT).show();
				} else if (!phonenum.startsWith("1")||phonenum.length()!=11) {
					edt_phonenum.setText("");
					Toast.makeText(RegistActivity.this, "请输入正确的手机号码",
							Toast.LENGTH_SHORT).show();
				}else if(!password.equals(password_confirm)){
					edt_password.setText("");
					edt_password_confirm.setText("");
					Toast.makeText(RegistActivity.this, "两次密码不一致，请重新输入",
							Toast.LENGTH_SHORT).show();
				} else {
					// 发送账号密码等到服务器进行注册
				}
				// 成功注册则进入主界面
				// 否则提示错误
			}
		});
		
		//获取验证码
		txt_get_verification.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//获取验证码
			}
		});
	}
}
