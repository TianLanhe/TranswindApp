package com.example.transwind;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	// UI���
	Button btn_login;
	EditText edt_phonenum;
	EditText edt_password;
	TextView txt_findback;
	TextView txt_regist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		edt_phonenum = (EditText) findViewById(R.id.edittext_login_phonenum);
		edt_password = (EditText) findViewById(R.id.edittext_login_password);
		txt_findback = (TextView) findViewById(R.id.txt_login_findback);
		txt_regist = (TextView) findViewById(R.id.txt_login_regist);
		btn_login = (Button) findViewById(R.id.btn_login_login);

		// ����û������ɹ���¼������ϵͳ�Զ������ֻ�����
		String phonenum = getSharedPreferences("user", MODE_PRIVATE).getString(
				"phonenum", "");
		edt_phonenum.setText(phonenum);

		// "�һ�����"��ť
		txt_findback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LoginActivity.this,
						FindbackActivity.class);
				startActivity(intent);
			}
		});

		// "����ע��"��ť
		txt_regist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LoginActivity.this,
						RegistActivity.class);
				startActivity(intent);
			}
		});

		// ��¼��ť
		btn_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String phonenum = edt_phonenum.getText().toString();
				String password = edt_password.getText().toString();
				if (phonenum.equals("")) {
					Toast.makeText(LoginActivity.this, "�������ֻ�����",
							Toast.LENGTH_SHORT).show();
				} else if (password.equals("")) {
					Toast.makeText(LoginActivity.this, "����������",
							Toast.LENGTH_SHORT).show();
				} else if (!phonenum.startsWith("1")||phonenum.length()!=11) {
					edt_phonenum.setText("");
					Toast.makeText(LoginActivity.this, "��������ȷ���ֻ�����",
							Toast.LENGTH_SHORT).show();
				} else {
					// �����˺����뵽���������е�¼
				}
				// ��¼�ɹ�����������
				// ������ʾ�������
			}
		});
	}
}
