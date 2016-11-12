package com.example.transwind;

import com.example.transwind.httptools.HttpControler;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	// UI相关
	Button btn_login;
	EditText edt_phonenum;
	EditText edt_password;
	TextView txt_findback;
	TextView txt_regist;

	// 数据相关
	String phonenum;
	String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		edt_phonenum = (EditText) findViewById(R.id.edittext_login_phonenum);
		edt_password = (EditText) findViewById(R.id.edittext_login_password);
		txt_findback = (TextView) findViewById(R.id.txt_login_findback);
		txt_regist = (TextView) findViewById(R.id.txt_login_regist);
		btn_login = (Button) findViewById(R.id.btn_login_login);

		// 如果用户曾经成功登录过，则系统自动输入手机号码
		phonenum = getSharedPreferences("user", MODE_PRIVATE).getString(
				"phonenum", "");
		edt_phonenum.setText(phonenum);

		// "找回密码"按钮
		txt_findback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LoginActivity.this,
						FindbackActivity.class);
				startActivity(intent);
			}
		});

		// "立即注册"按钮
		txt_regist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LoginActivity.this,
						RegistActivity.class);
				startActivity(intent);
			}
		});

		// 登录按钮
		btn_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				phonenum = edt_phonenum.getText().toString();
				password = edt_password.getText().toString();
				if (phonenum.equals("")) {
					Toast.makeText(LoginActivity.this, "请输入手机号码",
							Toast.LENGTH_SHORT).show();
				} else if (password.equals("")) {
					Toast.makeText(LoginActivity.this, "请输入密码",
							Toast.LENGTH_SHORT).show();
				} else if (!phonenum.startsWith("1") || phonenum.length() != 11) {
					edt_phonenum.setText("");
					Toast.makeText(LoginActivity.this, "请输入正确的手机号码",
							Toast.LENGTH_SHORT).show();
				} else {
					new Thread(new Runnable() {
						@Override
						public void run() {
							int result_code = HttpControler.login(phonenum,
									password);
							Message msg = new Message();
							msg.what = result_code;
							handler.sendMessage(msg);
						}
					}).start();
				}
			}
		});
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HttpControler.INTERNET_ERROR:
				Toast.makeText(LoginActivity.this, "网络错误，请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.HTTP_RESULT_ERROR:
				Toast.makeText(LoginActivity.this, "手机号或密码错误",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.DATA_ERROR:
				Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT)
						.show();
				break;
			case HttpControler.HTTP_RESULT_OK:
				//保存相关配置
				SharedPreferences.Editor editor = getSharedPreferences("user",
						MODE_PRIVATE).edit();
				editor.putString("phonenum", phonenum);
				editor.putBoolean("hasLogin", true);
				editor.commit();

				//进入主界面
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
				break;
			}
		}
	};
}
