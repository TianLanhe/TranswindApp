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

	// UI���
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

	// �������
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

		// ��ʱ�����Ի���
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

		// "ע��"��ǩҳ
		txt_regist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				viewpager.setCurrentItem(1);
			}
		});

		// "��¼"��ǩҳ
		txt_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				viewpager.setCurrentItem(0);
			}
		});

		// ��ʼ��ָʾ��
		bitmap_width = BitmapFactory.decodeResource(getResources(),
				R.drawable.activity_login_triangle).getWidth();// ��ȡͼƬ���
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		offset = (dm.widthPixels / 2 - bitmap_width) / 2;// ����ƫ����
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		img_indicator.setImageMatrix(matrix);

		// ViewPager����������
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

		// ViewPager���øı��¼�
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
				img_indicator.startAnimation(animation);// Ҫstart����set
			}
		});

		// ����û������ɹ���¼������ϵͳ�Զ������ֻ�����
		login_phonenum = getSharedPreferences("user", MODE_PRIVATE).getString(
				"phonenum", "");
		edt_login_phonenum.setText(login_phonenum);

		// "�һ�����"��ť
		txt_findback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LoginOrRegistActivity.this,
						FindbackActivity.class);
				startActivity(intent);
				finish();
			}
		});

		// "���ŵ�¼"��ť
		txt_smslogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Toast.makeText(LoginOrRegistActivity.this, "���ŵ�¼",
						Toast.LENGTH_SHORT).show();
				/*
				 * TODO Intent intent = new Intent(LoginOrRegistActivity.this,
				 * FindbackActivity.class); startActivity(intent); finish();
				 */
			}
		});

		// ��¼��ť
		btn_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				login_phonenum = edt_login_phonenum.getText().toString();
				login_password = edt_login_password.getText().toString();
				if (login_phonenum.equals("")) {
					Toast.makeText(LoginOrRegistActivity.this, "�������ֻ�����",
							Toast.LENGTH_SHORT).show();
				} else if (login_password.equals("")) {
					Toast.makeText(LoginOrRegistActivity.this, "����������",
							Toast.LENGTH_SHORT).show();
				} else if (!login_phonenum.startsWith("1")
						|| login_phonenum.length() != 11) {
					edt_login_phonenum.setText("");
					Toast.makeText(LoginOrRegistActivity.this, "��������ȷ���ֻ�����",
							Toast.LENGTH_SHORT).show();
				} else {
					progress_dialog.setMessage("���ڵ�¼�����Ժ�...");
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

		// �����б�ѡ���¼�
		spn_user_type.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position == 0)
					user_type = "�̻�";
				else
					user_type = "����";
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		// ע�ᰴť
		btn_regist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				regist_phonenum = edt_regist_phonenum.getText().toString();
				regist_password = edt_regist_password.getText().toString();
				String password_confirm = edt_regist_password_confirm.getText()
						.toString();
				verification = edt_verificate.getText().toString();
				if (regist_phonenum.equals("")) {
					Toast.makeText(LoginOrRegistActivity.this, "�������ֻ�����",
							Toast.LENGTH_SHORT).show();
				} else if (regist_password.equals("")) {
					Toast.makeText(LoginOrRegistActivity.this, "����������",
							Toast.LENGTH_SHORT).show();
				} else if (password_confirm.equals("")) {
					Toast.makeText(LoginOrRegistActivity.this, "������ȷ������",
							Toast.LENGTH_SHORT).show();
				} else if (verification.equals("")) {
					Toast.makeText(LoginOrRegistActivity.this, "��������֤��",
							Toast.LENGTH_SHORT).show();
				} else if (!regist_phonenum.startsWith("1")
						|| regist_phonenum.length() != 11) {
					edt_regist_phonenum.setText("");
					Toast.makeText(LoginOrRegistActivity.this, "��������ȷ���ֻ�����",
							Toast.LENGTH_SHORT).show();
				} else if (regist_password.length() < 6
						|| regist_password.length() > 15) {
					edt_regist_password.setText("");
					edt_regist_password_confirm.setText("");
					Toast.makeText(LoginOrRegistActivity.this, "������6~15λ������",
							Toast.LENGTH_SHORT).show();
				} else if (!regist_password.equals(password_confirm)) {
					edt_regist_password.setText("");
					edt_regist_password_confirm.setText("");
					Toast.makeText(LoginOrRegistActivity.this, "�������벻һ�£�����������",
							Toast.LENGTH_SHORT).show();
				} else {
					BmobSMS.verifySmsCode(LoginOrRegistActivity.this,
							regist_phonenum, verification,
							new VerifySMSCodeListener() {
								@Override
								public void done(BmobException ex) {
									if (ex == null) {
										// ������֤������֤�ɹ��������������ע������
										progress_dialog
												.setMessage("����ע�ᣬ���Ժ�...");
										progress_dialog.show();

										new Thread(new Runnable() {
											@Override
											public void run() {
												int result_code = HttpControler
														.regist(regist_phonenum,
																regist_password,
																user_type);
												Message msg = new Message();
												// Ϊ������ע��ĳɹ�ʧ�����¼�ĳɹ�ʧ��
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
												"��֤�����", Toast.LENGTH_SHORT)
												.show();
									}
								}
							});
				}
			}
		});

		// ��ȡ��֤��
		txt_get_verification.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				regist_phonenum = edt_regist_phonenum.getText().toString();
				if (regist_phonenum.equals("")) {
					Toast.makeText(LoginOrRegistActivity.this, "�������ֻ�����",
							Toast.LENGTH_SHORT).show();
				} else if (!regist_phonenum.startsWith("1")
						|| regist_phonenum.length() != 11) {
					edt_regist_phonenum.setText("");
					Toast.makeText(LoginOrRegistActivity.this, "��������ȷ���ֻ�����",
							Toast.LENGTH_SHORT).show();
				} else {
					// �����Ͷ�����֤��
					BmobSMS.requestSMSCode(LoginOrRegistActivity.this,
							regist_phonenum, "ע��ģ��",
							new RequestSMSCodeListener() {
								@Override
								public void done(Integer smsId, BmobException ex) {
									if (ex == null) {// ��֤�뷢�ͳɹ�
										Toast.makeText(
												LoginOrRegistActivity.this,
												"��֤�뷢�ͳɹ�", Toast.LENGTH_SHORT)
												.show();
									} else {
										Toast.makeText(
												LoginOrRegistActivity.this,
												"��֤�뷢��ʧ�ܣ������ֻ����������",
												Toast.LENGTH_SHORT).show();
									}
									Log.d("FindbackActivity", "SMSid��" + smsId);// ���ڲ�ѯ���ζ��ŷ�������
								}
							});
				}
			}
		});

		// "ͬ��Э��"��ť
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
			// ȡ���ȴ��Ի���
			progress_dialog.dismiss();
			switch (msg.what) {
			case HttpControler.INTERNET_ERROR:
				Toast.makeText(LoginOrRegistActivity.this, "���������������",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.HTTP_RESULT_ERROR:
				Toast.makeText(LoginOrRegistActivity.this, "�ֻ��Ż��������",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.DATA_ERROR:
				Toast.makeText(LoginOrRegistActivity.this, "��¼ʧ��",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.HTTP_RESULT_OK:
				// �����������
				SharedPreferences.Editor editor = getSharedPreferences("user",
						MODE_PRIVATE).edit();
				editor.putString("phonenum", login_phonenum);
				editor.putBoolean("hasLogin", true);
				editor.commit();

				// ����������
				Intent intent = new Intent(LoginOrRegistActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
				break;
			case HttpControler.HTTP_RESULT_ERROR + 100:
				Toast.makeText(LoginOrRegistActivity.this, "�ֻ�����ע��",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.HTTP_RESULT_OK + 100:
				Toast.makeText(LoginOrRegistActivity.this, "ע��ɹ�",
						Toast.LENGTH_SHORT).show();
				// �л�����¼����
				viewpager.setCurrentItem(0);
				break;
			case HttpControler.DATA_ERROR + 100:
				Toast.makeText(LoginOrRegistActivity.this, "ע��ʧ��",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
}
