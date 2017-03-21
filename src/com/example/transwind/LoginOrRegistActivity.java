package com.example.transwind;

import cn.bmob.newsmssdk.BmobSMS;
import cn.bmob.newsmssdk.exception.BmobException;
import cn.bmob.newsmssdk.listener.RequestSMSCodeListener;
import cn.bmob.newsmssdk.listener.VerifySMSCodeListener;

import com.example.transwind.httptools.HttpControler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class LoginOrRegistActivity extends Activity {

	// UI相关
	TextView txt_regist;
	TextView txt_login;
	ViewPager viewpager;
	ImageView img_indicator;
	ProgressDialog progress_dialog;

	View view_login;
	Button btn_login;
	EditText edt_login_phonenum;
	EditText edt_login_password;
	TextView txt_findback;
	TextView txt_smslogin;

	View view_regist;
	EditText edt_regist_phonenum;
	EditText edt_regist_password;
	EditText edt_regist_password_confirm;
	TextView txt_yes;
	Spinner spn_user_type;
	EditText edt_verificate;
	TextView txt_get_verification;
	Button btn_regist;

	// 数据相关
	int bitmap_width;
	int offset;

	String login_phonenum;
	String login_password;

	String regist_phonenum;
	String regist_password;
	String user_type;
	String verification;
	Boolean clickable = false;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_or_regist);

		viewpager = (ViewPager) findViewById(R.id.vwp_login_pager);
		txt_login = (TextView) findViewById(R.id.txt_login_login);
		txt_regist = (TextView) findViewById(R.id.txt_login_regist);
		img_indicator = (ImageView) findViewById(R.id.img_login_indicator);

		// 耗时操作对话框
		progress_dialog = new ProgressDialog(this);
		progress_dialog.setCancelable(false);
		progress_dialog.setCanceledOnTouchOutside(false);

		view_login = LayoutInflater.from(this).inflate(
				R.layout.viewpager_login, null);
		btn_login = (Button) view_login.findViewById(R.id.btn_login_login);
		edt_login_phonenum = (EditText) view_login
				.findViewById(R.id.edt_login_phonenum);
		edt_login_password = (EditText) view_login
				.findViewById(R.id.edt_login_password);
		txt_findback = (TextView) view_login
				.findViewById(R.id.txt_login_findback);
		txt_smslogin = (TextView) view_login
				.findViewById(R.id.txt_login_smslogin);

		view_regist = LayoutInflater.from(this).inflate(
				R.layout.viewpager_regist, null);
		edt_regist_phonenum = (EditText) view_regist
				.findViewById(R.id.edt_regist_phonenum);
		edt_regist_password = (EditText) view_regist
				.findViewById(R.id.edt_regist_password);
		edt_regist_password_confirm = (EditText) view_regist
				.findViewById(R.id.edt_regist_password_confirm);
		txt_get_verification = (TextView) view_regist
				.findViewById(R.id.txt_regist_get_verification);
		edt_verificate = (EditText) view_regist
				.findViewById(R.id.edt_regist_verificate);
		txt_yes = (TextView) view_regist.findViewById(R.id.txt_regist_yes);
		btn_regist = (Button) view_regist.findViewById(R.id.btn_regist_regist);
		spn_user_type = (Spinner) view_regist
				.findViewById(R.id.spn_regist_user_type);

		// "注册"标签页
		txt_regist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				viewpager.setCurrentItem(1);
			}
		});

		// "登录"标签页
		txt_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				viewpager.setCurrentItem(0);
			}
		});

		// 初始化指示器
		bitmap_width = BitmapFactory.decodeResource(getResources(),
				R.drawable.activity_login_triangle).getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		offset = (dm.widthPixels / 2 - bitmap_width) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		img_indicator.setImageMatrix(matrix);

		// ViewPager设置适配器
		viewpager.setAdapter(new PagerAdapter() {
			@Override
			public int getCount() {
				return 2;
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				View view;
				if (position == 0)
					view = view_login;
				else
					view = view_regist;
				((ViewPager) container).removeView(view);
			}

			@Override
			public Object instantiateItem(View container, int position) {
				View view;
				if (position == 0)
					view = view_login;
				else
					view = view_regist;
				((ViewPager) container).addView(view);
				return view;
			}
		});

		// ViewPager设置改变事件
		viewpager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int position) {
				Animation animation;
				if (position == 0)
					animation = new TranslateAnimation(offset * 2
							+ bitmap_width, 0, 0, 0);
				else
					animation = new TranslateAnimation(0, offset * 2
							+ bitmap_width, 0, 0);
				animation.setDuration(500);
				animation.setFillAfter(true);
				img_indicator.startAnimation(animation);// 要start不能set
			}
		});

		// 如果用户曾经成功登录过，则系统自动输入手机号码
		login_phonenum = getSharedPreferences("user", MODE_PRIVATE).getString(
				"phonenum", "");
		edt_login_phonenum.setText(login_phonenum);

		// "找回密码"按钮
		txt_findback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LoginOrRegistActivity.this,
						FindbackActivity.class);
				startActivity(intent);
				finish();
			}
		});

		// "短信登录"按钮
		txt_smslogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Toast.makeText(LoginOrRegistActivity.this, "短信登录",
						Toast.LENGTH_SHORT).show();
				/*
				 * TODO Intent intent = new Intent(LoginOrRegistActivity.this,
				 * FindbackActivity.class); startActivity(intent); finish();
				 */
			}
		});

		// 登录按钮
		btn_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				login_phonenum = edt_login_phonenum.getText().toString();
				login_password = edt_login_password.getText().toString();
				if (login_phonenum.equals("")) {
					Toast.makeText(LoginOrRegistActivity.this, "请输入手机号码",
							Toast.LENGTH_SHORT).show();
				} else if (login_password.equals("")) {
					Toast.makeText(LoginOrRegistActivity.this, "请输入密码",
							Toast.LENGTH_SHORT).show();
				} else if (!login_phonenum.startsWith("1")
						|| login_phonenum.length() != 11) {
					edt_login_phonenum.setText("");
					Toast.makeText(LoginOrRegistActivity.this, "请输入正确的手机号码",
							Toast.LENGTH_SHORT).show();
				} else {
					progress_dialog.setMessage("正在登录，请稍候...");
					progress_dialog.show();

					new Thread(new Runnable() {
						@Override
						public void run() {
							int result_code = HttpControler.login(
									login_phonenum, login_password);
							Message msg = new Message();
							msg.what = result_code;
							handler.sendMessage(msg);
						}
					}).start();
				}
			}
		});

		// 下拉列表选择事件
		spn_user_type.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position == 0)
					user_type = "商户";
				else
					user_type = "译者";
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		// 注册按钮
		btn_regist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				regist_phonenum = edt_regist_phonenum.getText().toString();
				regist_password = edt_regist_password.getText().toString();
				String password_confirm = edt_regist_password_confirm.getText()
						.toString();
				verification = edt_verificate.getText().toString();
				if (regist_phonenum.equals("")) {
					Toast.makeText(LoginOrRegistActivity.this, "请输入手机号码",
							Toast.LENGTH_SHORT).show();
				} else if (regist_password.equals("")) {
					Toast.makeText(LoginOrRegistActivity.this, "请输入密码",
							Toast.LENGTH_SHORT).show();
				} else if (password_confirm.equals("")) {
					Toast.makeText(LoginOrRegistActivity.this, "请输入确认密码",
							Toast.LENGTH_SHORT).show();
				} else if (verification.equals("")) {
					Toast.makeText(LoginOrRegistActivity.this, "请输入验证码",
							Toast.LENGTH_SHORT).show();
				} else if (!regist_phonenum.startsWith("1")
						|| regist_phonenum.length() != 11) {
					edt_regist_phonenum.setText("");
					Toast.makeText(LoginOrRegistActivity.this, "请输入正确的手机号码",
							Toast.LENGTH_SHORT).show();
				} else if (regist_password.length() < 6
						|| regist_password.length() > 15) {
					edt_regist_password.setText("");
					edt_regist_password_confirm.setText("");
					Toast.makeText(LoginOrRegistActivity.this, "请输入6~15位的密码",
							Toast.LENGTH_SHORT).show();
				} else if (!regist_password.equals(password_confirm)) {
					edt_regist_password.setText("");
					edt_regist_password_confirm.setText("");
					Toast.makeText(LoginOrRegistActivity.this, "两次密码不一致，请重新输入",
							Toast.LENGTH_SHORT).show();
				} else {
					BmobSMS.verifySmsCode(LoginOrRegistActivity.this,
							regist_phonenum, verification,
							new VerifySMSCodeListener() {
								@Override
								public void done(BmobException ex) {
									if (ex == null) {
										// 短信验证码已验证成功，向服务器发起注册请求
										progress_dialog
												.setMessage("正在注册，请稍候...");
										progress_dialog.show();

										new Thread(new Runnable() {
											@Override
											public void run() {
												int result_code = HttpControler
														.regist(regist_phonenum,
																regist_password,
																user_type);
												Message msg = new Message();
												// 为了区分注册的成功失败与登录的成功失败
												if (result_code != HttpControler.INTERNET_ERROR)
													msg.what = result_code + 100;
												else
													msg.what = result_code;
												handler.sendMessage(msg);
											}
										}).start();
									} else {
										Toast.makeText(
												LoginOrRegistActivity.this,
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
				regist_phonenum = edt_regist_phonenum.getText().toString();
				if (regist_phonenum.equals("")) {
					Toast.makeText(LoginOrRegistActivity.this, "请输入手机号码",
							Toast.LENGTH_SHORT).show();
				} else if (!regist_phonenum.startsWith("1")
						|| regist_phonenum.length() != 11) {
					edt_regist_phonenum.setText("");
					Toast.makeText(LoginOrRegistActivity.this, "请输入正确的手机号码",
							Toast.LENGTH_SHORT).show();
				} else {
					// 请求发送短信验证码
					BmobSMS.requestSMSCode(LoginOrRegistActivity.this,
							regist_phonenum, "注册模板",
							new RequestSMSCodeListener() {
								@Override
								public void done(Integer smsId, BmobException ex) {
									if (ex == null) {// 验证码发送成功
										Toast.makeText(
												LoginOrRegistActivity.this,
												"验证码发送成功", Toast.LENGTH_SHORT)
												.show();
									} else {
										Toast.makeText(
												LoginOrRegistActivity.this,
												"验证码发送失败，请检查手机号码或网络",
												Toast.LENGTH_SHORT).show();
									}
									Log.d("FindbackActivity", "SMSid：" + smsId);// 用于查询本次短信发送详情
								}
							});
				}
			}
		});

		// "同意协议"按钮
		txt_yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				clickable = !clickable;
				Drawable drawable;
				if (clickable) {
					drawable = getResources().getDrawable(
							R.drawable.viewpager_regist_yes_pressed);
					//btn_regist.setBackgroundResource(R.drawable.button);
				} else {
					drawable = getResources().getDrawable(
							R.drawable.viewpager_regist_yes_normal);
					//btn_regist
							//.setBackgroundResource(R.drawable.button_unclickable);
				}
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				txt_yes.setCompoundDrawables(drawable, null, null, null);
				btn_regist.setEnabled(clickable);
			}
		});
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 取消等待对话框
			progress_dialog.dismiss();
			switch (msg.what) {
			case HttpControler.INTERNET_ERROR:
				Toast.makeText(LoginOrRegistActivity.this, "网络错误，请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.HTTP_RESULT_ERROR:
				Toast.makeText(LoginOrRegistActivity.this, "手机号或密码错误",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.DATA_ERROR:
				Toast.makeText(LoginOrRegistActivity.this, "登录失败",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.HTTP_RESULT_OK:
				// 保存相关配置
				SharedPreferences.Editor editor = getSharedPreferences("user",
						MODE_PRIVATE).edit();
				editor.putString("phonenum", login_phonenum);
				editor.putBoolean("hasLogin", true);
				editor.commit();

				// 进入主界面
				Intent intent = new Intent(LoginOrRegistActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
				break;
			case HttpControler.HTTP_RESULT_ERROR + 100:
				Toast.makeText(LoginOrRegistActivity.this, "手机号已注册",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.HTTP_RESULT_OK + 100:
				Toast.makeText(LoginOrRegistActivity.this, "注册成功",
						Toast.LENGTH_SHORT).show();
				// 切换到登录界面
				viewpager.setCurrentItem(0);
				break;
			case HttpControler.DATA_ERROR + 100:
				Toast.makeText(LoginOrRegistActivity.this, "注册失败",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
}
