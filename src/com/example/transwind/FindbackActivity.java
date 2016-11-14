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

	// UI���
	EditText edt_phonenum;
	EditText edt_verificate;
	TextView txt_get_verification;
	Button btn_findback;
	ImageView img_back;
	TextView txt_title;

	// �������
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

		txt_title.setText("�һ�����");

		// ���ذ�ť
		img_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		// ȷ����ť
		btn_findback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				phonenum = edt_phonenum.getText().toString();
				verification = edt_verificate.getText().toString();
				if (phonenum.equals("")) {
					Toast.makeText(FindbackActivity.this, "�������ֻ�����",
							Toast.LENGTH_SHORT).show();
				} else if (verification.equals("")) {
					Toast.makeText(FindbackActivity.this, "��������֤��",
							Toast.LENGTH_SHORT).show();
				} else if (!phonenum.startsWith("1") || phonenum.length() != 11) {
					edt_phonenum.setText("");
					Toast.makeText(FindbackActivity.this, "��������ȷ���ֻ�����",
							Toast.LENGTH_SHORT).show();
				} else {
					BmobSMS.verifySmsCode(FindbackActivity.this, phonenum,
							verification, new VerifySMSCodeListener() {
								@Override
								public void done(BmobException ex) {
									if (ex == null) {
										// ������֤������֤�ɹ���������������һ���������
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
				phonenum = edt_phonenum.getText().toString();
				if (phonenum.equals("")) {
					Toast.makeText(FindbackActivity.this, "�������ֻ�����",
							Toast.LENGTH_SHORT).show();
				} else if (!phonenum.startsWith("1") || phonenum.length() != 11) {
					edt_phonenum.setText("");
					Toast.makeText(FindbackActivity.this, "��������ȷ���ֻ�����",
							Toast.LENGTH_SHORT).show();
				} else {
					// �����Ͷ�����֤��
					BmobSMS.requestSMSCode(FindbackActivity.this, phonenum,
							"�һ�ģ��", new RequestSMSCodeListener() {
								@Override
								public void done(Integer smsId, BmobException ex) {
									if (ex == null) {// ��֤�뷢�ͳɹ�
										Toast.makeText(FindbackActivity.this,
												"��֤�뷢�ͳɹ�", Toast.LENGTH_SHORT)
												.show();
									} else {
										Toast.makeText(FindbackActivity.this,
												"��֤�뷢��ʧ�ܣ������ֻ����������",
												Toast.LENGTH_SHORT).show();
									}
									Log.d("FindbackActivity", "SMSid��" + smsId);// ���ڲ�ѯ���ζ��ŷ�������
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
				Toast.makeText(FindbackActivity.this, "���������������",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.HTTP_RESULT_ERROR:
				Toast.makeText(FindbackActivity.this, "�ֻ��Ŵ����δע��",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.DATA_ERROR:
				Toast.makeText(FindbackActivity.this, "�һ�����ʧ��",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.HTTP_RESULT_OK:
				Toast.makeText(FindbackActivity.this, "�һسɹ�",
						Toast.LENGTH_SHORT).show();

				// �������ý��棬Я���ֻ�����
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
