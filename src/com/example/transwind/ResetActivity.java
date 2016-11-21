package com.example.transwind;

import com.example.transwind.httptools.HttpControler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ResetActivity extends Activity {

	// UI相关
	EditText edt_password;
	EditText edt_password_confirm;
	Button btn_reset;
	ImageView img_back;
	TextView txt_title;

	// 数据相关
	String phonenum;
	String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset);

		edt_password = (EditText) findViewById(R.id.edt_reset_password);
		edt_password_confirm = (EditText) findViewById(R.id.edt_reset_password_confirm);
		btn_reset = (Button) findViewById(R.id.btn_reset_reset);
		img_back = (ImageView) findViewById(R.id.imgv_header_back);
		txt_title = (TextView) findViewById(R.id.txt_header_title);

		txt_title.setText("重置密码");

		// 返回按钮
		img_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		// 获取手机号码
		phonenum = getIntent().getStringExtra("phonenum");

		// 确定按钮
		btn_reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				password = edt_password.getText().toString();
				String password_confirm = edt_password_confirm.getText()
						.toString();
				if (password.equals("")) {
					Toast.makeText(ResetActivity.this, "请输入密码",
							Toast.LENGTH_SHORT).show();
				} else if (password_confirm.equals("")) {
					Toast.makeText(ResetActivity.this, "请输入确认密码",
							Toast.LENGTH_SHORT).show();
				} else if (!password.equals(password_confirm)) {
					edt_password.setText("");
					edt_password_confirm.setText("");
					Toast.makeText(ResetActivity.this, "两次密码不一致，请重新输入",
							Toast.LENGTH_SHORT).show();
				} else {
					new Thread(new Runnable() {
						@Override
						public void run() {
							int result_code = HttpControler.reset(phonenum,
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

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HttpControler.INTERNET_ERROR:
				Toast.makeText(ResetActivity.this, "网络错误，请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.HTTP_RESULT_ERROR:
				Toast.makeText(ResetActivity.this, "手机号错误",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.DATA_ERROR:
				Toast.makeText(ResetActivity.this, "重置密码失败", Toast.LENGTH_SHORT)
						.show();
				break;
			case HttpControler.HTTP_RESULT_OK:
				Toast.makeText(ResetActivity.this, "重置成功", Toast.LENGTH_SHORT)
						.show();

				// 进入登录界面
				Intent intent = new Intent(ResetActivity.this,
						LoginOrRegistActivity.class);
				startActivity(intent);
				finish();
				break;
			}
		}
	};
}
