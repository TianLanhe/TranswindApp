package com.example.transwind;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FindbackActivity extends Activity {
	
	//UI相关
	EditText edt_phonenum;
	EditText edt_verificate;
	TextView txt_get_verification;
	Button btn_findback;
	ImageView img_back;
	TextView txt_title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_findback);
		
		edt_phonenum=(EditText) findViewById(R.id.edt_findback_phonenum);
		edt_verificate=(EditText) findViewById(R.id.edt_findback_verificate);
		txt_get_verification=(TextView) findViewById(R.id.txt_findback_get_verification);
		btn_findback=(Button) findViewById(R.id.btn_findback_findback);
		img_back=(ImageView) findViewById(R.id.imgv_header_back);
		txt_title=(TextView) findViewById(R.id.txt_header_title);
		
		txt_title.setText("找回密码");
		
		//返回按钮
		img_back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		//确定按钮
		btn_findback.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				String phonenum = edt_phonenum.getText().toString();
				String verification = edt_verificate.getText().toString();
				if (phonenum.equals("")) {
					Toast.makeText(FindbackActivity.this, "请输入手机号码",
							Toast.LENGTH_SHORT).show();
				} else if (verification.equals("")) {
					Toast.makeText(FindbackActivity.this, "请输入验证码",
							Toast.LENGTH_SHORT).show();
				} else if (!phonenum.startsWith("1")||phonenum.length()!=11) {
					edt_phonenum.setText("");
					Toast.makeText(FindbackActivity.this, "请输入正确的手机号码",
							Toast.LENGTH_SHORT).show();
				} else {
					// 发送账号验证码到服务器找回密码
				}
				// 成功找回进入重设密码界面
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
