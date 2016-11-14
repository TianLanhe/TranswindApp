package com.example.transwind;

import cn.bmob.newsmssdk.BmobSMS;
import cn.bmob.newsmssdk.exception.BmobException;
import cn.bmob.newsmssdk.listener.RequestSMSCodeListener;
import cn.bmob.newsmssdk.listener.VerifySMSCodeListener;

import com.example.transwind.httptools.HttpControler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FindbackActivity extends Activity {

	// UI相关
	EditText edt_phonenum;
	EditText edt_verificate;
	TextView txt_get_verification;
	Button btn_findback;
	ImageView img_back;
	TextView txt_title;

	// 数据相关
	String phonenum;
	String verification;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_findback);

		edt_phonenum = (EditText) findViewById(R.id.edt_findback_phonenum);
		edt_verificate = (EditText) findViewById(R.id.edt_findback_verificate);
		txt_get_verification = (TextView) findViewById(R.id.txt_findback_get_verification);
		btn_findback = (Button) findViewById(R.id.btn_findback_findback);
		img_back = (ImageView) findViewById(R.id.imgv_header_back);
		txt_title = (TextView) findViewById(R.id.txt_header_title);

		txt_title.setText("找回密码");

		// 返回按钮
		img_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		// 确定按钮
		btn_findback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				phonenum = edt_phonenum.getText().toString();
				verification = edt_verificate.getText().toString();
				if (phonenum.equals("")) {
					Toast.makeText(FindbackActivity.this, "请输入手机号码",
							Toast.LENGTH_SHORT).show();
				} else if (verification.equals("")) {
					Toast.makeText(FindbackActivity.this, "请输入验证码",
							Toast.LENGTH_SHORT).show();
				} else if (!phonenum.startsWith("1") || phonenum.length() != 11) {
					edt_phonenum.setText("");
					Toast.makeText(FindbackActivity.this, "请输入正确的手机号码",
							Toast.LENGTH_SHORT).show();
				} else {
					BmobSMS.verifySmsCode(FindbackActivity.this, phonenum,
							verification, new VerifySMSCodeListener() {
								@Override
								public void done(BmobException ex) {
									if (ex == null) {
										// 短信验证码已验证成功，向服务器发起找回密码请求
										new Thread(new Runnable() {
											@Override
											public void run() {
												int result_code = HttpControler
														.findback(phonenum);
												Message msg = new Message();
												msg.what = result_code;
												handler.sendMessage(msg);
											}
										}).start();
									} else {
										Toast.makeText(FindbackActivity.this,
												"验证码错误", Toast.LENGTH_SHORT)
												.show();
									}
								}
							});
				}
			}
		});

		// 获取验证码
		txt_get_verification.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				phonenum = edt_phonenum.getText().toString();
				if (phonenum.equals("")) {
					Toast.makeText(FindbackActivity.this, "请输入手机号码",
							Toast.LENGTH_SHORT).show();
				} else if (!phonenum.startsWith("1") || phonenum.length() != 11) {
					edt_phonenum.setText("");
					Toast.makeText(FindbackActivity.this, "请输入正确的手机号码",
							Toast.LENGTH_SHORT).show();
				} else {
					// 请求发送短信验证码
					BmobSMS.requestSMSCode(FindbackActivity.this, phonenum,
							"找回模板", new RequestSMSCodeListener() {
								@Override
								public void done(Integer smsId, BmobException ex) {
									if (ex == null) {// 验证码发送成功
										Toast.makeText(FindbackActivity.this,
												"验证码发送成功", Toast.LENGTH_SHORT)
												.show();
									} else {
										Toast.makeText(FindbackActivity.this,
												"验证码发送失败，请检查手机号码或网络",
												Toast.LENGTH_SHORT).show();
									}
									Log.d("FindbackActivity", "SMSid：" + smsId);// 用于查询本次短信发送详情
								}
							});
				}
			}
		});
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HttpControler.INTERNET_ERROR:
				Toast.makeText(FindbackActivity.this, "网络错误，请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.HTTP_RESULT_ERROR:
				Toast.makeText(FindbackActivity.this, "手机号错误或未注册",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.DATA_ERROR:
				Toast.makeText(FindbackActivity.this, "找回密码失败",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.HTTP_RESULT_OK:
				Toast.makeText(FindbackActivity.this, "找回成功",
						Toast.LENGTH_SHORT).show();

				// 进入重置界面，携带手机号码
				Intent intent = new Intent(FindbackActivity.this,
						ResetActivity.class);
				intent.putExtra("phonenum", phonenum);
				startActivity(intent);
				finish();
				break;
			}
		}
	};
}
