package com.example.transwind;

import com.example.transwind.httptools.HttpControler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class ModifyPasswordActivity extends Activity {

	// UI相关
	TextView txt_title;
	ImageView img_back;
	EditText edt_oldpsw;
	EditText edt_newpsw;
	EditText edt_newpsw_confirm;
	Button btn_modify;

	ProgressDialog progress_dialog;

	// 数据相关
	private String phonenum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_password);

		edt_oldpsw = (EditText) findViewById(R.id.edt_modify_oldpsw);
		edt_newpsw = (EditText) findViewById(R.id.edt_modify_newpsw);
		edt_newpsw_confirm = (EditText) findViewById(R.id.edt_modify_newpsw_confirm);
		btn_modify = (Button) findViewById(R.id.btn_modify_modify);
		txt_title = (TextView) findViewById(R.id.txt_header_title);
		img_back = (ImageView) findViewById(R.id.imgv_header_back);

		txt_title.setText("修改密码");
		// 获取用户手机号码
		phonenum = getSharedPreferences("user", MODE_PRIVATE).getString(
				"phonenum", "");

		// 返回按钮
		img_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		// 修改密码按钮
		btn_modify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final String oldpsw = edt_oldpsw.getText().toString();
				final String newpsw = edt_newpsw.getText().toString();
				String newpsw_confirm = edt_newpsw_confirm.getText().toString();

				if (oldpsw.equals("")) {
					Toast.makeText(ModifyPasswordActivity.this, "请输入旧密码",
							Toast.LENGTH_SHORT).show();
				} else if (newpsw.equals("")) {
					Toast.makeText(ModifyPasswordActivity.this, "请输入新密码",
							Toast.LENGTH_SHORT).show();
				} else if (newpsw_confirm.equals("")) {
					Toast.makeText(ModifyPasswordActivity.this, "请确认新密码",
							Toast.LENGTH_SHORT).show();
				} else if (oldpsw.length() < 6 || oldpsw.length() > 15) {
					edt_oldpsw.setText("");
					Toast.makeText(ModifyPasswordActivity.this,
							"旧密码长度必须为6~15位", Toast.LENGTH_SHORT).show();
				} else if (!newpsw.equals(newpsw_confirm)) {
					edt_newpsw.setText("");
					edt_newpsw_confirm.setText("");
					Toast.makeText(ModifyPasswordActivity.this,
							"两次密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
				} else if (newpsw.equals(oldpsw)) {
					edt_newpsw.setText("");
					edt_newpsw_confirm.setText("");
					Toast.makeText(ModifyPasswordActivity.this,
							"新密码不能与旧密码相同，请重新输入", Toast.LENGTH_SHORT).show();
				} else if (newpsw.length() < 6 || newpsw.length() > 15) {
					edt_newpsw.setText("");
					edt_newpsw_confirm.setText("");
					Toast.makeText(ModifyPasswordActivity.this,
							"新密码长度必须为6~15位", Toast.LENGTH_SHORT).show();
				} else {
					// 耗时操作对话框
					progress_dialog = new ProgressDialog(
							ModifyPasswordActivity.this);
					progress_dialog.setCancelable(false);
					progress_dialog.setCanceledOnTouchOutside(false);
					progress_dialog.setMessage("正在修改，请稍候...");
					progress_dialog.show();

					// 将新老密码和手机号发送到服务器修改密码
					new Thread(new Runnable() {
						@Override
						public void run() {
							int result_code = HttpControler.modifyPassword(
									phonenum, oldpsw, newpsw);
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
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progress_dialog.dismiss();
			switch (msg.what) {
			case HttpControler.INTERNET_ERROR:
				Toast.makeText(ModifyPasswordActivity.this, "网络错误，请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.HTTP_RESULT_ERROR:
				Toast.makeText(ModifyPasswordActivity.this, "旧密码错误",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.DATA_ERROR:
				Toast.makeText(ModifyPasswordActivity.this, "修改密码失败",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.HTTP_RESULT_OK:
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ModifyPasswordActivity.this);
				builder.setMessage("修改密码成功，请重新登录!");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// 修改成未登录
								SharedPreferences.Editor editor = getSharedPreferences(
										"user", MODE_PRIVATE).edit();
								editor.putBoolean("hasLogin", false);
								editor.commit();

								// 跳转到登录界面
								Intent intent = new Intent(
										ModifyPasswordActivity.this,
										LoginOrRegistActivity.class);
								// 可以清除返回栈中所有活动，但没有经过StartActivity，不知道Bmob的有没有出问题
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
										| Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							}
						});
				AlertDialog dialog = builder.create();
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
				break;
			}
		}
	};
}
